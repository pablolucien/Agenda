package tests;

import org.pclg.xtras.ClassPathHacker;
import org.pclg.xtras.LibPathHacker;

import java.io.IOException;

/*
 * We should forget about small efficiencies, say about 97% of the time: 
 * Premature optimization is the root of all evil. — Donald Knuth
 * 
 * Creado el 12-Jun-2008
 */

/**
 * @author Un autor en busca de personajes.
 * @since 12-Jun-2008
 */
final class UnsatisfiedLinkErrorTest {
    
    public static void main(final String[] args) {
        try {
            LibPathHacker.addDir("C:/plucien/IBM/rationalsdp6.0/workspace/kk/Native/Debug");
			final String libPath = System.getProperty("java.library.path");
			System.out.println(libPath);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            ClassPathHacker.addFile("c:/plucien");
            Class.forName("tests.EnOtroClassPath");
        } catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        
        doTest();
        doTest();
    }

    /**
     * 
     */
    private static void doTest() {
        try {
            new Native().erewhon();
        } catch (final Throwable ex) {
            System.err.println("==========================================");
            ex.printStackTrace();
        }
    }
}