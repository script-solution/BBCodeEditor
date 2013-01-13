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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import bbcodeeditor.gui.Helper;


/**
 * A cellrenderer for a Table which allows to set the alignment
 * 
 * @author hrniels
 */
public class HoriAlignmentRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The maximum image-size that will be displayed.
	 */
	private static final int MAX_IMAGE_SIZE = 40;

	/**
	 * the alignment
	 */
	protected int _align = SwingConstants.LEFT;
	
	/**
	 * constructor
	 * 
	 * @param align the horizontal alignment (see SwingConstants)
	 */
	public HoriAlignmentRenderer(int align) {
		_align = align;
		setOpaque(true); // HAVE TO do this for background to show up.
		
		setBorder(new EmptyBorder(2,2,2,2));
		setHorizontalAlignment(_align);
	}

	public Component getTableCellRendererComponent(JTable table,Object color,
			boolean isSelected,boolean hasFocus,int row,int column) {
		if(isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		}
		else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		
		setFont(table.getFont());
		
		Object val = table.getModel().getValueAt(row,column);
		String text = "";
		if(val instanceof CellContent) {
			CellContent cval = (CellContent)val;
			if(cval.getValue() instanceof Image) {
				Dimension size = new Dimension(MAX_IMAGE_SIZE,MAX_IMAGE_SIZE);
				ImageIcon icon = Helper.getLimitedImageIcon(this,(Image)cval.getValue(),size);
				icon.setImageObserver(table);
				setIcon(icon);
				if(icon.getIconWidth() > MAX_IMAGE_SIZE || icon.getIconHeight() > MAX_IMAGE_SIZE)
					setPreferredSize(new Dimension(MAX_IMAGE_SIZE,MAX_IMAGE_SIZE));
			}
			else
				text = String.valueOf(((CellContent)val).getValue());
		}
		
		// remove icon
		if(text.length() > 0 || val == null) {
			setText(text);
			setIcon(null);
		}
		
		return this;
	}
}