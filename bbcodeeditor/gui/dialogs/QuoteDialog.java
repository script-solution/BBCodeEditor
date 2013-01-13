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

package bbcodeeditor.gui.dialogs;

import java.awt.Component;
import java.awt.Frame;

import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;

/**
 * The dialog for the quote-button
 * 
 * @author hrniels
 */
public class QuoteDialog extends EditDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the title-id
	 */
	public static final int AUTHOR							= 0;
	
	/**
	 * constructor
	 * 
	 * @param comp the component to which the dialog-position should be relativ to
	 * @param parent the parent frame (to center the dialog)
	 * @param dialogTitle the title of this dialog
	 * @param author the author-name
	 */
	public QuoteDialog(Component comp,Frame parent,String dialogTitle,String author) {
		super(comp,parent,dialogTitle);

		addTextField(AUTHOR,LanguageContainer.getText(Language.GUI_DIALOG_QUOTE_AUTHOR_FIELD),
				author,false);
		
		initLayout();

		setVisible(true);
	}
}