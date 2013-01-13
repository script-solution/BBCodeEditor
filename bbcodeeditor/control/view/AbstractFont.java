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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import bbcodeeditor.control.Environment;
import bbcodeeditor.control.Paragraph;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.TextSection;


/**
 * the interface for a font.
 * This may be a simple one or an extended one.
 * 
 * @author hrniels
 */
public abstract class AbstractFont {
	
	/**
	 * the section for this font
	 */
	protected final TextSection _section;
	
	/**
	 * Contains the Font- and FontMetrics-object we want to use for painting
	 */
	protected FontInfo _fontInfo;
	
	/**
	 * the cached rectangle of this section
	 * has to be refreshed as soon as the content or the attributes change
	 */
	protected Rectangle2D _textBounds = null;
	
	/**
	 * The last id of the attributes. This is used to notice changes
	 */
	private int _lastId = -1;
	
	/**
	 * constructor
	 * 
	 * @param section the section for this font
	 */
	public AbstractFont(TextSection section) {
		_section = section;
	}
	
	public void finalize() {
		if(_fontInfo != null) {
			int id = _section.getAttributes().getId();
			_section.getTextField().getFontCache().removeFont(id,_fontInfo);
		}
	}
	
	/**
	 * the ascent of this font
	 * 
	 * @return the ascent of this font
	 */
	public int getAscent() {
		return _fontInfo.getFontMetrics().getAscent();
	}
	
	/**
	 * the descent of this font
	 * 
	 * @return the descent of this font
	 */
	public int getDescent() {
		return _fontInfo.getFontMetrics().getMaxDescent();
	}
	
	/**
	 * the height of the string
	 * 
	 * @return the height of the string
	 */
	public int getStringHeight() {
		return _fontInfo.getFontMetrics().getHeight();
	}
	
	/**
	 * the total length of the section-text in pixel
	 * 
	 * @return total pixel length of the section-text
	 */
	public int getTotalStringWidth() {
		if(_textBounds == null)
			refreshTextBounds();
		
		return (int)_textBounds.getWidth();
	}
	
	/**
	 * the width of given character
	 * 
	 * @param c the character to measure
	 * @return the width of the given character
	 */
	int getCharWidth(char c) {
		return _fontInfo.getFontMetrics().charWidth(c);
	}
	
	/**
	 * the width of character at the given position in the section-text
	 * 
	 * @param index the index of the character in the text
	 * @return the width of character at the given position in the section-text
	 */
	int getCharWidth(int index) {
		char c = _section.getCharAt(index);
		if(c == '\t') {
			String substr = _section.getTextSectionView().getPaintText(index,index + 1);
			return _fontInfo.getFontMetrics().stringWidth(substr);
		}
		
		return _fontInfo.getFontMetrics().charWidth(c);
	}
	
	/**
	 * the width of the given string
	 * 
	 * @param str the input-string
	 * @return the width of the given string
	 */
	int getStringWidth(String str) {
		if(str.equals(_section.getTextSectionView().getPaintText()))
			return getTotalStringWidth();
		
		Rectangle2D rect = _fontInfo.getFontMetrics().getStringBounds(
				str,_section.getTextField().getGraphics());
		return (int)rect.getWidth();
	}
	
	/**
	 * the width of the section-text starting at <code>start</code>
	 * 
	 * @param start the start-position in the text
	 * @return the width of the section-text starting at <code>start</code>
	 */
	int getStringWidth(int start) {
		if(start == 0)
			return getTotalStringWidth();
		
		Rectangle2D rect = _fontInfo.getFontMetrics().getStringBounds(
				_section.getTextSectionView().getPaintText(start),_section.getTextField().getGraphics());
		return (int)rect.getWidth();
	}
	
	/**
	 * the width of the section-text starting at <code>start</code> and given length
	 * 
	 * @param start the start-position in the text
	 * @param length the length of the text
	 * @return the width of the section-text starting at <code>start</code> and given length
	 */
	int getStringWidth(int start,int length) {
		if(length == _section.getElementLength())
			return getTotalStringWidth();

		if(length == 0)
			return 0;
		
		Rectangle2D rect = _fontInfo.getFontMetrics().getStringBounds(
				_section.getTextSectionView().getPaintText(start,start + length),
				_section.getTextField().getGraphics());
		return (int)rect.getWidth();
	}
	
