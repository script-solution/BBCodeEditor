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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.UIManager;

import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.TextSection;


/**
 * An extended font-implementation which allows subscript and superscript and may support
 * many other text-attributes.<br>
 * Because this version is much slower than the SimpleFont we implement two different
 * versions.
 * 
 * @author hrniels
 */
public class ExtendedFont extends AbstractFont {
	
	/**
	 * the cached section-height
	 */
	private int _sectionHeight = -1;
	
	/**
	 * constructor
	 * 
	 * @param section the section for this font
	 */
	public ExtendedFont(TextSection section) {
		super(section);
	}

	public int getDescent() {
		byte pos = _section.getAttributes().getPosition();
		if(pos == TextAttributes.POS_SUBSCRIPT) {
			Rectangle2D bounds = getCachedStringBounds();
			return (int)(bounds.getY() + bounds.getHeight());
		}
		
		return super.getDescent();
	}

	public int getStringHeight() {
		return _sectionHeight;
	}
	
	int getCharWidth(int index) {
		String substr = _section.getTextSectionView().getPaintText(index,index + 1);
		
		Rectangle2D rect = _fontInfo.getFontMetrics().getStringBounds(
				substr,_section.getTextField().getGraphics());
		return (int)rect.getWidth();
	}

	int paintString(Graphics g,String text,int x,int y,boolean isSelected) {
		if(text.length() == 0)
			return 0;
		
		if(isSelected) {
			Map add = new HashMap();
			Color highlight = (Color)_section.getAttributes().get(TextAttributes.HIGHLIGHT);
			if(highlight == null) {
				add.put(TextAttribute.BACKGROUND,
						UIManager.getColor("FormattedTextField.selectionBackground"));
			}
			
			add.put(TextAttribute.FOREGROUND,
					UIManager.getColor("FormattedTextField.selectionForeground"));
			g.setFont(_fontInfo.getFont().deriveFont(add));
		}
		else
			g.setFont(_fontInfo.getFont());
		
		// paint the background if it is not the default-background
		int width = getStringWidth(text);
		ILineView lView = _section.getSectionLine().getLineView();
		
		// draw the text
		g.drawString(text,x,y + lView.getHeight() - lView.getDescent());
		
		return width;
	}
	
	void refreshFont() {
		super.refreshFont();
		refreshSectionHeight(_fontInfo);
	}

	FontInfo getFontInfo(TextAttributes attr) {
		Map fontAttrs = new HashMap();
		
		// iterate through the attributes of this section
		Iterator it = attr.iterator();
		while(it.hasNext()) {
			Integer key = (Integer)it.next();
			Object value = attr.get(key);
			if(value == null)
				continue;
			
			if(key.equals(TextAttributes.BOLD)) {
				if(value.equals(new Boolean(true)))
					fontAttrs.put(TextAttribute.WEIGHT,TextAttribute.WEIGHT_BOLD);
			}
			else if(key.equals(TextAttributes.ITALIC)) {
				if(value.equals(new Boolean(true)))
					fontAttrs.put(TextAttribute.POSTURE,TextAttribute.POSTURE_OBLIQUE);
			}
			else if(key.equals(TextAttributes.FONT_FAMILY))
				fontAttrs.put(TextAttribute.FAMILY,value);
			else if(key.equals(TextAttributes.FONT_SIZE)) {
				// use float here for JRE 1.5 and below
				fontAttrs.put(TextAttribute.SIZE,new Float(((Integer)value).intValue()));
			}
			else if(key.equals(TextAttributes.UNDERLINE)) {
				if(value.equals(new Boolean(true)))
					fontAttrs.put(TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_ON);
			}
			else if(key.equals(TextAttributes.STRIKE)) {
				if(value.equals(new Boolean(true)))
					fontAttrs.put(TextAttribute.STRIKETHROUGH,TextAttribute.STRIKETHROUGH_ON);
			}
			else if(key.equals(TextAttributes.POSITION)) {
				if(value.equals(new Byte(TextAttributes.POS_SUBSCRIPT)))
					fontAttrs.put(TextAttribute.SUPERSCRIPT,TextAttribute.SUPERSCRIPT_SUB);
				else if(value.equals(new Byte(TextAttributes.POS_SUPERSCRIPT)))
					fontAttrs.put(TextAttribute.SUPERSCRIPT,TextAttribute.SUPERSCRIPT_SUPER);
			}
			else if(key.equals(TextAttributes.URL) || key.equals(TextAttributes.EMAIL)) {
				fontAttrs.put(TextAttribute.FOREGROUND,Color.BLUE);
				fontAttrs.put(TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_ON);
			}
			else if(key.equals(TextAttributes.FONT_COLOR))
				fontAttrs.put(TextAttribute.FOREGROUND,value);
			else if(key.equals(TextAttributes.BG_COLOR) || key.equals(TextAttributes.HIGHLIGHT))
				fontAttrs.put(TextAttribute.BACKGROUND,value);
		}

		Font font = Font.getFont(fontAttrs);
		FontInfo info = new FontInfo(font,_section.getTextField().getFontMetrics(font));
		return info;
	}
	
	/**
	 * refreshes the section-height
	 */
	private void refreshSectionHeight(FontInfo info) {
		Object pos = _section.getAttribute(TextAttributes.POSITION);
		if(pos != null) {
			Map m = new HashMap();
			m.put(TextAttribute.SUPERSCRIPT,new Integer(0));
			Font f = info.getFont().deriveFont(m);
			FontMetrics fm = _section.getTextField().getFontMetrics(f);
			
			Rectangle2D bounds = fm.getStringBounds(_section.getText(),
					_section.getTextField().getGraphics());
			int height = fm.getHeight();
			_sectionHeight = height + (int)(bounds.getY() + bounds.getHeight());
		}
		else
			_sectionHeight = super.getStringHeight();
	}
}