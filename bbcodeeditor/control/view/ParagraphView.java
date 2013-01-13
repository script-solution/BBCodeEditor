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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;

import javax.swing.UIManager;

import bbcodeeditor.control.*;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The view for a paragraph
 * 
 * @author hrniels
 */
public class ParagraphView extends View implements IParagraphView {
	
	/**
	 * The Paragraph-instance
	 */
	protected final Paragraph _p;
	
	/**
	 * Constructor
	 * 
	 * @param p the paragraph
	 */
	public ParagraphView(Paragraph p) {
		_p = p;
	}
	
	public void refresh() {
		clearRefreshes();
	}
	
	public AbstractTextField getTextField() {
		return _p.getTextField();
	}
	
	public Point getPaintPos() {
		return _p.getFirstSection().getView().getPaintPos();
	}
	
	public int getHeight() {
		int height = 0;
		Line l = _p.getFirstLine();
		do {
			height += ((ILineView)l.getView()).getHeight();
			
			l = (Line)l.getNext();
		} while(l != null);
		
		return height;
	}

	public void refreshTabWidth() {
		Section s = _p.getFirstSection();
		
		// do we have an environment?
		if(s instanceof Environment) {
			Environment es = (Environment)s;
			es.getEnvView().refreshTabWidth();
			return;
		}
		
		// refresh the paint-text and total width in all sections
		while(s != null) {
			if(s instanceof TextSection) {
				TextSection ts = (TextSection)s;
				ts.getView().forceRefresh(TextSectionView.PAINT_TEXT);
			}
			
			s = s.getNextInParagraph();
		}
	}
	
	public void refreshFonts() {
		if(_p.containsEnvironment()) {
			((Environment)_p.getFirstSection()).getEnvView().refreshFonts();
			return;
		}
		
		Line l = _p.getFirstLine();
		do {
			l.getLineView().refreshFonts();
			
			l = (Line)l.getNext();
		} while(l != null);
	}

	public Line getLineAtPixelPosition(int y,boolean exact) {
		int index = getIndexOfLineAtPixelPos(y);
		if(index < 0 && exact)
			return null;
		
		if(index == -1)
			return _p.getLastLine();
		if(index == -2)
			return _p.getFirstLine();
		
		return _p.getLine(index);
	}
	
	public int getIndexOfLineAtPixelPos(int y) {
		Line first = _p.getFirstLine();
		Point firstPP = first.getView().getPaintPos();
		// in front of the first line?
		if(firstPP == null || y <= firstPP.y)
			return -2;
		
		// the lines are sorted, so we can use binarySearch :)
		int index = _p.getLines().getIndexBinarySearch(new Integer(y),new Comparator() {
			public int compare(Object arg0,Object arg1) {
				if(arg0 instanceof Line && arg1 instanceof Integer) {
					int pos = ((Integer)arg1).intValue();
					View l = ((Line)arg0).getView();
					Point paintPos = l.getPaintPos();
					if(paintPos == null)
						return 1;
					
					if(pos < paintPos.y)
						return 1;
					
					if(pos > paintPos.y + ((ILineView)l).getHeight())
						return -1;
				}
				
				return 0;
			}
		});
		
		if(index >= 0)
			return index;
		
		return -1;
	}

