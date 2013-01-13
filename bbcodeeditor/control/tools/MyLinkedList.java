/**
 * Copyright (C) 2004 - 2012 Nils Asmussen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package bbcodeeditor.control.tools;

import java.security.InvalidParameterException;
import java.util.*;


/**
 * a double linked list which stores AbstractElement's
 * 
 * @author hrniels
 */
public final class MyLinkedList implements Collection {
	
	/**
	 * the entries
	 */
	private final MyVector _entries;
	
	/**
	 * the first element
	 */
	private AbstractElement _head;
	
	/**
	 * the last element
	 */
	private AbstractElement _foot;
	
	/**
	 * constructor
	 */
	public MyLinkedList() {
		this(10);
	}
	
	/**
	 * constructor
	 * 
	 * @param initialSize the initial size of the intern List
	 */
	public MyLinkedList(int initialSize) {
		_entries = new MyVector(initialSize);
		
		_head = new EmptyElement(null,null);
		_foot = new EmptyElement(_head,null);
		_head._next = _foot;
	}
	
	public int size() {
		return _entries.size();
	}

	public boolean add(Object o) {
		if(!(o instanceof AbstractElement))
			return false;
		
		return addBefore(_entries.size(),(AbstractElement)o);
	}

	public boolean addAll(Collection elements) {
		int added = 0;
		Iterator it = elements.iterator();
		while(it.hasNext())
			added += add(it.next()) ? 1 : 0;

		return added > 0;
	}
	
	/**
	 * adds the given element after <code>prev</code>
	 * 
	 * @param prev the prev element
	 * @param element the element to add
	 * @return true if successfull
	 * @throws InvalidParameterException if <code>prev</code> does not exist
	 */
	public boolean addAfter(AbstractElement prev,AbstractElement element)
		throws InvalidParameterException {
		// check if it is the last one (which is the default-case)
		if(prev.isLast())
			return addBefore(_entries.size(),element);
		
		int index = _entries.indexOf(prev);
		if(index == -1)
			throw new InvalidParameterException("The element " + prev + " does not exist"
					+ " in the LinkedList");
		
		return addBefore(index + 1,element);
	}
	
	/**
	 * adds the given element before <code>next</code>
	 * 
	 * @param next the next element
	 * @param element the element to add
	 * @return true if successfull
	 * @throws InvalidParameterException if <code>next</code> does not exist
	 */
	public boolean addBefore(AbstractElement next,AbstractElement element)
		throws InvalidParameterException {
		// check if it is the first one
		if(next.isFirst())
			return addBefore(0,element);
		
		int index = _entries.indexOf(next);
		if(index == -1)
			throw new InvalidParameterException("The element " + next + " does not exist"
					+ " in the LinkedList");
		
		return addBefore(index,element);
	}
	
	/**
	 * adds the given element at the given index<br>
	 * all other elements will be pushed one step forward
	 * 
	 * @param index the index of the element
	 * @param element the element to add
	 * @return true if successfull
	 * @throws InvalidParameterException if the index does not exist
	 */
	public boolean addBefore(int index,AbstractElement element)
		throws InvalidParameterException {
		if(index < 0)
			throw new InvalidParameterException("negative index");
		
		AbstractElement next = null;
		
		if(index >= _entries.size())
			next = _foot;
		else
			next = (AbstractElement)_entries.get(index);
		
		AbstractElement prev = next._prev;
		
		element._prev = prev;
		element._next = next;
		next._prev = element;
		prev._next = element;
		
		_entries.add(index,element);
		
		return true;
	}
	
	/**
	 * determines the index of the given element with the given Comparator
	 * 
	 * @param o the object for the comparator
	 * @param cmp the comparator
	 * @return the index of the element, or -1 if not found
	 */
	public int getIndexBinarySearch(Object o,Comparator cmp) {
		// Unfortunatly we have to copy all elements for JRE < 1.5.0 :(
		return Arrays.binarySearch(_entries.toArray(),o,cmp);
		
		// better version, but not available:
		//Object[] elements = _entries.getElements();
		//return Arrays.binarySearch(elements,0,_entries.size(),o,cmp);
	}
	
	/**
	 * @param index the index of the element
	 * @return the AbstractElement at the given position
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public AbstractElement get(int index) throws IndexOutOfBoundsException {
		return (AbstractElement)_entries.get(index);
	}
	
	/**
	 * @return the first element
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public AbstractElement getFirst() throws IndexOutOfBoundsException {
		return get(0);
	}
	
	/**
	 * @return the first element
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public AbstractElement getLast() throws IndexOutOfBoundsException {
		return get(_entries.size() - 1);
	}

	public void clear() {
		_entries.clear();
		_head = new EmptyElement(null,null);
		_foot = new EmptyElement(_head,null);
		_head._next = _foot;
	}

	public boolean contains(Object o) {
		return _entries.contains(o);
	}

	public boolean containsAll(Collection elements) {
		return _entries.containsAll(elements);
	}

	public boolean isEmpty() {
		return _entries.size() == 0;
	}

	public Iterator iterator() {
		return _entries.iterator();
	}

	public boolean remove(Object o) {
		return remove(_entries.indexOf(o));
	}

	public boolean removeAll(Collection elements) {
		int count = 0;
		Iterator it = elements.iterator();
		while(it.hasNext()) {
			try {
				count += remove(it.next()) ? 1 : 0;
			}
			catch(IndexOutOfBoundsException e) {
				// ignore
			}
		}
		
		return count > 0;
	}

	public boolean retainAll(Collection elements) {
		int count = 0;
		for(int i = _entries.size() - 1;i >= 0;i--) {
			AbstractElement e = (AbstractElement)_entries.get(i);
			if(!elements.contains(e))
				count += remove(e) ? 1 : 0;
		}
		
		return count > 0;
	}
	
	/**
	 * removes the element at given index
	 * 
	 * @param index the index of the element to remove
	 * @return true if successfull
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public boolean remove(int index) throws IndexOutOfBoundsException {
		AbstractElement element = (AbstractElement)_entries.get(index);
		element._prev._next = element._next;
		element._next._prev = element._prev;
		_entries.remove(index);
		
		return true;
	}

	public Object[] toArray() {
		return _entries.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _entries.toArray(a);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("{");
		AbstractElement e = _head._next;
		while(e._next != null) {
			buf.append(e);
			
			e = e._next;
			if(e._next != null)
				buf.append(",");
		}
		buf.append("}");
		return buf.toString();
	}
	
	/**
	 * a sub-class of Vector which allows us to read the elements without copying them
	 * 
	 * @author hrniels
	 */
	private static final class MyVector extends Vector {

		private static final long serialVersionUID = 4693381745036583980L;

		/**
		 * constructor
		 * 
		 * @param initSize the initial size
		 */
		public MyVector(int initSize) {
			super(initSize);
		}
		
		/**
		 * @return the elements of the vector (no copy!)
		 */
		Object[] getElements() {
			return elementData;
		}
	}
	
	/**
	 * a simple implementation of an element for the head- and foot-element
	 * 
	 * @author hrniels
	 */
	private static final class EmptyElement extends AbstractElement {

		/**
		 * constructor
		 * 
		 * @param prev the previous element
		 * @param next the next element
		 */
		public EmptyElement(AbstractElement prev,AbstractElement next) {
			super(prev,next);
		}
	}
}