	/**
	 * @return the cached string bounds
	 */
	Rectangle2D getCachedStringBounds() {
		if(_textBounds == null)
			refreshTextBounds();
		
		return _textBounds;
	}
	
	/**
	 * @param str the string
	 * @param g the graphics
	 * @return the bounds of the given string
	 */
	Rectangle2D getStringBounds(String str,Graphics g) {
		if(str.equals(_section.getTextSectionView().getPaintText())) {
			if(_textBounds == null)
				refreshTextBounds();
			
			return _textBounds;
		}
		
		return _fontInfo.getFontMetrics().getStringBounds(str,g);
	}
	
	/**
	 * refreshs the text bounds of the section
	 */
	void refreshTextBounds() {
		String text = _section.getTextSectionView().getPaintText();
		_textBounds = _fontInfo.getFontMetrics().getStringBounds(
				text,_section.getTextField().getGraphics());
			
		// we have to refresh the paragraph of the section
		Paragraph p = _section.getSectionParagraph();
		if(_section.getTextField().getPaintPosManager() != null)
			_section.getTextField().getPaintPosManager().addParagraph(p);
	}
	
	/**
	 * Should create the FontInfo-object for the given attributes.
	 * 
	 * @param attr the attributes
	 * @return the FontInfo-object
	 */
	abstract FontInfo getFontInfo(TextAttributes attr);
	
	/**
	 * Refreshes the font<br>
	 * This should be called if an attribute has changed
	 */
	void refreshFont() {
		// regenerate attributes-id
		TextAttributes oattr = _section.getAttributes();
		if(oattr.idNeedsRefresh())
			oattr.regenerateId();
		
		TextAttributes attr = _section.getCloneOfAttributes();
		_applyEnv(attr);
		
		// regenerate the id of the clone if necessary
		if(attr.idNeedsRefresh())
			attr.regenerateId();

		// has the id not changed?
		if(_lastId == attr.getId())
			return;
		
		FontCache fc = _section.getTextField().getFontCache();
		
		// if the fontInfo is set we want to remove it or at least decrement the
		// number of references
		if(_fontInfo != null)
			fc.removeFont(_lastId,_fontInfo);
		
		// check if a font for our attributes exists
		FontInfo info = fc.getFont(attr.getId());
		if(info == null) {
			// ok, generate it and add it to the cache
			_fontInfo = getFontInfo(attr);
			if(_fontInfo != null)
				fc.announceFont(attr.getId(),_fontInfo);
		}
		else {
			// ok, use it and increment the references
			info.increaseReferences();
			_fontInfo = info;
		}
		
		// store id
		_lastId = attr.getId();
	}
	
	/**
	 * Applies the Environment-default-style to the given attributes
	 * 
	 * @param attr the attributes
	 */
	private void _applyEnv(TextAttributes attr) {
		Environment env = _section.getParentEnvironment();
		// if there is no parent-environment we don't need to do this
		if(env == null)
			return;
		
		IEnvironmentView envView = env.getEnvView();
		int style = envView.getDefaultFontStyle();
		
		if(!attr.isSet(TextAttributes.FONT_SIZE))
			attr.set(TextAttributes.FONT_SIZE,Integer.valueOf(envView.getDefaultFontSize()));
		if(!attr.isSet(TextAttributes.BOLD))
			attr.set(TextAttributes.BOLD,Boolean.valueOf((style & Font.BOLD) != 0));
		if(!attr.isSet(TextAttributes.ITALIC))
			attr.set(TextAttributes.ITALIC,Boolean.valueOf((style & Font.ITALIC) != 0));
		if(!attr.isSet(TextAttributes.FONT_FAMILY))
			attr.set(TextAttributes.FONT_FAMILY,envView.getDefaultFontFamily());
		if(!attr.isSet(TextAttributes.FONT_COLOR))
			attr.set(TextAttributes.FONT_COLOR,envView.getDefaultFontColor());
	}

	/**
	 * paints the given string at the given position
	 * 
	 * @param g the graphics-object
	 * @param text the text to draw
	 * @param x the x-position of the text
	 * @param y the y-position of the text
	 * @param isSelected should the text be painted as selected?
	 * @return the width of the painted string
	 */
	abstract int paintString(Graphics g,String text,int x,int y,boolean isSelected);
}