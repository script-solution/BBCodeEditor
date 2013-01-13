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

package bbcodeeditor.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JToggleButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.TextAttributes;


/**
 * The position-button for the button-bar<br>
 * Will be used for superscript and subscript
 * 
 * @author hrniels
 */
public class PositionButton extends JToggleButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * the value to use
	 */
	private final Byte _value;

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 * @param icon the icon for the button
	 * @param toolTip the tooltip-text
	 * @param value the value to use
	 */
	public PositionButton(AbstractTextField textArea,Icon icon,String toolTip,Byte value) {
		super(icon);
		
		_textArea = textArea;
		_value = value;
		
		setToolTipText(toolTip);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		TextAttributes attributes = _textArea.getAttributesAtCursor();
		Object val = attributes.get(TextAttributes.POSITION);
		if(val == null || (val instanceof Byte && !val.equals(_value)))
			_textArea.setAttribute(TextAttributes.POSITION,_value);
		else
			_textArea.setAttribute(TextAttributes.POSITION,null);
		
		_textArea.requestFocus();
	}
}