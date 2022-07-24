// ******************************** package
package org.pclg.tools;

// ******************************** imports

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Provee algunas funciones de uso comun.
 *
 * @author El Coyote Cojo
 */
public final class ToolBox {
	/** El Logger de esta clase. */
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	/** One kilobyte. */
	private static final long KBYTE = 1024L;
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

	/**
	 * El constructor por omision es privado para evitar que a algun capullo
	 * se le ocurra hacer new ToolBox()
	 */
	private ToolBox() {
	}

    /**
     * Escribe 's' en out1 y out2 simultaneamente.
     */
    public static void tee(final PrintStream out1, final PrintStream out2, final String s) {
        out1.println(s);
        out2.println(s);
    }


	/**
	 * Leer un string del teclado
	 *
	 * @return El string leido
	 */
	public static String getString() throws IOException {
		return getString("");
	}

    /**
     * Leer un string del teclado.
     *
     * @param prompt Un prompt para el usuario.
     * @return El string leido.
     */
    public static String getString(final String prompt) throws IOException {
        final BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(prompt);
        return sysin.readLine();
    }


	/**
	 * Devuelve el String str completado con el padChar hasta la longitud
	 * exacta len.
	 *
	 * @param str     El String que hay que completar
	 * @param len     La longitud que queremos
	 * @param padChar El caracter de relleno
	 * @return el String str completado con el padChar hasta la longitud exacta
	 * len.
	 */
	public static String padAndClip(final String str, final int len, final char padChar) {
		return pad(str, len, padChar).substring(0, len);
	}

    /**
     * Devuelve el String s completado con ' ' hasta la longitud minima len
     *
     * @param s   El String que hay que completar
     * @param len La longitud que queremos
     * @return el String s completado con ' ' hasta la longitud minima len
     */
    public static String pad(final String s, final int len) {
        return pad(s, len, ' ');
    }

    /**
     * Devuelve el String str completado por la derecha con el padChar hasta la longitud minima len
     *
     * @param str       El String que hay que completar
     * @param len     La longitud que queremos
     * @param padChar El caracter de relleno
     * @return el String str completado por la derecha con el padChar hasta la longitud minima len
     */
    public static String pad(final String str, final int len, final char padChar) {
		final int strLen = str.length();
		if (strLen >= len) {
			return str;
		}
        return str + String.valueOf(padChar).repeat(len - strLen);
    }

    /**
     * Devuelve el String str completado por la izquierda con el padChar hasta
	 * la longitud minima len.
     *
     * @param str     El String que hay que completar
     * @param len     La longitud que queremos
     * @param padChar El caracter de relleno
     * @return el String str completado por la izquierda con el padChar hasta
	 * la longitud minima len.
     */
    public static String leftPad(final String str, final int len,
			final char padChar) {
		final int strLen = str.length();
		if (strLen >= len) {
			return str;
		}
        return String.valueOf(padChar).repeat(len - strLen) + str;
    }

    /**
     * Reemplaza 'len' caracteres de 'oldString' a partir de 'start'
     * con el contenido de 'replacement'
     * Podria usar StringBuffers por eficiencia
     *
     * @param oldString   El objeto del reemplazo
     * @param start       Desde donde comienza el reemplazo.
     *                    Si es negativo comienza desde el final del String
     *                    (-1 es el ultimo caracter)
     * @param len         La cantidad de caracteres a reemplazar (>= 0)
     * @param replacement El reemplazo
     *                    --author El Zorro
     * @return La string con los reemplazos.
     */
    public static String replace(final String oldString, int start, final int len,
            final String replacement) {
        if (len < 0) {
            throw new IllegalArgumentException("len: " + len + " < 0");
        }

        String newString;
        try {
            if (start < 0) {
                start = oldString.length() - len + start + 1;
            }
//??? Ademas estas 3 intrucciones se pueden convertir en 2
            // Elimino
            newString = oldString
                    .substring(0, start);    // Guardo los primeros caracteres
            newString += oldString
                    .substring(start + len); // Agrego los restantes
            // y agrego
            newString = newString.substring(0, start) + replacement + newString
                    .substring(start);
        } catch (final StringIndexOutOfBoundsException ex) {
            newString = oldString;
        }
        return newString;
    }

