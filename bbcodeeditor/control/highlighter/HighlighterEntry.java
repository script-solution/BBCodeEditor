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

import java.net.URL;

/**
 * An entry of the highlighter-map
 * 
 * @author hrniels
 */
public class HighlighterEntry {
	
	/**
	 * The id of the highlighter
	 */
	private final String _id;
	
	/**
	 * The name of the highlighter
	 */
	private final String _name;
	
	/**
	 * The URL of the file in which the highlighter-definitions are
	 */
	private final URL _fileURL;
	
	/**
	 * Constructor
	 * 
	 * @param id the id of the highlighter
	 * @param name the name of the highlighter
	 * @param file the URL of the file in which the highlighter-definitions are
	 */
	public HighlighterEntry(String id,String name,URL file) {
		_id = id;
		_name = name;
		_fileURL = file;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof HighlighterEntry))
			return false;
		
		if(o == this)
			return true;
		
		HighlighterEntry e = (HighlighterEntry)o;
		return _id.equals(e.getId());
	}
	
	public int hashCode() {
		return _id.hashCode();
	}
	
	/**
	 * @return the id of the highlighter
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * @return the name of the highlighter
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * @return the URL of the file in which the highlighter-definitions are
	 */
	public URL getFile() {
		return _fileURL;
	}
	
	public String toString() {
		return "HighlighterEntry[id=" + _id + ",name=" + _name + ",file=" + _fileURL + "]";
	}
}