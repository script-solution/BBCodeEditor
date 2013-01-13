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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import bbcodeeditor.control.IPublicController;


/**
 * @author Assi Nilsmussen
 */
public class TextAreaKeyListener extends KeyAdapter {

	/**
	 * The controller
	 */
	protected final IPublicController _controller;

	/**
	 * constructor
	 * 
	 * @param textArea the textarea to which you want to connect the listener
	 */
	public TextAreaKeyListener(IPublicController textArea) {
		_controller = textArea;
	}

	public void keyPressed(KeyEvent e) {
		if(e.isConsumed())
			return;
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_A:
				if(e.isControlDown()) {
					_controller.selectCompleteText();
					e.consume();
				}
				break;
			
			case KeyEvent.VK_V:
				if(e.isControlDown()) {
					_controller.pasteTextAtCursor();
					e.consume();
				}
				break;
			
			case KeyEvent.VK_C:
				if(e.isControlDown()) {
					_controller.copySelectedText();
					e.consume();
				}
				break;

			case KeyEvent.VK_X:
				if(e.isControlDown()) {
					_controller.cutSelectedText();
					e.consume();
				}
				break;
			
			case KeyEvent.VK_LEFT:
				if(e.isControlDown())
					_controller.goToPreviousWord(e.isShiftDown());
				else
					_controller.back(e.isShiftDown());

				// consume event to prevent scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_RIGHT:
				if(e.isControlDown())
					_controller.goToNextWord(e.isShiftDown());
				else
					_controller.forward(e.isShiftDown());

				// consume event to prevent scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_UP:
				if(e.isControlDown())
					_controller.moveVisibleAreaLineUp();
				else
					_controller.lineUp(e.isShiftDown());

				// consume event to prevent focus changes and scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_DOWN:
				if(e.isControlDown())
					_controller.moveVisibleAreaLineDown();
				else
					_controller.lineDown(e.isShiftDown());
				
				// consume event to prevent focus changes and scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_BACK_SPACE:
				if(e.isControlDown())
					_controller.removePreviousWord();
				else
					_controller.removePreviousChar();
				
				e.consume();
				break;
			
			case KeyEvent.VK_DELETE:
				if(e.isControlDown())
					_controller.removeNextWord();
				else
					_controller.removeFollowingChar();
				
				e.consume();
				break;
			
			case KeyEvent.VK_END:
				if(e.isControlDown())
					_controller.goToVeryEnd(e.isShiftDown());
				else
					_controller.goToLineEnd(e.isShiftDown());

				// consume event to prevent scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_HOME:
				if(e.isControlDown())
					_controller.goToVeryBeginning(e.isShiftDown());
				else
					_controller.goToLineStart(e.isShiftDown());
				
				// consume event to prevent scrollpane-movements
				e.consume();
				break;
				
			case KeyEvent.VK_PAGE_UP:
				_controller.pageUp(e.isShiftDown());
				
				// consume event to prevent scrollpane-movements
				e.consume();
				break;
			
			case KeyEvent.VK_PAGE_DOWN:
				_controller.pageDown(e.isShiftDown());

				// consume event to prevent scrollpane-movements
				e.consume();
				break;
				
			case KeyEvent.VK_ENTER:
				if(e.isControlDown())
					_controller.addNewLineInList();
				else
					_controller.addNewLine();

				e.consume();
				break;
				
			case KeyEvent.VK_Z:
				if(e.isControlDown()) {
					_controller.undo();
					e.consume();
				}
				break;
			
			case KeyEvent.VK_Y:
				if(e.isControlDown()) {
					_controller.redo();
					e.consume();
				}
				break;
			
			case KeyEvent.VK_TAB:
				if(e.isShiftDown()) {
					_controller.unindentParagraphs();
					e.consume();
				}
				break;
		}
	}

	public void keyTyped(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if(!e.isControlDown() && isPrintableChar(keyChar)) {
			if(keyChar == '\t')
				_controller.indentParagraphs();
			else
				_controller.pasteTextAtCursor(String.valueOf(keyChar),false);
		}
	}
	
	/**
	 * determines if the given char is printable
	 * 
	 * @param c the character to test
	 * @return true if the character is printable
	 */
	private boolean isPrintableChar(char c) {
		// 0xFFFF = undefined, 0x8 = backspace, 0xA = enter, 0x7F = delete, 0x1B = Esc
		return c != 0xFFFF && c != 0x8 && c != 0xA && c != 0x7F && c != 0x1B;
	}
}