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

import java.util.HashMap;
import java.util.Map;


/**
 * Contains FontInfo objects for ids of TextAttribute-objects.
 * This should save memory and performance because we need less Font- and
 * FontMetrics objects and don't have to create this objects that often.
 * 
 * @author hrniels
 */
public class FontCache {

	/**
	 * All cached FontInfo-objects
	 */
	private final Map _fonts = new HashMap();
	
	/**
	 * Constructor
	 */
	public FontCache() {
		
	}
	
	/**
	 * Clears the cache. <b>This may only be called if all sections will be removed,
	 * too or we refresh the font of all sections!</b>
	 */
	public void clear() {
		_fonts.clear();
	}
	
	/**
	 * Looks in the internal map if there is an entry for the id. If so it
	 * will be returned. Otherwise you'll get <code>null</code>
	 * 
	 * @param id the id of the TextAttribute-object
	 * @return the FontInfo-object or null
	 */
	public FontInfo getFont(int id) {
		Integer iid = Integer.valueOf(id);
		FontInfo info = (FontInfo)_fonts.get(iid);
		if(info != null)
			return info;
		
		return null;
	}

	/**
	 * @return the number of cached fonts
	 */
	public int size() {
		return _fonts.size();
	}
	
	/**
	 * Adds the given FontInfo-object for the given id to the cache
	 * 
	 * @param id the id of the TextAttribute-object
	 * @param font the FontInfo-object
	 */
	public void announceFont(int id,FontInfo font) {
		font.increaseReferences();
		_fonts.put(Integer.valueOf(id),font);
	}
	
	/**
	 * Decreases the number of references of the FontInfo-object. If there are
	 * no other references the object will be removed.
	 * 
	 * @param id the id of the TextAttribute-object
	 * @param font the FontInfo-object
	 */
	public void removeFont(int id,FontInfo font) {
		font.decreaseReferences();
		if(font.getReferences() == 0)
			_fonts.remove(Integer.valueOf(id));
	}
	
	public String toString() {
		return "FontCache[" + _fonts + "]";
	}
}