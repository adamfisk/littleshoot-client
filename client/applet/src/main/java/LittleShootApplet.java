import java.applet.Applet;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import netscape.javascript.JSObject;

/**
 * General applet for handling LittleShoot operations.
 */
public class LittleShootApplet extends Applet {

    private Frame m_parent;
    private FileDialog m_fileDialog;

    private boolean m_littleShootDetected = false;

    /**
     * Generated serialization ID.
     */
    private static final long serialVersionUID = -2662327150714792175L;

    /**
     * Displays a file dialog, calling standard JavaScript methods when the user
     * selects a file or cancels the dialog.
     */
    public void newFileDialog() {
        openDialog("onFileDialogFile", "onFileDialogCancel");
    }

    /**
     * Displays a file dialog, calling the specified JavaScript functions when
     * the user selects a file or cancels the dialog.
     * 
     * @param onFile
     *            The name of the function to call when the user selects a file.
     * @param onCancel
     *            The name of the function to call when the user cancels a
     *            dialog selection.
     */
    public void openDialog(final String onFile, final String onCancel) {

        if (m_parent == null) {
            m_parent = new Frame();
        }
        if (m_fileDialog == null) {
            m_fileDialog = new FileDialog(m_parent, "File Upload",
                    FileDialog.LOAD);
        }
        System.out.println("Calling open dialog...");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_fileDialog.setVisible(true);
                m_fileDialog.toFront();
                m_fileDialog.repaint();
                final String file = m_fileDialog.getFile();
                final String directory = m_fileDialog.getDirectory();
                m_fileDialog.setVisible(false);
                m_parent.setVisible(false);

                if (file == null || file.length() == 0) {
                    callJavaScript(onCancel);
                } else {
                    final String path = directory + file;
                    callJavaScript(onFile, path);
                }
            }
        });
    }

    private void callJavaScript(final String func, final Object... args) {
        final JSObject window = JSObject.getWindow(LittleShootApplet.this);
        if (window == null) {
            System.out.println("Could not get window from JSObject!!!");
            return;
        }
        System.out.println("Calling func through window");
        try {
            window.call(func, args);
        } catch (final Exception e) {
            System.out.println("Got error!!" + e.getMessage());
            e.printStackTrace();
            showError(e);
        }
        System.out.println("Finished JavaScript call...");
    }

    private void showError(final Exception e) {
        final String[] args = new String[] { e.getMessage() };
        final JSObject window = JSObject.getWindow(this);
        try {
            window.call("alert", args);
        } catch (final Exception ex) {
            System.out.println("Error showing error! " + ex);
        }
    }

    @Override
    public void init() {
        System.out.println("Initing applet with polling...");
        super.init();
    }

    @Override
    public void start() {
        System.out.println("Starting applet with polling...");
        super.start();
    }

    @Override
    public void stop() {
        System.out.println("Stopping applet...");
        super.stop();
        m_fileDialog = null;
        m_parent = null;
    }

    @Override
    public void destroy() {
        System.out.println("Destroying applet...");
        super.destroy();
    }
    
    public void pollForLittleShoot() {
        final String jsMethod = "onLittleShootFromApplet";
        final Runnable checkRunner = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        System.out.println("Creating socket...");
                        final Socket sock = new Socket();
                        // We don't want to wait forever, but we want to make
                        // sure
                        // the client has plenty of time to write data back when
                        // we
                        // do get a connection
                        sock.setSoTimeout(6000);
                        final InetSocketAddress isa = new InetSocketAddress(
                                "127.0.0.1", 8107);
                        // new InetSocketAddress("http://p2p2o.littleshoot.org",
                        // 8107);
                        sock.connect(isa, 3000);

                        m_littleShootDetected = true;
                        callJavaScript(jsMethod);
                        break;
                        // sleep(2000);
                    } catch (final ConnectException e) {
                        System.out.println("Exception connecting...");
                        e.printStackTrace();
                        sleep(1000);
                    } catch (final SocketTimeoutException e) {
                        e.printStackTrace();
                        sleep(2000);
                    } catch (final UnknownHostException e) {
                        e.printStackTrace();
                    } catch (final IOException e) {
                        e.printStackTrace();
                        sleep(2000);
                    }
                }
            }
        };

        final Thread runnerThread = new Thread(checkRunner,
                "LittleShoot-Applet-Checker");
        runnerThread.setDaemon(true);
        runnerThread.start();
    }

    private void sleep(final long millis) {
        System.out.println("Sleeping for " + millis + " milliseconds...");
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
