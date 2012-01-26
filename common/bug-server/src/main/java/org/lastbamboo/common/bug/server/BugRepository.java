package org.lastbamboo.common.bug.server;

import java.io.IOException;
import java.util.Collection;

import org.littleshoot.util.Pair;

/**
 * Interface for entering new bugs.
 */
public interface BugRepository
    {

    void insertBug(final Bug bug) throws IOException;

    Collection<Bug> getBugs() throws IOException;

    void clearBugs();

    Collection<Pair<Long, Bug>> getOrderedGroupedBugs() throws IOException;

    }
