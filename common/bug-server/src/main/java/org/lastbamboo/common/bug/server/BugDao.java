package org.lastbamboo.common.bug.server;

import java.io.IOException;
import java.util.Collection;

import org.littleshoot.util.Pair;

/**
 * DAO for manipulating bug entries in the database.
 */
public interface BugDao
    {

    /**
     * Inserts a new bug in the database.
     * 
     * @param bug The bug to insert.
     */
    void insertBug(Bug bug);

    /**
     * Access the bugs in the database in their default order.
     * 
     * @return {@link Collection} of bugs.
     * @throws IOException If we cannot access the data.
     */
    Collection<Bug> getBugs() throws IOException;

    /**
     * Clears all bugs from the database.
     */
    void clearBugs();

    /**
     * Get bugs grouped by class and line and ordered by the number of bugs
     * in the group.
     * 
     * @return A {@link Collection} of bugs grouped by class and line number
     * and ordered by the number of bugs in the group.
     * @throws IOException If we cannot access the data.
     */
    Collection<Pair<Long, Bug>> getOrderedGroupedBugs() throws IOException;

    }
