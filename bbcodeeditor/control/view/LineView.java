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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import bbcodeeditor.control.*;
import bbcodeeditor.control.tools.MutableInt;

/**
 * The view for a Line
 * 
 * @author hrniels
 */
public class LineView extends View implements ILineView {
	
	/**
	 * The line instance
	 */
	protected final Line _line;
	
	/**
	 * the height of this line
	 */
	private int _height;
	
	/**
	 * the descent of this line
	 */
	private int _descent;

	/**
	 * Constructor
	 * 
	 * @param line the line
	 */
	public LineView(Line line) {
		_line = line;
	}
	
	public void forceRefresh(byte type) {
		if(type == TAB_WIDTH)
			forceTabWidthRefresh();
		else
			super.forceRefresh(type);
	}
	
	public void refresh() {
		if(shouldRefresh(LINE_HEIGHT))
			recalculateHeight();
		
		clearRefreshes();
	}
	
	public AbstractTextField getTextField() {
		return _line.getParentEnvironment().getTextField();
	}

	/**
	 * refreshes the tab-width in this line
	 */
	private void forceTabWidthRefresh() {
		ContentSection sec = (ContentSection)_line.getFirstSection();
		while(sec != null) {
			if(sec instanceof TextSection) {
				TextSection tSec = (TextSection)sec;
				tSec.getView().forceRefresh(TextSectionView.PAINT_TEXT);
			}
			
			sec = (ContentSection)sec.getNext();
		}
	}
	
	public void refreshFonts() {
		Section s = _line.getFirstSection();
		do {
			if(s instanceof TextSection)
				((TextSection)s).getTextSectionView().refreshFont();
			
			s = (Section)s.getNext();
		} while(s != null);
		
		recalculateHeight();
	}
	
	/**
	 * calculates the height of this line
	 */
	private void recalculateHeight() {
		int max = 0;
		int maxDesc = 0;
		int len = _line.getSectionCount();
		for(int i = 0;i < len;i++) {
			Section s = _line.getSection(i);
			if(s instanceof ContentSection) {
				int tHeight = ((ContentSection)s).getSectionView().getSectionHeight();
				if(tHeight > max)
					max = tHeight;
				
				int descent = ((ContentSection)s).getSectionView().getDescent();
				if(descent > maxDesc)
					maxDesc = descent;
			}
		}
		
		int old = _height;
		_descent = maxDesc;
		_height = max;
		
		// we have to refresh all if the height has changed
		if(_height != old && _line.getParentEnvironment().getTextField().getPaintPosManager() != null)
			_line.getParentEnvironment().getTextField().getPaintPosManager().markAllDirty();
	}
	
	public int getHeight() {
		if(_line.containsEnvironment())
			return ((Environment)_line.getFirstSection()).getEnvView().getHeight();
		
		// we want to apply the spacing not in the first line of the first paragraph
		int add = 0;
		if(!_line.isFirst() || !_line.getParagraph().isFirst())
			add = _line.getParentEnvironment().getEnvView().getLineSpacing();
		
		return _height + add;
	}
	
	/**
	 * determines the paint-position of the first section in this line
	 * or the environment
	 * 
	 * @return the position
	 */
	public Point getPaintPos() {
		Section first = _line.getFirstSection();
		return first.getView().getPaintPos();
	}
	
	public int getDescent() {
		return _descent;
	}
	
	public int getPixelWidth() {
		int width = 0;
		Section first = _line.getFirstSection();
		if(first instanceof Environment)
			return 0;
		
		width += ((ContentSection)first).getSectionView().getSectionWidth();
		int len = _line.getSectionCount();
		for(int i = 1;i < len;i++) {
			ContentSection section = (ContentSection)_line.getSection(i);
			width += section.getSectionView().getSectionWidth();
		}
		
		return width;
	}
	
