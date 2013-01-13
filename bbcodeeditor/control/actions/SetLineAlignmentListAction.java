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

import java.util.Iterator;
import java.util.List;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.ParagraphAttributes;


/**
 * the line-alignment action
 * 
 * @author hrniels
 */
public final class SetLineAlignmentListAction extends HistoryAction {
	
	/**
	 * the list of actions
	 */
	private final List _actions;
	
	/**
	 * the alignment
	 */
	private final int _align;
	
	/**
	 * constructor
	 * 
	 * @param con the Controller-instance
	 * @param actions an ArrayList with SetAttributeAction`s
	 * @param align the new alignment
	 */
	public SetLineAlignmentListAction(Controller con,List actions,int align) {
		super(con,-1,-1);
		
		_align = align;
		_actions = actions;
		
		SetLineAlignmentActionPart first = (SetLineAlignmentActionPart)actions.get(0);
		SetLineAlignmentActionPart last = (SetLineAlignmentActionPart)actions.get(actions.size() - 1);
		_start = first.getStartPosition();
		_end = last.getEndPosition();
	}
	
	public void performAction() {
		switch(_actionType) {
			case UNDO:
				Iterator it = _actions.iterator();
				while(it.hasNext()) {
					HistoryActionPart s = (HistoryActionPart)it.next();
					s.execute(_con);
				}
				
				_actionType = REDO;
				break;
				
			case REDO:
				_con.setLineAlignment(_start,_end,_align);
				
				_actionType = UNDO;
				break;
		}
	}
	
	public String getName() {
		StringBuffer buf = new StringBuffer();
		Iterator it = _actions.iterator();
		while(it.hasNext()) {
			HistoryActionPart part = (HistoryActionPart)it.next();
			buf.append(part.getText(_con));
		}
		
		String text = HistoryAction.getPrintText(buf.toString());
		
		if(_actionType == REDO)
			return "Align=" + ParagraphAttributes.getAlignmentName(_align) + " of '" + text + "'";

		return "Undo Align=" + ParagraphAttributes.getAlignmentName(_align) + " of '" + text + "'";
	}
	
	public String toString() {
		return getName();
	}
}