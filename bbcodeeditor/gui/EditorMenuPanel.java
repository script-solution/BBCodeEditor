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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import bbcodeeditor.control.*;
import bbcodeeditor.control.events.*;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.tools.MutableBoolean;
import bbcodeeditor.gui.combobox.ButtonComboBox;
import bbcodeeditor.gui.combobox.CellContent;
import bbcodeeditor.gui.combobox.ItemSelectedListener;
import bbcodeeditor.gui.dialogs.HelpDialog;
import bbcodeeditor.gui.extra.*;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;
import bbcodeeditor.gui.listener.*;


/**
 * The menu-panel for the editor-tab
 * 
 * @author hrniels
 */
public class EditorMenuPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -3693585839811292917L;

	private final BBCTextField _textArea;
	
	private JPanel _topPanel;
	private JPanel _topLeftPanel;
	private JPanel _topRightPanel;
	private JPanel _bottomPanel;
	
	private JButton _btnHelp;

	private JButton _btnNew;
	private JButton _btnUndo;
	private JButton _btnRedo;
	private JButton _btnCut;
	private JButton _btnCopy;
	private JButton _btnPaste;
	private JButton _btnSearchNReplace;
	
	private JToggleButton _btnBold;
	private JToggleButton _btnItalic;
	private JToggleButton _btnUnderline;
	private JToggleButton _btnStrike;
	
	private AlignButton _btnAlignLeft;
	private AlignButton _btnAlignCenter;
	private AlignButton _btnAlignRight;
	
	private JToggleButton _btnSupScript;
	private JToggleButton _btnSubScript;
	
	private QuoteButton _btnQuote;
	private CodeButton _btnCode;
	private ButtonComboBox _btnList;
	
	private FontFamilyCombo _cBFontFamily;
	private FontSizeCombo _cBFontSize;
	private ColorButton _btnFontColor;
	private ColorButton _btnBgColor;
	
	private URLButton _btnURL;
	private EmailButton _btnEmail;
	private ImageButton _btnImage;
	private ButtonComboBox _btnSmiley;

	private Timer _attrTimer;
	private Timer _selTimer;
	private ContentSection _lastSec;
	private MutableBoolean _invokeChangeEvent = new MutableBoolean(true);
	
	/**
	 * constructor
	 * 
	 * @param textArea the textArea-instance
	 */
	public EditorMenuPanel(BBCTextField textArea) {
		super(new GridLayout(1,0));
		
		_textArea = textArea;
		
		initLayout();
	}
	
	/**
	 * inits all components
	 */
	private void initLayout() {
		_topPanel = new JPanel(new BorderLayout());
		add(new ScrollableBar(_topPanel));
		
		_topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,2));
		_topPanel.add(_topLeftPanel,BorderLayout.CENTER);
		
		_topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,2));
		
		_btnHelp = new JButton(new ImageIcon(Helper.getFileInDocumentBase("./images/help.png")));
		_btnHelp.setPreferredSize(Settings.BUTTON_SIZE);
		_btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HelpDialog hd = new HelpDialog(Settings.DIALOG_COMPONENT,null);
				hd.setVisible(true);
			}
		});
		_topRightPanel.add(_btnHelp);
		
		_topPanel.add(_topRightPanel,BorderLayout.EAST);
		
		_bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,2));
		
		_textArea.addHistoryChangedListener(new HistoryChangedListener() {
			public void historyChanged(int undoLen,int redoLen) {
				_btnUndo.setEnabled(undoLen > 0);
				_btnRedo.setEnabled(redoLen > 0);
			}
		});
		
		_textArea.addSelectionChangedListener(new SelectionChangedListener() {
			public void selectionChanged(boolean empty,int start,int end,int direction) {
				if(_selTimer != null)
					_selTimer.stop();
					
				_selTimer = new Timer(50,new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Selection sel = _textArea.getSelection();
						boolean isEmpty = sel.isEmpty();
						_btnCut.setEnabled(!isEmpty);
						_btnCopy.setEnabled(!isEmpty);
					}
				});
				_selTimer.setRepeats(false);
				_selTimer.start();
			}
		});
		
    _textArea.addCaretPositionChangedListener(new CaretPositionChangedListener() {
			public void caretPositionChanged(CaretPositionChangedEvent e) {
				if(e == null)
					return;
				
				_lastSec = e.getNewSection();
				
				if(_attrTimer != null)
					_attrTimer.stop();
					
				_attrTimer = new Timer(200,EditorMenuPanel.this);
				_attrTimer.setRepeats(false);
				_attrTimer.start();
			}
		});
    
    _textArea.addAttributesChangedListener(new AttributesChangedListener() {
			public void attributesChanged() {
				if(_attrTimer != null)
					_attrTimer.stop();
					
				_attrTimer = new Timer(200,EditorMenuPanel.this);
				_attrTimer.setRepeats(false);
				_attrTimer.start();
			}
		});
    
    _btnNew = new NewButton(_textArea);
    _btnNew.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnNew);
	  
    addSeparator(_topLeftPanel);
    
    _btnUndo = new UndoButton(_textArea);
    _btnUndo.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnUndo);
    
    _btnRedo = new RedoButton(_textArea);
    _btnRedo.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnRedo);
	  
    addSeparator(_topLeftPanel);
    
    _btnCut = new CutButton(_textArea);
    _btnCut.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnCut);
    
    _btnCopy = new CopyButton(_textArea);
    _btnCopy.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnCopy);
    
    _btnPaste = new PasteButton(_textArea);
    _btnPaste.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnPaste);
    
    _btnSearchNReplace = new SearchNReplaceButton(_textArea);
    _btnSearchNReplace.setPreferredSize(Settings.BUTTON_SIZE);
    _topLeftPanel.add(_btnSearchNReplace);
    
    boolean enableQuote = Settings.isTagEnabled(BBCodeTags.QUOTE);
    boolean enableCode = Settings.isTagEnabled(BBCodeTags.CODE);
    boolean enableList = Settings.isTagEnabled(BBCodeTags.LIST);
    
    if(enableQuote || enableCode || enableList)
    	addSeparator(_topLeftPanel);
    
    if(enableQuote) {
	    _btnQuote = new QuoteButton(_textArea,Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnQuote);
    }
    
    if(enableCode) {
	    _btnCode = new CodeButton(_textArea,Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnCode);
    }
    
    if(enableList) {
	    _btnList = new ListButton(_textArea,Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnList);
    }
    
    
    boolean enableURL = Settings.isTagEnabled(BBCodeTags.URL);
    boolean enableEmail = Settings.isTagEnabled(BBCodeTags.EMAIL);
    boolean enableImage = Settings.isTagEnabled(BBCodeTags.IMAGE);
    
    if(enableURL || enableEmail || enableImage)
    	addSeparator(_topLeftPanel);
    
    if(enableURL) {
	    _btnURL = new URLButton(_textArea);
	    _btnURL.setPreferredSize(Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnURL);
    }
    
    if(enableEmail) {
	    _btnEmail = new EmailButton(_textArea);
	    _btnEmail.setPreferredSize(Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnEmail);
    }
    
    if(enableImage) {
	    _btnImage = new ImageButton(_textArea);
	    _btnImage.setPreferredSize(Settings.BUTTON_SIZE);
	    _topLeftPanel.add(_btnImage);
    }
    
    // add extra-tag-combo
    ExtraTagParser etp = new ExtraTagParser(Helper.getFileInDocumentBase("./extra_tags.xml"));
    Map extraButtons = etp.getExtraButtons();
    if(extraButtons.size() > 0) {
    	boolean addedExtraSep = false;
    	Iterator it = extraButtons.entrySet().iterator();
    	while(it.hasNext()) {
    		ExtraButton btn = (ExtraButton)((Entry)it.next()).getValue();
    		
    		List items = btn.getItems();
	    	CellContent[] aExtraTags = new CellContent[items.size()];
	    	for(int i = 0;i < aExtraTags.length;i++) {
	    		ExtraTag tag = (ExtraTag)items.get(i);
	    		aExtraTags[i] = new CellContent(tag,LanguageContainer.getText(tag.getComboName()));
	    	}
	    	
	    	if(aExtraTags.length > 0) {
	    		if(!addedExtraSep) {
	    			addSeparator(_topLeftPanel);
	    			addedExtraSep = true;
	    		}
	    		
	    		// if the button has just one element we want to display a button
	    		// not a combobox
	    		if(btn.getRealItemCount() == 1) {
	    			ExtraTag tag = (ExtraTag)aExtraTags[0].getKey();
	    			ExtraTagButton eBtn = new ExtraTagButton(_textArea,tag,btn.getImage(),btn.getTooltip());
	    			eBtn.setPreferredSize(Settings.BUTTON_SIZE);
	    			_topLeftPanel.add(eBtn);
	    		}
	    		else {
			    	ExtraTagCombo combo = new ExtraTagCombo(_textArea,Settings.BUTTON_SIZE,aExtraTags,
			    			btn.getImage(),btn.getTooltip());
			    	_topLeftPanel.add(combo);
	    		}
	    	}
    	}
    }

    boolean enableFontFamily = Settings.isTagEnabled(BBCodeTags.FONT_FAMILY);
    boolean enableFontSize = Settings.isTagEnabled(BBCodeTags.FONT_SIZE);

    if(enableFontFamily) {
	    _cBFontFamily = new FontFamilyCombo(Settings.ENABLED_FONTS,_textArea,_invokeChangeEvent);
	    _bottomPanel.add(_cBFontFamily);
    }

    if(enableFontSize) {
		  _cBFontSize = new FontSizeCombo(_textArea,_invokeChangeEvent);
		  _bottomPanel.add(_cBFontSize);
    }

    boolean enableBold = Settings.isTagEnabled(BBCodeTags.BOLD);
    boolean enableItalic = Settings.isTagEnabled(BBCodeTags.ITALIC);
    boolean enableUnderline = Settings.isTagEnabled(BBCodeTags.UNDERLINE);
    boolean enableStrike = Settings.isTagEnabled(BBCodeTags.STRIKE);
    boolean enableSub = Settings.isTagEnabled(BBCodeTags.SUBSCRIPT);
    boolean enableSup = Settings.isTagEnabled(BBCodeTags.SUPERSCRIPT);
    
    if(_bottomPanel.getComponentCount() > 0 &&
    		(enableBold || enableItalic || enableUnderline || enableStrike || enableSub || enableSup))
    	addSeparator(_bottomPanel);
    
    if(enableBold) {
	    _btnBold = new ToggleAttrButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/bold.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_BOLD_TOOLTIP),
	    		TextAttributes.BOLD);
	    _btnBold.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnBold);
    }

    if(enableItalic) {
	    _btnItalic = new ToggleAttrButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/italic.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_ITALIC_TOOLTIP),
	    		TextAttributes.ITALIC);
	    _btnItalic.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnItalic);
    }

    if(enableUnderline) {
	    _btnUnderline = new ToggleAttrButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/underline.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_UNDERLINE_TOOLTIP),
	    		TextAttributes.UNDERLINE);
	    _btnUnderline.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnUnderline);
    }

    if(enableStrike) {
	    _btnStrike = new ToggleAttrButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/strike.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_STRIKE_TOOLTIP),
	    		TextAttributes.STRIKE);
	    _btnStrike.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnStrike);
    }
    
    if(enableSub) {
	    _btnSubScript = new PositionButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/subscript.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_SUBSCRIPT_TOOLTIP),new Byte(TextAttributes.POS_SUBSCRIPT));
	    _btnSubScript.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnSubScript);
    }

    if(enableSup) {
    	_btnSupScript = new PositionButton(_textArea,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/supscript.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_SUPSCRIPT_TOOLTIP),new Byte(TextAttributes.POS_SUPERSCRIPT));
	    _btnSupScript.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnSupScript);
    }
    

    boolean enableLeft = Settings.isTagEnabled(BBCodeTags.LEFT);
    boolean enableCenter = Settings.isTagEnabled(BBCodeTags.CENTER);
    boolean enableRight = Settings.isTagEnabled(BBCodeTags.RIGHT);

    if(_bottomPanel.getComponentCount() > 0 && (enableLeft || enableCenter || enableRight))
    	addSeparator(_bottomPanel);

    if(enableLeft) {
    	_btnAlignLeft = new AlignButton(_textArea,ParagraphAttributes.ALIGN_LEFT,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/left.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_ALIGN_LEFT_TOOLTIP));
	    _btnAlignLeft.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnAlignLeft);
    }

    if(enableCenter) {
	    _btnAlignCenter = new AlignButton(_textArea,ParagraphAttributes.ALIGN_CENTER,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/center.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_ALIGN_CENTER_TOOLTIP));
	    _btnAlignCenter.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnAlignCenter);
    }

    if(enableRight) {
	    _btnAlignRight = new AlignButton(_textArea,ParagraphAttributes.ALIGN_RIGHT,
	    		new ImageIcon(Helper.getFileInDocumentBase("./images/right.png")),
	    		LanguageContainer.getText(Language.GUI_BTN_ALIGN_RIGHT_TOOLTIP));
	    _btnAlignRight.setPreferredSize(Settings.BUTTON_SIZE);
	    _bottomPanel.add(_btnAlignRight);
    }

    
    boolean enableFontColor = Settings.isTagEnabled(BBCodeTags.FONT_COLOR);
    boolean enableBGColor = Settings.isTagEnabled(BBCodeTags.BG_COLOR);
    
    if(_bottomPanel.getComponentCount() > 0 && (enableFontColor || enableBGColor))
    	addSeparator(_bottomPanel);

	  if(enableFontColor) {
	    _btnFontColor = new FontColorButton(_textArea);
	    _btnFontColor.setPreferredSize(Settings.BUTTON_SIZE);
		  _bottomPanel.add(_btnFontColor);
	  }
    
	  if(enableBGColor) {
	  	_btnBgColor = new BGColorButton(_textArea);
	  	_btnBgColor.setPreferredSize(Settings.BUTTON_SIZE);
    	_bottomPanel.add(_btnBgColor);
	  }
	  
    if(Settings.ENABLE_SMILEYS) {
    	// put it in the top-panel if the bottom one is empty
	    buildSmileyCombo(_textArea.getSmileys());
    	if(_bottomPanel.getComponentCount() > 0) {
    		addSeparator(_bottomPanel);
  	    _bottomPanel.add(_btnSmiley);
    	}
    	else {
    		addSeparator(_topLeftPanel);
    		_topLeftPanel.add(_btnSmiley);
    	}
    }
    
    // just add the panel if we have buttons in it
    if(_bottomPanel.getComponentCount() > 0) {
    	setLayout(new GridLayout(2,0));
    	ScrollableBar sb = new ScrollableBar(_bottomPanel);
  		add(sb);
    }
	}
	
	public void actionPerformed(ActionEvent e) {
		if(_lastSec == null)
			return;
		
		TextAttributes attributes = _textArea.getAttributesAtCursor();
		if(attributes == null)
			return;
		
		// set alignment
		Paragraph p = _lastSec.getSectionParagraph();
		int align = p.getHorizontalAlignment();
		
		if(Settings.isTagEnabled(BBCodeTags.LEFT))
			_btnAlignLeft.setSelected(align == ParagraphAttributes.ALIGN_LEFT);
		if(Settings.isTagEnabled(BBCodeTags.CENTER))
			_btnAlignCenter.setSelected(align == ParagraphAttributes.ALIGN_CENTER);
		if(Settings.isTagEnabled(BBCodeTags.RIGHT))
			_btnAlignRight.setSelected(align == ParagraphAttributes.ALIGN_RIGHT);
		
		// set attributes
		boolean containsBold = false,containsItalic = false,containsUnderline = false;
		boolean containsStrike = false,containsSubscript = false,containsSupscript = false;
		boolean containsFontSize = false,containsFontFamily = false,containsFontColor = false;
		boolean containsBGColor = false;
		
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			Object val = attributes.get(attr);
			
			if(attr.equals(TextAttributes.BOLD) && _btnBold != null) {
				containsBold = true;
				if(val != null && val.equals(new Boolean(true)))
					_btnBold.setSelected(true);
				else
					_btnBold.setSelected(false);
			}
			else if(attr.equals(TextAttributes.ITALIC) && _btnItalic != null) {
				containsItalic = true;
				if(val != null && val.equals(new Boolean(true)))
					_btnItalic.setSelected(true);
				else
					_btnItalic.setSelected(false);
			}
			else if(attr.equals(TextAttributes.UNDERLINE) && _btnUnderline != null) {
				containsUnderline = true;
				if(val != null && val.equals(new Boolean(true)))
					_btnUnderline.setSelected(true);
				else
					_btnUnderline.setSelected(false);
			}
			else if(attr.equals(TextAttributes.STRIKE) && _btnStrike != null) {
				containsStrike = true;
				if(val != null && val.equals(new Boolean(true)))
					_btnStrike.setSelected(true);
				else
					_btnStrike.setSelected(false);
			}
			else if(attr.equals(TextAttributes.POSITION) && _btnSubScript != null) {
				if(val != null) {
					if(val.equals(new Byte(TextAttributes.POS_SUPERSCRIPT))) {
						containsSupscript = true;
						_btnSupScript.setSelected(true);
					}
					else if(val.equals(new Byte(TextAttributes.POS_SUBSCRIPT))) {
						containsSubscript = true;
						_btnSubScript.setSelected(true);
					}
				}
			}
			else if(attr.equals(TextAttributes.FONT_COLOR) && _btnFontColor != null) {
				if(val != null) {
					containsFontColor = true;
					_btnFontColor.setColor((Color)val);
				}
			}
			else if(attr.equals(TextAttributes.FONT_FAMILY) && _cBFontFamily != null) {
				if(val != null) {
					containsFontFamily = true;
					_invokeChangeEvent.setValue(false);
					_cBFontFamily.setSelectedItem(val);
					_invokeChangeEvent.setValue(true);
				}
			}
			else if(attr.equals(TextAttributes.FONT_SIZE) && _cBFontSize != null) {
				if(val != null) {
					containsFontSize = true;
					_invokeChangeEvent.setValue(false);
					_cBFontSize.setSelectedItem(String.valueOf(val));
					_invokeChangeEvent.setValue(true);
				}
			}
			else if(attr.equals(TextAttributes.BG_COLOR) && _btnBgColor != null) {
				if(val != null) {
					containsBGColor = true;
					_btnBgColor.setColor((Color)val);
				}
			}
		}
		
		boolean styles = _lastSec.getParentEnvironment().containsStyles();
		
		if(_btnBold != null) {
			_btnBold.setEnabled(styles);
			if(!containsBold)
				_btnBold.setSelected(false);
		}
		
		if(_btnItalic != null) {
			_btnItalic.setEnabled(styles);
			if(!containsItalic)
				_btnItalic.setSelected(false);
		}
		
		if(_btnUnderline != null) {
			_btnUnderline.setEnabled(styles);
			if(!containsUnderline)
				_btnUnderline.setSelected(false);
		}
		
		if(_btnStrike != null) {
			_btnStrike.setEnabled(styles);
			if(!containsStrike)
				_btnStrike.setSelected(false);
		}
		
		if(_btnSubScript != null) {
			_btnSubScript.setEnabled(styles);
			if(!containsSubscript)
				_btnSubScript.setSelected(false);
		}
		
		if(_btnSupScript != null) {
			_btnSupScript.setEnabled(styles);
			if(!containsSupscript)
				_btnSupScript.setSelected(false);
		}
		
		if(_cBFontFamily != null) {
			_cBFontFamily.setEnabled(styles);
			if(!containsFontFamily) {
				String defFontFamily = _lastSec.getParentEnvironment().getEnvView().getDefaultFontFamily();
				_invokeChangeEvent.setValue(false);
				_cBFontFamily.setSelectedItem(defFontFamily);
				_invokeChangeEvent.setValue(true);
			}
		}
		
		if(_cBFontSize != null) {
			_cBFontSize.setEnabled(styles);
			if(!containsFontSize) {
				int defFontSize = _lastSec.getParentEnvironment().getEnvView().getDefaultFontSize();
				_invokeChangeEvent.setValue(false);
				_cBFontSize.setSelectedItem(String.valueOf(defFontSize));
				_invokeChangeEvent.setValue(true);
			}
		}
		
		if(_btnFontColor != null) {
			_btnFontColor.setEnabled(styles);
			if(!containsFontColor) {
				Color defFontColor = _lastSec.getParentEnvironment().getEnvView().getDefaultFontColor();
				_btnFontColor.setColor(defFontColor);
			}
		}
		
		if(_btnBgColor != null) {
			_btnBgColor.setEnabled(styles);
			if(!containsBGColor)
				_btnBgColor.setColor(_textArea.getBackground());
		}
		
		if(_btnAlignLeft != null)
			_btnAlignLeft.setEnabled(styles);
		if(_btnAlignCenter != null)
			_btnAlignCenter.setEnabled(styles);
		if(_btnAlignRight != null)
			_btnAlignRight.setEnabled(styles);
		
		if(_btnURL != null)
			_btnURL.setEnabled(styles);
		if(_btnEmail != null)
			_btnEmail.setEnabled(styles);
		if(_btnSmiley != null)
			_btnSmiley.setEnabled(styles);
		if(_btnImage != null)
			_btnImage.setEnabled(styles);
		
		boolean subEnvs = _lastSec.getParentEnvironment().containsSubEnvironments();
		if(_btnCode != null)
			_btnCode.setEnabled(subEnvs);
		if(_btnQuote != null)
			_btnQuote.setEnabled(subEnvs);
		if(_btnList != null)
			_btnList.setEnabled(subEnvs);
	}
	
	/**
	 * builds the smiley comboBox
	 * 
	 * @param con the SmileyContainer instance
	 */
	private void buildSmileyCombo(SmileyContainer con) {
		List smileys = con.getSmileys();
		URL defURL = null;
		CellContent[] items = new CellContent[smileys.size()];
		Iterator it = smileys.iterator();
		for(int i = 0;it.hasNext();i++) {
			SecSmiley sm = (SecSmiley)it.next();

			items[i] = new CellContent(
					sm,
					Toolkit.getDefaultToolkit().getImage(sm.getImageURL())
			);
			
			if(i == 0)
				defURL = sm.getImageURL();
		}
    
    _btnSmiley = new ButtonComboBox(items,6,SwingConstants.CENTER);
    
    if(defURL != null) {
    	JButton listBtn = new JButton();
    	
    	listBtn.setIcon(Helper.getLimitedImageIcon(listBtn,defURL));
  		listBtn.setPreferredSize(Settings.BUTTON_SIZE);
  	  
  		listBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CellContent val = _btnSmiley.getDefaultValue();
					_textArea.addSmiley((SecSmiley)val.getKey());
					_textArea.pasteTextAtCursor(" ",false);
					_textArea.requestFocus();
				}
			});
  		_btnSmiley.setDefaultButton(listBtn,true);
	    
	    _btnSmiley.addItemSelectedListener(new ItemSelectedListener() {
				public void valueSelected(int row,int col,CellContent val) {
					_textArea.addSmiley((SecSmiley)val.getKey());
					_textArea.pasteTextAtCursor(" ",false);
					_textArea.requestFocus();
				}
			});
	    
	    _btnSmiley.setToolTipText(LanguageContainer.getText(Language.GUI_BTN_SMILEY_TOOLTIP));
    }
	}
	
	/**
	 * adds a separator to the given panel
	 * 
	 * @param panel the panel where to add the separator
	 */
	private void addSeparator(JPanel panel) {
    panel.add(Helper.getSeparator());
	}
}