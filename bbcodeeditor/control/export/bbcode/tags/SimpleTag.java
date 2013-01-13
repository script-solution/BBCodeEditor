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

package bbcodeeditor.control.export.bbcode.tags;


/**
 * A simple-bbcode-tag. That means that this tag receives no parameter
 * 
 * @author hrniels
 */
public class SimpleTag implements BBCodeTag {
	
	/**
	 * The attribute
	 */
	private Integer _attribute;
	
	/**
	 * Is it a closing tag?
	 */
	private boolean _isClosingTag;
	
	/**
	 * The tag name
	 */
	private String _name;
	
	/**
	 * constructor
	 * 
	 * @param attribute the attribute-id
	 * @param name the name of this tag
	 * @param isClosingTag set this to true if this tag is a closing tag
	 */
	public SimpleTag(Integer attribute,String name,boolean isClosingTag) {
		_attribute = attribute;
		_name = name;
		_isClosingTag = isClosingTag;
	}
	
	public boolean isAttribute() {
		return true;
	}
	
	public Integer getAttribute() {
		return _attribute;
	}

	public String getName() {
		return _name.toLowerCase();
	}
	
	public String getBBCodeTag() {
		if(_isClosingTag)
			return "[/" + _name + "]";
		
		return "[" + _name + "]";
	}

	public Object getValue() {
		return new Boolean(true);
	}
	
	public void setValue(Object value) {
		// do nothing
	}

	public boolean isClosingtag() {
		return _isClosingTag;
	}

	public boolean isSimpleTag() {
		return true;
	}
	
	public String toString() {
		return "[" + (_isClosingTag ? "/" : "") + _name + "]";
	}
}