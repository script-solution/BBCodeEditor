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

import bbcodeeditor.control.tools.MutableInt;


/**
 * The view-interface for a content-section
 * 
 * @author hrniels
 */
public interface IContentSectionView {

	/**
	 * @return the height of this section in pixel
	 */
	int getSectionHeight();
	
	/**
	 * @return the width of this section in pixel
	 */
	int getSectionWidth();
	
	/**
	 * this will be used to highlight the line-wrap
	 * 
	 * @return the width of a char (doesn't matter which char...)
	 */
	int getCharWidth();
	
	/**
	 * @return the descent of this section
	 */
	int getDescent();
	
	/**
	 * paints a rectangle with given width at the given position in this section with
	 * the corresponding height and so on.<br>
	 * Will be used to paint the cursor and the line-wrap
	 * 
	 * @param g the graphics-context
	 * @param x the x-coordinate of this section
	 * @param y the y-coordinate of this section
	 * @param cursorPos the cursor-position in this section
	 * @param width the width of the rectangle
	 */
	void paintRect(Graphics g,int x,int y,int cursorPos,int width);
	
	/**
	 * sets the paint-position
	 * 
	 * @param x the x-position (will be changed)
	 * @param y the y-position (will be changed)
	 */
	void setPaintPosition(MutableInt x,MutableInt y);
	
	/**
	 * paints this text-section
	 * 
	 * @param g the graphics to draw with
	 * @param paintRect the rectangle which should be painted
	 * @param x an MutableInt-object with the current x-position for drawing
	 * @param y the current y-position for drawing
	 * @param selStart the selection-start-position (-1 if nothing is selected)
	 * @param selEnd the selection-end-position (-1 if nothing is selected)
	 */
	void paint(Graphics g,Rectangle paintRect,MutableInt x,int y,int selStart,int selEnd);
}