// ******************************** package
package org.pclg.tools;

// ******************************** imports

/**
 * Provee algunas funciones de uso comun.
 *
 * @author El Coyote Cojo.
 * @version 1.0
 */
public final class PCLGTools {

	/**
	 * Avoids instantiation.
	 */
	private PCLGTools() {
	}

	/**
		Guarda en un archivo el resultado de 'getText()' de un componente.
		@since 2003.08.20
	*/
//	public void saveComponentText(Componenent comp) throws IOException {
//	}


    /**
	 * Escribe su entrada en System.out y System.err simultaneamente.
	 *
	 * @param text el texto a escribir.
	 */
	public static void tee(final String text) {
		System.err.println(text);
		System.out.println(text);
	}

	/**
	 * Una traza de donde hubo un error. TODO: HAY QUE HACELO DE VERDAD
	 *
	 * @since 2003.09.21
	 */
	public static void trace(final Throwable ex, final String msg) {
		System.err.println("PCLGTools.trace(): " + ex + ' ' + msg);
	}

	/**
	 * Convierte un byte[] que contiene una dirección IPv4 en una String.
	 *
	 * @param address la dirección a convertir.
	 * @return la dirección IP como String.
	 */
	public static String array2IP(final byte[] address) {
		return new StringBuilder().append(ToolBox.unsignedByte(address[0]))
            .append('.').append(ToolBox.unsignedByte(address[1])).append('.')
            .append(ToolBox.unsignedByte(address[2])).append('.')
            .append(ToolBox.unsignedByte(address[3])).toString();
	}

	public static String getClassName() {
		return new Exception().getStackTrace()[1].getClassName();
	}
}
