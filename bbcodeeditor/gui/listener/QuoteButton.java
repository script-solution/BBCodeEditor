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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.Settings;
import bbcodeeditor.gui.combobox.ButtonComboBox;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.combobox.ItemSelectedListener;
import bbcodeeditor.gui.dialogs.QuoteDialog;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;



/**
 * The quote-button for the button-bar
 * 
 * @author hrniels
 */
public class QuoteButton extends ButtonComboBox implements ActionListener,ItemSelectedListener {
	
	private static final long serialVersionUID = 6298808480962408119L;
	
	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 * @param prefSize the preferred size of the button
	 */
	public QuoteButton(AbstractTextField textArea,Dimension prefSize) {
		super(new CellContent[] {
				new CellContent(new Boolean(false),
						LanguageContainer.getText(Language.GUI_BTN_QUOTE_LIST_DEFAULT)),
				new CellContent(new Boolean(true),
						LanguageContainer.getText(Language.GUI_BTN_QUOTE_LIST_USER_DEFINED)),
		});
		
		_textArea = textArea;
		
		JButton listBtn = new JButton(new ImageIcon(
				Helper.getFileInDocumentBase("./images/quote.png")));
		listBtn.setPreferredSize(prefSize);
		listBtn.addActionListener(this);
		setDefaultButton(listBtn,false);
    
    setToolTipText(LanguageContainer.getText(Language.GUI_BTN_QUOTE_TOOLTIP));
    
    addItemSelectedListener(this);
	}
	
	public void valueSelected(int row,int col,CellContent val) {
		if(((Boolean)val.getKey()).booleanValue()) {
			QuoteDialog qd = new QuoteDialog(Settings.DIALOG_COMPONENT,null,
					LanguageContainer.getText(Language.GUI_DIALOG_QUOTE_INSERT_TITLE),"");
			if(qd.okClicked()) {
				String author = (String)qd.getValueOf(QuoteDialog.AUTHOR);
				if(author.length() > 0)
					_textArea.addQuoteEnvironment(false,author);
				else
					_textArea.addQuoteEnvironment(false);
				_textArea.requestFocus();
			}
		}
		else
			actionPerformed(null);
	}

	public void actionPerformed(ActionEvent evt) {
		_textArea.addQuoteEnvironment(false);
		_textArea.requestFocus();
	}
}