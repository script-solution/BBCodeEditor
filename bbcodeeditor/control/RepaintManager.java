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

import java.util.*;
import java.util.Map.Entry;


/**
 * The repaint-manager for the textfield. Stores lines and sections that
 * have to be repainted or wether the complete control needs a repaint
 * 
 * @author hrniels
 */
public class RepaintManager {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;

	/**
	 * Contains all lines that are "dirty". Stores as value if the complete
	 * line has to be repainted
	 */
	private final Map _dirtyLines = new HashMap();
	
	/**
	 * Contains all sections that are "dirty"
	 */
	private final List _dirtySections = new ArrayList();
	
	/**
	 * Indicates if the complete control should be repainted
	 */
	private boolean _completlyDirty = false;
	
	/**
	 * Constructor
	 * 
	 * @param tf the textfield-instance
	 */
	public RepaintManager(AbstractTextField tf) {
		_textField = tf;
	}
	
	/**
	 * Repaints everything that is necessary
	 */
	public void repaint() {
		if(_completlyDirty)
			_textField.repaint(true);
		else {
			// paint lines
			Iterator it = _dirtyLines.entrySet().iterator();
			while(it.hasNext()) {
				Entry e = (Entry)it.next();
				boolean complete = ((Boolean)e.getValue()).booleanValue();
				_textField.paintLine((Line)e.getKey(),complete);
			}
			
			// paint sections
			if(_dirtySections.size() > 0) {
				ContentSection[] s = (ContentSection[])_dirtySections.toArray(new ContentSection[0]);
				_textField.paintSections(s);
			}
		}
		
		// reset
		_completlyDirty = false;
		_dirtyLines.clear();
		_dirtySections.clear();
	}
	
	/**
	 * Marks everything dirty
	 */
	public void markCompletlyDirty() {
		_completlyDirty = true;
		_dirtyLines.clear();
		_dirtySections.clear();
	}
	
	/**
	 * Adds the given line to the dirty lines
	 * 
	 * @param l the line
	 * @param complete repaint the complete line?
	 */
	public void addDirtyLine(Line l,boolean complete) {
		if(_completlyDirty)
			return;
		
		if(_dirtyLines.containsKey(l))
			return;
		
		_dirtyLines.put(l,Boolean.valueOf(complete));
		
		// remove the sections that are in the line
		Iterator it = _dirtySections.iterator();
		while(it.hasNext()) {
			ContentSection s = (ContentSection)it.next();
			if(s.getSectionLine() == l)
				_dirtySections.remove(s);
		}
	}
	
	/**
	 * Adds the given section to the dirty sections
	 * 
	 * @param s the section
	 */
	public void addDirtySection(ContentSection s) {
		addDirtySections(new ContentSection[] {s});
	}

	/**
	 * Adds the given sections to the dirty sections
	 * 
	 * @param s the sections
	 */
	public void addDirtySections(ContentSection[] s) {
		addDirtySections(Arrays.asList(s));
	}

	/**
	 * Adds the given sections to the dirty sections
	 * 
	 * @param sections the sections
	 */
	public void addDirtySections(List sections) {
		if(_completlyDirty)
			return;
		
		Iterator it = sections.iterator();
		while(it.hasNext()) {
			ContentSection s = (ContentSection)it.next();
			if(!_dirtySections.contains(s)) {
				if(!_dirtyLines.containsKey(s.getSectionLine()))
					_dirtySections.add(s);
			}
		}
	}
}