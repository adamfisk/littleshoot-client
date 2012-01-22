package org.lastbamboo.client.nativeos;

import java.io.File;

import org.lastbamboo.client.nativeos.FileDialogLauncher;
import org.lastbamboo.client.nativeos.FileDialogLauncherProxy;

import junit.framework.TestCase;

public class FileDialogTest extends TestCase
    {

    public void testFileChooser() throws Exception
        {
        final FileDialogLauncher launcher = new FileDialogLauncherProxy();
        //final File file = launcher.openFileDialog("Safar", false);
        //assertTrue(file.isFile());
        }
    }
