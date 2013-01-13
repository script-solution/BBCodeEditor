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
import java.util.List;
import java.util.Map;

import bbcodeeditor.control.highlighter.HighlightSyntax;
import bbcodeeditor.control.tools.TextPart;

/**
 * the public interface for the text-field<br>
 * Contains all methods to get infos from the text-field and manipulate the text-field
 * <br>
 * There are, for now, two implementations:
 * <ul>
 * 	<li><code>BBCTextField</code></li>
 * 	<li><code>SBBCTextField</code></li>
 * </ul>
 * Where <code>SBBCTextField</code> is synchronized.
 * 
 * @author hrniels
 */
public interface IPublicController {
	
	/**
	 * the default syntax for exports
	 */
	static final int SYNTAX_DEFAULT					= -1;
	
	/**
	 * the BBCode-syntax for exports
	 */
	static final int SYNTAX_BBCODE					= 0;
	
	/**
	 * the HTML-syntax for exports
	 */
	static final int SYNTAX_HTML						= 1;
	
	/**
	 * the plain-syntax for exports
	 */
	static final int SYNTAX_PLAIN						= 2;
	
	/**
	 * A plain-text (mimetype: text/plain) which will be interpreted as HTML
	 */
	static final int SYNTAX_PLAIN_HTML			= 3;
	
	
	/**
	 * indicates that no wrap should be performed
	 */
	static final int WORD_WRAP_NO_WRAP			= -1;
	
	/**
	 * indicates that the wordwrap should be done char-based.
	 */
	static final int WORD_WRAP_CHAR_BASED		= 0;
	
	/**
	 * indicates that the wordwrap should be done word-based.
	 */
	static final int WORD_WRAP_WORD_BASED		= 1;

	/**
	 * indicates that the wordwrap should be done pixel and word-based.
	 */
	static final int WORD_WRAP_PIXEL_BASED	= 2;
	
	
	/**
	 * Indicates that the editor should be used for editing bbcode
	 */
	static final int MODE_BBCODE						= 0;
	
	/**
	 * Indicates that the editor should be used as a text-editor
	 */
	static final int MODE_TEXT_EDITOR				= 1;
	
	/**
	 * Indicates that the editor should be used as a html-editor
	 */
	static final int MODE_HTML							= 2;


	/**
	 * Returns the current mode of the editor. That may be either MODE_BBCODE, MODE_HTML or
	 * MODE_TEXT_EDITOR.
	 * 
	 * @return the mode
	 */
	int getEditorMode();
	
	/**
	 * Sets the mode of the editor. That may be either MODE_BBCODE, MODE_HTML or
	 * MODE_TEXT_EDITOR.
	 * 
	 * @param mode the mode to use
	 */
	void setEditorMode(int mode);
	
	/**
	 * Checks wether the given tag is enabled
	 * 
	 * @param tag the tag to check. see BBCodeTags.*
	 * @return true if the tag is enabled
	 * @see bbcodeeditor.control.export.bbcode.BBCodeTags
	 */
	boolean isTagEnabled(int tag);
	
	/**
	 * checks wether the given attributes is enabled
	 * 
	 * @param attribute the attribute to check (see Attributes.*)
	 * @return true if the attribute is enabled
	 * @see bbcodeeditor.control.ParagraphAttributes
	 */
	boolean isAttributeEnabled(Integer attribute);
	
	/**
	 * Cleans the given map. Checks wether the attributes that are set are
	 * enabled.
	 * 
	 * @param attributes the attributes you have
	 */
	void cleanAttributes(TextAttributes attributes);
	
	/**
	 * Enables the given tag. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tag the tag to enable. see BBCodeTags.*
	 * @see bbcodeeditor.control.export.bbcode.BBCodeTags
	 */
	void enableTag(int tag);

	/**
	 * Disables the given tag. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tag the tag to disable. see BBCodeTags.*
	 * @see bbcodeeditor.control.export.bbcode.BBCodeTags
	 */
	void disableTag(int tag);
	
