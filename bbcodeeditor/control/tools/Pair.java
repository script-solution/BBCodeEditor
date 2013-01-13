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

import java.util.Arrays;


/**
 * A simple pair of two objects
 * 
 * @author hrniels
 */
public class Pair {
	
	/**
	 * The key
	 */
	private final Object _key;
	
	/**
	 * The value
	 */
	private final Object _value;

	/**
	 * Constructor
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public Pair(Object key,Object value) {
		_key = key;
		_value = value;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Pair))
			return false;
		
		if(o == this)
			return true;
		
		Pair p = (Pair)o;
		return p._key.equals(_key) && p._value.equals(_value);
	}
	
	public int hashCode() {
		return Arrays.hashCode(new Object[] {_key,_value});
	}
	
	/**
	 * @return the key
	 */
	public Object getKey() {
		return _key;
	}
	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return _value;
	}
	
	public String toString() {
		return _key + "=>" + _value;
	}
}