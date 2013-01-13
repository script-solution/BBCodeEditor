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

package bbcodeeditor.control;

import bbcodeeditor.control.view.ListEnvironmentView;


/**
 * represents a List-Environment, which means that every line is a new list-point
 * A List may contain sub-environments and formating.<br>
 * Every Line will be painted with a list-point (depending on the list-type) in front of
 * the Line.
 * 
 * @author hrniels
 */
public final class ListEnvironment extends Environment {
	
	/**
	 * the list-type of this environment
	 */
	private int _listType = ListTypes.TYPE_DEFAULT;
	
	/**
	 * constructor
	 * 
	 * @param textArea the BBCTextField-instance
	 * @param parent the parent environment
	 * @param line the Line of this environment
	 * @param p the paragraph of this environment
	 * @param listType the type of the list (see TYPE_*)
	 */
	ListEnvironment(AbstractTextField textArea,Environment parent,Line line,Paragraph p,
			int listType) {
		super(textArea,parent,line,p);
		
		_view = new ListEnvironmentView(this);
		
		if(ListTypes.isValidType(listType))
			_listType = listType;
	}
	
	/**
	 * @return the list-type of this list-environment
	 */
	public int getListType() {
		return _listType;
	}
	
	/**
	 * sets the list-type to given value
	 * 
	 * @param type the new list-type. see TYPE_*
	 * @return true if the type has been changed
	 */
	public boolean setListType(int type) {
		if(ListTypes.isValidType(type)) {
			_listType = type;

			// we have to refresh the list-points
			_view.forceRefresh(ListEnvironmentView.LIST_CACHE);
			
			return true;
		}
		
		return false;
	}
	
	public int getType() {
		return EnvironmentTypes.ENV_LIST;
	}
}