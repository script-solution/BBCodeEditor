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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.List;

import javax.swing.JViewport;

import bbcodeeditor.control.actions.*;
import bbcodeeditor.control.export.Exporter;
import bbcodeeditor.control.export.IExportContent;
import bbcodeeditor.control.export.bbcode.BBCodeExportContent;
import bbcodeeditor.control.export.bbcode.BBCodeParser;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.export.bbcode.BBCodeTokenizer;
import bbcodeeditor.control.export.html.HTMLExportContent;
import bbcodeeditor.control.export.html.HTMLParser;
import bbcodeeditor.control.export.html.HTMLTokenizer;
import bbcodeeditor.control.export.plain.PlainExportContent;
import bbcodeeditor.control.highlighter.HighlightSyntax;
import bbcodeeditor.control.tools.MutableInt;
import bbcodeeditor.control.tools.MutablePointer;
import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.control.tools.TextPart;
import bbcodeeditor.control.view.IEnvironmentView;


/**
 * this class, like the name says, controls everything which happens in the textfield.
 * That means that it contains the undo-/redo-actions, the selection, the smileys and
 * the temporary attributes.<br>
 * Additionally it contains the root-environment and the Environment instance
 * of the current cursor-position.
 * <p>
 * The basic model looks like the following:<br>
 *
 * <table style="border: 1px solid #000000;">
 * <tr><td>Root-Environment</td></tr>
 * <tr><td>
 * <pre>
 * Paragraph 1: <==================================
 * Line 1:       <----------------------------------
 * Section's:     |abc|<b>def</b>|ghi|
 *               ---------------------------------->
 * Line 2:       <----------------------------------
 * Section's:     |test|
 *               ---------------------------------->
 *              ==================================>
 * Paragraph 2: <==================================
 * Line 1:       <----------------------------------
 * Section's:     </pre>
 * <table style="border: 1px solid #000000; margin-left: 30px;">
 * <tr><td>Sub-Environment</td></tr>
 * <tr><td>
 * <pre>
 * Paragraph 1:   <==============================
 * Line 1:         <------------------------------
 * Section's:       |text|
 *                 ------------------------------>
 *                ==============================></pre>
 * </td></tr></table>
 * <pre>
 *               ------------------------------>
 *              ==============================></pre>
 * </td></tr></table>
 * <br>
 * 
 * @author hrniels
 */
public final class Controller {
	
	/**
	 * Indicates that nothing has been done
	 */
	public static final int RES_NOTHING_DONE				= 0;
	
	/**
	 * Indicates that the action has been performed
	 */
	public static final int RES_DEFAULT						= 1;

	/**
	 * the instance of the textfield
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * The history-manager
	 */
	private final HistoryManager _historyManager;
	
	/**
	 * the smiley-container
	 */
	private SmileyContainer _smileys = new SmileyContainer();
	
	/**
	 * the root-environment<br>
	 * this environment is the default-environment which allows formating and may contain
	 * sub-environments.
	 */
	private Environment _rootEnv;
	
	/**
	 * a set with all tags which have been disabled.<br>
	 * contains integers from BBCodeTags which represent the tags.
	 * 
	 * @see BBCodeTags
	 */
	private Set _disabledAttributes = new HashSet();
	
	/**
	 * wether smileys will currently be replaced
	 */
	private boolean _replaceSmileys = true;
	
	/**
	 * contains the current environment (of the cursor)
	 */
	private Environment _currentEnv;
	
	/**
	 * temporary attributes which replace empty sections<br>
	 * if no text is selected and the cursor is at no word it gives the user the opportunity
	 * to manipulate this attributes and type afterwards
	 */
	private TextAttributes _tempAttributes = null;
	
	/**
	 * maps all available attributes to the corresponding tag-id
	 */
	private static final Map _attributesToTags;

	// init the map
	static {
		_attributesToTags = new HashMap();
		_attributesToTags.put(TextAttributes.BOLD,new Integer(BBCodeTags.BOLD));
		_attributesToTags.put(TextAttributes.UNDERLINE,new Integer(BBCodeTags.UNDERLINE));
		_attributesToTags.put(TextAttributes.ITALIC,new Integer(BBCodeTags.ITALIC));
		_attributesToTags.put(TextAttributes.STRIKE,new Integer(BBCodeTags.STRIKE));
		// this attribute is controlled by 2 tags
		// therefore we have to make sure that either both tags are enabled or both disabled
		_attributesToTags.put(TextAttributes.POSITION,new Integer(BBCodeTags.SUPERSCRIPT));
		_attributesToTags.put(TextAttributes.FONT_SIZE,new Integer(BBCodeTags.FONT_SIZE));
		_attributesToTags.put(TextAttributes.FONT_COLOR,new Integer(BBCodeTags.FONT_COLOR));
		_attributesToTags.put(TextAttributes.FONT_FAMILY,new Integer(BBCodeTags.FONT_FAMILY));
		_attributesToTags.put(TextAttributes.BG_COLOR,new Integer(BBCodeTags.BG_COLOR));
		_attributesToTags.put(TextAttributes.URL,new Integer(BBCodeTags.URL));
		_attributesToTags.put(TextAttributes.EMAIL,new Integer(BBCodeTags.EMAIL));
		_attributesToTags.put(TextAttributes.HIGHLIGHT,new Integer(BBCodeTags.HIGHLIGHT));
	}

	/**
	 * constructor
	 * 
	 * @param textField the text-field for this controller
	 */
	Controller(AbstractTextField textField) {
		_textArea = textField;

		_historyManager = new HistoryManager(_textArea);
		
		if(_textArea.getEditorMode() == IPublicController.MODE_TEXT_EDITOR)
			_rootEnv = new CodeEnvironment(_textArea,null,null,null);
		else
			_rootEnv = new Environment(_textArea,null,null,null);
		
		_currentEnv = _rootEnv;
		_currentEnv.enterCursorFront();
	}

	/**
	 * @return the max nesting-level for tags
	 */
	public int getMaxTagNestingLevel() {
		return _textArea.getMaxTagNestingLevel();
	}
	
	/**
	 * @return the textfield
	 */
	public AbstractTextField getTextField() {
		return _textArea;
	}
	
	/**
	 * @return the history-manager
	 */
	public HistoryManager getHistoryManager() {
		return _historyManager;
	}
	
	/**
	 * @return wether smileys will be replaced
	 */
	public boolean replaceSmileys() {
		return _replaceSmileys;
	}
	
	/**
	 * sets wether smileys will be replaced
	 * 
	 * @param replace replace smileys?
	 */
	public void setReplaceSmileys(boolean replace) {
		_replaceSmileys = replace;
	}
	
	/**
	 * @return the SmileyContainer with the smileys
	 */
	public SmileyContainer getSmileys() {
		return _smileys;
	}
	
	/**
	 * sets the smileys for this controller
	 * 
	 * @param con the SmileyContainer
	 */
	public void setSmileys(SmileyContainer con) {
		if(con == null)
			throw new InvalidParameterException("con = null");
		
		_smileys = con;
		
		// we have to refresh the content in this case
		refreshContent();
	}

	/**
	 * Returns the environment in which the cursor currently is. This may be the root-
	 * environment or any sub-environment.
	 * 
	 * @return the current environment
	 */
	public Environment getCurrentEnvironment() {
		return _currentEnv;
	}

	/**
	 * Returns the global position of the cursor. That means the position with the view
	 * of the root-environment.
	 * 
	 * @return the current (global) position of the cursor
	 */
	public int getCurrentCursorPos() {
		return _currentEnv.getGlobalCurrentCursorPos();
	}

	/**
	 * Returns the ContentSection in which the cursor currently is. This may NOT be an
	 * environment but any Section which directly contains content.
	 * 
	 * @return the current section of the cursor
	 */
	public ContentSection getCurrentSection() {
		return _currentEnv.getCurrentSection();
	}

	/**
	 * Returns the Line-object in which the cursor is. This may be a line of a sub-
	 * environment.
	 * 
	 * @return the current Line of the cursor
	 */
	public Line getCurrentLine() {
		return _currentEnv.getCurrentSection().getSectionLine();
	}

	/**
	 * Returns the paragraph in which the cursor is currently in.
	 * 
	 * @return the paragraph at the cursor
	 */
	public Paragraph getCurrentParagraph() {
		return _currentEnv.getCurrentSection().getSectionParagraph();
	}
	
	/**
	 * @return the root environment
	 */
	public Environment getRootEnvironment() {
		return _rootEnv;
	}
	
	/**
	 * Checks wether the given tag is enabled
	 * 
	 * @param tag the tag to check. see BBCodeTags.*
	 * @return true if the tag is enabled
	 * @see BBCodeTags
	 */
	public boolean isTagEnabled(int tag) {
		if(!BBCodeTags.isValidTag(tag))
			return false;
		
		return !_disabledAttributes.contains(new Integer(tag));
	}
	
	/**
	 * checks wether the given alignment is enabled
	 * 
	 * @param align the alignment to check (may be Attributes.ALIGN_UNDEF)
	 * @return true if it is enabled or ALIGN_UNDEF or unknown
	 */
	public boolean isAlignmentEnabled(int align) {
		switch(align) {
			case ParagraphAttributes.ALIGN_LEFT:
				return isTagEnabled(BBCodeTags.LEFT);
			
			case ParagraphAttributes.ALIGN_CENTER:
				return isTagEnabled(BBCodeTags.CENTER);
				
			case ParagraphAttributes.ALIGN_RIGHT:
				return isTagEnabled(BBCodeTags.RIGHT);
				
			default:
					return true;
		}
	}
	
	/**
	 * checks wether the given alignment is valid
	 * 
	 * @param align the align to check
	 * @param allowUndef should Attributes.ALIGN_UNDEF be valid?
	 * @return true if it is valid
	 */
	public boolean isValidAlignment(int align,boolean allowUndef) {
		if(align == ParagraphAttributes.ALIGN_LEFT || align == ParagraphAttributes.ALIGN_RIGHT ||
				align == ParagraphAttributes.ALIGN_CENTER)
			return true;
		
		return align == ParagraphAttributes.ALIGN_UNDEF && allowUndef;
	}
	
	/**
	 * Enables the given tag. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tag the tag to enable. see BBCodeTags.*
	 * @see BBCodeTags
	 * @return the result
	 */
	public int enableTag(int tag) {
		return enableTags(Arrays.asList(new Integer[] {new Integer(tag)}));
	}

	/**
	 * Disables the given tag. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tag the tag to disable. see BBCodeTags.*
	 * @see BBCodeTags
	 * @return the result
	 */
	public int disableTag(int tag) {
		return disableTags(Arrays.asList(new Integer[] {new Integer(tag)}));
	}
	
	/**
	 * Enables all tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @return the result
	 */
	public int enableAllTags() {
		return enableTags(BBCodeTags.getAllTags());
	}
	
