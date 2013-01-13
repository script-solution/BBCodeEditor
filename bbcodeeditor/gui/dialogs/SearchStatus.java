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

package bbcodeeditor.gui.dialogs;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.Selection;
import bbcodeeditor.control.tools.TextPart;

/**
 * Stores and manages the status of the search
 * 
 * @author hrniels
 */
class SearchStatus {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;
	
	/**
	 * Stores wether we have searched at least once
	 */
	private boolean _initialized = false;
	
	/**
	 * Stores wether the next find should restart the search
	 */
	private boolean _requiresRestart = true;
	
	/**
	 * Stores wether the direction has changed
	 */
	private boolean _dirChanged = false;
	
	/**
	 * The last match
	 */
	private TextPart _lastMatch = null;

	/**
	 * The current keyword
	 */
	private String _keyword = null;
	
	/**
	 * Stores wether the current direction is forward
	 */
	private boolean _forward = false;
	
	/**
	 * Stores wether the scope is "all"
	 */
	private boolean _scopeIsAll = false;
	
	/**
	 * Stores wether case-sensitivity is currently enabled
	 */
	private boolean _caseSensitiv = false;
	
	/**
	 * The current match-number
	 */
	private int _pos = -1;
	
	/**
	 * The number of matches
	 */
	private int _matches = -1;
	
	/**
	 * The selection start-pos
	 */
	private int _selStart = -1;
	
	/**
	 * The selection-end-pos
	 */
	private int _selEnd = -1;
	
	/**
	 * Constructor
	 * 
	 * @param textField the textField
	 */
	public SearchStatus(AbstractTextField textField) {
		_textField = textField;
	}
	
	/**
	 * Inits the search
	 * 
	 * @param keyword the entered keyword
	 * @param caseSensitive case-sensitive?
	 * @param forward search forward?
	 * @param scopeIsAll is the scope "all"?
	 */
	public void initSearch(String keyword,boolean caseSensitive,boolean forward,
			boolean scopeIsAll) {
		boolean hasChanged = false;
		Selection sel = _textField.getSelection();
		
		if(!keyword.equals(_keyword))
			hasChanged = true;
		else if(caseSensitive != _caseSensitiv)
			hasChanged = true;
		else if(scopeIsAll != _scopeIsAll)
			hasChanged = true;
		else if(!_scopeIsAll && sel.getSelectionStart() != _selStart)
			hasChanged = true;
		else if(!_scopeIsAll && sel.getSelectionEnd() != _selEnd)
			hasChanged = true;
		
		_requiresRestart = !_initialized || hasChanged;
		_dirChanged = forward != _forward;
		_initialized = true;
		
		_keyword = keyword;
		_caseSensitiv = caseSensitive;
		_forward = forward;
		_scopeIsAll = scopeIsAll;
		_selStart = sel.getSelectionStart();
		_selEnd = sel.getSelectionEnd();
	}
	
	/**
	 * Resets everything so that we start from the beginning
	 */
	public void reset() {
		_initialized = false;
	}
	
	/**
	 * Reports a start of the search
	 * 
	 * @param matches the number of matches
	 */
	public void reportStart(int matches) {
		_matches = matches;
		_pos = _forward ? 1 : _matches;
	}
	
	/**
	 * Finishes a search. Updates the match
	 * 
	 * @param match the found match
	 */
	public void finishSearch(TextPart match) {
		_lastMatch = match;
		_pos += _forward ? 1 : -1;
	}
	
	/**
	 * Determines the start- and end-position for the search. Returns
	 * null if the last search was not successfull and therefore there is
	 * no position with which we can continue.
	 * 
	 * @return the scope where to search in: <code>array(start,end)</code>
	 */
	public int[] getScope() {
		int start,end;
		Selection sel = _textField.getSelection();
		
		if(_requiresRestart) {
			if(!sel.isEmpty()) {
				start = sel.getSelectionStart();
				end = sel.getSelectionEnd();
			}
			else {
				start = 0;
				end = _textField.length();
			}
			
			return new int[] {start,end};
		}
		
		if(_lastMatch == null)
			return null;
		
		if(_scopeIsAll || sel.isEmpty()) {
			if(_forward) {
				start = _lastMatch.endPos;
				end = _textField.length();
			}
			else {
				start = 0;
				end = _lastMatch.startPos;
			}
		}
		else {
			if(_forward) {
				start = _lastMatch.endPos;
				end = sel.getSelectionEnd();
			}
			else {
				start = sel.getSelectionStart();
				end = _lastMatch.startPos;
			}
		}
		
		if(_dirChanged) {
			if(_forward)
				end--;
			else
				start++;
		}
		
		if(start >= end) {
			reset();
			return null;
		}
		
		return new int[] {start,end};
	}
	
	/**
	 * @return the current position
	 */
	public int getPosition() {
		return _pos;
	}
	
	/**
	 * @return the number of matches
	 */
	public int getMatches() {
		return _matches;
	}
	
	/**
	 * @return wether the search should be restarted
	 */
	public boolean shouldRestart() {
		return _requiresRestart;
	}
	
	/**
	 * @return the lastMatch
	 */
	public TextPart getLastMatch() {
		return _lastMatch;
	}
}
