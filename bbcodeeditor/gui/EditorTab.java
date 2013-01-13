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

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bbcodeeditor.control.*;
import bbcodeeditor.control.events.BBCodeParseError;
import bbcodeeditor.control.events.BBCodeParseErrorListener;
import bbcodeeditor.control.events.HyperLinkListener;
import bbcodeeditor.control.export.bbcode.BBCodeParser;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The editor-tab in the bbcode-editor
 * 
 * @author hrniels
 */
public final class EditorTab extends JPanel implements TextAreaContainer {

	private static final long serialVersionUID = -3773500116606207883L;
	
	private EditorMenuPanel _menuPanel;
	private JPanel _mainPanel;
	
	private JPanel _statusBar;
	private JLabel _statusText;
	private ImageLoadingPanel _imageLoadingPanel;
	
	private Timer _hoverTimer;
	
	private MainPanel _window;
	
	private boolean DEBUG = false;
	
	private JTextPane _debugArea;
	
	// TODO make private
	public BBCTextField _textArea;
	
	/**
	 * constructor
	 * 
	 * @param window the main-window
	 */
	public EditorTab(MainPanel window) {
		super(new BorderLayout());
		
		_window = window;
		
		initLayout();
	}
	
	/**
	 * inits all components
	 */
	private void initLayout() {
		_mainPanel = new JPanel(new BorderLayout());
		add(_mainPanel,BorderLayout.CENTER);
		
		if(DEBUG) {
			_debugArea = new JTextPane();
			_debugArea.setFont(new Font("Verdana",Font.PLAIN,9));
			_debugArea.setPreferredSize(new Dimension(300,200));
			add(new JScrollPane(_debugArea),BorderLayout.WEST);
		}
		
		
    // the textarea
		
	  _textArea = new BBCTextField() {
	  	private static final long serialVersionUID = 6583703960397359644L;

			public void paint(Graphics g) {
	  		super.paint(g);
	  		
	  		if(DEBUG && isFocusOwner())
	  			_debugArea.setText(toString());
	  	}
	  };
	  
	  _textArea.setPopupMenu(new TextFieldPopupMenu(_textArea));
	  _textArea.setBaseURL(Settings.getBaseURL());
	  _textArea.setTabWidth(8);
	  _textArea.setBasicKeyListener(new AdvancedTextAreaKeyListener(_textArea));
		
	  _statusBar = new JPanel(new BorderLayout());
	  _statusBar.setBorder(new EmptyBorder(0,5,0,5));
	  _statusText = new JLabel();
	  _statusBar.add(_statusText,BorderLayout.CENTER);
	  
	  _imageLoadingPanel = new ImageLoadingPanel(_textArea);
	  _textArea.setImageLoader(_imageLoadingPanel);
	  _statusBar.add(_imageLoadingPanel,BorderLayout.EAST);
	  _statusBar.setPreferredSize(new Dimension(0,28));
	  
	  _mainPanel.add(_statusBar,BorderLayout.SOUTH);

	  if(Settings.ENABLE_SMILEYS)
			_textArea.setSmileys(getSmileyContainer());
		
		// disabled some tags if necessary
		List disabled = new ArrayList();
		Iterator it = BBCodeTags.getAllTags().iterator();
		while(it.hasNext()) {
			Integer tag = (Integer)it.next();
			if(!Settings.isTagEnabled(tag.intValue()))
				disabled.add(tag);
		}
		_textArea.disableTags(disabled);
		
	  _textArea.setDisplayCodeLineNumbers(Settings.DISPLAY_CODE_LINE_NUMBERS);
	  
		// set word wrap
		IWordWrap wrapStyle;
		switch(Settings.WORD_WRAP) {
			case Settings.WORD_WRAP_NO_WRAP:
				wrapStyle = new WordWrapNoWrap();
				break;
			case Settings.WORD_WRAP_CHAR_BASED:
				wrapStyle = new WordWrapCharBased(Settings.WORD_WRAP_POSITION);
				break;
			case Settings.WORD_WRAP_WORD_BASED:
				wrapStyle = new WordWrapWordBased(Settings.WORD_WRAP_POSITION);
				break;
			default:
				wrapStyle = new WordWrapPixelBased(_textArea);
				break;
		}
		_textArea.setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_ROOT,wrapStyle);
		_textArea.setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_QUOTE,wrapStyle);
		_textArea.setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_LIST,wrapStyle);
		
		// add bbcode-error-listener
		_textArea.addBBCodeParseErrorListener(new BBCodeParseErrorListener() {
			public void parseError(BBCodeParseError e) {
				String message = "";
				switch(e.getError()) {
					case BBCodeParser.ERR_INVALID_CONTENT:
						message = LanguageContainer.getText(Language.PARSER_ERR_INVALID_CONTENT);
						break;
					case BBCodeParser.ERR_MAX_NEST_LEVEL:
						message = LanguageContainer.getText(Language.PARSER_ERR_MAX_NEST_LEVEL);
						break;
					case BBCodeParser.ERR_MISS_OPEN_TAG:
						message = LanguageContainer.getText(Language.PARSER_ERR_MISS_OPEN_TAG);
						break;
					case BBCodeParser.ERR_NESTED_TAG:
						message = LanguageContainer.getText(Language.PARSER_ERR_NESTED_TAG);
						break;
					case BBCodeParser.ERR_WRONG_CLOSE_ORDER:
						message = LanguageContainer.getText(Language.PARSER_ERR_WRONG_CLOSE_ORDER);
						break;
					case BBCodeParser.ERR_MISSING_CLOSING_TAG:
						message = LanguageContainer.getText(Language.PARSER_ERR_MISSING_CLOSING_TAG);
						break;
				}
				
				JOptionPane.showMessageDialog(_textArea,message,"Parse-Error",JOptionPane.ERROR_MESSAGE);
				
				_window.switchToSource(e.getBBCode());
			}
		});
		
		// watch for hyperlinks
		_textArea.addHyperLinkListener(new HyperLinkListener(){
			public void hyperLinkClicked(boolean isEmail,String url) {
				if(!isEmail)
					Helper.openURL(url);
			}
		});
    
		// mouse listener for links
    _textArea.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(final MouseEvent e) {
				if(_hoverTimer != null)
					_hoverTimer.stop();
				
				_hoverTimer = new Timer(1,new ActionListener() {
					public void actionPerformed(ActionEvent ex) {
						final ContentSection s = _textArea.getSectionAtPixelPos(e.getX(),e.getY());
						
						if(s instanceof TextSection) {
							TextSection ts = (TextSection)s;

							String url = (String)ts.getAttribute(TextAttributes.URL);
							String email = null;
							if(url == null)
								email = (String)ts.getAttribute(TextAttributes.EMAIL);
							
							if(url != null)
								_statusText.setText(url);
							else if(email != null)
								_statusText.setText(email);
							else
								_statusText.setText("");
							
							if(url != null)
								_textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
							else
								_textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
						}
						else {
							if(s instanceof ImageSection) {
								ImageSection iSec = (ImageSection)s;
								if(iSec.isMaximizable()) {
									Image img;
									if(iSec.isMaximized())
										img = Toolkit.getDefaultToolkit().getImage(
												Helper.getFileInDocumentBase("./images/zoom_out.gif"));
									else
										img = Toolkit.getDefaultToolkit().getImage(
												Helper.getFileInDocumentBase("./images/zoom_in.gif"));
									
									Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,new Point(10,10),"zoom");
									_textArea.setCursor(cur);
								}
								else
									_textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
							}
							else
								_textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
							
							_statusText.setText("");
						}
						
						_hoverTimer = null;
					}
				});
				_hoverTimer.setRepeats(false);
				_hoverTimer.start();
			}
		});
	  
    JScrollPane p = new JScrollPane(this._textArea);
    p.setPreferredSize(new Dimension(900,600));
    
    _mainPanel.add(p,BorderLayout.CENTER);
    
    _menuPanel = new EditorMenuPanel(_textArea);
    add(_menuPanel,BorderLayout.NORTH);
	}
	
	/**
	 * builds the smiley-container from the parameters of the applet
	 * 
	 * @return the smiley-container
	 */
	private SmileyContainer getSmileyContainer() {
		int smileyCount = 0;
		String smCount = _window.getParameter("smileyCount");
		if(smCount != null) {
			try {
				smileyCount = Integer.parseInt(smCount);
			}
			catch(NumberFormatException e) {
				
			}
		}
		
		URL codeBase = Settings.getBaseURL();
		SmileyContainer con = new SmileyContainer();
		for(int i = 1;i <= smileyCount;i++) {
			String primCode = _window.getParameter("smileyPrimCode" + i);
			String secCode = _window.getParameter("smileySecCode" + i);
			String path = _window.getParameter("smileyPath" + i);
			if(primCode != null && path != null) {
				if(secCode == null)
					secCode = "";
				
				try {
					URL url = new URL(codeBase,path);
					con.addSmiley(new SecSmiley(_textArea,url.toString(),primCode,secCode));
				}
				catch(MalformedURLException e) {
					
				}
			}
		}
		
		return con;
	}
	
	public String getText() {
		return _textArea.getText();
	}
	
	public void insertText(String text) {
		_textArea.pasteTextAtCursor(text,true);
	}
	
	public void setText(String text) {
		_textArea.setText(text);
		_textArea.requestFocus();
	}
}
