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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.gui.ColorButton;
import bbcodeeditor.gui.Settings;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The background-color-button for the button-bar
 * 
 * @author hrniels
 */
public class BGColorButton extends ColorButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 */
	public BGColorButton(AbstractTextField textArea) {
		super("B",Color.WHITE);
		
		_textArea = textArea;
		
		setToolTipText(LanguageContainer.getText(Language.GUI_BTN_BGCOLOR_TOOLTIP));
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Color newColor = JColorChooser.showDialog(Settings.DIALOG_COMPONENT,
				LanguageContainer.getText(Language.GUI_DIALOG_COLOR_TITLE),getColor());
		if(newColor != null) {
			setColor(newColor);
			_textArea.setAttribute(TextAttributes.BG_COLOR,newColor);
		}
		
		_textArea.requestFocus();
	}
}