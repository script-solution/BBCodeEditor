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

package bbcodeeditor.control.events;

import bbcodeeditor.control.ContentSection;


/**
 * @author Assi Nilsmussen
 *
 */
public class CaretPositionChangedEvent {

	private int _oldPosition;
	private ContentSection _oldSection;
	
	private int _newPosition;
	private ContentSection _newSection;
	
	private boolean _contentChanged;
	
	/**
	 * constructor
	 * 
	 * @param oldPosition the old position of the cursor
	 * @param oldSection the old section of the cursor
	 * @param newPosition the new position of the cursor
	 * @param newSection the new section of the cursor
	 * @param contentChanged has the content been changed?
	 */
	public CaretPositionChangedEvent(int oldPosition,ContentSection oldSection,
			int newPosition,ContentSection newSection,boolean contentChanged) {
		this._oldPosition = oldPosition;
		this._oldSection = oldSection;
		this._newPosition = newPosition;
		this._newSection = newSection;
		this._contentChanged = contentChanged;
	}
	
	/**
	 * @return true if the content has been changed
	 */
	public boolean hasContentChanged() {
		return this._contentChanged;
	}
	
	/**
	 * @return the old position of the cursor
	 */
	public int getOldPosition() {
		return this._oldPosition;
	}
	
	/**
	 * @return the old section of the cursor
	 */
	public ContentSection getOldSection() {
		return this._oldSection;
	}
	
	/**
	 * @return the new position of the cursor
	 */
	public int getNewPosition() {
		return this._newPosition;
	}
	
	/**
	 * @return the new section of the cursor
	 */
	public ContentSection getNewSection() {
		return this._newSection;
	}
}