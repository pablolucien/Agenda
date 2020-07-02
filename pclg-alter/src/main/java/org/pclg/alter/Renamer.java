package org.pclg.alter;

/**
 * This is the business class that does the renaming.
 *
 * @since 30/11/2017.
 */
final class Renamer {
    void rename(final PairRepository pairRepository) {
        pairRepository.forEach(pair -> pair.renamed = pair.first().renameTo(pair.second()));
    }
}
