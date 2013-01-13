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
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The source-tab
 * 
 * @author hrniels
 */
public class SourceTab extends JPanel implements TextAreaContainer {

	private static final long serialVersionUID = 1358455352069156144L;
	
	private JPanel _menuPanel;

	/**
	 * The textarea
	 */
	private JTextArea _textArea;

  /**
   * constructor
   */
	public SourceTab() {
		super(new BorderLayout());
		
		_textArea = new JTextArea();
		_textArea.setMargin(new Insets(5,5,5,5));
		_textArea.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(_textArea);
		add(scrollPane,BorderLayout.CENTER);

		_menuPanel = new SourceMenuPanel(_textArea);
		add(_menuPanel,BorderLayout.NORTH);
	}
	
	public String getText() {
		return _textArea.getText();
	}
	
	public void insertText(String text) {
		// do nothing
	}
	
	public void setText(String text) {
		_textArea.setText(text);
		_textArea.requestFocus();
	}
}