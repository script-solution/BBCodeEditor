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
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import bbcodeeditor.control.*;
import bbcodeeditor.control.tools.MutableInt;
import bbcodeeditor.control.tools.RomanNumerals;


/**
 * The view for list-environments
 * 
 * @author hrniels
 */
public class ListEnvironmentView extends EnvironmentView {
	
	/**
	 * Refresh the list-cache
	 */
	public static final byte LIST_CACHE		= 8;
	
	/**
	 * the layer of this list-environment (start with 1)<br>
	 * (counts just the list-envs)
	 */
	private final int _listLayer;

	/**
	 * a map for all list-numbers in this environment<br>
	 * This is required if the list-point depends on the number of the point
	 * because we don't paint everything everytime.
	 */
	private Map _listNumberCache = new HashMap();

	/**
	 * Constructor
	 * 
	 * @param env the environment
	 */
	public ListEnvironmentView(Environment env) {
		super(env);
		
		_listLayer = Math.min(3,getListLayer());
	}
	
	public void refresh() {
		if(shouldRefresh(LIST_CACHE))
			_variableLeftPadding = cacheListNumbers(_env.getTextField().getGraphics());
		
		super.refresh();
	}
	
	public int getHeight() {
		int height = getOuterPadding() * 2;
		return height + super.getHeight();
	}
	
