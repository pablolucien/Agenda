package org.pclg.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that logs to a TextAreaAppender and to the configured appenders of the provider logger.
 */
public class TextAreaLogger {
    private final Logger logger;
    private final List<TextAreaAppender> appenders = new ArrayList<>();

    public TextAreaLogger(Logger logger) {
        this.logger = logger;
    }


    public void addAppender(TextAreaAppender appender) {
        appenders.add(appender);
    }

    public void debug(String message) {
        logger.debug(message);
        if (logger.isDebugEnabled()) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void info(String message) {
        logger.info(message);
        if (logger.isInfoEnabled()) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void warn(String message) {
        logger.warn(message);
        if (logger.isWarnEnabled()) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        if (logger.isErrorEnabled()) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void error(String message) {
        logger.error(message);
        if (logger.isErrorEnabled()) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void log(Level level, String message) {
        logger.log(level, message);
        if (logger.isEnabled(level)) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public void log(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
        if (logger.isEnabled(level)) {
            appenders.forEach(appender -> appender.write(message));
        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}
