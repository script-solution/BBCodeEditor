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

import bbcodeeditor.control.ContentSection;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The view-interface for lines
 * 
 * @author hrniels
 */
public interface ILineView {
	
	/**
	 * Refresh line-height
	 */
	static final byte LINE_HEIGHT	= 1;
	
	/**
	 * Refresh tab-width in the line
	 */
	static final byte TAB_WIDTH		= 2;
	
	/**
	 * recalculates the fonts of the sections and the line-heights
	 */
	void refreshFonts();

	/**
	 * the height of this line
	 * 
	 * @return the height of the line
	 */
	int getHeight();
	
	/**
	 * the descent is the maximum descent of all TextSections in this line
	 * 
	 * @return the descent of this line
	 */
	int getDescent();
	
	/**
	 * @return the width of this line in pixel
	 */
	int getPixelWidth();
	
	/**
	 * calculates the TextSection and the position in it of the cursor<br>
	 * Note that the cursor-position must NOT be valid if exact is enabled!
	 * 
	 * @param targetX the target-X-position
	 * @param targetY the target-Y-position. Will just be used if exact is enabled
	 * @param posX the start-X-position
	 * @param cursorPos will contain the cursor-position after the call (in this env)
	 * @param exact if enabled the x-position HAS TO hit a section
	 * @return the section at the given position or null if not found
	 */
	ContentSection getSectionAtPixelPos(int targetX,int targetY,int posX,MutableInt cursorPos,
			boolean exact);
	
	/**
	 * sets all paint-positions in this line
	 * 
	 * @param x the x-position (will be changed)
	 * @param y the y-position (will be changed)
	 */
	void setPaintPositions(MutableInt x,MutableInt y);
	
	/**
	 * paints this line and calls the paint-method of the sections
	 * 
	 * @param g the graphics to draw with
	 * @param paintRect the rectangle which should be painted
	 * @param x an MutableInt-object with the current x-position for drawing
	 * @param y the current y-position for drawing
	 * @param showCursor should the cursor be drawn?
	 * @param selStart the start-position of the selection in the current environment
	 * @param selEnd the end-position of the selection in the current environment
	 */
	void paint(Graphics g,Rectangle paintRect,MutableInt x,int y,boolean showCursor,
			int selStart,int selEnd);
}