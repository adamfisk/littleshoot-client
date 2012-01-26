package org.lastbamboo.client.launcher.all;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.prefs.Prefs;
import org.lastbamboo.client.services.http.HttpServer;
import org.lastbamboo.client.services.http.HttpServerImpl;
import org.lastbamboo.client.services.opener.OsxLaunchServicesHandler;
import org.lastbamboo.common.log4j.AppenderCallback;
import org.lastbamboo.common.log4j.BugReportingAppender;
import org.littleshoot.util.CommonUtils;
import org.littleshoot.util.NativeUtils;
import org.littleshoot.util.ShootConstants;
import org.littleshoot.util.StringListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Launches the application. 
 */
public final class Launcher  {
    
    private static final long START_TIME = System.currentTimeMillis();
    
    private static Logger LOG;// = LoggerFactory.getLogger(Launcher.class);
    private static Launcher s_launcher;
    
    /**
     * We use this to obtain a read and write exclusive lock on the file
     * indicating LittleShoot is running.
     */
    private static RandomAccessFile s_randomAccessFile;
    private static FileChannel s_fileChannel;

    private static final AtomicReference<FileLock> s_atomicLockRef = 
        new AtomicReference<FileLock>();
 
    /**
     * This is the version signifying we're running on the main line.
     */
    private static final double MAIN_LINE_VERSION = 0.00;
    
    /**
     * The version of LittleShoot we're running. 
     * This needs to get called before the logger config!!!!
     */
    private static double s_version = 
        Double.parseDouble(System.getProperty("org.lastbamboo.client.version", 
            "0.00"));
    
    /**
     * We hold this in a variable to make sure it doesn't get garbage collected.
     */
    private static OsxLaunchServicesHandler s_launcherServicesHandler;
    
    private static final File s_lsHome = 
        new File(SystemUtils.USER_HOME, ".littleshoot");
    
    private static final AtomicBoolean s_releasingLock = 
        new AtomicBoolean(false);
    
    private HttpServer m_server;
    
