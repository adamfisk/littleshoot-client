package org.lastbamboo.client.services;

import java.awt.CheckboxMenuItem;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.lastbamboo.client.LittleShootModule;
import org.lastbamboo.client.prefs.PrefKeys;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.NativeUtils;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for handling all system tray interactions.
 */
public class SystemTrayImpl implements SystemTray {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * Creates a new system tray handler class.
     * @param limeWire The interface to LimeWire.
     * @param libTorrent Reference to the LibTorrent manager.
     */
    public SystemTrayImpl() {
        if (!CommonUtils.isTrue(ShootConstants.HII_KEY)) {
            createTray();
        }
    }

    private void createTray() {
        m_log.info("Creating tray...");
        final Preferences prefs = Preferences.userRoot();

        // This is only enabled on Windows for now because it creates a screen
        // menu bar on OSX.
        if (SystemUtils.isJavaVersionAtLeast(1.6f)
                && NativeUtils.supportsTray()) {
            if (SystemUtils.IS_OS_WINDOWS) {
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                } catch (final Exception e2) {
                    m_log.error("Could not set look and feel", e2);
                }
            }
            final File iconFile;
            final File iconCandidate1 = new File(
                    "src/main/resources/littleshoot_logo_osx_16.png");
            if (iconCandidate1.isFile()) {
                iconFile = iconCandidate1;
            } else {
                iconFile = new File("littleshoot_logo_osx_16.png");
            }
            if (!iconFile.isFile()) {
                m_log.error("Still no icon file at: " + iconFile);
            }
            final Image image;
            try {
                image = Toolkit.getDefaultToolkit().getImage(
                        iconFile.toURI().toURL());
            } catch (final MalformedURLException e) {
                m_log.error("Could not load icon", e);
                return;
            }
            final PopupMenu popup = new PopupMenu();
            final MenuItem openSearchItem = new MenuItem("Open Search Tab");
            final Map<String, String> paramMap = new TreeMap<String, String>();
            paramMap.put("fromTray", "true");
            paramMap.put("os", SystemUtils.OS_NAME);

            openSearchItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final String url = UriUtils.newUrl(
                            "http://www.littleshoot.org/search", paramMap);
                    NativeUtils.openUri(url);
                }
            });

            final MenuItem openDownloadsItem = new MenuItem(
                    "Open Downloads Tab");
            openDownloadsItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final String url = UriUtils.newUrl(
                            "http://www.littleshoot.org/downloads", paramMap);
                    NativeUtils.openUri(url);
                }
            });

            final MenuItem openDownloadsFolderItem = new MenuItem(
                    "Open Downloads Folder");
            openDownloadsFolderItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    final String path = prefs.get(PrefKeys.DOWNLOAD_DIR, "");
                    final File folder = new File(path);
                    if (!folder.isDirectory()) {
                        m_log.error("Downloads folder does not exist!!");
                    } else {
                        try {
                            NativeUtils.openFolder(folder);
                        } catch (final IOException e1) {
                            m_log.error("Could not open downloads dir", e1);
                        }
                    }
                }
            });

            final MenuItem changeDownloadsFolderItem = new MenuItem(
                    "Change Downloads Folder");
            changeDownloadsFolderItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setFolderPref(PrefKeys.DOWNLOAD_DIR);
                }
            });

            final MenuItem changeIncompleteFolderItem = new MenuItem(
                    "Change Incomplete Folder");
            changeIncompleteFolderItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setFolderPref(PrefKeys.INCOMPLETE_DIR);
                }
            });

            // Note the LimeWire module handles setting it's enabled state
            // independently on startup.
            final boolean limeWireEnabled = prefs.getBoolean(
                    CommonUtils.LIMEWIRE_ENABLED_KEY, true);
            final String limeWireStatusLabel;
            if (limeWireEnabled) {
                limeWireStatusLabel = "LimeWire: On";
            } else {
                limeWireStatusLabel = "LimeWire: Off";
            }
            final MenuItem limeWireStatusMenuItem = new MenuItem(
                    limeWireStatusLabel);
            limeWireStatusMenuItem.setEnabled(false);

            final String limeWireEnabledLabel;
            if (limeWireEnabled) {
                limeWireEnabledLabel = "Turn LimeWire Off";
            } else {
                limeWireEnabledLabel = "Turn LimeWire On";
            }
            final MenuItem limeWireMenuItem = new MenuItem(limeWireEnabledLabel);
            limeWireMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    final boolean enabled = prefs.getBoolean(
                            CommonUtils.LIMEWIRE_ENABLED_KEY, true);
                    if (enabled) {
                        limeWireMenuItem.setLabel("Turn LimeWire On");
                        limeWireStatusMenuItem.setLabel("LimeWire: Off");
                        prefs.putBoolean(CommonUtils.LIMEWIRE_ENABLED_KEY,
                                false);
                        LittleShootModule.getLimeWire().setEnabled(false);
                    } else {
                        limeWireMenuItem.setLabel("Turn LimeWire Off");
                        limeWireStatusMenuItem.setLabel("LimeWire: On");
                        prefs.putBoolean(CommonUtils.LIMEWIRE_ENABLED_KEY, true);
                        LittleShootModule.getLimeWire().setEnabled(true);
                    }
                }
            });

            final boolean seedingEnabled = prefs.getBoolean(
                    CommonUtils.SEEDING_ENABLED_KEY, true);

            final String seedingStatusLabel;
            if (seedingEnabled) {
                seedingStatusLabel = "Seeding: On";
                LittleShootModule.getTorrentManager().setSeeding(true);
            } else {
                seedingStatusLabel = "Seeding: Off";
                LittleShootModule.getTorrentManager().setSeeding(false);
            }
            final MenuItem seedingStatusMenuItem = new MenuItem(
                    seedingStatusLabel);
            seedingStatusMenuItem.setEnabled(false);

            final String seedingEnabledLabel;
            if (seedingEnabled) {
                seedingEnabledLabel = "Turn Seeding Off";
            } else {
                seedingEnabledLabel = "Turn Seeding On";
            }
            final MenuItem seedingMenuItem = new MenuItem(seedingEnabledLabel);
            seedingMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    final boolean enabled = prefs.getBoolean(
                            CommonUtils.SEEDING_ENABLED_KEY, true);
                    if (enabled) {
                        seedingStatusMenuItem.setLabel("Seeding: Off");
                        seedingMenuItem.setLabel("Turn Seeding On");
                        System.out.println("Disabling seeding");
                    } else {
                        seedingStatusMenuItem.setLabel("Seeding: On");
                        seedingMenuItem.setLabel("Turn Seeding Off");
                        System.out.println("Enabling seeding");
                    }
                    prefs.putBoolean(CommonUtils.SEEDING_ENABLED_KEY, !enabled);
                    LittleShootModule.getTorrentManager().setSeeding(!enabled);
                }
            });

            final MenuItem quitItem = new MenuItem("Quit LittleShoot");
            quitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Got exit call");
                    if (SystemUtils.IS_OS_MAC_OSX) {
                        final List<String> cmds = new LinkedList<String>();
                        cmds.add("/bin/launchctl");
                        cmds.add("unload");
                        cmds.add("/Library/LaunchAgents/org.littleshoot.littleshoot.plist");
                        final ProcessBuilder pb = new ProcessBuilder(cmds);
                        try {
                            System.out.println("Unloading...");
                            final Process start = pb.start();

                        } catch (final Exception pbe) {
                            m_log.error("Could not unload", pbe);
                        }
                    } else {
                        System.exit(0);
                    }
                }

            });

            final int savedSpeed = prefs.getInt(CommonUtils.UPLOAD_SPEED_KEY,
                    -1);

            if (savedSpeed == -1) {
                LittleShootModule.getTorrentManager().setMaxUploadSpeed(-1);
            } else {
                LittleShootModule.getTorrentManager().setMaxUploadSpeed(
                        savedSpeed * 1024);
            }

            final PopupMenu uploadSpeedMenu = new PopupMenu("Upload Speed");
            addItem(uploadSpeedMenu, 5, savedSpeed);
            addItem(uploadSpeedMenu, 10, savedSpeed);
            addItem(uploadSpeedMenu, 20, savedSpeed);
            addItem(uploadSpeedMenu, 50, savedSpeed);
            addItem(uploadSpeedMenu, 75, savedSpeed);
            addItem(uploadSpeedMenu, 100, savedSpeed);
            addItem(uploadSpeedMenu, 150, savedSpeed);
            addItem(uploadSpeedMenu, 200, savedSpeed);
            addItem(uploadSpeedMenu, 300, savedSpeed);
            addItem(uploadSpeedMenu, 400, savedSpeed);
            addItem(uploadSpeedMenu, 500, savedSpeed);
            addItem(uploadSpeedMenu, 750, savedSpeed);
            addItem(uploadSpeedMenu, 1000, savedSpeed);
            addItem(uploadSpeedMenu, 2000, savedSpeed);
            addItem(uploadSpeedMenu, 4000, savedSpeed);
            uploadSpeedMenu.addSeparator();
            addRawItem(uploadSpeedMenu, "Unlimited (Default)", savedSpeed == -1);

            popup.add(openSearchItem);
            popup.add(openDownloadsItem);
            popup.addSeparator();
            popup.add(openDownloadsFolderItem);
            popup.add(changeDownloadsFolderItem);
            popup.add(changeIncompleteFolderItem);
            popup.addSeparator();
            popup.add(limeWireStatusMenuItem);
            popup.add(limeWireMenuItem);
            popup.addSeparator();
            popup.add(seedingStatusMenuItem);
            popup.add(seedingMenuItem);
            popup.addSeparator();
            popup.add(uploadSpeedMenu);
            popup.addSeparator();
            popup.add(quitItem);
            System.out.println("Adding system tray...");
            NativeUtils.addTray(image, "LittleShoot", popup);
        } else {
            m_log.debug("System tray not supported..");
        }
    }

    private void addItem(final PopupMenu uploadSpeedMenu, final int speed,
            final int savedSpeed) {
        addRawItem(uploadSpeedMenu, speed + " KB/s", speed == savedSpeed);
    }

    private void addRawItem(final PopupMenu uploadSpeedMenu,
            final String label, final boolean checked) {
        final CheckboxMenuItem menuItem = new CheckboxMenuItem(label);
        if (checked) {
            menuItem.setState(true);
        }
        uploadSpeedMenu.add(menuItem);
        menuItem.addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                final Preferences prefs = Preferences.userRoot();
                final CheckboxMenuItem source = (CheckboxMenuItem) e
                        .getSource();
                final int items = uploadSpeedMenu.getItemCount();
                for (int i = 0; i < items; i++) {
                    final MenuItem item = uploadSpeedMenu.getItem(i);

                    // It could be a separator, for example.
                    if (!(item instanceof CheckboxMenuItem)) {
                        continue;
                    }
                    final CheckboxMenuItem cbmi = (CheckboxMenuItem) item;
                    if (cbmi == source) {
                        continue;
                    } else {
                        cbmi.setState(false);
                    }
                }

                final int speed;
                final String speedLabel = source.getLabel().trim();
                if (StringUtils.startsWithIgnoreCase(speedLabel, "unlimited")) {
                    speed = -1;
                    LittleShootModule.getTorrentManager().setMaxUploadSpeed(
                            speed);
                } else {
                    final String speedString = StringUtils.substringBefore(
                            speedLabel, " ");
                    speed = Integer.parseInt(speedString);
                    LittleShootModule.getTorrentManager().setMaxUploadSpeed(
                            speed * 1024);
                }
                prefs.putInt(CommonUtils.UPLOAD_SPEED_KEY, speed);

            }
        });
    }

    private void setFolderPref(final String prefKey) {
        final Preferences prefs = Preferences.userRoot();
        final String path = prefs.get(prefKey, "");
        final File startFolder = new File(path);

        final File newFolder;
        if (SystemUtils.IS_OS_MAC_OSX) {
            newFolder = chooseDirectoryOsx(startFolder);
        } else {
            newFolder = chooseDirectory(startFolder);
        }

        if (!newFolder.isDirectory()) {
            m_log.error("Downloads folder does not exist at: " + newFolder);
        } else {
            m_log.debug("Setting save folder to: {}", newFolder);
            prefs.put(prefKey, newFolder.getAbsolutePath());
        }
        try {
            prefs.flush();
        } catch (final BackingStoreException e1) {
            m_log.error("Could not flush prefs", e1);
        }
    }

    private File chooseDirectory(final File startFolder) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(startFolder);
        chooser.setDialogTitle("Select Download Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // disable the "All files" option.
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            m_log.debug("No Selection.");
            return startFolder;
        }
    }

    private File chooseDirectoryOsx(final File startFolder) {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        final FileDialog dialog = new FileDialog((Frame) null, "Choose Folder",
                FileDialog.LOAD);
        dialog.setDirectory(startFolder.getAbsolutePath());
        dialog.setVisible(true);
        final String folderPath = dialog.getDirectory();
        final String fileName = dialog.getFile();
        System.setProperty("apple.awt.fileDialogForDirectories", "false");

        if (StringUtils.isBlank(folderPath))
            return startFolder;
        if (StringUtils.isBlank(fileName))
            return startFolder;
        final File folder = new File(folderPath, fileName);
        if (folder.isDirectory()) {
            return folder;
        } else {
            m_log.error("Selected a non-folder?");
            return startFolder;
        }

    }
}
