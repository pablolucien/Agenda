package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 */
public class Lector {
    /**
     * @param args gg
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String[] inputFiles = {
//				"C:/_Reise/catalog.txt",
				"C:/_Reise/ContenidoACME.txt",
				"C:/_Reise/ContenidoMercurio.txt",
				"C:/_Reise/ContenidoMinerva.txt",
		};
		for (final String inputFile : inputFiles) {
			generateOutputFile(inputFile);
		}
    }

	private static void generateOutputFile(final String inputFile)
			throws IOException {
		System.out.println("*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-  "
			+ inputFile + " */-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-*/-");
		final String volumeDescriptor = " El volumen de la unidad ";
		final String serialDescriptor = "mero de serie del volumen es";
		final String dirDescriptor = " Directorio de ";
		String volume = "[VOL UNKNOWN]";
		String dir = "[DIR UNKNOWN]";
		String file = "[FILE UNKNOWN]";
		final BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		try {
			String line;
			int totalFiles = 0;
			while ((line = reader.readLine()) != null) {
				int index;
				if (line.trim().length() == 0) {
					continue;
				} else if ((index = line.indexOf(volumeDescriptor)) >= 0) {
					volume = line.substring(index + volumeDescriptor.length());
					continue;
				} else if ((index = line.indexOf(serialDescriptor)) >= 0) {
					continue;
				} else if (line.contains("archivos") && line.contains("bytes")) {
					continue;
				} else if (line.contains("bytes libres")) {
					continue;
				} else if ((index = line.indexOf(dirDescriptor)) >= 0) {
					dir = line.substring(index + dirDescriptor.length());
					continue;
				} else if (line.length() > 37 && !line.contains("<DIR>")) {
					file = line.substring(36);
					System.out.printf("%15s > %15s > %15s \n", volume, dir, file);
					totalFiles++;
				}
			}
			System.out.printf("Total de archivos en la lista: %5d \n", totalFiles);
		} finally {
			reader.close();
		}
	}
}
