/*
 * Created on Apr 2, 2008
 */
package org.lastbamboo.common.bencode.util;

/**
 * @author Daniel Spiewak
 */
public final class Some<T> implements Option<T> {
	private final T value;
	
	public Some(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}
}
