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
 * @author Assi Nilsmussen
 *
 */
public class MutableBoolean {
	
	private boolean _val = false;
	
	/**
	 * constructor
	 */
	public MutableBoolean() {
		
	}
	
	/**
	 * constructor
	 * 
	 * @param val the initial value of this object
	 */
	public MutableBoolean(boolean val) {
		this._val = val;
	}
	
	/**
	 * sets the value to <code>val</code>
	 * 
	 * @param val the new value of this object
	 */
	public void setValue(boolean val) {
		this._val = val;
	}
	
	/**
	 * @return the value of this object
	 */
	public boolean getValue() {
		return this._val;
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		return String.valueOf(this._val);
	}
}