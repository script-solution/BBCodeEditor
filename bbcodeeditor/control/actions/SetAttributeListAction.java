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
import bbcodeeditor.control.TextAttributes;


/**
 * the action for setting an attribute
 * contains a list of actions
 * 
 * @author hrniels
 */
public final class SetAttributeListAction extends HistoryAction {

	/**
	 * the list of actions
	 */
	private final List _actions;
	
	/**
	 * the attributes to apply
	 */
	private final TextAttributes _attributes;
	
	/**
	 * constructor
	 * 
	 * @param con the Controller-instance
	 * @param actions an ArrayList with SetAttributeAction`s
	 * @param attributes the attributes to apply / remove
	 */
	public SetAttributeListAction(Controller con,List actions,TextAttributes attributes) {
		super(con,-1,-1);
		
		_attributes = attributes;
		
		_actions = actions;
		SetAttributeActionPart first = (SetAttributeActionPart)actions.get(0);
		SetAttributeActionPart last = (SetAttributeActionPart)actions.get(actions.size() - 1);
		_start = first.getStartPosition();
		_end = last.getEndPosition();
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				Iterator it = _actions.iterator();
				while(it.hasNext()) {
					SetAttributeActionPart s = (SetAttributeActionPart)it.next();
					s.execute(_con);
				}
				
				_actionType = REDO;
				break;
				
			case REDO:
				_con.setAttributes(_start,_end,_attributes);
				
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
		
		if(_actionType == REDO)
			return "Set attributes in '" + HistoryAction.getPrintText(buf.toString()) + "'";

		return "Undo set attributes in '" + HistoryAction.getPrintText(buf.toString()) + "'";
	}
	
	public String toString() {
		return getName();
	}
}