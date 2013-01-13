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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.Line;




/**
 * @author Assi Nilsmussen
 */
public class TextAreaMouseListener extends MouseAdapter {
	
	private long _lastClick = 0;
	private long _lastLastClick = 0;
	private Line _lastLine = null;
	
	private AbstractTextField _textField;
	
	/**
	 * constructor
	 * 
	 * @param textField the AbstractTextField instance
	 */
	public TextAreaMouseListener(AbstractTextField textField) {
		_textField = textField;
	}

	public void mouseClicked(MouseEvent e) {
		// focus the control if not already done
		if(!_textField.hasFocus())
			_textField.requestFocus();
		
		super.mouseClicked(e);
		
		// move the cursor to the character at this position
		_textField.moveCursorToPos(e.getX(),e.getY(),e.isShiftDown(),false,
				e.getButton() == MouseEvent.BUTTON3);
		
		Line current = _textField.getCurrentLine();
		if(!current.equals(_lastLine)) {
			_lastClick = 0;
			_lastLastClick = 0;
		}
		
		if(System.currentTimeMillis() <= _lastLastClick + 400)
			_textField.selectCurrentLine();
		else if(System.currentTimeMillis() <= _lastClick + 200)
			_textField.selectWordAtCursor();
		
		_lastLastClick = _lastClick;
		_lastClick = System.currentTimeMillis();
		_lastLine = current;
	}
}