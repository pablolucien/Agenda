package org.pclg.tools.productivity;

import org.jpatterns.gof.ChainOfResponsibilityPattern;
import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author paceLucien
 * @since 29-abr-2011 9:08:54
 */
@ChainOfResponsibilityPattern
class ChainOfResponsibility {
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();

    private final List<RegexReplacer> replacers = new ArrayList<>();

    public void add(final RegexReplacer replacer) {
		replacers.add(replacer);
    }

    public int replace(final File inFile) throws IOException {
        LOGGER.fine("Procesando " + inFile);
        int cambios = 0;
        final BufferedReader reader = new BufferedReader(new FileReader(inFile));
        final File outFile1 = File.createTempFile("tmp", ".java", inFile.getParentFile());
        final PrintWriter writer1 = new PrintWriter(outFile1);
        String line;
        boolean changed = false;
        while ((line = reader.readLine()) != null) {
            for (final RegexReplacer replacer : replacers) {
                final String newLine = replacer.replace(line);
                if (!newLine.equals(line)) {
                    changed = true;
                    cambios++;
                    line = newLine;
                }
            }
            writer1.println(line);
        }
        writer1.close();
        reader.close();
        if (changed) {
            FileUtil.substituteFiles(outFile1, inFile);
        } else {
            if (!outFile1.delete()) {
                LOGGER.severe(String.format("No pude borrar %s. Borrarla manualmente.", outFile1));
            }
        }
       return cambios;
    }

}
