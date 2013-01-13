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

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JViewport;

import bbcodeeditor.control.events.*;
import bbcodeeditor.control.export.Exporter;
import bbcodeeditor.control.export.IExportContent;
import bbcodeeditor.control.export.bbcode.BBCodeExportContent;
import bbcodeeditor.control.export.html.HTMLExportContent;
import bbcodeeditor.control.export.plain.PlainExportContent;
import bbcodeeditor.control.tools.MutableInt;
import bbcodeeditor.control.tools.TextPart;
import bbcodeeditor.control.view.ILineView;


/**
 * This is the textfield-component.<br>
 * Don't forget to use SwingUtilities.invokeLater() to call methods of this class if you
 * are not in the UI-thread!
 * <br>
 * All actions can be done directly with this class.
 * <br>
 * An example:
 * <pre>
 * BBCTextField myTf = new BBCTextField();
 * myTf.setText("[b]test [i]it[/i]![/b]");
 * System.out.println(myTf.getText());
 * 
 * myTf.goToVeryBeginning(false);
 * myTf.pasteTextAtCursor("[color=red]red text[/color]",true);
 * </pre>
 * 
 * @author hrniels
 */
public class BBCTextField extends AbstractTextField {
	
	private static final long serialVersionUID = -3889218294991714454L;

	/**
	 * the history-limit
	 */
	private int _historyLimit = 100;
	
	/**
	 * the selection
	 */
	private final Selection _selection;
	
	/**
	 * the history which allows us to undo/redo actions
	 */
	private final History _history;
	
	/**
	 * The repaint-manager
	 */
	private bbcodeeditor.control.RepaintManager _repaintManager;
	
	/**
	 * The attributes-manager
	 */
	private AttributesManager _attrManager;
	
	/**
	 * The cursor-manager
	 */
	private CursorManager _cursorManager;
	
	/**
	 * The paint-position-manager
	 */
	private PaintPosManager _paintPosManager;
	
	/**
	 * The view-manager
	 */
	private ViewManager _viewManager;
	
	/**
	 * The wordwrap-manager
	 */
	private WordwrapManager _wordwrapManager;
	
	/**
	 * Constructor
	 */
	public BBCTextField() {
		this("");
	}
		
	/**
	 * constructor
	 * 
	 * @param text the initial text (BBCode)
	 */
	public BBCTextField(String text) {
		_history = new History(getHistoryLimit());
		_selection = new Selection(_controller);
		
		addMouseMotionListener(new TextAreaMouseMotionListener(this));
		setBasicKeyListener(new TextAreaKeyListener(this));
		addMouseListener(new TextAreaMouseListener(this));
		
		if(text != null && text.length() > 0)
			setText(text);
	}
	
	bbcodeeditor.control.RepaintManager getRepaintManager() {
		if(_repaintManager == null)
			_repaintManager = new bbcodeeditor.control.RepaintManager(this);
		
		return _repaintManager;
	}
	
	public PaintPosManager getPaintPosManager() {
		if(_paintPosManager == null)
			_paintPosManager = new PaintPosManager(_controller);
		
		return _paintPosManager;
	}
	
	AttributesManager getAttributesManager() {
		if(_attrManager == null)
			_attrManager = new AttributesManager(this);
		
		return _attrManager;
	}
	
	CursorManager getCursorManager() {
		if(_cursorManager == null)
			_cursorManager = new CursorManager(this);
		
		return _cursorManager;
	}
	
	public ViewManager getViewManager() {
		if(_viewManager == null)
			_viewManager = new ViewManager();
		
		return _viewManager;
	}
	
	public WordwrapManager getWordwrapManager() {
		if(_wordwrapManager == null)
			_wordwrapManager = new WordwrapManager(this);
		
		return _wordwrapManager;
	}
	
	/**
	 * Returns the limit for the history. The limit affects the undo- and redo-list
	 * separatly. That means that if you for example set the limit to 20 the undo-list
	 * can't contain more that 20 elements and the redo-list, too.
	 * 
	 * @return the limit for the history
	 */
	public int getHistoryLimit() {
		return _historyLimit;
	}
	
	/**
	 * Sets the limit for the history. The limit affects the undo- and redo-list
	 * separatly. That means that if you for example set the limit to 20 the undo-list
	 * can't contain more that 20 elements and the redo-list, too.
	 * 
	 * @param limit the new limit
	 */
	public void setHistoryLimit(int limit) {
		if(_historyLimit != limit)
			_history.setLimit(limit);
		
		_historyLimit = limit;
	}
	
	/**
	 * Adds the given listener to the history-changed-listener list. It will receive an
	 * event as soon as the number of undo- or redo-items has changed
	 * 
	 * @param l the listener
	 */
	public void addHistoryChangedListener(HistoryChangedListener l) {
		if(l != null)
			_history.addHistoryChangedListener(l);
	}
	
	/**
	 * Removes the given listener from the list
	 * 
	 * @param l the listener
	 */
	public void removeHistoryChangedListener(HistoryChangedListener l) {
		if(l != null)
			_history.removeHistoryChangedListener(l);
	}
	
	/**
	 * Adds the given listener to the selection-changed-listener list. It will receive an
	 * event as soon as the selection has changed in any way.
	 * 
	 * @param l the listener
	 */
	public void addSelectionChangedListener(SelectionChangedListener l) {
		if(l != null)
			_selection.addSelectionChangedListener(l);
	}
	
