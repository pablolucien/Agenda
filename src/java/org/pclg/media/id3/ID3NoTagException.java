package org.pclg.media.id3;

public final class ID3NoTagException extends Exception {
    private static final long serialVersionUID = -2563620921599374367L;

    public ID3NoTagException(final String message) {
        super(message);
    }
}