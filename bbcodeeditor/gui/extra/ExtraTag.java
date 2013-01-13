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

package bbcodeeditor.gui.extra;


/**
 * Represents an extra tag
 * 
 * @author hrniels
 */
public final class ExtraTag {

	/**
	 * the tag-name
	 */
	private String _tagName;
	
	/**
	 * the name of the combobox-entry (lang-entry)
	 */
	private String _comboName = "";
	
	/**
	 * the title of the dialog (lang-entry)
	 */
	private String _dialogTitle = "";
	
	/**
	 * the title of the parameter (lang-entry)
	 */
	private String _parameterTitle = "";
	
	/**
	 * Does the tag have a parameter?
	 */
	private boolean _hasParameter = false;
	
	/**
	 * The description of this tag (lang-entry)
	 */
	private String _description = "";
	
	/**
	 * constructor
	 * 
	 * @param tagName the name of the tag
	 */
	public ExtraTag(String tagName) {
		_tagName = tagName;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof ExtraTag))
			return false;
		
		ExtraTag e = (ExtraTag)o;
		return e.getTagName() == _tagName;
	}
	
	public int hashCode() {
		return _tagName.hashCode();
	}
	
	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return _tagName;
	}
	
	/**
	 * @return the comboName
	 */
	public String getComboName() {
		return _comboName;
	}
	
	/**
	 * @param comboName the comboName to set
	 */
	public void setComboName(String comboName) {
		_comboName = comboName;
	}
	
	/**
	 * @return the dialogTitle
	 */
	public String getDialogTitle() {
		return _dialogTitle;
	}
	
	/**
	 * @param dialogTitle the dialogTitle to set
	 */
	public void setDialogTitle(String dialogTitle) {
		_dialogTitle = dialogTitle;
	}
	
	/**
	 * @return the parameterTitle
	 */
	public String getParameterTitle() {
		return _parameterTitle;
	}
	
	/**
	 * @param parameterTitle the parameterTitle to set
	 */
	public void setParameterTitle(String parameterTitle) {
		_parameterTitle = parameterTitle;
	}
	
	/**
	 * @return wether this tag has a parameter
	 */
	public boolean hasParameter() {
		return _hasParameter;
	}
	
	/**
	 * Sets wether this tag has a parameter
	 * 
	 * @param parameter the new value
	 */
	public void setHasParameter(boolean parameter) {
		_hasParameter = parameter;
	}
	
	/**
	 * @return the description of this extra-tag
	 */
	public String getDescription() {
		return _description;
	}
	
	/**
	 * Sets the description of this extra-tag
	 * 
	 * @param desc the new value
	 */
	public void setDescription(String desc) {
		_description = desc;
	}
}