package org.pclg.runtime;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A MS-DOS shell wrapper in Java.
 *
 * @deprecated Use the JavaFX version
 * @author El Coyote Cojo.
 * @since 30/11/2018.
 */
@Deprecated
public class JavaShellSwing_Deprecated {
    public static final String[] commands = {
      "dir",
//      "cd ..",
//      "dir",


      "exit",   // Necesario para que cmd termine
    };

    private JavaShellSwing_Deprecated() {
    }

    private static void option2() throws IOException, InterruptedException {
        final Executioner executioner = new Executioner("JavaShell with ExecutorService", true, true, "cmd");
        final Runnable runnable = executioner::launchAndWait;
        final ExecutorService es = Executors.newCachedThreadPool();
        es.execute(runnable);
        Arrays.stream(commands).forEach(executioner::accept);
        es.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void option1() throws IOException, InterruptedException {
        final Executioner executioner = new Executioner("JavaShell with Thread", true, true, "cmd");
        final Thread thread = new Thread(executioner::launchAndWait);
        thread.start();
        Arrays.stream(commands).forEach(executioner::accept);
        thread.join();
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        option1();
        System.exit(42);
    }
}
