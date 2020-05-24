package tests;

import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * @author Pablo
 * @since 20/01/13 14:08
 */
public class Matcher {
	/** Logger for this class. */
	private static final Logger LOGGER = LoggerFactory.make();

	private static class Entry implements Comparable {
		private final String name;
		private final String path;

		private Entry(final String name, final String path) {
			this.name = name;
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}

		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Entry other = (Entry) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public String toString() {
			return path + '\\' + name;
		}

		@Override
		public int compareTo(final Object o) {
			return name.compareTo(((Entry) o).name);
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws IOException {
		Set<Entry> set1 = generateSet("C:/_Reise/ContenidoMinervaProcesadoPorLector.txt");
		Set<Entry> set2 = generateSet("C:/_Reise/ContenidoMercurioProcesadoPorLector.txt");
		Set<Entry> intersection = new TreeSet<Entry>(set1);
		intersection.retainAll(set2);
		System.out.println(intersection.size());
		System.out.println(intersection);

		set2 = generateSet("C:/_Reise/ContenidoACMEProcesadoPorLector.txt");
		intersection = new TreeSet<Entry>(set1);
		intersection.retainAll(set2);
		System.out.println(intersection.size());
		System.out.println(intersection);

		set1 = set2;
		set2 = generateSet("C:/_Reise/ContenidoMercurioProcesadoPorLector.txt");
		intersection = new TreeSet<Entry>(set1);
		intersection.retainAll(set2);
		System.out.println(intersection.size());
		System.out.println(intersection);
	}

	private static Set<Entry> generateSet(final String fileName)
			throws IOException {
		LOGGER.info("Procesando " + fileName + " */*/*/");
		final BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try {
			final Set<Entry> result =  new HashSet<Entry>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0 || line.indexOf('>') < 0) {
					continue;
				}
				final String[] strings = line.split(">");
				if (!result.add(new Entry(strings[2].trim(), strings[1].trim()))) {
					LOGGER.warning(strings[2] + " Ya está en la lista");
				}
			}

			return result;
		} finally {
			reader.close();
		}
	}
}
