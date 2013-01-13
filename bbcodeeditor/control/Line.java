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

import java.util.*;

import bbcodeeditor.control.tools.AbstractElement;
import bbcodeeditor.control.tools.MyLinkedList;
import bbcodeeditor.control.view.ILineView;
import bbcodeeditor.control.view.LineView;
import bbcodeeditor.control.view.View;
import bbcodeeditor.control.view.Viewable;



/**
 * Represents a horizontal line in the control.<br>
 * A paragraph may contain multiple Line's.
 * <p>
 * This object (and only this one) contains and manages the ContentSections
 * Every Line starts at the start-position of the first section.
 * All lines are directly together. A paragraph may only contain multiple lines if
 * wordwrap has done that. Therefore there is no new-line at the end of a line and the
 * next line starts with the end-position + 1 or the previous line.
 * <p>
 * A Line has to contain at least one Section. Empty Lines are not allowed.
 * 
 * @author hrniels
 */
public final class Line extends AbstractElement implements Viewable {
	
	/**
	 * the environment which contains this line
	 */
	private final Environment _env;
	
	/**
	 * a LinkedList with all sections
	 */
	private final MyLinkedList _sections = new MyLinkedList();
	
	/**
	 * The view
	 */
	private View _view;
	
	/**
	 * the paragraph of this line
	 */
	private Paragraph _para;
	
	/**
	 * indicates wether this line contains an environment
	 */
	private boolean _containsEnv = false;
	
	/**
	 * constructor
	 * 
	 * @param env the Environment which contains this line
	 * @param p the paragraph of this line
	 */
	Line(Environment env,Paragraph p) {
		this(env,0,p);
	}
	
	/**
	 * constructor
	 * 
	 * @param env the Environment which contains this line
	 * @param startPos the startPos of the line (-1 = create no section)
	 * @param p the paragraph of this line
	 */
	Line(Environment env,int startPos,Paragraph p) {
		super(null,null);
		
		_env = env;
		_para = p;
		_view = new LineView(this);
		
		if(startPos >= 0) {
			// at least 1 section is required!
			TextSection sec = new TextSection(_env,"",startPos,this,_para,null);
			_sections.add(sec);
		}
	}
	
	public View getView() {
		return _view;
	}
	
	/**
	 * @return the current view as ILineView implementation
	 */
	public ILineView getLineView() {
		return (ILineView)_view;
	}
	
	/**
	 * @return wether this line contains an environment
	 */
	public boolean containsEnvironment() {
		return _containsEnv;
	}
	
	/**
	 * @return the paragraph of this line
	 */
	public Paragraph getParagraph() {
		return _para;
	}
	
	/**
	 * @return true if this line is the first one in the environment
	 */
	public boolean isFirstInEnv() {
		return isFirst() && _para.isFirst();
	}

	/**
	 * @return true if this line is the last one in the environment
	 */
	public boolean isLastInEnv() {
		return isLast() && _para.isLast();
	}
	
	/**
	 * determines the next line in the whole environment. That means
	 * that you'll get the first line of the next paragraph if this line is the
	 * last line in the paragraph.
	 * 
	 * @return the next line in the whole environment (or null if not existing)
	 */
	public Line getNextInEnv() {
		if(isLast()) {
			if(isLastInEnv())
				return null;
			
			Paragraph next = (Paragraph)_para.getNext();
			return next.getFirstLine();
		}
		
		return (Line)getNext();
	}
	
	/**
	 * determines the previous line in the whole environment. That means
	 * that you'll get the last line of the previous paragraph if this line is the
	 * first line in the paragraph.
	 * 
	 * @return the previous line in the whole environment (or null if not existing)
	 */
	public Line getPrevInEnv() {
		if(isFirst()) {
			if(isFirstInEnv())
				return null;
			
			Paragraph prev = (Paragraph)_para.getPrev();
			return prev.getLastLine();
		}
		
		return (Line)getPrev();
	}
	
	/**
	 * @return the first section in this environment
	 */	
	public Section getFirstSection() {
		return (Section)_sections.getFirst();
	}
	
	/**
	 * determines the section at given index (in the whole environment)
	 * 
	 * @param index the index of the section (in the whole environment)
	 * @return the section or null if not found
	 */
	public Section getSection(int index) {
		return (Section)_sections.get(index);
	}
	
