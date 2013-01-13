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
import java.util.Comparator;

import bbcodeeditor.control.*;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The default view for the Environment
 * 
 * @author hrniels
 */
public class EnvironmentView extends View implements IEnvironmentView,
		EnvironmentProperties.PropertyListener {
	
	/**
	 * Refresh the paddings
	 */
	public static final byte ENV_PROPERTIES		= 16;
	
	/**
	 * Refresh the default font
	 */
	public static final byte DEF_FONT	= 32;
	
	/**
	 * The Environment-instance
	 */
	protected final Environment _env;
	
	/**
	 * The default font-info for this environment
	 */
	protected FontInfo _fontInfo;
	
	/**
	 * An additional top padding, for example for quote-environments which have a header
	 * that depends on the default-font-size and so on
	 */
	protected int _variableTopPadding = 0;
	
	/**
	 * An additional left padding, for example for list-environments which will depend
	 * on the list-type and the number of list-points
	 */
	protected int _variableLeftPadding = 0;
	
	/**
	 * the cached value for the innerpadding property
	 */
	protected int _innerPadding = -1;
	
	/**
	 * the cached value for the outerpadding property
	 */
	protected int _outerPadding = -1;
	
	/**
	 * The cached line-spacing
	 */
	protected int _lineSpacing = -1;

	/**
	 * The cached font-family
	 */
	private String _defFontFamily = null;
	
	/**
	 * The cached font-size
	 */
	private int _defFontSize = -1;
	
	/**
	 * The cached font-style
	 */
	private int _defFontStyle = -1;
	
	/**
	 * The cached font-color
	 */
	private Color _defFontColor = null;
	
	/**
	 * The cached bg-color
	 */
	private Color _bgColor = null;
	
	/**
	 * Constructor
	 * 
	 * @param env the Environment
	 */
	public EnvironmentView(Environment env) {
		_env = env;
		
		_env.getTextField().addEnvPropertyListener(this);
		
		refreshDefaultFontMetrics();
	}
	
	public void finalize() {
		_env.getTextField().removeEnvPropertyListener(this);
	}
	
	public void propertyChanged(Integer property,Object oldVal,Object newVal) {
		if(property.equals(EnvironmentProperties.BACKGROUND_COLOR))
			_bgColor = null;
		else if(property.equals(EnvironmentProperties.DEF_FONT_COLOR))
			_defFontColor = null;
		else if(property.equals(EnvironmentProperties.INNER_PADDING))
			_innerPadding = -1;
		else if(property.equals(EnvironmentProperties.OUTER_PADDING))
			_outerPadding = -1;
		else if(property.equals(EnvironmentProperties.LINE_SPACING))
			_lineSpacing = -1;
	}
	
	public void refresh() {
		if(shouldRefresh(DEF_FONT))
			refreshDefaultFontMetrics();
		
		clearRefreshes();
	}
	
	public AbstractTextField getTextField() {
		return _env.getTextField();
	}
	
	/**
	 * refreshes the default-font-metrics
	 */
	protected void refreshDefaultFontMetrics() {
		// reset values to force a refresh
		_defFontFamily = null;
		_defFontSize = -1;
		_defFontStyle = -1;
		
		String fontFamily = getDefaultFontFamily();
		int fontSize = getDefaultFontSize();
		int fontStyle = getDefaultFontStyle();
		
		TextAttributes attr = new TextAttributes();
		attr.set(TextAttributes.FONT_FAMILY,fontFamily);
		attr.set(TextAttributes.FONT_SIZE,new Integer(fontSize));
		if((fontStyle & Font.BOLD) != 0)
			attr.set(TextAttributes.BOLD,new Boolean(true));
		if((fontStyle & Font.ITALIC) != 0)
			attr.set(TextAttributes.ITALIC,new Boolean(true));
		if(attr.idNeedsRefresh())
			attr.regenerateId();
		
		FontCache fc = getTextField().getFontCache();
		FontInfo info = fc.getFont(attr.getId());
		if(info == null) {
			Font font = new Font(fontFamily,fontStyle,fontSize);
			info = new FontInfo(font,_env.getTextField().getFontMetrics(font));
			fc.announceFont(attr.getId(),info);
		}
		
		_fontInfo = info;
	}
	
	public void refreshTabWidth() {
		Paragraph p = _env.getFirstParagraph();
		do {
			p.getParagraphView().refreshTabWidth();
			p = (Paragraph)p.getNext();
		} while(p != null);
	}
	
	public void refreshFonts() {
		refreshDefaultFontMetrics();
		
		Paragraph p = _env.getFirstParagraph();
		do {
			p.getParagraphView().refreshFonts();
			
			p = (Paragraph)p.getNext();
		} while(p != null);
	}
	
	public Font getDefaultFont() {
		return _fontInfo.getFont();
	}
	
	public FontMetrics getDefaultFontMetrics() {
		return _fontInfo.getFontMetrics();
	}

	public String getDefaultFontFamily() {
		if(_defFontFamily == null)
			_defFontFamily = (String)_env.getTextField().getEnvProperty(
				EnvironmentProperties.DEF_FONT_FAMILY,_env.getType());
		
		return _defFontFamily;
	}
	
	public int getDefaultFontSize() {
		if(_defFontSize == -1)
			_defFontSize = _env.getTextField().getEnvIntProperty(
				EnvironmentProperties.DEF_FONT_SIZE,_env.getType());
		
		return _defFontSize;
	}
	
	public int getDefaultFontStyle() {
		if(_defFontStyle == -1)
			_defFontStyle = _env.getTextField().getEnvIntProperty(
				EnvironmentProperties.DEF_FONT_STYLE,_env.getType());
		
		return _defFontStyle;
	}
	
	public Color getDefaultFontColor() {
		if(_defFontColor == null)
			_defFontColor = (Color)_env.getTextField().getEnvProperty(
				EnvironmentProperties.DEF_FONT_COLOR,_env.getType());
		
		return _defFontColor;
	}
	
	public Color getBackgroundColor() {
		if(_bgColor == null)
			_bgColor = (Color)_env.getTextField().getEnvProperty(
				EnvironmentProperties.BACKGROUND_COLOR,_env.getType());
		
		return _bgColor;
	}
	
	public int getLineSpacing() {
		if(_lineSpacing < 0)
			_lineSpacing = _env.getTextField().getEnvIntProperty(
				EnvironmentProperties.LINE_SPACING,_env.getType());
		
		return _lineSpacing;
	}
	
	public int getInnerTopPadding() {
		if(_innerPadding < 0)
			_innerPadding = _env.getTextField().getEnvIntProperty(
					EnvironmentProperties.INNER_PADDING,_env.getType());
		
		return _innerPadding + _variableTopPadding;
	}
	
	public int getInnerBottomPadding() {
		if(_innerPadding < 0)
			_innerPadding = _env.getTextField().getEnvIntProperty(
					EnvironmentProperties.INNER_PADDING,_env.getType());
		
		return _innerPadding;
	}
	
	public int getInnerLeftPadding() {
		if(_innerPadding < 0)
			_innerPadding = _env.getTextField().getEnvIntProperty(
					EnvironmentProperties.INNER_PADDING,_env.getType());
		
		return _innerPadding + _variableLeftPadding;
	}
	
	public int getInnerRightPadding() {
		if(_innerPadding < 0)
			_innerPadding = _env.getTextField().getEnvIntProperty(
					EnvironmentProperties.INNER_PADDING,_env.getType());
		
		return _innerPadding;
	}
	
	public int getOuterPadding() {
		if(_outerPadding < 0)
			_outerPadding = _env.getTextField().getEnvIntProperty(
					EnvironmentProperties.OUTER_PADDING,_env.getType());
		
		return _outerPadding;
	}
	
	public int getTotalWidth() {
		Environment parent = _env.getParentEnvironment();
		if(parent != null) {
			IEnvironmentView pView = parent.getEnvView();
			int width = pView.getTotalWidth();
			width -= (getOuterPadding() * 2) + pView.getInnerLeftPadding() +
				pView.getInnerRightPadding();
			return width;
		}
		
		AbstractTextField tf = _env.getTextField();
		return Math.max(tf.getRequiredWidth(),tf.getWidth());
	}
	
	public int getHeight() {
		int height = getInnerTopPadding();
		
		Paragraph p = _env.getFirstParagraph();
		do {
			IParagraphView pView = p.getParagraphView();
			height += pView.getHeight();
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		height += getInnerBottomPadding();
		
		return height;
	}
	
	public int getGlobalTextStart() {
		int pos = 0;
		Environment env = _env;
		do {
			IEnvironmentView eView = env.getEnvView();
			pos += eView.getInnerLeftPadding();
			pos += eView.getOuterPadding();
			env = env.getParentEnvironment();
		} while(env != null);
		
		return pos;
	}

	public Line getLineAtPixelPos(int targetY,boolean exact) {
		int index = getIndexOfParagraphAtPixelPos(targetY);
		if(index < 0 && exact)
			return null;
		
		Paragraph p;
		if(index == -1)
			p = _env.getLastParagraph();
		else if(index == -2)
			p = _env.getFirstParagraph();
		else
			p = _env.getParagraph(index);
		
		if(p.containsEnvironment()) {
			Environment env = (Environment)p.getFirstSection();
			Line line = env.getEnvView().getLineAtPixelPos(targetY,exact);
			if(line != null || exact)
				return line;
		}
		
		return p.getParagraphView().getLineAtPixelPosition(targetY,exact);
	}
	
	public Paragraph getParagraphAtPixelPosition(int y) {
		int index = getIndexOfParagraphAtPixelPos(y);
		if(index == -1)
			return _env.getLastParagraph();
		if(index == -2)
			return _env.getFirstParagraph();
		
		return _env.getParagraph(index);
	}
	
	public int getIndexOfParagraphAtPixelPos(int y) {
		Paragraph first = _env.getFirstParagraph();
		// in front of the first line?
		if(first.getView().getPaintPos() == null || y <= first.getView().getPaintPos().y)
			return -2;
		
		// the lines are sorted, so we can use binarySearch :)
		int index = _env.getParagraphs().getIndexBinarySearch(new Integer(y),new Comparator() {
			public int compare(Object arg0,Object arg1) {
				if(arg0 instanceof Paragraph && arg1 instanceof Integer) {
					int pos = ((Integer)arg1).intValue();
					Paragraph p = (Paragraph)arg0;
					Point paintPos = p.getView().getPaintPos();
					if(paintPos == null)
						return 1;
					
					if(pos < paintPos.y)
						return 1;
					
					if(pos > paintPos.y + p.getParagraphView().getHeight())
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
		setPaintPos(new Point(x.getValue(),y.getValue()));
		
		int outerPadding = getOuterPadding();
		y.increaseValue(getInnerTopPadding() + outerPadding);
		x.increaseValue(getInnerLeftPadding() + outerPadding);
		
		Paragraph p = _env.getFirstParagraph();
		do {
			IParagraphView pView = p.getParagraphView();
			pView.setPaintPositions(g,x,y,maxWidth);
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		y.increaseValue(getInnerBottomPadding() + outerPadding);
	}
	
	public void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd) {
		y.increaseValue(getInnerTopPadding());
		x.increaseValue(getInnerLeftPadding());
		
		Paragraph p = getParagraphAtPixelPosition(paintRect.y);
		if(p.getView().getPaintPos() != null)
			y.setValue(p.getView().getPaintPos().y);
		
		do {
			IParagraphView pView = p.getParagraphView();
			Point point = p.getView().getPaintPos();
			if(point != null && paintRect.y + paintRect.height < point.y)
				break;
		
			pView.paint(g,paintRect,x,y,showCursor,selStart,selEnd);
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		y.increaseValue(getInnerBottomPadding());
	}
}