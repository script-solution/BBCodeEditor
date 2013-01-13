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

import javax.swing.UIManager;

import bbcodeeditor.control.*;
import bbcodeeditor.control.tools.MutableInt;


/**
 * The view for a QuoteEnvironment
 * 
 * @author hrniels
 */
public class QuoteEnvironmentView extends EnvironmentView {
	
	/**
	 * Refreshes the title
	 */
	public static final byte QUOTE_TITLE		= 1;

	/**
	 * the position of the title
	 */
	private int _titlePos = -1;
	
	/**
	 * Constructor
	 * 
	 * @param env the environment
	 */
	public QuoteEnvironmentView(QuoteEnvironment env) {
		super(env);
	}
	
	public void refresh() {
		if(shouldRefresh(QUOTE_TITLE))
			refreshTitle();
		
		super.refresh();
	}

	public int getHeight() {
		int height = getOuterPadding() * 2;
		return height + super.getHeight();
	}
	
	/**
	 * Refreshes the title
	 */
	private void refreshTitle() {
		Graphics g = _env.getTextField().getGraphics();
		if(g != null) {
			g.setFont(getDefaultFont());
			FontMetrics met = g.getFontMetrics();
			String text;
			if(((QuoteEnvironment)_env).getAuthor() != null)
				text = ((QuoteEnvironment)_env).getAuthor() + " hat folgendes geschrieben:";
			else
				text = "Zitat";
			_titlePos = met.getAscent() / 2;
			_variableTopPadding = (int)met.getStringBounds(text,g).getHeight() + 10;
		}
	}
	
	public void setPaintPositions(Graphics g,MutableInt x,MutableInt y,MutableInt maxWidth) {
		// if we don't have set it yet we have to do that now
		if(_variableTopPadding == 0 || shouldRefresh(QUOTE_TITLE))
			refreshTitle();
		
		super.setPaintPositions(g,x,y,maxWidth);
	}

	public void paint(Graphics g,Rectangle paintRect,MutableInt x,MutableInt y,
			boolean showCursor,int selStart,int selEnd) {
		int outerPadding = getOuterPadding();
		
		x.increaseValue(outerPadding);
		y.increaseValue(outerPadding);
		
		// paint background
		g.setColor(getBackgroundColor());
		g.fillRect(x.getValue(),y.getValue(),getTotalWidth(),
				getHeight() - outerPadding - getInnerBottomPadding());
		
		int saveX = x.getValue();
		int saveY = y.getValue();
		int totalWidth = getTotalWidth();

		// if we don't have set it yet we have to do that now
		if(_variableTopPadding == 0 || shouldRefresh(QUOTE_TITLE))
			refreshTitle();
		
		// paint title
		if(paintRect.intersects(new Rectangle(saveX,saveY,totalWidth,_variableTopPadding))) {
			Color titleBG = _env.getTextField().getEnvColorProperty(
					EnvironmentProperties.TITLE_BG_COLOR,_env.getType());
			Color titleFG = _env.getTextField().getEnvColorProperty(
					EnvironmentProperties.TITLE_FONT_COLOR,_env.getType());
			
			g.setColor(titleBG);
			g.fillRect(saveX,saveY,totalWidth,_variableTopPadding);
			
			String text;
			if(((QuoteEnvironment)_env).getAuthor() != null)
				text = ((QuoteEnvironment)_env).getAuthor() + " hat folgendes geschrieben:";
			else
				text = "Zitat";
			
			g.setColor(titleFG);
			g.setFont(getDefaultFont());
			g.drawString(text,saveX + 5,saveY + _variableTopPadding / 2 + _titlePos);
		}
		
		// paint content
		super.paint(g,paintRect,x,y,showCursor,selStart,selEnd);
		
		// select environment?
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
		
		// draw border
		if(paintRect.intersects(new Rectangle(saveX,saveY,totalWidth,y.getValue() - saveY))) {
			Color border = _env.getTextField().getEnvColorProperty(
					EnvironmentProperties.BORDER_COLOR,_env.getType());
			g.setColor(border);
			g.drawRect(saveX,saveY,totalWidth,y.getValue() - saveY);
		}
		
		y.increaseValue(outerPadding);
	}
}