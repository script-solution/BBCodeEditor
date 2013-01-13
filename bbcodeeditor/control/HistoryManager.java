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

package bbcodeeditor.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bbcodeeditor.control.actions.HistoryAction;
import bbcodeeditor.control.actions.HistoryActionList;


/**
 * The history-manager which collects actions and adds them to the history
 * if necessary
 * 
 * @author hrniels
 */
public class HistoryManager {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;

	/**
	 * The collected actions
	 */
	private final List _actions = new ArrayList();
	
	/**
	 * Constructor
	 * 
	 * @param tf the textfield
	 */
	public HistoryManager(AbstractTextField tf) {
		_textField = tf;
	}
	
	/**
	 * Adds the given action to the list
	 * 
	 * @param action the action
	 */
	public void add(HistoryAction action) {
		_actions.add(action);
	}
	
	/**
	 * @return the number of entries
	 */
	public int size() {
		return _actions.size();
	}
	
	/**
	 * Removes the last <code>amount</code> added entries
	 * 
	 * @param amount the number of entries to remove
	 */
	public void removeLast(int amount) {
		for(int i = 0;_actions.size() > 0 && i < amount;i++)
			_actions.remove(_actions.size() - 1);
	}
	
	/**
	 * Clears all actions
	 */
	public void clear() {
		_actions.clear();
	}
	
	/**
	 * Adds all collected actions to the history and resets this container
	 */
	public void addToHistory() {
		if(_actions.size() > 0) {
			List actions = Arrays.asList(_actions.toArray(new HistoryAction[0]));
			HistoryActionList list = new HistoryActionList(actions);
			_textField.getHistory().addActions(list);
			_actions.clear();
		}
	}
}