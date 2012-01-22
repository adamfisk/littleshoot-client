package org.lastbamboo.client.nativeos;

import java.io.File;
import java.util.prefs.Preferences;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for launching file dialogs from the backend. This overcomes some of
 * the limitations of requesting file dialogs from web pages, such as the 
 * inability to select folders.
 */
public class FileDialogLauncherProxy implements FileDialogLauncher
    {

    private final Logger LOG = LoggerFactory.getLogger(FileDialogLauncherProxy.class);
    
    /**
     * Stored in a member variable to pre-load the underlying Swing components.
     */
    private FileDialogLauncher m_generalLauncher;
    
    /**
     * Creates a new proxy.
     */
    public FileDialogLauncherProxy()
        {
        final Preferences prefs = Preferences.userRoot();
        final boolean cli = prefs.getBoolean("CLI", false);
        if (!cli && !SystemUtils.IS_OS_MAC_OSX)
            {
            // Commented out because we're not using this for now.
            //this.m_generalLauncher = new GeneralFileDialogLauncher();
            }
        }
    
    public File openFileDialog(final String browser, final boolean folder)
        {
        /*
        if (SystemUtils.IS_OS_MAC_OSX)
            {
            return launchNativeChooser(browser, folder, 
                OsxFileDialogLauncher.class);
            }
        else 
            {
            return this.m_generalLauncher.openFileDialog(browser, folder);
            }
            */
        return null;
        }

    private File launchNativeChooser(final String browser, 
        final boolean folder, final Class launcherClass)
        {
        try
            {
            final FileDialogLauncher launcher = 
                (FileDialogLauncher) launcherClass.newInstance();
            return launcher.openFileDialog(browser, folder);
            }
        catch (final InstantiationException e)
            {
            LOG.error("Could not create dialog", e);
            return null;
            }
        catch (final IllegalAccessException e)
            {
            LOG.error("Could not create dialog", e);
            return null;
            }
        }
    }
