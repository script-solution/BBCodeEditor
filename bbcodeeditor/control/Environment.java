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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bbcodeeditor.control.actions.*;
import bbcodeeditor.control.export.Exporter;
import bbcodeeditor.control.export.bbcode.BBCodeExportContent;
import bbcodeeditor.control.tools.MutablePointer;
import bbcodeeditor.control.tools.MyLinkedList;
import bbcodeeditor.control.tools.TextPart;
import bbcodeeditor.control.view.EnvironmentView;
import bbcodeeditor.control.view.IEnvironmentView;



/**
 * The sense of Environments is that it is possible to change the content-style of specific
 * areas in the control. Environments should control everything which may be different
 * in different areas.<br>
 * For example there is a CodeEnvironment which doesn't allow sub-environments and
 * formating.
 * <p>
 * The Environment contains a ParagraphContainer and manages the cursor-position in the
 * environment. Every Environment may have different default-attributes and may or may
 * not allow formating and sub-environments.
 * <p>
 * Environments are Sections, too, like ContentSections. Therefore they are directy
 * integrated into the "Line- and Section-model".<br>
 * If the first Section of a Line is an Environment there are no other Sections in this
 * line. Additionally the paragraph of the line contains no other lines!
 * 
 * @author hrniels
 */
public class Environment extends Section {
	
	/**
	 * the textfield instance
	 */
	protected final AbstractTextField _textArea;
	
	/**
	 * the paragraph-container which contains the paragraphs of this environment
	 */
	protected final ParagraphContainer _paragraphs;
	
	/**
	 * the ContentSection at the current cursor-position<br>
	 * will be <code>null</code> if the cursor is not in this Environment
	 */
	protected ContentSection _currentSection = null;
	
	/**
	 * the cursor-position in this Environment<br>
	 * will be <code>-1</code> if the cursor is not in this Environment
	 */
	protected int _currentCursorPos = -1;
	
	/**
	 * this is used to change the horizontal cursor position only if we change this 
	 * explicitly, not if we walk up or down<br>
	 * So this is the cursor-position of the last horizontal cursor-position-change.
	 */
	protected int _currentCursorPosLHC = -1;
	
	/**
	 * the cached value for the containsStyles property
	 */
	protected boolean _containsStyles;
	
	/**
	 * have we reached the max nested level?
	 */
	protected boolean _maxSubEnvs = false;
	
	/**
	 * is this Environment selected?
	 */
	protected boolean _selected = false;
	
	/**
	 * constructor
	 * 
	 * @param textArea the AdvancedTextArea-object
	 * @param parent the parent environment
	 * @param line the line of this environment
	 * @param p the paragraph of this environment
	 */
	Environment(AbstractTextField textArea,Environment parent,Line line,Paragraph p) {
		super(parent,0,-1,line,p);

		_textArea = textArea;
		_view = new EnvironmentView(this);
		refreshContainsStyles();
		
		_paragraphs = new ParagraphContainer(this);
		
		// take care of the environment-nest-limit
		if(getLayer() >= _textArea.getMaxTagNestingLevel())
			_maxSubEnvs = true;
	}
	
	/**
	 * @return the IEnvironment-view implementation
	 */
	public IEnvironmentView getEnvView() {
		return (IEnvironmentView)_view;
	}
	
	public AbstractTextField getTextField() {
		return _textArea;
	}
	
	/**
	 * @return the environment-type (See ContentTypes.ENV_*)
	 */
	public int getType() {
		return EnvironmentTypes.ENV_ROOT;
	}
	
	/**
	 * refreshes the cache of containsStyles
	 */
	void refreshContainsStyles() {
		_containsStyles = _textArea.getEnvBoolProperty(EnvironmentProperties.CONTAINS_STYLES,
				getType());
	}
	
	/**
	 * @return true if this environment can contain sub environments
	 */
	public boolean containsSubEnvironments() {
		return !_maxSubEnvs &&
			_textArea.getEnvBoolProperty(EnvironmentProperties.CONTAINS_ENVS,getType());
	}
	
	/**
	 * @return true if this environment can contain styles
	 */
	public boolean containsStyles() {
		return _containsStyles;
	}
	
	/**
	 * Checks wether the given attributes are allowed in this environment
	 * 
	 * @param attributes the attribute-map
	 * @return true if so
	 */
	public boolean attributesAllowed(Map attributes) {
		if(_containsStyles)
			return true;
		
		if(attributes.size() != 1)
			return false;
		
		if(attributes.keySet().iterator().next().equals(TextAttributes.HIGHLIGHT))
			return true;
		
		return false;
	}
	
