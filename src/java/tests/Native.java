package tests;/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. — Donald Knuth
 * 
 * Creado el 12-Jun-2008
 */

/**
 * @author Un autor en busca de personajes.
 * @since 12-Jun-2008
 */
final class Native {
    static {
        System.loadLibrary("tests.Native");
     }
     
     public native void erewhon();
}
