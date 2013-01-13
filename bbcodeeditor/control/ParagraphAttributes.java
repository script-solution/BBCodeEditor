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
 * All available attributes for paragraphs. At the moment this is just
 * the alignment.
 * 
 * @author hrniels
 */
public final class ParagraphAttributes {

	/**
	 * Represents the left-alignment
	 */
	public static final int ALIGN_LEFT				= 1;
	
	/**
	 * Represents the center-alignment
	 */
	public static final int ALIGN_CENTER			= 2;
	
	/**
	 * Represents the right-alignment
	 */
	public static final int ALIGN_RIGHT				= 3;
	
	/**
	 * Indicates that the alignment has not been defined.<br>
	 * This may be used to keep the alignment of the current line
	 */
	public static final int ALIGN_UNDEF				= 4;
	
	private ParagraphAttributes() {
		// prevent instantiation
	}
	
	/**
	 * determines the name for the given alignment
	 * 
	 * @param align the align
	 * @return the name of it
	 */
	public static String getAlignmentName(int align) {
		switch(align) {
			case ALIGN_LEFT:
				return "left";
			case ALIGN_RIGHT:
				return "right";
			case ALIGN_CENTER:
				return "center";
			default:
				return "undefined";
		}
	}
}