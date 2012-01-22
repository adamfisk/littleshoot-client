package org.lastbamboo.client.nativeos;

import java.io.File;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.cocoa.application.NSApplication;
import com.apple.cocoa.foundation.NSAppleEventDescriptor;
import com.apple.cocoa.foundation.NSAppleScript;
import com.apple.cocoa.foundation.NSData;
import com.apple.cocoa.foundation.NSMutableDictionary;

/**
 * Class for launching a file dialog on OS X.
 */
public class OsxFileDialogLauncher implements FileDialogLauncher
    {
    /**
     * The log for this class.
     */
    private final Logger LOG = LoggerFactory.getLogger(OsxFileDialogLauncher.class);
    
    /**
     * {@inheritDoc}
     */
//    public File openFileDialog
//            (final String browser,
//             final boolean folder)
//        {
//        Display.setAppName("LittleShoot File Helper");
//        
//        final Display display = new Display();
//        final Shell shell = new Shell(display, SWT.NO_TRIM);
//        
//        final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
//        final String fileName = dialog.open();
//            
//        if (fileName == null)
//            {
//            // The user did not choose a file.
//            return null;
//            }
//        else
//            {
//            return new File(fileName);
//            }
//        }

    public File openFileDialog(final String browser, final boolean folder)
        {
        LOG.debug("Launching dialog for browser: "+browser);
        NSApplication.sharedApplication();
        
        final String launchApp;
        if (browser.contains("safari"))
            {
            launchApp = "Safari";
            }
        else
            {
            launchApp = "System Events";
            }
        final String chooserType;
        if (folder)
            {
            chooserType = "folder";
            }
        else
            {
            chooserType = "file";
            }
        final String script = "tell app \"" + launchApp + "\"\n" + 
            " return choose " + chooserType + " \n end tell";
        LOG.debug("Using applescript: "+script);

        final NSAppleScript myScript = new NSAppleScript(script);
        final NSMutableDictionary errors = new NSMutableDictionary();

        LOG.debug("About to execute applescript...");
        final NSAppleEventDescriptor descriptor = myScript.execute(errors);
        
        for (final Object error : Collections.list(errors.keyEnumerator()))
            {
            LOG.debug("Error class: " + error.getClass());
            LOG.debug("Error: " + error);
            }
        
        LOG.debug("Executed applescript...");

        if (descriptor == null)
            {
            // The user likely pressed "Cancel". Don't do anything.
            LOG.debug("No descriptor -- user likely canceled the publish");
            return null;
            }
        
        LOG.debug("Descriptor string value: " + descriptor.stringValue());
        
        final int numberOfItems = descriptor.numberOfItems();
        
        LOG.debug("Number of items: " + numberOfItems);
        
        for (int i = 1; i <= numberOfItems; i++)
            {
            NSAppleEventDescriptor subDescriptor = descriptor.descriptorAtIndex(i);
            LOG.debug(subDescriptor.stringValue());
            } 

        final NSData data = descriptor.data();
        final int length = data.length();
        
        final String raw = new String(data.bytes(0, length));
        
        LOG.debug("Publishing raw string: "+raw);

        final int firstSlash = raw.indexOf("/");
        final Pattern pattern = Pattern.compile("\\w+/");
        final Matcher matcher = pattern.matcher(raw);
        matcher.find();
        String firstFolder = matcher.group();

        if (firstFolder.startsWith("9"))
            {
            LOG.debug("Stripping crazy number from beginning of path");
            firstFolder = firstFolder.substring(1);
            }
        LOG.debug("First folder: " + firstFolder);
        final int lastSlash = raw.lastIndexOf("/");
        final String path = "/" + firstFolder
                + raw.substring(firstSlash + 1, lastSlash).trim();
        LOG.debug("Path: " + path);

        final File selected = new File(path);
        LOG.debug("Found file: " + selected);
        if (!selected.isFile() && !folder)
            {
            LOG.warn("Not a file: "+selected);
            }
        else if (!selected.isDirectory() && folder)
            {
            LOG.warn("Not a directory: "+selected);
            }
        
        return selected;
        }
    }
