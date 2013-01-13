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
 * The interface for a BBCodeTag
 * 
 * @author hrniels
 */
public interface BBCodeTag {
	
	/**
	 * the none-value-type
	 */
	public static final int VALUE_TYPE_NONE = -1;
	
	/**
	 * the string-value-type
	 */
	public static final int VALUE_TYPE_STRING = 0;
	
	/**
	 * the color-value-type
	 */
	public static final int VALUE_TYPE_COLOR = 1;
	
	/**
	 * the integer-value-type
	 */
	public static final int VALUE_TYPE_INTEGER = 2;
	
	/**
	 * @return true if this tag is an attribute (b,i,u,font, ... are attributes. code, quote, ... not)
	 */
	public boolean isAttribute();

	/**
	 * @return true if this is a closing tag
	 */
	public boolean isClosingtag();

	/**
	 * @return true if this tag is a simple tag like b,u,i,...
	 */
	public boolean isSimpleTag();

	/**
	 * @return the attribute-id if this object is an attribute, otherwise -1
	 */
	public Integer getAttribute();

	/**
	 * @return the name of the tag
	 */
	public String getName();
	
	/**
	 * @return the tag in BBCode-syntax
	 */
	public String getBBCodeTag();

	/**
	 * @return the value of the tag, if existing
	 */
	public Object getValue();
	
	/**
	 * sets the value to given object
	 * 
	 * @param value the new value
	 */
	public void setValue(Object value);
	
	/**
	 * should return the tag in BBCode-syntax
	 * (to treat as plain-text if necessary)
	 * 
	 * @return the tag in BBCode-syntax
	 */
	public String toString();
}