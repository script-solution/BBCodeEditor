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

import bbcodeeditor.control.CodeEnvironment;
import bbcodeeditor.control.Controller;
import bbcodeeditor.control.Environment;
import bbcodeeditor.control.QuoteEnvironment;


/**
 * an action-part to add an environment
 * 
 * @author hrniels
 */
public final class AddEnvironmentActionPart extends HistoryActionPart {

	/**
	 * the environment to add
	 */
	private final Environment _env;
	
	/**
	 * is it a new list-point?
	 */
	private final boolean _isListPoint;
	
	/**
	 * constructor
	 * 
	 * @param env the Environment to add
	 * @param start the start-position of this action (global!)
	 * @param isListPoint is it a new list-point?
	 */
	public AddEnvironmentActionPart(Environment env,int start,boolean isListPoint) {
		super(start);
		
		_env = env;
		_isListPoint = isListPoint;
	}
	
	public void execute(Controller con) {
		// add the environment
		con.addEnvironment(_env,_start,_isListPoint,true);
		
		// go behind the environment
		con.goToPosition(_start + _env.getElementLength() + 2);
	}
	
	public String getName() {
		String envName;
		if(_env instanceof QuoteEnvironment)
			envName = "quote-environment";
		else if(_env instanceof CodeEnvironment)
			envName = "code-environment";
		else
			envName = "list-environment";
		
		return "Add " + envName + " [" + _start + "]";
	}
	
	public String getText(Controller con) {
		String envName;
		if(_env instanceof QuoteEnvironment)
			envName = "[quote]";
		else if(_env instanceof CodeEnvironment)
			envName = "[code]";
		else
			envName = "[list]";
		return envName;
	}
	
	public String toString() {
		return getName();
	}
}