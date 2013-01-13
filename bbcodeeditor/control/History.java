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

import java.util.*;

import bbcodeeditor.control.actions.HistoryAction;
import bbcodeeditor.control.actions.HistoryActionList;
import bbcodeeditor.control.events.HistoryChangedListener;
import bbcodeeditor.control.tools.LimitedStack;


/**
 * contains all actions for undo/redo<br>
 * The number of actions for each type is limited to a specific number.
 * 
 * @author hrniels
 */
public final class History {
	
	/**
	 * the undo-actions
	 */
	private final LimitedStack _undo;
	
	/**
	 * the redo-actions
	 */
	private final LimitedStack _redo;
	
	/**
	 * A list with all listeners
	 */
	private final List _listeners = new ArrayList();
	
	/**
	 * The cache to group multiple actions to one
	 */
	private List _cache = new ArrayList();
	
	/**
	 * Are we currently caching the actions that are added?
	 */
	private boolean _cacheActions = false;

	/**
	 * constructor
	 * 
	 * @param limit the limit for the stacks
	 */
	public History(int limit) {
		_undo = new LimitedStack(limit);
		_redo = new LimitedStack(limit);
	}
	
	/**
	 * sets the limit for the two lists
	 * 
	 * @param limit the new limit
	 */
	void setLimit(int limit) {
		if(limit > 0) {
			_undo.setLimit(limit);
			_redo.setLimit(limit);
		}
	}
	
	/**
	 * clears the undo and redo history
	 */
	public void clear() {
		_cache.clear();
		_undo.clear();
		_redo.clear();
		
		fireHistoryChange();
	}
	
	/**
	 * @return the number of saved undo-operations
	 */
	public int getUndoLength() {
		return _undo.length();
	}
	
	/**
	 * @return the number of saved redo-operations
	 */
	public int getRedoLength() {
		return _redo.length();
	}
	
	/**
	 * Starts to cache all following actions
	 */
	void startCaching() {
		// if we already cache the actions we want to add them and start a new
		// cache-process
		stopCaching();
		
		_cacheActions = true;
	}
	
	/**
	 * Stops the caching and adds the collected actions to the undo-actions
	 */
	void stopCaching() {
		if(!_cacheActions)
			return;
		
		if(_cache.size() > 0) {
			_undo.push(_cache);
			fireHistoryChange();
		}
		
		_cacheActions = false;
		_cache = new ArrayList();
	}
	
	/**
	 * Adds the given list with actions to the history
	 * 
	 * @param list the action-list
	 */
	void addActions(HistoryActionList list) {
		if(_cacheActions)
			_cache.add(list);
		else {
			_undo.push(Arrays.asList(new HistoryActionList[] {list}));
			_redo.clear();
				
			fireHistoryChange();
		}
	}
	
	/**
	 * adds the given action to the undo-list
	 * 
	 * @param action the Action to add
	 */
	void addAction(HistoryAction action) {
		HistoryActionList list = new HistoryActionList();
		list.addAction(action);
		
		if(_cacheActions)
			_cache.add(list);
		else {
			_undo.push(Arrays.asList(new HistoryActionList[] {list}));
			_redo.clear();
		
			fireHistoryChange();
		}
	}
	
	/**
	 * undo's the last action
	 */
	void undo() {
		stopCaching();
		
		if(_undo.length() > 0) {
			List actions = (List)_undo.pop();
			// Note that we have to do that in reverse order
			ListIterator it = actions.listIterator(actions.size());
			while(it.hasPrevious()) {
				HistoryActionList list = (HistoryActionList)it.previous();
				list.execute();
			}
			
			_redo.push(actions);
			fireHistoryChange();
		}
	}
	
	/**
	 * redo's the last action
	 */
	void redo() {
		stopCaching();
		
		if(_redo.length() > 0) {
			List actions = (List)_redo.pop();
			Iterator it = actions.iterator();
			while(it.hasNext()) {
				HistoryActionList list = (HistoryActionList)it.next();
				list.execute();
			}
			
			_undo.push(actions);
			fireHistoryChange();
		}
	}
	
	/**
	 * Adds the given listener to the list. It will receive an event as soon as the number
	 * of undo- or redo-items has changed
	 * 
	 * @param l the listener
	 */
	void addHistoryChangedListener(HistoryChangedListener l) {
		_listeners.add(l);
	}
	
	/**
	 * Removes the given listener from the list
	 * 
	 * @param l the listener
	 */
	void removeHistoryChangedListener(HistoryChangedListener l) {
		_listeners.remove(l);
	}
	
	/**
	 * fires a history-change
	 */
	private void fireHistoryChange() {
		Iterator it = _listeners.iterator();
		while(it.hasNext()) {
			HistoryChangedListener l = (HistoryChangedListener)it.next();
			l.historyChanged(_undo.length(),_redo.length());
		}
	}
	
	public String toString() {
		StringBuffer debug = new StringBuffer();
		debug.append("Undo:\n");
		debug.append(_undo.toString());
		debug.append("\n\n");
		debug.append("Redo:\n");
		debug.append(_redo.toString());
		debug.append("\n");
		return debug.toString();
	}
}