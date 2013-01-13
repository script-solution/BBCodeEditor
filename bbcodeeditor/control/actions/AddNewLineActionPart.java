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

package bbcodeeditor.control.actions;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.TextAttributes;


/**
 * An action-part which adds a new line with an alignment and attributes at a position
 * 
 * @author hrniels
 */
public class AddNewLineActionPart extends HistoryActionPart {
	
	/**
	 * the attributes of the new line
	 */
	private final TextAttributes _attributes;
	
	/**
	 * the alignment of the new line
	 */
	private final int _align;
	
	/**
	 * should the new line be a list-point?
	 */
	private final boolean _isListPoint;

	/**
	 * constructor
	 * 
	 * @param start the global position
	 * @param attributes the attributes of the new line
	 * @param align the alignment of the new line
	 * @param isListPoint should the new line be a list-point?
	 */
	public AddNewLineActionPart(int start,TextAttributes attributes,int align,
			boolean isListPoint) {
		super(start);
		
		_attributes = attributes;
		_align = align;
		_isListPoint = isListPoint;
	}
	
	public void execute(Controller con) {
		con.goToPosition(_start);
		con.addNewLine(_attributes,_align,_isListPoint);
	}
	
	public String getName() {
		return "Add '{nl}' [" + _start + "]";
	}
	
	public String getText(Controller con) {
		return "{nl}";
	}
	
	public String toString() {
		return getName();
	}
}