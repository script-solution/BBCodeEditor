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

import java.util.Map;
import java.util.HashMap;
import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * the main-class of the applet
 */
public class WysiwygBBCodeEditor extends JApplet {

	private static final long serialVersionUID = -6301848975640103299L;
	
	private MainPanel _mainPanel;
	
	/**
	 * constructor
	 */
	public void init() {
		Map params = new HashMap();
		params.put("lookAndFeel",getParameter("lookAndFeel"));
		params.put("path",getParameter("path"));
		params.put("lang",getParameter("lang"));
		params.put("smileyCount",getParameter("smileyCount"));
		params.put("displayCodeLineNumbers",getParameter("displayCodeLineNumbers"));
		params.put("maxLineLength",getParameter("maxLineLength"));
		params.put("bgColor",getParameter("bgColor"));
		params.put("text",getParameter("text"));
		params.put("fontFamilies",getParameter("fontFamilies"));
		params.put("enabledTags",getParameter("enabledTags"));
		int count = Integer.parseInt(getParameter("smileyCount"));
		params.put("smileyCount",getParameter("smileyCount"));
		for(int i = 1; i <= count; i++) {
			params.put("smileyPrimCode" + i,getParameter("smileyPrimCode" + i));
			params.put("smileySecCode" + i,getParameter("smileySecCode" + i));
			params.put("smileyPath" + i,getParameter("smileyPath" + i));
		}
		
		String path = (String)params.get("path");
		if(path == null)
			path = "";
		Settings.setBaseURL(getCodeBase(),path);
		
		_mainPanel = new MainPanel(params);
		getContentPane().add(_mainPanel);
	}
	
	/**
	 * inserts the given text at cursor-position
	 * 
	 * @param text the text to insert
	 * @return true if successfull
	 */
	public boolean insertText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_mainPanel.getEditorTab().insertText(text);
			}
		});
		return true;
	}
	
	/**
	 * @return the BBCode of the editor
	 */
	public String getBBCode() {
		return _mainPanel.getBBCode();
	}

	/**
	 * switches to the source-tab and sets the given string as text
	 * 
	 * @param code the text to display
	 */
	public void switchToSource(String code) {
		_mainPanel.switchToSource(code);
	}
	
	public String toString() {
		return getBBCode();
	}
}
