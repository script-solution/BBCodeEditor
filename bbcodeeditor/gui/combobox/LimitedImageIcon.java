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

package bbcodeeditor.gui.combobox;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;


/**
 * A limited image icon which displays a resized images with a specified
 * width and height.
 * 
 * @author hrniels
 */
public class LimitedImageIcon extends ImageIcon {
	
	private static final long serialVersionUID = 1413543352873632878L;
	
	/**
	 * The width to use for painting the image
	 */
	private final int _width;
	
	/**
	 * The height to use for painting the image
	 */
	private final int _height;
	
	/**
	 * Constructor
	 * 
	 * @param img the image to use
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public LimitedImageIcon(Image img,int width,int height) {
		super(img);
		
		_width = width;
		_height = height;
	}
	
	/**
	 * Constructor
	 * 
	 * @param imageUrl the URL to the image to use
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public LimitedImageIcon(URL imageUrl,int width,int height) {
		super(imageUrl);
		
		_width = width;
		_height = height;
	}
	
	public synchronized void paintIcon(Component c,Graphics g,int x,int y) {
		ImageObserver ob = getImageObserver();
		Image img = getImage();
		int iwidth = img.getWidth(ob);
		int iheight = img.getHeight(ob);
		int pwidth = iwidth,pheight = iheight;
		if(iwidth > _width || iheight > _height) {
			if(iwidth > iheight) {
				pwidth = _width;
				pheight = (int)(pwidth * ((float)iheight / iwidth));
			}
			else {
				pheight = _height;
				pwidth = (int)(pheight * ((float)iwidth / iheight));
			}
		}
		
		if(ob == null) {
			int cwidth = c.getWidth();
			int cheight = c.getHeight();
			g.drawImage(
				img,
				cwidth / 2 - pwidth / 2,
				cheight / 2 - pheight / 2,
				pwidth,
				pheight,
				c
			);
		}
    else {
    	Insets padding;
  		if(c instanceof JComponent)
  			padding = ((JComponent)c).getInsets();
  		else
  			padding = new Insets(2,2,2,2);
  		
  		g.drawImage(
				img,
				(_width + padding.left + padding.right) / 2 - pwidth / 2,
				(_height + padding.top + padding.bottom) / 2 - pheight / 2,
				pwidth,
				pheight,
				ob
			);
    }
	}
}