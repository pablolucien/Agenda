package org.pclg.log;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class EnhancedLogger {
    private final Logger delegate;

    public EnhancedLogger(final Logger delegate) {
        this.delegate = delegate;
    }

    public void debug(final String format, final Object... args) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(resolve(format, args));
        }
    }

    public void error(final String format, final Object... args) {
        if (delegate.isErrorEnabled()) {
            delegate.error(resolve(format, args));
        }
    }

    public void error(final Throwable thr) {
        if (delegate.isErrorEnabled()) {
            delegate.error(thr);
        }
    }

    public void warn(final String format, final Object... args) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(resolve(format, args));
        }
    }

    public void warn(final Throwable thr) {
        if (delegate.isErrorEnabled()) {
            delegate.error(thr);
        }
    }

    public void log(final Level level, final String format, final Object... args) {
        if (delegate.isEnabled(level)) {
            delegate.log(level, resolve(format, args));
        }
    }

    static String resolve(final String format, final Object[] args) {
        int lastFound = 0;
        final StringBuilder resolved = new StringBuilder(format);
        for (final Object arg : args) {
            lastFound = resolved.indexOf("{}", lastFound);
            if (lastFound == -1) {
                break;
            }
            resolved.replace(lastFound, lastFound + 2, String.valueOf(arg));
        }
        return resolved.toString();
    }
}
