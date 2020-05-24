package org.pclg.filesystem.synchonizer;

/**
 * @since 27/07/2018.
 */
class DirectorySynchronizerException extends RuntimeException {
    DirectorySynchronizerException(final String message) {
        super(message);
    }

    DirectorySynchronizerException(final Throwable ex) {
        super(ex);
    }
}
