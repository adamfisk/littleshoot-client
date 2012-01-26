/*
 * Created on Apr 2, 2008
 */
package org.lastbamboo.common.bencode.util;

/**
 * @author Daniel Spiewak
 */
public final class None<T> implements Option<T> {
	
	public None() {}
	
	public T value() {
		throw new UnsupportedOperationException("Cannot resolve value on None");
	}
}