	/**
	 * Enables all given tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tags a List with all tags to enable. see BBCodeTags.*
	 * @see bbcodeeditor.control.export.bbcode.BBCodeTags
	 */
	void enableTags(List tags);
	
	/**
	 * Disables all given tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 * 
	 * @param tags a List with all tags to disable. see BBCodeTags.*
	 * @see bbcodeeditor.control.export.bbcode.BBCodeTags
	 */
	void disableTags(List tags);
	
	/**
	 * Enables all tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 */
	void enableAllTags();
	
	/**
	 * Disables all tags. If this causes a change the textfield refreshes
	 * the content with the current state. That means that the history will be lost!
	 */
	void disableAllTags();
	
	/**
	 * Refreshes all fonts
	 */
	void refreshFonts();
	
	/**
	 * Refreshes the content of the textfield
	 */
	void refreshContent();

	/**
	 * Refreshes the wordwrap in all paragraphs
	 */
	void refreshWordWrap();
	
	/**
	 * @return the SmileyContainer with the smileys
	 */
	SmileyContainer getSmileys();
	
	/**
	 * sets the smileys for this textField
	 * 
	 * @param con the SmileyContainer
	 */
	void setSmileys(SmileyContainer con);
	
	/**
	 * @return wether the control will replace smileys at the moment
	 */
	boolean replaceSmileys();
	
	/**
	 * sets wether the control should replace smileys
	 * 
	 * @param replaceSmileys replace smileys?
	 */
	void setReplaceSmileys(boolean replaceSmileys);

	/**
	 * this is the root-environment in the control. The root-environment does always exist
	 * and contains the text which is not in a quote, code or list.
	 * 
	 * @return the root environment
	 */
	Environment getRootEnvironment();

	/**
	 * Returns the environment in which the cursor currently is. This may be the root-
	 * environment or any sub-environment.
	 * 
	 * @return the current environment
	 */
	Environment getCurrentEnvironment();

	/**
	 * Returns the global position of the cursor. That means the position with the view
	 * of the root-environment.
	 * 
	 * @return the current (global) position of the cursor
	 */
	int getCurrentCursorPos();

	/**
	 * Returns the ContentSection in which the cursor currently is. This may NOT be an
	 * environment but any Section which directly contains content.
	 * 
	 * @return the current section of the cursor
	 */
	ContentSection getCurrentSection();
	
	/**
	 * Returns the paragraph in which the cursor is currently in.
	 * 
	 * @return the paragraph at the cursor
	 */
	Paragraph getCurrentParagraph();

	/**
	 * determines the section at the given pixel-position
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the ContentSection at this position
	 */
	ContentSection getSectionAtPixelPos(int x,int y);
	
	/**
	 * Returns the Line-object in which the cursor is. This may be a line of a sub-
	 * environment.
	 * 
	 * @return the current Line of the cursor
	 */
	Line getCurrentLine();

	/**
	 * The Selection-object which contains the selection-start-position, end-position
	 * and the direction of the selection.<br>
	 * You can't manipulate anything with this object. If you want to change the selection
	 * you can do this directly with this class.
	 * 
	 * @return the selection
	 */
	Selection getSelection();
	
	/**
	 * determines the positions to perform an action.<br>
	 * This affects manipulating the attributes in most cases.<br>
	 * The method returns an array with 2 elements. The first one will be the start-position
	 * and the second one, you might guess it ;), the end-position.<br>
	 * The result may be {-1,-1} if the action will affect the temporary attributes. If
	 * not if affects either the selection or the word at the cursor.
	 * 
	 * @return the positions
	 */
	int[] getPositionsForActions();

	/**
	 * The history-object which contains the actions which have been done or can be done
	 * again. But you can't manipulate anything with this object. Please use this class
	 * do undo / redo an action!
	 * 
	 * @return the history (undo/redo)
	 */
	History getHistory();
	