	/**
	 * @return the last section in this environment
	 */
	public Section getLastSection() {
		return (Section)_sections.getLast();
	}
	
	/**
	 * collects the text of this line (without formating)
	 * 
	 * @return the text in this line
	 */
	public String getText() {
		StringBuffer text = new StringBuffer();
		Section s = getFirstSection();
		do {
			if(s instanceof TextSection)
				text.append(((TextSection)s).getText());
			else if(s instanceof ImageSection)
				text.append(ImageSection.dummyText);
			
			s = (Section)s.getNext();
		} while(s != null);
		
		return text.toString();
	}
	
	/**
	 * adds the given text to the given position in this line
	 * 
	 * @param text the text to add
	 * @param position the position in this line
	 * @param attributes the attributes for the text to add
	 * @return the section where the text has been added
	 */
	ContentSection addTextAt(String text,int position,TextAttributes attributes) {
		// find section at the position
		ContentSection sec = null;
		try {
			sec = getSectionAt(position);
		}
		catch(InvalidTextPositionException e) {
			return null;
		}
		
		// empty text?
		if(text.length() == 0)
			return sec;
		
		if(sec instanceof TextSection) {
			TextSection tSec = (TextSection)sec;
			
			// do we have to create a new section and split the existing one?
			if(tSec.getElementLength() > 0 && attributes != null &&
				 TextAttributes.splitSectionBFromA(_env,tSec.getAttributes(),attributes)) {
				TextSection newSec = new TextSection(_env,text,position,this,
						_para,attributes);
				
				// split the section at the position, if necessary
				Section prevSec = ensureNewSection(position);
				if(prevSec == null)
					addSection(0,newSec);
				else
					addSectionAfter(prevSec,newSec);
				
				// adjust the positions
				adjustPositions((Section)newSec.getNext(),text.length());
				
				return newSec;
			}
			
			tSec.addTextAt(text,position - tSec.getElementStartPos(),false);
			if(attributes != null)
				tSec.setAttributeRange(attributes,true);
			
			adjustPositions((Section)tSec.getNext(),text.length());
		}
		// image section?
		else if(sec instanceof ImageSection) {
			// create section
			TextSection newSec = new TextSection(_env,text,position,this,
					_para,attributes);
			
			// insert behind the section?
			if(position == sec.getElementEndPos() + 1) {
				addSectionAfter(sec,newSec);
				adjustPositions((Section)newSec.getNext(),text.length());
			}
			else {
				// insert in front of the image-section
				Section prev = (Section)sec.getPrev();
				if(prev == null)
					addSection(0,newSec);
				else
					addSectionAfter(prev,newSec);
				
				adjustPositions(sec,text.length());
			}
			
			// adjust cursor
			sec = newSec;
		}
		else {
			// should never happen
			throw new UnsupportedOperationException("Unsupported section!");
		}
		
		return sec;
	}
	
	/**
	 * adds a new image section at given position
	 * 
	 * @param position the position where you want to insert the smiley
	 * @param img the image of the image-section
	 * @return the new section of the cursor
	 */
	ContentSection addImageSectionAt(int position,SecImage img) {
		// find section at the position
		Section sec = null;
		try {
			sec = getSectionAt(position);
		}
		catch(InvalidTextPositionException e) {
			return null;
		}
		
		// create image-section
		ImageSection newSec;
		if(img instanceof SecSmiley)
			newSec = new SmileySection(_env,(SecSmiley)img,position,sec.getSectionLine(),
					sec.getSectionParagraph());
		else
			newSec = new ImageSection(_env,img,position,sec.getSectionLine(),
					sec.getSectionParagraph());
		
		// split the section at the position, if necessary
		Section prevSec = ensureNewSection(position);
		if(prevSec == null)
			addSection(0,newSec);
		else
			addSectionAfter(prevSec,newSec);

		// adjust the positions
		adjustPositions((Section)newSec.getNext(),1);
		
		return newSec;
	}

