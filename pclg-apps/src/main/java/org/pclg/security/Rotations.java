package org.pclg.security;

import org.pclg.tools.Chrono;

import static org.pclg.Globals.THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING;

/**
 * Esta clase provee métodos que implementan los algoritmos de 'obfuscación'
 * Rot13 y Rot47 así como memfrob.
 */
final class Rotations {
    private static final char[] ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] abc = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final char[] printables = new char[94];
    static {
        for (int ii = 0; ii < printables.length; ii++) {
            printables[ii] = (char) (ii + 33);
        }
    }

    private Rotations() {
    }

    private static String rot47(final String value) {
        final int len = value.length();
        final char[] chars = value.toCharArray();
        outer:
        for (int ii = 0; ii < len; ii++) {
            for (int jj = 0; jj < 94; jj++) {
                if (chars[ii] == printables[jj]) {
                    chars[ii] = printables[(jj + 47) % 94];
                    continue outer;
                }
            }
        }
        return new String(chars);
    }


    private static String rot13(final String value) {
        final int len = value.length();
        final char[] chars = value.toCharArray();
        outer:
        for (int ii = 0; ii < len; ii++) {
            for (int jj = 0; jj < 26; jj++) {
                if (chars[ii] == abc[jj]) {
                    chars[ii] = abc[(jj + 13) % 26];
                    continue outer;
                }
                if (chars[ii] == ABC[jj]) {
                    chars[ii] = ABC[(jj + 13) % 26];
                    continue outer;
                }
            }
        }
        return new String(chars);
    }

    /**
     * The GNU C library, a set of standard routines available for use in
     * computer programming, contains a function—memfrob()[13]—which has a
     * similar purpose to ROT13, although it is intended for use with arbitrary
     * binary data. The function operates by combining each byte with the binary
     * pattern 00101010 (42) using the exclusive or (XOR) operation. This
     * effects a simple XOR cipher. Like ROT13, memfrob() is self-reciprocal,
     * and provides a similar, virtually absent, level of security.The GNU C
     * library, a set of standard routines available for use in computer
     * programming, contains a function—memfrob()[13]—which has a similar
     * purpose to ROT13, although it is intended for use with arbitrary binary
     * data. The function operates by combining each byte with the binary
     * pattern 00101010 (42) using the exclusive or (XOR) operation. This
     * effects a simple XOR cipher. Like ROT13, memfrob() is self-reciprocal,
     * and provides a similar, virtually absent, level of security.
     *
     * @param value el byte[] a 'cifrar'. su contenido no es modificado.
     *
     * @return una copia 'cifrada' de value
     */
    public static byte[] memfrob(final byte[] value) {
        final byte[] retval = new byte[value.length];
        for (int ii = 0; ii < retval.length; ii++) {
            retval[ii] = (byte) (value[ii] ^ THE_ANSWER_TO_THE_ULTIMATE_QUESTION_OF_LIFE_THE_UNIVERSE_AND_EVERYTHING);
        }
        return retval;
    }


    public static void main(final String[] args) {
        final int handle = Chrono.getChrono();
        for (int jj = 0; jj < 10; jj++) {
            Chrono.start(handle);
            for (int ii = 0; ii < 10000; ii++) {
                rot13("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
            }
            Chrono.mark(handle);
            System.out.println(Chrono.timeDetail(Chrono.elapsed(handle)));

            Chrono.start(handle);
            for (int ii = 0; ii < 10000; ii++) {
                rot47("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
            }
            Chrono.mark(handle);
            System.out.println(Chrono.timeDetail(Chrono.elapsed(handle)));
            System.out.println();
        }

        System.out.println(rot13("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));
        System.out.println(rot47("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));
        System.out.println(rot47("%96 \"F:4< qC@H? u@I yF>AD ~G6C %96 {2KJ s@8]"));
    }
}