	/**
	 * Disables all tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @return the result
	 */
	public int disableAllTags() {
		return disableTags(BBCodeTags.getAllTags());
	}
	
	/**
	 * Enables all given tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tags a List with all tags to enable. see BBCodeTags.*
	 * @see BBCodeTags
	 * @return the result
	 */
	public int enableTags(List tags) {
		if(tags == null)
			throw new InvalidParameterException("tags is null");
		
		// We have to make sure that either subscript and superscript is in the list
		// or none of them. Because if one tag of these is enabled or disabled the other
		// one has to be enabled or disabled, too!
		if(tags.contains(new Integer(BBCodeTags.SUBSCRIPT))) {
			if(!tags.contains(new Integer(BBCodeTags.SUPERSCRIPT)))
				tags.add(new Integer(BBCodeTags.SUPERSCRIPT));
		}
		else if(tags.contains(new Integer(BBCodeTags.SUPERSCRIPT))) {
			if(!tags.contains(new Integer(BBCodeTags.SUBSCRIPT)))
				tags.add(new Integer(BBCodeTags.SUBSCRIPT));
		}
		
		boolean refresh = false;
		Iterator it = tags.iterator();
		while(it.hasNext()) {
			Object n = it.next();
			
			if(n instanceof Integer) {
				int id = ((Integer)n).intValue();
				if(BBCodeTags.isValidTag(id) && !isTagEnabled(id)) {
					_disabledAttributes.remove(n);
					refresh = true;
				}
			}
		}
		
		if(refresh)
			refreshContent();
		
		return refresh ? RES_DEFAULT : RES_NOTHING_DONE;
	}
	
	/**
	 * Disables all given tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tags a List with all tags to disable. see BBCodeTags.*
	 * @see BBCodeTags
	 * @return the result
	 */
	public int disableTags(List tags) {
		if(tags == null)
			throw new InvalidParameterException("tags is null");

		// We have to make sure that either subscript and superscript is in the list
		// or none of them. Because if one tag of these is enabled or disabled the other
		// one has to be enabled or disabled, too!
		if(tags.contains(new Integer(BBCodeTags.SUBSCRIPT))) {
			if(!tags.contains(new Integer(BBCodeTags.SUPERSCRIPT)))
				tags.add(new Integer(BBCodeTags.SUPERSCRIPT));
		}
		else if(tags.contains(new Integer(BBCodeTags.SUPERSCRIPT))) {
			if(!tags.contains(new Integer(BBCodeTags.SUBSCRIPT)))
				tags.add(new Integer(BBCodeTags.SUBSCRIPT));
		}
		
		boolean refresh = false;
		Iterator it = tags.iterator();
		while(it.hasNext()) {
			Object n = it.next();
			
			if(n instanceof Integer) {
				int id = ((Integer)n).intValue();
				if(BBCodeTags.isValidTag(id) && isTagEnabled(id)) {
					_disabledAttributes.add(n);
					refresh = true;
				}
			}
		}
		
		if(refresh)
			refreshContent();

		return refresh ? RES_DEFAULT : RES_NOTHING_DONE;
	}

	/**
	 * adds a code-environment at the current cursor-position
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param hlSyntax the highlight-syntax to use
	 * @return the status-code
	 */
	public int addCodeEnvironment(boolean isListPoint,Object hlSyntax) {
		CodeEnvironment env = new CodeEnvironment(_textArea,getCurrentEnvironment(),null,null,hlSyntax);
		return addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}

	/**
	 * adds a list-environment at the current cursor-position
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param listType the type of the list
	 * @return the status-code
	 */
	public int addListEnvironment(boolean isListPoint,int listType) {
		ListEnvironment env = new ListEnvironment(_textArea,getCurrentEnvironment(),null,null,listType);
		return addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}

	/**
	 * adds a quote-environment at the current cursor-position
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param author the author of the quote
	 * @return the status-code
	 */
	public int addQuoteEnvironment(boolean isListPoint,String author) {
		QuoteEnvironment env = new QuoteEnvironment(_textArea,getCurrentEnvironment(),null,null,author);
		return addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}
	
	/**
	 * adds the given environment to the given parent-environment
	 * 
	 * @param newEnv the environment to add
	 * @param pos the position where to insert the environment
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param forceNewLine do you want to insert a newline after the environment?
	 * @return the status-code
	 */
	public int addEnvironment(Environment newEnv,int pos,boolean isListPoint,
			boolean forceNewLine) {
		// are code environments allowed?
		if(newEnv instanceof CodeEnvironment && !isTagEnabled(BBCodeTags.CODE))
			return RES_NOTHING_DONE;

		// are quote environments allowed?
		if(newEnv instanceof QuoteEnvironment && !isTagEnabled(BBCodeTags.QUOTE))
			return RES_NOTHING_DONE;
		
		// are list environments allowed?
		if(newEnv instanceof ListEnvironment && !isTagEnabled(BBCodeTags.LIST))
			return RES_NOTHING_DONE;
		
		ContentSection sec = _rootEnv.getSectionAt(pos);
		Environment parent = sec.getParentEnvironment();
		
		if(!parent.containsSubEnvironments())
			return RES_NOTHING_DONE;
		
		// ensure the environment is not selected and the cursor is not in it
		newEnv.setSelected(0,newEnv.getElementLength(),-1,-1);
		newEnv.leaveCursor();
		
		// ensure that we add environments always with "isListPoint" = false
		// in list-environments
		if(isListPoint && _textArea.getCurrentEnvironment() instanceof ListEnvironment)
			isListPoint = false;
		// ensure that this is just possible in list-envs
		else if(!isListPoint && !(_textArea.getCurrentEnvironment() instanceof ListEnvironment))
			isListPoint = true;
		
		boolean newLine = false;
		if(parent.addChildEnvironment(newEnv,pos - parent.getGlobalStartPos(),
				isListPoint,forceNewLine))
			newLine = true;

		// enter new env
		_currentEnv.leaveCursor();
		_currentEnv = newEnv;
		_currentEnv.enterCursorFront();
		
		// add it to history
		AddEnvironmentAction action = new AddEnvironmentAction(this,newEnv,
				pos,pos + 2,isListPoint,newLine);
		_historyManager.add(action);
		
		markRepaintAll();
		markContentChanged();
		
		return RES_DEFAULT;
	}
	
	/**
	 * Adds the given image at the cursor-position and moves the cursor one
	 * step forward
	 * 
	 * @param image the image
	 * @return the result-code
	 */
	public int addImageAtCursor(SecImage image) {
		int res = addImage(image,getCurrentCursorPos());
		forward();
		return res;
	}
	
	/**
	 * adds the smiley with given image at the given position
	 * 
	 * @param image the image to add
	 * @param pos the position
	 * @return the result-code
	 */
	public int addImage(SecImage image,int pos) {
		if(image == null)
			throw new InvalidParameterException("image is null");

		ContentSection sec = _rootEnv.getSectionAt(pos);
		Environment cEnv = sec.getParentEnvironment();

		if(!cEnv.containsStyles())
			return RES_NOTHING_DONE;
		
		cEnv.addImage(image,pos - cEnv.getGlobalStartPos());
		
		// add the typed character to history
		AddImageAction action = new AddImageAction(this,pos,pos + 1,image);
		_historyManager.add(action);
		
		_textArea.getRepaintManager().addDirtyLine(sec.getSectionLine(),false);
		
		correctCurrentSection();
		
		markContentChanged();

		return RES_DEFAULT;
	}
	
	/**
	 * @return the word at the cursor
	 */
	public TextPart getWordAtCursor() {
		Paragraph p = _currentEnv.getCurrentSection().getSectionParagraph();
		int pStart = p.getElementStartPos() + _currentEnv.getGlobalStartPos();
		String text = p.getText();
		if(text.equals(""))
			return null;

		int cursorPosInLine = getCurrentCursorPos() - pStart;
		
		char charBehindCursor = (char)0;
		if(cursorPosInLine < text.length())
			charBehindCursor = text.charAt(cursorPosInLine);

		// return null if there is no word at this position
		// if we are in front of a word it counts as the word at the cursor. if we
		// are behind a word it does not
		if(!_currentEnv.isWordChar(charBehindCursor))
			return null;

		// walk back until the current character doesn't belong to the word at the cursor
		int start = cursorPosInLine - 1;
		for(;start >= 0;start--) {
			if(!_currentEnv.isWordChar(text.charAt(start)))
				break;
		}

		// walk forward until the current character doesn't belong to the word at the cursor
		int len = text.length();
		int end = cursorPosInLine;
		for(;end < len;end++) {
			if(!_currentEnv.isWordChar(text.charAt(end)))
				break;
		}

		start++;

		return new TextPart(text.substring(start,end),start + pStart,end + pStart);
	}
	
	/**
	 * Moves the cursor to the previous word
	 * 
	 * @return the result-code
	 */
	public int goToPreviousWord() {
		ContentSection oldSec = getCurrentSection();
		
		// move the cursor in the environment
		int moved = _currentEnv.moveCursorToPrevWord();
		
		// are we at the beginning of the env? so simply go back
		if(moved == -1)
			return back();
		
		if(moved == 0)
			return RES_NOTHING_DONE;

		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {oldSec,getCurrentSection()});
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the next word
	 * 
	 * @return the result-code
	 */
	public int goToNextWord() {
		ContentSection oldSec = getCurrentSection();
		
		// move the cursor in the environment
		int moved = _currentEnv.moveCursorToNextWord();
		
		// are we at the end of the env? so simply go forward
		if(moved == -1)
			return forward();
		
		if(moved == 0)
			return RES_NOTHING_DONE;

		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {oldSec,getCurrentSection()});
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the beginning of the paragraph with given index
	 * 
	 * @param index the index of the paragraph
	 * @return the result-code
	 */
	public int goToParagraph(int index) {
		Paragraph p = _rootEnv.getParagraph(index);
		if(p == null)
			return RES_NOTHING_DONE;
		
		// ensure that we go to the first text-position in that paragraph.
		// the paragraph may contain an environment...
		while(p.containsEnvironment()) {
			Environment env = (Environment)p.getFirstSection();
			p = env.getFirstParagraph();
		}
		
		int pos = p.getElementStartPos() + p.getParentEnvironment().getGlobalStartPos();
		_currentEnv.leaveCursor();
		_currentEnv = _rootEnv.goToPosition(pos);
		
		markRepaintAll();
		
		return RES_DEFAULT;
	}
	
	/**
	 * Moves the cursor to the given position
	 * 
	 * @param pos the position
	 * @return the result-code
	 */
	public int goToPosition(int pos) {
		_currentEnv.leaveCursor();
		_currentEnv = _rootEnv.goToPosition(pos);
		
		markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * moves the cursor to the given position in the given Environment :)<br>
	 * will NOT invoke the caret-position-changed-listener!
	 * 
	 * @param parent the environment
	 * @param pos the position in the environment
	 */
	public void goToPosition(Environment parent,int pos) {
		_currentEnv.leaveCursor();
		_currentEnv = parent.goToPosition(pos);
	}