	/**
	 * breaks the section at given position, if necessary<br>
	 * collects and removes all following/previous sections and returns them
	 * 
	 * @param cursorPos the cursor-position in this environment
	 * @param newLine the line where to move the sectionst to
	 * @param next do you want to add the sections to the next line? In this case
	 * all <b>following</b> sections will be removed and collected.
	 * @return an Array with all sections which should be added to the next line
	 */
	ContentSection[] moveToNewLine(int cursorPos,boolean next) {
		ContentSection prev = ensureNewSection(cursorPos);
		Section start;
		if(next)
			start = (Section)prev.getNext();
		else
			start = prev;
		
		// collect the sections for the next line
		List sections = new ArrayList();
		if(start != null && !(start instanceof Environment)) {
			do {
				sections.add(start);
				if(next)
					start = (Section)start.getNext();
				else
					start = (Section)start.getPrev();
			} while(start != null);
			
			// remove sections from this line
			for(int i = 0;i < sections.size();i++)
				removeSection((Section)sections.get(i));
			
			_view.forceRefresh(ILineView.LINE_HEIGHT);
		}
		
		return (ContentSection[])sections.toArray(new ContentSection[0]);
	}
	
	/**
	 * adds the given section to this line
	 * 
	 * @param s the section to add
	 */
	void addSection(Section s) {
		addSection(_sections.size(),s);
	}
	
	/**
	 * adds the given section to this line at given index
	 * 
	 * @param index the index of the section
	 * @param sec the section to add
	 */
	void addSection(int index,Section sec) {
		// save first section
		Section first = null;
		if(_sections.size() == 1)
			first = getFirstSection();
		
		_sections.addBefore(index,sec);
		
		// remove the first section, if it is empty
		if(first != null && first.getElementLength() == 0)
			_sections.remove(first);
		
		_view.forceRefresh(ILineView.LINE_HEIGHT);
		
		if(sec instanceof Environment)
			_containsEnv = true;
	}
	
	/**
	 * adds the given section to this line after the given previous section
	 * 
	 * @param prev the previous section
	 * @param sec the section to add
	 */
	private void addSectionAfter(Section prev,Section sec) {
		// save first section
		Section first = null;
		if(_sections.size() == 1)
			first = getFirstSection();
		
		if(prev == null)
			_sections.addBefore(_sections.getFirst(),sec);
		else
			_sections.addAfter(prev,sec);
		
		// remove the first section, if it is empty
		if(first != null && first.getElementLength() == 0)
			_sections.remove(first);
		
		_view.forceRefresh(ILineView.LINE_HEIGHT);
		
		if(sec instanceof Environment)
			_containsEnv = true;
	}
	
	/**
	 * removes the given section
	 * 
	 * @param sec the section to remove
	 */
	void removeSection(Section sec) {
		_sections.remove(sec);
		
		// do we have to add an empty section?
		if(_sections.size() == 0) {
			// use the previous line because the given section might not contain
			// the correct position
			Line prev = (Line)getPrev();
			int startPos = prev != null ? prev.getLineEndPosition() + 2 : 0;
			TextAttributes attributes = null;
			if(sec instanceof TextSection)
				attributes = ((TextSection)sec).getAttributes();
			
			TextSection newSec = new TextSection(_env,"",startPos,this,
					_para,attributes);
			addSection(0,newSec);
			
			_containsEnv = false;
		}
	}
	
	/**
	 * ensures that at the given position starts a new section
	 * returns the section in front of the position. if there is no section in this line
	 * null will be returned, so that you can insert in front of the first section in this
	 * line
	 * 
	 * @param position the position
	 * @return the section in front of the position (the point after which you can insert a
	 * 				 new section)
	 */
	ContentSection ensureNewSection(int position) {
		return ensureNewSection(position,true);
	}
	
