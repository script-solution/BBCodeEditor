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

package bbcodeeditor.gui.international;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import bbcodeeditor.gui.Helper;


/**
 * The language-interface
 * 
 * @author hrniels
 */
public class Language {
	
	/**
	 * The ok-button of the dialogs
	 */
	public static final String GUI_DIALOG_OK										= "gui.dialog.ok";
	
	/**
	 * The cancel-button of the dialogs
	 */
	public static final String GUI_DIALOG_CANCEL								= "gui.dialog.cancel";

	/**
	 * The title for the search&replace-dialog
	 */
	public static final String GUI_DIALOG_SEARCH_TITLE					= "gui.dialog.search.title";
	
	/**
	 * The search-button-title
	 */
	public static final String GUI_DIALOG_SEARCH_SEARCH					= "gui.dialog.search.search";
	
	/**
	 * The replace-button-title
	 */
	public static final String GUI_DIALOG_SEARCH_REPLACE				= "gui.dialog.search.replace";
	
	/**
	 * The scope in the s&r dialog
	 */
	public static final String GUI_DIALOG_SEARCH_SCOPE					= "gui.dialog.search.scope";
	
	/**
	 * Wether the replacement should be done in the whole document
	 */
	public static final String GUI_DIALOG_SEARCH_ALL						= "gui.dialog.search.all";
	
	/**
	 * Wether just the selection should be used
	 */
	public static final String GUI_DIALOG_SEARCH_SELECTION			= "gui.dialog.search.selection";
	
	/**
	 * Wether the search should be case-sensitve
	 */
	public static final String GUI_DIALOG_SEARCH_CASESENSITIVE	= "gui.dialog.search.casesensitive";
	
	/**
	 * The search-direction
	 */
	public static final String GUI_DIALOG_SEARCH_DIRECTION			= "gui.dialog.search.direction";
	
	/**
	 * Search backwards
	 */
	public static final String GUI_DIALOG_SEARCH_BACKWARDS			= "gui.dialog.search.backwards";
	
	/**
	 * Search forward
	 */
	public static final String GUI_DIALOG_SEARCH_FORWARD				= "gui.dialog.search.forward";
	
	/**
	 * Replace & find
	 */
	public static final String GUI_DIALOG_SEARCH_REPLACEFIND		= "gui.dialog.search.replacefind";
	
	/**
	 * Replace all
	 */
	public static final String GUI_DIALOG_SEARCH_REPLACEALL			= "gui.dialog.search.replaceall";
	
	/**
	 * Wrap the search
	 */
	public static final String GUI_DIALOG_SEARCH_WRAPSEARCH			= "gui.dialog.search.wrapsearch";
	
	/**
	 * Parse BBCode in the result
	 */
	public static final String GUI_DIALOG_SEARCH_PARSEBBCODE		= "gui.dialog.search.parsebbcode";
	
	/**
	 * No matches have been found
	 */
	public static final String GUI_DIALOG_SEARCH_NOMATCHES			= "gui.dialog.search.nomatches";
	
	/**
	 * X ( &gt; 0 ) matches have been found
	 */
	public static final String GUI_DIALOG_SEARCH_XMATCHES				= "gui.dialog.search.xmatches";
	
	/**
	 * The tooltip of the bold-button
	 */
	public static final String GUI_BTN_BOLD_TOOLTIP							= "gui.btn.bold.tooltip";

	/**
	 * The tooltip of the italic-button
	 */
	public static final String GUI_BTN_ITALIC_TOOLTIP						= "gui.btn.italic.tooltip";

	/**
	 * The tooltip of the underline-button
	 */
	public static final String GUI_BTN_UNDERLINE_TOOLTIP				= "gui.btn.underline.tooltip";
	
	/**
	 * The tooltip of the strike-button
	 */
	public static final String GUI_BTN_STRIKE_TOOLTIP						= "gui.btn.strike.tooltip";

	/**
	 * The tooltip of the font-color-button
	 */
	public static final String GUI_BTN_FONTCOLOR_TOOLTIP				= "gui.btn.fontcolor.tooltip";

	/**
	 * The tooltip of the bgcolor-button
	 */
	public static final String GUI_BTN_BGCOLOR_TOOLTIP					= "gui.btn.bgcolor.tooltip";

	/**
	 * The tooltip of the smiley-button
	 */
	public static final String GUI_BTN_SMILEY_TOOLTIP						= "gui.btn.smiley.tooltip";

