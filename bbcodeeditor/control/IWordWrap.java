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
 * the interface for the wordwrap-strategies.<br>
 * We want to allow different strategies:
 * <ul>
 * 	<li>character-based</li>
 *	<li>word-based</li>
 * 	<li>pixel- and word-based</li>
 * 	<li>no-wrap (never wraps a line; for code-environments)</li>
 * </ul>
 * 
 * @author hrniels
 */
public interface IWordWrap {

	/**
	 * determines the break-position in the given line<br>
	 * A negative result indicates that no wrap is necessary
	 * 
	 * @param line the line to break
	 * @return the position in the line where to wrap (or -1 if not necessary)
	 */
	int getBreakPosition(Line line);
	
	/**
	 * determines the number of chars from <code>next</code> which should be moved
	 * to <code>line</code>.
	 * 
	 * @param line the line where to add chars
	 * @param next the next line where to remove chars
	 * @return int the number of chars which can be moved
	 */
	int getCharsToMove(Line line,Line next);
}