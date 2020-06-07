package org.pclg.runtime;

import org.apache.log4j.Logger;
import org.pclg.log.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author El Coyote Cojo
 * @since 30/08/19 17:20
 */
public final class RuntimeControl {
    private static class RunnableController implements Runnable {
        private final Set<Runnable> targets = new HashSet<>(5);

        @Override
        public void run() {
            targets.forEach(Runnable::run);
        }

        public void addTarget(final Runnable runnable) {
            targets.add(runnable);
        }
    }

    private static RunnableController runnableController;

    private RuntimeControl() {
    }

    public static void registerShutdownHook(final Runnable runnable) {
        synchronized (RuntimeControl.class) {
            if (runnableController == null) {
                runnableController = new RunnableController();
                Runtime.getRuntime().addShutdownHook(new Thread(runnableController));
            }
            runnableController.addTarget(runnable);
        }
    }
}
