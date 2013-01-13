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

import bbcodeeditor.control.TextAttributes;


/**
 * a text with attributes
 * 
 * @author hrniels
 */
public final class AttributeText implements IText {

	/**
	 * the text to add
	 */
	private final String _text;
	
	/**
	 * the attributes to apply to the text
	 */
	private final TextAttributes _attr;
	
	/**
	 * the alignment
	 */
	private final int _align;
	
	/**
	 * constructor
	 * 
	 * @param text the parsed BBCode-text
	 * @param attr the attributes
	 * @param align the alignment
	 */
	public AttributeText(String text,TextAttributes attr,int align) {
		_text = text;
		_attr = attr;
		_align = align;
	}
	
	public String getText() {
		return _text;
	}
	
	/**
	 * @return the alignment
	 */
	public int getAlign() {
		return _align;
	}
	
	/**
	 * @return the attributes to use for this text
	 */
	public TextAttributes getAttributes() {
		return _attr;
	}
	
	public String toString() {
		return "AttributeText[" + _text + "]";
	}
}