    /**
     * Reemplaza en 'oldString' todas las ocurrencias de 'notWanted' con 'replacement'
     * --author El Coyote Cojo
     * @since 2002.ene.21 16:50:01, CET
     *
     * @param oldString   El String donde se van a hacer los reemplazos
     * @param notWanted   El substring que no queremos
     * @param replacement El reemplazo
     * @return La string con los reemplazos.
     */
    public static String replaceAll(String oldString, final String notWanted,
            final String replacement) {
        String newString = oldString;
        final int lenOld = notWanted.length();
        final int lenNew = replacement.length();
        int lastFoundPos = -1;
        while ((lastFoundPos = newString.indexOf(notWanted, lastFoundPos)) > -1)
        {
            // Elimino
            newString = newString.substring(0, lastFoundPos);	// Guardo los primeros caracteres
            newString += replacement;	// Agrego el reemplazo
            newString += oldString.substring( lastFoundPos + lenOld);    // Agrego los restantes
            lastFoundPos += lenNew;	// La siguiente busqueda es a partir del resto del string
            oldString = newString;	// La busqueda la seguimos dentro del resultado actual
        }
        return newString;
    }


    /**
     * Determina la forma aceptable de mostrar un numero hic et nunc
     */
    private static NumberFormat numberFormat;

    /**
     * @param n el susodicho numero
     * @return n en una forma aceptable
     * @since 2001.09.08 22:27
     */
    public static String formatNumber(final long n) {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance();
        }
        return numberFormat.format(n);
    }

