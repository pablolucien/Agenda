package misc.sopra;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.pclg.log.LoggerFactory;

import java.io.Console;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

final class PomUpdater {
	private static final String SW_VERSION;
	static {
        final Console console;
		if ((console = System.console()) == null) {
			SW_VERSION = "3310";
		} else {
			SW_VERSION = console.readLine("Introduzca el n£mero de version (3300, 3310, 3320, etc): ");
		}
	}

    @SuppressWarnings("deprecation")
    private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
    private static final String UNIX_STYLE_LINE_SEPARATOR = "\n";
    private static final String LIQUIDATION_WEB_PROJECT = "nci-liquidation-web";
    private static final String POM_XML = "pom.xml";
    private static final String PARENT_NODE_NAME = "parent";
    private static final String VERSION_NODE_NAME = "version";
    private static final int BUFFER_CAPACITY = 1024;
    private static final String MSG_UNKNOWN_VERSION =
            "Imposible determinar la version de POM. Abortando la ejecuci¢n.";
    private static final String MSG_KNOWN_VERSION =
            "Utilizando la version de POM: [%s]";

    /**
     *
     * @param args the arguments to this appliction.
     * @throws IOException guess when...
     * @throws org.jdom.JDOMException if errors with the parsing of the pom.
     */
    public static void main(final String[] args) throws IOException, JDOMException {
        final File baseDir = new File("C:/C" + SW_VERSION + ".1");
        final String pomVersion;
        switch (args.length) {
        case 0:
            pomVersion = obtenerVersionPomLiquidation(baseDir);
            if (pomVersion == null) {
                LOGGER.severe(MSG_UNKNOWN_VERSION);
                System.exit(1);
            }
            LOGGER.info(String.format(MSG_KNOWN_VERSION, pomVersion));
            break;
        case 1:
            if (args[0].equalsIgnoreCase("-h")
                    || args[0].equalsIgnoreCase("-help")
                    || args[0].equalsIgnoreCase("-?")) {
                usage();
            }
            pomVersion = args[0];
            break;
        default:
            usage();
            return;
        }

        for (final File fstLevelDir : baseDir.listFiles(new FileFilter() {
		            @Override public boolean accept(final File pathname) {
		                return pathname.isDirectory();
		            }
	        	})) {
            final File pomFile = new File(fstLevelDir, POM_XML);
            if (pomFile.exists()) {
                processUsingJdom(pomFile, pomVersion);
            }
        }
    }

    private static void usage() {
        LOGGER.severe("Uso: java misc.sopra.PomUpdater [version_pom].\n"
                + "(Si no se indica version_pom se usa la de liquidation)");
        System.exit(1);
    }


    private static void processUsingJdom(final File file, final String verNr)
            throws IOException, JDOMException {
        final StringBuilder stringBuilder = new StringBuilder(BUFFER_CAPACITY);
        stringBuilder.append("Procesando ").append(file);

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(file);
        final Element rootElement = doc.getRootElement();
        final Namespace namespace = rootElement.getNamespace();
        final Element parent = rootElement.getChild(PARENT_NODE_NAME, namespace);
        if (parent != null) {
            final Element version = parent.getChild(VERSION_NODE_NAME, namespace);
            final String newVersion = SW_VERSION + '.' + verNr + "-SNAPSHOT";
            final boolean changed = !newVersion.equals(version.getText());
            if (changed) {
                version.setText(newVersion);
                final File outFile = new File(file.getParent(), "pom_pclg.xml");
                final PrintWriter printWriter = new PrintWriter(outFile, "UTF-8");
                final Format rawFormat =
                    Format.getRawFormat().setLineSeparator(UNIX_STYLE_LINE_SEPARATOR);
                final XMLOutputter outputter = new XMLOutputter(rawFormat);
                stringBuilder.append(" CAMBIADA");
                printWriter.print(outputter.outputString(doc));
                printWriter.close();
                if (file.delete()) {
                    if (!outFile.renameTo(file)) {
                        stringBuilder.append(" No pude renombrar el pom final");
                    }
                } else {
                    stringBuilder.append(" No pude borrar el pom inicial");
                }
            } else {
                stringBuilder.append(" no hay cambios");
            }
        } else {
            stringBuilder.append(" ** No encontré el nodo <parent>.Proceso abortado");
        }
        LOGGER.info(stringBuilder.toString());
    }

    private static String obtenerVersionPomLiquidation(final File baseDir)
            throws IOException, JDOMException {
        final File liqDir = new File(baseDir, LIQUIDATION_WEB_PROJECT);
        final File liqPom = new File(liqDir, POM_XML);
        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(liqPom);
        final Element rootElement = doc.getRootElement();
        final Namespace namespace = rootElement.getNamespace();
        final Element parent = rootElement.getChild(PARENT_NODE_NAME, namespace);
        final String version = parent.getChild(VERSION_NODE_NAME, namespace).getText();
        return version.substring(version.indexOf('.') + 1, version.indexOf('-'));
	}
}
