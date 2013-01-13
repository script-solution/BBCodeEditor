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

import java.awt.*;

import bbcodeeditor.control.Line;
import bbcodeeditor.control.Paragraph;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The view-interface for an environment
 * 
 * @author hrniels
 */
public interface IEnvironmentView {
	
	/**
	 * @return the default font for this environment
	 */
	Font getDefaultFont();
	
	/**
	 * @return the default font-metrics for this environment
	 */
	FontMetrics getDefaultFontMetrics();
	
	/**
	 * @return the default font-family of this environment
	 */
	String getDefaultFontFamily();
	
	/**
	 * @return the default font-size of this environment
	 */
	int getDefaultFontSize();
	
	/**
	 * @return the default font-style of this environment
	 */
	int getDefaultFontStyle();
	
	/**
	 * @return the default font-color of this environment
	 */
	Color getDefaultFontColor();
	
	/**
	 * @return the default Background-color of this environment
	 */
	Color getBackgroundColor();
	
	/**
	 * @return the line-spacing for this environment
	 */
	int getLineSpacing();
	
	/**
	 * @return the top inner-padding of this environment
	 */
	int getInnerTopPadding();
	
	/**
	 * @return the bottom inner-padding of this environment
	 */
	int getInnerBottomPadding();
	
	/**
	 * @return the left inner-padding of this environment
	 */
	int getInnerLeftPadding();
	
	/**
	 * @return the right inner-padding of this environment
	 */
	int getInnerRightPadding();
	
	/**
	 * @return the outer-padding of this environment
	 */
	int getOuterPadding();
	
	/**
	 * @return the total width of this environment
	 */
	int getTotalWidth();

	/**
	 * determines the complete height of this environment
	 * 
	 * @return the height
	 */
	int getHeight();
	
	/**
	 * @return the start-position of the text in this environment in pixel
	 */
	int getGlobalTextStart();
	
	/**
	 * refreshes the tab-width in all sections of this environment
	 */
	void refreshTabWidth();
	
	/**
	 * recalculates the fonts of the sections and the line-heights
	 */
	void refreshFonts();
	
	/**
	 * calculates the line of the cursor
	 * 
	 * @param targetY the target-Y-position
	 * @param exact if enabled the y-position HAS TO hit this line
	 * @return the line at that position or null if not found
	 */
	Line getLineAtPixelPos(int targetY,boolean exact);
	
	/**
	 * determines the paragraph which will be painted at the given y-position
	 * 
	 * @param y the y-position to search for
	 * @return the paragraph (may NOT be null!)
	 */
	Paragraph getParagraphAtPixelPosition(int y);
	
	/**
	 * determines the index of the paragraph which will be painted at the given y-position
	 * 
	 * @param y the y-position to search for
	 * @return the index of the paragraph (-1 = below the last paragraph; -2 = above the first)
	 */
	int getIndexOfParagraphAtPixelPos(int y);
	
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
	 * paints the environment and calls the paint-method of the lines
	 * 
	 * @param g the graphics to draw with
	 * @param paintRect the rectangle which should be painted
	 * @param x an MutableInt-object with the current x-position for drawing
	 * @param y an MutableInt-object with the current y-position for drawing
	 * @param showCursor should the cursor be drawn?
	 * @param selStart the start-position of the selection in the this environment
	 * @param selEnd the end-position of the selection in the this environment
	 */
	void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd);
}