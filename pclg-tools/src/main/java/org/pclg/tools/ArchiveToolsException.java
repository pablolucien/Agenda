package org.pclg.tools;

public class ArchiveToolsException extends RuntimeException {
    public ArchiveToolsException(final String message) {
        super(message);
    }

    public ArchiveToolsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ArchiveToolsException(final Throwable cause) {
        super(cause);
    }
}
