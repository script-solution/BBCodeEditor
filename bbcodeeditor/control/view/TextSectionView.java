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
import java.awt.geom.Rectangle2D;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.ContentSection;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.TextSection;
import bbcodeeditor.control.tools.MutableInt;
import bbcodeeditor.control.tools.StringUtils;


/**
 * The view for textsections
 * 
 * @author hrniels
 */
public class TextSectionView extends ContentSectionView {

	/**
	 * Refresh the text-bounds
	 */
	public static final byte TEXT_BOUNDS			= 1;

	/**
	 * Refresh the font
	 */
	public static final byte FONT							= 2;
	
	/**
	 * Refresh the paint-text
	 */
	public static final byte PAINT_TEXT				= 4;
	
	/**
	 * The last attributes-id
	 */
	private int _lastId = -1;
	
	/**
	 * the font for this section
	 */
	private AbstractFont _font;

	/**
	 * the text of this section to paint
	 * contains tabs replaced by spaces
	 */
	private String _paintText = null;
	
	/**
	 * Constructor
	 * 
	 * @param section the section
	 */
	public TextSectionView(TextSection section) {
		super(section);
	}
	
	/**
	 * Forces a refresh of the given type and informs the manager if you want to
	 * 
	 * @param type the refresh-type
	 * @param informManager inform the manager?
	 */
	public void forceRefresh(byte type,boolean informManager) {
		if(!informManager)
			_forceRefresh |= type;
		else
			forceRefresh(type);
	}
	
	public void forceRefresh(byte type) {
		super.forceRefresh(type);
		
		// does the refresh affect the line-height?
		if(type == FONT || type == TEXT_BOUNDS)
			_section.getSectionLine().getView().forceRefresh(ILineView.LINE_HEIGHT);
	}
	
	public void refresh() {
		boolean refreshPaintText = shouldRefresh(PAINT_TEXT);
		
		if(refreshPaintText)
			refreshPaintText();
		if(shouldRefresh(FONT))
			refreshFont();
		
		// check this here because we want to wait for refreshFont()
		// which regenerates the id of the attributes, if necessary
		int id = ((TextSection)_section).getAttributes().getId();
		boolean potBoundsChange = _lastId != id || refreshPaintText;
		_lastId = id;
		if(potBoundsChange && shouldRefresh(TEXT_BOUNDS))
			refreshTextBounds();
		
		clearRefreshes();
	}
	
	public AbstractTextField getTextField() {
		return _section.getTextField();
	}
	
	public int getSectionHeight() {
		return _font.getStringHeight();
	}
	
	public int getSectionWidth() {
		return _font.getTotalStringWidth();
	}
	
	/**
	 * the width of the section-text starting at <code>start</code> and given length
	 * 
	 * @param start the start-position in the text
	 * @param length the length of the text
	 * @return the width of the section-text starting at <code>start</code> and given length
	 */
	public int getStringWidth(int start,int length) {
		return _font.getStringWidth(start,length);
	}
	
	public int getCharWidth() {
		return _font.getCharWidth(' ');
	}
	
	/**
	 * the width of the character at given position
	 * 
	 * @param index the index of the character in this section
	 * @return the width of the character at given position
	 */
	public int getCharWidth(int index) {
		return _font.getCharWidth(index);
	}
	
	public int getDescent() {
		return _font.getDescent();
	}
	
	/**
	 * @return the bounds of the section
	 */
	public Rectangle2D getBounds() {
		if(_font == null)
			return null;
		
		return _font.getCachedStringBounds();
	}
	
	/**
	 * this supports tabs
	 * 
	 * @return the paint-text
	 */
	public String getPaintText() {
		if(_paintText != null)
			return _paintText;
		
		return ((TextSection)_section).getText();
	}
	
	/**
	 * returns a substring from start to the end of the section in the paint-text
	 * this supports tabs
	 * 
	 * @param start the start-position
	 * @return the substring
	 */
	String getPaintText(int start) {
		return getPaintText(start,
				_section.getElementEndPos() - _section.getElementStartPos() + 1);
	}
	
	/**
	 * returns a substring from start to end in the paint-text
	 * this supports tabs
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the substring
	 */
	String getPaintText(int start,int end) {
		// we may get exceptions if the control wants to repaint and the cursor-position
		// is not correct yet
		if(start < 0 || end < 0 || end > _section.getElementLength() || start > end)
			return "";

		String text = ((TextSection)_section).getText();
		if(_paintText == null)
			return text.substring(start,end);
		
		int posInLine = getPaintPositionInLine();
		int tabWidth = _section.getTextField().getTabWidth();
		int paintStart = start;
		int paintEnd = end;
		for(int i = 0;i < end;i++) {
			char c = text.charAt(i);
			if(c == '\t') {
				int inc = tabWidth - (posInLine % tabWidth) - 1;
				if(i < start)
					paintStart += inc;
				paintEnd += inc;
				posInLine += inc + 1;
			}
			else
				posInLine++;
		}
		
		return _paintText.substring(paintStart,paintEnd);
	}
	
