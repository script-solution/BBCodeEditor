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

import javax.swing.UIManager;

import bbcodeeditor.control.*;
import bbcodeeditor.control.highlighter.HighlightSyntax;
import bbcodeeditor.control.highlighter.Highlighter;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The view for Code-environments
 * 
 * @author hrniels
 */
public class CodeEnvironmentView extends EnvironmentView {
	
	/**
	 * Refresh line-numbers
	 */
	public static final byte LINE_NUMBERS		= 2;
	
	/**
	 * Refresh Code-title
	 */
	public static final byte CODE_TITLE			= 4;

	/**
	 * The language-name we want to display in the header
	 */
	private String _langName = null;
	
	/**
	 * the position of the title
	 */
	private int _titlePos = -1;
	
	/**
	 * Constructor
	 * 
	 * @param env the environment
	 */
	public CodeEnvironmentView(CodeEnvironment env) {
		super(env);
	}
	
	public void refresh() {
		if(shouldRefresh(LINE_NUMBERS))
			setLineNumberBounds();
		if(shouldRefresh(CODE_TITLE))
			setTitleBounds();
		
		super.refresh();
	}

	/**
	 * sets the bounds of the title
	 */
	private void setTitleBounds() {
		Graphics g = _env.getTextField().getGraphics();
		if(g == null) {
			_variableTopPadding = 0;
			return;
		}
		
		// no head in the text-editor-mode
		if(_env.getTextField().getEditorMode() == IPublicController.MODE_TEXT_EDITOR) {
			_variableTopPadding = 1;
			return;
		}
		
		g.setFont(new Font("Verdana",Font.PLAIN,12));
		FontMetrics met = g.getFontMetrics();
		Object syntax = ((CodeEnvironment)_env).getHighlightSyntax();
		Highlighter hl = HighlightSyntax.getHighlighter(syntax);
		if(hl == null)
			_langName = "Code:";
		else
			_langName = hl.getLangName() + ":";
		
		Rectangle2D rect = met.getStringBounds(_langName,g);
		_titlePos = met.getAscent() / 2;
		_variableTopPadding = (int)rect.getHeight() + 10;
	}
	
	/**
	 * Sets the bounds of the line-numbers
	 */
	private void setLineNumberBounds() {
		Graphics g = _env.getTextField().getGraphics();
		if(g == null) {
			_variableLeftPadding = 0;
			return;
		}
		
		if(_env.getTextField().displayCodeLineNumbers()) {
			// calculate the required left-padding
			int pCount = _env.getParagraphCount();
			g.setFont(getDefaultFont());
			String text = String.valueOf(pCount);
			Rectangle2D rect = g.getFontMetrics().getStringBounds(text,g);
			_variableLeftPadding = (int)rect.getWidth() + 15;
		}
	}
	
	public int getHeight() {
		int height = getOuterPadding() * 2;
		return height + super.getHeight();
	}
	
	public void setPaintPositions(Graphics g,MutableInt x,MutableInt y,MutableInt maxWidth) {
		// if we don't have set it yet we have to do that now
		if(_variableTopPadding == 0)
			setTitleBounds();
		
		setLineNumberBounds();
		
		super.setPaintPositions(g,x,y,maxWidth);
	}

