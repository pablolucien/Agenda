package org.pclg.tools.productivity;

/**
 * ¡Yo soy un "RegexReplacer" que no usa regex, como molo!
 *
 * @author paceLucien
 * @since 28-abr-2011 13:13:50
 */
public class ServicesReplacer implements RegexReplacer {

	@Override
    @SuppressWarnings({"AssignmentToMethodParameter"})
    public String replace(String line) {
        if (line.startsWith("import cnaf.cristal.services.")
                && !line.contains(".Cssr00")
                && !line.contains(".sanscnafdate.")) {
            line = line.replace("import cnaf.cristal.services.",
                "import cnaf.cristal.services.sanscnafdate.");
        }

        return line;
    }
}