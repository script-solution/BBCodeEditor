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

package bbcodeeditor.gui.listener;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.tools.MutableBoolean;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;



/**
 * The font-family-combo for the button-bar
 * 
 * @author hrniels
 */
public class FontFamilyCombo extends JComboBox implements ItemListener {

	private static final long serialVersionUID = -1251845879808350232L;

	/**
	 * the textField instance
	 */
	private final AbstractTextField _textArea;
	
	/**
	 * A mutable boolean which indicates if we should invoke a change-event
	 */
	private final MutableBoolean _invokeChangeEvent;
	
	/**
	 * constructor
	 * 
	 * @param fonts the available fonts
	 * @param textArea the textArea
	 * @param invokeChangeEvent a MutableBoolean which indicates if the attribute should be set
	 */
	public FontFamilyCombo(java.util.List fonts,AbstractTextField textArea,MutableBoolean invokeChangeEvent) {
		_invokeChangeEvent = invokeChangeEvent;
		_textArea = textArea;
		
		Collections.sort(fonts);
    for(int i = 0;i < fonts.size();i++)
    	addItem(fonts.get(i));
    addItemListener(this);
    
    setToolTipText(LanguageContainer.getText(Language.GUI_BTN_FONTFAMILY_TOOLTIP));
    // looks senseless, but works :)
    setPreferredSize(getPreferredSize());
    
    setRenderer(new FontFamilyRenderer());
    setEditable(true);
    setEditor(new FontFamilyEditor());
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED) {
			if(_invokeChangeEvent.getValue()) {
				_textArea.setAttribute(TextAttributes.FONT_FAMILY,((JComboBox)e.getSource()).getSelectedItem());
				_textArea.requestFocus();
			}
		}
	}
	
	/**
	 * The "editor" for the fontfamily-combo<br>
	 * Actually this does not make the comboBox editable but we make sure that the currently
	 * selected item in the combobox will be painted well with GTK and it does have the
	 * default fontfamily
	 * 
	 * @author hrniels
	 */
	private static final class FontFamilyEditor extends JTextField implements ComboBoxEditor {
		
		private static final long serialVersionUID = -4260244399494729589L;

		/**
		 * constructor
		 */
		public FontFamilyEditor() {
			setEditable(false);
		}
		
		public void setItem(Object anObject) {
			setText(String.valueOf(anObject));
		}
	
		public Object getItem() {
			return getText();
		}
	
		public Component getEditorComponent() {
			return this;
		}
	}
	
	/**
	 * A cellrenderer for a Table which renders each cell in the font-family specified by
	 * the cell-value
	 * 
	 * @author hrniels
	 */
	private static final class FontFamilyRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;
		
		/**
		 * constructor
		 */
		public FontFamilyRenderer() {
			setOpaque(true); // HAVE TO do this for background to show up.
			
			setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
      setBorder(new EmptyBorder(3,3,3,3));
		}

		public Component getListCellRendererComponent(JList list,Object value,int index,
				boolean isSelected,boolean cellHasFocus) {
			if(isSelected) {
				setForeground(list.getSelectionForeground());
				setBackground(list.getSelectionBackground());
			}
			else {
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}
			
			Font f = list.getFont();
			setFont(new Font(String.valueOf(value),f.getStyle(),f.getSize()));
			
			setText(String.valueOf(value));
			
			return this;
		}
	}
}