	/**
	 * This will force the history to cache all actions that will be added
	 * until {@link #stopHistoryCache()} will be called.
	 * <p>
	 * This gives you the opportunity to "group" actions. If you want to perform
	 * multiple actions that should be undone in one step you can simply call
	 * this method before all actions and {@link #stopHistoryCache()} if you
	 * are finished.
	 * 
	 * @see #stopHistoryCache()
	 */
	void startHistoryCache();
	
	/**
	 * This will stop the history caching actions and all cached actions will
	 * be added to the history.
	 * 
	 * @see #startHistoryCache()
	 */
	void stopHistoryCache();
	
	/**
	 * determines the value for the given property and the given environment-type
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return the value of the property or null if an error occurred
	 */
	Object getEnvProperty(Integer property,int envType);
	
	/**
	 * sets the value for the given property and the given environment-type
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @param value the new value
	 * @return true if the value has been changed
	 */
	boolean setEnvProperty(Integer property,int envType,Object value);
	
	/**
	 * sets all properties in the map for the given environment type
	 * 
	 * @param properties the properties to set
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return true if something has changed
	 */
	boolean setEnvProperties(Map properties,int envType);
	
	/**
	 * Sets the highlighting in the given environment to the given syntax
	 * 
	 * @param env the environment
	 * @param highlightSyntax the new syntax
	 * @see HighlightSyntax
	 */
	void setHighlightSyntax(CodeEnvironment env,Object highlightSyntax);
	
	/**
	 * @return the number of spaces for a tab
	 */
	int getTabWidth();
	
	/**
	 * sets the tab-width to given value
	 * 
	 * @param width the number of spaces for a tab
	 */
	void setTabWidth(int width);
	
	/**
	 * undo's the last action if there is any
	 */
	void undo();
	
	/**
	 * redo's the last action if there is any
	 */
	void redo();

	/**
	 * inserts an email / url with given title and address at the current cursor-position
	 * 
	 * @param isEmail email or URL
	 * @param title the title or the link
	 * @param address the address or the link
	 */
	void insertLink(boolean isEmail,String title,String address);

	/**
	 * edits the link in the selection or at the cursor-position
	 * and sets it to the given address
	 * 
	 * @param isEmail email or URL
	 * @param newAddress the new address or the link
	 */
	void editLink(boolean isEmail,String newAddress);
	
	/**
	 * Edits the image-url of the current section.
	 * Note that this has to be an image-section and not a smiley-section or
	 * something else!
	 * 
	 * @param newURL the new image-url
	 */
	void editImageURL(String newURL);
	
	/**
	 * sets the list-type of the current environment<br>
	 * Note that the current environment has to be a list-environment!
	 * 
	 * @param type the new type
	 */
	void setListType(int type);

	/**
	 * edits the author of the current quote-environment.<br>
	 * Note that the current environment has to be a quote-environment!
	 * 
	 * @param author the new value
	 */
	void editAuthor(String author);

	/**
	 * adds a quote-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 */
	void addQuoteEnvironment(boolean isListPoint);

	/**
	 * adds a quote-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param author the author of the quote
	 */
	void addQuoteEnvironment(boolean isListPoint,String author);

	/**
	 * adds a code-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 */
	void addCodeEnvironment(boolean isListPoint);

	/**
	 * adds a code-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param hlSyntax the highlight-syntax to use
	 * @see HighlightSyntax
	 */
	void addCodeEnvironment(boolean isListPoint,Object hlSyntax);

	/**
	 * adds a list-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 */
	void addListEnvironment(boolean isListPoint);

	/**
	 * adds a list-environment to the current environment at the current cursor-position
	 * the cursor will be moved into the environment!
	 * 
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 * @param listType the type for the list. see ListTypes.TYPE_*
	 */
	void addListEnvironment(boolean isListPoint,int listType);

