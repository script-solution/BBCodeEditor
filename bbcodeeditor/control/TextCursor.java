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

package bbcodeeditor.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The cursor for the control. Will be called in a specific interval to show or hide the
 * cursor. Repaints the required area for the cursor.
 * 
 * @author hrniels
 */
final class TextCursor implements ActionListener {
	
	/**
	 * the instance of the textfield
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * do we want to show the cursor next time?
	 */
	private boolean _show = false;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea
	 */
	public TextCursor(AbstractTextField textArea) {
		_textArea = textArea;
	}
	
	/**
	 * should the cursor be painted at the moment?
	 * 
	 * @return true if it should be painted
	 */
	public boolean showCursor() {
		return _show;
	}
	
	/**
	 * sets wether the cursor should be displayed
	 * 
	 * @param show show the cursor?
	 */
	public void setShowCursor(boolean show) {
		_show = show;
	}
	
	public void actionPerformed(ActionEvent e) {
		_show = _show ? false : true;
		ContentSection current = _textArea.getCurrentSection();
		if(current == null)
			return;
		
		Point pos = current.getView().getPaintPos();
		
		// if we have no paint-position we have to repaint the complete control
		if(pos == null)
			_textArea.repaint();
		// otherwise we can repaint just the necessary rectangle
		else {
			int secWidth = current.getSectionView().getSectionWidth();
			int lineHeight = current.getSectionLine().getLineView().getHeight();
			Rectangle repaintRect = new Rectangle(pos.x - 4,pos.y - 1,secWidth + 8,
					lineHeight + 4);
			_textArea.repaint(repaintRect);
		}
	}
}