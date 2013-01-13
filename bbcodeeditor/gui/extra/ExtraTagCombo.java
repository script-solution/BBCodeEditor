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

package bbcodeeditor.gui.extra;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.combobox.ButtonComboBox;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The combobox for extra-tags
 * 
 * @author hrniels
 */
public class ExtraTagCombo extends ButtonComboBox implements ActionListener {
	
	private static final long serialVersionUID = 8822349461952533962L;

	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 * @param prefSize the preferred size of the button
	 * @param extraTags an array of CellContent-objects with the extra-tags
	 * @param image the image-icon for the button
	 * @param tooltip the language-entry for the tooltip
	 */
	public ExtraTagCombo(AbstractTextField textArea,Dimension prefSize,CellContent[] extraTags,
			String image,String tooltip) {
		super(extraTags);
		
		JButton listBtn = new JButton();
		listBtn.setIcon(Helper.getLimitedImageIcon(this,Helper.getFileInDocumentBase(image)));
		listBtn.setPreferredSize(prefSize);
		listBtn.addActionListener(this);
		setDefaultButton(listBtn,false);
    
    setToolTipText(LanguageContainer.getText(tooltip));
    
    addItemSelectedListener(new ExtraTagItemSelectedListener(textArea));
	}

	public void actionPerformed(ActionEvent evt) {
		showPopup();
	}
}