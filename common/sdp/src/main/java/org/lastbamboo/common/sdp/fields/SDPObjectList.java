/*******************************************************************************
* Product of NIST/ITL Advanced Networking Technologies Division (ANTD).        *
*******************************************************************************/
package org.lastbamboo.common.sdp.fields;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.lastbamboo.common.sdp.GenericObject;
import org.lastbamboo.common.sdp.GenericObjectList;

public class SDPObjectList extends GenericObjectList {

	/** 
	 * Do a merge of the GenericObjects contained in this list with the 
	 * GenericObjects in the mergeList. Note that this does an inplace 
	 * modification of the given list. This does an object by object 
	 * merge of the given objects.
	 *
	 *@param mergeList is the list of Generic objects that we want to do 
	 * an object by object merge with. Note that no new objects are
	 * added to this list.
	 *
	 */

	public void mergeObjects(GenericObjectList mergeList) {
		if (!mergeList.getMyClass().equals(this.getMyClass()))
			throw new IllegalArgumentException("class mismatch");
		Iterator it1 = this.listIterator();
		Iterator it2 = mergeList.listIterator();
		while (it1.hasNext()) {
			GenericObject outerObj = (GenericObject) it1.next();
			while (it2.hasNext()) {
				Object innerObj = it2.next();
				outerObj.merge(innerObj);
			}
		}
	}

	/**
	 * Add an sdp object to this list.
	 */
	public void add(SDPObject s) {
		super.add(s);
	}

	/**
	 * Get the input text of the sdp object (from which the object was
	 * generated).
	 */
	public SDPObjectList(String lname, Class clazz) {
		super(lname, clazz);
	}

	public SDPObjectList() {
		super(null, SDPObject.class);
	}

	public SDPObjectList(String lname) {
		super(lname, SDPObject.class);
	}

	public GenericObject first() {
		return (SDPObject) super.first();
	}

	public GenericObject next() {
		return (SDPObject) super.next();
	}

	public GenericObject next(ListIterator li) {
		return (SDPObject) super.next(li);
	}

	public String encode() {
		StringBuffer retval = new StringBuffer();
		SDPObject sdpObject;
		for (sdpObject = (SDPObject) this.first();
			sdpObject != null;
			sdpObject = (SDPObject) this.next()) {
			retval.append (sdpObject.encode());
		}
		return retval.toString();
	}

	public String toString() {
		return this.encode();
	}

	/**
	 * Do a find and replace of objects in this list.
	 * @param objectText text of the object to find.
	 * @param replacementObject object to replace the target with (
	 * in case a target is found).
	 * @param matchSubstring boolean that indicates whether to flag a
	 * match when objectText is a substring of a candidate object's 
	 * encoded text.
	 */
	public void replace(
		String objectText,
		GenericObject replacementObject,
		boolean matchSubstring)
		throws IllegalArgumentException {

		if (objectText == null || replacementObject == null) {
			throw new IllegalArgumentException("null argument");
		}
		ListIterator listIterator = this.listIterator();
		LinkedList ll = new LinkedList();

		while (listIterator.hasNext()) {
			Object obj = listIterator.next();
			if (GenericObject.isMySubclass(obj.getClass())) {
				GenericObject gobj = (GenericObject) obj;
				if (gobj.getClass().equals(replacementObject.getClass())) {
					if ((!matchSubstring)
						&& gobj.encode().compareTo(objectText) == 0) {
						// Found the object that we want,
						ll.add(obj);
					} else if (
						matchSubstring
							&& gobj.encode().indexOf(objectText) >= 0) {
						ll.add(obj);
					} else {
						gobj.replace(
							objectText,
							replacementObject,
							matchSubstring);
					}
				}
			} else if (GenericObjectList.isMySubclass(obj.getClass())) {
				GenericObjectList gobj = (GenericObjectList) obj;
				if (gobj.getClass().equals(replacementObject.getClass())) {
					if ((!matchSubstring)
						&& gobj.encode().compareTo(objectText) == 0) {
						// Found the object that we want,
						ll.add(obj);
					} else if (
						matchSubstring
							&& gobj.encode().indexOf(objectText) >= 0) {
						ll.add(obj);
					} else {
						gobj.replace(
							objectText,
							replacementObject,
							matchSubstring);
					}
				}
			}
		}
		for (int i = 0; i < ll.size(); i++) {
			Object obj = ll.get(i);
			this.remove(obj);
			this.add(i, replacementObject);
		}

	}

	/**
	 * Do a find and replace of objects in this list.
	 *@since v1.0
	 *@param objectText text of the object to find.
	 *@param replacementObject object to replace the target with (in
	 * case a target is found).
	 *@param matchSubstring boolean that indicates whether to flag a
	 * match when objectText is a substring of a candidate object's 
	 * encoded text.
	 */
	public void replace(
		String objectText,
		GenericObjectList replacementObject,
		boolean matchSubstring)
		throws IllegalArgumentException {
		if (objectText == null || replacementObject == null) {
			throw new IllegalArgumentException("null argument");
		}

		ListIterator listIterator = this.listIterator();
		LinkedList ll = new LinkedList();

		while (listIterator.hasNext()) {
			Object obj = listIterator.next();
			if (GenericObject.isMySubclass(obj.getClass())) {
				GenericObject gobj = (GenericObject) obj;
				if (gobj.getClass().equals(replacementObject.getClass())) {
					if ((!matchSubstring)
						&& gobj.encode().compareTo(objectText) == 0) {
						// Found the object that we want,
						ll.add(obj);
					} else if (
						matchSubstring
							&& gobj.encode().indexOf(objectText) >= 0) {
						ll.add(obj);
					} else {
						gobj.replace(
							objectText,
							replacementObject,
							matchSubstring);
					}
				}
			} else if (GenericObjectList.isMySubclass(obj.getClass())) {
				GenericObjectList gobj = (GenericObjectList) obj;
				if (gobj.getClass().equals(replacementObject.getClass())) {
					if ((!matchSubstring)
						&& gobj.encode().compareTo(objectText) == 0) {
						// Found the object that we want,
						ll.add(obj);
					} else if (
						matchSubstring
							&& gobj.encode().indexOf(objectText) >= 0) {
						ll.add(obj);
					} else {
						gobj.replace(
							objectText,
							replacementObject,
							matchSubstring);
					}

				}
			}
		}
		for (int i = 0; i < ll.size(); i++) {
			Object obj = ll.get(i);
			this.remove(obj);
			this.add(i, replacementObject);
		}
	}
}
