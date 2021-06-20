package org.pclg.root;

import org.apache.logging.log4j.Logger;
import org.pclg.log.LoggerFactory;
import org.pclg.tools.ToolBox;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

class DNI {
	private static final String LETRA_DE_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";
	private static final Logger LOGGER = LoggerFactory.makeLog4J();

	private DNI() {
	}

	public static void main(final String[] args) {
		if (args.length == 0) {
			LOGGER.warn("Usage: java DNI <dni> ...");
			System.exit(THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
		}
		for (final String arg : args) {
			if (!ToolBox.isInteger(arg)) {
				LOGGER.error(arg + " no es num�rico");
				continue;
			}
			LOGGER.warn(arg + ' ' + LETRA_DE_CONTROL.charAt(
	            Integer.parseInt(arg) % LETRA_DE_CONTROL.length()));
		}
	}
}
