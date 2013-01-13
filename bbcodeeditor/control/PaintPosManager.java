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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * The manager for the paint-positions. Stores which positions should be
 * refreshed.
 * 
 * @author hrniels
 */
public class PaintPosManager {
	
	/**
	 * The Controller-instance
	 */
	private final Controller _con;
	
	/**
	 * A list with all paragraphs that need a refresh
	 */
	private final List _dirtyParagraphs = new ArrayList();

	/**
	 * Stores wether all paint-positions should be refreshed
	 */
	private boolean _refreshAll = false;
	
	/**
	 * Constructor
	 * 
	 * @param con the controller
	 */
	public PaintPosManager(Controller con) {
		_con = con;
	}
	
	/**
	 * Marks all as dirty
	 */
	public void markAllDirty() {
		_refreshAll = true;
		_dirtyParagraphs.clear();
	}
	
	/**
	 * Adds the given paragraph as dirty
	 * 
	 * @param p the paragraph
	 */
	public void addParagraph(Paragraph p) {
		if(!_refreshAll) {
			if(!_dirtyParagraphs.contains(p))
				_dirtyParagraphs.add(p);
		}
	}
	
	/**
	 * Refreshes the paint-positions if necessary
	 */
	public void refresh() {
		if(_refreshAll)
			_con.refreshPaintPositions();
		else {
			Iterator it = _dirtyParagraphs.iterator();
			while(it.hasNext()) {
				Paragraph p = (Paragraph)it.next();
				
				// not possible here?
				Paragraph prev = (Paragraph)p.getPrev();
				if(prev != null && prev.getView().getPaintPos() == null) {
					_con.refreshPaintPositions();
					break;
				}
				
				_con.refreshPaintPositionsInParagraph(p);
			}
		}
		
		_refreshAll = false;
		_dirtyParagraphs.clear();
	}
}