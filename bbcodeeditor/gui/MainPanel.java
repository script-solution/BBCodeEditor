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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bbcodeeditor.control.export.ColorFunctions;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.highlighter.CustomHighlighters;
import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;
import bbcodeeditor.gui.international.LanguageException;


/**
 * The main-panel that is used for the applet and standalone-version
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = -6301848975640103299L;
	
	private Map _params;
	private JPanel _basePanel;
	
	private JTabbedPane _tabPane;
	private EditorTab _editorPanel;
	private SourceTab _sourcePanel;
	
	/**
	 * constructor
	 */
	public MainPanel(Map params) {
		super(new BorderLayout());
		
		_params = params;
		
		// set look and feel
		try {
			String lookAndFeel = (String)params.get("lookAndFeel");
			if(lookAndFeel == null)
				lookAndFeel = "system";
			else
				lookAndFeel = lookAndFeel.toLowerCase();
			
			String laf = "";
			if(lookAndFeel.equals("metal"))
				laf = "javax.swing.plaf.metal.MetalLookAndFeel";
			else if(lookAndFeel.equals("motif"))
				laf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			else
				laf = UIManager.getSystemLookAndFeelClassName();
			
			UIManager.setLookAndFeel(laf);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		Settings.DIALOG_COMPONENT = this;
		
		// set language
		String lang = (String)params.get("lang");
		if(lang == null)
			lang = "ger_du";
		
		try {
			LanguageContainer.setLanguage(lang);
		}
		catch(LanguageException e) {
			try {
				// fallback language
				LanguageContainer.setLanguage("ger_du");
			}
			catch(LanguageException ex) {
				ex.printStackTrace();
				return;
			}
		}
		
		setEnabledTags();
		setEnabledFonts();
		
		String enableSmileys = (String)params.get("smileyCount");
		Settings.ENABLE_SMILEYS = false;
		if(enableSmileys != null) {
			try {
				Settings.ENABLE_SMILEYS = Integer.parseInt(enableSmileys) > 0;
			}
			catch(NumberFormatException e) {
				
			}
		}
		
		String displayCodeLineNumbers = (String)params.get("displayCodeLineNumbers");
		if(displayCodeLineNumbers != null) {
			try {
				Settings.DISPLAY_CODE_LINE_NUMBERS = Integer.parseInt(displayCodeLineNumbers) == 1;
			}
			catch(NumberFormatException e) {
				
			}
		}
		
		String wordWrapPos = (String)params.get("maxLineLength");
		if(wordWrapPos != null) {
			try {
				Settings.WORD_WRAP_POSITION = Integer.parseInt(wordWrapPos);
			}
			catch(NumberFormatException e) {
				
			}
		}
		
		CustomHighlighters.init(Helper.getFileInDocumentBase("./highlighter/"),"languages.xml");
	  
		_basePanel = new JPanel(new BorderLayout());
		String bgColor = (String)params.get("bgColor");
		if(bgColor != null)
			_basePanel.setBackground(ColorFunctions.getColorFromString(bgColor,Color.WHITE));
		else
			_basePanel.setBackground(Color.WHITE);
		add(_basePanel);
		
		_tabPane = new JTabbedPane();
		_tabPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(_sourcePanel == null || _editorPanel == null)
					return;
				
				switch(_tabPane.getSelectedIndex()) {
					case 0:
						_editorPanel.setText(_sourcePanel.getText());
						break;
					
					case 1:
						_sourcePanel.setText(_editorPanel.getText());
						break;
				}
			}
		});
		
		_editorPanel = new EditorTab(this);
		_tabPane.addTab(LanguageContainer.getText(Language.GUI_TAB_EDITOR_TITLE),_editorPanel);
	  
		_sourcePanel = new SourceTab();
		_tabPane.addTab(LanguageContainer.getText(Language.GUI_TAB_SOURCE_TITLE),_sourcePanel);
	  
		_basePanel.add(_tabPane,BorderLayout.CENTER);
		
		setVisible(true);
		
		String text = (String)params.get("text");
		if(text != null && text.length() > 0) {
			text = StringUtils.simpleReplace(text,"%n%","\n");
			final String tbbcode = StringUtils.simpleReplace(text,"%t%","\t");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					_editorPanel.setText(tbbcode);
				}
			});
		}
	}
	
	/**
	 * @return the value of the parameter <name>
	 */
	public String getParameter(String name) {
		return (String)_params.get(name);
	}
	
	/**
	 * @return the editor tab
	 */
	public EditorTab getEditorTab() {
		return _editorPanel;
	}
	
	/**
	 * @return the source tab
	 */
	public SourceTab getSourceTab() {
		return _sourcePanel;
	}
	
	/**
	 * @return the BBCode of the editor
	 */
	public String getBBCode() {
		if(_tabPane.getSelectedIndex() == 0)
			return _editorPanel.getText();
		
		return _sourcePanel.getText();
	}

	/**
	 * switches to the source-tab and sets the given string as text
	 * 
	 * @param code the text to display
	 */
	public void switchToSource(String code) {
		_tabPane.setSelectedIndex(1);
		_sourcePanel.setText(_editorPanel.getText() + code);
	}
	
	/**
	 * Enables the fonts given by the parameter "fontFamilies"
	 */
	private void setEnabledFonts() {
		String enabledFonts = (String)_params.get("fontFamilies");
		if(enabledFonts == null)
			enabledFonts = "Verdana,Helvetica,Courier New,Arial";

		Settings.ENABLED_FONTS.clear();
		String[] fonts = enabledFonts.split(",");
		for(int i = 0;i < fonts.length;i++)
			Settings.ENABLED_FONTS.add(fonts[i].trim());
	}
	
	/**
	 * enables the tags given by the parameter "enabledTags"
	 */
	private void setEnabledTags() {
		String enabledTags = (String)_params.get("enabledTags");
		if(enabledTags == null)
			return;
		
		enabledTags = enabledTags.toLowerCase();
		Settings.ENABLED_EXTRA_TAGS.clear();
		
		List lTags = new ArrayList();
		String[] tags = enabledTags.split(",");
		for(int i = 0;i < tags.length;i++) {
			int id = BBCodeTags.getIdFromTag(tags[i]);
			if(BBCodeTags.isValidTag(id))
				lTags.add(new Integer(id));
			else if(tags[i] != null && tags[i].length() > 0)
				Settings.ENABLED_EXTRA_TAGS.add(tags[i]);
		}
		
		Settings.ENABLED_TAGS = lTags;
	}
}
