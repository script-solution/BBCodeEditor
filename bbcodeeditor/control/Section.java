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
 * A Section is the general content of Lines.<br>
 * Lines contain and manage the Sections. A Section may be a ContentSection, which contains
 * text or an image or may be an Environment.
 * <p>
 * A Section has a start- and end-position and a length (which will be calculated by the
 * start- and end-position). Additionally it knows the Environment, the Paragraph and
 * the Line in which it is stored.
 * <p>
 * A Section may be empty, but only if there is no other Section in the Line!
 * <p>
 * Every Section has a paint-position which must be calculated as soon as it may have
 * changed!
 *  
 * @author hrniels
 */
public abstract class Section extends PositionElement {
	
	/**
	 * the Line this section belongs to
	 */
	protected Line _line;
	
	/**
	 * the Paragraph this section belongs to
	 */
	protected Paragraph _paragraph;
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-object
	 * @param parentEnv the environment which contains this section
	 * @param startPos the "global" start-position
	 * @param endPos the "global" end-position
	 * @param line the line of this section
	 * @param p the paragraph of this section
	 */
	Section(Environment parentEnv,int startPos,int endPos,
			Line line,Paragraph p) {
		super(parentEnv,startPos,endPos);
		
		_line = line;
		_paragraph = p;
	}
	
	/**
	 * @return the start-position in the environment
	 */
	public int getStartPosInEnv() {
		return _startPos + (_paragraph == null ? 0 : _paragraph.getElementStartPos());
	}
	
	/**
	 * @return the end-position in the environment
	 */
	public int getEndPosInEnv() {
		return _endPos + (_paragraph == null ? 0 : _paragraph.getElementStartPos());
	}
	
	/**
	 * @return the line of this section
	 */
	public Line getSectionLine() {
		return _line;
	}
	
	/**
	 * sets the line of this section
	 * 
	 * @param l the new line
	 */
	void setSectionLine(Line l) {
		_line = l;
	}
	
	/**
	 * @return the paragraph of this section
	 */
	public Paragraph getSectionParagraph() {
		return _paragraph;
	}
	
	/**
	 * sets the paragraph of this section
	 * 
	 * @param p the new paragraph
	 */
	void setSectionParagraph(Paragraph p) {
		_paragraph = p;
	}

	/**
	 * @return the previous section in this paragraph (null if it is the first one)
	 */
	public Section getPrevInParagraph() {
		if(isFirst()) {
			if(isFirstInParagraph())
				return null;
			
			Line prevLine = (Line)_line.getPrev();
			return prevLine.getLastSection();
		}
		
		return (Section)getPrev();
	}
	
	/**
	 * @return the next section in this paragraph (null if it is the last one)
	 */
	public Section getNextInParagraph() {
		if(isLast()) {
			if(isLastInParagraph())
				return null;
			
			Line nextLine = (Line)_line.getNext();
			return nextLine.getFirstSection();
		}
		
		return (Section)getNext();
	}
	
	/**
	 * @return true if this section is the first one in the paragraph
	 */
	public boolean isFirstInParagraph() {
		return _line.isFirst() && isFirst();
	}
	
	/**
	 * @return true if this section is the last one in the paragraph
	 */
	public boolean isLastInParagraph() {
		return _line.isLast() && isLast();
	}
}