	/**
	 * The tooltip of the font-size-button
	 */
	public static final String GUI_BTN_FONTSIZE_TOOLTIP					= "gui.btn.fontsize.tooltip";

	/**
	 * The tooltip of the font-family-button
	 */
	public static final String GUI_BTN_FONTFAMILY_TOOLTIP				= "gui.btn.fontfamily.tooltip";

	/**
	 * The tooltip of the sub-script-button
	 */
	public static final String GUI_BTN_SUBSCRIPT_TOOLTIP				= "gui.btn.subscript.tooltip";
	
	/**
	 * The tooltip of the super-script-button
	 */
	public static final String GUI_BTN_SUPSCRIPT_TOOLTIP				= "gui.btn.supscript.tooltip";

	/**
	 * The tooltip of the new-button
	 */
	public static final String GUI_BTN_NEW_TOOLTIP							= "gui.btn.new.tooltip";

	/**
	 * The tooltip of the cut-button
	 */
	public static final String GUI_BTN_CUT_TOOLTIP							= "gui.btn.cut.tooltip";

	/**
	 * The tooltip of the copy-button
	 */
	public static final String GUI_BTN_COPY_TOOLTIP							= "gui.btn.copy.tooltip";

	/**
	 * The tooltip of the paste-button
	 */
	public static final String GUI_BTN_PASTE_TOOLTIP						= "gui.btn.paste.tooltip";
	
	/**
	 * The tooltip for the search&replace-button
	 */
	public static final String GUI_BTN_SEARCH_N_REPLACE_TOOLTIP	= "gui.btn.searchnreplace.tooltip";

	/**
	 * The tooltip of the redo-button
	 */
	public static final String GUI_BTN_REDO_TOOLTIP							= "gui.btn.redo.tooltip";

	/**
	 * The tooltip of the undo-button
	 */
	public static final String GUI_BTN_UNDO_TOOLTIP							= "gui.btn.undo.tooltip";

	/**
	 * The tooltip of the align-left-button
	 */
	public static final String GUI_BTN_ALIGN_LEFT_TOOLTIP				= "gui.btn.alignleft.tooltip";

	/**
	 * The tooltip of the align-center-button
	 */
	public static final String GUI_BTN_ALIGN_CENTER_TOOLTIP			= "gui.btn.aligncenter.tooltip";

	/**
	 * The tooltip of the align-right-button
	 */
	public static final String GUI_BTN_ALIGN_RIGHT_TOOLTIP			= "gui.btn.alignright.tooltip";

	/**
	 * The tooltip of the quote-button
	 */
	public static final String GUI_BTN_QUOTE_TOOLTIP						= "gui.btn.quote.tooltip";

	/**
	 * The tooltip of the code-button
	 */
	public static final String GUI_BTN_CODE_TOOLTIP							= "gui.btn.code.tooltip";
	
	/**
	 * The name of the no-highlighting-item of the code-button
	 */
	public static final String GUI_BTN_CODE_NO_HL								= "gui.btn.code.nohl";

	/**
	 * The tooltip of the list-button
	 */
	public static final String GUI_BTN_LIST_TOOLTIP							= "gui.btn.list.tooltip";

	/**
	 * The tooltip of the link-button
	 */
	public static final String GUI_BTN_LINK_TOOLTIP							= "gui.btn.link.tooltip";

	/**
	 * The tooltip of the email-button
	 */
	public static final String GUI_BTN_EMAIL_TOOLTIP						= "gui.btn.email.tooltip";
	
	/**
	 * The name of the default-item in the list of the quote-button
	 */
	public static final String GUI_BTN_QUOTE_LIST_DEFAULT				= "gui.btn.quote.list.default";
	
	/**
	 * The name of the user-defined-item in the list of the quote-button
	 */
	public static final String GUI_BTN_QUOTE_LIST_USER_DEFINED	= "gui.btn.quote.list.userdef";
	