	/**
	 * Adds the given environment to the given position
	 * the cursor will be moved into the environment!
	 * 
	 * @param newEnv the environment to insert
	 * @param pos the position
	 * @param isListPoint should the environment be a new list-point (if in a list-environment)?
	 */
	void addEnvironmentAt(Environment newEnv,int pos,boolean isListPoint);

	/**
	 * adds the smiley with given image at the current cursor-position
	 * 
	 * @param smiley the smiley to add
	 */
	void addSmiley(SecSmiley smiley);

	/**
	 * adds the smiley with given image at the current cursor-position
	 * 
	 * @param image the image you want to add
	 */
	void addImage(SecImage image);

	/**
	 * adds the smiley with given image at the given position
	 * 
	 * @param image the image you want to add
	 * @param pos the position
	 */
	void addImage(SecImage image,int pos);

	/**
	 * clears everything, so that the control is empty
	 */
	void clear();

	/**
	 * determines the word at the cursor.<br>
	 * If the cursor is in front of or in a word this word will be returned.
	 * If the cursor is behind a word no word will be found.
	 * <p>
	 * So for example:
	 * <ul>
	 * 	<li>"|abc"			=> "abc"</li>
	 * 	<li>"a|bc"			=> "abc"</li>
	 * 	<li>"abc|"			=> null</li>
	 * 	<li>"abc |def"	=> "def"</li>
	 * 	<li>"abc| def"	=> null</li>
	 * </ul>
	 * <p>
	 * Note that this will also be used for selecting the word at the cursor-position,
	 * set/toggle an attribute if no text is selected and so on.
	 * 
	 * @return the word at the cursor-position
	 */
	TextPart getWordAtCursor();

	/**
	 * clears the selection
	 */
	void clearSelection();

	/**
	 * selects the word at the cursor
	 * 
	 * @see #getWordAtCursor()
	 */
	void selectWordAtCursor();

	/**
	 * selects the current line
	 */
	void selectCurrentLine();

	/**
	 * select the complete text
	 */
	void selectCompleteText();

	/**
	 * selects the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 */
	void selectText(int start,int end);

	/**
	 * moves the cursor to the beginning of the previous word
	 * If you want to change the selection, too, you have to enable <code>shiftDown</code>.
	 * <p>
	 * Some examples:
	 * <ul>
	 * 	<li>"abc def|"		=> "abc |def"</li>
	 * 	<li>"abc def |"		=> "abc |def"</li>
	 * 	<li>"abc d|ef"		=> "abc |def"</li>
	 * 	<li>"abc |def"		=> "|abc def"</li>
	 * 	<li>"abc| def"		=> "|abc def"</li>
	 * </ul>
	 * 
	 * @param shiftDown should the selection be changed?
	 */
	void goToPreviousWord(boolean shiftDown);

	/**
	 * moves the cursor to the beginning of the next word
	 * If you want to change the selection, too, you have to enable <code>shiftDown</code>.
	 * <p>
	 * Some examples:
	 * <ul>
	 * 	<li>"abc| def"		=> "abc |def"</li>
	 * 	<li>"ab|c def"		=> "abc |def"</li>
	 * 	<li>"|abc def"		=> "abc |def"</li>
	 * 	<li>"| abc def"		=> "|abc def"</li>
	 * 	<li>"abc |def"		=> "abc def|"</li>
	 * </ul>
	 * 
	 * @param shiftDown should the selection be changed?
	 */
	void goToNextWord(boolean shiftDown);

	/**
	 * toggles the given attribute in the temporary attributes
	 * <p>
	 * The temporary attributes are used if no text is selected, and no word
	 * is at the cursor. In this case the attributes to set/toggle/remove will be passed
	 * to the temporary attributes. These will be applied to the next character the user
	 * types.<br>
	 * But if the user moves the cursor or does something else before the temporary attributes
	 * will be cleared.
	 * 
	 * @param attribute the attribute to toggle
	 */
	void toggleTemporaryAttribute(Integer attribute);

