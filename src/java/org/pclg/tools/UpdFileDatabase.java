// ******************************** package
package org.pclg.tools;

// ******************************** imports

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
	UpdFileDatabase <BR>
	Borra de la base de datos las entradas que no se corresponden con archivos en el disco
	Ejecutarlo mensualmente
	@author El Coyote Cojo
	@version 2002.oct.12 20:44:42, CEST
*/
public class UpdFileDatabase {
	// ******************************** Variables de clase
	private static Connection conn;
	private static final String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=c:/databases/FileDataBase.mdb;PWD=";
	private static PreparedStatement pstmtSelect;

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		Constructor por omision
		--author El Coyote Cojo
		--version 2002.oct.12 20:44:42, CEST
	*/
	private UpdFileDatabase() {
		ResultSet rset = null;
		int count = 0;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			if(conn == null) {
				conn = DriverManager.getConnection(url);
			}
			if(pstmtSelect == null) {
				pstmtSelect = conn.prepareStatement("SELECT camino, nombre, tamaño, fechaModificacion FROM Archivos"
																+ " ORDER BY camino, nombre",
																ResultSet.TYPE_SCROLL_INSENSITIVE,
																ResultSet.CONCUR_UPDATABLE);
			}

			rset = pstmtSelect.executeQuery();
			count = 0;
			while(rset.next()) {
				int pos = 1;
				final String filePath = rset.getString(pos++);
				final String fileName = rset.getString(pos++);
				final long fileSize = rset.getLong(pos++);
				final long fileDate = rset.getTimestamp(pos++).getTime();
				final File file = new File(filePath, fileName);

//esto si existe. pero getString() se vuelve un culo con las ö, ô, ó, ñ, á, etc
//	D:\zips\Wörterbuch\Russisch_Deutsch.zip no existe. Borrada de la db

				boolean mustDelete = false;
				final boolean verbose = true;
				String reason = null;

				if(!file.exists()) {
					mustDelete = true;
					reason = file.getAbsolutePath() + " no existe.";
				}
				else if(file.length() != fileSize) {
					mustDelete = true;
					reason = file.getAbsolutePath() + " tiene tamaño distinto.";
				}
				else if(file.lastModified() != fileDate) {
					mustDelete = true;
					reason = file.getAbsolutePath() + " tiene fecha distinta.";
				}

				if(mustDelete) {
					mustDelete = false;
					if(verbose) {
						System.out.println(reason + " Borrada de la db");
					}
					rset.deleteRow();
					count++;
					rset.previous();	// Porque parece que al borrar quedamos en el siguiente registro
											// probablemente una variable dontAvance sería más eficiente- probar
				}
			}
		} catch(final SQLException ex) {
			ToolBox.showInfo(ex, true);
		} catch (final ClassNotFoundException ex) {
			ToolBox.showInfo(ex, true);
		} finally {
			System.out.println("Borrados: " + count);
			try {
				rset.close();
				pstmtSelect.close();
				conn.close();
			}
			catch(final SQLException ex) {
				ToolBox.showInfo(ex, true);
			}
		}
	}

	// ******************************** Metodos de instancia


	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		--author El Coyote Cojo
		--version 2002.oct.12 20:44:42, CEST
	*/
	public static void usage(final String[] args) {
		System.err.println("Usage: java UpdFileDatabase " +  "");
		System.exit(1);
	}

	/**
		Ejecuta la aplicación
		--author El Coyote Cojo
		--version 2002.oct.12 20:44:42, CEST
	*/
	public static void main(final String[] args) {
		new UpdFileDatabase();
	}
}
