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

package bbcodeeditor.control.view;

import java.awt.Point;

import bbcodeeditor.control.AbstractTextField;

/**
 * The base-class of all views
 * 
 * @author hrniels
 */
public abstract class View {
	
	/**
	 * the paint-position of this element
	 */
	private Point _paintPos = null;
	
	/**
	 * Stores what we have to refresh, if any
	 */
	protected byte _forceRefresh = 0;
	
	/**
	 * Stores wether we have already sent the refresh to the ViewManager
	 */
	private boolean _sentRefresh = false;
	
	/**
	 * Checks wether the given type should been refreshed
	 * 
	 * @param type the type
	 * @return true if so
	 */
	protected boolean shouldRefresh(byte type) {
		return (_forceRefresh & type) != 0;
	}
	
	/**
	 * Forces a refresh of the given type
	 * 
	 * @param type the type
	 */
	public void forceRefresh(byte type) {
		boolean changed = (_forceRefresh & type) == 0;
		_forceRefresh |= type;
		
		// mark this view dirty
		if(changed && !_sentRefresh) {
			getTextField().getViewManager().markDirty(this);
			_sentRefresh = true;
		}
	}
	
	/**
	 * Clears all refresh-types (so that there is nothing to refresh anymore)
	 */
	protected void clearRefreshes() {
		_sentRefresh = false;
		_forceRefresh = 0;
	}
	
	/**
	 * @return the textfield-instance
	 */
	public abstract AbstractTextField getTextField();
	
	/**
	 * Refreshes all required stuff
	 */
	public abstract void refresh();
	
	/**
	 * @return the Position of this section at the last paint-event
	 */
	public Point getPaintPos() {
		return _paintPos;
	}
	
	/**
	 * sets the paint-position of this section
	 * 
	 * @param p the paint-position
	 */
	public void setPaintPos(Point p) {
		_paintPos = p;
	}
}