	/**
	 * sets the temporary attibute<br>
	 * If the value is null the attribute will be removed!
	 * 
	 * 
	 * @param attribute the attribute to set
	 * @param value the new value of the attribute
	 * @see #toggleTemporaryAttribute(Integer)
	 */
	void setTemporaryAttribute(Integer attribute,Object value);

	/**
	 * replaces the temporary attributes with the given ones.<br>
	 * All null-values will remove the attribute!
	 * 
	 * @param attributes the attributes to add / remove
	 * @see #toggleTemporaryAttribute(Integer)
	 */
	void setTemporaryAttributes(TextAttributes attributes);

	/**
	 * toggles the given attribute
	 * <p>
	 * If a selection exists, the attribute will be applied to the selection
	 * If there is a word at the cursor-position this will get the attribute.
	 * Otherwise the next character, which the user types, will get the attribute
	 * 
	 * @param attribute the attribute you want to set
	 * @see #toggleTemporaryAttribute(Integer)
	 * @see #getWordAtCursor()
	 */
	void toggleAttribute(Integer attribute);

	/**
	 * toggles the given attribute in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attribute the attribute you want to set
	 */
	void toggleAttribute(int start,int end,Integer attribute);

	/**
	 * removes all links in the selection, in the word at cursor or in the temporary
	 * attributes
	 * 
	 * @see #toggleTemporaryAttribute(Integer)
	 * @see #getWordAtCursor()
	 */
	void removeLinks();

	/**
	 * removes all links in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 */
	void removeLinks(int start,int end);

	/**
	 * removes all attributes in the selection, in the word at cursor or in the temporary
	 * attributes
	 * 
	 * @see #toggleTemporaryAttribute(Integer)
	 * @see #getWordAtCursor()
	 */
	void removeAttributes();

	/**
	 * removes all attributes in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 */
	void removeAttributes(int start,int end);

	/**
	 * removes the given attributes in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attributes a List with all attributes to disable
	 */
	void removeAttributes(int start,int end,List attributes);

	/**
	 * sets the given attribute to given value<br>
	 * This will be applied to the selection or the word at the cursor
	 * or the temporary attributes
	 * 
	 * @param attribute the attribute you want to set
	 * @param value the value of the attribute
	 * @see #toggleTemporaryAttribute(Integer)
	 * @see #getWordAtCursor()
	 */
	void setAttribute(Integer attribute,Object value);

	/**
	 * sets the given attribute of the given interval to given value
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attribute the attribute you want to set
	 * @param value the value of the attribute
	 */
	void setAttribute(int start,int end,Integer attribute,Object value);

	/**
	 * sets the given attribute of the given interval to given value
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attributes a Map with the attributes to set
	 */
	void setAttributes(int start,int end,TextAttributes attributes);
	
	/**
	 * sets the alignment of the current line to given value
	 * 
	 * @param align the new alignment
	 */
	void setLineAlignment(int align);
	
	/**
	 * sets the alignment of the lines in the given interval to given value
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param align the new alignment
	 */
	void setLineAlignment(int start,int end,int align);

	/**
	 * moves the cursor to given X/Y position. (to the corresponding character at this position)
	 * <p>
	 * Lines will be entered as soon as the top of the cursor hits the line<br>
	 * If the position is right of the middle of a character, the cursor moves behind the
	 * character. Otherwise in front of it.
	 * <p>
	 * You can also change the selection or display the popup-menu
	 * 
	 * @param x the x-position of the cursor
	 * @param y the y-position of the cursor
	 * @param isShiftDown is the shift-key pressed?
	 * @param newSel do you want to start a new selection?
	 * @param rightMouse is the right-mouse-button pressed? (show popup-menu)
	 */
	void moveCursorToPos(int x,int y,boolean isShiftDown,boolean newSel,
			boolean rightMouse);
	
