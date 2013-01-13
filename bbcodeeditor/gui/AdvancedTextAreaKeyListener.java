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

package bbcodeeditor.gui;

import java.awt.event.KeyEvent;

import bbcodeeditor.control.*;
import bbcodeeditor.control.events.TextAreaKeyListener;


/**
 * The textarea-key-listener we will use. This one supports more shortcuts
 * for the bbcode-tags
 * 
 * @author hrniels
 */
public class AdvancedTextAreaKeyListener extends TextAreaKeyListener {

	/**
	 * Constructor
	 * 
	 * @param textArea the textfield-instance
	 */
	public AdvancedTextAreaKeyListener(IPublicController textArea) {
		super(textArea);
	}

	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		
		if(e.isConsumed())
			return;
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_Q:
				if(e.isControlDown()) {
					_controller.addQuoteEnvironment(false);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_P:
				if(e.isControlDown()) {
					_controller.addCodeEnvironment(false);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_O:
				if(e.isControlDown()) {
					_controller.addListEnvironment(false);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_L:
				if(e.isControlDown()) {
					_controller.setLineAlignment(ParagraphAttributes.ALIGN_LEFT);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_E:
				if(e.isControlDown()) {
					_controller.setLineAlignment(ParagraphAttributes.ALIGN_CENTER);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_R:
				if(e.isControlDown()) {
					_controller.setLineAlignment(ParagraphAttributes.ALIGN_RIGHT);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_B:
				if(e.isControlDown()) {
					_controller.toggleAttribute(TextAttributes.BOLD);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_S:
				if(e.isControlDown()) {
					// are we in a sub-environment?
					Environment env = _controller.getCurrentEnvironment();
					if(env.getParentEnvironment() != null) {
						int pos = _controller.getCurrentCursorPos();
						int envLength = env.getElementLength();
						int end = env.getGlobalStartPos() + envLength;
						// just split if there is text to cut
						if(pos != end) {
							// we want to group the actions
							_controller.startHistoryCache();
							
							// at first we have to cut the text after the cursor-position
							String text = _controller.getText(pos,end);
							_controller.removeText(pos,end);
							
							// now walk one step forward so that we move out of the environment
							_controller.forward();
							
							// insert another env of the same type
							if(env instanceof QuoteEnvironment) {
								String author = ((QuoteEnvironment)env).getAuthor();
								_controller.addQuoteEnvironment(false,author);
							}
							else if(env instanceof CodeEnvironment)
								_controller.addCodeEnvironment(false);
							else {
								int listType = ((ListEnvironment)env).getListType();
								_controller.addListEnvironment(false,listType);
							}
							
							// paste the previously cutted text
							_controller.pasteTextAtCursor(text,true);
							
							// now go behind the split-position
							_controller.goToPosition(pos + 1);
							
							_controller.stopHistoryCache();
							e.consume();
						}
					}
				}
				break;
			
			case KeyEvent.VK_I:
				if(e.isControlDown()) {
					_controller.toggleAttribute(TextAttributes.ITALIC);
					e.consume();
				}
				break;
			
			case KeyEvent.VK_U:
				if(e.isControlDown()) {
					_controller.toggleAttribute(TextAttributes.UNDERLINE);
					e.consume();
				}
				break;
		}
	}
}