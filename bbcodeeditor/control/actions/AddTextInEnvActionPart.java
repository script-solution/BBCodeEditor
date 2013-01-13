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
import bbcodeeditor.control.Environment;


/**
 * the add-text-action-part
 * 
 * @author hrniels
 */
public final class AddTextInEnvActionPart extends HistoryActionPart {
	
	private final boolean _containsStyles;
	
	/**
	 * the text to add
	 */
	private final String _text;

	/**
	 * constructor
	 * 
	 * @param env the parent-environment
	 * @param start the global position
	 * @param text the text to add
	 */
	public AddTextInEnvActionPart(Environment env,int start,String text) {
		super(start);

		_containsStyles = env.containsStyles();
		_text = text;
	}
	
	public void execute(Controller con) {
		con.goToPosition(_start);
		con.pasteTextAtCursor(_text,_containsStyles);
	}
	
	public String getName() {
		return "Add '" + HistoryAction.getPrintText(_text) + "' [" + _start + "]";
	}
	
	public String getText(Controller con) {
		return _text;
	}
	
	public String toString() {
		return getName();
	}
}