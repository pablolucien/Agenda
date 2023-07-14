package tests;

import org.pclg.tools.Chrono;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. � Donald Knuth
 * 
 * Creado el 10-Jul-2008
 */

/**
 * @author Un autor en busca de personajes.
 * @since 10-Jul-2008
 */
final class BuchstabenFrequenzRechner {
    static final class FrequenzPaar implements Comparable<FrequenzPaar> {
        private final Character character;
        private final int count;

        /**
         * @param character
         * @param count
         */
        FrequenzPaar(final Character character, final int count) {
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
        public Character getCharacter() {
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
    
    private final Map<Character, int[]> almacenAlfabetico = new TreeMap<>();
    private final Map<Character, int[]> almacenAlfabeticoNIO = new TreeMap<>();
    private float total;
    
    /**
     * @throws IOException
     * 
     */
    private BuchstabenFrequenzRechner(final String[] args) throws IOException {
        final int cron = Chrono.getChrono();
		for (int j = 0; j < 20; j++) {
            for (final String arg : args) {
                Chrono.start(cron);
                processFile(arg);
                Chrono.mark(cron);
                System.out.println("------------------------>" + Chrono.timeDetail(Chrono.elapsed(cron)));

                Chrono.start(cron);
                processFileNIO(arg);
                Chrono.mark(cron);
                System.out.println("------------------------>" + Chrono.timeDetail(Chrono.elapsed(cron)));

                System.out.println("Son iguales = " + almacenAlfabetico.equals(almacenAlfabeticoNIO));
            }
		}
		showResult();
    }

    /**
     * 
     */
    private void showResult() {
        final Set<Character> characterSet = almacenAlfabetico.keySet();
        final List<FrequenzPaar> almacenNumerico =
                new ArrayList<>(characterSet.size());
        for (final Character character : characterSet) {
            final int[] count = almacenAlfabetico.get(character);
            System.err.println(new StringBuilder().append("[").append(character)
                .append("] occurs ").append(count[0]).append(" times").toString());

            almacenNumerico.add(new FrequenzPaar(character, count[0]));
        } 

        System.err.println();
        System.err.println("--------------------------------------");
        System.err.println();
        
        Collections.sort(almacenNumerico);
        for (final FrequenzPaar data : almacenNumerico) {
            final int count = data.getCount();
            final Character character = data.getCharacter();
            System.err.println(new StringBuilder().append("[").append(character)
                    .append("] occurs ").append(count).append(" times (")
                    .append(count / total * 100).append(" %)").toString());
        }
    }

    /**
     * @param name
     * @throws IOException
     */
    private void processFile(final String name) throws IOException {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(name));
            int c;
            while ((c = stream.read()) != -1) {
                final char ch = Character.toLowerCase((char) c);
                if (Character.isWhitespace(ch)) {
                    continue;
                }
                final Character character = new Character(ch);
                int[] count = almacenAlfabetico.get(character);
                if (count == null) {
                    count = new int[1];
                    almacenAlfabetico.put(character, count);
                }
                count[0]++;
                total++;
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        
    }


    /**
     * @param name
     * @throws IOException
     */
    private void processFileNIO(final String name) throws IOException {
		FileInputStream fin = null;
		try {
        	fin = new FileInputStream(name);
            final FileChannel fc = fin.getChannel();
            final ByteBuffer buffer = ByteBuffer.allocate( 1024 );
            fc.read(buffer);
            int bytesRead;
            while ((bytesRead = fc.read( buffer )) != -1) {
				buffer.flip();
				for (int ii = 0; ii < bytesRead; ii++) {
	                final int c = buffer.get();
	                final char ch = Character.toLowerCase((char) c);
	                if (Character.isWhitespace(ch)) {
	                    continue;
	                }
	                final Character character = new Character(ch);
	                int[] count = almacenAlfabetico.get(character);
	                if (count == null) {
	                    count = new int[1];
	                    almacenAlfabeticoNIO.put(character, count);
	                }
	                count[0]++;
	                total++;
                }
				buffer.clear();
            }
        } finally {
            if (fin != null) {
                fin.close();
            }
        }
        
    }

    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        new BuchstabenFrequenzRechner(args);
    }

    /**
     * 
     */
    private static void usage() {
        System.err.println("¿A qué le voy a calcular la frecuencia?");
    }
}
