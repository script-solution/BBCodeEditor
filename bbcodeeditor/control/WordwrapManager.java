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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * The manager for word-wraps. Collects paragraphs that need a refresh.
 * 
 * @author hrniels
 */
public class WordwrapManager {
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;

	/**
	 * A set with all dirty paragraphs
	 */
	private final Set _dirtyParagraphs = new HashSet();
	
	/**
	 * Constructor
	 * 
	 * @param textField the textfield
	 */
	public WordwrapManager(AbstractTextField textField) {
		_textField = textField;
	}
	
	/**
	 * Marks the given paragraph as "dirty". That means that wordwrap will
	 * be performed after all actions are finished.
	 * 
	 * @param p the paragraph
	 */
	public void markDirty(Paragraph p) {
		_dirtyParagraphs.add(p);
	}
	
	/**
	 * Refreshes all dirty paragraphs
	 */
	public void refresh() {
		boolean changed = false;
		Iterator it = _dirtyParagraphs.iterator();
		while(it.hasNext()) {
			Paragraph p = (Paragraph)it.next();
			if(p.performWordWrap() > 0)
				changed = true;
		}
		
		if(changed) {
			_textField._controller.getCurrentEnvironment().correctCurrentSection();
			_textField.getRepaintManager().markCompletlyDirty();
			_textField.getPaintPosManager().markAllDirty();
		}
		
		_dirtyParagraphs.clear();
	}
}