	/**
	 * moves the cursor to the given position. The position will be treaten as "global".
	 * 
	 * @param pos the position
	 */
	void goToPosition(int pos);
	
	/**
	 * moves the cursor to the given position. The position will be treaten as "global".
	 * 
	 * @param pos the position
	 * @param shiftDown change selection?
	 */
	void goToPosition(int pos,boolean shiftDown);
	
	/**
	 * Adds a tab in front of all paragraphs in the current selection.
	 * If the selection is empty a tab will be inserted at the cursor-position.
	 * The selection will be adjusted after the action!
	 * 
	 * @see #unindentParagraphs()
	 */
	void indentParagraphs();
	
	/**
	 * Adds a tab in front of all paragraphs in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @see #unindentParagraphs(int, int)
	 */
	void indentParagraphs(int start,int end);
	
	/**
	 * Removes a tab in front of all paragraphs in the current selection.
	 * If the selection is empty the current paragraph will be unindented.
	 * The selection will be adjusted after the action!
	 * 
	 * @see #indentParagraphs()
	 */
	void unindentParagraphs();

	/**
	 * Removes a tab in front of all paragraphs in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @see #indentParagraphs(int, int)
	 */
	void unindentParagraphs(int start,int end);
	
	/**
	 * highlights all given regions.
	 * 
	 * @param regions a List which contains TextPart-objects. The text-field will be ignored!
	 * @param highlight the color to use for the highlighting
	 * @see #clearHighlighting()
	 */
	void highlightRegions(List regions,Color highlight);
	
	/**
	 * removes all highlighting attributes
	 * 
	 * @see #highlightRegions(List, Color)
	 */
	void clearHighlighting();
	
	/**
	 * Replaces the first occurrence of <code>text</code> with <code>repl</code> between
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
	 * @return the start-position of the replace or -1 if nothing has been done
	 * @see #replaceLast(String, String, int, int, boolean, boolean)
	 * @see #replaceAll(String, String, int, int, boolean, boolean)
	 */
	int replaceFirst(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode);
	
	/**
	 * Replaces the last occurrence of <code>text</code> with <code>repl</code> between
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
	 * @return the start-position of the replace or -1 if nothing has been done
	 * @see #replaceFirst(String, String, int, int, boolean, boolean)
	 * @see #replaceAll(String, String, int, int, boolean, boolean)
	 */
	int replaceLast(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode);
	
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
	 * @see #replaceFirst(String, String, int, int, boolean, boolean)
	 * @see #replaceLast(String, String, int, int, boolean, boolean)
	 */
	int replaceAll(String text,String repl,int start,int end,boolean caseSensitive,
			boolean parseBBCode);
	
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
	 * @see #getLastOccurrence(String, int, int, boolean)
	 * @see #getAllOccurrences(String, int, int, boolean)
	 */
	TextPart getFirstOccurrence(String text,int start,int end,boolean caseSensitive);

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
	 * @see #getFirstOccurrence(String, int, int, boolean)
	 * @see #getAllOccurrences(String, int, int, boolean)
	 */
	TextPart getLastOccurrence(String text,int start,int end,boolean caseSensitive);
	
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
	 * @see #getFirstOccurrence(String, int, int, boolean)
	 * @see #getLastOccurrence(String, int, int, boolean)
	 */
	List getAllOccurrences(String text,int start,int end,boolean caseSensitive);

	/**
	 * exports the selected text of the control in the given syntax
	 * 
	 * @param syntax the syntax-type. see SYNTAX_*
	 * @return the selected text in the given syntax
	 */
	String getSelectedText(int syntax);
	
	/**
	 * determines the attributes by the following algorithm:
	 * <ul>
	 * 	<li>If the selection is not empty, the attributes of the selection will be used</li>
	 * 	<li>If the temporary attributes are set they will be used</li>
	 * 	<li>Otherwise the attributes of the current section will be used</li>
	 * </ul>
	 * 
	 * @return the attributes
	 * @see #toggleTemporaryAttribute(Integer)
	 */
	TextAttributes getAttributesAtCursor();
	
