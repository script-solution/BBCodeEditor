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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.gui.Helper;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * An extra-button
 * 
 * @author hrniels
 */
public class ExtraTagButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * the textField instance
	 */
	private final ExtraTagItemSelectedListener _listener;
	
	/**
	 * The extra-tag
	 */
	private final ExtraTag _tag;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 * @param tag the extra-tag
	 * @param image the image-icon for the button
	 * @param tooltip the language-entry for the tooltip
	 */
	public ExtraTagButton(AbstractTextField textArea,ExtraTag tag,String image,String tooltip) {
		super();
		
		setIcon(Helper.getLimitedImageIcon(this,Helper.getFileInDocumentBase(image)));
	
		_listener = new ExtraTagItemSelectedListener(textArea);
		_tag = tag;
		
		setToolTipText(LanguageContainer.getText(tooltip));
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		_listener.valueSelected(1,1,new CellContent(_tag,new Integer(1)));
	}
}