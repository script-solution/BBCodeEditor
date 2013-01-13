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

import bbcodeeditor.control.tools.MyLinkedList;


/**
 * A ParagraphContainer contains and manages, as you might guess ;), paragraphs.<br>
 * Every environment has a ParagraphContainer.
 * <p>
 * The ParagraphContainer manages the paragraphs such as creates new ones, moves lines
 * from paragraph to paragraph and so on.
 * <p>
 * The container has to have at least one paragraph!
 * 
 * @author hrniels
 */
public class ParagraphContainer {
	
	/**
	 * the environment which contains this line
	 */
	private final Environment _env;

	/**
	 * a linked list with all paragraphs of this container
	 */
	private final MyLinkedList _paragraphs = new MyLinkedList();
	
	/**
	 * constructor
	 * 
	 * @param textField the textField
	 * @param env the Environment which contains this paragraph-container
	 */
	ParagraphContainer(Environment env) {
		_env = env;
		
		// at least 1 line is required
		_paragraphs.add(new Paragraph(_env,0,-1));
	}

	/**
	 * resets this container
	 */
	void clear() {
		_paragraphs.clear();
		_paragraphs.add(new Paragraph(_env,0,-1));
	}
	
	/**
	 * moves all lines of the given Paragraph to the previous one
	 * and moves all following Paragraphs one upwards
	 * 
	 * @param p the paragraph to move
	 */
	void moveToPrevParagraph(Paragraph p) {
		Paragraph prev = (Paragraph)p.getPrev();
		if(prev == null)
			return;
		
		int count = 0;
		Section s = p.getFirstSection();
		if(p.getElementLength() > 0) {
			// we can't use the getNext() method here, because the sections
			// will be moved to another paragraph, so that the pointer will be changed

			// therefore we collect the sections at first
			List sections = new ArrayList();
			do {
				sections.add(s);
				s = s.getNextInParagraph();
			}
			while(s != null);
			
			// move the sections to the prev paragraph
			Line lastLine = prev.getLastLine();
			Iterator it = sections.iterator();
			while(it.hasNext()) {
				s = (Section)it.next();
				
				// we move the paragraph up, so we have to reset the position
				int startPos = lastLine.getLineEndPosition() + 1;
				s.setElementPos(startPos,startPos + s.getElementLength() - 1);
				s.setSectionParagraph(prev);
				s.setSectionLine(lastLine);
				prev.addSection(s);
				
				count += s.getElementLength();
			}
		}
		
		// increase the end-position of the previous paragraph
		prev.increaseElementEndPos(count);
		
		// remove the line and adjust positions
		_paragraphs.remove(p);
		_env.getTextField().getPaintPosManager().markAllDirty();
		
		adjustParagraphs((Paragraph)prev.getNext(),-1);
	}
	
	/**
	 * moves all lines beginning at cursorPos to the next paragraph
	 *
	 * @param p the paragraph of the given cursor-position
	 * @param cursorPos the position of the cursor (in this environment)
	 * @param isListPoint should the new line be a new list point?
	 * @return the new section of the cursor
	 */
	ContentSection moveToNewParagraph(Paragraph p,int cursorPos,boolean isListPoint) {
		return moveToNewParagraph(p,cursorPos,isListPoint,null);
	}
			
