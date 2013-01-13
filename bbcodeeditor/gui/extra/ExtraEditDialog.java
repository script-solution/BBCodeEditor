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

import bbcodeeditor.gui.Settings;
import bbcodeeditor.gui.dialogs.EditDialog;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The Edit-Dialog for the extra-tags
 * 
 * @author hrniels
 */
public class ExtraEditDialog extends EditDialog {
	
	private static final long serialVersionUID = 7044631167890282141L;

	/**
	 * constructor
	 * 
	 * @param tag the ExtraTag-instance
	 * @param paramID the id for the parameter
	 * @param titleID the id for the title
	 * @param title the title
	 */
	public ExtraEditDialog(ExtraTag tag,int paramID,int titleID,String title) {
		super(Settings.DIALOG_COMPONENT,null,
				LanguageContainer.getText(tag.getDialogTitle()));
		
		if(tag.getDescription().length() > 0)
			setDescription(LanguageContainer.getText(tag.getDescription()));
		
		if(tag.hasParameter())
			addTextField(titleID,LanguageContainer.getText(Language.GUI_DIALOG_EXTRA_TITLE),title,false);
		addTextField(paramID,LanguageContainer.getText(tag.getParameterTitle()),"",false);
		
		initLayout();

		setVisible(true);
	}
}