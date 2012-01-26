package org.lastbamboo.client.util.settings;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.client.util.settings.types.FileArraySetting;
import org.lastbamboo.client.util.settings.types.FileSetting;



/**
 * Settings for shared resources.
 */
public final class SharingSettings extends ShootSettings 
    {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SharingSettings.class);
    
    private SharingSettings() 
        {
        // Nothing to construct.
        }
    
    private static final File BASE_RESOURCES_DIR_FILE =
        new File(SystemUtils.USER_HOME, "Shared");
    
    
    /**
     * Base shared directory.
     */
    public static final FileSetting BASE_RESOURCES_DIR =
        FACTORY.createFileSetting("BASE_RESOURCES_DIR", 
            BASE_RESOURCES_DIR_FILE);
    
    /**
     * The shared directories.
     */
    public static final FileArraySetting DIRECTORIES_TO_SHARE =
        FACTORY.createFileArraySetting("DIRECTORIES_TO_SEARCH_FOR_FILES", 
            new File[0]);
        
    }