	/**
	 * moves all lines beginning at cursorPos to the next paragraph
	 *
	 * @param p the paragraph of the given cursor-position
	 * @param cursorPos the position of the cursor (in this environment)
	 * @param isListPoint should the new line be a new list point?
	 * @param attributes the attributes for the new section
	 * @return the new section of the cursor
	 */
	ContentSection moveToNewParagraph(Paragraph p,int cursorPos,boolean isListPoint,
			TextAttributes attributes) {
		List sections = p.getSectionsForNewParagraph(cursorPos - p.getElementStartPos());
		int pEnd = p.getElementEndPos();
		
		Paragraph newPara = new Paragraph(_env,pEnd + 2,pEnd + 1);
		newPara.setListPoint(isListPoint);
		
		// are there sections to move?
		if(sections.size() > 0) {
			int len = 0;
			int pos = 0;
			Iterator it = sections.iterator();
			while(it.hasNext()) {
				ContentSection sec = (ContentSection)it.next();
				len += sec.getElementLength();
				
				sec.setElementPos(pos,pos + sec.getElementLength() - 1);
				newPara.addSection(sec);
				
				pos = sec.getElementEndPos() + 1;
			}
			newPara.increaseElementEndPos(len);
		}
		// otherwise apply the attributes
		else {
			Section first = newPara.getFirstSection();
			if(first instanceof TextSection) {
				Section last = p.getLastSection();
				TextSection tFirst = (TextSection)first;
				
				// use the attributes of the last section?
				if(attributes == null && last instanceof TextSection) {
					TextSection tLast = (TextSection)last;
					tFirst.setAttributeRange(tLast.getAttributes());
				}
				// or given ones (may be null)
				else
					tFirst.setAttributeRange(attributes);
			}
		}
		
		_paragraphs.addAfter(p,newPara);
		
		// we have added a new-line
		adjustParagraphs((Paragraph)newPara.getNext(),1);
		
		return (ContentSection)newPara.getFirstSection();
	}
	
	/**
	 * @return the number of paragraphs
	 */
	public int getParagraphCount() {
		return _paragraphs.size();
	}
	
	/**
	 * NOTE: this has to loop through all paragraphs. So use this method carefully!
	 * 
	 * @return the total number of lines in this environment
	 */
	public int getLineCount() {
		int total = 0;
		int len = _paragraphs.size();
		for(int i = 0;i < len;i++) {
			Paragraph p = (Paragraph)_paragraphs.get(i);
			total += p.getLineCount();
		}
		
		return total;
	}
	
	/**
	 * NOTE: this has to loop through all paragraphs and lines. So use this method carefully!
	 * 
	 * @return the total number of sections in this environment
	 */
	public int getSectionCount() {
		int total = 0;
		int len = _paragraphs.size();
		for(int i = 0;i < len;i++) {
			Paragraph p = (Paragraph)_paragraphs.get(i);
			total += p.getSectionCount();
		}
		
		return total;
	}
	
	/**
	 * @return the first paragraph in this environment
	 */
	public Paragraph getFirstParagraph() {
		return (Paragraph)_paragraphs.getFirst();
	}
	
	/**
	 * @return the last paragraph in this environment
	 */
	public Paragraph getLastParagraph() {
		return (Paragraph)_paragraphs.getLast();
	}
	
	/**
	 * @return the first line in this environment
	 */	
	public Line getFirstLine() {
		Paragraph p = (Paragraph)_paragraphs.getFirst();
		return p.getFirstLine();
	}
	
	/**
	 * @return the last line in this environment
	 */
	public Line getLastLine() {
		Paragraph p = (Paragraph)_paragraphs.getLast();
		return p.getLastLine();
	}
	
	/**
	 * @return the first section in this environment
	 */	
	public Section getFirstSection() {
		Paragraph p = (Paragraph)_paragraphs.getFirst();
		return p.getFirstSection();
	}
	
	/**
	 * @return the last section in this environment
	 */
	public Section getLastSection() {
		Paragraph p = (Paragraph)_paragraphs.getLast();
		return p.getLastSection();
	}
	
	/**
	 * returns the section at given cursor-position
	 * 
	 * @param cursorPos the cursor-position
	 * @return the Section-object
	 * @throws InvalidTextPositionException if the position is invalid
	 */
	public ContentSection getSectionAt(int cursorPos) throws InvalidTextPositionException {
		if(cursorPos < 0)
			throw new InvalidTextPositionException(cursorPos);
		
		Paragraph p = getParagraphAtPosition(cursorPos);
		if(p != null)
			return getSectionAt(p,cursorPos);
		
		throw new InvalidTextPositionException(cursorPos);
	}
	
