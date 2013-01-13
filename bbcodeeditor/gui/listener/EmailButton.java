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

import javax.swing.ImageIcon;
import javax.swing.JButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.IPublicController;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.Settings;
import bbcodeeditor.gui.dialogs.EmailDialog;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;



/**
 * The email-button for the button-bar
 * 
 * @author hrniels
 */
public class EmailButton extends JButton implements ActionListener {

	private static final long serialVersionUID = -5876429921997911097L;

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 */
	public EmailButton(AbstractTextField textArea) {
		super(new ImageIcon(Helper.getFileInDocumentBase("./images/mail.png")));
		
		_textArea = textArea;
		
		addActionListener(this);
		setToolTipText(LanguageContainer.getText(Language.GUI_BTN_EMAIL_TOOLTIP));
	}
	
	public void actionPerformed(ActionEvent evt) {
		String dialogTitle;
		String address = "";
		String title = null;
		boolean add = true;
		if(_textArea.getSelection().isInSelectionMode()) {
			add = false;
			TextAttributes attributes = _textArea.getAttributes();
			Object val = attributes.get(TextAttributes.EMAIL);
			if(val != null)
				address = (String)val;
			title = _textArea.getSelectedText(IPublicController.SYNTAX_PLAIN);
			dialogTitle = LanguageContainer.getText(Language.GUI_DIALOG_EMAIL_EDIT_TITLE);
		}
		else
			dialogTitle = LanguageContainer.getText(Language.GUI_DIALOG_EMAIL_INSERT_TITLE);
		
		EmailDialog frm = new EmailDialog(Settings.DIALOG_COMPONENT,null,dialogTitle,address,title);
		if(frm.okClicked()) {
			String newAddress = (String)frm.getValueOf(EmailDialog.EMAIL_ADDRESS);
			String newTitle = (String)frm.getValueOf(EmailDialog.EMAIL_TITLE);
			if(newTitle.length() == 0)
				newTitle = newAddress;
			
			if(add) {
				_textArea.insertLink(true,newTitle,newAddress);
				_textArea.setTemporaryAttribute(TextAttributes.EMAIL,null);
			}
			else
				_textArea.setAttribute(TextAttributes.EMAIL,newAddress);
		}
		
		_textArea.requestFocus();
	}
}