	/**
	 * Removes the given listener from the list
	 * 
	 * @param l the listener
	 */
	public void removeSelectionChangedListener(SelectionChangedListener l) {
		if(l != null)
			_selection.removeSelectionChangedListener(l);
	}
	
	// -----------------------------
	// ----- GET CURRENT STATE -----
	// -----------------------------
	
	public Environment getCurrentEnvironment() {
		return _controller.getCurrentEnvironment();
	}
	
	public Environment getRootEnvironment() {
		return _controller.getRootEnvironment();
	}
	
	public int getCurrentCursorPos() {
		return _controller.getCurrentCursorPos();
	}
	
	public ContentSection getCurrentSection() {
		return _controller.getCurrentSection();
	}
	
	public Line getCurrentLine() {
		return _controller.getCurrentLine();
	}
	
	public Paragraph getCurrentParagraph() {
		return _controller.getCurrentParagraph();
	}
	
	public int length() {
		return _controller.length();
	}
	
	public ContentSection getSectionAtPixelPos(int x,int y) {
		return _controller.getSectionAtPixelPos(x,y);
	}

	public Selection getSelection() {
		return _selection;
	}
	
	public History getHistory() {
		return _history;
	}

	public int getEditorMode() {
		return _editorMode;
	}

	public void setEditorMode(int mode) {
		if(mode == MODE_BBCODE || mode == MODE_HTML || mode == MODE_TEXT_EDITOR)
			changeMode(mode);
	}
	
	public int[] getPositionsForActions() {
		int start = -1;
		int end = -1;
		
		// if something is selected apply the attribute to the selection
		if(!_selection.isEmpty()) {
			start = _selection.getSelectionStart();
			end = _selection.getSelectionEnd();
		}
		// apply the attribute to the word at the cursor
		else if(getCurrentEnvironment().containsStyles()) {
			TextPart word = getWordAtCursor();
			if(word != null) {
				start = word.startPos;
				end = start + word.text.length();
			}
		}
		
		return new int[] {start,end};
	}

	public TextPart getWordAtCursor() {
		return _controller.getWordAtCursor();
	}
	
	// -----------------------------
	// ------- MISCELLANEOUS -------
	// -----------------------------

	public void startHistoryCache() {
		_history.startCaching();
	}

	public void stopHistoryCache() {
		_history.stopCaching();
	}
	
	public void refreshFonts() {
		_controller.refreshFonts();
		finish();
	}
	
	public void refreshContent() {
		int res = _controller.refreshContent();
		finishDefault(res);
	}
	
	public void refreshWordWrap() {
		int res = _controller.refreshWordWrap();
		finishDefault(res);
	}

	public int getTabWidth() {
		return _tabWidth;
	}
	
	public void setTabWidth(int width) {
		_tabWidth = width;
		_controller.refreshTabWidth();
		finish();
	}

	public SmileyContainer getSmileys() {
		return _controller.getSmileys();
	}
	
	public void setSmileys(SmileyContainer con) {
		_controller.setSmileys(con);
		finish();
	}

	public boolean replaceSmileys() {
		return _controller.replaceSmileys();
	}

	public void setReplaceSmileys(boolean replaceSmileys) {
		_controller.setReplaceSmileys(replaceSmileys);
	}
	
	public void redo() {
		if(isReadOnly())
			return;
		
		_history.redo();
		// we don't want to add anything to the history
		_controller.getHistoryManager().clear();
		finish();
	}

	public void undo() {
		if(isReadOnly())
			return;
		
		_history.undo();
		// we don't want to add anything to the history
		_controller.getHistoryManager().clear();
		finish();
	}
	
	public void clear() {
		if(isReadOnly())
			return;
		
		_controller.clear();
		if(_selection.isInSelectionMode())
			_selection.clearSelection();
		
		finish();
	}

	public void moveVisibleAreaLineUp() {
		Container parent = getParent();
		if(parent instanceof JViewport) {
			JViewport viewPort = (JViewport)parent;
			Point currentStart = viewPort.getViewPosition();
			int scrollIncrement = getScrollableUnitIncrement(viewPort.getVisibleRect(),0,0);
			if(currentStart.y > 0)
				viewPort.setViewPosition(new Point(currentStart.x,
						Math.max(0,currentStart.y - scrollIncrement)));
			
			// no repaint here!
		}
	}
	
	public void moveVisibleAreaLineDown() {
		Container parent = getParent();
		if(parent instanceof JViewport) {
			JViewport viewPort = (JViewport)parent;
			Point currentStart = viewPort.getViewPosition();
			int scrollIncrement = getScrollableUnitIncrement(viewPort.getVisibleRect(),0,0);
			int maxPos = getRequiredHeight() - viewPort.getVisibleRect().height;
			if(currentStart.y < maxPos)
				viewPort.setViewPosition(new Point(currentStart.x,
						Math.min(maxPos,currentStart.y + scrollIncrement)));
			
			// no repaint here!
		}
	}

	public void insertLink(boolean isEmail,String title,String address) {
		if(isReadOnly())
			return;
		
		// check parameters
		if(title == null || address == null)
			throw new InvalidParameterException("title or address is null");
		
		if(address.length() > 0) {
			if(title.length() > 0) {
				TextAttributes attributes = new TextAttributes();
				attributes.set(isEmail ? TextAttributes.EMAIL : TextAttributes.URL,address);
				addTextAt(title,getCurrentCursorPos(),attributes);
			}
			else
				setAttribute(isEmail ? TextAttributes.EMAIL : TextAttributes.URL,address);
		}
	}
	
