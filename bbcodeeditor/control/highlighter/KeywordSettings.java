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

package bbcodeeditor.control.highlighter;

/**
 * Settings for keywords
 * 
 * @author hrniels
 */
public class KeywordSettings {
	
	/**
	 * case-sensitive?
	 */
	private final boolean _caseSensitive;
	
	/**
	 * require words?
	 */
	private final boolean _requireWord;
	
	/**
	 * Constructor
	 * 
	 * @param cs case-sensitive?
	 * @param reqWord require words?
	 */
	public KeywordSettings(boolean cs,boolean reqWord) {
		_caseSensitive = cs;
		_requireWord = reqWord;
	}
	
	/**
	 * @return wether the keywords should be matched case-sensitivly
	 */
	public boolean isCaseSensitive() {
		return _caseSensitive;
	}
	
	/**
	 * @return wether the keywords must be complete words
	 */
	public boolean requireWord() {
		return _requireWord;
	}
}