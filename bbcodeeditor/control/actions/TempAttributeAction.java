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

package bbcodeeditor.control.actions;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.TextAttributes;




/**
 * @author Assi Nilsmussen
 *
 */
public final class TempAttributeAction extends HistoryAction {
	
	/**
	 * the old attributes
	 */
	private final TextAttributes _oldAttributes;
	
	/**
	 * the new attributes
	 */
	private final TextAttributes _newAttributes;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param oldAttr the old attributes
	 * @param newAttr the new attributes
	 */
	public TempAttributeAction(Controller con,TextAttributes oldAttr,TextAttributes newAttr) {
		super(con,0,0);
		
		_oldAttributes = oldAttr;
		_newAttributes = newAttr;
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				_con.setTemporaryAttributes(_oldAttributes);
				
				_actionType = REDO;
				break;
			
			case RemoveTextListAction.REDO:
				_con.setTemporaryAttributes(_newAttributes);
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		return "Change temporary attributes [" + _start + "," + _end + "]";
	}
	
	public String toString() {
		return getName();
	}
}