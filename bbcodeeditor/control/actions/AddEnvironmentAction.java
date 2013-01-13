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

import bbcodeeditor.control.*;


/**
 * the add-environment-action
 * 
 * @author hrniels
 */
public final class AddEnvironmentAction extends HistoryAction {

	/**
	 * the environment to add
	 */
	private final Environment _env;
	
	/**
	 * is it a new list-point?
	 */
	private final boolean _isListPoint;
	
	/**
	 * add a new line after removing the env?
	 */
	private final boolean _newLine;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param env the Environment to add
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 * @param isListPoint is it a new list-point?
	 * @param newLine add a new line after removing the env?
	 */
	public AddEnvironmentAction(Controller con,Environment env,int start,
			int end,boolean isListPoint,boolean newLine) {
		super(con,start,end);
		
		_env = env;
		_isListPoint = isListPoint;
		_newLine = newLine;
	}

	public void performAction() {
		switch(_actionType) {
			case UNDO:
				_con.removeText(_start,_end,false);
				
				if(_newLine)
					_con.addNewLine(null,ParagraphAttributes.ALIGN_UNDEF,true);
				
				_con.goToPosition(_start);
				
				_actionType = REDO;
				break;
				
			case REDO:
				_con.addEnvironment(_env,_start,_isListPoint,!_newLine);
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		String envName;
		if(_env instanceof QuoteEnvironment)
			envName = "quote-environment";
		else if(_env instanceof CodeEnvironment)
			envName = "code-environment";
		else
			envName = "list-environment";
		
		String res;
		if(_actionType == UNDO)
			res = "Remove " + envName;
		else
			res = "Add " + envName;
		
		return res + " [" + _start + "," + _end + "]";
	}
	
	public String toString() {
		return getName();
	}
}