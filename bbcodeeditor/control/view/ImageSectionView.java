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
 * The view for image-sections
 * 
 * @author hrniels
 */
public class ImageSectionView extends ContentSectionView {
	
	/**
	 * Refresh the image-size
	 */
	public static final byte IMAGE_SIZE		= 1;
	
	
	/**
	 * the left-padding of the image
	 */
	public static final int PADDING_LEFT		= 2;
	
	/**
	 * the top-padding of the image
	 */
	public static final int PADDING_TOP			= 0;

	/**
	 * the right-padding of the image
	 */
	public static final int PADDING_RIGHT		= 2;
	
	/**
	 * the bottom-padding of the image
	 */
	public static final int PADDING_BOTTOM	= 0;

	/**
	 * Constructor
	 * 
	 * @param section the section
	 */
	public ImageSectionView(ImageSection section) {
		super(section);
	}
	
	public void forceRefresh(byte type) {
		super.forceRefresh(type);
		
		if(type == IMAGE_SIZE)
			_section.getSectionLine().getView().forceRefresh(ILineView.LINE_HEIGHT);
	}
	
	public void refresh() {
		clearRefreshes();
	}
	
	public AbstractTextField getTextField() {
		return _section.getTextField();
	}

	public int getSectionHeight() {
		return ((ImageSection)_section).getImageHeight() + PADDING_TOP + PADDING_BOTTOM;
	}
	
	public int getSectionWidth() {
		return ((ImageSection)_section).getImageWidth() + PADDING_LEFT + PADDING_RIGHT;
	}
	
	public int getDescent() {
		return 0;
	}
	
	public int getCharWidth() {
		return 4;
	}
	
	public void setPaintPosition(MutableInt x,MutableInt y) {
		setPaintPos(new Point(x.getValue(),y.getValue()));
		x.increaseValue(getSectionWidth());
	}
	
	public void paintRect(Graphics g,int x,int y,int cursorPos,int width) {
		int cStart = x;
		if(cursorPos > 0)
			cStart += getSectionWidth();
		
		int height = _section.getSectionLine().getLineView().getHeight();
		int imgHeight = ((ImageSection)_section).getImageHeight();
		g.fillRect(cStart,y + height - imgHeight,width,imgHeight);
	}
	
	public void paint(Graphics g,Rectangle paintRect,MutableInt x,int y,int selStart,
			int selEnd) {
		int width = getSectionWidth();
		int saveX = x.getValue();
		
		// break here if this section isn't visible.
		if(!isPaintingRequired(paintRect,saveX,y,width)) {
			x.increaseValue(width);
			return;
		}
		
		// We have to determine wether the board would replace the smiley
		// The board replaces it if behind the smiley is a whitespace-character
		// or nothing.
		boolean drawImage = true;
		
		// is it a smiley-section?
		if(_section instanceof SmileySection) {
			Section n = (Section)_section.getNext();
			// no section behind this one is ok
			if(n != null) {
				// we don't want to paint multiple smileys without a space between them
				if(n instanceof SmileySection)
					drawImage = false;
				else if(n instanceof TextSection) {
					TextSection tsn = (TextSection)n;
					// empty section is ok (should never happen, but just to be sure)
					if(tsn.getElementLength() > 0) {
						// is it no whitespace?
						char c = tsn.getText(0,1).charAt(0);
						if(!Character.isWhitespace(c))
							drawImage = false;
					}
				}
			}
		}
		
		Line l = _section.getSectionLine();
		int height = l.getLineView().getHeight();
		SecImage img = ((ImageSection)_section).getImage();
		int imgHeight = ((ImageSection)_section).getImageHeight();
		if(drawImage) {
			g.drawImage(
		  		img.getImage(),
		  		saveX + ImageSectionView.PADDING_LEFT,
		  		y + PADDING_TOP + height - imgHeight,
		  		((ImageSection)_section).getImageWidth(),
		  		imgHeight,
		  		l.getParentEnvironment().getTextField()
		  );
		}
		// otherwise we paint the smiley-code
		else {
			int descent = l.getLineView().getDescent();
			IEnvironmentView envView = _section.getParentEnvironment().getEnvView();
			String text = ((SecSmiley)img).getPrimaryCode();
			// use the default font and color if it has been changed
			g.setFont(envView.getDefaultFont());
			g.setColor(envView.getDefaultFontColor());
			g.drawString(text,saveX + ImageSectionView.PADDING_LEFT,y + height - descent);
		}
		
		int startPos = _section.getElementStartPos();
		int endPos = _section.getElementEndPos();
		
	  // select the image if necessary
	  int start = Math.max(selStart,startPos);
		int end = Math.min(selEnd,endPos + 1);
	  if((selStart >= 0 || selEnd >= 0) && end > startPos && start <= endPos) {
	  	Color selCol = UIManager.getColor("FormattedTextField.selectionBackground");
	  	g.setColor(new Color(selCol.getRed(),selCol.getGreen(),selCol.getBlue(),100));
	  	g.fillRect(saveX,y + height - imgHeight,width,imgHeight);
	  }
	  
	  x.increaseValue(width);
	}
}