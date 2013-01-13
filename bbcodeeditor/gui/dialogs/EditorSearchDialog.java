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
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.tools.TextPart;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * The search-dialog for the editor
 * 
 * @author hrniels
 */
public class EditorSearchDialog extends JDialog {

	private static final long serialVersionUID = -2068502173810131203L;
	
	private final JPanel _basePanel;
	private final JPanel _buttonPanel;
	private final JPanel _contentPanel;
	private final JPanel _bottomPanel;
	
	private final JLabel _findLabel;
	private final JTextField _find;
	private final JLabel _replaceLabel;
	private final JTextField _replace;
	private final JLabel _scopeLabel;
	private final ButtonGroup _scopeGroup;
	private final JPanel _scopePanel;
	private final JRadioButton _scopeAll;
	private final JRadioButton _scopeSel;
	private final JLabel _caseSensitiveLabel;
	private final JCheckBox _caseSensitive;
	private final JLabel _directionLabel;
	private final ButtonGroup _dirGroup;
	private final JPanel _dirPanel;
	private final JRadioButton _dirBackwards;
	private final JRadioButton _dirForward;
	private final JLabel _wrapSearchLabel;
	private final JCheckBox _wrapSearch;
	private final JLabel _parseBBCodeLabel;
	private final JCheckBox _parseBBCode;
	
	private final JButton _cancelBtn;
	private final JButton _findBtn;
	private final JButton _replaceFindBtn;
	private final JButton _replaceAllBtn;
	private final JLabel _resultsLabel;
	
	/**
	 * The textfield-instance
	 */
	private final AbstractTextField _textField;
	
	/**
	 * The search-status
	 */
	private final SearchStatus _status;
	
	/**
	 * The component to use for the location
	 */
	private final Component _comp;