	public void setPaintPositions(Graphics g,MutableInt x,MutableInt y,MutableInt maxWidth) {
		int saveX = x.getValue();
		
		// do we have an environment?
		if(_p.containsEnvironment()) {
			Section first = _p.getFirstSection();
			((Environment)first).getEnvView().setPaintPositions(g,x,y,maxWidth);
			
			// restore the x-position
			x.setValue(saveX);
		}
		else {
			IEnvironmentView envView = _p.getParentEnvironment().getEnvView();
			int align = _p.getHorizontalAlignment();
			int envWidth = 0;
			int textStart = envView.getGlobalTextStart();
			
			switch(align) {
				case ParagraphAttributes.ALIGN_RIGHT:
				case ParagraphAttributes.ALIGN_CENTER:
					envWidth = envView.getTotalWidth() - envView.getInnerLeftPadding() -
										 envView.getInnerRightPadding();
					break;
			}
			
			// go through all lines
			int lineWidth = 0;
			Line l = _p.getFirstLine();
			ILineView lView = l.getLineView();
			do {
				switch(align) {
					case ParagraphAttributes.ALIGN_CENTER:
						lineWidth = lView.getPixelWidth();
						x.increaseValue((envWidth - lineWidth) / 2);
						break;
					case ParagraphAttributes.ALIGN_RIGHT:
						lineWidth = lView.getPixelWidth();
						x.increaseValue(envWidth - lineWidth);
						break;
				}
				
				// set paint-positions in line
				lView.setPaintPositions(x,y);
				
				// determine the line-width and store it th maxWidth if necessary
				int totalWidth = 0;
				switch(align) {
					case ParagraphAttributes.ALIGN_LEFT:
						totalWidth = x.getValue() + textStart;
						break;
					case ParagraphAttributes.ALIGN_CENTER:
					case ParagraphAttributes.ALIGN_RIGHT:
						totalWidth = lineWidth + textStart * 2;
						break;
				}

				if(totalWidth > maxWidth.getValue())
					maxWidth.setValue(totalWidth);
				
				// change x,y for the next line
				y.increaseValue(lView.getHeight());
				x.setValue(saveX);
				
				l = (Line)l.getNext();
				if(l != null)
					lView = l.getLineView();
			} while(l != null);
		}
	}
	
	public void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd) {
		int saveX = x.getValue();
		
		if(_p.containsEnvironment()) {
			IEnvironmentView envView = ((Environment)_p.getFirstSection()).getEnvView();
			envView.paint(g,paintRect,x,y,showCursor,selStart - _p.getElementStartPos(),
					selEnd - _p.getElementStartPos());
			
			// restore the x-position
			x.setValue(saveX);
		}
		else {
			IEnvironmentView envView = _p.getParentEnvironment().getEnvView();
			int align = _p.getHorizontalAlignment();
			int envWidth = 0;
			switch(align) {
				case ParagraphAttributes.ALIGN_RIGHT:
				case ParagraphAttributes.ALIGN_CENTER:
					envWidth = envView.getTotalWidth() - envView.getInnerLeftPadding() -
										 envView.getInnerRightPadding();
					break;
			}

			// go through all lines
			int lineWidth = 0;
			Line l = _p.getFirstLine();
			ILineView lView = l.getLineView();
			do {
				switch(align) {
					case ParagraphAttributes.ALIGN_CENTER:
						lineWidth = lView.getPixelWidth();
						x.increaseValue((envWidth - lineWidth) / 2);
						break;
					case ParagraphAttributes.ALIGN_RIGHT:
						lineWidth = lView.getPixelWidth();
						x.increaseValue(envWidth - lineWidth);
						break;
				}
				
				// paint the line
				lView.paint(g,paintRect,x,y.getValue(),showCursor,selStart,selEnd);
				
				// highlight the line-wrap if necessary
				if(l.isLast()) {
					if(selStart != -1 || selEnd != -1) {
						ContentSection lastSec = (ContentSection)l.getLastSection();
						if(selStart <= lastSec.getEndPosInEnv() + 1 &&
								selEnd > lastSec.getEndPosInEnv() + 1) {
							int width = lastSec.getSectionView().getCharWidth();
							g.setColor(UIManager.getColor("FormattedTextField.selectionBackground"));
							Point paintPos = lastSec.getView().getPaintPos();
							lastSec.getSectionView().paintRect(g,paintPos.x,y.getValue(),
								lastSec.getElementLength(),width);
						}
					}
				}
				
				// change x,y for the next line
				y.increaseValue(lView.getHeight());
				x.setValue(saveX);
				
				l = (Line)l.getNext();
				if(l != null)
					lView = l.getLineView();
			} while(l != null);
		}
	}
}