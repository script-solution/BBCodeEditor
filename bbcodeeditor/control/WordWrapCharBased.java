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
 * the char-based wordwrap strategie<br>
 * This strategie determines the wrap-position from the number of characters
 * in a line
 * 
 * @author hrniels
 */
public class WordWrapCharBased implements IWordWrap {

	/**
	 * The position where to wrap
	 */
	private final int _wrapPosition;
	
	/**
	 * constructor
	 * 
	 * @param wrapPosition the position (in chars) where the word-wrap-strategie will wrap
	 */
	public WordWrapCharBased(int wrapPosition) {
		_wrapPosition = wrapPosition;
	}
	
	public int getBreakPosition(Line line) {
		return line.getLineLength() > _wrapPosition ? _wrapPosition : -1;
	}
	
	public int getCharsToMove(Line line,Line next) {
		return Math.min(next.getLineLength(),_wrapPosition - line.getLineLength());
	}
}