	/**
	 * ensures that at the given position starts a new section
	 * returns the section in front of the position. if there is no section in this line
	 * null will be returned, so that you can insert in front of the first section in this
	 * line
	 * 
	 * @param position the position
	 * @param forceFontRefresh do you want to force a FONT and TEXT_BOUNDS refresh?
	 * @return the section in front of the position (the point after which you can insert a
	 * 				 new section)
	 */
	ContentSection ensureNewSection(int position,boolean forceFontRefresh) {
		// find section at the position
		ContentSection sec = null;
		try {
			sec = getSectionAt(position);
		}
		catch(InvalidTextPositionException e) {
			return null;
		}
		
		// position at the start? so return prev one
		if(position == sec.getElementStartPos())
			return (ContentSection)sec.getPrev();
		
		// are we behind this section? so return it
		if(position == sec.getElementEndPos() + 1)
			return sec;
		
		if(sec instanceof TextSection) {
			// ok, we are _in_ the section
			// it has to be a TextSection because otherwise we can't be _in_ it :)
			TextSection tSec = (TextSection)sec;
	
			// extract the text
			String secEnd = tSec.getText(position - tSec.getElementStartPos());
			tSec.removeText(position - tSec.getElementStartPos());
			
			// create section with the extracted text behind the section
			TextSection newSec = new TextSection(_env,secEnd,tSec.getElementEndPos() + 1,
					this,_para,tSec.getAttributes(),forceFontRefresh);
			addSectionAfter(tSec,newSec);
			
			return tSec;
		}
		
		return sec;
	}
	
	/**
	 * adjusts the start- and end-positions of the sections in this line
	 * starting at the startIndex. will add the given amount
	 * 
	 * @param startIndex the index where to start
	 * @param amount the amount to add
	 */
	void adjustPositions(int startIndex,int amount) {
		Section s = (Section)_sections.get(startIndex);
		adjustPositions(s,amount);
	}
	
	/**
	 * adjusts the start- and end-positions of the sections in this line
	 * starting with the given section. will add the given amount
	 * 
	 * @param start the start-section
	 * @param amount the amount to add
	 */
	void adjustPositions(Section start,int amount) {
		if(start == null)
			return;
		
		if(amount != 0) {
			do {
				start.increaseElementPos(amount);
			} while((start = (Section)start.getNext()) != null);
		}
	}
	
