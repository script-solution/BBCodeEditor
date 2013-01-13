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

package bbcodeeditor.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import bbcodeeditor.control.*;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The dialog to display some notices, tipps and tricks
 * 
 * @author hrniels
 */
public class HelpDialog extends JDialog {

	private static final long serialVersionUID = -2990575580174049102L;

	/**
	 * The used textfield
	 */
	private BBCTextField _textPane;
	
	/**
	 * The bottom panel
	 */
	private JPanel _bottomPanel;
	
	/**
	 * The close-button
	 */
	private JButton _btnClose;
	
	/**
	 * Constructor
	 * 
	 * @param comp the component to use for the location of the dialog
	 * @param parent the parent-Frame
	 */
	public HelpDialog(Component comp,Frame parent) {
		super(parent);
		
		_textPane = new BBCTextField(null);
		
		IWordWrap wrapStyle = new WordWrapPixelBased(_textPane);
		_textPane.setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_ROOT,wrapStyle);
		_textPane.setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_LIST,wrapStyle);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_textPane.setText(LanguageContainer.getText(Language.GUI_DIALOG_HELP_TEXT));
				_textPane.setReadOnly(true);
				_textPane.goToVeryBeginning(false);
			}
		});
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(_textPane),BorderLayout.CENTER);
		
		_bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,2));
		_btnClose = new JButton(LanguageContainer.getText(Language.GUI_DIALOG_HELP_CLOSE));
		_btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		_bottomPanel.add(_btnClose);
		getContentPane().add(_bottomPanel,BorderLayout.SOUTH);

		setTitle(LanguageContainer.getText(Language.GUI_DIALOG_HELP_TITLE));
		setSize(new Dimension(640,480));
		setLocationRelativeTo(comp);
		setModal(true);
	}
}