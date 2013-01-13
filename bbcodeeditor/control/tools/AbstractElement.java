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



/**
 * an abstract element for the LinkedList
 * 
 * @author hrniels
 */
public abstract class AbstractElement {

	/**
	 * the previous element
	 */
	AbstractElement _prev;
	
	/**
	 * the next element
	 */
	AbstractElement _next;
	
	/**
	 * constructor
	 * 
	 * @param prev the previous element
	 * @param next the next element
	 */
	public AbstractElement(AbstractElement prev,AbstractElement next) {
		_prev = prev;
		_next = next;
	}

	/**
	 * @return the next element
	 */
	public final AbstractElement getNext() {
		return isLast() ? null : _next;
	}

	/**
	 * @return the previous element
	 */
	public final AbstractElement getPrev() {
		return isFirst() ? null : _prev;
	}
	
	/**
	 * @return true if this is the first element
	 */
	public final boolean isFirst() {
		return _prev != null && _prev._prev == null;
	}
	
	/**
	 * @return true if this is the last element
	 */
	public final boolean isLast() {
		return _next != null && _next._next == null;
	}
}