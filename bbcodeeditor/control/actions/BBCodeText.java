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


/**
 * @author Assi Nilsmussen
 *
 */
public final class BBCodeText implements IText {

	/**
	 * the text
	 */
	private final String _bbcodeText;
	
	/**
	 * constructor
	 * 
	 * @param text the parsed BBCode-text
	 */
	public BBCodeText(String text) {
		_bbcodeText = text;
	}
	
	public String getText() {
		return _bbcodeText;
	}
	
	public String toString() {
		return "BBCode[" + _bbcodeText + "]";
	}
}