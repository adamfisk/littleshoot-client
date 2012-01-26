package org.lastbamboo.client.nativeos;

import java.io.File;

/**
 * Interface for file dialog launching.
 */
public interface FileDialogLauncher
    {

    /**
     * Opens a file dialog.
     * 
     * @param browser The browser initiating the request.
     * @param folder Whether it's a request for a folder or not.
     * @return The selected file, or <code>null</code> if no file was 
     * selected.
     */
    File openFileDialog(String browser, boolean folder);
    }
