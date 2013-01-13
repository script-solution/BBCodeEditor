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
import java.util.Arrays;
import java.util.Iterator;


/**
 * A container that maps characters to arbitrary objects. The entries
 * are sorted by the character so that binarySearch can be used to find
 * entries by the character
 * 
 * @author hrniels
 */
public class SortedCharMap {
	
	/**
	 * The default-capacity
	 */
	private static final int DEF_CAPACITY = 10;

	/**
	 * The elements
	 */
	private Element[] _elements;
	
	/**
	 * The number of elements in the container
	 */
	private int _filled = 0;
	
	/**
	 * Constructor
	 */
	public SortedCharMap() {
		this(DEF_CAPACITY);
	}
	
	/**
	 * Constructor
	 * 
	 * @param capacity the initial capacity
	 */
	public SortedCharMap(int capacity) {
		if(capacity <= 0)
			throw new InvalidParameterException("capacity <= 0");
		
		_elements = new Element[capacity];
	}
	
	/**
	 * You will get instances of {@link Element} which contain the character
	 * and the value of it.
	 * 
	 * @return an Iterator-implementation to walk through all elements in the map
	 */
	public Iterator iterator() {
		return new CharMapIterator();
	}
	
	/**
	 * @return the number of elements in the container
	 */
	public int size() {
		return _filled;
	}
	
	/**
	 * Determines wether the given character exists in the container
	 * 
	 * @param key the char to search for
	 * @return true if it exists
	 */
	public boolean contains(char key) {
		return indexOf(key) >= 0;
	}
	
	/**
	 * Determines the value of the given character.
	 * 
	 * @param key the char to search for
	 * @return the value of it or null if not found
	 */
	public Object getValue(char key) {
		int index = indexOf(key);
		if(index < 0)
			return null;
		
		return _elements[index].getValue();
	}
	
	/**
	 * Determines the index of the given character
	 * 
	 * @param k the character
	 * @return the index or a negative value if not found
	 */
	public int indexOf(char k) {
		return binarySearch(_elements,0,_filled,k);
	}
	
	/**
	 * Adds the given character with the given value to the heap
	 * 
	 * @param key
	 * @param value
	 */
	public void add(char key,Object value) {
		// no space left?
		if(_filled >= _elements.length) {
			// algorithm borrowed from java.util.ArrayList :)
			int oldCapacity = _elements.length;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if(newCapacity < oldCapacity + 1)
				newCapacity = oldCapacity + 1;
			_elements = (Element[])Arrays.copyOf(_elements,newCapacity);
		}
		
		// find the right place
		int i = 0;
		while(i < _filled && _elements[i].getKey() <= key)
			i++;
		
		// move the elements behind i one step forward
		for(int x = _filled - 1;x >= i;x--)
			_elements[x + 1] = _elements[x];
		
		// insert at i
		_elements[i] = new Element(key,value);
		_filled++;
	}
	
	/**
	 * Removes the given char from the container.
	 * 
	 * @param k the character
	 */
	public void remove(char k) {
		int index = indexOf(k);
		if(index < 0)
			return;
		
		// move the following elements one step back
		for(;index < _filled;index++)
			_elements[index] = _elements[index + 1];
		
		_filled--;
	}
	
	/**
	 * The binary-search-algorithm. Borrowed from java.util.Arrays.
	 * Because this version is just available in 1.6
	 * 
	 * @param a the array
	 * @param fromIndex the begin-index
	 * @param toIndex the end-index
	 * @param key the key to search
	 * @return index of the search key, if it is contained in the array
	 *	       within the specified range;
	 *	       otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
	 *	       <i>insertion point</i> is defined as the point at which the
	 *	       key would be inserted into the array: the index of the first
	 *	       element in the range greater than the key,
	 *	       or <tt>toIndex</tt> if all
	 *	       elements in the range are less than the specified key.  Note
	 *	       that this guarantees that the return value will be &gt;= 0 if
	 *	       and only if the key is found.
	 */
	private int binarySearch(Element[] a,int fromIndex,int toIndex,char key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while(low <= high) {
			int mid = (low + high) >>> 1;
			Element midVal = a[mid];
			char midValChar = midVal.getKey();
			int cmp = midValChar == key ? 0 : midValChar < key ? -1 : 1;

			if(cmp < 0)
				low = mid + 1;
			else if(cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		
		return -(low + 1); // key not found.
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for(int i = 0;i < _filled;i++) {
			buf.append(_elements[i]);
			if(i < _filled - 1)
				buf.append(", ");
		}
		buf.append("]");
		return buf.toString();
	}
	
	/**
	 * An element in the heap
	 * 
	 * @author hrniels
	 */
	public static final class Element {
		
		/**
		 * The character
		 */
		private char _key;
		
		/**
		 * The value of it
		 */
		private Object _val;
		
		/**
		 * Constructor
		 * 
		 * @param key the character
		 * @param val the value of it
		 */
		private Element(char key,Object val) {
			_key = key;
			_val = val;
		}
		
		/**
		 * @return the character
		 */
		public char getKey() {
			return _key;
		}
		
		/**
		 * @return the value of it
		 */
		public Object getValue() {
			return _val;
		}
		
		public String toString() {
			return "Element[" + _key + "=" + _val + "]";
		}
	}
	
	/**
	 * The iterator for the char-map
	 * 
	 * @author hrniels
	 */
	private final class CharMapIterator implements Iterator {
		
		/**
		 * The current position
		 */
		private int _pos = 0;

		public boolean hasNext() {
			return _pos < _filled;
		}

		public Object next() {
			if(_pos < _filled)
				return _elements[_pos++];
			
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}