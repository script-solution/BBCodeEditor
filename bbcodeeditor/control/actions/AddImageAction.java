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
 * @author Assi Nilsmussen
 *
 */
public final class AddImageAction extends HistoryAction {

	/**
	 * the image to add
	 */
	private final SecImage _image;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 * @param image the image
	 */
	public AddImageAction(Controller con,int start,int end,SecImage image) {
		super(con,start,end);
		
		_image = image;
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				_con.removeText(_start,_end,false);
				_actionType = REDO;
				break;
				
			case REDO:
				_con.addImage(_image,_start);
				_con.goToPosition(_start + 1);
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		String imgName;
		if(_image instanceof SecSmiley)
			imgName = "smiley '" + ((SecSmiley)_image).getPrimaryCode() + "'";
		else
			imgName = "image";
		
		String res;
		if(_actionType == UNDO)
			res = "Remove " + imgName;
		else
			res = "Add " + imgName;
		
		return res + " [" + _start + "," + _end + "]";
	}
	
	public String toString() {
		return getName();
	}
}