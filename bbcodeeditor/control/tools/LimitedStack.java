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

import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Assi Nilsmussen
 */
public class LimitedStack {

	private LinkedList _list;
	private int _limit = 50;
	
	/**
	 * constructor
	 */
	public LimitedStack() {
		this._list = new LinkedList();
	}
	
	/**
	 * constructor
	 * 
	 * @param limit the limit of this list
	 */
	public LimitedStack(int limit) {
		this._limit = limit;
		this._list = new LinkedList();
	}
	
	/**
	 * clears the stack
	 */
	public void clear() {
		this._list.clear();
	}
	
	/**
	 * adds the given object to list
	 * 
	 * @param obj the object you want to add
	 */
	public void push(Object obj) {
		if(this.length() == this._limit)
			this._list.removeLast();
		
		this._list.addFirst(obj);
	}
	
	/**
	 * removes and returns the first element
	 * 
	 * @return the first element in the list
	 */
	public Object pop() {
		return this._list.removeFirst();
	}
	
	/**
	 * sets the limit to given value. if the list is greater than the given limit it will be trimmed.
	 * 
	 * @param limit the new limit
	 */
	public void setLimit(int limit) {
		while(this.length() > limit)
			this._list.removeLast();
		
		this._limit = limit;
	}
	
	/**
	 * @return the number of elements in this list
	 */
	public int length() {
		return this._list.size();
	}
	
	/**
	 * Builds an array of the elements and returns it
	 * 
	 * @return the array with all elements
	 */
	public Object[] toArray() {
		return _list.toArray();
	}
	
	public String toString() {
		StringBuffer debug = new StringBuffer();
		Iterator it = this._list.iterator();
		for(int i = 0;it.hasNext();i++) {
			debug.append(it.next());
			if(i < this.length() - 1)
				debug.append("\n");
		}
		return debug.toString();
	}
}