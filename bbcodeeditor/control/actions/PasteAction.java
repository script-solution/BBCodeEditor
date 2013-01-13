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
 * A collection for pasting a BBCode-text. Contains multiple actions which will be
 * performed to undo this operation
 * 
 * @author hrniels
 */
public class PasteAction extends HistoryAction {
	
	/**
	 * the text to paste
	 */
	private final IText _text;
	
	/**
	 * The alignment of the start-position
	 */
	private final int _align;
	
	/**
	 * The attributes of the start-position
	 */
	private final TextAttributes _attributes;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param start the start-position
	 * @param end the end-position
	 * @param align the alignment of the start-position
	 * @param attributes the attributes of the start-positin
	 * @param text the text of this action
	 */
	public PasteAction(Controller con,int start,int end,int align,
			TextAttributes attributes,BBCodeText text) {
		super(con,start,end);
		
		_text = text;
		_align = align;
		_attributes = attributes;
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				_con.removeText(_start,_end,false);
				
				// apply the attributes and alignment that the position had
				// before the action has been done
				_con.setLineAlignment(_align);
				_con.setAttributes(_start,_start,_attributes);
				
				_actionType = REDO;
				break;
				
			case REDO:
				_con.goToPosition(_start);
				_con.pasteTextAtCursor(_text.getText(),true);
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		String text = HistoryAction.getPrintText(_text.getText());
		
		String res;
		if(_actionType == UNDO)
			res = "Remove '" + text + "'";
		else
			res = "Add '" + text + "'";
		
		return res + " [" + _start + "]";
	}
	
	public String toString() {
		return getName();
	}
}