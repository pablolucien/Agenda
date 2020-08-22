package org.pclg.tools;


import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * ChronoTest<BR>
 *
 * @author El Coyote Cojo
 * @since 03-sep-2010 18:46:08
 */
public class ChronoTest {

    @BeforeClass
    public void setup() {
        Awaitility.reset();
    }

    @Test
    public void getChrono() {
        final int handle = Chrono.getChrono();
        assertTrue(handle >= 0, "Handle debe ser >= 0");
    }

    @Test
    public void start() {
        final int handle = Chrono.getChrono();
        final long elapsed1 = Chrono.elapsed(handle);
        Chrono.start(handle);
        sleep();
        final long elapsed2 = Chrono.elapsed(handle);
        assertEquals(elapsed1, elapsed2);
    }

    @Test
    public void mark() {
        final int handle = Chrono.getChrono();
        sleep();
        Chrono.mark(handle);
        final long elapsed = Chrono.elapsed(handle);
        assertThat(elapsed, greaterThanOrEqualTo(1000L));
    }

    @Test
    public void testElapsed() {
        final int handle = Chrono.getChrono();
        final long elapsed1 = Chrono.elapsed(handle);
        sleep();
        final long elapsed2 = Chrono.elapsed(handle);
        assertEquals(elapsed2, elapsed1);
    }

    private static void sleep() {
        final long millis = System.currentTimeMillis();
        await().atMost(Duration.TEN_SECONDS).until(System::currentTimeMillis, greaterThanOrEqualTo(millis + 1000));
    }

    @Test
    public void timeDetail1() {
        assertEquals(
            Chrono.timeDetail(Long.MAX_VALUE),
            "106751991167 Tage, 7 Stunden, 12 Minuten, 55 Sekunden, 807 Millisekunden.");
    }

    @Test
    public void timeDetail2() {
        assertEquals(
            Chrono.timeDetail(Long.MAX_VALUE, Chrono.TimeDetailMessages.Format.LONG),
            "106751991167 Tage, 7 Stunden, 12 Minuten, 55 Sekunden, 807 Millisekunden.");
    }

    @Test
    public void timeDetail3() {
        assertEquals(
            Chrono.timeDetail(Long.MAX_VALUE, Chrono.TimeDetailMessages.Format.SHORT),
            "106751991167 days, 7 hr, 12 min, 55 sec, 807 ms.");
    }

    @Test
    public void timeDetail4() {
        assertEquals(Chrono.timeDetail(1001), "1 Sekunden, 1 Millisekunden.");
    }
}
