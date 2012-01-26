package org.lastbamboo.server.resource;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for a bean encapsulating data for a single file and all the 
 * online LittleShoot instances with that file.
 */
public interface FileAndInstances
    {

    /**
     * Accessor for the file resource bean.
     * 
     * @return The file resource bean.
     */
    MetaFileResource getMetaFileResource();
    
    /**
     * Accessor for the {@link Collection} of online instances.
     * 
     * @return The {@link Collection} of online instances.
     */
    Set<OnlineInstance> getOnlineInstances();
    }