	/**
	 * moves the cursor to the given position and the given TextSection in the given
	 * Environment :)<br>
	 * will invoke the caret-position-changed-listener!
	 * 
	 * @param parent the environment
	 * @param section the TextSection
	 * @param pos the position in the environment
	 */
	private void goToPosition(Environment parent,ContentSection section,int pos) {
		if(!parent.equals(_currentEnv)) {
			_currentEnv.leaveCursor();
			_currentEnv = parent;
		}
		
		_currentEnv.enterCursor(section,pos,pos);
	}

	/**
	 * Determines the attributes at the cursor-position
	 * 
	 * @return a TextAttributes-object which contains the attributes
	 */
	public TextAttributes getAttributesAtCursor() {
		Selection sel = _textArea.getSelection();
		if(!sel.isEmpty())
			return getAttributes(sel.getSelectionStart(),sel.getSelectionEnd());
		
		if(_tempAttributes != null)
			return (TextAttributes)_tempAttributes.clone();
		
		ContentSection current = getCurrentSection();
		if(current instanceof TextSection)
			return ((TextSection)current).getCloneOfAttributes();
		
		return new TextAttributes();
	}

	/**
	 * Determines the attributes for the selection, the current position or
	 * the temporary attributes.
	 * 
	 * @return a TextAttributes-object which contains the attributes
	 */
	public TextAttributes getAttributes() {
		int[] positions = _textArea.getPositionsForActions();
		if(positions[0] >= 0 && positions[1] >= 0)
			return getAttributes(positions[0],positions[1]);
		
		// if the temporary attributes are not set return the attributes of the current section
		// (inserted text would get these attributes)
		if(_tempAttributes == null) {
			ContentSection current = _currentEnv.getCurrentSection();
			if(current instanceof TextSection)
				return ((TextSection)current).getCloneOfAttributes();
			
			return new TextAttributes();
		}
		
		return (TextAttributes)_tempAttributes.clone();
	}

