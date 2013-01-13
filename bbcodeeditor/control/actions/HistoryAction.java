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
 * @author Assi Nilsmussen
 * 
 * the base-class for an action in the textfield. this will be used for cut,copy
 * and paste and for undo/redo
 */
public abstract class HistoryAction {

	/**
	 * represents the undo-action
	 */
	public static final int UNDO = 0;
	
	/**
	 * represents the redo-action
	 */
	public static final int REDO = 1;
	
	/**
	 * the controller-instance
	 */
	protected final Controller _con;
	
	/**
	 * the start-position
	 */
	protected int _start;
	
	/**
	 * the end-position
	 */
	protected int _end;
	
	/**
	 * the next action-type
	 */
	protected int _actionType = HistoryAction.UNDO;
	
	/**
	 * constructor
	 * 
	 * @param con the controller of the text-field
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 */
	public HistoryAction(Controller con,int start,int end) {
		_con = con;
		_start = start;
		_end = end;
	}
	
	/**
	 * performs the action corresponding to the type
	 */
	public abstract void performAction();
	
	/**
	 * @return the start-position
	 */
	public final int getStartPos() {
		return _start;
	}
	
	/**
	 * @return the end-position
	 */
	public final int getEndPos() {
		return _end;
	}
	
	/**
	 * builds the text to print from the given text
	 * 
	 * @param text the text
	 * @return the text to print
	 */
	public static final String getPrintText(String text) {
		final int max = 49;
		if(text.length() > max) {
			String start = text.substring(0,(max - 3) / 2);
			String end = text.substring(text.length() - (max - 3) / 2);
			text = start + "..." + end;
		}
		text = text.replaceAll("(\n|\r)","{nl}");
		
		return text;
	}
	
	/**
	 * generates a string to print out for this action
	 * 
	 * @return the name of this action
	 */
	public abstract String getName();
}