//    /**
//     * @param n el susodicho numero
//     * @return 'n' en una forma aceptable
//     * @since 2001.09.08 22:27
//     */
//    public static String formatNumber(int n) {
//        return (formatNumber(n));    // �No hay que hacer el cast a long ?????
//    }


	/**
	 * Se encarga de escribir datos en un archivo
	 * --author El Zorro
	 * @since 2001.02.21
	 *
	 * @param data los datos que quiero escribir
	 * @param file el sitio donde quiero ponerlos
	 * @return 'true' si pudo hacerlo, 'false' de lo contrario
	 */
	public static boolean writeToFile(final byte[] data, final File file) {
		// verificacion logica
		int choice = 0;
		if (file.exists()) {
			final Object[] options = {"Cancelar", "Agregar", "Sobre-escribir"};
			final String msg =
					"ToolBox.writeToFile(byte [] data, File file)\nEl archivo <"
							+ file.getName()
							+ "> existe.\n�Qu\u00E9 desea hacer:";
			final String title = "Atenci�n";
			choice = GUITools.showMessage(msg, title, options);
			if (choice == 0) {
				return false;
			}
		}
		// Verificar tambien si es read only, etc.

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
			if (choice == 2) {
				raf.setLength(0);
			} else {
				raf.seek(file.length());
			}
			raf.write(data);
			return true;
		} catch (final IOException ex) {
			showInfo(ex);
		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (final IOException ex1) {
				LOGGER.log(Level.ERROR, "Error", ex1);
			}
		}
		return false;
	}


	/**
	 * Muestra la informacion relativa a una Exception dando m�s informacion cuando se puede
	 * --author El Coyote cojo
	 * @since 2001.07.13
	 *
	 * @param ex La excepcion cuya informacion queremos mostrar
	 */
	public static void showInfo(final Throwable ex) {
		showInfo(ex, true);
	}

    /**
     * Muestra la informacion relativa a una Exception dando m�s informacion cuando se puede
     * --author El Coyote cojo
     * @since 2001.07.13
     *
     * @param ex        La excepcion cuya informacion queremos mostrar
     * @param showStack Indica si queremos mostrar el Stack Trace
     */
    public static void showInfo(final Throwable ex, final boolean showStack) {
        LOGGER.error("\t\tSituation normal, all fucked up.");
        if (ex == null) {
            LOGGER.error("\t\tYa es el colmo: pasar null como excepci�n.");
            return;
        }
        // si no es una de las excepciones que manejamos o se nos pide
		// expl�citamente mostramos el Stack Trace.
        if (!(ex instanceof SQLException) && !(ex instanceof ParseException)
				|| showStack) {
            LOGGER.log(Level.ERROR, "Error", ex);
        }

        if (ex instanceof SQLException) {
            SQLException sql_ex = (SQLException) ex;
            LOGGER.error("--- Atrapada una SQLException  ---");
            while (sql_ex != null) {
                LOGGER.error("Message:   " + sql_ex.getMessage());
                LOGGER.error(
                        "LocalizedMessage: " + ex.getLocalizedMessage());
                LOGGER.error("SQLState:  " + sql_ex.getSQLState());
                LOGGER.error("ErrorCode: " + sql_ex.getErrorCode());
                LOGGER.error("--");
                sql_ex = sql_ex.getNextException();
            }
            LOGGER.error("----------------------------------");
            return;
        }
        LOGGER.error("--- Atrapada una Exception  ---");
        LOGGER.error("Message:          " + ex.getMessage());
        LOGGER.error("LocalizedMessage: " + ex.getLocalizedMessage());
    }

    /**
     Devuelve un objeto Date construido a partir de un String que representa una fecha
     --author El Coyote cojo
     @since 2001.08.10
     @param date La supuesta fecha
     @throws ParseException si no se puede interpretar como una fecha el argumento
     public static Date parseDate(String strDate) throws ParseException {
     Date d = null;
     //DateFormat df = DateFormat.getDateInstance();
     //DateFormat df = DateFormat.getTimeInstance();
     DateFormat df = DateFormat.getDateTimeInstance();
     //d = df.parse(strDate);
     //d = new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
     //d = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(strDate);
     d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(strDate);
     return(d);
     }
     */


    /**
     * Encuentra todas las clases de cierto tipo
     * --author El Coyote cojo
     * @since 2001.11.13
     *
     * @param targetClass La clase o interfaz de cuyo tipo debe ser el resultado
     * @param path        El sitio donde debe buscar las clases, puede ser un directorio, un zip o un jar
     * @return Un arreglo con las clases encontradas
     */
    public static Class<?>[] findClasses(final Class<?> targetClass, final File path) {	// TODO: generify
        //System.out.println(System.getProperty("java.class.path"));
        final List<Class<?>> v = new ArrayList<>();

        // 2003.01.05 kludge
        if (targetClass == null || path == null) {
            return EMPTY_CLASS_ARRAY;
        }

        final Optional<String[]> files = path.isDirectory() ? 
        	findAllClassesInDirSubTree(path) : ArchiveTools.listZip(path);    // suponemos que es un zip o un jar

        if (files.isPresent()) {
			for (final String file : files.get()) {
				if (file.endsWith(".class")) {
					try {
						final String className = file
								.substring(0, file.indexOf(".class"))
								.replace('/', '.')
								.replace('\\', '.');
						final Class<?> clase = Class.forName(className);
						final Class<?>[] implementedInterfaces = clase.getInterfaces();
						for (final Class<?> implementedInterface : implementedInterfaces) {
							if (implementedInterface == targetClass) {
								v.add(clase);
								break;
							}
						}
					} catch (final Throwable ex) {
						showInfo(ex);
					}
				}
			}
	        Class<?>[] result = new Class[v.size()];
	        result = v.toArray(result);
	        return result;
        } else {
            return EMPTY_CLASS_ARRAY;
        }
    }

    /**
     * Lo que su nombre indica.
     * @param path el directorio donde hacer la b�squeda.
     * @return un array con todas las clases que hay en este directorio o hijos.
     */
    private static Optional<String[]> findAllClassesInDirSubTree(final File path) {
        final String basePath = path.getAbsolutePath();
        final File[] files = FileTools.listFiles(path);
        final String[] names = new String[files.length];
        for (int ii = 0; ii < files.length; ii++) {
            names[ii] = files[ii].getAbsolutePath().replace(basePath, "").substring(1); // .substring(1) para eliminar el separator que queda al principio. FIXME:
        }
        return Optional.of(names);
    }


    /**
     * Cambia los caracteres extra�os por '_'
     *
     * @param name El nombre a normalizar
     * @return El nombre normalizado
     *         --author El Coyote Cojo
     *         @since 2001.12.21
     */
    public static String normalizeName(final String name) {
        final char[] chars = new char[name.length()];
        name.getChars(0, chars.length, chars, 0);
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isJavaIdentifierPart(chars[i])) {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }


    /**
     * no estoy seguro de si esto es una chapuza
     */
    public static int unsignedByte(final byte b) {
		return b < 0 ? b + 256 : b;
    }

    /**
     * Devuelve un long, tratando de interpretar el String 'cant' que debe
     * ser de la forma <n>[b|k|m|g|t] (bytes por omisi�n).
     *
     * @param cant El string
     * @return El tama�o
     * --author El Coyote Cojo
     * @since 2002.12.20
     */
    public static long parseSize(String cant) {
        char modifier = 'b';
        final int lastPos = cant.length() - 1;
        if (!Character.isDigit(cant.charAt(lastPos))) {
            modifier = cant.charAt(lastPos);
            cant = cant.substring(0, lastPos);
        }

        final long size;
        switch (modifier) {
            case 'B':
			case 'b':
                size = Long.parseLong(cant);
                break;
            case 'K':
            case 'k':
                size = KBYTE * Long.parseLong(cant);
                break;
            case 'M':
            case 'm':
                size = KBYTE * KBYTE * Long.parseLong(cant);
                break;
            case 'G':
            case 'g':
                size = KBYTE * KBYTE * KBYTE * Long.parseLong(cant);
                break;
            case 'T':
            case 't':
                size = KBYTE * KBYTE * KBYTE * KBYTE * Long.parseLong(cant);
                break;
            default:
                throw new IllegalArgumentException(cant);
        }

        return size;
    }

    /**
     * Determina de donde se est� ejecutando la aplicacion: un directorio o un jar.
     *
     * @param requester el objeto que hace la petici�n
     * @return el directorio o jar donde est� la aplicaci�n.
     */
    public static Optional<File> getExecutionPath(final Object requester) {
        final URL appURL = requester.getClass()
            .getResource(requester.getClass().getName() + ".class");
        if(appURL == null) {
            LOGGER.error("No puedo determinar el punto de ejecucion");
            return Optional.empty();
        }

        // Determinar que jar o directorio se est� ejecutando
        final String path = appURL.getPath();
        int end = path.lastIndexOf('!');		// Si es un jar, termina con ! y el nombre de la clase
        if(end == -1) {
            end = path.lastIndexOf('/');		// Si es un directorio, termina con / y el nombre de la clase
        }
        final String baseDir = path.substring(path.indexOf('/') + 1, end);
        return Optional.of(new File(baseDir));
    }

	/** Regex for only decimal numbers (at least one digit after the decimal point). */
	private static final Pattern NUMERIC_PATTERN_2 = Pattern.compile("\\d*\\.?\\d+");

    /** Regex for only decimal numbers (at least one digit before the decimal point). */
    private static final Pattern NUMERIC_PATTERN_1 = Pattern.compile("\\d+\\.?\\d*");

	/** Regex for only integer numbers. */
	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

    /**
	 * <code>true</code> si esta String representa un n�mero.
	 * @param string la String a comprobar.
	 * @return <code>true</code> si esta String representa un n�mero <code>false</code> de lo contrario.
	 */
	public static boolean isNumber(final String string) {
		return string != null && (NUMERIC_PATTERN_1.matcher(string).matches() || NUMERIC_PATTERN_2.matcher(string).matches());
	}

	/**
	 * <code>true</code> si esta String representa un entero.
	 * @param string la String a comprobar.
	 * @return <code>true</code> si esta String representa un entero <code>false</code> de lo contrario.
	 */
	public static boolean isInteger(final String string) {
		return string != null && INTEGER_PATTERN.matcher(string).matches();
	}

    /**
     * Compares numerically two BigDecimal objects.
     */
    public static boolean bigDecimalEquals(final BigDecimal bigDecimal1, final BigDecimal bigDecimal2) {
        return bigDecimal1 == null ? bigDecimal2 == null : bigDecimal2 != null && bigDecimal1.compareTo(bigDecimal2) ==  0;
    }
}
