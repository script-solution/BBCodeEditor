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

package bbcodeeditor.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.*;

import bbcodeeditor.control.*;
import bbcodeeditor.control.highlighter.CustomHighlighters;
import bbcodeeditor.control.highlighter.HighlighterEntry;
import bbcodeeditor.gui.dialogs.EmailDialog;
import bbcodeeditor.gui.dialogs.ImageDialog;
import bbcodeeditor.gui.dialogs.QuoteDialog;
import bbcodeeditor.gui.dialogs.URLDialog;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * @author Assi Nilsmussen
 * 
 * the popup-menu of the textfield.
 */
final class TextFieldPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	
	private JMenuItem _undo;
	private JMenuItem _redo;
	private JMenuItem _cut;
	private JMenu _copy;
	private JMenu _paste;
	private JSeparator _sep;
	private JMenuItem _editURL;
	private JMenuItem _editEmail;
	private JMenuItem _editQuoteTitle;
	private JMenuItem _editImage;
	private JMenuItem _copyURL;
	private JMenuItem _copyEmail;
	private JMenuItem _removeLinks;
	private JMenuItem _removeFormating;
	
	private JMenuItem _copyAsPlainText;
	private JMenuItem _copyAsBBCode;
	private JMenuItem _copyAsHTML;
	
	private JMenuItem _pasteAsPlainText;
	private JMenuItem _pasteAsBBCode;
	private JMenuItem _pasteAsHTML;
	private JMenuItem _pasteAsPlainTextHTML;
	
	private JMenu _changeHighlighting;
	private JMenu _listStyles;
	private JRadioButtonMenuItem _listStyleDefault;
	private JRadioButtonMenuItem _listStyleDisc;
	private JRadioButtonMenuItem _listStyleSquare;
	private JRadioButtonMenuItem _listStyleCircle;
	private JRadioButtonMenuItem _listStyleRomanB;
	private JRadioButtonMenuItem _listStyleRomanS;
	private JRadioButtonMenuItem _listStyleAlphaB;
	private JRadioButtonMenuItem _listStyleAlphaS;
	private JRadioButtonMenuItem _listStyleNum;
	
	private AbstractTextField _textField;
	
	private boolean _initialized = false;

	/**
	 * constructor
	 * 
	 * @param textField the textField
	 */
	public TextFieldPopupMenu(AbstractTextField textField) {
		_textField = textField;
	}
	
	public void show(Component origin,int x,int y) {
		init();
		
		// enable / disable the menu-items
		boolean isInSelMode = _textField.getSelection().isInSelectionMode();
		_copy.setEnabled(isInSelMode);
		_cut.setEnabled(isInSelMode);
		
		Transferable clipboardContent = Toolkit.getDefaultToolkit().
			getSystemClipboard().getContents(this);
		_paste.setEnabled(clipboardContent != null);
		
		_undo.setEnabled(_textField.getHistory().getUndoLength() > 0);
		_redo.setEnabled(_textField.getHistory().getRedoLength() > 0);
		
		// add the "edit-item" if necessary
		ContentSection current = _textField.getCurrentSection();
		if(current instanceof TextSection) {
			TextAttributes attributes = ((TextSection)current).getAttributes();
			boolean isLink = attributes.isSet(TextAttributes.URL);
			boolean isEmail = attributes.isSet(TextAttributes.EMAIL);
			boolean isQuoteEnv = _textField.getCurrentEnvironment() instanceof QuoteEnvironment;
			boolean isCodeEnv = _textField.getCurrentEnvironment() instanceof CodeEnvironment;
			boolean isListEnv = _textField.getCurrentEnvironment() instanceof ListEnvironment;
			
			if(isLink || isEmail || isQuoteEnv) {
				if(getComponentIndex(_sep) == -1)
					add(_sep);
			
				if(isQuoteEnv)
					add(_editQuoteTitle);
				
				if(isLink) {
					add(_editURL);
					add(_copyURL);
				}
				if(isEmail) {
					add(_editEmail);
					add(_copyEmail);
				}
			}
			
			// don't show formating buttons in code-envs
			if(isCodeEnv) {
				remove(_removeFormating);
				remove(_removeLinks);
				add(_changeHighlighting);
			}
			else {
				remove(_changeHighlighting);
				add(_removeFormating);
				add(_removeLinks);
			}
			
			if(isListEnv) {
				ListEnvironment env = (ListEnvironment)_textField.getCurrentEnvironment();
				int type = env.getListType();
				
				// deselect all
				Component[] comps = _listStyles.getMenuComponents();
				for(int i = 0;i < comps.length;i++) {
					JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem)comps[i];
					menuItem.setSelected(false);
				}
				
				// selcect active type
				switch(type) {
					case ListTypes.TYPE_DEFAULT:
						_listStyleDefault.setSelected(true);
						break;
					case ListTypes.TYPE_CIRCLE:
						_listStyleCircle.setSelected(true);
						break;
					case ListTypes.TYPE_DISC:
						_listStyleDisc.setSelected(true);
						break;
					case ListTypes.TYPE_SQUARE:
						_listStyleSquare.setSelected(true);
						break;
					case ListTypes.TYPE_ALPHA_B:
						_listStyleAlphaB.setSelected(true);
						break;
					case ListTypes.TYPE_ALPHA_S:
						_listStyleAlphaS.setSelected(true);
						break;
					case ListTypes.TYPE_ROMAN_B:
						_listStyleRomanB.setSelected(true);
						break;
					case ListTypes.TYPE_ROMAN_S:
						_listStyleRomanS.setSelected(true);
						break;
					case ListTypes.TYPE_NUM:
						_listStyleNum.setSelected(true);
						break;
				}
				
				add(_listStyles);
			}
			else if(!isListEnv)
				remove(_listStyles);
			
			if(!isLink) {
				remove(_editURL);
				remove(_copyURL);
			}
			if(!isEmail) {
				remove(_editEmail);
				remove(_copyEmail);
			}
			if(!isLink && !isEmail && !isQuoteEnv && _sep != null)
				remove(_sep);
			if(!isQuoteEnv && _editQuoteTitle != null)
				remove(_editQuoteTitle);
			
			remove(_editImage);
		}
		// image?
		else {
			remove(_changeHighlighting);
			remove(_removeFormating);
			remove(_removeLinks);
			remove(_listStyles);
			remove(_editURL);
			remove(_copyURL);
			remove(_editEmail);
			remove(_copyEmail);
			remove(_editQuoteTitle);
			
			// can we edit the image-URL?
			if(current instanceof ImageSection && !(current instanceof SmileySection))
				add(_editImage);
			else
				remove(_editImage);
		}
		
		// show the popup
		super.show(origin,x,y);
	}
	
	/**
	 * Inits the components if necessary
	 */
	private void init() {
		if(_initialized)
			return;
		
		// UNDO
		_undo = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_UNDO));
		_undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.undo();
			}
		});
		add(_undo);
		
		// REDO
		_redo = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_REDO));
		_redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.redo();
			}
		});
		add(_redo);
		
		addSeparator();
		
		// CUT
		_cut = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_CUT));
		_cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.cutSelectedText();
			}
		});
		add(_cut);
		
		// COPY
		_copy = new JMenu(LanguageContainer.getText(Language.GUI_POPUP_COPY));
		
		_copyAsBBCode = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_COPY_BBCODE));
		_copyAsBBCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.copySelectedText();
			}
		});
		_copy.add(_copyAsBBCode);
		
		_copyAsHTML = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_COPY_HTML));
		_copyAsHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String html = _textField.getSelectedText(IPublicController.SYNTAX_HTML);
				StringSelection selString = new StringSelection(html);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selString,selString);
			}
		});
		_copy.add(_copyAsHTML);
		
		_copyAsPlainText = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_COPY_PLAIN));
		_copyAsPlainText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String html = _textField.getSelectedText(IPublicController.SYNTAX_PLAIN);
				StringSelection selString = new StringSelection(html);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selString,selString);
			}
		});
		_copy.add(_copyAsPlainText);
		
		add(_copy);
		
		// PASTE
		_paste = new JMenu(LanguageContainer.getText(Language.GUI_POPUP_PASTE));
				
		_pasteAsBBCode = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_PASTE_BBCODE));
		_pasteAsBBCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.pasteTextAtCursor(IPublicController.SYNTAX_BBCODE);
			}
		});
		_paste.add(_pasteAsBBCode);
				
		_pasteAsHTML = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_PASTE_HTML));
		_pasteAsHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.pasteTextAtCursor(IPublicController.SYNTAX_HTML);
			}
		});
		_paste.add(_pasteAsHTML);
				
		_pasteAsPlainText = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_PASTE_PLAIN));
		_pasteAsPlainText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.pasteTextAtCursor(IPublicController.SYNTAX_PLAIN);
			}
		});
		_paste.add(_pasteAsPlainText);
				
		_pasteAsPlainTextHTML = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_PASTE_PLAIN_HTML));
		_pasteAsPlainTextHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.pasteTextAtCursor(IPublicController.SYNTAX_PLAIN_HTML);
			}
		});
		_paste.add(_pasteAsPlainTextHTML);
		
		add(_paste);

		// REMOVE LINKS
		_removeLinks = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_REMOVE_LINKS));
		_removeLinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.removeLinks();
			}
		});
		add(_removeLinks);

		// REMOVE FORMATING
		_removeFormating = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_REMOVE_FORMATING));
		_removeFormating.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.removeAttributes();
			}
		});
		add(_removeFormating);
		
		// EDIT URL
		_editURL = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_EDIT_LINK));
		_editURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] positions = _textField.getPositionsForActions();
				if(positions[0] == -1 && positions[1] == -1)
					return;
				
				TextAttributes attributes = _textField.getAttributes(positions[0],positions[1]);
				String address = attributes.getURL();
				String title = _textField.getText(positions[0],positions[1],IPublicController.SYNTAX_PLAIN);
				
				_textField.selectText(positions[0],positions[1]);

				URLDialog frm = new URLDialog(Settings.DIALOG_COMPONENT,null,
						LanguageContainer.getText(Language.GUI_DIALOG_LINK_EDIT_TITLE),address,title);
				if(frm.okClicked()) {
					String newAddress = (String)frm.getValueOf(URLDialog.URL_ADDRESS);
					_textField.editLink(false,newAddress);
				}
				
				_textField.clearSelection();
			}
		});
		
		// EDIT EMAIL
		_editEmail = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_EDIT_EMAIL));
		_editEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] positions = _textField.getPositionsForActions();
				if(positions[0] == -1 && positions[1] == -1)
					return;
				
				TextAttributes attributes = _textField.getAttributes(positions[0],positions[1]);
				String address = attributes.getEmail();
				String title = _textField.getText(positions[0],positions[1],IPublicController.SYNTAX_PLAIN);
				
				_textField.selectText(positions[0],positions[1]);
				
				EmailDialog frm = new EmailDialog(Settings.DIALOG_COMPONENT,null,
						LanguageContainer.getText(Language.GUI_DIALOG_EMAIL_EDIT_TITLE),address,title);
				if(frm.okClicked()) {
					String newAddress = (String)frm.getValueOf(URLDialog.URL_ADDRESS);
					_textField.editLink(true,newAddress);
				}
				
				_textField.clearSelection();
			}
		});
		
		// EDIT QUOTE AUTHOR
		_editQuoteTitle = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_EDIT_QUOTE_AUTHOR));
		_editQuoteTitle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment current = _textField.getCurrentEnvironment();
				if(current instanceof QuoteEnvironment) {
					String author = ((QuoteEnvironment)current).getAuthor();
					QuoteDialog frm = new QuoteDialog(Settings.DIALOG_COMPONENT,null,
							LanguageContainer.getText(Language.GUI_DIALOG_QUOTE_EDIT_TITLE),author);
					if(frm.okClicked()) {
						String newAuthor = (String)frm.getValueOf(QuoteDialog.AUTHOR);
						_textField.editAuthor(newAuthor);
					}
				}
				
				_textField.requestFocusInWindow();
			}
		});
		
		// EDIT IMAGE
		_editImage = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_EDIT_IMAGE_URL));
		_editImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContentSection current = _textField.getCurrentSection();
				if(current instanceof ImageSection && !(current instanceof SmileySection)) {
					String url = ((ImageSection)current).getImage().getImagePath();
					ImageDialog frm = new ImageDialog(Settings.DIALOG_COMPONENT,null,
							LanguageContainer.getText(Language.GUI_DIALOG_IMAGE_TITLE),url);
					if(frm.okClicked())
						_textField.editImageURL((String)frm.getValueOf(ImageDialog.IMAGE_ADDRESS));
				}
				
				_textField.requestFocusInWindow();
			}
		});
		
		_copyURL = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_COPY_LINK));
		_copyURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextAttributes attributes = _textField.getAttributes();
				Object url = attributes.getURL();
				String val;
				if(url != null)
					val = (String)url;
				else
					val = "";
				
				StringSelection selString = new StringSelection(val);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selString,selString);
			}
		});
		
		_copyEmail = new JMenuItem(LanguageContainer.getText(Language.GUI_POPUP_COPY_EMAIL));
		_copyEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextAttributes attributes = _textField.getAttributes();
				Object email = attributes.getEmail();
				String val;
				if(email != null)
					val = (String)email;
				else
					val = "";
				
				StringSelection selString = new StringSelection(val);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selString,selString);
			}
		});
		
		// LIST STYLES
		_listStyles = new JMenu(LanguageContainer.getText(Language.GUI_POPUP_CHANGE_LIST_TYPE));
		
		_listStyleDefault = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_DEFAULT));
		_listStyleDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_DEFAULT);
			}
		});
		_listStyles.add(_listStyleDefault);
		
		_listStyleNum = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_NUMERIC));
		_listStyleNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_NUM);
			}
		});
		_listStyles.add(_listStyleNum);

		_listStyleDisc = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_DISC));
		_listStyleDisc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_DISC);
			}
		});
		_listStyles.add(_listStyleDisc);
		
		_listStyleSquare = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_SQUARE));
		_listStyleSquare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_SQUARE);
			}
		});
		_listStyles.add(_listStyleSquare);
		
		_listStyleCircle = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_CIRCLE));
		_listStyleCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_CIRCLE);
			}
		});
		_listStyles.add(_listStyleCircle);
		
		_listStyleAlphaB = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_ALPHA_B));
		_listStyleAlphaB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_ALPHA_B);
			}
		});
		_listStyles.add(_listStyleAlphaB);
		
		_listStyleAlphaS = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_ALPHA_S));
		_listStyleAlphaS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_ALPHA_S);
			}
		});
		_listStyles.add(_listStyleAlphaS);
		
		_listStyleRomanS = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_ROMAN_S));
		_listStyleRomanS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_ROMAN_S);
			}
		});
		_listStyles.add(_listStyleRomanS);
		
		_listStyleRomanB = new JRadioButtonMenuItem(
				LanguageContainer.getText(Language.GUI_POPUP_LIST_TYPE_ROMAN_B));
		_listStyleRomanB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.setListType(ListTypes.TYPE_ROMAN_B);
			}
		});
		_listStyles.add(_listStyleRomanB);
		
		// HIGHLIGHTING
		_changeHighlighting = new JMenu(
				LanguageContainer.getText(Language.GUI_POPUP_CODE_CHANGE_HL));
		
		// add no-hl item
		JMenuItem noHLItem = new JMenuItem(LanguageContainer.getText(Language.GUI_BTN_CODE_NO_HL));
		noHLItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment current = _textField.getCurrentEnvironment();
				if(current instanceof CodeEnvironment)
					_textField.setHighlightSyntax((CodeEnvironment)current,null);
			}
		});
		_changeHighlighting.add(noHLItem);
		
		// add all highlighters
		java.util.List highlighter = CustomHighlighters.getHighlighter();
		Iterator it = highlighter.iterator();
		while(it.hasNext()) {
			final HighlighterEntry entry = (HighlighterEntry)it.next();
			JMenuItem item = new JMenuItem(entry.getName());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Environment current = _textField.getCurrentEnvironment();
					if(current instanceof CodeEnvironment)
						_textField.setHighlightSyntax((CodeEnvironment)current,entry.getId());
				}
			});
			_changeHighlighting.add(item);
		}
		
		// SEPARATOR
		_sep = new JSeparator();
		
		_initialized = true;
	}
}