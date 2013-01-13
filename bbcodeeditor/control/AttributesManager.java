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
 * The manager for the attributes. Stores wether something has changed
 * and fires the change-event, if necessary
 * 
 * @author hrniels
 */
public class AttributesManager {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;

	/**
	 * Stores wether the attributes have changed
	 */
	private boolean _attrChanged = false;
	
	/**
	 * Constructor
	 * 
	 * @param tf the textfield
	 */
	public AttributesManager(AbstractTextField tf) {
		_textField = tf;
	}
	
	/**
	 * Marks that something has changed
	 */
	public void markChanged() {
		_attrChanged = true;
	}
	
	/**
	 * Checks wether something has changed and if so the listeners will be
	 * notified
	 */
	public void checkChanged() {
		if(_attrChanged)
			_textField.invokeAttributesChangedListeners();
		
		_attrChanged = false;
	}
}