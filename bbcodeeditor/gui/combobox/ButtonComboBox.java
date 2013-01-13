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

package bbcodeeditor.gui.combobox;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import bbcodeeditor.gui.Helper;

/**
 * A comboBox with a button for the default-action and some additional items in
 * the comboBox which will be some special actions.
 * 
 * @author hrniels
 */
public class ButtonComboBox extends JPanel {

	private static final long serialVersionUID = -3405826611927521013L;
	
	/**
	 * The table in a popup including the arrow-button
	 */
	private final ButtonTablePopup _tableCombo;
	
	/**
	 * Indicates wether the default button should be changed to the last selected item
	 */
	private boolean _changeDefBtn;
	
	/**
	 * Our default button
	 */
	private AbstractButton _defaultButton;
	
	/**
	 * The value of the default-button
	 */
	private CellContent _defaultVal = null;

	/**
	 * constructor
	 * 
	 * @param items the items for the table
	 */
	public ButtonComboBox(CellContent[] items) {
		this(items,1,SwingConstants.LEFT);
	}

	/**
	 * constructor
	 * 
	 * @param items the items for the table
	 * @param cols the number of columns
	 * @param align the alignment of the table-content. See SwingConstants
	 */
	public ButtonComboBox(CellContent[] items,int cols,int align) {
		super(new BorderLayout());
		
		_tableCombo = new ButtonTablePopup(items,cols,align);
		add(_tableCombo,BorderLayout.EAST);
		
		// init default value
		if(items.length > 0)
			_defaultVal = items[0];

		_tableCombo.addItemSelectedListener(new ItemSelectedListener() {
			public void valueSelected(int row,int col,CellContent val) {
				if(_changeDefBtn)
					setDefaultValue(val);
			}
		});
	}
	
	/**
	 * sets the TableCellRenderer to the given one
	 * 
	 * @param renderer the renderer to use
	 */
	public void setCellRenderer(TableCellRenderer renderer) {
		_tableCombo.setCellRenderer(renderer);
	}
	
	/**
	 * @return the CellContent object of the default-button
	 */
	public CellContent getDefaultValue() {
		return _defaultVal;
	}
	
	/**
	 * Sets the default-value to the given one
	 * 
	 * @param con the CellContent
	 */
	public void setDefaultValue(CellContent con) {
		if(con.getValue() instanceof Image) {
			Dimension size = _defaultButton.getPreferredSize();
			ImageIcon icon = Helper.getLimitedImageIcon(_defaultButton,(Image)con.getValue(),size);
			_defaultButton.setIcon(icon);
		}
		else
			_defaultButton.setText(String.valueOf(con.getValue()));
		
		_defaultVal = con;
	}
	
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		
		_defaultButton.setToolTipText(text);
		_tableCombo.setToolTipText(text);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		_defaultButton.setEnabled(enabled);
		_tableCombo.setEnabled(enabled);
	}

	/**
	 * sets the default button for this combobox
	 * 
	 * @param defButton the button for the default-action
	 * @param changeDefBtn do you want to change the default button to the last selected item?
	 */
	public void setDefaultButton(AbstractButton defButton,boolean changeDefBtn) {
		setDefaultButton(defButton,changeDefBtn,false);
	}

	/**
	 * sets the default button for this combobox
	 * 
	 * @param defButton the button for the default-action
	 * @param changeDefBtn do you want to change the default button to the last selected item?
	 * @param adjustButtonToTableSize do you want to adjust the size of the button to fit
	 * to the table-size?
	 */
	public void setDefaultButton(AbstractButton defButton,boolean changeDefBtn,
			boolean adjustButtonToTableSize) {
		if(_defaultButton != null)
			remove(_defaultButton);
		
		_defaultButton = defButton;
		_changeDefBtn = changeDefBtn;
		add(_defaultButton,BorderLayout.CENTER);
		
		if(adjustButtonToTableSize) {
			int colWidth = _tableCombo.getColumnWidth();
			int arrowWidth = _tableCombo.getPreferredSize().width;
			
			_defaultButton.setPreferredSize(new Dimension(
					colWidth + _defaultButton.getMargin().left + _defaultButton.getMargin().right,
					_defaultButton.getPreferredSize().height));
			_tableCombo.setColumnWidth(colWidth + arrowWidth);
		}
	}
	
	/**
	 * displays the popup
	 */
	public void showPopup() {
		_tableCombo.showPopup();
	}
	
	/**
	 * Adds the given listener to the list. It will be notified as soon as a value
	 * has been selected
	 * 
	 * @param listener the listener to add
	 */
	public void addItemSelectedListener(ItemSelectedListener listener) {
		_tableCombo.addItemSelectedListener(listener);
	}
}