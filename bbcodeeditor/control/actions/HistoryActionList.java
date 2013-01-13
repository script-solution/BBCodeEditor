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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A list of actions
 * 
 * @author hrniels
 */
public class HistoryActionList {

	/**
	 * The action-list
	 */
	protected final List _actions;
	
	/**
	 * Constructor
	 */
	public HistoryActionList() {
		this(new ArrayList());
	}

	/**
	 * Constructor
	 * 
	 * @param actions all actions of this history-action-list
	 */
	public HistoryActionList(List actions) {
		_actions = actions;
	}
	
	/**
	 * @return all actions
	 */
	public List getActions() {
		return _actions;
	}
	
	/**
	 * Executes the action-list and changes the status
	 */
	public void execute() {
		if(_actions.size() == 0)
			return;
		
		int type = ((HistoryAction)_actions.get(0))._actionType;
		if(type == HistoryAction.UNDO) {
			// we have to walk backwards because the actions have to be undone in
			// the opposite order
			ListIterator it = _actions.listIterator(_actions.size());
			while(it.hasPrevious()) {
				HistoryAction action = (HistoryAction)it.previous();
				action.performAction();
			}
		}
		else {
			Iterator it = _actions.iterator();
			while(it.hasNext()) {
				HistoryAction action = (HistoryAction)it.next();
				action.performAction();
			}
		}
	}
	
	/**
	 * Adds the given action to the list
	 * 
	 * @param action the action to add
	 */
	public void addAction(HistoryAction action) {
		_actions.add(action);
	}
	
	/**
	 * Removes the given action from the list
	 * 
	 * @param action the action to remove
	 */
	public void removeAction(HistoryAction action) {
		_actions.remove(action);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		
		Iterator it = _actions.iterator();
		while(it.hasNext()) {
			HistoryAction action = (HistoryAction)it.next();
			buf.append(action);
			if(it.hasNext())
				buf.append(" | ");
		}
		
		buf.append("}");
		return buf.toString();
	}
}