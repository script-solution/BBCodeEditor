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

package bbcodeeditor.control.export.bbcode.blocks;

import bbcodeeditor.control.SecSmiley;


/**
 * A content which contains a smiley-code.
 * 
 * @author hrniels
 */
public class SmileyContent extends Content {

	/**
	 * The detected smiley-code
	 */
	private final String _smileyCode;
	
	/**
	 * constructor
	 * 
	 * @param smileyCode the detected smiley-code
	 * @param smiley the SecSmiley-instance
	 */
	public SmileyContent(String smileyCode,SecSmiley smiley) {
		_smileyCode = smileyCode;
		_value = smiley;
	}
	
	/**
	 * @return the detected smiley-code
	 */
	public String getSmileyCode() {
		return _smileyCode;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public String toString() {
		return "{" + ((SecSmiley)_value).getPrimaryCode() + "}";
	}
}