	/**
	 * determines the attributes of the selection, the word at the cursor or the
	 * temporary attributes. Will return a clone!
	 * 
	 * @return a Map with the attributes
	 * @see #toggleTemporaryAttribute(Integer)
	 * @see #getWordAtCursor()
	 */
	TextAttributes getAttributes();
	
	/**
	 * collects all attributes in the given interval.<br>
	 * Attributes which have in the whole interval the same value will
	 * have this value in the map. Attributes which change in the interval
	 * will be in the map but have a null-value.<br>
	 * Other attributes will not be in the map
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return a Map with the attributes
	 */
	TextAttributes getAttributes(int start,int end);
	
	/**
	 * returns the number of characters in the textField
	 * 
	 * @return the number of characters
	 */
	int length();

	/**
	 * exports the complete text in BBCode-syntax.
	 * 
	 * @return the text of the control in BBCode-syntax
	 */
	String getText();
	
	/**
	 * exports the text from the given start-position to the end-position
	 * in BBCode-syntax.
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the text in the given interval in BBCode-syntax
	 */
	String getText(int start,int end);
	
	/**
	 * exports the complete text of the control in the given syntax
	 * 
	 * @param syntax the syntax-type. see SYNTAX_*
	 * @return the text in the given syntax
	 */
	String getText(int syntax);
	
	/**
	 * exports the text from the given start-position to the end-position
	 * in the given syntax
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param syntax the syntax to use, see SYNTAX_*
	 * @return the text in the given interval in the given syntax
	 */
	String getText(int start,int end,int syntax);
	
	/**
	 * moves the cursor one step back
	 * <p>
	 * <ul>
	 * 	<li>If the cursor is at a line-beginning the cursor moves behind the previous line</li>
	 * 	<li>If the cursor is behind an environment the cursor moves at the end of the
	 * environment</li>
	 * 	<li>If the cursor is at the beginning of an environment the cursor moves out of the
	 * environment to the end of the previous line.
	 * 	<li>If the cursor is at the very beginning nothing happens
	 * </ul>
	 * Clears always the selection!
	 */
	void back();

	/**
	 * moves the cursor one step back.<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 * @see #back()
	 */
	void back(boolean shiftDown);

	/**
	 * moves the cursor one step forward
	 * <p>
	 * <ul>
	 * 	<li>If the cursor is at a line-end the cursor moves at the beginning of the next
	 * line</li>
	 * 	<li>If the cursor is in front of an environment the cursor moves at the beginning
	 * of the environment
	 * 	<li>If the cursor is at the end of an environment the cursor moves out of the
	 * environment to the beginning of the next line.
	 * 	<li>If the cursor is at the very end nothing happens
	 * </ul>
	 * Clears always the selection!
	 */
	void forward();

	/**
	 * moves the cursor one step forward.<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 * @see #forward()
	 */
	void forward(boolean shiftDown);
	
	/**
	 * Goes to the beginning of the paragraph at given index
	 * 
	 * @param index the index of the paragraph
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void goToParagraph(int index,boolean shiftDown);

	/**
	 * go to the end of the current line.<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void goToLineEnd(boolean shiftDown);

	/**
	 * go to the beginning of the current line<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void goToLineStart(boolean shiftDown);

	/**
	 * go to the very beginning of all<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void goToVeryBeginning(boolean shiftDown);

	/**
	 * go to the very end of all<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void goToVeryEnd(boolean shiftDown);

	/**
	 * adds a new-line at current position.<br>
	 * moves the cursor to the created line
	 */
	void addNewLine();

	/**
	 * adds a new-line at current position in a list-environment.<br>
	 * That means that no new list-point will be created but just a new line<br>
	 * moves the cursor to the created line
	 */
	void addNewLineInList();

