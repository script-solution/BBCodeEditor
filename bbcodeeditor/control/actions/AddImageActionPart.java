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

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.SecImage;
import bbcodeeditor.control.SecSmiley;


/**
 * An action-part which adds an image with an alignment at a position
 * 
 * @author hrniels
 */
public class AddImageActionPart extends HistoryActionPart {
	
	/**
	 * the image to add
	 */
	private final SecImage _image;

	/**
	 * constructor
	 * 
	 * @param start the global position
	 * @param image the image to add
	 */
	public AddImageActionPart(int start,SecImage image) {
		super(start);
		
		_image = image;
	}
	
	public void execute(Controller con) {
		con.addImage(_image,_start);
		con.goToPosition(_start + 1);
	}
	
	public String getName() {
		if(_image instanceof SecSmiley)
			return "Add '" + ((SecSmiley)_image).getPrimaryCode() + "' [" + _start + "]";
		
		return "Add '" + _image.getImageURL() + "'" + " [" + _start + "]";
	}
	
	public String getText(Controller con) {
		if(_image instanceof SecSmiley)
			return ((SecSmiley)_image).getPrimaryCode();
		
		return "[image]";
	}
	
	public String toString() {
		return getName();
	}
}