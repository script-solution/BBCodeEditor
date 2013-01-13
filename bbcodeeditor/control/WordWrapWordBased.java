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


/**
 * This is the wordwrap-strategie which will determine the wrap-position by the number
 * of characters in a line and will try not to wrap in words.
 * 
 * @author hrniels
 */
public class WordWrapWordBased implements IWordWrap {

	/**
	 * the maximum break position
	 */
	private final int _wrapPosition;
	
	/**
	 * constructor
	 * 
	 * @param wrapPosition the position (in chars) where the word-wrap-strategie will try
	 * to wrap
	 */
	public WordWrapWordBased(int wrapPosition) {
		_wrapPosition = wrapPosition;
	}
	
	public int getBreakPosition(Line line) {
		int len = line.getLineLength();
		if(len > _wrapPosition) {
			String text = line.getText();
			for(int i = _wrapPosition - 1;i >= 0;i--) {
				if(Character.isWhitespace(text.charAt(i)))
					return i + 1;
			}
			
			return _wrapPosition;
		}
		
		return -1;
	}
	
	public int getCharsToMove(Line line,Line next) {
		int lLen = line.getLineLength();
		int nLen = next.getLineLength();
		if(nLen > 0 && lLen < _wrapPosition) {
			int max = Math.min(nLen,_wrapPosition - lLen);
			
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
			
			// if we have a hardwrap we can simply return the maximum number of characters we
			// can move
			if(!isSoftWrap)
				return max;
			
			String text = next.getText();
			int i;
			for(i = max - 1;i >= 0;i--) {
				if(Character.isWhitespace(text.charAt(i)))
					return i + 1;
			}
			
			// can we move the complete line?
			if(max == text.length())
				return max;
			
			return 0;
		}
		
		return 0;
	}
}