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
 * @author Assi Nilsmussen
 *
 */
public class URLDialog extends EditDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the title-id
	 */
	public static final int URL_TITLE		= 0;
	
	/**
	 * the address-id
	 */
	public static final int URL_ADDRESS	= 1;
	
	/**
	 * constructor for an insert-dialog
	 * 
	 * @param comp the component to which the dialog-position should be relativ to
	 * @param parent the parent frame (to center the dialog)
	 * @param dialogTitle the title of this dialog
	 * @param address the link-address
	 * @param title the link-title
	 */
	public URLDialog(Component comp,Frame parent,String dialogTitle,String address,String title) {
		super(comp,parent,dialogTitle);

		addTextField(URL_TITLE,LanguageContainer.getText(Language.GUI_DIALOG_LINK_TITLE_FIELD),
				title,title != null);
		addTextField(URL_ADDRESS,LanguageContainer.getText(Language.GUI_DIALOG_LINK_LINK_FIELD),
				address,false);
		
		addValidation(URL_ADDRESS,new InputValidation() {
			public String validate(Object value) {
				if(String.valueOf(value).length() == 0)
					return LanguageContainer.getText(Language.ERROR_MISSING_LINK_ADDRESS);
				
				return "";
			}
		});
		
		initLayout();

		setVisible(true);
	}
}