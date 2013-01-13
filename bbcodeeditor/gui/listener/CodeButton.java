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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.highlighter.CustomHighlighters;
import bbcodeeditor.control.highlighter.HighlighterEntry;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.combobox.ButtonComboBox;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.combobox.ItemSelectedListener;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The code-button for the button-bar
 * 
 * @author hrniels
 */
public class CodeButton extends ButtonComboBox implements ActionListener,ItemSelectedListener {
	
	private static final long serialVersionUID = -1794654947368910864L;
	
	/**
	 * Builds the CellContent-array for the combobox
	 * 
	 * @return the array
	 */
	private static CellContent[] getCells() {
		Vector l = new Vector();
		List list = CustomHighlighters.getHighlighter();
		Iterator it = list.iterator();
		l.add(new CellContent(null,LanguageContainer.getText(Language.GUI_BTN_CODE_NO_HL)));
		while(it.hasNext()) {
			HighlighterEntry e = (HighlighterEntry)it.next();
			l.add(new CellContent(e.getId(),e.getName()));
		}
		return (CellContent[])l.toArray(new CellContent[0]);
	}
	
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
	public CodeButton(AbstractTextField textArea,Dimension prefSize) {
		super(getCells());
		
		_textArea = textArea;
		
		JButton listBtn = new JButton(new ImageIcon(
				Helper.getFileInDocumentBase("./images/code.png")));
		listBtn.setPreferredSize(prefSize);
		listBtn.addActionListener(this);
		setDefaultButton(listBtn,false);
    
    setToolTipText(LanguageContainer.getText(Language.GUI_BTN_CODE_TOOLTIP));
    
    addItemSelectedListener(this);
	}
	
	public void actionPerformed(ActionEvent evt) {
		_textArea.addCodeEnvironment(false,null);
		_textArea.requestFocus();
	}

	public void valueSelected(int row,int col,CellContent val) {
		_textArea.addCodeEnvironment(false,val.getKey());
		_textArea.requestFocus();
	}
}