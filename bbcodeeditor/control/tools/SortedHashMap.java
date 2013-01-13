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
 * a HashMap bound together with a SortedArrayList. Therefore you have quick access
 * to a value by a key, you can iterate in a fix order over the elements and can perform
 * a binary search over the keys. Which means also that you have access to the values
 * in a fixed order.#
 * 
 * TODO we can use LinkedHashMap instead!
 */
public final class SortedHashMap implements Map {

	/**
	 * the HashMap which maps the keys to values
	 */
	protected final Map _map;
	
	/**
	 * a sorted list which maps an index to the key of the HashMap
	 */
	protected final SortedArrayList _keys;
	
	/**
	 * constructor
	 * 
	 * @param comparator the comparator to compare the keys
	 */
	public SortedHashMap(Comparator comparator) {
		this(comparator,50);
	}
	
	/**
	 * constructor
	 * 
	 * @param comparator the comparator to compare the keys
	 * @param capacity the initially capacity of the internal array.
	 */
	public SortedHashMap(Comparator comparator,int capacity) {
		_keys = new SortedArrayList(comparator,capacity);
		_map = new HashMap(capacity);
	}

	public Set entrySet() {
		return _map.entrySet();
	}

	public Set keySet() {
		return _map.keySet();
	}

	public Collection values() {
		return _map.values();
	}
	
	public boolean containsKey(Object key) {
		return _keys.contains(key);
	}
	
	public boolean containsValue(Object value) {
		return _map.containsValue(value);
	}

	public Object put(Object key,Object value) {
		if(_keys.add(key))
			return _map.put(key,value);
		
		return null;
	}

	public void putAll(Map map) {
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			put(e.getKey(),e.getValue());
		}
	}
	
	public int size() {
		return _keys.size();
	}

	public boolean isEmpty() {
		return _keys.size() == 0;
	}
	
	public Object get(Object key) {
		if(key == null)
			throw new NullPointerException("Null-values are not permitted!");
		
		if(!(key instanceof Comparable))
			throw new ClassCastException("Invalid type");
		
		return _map.get(key);
	}
	
	/**
	 * searches for the given value and returns the key if found
	 * 
	 * @param value the value of the element you're looking for
	 * @return the key of the given value or null if not found
	 */
	public Object getKey(Object value) {
		Iterator it = _keys.iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Object val = _map.get(key);
			if(val.equals(value))
				return key;
		}
		
		return null;
	}
	
	/**
	 * @return an iterator to go through all elements
	 */
	public Iterator iterator() {
		return _keys.iterator();
	}

	public void clear() {
		_map.clear();
		_keys.clear();
	}

	public Object remove(Object key) {
		Object old = get(key);
		
		_keys.remove(key);
		_map.remove(key);
		
		return old;
	}
	
	/**
	 * removes the element with given value from the list
	 * 
	 * @param value the value of the element you want to remove
	 */
	public void removeValue(Object value) {
		Object key = this.getKey(value);
		if(key != null) {
			_keys.remove(key);
			_map.remove(key);
		}
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		StringBuffer debug = new StringBuffer();

		Iterator it = _keys.iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Object val = _map.get(key);
			debug.append(key + " -> " + val + "\n");
		}
		
		return debug.toString();
	}
}