// ******************************** package
package net.asintec.migrator;

/**
	Esta interfaz se encarga de manejar la informacion de usuario
	Pensado para usarlo en Migrator.
	@author El Coyote Cojo
	@version 2002.01.08 (Cumpleaños de Livia)
	@see Migrator
*/
public interface MigratorUserInterface {
	/**
		Da un mensaje al usuario
		@param msg El mensaje
	*/
	void showMsg(String title, String msg);

	/**
		Notificacion de fin de la migracion
	*/
	void endMsg();

	/**
		Da un mensaje de informacion al usuario
		@param msg El mensaje
	*/
	void showInfo(String msg);

	/**
		Da un mensaje de alerta al usuario
		@param msg El mensaje
	*/
	void showAlert(String msg);
}