	public void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
										boolean showCursor,int selStart,int selEnd) {
		int outerPadding = getOuterPadding();
		
		x.increaseValue(outerPadding);
		y.increaseValue(outerPadding);
		int saveX = x.getValue();
		int saveY = y.getValue();
		int totalWidth = getTotalWidth();
	
		// paint background
		g.setColor(getBackgroundColor());
		g.fillRect(saveX,saveY,totalWidth,
				getHeight() - outerPadding - getInnerBottomPadding());
		
		AbstractTextField tf = _env.getTextField();
		
		if(_variableTopPadding == 0)
			setTitleBounds();
		
		// draw top
		if(tf.getEditorMode() != IPublicController.MODE_TEXT_EDITOR) {
			if(paintRect.intersects(new Rectangle(saveX,saveY,totalWidth,_variableTopPadding))) {
				Color titleBG = tf.getEnvColorProperty(EnvironmentProperties.TITLE_BG_COLOR,
						_env.getType());
				Color titleFG = tf.getEnvColorProperty(EnvironmentProperties.TITLE_FONT_COLOR,
						_env.getType());
				
				g.setColor(titleBG);
				g.fillRect(saveX,saveY,totalWidth,_variableTopPadding);
				
				g.setColor(titleFG);
				g.setFont(new Font("Verdana",Font.PLAIN,12));
				g.drawString(_langName,saveX + 5,saveY + _variableTopPadding / 2 + _titlePos);
			}
		}
		
		y.increaseValue(getInnerTopPadding());
		x.increaseValue(getInnerLeftPadding());
		
		Paragraph p = getParagraphAtPixelPosition(paintRect.y);
		if(p.getView().getPaintPos() != null)
			y.setValue(p.getView().getPaintPos().y);
		
		// determine start-line-number
		int i = 1;
		Font f = null;
		FontMetrics fm = null;
		if(tf.displayCodeLineNumbers()) {
			Paragraph cp = p;
			while(cp.getPrev() != null) {
				i++;
				cp = (Paragraph)cp.getPrev();
			}
			
			f = getDefaultFont();
			fm = g.getFontMetrics(f);
		}
		
		// loop through the paragraphs and paint them
		int lastYPos = y.getValue();
		do {
			IParagraphView pView = p.getParagraphView();
			Point point = p.getView().getPaintPos();
			if(point != null && paintRect.y + paintRect.height < point.y)
				break;
			
			pView.paint(g,paintRect,x,y,showCursor,selStart,selEnd);
			
			// draw the line-number
			if(tf.displayCodeLineNumbers()) {
				int firstLineHeight = p.getFirstLine().getLineView().getHeight();
				g.setFont(f);
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(i),saveX + 3,
						lastYPos + firstLineHeight - fm.getDescent());
				i++;
			}
			
			lastYPos = y.getValue();
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		// draw line-number and text separator
		if(tf.displayCodeLineNumbers()) {
			g.setColor(Color.LIGHT_GRAY);
			if(tf.getEditorMode() == IPublicController.MODE_TEXT_EDITOR) {
				g.drawLine(x.getValue() - 3,0,x.getValue() - 3,tf.getHeight());
			}
			else {
				g.drawLine(x.getValue() - 3,saveY + getInnerTopPadding(),
						x.getValue() - 3,y.getValue());
			}
		}
		
		y.increaseValue(getInnerBottomPadding());
		
		// mark selected if necessary
		if(_env.isSelected()) {
			Environment parent = _env.getParentEnvironment();
			if(!(parent instanceof QuoteEnvironment || parent instanceof CodeEnvironment) ||
					!parent.isSelected()) {
				if(paintRect.intersects(new Rectangle(saveX,saveY,totalWidth,y.getValue() - saveY))) {
					Color selCol = UIManager.getColor("FormattedTextField.selectionBackground");
					g.setColor(new Color(selCol.getRed(),selCol.getGreen(),selCol.getBlue(),100));
					g.fillRect(saveX,saveY,totalWidth,y.getValue() - saveY);
				}
			}
		}
		
		// draw the border and the line-number-separator
		if(tf.getEditorMode() != IPublicController.MODE_TEXT_EDITOR) {
			if(paintRect.intersects(new Rectangle(saveX,saveY,totalWidth,y.getValue() - saveY))) {
				Color border = tf.getEnvColorProperty(EnvironmentProperties.BORDER_COLOR,
						_env.getType());
				g.setColor(border);
				g.drawRect(saveX,saveY,totalWidth,y.getValue() - saveY);
			}
		}
		
		y.increaseValue(outerPadding);
	}
}