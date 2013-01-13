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

import java.awt.Container;

import javax.swing.JViewport;

import bbcodeeditor.control.view.IContentSectionView;
import bbcodeeditor.control.view.ILineView;


/**
 * This is the wordwrap-strategie which will determine the wrap-position by the number
 * of characters in a line and will try not to wrap in words. Additionally it wraps not
 * at a specific position but before the width of the control
 * 
 * @author hrniels
 */
public class WordWrapPixelBased implements IWordWrap {
	
	/**
	 * the textField
	 */
	private final AbstractTextField _textField;
	
	/**
	 * constructor
	 * 
	 * @param textField the textField-instance
	 */
	public WordWrapPixelBased(AbstractTextField textField) {
		_textField = textField;
	}
	
	public int getBreakPosition(Line line) {
		// we have to refresh the view here
		_textField.getViewManager().refresh();
		
		ILineView lineView = line.getLineView();
		int envStart = line.getParentEnvironment().getEnvView().getGlobalTextStart();
		int maxWidth = getMaxWidth(envStart);
		if(maxWidth < 0)
			return -1;
		
		int lineWidth = lineView.getPixelWidth() + envStart;
		
		if(lineWidth > maxWidth) {
			int softBreakPos = 0;
			int hardBreakPos = 0;
			
			// at first we search for a hardbreak-position
			ContentSection sec = (ContentSection)line.getFirstSection();
			int width = envStart;
			while(sec != null && hardBreakPos == 0) {
				int secWidth = sec.getSectionView().getSectionWidth();
				
				// does the section fit?
				if(width + secWidth <= maxWidth)
					width += secWidth;
				else {
					// image-section?
					if(sec instanceof ImageSection)
						hardBreakPos = sec.getElementStartPos() - line.getLineStartPosition();
					else {
						// otherwise we have to walk through the text
						TextSection tSec = (TextSection)sec;
						String text = tSec.getText();
						for(int x = 0,tLen = text.length();x < tLen;x++) {
							// determine char-width
							int cWidth = tSec.getTextSectionView().getCharWidth(x);
							if(width + cWidth <= maxWidth)
								width += cWidth;
							else {
								hardBreakPos = sec.getElementStartPos() + x - line.getLineStartPosition();
								break;
							}
						}
					}
				}
				
				sec = (ContentSection)sec.getNext();
			}
			
			// lets see if we can find a space where to wrap
			String lineText = line.getText();
			for(int i = hardBreakPos - 1;i >= 0;i--) {
				if(Character.isWhitespace(lineText.charAt(i))) {
					softBreakPos = i + 1;
					break;
				}
			}
			
			// we prefer a soft-break
			if(softBreakPos > 0)
				return softBreakPos;
			
			return hardBreakPos;
		}
		
		return -1;
	}
	
	/**
	 * determines the max-width we have
	 * 
	 * @param envPadding the padding of the environment
	 * @return the max width
	 */
	private int getMaxWidth(int envPadding) {
		int maxWidth;
		Container parent = _textField.getParent();
		if(parent instanceof JViewport) {
			JViewport viewPort = (JViewport)parent;
			// we have to substract the environment-padding
			maxWidth = viewPort.getViewRect().width - envPadding - 10;
		}
		else
			maxWidth = _textField.getWidth() - envPadding - 10;
		
		return maxWidth;
	}
	
	public int getCharsToMove(Line line,Line next) {
		// we have to refresh the view here
		_textField.getViewManager().refresh();
		
		int envStart = line.getParentEnvironment().getEnvView().getGlobalTextStart();
		int maxWidth = getMaxWidth(envStart);
		int lineWidth = envStart + line.getLineView().getPixelWidth();
		
		// determine which wrap-type has been performed in the prev line
		boolean isSoftWrap = false;
		Section last = line.getLastSection();
		if(last instanceof TextSection) {
			isSoftWrap = true;
			TextSection tLast = (TextSection)last;
			if(tLast.getElementLength() > 0) {
				String end = tLast.getText(tLast.getElementLength() - 1);
				if(!Character.isWhitespace(end.charAt(0)))
					isSoftWrap = false;
			}
			else
				isSoftWrap = false;
		}
		
		int missingWidth = maxWidth - lineWidth;
		int nLen = next.getLineLength();
		// is there some place left?
		if(nLen > 0 && missingWidth > 5) {
			int breakPos = 0;
			
			// we have to walk through the sections
			ContentSection sec = (ContentSection)next.getFirstSection();
			while(sec != null && breakPos == 0) {
				IContentSectionView secView = sec.getSectionView();
				
				// is it a image section and can we move that to the prev line?
				if(sec instanceof ImageSection) {
					int secWidth = secView.getSectionWidth();
					if(missingWidth < secWidth)
						breakPos = sec.getElementStartPos() - next.getLineStartPosition();
					else if(missingWidth == secWidth)
						breakPos = sec.getElementEndPos() - next.getLineStartPosition();
				}
				// does the section NOT fit?
				else if(sec instanceof TextSection && missingWidth < secView.getSectionWidth()) {
					TextSection tSec = (TextSection)sec;
					int eleStart = sec.getElementStartPos() - next.getLineStartPosition();
					
					// so we walk through the text and see where we can break it
					String text = tSec.getText();
					for(int i = 0,len = text.length();i < len && missingWidth > 0;i++) {
						char c = text.charAt(i);
						
						// soft-wrap position?
						if(isSoftWrap && Character.isWhitespace(c))
							breakPos = eleStart + i + 1;
						
						missingWidth -= tSec.getTextSectionView().getCharWidth(i);
						
						// if we want to perform a hard-wrap we want to wrap back as many chars
						// as we can
						if(missingWidth < 0 && !isSoftWrap)
							breakPos = eleStart + i;
					}
				}
				
				// substract the section width
				missingWidth -= secView.getSectionWidth();
				
				sec = (ContentSection)sec.getNext();
			}
			
			if(sec == null && breakPos == 0 && missingWidth >= 0)
				return next.getLineLength();
			
			return breakPos;
		}
		
		return 0;
	}
}