	/**
	 * refreshs the paint-text
	 * 
	 * @return true if something has changed
	 */
	private boolean refreshPaintText() {
		String old = _paintText;
		String text = ((TextSection)_section).getText();
		if(text.indexOf("\t") >= 0) {
			int posInLine = getPaintPositionInLine();
			int tabWidth = _section.getTextField().getTabWidth();
			
			// replace the tabs by the corresponding number of spaces depending on the position
			// of the tab in the line
			
			StringBuffer paintText = new StringBuffer();
			for(int i = 0,len = text.length();i < len;i++) {
				char c = text.charAt(i);
				if(c == '\t') {
					int spaces = tabWidth - (posInLine % tabWidth);
					paintText.append(StringUtils.repeat(' ',spaces));
					posInLine += spaces;
				}
				else {
					paintText.append(c);
					posInLine++;
				}
			}

			_paintText = paintText.toString();
		}
		else
			_paintText = null;
		
		// has the paint-text changed?
		return (_paintText == null && old != null) || (_paintText != null && old == null) ||
						(_paintText != null && old != null && !_paintText.equals(old));
	}
	
	/**
	 * refreshes the text bounds of this TextSection
	 */
	private void refreshTextBounds() {
		_font.refreshTextBounds();
	}
	
	/**
	 * refreshes the font and recalculates the line-height
	 */
	void refreshFont() {
		TextAttributes attributes = ((TextSection)_section).getAttributes();
		boolean extended = attributes.isSet(TextAttributes.POSITION);
		if(extended && !(_font instanceof ExtendedFont))
			_font = new ExtendedFont((TextSection)_section);
		else if(!extended && !(_font instanceof SimpleFont))
			_font = new SimpleFont((TextSection)_section);
		
		_font.refreshFont();
	}
	
	/**
	 * determines the position in this line in the paint-text<br>
	 * This will be used to determine the tab-size depending on the position in the line
	 * 
	 * @return the position
	 */
	private int getPaintPositionInLine() {
		int posInLine = 0;
		ContentSection sec = (ContentSection)_section.getSectionLine().getFirstSection();
		while(sec != null && sec != _section) {
			if(sec instanceof TextSection)
				posInLine += ((TextSection)sec).getTextSectionView().getPaintText().length();
			else
				posInLine++;
			
			sec = (ContentSection)sec.getNext();
		}
		
		return posInLine;
	}
	
	public void setPaintPosition(MutableInt x,MutableInt y) {
		if(_font == null)
			refreshFont();
		
		setPaintPos(new Point(x.getValue(),y.getValue()));
		int width = _font.getTotalStringWidth();
		x.increaseValue(width);
	}
	
	public void paintRect(Graphics g,int x,int y,int cursorPos,int width) {
		String text = getPaintText(0,cursorPos);
		Rectangle2D rect = _font.getStringBounds(text,g);
		ILineView lView = _section.getSectionLine().getLineView();
		
		g.fillRect(
				x + (int)rect.getWidth(),
				y + lView.getHeight() - lView.getDescent() + (int)rect.getY(),
				width,
				(int)rect.getHeight()
		);
	}

	public void paint(Graphics g,Rectangle paintRect,MutableInt x,int y,int selStart,
			int selEnd) {
		int width = _font.getTotalStringWidth();
		int saveX = x.getValue();
		int startPos = _section.getElementStartPos();
		int endPos = _section.getElementEndPos();
		
		// break here if this section isn't visible.
		if(!isPaintingRequired(paintRect,saveX,y,width + 1)) {
			x.increaseValue(width);
			return;
		}
		
		int start = Math.max(selStart,startPos);
		int end = Math.min(selEnd,endPos + 1);
		
		// if nothing is selected or the selection does not affect this section paint
		// the text 'normal'
		if((selStart == -1 && selEnd == -1) || end < startPos || start > endPos) {
			_font.paintString(g,getPaintText(),saveX,y,false);
			x.increaseValue(width);
		}
		else {
			// if the selection starts not at the beginning of the section, paint the text in
			// front of the selection 'normal'
			if(start > startPos) {
				String sfront = getPaintText(0,start - startPos);
				int frontWidth = _font.paintString(g,sfront,x.getValue(),y,false);
				x.increaseValue(frontWidth);
			}
			
			// paint the selected text
			String smiddle = getPaintText(start - startPos,end - startPos);
			int middleWidth = _font.paintString(g,smiddle,x.getValue(),y,true);
			x.increaseValue(middleWidth);
			
			// paint the text after the selection 'normal' if existing
			if(end - 1 < endPos) {
				String send = getPaintText(end - startPos);
				int endWidth = _font.paintString(g,send,x.getValue(),y,false);
				x.increaseValue(endWidth);
			}
		}
	}
}