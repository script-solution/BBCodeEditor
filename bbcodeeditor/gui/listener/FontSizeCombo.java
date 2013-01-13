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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.tools.MutableBoolean;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;



/**
 * The font-size-button for the button-bar
 * 
 * @author hrniels
 */
public class FontSizeCombo extends JComboBox implements ItemListener {

	private static final long serialVersionUID = 7943582883326886110L;

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
	 * @param textArea the textArea-object
	 * @param invokeChangeEvent a MutableBoolean which indicates if the attribute should be set
	 */
	public FontSizeCombo(AbstractTextField textArea,MutableBoolean invokeChangeEvent) {
		_invokeChangeEvent = invokeChangeEvent;
		_textArea = textArea;
		
		for(int i = 8;i <= 29;i++)
	  	addItem(String.valueOf(i));
		addItemListener(this);
		
		setPreferredSize(new Dimension(70,getPreferredSize().height));
		
		setToolTipText(LanguageContainer.getText(Language.GUI_BTN_FONTSIZE_TOOLTIP));
		setRenderer(new FontSizeRenderer());
		setEditable(true);
		setEditor(new FontSizeEditor());
	}
	
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED) {
			if(_invokeChangeEvent.getValue()) {
				int fontSize = Integer.parseInt((String)((JComboBox)e.getSource()).getSelectedItem());
				_textArea.setAttribute(TextAttributes.FONT_SIZE,new Integer(fontSize));
			}
		}
		
		_textArea.requestFocus();
	}
	
	/**
	 * The "editor" for the fontsize-combo<br>
	 * Actually this does not make the comboBox editable but we make sure that the currently
	 * selected item in the combobox will be painted well with GTK and it does have the
	 * default font-size
	 * 
	 * @author hrniels
	 */
	private static final class FontSizeEditor extends JTextField implements ComboBoxEditor {
		
		private static final long serialVersionUID = 126746252761168274L;

		/**
		 * constructor
		 */
		public FontSizeEditor() {
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
	private static final class FontSizeRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;
		
		/**
		 * constructor
		 */
		public FontSizeRenderer() {
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
			int size = Integer.parseInt(String.valueOf(value));
			setFont(new Font(f.getFamily(),f.getStyle(),size));
			
			setText(String.valueOf(value));
			
			return this;
		}
	}
}