	/**
	 * returns the section at given cursor-position
	 * assumes that the cursor-position is in the given paragraph
	 * 
	 * @param p the paragraph the cursor-position belongs to
	 * @param cursorPos the cursor-position
	 * @return the Section-object
	 * @throws InvalidTextPositionException if the position is invalid
	 */
	public ContentSection getSectionAt(Paragraph p,int cursorPos) throws InvalidTextPositionException {
		if(cursorPos < 0)
			throw new InvalidTextPositionException(cursorPos);
		
		return p.getSectionAt(cursorPos - p.getElementStartPos());
	}
	
	/**
	 * @param index the index of the Paragraph
	 * @return the Paragraph with given index or null if not found
	 */
	public Paragraph getParagraph(int index) {
		if(index < 0 || index >= _paragraphs.size())
				return null;
		
		return (Paragraph)_paragraphs.get(index);
	}
	
	/**
	 * @return the linked list with all paragraphs
	 */
	MyLinkedList getParagraphs() {
		return _paragraphs;
	}
	
	/**
	 * adds a new Environment to this environment
	 * 
	 * @param env the Environment
	 * @param position the position in the text
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param forceNewLine do you want to insert a newline after the environment?
	 * @return the number of added chars
	 */
	int addEnvironmentAt(Environment env,int position,boolean isListPoint,boolean forceNewLine) {
		int inc = env.getElementLength();
		
		Paragraph p = getParagraphAtPosition(position);
		
		// move to a new paragraph
		ContentSection s = moveToNewParagraph(p,position,isListPoint);
		p = s.getSectionParagraph();
		inc++;
		
		boolean addAfter = true;
		
		// is the paragraph empty?
		if(!p.isEmpty()) {
			p = addNewParagraph((Paragraph)p.getPrev(),isListPoint);
			addAfter = false;
			inc++;
		}
		
		// insert environment in empty paragraph
		p.insertEnvironment(env);
		p.increaseElementEndPos(env.getElementLength());
		
		// adjust following paragraphs
		adjustParagraphs((Paragraph)p.getNext(),env.getElementLength());
		
		// do we have to insert a new paragraph after the env?
		if(addAfter && (forceNewLine || p.isLast() ||
				((Paragraph)p.getNext()).containsEnvironment())) {
			addNewParagraph(p,isListPoint);
			inc++;
		}
		
		return inc;
	}
	
	/**
	 * adds a new image section at given position
	 * 
	 * @param position the position where you want to insert the smiley
	 * @param img the image of the image-section
	 * @return the new section of the cursor
	 */
	ContentSection addImageSectionAt(int position,SecImage img) {
		Paragraph p = getParagraphAtPosition(position);
		return addImageSectionAt(p,position,img);
	}
	
	/**
	 * adds a new image section at given position
	 * assumes that the given position is in the given paragraph
	 * 
	 * @param p the Paragraph of the position
	 * @param position the position where you want to insert the smiley
	 * @param img the image of the image-section
	 * @return the new section of the cursor
	 */
	ContentSection addImageSectionAt(Paragraph p,int position,SecImage img) {
		ContentSection sec = p.addImageSectionAt(position - p.getElementStartPos(),img);
		
		adjustParagraphs((Paragraph)p.getNext(),1);
		
		return sec;
	}
	
	/**
	 * adds the given text at the given position to the corresponding paragraph
	 * 
	 * @param text the text to add
	 * @param position the position where to add the text (in this environment)
	 * @return the section where the text has been added
	 */
	ContentSection addTextAt(String text,int position) {
		return addTextAt(text,position,null);
	}
	
	/**
	 * adds the given text at the given position to the corresponding paragraph
	 * 
	 * @param text the text to add
	 * @param position the position where to add the text (in this environment)
	 * @param attributes the attributes of the text to add
	 * @return the section where the text has been added
	 */
	ContentSection addTextAt(String text,int position,TextAttributes attributes) {
		Paragraph p = getParagraphAtPosition(position);
		return addTextAt(text,p,position,attributes);
	}
	
	/**
	 * adds the given text at the given position to the corresponding line
	 * 
	 * @param text the text to add
	 * @param p the Paragraph of the position
	 * @param position the position where to add the text (in this environment)
	 * @param attributes the attributes of the text to add
	 * @return the section where the text has been added
	 */
	ContentSection addTextAt(String text,Paragraph p,int position,TextAttributes attributes) {
		ContentSection s = p.addTextAt(text,position - p.getElementStartPos(),attributes);
		
		adjustParagraphs((Paragraph)p.getNext(),text.length());
		
		return s;
	}
	