	public void editLink(boolean isEmail,String newAddress) {
		if(isReadOnly())
			return;
		
		// check parameter
		if(newAddress == null)
			throw new InvalidParameterException("address is null");

		if(newAddress.length() > 0) {
			int[] positions = getPositionsForActions();
			if(positions[0] >= 0 && positions[1] >= 0) {
				Integer attr = isEmail ? TextAttributes.EMAIL : TextAttributes.URL;
				setAttribute(positions[0],positions[1],attr,newAddress);
			}
		}
	}
	
	public void setListType(int type) {
		if(isReadOnly())
			return;
		
		if(getCurrentEnvironment() instanceof ListEnvironment) {
			boolean changed = ((ListEnvironment)getCurrentEnvironment()).setListType(type);
			if(changed) {
				getPaintPosManager().markAllDirty();
				getRepaintManager().markCompletlyDirty();
				finish();
			}
		}
	}
	
	public void editImageURL(String newURL) {
		if(isReadOnly())
			return;
		
		// check parameter
		if(newURL == null)
			throw new InvalidParameterException("URL is null");

		if(newURL.length() > 0) {
			ContentSection current = getCurrentSection();
			if(current instanceof ImageSection && !(current instanceof SmileySection)) {
				((ImageSection)current).setImage(new SecImage(this,newURL));
				getRepaintManager().markCompletlyDirty();
				finish();
			}
		}
	}
	
	public void editAuthor(String author) {
		if(isReadOnly())
			return;
		
		if(author == null)
			throw new InvalidParameterException("author = null");
		
		if(getCurrentEnvironment() instanceof QuoteEnvironment) {
			((QuoteEnvironment)getCurrentEnvironment()).setAuthor(author);
			getRepaintManager().markCompletlyDirty();
			finish();
		}
	}
	
	public void indentParagraphs() {
		if(isReadOnly())
			return;
		
		if(_selection.isInSelectionMode()) {
			int oldLen = length();
			int start = _selection.getSelectionStart();
			int end = _selection.getSelectionEnd();
			
			_controller.indentParagraphs(start,end);
			
			int newLen = length();
			_selection.clearSelection();
			_controller.goToPosition(start);
			goToPosition(end + (newLen - oldLen),true);
		}
		else
			pasteTextAtCursor("\t",null);
	}
	
	public void indentParagraphs(int start,int end) {
		if(isReadOnly())
			return;
		
		int res = _controller.indentParagraphs(start,end);
		finishDefault(res);
	}
	
	public void unindentParagraphs() {
		if(isReadOnly())
			return;
		
		if(_selection.isInSelectionMode()) {
			int oldLen = length();
			int start = _selection.getSelectionStart();
			int end = _selection.getSelectionEnd();
			
			_controller.unindentParagraphs(start,end);
			
			int newLen = length();
			_selection.clearSelection();
			_controller.goToPosition(start);
			goToPosition(end + (newLen - oldLen),true);
		}
		else {
			int oldPos = getCurrentCursorPos();
			int oldLen = length();
			Paragraph p = getCurrentParagraph();
			int start = p.getElementStartPos() + p.getParentEnvironment().getGlobalStartPos();
			_controller.unindentParagraphs(start,start);
			
			int newLen = length();
			goToPosition(oldPos + (newLen - oldLen));
		}
	}
	
	public void unindentParagraphs(int start,int end) {
		if(isReadOnly())
			return;
		
		int res = _controller.unindentParagraphs(start,end);
		finishDefault(res);
	}

	// -----------------------------
	// -------- BBCODE-TAGS --------
	// -----------------------------
	
	public void disableAllTags() {
		int res = _controller.disableAllTags();
		finishDefault(res);
	}

	public void disableTag(int tag) {
		int res = _controller.disableTag(tag);
		finishDefault(res);
	}

	public void disableTags(List tags) {
		int res = _controller.disableTags(tags);
		finishDefault(res);
	}

	public void enableAllTags() {
		int res = _controller.enableAllTags();
		finishDefault(res);
	}

	public void enableTag(int tag) {
		int res = _controller.enableTag(tag);
		finishDefault(res);
	}

	public void enableTags(List tags) {
		int res = _controller.enableTags(tags);
		finishDefault(res);
	}

	public boolean isTagEnabled(int tag) {
		return _controller.isTagEnabled(tag);
	}

	// -----------------------------
	// -------- HIGHLIGHTING -------
	// -----------------------------
	
	public void setHighlightSyntax(CodeEnvironment env,Object highlightSyntax) {
		int res = _controller.setHighlightSyntax(env,highlightSyntax);
		finishDefault(res);
	}
	
	public void highlightRegions(List regions,Color highlight) {
		int res = _controller.highlightRegions(regions,highlight);
		finishDefault(res);
	}
	
	public void clearHighlighting() {
		int res = _controller.clearHighlighting();
		finishDefault(res);
	}

	// -----------------------------
	// ------ SEARCH & REPLACE -----
	// -----------------------------
	
	public int replaceFirst(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode) {
		if(isReadOnly())
			return -1;
		
		int pos = _controller.replaceNext(text,repl,start,end,caseSensitive,true,parseBBCode);
		if(pos >= 0)
			finish();
		return pos;
	}
	
