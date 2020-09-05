package org.pclg.tools.productivity;

import org.pclg.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Para hacer los cambios automáticos de la DM2006D (Eliminación de CnafDate.
 *
 * @author paceLucien
 * @since 30-mar-2011 13:41:30
 */
public class Replacer {
	private enum FgType {
        FGG {
            @Override
			public String toString() {
                return "fgg";
            }
        },
        FGE {
            @Override
			public String toString() {
                return "fge";
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
    private static final Map<String, String> dicosReplacementMap =
        new LinkedHashMap<>();
    private static final Map<String, String> javasReplacementMap =
        new LinkedHashMap<>();

    private static int cambios;

	/** Prevents instantiation. */
	private Replacer() {
	}

    public static void main(final String[] args) throws IOException {
        final File baseDir = new File("C:/C3410.1");
        final String project = args[0].toLowerCase();
        final FgType typeFg;
        if (new File(baseDir, project
                + "metier/src/main/java/cnaf/cristal/majsit/metier/fge/"
                + project).exists()) {
            typeFg = FgType.FGE;
        } else if (new File(baseDir, project
                + "metier/src/main/java/cnaf/cristal/majsit/metier/fgg/"
                + project).exists()) {
            typeFg = FgType.FGE;
        } else {
            throw new IllegalArgumentException(project);
        }

        fillDicosReplacementMap(typeFg);
        fillJavasReplacementMap();
        replaceDicos(baseDir, project);
        replaceJavas(baseDir, project, typeFg);
        replaceInjectionMapping(baseDir, project, typeFg);
        LOGGER.info("Hechos " + cambios + " cambios (aprox).");
        if (cambios > 0) {
            LOGGER.info("Don't forget to refresh the project (F5), INIT "
                    + "and organize imports (ctrl+shift-O)");
        }
    }

    private static void fillJavasReplacementMap() throws IOException {
       // final BufferedReader reader = new BufferedReader(new FileReader("D:/plucien/javas_replacements.txt"));
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                ClassLoader.getSystemResourceAsStream("javas_replacements.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            final String trimmedLine = line.trim();
            if (trimmedLine.length() == 0 || trimmedLine.charAt(0) == '#') {
                continue;
            }
            final String[] data = line.split("\\|");
            if (data.length == 1) {
                javasReplacementMap.put(data[0], null);
            } else if (data.length == 2) {
                javasReplacementMap.put(data[0], data[1]);
            } else {
                throw new IllegalArgumentException(line);
            }
        }
        reader.close();
        LOGGER.fine("javasReplacementMap = " + javasReplacementMap);
    }

    private static void replaceJavas(final File baseDir, final String project,
            final FgType fgType)
            throws IOException {
        final File[] possibleJavaDirs = {
                new File(baseDir, project
                        + "metier/src/main/java/cnaf/cristal/majsit/metier/"
                        + fgType + '/' + project),
                new File(baseDir, project
                        + "metier/src/main/java/cnaf/cristal/majsit/metier/"
                        + fgType + '/' + project + "/util"),
                new File(baseDir, project
                        + "metier/src/main/java/cnaf/cristal/majsit/composant/"
                        + fgType + '/' + project),
                new File(baseDir, project
                        + "metier/src/test/java/cnaf/cristal/majsit/metier/"
                        + fgType + '/' + project),
                new File(baseDir, project
                        + "presentation/src/main/java/cnaf/cristal/majsit/presentation/"
                        + fgType + '/' + project),
        };
        for (final File javaDir : possibleJavaDirs) {
            if (javaDir.exists()) {
                LOGGER.fine(new StringBuilder().append("javaDir = ")
                    .append(javaDir).toString());
                replaceJavasDir(javaDir);
            }
        }
    }

    private static void replaceJavasDir(final File javasDir) throws IOException {
        final File[] contents = javasDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".java");
            }
        });
        for (final File file : contents) {
            LOGGER.fine(new StringBuilder().append("file = ")
                .append(file).toString());
            final int cambiosCases = new CasesReplacer().replace(file);
            cambios += cambiosCases;
            replaceStrings(file, javasReplacementMap);
            final ChainOfResponsibility chain = new ChainOfResponsibility();
            chain.add(new DataLayerConverterReplacer());
            chain.add(new NegationsReplacer());
            chain.add(new ServicesReplacer());
            chain.add(new MethodReplacer(MethodReplacer.MethodName.GETINTANNEE));
            chain.add(new MethodReplacer(MethodReplacer.MethodName.GETINTMOIS));
            chain.add(new MethodReplacer(MethodReplacer.MethodName.GETINTJOUR));
            cambios += chain.replace(file);
        }
    }

    private static void fillDicosReplacementMap(final FgType typeFg) {
        if (typeFg == FgType.FGE) {
            dicosReplacementMap.put("genType=\"Simple\"", "genType=\"SimpleSansCnafDate\"");
        } else if (typeFg == FgType.FGG) {
            dicosReplacementMap.put("genType=\"Simple\"", "genType=\"SimpleSansCnafDate2Met\"");
        } else {
            throw new IllegalArgumentException(typeFg.toString());
        }
        final String[] genTypes = {"IHM1", "Simple2", "WS", "SimpleWS"};
        for (final String genType : genTypes) {
            dicosReplacementMap.put(generateAttribGentype(genType),
                generateAttribGentype(new StringBuilder().append(genType)
                    .append("SansCnafDate").toString()));
        }
        dicosReplacementMap.put("cnaf.framework.daq.CnafDateFieldHandler",
            "cnaf.framework.daq.DateFieldHandler");
        LOGGER.fine("dicosReplacementMap = " + dicosReplacementMap);
    }

    private static String generateAttribGentype(final String genType) {
        return new StringBuilder().append("genType=\"").append(genType)
            .append('\"').toString();
    }

    private static void replaceDicos(final File baseDir, final String project) throws IOException {
        final String metier = project + "metier";
        final String presentation = project + "presentation";
        replaceDicosSubproject(baseDir, metier);
        replaceDicosSubproject(baseDir, presentation);
    }

    private static void replaceInjectionMapping(final File baseDir,
            final String project, final FgType typeFg) throws IOException {
        final File defDir = new File(baseDir, typeFg.toString()
            + "presentation-webContent/src/main/resources/daqInjection/acte/"
            + project + "/def");
        final File[] contents = defDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("Mapping.xml");
            }
        });
        if (contents != null) {
            for (final File file : contents) {
                replaceStrings(file, dicosReplacementMap);
            }
        }
    }

    private static void replaceDicosSubproject(final File baseDir,
            final String subproject) throws IOException {
        final File dicosDir = new File(baseDir, subproject + "/dico-" + subproject);
        final File[] contents = dicosDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".xml");
            }
        });
        for (final File file : contents) {
            replaceStrings(file, dicosReplacementMap);
        }
    }

    private static void replaceStrings(final File inFile,
            final Map<String, String> replacementMap) throws IOException {
        LOGGER.fine("Procesando " + inFile);
        final BufferedReader reader = new BufferedReader(new FileReader(inFile));
        final File outFile = File.createTempFile("tmp", ".tmp", inFile.getParentFile());
        final PrintWriter writer = new PrintWriter(outFile);
        boolean changed = false;
        String line;
        outer:
        while ((line = reader.readLine()) != null) {
            for (final String key : replacementMap.keySet()) {
                if (line.contains(key)) {
                    final String value = replacementMap.get(key);
                    if (value == null) {
                        continue outer;
                    }
                    line = line.replace(key, value);
                    changed = true;
                    cambios++;
                }
            }
            writer.println(line);
        }
        writer.close();
        reader.close();
        FileUtil.replaceFile(inFile, outFile, changed);
    }
}
