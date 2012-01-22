package org.lastbamboo.client.nativeos;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for launching file dialogs on Windows and other platforms.
 */
public class GeneralFileDialogLauncher implements FileDialogLauncher, Runnable
    {
    
    private final Logger LOG = LoggerFactory.getLogger(GeneralFileDialogLauncher.class);

    private final JDialog m_dialogParent;
    private final JFileChooser m_fileChooser;
    private File m_selectedFile;

    private final Point m_screenCenter;

    /**
     * Creates a new Windows dialog launcher.
     */
    public GeneralFileDialogLauncher()
        {
        setNativeLookAndFeel();
        this.m_dialogParent = new JDialog();
        
        // This is the magic line that makes the dialog disappear!
        this.m_dialogParent.setUndecorated(true);
        final Dimension size = new Dimension(1, 1);
        this.m_dialogParent.setSize(size);
        this.m_dialogParent.setMaximumSize(size);
        this.m_dialogParent.setPreferredSize(size);
        this.m_fileChooser = new JFileChooser();
        this.m_fileChooser.setFocusable(true);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();

        final int x = (screenSize.width - this.m_dialogParent.getWidth()) / 2;
        final int y = (screenSize.height - this.m_dialogParent.getHeight()) / 2;
        
        this.m_screenCenter = new Point(x, y);
        this.m_dialogParent.setLocation(this.m_screenCenter);  
        this.m_dialogParent.setAlwaysOnTop(true);
        }
    
    private void setNativeLookAndFeel()
        {
        LOG.debug("Setting look and feel...");
        try
            {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        
        catch (final ClassNotFoundException e)
            {
            LOG.error("Error setting look and feel", e);
            }
        catch (final InstantiationException e)
            {
            LOG.error("Error setting look and feel", e);
            }
        catch (final IllegalAccessException e)
            {
            LOG.error("Error setting look and feel", e);
            }
            
        catch (final UnsupportedLookAndFeelException e)
            {
            LOG.error("Error setting look and feel", e);
            }
        }

    public File openFileDialog(final String browser, final boolean folder)
        {
        LOG.debug("Launching file chooser...");
        if (folder)
            {
            LOG.debug("Selecting folders...");
            this.m_fileChooser.setFileSelectionMode(
                JFileChooser.DIRECTORIES_ONLY);
            }
        else
            {
            LOG.debug("Selecting files only...");
            this.m_fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
        try
            {
            SwingUtilities.invokeAndWait(this);
            return this.m_selectedFile;
            }
        catch (final InterruptedException e)
            {
            LOG.warn("Interrupted during file dialog", e);
            }
        catch (final InvocationTargetException e)
            {
            LOG.warn("Could not invoke file dialog", e);
            }
        return null;
        }

    public void run()
        { 
        // Clear any previous selected files.
        this.m_selectedFile = null;
        this.m_dialogParent.setVisible(true);
        this.m_fileChooser.setLocation(this.m_screenCenter);
        int returnVal = this.m_fileChooser.showOpenDialog(this.m_dialogParent);
        this.m_fileChooser.requestFocus();
        LOG.debug("Return val: "+returnVal);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            {
            this.m_selectedFile = this.m_fileChooser.getSelectedFile();
            LOG.debug("Selected file: "+this.m_selectedFile);
            }
        this.m_dialogParent.setVisible(false);
        }
    }
