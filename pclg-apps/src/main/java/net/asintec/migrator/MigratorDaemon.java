// ******************************** package
package net.asintec.migrator;


// ******************************** imports
import org.pclg.tools.ToolBox;

/**
	MigratorDaemon <BR>
	@author El Coyote Cojo
	@version 2002.may.31 11:00:47, CEST
*/
public class MigratorDaemon implements MigratorUserInterface {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		Constructor por omision
		@author El Coyote Cojo
		@version 2002.may.31 11:00:47, CEST
	*/
	public MigratorDaemon() {
	}

	/**
		Constructor por omision
		@param confFile El archivo de configuracion a utilizar
		@author El Coyote Cojo
		@version 2002.may.31 11:00:47, CEST
	*/
	private MigratorDaemon(final String confFile) {
		migrate(confFile);
	}

	// ******************************** Metodos de instancia

	/**
		Realiza una migracion automatizada
		@param configFiles Un arreglo con los nombres de los archivos de configuracion que tienen los datos de la migracion
	*/
	public void migrate(final String[] configFiles) {
		for(int i = 0; i < configFiles.length; i++) {
			migrate(configFiles[i]);
		}
	}

	public void migrate(final String confFile) {
		//System.err.println("Comenzando " + confFile);
		//int cron = Chrono.getChrono();
		//Chrono.start(cron);
		try {
			/* El señor que realiza la migracion */
			final MigratorArbeiter currentMigrator = new MigratorArbeiterStandard();	// ??? ¿es necesario uno nuevo cada vez?
			final MigrationInfoImpl myInfo = new MigrationInfoImpl(confFile);

			final String sourceTable = myInfo.getSourceTable();
			final DataSource dataSource = (DataSource) Class.forName(myInfo.getDataSource()).newInstance();
			//System.out.println(getClass().getName() + ".setDataSource(): " + dataSource);

			dataSource.setDatabase(myInfo.getSourceDatabase(), "");
			dataSource.openDatabase();
			dataSource.openTable(sourceTable);

			final String targetDatabase = myInfo.getTargetDatabase();
			final String targetTable = myInfo.getTargetTable();

			final DataSource dataSink = (DataSource) Class.forName(myInfo.getDataSink()).newInstance();
			dataSink.setDatabase(targetDatabase, "");
			dataSink.openDatabase();
			dataSink.openTable(targetTable);

			currentMigrator.migrate(this, dataSource, dataSink,
									sourceTable,
									targetTable,
									dataSink.getFieldTypes(),
									dataSink.getUniqueFields(),
									myInfo);
			try {
				currentMigrator.join();
			} catch(final InterruptedException ex) {
				ToolBox.showInfo(ex);
				Thread.currentThread().interrupt();
			}
			dataSource.closeDatabase();
		}
		catch(final ClassNotFoundException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final InstantiationException ex) {
			ToolBox.showInfo(ex);
		}
		catch(final IllegalAccessException ex) {
			ToolBox.showInfo(ex);
		}

		//Chrono.mark(cron);
		//System.out.println("Tiempo: " + Chrono.timeDetail(Chrono.elapsed(cron)));
	}

	// ---------- Implementacion de MigratorUserInterface ---------------------------------- //
	/**
		Da un mensaje al usuario
		@param msg El mensaje
	*/
	@Override
	public void showMsg(final String title, final String msg) {
		System.err.println(title + " -> " + msg);
	}

	/**
		Notificacion de fin de la migracion
	*/
	@Override
	public void endMsg() {
	}

	/**
		Da un mensaje de informacion al usuario
		@param msg El mensaje
	*/
	@Override
	public void showInfo(final String msg) {
		System.err.println(msg);
	}

	/**
		Da un mensaje de alerta al usuario
		@param msg El mensaje
	*/
	@Override
	public void showAlert(final String msg) {
		System.err.println(msg);
	}

	// ******************************** Metodos estaticos

	/**
		Ejecuta la aplicación
		@author El Coyote Cojo
		@version 2002.may.31 11:00:47, CEST
	*/
	public static void main(final String[] args) {
		if(args.length > 0) {
			new MigratorDaemon(args[0]);
		}
		else {
			System.err.println("Uso: java MigratorDaemon <archivo de configuracion>");
			System.exit(0);
		}
	}
}
