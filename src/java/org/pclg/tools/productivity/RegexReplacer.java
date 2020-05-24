package org.pclg.tools.productivity;

/**
 * @author paceLucien
 * @since 28-abr-2011 13:10:13
 */
interface RegexReplacer {
    /**
     * Replaces the strings according to a regex.
     * @param line the string to be replaced.
     * @return the number of changes made.
     */
    String replace(String line);
}
