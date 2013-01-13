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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.UIManager;

import bbcodeeditor.control.Environment;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.TextSection;


/**
 * A simple font-implementation. supports the basic text-attributes.<br>
 * This version is much faster than ExtendedFont. Therefore we use this one if it is
 * possible and if we need some special text-attributes we can use the extended version
 * 
 * @author hrniels
 */
public final class SimpleFont extends AbstractFont {
	
	/**
	 * constructor
	 * 
	 * @param section the section for this font
	 */
	public SimpleFont(TextSection section) {
		super(section);
	}

	int paintString(Graphics g,String text,int x,int y,boolean isSelected) {
		if(text.length() == 0)
			return 0;
		
		TextAttributes attributes = _section.getAttributes();
		IEnvironmentView envView = _section.getParentEnvironment().getEnvView();
		
		//boolean isLink = attributes.isSet(TextAttributes.URL) || attributes.isSet(TextAttributes.EMAIL);
		boolean isLink = attributes.get(TextAttributes.URL) != null ||
			attributes.get(TextAttributes.EMAIL) != null;
		
		// determine colors
		Color foreGround = getForeground(attributes,envView,isLink);
		Color backGround = getBackground(attributes,envView);
		if(isSelected) {
			foreGround = UIManager.getColor("FormattedTextField.selectionForeground");
			
			Color highlight = (Color)attributes.get(TextAttributes.HIGHLIGHT);
			if(highlight != null)
				backGround = highlight;
			else
				backGround = UIManager.getColor("FormattedTextField.selectionBackground");
		}
		
		g.setFont(_fontInfo.getFont());
		
		// grab some infos
		int width = getStringWidth(text);
		int height = getStringHeight();
		ILineView lView = _section.getSectionLine().getLineView();
		int descent = lView.getDescent();
		int lineHeight = lView.getHeight();
		
		// paint background
		if(backGround != null && !backGround.equals(_section.getTextField().getBackground())) {
			g.setColor(backGround);
			Rectangle2D rect = getCachedStringBounds();
			g.fillRect(
					x,
					y + lineHeight - descent + (int)rect.getY(),
					width,
					(int)rect.getHeight()
			);
		}
		
		// draw the text
		g.setColor(foreGround);
		g.drawString(text,x,y + lineHeight - descent);
		
		// draw line under the text
		if(attributes.isUnderline() || isLink) {
			int ulHeight = Math.max(1,height / 12);
			Boolean bold = (Boolean)attributes.get(TextAttributes.BOLD);
			if(bold != null && bold.equals(new Boolean(true)))
				ulHeight *= 2;
			
			g.fillRect(x,y + lineHeight - descent + 1,width,ulHeight);
		}
		
		// draw line over the text
		if(attributes.isStrike()) {
			int yPos = y + lineHeight - descent - height / 2 + getDescent();
			g.drawLine(x,yPos,x + width,yPos);
		}
		
		return width;
	}

	/**
	 * Determines the background color that should be used
	 * 
	 * @param attr the attributes
	 * @param envView the EnvironmentView
	 * @return the color
	 */
	private Color getBackground(TextAttributes attr,IEnvironmentView envView) {
		Color hl = attr.getHighlight();
		if(hl != null)
			return hl;
		
		return attr.getBgColor();
	}
	
	/**
	 * Determines the foreground color that should be used
	 * 
	 * @param attr the attributes
	 * @param envView the EnvironmentView
	 * @param isLink is it a link?
	 * @return the color
	 */
	private Color getForeground(TextAttributes attr,IEnvironmentView envView,boolean isLink) {
		if(isLink)
			return Color.BLUE;
		
		Color c = attr.getFontColor();
		if(c == null)
			return envView.getDefaultFontColor();
		
		return c;
	}

	FontInfo getFontInfo(TextAttributes attr) {
		// determine styles
		int style = Font.PLAIN;
		if(attr.isBold())
			style |= Font.BOLD;
		if(attr.isItalic())
			style |= Font.ITALIC;
		int fontSize = attr.getFontSize();
		String fontFamily = attr.getFontFamily();
		Font font = new Font(fontFamily,style,fontSize);
		return new FontInfo(font,_section.getTextField().getFontMetrics(font));
	}
}