	/**
	 * adds a new paragraph after the given one
	 * 
	 * @param prev the paragraph after which to add the new paragraph
	 * @param isListPoint should the new paragraph be a list-point?
	 * @return the created paragraph
	 */
	Paragraph addNewParagraph(Paragraph prev,boolean isListPoint) {
		int pos = prev.getElementEndPos() + 2;
		Paragraph p = new Paragraph(_env,pos,pos - 1);
		p.setListPoint(isListPoint);
		
		_paragraphs.addAfter(prev,p);
		
		adjustParagraphs((Paragraph)p.getNext(),1);
		
		return p;
	}
	
	/**
	 * deletes the paragraph with given index
	 * adjusts the positions of all following paragraphs
	 * 
	 * @param index the paragraph-index
	 */
	void remove(int index) {
		Paragraph p = (Paragraph)_paragraphs.get(index);
		if(p != null)
			remove(p);
	}
	
	/**
	 * removes the given paragraph from the container
	 * adjusts the positions of the following paragraphs
	 * 
	 * @param p the Paragraph to remove
	 */
	void remove(Paragraph p) {
		_paragraphs.remove(p);
		
		adjustParagraphs((Paragraph)p.getNext(),-(p.getElementLength() + 1));
		_env.getTextField().getPaintPosManager().markAllDirty();
	}
	
	/**
	 * removes the text between the start- and end-position in the given
	 * paragraph and adjusts all positions of the following paragraphs
	 * 
	 * @param p the paragraph
	 * @param start the start-position
	 * @param end the end-position
	 * @return the number of removed characters
	 */
	int removeTextInParagraph(Paragraph p,int start,int end) {
		// remove text in paragraph
		int oldEnd = p.getElementEndPos();
		int count = p.removeText(start,end);
		
		// adjust paragraph position
		Section last = p.getLastSection();
		p.setElementPos(p.getElementStartPos(),last.getElementEndPos() + p.getElementStartPos());
		int diff = oldEnd - p.getElementEndPos();
		
		// adjust following ones
		adjustParagraphs((Paragraph)p.getNext(),-diff);
		
		return count;
	}
	
	/**
	 * adjusts the positions from the given paragraph to the last paragraph<br>
	 * If <code>amount</br> is negative it will be substracted!
	 * 
	 * @param start the start-paragraph
	 * @param amount the amount to add
	 */
	private void adjustParagraphs(Paragraph start,int amount) {
		if(amount != 0) {
			while(start != null) {
				start.increaseElementPos(amount);
				start = (Paragraph)start.getNext();
			}
		}
	}
	
	/**
	 * determines the Paragraph which contains the given position
	 * 
	 * @param position the position to search for
	 * @return the Paragraph (may NOT be null!)
	 */
	public Paragraph getParagraphAtPosition(int position) {
		Paragraph first = (Paragraph)_paragraphs.getFirst();
		if(position <= first.getElementEndPos() + 1)
			return first;
		
		// the lines are sorted, so we can use binarySearch :)
		int index = _paragraphs.getIndexBinarySearch(new Integer(position),new Comparator() {
			public int compare(Object arg0,Object arg1) {
				if(arg0 instanceof Paragraph && arg1 instanceof Integer) {
					int pos = ((Integer)arg1).intValue();
					Paragraph p = (Paragraph)arg0;
					if(pos < p.getElementStartPos())
						return 1;
					
					if(pos > p.getElementEndPos() + 1)
						return -1;
				}
				
				return 0;
			}
		});
		
		if(index >= 0)
			return (Paragraph)_paragraphs.get(index);
		
		// return the last line
		return (Paragraph)_paragraphs.getLast();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		int len = _paragraphs.size();
		for(int i = 0;i < len;i++)
			buf.append(_paragraphs.get(i) + "\n");

		return buf.toString();
	}
}