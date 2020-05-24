package org.pclg.tools;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Premature optimization is the root of all evil.
 * —Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-oct-2007 15:03:40
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class LightweightMapTest extends TestCase {

	private Map<Number, String> lightweightMap;
	private Map<?, ?> emptyMap;
	private static final Object[] EMPTY_ARRAY = new Object[] {};

	@Override
	@SuppressWarnings({"PublicMethodNotExposedInInterface"})
	@Before
	public void setUp() throws Exception {
		super.setUp();
		lightweightMap = new LightweightMap<>(new Number[] {1, 2, 3, 4},
				new String[] {"Alta", "Baja", "Modif", "Repos"});
		emptyMap = new LightweightMap<>(EMPTY_ARRAY, EMPTY_ARRAY);
	}

	@Test
	public void testLightweightMap() throws Exception {
		try {
			new LightweightMap<>(new String[] {"001", "002"},
					new String[] {"Alta", "Baja", "Modif"});
			fail("No debería haber llegado aquí");
		} catch (final IllegalArgumentException ex) {
			assertEquals(LightweightMap.MSG_SIZE_MISMATCH, ex.getMessage());
		}
	}

	@Test
	public void testSize() throws Exception {
		assertEquals(lightweightMap.size(), 4);
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertTrue(emptyMap.isEmpty());
		assertFalse(lightweightMap.isEmpty());
	}

//	@Test(expected = IndexOutOfBoundsException.class)
//	public void testEmpty() {
//		new ArrayList<Object>(5).get(0);
//	}

	@Test
	public void testContainsKey() throws Exception {
		assertTrue(lightweightMap.containsKey(1));
		assertFalse(lightweightMap.containsKey(5));
	}

	@Test
	public void testGet() throws Exception {
		assertEquals(lightweightMap.get(1), "Alta");
		assertNull(lightweightMap.get(5));
	}

	@Test
	public void testContainsValue() throws Exception {
		assertTrue(lightweightMap.containsValue("Alta"));
		assertFalse(lightweightMap.containsValue("pepito"));
	}

	@Test
	public void testKeySet() throws Exception {
        try {
            final Set<Number> set = lightweightMap.keySet();
            fail("This is not supported");
            assertNotNull(set);
            assertEquals(set.size(), lightweightMap.size());
            assertSame(set.isEmpty(), lightweightMap.isEmpty());
        } catch (final UnsupportedOperationException e) {
            //It's OK;
        }
    }

	@Test
	public void testIDontKnowWhat() {
		final Map<String, String> kkk = new LightweightMap<String, String>(
				new String[] {"001", "002", "003", "004"},
				new String[] {"Alta", "Baja", "Modif", "Repos"});
		assertEquals(kkk.get("002"), "Baja");
		assertNull(kkk.get("005"));
		final Map<String, String> kkk1 = new LightweightMap<String, String>(
				new String[] {"B", "O"},
				new String[] {"Batch", "Online"});
		assertEquals(kkk1.get("O"), "Online");
		assertNull(kkk1.get("X"));
		assertTrue(kkk1.containsKey("B"));
		assertFalse(kkk1.containsKey("X"));
	}

}