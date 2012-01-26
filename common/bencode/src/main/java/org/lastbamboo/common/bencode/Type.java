/*
 * Created on Mar 19, 2008
 */
package org.lastbamboo.common.bencode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Daniel Spiewak
 */
public interface Type {
	public void write(OutputStream os) throws IOException;
}
