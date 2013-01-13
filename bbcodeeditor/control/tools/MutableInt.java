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
 * a class that contains an integer which can by modified.
 * can by used as a parameter that should be "passed by reference"
 */
public class MutableInt {
	
	private int _val = 0;
	
	/**
	 * constructor
	 */
	public MutableInt() {
		
	}
	
	/**
	 * constructor
	 * 
	 * @param val the initial value of this object
	 */
	public MutableInt(int val) {
		this._val = val;
	}
	
	/**
	 * sets the value to <code>val</code>
	 * 
	 * @param val the new value of this object
	 */
	public void setValue(int val) {
		this._val = val;
	}
	
	/**
	 * @return the value of this object
	 */
	public int getValue() {
		return this._val;
	}
	
	/**
	 * increases the value by 1
	 */
	public void increaseValue() {
		this._val++;
	}
	
	/**
	 * increases the value by amount
	 * 
	 * @param amount the amount by which you want to increase
	 */
	public void increaseValue(int amount) {
		this._val += amount;
	}
	
	/**
	 * decreases the value by 1
	 */
	public void decreaseValue() {
		this._val--;
	}
	
	/**
	 * decreases the value by amount
	 * 
	 * @param amount the amount by which you want to decrease
	 */
	public void decreaseValue(int amount) {
		this._val -= amount;
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		return "[" + this._val + "]";
	}
}