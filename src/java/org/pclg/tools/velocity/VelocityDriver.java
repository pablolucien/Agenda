package org.pclg.tools.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.FileTools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author El Coyote Cojo
 * @since 18-mar-2011 14:17:44
 */
public class VelocityDriver {
	private static final Logger LOGGER = LoggerFactory.makeSimpleLogger();
    private static final String ARRAY_INDICATOR = "[]";
    private static final String SPLIT_REGEX = ";";

    /**
	 * Prevents instantiation.
	 */
	private VelocityDriver() {
	}

	public static void main(final String[] args) throws IOException {
		if (args.length != 1) {
			usage();
		}

		Writer writer = null;
		try {
			final VelocityEngine engine = new VelocityEngine();
//			engine.setProperty( VelocityEngine.RUNTIME_LOG_LOGSYSTEM, LOGGER);
			engine.init();
			final Context context = new VelocityContext();
			final Properties properties = FileTools.loadProperties(args[0]);
			for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
				final String key = (String) entry.getKey();
				final String value = (String) entry.getValue();
				if (value.startsWith(ARRAY_INDICATOR)) {
					context.put(key, value.substring(ARRAY_INDICATOR.length())
                        .split(SPLIT_REGEX));
				} else {
					context.put(key, value);
				}
			}
            final String dirName = properties.getProperty("output.dir");
            final File dir = dirName == null ? new File(".") : new File(dirName);
            final Template template =
				engine.getTemplate(properties.getProperty("input.file"));
			final boolean overwrite =
				Boolean.parseBoolean(properties.getProperty("overwrite"));
			final String fileName = properties.getProperty("output.file");
            final File outFile = overwrite ? new File(dir, fileName) :
				FileTools.nextAvailableFile(dir, fileName);
			writer = new FileWriter(outFile);
			template.merge(context, writer);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (final IOException ex) {
					LOGGER.log(Level.SEVERE, "Error", ex);
				}
			}
		}
	}

	private static void usage() {
		LOGGER.severe("\n\nUsage: java VelocityDriver <properties file>,\n"
			+ "where <properties file> must define the properties:\n"
			+ "\tinput.file=the velocity template file\n"
			+ "\toutput.file=guess what ;-)\n"
			+ "\toutput.dir=guess what ;-). If not defined, CWD is used.\n"
			+ "\toverwrite=true|false. If not defined, then false.\n"
			+ "\tand the VTL references used in <input.file>\n");
		System.exit(-1);
	}
}