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
import bbcodeeditor.control.IPublicController;
import bbcodeeditor.control.ParagraphAttributes;


/**
 * the action-part to set the line-alignment
 * 
 * @author hrniels
 */
public final class SetLineAlignmentActionPart extends HistoryActionPart {

	/**
	 * the alignment
	 */
	private final int _align;
	
	/**
	 * the end-position
	 */
	private final int _end;
	
	/**
	 * constructor
	 * 
	 * @param start the start-position of this action
	 * @param end the end-position of this action
	 * @param align the line-alignment
	 */
	public SetLineAlignmentActionPart(int start,int end,int align) {
		super(start);
		
		_align = align;
		_end = end;
	}
	
	/**
	 * @return the end-position
	 */
	public int getEndPosition() {
		return _end;
	}
	
	/**
	 * @return the align
	 */
	public int getAlign() {
		return _align;
	}
	
	public void execute(Controller con) {
		con.setLineAlignment(_start,_end,_align);
	}
	
	public String getText(Controller con) {
		return con.getText(_start,_end,IPublicController.SYNTAX_PLAIN);
	}
	
	public String getName() {
		return "Set alignment: " + ParagraphAttributes.getAlignmentName(_align) + " [" + _start + "," + _end + "]";
	}
	
	public String toString() {
		return getName();
	}
}