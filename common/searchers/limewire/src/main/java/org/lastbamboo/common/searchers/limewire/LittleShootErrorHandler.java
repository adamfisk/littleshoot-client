package org.lastbamboo.common.searchers.limewire;

import java.util.ArrayList;
import java.util.List;

import org.limewire.service.ErrorCallback;

import com.sun.jna.Callback;

// Use this class if you want to hook into errors coming from LimeWire.
class LittleShootErrorHandler implements ErrorCallback,
										 Thread.UncaughtExceptionHandler, 
										 Callback.UncaughtExceptionHandler{

    private final List<StackTraceElement> notReported;

    public LittleShootErrorHandler() {
        notReported = new ArrayList<StackTraceElement>();
        notReported.add(new StackTraceElement("javax.jmdns.DNSRecord", "suppressedBy", null, -1));
        // add more unreported stacktraces here.
    }
	
	public void error(Throwable t) {
		t.printStackTrace();
	}
	
	public void error(Throwable t, String msg) {
		synchronized(System.err) {
			System.err.println("Error message: " + msg);
			t.printStackTrace();
		}
	}

    public void uncaughtException(Thread thread, Throwable throwable) {
        handleUncaughtException(thread.getName(), throwable);
    }
    
    public void uncaughtException(Callback c, Throwable e) {
        handleUncaughtException(Thread.currentThread().getName(), e);
    }
    
    private void handleUncaughtException(String name, Throwable throwable) {
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (matchesUnreported(stackTraceElement)) {
                throwable.printStackTrace();
                return;
            }
        }
        error(throwable, "Uncaught thread error: " + name);
    }

    /**
     * Checks to see if the give stack trace matches any of the stacktraces
     * which we will NOT report.
     */
    private boolean matchesUnreported(StackTraceElement stackTraceElement) {
        for (StackTraceElement notReportedStackTrace : notReported) {
            if (matches(notReportedStackTrace, stackTraceElement)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if a given stack trace element matches against a given
     * filter. For a match to be successful, either the ClassName methodName and
     * line number must match. Or the class name method name can match and the
     * filter line number can be a wild card by having a negative value.
     */
    private boolean matches(StackTraceElement filter, StackTraceElement element) {
        return filter.getClassName().equals(element.getClassName())
                && filter.getMethodName().equals(element.getMethodName())
                && (filter.getLineNumber() < 0 || filter.getLineNumber() == element.getLineNumber());
    }
}

