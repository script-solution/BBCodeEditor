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
 * An action which replaces a text with a smiley
 * 
 * @author hrniels
 */
public class ReplaceSmileyListAction extends HistoryAction {

	/**
	 * the actions to perform
	 */
	private final List _actions;
	
	/**
	 * has text been replaced?
	 */
	private final boolean _replace;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 * @param actions the actions to perform
	 * @param replace has text been replaced?
	 */
	public ReplaceSmileyListAction(Controller con,int start,int end,List actions,
			boolean replace) {
		super(con,start,end);

		_actions = actions;
		_replace = replace;
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				// at first replace the complete text
				_con.removeText(_start,_end,false);
				
				// if necessary insert the first action
				if(_replace) {
					// we have to walk to the position because the actions may be cursor-dependend
					_con.goToPosition(_start);
					
					HistoryActionPart first = (HistoryActionPart)_actions.get(0);
					first.execute(_con);
				}

				_actionType = REDO;
				break;
				
			case REDO:
				// we have to walk to the position because the actions may be cursor-dependend
				_con.goToPosition(_start);
				
				Iterator it = _actions.iterator();
				
				// remove the first if necessary
				if(_replace) {
					HistoryActionPart first = (HistoryActionPart)it.next();
					if(first instanceof AddPlainTextInEnvActionPart) {
						AddPlainTextInEnvActionPart aFirst = (AddPlainTextInEnvActionPart)first;
						_con.removeText(_start,_start + aFirst.getText(_con).length(),false);
					}
				}
				
				// perform the actions
				while(it.hasNext()) {
					HistoryActionPart part = (HistoryActionPart)it.next();
					part.execute(_con);
				}
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		String rem = "";
		int i = 0;
		StringBuffer buf = new StringBuffer();
		Iterator it = _actions.iterator();
		while(it.hasNext()) {
			HistoryActionPart part = (HistoryActionPart)it.next();
			
			if(_replace && i == 0)
				rem = part.getText(_con);
			else
				buf.append(part.getText(_con));
			
			i++;
		}
		
		String remText = HistoryAction.getPrintText(rem);
		String text = HistoryAction.getPrintText(buf.toString());
		
		if(_actionType == UNDO) {
			if(_replace)
				return "Replace '" + text + "' with '" + remText + "'";
			
			return "Remove '" + text + "'";
		}
		
		if(_replace)
			return "Replace '" + remText + "' with '" + text + "'";
		
		return "Add '" + text + "'";
	}
	
	public String toString() {
		return getName();
	}
}