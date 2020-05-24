package org.pclg.tools.productivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author paceLucien
 * @since 28-abr-2011 15:40:03
 */
public class RegexTester {
    /** Pattern para reemplazar las negaciones. */
//    public static final String REGEX_IN = ".*(!\\((\\w+) == null\\)).*";
//    public static final String LINE = "\t\tif (!(epcevfSalETI == null))";

    /** Pattern para !DataLayerConverter.isDate*Vide(var). */
//    public static final String REGEX_IN =
//        ".*(!*DataLayerConverter\\.isDate.*Vide)\\(([\\w.]+)\\).*";
    //public static final String LINE = "\t\tif (!DataLayerConverter.isDateBgVide(date2))";


    /** Pattern para !DataLayerConverter.isDate*Vide(var.met()). */
//    public static final String REGEX_IN =
//        ".*(!*DataLayerConverter\\.isDate.*Vide)\\(([\\w.]+\\(\\))\\).*";
//    public static final String LINE = "\t\t\t\tif (!DataLayerConverter.isDateVsbVide(vsbSalaire.getFpcevf()))";

    /** Pattern para var..getInt*(). */
    private static final String REGEX_IN =
        ".*[\\(\\s\\&\\|]!*((\\w+)\\.getIntMois\\s*\\(\\s*\\)).*";
    private static String line = "\t\t\t\t\t\t\tif ((dmoief.getIntMois() <= 2007 && dmoief.getIntMois () != 5)";
    //public static  String line = "\tCnafDate epcevfCID = new CnafDate(epcevf.getIntAnnee(), epcevf";





    private static final Pattern PATTERN = Pattern.compile(REGEX_IN);

	private RegexTester() {
	}


	public static void main(final String[] args) {
        final Matcher matcher = PATTERN.matcher(line);
        while (matcher.matches()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            line = line.replace(matcher.group(1), "DateUtil.getIntAnnee(" + matcher.group(2) + ')');
            System.out.println(line);
            matcher.reset(line);               
        }


//        System.out.println(matcher.group(2));
        //System.out.println(matcher.group(3));
    }
}
