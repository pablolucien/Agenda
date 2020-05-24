package tests;

import org.pclg.tools.Chrono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. — Donald Knuth
 * 
 * Creado el 10-Jul-2008
 */

/**
 * @author Un autor en busca de personajes.
 * @since 10-Jul-2008
 */
final class WorterFrequenzRechner {
    static final class FrequenzPaar implements Comparable<FrequenzPaar> {
        private final String character;
        private final int count;

        /**
         * @param character
         * @param count
         */
        FrequenzPaar(final String character, final int count) {
            this.character = character;
            this.count = count;
        }
        
        /* (sin Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
		public int compareTo(final FrequenzPaar other) {
            return other.count - count;
        }
        
        /**
         * Obtiene character.
         *
         * @return Devuelve character.
         */
        public String getString() {
            return character;
        }
        /**
         * Obtiene count.
         *
         * @return Devuelve count.
         */
        public int getCount() {
            return count;
        }
    }
    
    private final Map<String, int[]> almacenAlfabetico = new TreeMap<>();
    private double total;
    
    /**
     * @throws IOException
     * 
     */
    private WorterFrequenzRechner(final String[] args) throws IOException {
        final int cron = Chrono.getChrono();
		for (int j = 0; j < 1; j++) {
            for (final String arg : args) {
                Chrono.start(cron);
                processFile(arg);
                Chrono.mark(cron);
                System.out.printf("---> Procesadas %5.0f palabras distintas en %s\n", total, Chrono.timeDetail(Chrono.elapsed(cron)));
            }
		}
		showResult();
    }

    /**
     * 
     */
    private void showResult() {
        final Set<String> characterSet = almacenAlfabetico.keySet();
        final List<FrequenzPaar> almacenNumerico =
                new ArrayList<>(characterSet.size());
        final MessageFormat format = new MessageFormat("[{0}] occurs {1,number} times ({2,number,#.#####%})");

        for (final String character : characterSet) {
            final int[] count = almacenAlfabetico.get(character);
            final Object[] testArgs = {character, Integer.valueOf(count[0]), Double.valueOf(count[0] / total)};
            System.out.println(format.format(testArgs));
            almacenNumerico.add(new FrequenzPaar(character, count[0]));
        } 

        System.out.println();
        System.out.println("--------------------------------------");
        System.out.println();
        
        Collections.sort(almacenNumerico);
        for (final FrequenzPaar data : almacenNumerico) {
            final int count = data.getCount();
            final String character = data.getString();
            final Object[] testArgs = {character, Integer.valueOf(count), Double.valueOf(count / total)};
            System.out.println(format.format(testArgs));
        }
    }

    /**
     * @param name
     * @throws IOException
     */
    private void processFile(final String name) throws IOException {
    	/* Paula Diaz Blanes paula-maria.diaz@sopra.com */
	    try (final Stream<String> lines = Files.lines(Paths.get(name), StandardCharsets.ISO_8859_1)) {
	    	lines
	    		.flatMap(string -> Arrays.asList(string.split("[\\s\\p{Punct}¡“”‘¿?—…]+", 0)).stream())
				.filter(s -> s.trim().length() > 0)
				.map(String::toLowerCase)
				.forEach(word -> {
			        int[] count = almacenAlfabetico.get(word);
			        if (count == null) {
			            count = new int[1];
			            almacenAlfabetico.put(word, count);
			        }
			        count[0]++;
			        total++;
				});
	    }
    }


    /**
     * @param name
     * @throws IOException
     */
    private void processFile_NEW1(final String name) throws IOException {
    	/* Paula Diaz Blanes paula-maria.diaz@sopra.com */
	    try (final Stream<String> lines = Files.lines(Paths.get(name), StandardCharsets.ISO_8859_1)) {
	    	lines.flatMap(string -> Arrays.asList(string.split("[\\s\\p{Punct}¡“”‘¿?—…]+", 0)).stream())
				.filter(s -> s.trim().length() > 0)
				.map(String::toLowerCase)
				.forEach(word -> {
			        int[] count = almacenAlfabetico.get(word);
			        if (count == null) {
			            count = new int[1];
			            almacenAlfabetico.put(word, count);
			        }
			        count[0]++;
			        total++;
				});
	    }
    }

    /**
     * @param name
     * @throws IOException
     */
    private void processFile_OLD(final String name) throws IOException {
    	/* Paula Diaz Blanes paula-maria.diaz@sopra.com */
	    try (final Stream<String> lines = Files.lines(Paths.get(name), StandardCharsets.ISO_8859_1)) {
	    	lines
	    		//.onClose(() -> System.out.println("File closed"))
	    		.map(string -> string.split("[\\s\\p{Punct}¡“”‘¿?—…]"))
	    		.map(Arrays::asList)
	    		.map(list -> list.stream())
	    		.forEach(stream -> stream.filter(s -> s.trim().length() > 0)
					.map(String::toLowerCase)
					.forEach(word -> {
						int[] count = almacenAlfabetico.get(word);
						if (count == null) {
							count = new int[1];
							almacenAlfabetico.put(word, count);
						}
						count[0]++;
						total++;
					}));
	    }
    }

	void split(final Stream<String> stream) {
		stream
			.filter(s -> s.trim().length() > 0)
			.map(String::toLowerCase)
//			.forEach(this::compute);
			.forEach(word -> {
		        int[] count = almacenAlfabetico.get(word);
		        if (count == null) {
		            count = new int[1];
		            almacenAlfabetico.put(word, count);
		        }
		        count[0]++;
		        total++;
			});
	}

	void compute(final String word) {
        int[] count = almacenAlfabetico.get(word);
        if (count == null) {
            count = new int[1];
            almacenAlfabetico.put(word, count);
        }
        count[0]++;
        total++;
	}


    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        new WorterFrequenzRechner(args);
    }

    /**
     * 
     */
    private static void usage() {
        System.out.println("¿A qué le voy a calcular la frecuencia?");
    }
}
