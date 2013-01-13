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

package bbcodeeditor.control.tools;

/**
 * @author Assi Nilsmussen
 */
public class TextPart {
	
	/**
	 * the text of this TextPart
	 */
	public String text;
	
	/**
	 * the start-position
	 */
	public int startPos;
	
	/**
	 * the end-position
	 */
	public int endPos;
	
	/**
	 * constructor
	 * 
	 * @param text the text of the part
	 * @param startPos the start-position
	 * @param endPos the end-position
	 */
	public TextPart(String text,int startPos,int endPos) {
		this.text = text;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	/**
	 * @return debugging information
	 */
	public String toString() {
		return "[S: " + this.startPos + ",E: " + this.endPos + ",T: '" + this.text + "']";
	}
}