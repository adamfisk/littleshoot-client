package org.lastbamboo.common.searchers.limewire;

import org.limewire.service.MessageCallback;
import org.limewire.service.Switch;


// Alter this to hook into message the LW core sends.
class LittleShootMessageHandler implements MessageCallback {

    public void showError(final String error) {
    	System.out.println("Error: " + error);
    }

    public void showError(final String error, final Switch ignore) {
        if (!ignore.getValue()) {
        	System.out.println("Error: " + error);
        }
    }

    public void showMessage(final String message) {
    	System.out.println("Message: " + message);
    }

    public void showMessage(final String message, final Switch ignore) {
    	if (!ignore.getValue()) {
    		System.out.println("Message: " + message);
    	}
    }

    public void showFormattedError(final String error, final Object... args) {
    	System.out.printf("Error: " + error, args);
    }

    public void showFormattedError(final String error, final Switch ignore, final Object... args) {    	
        if (!ignore.getValue()) {        	
        	System.out.printf("Error: " + error, args);
        }
    }

    public void showFormattedMessage(final String message, final Object... args) {
    	System.out.printf("Message: " + message, args);
    }

    public void showFormattedMessage(final String message, final Switch ignore,
            final Object... args) {
        if (!ignore.getValue()) {
        	System.out.printf("Message: " + message, args);
        }
    }
}
