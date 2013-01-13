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

package bbcodeeditor.gui.combobox;


/**
 * Represents the content of a cell. Contains a key and a value. The value will be
 * displayed and the key will be passed out if a cell has been selected.<br>
 * The value may be an Image (which be be painted as an image) or any other object
 * which will be painted as a String.
 * 
 * @author hrniels
 */
public class CellContent {

	/**
	 * the key of the cell
	 */
	private final Object _key;
	
	/**
	 * the value of the cell
	 */
	private final Object _value;
	
	/**
	 * constructor
	 * 
	 * @param key the key of the cell
	 * @param value the value of the cell
	 */
	public CellContent(Object key,Object value) {
		_key = key;
		_value = value;
	}
	
	/**
	 * @return the key of the cell
	 */
	public Object getKey() {
		return _key;
	}
	
	/**
	 * @return the value of the cell (the object to display)
	 */
	public Object getValue() {
		return _value;
	}
}