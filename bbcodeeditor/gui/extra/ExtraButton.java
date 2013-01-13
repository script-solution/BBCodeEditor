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

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a button for extra-tags
 * 
 * @author hrniels
 */
public class ExtraButton {

	/**
	 * the list with the items of the button
	 */
	private final List _items = new ArrayList();
	
	/**
	 * the name of the button
	 */
	private final String _name;
	
	/**
	 * The total number of items including the deactivated
	 */
	private int _realItemCount = 0;
	
	/**
	 * the tooltip of the button
	 */
	private String _tooltip;
	
	/**
	 * the image of the button
	 */
	private String _image;
	
	/**
	 * Constructor
	 * 
	 * @param name the name of the button
	 */
	public ExtraButton(String name) {
		_name = name;
	}
	
	/**
	 * @return the list with all items
	 */
	public List getItems() {
		return _items;
	}
	
	/**
	 * @return the real number of items including the deactivated ones
	 */
	public int getRealItemCount() {
		return _realItemCount;
	}
	
	/**
	 * adds the given item to this button
	 * 
	 * @param item the item to add
	 */
	public void addItem(ExtraTag item) {
		_items.add(item);
		_realItemCount++;
	}
	
	/**
	 * Increases the item-count without adding an item
	 */
	public void increaseItemCount() {
		_realItemCount++;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * @return the image
	 */
	public String getImage() {
		return _image;
	}
	
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		_image = image;
	}
	
	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return _tooltip;
	}
	
	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		_tooltip = tooltip;
	}
}