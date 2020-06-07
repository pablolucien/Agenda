package org.pclg.alter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @since 09/01/2018.
 */
final class UndoRedoManager {
    private final List<PairRepository> queue = new ArrayList<>();
    private int undoPointer = -1;
    private final BiConsumer<File, File> consumer;

    UndoRedoManager(final BiConsumer<File, File> function) {
        this.consumer = function;
    }

    void startBatch(final PairRepository pairRepository) {
        undoPointer++;
        queue.subList(undoPointer, queue.size()).clear();
        queue.add(pairRepository);
    }

    void undoLastAction() {
        if (undoPointer >= 0) {
            final PairRepository undoPairRepository = queue.get(undoPointer--);
            undoPairRepository.stream().filter(filePair -> filePair.renamed)
                .forEach(pair -> consumer.accept(pair.targetFile, pair.sourceFile));
        }
    }

    void redoLastAction() {
        final int redoPointer = undoPointer + 1;
        if (redoPointer < queue.size()) {
            undoPointer++;
            final PairRepository redoPairRepository = queue.get(redoPointer);
            redoPairRepository.forEach(pair -> consumer.accept(pair.sourceFile, pair.targetFile));
        }
    }

    boolean isUndoable() {
        return undoPointer >= 0;
    }

    boolean isRedoable() {
        return undoPointer + 1 < queue.size();
    }
}