	/**
	 * @return the wordwrap-strategie for this environment
	 */
	public IWordWrap getWordWrapStrategie() {
		return (IWordWrap)_textArea.getEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				getType());
	}
	
	/**
	 * @return the first line in this environment
	 */	
	public Line getFirstLine() {
		return _paragraphs.getFirstLine();
	}
	
	/**
	 * @return the last line in this environment
	 */
	public Line getLastLine() {
		return _paragraphs.getLastLine();
	}

	/**
	 * determines the section at given position
	 * 
	 * @param position the position (in this environment)
	 * @return the section at given position or null if not found
	 */
	public ContentSection getSectionAt(int position) {
		try {
			Paragraph p = _paragraphs.getParagraphAtPosition(position);
			if(p.containsEnvironment()) {
				Environment env = (Environment)p.getFirstSection();
				return env.getSectionAt(position - p.getElementStartPos());
			}
			
			return p.getSectionAt(position - p.getElementStartPos());
		}
		catch(InvalidTextPositionException e) {
			return null;
		}
	}
	
	/**
	 * Returns the paragraph at given index. Note that the paragraph
	 * might contain an environment!
	 * 
	 * @param index the index of the paragraph
	 * @return the paragraph or null if not found
	 */
	public Paragraph getParagraph(int index) {
		return _paragraphs.getParagraph(index);
	}
	
	/**
	 * @return the linked list with all paragraphs
	 */
	public MyLinkedList getParagraphs() {
		return _paragraphs.getParagraphs();
	}
	
	/**
	 * @return the number of paragraphs
	 */
	public int getParagraphCount() {
		return _paragraphs.getParagraphCount();
	}

	/**
	 * @return the number of lines
	 */
	public int getLineCount() {
		return _paragraphs.getLineCount();
	}
	
	/**
	 * @return the number of sections
	 */
	public int getSectionCount() {
		return _paragraphs.getSectionCount();
	}
	
	/**
	 * @return the current cursor-position
	 */
	public int getCurrentCursorPos() {
		return _currentCursorPos;
	}
	
	/**
	 * @return the position of the cursor at the last _horizontal_ position change
	 */
	public int getCurrentCursorLHCPos() {
		return _currentCursorPosLHC;
	}
	
	/**
	 * @return the global cursor-position
	 */
	public int getGlobalCurrentCursorPos() {
		return _currentCursorPos + getGlobalStartPos();
	}
	
	/**
	 * @return the current section
	 */
	public ContentSection getCurrentSection() {
		return _currentSection;
	}
	
	/**
	 * @return the first paragraph in this environment
	 */
	public Paragraph getFirstParagraph() {
		return _paragraphs.getFirstParagraph();
	}
	
	/**
	 * @return the last paragraph in this environment
	 */
	public Paragraph getLastParagraph() {
		return _paragraphs.getLastParagraph();
	}
	
	/**
	 * @return the layer of this environment (the number of parent-environments)
	 */
	public int getLayer() {
		int count = 0;
		Environment parent = this;
		while(parent._env != null) {
			parent = parent._env;
			count++;
		}
		return count;
	}
	
	/**
	 * @return the global start-position of this environment
	 */
	public int getGlobalStartPos() {
		Environment env = this;
		int pos = env.getStartPosInEnv();
		while(env.getParentEnvironment() != null) {
			env = env.getParentEnvironment();
			pos += env.getStartPosInEnv();
		}

		return pos;
	}
	
	/**
	 * clears all content in this environment
	 */
	void clear() {
		_paragraphs.clear();
		_currentSection = (ContentSection)_paragraphs.getFirstSection();
		
		setElementPos(0,-1);
		_currentCursorPos = 0;
		_currentCursorPosLHC = 0;
	}

	/**
	 * Searches for the given text in the given interval.<br>
	 * If the text has been found the TextPart-instance (the match) will be
	 * returned. Otherwise null will be returned.
	 * If <code>forward</code> is true the method searches forward ;)
	 * 
	 * @param text the text to search for
	 * @param start the start-position
	 * @param end the end-position
	 * @param caseSensitive perform a case-sensitive search?
	 * @param forward search forward?
	 * @return the last TextPart if something has been found or null if not
	 */
	public TextPart getNextOccurrence(String text,int start,int end,boolean caseSensitive,
			boolean forward) {
		int envStart = getGlobalStartPos();
		int textLen = text.length();
		TextPart part = null;
		
		Paragraph p = _paragraphs.getParagraphAtPosition(forward ? start : end);
		do {
			int pStart = p.getElementStartPos();
			int pEnd = p.getElementEndPos();
			
			// are we finished?
			if(forward ? end < pStart : start > pEnd)
				break;
			
			// is it an environment?
			if(p.containsEnvironment()) {
				Environment env = (Environment)p.getFirstSection();
				TextPart res = env.getNextOccurrence(text,start - pStart,end - pStart,
						caseSensitive,forward);
				if(res != null)
					return res;
			}
			else {
				// collect the text to search in
				String pText = p.getText();
				int tStart = Math.max(0,start - pStart);
				int tEnd = Math.min(p.getElementLength(),end - pStart);
				String sText = pText.substring(tStart,tEnd);
				if(!caseSensitive)
					sText = sText.toLowerCase();
				
				// search the text
				int index = forward ? sText.indexOf(text) : sText.lastIndexOf(text);
				if(index >= 0) {
					// create the match
					String match = sText.substring(index,index + textLen);
					int matchStart = index + pStart + tStart + envStart;
					int matchEnd = matchStart + textLen;
					return new TextPart(match,matchStart,matchEnd);
				}
			}
			
			p = forward ? (Paragraph)p.getNext() : (Paragraph)p.getPrev();
		} while(p != null);
		
		return part;
	}
	
	/**
	 * Searches for the given text in the given interval.<br>
	 * A List with TextPart-objects will be collected which contains all found
	 * occurrences
	 * 
	 * @param text the text to search for
	 * @param start the start-position
	 * @param end the end-position
	 * @param caseSensitive perform a case-sensitive search?
	 * @param results the List with the TextPart-objects to create
	 */
	public void collectSearchResults(String text,int start,int end,boolean caseSensitive,
			List results) {
		int envStart = getGlobalStartPos();
		int textLen = text.length();
		
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			int pStart = p.getElementStartPos();
			
			// are we finished?
			if(end < pStart)
				break;
			
			// is it an environment?
			if(p.containsEnvironment()) {
				Environment env = (Environment)p.getFirstSection();
				env.collectSearchResults(text,start - pStart,end - pStart,caseSensitive,results);
			}
			else {
				// collect the text to search in
				String pText = p.getText();
				int tStart = Math.max(0,start - pStart);
				int tEnd = Math.min(p.getElementLength(),end - pStart);
				String sText = pText.substring(tStart,tEnd);
				if(!caseSensitive)
					sText = sText.toLowerCase();
				
				// search the text
				int index = sText.indexOf(text);
				while(index >= 0) {
					// create the match
					String match = sText.substring(index,index + textLen);
					int matchStart = index + pStart + tStart + envStart;
					int matchEnd = matchStart + textLen;
					results.add(new TextPart(match,matchStart,matchEnd));
					
					// search the next one
					index = sText.indexOf(text,index + textLen);
				}
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null);
	}
	
	/**
	 * collects all attributes in the given interval.<br>
	 * Attributes which have in the whole interval the same value will
	 * have this value in the map. Attributes which change in the interval
	 * will be in the map but have a null-value.<br>
	 * Other attributes will not be in the map
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @param attributes the map which will be collected
	 */
	void collectAttributes(int start,int end,MutablePointer attributes) {
		// ignore envs which don't contain styles
		if(!containsStyles())
			return;
		
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			int pStart = p.getElementStartPos();
			
			// do we "hit" this line?
			if(start <= p.getElementEndPos() && end >= pStart)
				p.collectAttributes(start - pStart,end - pStart,attributes);
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());
	}
	
	/**
	 * collects all regions with different attributes in the given interval
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @param attributes an List with all attributes to check
	 * @return true if all sections in this interval have enabled the attribute
	 */
	List getAttributeRegions(int start,int end,List attributes) {
		List attrRegions = new ArrayList();
		
		// ignore envs which don't contain styles
		if(!containsStyles())
			return attrRegions;
		
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			int pStart = p.getElementStartPos();
			
			// do we "hit" this line?
			if(start <= p.getElementEndPos() + 1 && end >= pStart) {
				List lineRegions = p.getAttributeRegions(start - pStart,end - pStart,attributes);
				if(attrRegions.size() > 0 && lineRegions.size() > 0) {
					SetAttributeActionPart last = (SetAttributeActionPart)attrRegions.get(attrRegions.size() - 1);
					TextAttributes lastAttr = last.getAttributes();
					SetAttributeActionPart first = (SetAttributeActionPart)lineRegions.get(0);
					TextAttributes firstAttr = first.getAttributes();
					
					if(TextAttributes.compareAttributes(this,lastAttr,firstAttr)) {
						lineRegions.remove(0);
						attrRegions.remove(attrRegions.size() - 1);
						last = new SetAttributeActionPart(last.getStartPosition(),first.getEndPosition(),
								last.getAttributes());
						attrRegions.add(last);
					}
				}
				
				attrRegions.addAll(lineRegions);
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());
		
		return attrRegions;
	}
	
	/**
	 * applies the given attribute to the given interval
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @param attributes all attributes to apply
	 * 				null-values will remove an attribute
	 * @param replace replace the attributes in the sections?
	 * @param allowAll if true the attributes will be set in envs that contain
	 * 				no styles, too
	 * @return true if something has been changed
	 */
	boolean applyAttributes(int start,int end,TextAttributes attributes,
			boolean replace,boolean allowAll) {
		if(!allowAll && !containsStyles())
			return false;
		
		// does this environment perform pixel based wordwrap?
		boolean wordWrapPixelBased = _textArea.getEnvProperty(
				EnvironmentProperties.WORD_WRAP_STRATEGIE,getType()) instanceof WordWrapPixelBased;
		
		boolean changed = false;
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			int pStart = p.getElementStartPos();
			
			// do we "hit" this line?
			if(start <= p.getElementEndPos() + 1 && end >= pStart) {
				boolean res = p.applyAttributes(start - pStart,end - pStart,attributes,replace,
						allowAll,true);
				if(res) {
					changed = true;

					// if the wordwrap is pixel based we have to perform wordwrap for the case
					// that the width has changed
					if(wordWrapPixelBased)
						_textArea.getWordwrapManager().markDirty(p);
				}
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());
		
		return changed;
	}
	
	/**
	 * collections all paragraph-alignment regions in the given interval
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @return a List with all alignment-regions
	 */
	List getAlignmentRegions(int start,int end) {
		List attrRegions = new ArrayList();
		
		// ignore envs which don't contain styles
		if(!containsStyles())
			return attrRegions;
		
		boolean foundFirst = false;
		int startPos = start;
		int align = -1;
		
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			// do we "hit" this line?
			if(start <= p.getElementEndPos() && end >= p.getElementStartPos()) {
				if(align != p.getHorizontalAlignment()) {
					if(foundFirst) {
						SetLineAlignmentActionPart action = new SetLineAlignmentActionPart(startPos,
								p.getElementStartPos() - 1,align);
						attrRegions.add(action);
					}
					
					foundFirst = true;
					start = p.getElementStartPos();
					align = p.getHorizontalAlignment();
				}
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());
		
		// add last region
		SetLineAlignmentActionPart action = new SetLineAlignmentActionPart(startPos,
				Math.min(getElementEndPos(),end),align);
		attrRegions.add(action);
		
		return attrRegions;
	}
	
	/**
	 * sets the alignment of the paragraphs in the given interval to given value
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @param align the new alignment
	 * @return true if something has been changed
	 */
	boolean setAlignment(int start,int end,int align) {
		if(!containsStyles())
			return false;
		
		boolean changed = false;
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			// do we "hit" this line?
			// in this case we also want to apply the alignment if we hit just the line-end
			if(start <= p.getElementEndPos() + 1 && end >= p.getElementStartPos()) {
				if(p.containsEnvironment()) {
					Environment env = (Environment)p.getFirstSection();
					int envStart = start - p.getElementStartPos();
					int envEnd = end - p.getElementStartPos();
					boolean res = env.setAlignment(envStart,envEnd,align);
					if(res)
						changed = true;
				}
				else if(p.setHorizontalAlignment(align))
					changed = true;
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());
		
		return changed;
	}
	
	/**
	 * corrects the current section
	 * assumes that the position has NOT changed, just the pointer to the current
	 * section may have changed
	 */
	void correctCurrentSection() {
		try {
			_currentSection = _paragraphs.getSectionAt(_currentCursorPos);
		}
		catch(InvalidTextPositionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * enters this section with the cursor at the beginning of the environment
	 */
	void enterCursorFront() {
		enterCursor((ContentSection)_paragraphs.getFirstSection(),0,0);
	}
	
	/**
	 * enters this section with the cursor at the end of the environment
	 */
	void enterCursorBack() {
		ContentSection s = (ContentSection)_paragraphs.getLastSection();
		int cp = s.getEndPosInEnv() + 1;
		enterCursor(s,cp,cp);
	}
	
	/**
	 * enter this section with the cursor.
	 * 
	 * @param section the section which contains the cursor
	 * @param cursorPos the position of the cursor in this environment
	 * @param cursorPosLHC the position of the cursor at the last _horizontal_ position change
	 */
	void enterCursor(ContentSection section,int cursorPos,int cursorPosLHC) {
		_currentSection = section;
		_currentCursorPos = cursorPos;
		_currentCursorPosLHC = cursorPosLHC;
	}
	
	/**
	 * the cursor leaves this section
	 */
	void leaveCursor() {
		_currentSection = null;
		_currentCursorPos = -1;
		_currentCursorPosLHC = -1;
	}
	
	/**
	 * Adds an environment to this one at the current cursor-position
	 * 
	 * @param env the environment you want to add
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param forceNewLine do you want to insert a newline after the environment?
	 * @return true if a new-line has NOT been inserted in front of the environment
	 */
	boolean addChildEnvironment(Environment env,boolean isListPoint,boolean forceNewLine) {
		return addChildEnvironment(env,_currentCursorPos,isListPoint,forceNewLine);
	}
	
	/**
	 * Adds an environment at the given position
	 * 
	 * @param env the environment you want to add
	 * @param pos the position in this env
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param forceNewLine do you want to insert a newline after the environment?
	 * @return true if a new-line has NOT been inserted in front of the environment
	 */
	boolean addChildEnvironment(Environment env,int pos,boolean isListPoint,boolean forceNewLine) {
		if(!containsSubEnvironments())
			return false;
		
		int count = _paragraphs.addEnvironmentAt(env,pos,isListPoint,forceNewLine);
		adjustEnvPositions(count);
		
		return count < env.getElementLength() + 2;
	}

	/**
	 * adds an image at the given position
	 * 
	 * @param image the image to add
	 * @param pos the position
	 */
	void addImage(SecImage image,int pos) {
		if(!containsStyles())
			return;
		
		Paragraph p = getParagraphAtPosition(pos);
		_paragraphs.addImageSectionAt(p,pos,image);
		adjustEnvPositions(1);

		_textArea.getWordwrapManager().markDirty(p);
		updateHighlighting(p);
	}
	
	/**
	 * adds a string at the cursor-position
	 * 
	 * @param text the text you want to add
	 */
	void addStringAtCursor(String text) {
		addStringAtCursor(text,null);
	}
	
	/**
	 * adds a string at the cursor-position. this will create a new section!
	 * 
	 * @param text the text you want to add
	 * @param attributes the attributes of the text
	 */
	void addStringAtCursor(String text,TextAttributes attributes) {
		// if we don't support styles here, use no attributes
		if(!containsStyles())
			attributes = null;
		
		int len = text.length();
		Paragraph p = _currentSection.getSectionParagraph();
		_currentSection = _paragraphs.addTextAt(text,p,_currentCursorPos,attributes);
		adjustEnvPositions(text.length());

		_textArea.getWordwrapManager().markDirty(p);
		updateHighlighting(p);
		
		_currentCursorPos += len;
		_currentCursorPosLHC = _currentCursorPos;
		correctCurrentSection();
	}
	
	/**
	 * determines if the previous char can be removed
	 * this has to be the current environment!
	 * 
	 * @return 1 if the char can simply be removed
	 * 				 0 if we should walk backwards
	 * 				-1 if the current environment should be removed
	 */
	int getRemovePreviousCharType() {
		// beginning of the env?
		if(_currentCursorPos == 0) {
			if(getElementLength() == 0)
				return -1;
			
			return 0;
		}
		
		// behind an env?
		if(_currentCursorPos == _currentSection.getStartPosInEnv() &&
				_currentSection.isFirstInParagraph()) {
			Paragraph prev = (Paragraph)_currentSection.getSectionParagraph().getPrev();
			if(prev.containsEnvironment())
				return 0;
		}
		
		return 1;
	}
	
	/**
	 * removes the previous char if possible (the cursor will be moved)
	 * 
	 * @param cursorPos the position of the cursor
	 * @return the number of removed chars
	 */
	int removePreviousChar(int cursorPos) {
		return removeText(cursorPos - 1,cursorPos);
	}
	
	/**
	 * removes the following char if possible (the cursor will NOT be moved)
	 * 
	 * @param cursorPos the position of the cursor
	 * @return the number of removed chars
	 */
	int removeFollowingChar(int cursorPos) {
		return removeText(cursorPos,cursorPos + 1);
	}
	
	/**
	 * removes the text from start to end in this environment
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the number of removed characters
	 */
	int removeText(int start,int end) {
		// remove the text
		int res = removeTextImpl(start,end);
		
		// we have to refresh the positions of the environment
		Paragraph last = getLastParagraph();
		setElementPos(0,last.getElementEndPos());
		
		Paragraph p = getParagraphAtPosition(start);
		if(p != null && !p.containsEnvironment()) {
			p.getParagraphView().refreshTabWidth();
			updateHighlighting(p);
			_textArea.getWordwrapManager().markDirty(p);
		}
		
		return res;
	}
	
	/**
	 * removes a text from start with the given length
	 *
	 * @param start the start-position of the text you want to delete (in this env)
	 * @param end the lend-position of the text you want to delete (in this env)
	 * @return the number of removed characters
	 */
	protected int removeTextImpl(int start,int end) {
		int c = 0;
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			// do we "hit" this line?
			if(start <= p.getElementEndPos() + 1 && end >= p.getElementStartPos()) {
				// delete the line?
				if(removeParagraph(start,end,p)) {
					int pLen = p.getElementLength();
					_paragraphs.remove(p);
					
					// we can just remove a line-end if there is another line :)
					if(!p.isLast()) {
						// in this case we don't want to increase c, just decrease end
						//if(((Paragraph)p.getNext()).containsEnvironment())
						//	c--;
						pLen++;
					}
					
					// adjust vars
					end -= pLen;
					c += pLen;
				}
				else {
					int pStart = p.getElementStartPos();
					int count = _paragraphs.removeTextInParagraph(p,start - pStart,
							end - pStart);
					c += count;
					end -= count;
					
					// we have to count the line-end if it has to be removed
					if(!p.isLast() && end > p.getElementEndPos() + 1) {
						// don't do that if we have an environment-line
						if(!p.containsEnvironment()) {
							// if the next line is an environment
							boolean inc = true;
							Paragraph next = (Paragraph)p.getNext();
							if(next != null && next.containsEnvironment()) {
								// just increase if we delete the complete env
								if(end <= next.getElementEndPos() + 1)
									inc = false;
							}
							
							if(inc)
								c++;
						}
					}
					
					// is it the last line?
					if(end <= p.getElementEndPos() + 1) {
						if(start < p.getElementStartPos()) {
							// don't move paragraphs with an environment
							if(!p.containsEnvironment()) {
								// prevent that we move the sections to an environment-paragraph
								Paragraph pPara = (Paragraph)p.getPrev();
								if(pPara != null && !pPara.containsEnvironment())
									_paragraphs.moveToPrevParagraph(p);
							}
						}
						break;
					}
				}
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		return c;
	}
	
	/**
	 * collects all actions for the removeText() method in the given interval
	 * 
	 * @param start the start-position in this environment
	 * @param end the end-position in this environment
	 * @param actions the actions which have been collected
	 */
	void collectRemoveTextActions(int start,int end,List actions) {
		int globalStart = getGlobalStartPos();
		int regionStart = Math.max(0,start);
		int regionEnd = regionStart;
		
		Paragraph p = _paragraphs.getParagraphAtPosition(start);
		do {
			// do we "hit" this paragraph?
			if(start <= p.getElementEndPos() + 1 && end >= p.getElementStartPos()) {
				// delete the paragraph?
				if(removeParagraph(start,end,p)) {
					if(p.containsEnvironment()) {
						// do we have to add some text before?
						if(regionStart != regionEnd) {
							Exporter ex = new Exporter(_textArea,new BBCodeExportContent());
							String text = ex.getContent(regionStart + globalStart,regionEnd + globalStart);
							AddTextInEnvActionPart action = new AddTextInEnvActionPart(this,
									globalStart + regionStart,text);
							actions.add(action);
						}
						
						// add environment-action
						Environment env = (Environment)p.getFirstSection();
						actions.add(new AddEnvironmentActionPart(env,globalStart + p.getElementStartPos() - 1,
								p.isListPoint()));
						
						// set start-pos to the start-pos of the next line
						regionStart = p.getElementEndPos() + 2;
					}

					// if the next paragraph is an env-paragraph we don't want to remove the
					// complete paragraph
					Paragraph nextPara = (Paragraph)p.getNext();
					if(nextPara == null || nextPara.containsEnvironment())
						regionEnd = p.getElementEndPos() + 1;
					else
						regionEnd = p.getElementEndPos() + 2;
				}
				else {
					// env-paragraph
					if(p.containsEnvironment()) {
						// add the action if necessary
						if(regionStart != regionEnd) {
							Exporter ex = new Exporter(_textArea,new BBCodeExportContent());
							String text = ex.getContent(regionStart + globalStart,regionEnd + globalStart);
							AddTextInEnvActionPart action = new AddTextInEnvActionPart(this,
									globalStart + regionStart,text);
							actions.add(action);
						}
						
						if(regionStart < p.getElementStartPos())
							actions.add(new WalkForwardActionPart());
						
						// remove in environment
						Environment env = (Environment)p.getFirstSection();
						int lineStart = p.getElementStartPos();
						env.collectRemoveTextActions(start - lineStart,end - lineStart,actions);
						
						// add a forward-action if there is any text to delete left
						if(end > p.getElementEndPos() + 2)
							actions.add(new WalkForwardActionPart());
						
						// set start-pos to the line-start of the next paragraph
						regionStart = p.getElementEndPos() + 2;
						regionEnd = regionStart;
					}
					// simple paragraph
					else
						regionEnd = Math.min(_endPos + 1,Math.min(end,p.getElementEndPos() + 1));
				}
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null && end >= p.getElementStartPos());

		// add the remaining text, if any
		if(regionStart != regionEnd) {
			Exporter ex = new Exporter(_textArea,new BBCodeExportContent());
			String text = ex.getContent(regionStart + globalStart,regionEnd + globalStart);
			AddTextInEnvActionPart action = new AddTextInEnvActionPart(this,
					globalStart + regionStart,text);
			actions.add(action);
		}
	}
	
	/**
	 * determines if the given paragraph should be removed completely
	 * 
	 * @param start the start-position to remove in this environment
	 * @param end the end-position to remove in this environment
	 * @param p the paragraph
	 * @return true if the paragraph should be removed completely
	 */
	private boolean removeParagraph(int start,int end,Paragraph p) {
		boolean pDelete = start < p.getElementStartPos() && end > p.getElementEndPos() + 1;
		
		if(pDelete) {
			// don't remove the first paragraph
			if(p.isFirst())
				pDelete = false;
			// don't remove the only paragraph in this environment
			if(_paragraphs.getLineCount() <= 1)
				pDelete = false;
			// don't remove the paragraph if the previous contains an environment
			Paragraph prev = (Paragraph)p.getPrev();
			if(prev != null && prev.containsEnvironment())
				pDelete = false;
		}
		
		return pDelete;
	}
	
	/**
	 * walks to the given position. the position may also be in a subenvironment
	 * therefore the environment in which the cursor is will be returned
	 * 
	 * @param cursorPos the position to walk to
	 * @return the environment in which the position is
	 */
	Environment goToPosition(int cursorPos) {
		Paragraph p = _paragraphs.getParagraphAtPosition(cursorPos);
		if(p.containsEnvironment()) {
			Environment env = (Environment)p.getFirstSection();
			return env.goToPosition(cursorPos - p.getElementStartPos());
		}
		
		// walk to the position
		try {
			_currentSection = _paragraphs.getSectionAt(cursorPos);
			_currentCursorPos = cursorPos;
			_currentCursorPosLHC = cursorPos;
		}
		catch(InvalidTextPositionException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	/**
	 * Updates the highlighting in all paragraphs
	 */
	void updateHighlighting() {
		// reset the highlighters
		Paragraph p = getFirstParagraph();
		do {
			p.getHighlighter().resetHighlighter();
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		// update highlighting
		p = getFirstParagraph();
		do {
			// TODO perhaps we can optimize that by skipping paragraphs
			// that have already been highlighted?
			// updateHighlighting(p) will update the following paragraphs in
			// some cases
			
			if(p.containsEnvironment()) {
				Environment parent = (Environment)p.getFirstSection();
				parent.updateHighlighting();
			}
			else
				updateHighlighting(p);
		
			p = (Paragraph)p.getNext();
		} while(p != null);
	}
	
	/**
	 * Updates the highlighting in the given paragraph and, if necessary, also
	 * in the required paragraphs around the given one.
	 * 
	 * @param p the paragraph which has changed
	 * @return true if multiple lines have been changed
	 */
	boolean updateHighlighting(Paragraph p) {
		// is there nothing to highlight?
		if(!(this instanceof CodeEnvironment) ||
				((CodeEnvironment)this).getHighlightSyntax() == null)
			return false;
		
		int i = 0;
		do {
			ParagraphHighlighter phl = p.getHighlighter();
			
			// look if there is a prev-paragraph we should take a look at
			Paragraph prev = (Paragraph)p.getPrev();
			if(prev != null) {
				ParagraphHighlighter prevhl = prev.getHighlighter();
				
				// determine if this or the previous paragraph contains comments
				boolean prevContainsComment = prevhl.containsCommentOrStart();
				Object prevCommentId = prevContainsComment ? prevhl.getCommentId() : null;
				boolean pContainsCommentEnd;
				// if the previous contains a comment we have to search for the comment-end
				// of the type of the previous comment
				if(prevContainsComment)
					pContainsCommentEnd = phl.containsCommentOrEnd(prevCommentId);
				// otherwise we simply search for any comment-end
				else
					pContainsCommentEnd = phl.containsCommentOrEnd();
				
				// determine if this or the previous paragraph contains strings
				boolean prevContainsString = prevhl.containsStringOrStart();
				Object prevStringId = prevContainsString ? prevhl.getStringId() : null;
				boolean pContainsStringEnd;
				if(prevContainsString)
					pContainsStringEnd = phl.containsStringOrEnd(prevStringId);
				else
					pContainsStringEnd = phl.containsStringOrEnd();
				
				// if the comment-state has changed and the prev contains comments
				// we have to highlight this paragraph as a comment
				if(prevContainsComment && !pContainsCommentEnd)
					phl.highlightAsComment(prevCommentId);
				// the same for strings
				else if(prevContainsString && !pContainsStringEnd)
					phl.highlightAsString(prevStringId);
				// the other way around. it was a comment/string but is no more
				// so we want to highlight it by default
				else if((!prevContainsString && pContainsStringEnd) ||
						(!prevContainsComment && pContainsCommentEnd))
					phl.highlight();
				// if we are updating the following paragraphs and there has
				// nothing changed, we can break here
				else if(i > 0)
					break;
				// this is the first paragraph to highlight. so we have to check
				// if we should apply the style of the prev paragraph
				else if(prevStringId != null)
					phl.highlightAsString(prevStringId);
				else if(prevCommentId != null)
					phl.highlightAsComment(prevCommentId);
				// ok, highlight by default
				else
					phl.highlight();
			}
			// if there is no prev paragraph, highlight the paragraph by default
			else
				phl.highlight();
			
			// go to the next one
			p = (Paragraph)p.getNext();
			i++;
		} while(p != null);
		
		// we have to repaint all, if more than one paragraph has changed
		if(i > 1)
			_textArea.getRepaintManager().markCompletlyDirty();

		return i > 1;
	}
	
	/**
	 * moves all sections to the next paragraph, starting at the current cursor position
	 */
	void moveToNextParagraph() {
		moveToNextParagraph(null,ParagraphAttributes.ALIGN_UNDEF,true);
	}
	
	/**
	 * moves all sections to the next paragraph, starting at the current cursor position
	 * 
	 * @param attributes the attributes to use for the new line (null = attributes from
	 *				the last section in the last paragraph)
	 * @param align the align for the new paragraph
	 * @param isListPoint should the new line be a new list point?
	 */
	void moveToNextParagraph(TextAttributes attributes,int align,boolean isListPoint) {
		Paragraph p = _currentSection.getSectionParagraph();
		boolean updateOld = _currentCursorPos != p.getElementEndPos() + 1;
		
		_currentSection = _paragraphs.moveToNewParagraph(p,_currentCursorPos,
				isListPoint,attributes);
		
		// apply the alignment to the new paragraph
		Paragraph newPara = _currentSection.getSectionParagraph();
		if(align == ParagraphAttributes.ALIGN_UNDEF)
			newPara.setHorizontalAlignment(p.getHorizontalAlignment());
		else
			newPara.setHorizontalAlignment(align);
		
		adjustEnvPositions(1);
		_currentCursorPos++;
		_currentCursorPosLHC = _currentCursorPos;
		
		if(updateOld)
			updateHighlighting(p);
		updateHighlighting(newPara);
		
		_textArea.getWordwrapManager().markDirty(newPara);
	}
	
	/**
	 * moves the cursor one line up
	 * 
	 * @return > 0: the number of chars to go back
	 * 				 < 0: the controller has to do that
	 */
	int moveCursorUp() {
		int count = 0;
		
		// are we in the first line?
		Line current = _currentSection.getSectionLine();
		if(current.isFirstInEnv())
			return -1;
		
		// is the previous line an environment?
		if(current.isFirst()) {
			Paragraph prev = (Paragraph)current.getParagraph().getPrev();
			if(prev.containsEnvironment())
				return -1;
		}
		
		// calculate new position
		Line lastLine = current.getPrevInEnv();
		MutablePointer newSec = new MutablePointer();
		int[] positions = getPositionInLine(newSec,lastLine);
		
		count = _currentCursorPos - positions[0];
		
		// move to the calculated position
		_currentCursorPos = positions[0];
		_currentCursorPosLHC = positions[1];
		_currentSection = (ContentSection)newSec.getValue();
		
		return count;
	}
	
	/**
	 * moves the cursor one line down
	 * 
	 * @return > 0: the number of chars to go back
	 * 				 < 0: the controller has to do that
	 */
	int moveCursorDown() {
		int count = 0;
		
		// are we in the last line?
		Line current = _currentSection.getSectionLine();
		if(current.isLastInEnv())
			return -1;
		
		// is the next line an environment?
		if(current.isLast()) {
			Paragraph next = (Paragraph)current.getParagraph().getNext();
			if(next.containsEnvironment())
				return -1;
		}

		// calculate new position
		Line nextLine = current.getNextInEnv();
		MutablePointer newSec = new MutablePointer();
		int[] positions = getPositionInLine(newSec,nextLine);
		
		count = positions[0] - _currentCursorPos;
		
		// move to the calculated position
		_currentCursorPos = positions[0];
		_currentCursorPosLHC = positions[1];
		_currentSection = (ContentSection)newSec.getValue();
		
		return count;
	}
	
	/**
	 * moves the cursor back by <code>amount</code>
	 * 
	 * @param amount the number of characters you want to go back
	 * @return 1 if the cursor has been moved
	 * 				-1 if not possible
	 */
	int moveCursorBack(int amount) {
		// will be leave the environment let the controller decide
		if(amount > _currentCursorPos)
			return -1;
		
		// will we leave the current section?
		if(_currentCursorPos - amount < _currentSection.getFirstCursorPosInEnv()) {
			if(_currentSection.isFirstInParagraph()) {
				Paragraph prev = (Paragraph)_currentSection.getSectionParagraph().getPrev();
				// if the first section of the prev paragraph is an environment,
				// we have to leave here
				if(prev.containsEnvironment())
					return -1;
			}
		}
		
		_currentCursorPos -= amount;
		_currentCursorPosLHC = _currentCursorPos;
		
		// determine new section
		ContentSection prev;
		while(_currentCursorPos < _currentSection.getFirstCursorPosInEnv()) {
			prev = (ContentSection)_currentSection.getPrevInParagraph();
			
			if(prev == null) {
				Paragraph pPara = (Paragraph)_currentSection.getSectionParagraph().getPrev();
				if(pPara == null)
					break;
				
				prev = (ContentSection)pPara.getLastSection();
			}
			
			_currentSection = prev;
		}
		
		return 1;
	}
	
	/**
	 * moves the cursor forward by <code>amount</code>
	 * 
	 * @param amount the number of characters you want to go forward
	 * @return 1 if the cursor has been moved
	 * 				-1 if not possible
	 */
	int moveCursorForward(int amount) {
		// will we move behind this env?
		if(_currentCursorPos + amount > getElementLength())
			return -1;
		
		// will we leave the current section?
		if(_currentCursorPos + amount > _currentSection.getLastCursorPosInEnv()) {
			if(_currentSection.isLastInParagraph()) {
				Paragraph next = (Paragraph)_currentSection.getSectionParagraph().getNext();
				// if the first section of the next paragraph is an environment,
				// we have to leave here
				if(next.containsEnvironment())
					return -1;
			}
		}
		
		_currentCursorPos += amount;
		_currentCursorPosLHC = _currentCursorPos;
		
		// determine new section
		ContentSection next;
		while(_currentCursorPos > _currentSection.getLastCursorPosInEnv()) {
			next = (ContentSection)_currentSection.getNextInParagraph();
			
			if(next == null) {
				Paragraph nPara = (Paragraph)_currentSection.getSectionParagraph().getNext();
				next = (ContentSection)nPara.getFirstSection();
			}
			
			_currentSection = next;
		}
		
		return 1;
	}
	
	/**
	 * moves the cursor to the line-beginning
	 * 
	 * @return the number of characters to walk back
	 */
	int moveCursorToLineStart() {
		Line line = _currentSection.getSectionLine();
		int remainingChars = _currentCursorPos - line.getLineEnvStartPos();
		
		_currentCursorPos -= remainingChars;
		_currentCursorPosLHC = _currentCursorPos;
		_currentSection = (ContentSection)line.getFirstSection();
		
		return remainingChars;
	}
	
	/**
	 * moves the cursor to the line-end
	 * 
	 * @return the number of characters to walk forward
	 */
	int moveCursorToLineEnd() {
		Line line = _currentSection.getSectionLine();
		int cursorPos = line.getLastCursorPosInEnv();
		int remainingChars = cursorPos - _currentCursorPos;
		
		_currentCursorPos = cursorPos;
		_currentCursorPosLHC = _currentCursorPos;
		_currentSection = (ContentSection)line.getLastSection();
		
		return remainingChars;
	}
	
	/**
	 * moves the cursor to the position of the prev word
	 * 
	 * @return 1 if the cursor has been moved
	 * 				-1 if we are at the beginning of the env
	 * 				 0 if the cursor has not been moved
	 */
	int moveCursorToPrevWord() {
		// if we are at the beginning of the environment, let the controller decide what to do
		if(_currentCursorPos == 0)
			return -1;
		
		// move to the prev line?
		if(_currentCursorPos == _currentSection.getStartPosInEnv() &&
				_currentSection.isFirstInParagraph()) {
			// is the last paragraph an environment? so let the controller do the work
			Paragraph prev = (Paragraph)_currentSection.getSectionParagraph().getPrev();
			if(prev.containsEnvironment())
				return -1;
			
			return moveCursorBack(1);
		}
		
		int pos = getPreviousWordStart();
		
		// nothing to do?
		if(_currentCursorPos == pos)
			return 0;
		
		return moveCursorBack(_currentCursorPos - pos);
	}
	
	/**
	 * moves the cursor to the position of the next word
	 * 
	 * @return 1 if the cursor has been moved
	 * 				-1 if we are at the end of the env
	 * 				 0 if the cursor has not been moved
	 */
	int moveCursorToNextWord() {
		// move to the next line?
		if(_currentCursorPos == _currentSection.getEndPosInEnv() + 1 &&
				_currentSection.isLastInParagraph()) {
			// is the next paragraph an environment? so let the controller do the work
			Paragraph next = (Paragraph)_currentSection.getSectionParagraph().getNext();
			if(next == null || next.containsEnvironment())
				return -1;
			
			return moveCursorForward(1);
		}
		
		int pos = getNextWordStart();
		
		// nothing to do?
		if(_currentCursorPos == pos)
			return 0;
		
		return moveCursorForward(pos - _currentCursorPos);
	}
	
	/**
	 * determines the cursor- and LHC-cursor position in the line to move to
	 * the method saves the section to move to in the parameter <code>section</code>
	 * 
	 * @param section will contain the section to move to after the call
	 * @param line the Line to move to
	 * @return an array: array(0 => cursorPos, 1 cursorLHCPos)
	 */
	int[] getPositionInLine(MutablePointer section,Line line) {
		int[] positions = new int[] {0,0};
		
		int lineChars = line.getLineLength();
		
		// calculate the position in the current line
		Line currentLine = _currentSection.getSectionLine();
		int currentLineStartPos = currentLine.getLineEnvStartPos();
		int currentLinePosCmpChars = _currentCursorPosLHC - currentLineStartPos;
		int targetLineStart = line.getLineEnvStartPos();
		
		// the next / prev line is shorter, so go to the end of the line
		if(lineChars < currentLinePosCmpChars) {
			section.setValue(line.getLastSection());
			
			positions[0] = line.getLineEnvEndPos() + 1;
			positions[1] = targetLineStart + currentLinePosCmpChars;
		}
		// the next / prev line is longer than the current one, so go to the same position
		else {
			ContentSection tAtPos;
			try {
				int lineStart = line.getLineStartPosition();
				tAtPos = line.getSectionAt(currentLinePosCmpChars + lineStart);
				positions[0] = currentLinePosCmpChars + targetLineStart;
				positions[1] = currentLinePosCmpChars + targetLineStart;
				section.setValue(tAtPos);
			}
			catch(InvalidTextPositionException e) {
				e.printStackTrace();
			}
		}
		
		return positions;
	}
	
	/**
	 * Determines the paragraph at the given position <b>recursivly</b>.<br>
	 * That means that this paragraph will contain content and NOT an environment!
	 * 
	 * @param position the position in this environment
	 * @return the paragraph at this position (may NOT be null)
	 * @see #getParagraphAtPosition(int)
	 */
	public Paragraph getContentParagraphAtPosition(int position) {
		Paragraph p = _paragraphs.getParagraphAtPosition(position);
		if(p.containsEnvironment())
			return ((Environment)p.getFirstSection()).getContentParagraphAtPosition(position);
		
		return p;
	}
	
	/**
	 * determines the paragraph at the given position.<br>
	 * NOT recursivly. Therefore you'll get the paragraph in this environment, no matter
	 * if this one contains an Environment or ContentSections
	 * 
	 * @param position the position in this environment
	 * @return the paragraph at this position (may NOT be null)
	 * @see #getContentParagraphAtPosition(int)
	 */
	public Paragraph getParagraphAtPosition(int position) {
		return _paragraphs.getParagraphAtPosition(position);
	}
	
	/**
	 * determines the line at the given position.<br>
	 * NOT recursivly. Therefore you'll get the line in this environment, no matter
	 * if this one contains an Environment or ContentSections
	 * 
	 * @param position the position in this environment
	 * @return the Line at this position (may NOT be null)
	 */
	public Line getLineAtPosition(int position) {
		Paragraph p = _paragraphs.getParagraphAtPosition(position);
		return p.getLineAtPosition(position);
	}
	
	/**
	 * @return wether this environment is selected
	 */
	public boolean isSelected() {
		return _selected;
	}
	
	/**
	 * sets the environment and the child-environments
	 * in the given interval selected or not
	 * 
	 * @param from the position where to start to change the selection (in this env!)
	 * @param to the position where to stop to change the selection (in this env!)
	 * @param selStart the start-position of the selection (in this env!)
	 * @param selEnd the end-position of the selection (in this env!)
	 */
	void setSelected(int from,int to,int selStart,int selEnd) {
		_selected = selStart < 0 && selEnd > getElementLength();
		
		Paragraph p = _paragraphs.getParagraphAtPosition(from);
		do {
			int pStart = p.getElementStartPos();
			
			// are we finished?
			if(to < pStart)
				break;
			
			if(p.containsEnvironment()) {
				((Environment)p.getFirstSection()).setSelected(from - pStart,to - pStart,
						selStart - pStart,selEnd - pStart);
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null);
	}
	
	/**
	 * corrects the start- and end-positions of the environment and it parents
	 * 
	 * @param count the number of characters that have been added
	 */
	protected void adjustEnvPositions(int count) {
		_length += count;
		_endPos += count;
		
		if(_env != null)
			adjustEnvPositions(_env,count,getSectionParagraph());
	}
	
	/**
	 * corrects the start- and end-positions of the environment and it parents
	 * 
	 * @param parent the parent environment
	 * @param count the number of characters that have been added
	 * @param p the paragraph of the parent-parent-env
	 */
	protected void adjustEnvPositions(Environment parent,int count,Paragraph p) {
		p.increaseElementEndPos(count);
		parent.increaseElementEndPos(count);
		
		Environment pParent = parent.getParentEnvironment();
		if(pParent != null)
			adjustEnvPositions(pParent,count,parent.getSectionParagraph());
		
		if(p != null) {
			Paragraph para = (Paragraph)p.getNext();
			while(para != null) {
				para.increaseElementPos(count);
				para = (Paragraph)para.getNext();
			}
		}
	}
	
	/**
	 * determines if the cursor is at the start of this environment
	 * 
	 * @return true if the cursor is at the start of this env
	 */
	boolean isCursorAtEnvironmentStart() {
		if(_currentCursorPos == -1)
			return false;
		
		Section cSec = _currentSection;
		Paragraph cPara = cSec.getSectionParagraph();
		if(_currentCursorPos == cSec.getElementStartPos() + cPara.getElementStartPos())
			return cSec.isFirstInParagraph() && cPara.isFirst();
		
		return false;
	}
	
	/**
	 * determines if the cursor is at the end of this environment
	 * 
	 * @return true if the cursor is at the end of this env
	 */
	boolean isCursorAtEnvironmentEnd() {
		if(_currentCursorPos == -1)
			return false;
		
		Section cSec = _currentSection;
		Paragraph cPara = cSec.getSectionParagraph();
		if(_currentCursorPos == cPara.getElementEndPos() + 1)
			return cPara.isLast();
		
		return false;
	}
	
	/**
	 * the position will be in the current environment
	 * therefore you can compare the position with the current cursor-position
	 * if it is equal, no word has been found
	 * 
	 * @return the start-position of the previous word
	 */
	int getPreviousWordStart() {
		Paragraph p = _currentSection.getSectionParagraph();
		int pStart = p.getElementStartPos();
		String text = p.getText();
		int pos = _currentCursorPos - pStart - 2;
		
		for(;pos >= 0;pos--) {
			char c = text.charAt(pos);
			if(!isWordChar(c)) {
				pos++;
				break;
			}
		}
		
		if(pos <= 0)
			return pStart;
		
		// ensure that the position is >= 0 (the first section)
		return Math.max(0,pos + pStart);
	}
	
	/**
	 * the position will be in the current environment
	 * therefore you can compare the position with the current cursor-position
	 * if it is equal, no word has been found
	 * 
	 * @return the start-position of the next word
	 */
	int getNextWordStart() {
		Paragraph p = _currentSection.getSectionParagraph();
		int pStart = p.getElementStartPos();
		String text = p.getText();
		int pos = _currentCursorPos - pStart;
		for(;pos < text.length();pos++) {
			char c = text.charAt(pos);
			if(!isWordChar(c)) {
				if(c != '\n' || c == '\r')
					pos++;
				break;
			}
		}
		
		// ensure not to move behind the last section
		return Math.min(pos + pStart,getElementLength());
	}

	/**
	 * @param c the character to test
	 * @return true if this character is treaten as a 'wordchar'
	 */
	protected boolean isWordChar(char c) {
		// control-character?
		if(c < ' ')
			return false;
		
		return Character.isUnicodeIdentifierPart(c);
	}
	
	/**
	 * debugging information
	 * 
	 * @return a string with infos about this environment
	 */
	public String toString() {
		StringBuffer debug = new StringBuffer();
		debug.append(">>>=============================\n");
		debug.append("ID: " + String.valueOf(hashCode()) + "\n");
		debug.append("Current SectionID: " +
								 (_currentSection != null ? _currentSection.hashCode() : -1) + "\n");
		debug.append("Current CursorPos: " +
								 _currentCursorPos + "|" + _currentCursorPosLHC + "\n");
		debug.append("[S:" + _startPos + ",E:" + _endPos + ",L:" + _length + "]\n");
		debug.append("- - - - - - - - -\n\n");
		for(int i = 0;i < _paragraphs.getParagraphCount();i++)
			debug.append(_paragraphs.getParagraph(i).toString() + "\n");
		debug.append("<<<=============================\n");
		return debug.toString();
	}
}