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
import java.util.Iterator;
import java.util.List;

import bbcodeeditor.control.events.SelectionChangedListener;


/**
 * This class manages the selection.<br>
 * It changes the selection by given positions, saves the current start- and end-position
 * and the current direction.
 * 
 * @author hrniels
 */
public final class Selection implements Cloneable {
	
	/**
	 * no direction => no selection ;)
	 */
	public final static int DIR_NONE = -1;
	
	/**
	 * if the selection goes to the left side
	 */
	public final static int DIR_LEFT = 0;
	
	/**
	 * if the selection goes to the right side
	 */
	public final static int DIR_RIGHT = 1;
	
	/**
	 * the instance of the Controller
	 */
	private final Controller _con;
	
	/**
	 * The selection listeners
	 */
	private final List _listeners = new ArrayList();
	
	/**
	 * the start of the selection (-1 if no selection)
	 */
	private int _selectionStart = -1;
	
	/**
	 * the end of the selection (-1 if no selection)
	 */
	private int _selectionEnd = -1;
	
	/**
	 * the direction of the selection (DIR_NONE if no selection)
	 */
	private int _selectionDir = DIR_NONE;

	/**
	 * constructor
	 * 
	 * @param con the Controller
	 */
	Selection(Controller con) {
		_con = con;
	}
	
	/**
	 * @return a clone of this object
	 */
	public Object clone() {
		Selection sel = new Selection(_con);
		sel._selectionStart = _selectionStart;
		sel._selectionEnd = _selectionEnd;
		sel._selectionDir = _selectionDir;
		return sel;
	}
	
	/**
	 * Adds the given listener to the list. It will receive an event as soon as the number
	 * of undo- or redo-items has changed
	 * 
	 * @param l the listener
	 */
	void addSelectionChangedListener(SelectionChangedListener l) {
		_listeners.add(l);
	}
	
	/**
	 * Removes the given listener from the list
	 * 
	 * @param l the listener
	 */
	void removeSelectionChangedListener(SelectionChangedListener l) {
		_listeners.remove(l);
	}
	
	/**
	 * Fires a selection change
	 */
	private void fireSelectionChange() {
		Iterator it = _listeners.iterator();
		while(it.hasNext()) {
			SelectionChangedListener l = (SelectionChangedListener)it.next();
			l.selectionChanged(isEmpty(),_selectionStart,_selectionEnd,_selectionDir);
		}
	}
	
	/**
	 * returns true if we are in the selection mode
	 * 
	 * @return true if in selection-mode
	 */
	public boolean isInSelectionMode() {
		return _selectionStart != -1 || _selectionEnd != -1;
	}
	
	/**
	 * @return true if the selection is empty
	 */
	public boolean isEmpty() {
		return _selectionStart < 0 || _selectionStart == _selectionEnd;
	}
	
	/**
	 * the direction of the selection
	 * 
	 * @return the direction
	 */
	public int getSelectionDirection() {
		return _selectionDir;
	}
	
	/**
	 * the start-position of the selection
	 * 
	 * @return the start-position
	 */
	public int getSelectionStart() {
		return _selectionStart;
	}
	
	/**
	 * the end-position of the selection
	 * 
	 * @return the end-position
	 */
	public int getSelectionEnd() {
		return _selectionEnd;
	}
	
	/**
	 * clears the selection
	 */
	void clearSelection() {
		setSelection(-1,-1,DIR_NONE);
	}
	
	/**
	 * selects all text in the given environment
	 * 
	 * @param env the root-environment
	 */
	void selectAll(Environment env) {
		setSelection(0,env.getElementLength(),DIR_RIGHT);
	}
	
	/**
	 * changes the selection.
	 * 
	 * @param shiftDown is shift pressed?
	 * @param oldPos the old position of the cursor
	 * @param newPos the new position of the cursor
	 * @param direction the direction of the selection (use the SelectionDirection-class)
	 */
	void changeSelection(boolean shiftDown,int oldPos,int newPos,int direction) {
		if(shiftDown) {
			// we are not in selection-mode so create a new selection
			if(!isInSelectionMode()) {
				if(direction == DIR_LEFT)
					setSelection(newPos,oldPos,direction);
				else
					setSelection(oldPos,newPos,direction);
			}
			// left direction
			else if(_selectionDir == DIR_LEFT) {
				if(newPos > _selectionEnd)
					setSelection(_selectionEnd,newPos,direction);
				else
					setSelection(newPos,_selectionEnd,_selectionDir);
			}
			// right direction
			else {
				if(newPos < _selectionStart)
					setSelection(newPos,_selectionStart,direction);
				else
					setSelection(_selectionStart,newPos,_selectionDir);
			}
		}
		// shift is not pressed, so clear the selection
		else if(isInSelectionMode())
			clearSelection();
	}
	
	/**
	 * sets the selection
	 * 
	 * @param startPos the start-position
	 * @param endPos the end-position
	 * @param direction the direction
	 * @param startEnv the start-environment
	 * @param startSec the start-section
	 * @param endEnv the end-environment
	 * @param endSec the end-section
	 */
	private void setSelection(int startPos,int endPos,int direction) {
		int from = Math.min(_selectionStart,startPos);
		int to = Math.max(_selectionEnd,endPos);
		
		_con.getRootEnvironment().setSelected(from,to,startPos,endPos);

		// nothing selected?
		_selectionStart = startPos;
		_selectionEnd = endPos;
		_selectionDir = direction;
		
		fireSelectionChange();
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		StringBuffer debug = new StringBuffer();
		debug.append("  Start: " + _selectionStart + " | ");
		debug.append("  End: " + _selectionEnd + " | ");
		
		debug.append("  Direction: ");
		switch(_selectionDir) {
			case Selection.DIR_LEFT:
				debug.append("left");
				break;
			case Selection.DIR_RIGHT:
				debug.append("right");
				break;
			default:
				debug.append("none");
				break;
		}
		
		return debug.toString();
	}
}