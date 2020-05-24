package org.pclg.filesystem;

import junit.framework.TestCase;

import java.io.File;
import java.util.Properties;

public final class DirQueueTest extends TestCase {
    private Properties properties;
    private final File cwd = new File(".").getAbsoluteFile();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        properties = new Properties();
        final String canonicalName = DirQueue.class.getCanonicalName();
        properties.setProperty(canonicalName + ".queueSize", String.valueOf(1));
        properties.setProperty(canonicalName + ".cwdPointer", String.valueOf(0));
        properties.setProperty(canonicalName + ".dir0", cwd.getAbsolutePath());
    }

    public void testCurrentDir() {
        final DirQueue queue = new DirQueue(properties);
        assertEquals(1, queue.size());
        assertEquals(cwd, queue.cwd());
    }

    public void testCurrentDirNull() {
        final DirQueue queue = new DirQueue(new Properties());
        assertEquals(0, queue.size());
        assertEquals(null, queue.cwd());
    }

    public void testCreateDirQueue() {
        final DirQueue queue = new DirQueue(properties);
        assertEquals(1, queue.size());
        assertEquals(cwd, queue.cwd());
    }
    
    public void testFwd() {
        final DirQueue queue = new DirQueue(properties);
        assertEquals(cwd, queue.fwd());
        final File tmpDir1 = new File(cwd, "tmpDir1"); //        File.createTempFile("tmpDir", "kkk", cwd);
        tmpDir1.mkdirs();
        tmpDir1.deleteOnExit();
        assertEquals(tmpDir1, queue.fwd(tmpDir1));

        final File tmpDir2 = new File(cwd, "tmpDir2");
        tmpDir2.mkdirs();
        tmpDir2.deleteOnExit();
        assertEquals(tmpDir2, queue.fwd(tmpDir2));
    }
    
    public void testBack() {
        final DirQueue queue = new DirQueue(properties);
        final File tmpDir1 = new File(cwd, "tmpDir1");
        tmpDir1.mkdirs();
        tmpDir1.deleteOnExit();
        queue.fwd(tmpDir1);
        final File tmpDir2 = new File(cwd, "tmpDir2");
        tmpDir2.mkdirs();
        tmpDir2.deleteOnExit();
        queue.fwd(tmpDir2);
        assertEquals(tmpDir1, queue.back());
        assertEquals(cwd, queue.back());
        assertEquals(cwd, queue.back());
        assertEquals(cwd, queue.back());
    }
    
    public void testBackAndForth() {
        final DirQueue queue = new DirQueue(properties);
        final File tmpDir1 = new File(cwd, "tmpDir1");
        tmpDir1.mkdirs();
        tmpDir1.deleteOnExit();
        queue.fwd(tmpDir1);
        final File tmpDir2 = new File(cwd, "tmpDir2");
        tmpDir2.mkdirs();
        tmpDir2.deleteOnExit();
        queue.fwd(tmpDir2);
        final File tmpDir3 = new File(cwd, "tmpDir3");
        tmpDir3.mkdirs();
        tmpDir3.deleteOnExit();
        queue.fwd(tmpDir3);

        assertEquals(tmpDir2, queue.back());
        assertEquals(tmpDir3, queue.fwd());
        queue.back();
        assertEquals(tmpDir1, queue.back());
        assertEquals(cwd, queue.back());
        assertEquals(cwd, queue.back());
        assertEquals(tmpDir1, queue.fwd());
        assertEquals(tmpDir2, queue.fwd());
        assertEquals(tmpDir3, queue.fwd());
        assertEquals(tmpDir3, queue.fwd());
        assertEquals(tmpDir3, queue.fwd());
    }
    
    public void testTruncate() {
        final DirQueue queue = new DirQueue(properties);
        final File tmpDir1 = new File(cwd, "tmpDir1");
        tmpDir1.mkdirs();
        tmpDir1.deleteOnExit();
        queue.fwd(tmpDir1);
        final File tmpDir2 = new File(cwd, "tmpDir2");
        tmpDir2.mkdirs();
        tmpDir2.deleteOnExit();
        queue.fwd(tmpDir2);
        final File tmpDir3 = new File(cwd, "tmpDir3");
        tmpDir3.mkdirs();
        tmpDir3.deleteOnExit();
        queue.fwd(tmpDir3);
        assertEquals(4, queue.size());
        
//        System.out.println(queue);
        queue.back();
        
//        System.out.println(queue);
        queue.back();
        
//        System.out.println(queue);
        assertEquals(tmpDir1, queue.cwd());
        assertEquals(4, queue.size());
        assertEquals(cwd, queue.back());
        assertEquals(4, queue.size());

        final File tmpDir4 = new File(cwd, "tmpDir4");
        tmpDir4.mkdirs();
        tmpDir4.deleteOnExit();
        assertEquals(tmpDir4, queue.fwd(tmpDir4));
        
//        System.out.println(queue);
        assertEquals(2, queue.size());
    }

	public void testCanGo() {
        final DirQueue queue = new DirQueue(properties);
        assertFalse(queue.canGoBack());
        assertFalse(queue.canGoForth());

        final File tmpDir1 = new File(cwd, "tmpDir1");
        tmpDir1.mkdirs();
        tmpDir1.deleteOnExit();
        queue.fwd(tmpDir1);
        assertTrue(queue.canGoBack());
        assertFalse(queue.canGoForth());
        
        queue.back();
        assertFalse(queue.canGoBack());
        assertTrue(queue.canGoForth());

        queue.fwd();
        assertTrue(queue.canGoBack());
        assertFalse(queue.canGoForth());

        final File tmpDir2 = new File(cwd, "tmpDir2");
        tmpDir2.mkdirs();
        tmpDir2.deleteOnExit();
        queue.fwd(tmpDir2);
        assertTrue(queue.canGoBack());
        assertFalse(queue.canGoForth());
        
        queue.back();
//        System.out.println(queue);
        assertTrue(queue.canGoBack());
        assertTrue(queue.canGoForth());
	}
}