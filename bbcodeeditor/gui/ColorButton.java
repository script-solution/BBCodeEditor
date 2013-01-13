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

package bbcodeeditor.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;



/**
 * A button which displays a selected color
 * 
 * @author hrniels
 */
public class ColorButton extends JButton {

	private static final long serialVersionUID = 6408133519281846880L;
	
	/**
	 * The text for the button
	 */
	private final String _text;
	
	/**
	 * the currently selected color
	 */
	private Color _color;
	
	/**
	 * constructor
	 * 
	 * @param text the text for the button (should be short!)
	 * @param color the color of the button
	 */
	public ColorButton(String text,Color color) {
		super("-");
		
		_text = text;
		_color = color;
	}
	
	/**
	 * sets the color on the button
	 * 
	 * @param color the new color
	 */
	public void setColor(Color color) {
		_color = color;
		validate();
		repaint();
	}
	
	/**
	 * @return the color of this button
	 */
	public Color getColor() {
		return _color;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.GRAY);
		g.drawRect(6,5,getWidth() - 13,getHeight() - 11);
		g.setColor(_color);
		g.fillRect(7,6,getWidth() - 14,getHeight() - 12);
		
		if(_text != null && _text.length() > 0) {
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D bounds = fm.getStringBounds(_text,g);
			// determine a good visible fontcolor
			int total = _color.getRed() + _color.getGreen() + _color.getBlue();
			if(total > 382) // (255 * 3) / 2
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.WHITE);
			
			// draw centered string
			g.drawString(
				_text,
				(int)(getWidth() - bounds.getWidth()) / 2,
				getHeight() / 2 - fm.getHeight() / 2 + fm.getAscent()
			);
		}
	}
}