	/**
	 * The name of the default-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_DEFAULT				= "gui.btn.list.list.default";
	
	/**
	 * The name of the circle-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_CIRCLE					= "gui.btn.list.list.circle";
	
	/**
	 * The name of the square-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_SQUARE					= "gui.btn.list.list.square";
	
	/**
	 * The name of the disc-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_DISC						= "gui.btn.list.list.disc";
	
	/**
	 * The name of the numeric-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_NUMERIC				= "gui.btn.list.list.numeric";
	
	/**
	 * The name of the alpha-s-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_ALPHA_S				= "gui.btn.list.list.alphas";
	
	/**
	 * The name of the alpha-b-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_ALPHA_B				= "gui.btn.list.list.alphab";
	
	/**
	 * The name of the roman-s-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_ROMAN_S				= "gui.btn.list.list.romans";
	
	/**
	 * The name of the roman-b-item in the list of the list-button
	 */
	public static final String GUI_BTN_LIST_LIST_ROMAN_B				= "gui.btn.list.list.romanb";

	/**
	 * The tooltip of the image-button
	 */
	public static final String GUI_BTN_IMAGE_TOOLTIP						= "gui.btn.image.tooltip";

	/**
	 * The title of the editor-tab
	 */
	public static final String GUI_TAB_EDITOR_TITLE							= "gui.tab.editor.title";

	/**
	 * The title of the source-tab
	 */
	public static final String GUI_TAB_SOURCE_TITLE							= "gui.tab.source.title";

	/**
	 * The title of the color-dialog
	 */
	public static final String GUI_DIALOG_COLOR_TITLE						= "gui.dialog.color.title";

	/**
	 * The title of the insert-link-dialog
	 */
	public static final String GUI_DIALOG_LINK_INSERT_TITLE			= "gui.dialog.link.insert.title";

	/**
	 * The title of the edit-link-dialog
	 */
	public static final String GUI_DIALOG_LINK_EDIT_TITLE				= "gui.dialog.link.edit.title";

	/**
	 * The name of the title-field in the link-dialog
	 */
	public static final String GUI_DIALOG_LINK_TITLE_FIELD			= "gui.dialog.link.title.field";

	/**
	 * The name of the link-field in the link-dialog
	 */
	public static final String GUI_DIALOG_LINK_LINK_FIELD				= "gui.dialog.link.link.field";

	/**
	 * The title of the insert-email-dialog
	 */
	public static final String GUI_DIALOG_EMAIL_INSERT_TITLE		= "gui.dialog.email.insert.title";

	/**
	 * The title of the edit-email-dialog
	 */
	public static final String GUI_DIALOG_EMAIL_EDIT_TITLE			= "gui.dialog.email.edit.title";

	/**
	 * The name of the title-field in the email-dialog
	 */
	public static final String GUI_DIALOG_EMAIL_TITLE_FIELD			= "gui.dialog.email.title.field";

	/**
	 * The name of the email-field in the email-dialog
	 */
	public static final String GUI_DIALOG_EMAIL_EMAIL_FIELD			= "gui.dialog.email.email.field";

	/**
	 * The title of the image-dialog
	 */
	public static final String GUI_DIALOG_IMAGE_TITLE						= "gui.dialog.image.title";

	/**
	 * The name of the URL-field in the image-dialog
	 */
	public static final String GUI_DIALOG_IMAGE_URL_FIELD				= "gui.dialog.image.url.field";

	/**
	 * The title of the insert-quote-dialog
	 */
	public static final String GUI_DIALOG_QUOTE_INSERT_TITLE		= "gui.dialog.quote.insert.title";

	/**
	 * The title of the edit-quote-dialog
	 */
	public static final String GUI_DIALOG_QUOTE_EDIT_TITLE			= "gui.dialog.quote.edit.title";

	/**
	 * The name of the author-field in the quote-dialog
	 */
	public static final String GUI_DIALOG_QUOTE_AUTHOR_FIELD		= "gui.dialog.quote.author.field";

	/**
	 * The title of the new-dialog
	 */
	public static final String GUI_DIALOG_NEW_TITLE							= "gui.dialog.new.title";

	/**
	 * The message of the new-dialog
	 */
	public static final String GUI_DIALOG_NEW_MESSAGE						= "gui.dialog.new.message";