	/**
	 * Constructor
	 * 
	 * @param textField the textfield
	 * @param owner the owner-frame
	 * @param comp the component
	 */
	public EditorSearchDialog(AbstractTextField textField,JFrame owner,Component comp) {
		super(owner);
		
		_textField = textField;
		_comp = comp;
		
		_basePanel = new JPanel();
		_buttonPanel = new JPanel();
		_contentPanel = new JPanel();
		_bottomPanel = new JPanel();
		_findLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_SEARCH) + ":");
		_find = new JTextField();
		_replaceLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_REPLACE) + ":");
		_replace = new JTextField();
		_scopeLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_SCOPE) + ":");
		_scopeGroup = new ButtonGroup();
		_scopePanel = new JPanel();
		_scopeAll = new JRadioButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_ALL));
		_scopeSel = new JRadioButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_SELECTION));
		_caseSensitive = new JCheckBox();
		_caseSensitiveLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_CASESENSITIVE) + ":");
		_directionLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_DIRECTION) + ":");
		_dirGroup = new ButtonGroup();
		_dirPanel = new JPanel();
		_dirBackwards = new JRadioButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_BACKWARDS));
		_dirForward = new JRadioButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_FORWARD));
		_cancelBtn = new JButton(LanguageContainer.getText(Language.GUI_DIALOG_CANCEL));
		_findBtn = new JButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_SEARCH));
		_replaceFindBtn = new JButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_REPLACEFIND));
		_replaceAllBtn = new JButton(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_REPLACEALL));
		_wrapSearchLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_WRAPSEARCH) + ":");
		_wrapSearch = new JCheckBox();
		_resultsLabel = new JLabel();
		_parseBBCodeLabel = new JLabel(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_PARSEBBCODE) + ":");
		_parseBBCode = new JCheckBox();
		
		_status = new SearchStatus(_textField);
		
		initComponents();
	}
	
	/**
	 * Inits all components
	 */
	private void initComponents() {
		setTitle(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_TITLE));
		setResizable(false);
		
		_basePanel.setLayout(new BorderLayout());
		_basePanel.setBorder(new EmptyBorder(5,5,5,0));
		
		GridBagConstraints gcLeft = new GridBagConstraints();
		gcLeft.gridx = 1;
		gcLeft.gridy = 1;
		gcLeft.ipadx = 5;
		gcLeft.ipady = 2;
		gcLeft.anchor = GridBagConstraints.LINE_START;
		
		GridBagConstraints gcRight = new GridBagConstraints();
		gcRight.gridx = 2;
		gcRight.gridy = 1;
		gcRight.ipadx = 5;
		gcRight.ipady = 2;
		gcRight.fill = GridBagConstraints.HORIZONTAL;
		
		_contentPanel.setLayout(new GridBagLayout());
		
		// Find
		_contentPanel.add(_findLabel,gcLeft);
		_find.requestFocusInWindow();
		_find.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				// start search?
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					_findBtn.doClick();
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					_cancelBtn.doClick();
				
				super.keyReleased(e);
			}
		});
		_find.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				resetSearch();
			}
		
			public void insertUpdate(DocumentEvent e) {
				resetSearch();
			}
		
			public void changedUpdate(DocumentEvent e) {
				resetSearch();
			}
		});
		_find.setPreferredSize(new Dimension(300,_find.getPreferredSize().height));
		_contentPanel.add(_find,gcRight);
		
		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// Replace
		_contentPanel.add(_replaceLabel,gcLeft);
		
		_replace.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					_cancelBtn.doClick();
				
				super.keyReleased(e);
			}
		});
		_contentPanel.add(_replace,gcRight);

		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// Scope
		_contentPanel.add(_scopeLabel,gcLeft);
		
		_scopeGroup.add(_scopeAll);
		_scopeGroup.add(_scopeSel);
		if(_textField.getSelection().isEmpty())
			_scopeAll.setSelected(true);
		else
			_scopeSel.setSelected(true);
		
		_scopeAll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				resetSearch();
			}
		});
		_scopeSel.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				resetSearch();
			}
		});
		
		_scopePanel.setLayout(new GridLayout(1,2));
		_scopePanel.add(_scopeAll);
		_scopePanel.add(_scopeSel);
		_contentPanel.add(_scopePanel,gcRight);

		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// Direction
		_contentPanel.add(_directionLabel,gcLeft);
		
		_dirGroup.add(_dirBackwards);
		_dirGroup.add(_dirForward);
		_dirForward.setSelected(true);
		
		_dirPanel.setLayout(new GridLayout(1,2));
		_dirPanel.add(_dirBackwards);
		_dirPanel.add(_dirForward);
		_contentPanel.add(_dirPanel,gcRight);

		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// Case sensitive
		_contentPanel.add(_caseSensitiveLabel,gcLeft);

		_caseSensitive.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				resetSearch();
			}
		});
		_contentPanel.add(_caseSensitive,gcRight);

		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// Wrap search
		_contentPanel.add(_wrapSearchLabel,gcLeft);
		_contentPanel.add(_wrapSearch,gcRight);

		gcLeft.gridy += 1;
		gcRight.gridy += 1;
		
		// parse BBCode
		_contentPanel.add(_parseBBCodeLabel,gcLeft);
		_contentPanel.add(_parseBBCode,gcRight);
		
		_basePanel.add(_contentPanel,BorderLayout.CENTER);
		
		_bottomPanel.setLayout(new BorderLayout());
		_resultsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		_resultsLabel.setPreferredSize(new Dimension(100,20));
		_bottomPanel.add(_resultsLabel,BorderLayout.NORTH);
		
		_buttonPanel.setLayout(new GridLayout(2,2,3,3));
		_buttonPanel.setBorder(new EmptyBorder(5,0,0,0));
		
		_replaceFindBtn.setEnabled(false);
		_replaceFindBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = _find.getText();
				String repl = _replace.getText();
				boolean caseSensitive = _caseSensitive.isSelected();
				boolean forward = _dirForward.isSelected();
				boolean scopeIsAll = _scopeAll.isSelected();
				
				_status.initSearch(text,caseSensitive,forward,scopeIsAll);

				// determine scope
				int[] pos = _status.getScope();
				// we have to use the other position to ensure that we replace the text
				// we've already found the last time..
				if(_status.getLastMatch() != null) {
					if(forward)
						pos[0] = _status.getLastMatch().startPos;
					else
						pos[1] = _status.getLastMatch().endPos;
				}
				
				// replace text
				int replPos;
				if(forward)
					replPos = _textField.replaceFirst(text,repl,pos[0],pos[1],
							caseSensitive,_parseBBCode.isSelected());
				else
					replPos = _textField.replaceLast(text,repl,pos[0],pos[1],
							caseSensitive,_parseBBCode.isSelected());
				
				// mark results and search the next one
				if(replPos >= 0)
					_status.finishSearch(new TextPart("",replPos,replPos + repl.length()));
				else
					_status.finishSearch(null);

				searchNext();
			}
		});
		_buttonPanel.add(_replaceFindBtn);
		
		_replaceAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = _find.getText();
				if(text.length() == 0)
					return;
				
				String repl = _replace.getText();
				boolean caseSensitive = _caseSensitive.isSelected();
				boolean scopeIsAll = _scopeAll.isSelected();
				
				_status.reset();
				_status.initSearch(text,caseSensitive,true,scopeIsAll);

				int[] pos = _status.getScope();
				_textField.clearSelection();
				_textField.clearHighlighting();
				if(pos[1] > pos[0]) {
					_textField.replaceAll(text,repl,pos[0],pos[1],
							caseSensitive,_parseBBCode.isSelected());
				}
				
				// ensure that we restart the search
				_status.reset();
			}
		});
		_buttonPanel.add(_replaceAllBtn);
		
		_findBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchNext();
			}
		});
		_buttonPanel.add(_findBtn);

		_cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_textField.clearHighlighting();
				setVisible(false);
			}
		});
		_buttonPanel.add(_cancelBtn);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(_buttonPanel,BorderLayout.EAST);
		_bottomPanel.add(panel,BorderLayout.CENTER);
		
		_basePanel.add(_bottomPanel,BorderLayout.SOUTH);
		
		add(_basePanel);
		
		pack();
		setLocationRelativeTo(_comp);
	}
	
	/**
	 * Resets the search
	 */
	private void resetSearch() {
		_status.reset();
		_replaceFindBtn.setEnabled(false);
		_textField.clearHighlighting();
		_resultsLabel.setText(" ");
	}
	
	/**
	 * Reports the number of matches
	 * 
	 * @param pos the current position
	 * @param number the number
	 */
	private void reportMatches(int pos,int number) {
		if(number == 0)
			_resultsLabel.setText(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_NOMATCHES));
		else {
			String matches = String.format(LanguageContainer.getText(Language.GUI_DIALOG_SEARCH_XMATCHES),
					new Object[] {new Integer(pos),new Integer(number)});
			_resultsLabel.setText(matches);
		}
	}
	
	/**
	 * Searches the next occurrence. If the search isn't initialized or has to be
	 * restarted it highlights all results
	 */
	private void searchNext() {
		String text = _find.getText();
		boolean caseSensitive = _caseSensitive.isSelected();
		boolean forward = _dirForward.isSelected();
		boolean scopeIsAll = _scopeAll.isSelected();

		_status.initSearch(text,caseSensitive,forward,scopeIsAll);
		int[] pos = _status.getScope();
		if(text.length() == 0 || _textField.length() == 0 || pos == null) {
			_textField.clearHighlighting();
			reportMatches(0,0);
			return;
		}
		
		// do we have to (re)start the search and highlight all results?
		if(_status.shouldRestart()) {
			_textField.clearHighlighting();
			List res = _textField.getAllOccurrences(text,pos[0],pos[1],caseSensitive);
			
			// are there matches?
			if(res.size() > 0) {
				_textField.highlightRegions(res,Color.yellow);
				
				TextPart first = forward ? (TextPart)res.get(0) : (TextPart)res.get(res.size() - 1);
				_status.finishSearch(first);
				_textField.goToPosition(first.startPos);
				_textField.highlightRegions(Arrays.asList(new TextPart[] {first}),Color.ORANGE);
				
				// now we can use replace&find
				_replaceFindBtn.setEnabled(true);
			}
			else
				resetSearch();
			
			_status.reportStart(res.size());
			reportMatches(_status.getPosition(),_status.getMatches());
		}
		// otherwise search for the next occurrence
		else {
			TextPart part;
			if(forward)
				part = _textField.getFirstOccurrence(text,pos[0],pos[1],caseSensitive);
			else
				part = _textField.getLastOccurrence(text,pos[0],pos[1],caseSensitive);
			
			TextPart last = _status.getLastMatch();
			if(last != null)
				_textField.highlightRegions(Arrays.asList(new TextPart[] {last}),Color.YELLOW);
			
			// go to the result
			if(part != null) {
				_status.finishSearch(part);
				_textField.highlightRegions(Arrays.asList(new TextPart[] {part}),Color.ORANGE);
				_textField.goToPosition(part.startPos);
				
				reportMatches(_status.getPosition(),_status.getMatches());
			}
			// wrap search?
			else if(_wrapSearch.isSelected()) {
				_status.reset();
				searchNext();
			}
			else
				resetSearch();
		}
	}
}
