package org.pclg.tools.productivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author paceLucien
 * @since 31-mar-2011 14:22:24
 */
public class RegexTutor {

	private RegexTutor() {
	}

	public static void main(final String[] args) throws IOException {
//        final String input = "				if(vsbEnfPotentiel.getEcolca() != null && !vsbEnfPotentiel.getEcolca() == null	)";
//        final String input2_ESTA_NO_FUNCIONA_DE_MOMENTO = "				if(vsbEnfPotentiel.getEcolca() != null && !(vsbEnfPotentiel.getEcolca() == null)	)";
//        final String input3 = "\t\tif (!situation.getColcae().equals(\"\") && !situation.getEcolca() == null\t && !situation.getFcolca() == null\t)";

//        System.out.println("Replacer.REGEX_IN1 = " + Replacer.REGEX_IN1);

//        final Pattern pattern = Pattern.compile(Replacer.REGEX_IN1);
//        probar(pattern, input);
//        probar(pattern, input2_ESTA_NO_FUNCIONA_DE_MOMENTO);
//        probar(pattern, input3);
//        probar(pattern, " salk kkk !pepito() == null colega");
//        probar(pattern, " salk kkk !pepito == null colega");

        //probar2(TEST_STR1);
        //probar2(TEST_STR2);
        //probar2(TEST_STR3);
        probar2(TEST_STR4);
    }


    /*
    http://stackoverflow.com/questions/2631183/c-regex-how-to-specify-to-only-match-first-occurrence
   	I believe you just need to add a lazy qualifier on the first example.
	Whenever a wild card is "eating too much", you either need a lazy qualifier on
	the wild card or, in a more complicated scenario, look ahead. Add a lazy
	qualifier at the top (.+? in place of .+), and you should be good
    */
    private static final Pattern PATTERN2 = Pattern.compile(DataLayerConverterReplacer.REGEX_IN4);
    public static final String TEST_STR1 = "\t\tif (DataLayerConverter.isDateBgVide(fdroit) || !DataLayerConverter.isDateBgVide(pepito))";
    public static final String TEST_STR2 = "\t\tif (!DataLayerConverter.isDateBgVide(fdroit))";
    public static final String TEST_STR3 = "\t\tif (DataLayerConverter.isDateBgVide(propreDate) && DataLayerConverter.isDateBgVide(autreDate))";
    private static final String TEST_STR4 = "if (!DataLayerConverter.isDateVsbVide(vsbETI.getFpcevf()))";
    private static void probar2(String line) {
        final Matcher matcher = PATTERN2.matcher(line);
        System.out.println("In  -> " + line  + " en pos " + matcher.regionStart());
        while (matcher.matches()) {
            System.out.println("1 - " + matcher.group(1) + " en pos " + matcher.start(1));
            System.out.println("2 - " + matcher.group(2) + " en pos " + matcher.start(2));
            System.out.println("3 - " + matcher.group(3) + " en pos " + matcher.start(3));
            line = line.replaceFirst("\\Q" + matcher.group(1) + "\\E", "")
                .replaceFirst("\\Q" + matcher.group(2) + "\\E", matcher.group(3) + " == null");
            matcher.reset(line);
            System.out.println(line);
            System.out.println();
        }
        final Pattern pattern = Pattern.compile(NegationsReplacer.REGEX_IN1);
        probar(pattern, line);
    }

    private static void probar(final Pattern pattern, String line) {
        final Matcher matcher = pattern.matcher(line);
        System.out.println("In  -> " + line);
        while (matcher.matches()) {
//            System.out.println(matcher.group(0));
//            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
            line = line.replace(matcher.group(1), matcher.group(2) + " != null");
            matcher.reset(line);
        }
        System.out.println("Out -> " + line);
    }
}
