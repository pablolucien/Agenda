// ******************************** package
package dbinfo;

// ******************************** imports
import java.sql.SQLException;

/**
	Intenta dar mensajes de error inteligentes.
	@author El Coyote Cojo
	@version 2002.03.27 (Cumplaños de Ricardo)
*/
public final class SQLMessages {
    /** Avoids instantiation. */
	private SQLMessages() {
	}

	/**
		Intenta dar mensajes de error inteligentes.
		--author El Coyote Cojo
		@since 2002.03.27 (Cumplaños de Ricardo)
     * @param ex la excepcion cuyo mensaje intentamos dar.
     * @param database El nombre de la base de datos con la que estamos
     * trabajando.
     * @return Un mensaje de error.
     */
	public static String getMessage(final SQLException ex, final String database) {
		String msg = null;
		final String state = ex.getSQLState();
		final int errCode = ex.getErrorCode();
		if(state.equals("S0001")) {
			if(errCode == -1303) {
//LocalizedMessage: [Microsoft][Controlador ODBC Microsoft Access] La tabla 'Tickets' ya existe.
				msg = "La tabla ya existe.";
			}
		}
		else if(state.equals("S1000")) {
			if(errCode == -1811) {
				msg = '<' + database + "> no existe.";
			}
			else if(errCode == -1028) {
				msg = '<' + database + "> no es una base de datos Access";
			}
			else if(errCode == -    1024) {
				msg = '<' + database + "> Está abierta en modo exclusivo";
			}
		}
		else if(state.equals("37000")) {
			if(errCode == -3500) {
				msg = "Intrucción SQL incorrecta";
			}
			if(errCode == -3553) {
				msg = "Error de sintaxis en la definición del campo.";
			}
		}
		else if(state.equals("42000")) {
			if(errCode == -1905) {
				msg = "La contraseña no es correcta";
			}
		}
		return msg;
	}
}