    /**
     * Entry point for the application.  Starts everything.
     * 
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        configureLogger();
        LOG = LoggerFactory.getLogger(Launcher.class);
        // First, log any uncaught exceptions.
        final UncaughtExceptionHandler ueh = new UncaughtExceptionHandler() {
            public void uncaughtException(final Thread t, final Throwable e) {
                LOG.error("Uncaught exception on Thread " + t.getName(), e);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(ueh);
        final Set<String> argSet = new HashSet<String>(Arrays.asList(args));
        LOG.debug("Got args: " + argSet);
        
        // If there's a torrent file path in the argument set, we move it to
        // our pre-designated torrent file directory and open the user's
        // browser to a page that will load LittleShoot via the "normal"
        // launch process. When this happens, LittleShoot will find the
        // new torrent file in the designated directory and will start
        // downloading it.
        final StringListener torrentListener = new StringListener() {
            public void onString(final String torrentFileString) {
                final File torrentFile = new File(torrentFileString);

                if (!torrentFile.isFile()) {
                    LOG.error("Not a torrent file: {}", torrentFile);
                } else {
                    final File torrentsDir = Prefs.getTorrentDir();
                    final File newTorrentFile = new File(torrentsDir,
                            torrentFile.getName());
                    if (newTorrentFile.isFile()) {
                        LOG.info("Deleting existing torrent file");
                        newTorrentFile.delete();
                    }
                    try {
                        FileUtils.moveFileToDirectory(torrentFile, torrentsDir,
                                true);
                    } catch (final IOException e) {
                        LOG.warn("Could not move torrent file: " + torrentFile,
                                e);
                    }
                }

                final String uri = "http://www.littleshoot.org/downloads?fromTorrentListener=true";
                NativeUtils.openUri(uri);
            }
        };
        if (hasTorrentFile(argSet)) {
            final String torrentFileString = extractTorrentFile(argSet);
            torrentListener.onString(torrentFileString);
            System.exit(0);
        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            // We need a launch services handler in all cases to handle calls
            // from launch services.
            s_launcherServicesHandler = new OsxLaunchServicesHandler(
                    torrentListener);
            if (!argSet.contains("launchd")) {
                // OK, we're on OSX and there's no launchd argument. That leaves
                // two possibilities for how LittleShoot started:
                //
                // 1) The user double-clicked on the app bundle
                // 2) The user selected to open a file LittleShoot handles with
                // LittleShoot.
                //
                // To detect the latter case, we need to register for events
                // from launch services. If the user is attempting to open
                // a file with LittleShoot, we'll get the open file event
                // quickly once we register. If we don't get the event,
                // the user has double-clicked the app bundle, and we just
                // need to open a browser and exit to initiate the normal
                // LittleShoot launch sequence.
                System.out.println("Creating OSX opener...");

                s_launcherServicesHandler.waitForFile();
                System.out.println("After OSX opener...");

                // If the opener did handle the file, it also opened the
                // browser.
                // Otherwise, we open the browser here. In both cases, the point
                // is to give the user the LittleShoot browser-based UI while
                // also
                // initiating the standard LittleShoot browser-based launch
                // sequence.
                if (!s_launcherServicesHandler.handledFileEvent()) {
                    System.out
                            .println("Opening http://www.littleshoot.org and exiting");
                    NativeUtils
                            .openUri("http://www.littleshoot.org/search?fromAppBundle=true");
                }
                System.exit(0);
            }
        }

        configure();

        // We close if another version that's equal or newer than us has the
        // exclusive lock on a LittleShoot file.
        final Preferences prefs = Preferences.userRoot();
        addShutdownHook(s_atomicLockRef, prefs);
        configureLockFile();
        final boolean shouldStopExisting;

        FileLock tempLock = grabLock();
        final File oldVersionFile = new File(s_lsHome, "oldVersionRunningFlag");
        if (tempLock == null) {
            // This can happen on new installs, for example, if they encounter
            // the old version.
            LOG.warn("Already a running instance -- checking versions...");
            closeIfWeAreEqualOrOlder();
            shouldStopExisting = true;
        } else if (oldVersionFile.exists()) {
            // This means there's a file present indicating an old version was
            // running that didn't know about the lock mechanism.
            oldVersionFile.delete();
            shouldStopExisting = true;
        } else {
            shouldStopExisting = false;
            s_atomicLockRef.set(tempLock);
        }

        Prefs.setLastVersion();

        LOG.debug("Launching with version: " + s_version);

        // We just tell the existing version to stop running.
        if (shouldStopExisting) {
            stopExisting();
        }

        LOG.debug("Got args: " + argSet);
        s_launcher = new Launcher();
        if (argSet.contains("installer")) {
            LOG.debug("Setting installer to true");
            Prefs.setInstalled(true);
        }
        if (argSet.contains("stop")) {
            s_launcher.stopServer();
            return;
        }

        prefs.putBoolean(PrefKeys.RUNNING, true);

        // final ClassLoader cl = s_launcher.loadJars();
        s_launcher.startServer();

        // We never get here!!
        /*
         * if (s_atomicLockRef.get() == null) {
         * LOG.warn("We don't have the lock yet.  This instance was " +
         * "started with another one running"); continuallyGrabLock(); }
         */
    }
    
    private static boolean hasTorrentFile(final Set<String> argSet) {
        return StringUtils.isNotBlank(extractTorrentFile(argSet));
    }

    private static String extractTorrentFile(final Set<String> argSet) {
        for (final String arg : argSet) {
            if (StringUtils.endsWith(arg, ".torrent")) {
                return arg;
            }
        }
        return "";
    }

    /**
     * In this case, we had to shut down a running LittleShoot instance. We need
     * to make sure we get the lock when it finally releases it after shutdown.
     */
    private static void continuallyGrabLock() {
        final Runnable runner = new Runnable() {
            public void run() {
                while (true) {
                    synchronized (s_releasingLock) {
                        if (s_releasingLock.get()) {
                            LOG.debug("Not grabbing lock since we're "
                                    + "releasing it");
                            break;
                        }
                        final FileLock lock = grabLock();

                        if (lock == null) {
                            try {
                                Thread.sleep(1000);
                            } catch (final InterruptedException e) {
                                LOG.debug("Interrupted", e);
                            }
                        } else {
                            LOG.debug("Got lock");
                            s_atomicLockRef.set(lock);
                            break;
                        }
                    }
                }
            }
        };

        final Thread lockThread = new Thread(runner, "Lock-Grabbing-Thread");
        lockThread.setDaemon(true);
        lockThread.start();
    }
    
    private static void addShutdownHook(
        final AtomicReference<FileLock> atomicLockRef,
        final Preferences prefs) {
        final Runnable shutDownRunner = new Runnable() {
            public void run() {
                synchronized (s_releasingLock) {
                    System.out.println("Launcher caught shutdown call!!!\n\n\n"
                            + "***********************");
                    s_releasingLock.set(true);
                    prefs.putBoolean(PrefKeys.RUNNING, false);
                    final FileLock lock = atomicLockRef.get();
                    if (lock != null) {
                        try {
                            System.out.println("Releasing lock...");
                            lock.release();
                        } catch (final IOException e) {
                            LOG.error("Could not release lock?", e);
                        }
                    }
                    try {
                        s_fileChannel.close();
                    } catch (final IOException e) {
                        LOG.debug("Exception closing channel", e);
                    }
                    try {
                        s_randomAccessFile.close();
                    } catch (final IOException e) {
                        LOG.debug("Exception closing RAF", e);
                    }
                    try {
                        prefs.flush();
                    } catch (final BackingStoreException e) {
                        LOG.error("Could not flush prefs", e);
                    }
                }
            }
        };
        final Thread hook = new Thread(shutDownRunner,
                "LittleShoot-Shutdown-Thread");
        Runtime.getRuntime().addShutdownHook(hook);
    }

    private static void configureLockFile() {
        final File parentDir;
        if (SystemUtils.IS_OS_WINDOWS) {
            final File baseParent = CommonUtils.getDataDir();
            if (!baseParent.isDirectory()) {
                if (!baseParent.mkdirs()) {
                    LOG.error("Could not create parent at: ", baseParent);
                    return;
                }
            }
            parentDir = new File(baseParent, "littleshoot");
            if (!parentDir.isDirectory()) {
                if (!parentDir.mkdirs()) {
                    LOG.error("Could not create win lock file at: ", parentDir);
                    return;
                }
            }
        } else {
            parentDir = s_lsHome;
        }
        final File instanceLock = new File(parentDir,
                "littleshoot_instance.lck");
        LOG.debug("Lock file is: {}", instanceLock);
        if (!instanceLock.isFile()) {
            LOG.debug("No existing process file");
            try {
                instanceLock.createNewFile();
            } catch (final IOException e) {
                LOG.warn("Could not touch process running flag file", e);
            }
        }
        try {
            s_randomAccessFile = new RandomAccessFile(instanceLock, "rw");
            s_fileChannel = s_randomAccessFile.getChannel();
        } catch (final FileNotFoundException e) {
            // This should never happen.
            LOG.error("FNFE on file we've checked for?", e);
        }
    }

    private static void closeIfWeAreEqualOrOlder() {
        final double oldVersion = Prefs.getLastVersion();
        final double newVersion = Prefs.getVersion();
        if (newVersion <= oldVersion) {
            final String msg = "LS closing - equal to or older than running version:  "
                    + "old/new: " + oldVersion + "/" + newVersion;
            NativeUtils
                    .openUri("http://www.littleshoot.org/?fromCloseEvent=true");
            System.out.println(msg);
            LOG.warn(msg);
            System.exit(0);
        } else {
            LOG.debug("Not closing with last version at: " + oldVersion
                    + " and current at: " + newVersion);
        }
    }

    private static FileLock grabLock() {
        try {
            final FileLock lock = s_fileChannel.tryLock();
            LOG.debug("Returning lock: {}", lock);
            return lock;
        } catch (final OverlappingFileLockException e) {
            LOG.debug("Overlapping file lock", e);
        } catch (final ClosedChannelException e) {
            LOG.error("Closed channel?", e);
        } catch (final NonWritableChannelException e) {
            LOG.error("Closed channel?", e);
        } catch (final IOException e) {
            LOG.error("IO Error getting lock?", e);
        }
        return null;
    }

    private static void configure() {
        Prefs.getId();
        setAppVersion();
        configureDefaultSettings();
    }

    private static void setAppVersion() {
        final Preferences prefs = Preferences.userRoot();
        prefs.putDouble(PrefKeys.APP_VERSION, s_version);
    }

    private static void configureLogger() {
        final File logDirParent;
        final File logDir;
        if (SystemUtils.IS_OS_WINDOWS) {
            logDirParent = CommonUtils.getDataDir();
            logDir = new File(logDirParent, "logs");
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            logDirParent = new File("/Library/Logs/");
            logDir = new File(logDirParent, "LittleShoot");
        } else {
            logDirParent = new File(SystemUtils.getUserHome(), ".littleshoot");
            logDir = new File(logDirParent, "logs");
        }

        if (!logDirParent.isDirectory()) {
            if (!logDirParent.mkdirs()) {
                System.out.println("Could not create parent at: "
                        + logDirParent);
                return;
            }
        }
        if (!logDir.isDirectory()) {
            if (!logDir.mkdirs()) {
                System.out.println("Could not create dir at: " + logDir);
                return;
            }
        }

        if (s_version == MAIN_LINE_VERSION) {
            System.out.println("Running from main line");
            final String propsPath = "src/main/resources/log4j.properties";
            final File props = new File(propsPath);
            if (props.isFile()) {
                PropertyConfigurator
                        .configure("src/main/resources/log4j.properties");
                return;
            }
        } else {
            System.out.println("Not on main line...");
            final File logFile = new File(logDir, "java.log");
            setLoggerProps(logFile);
        }
        final AppenderCallback callback = new AppenderCallback() {
            public void addData(final Collection<NameValuePair> dataList) {
                final long id = Prefs.getId();
                dataList.add(new NameValuePair("instanceId", Long.toString(id)));
                dataList.add(new NameValuePair("version", String
                        .valueOf(s_version)));
            }
        };
        final BugReportingAppender bugAppender = new BugReportingAppender(
                callback);
        bugAppender.setUrl(ShootConstants.BUGS_URL);
        BasicConfigurator.configure(bugAppender);
    }
    
    private static void setLoggerProps(final File logFile) {
        final Properties props = new Properties();
        try {
            final String logPath = logFile.getCanonicalPath();
            props.put("log4j.appender.RollingTextFile.File", logPath);
            props.put("log4j.rootLogger", "warn, RollingTextFile");
            props.put("log4j.appender.RollingTextFile",
                    "org.apache.log4j.RollingFileAppender");
            props.put("log4j.appender.RollingTextFile.MaxFileSize", "1MB");
            props.put("log4j.appender.RollingTextFile.MaxBackupIndex", "1");
            props.put("log4j.appender.RollingTextFile.layout",
                    "org.apache.log4j.PatternLayout");
            props.put(
                    "log4j.appender.RollingTextFile.layout.ConversionPattern",
                    "%-6r %d{ISO8601} %-5p [%t] %c{2}.%M (%F:%L) - %m%n");
            props.put("log4j.logger.com.limegroup", "off");
            props.put("log4j.logger.org.limewire", "off");

            // This throws and swallows a FileNotFoundException, but it
            // doesn't matter. Just weird.
            PropertyConfigurator.configure(props);
            System.out.println("Set logger file to: " + logPath);
        } catch (final IOException e) {
            System.out.println("Exception setting log4j props with file: "
                    + logFile);
            e.printStackTrace();
        }
    }

    private static void stopExisting() {
        LOG.debug("Sending message to existing app.");
        final HttpClient client = new HttpClient();
        final String url = "http://127.0.0.1:8107/api/client/appCheck?callback=check&"
                + "stop=true&t=" + System.currentTimeMillis();

        LOG.debug("Calling local URL: " + url);
        final PostMethod method = new PostMethod(url);

        // This verifies we're running on the same system.
        final long id = Prefs.getId();
        final double version = Prefs.getVersion();

        method.setRequestHeader("X-Instance-ID", String.valueOf(id));
        method.setRequestHeader("X-LittleShoot-Version",
                String.valueOf(version));
        method.getParams().setSoTimeout(6000);
        client.getHttpConnectionManager().getParams()
                .setConnectionTimeout(3000);

        LOG.debug("About to execute method.");
        try {
            client.executeMethod(method);
            final int statusCode = method.getStatusCode();
            // Always read the body.
            final String body = IOUtils.toString(method
                    .getResponseBodyAsStream());
            if (statusCode == HttpStatus.SC_FORBIDDEN) {
                LOG.debug("Forbidden -- trying to stop a newer version?");
                LOG.debug("Shutting down");
                System.exit(0);
            } else if (statusCode != HttpStatus.SC_OK) {
                LOG.warn("Did not find app" + method.getStatusLine());
                LOG.warn("Body:\n" + body);
            } else {
                LOG.debug("App present");
                // Give it a second to shut down.
                try {
                    Thread.sleep(800);
                } catch (final InterruptedException e) {
                    LOG.error("Sleep interrupted", e);
                }
            }
        } catch (final HttpException e) {
            LOG.warn("Could not access existing app", e);
        } catch (final IOException e) {
            LOG.warn("Could not access existing app", e);
        } finally {
            method.releaseConnection();
        }
    }

    private void stopServer() {
        System.out.println("Stopping the server!!");

        m_server.stopServer();
    }

    /**
     * Starts the server and runs the web app.
     * 
     * @param cl The class loader to use for starting the app.
     */
    private void startServer() {
        this.m_server = new HttpServerImpl();
        final long elapsed = System.currentTimeMillis() - START_TIME;

        System.out.println("SYSTEM STARTUP IN " + elapsed / 1000
                + " seconds...");
        System.out.println("SYSTEM STARTUP IN " + elapsed + " milliseconds...");
        try {
            this.m_server.joinServer();
        } catch (final Throwable t) {
            LOG.warn("Caught unexpected exception", t);
        }
    }
    
    private static void configureDefaultSettings() {
        final Preferences prefs = Preferences.userRoot();
        final File baseSharedDir = new File(System.getProperty("user.home"),
                "shared");
        if (!isSet(prefs, PrefKeys.SHARED_DIR)) {
            final File publicDir = new File(baseSharedDir, "public");
            prefs.put(PrefKeys.SHARED_DIR, publicDir.getAbsolutePath());
        }
        mkdir(prefs, PrefKeys.SHARED_DIR);

        if (!isSet(prefs, PrefKeys.INCOMPLETE_DIR)) {
            final File incompleteDir = new File(baseSharedDir, "incomplete");
            prefs.put(PrefKeys.INCOMPLETE_DIR, incompleteDir.getAbsolutePath());
        }
        mkdir(prefs, PrefKeys.INCOMPLETE_DIR);

        if (!isSet(prefs, PrefKeys.DOWNLOAD_DIR)) {
            final File downloadsDir = new File(baseSharedDir, "downloads");
            prefs.put(PrefKeys.DOWNLOAD_DIR, downloadsDir.getAbsolutePath());
        }
        mkdir(prefs, PrefKeys.DOWNLOAD_DIR);

        if (!isSet(prefs, CommonUtils.LIMEWIRE_ENABLED_KEY)) {
            prefs.putBoolean(CommonUtils.LIMEWIRE_ENABLED_KEY, true);
        }
    }

    private static void mkdir(final Preferences prefs, final String dirKey) {
        final String path = prefs.get(dirKey, "");
        if (StringUtils.isBlank(path)) {
            LOG.error("Blank path!");
            return;
        }
        final File dir = new File(path);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            LOG.error("Could not make directory: " + dir + " exists? "
                    + dir.exists() + " isFile? " + dir.isFile()
                    + " parent permissions: " + dir.getParentFile().canWrite()
                    + " grandparent permissions: "
                    + dir.getParentFile().getParentFile().canWrite());
        }
    }

    private static boolean isSet(final Preferences prefs, final String key) {
        return StringUtils.isNotBlank(prefs.get(key, ""));
    }
}
