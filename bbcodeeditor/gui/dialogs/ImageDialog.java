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
import java.net.MalformedURLException;
import java.net.URL;

import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * @author Assi Nilsmussen
 *
 */
public class ImageDialog extends EditDialog {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * the title-id
	 */
	public static final int IMAGE_ADDRESS		= 0;
	
	/**
	 * constructor
	 * 
	 * @param comp the component to which the dialog-position should be relativ to
	 * @param parent the parent component (to center the dialog)
	 * @param dialogTitle the title of this dialog
	 * @param address the email-address
	 */
	public ImageDialog(Component comp,Frame parent,String dialogTitle,String address) {
		super(comp,parent,dialogTitle);

		addTextField(IMAGE_ADDRESS,LanguageContainer.getText(Language.GUI_DIALOG_IMAGE_URL_FIELD),
				address,false);
		addValidation(IMAGE_ADDRESS,new InputValidation() {
			public String validate(Object value) {
				if(value instanceof String) {
					try {
						new URL((String)value);
						return "";
					}
					catch(MalformedURLException e) {
						
					}
				}
				
				String errorMsg = LanguageContainer.getText(Language.ERROR_INVALID_IMAGE_URL);
				return StringUtils.simpleReplace(errorMsg,"%s",(String)value);
			}
		});
		initLayout();

		setVisible(true);
	}
}