package org.pclg.tools.productivity;

import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * No implements RegexReplacer porque hace algo especial con otro fichero.
 * @author paceLucien
 * @since 28-abr-2011 13:33:37
 */
final class CasesReplacer {
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();

    public int replace(final File inFile) throws IOException {
        final Set<String> codes = new HashSet<>();
        LOGGER.fine("Procesando " + inFile);
        int cambios = 0;
        final BufferedReader reader = new BufferedReader(new FileReader(inFile));
        final File outFile = File.createTempFile("tmp", ".java", inFile.getParentFile());
        final PrintWriter writer1 = new PrintWriter(outFile);
		final StringWriter declarationsWriter = new StringWriter();
		final PrintWriter writer2 = new PrintWriter(declarationsWriter);
        String line;
        boolean changed = false;
		final Pattern pattern = Pattern.compile("(\\s*)case(\\s*)(\\d+)\\s*:(.*)");
		final Matcher matcher = pattern.matcher("");
        while ((line = reader.readLine()) != null) {
			matcher.reset(line);
            if (matcher.matches()) {
                final String codeError = matcher.group(3);
                if (!codes.contains(codeError)) {
                    codes.add(codeError);
                    line = new StringBuilder().append(matcher.group(1))
                        .append("case").append(matcher.group(2))
                        .append("CODE_ERREUR_").append(codeError).append(':')
                        .append(matcher.group(4)).toString();
                    cambios++;
                    writer2.println(new StringBuilder()
                        .append("\t/** Code de retour (").append(codeError)
                        .append("). */").toString());
                    writer2.println(new StringBuilder()
                        .append("\tprivate static final int CODE_ERREUR_")
                        .append(codeError).append(" = ").append(codeError)
                        .append(';').toString());
                    writer2.println();
                    changed = true;
                }
            }
            writer1.println(line);
        }
        writer1.close();
        writer2.close();
        reader.close();
        if (changed) {
            FileUtil.substituteFiles(outFile, inFile);
			addDeclarations(inFile, declarationsWriter.toString());
        } else {
            if (!outFile.delete()) {
                LOGGER.severe(new StringBuilder().append("No pude borrar ")
                    .append(outFile).append(". Borrarla manualmente ")
                    .toString());
            }
        }
       return cambios;
    }

	private void addDeclarations(final File inFile, final String declarations)
			throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(inFile));
		final File outFile = File.createTempFile("tmp", ".java", inFile.getParentFile());
		final PrintWriter writer = new PrintWriter(outFile);
		final String filename = inFile.getName();
		final String className = filename.substring(0, filename.indexOf(".java"));
		final Pattern patternBegin = Pattern.compile(".*class.*" + className + ".*");
		final Pattern patternEnd = Pattern.compile(".*\\{.*");
		boolean classBeginFound = false;
		boolean classEndFound = false;
		boolean declarationsAdded = false;
		final Matcher matcherBegin = patternBegin.matcher("");
		final Matcher matcherEnd = patternEnd.matcher("");
		String line;
		while ((line = reader.readLine()) != null) {
			writer.println(line);
			if (!classBeginFound) {
				matcherBegin.reset(line);
				if (matcherBegin.matches()) {
					classBeginFound = true;
				}
			}
			if (!classEndFound) {
				matcherEnd.reset(line);
				if (matcherEnd.matches()) {
					classEndFound = true;
				}
			}
			if (classBeginFound && classEndFound && !declarationsAdded) {
				writer.println(declarations);
				declarationsAdded = true;
			}
		}
		writer.close();
		reader.close();
		FileUtil.substituteFiles(outFile, inFile);
	}
}
