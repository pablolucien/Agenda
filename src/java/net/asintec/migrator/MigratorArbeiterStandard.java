// ******************************** package
package net.asintec.migrator;

// package

// imports
import org.pclg.tools.Chrono;
import org.pclg.tools.ToolBox;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;


/**
	MigratorArbeiterStandard
	Esta clase se encarga de hacer la migracion
	Este es el migrador standard
	@author El Coyote Cojo
	@version 2001.ago.01 14:09:50, CEST
*/
public class MigratorArbeiterStandard extends MigratorArbeiter {
	/* Valores de retorno de insertOrUpdate() */
	private static final int INSERTED = 0;
	private static final int UPDATED = 1;
	private static final int ERROR = 2;

	/** Indica si debemos abortar */
	private boolean abortProcess;

	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		return(getClass().getName() + ":\n\t Hace una migracion de Bases de datos Access (*.mdb) o que tengan una entrada ODBC");
	}
	
	/**
		Este es el metodo que hay que implementar para hacer la migracion
	*/
	@Override
	public void run() {
		int migratedRecords = 0;
		int duplicatedRecords = 0;
		int totalRecords = 0;
		abortProcess = false;
		final int cron = Chrono.getChrono();
		Chrono.start(cron);
		final boolean autoCommit = true;

		parentComponent.showMsg("Migrando", "Migracion en proceso");

		try {
			dataSource.executeQuery();
			if (!autoCommit) {
				targetConn.setAutoCommit(false);
			}
			PreparedStatement pstmtInsert = targetConn.prepareStatement(insertSentence);
			PreparedStatement pstmtUpdate = targetConn.prepareStatement(updateSentence);
			PreparedStatement pstmtSelectForUpdate = targetConn.prepareStatement(selectForUpdateSentence);
			boolean ignoreErrors = false;
			Object[] sourceData;
			while((sourceData = dataSource.getNextRecord()) != null && !abortProcess) {
				totalRecords++;

				// Si la base de datos es INTERBASE, hay que crear el PS cada vez (¡Que putada!) //**************************************
				final boolean INTERBASE = false;
				if (INTERBASE) {
					pstmtInsert.close();
					pstmtUpdate.close();
					pstmtSelectForUpdate.close();
					pstmtInsert = targetConn.prepareStatement(insertSentence);
					pstmtUpdate = targetConn.prepareStatement(updateSentence);
					pstmtSelectForUpdate = targetConn.prepareStatement(selectForUpdateSentence);
				}

				if (sourceTable.startsWith(DatabaseSelector.QUERY_AD_HOC)) {			// ???????????????????????????????
																					// Esta chapuza tengo que corregirla antes de que se me olvide
					ignoreErrors = true;
					final String codigo = (String) sourceData[0];
					final String talla = (String) sourceData[3];
					fillStatement(pstmtInsert, 1, codigo, tipos[0], columnas.get(0));
					fillStatement(pstmtUpdate, 1, codigo, tipos[0], columnas.get(0));

// AQUI DEBERIA USAR PreprocessorHalf2M
					String barCode = ToolBox.leftPad(codigo, 6, '0') + talla;
					if(barCode.endsWith("\u00BD")) {
						barCode = barCode.substring(0, barCode.length() - 1)
								+ 'M';
					}
					if(barCode.endsWith(" 1/2")) {
						barCode = barCode.substring(0, barCode.length() - 4)
								+ 'M';
					}
					if(barCode.endsWith("1/2")) {
						barCode = barCode.substring(0, barCode.length() - 3)
								+ 'M';
					}

					fillStatement(pstmtInsert, 2, barCode, tipos[1], columnas.get(1));
					fillStatement(pstmtUpdate, 2, barCode, tipos[1], columnas.get(1));
					fillStatement(pstmtUpdate, 3, barCode, tipos[1], columnas.get(1));
					fillStatement(pstmtSelectForUpdate, 1, barCode, tipos[1], columnas.get(1));
				} else {
					int indexPos = 0;
					for(int i = 0; i < nrFields; i++) {
						Object datum = sourceData[i];
						if(preprocesadores[i] != null) {
							datum = preprocesadores[i].process(datum);
						}
						fillStatement(pstmtInsert, i + 1, datum, tipos[i], columnas.get(i));
						if (false && isUnique[i]) {	// FIXME: Esto falla. Debería tomar en cuenta los campos destino solamente
							indexPos++;
							fillStatement(pstmtSelectForUpdate, indexPos, datum, tipos[i], columnas.get(i));
							fillStatement(pstmtUpdate, nrFields - nrIndexes + indexPos, datum, tipos[i], columnas.get(i));
						}
						else {
							// FIXME: Verificar que funciona independientemente de
                            // la posicion de los indices en la tabla.
							fillStatement(pstmtUpdate, i + 1 - nrIndexes, datum, tipos[i], columnas.get(i));
						}
					}
				}

				final int result = insertOrUpdate(pstmtSelectForUpdate, pstmtUpdate, pstmtInsert);
				switch(result) {
				case UPDATED:
					duplicatedRecords++;
					// fall through
				case INSERTED:
					migratedRecords++;
					break;
				case ERROR:
					errors = true;
					if(!ignoreErrors) {
						migrating = false;
						break;
					}
					break;
				default:
					break;
				}
			}
			if(!autoCommit) {
				targetConn.commit();
			}
			pstmtInsert.close();
			pstmtUpdate.close();
		}
		catch(final ParseException ex) {
			errors = true;
			ToolBox.showInfo(ex);
		}
		catch(final IOException ex) {
			errors = true;
			ToolBox.showInfo(ex);
		}
		catch(final SQLException ex) {
			errors = true;
			ToolBox.showInfo(ex);
		}

		Chrono.mark(cron);
		System.out.println("Tiempo: " + Chrono.timeDetail(Chrono.elapsed(cron)));

		parentComponent.endMsg();
		String info = "Migrados " + migratedRecords + " registros de " + totalRecords;
		if(duplicatedRecords > 0) {
			info += "\n(" + duplicatedRecords + " duplicados)";
		}
		if(errors) {
			//?????? En realidad deberia retornar la informacion de errores
			parentComponent.showInfo("Hubo errores en la operación\n" + info);
		}
		else {
			if(!abortProcess) {
				parentComponent
						.showInfo("Migracion finalizada exitosamente\n" + info);
			}
			else {
				parentComponent.showInfo("Migracion abortada\n" + info);
			}
		}
		migrating = false;
	}


	/**
		Inserta o actualiza un registro 
		Primero intenta buscar el registro, si existe intenta la actualizacion y si no la insercion
		Esta forma parece ser más rápida que la anterior
		@param pstmtSelectForUpdate La sentencia para buscar el registro
		@param pstmtUpdate La sentencia para actualizar
		@param pstmtInsert La sentencia para insertar
		@return INSERTED si pudo insertar, UPDATED si pudo actualizar o ERROR si no pudo
		@since 2002.06.14
	*/
	private int insertOrUpdate(final PreparedStatement pstmtSelectForUpdate,
			final PreparedStatement pstmtUpdate, final PreparedStatement pstmtInsert) {
		try {
			if(pstmtSelectForUpdate.executeQuery().next()) {	// Si existe el registro
				pstmtUpdate.execute();  						// ejecutamos la orden UPDATE
				return(UPDATED);
			} else {											// de lo contrario
				pstmtInsert.execute();  						// ejecutamos la orden INSERT INTO
				return(INSERTED);
			}
		} catch(final SQLException ex) {
			ToolBox.showInfo(ex);
			return(ERROR);
		}
	}


	/** Este es el metodo que hay que implementar para abortar una migracion */
	@Override
	public void abort() {
		abortProcess = true;
	}
}