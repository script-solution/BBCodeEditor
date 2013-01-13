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
import bbcodeeditor.control.SecImage;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.Settings;
import bbcodeeditor.gui.dialogs.ImageDialog;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;



/**
 * The image-button for the button-bar
 * 
 * @author hrniels
 */
public class ImageButton extends JButton implements ActionListener {

	private static final long serialVersionUID = -5121133153306184772L;

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 */
	public ImageButton(AbstractTextField textArea) {
		super(new ImageIcon(Helper.getFileInDocumentBase("./images/image.png")));
		
		_textArea = textArea;
		
		addActionListener(this);
		setToolTipText(LanguageContainer.getText(Language.GUI_BTN_IMAGE_TOOLTIP));
	}
	
	public void actionPerformed(ActionEvent evt) {
		ImageDialog frm = new ImageDialog(Settings.DIALOG_COMPONENT,null,
				LanguageContainer.getText(Language.GUI_DIALOG_IMAGE_TITLE),"");
		if(frm.okClicked())
			_textArea.addImage(new SecImage(_textArea,(String)frm.getValueOf(ImageDialog.IMAGE_ADDRESS)));
		
		_textArea.requestFocus();
	}
}