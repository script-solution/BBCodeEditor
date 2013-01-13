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

import java.util.Iterator;
import java.util.List;

import bbcodeeditor.control.Controller;


/**
 * the remove-text-action
 * 
 * @author hrniels
 */
public final class RemoveTextListAction extends HistoryAction {
	
	/**
	 * the actions to perform
	 */
	private final List _actions;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param actions an ArrayList with the actions to perform
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 */
	public RemoveTextListAction(Controller con,List actions,int start,int end) {
		super(con,start,end);
		
		_actions = actions;
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				Iterator it = _actions.iterator();
				while(it.hasNext()) {
					HistoryActionPart part = (HistoryActionPart)it.next();
					part.execute(_con);
				}
				
				_actionType = REDO;
				break;
			
			case REDO:
				_con.removeText(_start,_end,false);
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		StringBuffer buf = new StringBuffer();
		Iterator it = _actions.iterator();
		while(it.hasNext()) {
			HistoryActionPart part = (HistoryActionPart)it.next();
			buf.append(part.getText(_con));
		}
		
		if(_actionType == UNDO)
			return "Add '" + HistoryAction.getPrintText(buf.toString()) + "'";

		return "Remove text '" + HistoryAction.getPrintText(buf.toString()) + "'";
	}
	
	public String toString() {
		return getName();
	}
}