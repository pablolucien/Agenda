package org.pclg.dbutil;

import org.jpatterns.gof.CommandPattern;

/**
 * Does pre or post processing.
 *
 * @author El Coyote Cojo.
 * @since 02/12/2016.
 */
@CommandPattern.Command
public interface VersatileProcessor {
    void go();
}
