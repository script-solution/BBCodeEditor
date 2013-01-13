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


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;

/**
 * The menu-panel for the source-tab
 * 
 * @author hrniels
 */
public class SourceMenuPanel extends JPanel {

	private static final long serialVersionUID = -8061693640347728317L;

	/**
	 * The textarea
	 */
	private final JTextArea _textArea;

	/**
	 * The undomanager
	 */
	private final UndoManager _undo = new UndoManager();

	/**
	 * All available actions of the textArea
	 */
	private Map _actions;

	private JButton _btnNew;
	private JButton _btnUndo;
	private JButton _btnRedo;
	private JButton _btnCut;
	private JButton _btnCopy;
	private JButton _btnPaste;

	/**
	 * constructor
	 * 
	 * @param textArea the textArea-instance
	 */
	public SourceMenuPanel(JTextArea textArea) {
		super(new FlowLayout(FlowLayout.LEFT,5,2));

		_textArea = textArea;
		Document doc = _textArea.getDocument();
		doc.addUndoableEditListener(new MyUndoableEditListener());

		createActionTable();
		
		_textArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				boolean select = e.getDot() != e.getMark();
				_btnCut.setEnabled(select);
				_btnCopy.setEnabled(select);
			}
		});
		
		initLayout();
	}

	/**
	 * inits all components
	 */
	private void initLayout() {
		_btnNew = new JButton(new ImageIcon(Helper.getFileInDocumentBase("./images/new.png")));
		_btnNew.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_NEW_TOOLTIP));
		_btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textArea.selectAll();
				_textArea.replaceSelection("");
			}
		});
		_btnNew.setPreferredSize(Settings.BUTTON_SIZE);
		add(_btnNew);

		add(Helper.getSeparator());

		_btnUndo = new JButton(new ImageIcon(Helper.getFileInDocumentBase("./images/undo.png")));
		_btnUndo.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_UNDO_TOOLTIP));
		_btnUndo.setPreferredSize(Settings.BUTTON_SIZE);
		_btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_undo.canUndo())
					_undo.undo();

				_btnUndo.setEnabled(_undo.canUndo());
				_btnRedo.setEnabled(_undo.canRedo());
			}
		});
		add(_btnUndo);

		_btnRedo = new JButton(new ImageIcon(Helper.getFileInDocumentBase("./images/redo.png")));
		_btnRedo.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_REDO_TOOLTIP));
		_btnRedo.setPreferredSize(Settings.BUTTON_SIZE);
		_btnRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(_undo.canRedo())
					_undo.redo();

				_btnUndo.setEnabled(_undo.canUndo());
				_btnRedo.setEnabled(_undo.canRedo());
			}
		});
		add(_btnRedo);

		add(Helper.getSeparator());

		_btnCut = new JButton(getActionByName(DefaultEditorKit.cutAction));
		_btnCut.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_CUT_TOOLTIP));
		_btnCut.setIcon(new ImageIcon(Helper.getFileInDocumentBase("./images/cut.png")));
		_btnCut.setText("");
		_btnCut.setPreferredSize(Settings.BUTTON_SIZE);
		add(_btnCut);

		_btnCopy = new JButton(getActionByName(DefaultEditorKit.copyAction));
		_btnCopy.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_COPY_TOOLTIP));
		_btnCopy.setIcon(new ImageIcon(Helper.getFileInDocumentBase("./images/copy.png")));
		_btnCopy.setText("");
		_btnCopy.setPreferredSize(Settings.BUTTON_SIZE);
		add(_btnCopy);

		_btnPaste = new JButton(getActionByName(DefaultEditorKit.pasteAction));
		_btnPaste.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_PASTE_TOOLTIP));
		_btnPaste.setIcon(new ImageIcon(Helper.getFileInDocumentBase("./images/paste.png")));
		_btnPaste.setText("");
		_btnPaste.setPreferredSize(Settings.BUTTON_SIZE);
		add(_btnPaste);
	}

	/**
	 * The following two methods allow us to find an 
	 * action provided by the editor kit by its name.
	 */
	private void createActionTable() {
		_actions = new HashMap();
		Action[] actionsArray = _textArea.getActions();
		for(int i = 0;i < actionsArray.length;i++) {
			Action a = actionsArray[i];
			_actions.put(a.getValue(Action.NAME),a);
		}
	}

	/**
	 * determines the action from the given name
	 * 
	 * @param name the name of the action
	 * @return the action
	 */
	private Action getActionByName(String name) {
		return (Action)_actions.get(name);
	}

	/**
	 * This one listens for edits that can be undone.
	 * 
	 * @author hrniels
	 */
	private final class MyUndoableEditListener implements UndoableEditListener {

		public void undoableEditHappened(UndoableEditEvent e) {
			_undo.addEdit(e.getEdit());

			_btnUndo.setEnabled(_undo.canUndo());
			_btnRedo.setEnabled(_undo.canRedo());
		}
	}
}