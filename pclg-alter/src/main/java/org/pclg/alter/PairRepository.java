package org.pclg.alter;

import org.pclg.tools.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

final class PairRepository {

    private final ArrayList<FilePair> pairs;

    PairRepository(final int initialCapacity) {
		pairs = new ArrayList<>(initialCapacity);
	}

    public boolean add(final FilePair filePair) {
        return pairs.add(filePair);
    }

    public void forEach(final Consumer<? super FilePair> action) {
        pairs.forEach(action);
    }

    public Stream<FilePair> stream() {
        return pairs.stream();
    }

    static final class FilePair extends Pair<File, File> {
		boolean renamed;

		FilePair(final File sourceFile, final File targetFile) {
            super(sourceFile, targetFile);
		}
	}

	void addPair(final File first, final File second) {
		pairs.add(new FilePair(first, second));
	}
}
