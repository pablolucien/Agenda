package org.pclg.root;

import gnu.getopt.Getopt;

import java.io.IOException;

/**
 * Magic <BR> Emula el comando file(1) de Unix.
 *
 * @author El Coyote Cojo
 * @version 2003.abr.30 22:52:47, CEST
 * @version 2003.jun.20 Modificado para que usar el archivo Magic.magic
 * @version 2003.jun.28 Modificado para que sea compatible con el archivo magic
 *          de file(1). Con suerte, s�lo habr� que modificar a org.pclg.filesystem.MagicEntry
 */
public final class Magic {
	private Magic() {
	}

	/**
     * Ayuda al usuario.
     *
     * --author El Coyote Cojo
     * @since 2003.abr.30 22:52:47, CEST
     */
    private static void usage() {
        errMessage("Usage: java Magic " + "[-rud] [<dirname(s)>]");
        errMessage("                  " + "-r: Recursivo");
        errMessage("                  " + "-u: Solamente informa sobre los archivos desconocidos");
        errMessage("                  " + "-d: Debug");
        errMessage("                  " + "<dirname(s)>: el nombre del o los directorios a procesar;");
        errMessage("                  " + "              si no se proporciona, se usa el cwd");
        errMessage("       java Magic " + "[-h|-?] Presenta esta ayuda");
        System.exit(1);
    }

    /**
     * Ejecuta la aplicaci�n.
     *
     * --author El Coyote Cojo
     * @since 2003.abr.30 22:52:47, CEST
     */
    public static void main(final String[] args) throws IOException {
        boolean debug = false;
        boolean recursivo = false;
        boolean desconocidos = false;
        final Getopt optionGetter = new Getopt("Magic", args, "dh?ru");
        int ch;
        while ((ch = optionGetter.getopt()) != -1) {
            switch (ch) {
            case 'd':
                debug = true;
                break;
			case 'r':
			   recursivo = true;
			   break;
            case 'u':
                desconocidos = true;
                break;
            case '?':
            case 'h':
            default:
                usage();
                System.exit(0);
            }
        }

        final int firstArg = optionGetter.getOptind();

        final String[] dirList;

        if (firstArg < args.length) {
        	dirList = new String[args.length - firstArg];
        	System.arraycopy(args, firstArg, dirList, 0, dirList.length);
        } else {
        	dirList = new String[]{"."};
        }
        new org.pclg.filesystem.Magic(dirList, recursivo, desconocidos, debug);
    }

    /**
     * Outputs a stderr message.
     * @param msg the maessage.
     */
    private static void errMessage(final String msg) {
        System.err.println(msg);
    }
}
