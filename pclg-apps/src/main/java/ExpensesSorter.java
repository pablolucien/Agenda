import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pclg.tools.StringTools.EMPTY_STRING_ARRAY;

/*
 * We should forget about small efficiencies, say about 97% of the time:
 * Premature optimization is the root of all evil. � Donald Knuth
 *
 * Creado el 19-Sep-2008
 */

/**
 * Organiza el archivo generado de las dietas de Sopra.
 *
 * @author Un autor en busca de personajes.
 * @since 19-Sep-2008
 */
final class ExpensesSorter {
    private static final String SEPARATOR =
            "-----------------------------------------";

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("d/M/yyyy");

    private static final SimpleDateFormat DATE_FORMAT_NORMAL =
            new SimpleDateFormat("dd/MM/yyyy");

    private static final Pattern PATTERN = Pattern
            .compile("\\d{1,2}/\\d{1,2}/\\d{4}");

    private static final Logger LOGGER = LoggerFactory.make();

    private static final Comparator<? super String> COMPARATOR =
            new Comparator<String>() {
        @Override
		public int compare(final String o1, final String o2) {
            final Matcher m1 = PATTERN.matcher(o1);
            final Matcher m2 = PATTERN.matcher(o2);
            if (m1.find() && m2.find()) {
                try {
                    return DATE_FORMAT.parse(m1.group()).compareTo(
                            DATE_FORMAT.parse(m2.group()));
                } catch (final ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(new StringBuffer("Invalid data: [")
                    .append(o1).append("] - [").append(o2).append("]")
                    .toString());
        }
    };

    private static final String[] conceptos = cargarConceptos();

    private final Map<String, List<String>> listas =
            new HashMap<String, List<String>>(conceptos.length);
    {
		for (final String concepto : conceptos) {
			listas.put(concepto, new ArrayList<String>());
		}
    }
    
    private static String[] cargarConceptos() {
    	try {
	    	final BufferedReader reader = new BufferedReader(
	    		new InputStreamReader(ExpensesSorter.class.getClassLoader()
	    			.getResourceAsStream("ExpensesSorter.conf")));
	    	final List<String> lines = new ArrayList<String>();
	    	String line;
	    	while ((line = reader.readLine()) != null) {
	    		lines.add(line);
	    	}
	    	reader.close();
	    	return lines.toArray(EMPTY_STRING_ARRAY);
	    } catch (final IOException ex) {
	    	throw new RuntimeException(ex);
	    }
	}

    /**
	 * Organiza el archivo.
	 *
     * @param filename el archivo a organizar.
     * @throws ParseException si hay errores de parseo
     * @throws FileNotFoundException si no existe el archivo o no se puede leer.
     */
    private void sortFile(final String filename) throws IOException,
            ParseException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            index = filename.length();
        }
        final String suffix = "_sorted";
        final String newName = new StringBuffer(filename.length()
                + suffix.length()).append(filename.substring(0, index))
                .append(suffix).append(filename.substring(index)).toString();
        try {
            reader = new BufferedReader(new FileReader(filename));
            writer = new PrintWriter(new FileWriter(newName));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Detalle")) {
                    writer.println(line);
                    writer.println(SEPARATOR);
                } else {
                    boolean found = false;
					for (final String concepto : conceptos) {
						if (line.startsWith(concepto)) {
							listas.get(concepto).add(line);
							found = true;
							break;
						}
					}
                    if (!found) {
                        LOGGER.severe("Invalid data: " + line);
                    }
                }
            }
            final Pattern patternGeld = Pattern.compile("[0-9]+\\.[0-9]+");
            BigDecimal grandTotal = new BigDecimal("0");
			for (final String concepto : conceptos) {
				final List<String> list = listas.get(concepto);
				if (list.size() == 0) {
					continue;
				}
				final String[] sorted = sort(list);
				normalizeDates(sorted);
				BigDecimal total = new BigDecimal("0");
				for (final String aSorted : sorted) {
					final Matcher m1 = patternGeld.matcher(aSorted);
					if (m1.find()) {
						total = total.add(new BigDecimal(m1.group()));
					}
					writer.println(aSorted);
				}
				writer.println("                             " + total + " �");
				writer.println(SEPARATOR);
				grandTotal = grandTotal.add(total);
			}
            writer.println("Grand Total                  " + grandTotal + " �");
        } finally {
            final File oldFile = new File(filename);
            final File newFile = new File(newName);
            if (reader != null) {
                reader.close();
                oldFile.delete();
            }
            if (writer != null) {
                writer.close();
                newFile.renameTo(oldFile);
            }
        }
    }


    /**
	 * Normaliza las fechas al formato dd/MM/yyyy.
     * @param array el array de fechas a normalizar.
     * @throws ParseException si hay errores de parseo.
     */
    private static void normalizeDates(final String[] array) throws ParseException {
        for (int ii = 0; ii < array.length; ii++) {
            final Matcher m1 = PATTERN.matcher(array[ii]);
            if (m1.find()) {
                final String d1 = m1.group();
                array[ii] = array[ii].replaceAll(d1, DATE_FORMAT_NORMAL
                        .format(DATE_FORMAT.parse(d1)));
            } else {
                throw new RuntimeException("Invalid data: [" + array[ii] + "]");
            }
        }
    }


    /**
	 * Ordena una List<String>.
     * @param list
     * @return la lista ordenada en un <code>String[]</code>
     */
    private static String[] sort(final List<String> list) {
        final String[] sorted = new String[list.size()];
        list.toArray(sorted);
        Arrays.sort(sorted, COMPARATOR);
        return sorted;
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            LOGGER.severe("Uso: java ExpensesSorter <archivo(s)>");
            System.exit(1);
        }
        final ExpensesSorter sorter = new ExpensesSorter();
		for (final String arg : args) {
			try {
				LOGGER.info("Organizando " + arg);
				sorter.sortFile(arg);
				LOGGER.info(arg + " organizado");
			} catch (final ParseException ex) {
				LOGGER.log(Level.SEVERE, "Error", ex);
			} catch (final FileNotFoundException ex) {
				LOGGER.log(Level.SEVERE, "Error", ex);
			} catch (final IOException ex) {
				LOGGER.log(Level.SEVERE, "Error", ex);
			} catch (final Exception ex) {
				LOGGER.log(Level.SEVERE, "Error", ex);
			}
		}
        LOGGER.info("La vida es dura...\n...luego mueres.");
    }
}
