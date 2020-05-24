// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant
// cada vez.
package org.pclg.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Revisa un archivo de texto y genera un indice de las palabras que aparecen,
 * una por linea y sin repeticiones.
 *
 * @author El Coyote Cojo
 * @version 1.0
 */
public final class Indexer {
	static final String INDEX_FILENAME_SUFFIX = ".index.txt";
	private static final Integer ZERO = Integer.valueOf(0);

    private Indexer() {
    }

    /**
     * Crea un Map con las frecuencias de las palabras contenidas en un archivo y lo escribe en otro.
     * @param filename  el archivo de dónde leer. El archivo a escribir se llama igual pero terminando en ".index.txt".
     * @return el Map con las frecuencias
     * @throws IOException si problemas haber.
     */
    static Map<String, Integer> createIndex(final String filename) throws IOException {
        final Map<String, Integer> frecuencies = new TreeMap<>();
        try (final Stream<String> lines = Files.lines(new File(filename).toPath(), StandardCharsets.ISO_8859_1)) {
            lines.filter(line -> line.trim().length() > 0)
                .map(String::toLowerCase)
                .forEach(line -> {
                    final StringTokenizer st = new StringTokenizer(line, " ,.;\"'()\t");
                    while (st.hasMoreTokens()) {
                        final String word = st.nextToken();
                        final Integer sum = frecuencies.computeIfAbsent(word, w -> ZERO);
                        frecuencies.put(word, Integer.valueOf(sum.intValue() + 1));
                    }
                });
        }

        try (final PrintWriter out = new PrintWriter(new FileOutputStream(filename + INDEX_FILENAME_SUFFIX))) {
            final Set<String> set = frecuencies.keySet();
            for (final String palabra : set) {
                out.println(palabra + " (" + frecuencies.get(palabra) + ')');
            }
        }
        return frecuencies;
    }

    /**
     * Ayuda al usuario
     * <p>
     * --author El Coyote Cojo
     * --version 2003.jul.17
     *
     */
    private static void usage() {
        System.err.println("Usage: java Indexer <filename>");
        System.exit(1);
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            usage();
        }
        createIndex(args[0]);
    }
}