	public ContentSection getSectionAtPixelPos(int targetX,int targetY,int posX,
			MutableInt cursorPos,boolean exact) {
		int lineWidth,envWidth;
		IEnvironmentView envView = _line.getParentEnvironment().getEnvView();
		switch(_line.getParagraph().getHorizontalAlignment()) {
			case ParagraphAttributes.ALIGN_CENTER:
				lineWidth = getPixelWidth();
				envWidth = envView.getTotalWidth() - envView.getInnerLeftPadding() -
					envView.getInnerRightPadding();
				posX += (envWidth - lineWidth) / 2;
				break;
				
			case ParagraphAttributes.ALIGN_RIGHT:
				lineWidth = getPixelWidth();
				envWidth = envView.getTotalWidth() - envView.getInnerLeftPadding() -
					envView.getInnerRightPadding();
				posX += envWidth - lineWidth;
				break;
		}

		// in front of the first section?
		if(targetX < posX && exact)
			return null;
		
		int secCount = _line.getSectionCount();
		ContentSection sec = null;
		for(int i = 0;i < secCount;i++) {
			sec = (ContentSection)_line.getSection(i);
			IContentSectionView secView = sec.getSectionView();
			int width = secView.getSectionWidth();
			
			// is the cursor in this section?
			if(targetX <= posX + width) {
				// we have found the section if exact is enabled
				if(exact) {
					// check if the y-position is correct
					int secYStart = sec.getView().getPaintPos().y + (getHeight() - secView.getSectionHeight());
					if(targetY >= secYStart && targetY <= secYStart + secView.getSectionHeight())
						return sec;
					
					return null;
				}
				
				// there are only two possible position in an ImageSection, so handle this separatly
				if(sec instanceof ImageSection) {
					int pAdd = (targetX - posX < (width / 2)) ? 0 : 1;
					cursorPos.setValue(sec.getStartPosInEnv() + pAdd);
					
					// we have to move the cursor to the prev section if we are at the beginning
					// of the section and there is a previous one
					if(pAdd == 0 && !sec.isFirst())
						return (ContentSection)sec.getPrev();
					
					return sec;
				}
				
				// else walk through the section and find the character at the position
				TextSection tSec = (TextSection)sec;
				String text = tSec.getText();
				int lastWidth = 0;
				float pos = posX;
				for(int x = 0;x < text.length();x++) {
					TextSectionView tView = tSec.getTextSectionView();
					
					// determine char-width
					int cWidth = tView.getCharWidth(x);
					
					// increase the x-position. we go from the middle of the previous
					// section to the middle of the current section
					if(lastWidth > 0)
						pos += lastWidth / 2.0f + cWidth / 2.0f;
					else
						pos += cWidth / 2.0f;
					
					// the cursor is at the beginning of the section
					if(x == 0 && targetX < pos) {
						cursorPos.setValue(sec.getStartPosInEnv());
						return sec.isFirst() ? sec : (ContentSection)sec.getPrev();
					}

					// the cursor is at the current character
					int cNextWidth = 0;
					if(x < text.length() - 1)
						cNextWidth = tView.getCharWidth(x + 1);
					
					if(targetX < pos + (cWidth / 2.0f) + (cNextWidth / 2.0f)) {
						cursorPos.setValue(sec.getStartPosInEnv() + x + 1);
						return sec;
					}
					
					lastWidth = cWidth;
				}

				// the cursor is behind this section
				cursorPos.setValue(sec.getLastCursorPosInEnv());
				return sec;
			}

			posX += width;
		}
		
		// we are behind the line
		if(exact)
			return null;

		// the cursor is behind this line
		cursorPos.setValue(sec.getLastCursorPosInEnv());
		return sec;
	}
	
	/**
	 * sets all paint-positions in this line
	 * 
	 * @param x the x-position (will be changed)
	 * @param y the y-position (will be changed)
	 */
	public void setPaintPositions(MutableInt x,MutableInt y) {
		ContentSection section = (ContentSection)_line.getFirstSection();
		do {
			section.getSectionView().setPaintPosition(x,y);
			
			section = (ContentSection)section.getNext();
		} while(section != null);
	}
	
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
	public void paint(Graphics g,Rectangle paintRect,MutableInt x,int y,boolean showCursor,
			int selStart,int selEnd) {
		int paraStart = _line.getParagraph().getElementStartPos();
		int selStartInPara = selStart - paraStart;
		int selEndInPara = selEnd - paraStart;
		
		Environment env = _line.getParentEnvironment();
		ContentSection currentSec = env.getCurrentSection();
		int currentCursorPos = env.getCurrentCursorPos() - paraStart;
		int cursorX = -1;
		
		// paint the sections in this line
		ContentSection section = (ContentSection)_line.getFirstSection();
		do {
			IContentSectionView secView = section.getSectionView();
			
			// save cursor-pos
			if(showCursor && currentSec != null && section == currentSec)
				cursorX = x.getValue();
			
			// paint the section			
			secView.paint(g,paintRect,x,y,selStartInPara,selEndInPara);
			
			section = (ContentSection)section.getNext();
		} while(section != null);
		
		// paint the cursor
		if(showCursor && cursorX >= 0) {
			g.setColor(Color.BLACK);
			int cursorInSec = currentCursorPos - currentSec.getElementStartPos();
			currentSec.getSectionView().paintRect(g,cursorX,y,cursorInSec,2);
		}
	}
}