	/**
	 * @param index the index of the section you're looking for
	 * @return the section or null if not found
	 */
	public Section getSectionAtIndex(int index) {
		try {
			return (Section)_sections.get(index);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * @return the number of sections
	 */
	public int getSectionCount() {
		return _sections.size();
	}
	
	/**
	 * @return the length of this line
	 */
	public int getLineLength() {
		int start = getLineStartPosition();
		int end = getLineEndPosition();
		return end - start + 1;
	}
	
	/**
	 * @return the start-position of this line in this environment
	 */
	public int getLineEnvStartPos() {
		Section first = (Section)_sections.getFirst();
		return first.getStartPosInEnv();
	}
	
	/**
	 * @return the end-position of this line in this environment
	 */
	public int getLineEnvEndPos() {
		Section last = (Section)_sections.getLast();
		return last.getEndPosInEnv();
	}
	
	/**
	 * @return the start-position of this line (in this paragraph)
	 */
	public int getLineStartPosition() {
		Section first = (Section)_sections.getFirst();
		return first.getElementStartPos();
	}
	
	/**
	 * @return the end-position of this line (in this paragraph)
	 */
	public int getLineEndPosition() {
		Section last = (Section)_sections.getLast();
		return last.getElementEndPos();
	}
	
	/**
	 * @return the last cursor-position in this line (in this paragraph)
	 */
	public int getLastCursorPos() {
		if(!isLast())
			return getLineEndPosition();
		
		return getLineEndPosition() + 1;
	}
	
	/**
	 * @return the last cursor-position in this line (in this environment)
	 */
	public int getLastCursorPosInEnv() {
		if(!isLast())
			return getLineEnvEndPos();
		
		return getLineEnvEndPos() + 1;
	}
	
	/**
	 * @return the parent-environment
	 */
	public Environment getParentEnvironment() {
		return _env;
	}
	
	/**
	 * determines the section at given position
	 * 
	 * @param position the position in this paragraph
	 * @return the section
	 * @throws InvalidTextPositionException if no section has been found
	 */
	public ContentSection getSectionAt(int position) throws InvalidTextPositionException {
		if(getFirstSection() instanceof Environment)
			throw new InvalidTextPositionException("This is an environment line!",position);
		
		// are we at the line start?
		if(position == getLineStartPosition())
			return (ContentSection)getFirstSection();
		
		// are we in the first section?
		// this should be faster than performing a binary search
		if(_sections.size() == 1 && position <= getFirstSection().getElementEndPos() + 1)
			return (ContentSection)getFirstSection();
		
		// the sections are sorted, so we can use binarySearch :)
		int index = Arrays.binarySearch(_sections.toArray(),new Integer(position),new Comparator() {
			public int compare(Object arg0,Object arg1) {
				if(arg0 instanceof Section && arg1 instanceof Integer) {
					int pos = ((Integer)arg1).intValue();
					Section s = (Section)arg0;
					if(pos < s.getElementStartPos())
						return 1;
					
					// if we are at the section-start-pos and there is a previous section
					// we want to use the previous one
					if(pos == s.getElementStartPos() && !s.isFirst())
						return 1;
					
					if(pos > s.getElementEndPos() + 1)
						return -1;
				}
				
				return 0;
			}
		});
		
		// have we found the line?
		if(index >= 0)
			return (ContentSection)_sections.get(index);
		
		throw new InvalidTextPositionException(position);
	}
	
	/**
	 * removes the text in this line from the given start-position to the end-position
	 * the positions are the section-positions in the environment
	 * one of the positions may be before or behind this line (not both) 
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the number of removed characters
	 */
	int removeText(int start,int end) {
		Section s = (Section)_sections.getFirst();
		
		// save the attributes of the first section to apply them later on
		// because otherwise we would loose the attributes in some cases
		TextAttributes firstAttr = null;
		if(start == s.getElementStartPos() && end >= getLineEndPosition() + 1) {
			if(s instanceof TextSection)
				firstAttr = ((TextSection)s).getAttributes();
		}
		
		boolean recalcHeight = false;
		int count = 0;
		
		// ok, loop through all sections in this line
		while(s != null) {
			// do we "hit" this section?
			if(start <= s.getElementEndPos() && end > s.getElementStartPos()) {
				// delete the complete section?
				if(start <= s.getElementStartPos() && end > s.getElementEndPos()) {
					Section next = (Section)s.getNext();
					removeSection(s);
					
					count += s.getElementLength();
					recalcHeight = true;
					
					// are we finished after this section?
					if(end == s.getElementEndPos() + 1) {
						// we have to adjust the positions of the following sections
						s = next;
						break;
					}
				}
				// remove in the middle?
				else if(start > s.getElementStartPos() && end <= s.getElementEndPos()) {
					TextSection ts = (TextSection)s;
					int remStart = start - s.getElementStartPos();
					int remEnd = end - s.getElementStartPos() - 1;
					ts.removeText(remStart,remEnd - remStart + 1);
					
					count += end - start;
					
					// go to the next one
					s = (Section)s.getNext();
					break;
				}
				// remove at the beginning or end
				else {
					int endPos = s.getElementEndPos();
					int startPos = s.getElementStartPos();
					boolean breakHere = end <= endPos + 1;
					
					// split at the start pos
					Section split = null;
					if(start > startPos) {
						split = ensureNewSection(start);
						// do we have to use the first section in the line?
						if(split == null)
							split = getFirstSection();
					}
					
					// split at the end-pos
					if(end <= endPos) {
						split = ensureNewSection(end);
						// do we have to use the first section in the line?
						if(split == null)
							split = getFirstSection();
					}
					
					// determine section to apply the attribute to
					Section remSec = null;
					if(end <= endPos)
						remSec = split;
					else
						remSec = (Section)split.getNext();

					// we need to know the section after the removed section
					s = (Section)split.getNext();
					
					removeSection(remSec);

					count += remSec.getElementLength();
					
					if(breakHere || s == null)
						break;
				}
			}
			
			s = (Section)s.getNext();
		}
		
		// apply the saved attributes to the first section
		if(firstAttr != null) {
			Section first = getFirstSection();
			if(first instanceof TextSection)
				((TextSection)first).setAttributeRange(firstAttr);
		}
		
		// do we have to recalculate the line-height
		if(recalcHeight)
			_view.forceRefresh(ILineView.LINE_HEIGHT);
		
		// are there sections left?
		if(s != null)
			adjustPositions(s,-count);
		
		return count;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Line [Sections:" + getSectionCount() + "]:\n");
		int len = _sections.size();
		for(int i = 0;i < len;i++)
			buf.append(_sections.get(i) + "\n");
		
		return buf.toString();
	}
}