	public int replaceLast(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode) {
		if(isReadOnly())
			return -1;
		
		int pos = _controller.replaceNext(text,repl,start,end,caseSensitive,false,parseBBCode);
		if(pos >= 0)
			finish();
		return pos;
	}
	
	public int replaceAll(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode) {
		if(isReadOnly())
			return 0;
		
		int num = _controller.replaceAll(text,repl,start,end,caseSensitive,parseBBCode);
		if(num > 0)
			finish();
		return num;
	}
	
	public TextPart getFirstOccurrence(String text,int start,int end,boolean caseSensitive) {
		return _controller.getFirstOccurrence(text,start,end,caseSensitive);
	}
	
	public TextPart getLastOccurrence(String text,int start,int end,boolean caseSensitive) {
		return _controller.getLastOccurrence(text,start,end,caseSensitive);
	}
	
	public List getAllOccurrences(String text,int start,int end,boolean caseSensitive) {
		return _controller.getAllOccurrences(text,start,end,caseSensitive);
	}

	// -----------------------------
	// -------- ENVIRONMENTS -------
	// -----------------------------
	
	public void addQuoteEnvironment(boolean isListPoint) {
		addQuoteEnvironment(isListPoint,null);
	}

	public void addQuoteEnvironment(boolean isListPoint,String author) {
		QuoteEnvironment env = new QuoteEnvironment(this,getCurrentEnvironment(),null,null,author);
		addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}
	
	public void addCodeEnvironment(boolean isListPoint) {
		CodeEnvironment env = new CodeEnvironment(this,getCurrentEnvironment(),null,null);
		addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}
	
	public void addCodeEnvironment(boolean isListPoint,Object hlSyntax) {
		CodeEnvironment env = new CodeEnvironment(this,getCurrentEnvironment(),null,null,hlSyntax);
		addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}

	public void addListEnvironment(boolean isListPoint) {
		addListEnvironment(isListPoint,ListTypes.TYPE_DEFAULT);
	}
	
	public void addListEnvironment(boolean isListPoint,int listType) {
		ListEnvironment env = new ListEnvironment(this,getCurrentEnvironment(),null,null,listType);
		addEnvironment(env,getCurrentCursorPos(),isListPoint,false);
	}
	
	public void addEnvironmentAt(Environment newEnv,int pos,boolean isListPoint) {
		if(newEnv == null)
			throw new InvalidParameterException("newEnv = null");
		
		addEnvironment(newEnv,pos,isListPoint,false);
	}
	
	/**
	 * Adds the given environment to the given position
	 * 
	 * @param newEnv the environment to add
	 * @param pos the position
	 * @param isListPoint add it as new list-point (if in list)
	 * @param forceNewLine force a new line behind the environment?
	 */
	private void addEnvironment(Environment newEnv,int pos,boolean isListPoint,
			boolean forceNewLine) {
		if(isReadOnly())
			return;
		
		String selText = null;
		if(_selection.isInSelectionMode()) {
			// store the selected text to paste it later
			if(newEnv instanceof CodeEnvironment)
				selText = getSelectedText(IPublicController.SYNTAX_PLAIN);
			else
				selText = getSelectedText(IPublicController.SYNTAX_BBCODE);
			
			pos = ensureNotInSelection(pos);
			removeSelectedText();
		}
		
		// add env
		int res = _controller.addEnvironment(newEnv,pos,isListPoint,forceNewLine);
		if(res == Controller.RES_NOTHING_DONE)
			return;
		
		// should we paste some text into the environment?
		if(selText != null)
			_controller.pasteTextAtCursor(selText,!(newEnv instanceof CodeEnvironment));
		
		finish();
	}

	// -----------------------------
	// ------- IMAGES/SMILEYS ------
	// -----------------------------
	
	public void addSmiley(SecSmiley smiley) {
		addImageImpl(smiley);
	}

	public void addImage(SecImage image) {
		addImageImpl(image);
	}
	
	public void addImage(SecImage image,int pos) {
		if(isReadOnly())
			return;
		
		int res = _controller.addImage(image,pos);
		finishDefault(res);
	}
	
	private void addImageImpl(SecImage image) {
		if(isReadOnly())
			return;

		if(_selection.isInSelectionMode())
			removeSelectedText();
		
		int res = _controller.addImageAtCursor(image);
		finishDefault(res);
	}

	// -----------------------------
	// ---------- ADD TEXT ---------
	// -----------------------------
	
	public void pasteTextAtCursor(String text,TextAttributes attributes) {
		addTextAt(text,getCurrentCursorPos(),attributes,ParagraphAttributes.ALIGN_UNDEF);
	}

	public void pasteTextAtCursor(String text,TextAttributes attributes,int align) {
		addTextAt(text,getCurrentCursorPos(),attributes,align);
	}

	public void addTextAt(String text,int pos,TextAttributes attributes) {
		addTextAt(text,pos,attributes,ParagraphAttributes.ALIGN_UNDEF);
	}

	public void addTextAt(String text,int pos,TextAttributes attributes,int align) {
		if(isReadOnly())
			return;

		if(_selection.isInSelectionMode()) {
			pos = ensureNotInSelection(pos);
			removeSelectedText();
		}
		
		int res = _controller.addTextAt(text,pos,attributes,align);
		finishDefault(res);
	}