	public void setPaintPositions(Graphics g,MutableInt x,MutableInt y,MutableInt maxWidth) {
		setPaintPos(new Point(x.getValue(),y.getValue()));
		
		_variableLeftPadding = cacheListNumbers(g);
		
		int leftPadding = getInnerLeftPadding();
		int outerPadding = getOuterPadding();
		x.increaseValue(outerPadding);
		y.increaseValue(outerPadding + getInnerTopPadding());
		
		Paragraph p = _env.getFirstParagraph();
		do {
			x.increaseValue(leftPadding);
			p.getParagraphView().setPaintPositions(g,x,y,maxWidth);
			x.decreaseValue(leftPadding);
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		y.increaseValue(outerPadding + getInnerBottomPadding());
	}
	
	public void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd) {
		int outerPadding = getOuterPadding();
		int leftPadding = getInnerLeftPadding();
		x.increaseValue(outerPadding);
		y.increaseValue(outerPadding);
		
		// paint background
		g.setColor(getBackgroundColor());
		g.fillRect(x.getValue(),y.getValue(),getTotalWidth(),
				getHeight() - outerPadding - getInnerBottomPadding());
		
		y.increaseValue(getInnerTopPadding());
		
		Paragraph p = getParagraphAtPixelPosition(paintRect.y);
		if(p.getView().getPaintPos() != null)
			y.setValue(p.getView().getPaintPos().y);
		
		// determine the point-number of the first line
		int pointNum = 0;
		Paragraph tp = p;
		do {
			if(tp.isListPoint())
				pointNum++;
				
			tp = (Paragraph)tp.getPrev();
		} while(tp != null);
		
		// if the first paragraph is no list-point we have to increment the pointNum
		// because the next list-point-paragraph has the number pointNum + 1
		if(!p.isListPoint())
			pointNum++;
		
		// paint the lines
		do {
			Point point = p.getView().getPaintPos();
			if(point != null && paintRect.y + paintRect.height < point.y)
				break;
		
			int saveY = y.getValue();
			x.increaseValue(leftPadding);
			p.getParagraphView().paint(g,paintRect,x,y,showCursor,selStart,selEnd);
			x.decreaseValue(leftPadding);
			
			if(p.isListPoint()) {
				Line firstInEnv;
				if(p.containsEnvironment())
					firstInEnv = ((Environment)p.getFirstSection()).getFirstLine();
				else
					firstInEnv = p.getFirstLine();
				
				int xStart = x.getValue() + 4;
				if(paintRect.intersects(new Rectangle(xStart,saveY,_variableLeftPadding,
						firstInEnv.getLineView().getHeight()))) {
					g.setColor(Color.BLACK);
					paintListPoint(g,xStart,saveY,pointNum,firstInEnv);
				}
				
				pointNum++;
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		y.increaseValue(outerPadding + getInnerBottomPadding());
	}
	
	/**
	 * determines the number of list-points
	 * 
	 * @return the number of points
	 */
	private int getNumberOfListPoints() {
		int i = 0;
		Paragraph p = _env.getFirstParagraph();
		do {
			if(p.isListPoint())
				i++;
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		return i;
	}
	
	/**
	 * caches all list-numbers and calculates the required indent
	 * 
	 * @param g the graphics-context
	 * @return the required indent
	 */
	private int cacheListNumbers(Graphics g) {
		if(g == null)
			return 0;
		
		_listNumberCache.clear();
		
		int listType = ((ListEnvironment)_env).getListType();
		int maxNum,maxIndent;
		switch(listType) {
			case ListTypes.TYPE_SQUARE:
			case ListTypes.TYPE_DISC:
			case ListTypes.TYPE_CIRCLE:
			case ListTypes.TYPE_DEFAULT:
				return 20;
				
			case ListTypes.TYPE_ALPHA_B:
			case ListTypes.TYPE_ALPHA_S:
			case ListTypes.TYPE_ROMAN_B:
			case ListTypes.TYPE_ROMAN_S:
				maxIndent = 0;
				maxNum = getNumberOfListPoints();
				for(int i = 1;i <= maxNum;i++) {
					String sText;
					if(listType == ListTypes.TYPE_ROMAN_B ||
							listType == ListTypes.TYPE_ROMAN_S)
						sText = RomanNumerals.intToRoman(i);
					else
						sText = getAlphaStr(i);
					
					if(listType == ListTypes.TYPE_ALPHA_S ||
							listType == ListTypes.TYPE_ROMAN_S)
						sText = sText.toLowerCase();
					
					_listNumberCache.put(new Integer(i),sText);
					
					FontMetrics fm = getDefaultFontMetrics();
					Rectangle2D rect = fm.getStringBounds(sText,g);
					int indent = (int)rect.getWidth() + 10;
					if(indent > maxIndent)
						maxIndent = indent;
				}

				return maxIndent;
				
			case ListTypes.TYPE_NUM:
				maxIndent = 0;
				maxNum = getNumberOfListPoints();
				for(int i = 1;i <= maxNum;i++) {
					String sText = String.valueOf(i) + ".";
					
					FontMetrics fm = getDefaultFontMetrics();
					Rectangle2D rect = fm.getStringBounds(sText,g);
					int indent = (int)rect.getWidth() + 10;
					if(indent > maxIndent)
						maxIndent = indent;
				}

				return maxIndent;
		}
		
		return 0;
	}
	
	/**
	 * paints the list-point at the given position
	 * 
	 * @param g the graphics context
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param number the number of the list-point
	 * @param l the line-object
	 */
	private void paintListPoint(Graphics g,int x,int y,int number,Line l) {
		ILineView lView = l.getLineView();
		switch(((ListEnvironment)_env).getListType()) {
			case ListTypes.TYPE_DEFAULT:
				y += (lView.getHeight() / 2) - lView.getDescent();
				switch(_listLayer) {
					case 1:
						g.fillOval(x,y,7,7);
						break;
						
					case 2:
						g.drawOval(x,y,6,6);
						break;
					
					case 3:
						g.fillRect(x,y,6,6);
						break;
				}
				break;
				
			case ListTypes.TYPE_CIRCLE:
				g.fillOval(x,y + (lView.getHeight() / 2) - lView.getDescent(),7,7);
				break;
				
			case ListTypes.TYPE_DISC:
				g.drawOval(x,y + (lView.getHeight() / 2) - lView.getDescent(),6,6);
				break;
				
			case ListTypes.TYPE_SQUARE:
				g.fillRect(x,y + (lView.getHeight() / 2) - lView.getDescent(),6,6);
				break;
				
			case ListTypes.TYPE_ALPHA_B:
			case ListTypes.TYPE_ALPHA_S:
			case ListTypes.TYPE_ROMAN_B:
			case ListTypes.TYPE_ROMAN_S:
				String listPoint = (String)_listNumberCache.get(new Integer(number));
				if(listPoint != null) {
					g.setFont(getDefaultFont());
					g.drawString(listPoint,x,y + lView.getHeight() - lView.getDescent());
				}
				break;
				
			case ListTypes.TYPE_NUM:
				g.setFont(getDefaultFont());
				g.drawString(String.valueOf(number) + ".",x,y + lView.getHeight() - lView.getDescent());
				break;
		}
	}
	
	/**
	 * calculates the representation in alpha-chars of the given number
	 * (in upper case!)
	 * 
	 * @param number the number to display
	 * @return the alpha-representation
	 */
	private String getAlphaStr(int number) {
		StringBuffer text = new StringBuffer();
		int rem;
		do {
			number--;
			rem = number % 26;
			text.insert(0,(char)('A' + rem));
			number /= 26;
		} while(number > 0);
		
		text.append('.');
		return text.toString();
	}
	
	/**
	 * determines the layer of this list-environment
	 * 
	 * @return the layer (1...n)
	 */
	private int getListLayer() {
		int count = 1;
		Environment parent = _env;
		while(parent.getParentEnvironment() != null) {
			parent = parent.getParentEnvironment();
			if(parent instanceof ListEnvironment)
				count++;
		}
		return count;
	}
}