	/**
	 * moves the visible area one line up, if possible<br>
	 * Will not move the cursor!
	 */
	void moveVisibleAreaLineUp();

	/**
	 * moves the visible area one line down, if possible<br>
	 * Will not move the cursor!
	 */
	void moveVisibleAreaLineDown();

	/**
	 * moves the cursor one page up<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift currently pressed?
	 */
	void pageUp(boolean shiftDown);

	/**
	 * moves the cursor one page down<br>
	 * You can decide if you want to change the selection or not
	 * 
	 * @param shiftDown is shift currently pressed?
	 */
	void pageDown(boolean shiftDown);

	/**
	 * go to the next line<br>
	 * You can decide if you want to change the selection or not<br>
	 * Will move into environments, too
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void lineDown(boolean shiftDown);

	/**
	 * go to the previous line<br>
	 * You can decide if you want to change the selection or not<br>
	 * Will move into environments, too
	 * 
	 * @param shiftDown is shift pressed? (change the selection)
	 */
	void lineUp(boolean shiftDown);

	/**
	 * copies the currently selected text to the clipboard and removes the text
	 */
	void cutSelectedText();

	/**
	 * copies the currently selected text to the clipboard
	 */
	void copySelectedText();

	/**
	 * pastes the current clipboard-content at the cursor-position
	 */
	void pasteTextAtCursor();

	/**
	 * pastes the current clipboard-content with given syntax at the cursor-position
	 * 
	 * @param syntax the syntax to use, see SYNTAX_*
	 */
	void pasteTextAtCursor(int syntax);

	/**
	 * pastes the given text at the current cursor-position
	 * 
	 * @param text the text to paste
	 * @param isBBCode treat as BBCode or plain-text?
	 */
	void pasteTextAtCursor(String text,boolean isBBCode);

	/**
	 * pastes the given text at the current cursor-position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes a Map with the attributes of the text to paste
	 */
	void pasteTextAtCursor(String text,TextAttributes attributes);

	/**
	 * pastes the given text at the current cursor-position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes a Map with the attributes of the text to paste
	 * @param align the alignment of the line. see Attributes.ALIGN_*
	 * @see bbcodeeditor.control.ParagraphAttributes
	 */
	void pasteTextAtCursor(String text,TextAttributes attributes,int align);

	/**
	 * Adds the given text with the given attributes at the given position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes a Map with the attributes of the text to paste
	 */
	void addTextAt(String text,int pos,TextAttributes attributes);

	/**
	 * Adds the given text with the given attributes at the given position
	 * 
	 * @param text the text to paste
	 * @param pos the position
	 * @param attributes a Map with the attributes of the text to paste
	 * @param align the alignment of the line. see Attributes.ALIGN_*
	 * @see bbcodeeditor.control.ParagraphAttributes
	 */
	void addTextAt(String text,int pos,TextAttributes attributes,int align);

	/**
	 * removes the text from <code>start</code> to <code>end</code>
	 *
	 * @param start the start-position
	 * @param end the end-position
	 */
	void removeText(int start,int end);

	/**
	 * removes the previous char if possible
	 */
	void removePreviousChar();

	/**
	 * removes the following character
	 */
	void removeFollowingChar();
	
	/**
	 * Deletes the previous word
	 * 
	 * @see #goToPreviousWord(boolean)
	 */
	void removePreviousWord();
	
	/**
	 * deletes the next word
	 * 
	 * @see #goToNextWord(boolean)
	 */
	void removeNextWord();
	
	/**
	 * sets the text of the BBCTextField to given value<br>
	 * it will not be interpreted as BBCode!
	 * 
	 * @param text the text to set
	 */
	void setPlainText(String text);
	
	/**
	 * sets the text of the textfield to the given value<br>
	 * the text can contain BBCodes which will be interpreted
	 * 
	 * @param text the new text of the control
	 */
	void setText(String text);
}