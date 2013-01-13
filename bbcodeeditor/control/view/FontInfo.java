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

import java.awt.Font;
import java.awt.FontMetrics;


/**
 * Contains the Font- and FontMetrics-object
 * 
 * @author hrniels
 */
public class FontInfo {
	
	/**
	 * The font-object
	 */
	private final Font _font;

	/**
	 * The font-metrics-object
	 */
	private final FontMetrics _fontMetrics;
	
	/**
	 * Stores the number of references of this object
	 */
	private int _references = 0;
	
	/**
	 * Constructor
	 * 
	 * @param font the Font-object
	 * @param metrics the FontMetrics-object
	 */
	public FontInfo(Font font,FontMetrics metrics) {
		_font = font;
		_fontMetrics = metrics;
	}
	
	/**
	 * @return the number of references
	 */
	int getReferences() {
		return _references;
	}
	
	/**
	 * Increases the number of references
	 */
	void increaseReferences() {
		_references++;
	}
	
	/**
	 * Decreases the number of references
	 */
	void decreaseReferences() {
		_references = Math.max(0,_references - 1);
	}
	
	/**
	 * @return the font-object
	 */
	public Font getFont() {
		return _font;
	}
	
	/**
	 * @return the font-metrics-object
	 */
	public FontMetrics getFontMetrics() {
		return _fontMetrics;
	}
	
	public String toString() {
		return "FontInfo[" + _references + ";" + _font + "]";
	}
}