package org.lastbamboo.client.services;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import org.apache.commons.io.FileUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.common.p2p.P2PClient;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.ShootConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for general configuration.
 */
public class Configurator {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * Creates a new configuration class.
     */
    public Configurator() {
        log.debug("Configuring...");
        log.info("Server URL: "+ShootConstants.SERVER_URL);
        install();
        configureP2P();
    }

    private void configureP2P() {
        final Runnable p2pRunner = new Runnable()  {
            public void run() {
                //final P2PClient p2pClient = LittleShootModule.getP2PSipClient();
                final P2PClient p2pClient = LittleShootModule.getP2PXmppClient();
                if (!isHii()) {
                    //log.info("Logging in -- not running HII!!");
                    //final NatDependentServicesLauncher servicesLauncher =
                    //    new NatDependentServicesLauncher(p2pClient);
                } else {
                    log.info("NOT AUTOMATICALLY LOGGING IN TO SIP SERVER " +
                        "WITH HII INSTANCE.");
                }
            }
        };
    
        final Thread p2pThread = new Thread(p2pRunner, "P2P-Init-Thread");
        p2pThread.setDaemon(true);
        p2pThread.start();
    }

    protected boolean isHii() {
        if (CommonUtils.isTrue(ShootConstants.HII_KEY)) {
            return true;
        }
        return CommonUtils.isPropertyTrue(ShootConstants.HII);
    }

    private void install() {
        if (CommonUtils.isTrue(ShootConstants.HII)) {
            CommonUtils.setProperty(ShootConstants.HII_KEY, 
                String.valueOf(new SecureRandom().nextInt()));
        }
        // This is disabled because the installer now just takes care of it.
        /*
        if (SystemUtils.IS_OS_MAC_OSX) {
            // Move the link app bundle on OSX.
            final File applicationsDir = new File("/Applications/LittleShoot");
            if (!applicationsDir.isDirectory()) {
                Prefs.setInstalled(true);
                if (!applicationsDir.mkdirs()) {
                    m_log.warn("Could not create LittleShoot directory -- "
                            + "non-admin account??");

                    // No point in continuing if we can't make the directory.
                    return;
                }
            }
            final String[] fileNames = new String[] { "LittleShoot.app",
                    "LittleShootUninstaller.app" };
            final File base = new File(SystemUtils.getUserHome(),
                    ".littleshoot");
            for (final String name : fileNames) {
                final File installedFile = new File(base, name);
                if (installedFile.exists()) {
                    final File applicationsDirFile = new File(applicationsDir,
                            name);
                    renameFileOrDirectory(installedFile, applicationsDirFile);
                } else {
                    // This will happen all the time -- it's already been
                    // configured and moved.
                    m_log.debug("No file or app bundle at " + installedFile);
                }
            }
        }
        */
    }

    private boolean renameFileOrDirectory(final File installedFile,
            final File applicationsDirFile) {
        // We use this to detect if this is an installation run.
        if (applicationsDirFile.exists()) {
            if (FileUtils.isFileNewer(installedFile, applicationsDirFile)) {
                Prefs.setInstalled(true);
            }
        } else {
            Prefs.setInstalled(true);
        }

        // If there's already a file where we want to put this one, delete it.
        if (applicationsDirFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(applicationsDirFile);
            } catch (final IOException e) {
                log.warn("Could not delete dir: " + applicationsDirFile, e);
            }
        } else if (applicationsDirFile.isFile()) {
            try {
                FileUtils.forceDelete(applicationsDirFile);
            } catch (final IOException e) {
                log.warn("Could not delete file: " + applicationsDirFile, e);
            }
        }

        final boolean renamed = installedFile.renameTo(applicationsDirFile);
        if (!renamed) {
            log.warn("Could not rename file or dir: " + installedFile
                    + " to " + applicationsDirFile);
        }
        return renamed;
    }
}