	public void pasteTextAtCursor() {
		if(isReadOnly())
			return;

		if(_editorMode == IPublicController.MODE_BBCODE)
			pasteTextAtCursor(IPublicController.SYNTAX_BBCODE);
		else if(_editorMode == IPublicController.MODE_HTML)
			pasteTextAtCursor(IPublicController.SYNTAX_PLAIN_HTML);
		else
			pasteTextAtCursor(IPublicController.SYNTAX_PLAIN);
	}

	public void pasteTextAtCursor(int syntax) {
		if(isReadOnly())
			return;

		if(_selection.isInSelectionMode())
			removeSelectedText();
		
		int res = _controller.pasteTextAtCursor(syntax);
		finishDefault(res);
	}

	public void pasteTextAtCursor(String text,boolean isBBCode) {
		if(isReadOnly())
			return;

		if(_selection.isInSelectionMode())
			removeSelectedText();

		int res = _controller.pasteTextAtCursor(text,isBBCode);
		finishDefault(res);
	}
	
	public void setPlainText(String text) {
		if(isReadOnly())
			return;

		_controller.clear();
		int res = _controller.pasteTextAtCursor(text,false);
		finishDefault(res);
	}
	
	public void setText(String text) {
		if(isReadOnly())
			return;

		int res = _controller.setText(text);
		finishDefault(res);
	}

	// -----------------------------
	// -------- ADD NEW LINE -------
	// -----------------------------
	
	public void addNewLine() {
		addNewLineImpl(null,ParagraphAttributes.ALIGN_UNDEF,true);
	}
	
	public void addNewLineInList() {
		addNewLineImpl(null,ParagraphAttributes.ALIGN_UNDEF,false);
	}
	
	/**
	 * Adds a new line
	 * 
	 * @param attributes
	 * @param align
	 * @param isListPoint
	 */
	private void addNewLineImpl(TextAttributes attributes,int align,boolean isListPoint) {
		if(isReadOnly())
			return;
		
		if(_selection.isInSelectionMode())
			removeSelectedText();

		// are we in a list-environment?
		Environment cEnv = getCurrentEnvironment();
		if(cEnv instanceof ListEnvironment && isListPoint) {
			// check wether the last paragraph is empty. In this case
			// we want to leave the list
			
			ContentSection curSec = cEnv.getCurrentSection();
			Paragraph p = curSec.getSectionParagraph();
			// is the current paragraph empty?
			if(p.isListPoint() && p.getElementLength() == 0) {
				int start,end,pos;
				// remove the complete env?
				if(cEnv.getParagraphCount() == 1) {
					start = cEnv.getParentEnvironment().getGlobalStartPos() +
					cEnv.getSectionParagraph().getElementStartPos() - 1;
					end = start + 2;
					pos = start;
				}
				// just the last paragraph...
				else {
					start = cEnv.getGlobalStartPos() + p.getElementStartPos() - 1;
					end = start + 1;
					pos = end;
				}
				
				// remove it and go behind the list
				_controller.removeText(start,end,true);
				_controller.goToPosition(pos);
				finish();
				return;
			}
		}
		
		int res = _controller.addNewLine(attributes,align,isListPoint);
		
		// were we in a code-environment?
		if(cEnv instanceof CodeEnvironment) {
			Paragraph cPara = _controller.getCurrentParagraph();
			Paragraph pPara = (Paragraph)cPara.getPrev();
			if(pPara != null) {
				// collect the whitespace at the beginning of the previous paragraph
				String pText = pPara.getText();
				StringBuffer whitespace = new StringBuffer();
				for(int i = 0,len = pText.length();i < len;i++) {
					char c = pText.charAt(i);
					if(Character.isWhitespace(c))
						whitespace.append(c);
					else
						break;
				}
				
				// is there any whitespace?
				if(whitespace.length() > 0)
					_controller.pasteTextAtCursor(whitespace.toString(),false);
			}
		}
		
		finishDefault(res);
	}

	// -----------------------------
	// ------ CURSOR MOVEMENT ------
	// -----------------------------

	public void back() {
		back(false);
	}

