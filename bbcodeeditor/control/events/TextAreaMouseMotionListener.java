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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import bbcodeeditor.control.AbstractTextField;



/**
 * @author Assi Nilsmussen
 */
public class TextAreaMouseMotionListener extends MouseMotionAdapter {

	private AbstractTextField _textArea;
	
	private boolean _hasInterupted = false;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea of this listener
	 */
	public TextAreaMouseMotionListener(AbstractTextField textArea) {
		_textArea = textArea;
	}
	
	public void mouseMoved(MouseEvent e) {
		_hasInterupted = true;
	}
	
	public void mouseDragged(MouseEvent e) {
		// focus the control if not already done
		if(!_textArea.hasFocus())
			_textArea.requestFocus();
		
		// go to the position
		_textArea.moveCursorToPos(e.getX(),e.getY(),true,_hasInterupted,false);
		
		_hasInterupted = false;
	}
}