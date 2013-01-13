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

import java.awt.Graphics;
import java.awt.Rectangle;

import bbcodeeditor.control.Line;
import bbcodeeditor.control.tools.MutableInt;

/**
 * The view-interface for paragraphs
 * 
 * @author hrniels
 */
public interface IParagraphView {
	
	/**
	 * refreshes the tab-width in all sections of this paragraph
	 */
	void refreshTabWidth();
	
	/**
	 * recalculates the fonts of the sections and the line-heights
	 */
	void refreshFonts();
	
	/**
	 * the height of the complete paragraph (the sum of all line-heights)
	 * 
	 * @return the paragraph-height
	 */
	int getHeight();
	
	/**
	 * determines the line which will be painted at the given y-position
	 * 
	 * @param y the y-position to search for
	 * @param exact if enabled the y-position HAS TO hit this line
	 * @return the line (may NOT be null if exact is false!)
	 */
	Line getLineAtPixelPosition(int y,boolean exact);
	
	/**
	 * determines the line which will be painted at the given y-position
	 * 
	 * @param y the y-position to search for
	 * @return the index of the line (-1 = below the last line; -2 = above the first)
	 */
	int getIndexOfLineAtPixelPos(int y);

	/**
	 * sets all paint-positions and calculates the maximum required width
	 * 
	 * @param g the Graphics context
	 * @param x the x-position (will be changed)
	 * @param y the y-position (will be changed)
	 * @param maxWidth contains the max-width after the call
	 */
	void setPaintPositions(Graphics g,MutableInt x,MutableInt y,MutableInt maxWidth);
	
	/**
	 * paints this line and calls the paint-method of the sections
	 * 
	 * @param g the graphics to draw with
	 * @param paintRect the rectangle which should be painted
	 * @param x an MutableInt-object with the current x-position for drawing
	 * @param y an MutableInt-object with the current y-position for drawing
	 * @param showCursor should the cursor be drawn?
	 * @param selStart the start-position of the selection in the current environment
	 * @param selEnd the end-position of the selection in the current environment
	 */
	void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd);
}