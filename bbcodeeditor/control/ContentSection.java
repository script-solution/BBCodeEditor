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

import bbcodeeditor.control.view.IContentSectionView;




/**
 * A Section which contains text or an image
 * Therefore the cursor is <b>always</b> in a ContentSection, never in an Environment!
 * 
 * @author hrniels
 */
public abstract class ContentSection extends Section {

	/**
	 * constructor
	 * 
	 * @param parentEnv the environment which contains this section
	 * @param startPos the "global" start-position
	 * @param endPos the "global" end-position
	 * @param line the line of this section
	 * @param p the paragraph of this section
	 */
	ContentSection(Environment parentEnv,int startPos,int endPos,
			Line line,Paragraph p) {
		super(parentEnv,startPos,endPos,line,p);
	}
	
	/**
	 * @return the {@link IContentSectionView} implementation
	 */
	public IContentSectionView getSectionView() {
		return (IContentSectionView)_view;
	}

	/**
	 * @return the first cursor-position in this section (in the current paragraph)
	 */
	public int getFirstCursorPos() {
		if(isFirst())
			return _startPos;
		
		return _startPos + 1;
	}
	
	/**
	 * @return the first cursor-position in this section (in the current environment)
	 */
	public int getFirstCursorPosInEnv() {
		if(isFirst())
			return _startPos + _paragraph.getElementStartPos();
		
		return _startPos + 1 + _paragraph.getElementStartPos();
	}
	
	/**
	 * @return the last cursor-position in this section (in the current paragraph)
	 */
	public int getLastCursorPos() {
		if(!isLast() || _line.isLast())
			return _endPos + 1;
		
		return _endPos;
	}
	
	/**
	 * @return the last cursor-position in this section (in the current environment)
	 */
	public int getLastCursorPosInEnv() {
		if(!isLast() || _line.isLast())
			return _endPos + 1 + _paragraph.getElementStartPos();
		
		return _endPos + _paragraph.getElementStartPos();
	}
}