	public void back(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.back();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void forward() {
		forward(false);
	}

	public void forward(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.forward();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToPreviousWord(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToPreviousWord();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToNextWord(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToNextWord();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToLineStart(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToLineStart();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToLineEnd(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToLineEnd();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToVeryBeginning(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToVeryBeginning();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToVeryEnd(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToVeryEnd();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void pageUp(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.pageUp();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void pageDown(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.pageDown();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void lineUp(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.lineUp();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void lineDown(boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.lineDown();
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToParagraph(int index,boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToParagraph(index);
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void goToPosition(int pos) {
		goToPosition(pos,false);
	}

	public void goToPosition(int pos,boolean shiftDown) {
		int savePos = getCurrentCursorPos();
		int res = _controller.goToPosition(pos);
		finishCursorMovement(res,savePos,shiftDown);
	}

	public void moveCursorToPos(int x,int y,boolean isShiftDown,boolean newSel,
			boolean rightMouse) {
		int savePos = getCurrentCursorPos();
		ContentSection saveSec = getCurrentSection();
		boolean wasInSelMode = _selection.isInSelectionMode();
		boolean imageResize = false;
		
		// move the cursor
		_controller.moveCursorToPos(x,y);
		
		// if a simply click has been performed we look which section we have hit
		if(!isShiftDown && !newSel && !rightMouse) {
			Line pointLine = getRootEnvironment().getEnvView().getLineAtPixelPos(y,true);
			int currentX = getCurrentEnvironment().getEnvView().getGlobalTextStart();
			if(pointLine != null) {
				MutableInt dummy = new MutableInt(0);
				ContentSection pointSec = pointLine.getLineView().getSectionAtPixelPos(x,y,currentX,dummy,true);
				if(pointSec != null) {
					// have we hit an image-section?
					if(pointSec instanceof ImageSection) {
						// so resize the image
						ImageSection iSec = (ImageSection)pointSec;
						boolean changed = iSec.setImageSize(!iSec.isMaximized());
						
						if(changed) {
							// we have to perform wordwrap if we have a pixel-based wordwrap
							IWordWrap ww = iSec.getParentEnvironment().getWordWrapStrategie();
							if(ww instanceof WordWrapPixelBased)
								iSec.getSectionParagraph().performWordWrap();
							
							imageResize = true;
							iSec.getSectionLine().getView().forceRefresh(ILineView.LINE_HEIGHT);
							getPaintPosManager().markAllDirty();
							getCursorManager().forceCursorChange();
						}
					}
					// watch for hyperlinks
					else if(pointSec instanceof TextSection) {
						TextSection tSec = (TextSection)pointSec;
						TextAttributes attributes = tSec.getAttributes();
						String linkURL = (String)attributes.get(TextAttributes.URL);
						String emailURL = (String)attributes.get(TextAttributes.EMAIL);
						if(linkURL != null)
							invokeHyperLinkClickedListeners(false,linkURL);
						else if(emailURL != null)
							invokeHyperLinkClickedListeners(true,emailURL);
					}
				}
			}
		}
		
		int currentPos = getCurrentCursorPos();
		
		// clear selection?
		if(wasInSelMode && (!isShiftDown || newSel) && !rightMouse)
			_selection.clearSelection();
		
		// change the selection or clear it
		if(isShiftDown && !rightMouse) {
			int direction = (savePos > currentPos) ? Selection.DIR_LEFT : Selection.DIR_RIGHT;			
			if(newSel)
				_selection.changeSelection(isShiftDown,currentPos,currentPos,direction);
			else
				_selection.changeSelection(isShiftDown,savePos,currentPos,direction);
		}
		// clear the selection only if the right-mouse-button has not been pressed
		else if(!rightMouse)
			_selection.clearSelection();
		
		// if we are still in selection-mode or are still not we can use fast-paint
		if(!rightMouse && wasInSelMode == isShiftDown && !imageResize) {
			// but only if we are in the same line
			if(saveSec.getSectionLine() != getCurrentLine())
				getRepaintManager().markCompletlyDirty();
		}
		else
			getRepaintManager().markCompletlyDirty();

		// show the popup-menu if the right-mouse-button has been pressed
		if(rightMouse && getPopupMenu() != null) {
			// repaint to set the cursor
			finish();
			
			getPopupMenu().show(this,x,y);
		}
		else
			finish();
	}

	// -----------------------------
	// --------- SELECTION ---------
	// -----------------------------
	
	public void clearSelection() {
		if(_selection.isInSelectionMode()) {
			_selection.clearSelection();
			getRepaintManager().markCompletlyDirty();
			finish();
		}
	}
	
	public void selectWordAtCursor() {
		TextPart word = getWordAtCursor();
		if(word != null) {
			_selection.clearSelection();
			
			ContentSection old = getCurrentSection();
			int globalStart = old.getParentEnvironment().getGlobalStartPos();
			
			// calculate the beginning- and end-section of the selection
			// the line can not change, so we use the line
			Line current = old.getSectionLine();
			try {
				int pStart = current.getParagraph().getElementStartPos();
				ContentSection begin = current.getSectionAt(word.startPos - globalStart - pStart);
				ContentSection end = current.getSectionAt(word.endPos - globalStart - pStart);
				// change the selection
				if(begin != null && end != null)
					_selection.changeSelection(true,word.startPos,word.endPos,Selection.DIR_RIGHT);
				
				// move the cursor to the end of the word
				getCurrentEnvironment().enterCursor(end,word.endPos - globalStart,word.endPos - globalStart);
				
				getRepaintManager().addDirtyLine(begin.getSectionLine(),false);
				finish();
			}
			catch(InvalidTextPositionException e) {
				
			}
		}
	}
	
	public void selectCurrentLine() {
		_selection.clearSelection();
		
		ContentSection old = getCurrentSection();
		Line line = old.getSectionLine();
		
		// calculate the beginning- and end-section of the line
		int globalStart = old.getParentEnvironment().getGlobalStartPos();
		ContentSection first = (ContentSection)line.getFirstSection();
		ContentSection last = (ContentSection)line.getLastSection();
		
		// change the selection
		_selection.changeSelection(true,first.getStartPosInEnv() + globalStart,
				last.getEndPosInEnv() + 1 + globalStart,Selection.DIR_RIGHT);
		
		// move the cursor to the end of the line
		getCurrentEnvironment().enterCursor(last,last.getEndPosInEnv() + 1,last.getEndPosInEnv() + 1);

		getRepaintManager().addDirtyLine(line,false);
		finish();
	}

	public void selectCompleteText() {
		// go to the end of the root environment
		_controller.goToVeryEnd();
		_selection.selectAll(_controller.getRootEnvironment());
		
		getRepaintManager().markCompletlyDirty();
		finish();
	}
	
	public void selectText(int start,int end) {
		if(start >= 0 && end >= 0 && end > start) {
			_selection.clearSelection();
			
			// calculate the beginning- and end-section of the selection
			Environment rootEnv = _controller.getRootEnvironment();
			ContentSection beginSec = rootEnv.getSectionAt(start);
			ContentSection endSec = rootEnv.getSectionAt(end);
			if(beginSec != null && endSec != null) {
				_controller.goToPosition(end);
				_selection.changeSelection(true,start,end,Selection.DIR_RIGHT);
			}
			
			getRepaintManager().markCompletlyDirty();
			finish();
		}

		throw new InvalidParameterException("Invalid start- or end-position: " + start + "-" + end);
	}

	// -----------------------------
	// --------- ATTRIBUTES --------
	// -----------------------------
	
	public boolean isAttributeEnabled(Integer attribute) {
		return _controller.isAttributeEnabled(attribute);
	}
	
	public void cleanAttributes(TextAttributes attributes) {
		_controller.cleanAttributes(attributes);
	}

	public TextAttributes getAttributes() {
		return _controller.getAttributes();
	}

	public TextAttributes getAttributesAtCursor() {
		return _controller.getAttributesAtCursor();
	}
	
	public TextAttributes getAttributes(int start,int end) {
		return _controller.getAttributes(start,end);
	}
	
	public void toggleAttribute(Integer attribute) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		int[] positions = getPositionsForActions();
		if(positions[0] >= 0 && positions[1] >= 0)
			toggleAttribute(positions[0],positions[1],attribute);
		// apply the attribute to the temporary table
		else if(getCurrentEnvironment().containsStyles())
			toggleTemporaryAttribute(attribute);
	}

	public void toggleAttribute(int start,int end,Integer attribute) {
		if(isReadOnly())
			return;

		int res = _controller.toggleAttribute(start,end,attribute);
		finishDefault(res);
	}

	public void toggleTemporaryAttribute(Integer attribute) {
		if(isReadOnly())
			return;
		
		int res = _controller.toggleTemporaryAttribute(attribute);
		finishDefault(res);
	}
	
	public void setTemporaryAttribute(Integer attribute,Object value) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		TextAttributes attributes = new TextAttributes();
		attributes.set(attribute,value);
		setTemporaryAttributes(attributes);
	}

	public void setTemporaryAttributes(TextAttributes attributes) {
		if(isReadOnly())
			return;
		
		int res = _controller.setTemporaryAttributes(attributes);
		finishDefault(res);
	}
	
	public void removeLinks() {
		TextAttributes attributeList = new TextAttributes();
		attributeList.remove(TextAttributes.URL);
		attributeList.remove(TextAttributes.EMAIL);
		
		int[] positions = getPositionsForActions();
		if(positions[0] >= 0 && positions[1] >= 0)
			setAttributes(positions[0],positions[1],attributeList);
		// apply the attribute to the temporary table
		else if(getCurrentEnvironment().containsStyles())
			setTemporaryAttributes(attributeList);
	}
	
	public void removeLinks(int start,int end) {
		TextAttributes attributeList = new TextAttributes();
		attributeList.remove(TextAttributes.URL);
		attributeList.remove(TextAttributes.EMAIL);
		
		setAttributes(start,end,attributeList);
	}
	
	public void removeAttributes() {
		int[] positions = getPositionsForActions();
		if(positions[0] >= 0 && positions[1] >= 0)
			removeAttributes(positions[0],positions[1],TextAttributes.getAll());
		else if(getCurrentEnvironment().containsStyles()) {
			TextAttributes attributeList = new TextAttributes();
			attributeList.ensureSet(TextAttributes.getAll());
			setTemporaryAttributes(attributeList);
		}
	}
	
	public void removeAttributes(int start,int end) {
		removeAttributes(start,end,TextAttributes.getAll());
	}
	
	public void removeAttributes(int start,int end,List attributes) {
		if(attributes == null)
			throw new InvalidParameterException("attributes is null");
		
		// generate the attributes-list with null-values (to disable them)
		TextAttributes attributeList = new TextAttributes();
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			attributeList.remove((Integer)o);
		}
		
		setAttributes(start,end,attributeList);
	}

	public void setAttribute(Integer attribute,Object value) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		int[] positions = getPositionsForActions();
		if(positions[0] >= 0 && positions[1] >= 0)
			setAttribute(positions[0],positions[1],attribute,value);
		// apply the attribute to the temporary table
		else if(getCurrentEnvironment().containsStyles())
			setTemporaryAttribute(attribute,value);
	}
	
	public void setAttribute(int start,int end,Integer attribute,Object value) {
		if(attribute == null)
			throw new InvalidParameterException("attribute is null");
		
		TextAttributes attributes = new TextAttributes();
		attributes.set(attribute,value);
		
		setAttributes(start,end,attributes);
	}

	public void setAttributes(int start,int end,TextAttributes attributes) {
		if(isReadOnly())
			return;

		int res = _controller.setAttributes(start,end,attributes);
		finishDefault(res);
	}
	
	public void setLineAlignment(int align) {
		if(isReadOnly())
			return;
		
		int res = _controller.setLineAlignment(align);
		finishDefault(res);
	}
	
	public void setLineAlignment(int start,int end,int align) {
		if(isReadOnly())
			return;

		int res = _controller.setLineAlignment(start,end,align);
		finishDefault(res);
	}

	// -----------------------------
	// --------- GET TEXT ----------
	// -----------------------------
	
	public String getText() {
		return _controller.getText();
	}
	
	public String getText(int start,int end) {
		return _controller.getText(start,end);
	}
	
	public String getText(int syntax) {
		return _controller.getText(syntax);
	}
	
	public String getText(int start,int end,int syntax) {
		return _controller.getText(start,end,syntax);
	}
	
	public String getSelectedText(int syntax) {
		if(!_selection.isEmpty()) {
			int start = _selection.getSelectionStart();
			int end = _selection.getSelectionEnd();
			return _controller.getText(start,end,syntax);
		}
		
		return "";
	}
	
	public void cutSelectedText() {
		if(_selection.isInSelectionMode()) {
			copySelectedText();
			removeSelectedText();
			finish();
		}
	}
	
	public void copySelectedText() {
		Selection sel = getSelection();
		if(!sel.isEmpty()) {
			IExportContent type;
			if(_editorMode == IPublicController.MODE_HTML)
				type = new HTMLExportContent(this);
			else if(_editorMode == IPublicController.MODE_TEXT_EDITOR)
				type = new PlainExportContent();
			else
				type = new BBCodeExportContent();
			
			Exporter ex = new Exporter(this,type);
			String res = ex.getContent(sel.getSelectionStart(),sel.getSelectionEnd());
			
			StringSelection selString = new StringSelection(res);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selString,selString);
		}
	}

	// -----------------------------
	// -------- REMOVE TEXT --------
	// -----------------------------
	
	public void removePreviousChar() {
		if(isReadOnly())
			return;
		
		// delete the selection if in selection-mode
		if(_selection.isInSelectionMode()) {
			removeSelectedText();
			finish();
			return;
		}

		int res = _controller.removePreviousChar(true);
		finishDefault(res);
	}

	public void removeFollowingChar() {
		if(isReadOnly())
			return;
		
		// delete the selection if in selection-mode
		if(_selection.isInSelectionMode()) {
			removeSelectedText();
			finish();
			return;
		}

		int res = _controller.removeFollowingChar(true);
		finishDefault(res);
	}

	public void removePreviousWord() {
		if(isReadOnly())
			return;
		
		// delete the selection if in selection-mode
		if(_selection.isInSelectionMode()) {
			removeSelectedText();
			finish();
			return;
		}
		
		int res = _controller.removePreviousWord(true);
		finishDefault(res);
	}

	public void removeNextWord() {
		if(isReadOnly())
			return;
		
		// delete the selection if in selection-mode
		if(_selection.isInSelectionMode()) {
			removeSelectedText();
			finish();
			return;
		}

		int res = _controller.removeNextWord(true);
		finishDefault(res);
	}

	public void removeText(int start,int end) {
		if(isReadOnly())
			return;

		int res = _controller.removeText(start,end,true);
		finishDefault(res);
	}

	
	// -----------------------------
	// ------ PRIVATE METHODS ------
	// -----------------------------
	
	/**
	 * Ensures that the given position is not in the current selection. If it is in it the selection
	 * start will be used. This is usefull if you want to remove the selected text and want to
	 * perform an action at a specified position afterwards. So you can ensure that the position
	 * exists after the removement of the selection.
	 * 
	 * @param pos your position
	 * @return the position you should use
	 */
	private int ensureNotInSelection(int pos) {
		if(pos > _selection.getSelectionStart())
			return _selection.getSelectionStart();
		
		return pos;
	}
	
	/**
	 * Delete the selected text. This action is NOT finished!
	 */
	private void removeSelectedText() {
		if(!_selection.isEmpty())
			_controller.removeText(_selection.getSelectionStart(),_selection.getSelectionEnd(),true);
		
		// finally clear the selection
		_selection.clearSelection();
	}
	
	/**
	 * The default finish. Checks wether there has been done something and
	 * if so calls finish().
	 * 
	 * @param res the result
	 */
	private void finishDefault(int res) {
		if(res == Controller.RES_NOTHING_DONE)
			return;
		
		finish();
	}

	/**
	 * Performs all actions that need to be done after every cursor-movement
	 * 
	 * @param res the result
	 * @param shiftDown change selection?
	 */
	private void finishCursorMovement(int res,int savePos,boolean shiftDown) {
		// just clear selection?
		if(res == Controller.RES_NOTHING_DONE && !shiftDown && _selection.isInSelectionMode()) {
			_selection.clearSelection();
			getRepaintManager().markCompletlyDirty();
			finish();
			return;
		}
	
		// don't repaint fast if we have to clear the selection
		if(_selection.isInSelectionMode() && !shiftDown)
			getRepaintManager().markCompletlyDirty();
		
		// change selection
		int cursorPos = getCurrentCursorPos();
		int direction = savePos < cursorPos ? Selection.DIR_RIGHT : Selection.DIR_LEFT;
		_selection.changeSelection(shiftDown,savePos,cursorPos,direction);
		
		finish();
	}
	
	void finish() {
		// we have to refresh the paint-texts, string-bounds, etc. _before_ we perform
		// wordwraps
		getViewManager().refresh();
		getWordwrapManager().refresh();
		
		// refresh the view-stuff again, because wordwrap may have changed something
		getViewManager().refresh();
		
		getPaintPosManager().refresh();
		getAttributesManager().checkChanged();
		getCursorManager().checkChange();
		getRepaintManager().repaint();
		_controller.getHistoryManager().addToHistory();
	}
}