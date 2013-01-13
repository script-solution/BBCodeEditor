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

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.IPublicController;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.combobox.ItemSelectedListener;
import bbcodeeditor.gui.dialogs.EditDialog;


/**
 * The Item-selected-listener for the extra-tags
 * 
 * @author hrniels
 */
public class ExtraTagItemSelectedListener implements ItemSelectedListener {

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * Constructor
	 * 
	 * @param textarea the textarea-instance
	 */
	public ExtraTagItemSelectedListener(AbstractTextField textarea) {
		_textArea = textarea;
	}
	
	public void valueSelected(int row,int col,CellContent val) {
		ExtraTag tag = (ExtraTag)val.getKey();
		String tagName = tag.getTagName();
		
		// is there any text selected?
		String text = _textArea.getSelectedText(IPublicController.SYNTAX_PLAIN);
		if(text == null)
			text = "";
		
		// open dialog
		EditDialog dlg = new ExtraEditDialog(tag,0,1,text);
		if(dlg.okClicked()) {
			String value = (String)dlg.getValueOf(0);
			
			if(value.length() > 0) {
				// paste the text
				String paste;
				if(tag.hasParameter()) {
					String title = (String)dlg.getValueOf(1);
					paste = "[" + tagName + "=" + value + "]" + title + "[/" + tagName + "]";
				}
				else
					paste = "[" + tagName + "]" + value + "[/" + tagName + "]";
				_textArea.pasteTextAtCursor(paste,false);
			}
		}
		
		_textArea.requestFocus();
	}
}