	/**
	 * Moves the cursor one step back
	 * 
	 * @return the result-code
	 */
	public int back() {
		boolean allowFastPaint = true;
		int savePos = getCurrentCursorPos();
		Environment saveEnv = getCurrentEnvironment();
		ContentSection saveSec = getCurrentSection();
		
		// move / delete in the current env
		int moved = _currentEnv.moveCursorBack(1);
		
		// not possible in the current env? so we have to do that here
		if(moved == -1) {
			Paragraph prevPara = (Paragraph)getCurrentParagraph().getPrev();
			
			// are we at an environment-start
			if(_currentEnv.isCursorAtEnvironmentStart()) {
				Paragraph parentPara = _currentEnv.getSectionParagraph();
				if(parentPara == null)
					return RES_NOTHING_DONE;
				
				Paragraph prev = (Paragraph)parentPara.getPrev();
				ContentSection s = (ContentSection)prev.getLastSection();
				
				// change to parent env
				_currentEnv.leaveCursor();
				_currentEnv = _currentEnv.getParentEnvironment();
				int cursorPos = s.getEndPosInEnv() + 1;
				_currentEnv.enterCursor(s,cursorPos,cursorPos);
			}
			// move backwards into an environment?
			else if(prevPara.containsEnvironment()) {
				_currentEnv.leaveCursor();
				_currentEnv = (Environment)prevPara.getFirstSection();
				_currentEnv.enterCursorBack();
			}
			
			allowFastPaint = false;
		}
		// break here if nothing has been done
		else if(moved == 0)
			return RES_NOTHING_DONE;

		if(allowFastPaint) {
			List sections = new ArrayList();
			sections.add(saveSec);
			
			ContentSection current = getCurrentSection();
			if(!saveSec.equals(current))
				sections.add(current);
			
			// paint the next section if it is in the same line and the cursor was at the end
			// of this section
			if(savePos == saveEnv.getGlobalStartPos() + saveSec.getEndPosInEnv() + 1) {
				Section next = (Section)saveSec.getNext();
				if(next instanceof ContentSection && next != null &&
					 next.getSectionLine() == current.getSectionLine()) {
					sections.add(next);
				}
			}
			
			_textArea.getRepaintManager().addDirtySections(sections);
		}
		else
			markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor one step forward
	 * 
	 * @return the result-code
	 */
	public int forward() {
		boolean allowFastPaint = true;
		ContentSection saveSec = getCurrentSection();
		
		// move in the current env
		int moved = _currentEnv.moveCursorForward(1);
		
		// not possible in the current env? so we have to do that here
		if(moved == -1) {
			Paragraph nextPara = (Paragraph)getCurrentParagraph().getNext();
			
			// are we at the env-end?
			if(_currentEnv.isCursorAtEnvironmentEnd()) {
				Paragraph parentPara = _currentEnv.getSectionParagraph();
				if(parentPara == null)
					return RES_NOTHING_DONE;
				
				Paragraph next = (Paragraph)parentPara.getNext();
				ContentSection s = (ContentSection)next.getFirstSection();
				
				// change to parent env
				_currentEnv.leaveCursor();
				_currentEnv = _currentEnv.getParentEnvironment();
				int cursorPos = s.getStartPosInEnv();
				_currentEnv.enterCursor(s,cursorPos,cursorPos);
			}
			// move forward into an environment?
			else if(nextPara.getFirstSection() instanceof Environment) {
				_currentEnv.leaveCursor();
				_currentEnv = (Environment)nextPara.getFirstSection();
				_currentEnv.enterCursorFront();
			}

			allowFastPaint = false;
		}
		// break here if nothing has been done
		else if(moved == 0)
			return RES_NOTHING_DONE;
		
		// repaint the two affected sections
		if(allowFastPaint) {
			ContentSection current = getCurrentSection();
			if(saveSec.equals(current))
				_textArea.getRepaintManager().addDirtySection(saveSec);
			else
				_textArea.getRepaintManager().addDirtySections(new ContentSection[] {saveSec,current});
		}
		else
			markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the beginning of the line
	 * 
	 * @return the result-code
	 */
	public int goToLineStart() {
		ContentSection saveSec = getCurrentSection();
		
		// change the position in the current environment and look if we have to
		// do anything
		int change = _currentEnv.moveCursorToLineStart();
		if(change == 0)
			return RES_NOTHING_DONE;
		
		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {saveSec,getCurrentSection()});
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the end of the line
	 * 
	 * @return the result-code
	 */
	public int goToLineEnd() {
		ContentSection saveSec = getCurrentSection();
		
		// change the position in the current environment and look if we have to
		// do anything
		int change = _currentEnv.moveCursorToLineEnd();
		if(change == 0)
			return RES_NOTHING_DONE;
		
		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {saveSec,getCurrentSection()});

		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the very beginning of the document
	 * 
	 * @return the result-code
	 */
	public int goToVeryBeginning() {
		ContentSection saveSec = getCurrentSection();
		
		if(getCurrentCursorPos() == 0)
			return RES_NOTHING_DONE;

		// change the environment and go to the beginning
		_currentEnv.leaveCursor();
		_currentEnv = _rootEnv;
		_currentEnv.enterCursorFront();
		
		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {saveSec,getCurrentSection()});
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor to the very beginning of the document
	 * 
	 * @return the result-code
	 */
	public int goToVeryEnd() {
		if(getCurrentCursorPos() == length())
			return RES_NOTHING_DONE;

		ContentSection saveSec = getCurrentSection();
		
		// leave the current environment, if necessary
		if(!_rootEnv.equals(_currentEnv)) {
			_currentEnv.leaveCursor();
			_currentEnv = _rootEnv;
		}
		
		_currentEnv.enterCursorBack();

		_textArea.getRepaintManager().addDirtySections(new ContentSection[] {saveSec,getCurrentSection()});
		
		return RES_DEFAULT;
	}
	
	/**
	 * Moves the cursor one page up
	 * 
	 * @return the result-code
	 */
	public int pageUp() {
		ContentSection sec = getCurrentSection();
		
		// calculate the target-line
		Container parent = _textArea.getParent();
		if(parent instanceof JViewport) {
			JViewport viewPort = (JViewport)parent;
			Rectangle rect = viewPort.getVisibleRect();
			
			Line currentLine = sec.getSectionLine();
			int targetY = sec.getView().getPaintPos().y + currentLine.getLineView().getHeight() - rect.height;
			Line targetLine = _rootEnv.getEnvView().getLineAtPixelPos(targetY,false);
			
			if(targetLine != null) {
				enterLine(currentLine,targetLine);
				markRepaintAll();
				return RES_DEFAULT;
			}
		}
		
		return RES_NOTHING_DONE;
	}

	/**
	 * Moves the cursor one page down
	 * 
	 * @return the result-code
	 */
	public int pageDown() {
		ContentSection sec = getCurrentSection();
		
		// calculate the target-line
		Container parent = _textArea.getParent();
		if(parent instanceof JViewport) {
			JViewport viewPort = (JViewport)parent;
			Rectangle rect = viewPort.getVisibleRect();
			
			Line currentLine = sec.getSectionLine();
			int targetY = sec.getView().getPaintPos().y + rect.height;
			Line targetLine = _rootEnv.getEnvView().getLineAtPixelPos(targetY,false);
			
			if(targetLine != null) {
				enterLine(currentLine,targetLine);
				markRepaintAll();
				return RES_DEFAULT;
			}
		}

		return RES_NOTHING_DONE;
	}

	/**
	 * Moves the cursor one line up
	 * 
	 * @return the result-code
	 */
	public int lineUp() {
		boolean allowFastPaint = true;
		ContentSection saveSec = getCurrentSection();
		
		// move the cursor in the environment
		int moved = _currentEnv.moveCursorUp();
		
		// should be do it here?
		if(moved < 0) {
			Line prevLine = _currentEnv.getCurrentSection().getSectionLine().getPrevInEnv();
			
			// determine the target-line
			Line targetEnvLine = null;
			if(prevLine != null && prevLine.getFirstSection() instanceof Environment) {
				Environment env = (Environment)prevLine.getFirstSection();
				targetEnvLine = env.getLastLine();
			}
			else if(_currentEnv.getSectionLine() != null)
				targetEnvLine = _currentEnv.getSectionLine().getPrevInEnv();
			
			// break here if there is no line to move to
			if(targetEnvLine != null) {
				MutablePointer newSec = new MutablePointer();
				int[] positions = _currentEnv.getPositionInLine(newSec,targetEnvLine);
				
				// are we in the first line of the current env?
				if(prevLine == null) {
					// don't walk back in the root-env
					if(_currentEnv.getParentEnvironment() != null) {
						_currentEnv.leaveCursor();
						_currentEnv = _currentEnv.getParentEnvironment();
					}
				}
				// is the prev line an environment?
				else if(prevLine.getFirstSection() instanceof Environment) {
					_currentEnv.leaveCursor();
					_currentEnv = (Environment)prevLine.getFirstSection();
				}
				
				// enter the new environment
				_currentEnv.enterCursor((ContentSection)newSec.getValue(),positions[0],positions[1]);
				
				// don't repaint fast if we change the environment
				allowFastPaint = false;
			}
		}
		
		if(moved == 0)
			return RES_NOTHING_DONE;
		
		if(allowFastPaint) {
			_textArea.getRepaintManager().addDirtyLine(saveSec.getSectionLine(),false);
			_textArea.getRepaintManager().addDirtyLine(getCurrentLine(),false);
		}
		else
			markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Moves the cursor one line up
	 * 
	 * @return the result-code
	 */
	public int lineDown() {
		boolean allowFastPaint = true;
		ContentSection saveSec = getCurrentSection();
		
		// move the cursor in the environment
		int moved = _currentEnv.moveCursorDown();
		
		// should be do it here?
		if(moved < 0) {
			Line nextLine = _currentEnv.getCurrentSection().getSectionLine().getNextInEnv();

			// determine the target-line
			Line targetEnvLine = null;
			if(nextLine != null && nextLine.getFirstSection() instanceof Environment) {
				Environment env = (Environment)nextLine.getFirstSection();
				targetEnvLine = env.getFirstLine();
			}
			else if(_currentEnv.getSectionLine() != null)
				targetEnvLine = _currentEnv.getSectionLine().getNextInEnv();
			
			// break here if there is no line to move to
			if(targetEnvLine != null) {
				MutablePointer newSec = new MutablePointer();
				int[] positions = _currentEnv.getPositionInLine(newSec,targetEnvLine);
				
				// are we in the last line of the current env?
				if(nextLine == null) {
					// don't walk forward in the root-env
					if(_currentEnv.getParentEnvironment() != null) {
						_currentEnv.leaveCursor();
						_currentEnv = _currentEnv.getParentEnvironment();
					}
				}
				// is the next line an environment?
				else if(nextLine.getFirstSection() instanceof Environment) {
					_currentEnv.leaveCursor();
					_currentEnv = (Environment)nextLine.getFirstSection();
				}
				
				// enter the new environment
				_currentEnv.enterCursor((ContentSection)newSec.getValue(),positions[0],positions[1]);
				
				// don't repaint fast if we change the environment
				allowFastPaint = false;
			}
		}
		
		if(moved == 0)
			return RES_NOTHING_DONE;
		
		if(allowFastPaint) {
			_textArea.getRepaintManager().addDirtyLine(saveSec.getSectionLine(),false);
			_textArea.getRepaintManager().addDirtyLine(getCurrentLine(),false);
		}
		else
			markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Removes the previous char
	 * 
	 * @param saveAction do you want to save the action?
	 * @return the result
	 */
	public int removePreviousChar(boolean saveAction) {
		int savePos = getCurrentCursorPos();
		// break here if we are at the very beginning
		if(savePos == 0)
			return RES_NOTHING_DONE;
		
		// check what to do
		int res = _currentEnv.getRemovePreviousCharType();
		// walk back
		if(res == 0)
			return back();
		// remove environment
		if(res == -1)
			return removeText(savePos - 1,savePos + 1,saveAction);
		
		// export text to remove
		Exporter ex = new Exporter(_textArea,new BBCodeExportContent());
		String removedText = ex.getContent(savePos - 1,savePos);
		
		// delete in the current env
		int removed = _rootEnv.removePreviousChar(savePos);
		
		// nothing done?
		if(removed == 0)
			return RES_NOTHING_DONE;
		
		// move backwards
		int moved = _currentEnv.moveCursorBack(1);
		correctCurrentSection();
		
		// break here if nothing has been done
		if(moved == 0)
			return RES_NOTHING_DONE;
		
		// add to history
		_historyManager.add(new RemoveTextAction(this,savePos - 1,savePos,
				new BBCodeText(removedText)));
		
		markContentChanged();
		markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Removes the following char
	 * 
	 * @param saveAction do you want to save the action?
	 * @return the result
	 */
	public int removeFollowingChar(boolean saveAction) {
		int cursorPos = getCurrentCursorPos();
		
		// export text to remove
		Exporter ex = new Exporter(_textArea,new BBCodeExportContent());
		String removedText = ex.getContent(cursorPos,cursorPos + 1);
		
		// delete in the current env
		int removed = _rootEnv.removeFollowingChar(cursorPos);
		
		// refresh current section
		correctCurrentSection();
		
		// nothing done?
		if(removed == 0)
			return RES_NOTHING_DONE;
		
		_historyManager.add(new RemoveTextAction(this,cursorPos,
				cursorPos + 1,new BBCodeText(removedText)));
		
		markContentChanged();
		markRepaintAll();
		
		return RES_DEFAULT;
	}

	/**
	 * Removes the previous word
	 * 
	 * @param saveAction do you want to save the action?
	 * @return the result
	 */
	public int removePreviousWord(boolean saveAction) {
		int prevWord =_currentEnv.getPreviousWordStart();
		if(prevWord == _currentEnv.getCurrentCursorPos())
			return removePreviousChar(saveAction);
		
		prevWord += _currentEnv.getGlobalStartPos();
		return removeText(prevWord,getCurrentCursorPos(),saveAction);
	}
	
	/**
	 * Removes the next word
	 * 
	 * @param saveAction do you want to save the action?
	 * @return the result
	 */
	public int removeNextWord(boolean saveAction) {
		int nextWord =_currentEnv.getNextWordStart();
		if(nextWord == _currentEnv.getCurrentCursorPos())
			return removeFollowingChar(saveAction);
		
		nextWord += _currentEnv.getGlobalStartPos();
		
		return removeText(getCurrentCursorPos(),nextWord,saveAction);
	}
	
	/**
	 * removes the text from <code>start</code> to <code>end</code>
	 *
	 * @param start the start-position
	 * @param end the end-position
	 * @param saveAction do you want to save the action?
	 * @return the result
	 */
	public int removeText(int start,int end,boolean saveAction) {
		if(start < 0 || end < 0 || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position: " +
					start + "," + end);
		
		int savePos = getCurrentCursorPos();
		
		List actions = null;
		if(saveAction) {
			actions = new ArrayList();
			_rootEnv.collectRemoveTextActions(start,end,actions);
			
			// are there no actions?
			if(actions.size() == 0)
				return RES_NOTHING_DONE;
		}
		
		// remove the text
		int removed = _rootEnv.removeText(start,end);
		if(removed == 0)
			return RES_NOTHING_DONE;
		
		if(savePos == start)
			correctCurrentSection();
		else {
			_currentEnv.leaveCursor();
			
			// we have to walk in the correct environment
			_currentEnv = _rootEnv.goToPosition(start);
		}
		
		if(saveAction)
			_historyManager.add(new RemoveTextListAction(this,actions,start,end));
		
		markContentChanged();
		markRepaintAll();
		
		return RES_DEFAULT;
	}
	
	/**
	 * enters the given target-line
	 * 
	 * @param currentLine the current line
	 * @param targetLine the target Line
	 */
	private void enterLine(Line currentLine,Line targetLine) {
		// determine current-line vars
		int currentLineStartPos = currentLine.getLineEnvStartPos();
		int currentLineCmpPos = _currentEnv.getCurrentCursorLHCPos() - currentLineStartPos;
		
		// leave the old section, if necessary
		if(!_currentEnv.equals(targetLine.getParentEnvironment())) {
			_currentEnv.leaveCursor();
			_currentEnv = targetLine.getParentEnvironment();
		}
		
		// determine targetline vars
		int targetLineLength = targetLine.getLineLength();
		int targetLineStartPos = targetLine.getLineEnvStartPos();

		// the target line is shorter, so go to the end of it
		int pos = 0;
		if(currentLineCmpPos > targetLineLength) {
			ContentSection last = (ContentSection)targetLine.getLastSection();
			pos = last.getEndPosInEnv() + 1;
			_currentEnv.enterCursor(last,pos,targetLineStartPos + currentLineCmpPos);
		}
		// go to the same position
		else {
			pos = currentLineCmpPos + targetLineStartPos;
			try {
				int lineStart = targetLine.getLineStartPosition();
				ContentSection tAtPos = targetLine.getSectionAt(currentLineCmpPos + lineStart);
				_currentEnv.enterCursor(tAtPos,pos,pos);
			}
			catch(InvalidTextPositionException e) {
				
			}
		}
	}
	
	/**
	 * corrects the current section in the current env
	 * assumes that the position has NOT changed, just the pointer to the current
	 * section may have changed
	 */
	private void correctCurrentSection() {
		_currentEnv.correctCurrentSection();
		
		// do we have to change the environment?
		int position = _currentEnv.getCurrentCursorPos();
		ContentSection sec = _currentEnv.getCurrentSection();
		if(sec.getParentEnvironment() != _currentEnv) {
			_currentEnv.leaveCursor();
			_currentEnv = sec.getParentEnvironment();
			_currentEnv.enterCursor(sec,position,position);
		}
	}
	
	/**
	 * clears everything
	 */
	public void clear() {
		if(_textArea.getEditorMode() == IPublicController.MODE_TEXT_EDITOR) {
			Environment old = _rootEnv;
			Object syntax = null;
			if(old instanceof CodeEnvironment)
				syntax = ((CodeEnvironment)old).getHighlightSyntax();
		
			_rootEnv = new CodeEnvironment(_textArea,null,null,null);
			((CodeEnvironment)_rootEnv).setHighlightSyntax(syntax);
		}
		else
			_rootEnv = new Environment(_textArea,null,null,null);

		// we have to reset the temporary attributes
		clearTemporaryAttributes();
		
		_currentEnv = _rootEnv;
		_currentEnv.enterCursorFront();
		
		// we have to clear the font-cache here to force a refresh of all
		// fonts. Note that this does work because we clear all sections, too.
		// Otherwise the reference-count of the FontInfo-objects would be wrong!
		_textArea.getFontCache().clear();
		
		_textArea.getHistory().clear();
		
		// to be sure we do everything here
		markRepaintAll();
		markContentChanged();
		markAttributesChanged();
	}

	/**
	 * toggles the given attribute in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attribute the attribute you want to set
	 * @return the result-code
	 */
	public int toggleAttribute(int start,int end,Integer attribute) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		// positions valid?
		if(start < 0 || end < 0 || end <= start)
			throw new InvalidParameterException("Invalid start or end-position");
		
		// toggle attribute?
		if(!TextAttributes.isToggleAttribute(attribute))
			return RES_NOTHING_DONE;
		
		// is the attribute enabled?
		if(!isAttributeEnabled(attribute))
			return RES_NOTHING_DONE;
		
		// determine all regions in the interval
		
		List keys = new ArrayList();
		keys.add(attribute);
		
		List regions = _rootEnv.getAttributeRegions(start,end,keys);
		
		// nothing to do?
		if(regions.size() == 0)
			return RES_NOTHING_DONE;
		
		// we want to enable an 1/0 attribute if there are multiple regions
		// which means that the attribute is not always enabled/disabled in the interval
		Boolean val;
		if(regions.size() > 1)
			val = new Boolean(true);
		// otherwise we invert it
		else {
			TextAttributes fAttr = ((SetAttributeActionPart)regions.get(0)).getAttributes();
			Boolean cVal = (Boolean)fAttr.get(attribute);
			val = new Boolean(cVal == null ? true : !cVal.booleanValue());
		}
		
		TextAttributes attributes = new TextAttributes();
		attributes.set(attribute,val);
		
		boolean changed = _rootEnv.applyAttributes(start,end,attributes,false,false);
		if(changed) {
			_historyManager.add(new SetAttributeListAction(this,regions,attributes));
			markRepaintAll();
			markAttributesChanged();
			return RES_DEFAULT;
		}
		
		correctCurrentSection();
		return RES_NOTHING_DONE;
	}

	/**
	 * sets the given attribute of the given interval to given value
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attributes a Map with the attributes to set
	 * @return the result-code
	 */
	public int setAttributes(int start,int end,TextAttributes attributes) {
		if(attributes == null)
			throw new InvalidParameterException("attribute is null");
		
		if(start < 0 || end < 0 || end < start)
			throw new InvalidParameterException("Invalid start or end-position: " + start + "," + end);
		
		// clean the attributes
		cleanAttributes(attributes);
		
		List regions = null;
		List keys = attributes.getKeys();
		regions = _rootEnv.getAttributeRegions(start,end,keys);
		
		// nothing to do?
		if(regions.size() == 0)
			return RES_NOTHING_DONE;
		
		boolean changed = _rootEnv.applyAttributes(start,end,attributes,false,false);
		if(changed) {
			_historyManager.add(new SetAttributeListAction(this,regions,attributes));
			markAttributesChanged();
			markRepaintAll();
			return RES_DEFAULT;
		}
		
		correctCurrentSection();
		return RES_NOTHING_DONE;
	}
	
	/**
	 * clears the temporary attributes
	 */
	void clearTemporaryAttributes() {
		_tempAttributes = null;
	}
	
	/**
	 * ensures that the temporary attributes are initialized
	 */
	private void ensureTemporaryAttributesInitialized() {
		if(_tempAttributes == null) {
			ContentSection current = getCurrentSection();
			if(current instanceof TextSection)
				_tempAttributes = ((TextSection)current).getCloneOfAttributes();
			else
				_tempAttributes = new TextAttributes();
		}
	}

	/**
	 * Toggles the given attribute in the temporary attributes
	 * 
	 * @param attribute the attribute
	 * @return the result
	 */
	public int toggleTemporaryAttribute(Integer attribute) {
		if(!TextAttributes.isToggleAttribute(attribute))
			return RES_NOTHING_DONE;
		
		// is the attribute enabled?
		if(!isAttributeEnabled(attribute))
			return RES_NOTHING_DONE;
		
		ensureTemporaryAttributesInitialized();

		// save org-attributes
		TextAttributes orgAttributes = (TextAttributes)_tempAttributes.clone();
		Vector v = new Vector();
		v.add(attribute);
		orgAttributes.ensureSet(v);
		
		Object val = _tempAttributes.get(attribute);
		if(val == null || (val instanceof Boolean && !((Boolean)val).booleanValue())) {
			val = new Boolean(true);
			_tempAttributes.set(attribute,val);
		}
		else {
			_tempAttributes.unset(attribute);
		}
		
		TextAttributes attributes = new TextAttributes();
		if(val == null)
			attributes.remove(attribute);
		else
			attributes.set(attribute,val);
		
		_historyManager.add(new TempAttributeAction(this,orgAttributes,attributes));
		
		markAttributesChanged();
		
		return RES_DEFAULT;
	}

	/**
	 * Sets the temporary-attributes to the given map
	 * 
	 * @param attributes the attributes
	 * @return the result
	 */
	public int setTemporaryAttributes(TextAttributes attributes) {
		if(attributes == null)
			throw new InvalidParameterException("attributes is null");
		
		// clean the attributes
		cleanAttributes(attributes);
		
		ensureTemporaryAttributesInitialized();
		
		// save org-attributes
		TextAttributes orgAttributes = (TextAttributes)_tempAttributes.clone();
		orgAttributes.ensureSet(attributes.getKeys());
		
		_tempAttributes.setAll(attributes);
		
		_historyManager.add(new TempAttributeAction(this,orgAttributes,attributes));
		markAttributesChanged();
		
		return RES_DEFAULT;
	}
	
	/**
	 * sets the alignment of the current paragraph to given value
	 * 
	 * @param align the new alignment
	 * @return the result-code
	 */
	public int setLineAlignment(int align) {
		Selection sel = _textArea.getSelection();
		if(!sel.isEmpty()) {
			int start = sel.getSelectionStart();
			int end = sel.getSelectionEnd();
			return setLineAlignment(start,end,align);
		}
		
		Paragraph current = getCurrentParagraph();
		return setLineAlignment(current,align);
	}
	
	/**
	 * sets the alignment of the paragraphs in the given interval to given value
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param align the new alignment
	 * @return the result-code
	 */
	public int setLineAlignment(int start,int end,int align) {
		if(start < 0 || end < 0 || end < start)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(!isValidAlignment(align,false))
			throw new InvalidParameterException("Invalid align-value");

		if(!isAlignmentEnabled(align))
			return RES_NOTHING_DONE;
		
		// collect regions
		List regions = _rootEnv.getAlignmentRegions(start,end);
		
		// nothing to do?
		if(regions.size() == 0)
			return RES_NOTHING_DONE;
		
		// set alignment
		boolean changed = _rootEnv.setAlignment(start,end,align);
		
		if(changed) {
			// add to history
			_historyManager.add(new SetLineAlignmentListAction(this,regions,align));
			
			markAttributesChanged();
			markRepaintAll();
			
			return RES_DEFAULT;
		}
		
		return RES_NOTHING_DONE;
	}
	
	/**
	 * Sets the alignment of the given paragraph
	 * 
	 * @param p the paragraph
	 * @param align the new alignment
	 * @return the result-code
	 */
	public int setLineAlignment(Paragraph p,int align) {
		if(!isValidAlignment(align,false))
			throw new InvalidParameterException("Invalid align-value");
		
		if(!p.getParentEnvironment().containsStyles())
			return RES_NOTHING_DONE;

		if(!isAlignmentEnabled(align))
			return RES_NOTHING_DONE;
		
		// collect regions
		List regions = new ArrayList();
		int envStart = getCurrentEnvironment().getGlobalStartPos();
		regions.add(new SetLineAlignmentActionPart(envStart + p.getElementStartPos(),
				envStart + p.getElementEndPos() + 1,p.getHorizontalAlignment()));

		// set alignment
		boolean changed = p.setHorizontalAlignment(align);
		if(!changed)
			return RES_NOTHING_DONE;
		
		// add to history
		_historyManager.add(new SetLineAlignmentListAction(this,regions,align));
		
		markAttributesChanged();
		_textArea.getRepaintManager().addDirtyLine(getCurrentLine(),true);

		return RES_DEFAULT;
	}

	/**
	 * moves the cursor to given X/Y position. (to the corresponding character at this position)
	 * 
	 * @param x the x-position of the cursor
	 * @param y the y-position of the cursor
	 * @return the result-code
	 */
	public int moveCursorToPos(int x,int y) {
		ContentSection saveSec = _currentEnv.getCurrentSection();
		
		// calculate the environment, TextSection and character-position at the cursor
		Line l = _rootEnv.getEnvView().getLineAtPixelPos(y,false);
		if(l != null) {
			int currentX = l.getParentEnvironment().getEnvView().getGlobalTextStart();
			MutableInt cursorPos = new MutableInt(0);
			ContentSection s = l.getLineView().getSectionAtPixelPos(x,y,currentX,cursorPos,false);
			if(s != null) {
				Environment env = l.getParentEnvironment();
				goToPosition(env,s,cursorPos.getValue());
				
				_textArea.getRepaintManager().addDirtyLine(saveSec.getSectionLine(),false);
				return RES_DEFAULT;
			}
		}
		
		return RES_NOTHING_DONE;
	}
	
	/**
	 * determines the section at the given pixel-position
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the ContentSection at this position
	 */
	public ContentSection getSectionAtPixelPos(int x,int y) {
		Line l = _rootEnv.getEnvView().getLineAtPixelPos(y,true);
		if(l != null) {
			int currentX = l.getParentEnvironment().getEnvView().getGlobalTextStart();
			MutableInt cursorPos = new MutableInt(0);
			ContentSection s = l.getLineView().getSectionAtPixelPos(x,y,currentX,cursorPos,true);
			return s;
		}
		
		return null;
	}
	
	/**
	 * collects all attributes in the given interval.<br>
	 * Attributes which have in the whole interval the same value will
	 * have this value in the map. Attributes which change in the interval
	 * will be in the map but have a null-value.<br>
	 * Other attributes will not be in the map
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the attributes
	 */
	public TextAttributes getAttributes(int start,int end) {
		MutablePointer attributes = new MutablePointer(null);
		_rootEnv.collectAttributes(start,end,attributes);
		
		if(attributes.getValue() == null)
			return new TextAttributes();
		
		return (TextAttributes)attributes.getValue();
	}
	
	/**
	 * @return the length of the text
	 */
	public int length() {
		return _rootEnv.getElementLength();
	}
	
	/**
	 * Adds a tab in front of all paragraphs in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the result
	 */
	public int indentParagraphs(int start,int end) {
		if(start < 0 || end > length() || start > end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		Paragraph p = _rootEnv.getContentParagraphAtPosition(start);
		int envStart = p.getParentEnvironment().getGlobalStartPos();
		do {
			if(p.getElementStartPos() > end)
				break;
			
			addTextAt("\t",p.getElementStartPos() + envStart,null);
			
			p = (Paragraph)p.getNext();
		}
		while(p != null);
		
		return RES_DEFAULT;
	}
	
	/**
	 * Removes a tab in front of all paragraphs in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the result
	 */
	public int unindentParagraphs(int start,int end) {
		if(start < 0 || end > length() || start > end)
			throw new InvalidParameterException("Invalid start- or end-position");

		Paragraph p = _rootEnv.getContentParagraphAtPosition(start);
		int envStart = p.getParentEnvironment().getGlobalStartPos();
		do {
			if(p.getElementStartPos() > end)
				break;
			
			Section first = p.getFirstLine().getFirstSection();
			if(first instanceof TextSection) {
				TextSection tSec = (TextSection)first;
				if(tSec.getText().startsWith("\t")) {
					int rstart = p.getElementStartPos() + envStart;
					removeText(rstart,rstart + 1,true);
				}
			}
			
			p = (Paragraph)p.getNext();
		}
		while(p != null);
		
		return RES_DEFAULT;
	}
	
	/**
	 * highlights all given regions.
	 * 
	 * @param regions a List which contains TextPart-objects. The text-field will be ignored!
	 * @param highlight the color to use for the highlighting
	 * @return the result
	 */
	public int highlightRegions(List regions,Color highlight) {
		if(regions == null || regions.size() == 0)
			throw new InvalidParameterException("Empty regions");
		
		if(highlight == null)
			throw new InvalidParameterException("Color must not be null");
		
		TextAttributes attributes = new TextAttributes();
		attributes.set(TextAttributes.HIGHLIGHT,highlight);
		
		boolean changed = false;
		Iterator it = regions.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof TextPart))
				throw new InvalidParameterException("Invalid list element: " + o.getClass());
			
			TextPart part = (TextPart)o;
			if(_rootEnv.applyAttributes(part.startPos,part.endPos,attributes,false,true))
				changed = true;
		}
		
		if(changed) {
			markRepaintAll();
			return RES_DEFAULT;
		}
		
		return RES_NOTHING_DONE;
	}
	
	/**
	 * removes all highlighting attributes
	 * 
	 * @return true if the control should be repainted
	 */
	public int clearHighlighting() {
		TextAttributes attributes = new TextAttributes();
		attributes.remove(TextAttributes.HIGHLIGHT);
		
		boolean changed = _rootEnv.applyAttributes(0,length(),attributes,false,true);

		if(changed) {
			markRepaintAll();
			return RES_DEFAULT;
		}
		
		return RES_NOTHING_DONE;
	}

	/**
	 * Replaces all occurrences of <code>text</code> with <code>repl</code> between
	 * <code>start</code> and <code>end</code>. If you like you can search case-sensitive.
	 * <br>
	 * Note that <code>repl</code> may contain BBCode if <code>parseBBCode</code>
	 * is enabled!
	 * 
	 * @param text the text to search
	 * @param repl the text to replace with
	 * @param start the start-position for the search
	 * @param end the end-position for the search
	 * @param caseSensitive search case-sensitive?
	 * @param parseBBCode parse bbcode in the replacement?
	 * @return the number of replaces
	 */
	public int replaceAll(String text,String repl,int start,int end,
			boolean caseSensitive,boolean parseBBCode) {
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Text must not be empty");
		
		if(start < 0 || end > length() || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(repl == null)
			throw new InvalidParameterException("repl = null");
		
		if(!caseSensitive)
			text = text.toLowerCase();
		
		// no new-lines are supported
		text = text.replace('\r',' ');
		text = text.replace('\n',' ');
		
		List results = new ArrayList();
		_rootEnv.collectSearchResults(text,start,end,caseSensitive,results);
		if(results.size() == 0)
			return 0;
		
		int dec = 0;
		Iterator it = results.iterator();
		while(it.hasNext()) {
			TextPart part = (TextPart)it.next();
			
			removeText(part.startPos - dec,part.endPos - dec,true);
			goToPosition(part.startPos - dec);
			
			if(repl.length() > 0)
				pasteTextAtCursor(repl,parseBBCode);
			
			int lenDiff = text.length() - (getCurrentCursorPos() - (part.startPos - dec));
			dec += lenDiff;
		}
		
		return results.size();
	}

	/**
	 * Replaces the next occurrence of <code>text</code> with <code>repl</code> between
	 * <code>start</code> and <code>end</code>. If you like you can search case-sensitive.
	 * Additionally you may search forward or backwards.
	 * <br>
	 * Note that <code>repl</code> may contain BBCode if <code>parseBBCode</code>
	 * is enabled!
	 * 
	 * @param text the text to search
	 * @param repl the text to replace with
	 * @param start the start-position for the search
	 * @param end the end-position for the search
	 * @param caseSensitive search case-sensitive?
	 * @param parseBBCode parse bbcode in the replacement?
	 * @param forward search forward?
	 * @return the start-position of the replace or -1 if nothing has been done
	 */
	public int replaceNext(String text,String repl,int start,int end,
			boolean caseSensitive,boolean parseBBCode,boolean forward) {
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Text must not be empty");
		
		if(start < 0 || end > length() || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(repl == null)
			throw new InvalidParameterException("repl = null");
		
		if(!caseSensitive)
			text = text.toLowerCase();
		
		// no new-lines are supported
		text = text.replace('\r',' ');
		text = text.replace('\n',' ');
		
		TextPart part = _rootEnv.getNextOccurrence(text,start,end,caseSensitive,forward);
		if(part == null)
			return -1;
		
		removeText(part.startPos,part.endPos,true);
		goToPosition(part.startPos);
		if(repl.length() > 0)
			pasteTextAtCursor(repl,parseBBCode);
		
		return part.startPos;
	}

	/**
	 * Searches the control <b>backwards</b> for the given text in the given interval.<br>
	 * If the text has been found a TextPart-object will be returned. Otherwise
	 * null.<br>
	 * Note that you can't search for linewraps
	 * 
	 * @param text the text to search for
	 * @param start the start-position
	 * @param end the end-position
	 * @param caseSensitive perform a case-sensitive search?
	 * @return the last TextPart if found, null otherwise
	 */
	public TextPart getLastOccurrence(String text,int start,int end,boolean caseSensitive) {
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Text must not be empty");
		
		if(start < 0 || end > length() || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(!caseSensitive)
			text = text.toLowerCase();
		
		// no new-lines are supported
		text = text.replace('\r',' ');
		text = text.replace('\n',' ');
		
		return _rootEnv.getNextOccurrence(text,start,end,caseSensitive,false);
	}

	/**
	 * Searches the control for the given text in the given interval.<br>
	 * If the text has been found a TextPart-object will be returned. Otherwise
	 * null.<br>
	 * Note that you can't search for linewraps
	 * 
	 * @param text the text to search for
	 * @param start the start-position
	 * @param end the end-position
	 * @param caseSensitive perform a case-sensitive search?
	 * @return the first TextPart if found, null otherwise
	 */
	public TextPart getFirstOccurrence(String text,int start,int end,boolean caseSensitive) {
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Text must not be empty");
		
		if(start < 0 || end > length() || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(!caseSensitive)
			text = text.toLowerCase();
		
		// no new-lines are supported
		text = text.replace('\r',' ');
		text = text.replace('\n',' ');
		
		return _rootEnv.getNextOccurrence(text,start,end,caseSensitive,true);
	}
	
	/**
	 * Searches the control for the given text in the given interval.<br>
	 * A List with TextPart-objects will be collected which contains all found
	 * occurrences.<br>
	 * Note that you can't search for linewraps
	 * 
	 * @param text the text to search for
	 * @param start the start-position
	 * @param end the end-position
	 * @param caseSensitive perform a case-sensitive search?
	 * @return a List with the TextPart-objects
	 */
	public List getAllOccurrences(String text,int start,int end,boolean caseSensitive) {
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Text must not be empty");
		
		if(start < 0 || end > length() || start >= end)
			throw new InvalidParameterException("Invalid start- or end-position");
		
		if(!caseSensitive)
			text = text.toLowerCase();
		
		// no new-lines are supported
		text = text.replace('\r',' ');
		text = text.replace('\n',' ');
		
		List results = new ArrayList();
		_rootEnv.collectSearchResults(text,start,end,caseSensitive,results);
		return results;
	}

	/**
	 * exports the complete text in BBCode-syntax.
	 * 
	 * @return the text of the control in BBCode-syntax
	 */
	public String getText() {
		IExportContent type = getExportType(IPublicController.SYNTAX_BBCODE);
		Exporter ex = new Exporter(_textArea,type);
		return ex.getContent();
	}
	
	/**
	 * exports the text from the given start-position to the end-position
	 * in BBCode-syntax.
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the text in the given interval in BBCode-syntax
	 */
	public String getText(int start,int end) {
		IExportContent type = getExportType(IPublicController.SYNTAX_BBCODE);
		Exporter ex = new Exporter(_textArea,type);
		return ex.getContent(start,end);
	}
	
	/**
	 * exports the complete text of the control in the given syntax
	 * 
	 * @param syntax the syntax-type. see SYNTAX_*
	 * @return the text in the given syntax
	 */
	public String getText(int syntax) {
		IExportContent type = getExportType(syntax);
		if(type != null) {
			Exporter ex = new Exporter(_textArea,type);
			return ex.getContent();
		}
		
		throw new InvalidParameterException("Invalid syntax-value");
	}
	
	/**
	 * exports the text from the given start-position to the end-position
	 * in the given syntax
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param syntax the syntax to use, see SYNTAX_*
	 * @return the text in the given interval in the given syntax
	 */
	public String getText(int start,int end,int syntax) {
		IExportContent type = getExportType(syntax);
		if(type != null) {
			Exporter ex = new Exporter(_textArea,type);
			return ex.getContent(start,end);
		}
		
		throw new InvalidParameterException("Invalid syntax-value");
	}
	
	/**
	 * creates the corresponding export-type by the given syntax
	 * 
	 * @param syntax the syntax to use. see IPublicController.SYNTAX_*
	 * @return the export-type or null
	 */
	private IExportContent getExportType(int syntax) {
		switch(syntax) {
			case IPublicController.SYNTAX_BBCODE:
				return new BBCodeExportContent();
			case IPublicController.SYNTAX_HTML:
				return new HTMLExportContent(_textArea);
			case IPublicController.SYNTAX_PLAIN:
				return new PlainExportContent();
			default:
				return null;
		}
	}

	/**
	 * adds a new-line at current position
	 * 
	 * @return the result-code
	 */
	public int addNewLine() {
		return addNewLine(null,ParagraphAttributes.ALIGN_UNDEF,true);
	}

	/**
	 * adds a new-line at current position
	 * 
	 * @param attributes the attributes to use for the new line (null = attributes from
	 *				the last section in the last paragraph)
	 * @param align the align for the new paragraph
	 * @param isListPoint is it a new list-point? (will just be used in list-envs)
	 * @return the result-code
	 */
	public int addNewLine(TextAttributes attributes,int align,boolean isListPoint) {
		// just use this in list-envs
		isListPoint = isListPoint || !(_currentEnv instanceof ListEnvironment);

		_currentEnv.moveToNextParagraph(attributes,align,isListPoint);
		
		// add to history
		int cursorPos = getCurrentCursorPos();
		String nl = String.valueOf(isListPoint ? '\n' : '\r');
		AttributeText text = new AttributeText(nl,attributes,align);
		AddTextAction action = new AddTextAction(this,cursorPos - 1,cursorPos,text);
		_historyManager.add(action);
		
		markContentChanged();
		markRepaintAll();
		
		return RES_DEFAULT;
	}
	
	/**
	 * @param syntax the syntax to use
	 * @return the html-text from the clipboard
	 */
	private String getStringFromClipBoard(int syntax) {
		Transferable clipboardContent = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(this);
		
		// look for text/html
		if(syntax == IPublicController.SYNTAX_HTML) {
			DataFlavor html = null;
			try {
				html = DataFlavor.selectBestTextFlavor(clipboardContent.getTransferDataFlavors());
				if(html.getMimeType().startsWith("text/html")) {
					// read html-code from stream
					Object o = clipboardContent.getTransferData(html);
					BufferedReader reader = new BufferedReader((InputStreamReader)o);
					StringBuffer str = new StringBuffer();
					char[] buf = new char[512];
					while(reader.read(buf) > 0) {
						str.append(buf);
					}
					
					return str.toString();
				}
			}
			catch(Exception e) {
				
			}
		}

		// ok, use the plain-text version
		if((clipboardContent != null) &&
				clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				return (String)clipboardContent.getTransferData(DataFlavor.stringFlavor);
			}
			catch(Exception ex) {
				
			}
		}
		
		return "";
	}
	
	/**
	 * pastes the current clipboard-content at the cursor-position
	 * 
	 * @param syntax the syntax to use 
	 * @return the result-code
	 */
	public int pasteTextAtCursor(int syntax) {
		String clipboard = getStringFromClipBoard(syntax);
		if(clipboard == null || clipboard.length() == 0)
			return RES_NOTHING_DONE;

		// we want to use just unix-linewraps
		clipboard = StringUtils.simpleReplace(clipboard,"\r\n","\n");
		clipboard = StringUtils.simpleReplace(clipboard,"\r","\n");
		
		if(syntax == IPublicController.SYNTAX_HTML || syntax == IPublicController.SYNTAX_PLAIN_HTML) {
			HTMLTokenizer tok = new HTMLTokenizer(clipboard);
			HTMLParser p = new HTMLParser(this,tok.getTokens());
			return pasteTextAtCursor(p.convertToBBCode(),true);
		}
		else if(syntax == IPublicController.SYNTAX_PLAIN)
			return pasteTextAtCursor(clipboard,false);
		else
			return pasteTextAtCursor(clipboard,true);
	}
	
	/**
	 * Adds the given text at the cursor-position
	 * 
	 * @param text the text to paste
	 * @param attributes a Map with the attributes of the text to paste
	 * @return the result-code
	 */
	public int pasteTextAtCursor(String text,TextAttributes attributes) {
		return addTextAt(text,getCurrentCursorPos(),attributes);
	}

	/**
	 * Adds the given text at the cursor-position. If <code>isBBCode</code> is
	 * true it will be parsed as BBCode.
	 * 
	 * @param text the text to paste
	 * @param isBBCode parse it as bbcode?
	 * @return the result-code
	 */
	public int pasteTextAtCursor(String text,boolean isBBCode) {
		return addTextAt(text,getCurrentCursorPos(),isBBCode);
	}
	
	/**
	 * Adds the given text at the given position. If <code>isBBCode</code> is
	 * true it will be parsed as BBCode.
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param isBBCode parse it as bbcode?
	 * @return the result-code
	 */
	public int addTextAt(String text,int pos,boolean isBBCode) {
		if(text == null)
			throw new InvalidParameterException("text is null");
		
		if(isBBCode) {
			if(text.length() == 0)
				return RES_NOTHING_DONE;

			int savePos = getCurrentCursorPos();
			goToPosition(pos);
			
			// store the number of entries in the history
			int currentHistoryPos = _historyManager.size();
			
			// save attributes and alignment
			TextAttributes attributes = null;
			ContentSection sec = getCurrentSection();
			if(sec instanceof TextSection) {
				attributes = ((TextSection)sec).getCloneOfAttributes();
				// ensure that we replace the attributes!
				attributes.ensureSet(TextAttributes.getAll());
			}
			int align = getCurrentParagraph().getHorizontalAlignment();
			
			// parse the text
			BBCodeTokenizer tok = new BBCodeTokenizer(this,text);
			BBCodeParser p = new BBCodeParser(this,tok.getTokens());
			int error = p.parse();
			
			// parse error?
			if(error != BBCodeParser.ERR_NO_ERROR)
				_textArea.invokeBBCodeParseErrorListeners(text,error,BBCodeParser.getErrorMsg(error));

			// remove all actions that have been added
			int amount = _historyManager.size() - currentHistoryPos;
			_historyManager.removeLast(amount);
			
			BBCodeText bbctext = new BBCodeText(text);
			PasteAction action = new PasteAction(this,savePos,getCurrentCursorPos(),
					align,attributes,bbctext);
			_historyManager.add(action);
			
			return RES_DEFAULT;
		}

		return addTextAt(text,pos,null);
	}

	/**
	 * Adds the given text with the given attributes at the given position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes the attributes of the text to paste
	 * @return the result-code
	 */
	public int addTextAt(String text,int pos,TextAttributes attributes) {
		return addTextAt(text,pos,attributes,ParagraphAttributes.ALIGN_UNDEF);
	}

	/**
	 * Adds the given text with the given attributes at the cursor-position
	 * 
	 * @param text the text to paste
	 * @param attributes the attributes of the text to paste
	 * @param align the horizontal alignment of the pasted text
	 * @return the result-code
	 */
	public int pasteTextAtCursor(String text,TextAttributes attributes,int align) {
		return addTextAt(text,getCurrentCursorPos(),attributes,align);
	}

	/**
	 * Adds the given text with the given attributes at the given position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes the attributes of the text to paste
	 * @param align the horizontal alignment of the pasted text
	 * @return the result-code
	 */
	public int addTextAt(String text,int pos,TextAttributes attributes,int align) {
		if(text == null)
			throw new InvalidParameterException("text is null");
		
		if(!isValidAlignment(align,true))
			throw new InvalidParameterException("invalid align");

		// text empty?
		if(text.length() == 0) {
			// look if we have to change attributes or the align
			if(attributes != null)
				setAttributes(pos,pos,attributes);
			
			if(align != ParagraphAttributes.ALIGN_UNDEF)
				setLineAlignment(align);
			
			if(attributes != null || align != ParagraphAttributes.ALIGN_UNDEF)
				return RES_DEFAULT;

			return RES_NOTHING_DONE;
		}
		
		// use temporary attributes, if any
		if(_tempAttributes != null)
			attributes = _tempAttributes;
		
		// collect text for smiley-replaces
		int smileyMaxLength = _smileys.getMaxSmileyLength();
		ContentSection sec = _rootEnv.getSectionAt(pos);
		Paragraph para = sec.getSectionParagraph();
		Environment env = sec.getParentEnvironment();
		
		String lineText = para.getText();
		int smEnd = Math.min(
				lineText.length(),
				pos - para.getElementStartPos() - env.getGlobalStartPos()
		);
		
		int smileyStartInPara = Math.max(0,smEnd - smileyMaxLength);
		String smileyText = lineText.substring(smileyStartInPara,smEnd);
		int replaceLen = smileyText.length();
		smileyText += text;
		
		// collect the actions
		List actions = new ArrayList();
		int replace = collectAddTextActions(env,pos,replaceLen,smileyText,
				attributes,align,actions);
		
		// store the number of entries in the history
		int currentHistoryPos = _historyManager.size();
		
		// remove the beginning?
		if(replace >= 0) {
			int replaceStart = replaceLen - replace;
			removeText(pos - replaceStart,pos,false);
		}
		
		Iterator it = actions.iterator();
		
		// we want to skip the text to replace if there is any
		if(replace >= 0 && it.hasNext())
			it.next();
		
		while(it.hasNext()) {
			HistoryActionPart part = (HistoryActionPart)it.next();
			part.execute(this);
		}

		// remove all actions that have been added because we want to store
		// the collected actions
		int amount = _historyManager.size() - currentHistoryPos;
		_historyManager.removeLast(amount);
		
		// add to history
		int startPos = replace >= 0 ? pos - (replaceLen - replace) : pos;
		ReplaceSmileyListAction haction = new ReplaceSmileyListAction(this,
				startPos,getCurrentCursorPos(),actions,replace >= 0);
		_historyManager.add(haction);
		
		if(attributes != null)
			markAttributesChanged();
		
		markContentChanged();
		
		return RES_DEFAULT;
	}
	
	/**
	 * collects all required actions to add the given text.<br>
	 * Will search for smileys and new lines.
	 * 
	 * @param env the Environment of the position
	 * @param pos the position to start with
	 * @param replaceLen the number of characters which might be replaced
	 * @param text the text to parse
	 * @param attributes the attributes to apply
	 * @param align the alignment to use
	 * @param actions the actions to collect
	 * @return the position where to start replacing (in the given text)
	 */
	private int collectAddTextActions(Environment env,int pos,int replaceLen,
			String text,TextAttributes attributes,int align,List actions) {
		int replace = -1;
		int start = -replaceLen;
		int maxSmileyLen = _smileys.getMaxSmileyLength();
		
		StringBuffer buf = new StringBuffer();
		for(int i = 0,len = text.length();i < len;i++) {
			if(env.containsStyles() && _replaceSmileys) {
				String sub = text.substring(i,Math.min(text.length(),i + maxSmileyLen));
				String smiley = _smileys.getSmileyAtBeginning(sub);
				if(smiley != null) {
					// is the first smiley in the existing text in the control?
					if(i < replaceLen) {
						// so we store the replacement start and add the text to the buffer
						replace = i;
						buf.append(text.charAt(i));
					}
					
					// add the collected text if necessary
					int tPos = pos + start + i;
					if(buf.length() > 0) {
						if(actions.size() == 0 && align != ParagraphAttributes.ALIGN_UNDEF) {
							Paragraph para = _rootEnv.getContentParagraphAtPosition(tPos);
							int globalStart = para.getElementStartPos() + para.getParentEnvironment().getGlobalStartPos();
							if(para.getHorizontalAlignment() != align && !para.isEmpty() && tPos != globalStart) {
								AddNewLineActionPart action = new AddNewLineActionPart(
										tPos - buf.length() + 1,attributes,align,true);
								actions.add(action);
								tPos++;
							}
						}
						
						AttributeText aText = new AttributeText(buf.toString(),attributes,align);
						AddPlainTextInEnvActionPart action = new AddPlainTextInEnvActionPart(
								tPos - buf.length() + 1,aText);
						actions.add(action);
						buf = new StringBuffer();
					}
					
					// add smiley
					SecSmiley sm = _smileys.getSmileyByCode(smiley);
					AddImageActionPart action = new AddImageActionPart(tPos,sm);
					actions.add(action);
					
					// skip following smiley-chars
					i += smiley.length() - 1;
					continue;
				}
			}
			
			// just add the text after the potential replacement section
			if(i >= replaceLen) {
				char c = text.charAt(i);
				if(c == '\n' || c == '\r') {
					// add the collected text if necessary
					int tPos = pos + start + i;
					if(buf.length() > 0) {
						if(actions.size() == 0 && align != ParagraphAttributes.ALIGN_UNDEF) {
							Paragraph para = _rootEnv.getContentParagraphAtPosition(tPos);
							int globalStart = para.getElementStartPos() + para.getParentEnvironment().getGlobalStartPos();
							if(para.getHorizontalAlignment() != align && !para.isEmpty() && tPos != globalStart) {
								AddNewLineActionPart action = new AddNewLineActionPart(tPos - buf.length(),
										attributes,align,true);
								actions.add(action);
								tPos++;
							}
						}
						
						AttributeText aText = new AttributeText(buf.toString(),attributes,align);
						AddPlainTextInEnvActionPart action = new AddPlainTextInEnvActionPart(
								tPos - buf.length(),aText);
						actions.add(action);
						buf = new StringBuffer();
					}
					
					// add new line
					boolean listPoint = c != '\r' || !(env instanceof ListEnvironment);
					AddNewLineActionPart action = new AddNewLineActionPart(tPos,
							attributes,align,listPoint);
					actions.add(action);
				}
				else
					buf.append(c);
			}
		}
		
		// add remaining text
		if(buf.length() > 0) {
			int tPos = pos + start + text.length() - buf.length();
			if(actions.size() == 0 && align != ParagraphAttributes.ALIGN_UNDEF) {
				Paragraph para = _rootEnv.getContentParagraphAtPosition(tPos);
				int globalStart = para.getElementStartPos() + para.getParentEnvironment().getGlobalStartPos();
				if(para.getHorizontalAlignment() != align && !para.isEmpty() && tPos != globalStart) {
					AddNewLineActionPart action = new AddNewLineActionPart(tPos,
							attributes,align,true);
					actions.add(action);
					tPos++;
				}
			}
			
			AttributeText aText = new AttributeText(buf.toString(),attributes,align);
			AddPlainTextInEnvActionPart action = new AddPlainTextInEnvActionPart(tPos,aText);
			actions.add(action);
		}
		
		return replace;
	}
	
	/**
	 * adds the given text at the cursor-position with the given attributes
	 * 
	 * @param text the text to paste
	 * @param attributes the attributes to use
	 * @param align the alignment
	 */
	public void addPlainText(String text,TextAttributes attributes,int align) {
		//Paragraph p = getCurrentParagraph();
		//if(p.getHorizontalAlignment() != align && !p.isEmpty())
		//	addNewLine(attributes,align,true);
		
		_currentEnv.addStringAtCursor(text,attributes);
		
		// apply the alignment to the paragraph
		if(align != ParagraphAttributes.ALIGN_UNDEF)
			getCurrentParagraph().setHorizontalAlignment(align);
		
		markContentChanged();
		_textArea.getRepaintManager().addDirtyLine(getCurrentLine(),false);
	}
	
	/**
	 * sets the text of the control to given value
	 * the text will be interpreted as BBCode
	 * 
	 * @param text the text to set
	 * @return the result-code
	 */
	public int setText(String text) {
		if(text == null)
			throw new InvalidParameterException("text is null");
		
		clear();
		pasteTextAtCursor(text,true);
		// Note that we don't return the result of pasteTextAtCursor() because the text may be
		// empty which results in RES_NOTHING_DONE.
		// But we have done something in all cases :)
		return RES_DEFAULT;
	}
	
	/**
	 * checks wether the given attributes is enabled
	 * 
	 * @param attribute the attribute to check
	 * @return true if the attribute is enabled
	 */
	public boolean isAttributeEnabled(Integer attribute) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		Integer tID = (Integer)_attributesToTags.get(attribute);
		return isTagEnabled(tID.intValue());
	}
	
	/**
	 * Cleans the given map. Checks wether the attributes that are set are
	 * enabled.
	 * 
	 * @param attributes the attributes you have
	 */
	public void cleanAttributes(TextAttributes attributes) {
		if(attributes == null)
			throw new InvalidParameterException("attributes is null");
		
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			
			Integer tID = (Integer)_attributesToTags.get(attr);
			if(tID == null || !isTagEnabled(tID.intValue()))
				attributes.unset(attr);
		}
	}
	
	/**
	 * refreshes the content
	 * 
	 * @return the result-code
	 */
	int refreshContent() {
		if(length() > 0) {
			IExportContent con = getExportType(IPublicController.SYNTAX_BBCODE);
			Exporter ex = new Exporter(_textArea,con);
			String content = ex.getContent();
			
			return setText(content);
		}
		
		return RES_NOTHING_DONE;
	}
	
	/**
	 * refreshes all fonts
	 */
	void refreshFonts() {
		_rootEnv.getEnvView().refreshFonts();
		
		markRepaintAll();
	}
	
	/**
	 * refreshes the wordwrap in all paragraphs
	 * 
	 * @return the result-code
	 */
	int refreshWordWrap() {
		if(refreshWordWrap(_rootEnv)) {
			
			// we have to current the current section because the section / line may change
			correctCurrentSection();
			
			// paint positions may have changed
			markRepaintAll();
			
			// we want to move the scrollpane back to the cursor
			_textArea.getCursorManager().forceCursorChange();
			
			return RES_DEFAULT;
		}
		
		return RES_NOTHING_DONE;
	}
	
	/**
	 * refreshes the wordwrap in all paragraphs of the environment recursivly
	 * 
	 * @param env the environment
	 * @return true if something has changed
	 */
	private boolean refreshWordWrap(Environment env) {
		boolean changed = false;
		
		Paragraph p = env.getFirstParagraph();
		do {
			if(p.containsEnvironment()) {
				if(refreshWordWrap((Environment)p.getFirstSection()))
					changed = true;
			}
			else {
				if(p.performWordWrap() > 0)
					changed = true;
			}
			
			p = (Paragraph)p.getNext();
		} while(p != null);
		
		return changed;
	}
	
	/**
	 * Sets the highlighting in the given environment to the given syntax
	 * 
	 * @param env the environment
	 * @param highlightSyntax the new syntax
	 * @see HighlightSyntax
	 * @return the result
	 */
	public int setHighlightSyntax(CodeEnvironment env,Object highlightSyntax) {
		if(env == null)
			throw new InvalidParameterException("env = null");
		
		Object old = env.getHighlightSyntax();
		if((old == null && highlightSyntax == null) || (old != null && old.equals(highlightSyntax)))
			return RES_NOTHING_DONE;
		
		env.setHighlightSyntax(highlightSyntax);
		
		markRepaintAll();
		return RES_DEFAULT;
	}
	
	/**
	 * refreshes the tab-width in all sections
	 */
	void refreshTabWidth() {
		_rootEnv.getEnvView().refreshTabWidth();
		markRepaintAll();
	}
	
	/**
	 * refreshes all paint-positions
	 */
	void refreshPaintPositions() {
		MutableInt x = new MutableInt(0);
		MutableInt y = new MutableInt(0);
		MutableInt maxWidth = new MutableInt(0);
		_rootEnv.getEnvView().setPaintPositions(_textArea.getGraphics(),x,y,maxWidth);
		
		_textArea.setCompleteSize(maxWidth.getValue(),y.getValue());
	}
	
	/**
	 * refreshes all paint-positions in the given paragraph
	 * assumes that the paint-position of the last paragraph is correct
	 * (and of the following of course)
	 * 
	 * @param p the paragraph in which to refresh the paint-positions
	 */
	void refreshPaintPositionsInParagraph(Paragraph p) {
		Paragraph prev = (Paragraph)p.getPrev();
		Environment parent = p.getParentEnvironment();
		IEnvironmentView parentView = parent.getEnvView();
		
		MutableInt x = new MutableInt(parentView.getGlobalTextStart());
		MutableInt y;
		if(prev != null) {
			Point lp = prev.getView().getPaintPos();
			if(lp == null)
				return;
			
			y = new MutableInt(lp.y + prev.getParagraphView().getHeight());
		}
		// so it must be the first line in the environment...
		else {
			Point ep = parent.getView().getPaintPos();
			if(ep == null)
				return;
			
			y = new MutableInt(ep.y + parentView.getInnerTopPadding() + parentView.getOuterPadding());
		}
		
		MutableInt maxWidth = new MutableInt(0);
		p.getParagraphView().setPaintPositions(_textArea.getGraphics(),x,y,maxWidth);
		
		// don't change the height and don't decrease the width
		int newWidth = Math.max(_textArea.getRequiredWidth(),maxWidth.getValue());
		_textArea.setCompleteSize(newWidth,_textArea.getRequiredHeight());
	}
	
	/**
	 * A convenience-method to mark that the content has changed
	 */
	private void markContentChanged() {
		_textArea.getCursorManager().markContentChanged();
	}
	
	/**
	 * A convenience-method to mark that the attributes have changed
	 */
	private void markAttributesChanged() {
		_textArea.getAttributesManager().markChanged();
	}

	/**
	 * A convenience-method to mark everything dirty
	 */
	private void markRepaintAll() {
		_textArea.getRepaintManager().markCompletlyDirty();
	}

	/**
	 * paint the 'root-environment'. this environment will do the rest :)
	 * 
	 * @param g the graphics-object
	 * @param paintRect the rectangle which should be repainted
	 * @param x the x-position
	 * @param y the y-position
	 * @param showCursor should the cursor be painted?
	 */
	void paint(Graphics g,Rectangle paintRect,int x,int y,boolean showCursor) {
		// we have to refresh the paint-positions if this has never been done yet
		if(_rootEnv.getView().getPaintPos() == null)
			refreshPaintPositions();
		
		Selection sel = _textArea.getSelection();
		MutableInt mx = new MutableInt(x);
		MutableInt my = new MutableInt(y);
		_rootEnv.getEnvView().paint(g,paintRect,mx,my,showCursor,sel.getSelectionStart(),
				sel.getSelectionEnd());
	}

	/**
	 * debugging information
	 * 
	 * @return information about the controller
	 */
	public String toString() {
		StringBuffer debug = new StringBuffer();
		debug.append(_textArea.getHistory().toString() + "\n");
		debug.append("Global CursorPos: " + _textArea.getCurrentCursorPos() + "\n");
		debug.append("Selection: \n" + _textArea.getSelection() + "\n");
		TextPart wordAtCursor = _textArea.getWordAtCursor();
		debug.append("Word at cursor: ");
		if(wordAtCursor != null)
			debug.append(wordAtCursor.text + " [" + wordAtCursor.startPos + "," + wordAtCursor.endPos + "]");
		else
			debug.append("null");
		debug.append("\n\n");
		debug.append("Environment:\n");
		debug.append(_rootEnv);
		
		return debug.toString();
	}
}