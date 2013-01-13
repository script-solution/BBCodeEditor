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


/**
 * a part of an history-action
 * 
 * @author hrniels
 */
public abstract class HistoryActionPart {

	/**
	 * the start-position
	 */
	protected final int _start;
	
	/**
	 * constructor
	 * 
	 * @param start the start-position
	 */
	public HistoryActionPart(int start) {
		_start = start;
	}
	
	/**
	 * @return the start-position
	 */
	public final int getStartPosition() {
		return _start;
	}
	
	/**
	 * executes this action-part
	 * 
	 * @param con the Controller-instance
	 */
	public abstract void execute(Controller con);

	
	/**
	 * generates a string to print out for this action
	 * 
	 * @return the name of this action
	 */
	public abstract String getName();
	
	/**
	 * @param con the Controller
	 * @return the text of this history-action-part (may be empty)
	 */
	public abstract String getText(Controller con);
}