	/**
	 * The name of the cut-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_CUT										= "gui.popup.cut";

	/**
	 * The name of the copy-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY										= "gui.popup.copy";

	/**
	 * The name of the bbcode-copy-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY_BBCODE						= "gui.popup.copy.bbcode";

	/**
	 * The name of the html-copy-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY_HTML							= "gui.popup.copy.html";

	/**
	 * The name of the plain-copy-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY_PLAIN							= "gui.popup.copy.plain";

	/**
	 * The name of the paste-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_PASTE									= "gui.popup.paste";

	/**
	 * The name of the bbcode-paste-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_PASTE_BBCODE						= "gui.popup.paste.bbcode";

	/**
	 * The name of the html-paste-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_PASTE_HTML							= "gui.popup.paste.html";

	/**
	 * The name of the plain-paste-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_PASTE_PLAIN						= "gui.popup.paste.plain";
	
	/**
	 * the name of the plain-html-paste-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_PASTE_PLAIN_HTML				= "gui.popup.paste.plain.html";

	/**
	 * The name of the undo-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_UNDO										= "gui.popup.undo";

	/**
	 * The name of the redo-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_REDO										= "gui.popup.redo";

	/**
	 * The name of the edit-email-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_EDIT_EMAIL							= "gui.popup.editemail";

	/**
	 * The name of the edit-link-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_EDIT_LINK							= "gui.popup.editlink";

	/**
	 * The name of the copy-link-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY_LINK							= "gui.popup.copylink";

	/**
	 * The name of the copy-email-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_COPY_EMAIL							= "gui.popup.copyemail";

	/**
	 * The name of the remove-formating-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_REMOVE_FORMATING				= "gui.popup.removeformating";

	/**
	 * The name of the remove-links-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_REMOVE_LINKS						= "gui.popup.removelinks";

	/**
	 * The name of the edit-quote-author-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_EDIT_QUOTE_AUTHOR			= "gui.popup.editquoteauthor";

	/**
	 * The name of the edit-image-url-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_EDIT_IMAGE_URL					= "gui.popup.editimageurl";
	
	/**
	 * The name of the menu-item "change-highlighting" in the popup
	 */
	public static final String GUI_POPUP_CODE_CHANGE_HL					= "gui.popup.code.changehl";

	/**
	 * The name of the change-list-type-item in the popup of the textfield
	 */
	public static final String GUI_POPUP_CHANGE_LIST_TYPE				= "gui.popup.changelisttype";
	
	/**
	 * The name of the default-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_DEFAULT				= "gui.popup.list.type.default";
	
	/**
	 * The name of the circle-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_CIRCLE					= "gui.popup.list.type.circle";
	
	/**
	 * The name of the square-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_SQUARE					= "gui.popup.list.type.square";
	
	/**
	 * The name of the disc-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_DISC						= "gui.popup.list.type.disc";
	
	/**
	 * The name of the numeric-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_NUMERIC				= "gui.popup.list.type.numeric";
	
	/**
	 * The name of the alpha-s-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_ALPHA_S				= "gui.popup.list.type.alphas";
	
	/**
	 * The name of the alpha-b-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_ALPHA_B				= "gui.popup.list.type.alphab";
	
	/**
	 * The name of the roman-s-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_ROMAN_S				= "gui.popup.list.type.romans";
	
	/**
	 * The name of the roman-b-item in the change-list-type popup
	 */
	public static final String GUI_POPUP_LIST_TYPE_ROMAN_B				= "gui.popup.list.type.romanb";

	/**
	 * The parser-error "invalid content"
	 */
	public static final String PARSER_ERR_INVALID_CONTENT				= "parser.err.invalidcontent";

	/**
	 * The parser-error "max nesting level"
	 */
	public static final String PARSER_ERR_MAX_NEST_LEVEL				= "parser.err.maxnestlevel";

	/**
	 * The parser-error "missing opening tag"
	 */
	public static final String PARSER_ERR_MISS_OPEN_TAG					= "parser.err.missopentag";

	/**
	 * The parser-error "nested tag"
	 */
	public static final String PARSER_ERR_NESTED_TAG						= "parser.err.nestedtag";

	/**
	 * The parser-error "wrong close order"
	 */
	public static final String PARSER_ERR_WRONG_CLOSE_ORDER			= "parser.err.wrongcloseorder";
	
	/**
	 * The parser-error for "missing closing tag"
	 */
	public static final String PARSER_ERR_MISSING_CLOSING_TAG		= "parser.err.missingclosingtag";

	/**
	 * The error-message for an invalid image url
	 */
	public static final String ERROR_INVALID_IMAGE_URL					= "error.invalid_image_url";

	/**
	 * The error-message for a missing link address
	 */
	public static final String ERROR_MISSING_LINK_ADDRESS				= "error.missing_link_address";

	/**
	 * The error-message for a missing email address
	 */
	public static final String ERROR_MISSING_EMAIL_ADDRESS			= "error.missing_email_address";
	
