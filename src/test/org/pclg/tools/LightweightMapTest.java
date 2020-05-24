package org.pclg.tools;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Premature optimization is the root of all evil.
 * �Donald E. Knuth
 *
 * @author El Coyote Cojo
 * @since 22-oct-2007 15:03:40
 */
@SuppressWarnings({"ClassWithoutLogger"})
public class LightweightMapTest {

	private Map<Number, String> lightweightMap;
	private Map<?, ?> emptyMap;
	private static final Object[] EMPTY_ARRAY = new Object[] {};

	@SuppressWarnings({"PublicMethodNotExposedInInterface"})
	@BeforeMethod
	public void setUp() {
		lightweightMap = new LightweightMap<>(new Number[] {1, 2, 3, 4},
				new String[] {"Alta", "Baja", "Modif", "Repos"});
		emptyMap = new LightweightMap<>(EMPTY_ARRAY, EMPTY_ARRAY);
	}

	@Test
	public void testLightweightMap() {
		try {
			new LightweightMap<>(new String[] {"001", "002"},
					new String[] {"Alta", "Baja", "Modif"});
			fail("No deber�a haber llegado aqu�");
		} catch (final IllegalArgumentException ex) {
			assertEquals(LightweightMap.MSG_SIZE_MISMATCH, ex.getMessage());
		}
	}

	@Test
	public void testSize() {
		assertEquals(lightweightMap.size(), 4);
	}

	@Test
	public void testIsEmpty() {
		assertTrue(emptyMap.isEmpty());
		assertFalse(lightweightMap.isEmpty());
	}

//	@Test(expected = IndexOutOfBoundsException.class)
//	public void testEmpty() {
//		new ArrayList<Object>(5).get(0);
//	}

	@Test
	public void testContainsKey() {
		assertTrue(lightweightMap.containsKey(1));
		assertFalse(lightweightMap.containsKey(5));
	}

	@Test
	public void testGet() {
		assertEquals(lightweightMap.get(1), "Alta");
		assertNull(lightweightMap.get(5));
	}

	@Test
	public void testContainsValue() {
		assertTrue(lightweightMap.containsValue("Alta"));
		assertFalse(lightweightMap.containsValue("pepito"));
	}

	@Test
	public void testKeySet() {
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
		final Map<String, String> kkk = new LightweightMap<>(
			new String[]{"001", "002", "003", "004"},
			new String[]{"Alta", "Baja", "Modif", "Repos"});
		assertEquals(kkk.get("002"), "Baja");
		assertNull(kkk.get("005"));
		final Map<String, String> kkk1 = new LightweightMap<>(
			new String[]{"B", "O"},
			new String[]{"Batch", "Online"});
		assertEquals(kkk1.get("O"), "Online");
		assertNull(kkk1.get("X"));
		assertTrue(kkk1.containsKey("B"));
		assertFalse(kkk1.containsKey("X"));
	}

}