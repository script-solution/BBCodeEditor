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

import java.util.*;



/**
 * @author Assi Nilsmussen
 * 
 * a sorted ArrayList. That means that you can store objects of an arbitrary type
 * in a list which will be sorted. The objects have to implement the Comparable-interface
 * and you have to provide a Comparator-function
 */
public final class SortedArrayList implements List {
	
	/**
	 * the comparator to compare the elements
	 */
	private final Comparator _comparator;

	/**
	 * the size for increasing the array
	 */
	private int _increaseSize;
	
	/**
	 * the element-array
	 */
	private Comparable[] _elements;
	
	/**
	 * the index of the next element which will be inserted
	 */
	private int _nextIndex = 0;
	
	/**
	 * constructor
	 * 
	 * @param comparator the comparator to compare the elements
	 */
	public SortedArrayList(Comparator comparator) {
		this(comparator,10);
	}
	
	/**
	 * constructor
	 * 
	 * @param comparator the comparator to compare the elements
	 * @param capacity the initially capacity of the internal array.
	 * this value will also be used to increase the array-size
	 */
	public SortedArrayList(Comparator comparator,int capacity) {
		_increaseSize = capacity;
		_comparator = comparator;
		_elements = new Comparable[this._increaseSize];
	}

	public int indexOf(Object o) {
		if(!(o instanceof Comparable))
			return -1;
		
		return Arrays.binarySearch(toArray(),o,_comparator);
	}

	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("This method is not supported!");
	}
	
	public boolean add(Object value) throws ClassCastException {
		if(!(value instanceof Comparable))
			throw new ClassCastException("Invalid type!");
		
		this.ensureCapacity();
		
		// insert at next position (for performance issues)
		if(this._nextIndex == 0 || _comparator.compare(_elements[_nextIndex - 1],value) < 0) {
			this._elements[this._nextIndex++] = (Comparable)value;
			return true;
		}
		
		int pos = indexOf(value);
		if(pos < 0) {
			pos = -pos - 1;
			for(int i = this._nextIndex - 1;i >= pos;i--)
				this._elements[i + 1] = this._elements[i];
			
			this._elements[pos] = (Comparable)value;
			this._nextIndex++;
			return true;
		}
		
		return false;
	}

	public boolean addAll(Collection c) throws ClassCastException {
		Iterator it = c.iterator();
		int i = 0;
		while(it.hasNext()) {
			add(it.next());
			i++;
		}
		
		return i > 0;
	}

	public void add(int arg0,Object arg1) {
		throw new UnsupportedOperationException("This list is sorted, therefore it is not"
				+ " possible to insert an element at a specified position!");
	}

	public boolean addAll(int arg0,Collection arg1) {
		throw new UnsupportedOperationException("This list is sorted, therefore it is not"
				+ " possible to insert an element at a specified position!");
	}

	public Object set(int arg0,Object arg1) {
		throw new UnsupportedOperationException("This list is sorted, therefore it is not"
				+ " possible to set an element at a specified position!");
	}

	public boolean contains(Object o) {
		if(!(o instanceof Comparable))
			return false;
		
		try {
			return indexOf(o) >= 0;
		}
		catch(Exception e) {
			return false;
		}
	}

	public boolean containsAll(Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			if(!contains(it.next()))
				return false;
		}
		
		return true;
	}

	public Iterator iterator() {
		return new SortedArrayListIterator();
	}

	public ListIterator listIterator() {
		return new SortedArrayListListIterator(0);
	}

	public ListIterator listIterator(int index) {
		if(index < 0 || index >= _nextIndex)
			throw new IndexOutOfBoundsException("invalid index");
		
		return new SortedArrayListListIterator(index);
	}

	public boolean isEmpty() {
		return _nextIndex == 0;
	}

	public int size() {
		return this._nextIndex;
	}
	
	public void clear() {
		this._elements = new Comparable[this._increaseSize];
		this._nextIndex = 0;
	}
	
	public Object get(int index) throws IndexOutOfBoundsException {
		if(index >= 0 && index < this._nextIndex)
			return this._elements[index];
		
		throw new IndexOutOfBoundsException("The index " + index + " is out of bounds");
	}

	public boolean remove(Object o) throws ClassCastException {
		if(!(o instanceof Comparable))
			throw new ClassCastException("The collection does just support Comparable-implementations");
		
		try {
			int pos = this.indexOf(o);
			if(pos >= 0)
				return remove(pos) != null;
		}
		catch(Exception e) {
			
		}
		
		return false;
	}

	public Object remove(int index) {
		if(index >= 0 && index < this._nextIndex) {
			for(int i = index + 1;i < this._nextIndex;i++)
				this._elements[i - 1] = this._elements[i];
			this._nextIndex--;
			return _elements[index];
		}
		
		return null;
	}

	public boolean removeAll(Collection c) throws ClassCastException {
		int i = 0;
		Iterator it = c.iterator();
		while(it.hasNext()) {
			if(remove(it.next()))
				i++;
		}
		
		return i > 0;
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException("This method is not supported!");
	}

	public Object[] toArray() {
		Object[] copy = new Object[_elements.length];
		System.arraycopy(_elements,0,copy,0,_nextIndex);
		if(copy.length > _nextIndex)
      copy[_nextIndex] = null;
		return copy;
	}

	public Object[] toArray(Object[] a) {
		/*if(!(a instanceof Comparable[]))
			return null;
		
		System.arraycopy(_elements,0,a,0,_nextIndex);
		if(a.length > _nextIndex)
      a[_nextIndex] = null;*/
		return a;
	}

	public List subList(int fromIndex,int toIndex) {
		throw new UnsupportedOperationException("This method is not supported!");
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		StringBuffer debug = new StringBuffer();
		for(int i = 0;i < this._nextIndex;i++)
			debug.append(i + " -> " + this._elements[i] + "\n");
		return debug.toString();
	}
	
	/**
	 * ensures that the size of the internal array is big enough
	 */
	private void ensureCapacity() {
		if(this._nextIndex >= this._elements.length) {
			Comparable[] temp = this._elements;
			int size = this._elements.length;
			this._elements = new Comparable[size + this._increaseSize];
			
			for(int i = 0;i < size;i++)
				this._elements[i] = temp[i];
		}
	}
	
	/**
	 * an iterator which allows you to iterate through the elements
	 */
	private class SortedArrayListIterator implements Iterator {
		
		/**
		 * the current index
		 */
		private int _index = 0;
		
		/**
		 * @return true if a next element exists
		 */
		public boolean hasNext() {
			return this._index < _nextIndex;
		}

		public Object next() {
			if(this.hasNext())
				return _elements[this._index++];

			throw new NoSuchElementException("No next element!");
		}

		public void remove() {
			throw new UnsupportedOperationException("This method is not supported!");
		}
	}
	
	/**
	 * an iterator which allows you to iterate through the elements
	 */
	private class SortedArrayListListIterator implements ListIterator {
		
		/**
		 * the current index
		 */
		private int _index;
		
		/**
		 * constructor
		 * 
		 * @param index the index where to start
		 */
		public SortedArrayListListIterator(int index) {
			_index = index;
		}

		public boolean hasPrevious() {
			return _index > 0;
		}

		public Object previous() {
			if(hasPrevious())
				return _elements[_index--];
			
			throw new NoSuchElementException("No previous element!");
		}

		public int previousIndex() {
			if(hasPrevious())
				return _index;
			
			return -1;
		}
		
		/**
		 * @return true if a next element exists
		 */
		public boolean hasNext() {
			return this._index < _nextIndex;
		}

		public int nextIndex() {
			if(hasNext())
				return _nextIndex;
			
			return _index;
		}

		public Object next() {
			if(this.hasNext())
				return _elements[this._index++];
			
			throw new NoSuchElementException("No next element!");
		}

		public void remove() {
			throw new UnsupportedOperationException("This method is not supported!");
		}

		public void add(Object arg0) {
			throw new UnsupportedOperationException("This method is not supported!");
		}

		public void set(Object arg0) {
			throw new UnsupportedOperationException("This method is not supported!");
		}
	}
}