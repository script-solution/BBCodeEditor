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


/**
 * the action to remove a char
 * 
 * @author hrniels
 */
public final class RemoveTextAction extends HistoryAction {

	/**
	 * the removed text
	 */
	private final IText _text;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param start the start-position of this action (global)
	 * @param end the end-position of this action (global)
	 * @param text the text to remove
	 */
	public RemoveTextAction(Controller con,int start,int end,IText text) {
		super(con,start,end);
		
		_text = text;
	}
	
	public void performAction() {
		switch(this._actionType) {
			case UNDO:
				_con.goToPosition(_start);
				_con.pasteTextAtCursor(_text.getText(),true);
				
				_actionType = REDO;
				break;
			
			case RemoveTextListAction.REDO:
				_con.removeText(_start,_end,false);
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		String text = HistoryAction.getPrintText(_text.getText());
		
		String res;
		if(_actionType == UNDO)
			res = "Add '" + text + "'";
		else
			res = "Remove '" + text + "'";
		
		return res + " [" + _start + "," + _end + "]";
	}
	
	public String toString() {
		return getName();
	}
}