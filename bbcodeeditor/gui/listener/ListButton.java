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
import bbcodeeditor.control.ListTypes;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.combobox.ButtonComboBox;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.combobox.ItemSelectedListener;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The list-button for the button-bar
 * 
 * @author hrniels
 */
public class ListButton extends ButtonComboBox implements ActionListener,ItemSelectedListener {
	
	private static final long serialVersionUID = 8822349461952533962L;
	
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
	public ListButton(AbstractTextField textArea,Dimension prefSize) {
		super(new CellContent[] {
				new CellContent(new Integer(ListTypes.TYPE_DEFAULT),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_DEFAULT)),
				new CellContent(new Integer(ListTypes.TYPE_NUM),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_NUMERIC)),
				new CellContent(new Integer(ListTypes.TYPE_CIRCLE),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_CIRCLE)),
				new CellContent(new Integer(ListTypes.TYPE_DISC),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_DISC)),
				new CellContent(new Integer(ListTypes.TYPE_SQUARE),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_SQUARE)),
				new CellContent(new Integer(ListTypes.TYPE_ALPHA_S),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_ALPHA_S)),
				new CellContent(new Integer(ListTypes.TYPE_ALPHA_B),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_ALPHA_B)),
				new CellContent(new Integer(ListTypes.TYPE_ROMAN_S),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_ROMAN_S)),
				new CellContent(new Integer(ListTypes.TYPE_ROMAN_B),
						LanguageContainer.getText(Language.GUI_BTN_LIST_LIST_ROMAN_B))
		});
		
		_textArea = textArea;
		
		JButton listBtn = new JButton(new ImageIcon(Helper.getFileInDocumentBase("./images/list.png")));
		listBtn.setPreferredSize(prefSize);
		listBtn.addActionListener(this);
		setDefaultButton(listBtn,false);
    
    setToolTipText(LanguageContainer.getText(Language.GUI_BTN_LIST_TOOLTIP));
    
    addItemSelectedListener(this);
	}
	
	public void valueSelected(int row,int col,CellContent val) {
		_textArea.addListEnvironment(false,((Integer)val.getKey()).intValue());
		_textArea.requestFocus();
	}

	public void actionPerformed(ActionEvent evt) {
		_textArea.addListEnvironment(false);
		_textArea.requestFocus();
	}
}