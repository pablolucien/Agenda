package org.pclg.filesystem.synchonizer;

/**
 * @author El Coyote Cojo
 * @since 21/02/20 20:01
 */
final class Counters {
    private final String msgFormat;
    private int dirsCreated;
    private int filesCreated;
    private int filesUpdated;
    private int filesWithSameContent;
    private int filesNotModified;

    Counters(final String msgFormat) {
        this.msgFormat = msgFormat;
    }

    void incrementDirsCreated() {
        dirsCreated++;
    }

    void incrementFilesCreated() {
        filesCreated++;
    }

    void incrementFilesUpdated() {
        filesUpdated++;
    }

    void incrementFilesWithSameContent() {
        filesWithSameContent++;
    }

    void incrementFilesNotModified() {
        filesNotModified++;
    }

    public int filesWithSameContent() {
        return filesWithSameContent;
    }

    void clear() {
        dirsCreated = filesCreated = filesUpdated = filesWithSameContent = 0;
    }

    @Override
    public String toString() {
        return String.format(msgFormat, dirsCreated, filesCreated, filesUpdated, filesWithSameContent, filesNotModified);
    }
}
