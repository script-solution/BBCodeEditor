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

import java.awt.Color;

import bbcodeeditor.control.export.ColorFunctions;



/**
 * An extended-bbcode-tag. That means that it receives (or may receive) a parameter
 * 
 * @author hrniels
 */
public class ExtendedTag implements BBCodeTag {
	
	/**
	 * The attribute
	 */
	protected Integer _attribute = null;
	
	/**
	 * Is it a closing tag?
	 */
	protected boolean _isClosingTag;
	
	/**
	 * The tag name
	 */
	protected String _name;
	
	/**
	 * The value of the tag (parameter-value)
	 */
	protected Object _value;
	
	/**
	 * The value-type of the parameter:
	 * BBCodeTag.VALUE_TYPE_STRING, BBCodeTag.VALUE_TYPE_COLOR, BBCodeTag.VALUE_TYPE_INTEGER
	 */
	protected int _valueType;
	
	/**
	 * constructor
	 * 
	 * @param attribute the attribute-id. specify null if there is no attribute
	 * @param valueType the type of value (BBCodeTag.VALUE_TYPE_STRING,
	 * 									BBCodeTag.VALUE_TYPE_COLOR, BBCodeTag.VALUE_TYPE_INTEGER)
	 * @param name the name of this tag
	 * @param value the value of this tag
	 * @param isClosingTag set this to true if this tag is a closing tag
	 */
	public ExtendedTag(Integer attribute,int valueType,String name,String value,boolean isClosingTag) {
		_name = name;
		_valueType = valueType;
		
		if(attribute != null)
			_attribute = attribute;
		
		_value = value;
		
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
		
		StringBuffer tag = new StringBuffer();
		tag.append("[");
		tag.append(_name);
		
		switch(_valueType) {
			case BBCodeTag.VALUE_TYPE_INTEGER:
			case BBCodeTag.VALUE_TYPE_STRING:
				tag.append("=" + _value);
				break;
			
			case BBCodeTag.VALUE_TYPE_COLOR:
				tag.append("=" + ColorFunctions.getStringFromColor((Color)_value));
				break;
		}
		
		tag.append("]");

		return tag.toString();
	}

	public Object getValue() {
		return _value;
	}
	
	public void setValue(Object value) {
		this._value = value;
	}

	public boolean isClosingtag() {
		return _isClosingTag;
	}

	public boolean isSimpleTag() {
		return false;
	}
	
	public String toString() {
		if(_isClosingTag)
			return "[/" + _name + "]";
		
		
		return "[" + _name + "=" + _value + "]";
	}
}