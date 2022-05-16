package org.pclg.filesystem.synchonizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @author El Coyote Cojo
* @since 15/05/22 14:50
*/
class OnlyIncludedFilesIterator implements Iterator<File[]> {
    private final Iterator<File[]> itemsIterator;

    OnlyIncludedFilesIterator(final File[] dirs, final String[] fileNames) {
        if (dirs.length < 2 || fileNames.length == 0) {
            throw new IllegalArgumentException("Not enough arguments passed: dirs = " + dirs.length + ", files = 0");
        }
        final List<File[]> items = new ArrayList<>(dirs.length * fileNames.length);
        for (int ii = 0; ii < dirs.length - 1; ii++) {
            final File dir1 = dirs[ii];
            final File dir2 = dirs[ii + 1];
            addPairs(items, dir1, dir2, fileNames);
        }
        if (dirs.length > 2) {
            addPairs(items, dirs[dirs.length - 1], dirs[0], fileNames);
        }
        itemsIterator = items.iterator();
        assert items.size() == dirs.length * fileNames.length : "Initial capacity should equal size";
    }

    private static void addPairs(final List<File[]> items, final File dir1, final File dir2, final String[] fileNames) {
        for (final String fileName : fileNames) {
            final var item = new File[2];
            item[0] = new File(dir1, fileName);
            item[1] = new File(dir2, fileName);
            items.add(item);
        }
    }

    @Override
    public boolean hasNext() {
        return itemsIterator.hasNext();
    }

    @Override
    public File[] next() {
        return itemsIterator.next();
    }
}
