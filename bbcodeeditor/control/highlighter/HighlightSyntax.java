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

package bbcodeeditor.control.highlighter;

import java.util.*;

/**
 * The highlight-syntaxes
 *
 * @author hrniels
 */
public class HighlightSyntax {
	
	/**
	 * All known highlighter-files
	 */
	public static final Map _highlighterFiles = new HashMap();
	
	/**
	 * All active highlighters
	 */
	public static final Map _highlighters = new HashMap();

	/**
	 * Adds the given highlighter with the given id to the known highlighters.
	 * 
	 * @param entry the HighlighterEntry to add
	 */
	public static void addHighlighter(HighlighterEntry entry) {
		addHighlighter(Arrays.asList(new HighlighterEntry[] {entry}));
	}
	
	/**
	 * Adds all highlighters in the given list to the known highlighters.
	 * The list should contain HighlighterEntries.
	 * 
	 * @param highlighters the highlighters to add
	 */
	public static void addHighlighter(List highlighters) {
		Iterator it = highlighters.iterator();
		while(it.hasNext()) {
			HighlighterEntry e = (HighlighterEntry)it.next();
			_highlighterFiles.put(e.getId(),e);
		}
	}
	
	/**
	 * Announces that the highlighter with given syntax is needed.
	 * 
	 * @param id the id
	 * @return true if successfull
	 */
	public static boolean announceHighlighter(Object id) {
		HighlighterInstance inst = (HighlighterInstance)_highlighters.get(id);
		if(inst != null)
			inst.incrementRefs();
		else {
			HighlighterEntry entry = (HighlighterEntry)_highlighterFiles.get(id);
			if(entry != null) {
				inst = new HighlighterInstance(new CustomHighlighter(entry.getId(),entry.getFile()));
				_highlighters.put(id,inst);
			}
			else
				return false;
		}
		
		return true;
	}
	
	/**
	 * Unloads the given highlighter. If there are no other references to
	 * the highlighter with given syntax it will be deleted.
	 * 
	 * @param id the id
	 */
	public static void removeHighlighter(Object id) {
		HighlighterInstance inst = (HighlighterInstance)_highlighters.get(id);
		if(inst != null) {
			inst.decrementRefs();
			if(inst.getReferences() == 0)
				_highlighters.remove(inst);
		}
	}
	
	/**
	 * Returns the highlighter-implementation for the given syntax
	 * 
	 * @param id the id
	 * @return the highlighter-implementation or null if there is nothing to highlight
	 */
	public static Highlighter getHighlighter(Object id) {
		HighlighterInstance inst = (HighlighterInstance)_highlighters.get(id);
		if(inst != null)
			return inst.getHighlighter();
		
		return null;
	}
	
	/**
	 * @return a Map with all available highlighters
	 */
	public static Map getHighlighter() {
		return _highlighterFiles;
	}
	
	/**
	 * Contains the instance of a highlighter including the number of references
	 * 
	 * @author hrniels
	 */
	private static final class HighlighterInstance {
		
		/**
		 * The highlighter instance
		 */
		private final Highlighter _hl;
		
		/**
		 * The number of references to this highlighter
		 */
		private int _references = 1;
		
		/**
		 * Constructor
		 * 
		 * @param hl the highlighter
		 */
		public HighlighterInstance(Highlighter hl) {
			_hl = hl;
		}
		
		/**
		 * @return the highlighter
		 */
		public Highlighter getHighlighter() {
			return _hl;
		}
		
		/**
		 * @return the number of references
		 */
		public int getReferences() {
			return _references;
		}
		
		/**
		 * Increments the number of references
		 */
		public void incrementRefs() {
			_references++;
		}
		
		/**
		 * Decrements the number of references
		 */
		public void decrementRefs() {
			_references = Math.max(0,_references - 1);
		}
	}
}