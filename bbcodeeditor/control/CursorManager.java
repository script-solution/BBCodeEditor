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


/**
 * The cursor-manager for the textfield.
 * 
 * @author hrniels
 */
public class CursorManager {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;

	/**
	 * The last position
	 */
	private int _lastPos = -1;
	
	/**
	 * The last section
	 */
	private ContentSection _lastSec = null;
	
	/**
	 * Stores wether the content has changed
	 */
	private boolean _contentChanged = false;

	/**
	 * Stores wether we should fire the event in every case
	 */
	private boolean _forceCursorChange = false;
	
	/**
	 * Constructor
	 * 
	 * @param tf the textfield-instance
	 */
	public CursorManager(AbstractTextField tf) {
		_textField = tf;
	}
	
	/**
	 * Use this method if you want to fire the cursor-change-event also
	 * if the cursor-position has not changed
	 */
	public void forceCursorChange() {
		_forceCursorChange = true;
	}
	
	/**
	 * Marks that the content has changed
	 */
	public void markContentChanged() {
		_contentChanged = true;
	}
	
	/**
	 * If the cursor has changed the caret-changed-event will be fired
	 */
	public void checkChange() {
		int pos = _textField.getCurrentCursorPos();
		ContentSection sec = _textField.getCurrentSection();
		
		if(_forceCursorChange || (pos != _lastPos && _lastPos >= 0)) {
			_textField.invokeCaretPositionChangeListeners(_lastPos,_lastSec,
					_contentChanged);
		}
		
		_lastPos = pos;
		_lastSec = sec;
		_forceCursorChange = false;
		_contentChanged = false;
	}
}