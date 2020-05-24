package org.pclg.agenda;

/**
 * Clase que obtiene los valores de usuario y password.
 * @author El Coyote Cojo
 * @since 19-sep-2007 18:22:46
 * FIXME: arreglar esta clase y sus usos
 */
public final class AgendaLogin {
	private static final String USERNAME = "root";
	private static String PASSWORD = "";


	private AgendaLogin() {
	}

	public static String getUsername() {
		return USERNAME;
	}

	public static void setPassword(final String password) {
		PASSWORD = password;
	}
	public static String getPassword() {
		return PASSWORD;
	}
}
