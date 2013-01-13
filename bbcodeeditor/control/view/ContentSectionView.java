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

import java.awt.Rectangle;

import bbcodeeditor.control.ContentSection;

/**
 * The view for a content-section
 * 
 * @author hrniels
 */
public abstract class ContentSectionView extends View implements IContentSectionView {

	/**
	 * The section of this view
	 */
	protected final ContentSection _section;
	
	/**
	 * Constructor
	 * 
	 * @param section the section
	 */
	public ContentSectionView(ContentSection section) {
		_section = section;
	}
	
	/**
	 * checks wether this section is visible
	 * 
	 * @param paintRect the rectangle to paint
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param width the width of this section
	 * @return true if this section is visible
	 */
	final boolean isPaintingRequired(Rectangle paintRect,int x,int y,int width) {
		int lineHeight = _section.getSectionLine().getLineView().getHeight();
		return paintRect.intersects(new Rectangle(x,y,width,lineHeight));
	}
}