	/**
	 * The dialog-title for the help-dialog
	 */
	public static final String GUI_DIALOG_HELP_TITLE						= "gui.dialog.help.title";
	
	/**
	 * The close-button-title for the help-dialog
	 */
	public static final String GUI_DIALOG_HELP_CLOSE						= "gui.dialog.help.close";
	
	/**
	 * The help-text for the help-dialog
	 */
	public static final String GUI_DIALOG_HELP_TEXT							= "gui.dialog.help.text";
	
	/**
	 * The text when loading an image
	 */
	public static final String GUI_STATUSBAR_LOADING_IMAGE			= "gui.statusbar.loading_image";
	
	/**
	 * The text for the title-field of the extra-dialogs
	 */
	public static final String GUI_DIALOG_EXTRA_TITLE						= "gui.dialog.extra.title";

	/**
	 * the language-entries
	 */
	protected final Map _entries = new HashMap();
	
	/**
	 * Constructor
	 * 
	 * @param language the language-name. Will be used as './language/&lt;language&gt;.txt'
	 * @throws LanguageException if loading the language fails
	 */
	public Language(String language) throws LanguageException {
		loadLanguageEntries("./language/" + language + ".txt");
	}

	/**
	 * Retrieves the text in this language for the given id
	 * 
	 * @param id the id of the text
	 * @return the text
	 */
	public String getText(String id) {
		return (String)_entries.get(id);
	}
	
	/**
	 * Loads all language-entries from the given file.<br>
	 * The following format will be expected:
	 * <pre>
	 * &lt;key1&gt;="&lt;value2&gt;"
	 * &lt;key2&gt;="&lt;value2&gt;"
	 * ...
	 * &lt;keyn&gt;="&lt;valuen&gt;"
	 * </pre>
	 * The value may have multiple lines, the key will be trimmed, and chars between = and "
	 * will be ignored.
	 * 
	 * @param file the file with the language-entries
	 * @throws LanguageException if loading the language fails
	 */
	void loadLanguageEntries(String file) throws LanguageException {
		URL loc = Helper.getFileInDocumentBase(file);
		if(loc == null)
			throw new LanguageException(file);
		
		// open file
		BufferedInputStream stream;
		InputStreamReader strReader;
		try {
			stream = new BufferedInputStream(loc.openStream());
			strReader = new InputStreamReader(stream,Charset.forName("ISO-8859-1"));
		}
		catch(IOException e1) {
			throw new LanguageException(file,e1);
		}
		
		BufferedReader buf = new BufferedReader(strReader);
		
		// read file
		try {
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line = buf.readLine()) != null) {
				buffer.append(line + "\n");
			}
			buf.close();
			
			addEntries(buffer);
		}
		catch(IOException e) {
			throw new LanguageException(file,e);
		}
	}
	
	/**
	 * Adds all entries in the given buffer
	 * 
	 * @param buf the buffer
	 */
	private void addEntries(StringBuffer buf) {
		String key = "";
		StringBuffer temp = new StringBuffer();
		boolean inVal = false;
		for(int i = 0,len = buf.length();i < len;i++) {
			char c = buf.charAt(i);
			switch(c) {
				case '=':
					// found a key?
					if(!inVal) {
						key = temp.toString().trim();
						temp = new StringBuffer();
					}
					// if we are in a value, we treat this as plain-text
					else
						temp.append(c);
					break;
				
				case '"':
					// if it is not escaped and we have a valid key
					if(!isEscaped(buf,i) && key.length() > 0) {
						// do we already have the value?
						if(inVal)
							_entries.put(key,temp.toString());
						
						// clear buffer and invert inVal
						inVal = !inVal;
						temp = new StringBuffer();
						
						continue;
					}
					// fall through
					
				default:
					// by default we append the char to the buffer
					if(c != '\\' || isEscaped(buf,i))
						temp.append(c);
					break;
			}
		}
	}
	
	/**
	 * checks wether the character at the given position is escaped
	 * 
	 * @param buf the buffer
	 * @param pos the position
	 * @return true if the char is escaped
	 */
	private boolean isEscaped(StringBuffer buf,int pos) {
		int c = 0;
		for(int i = pos - 1;i >= 0;i--) {
			if(buf.charAt(i) != '\\')
				break;
			c++;
		}
		return c % 2 == 1;
	}
}