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


/**
 * The Button for all toggle-attributes (bold,italic,underline)
 * 
 * @author hrniels
 */
public class ToggleAttrButton extends JToggleButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * the attribute to toggle
	 */
	private final Integer _attribute;

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
	 * @param attribute the attribute to toggle
	 */
	public ToggleAttrButton(AbstractTextField textArea,Icon icon,String toolTip,
			Integer attribute) {
		super(icon);
		
		_textArea = textArea;
		_attribute = attribute;
		
		setToolTipText(toolTip);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		_textArea.toggleAttribute(_attribute);
		_textArea.requestFocus();
	}
}