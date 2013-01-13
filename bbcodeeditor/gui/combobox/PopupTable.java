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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


/**
 * The table for a popup-menu
 * 
 * @author hrniels
 */
public class PopupTable extends JTable {
	
	private static final long serialVersionUID = 2604762489772891884L;
	
	/**
	 * the alignment of the content
	 */
	private int _align = SwingConstants.LEFT;
	
	/**
	 * A list with all listeners
	 */
	private final List _selectedListeners = new ArrayList();
	
	/**
	 * The renderer for the cells
	 */
	private TableCellRenderer _renderer = new HoriAlignmentRenderer(_align);

	/**
	 * constructor
	 * 
	 * @param rows the rows to use
	 * @param columns the columns
	 */
	public PopupTable(Vector rows,Vector columns) {
		super(rows,columns);
		
		setShowGrid(false);
		setDragEnabled(false);
		setColumnSelectionAllowed(true);
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		adjustCellSizes();
		
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				
				if(row >= 0 && col >= 0) {
					CellContent val = (CellContent)getValueAt(row,col);
					if(val != null)
						invokeListeners(row,col,val);
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				
				if(row >= 0 && col >= 0)
					changeSelection(row,col,false,false);
			}
		});
	}
	
	/**
	 * @return the width of a column
	 */
	public int getColumnWidth() {
		return getColumnModel().getColumn(0).getWidth();
	}
	
	/**
	 * sets the column widths for all columns
	 * 
	 * @param width the width
	 */
	public void setColumnWidth(int width) {
		TableColumnModel colModel = getColumnModel();
		for(int i = 0;i < getColumnCount();i++)
			colModel.getColumn(i).setPreferredWidth(width);
	}
	
	/**
	 * Adds the given listener to the list. It will be notified as soon as a value
	 * has been selected
	 * 
	 * @param listener the listener to add
	 */
	public void addItemSelectedListener(ItemSelectedListener listener) {
		_selectedListeners.add(listener);
	}
	
	/**
	 * invokes all listeners with the given value
	 * 
	 * @param row the selected row
	 * @param col the selected column
	 * @param val the value to use
	 */
	private void invokeListeners(int row,int col,CellContent val) {
		Iterator it = _selectedListeners.iterator();
		while(it.hasNext()) {
			ItemSelectedListener listener = (ItemSelectedListener)it.next();
			listener.valueSelected(row,col,val);
		}
	}
	
	/**
	 * adjusts all row-heights and column-widths
	 */
	private void adjustCellSizes() {
		int[] maxColWidths = new int[getColumnCount()];
		
		// set row heights
		for(int row = 0;row < getRowCount();row++) {
			int max = 0;
			for(int i = 0;i < getColumnCount();i++) {
				TableCellRenderer tcr = getCellRenderer(row,i);
				if(tcr instanceof HoriAlignmentRenderer) {
					TableCellRenderer renderer = getCellRenderer(row,i);
					Component comp = renderer.getTableCellRendererComponent(
							this,getValueAt(row,i),false,false,row,i);
					Dimension prefSize = comp.getPreferredSize();
					
					if(prefSize.width > maxColWidths[i])
						maxColWidths[i] = prefSize.width;
					
					int height = prefSize.height;
					if(height > max)
						max = height;
				}
			}
			
			setRowHeight(row,max + 5);
		}
		
		// set column widths
		TableColumnModel colModel = getColumnModel();
		for(int i = 0;i < maxColWidths.length;i++)
			colModel.getColumn(i).setPreferredWidth(maxColWidths[i] + 15);
	}

	/**
	 * Sets the alignment of the content<br>
	 * Use SwingConstants.LEFT, SwingConstants.RIGHT or SwingConstants.CENTER
	 * 
	 * @param align the new alignment
	 * @see SwingConstants
	 */
	public void setAlignment(int align) {
		if(align != _align)
			_renderer = new HoriAlignmentRenderer(align);
		
		_align = align;
	}

	public boolean isCellEditable(int row,int column) {
		// editing is not allowed
		return false;
	}
	
	/**
	 * sets the TableCellRenderer to the given one
	 * 
	 * @param renderer the renderer to use
	 */
	public void setCellRenderer(TableCellRenderer renderer) {
		_renderer = renderer;
		adjustCellSizes();
	}

	public TableCellRenderer getCellRenderer(int row,int column) {
		return _renderer;
	}
}