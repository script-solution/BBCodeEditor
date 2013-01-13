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

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.TableCellRenderer;

import bbcodeeditor.gui.Helper;


/**
 * 
 * @author hrniels
 */
public class ButtonTablePopup extends JButton {
	
	private static final long serialVersionUID = -8233858766473707003L;

	/**
	 * The maximum table-height
	 */
	private static final int MAX_TABLE_HEIGHT = 300;
	
	/**
	 * The table for the popup
	 */
	private PopupTable _table = null;
	
	/**
	 * The used popup
	 */
	private JPopupMenu _popup = null;
	
	/**
	 * small helper
	 */
	private boolean _inButtonBounds = false;
	
	// Cache some variables so that we can init the table lazy
	private CellContent[] _items;
	private int _cols;
	private int _align;
	private java.util.List _selItemListener = new ArrayList();
	
	/**
	 * constructor
	 * 
	 * @param items the items for the table
	 * @param cols the number of columns
	 * @param align the alignment of the table-content. See SwingConstants
	 */
	public ButtonTablePopup(CellContent[] items,int cols,int align) {
		super(new ImageIcon(Helper.getFileInDocumentBase("./images/arrow.png")));
		
		_items = items;
		_cols = cols;
		_align = align;
		
		// Very ugly but I think we have to catch global mouse-events to hide the popup
		// if necessary :/
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){
			public void eventDispatched(AWTEvent event) {
				if(event.getID() == MouseEvent.MOUSE_PRESSED) {
					_inButtonBounds = false;

					if(_popup.isVisible()) {
						MouseEvent me = (MouseEvent)event;
						if(me.getSource() instanceof Container) {
							// translate to application wide coordinates
							Point mp = getPositionInApp((Container)me.getSource());
							mp.translate(me.getX(),me.getY());
							Point pp = getPositionInApp(_popup);
	
							// store if we have hit the button
							Point bp = getPositionInApp(ButtonTablePopup.this);
							if(mp.x >= bp.x && mp.y >= bp.y && mp.x <= bp.x + getWidth() && 
									mp.y <= bp.y + getHeight())
								_inButtonBounds = true;
							
							// outside the popup?
							if(mp.x < pp.x || mp.y < pp.y || mp.x > pp.x + _popup.getWidth() ||
									mp.y > pp.y + _popup.getHeight())
								_popup.setVisible(false);
						}
					}
				}
			}
		},AWTEvent.MOUSE_EVENT_MASK);
		
		// hide popup if the component has been moved or something like that
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
      	_popup.setVisible(false);
			}
      
      public void ancestorRemoved(AncestorEvent event) {
      	_popup.setVisible(false);
      }
      
      public void ancestorMoved(AncestorEvent event) {
      	_popup.setVisible(false);
      }
		});

		_popup = new JPopupMenu();
		
		// show popup if the button has been clicked
		Insets margin = getMargin();
    setMargin(new Insets(margin.top,3,margin.bottom,3));
    addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPopup();
			}
		});
	}
	
	/**
	 * displays the popup
	 */
	public void showPopup() {
		// if the last mouse-click was in the button-bounds the popup has just been hidden
		// therefore we don't want to show it again
		if(_inButtonBounds)
			return;
		
		initTable();
		_table.clearSelection();
		
		Dimension tblSize = _popup.getLayout().preferredLayoutSize(_popup);
		Point p = getLocationOnScreen();
    _popup.setLocation(p.x - (int)tblSize.getWidth() + getWidth(),p.y + getHeight());
    
    _popup.setVisible(true);
	}
	
	/**
	 * @return the width of a column
	 */
	public int getColumnWidth() {
		initTable();
		return _table.getColumnWidth();
	}
	
	/**
	 * sets the column widths for all columns
	 * 
	 * @param width the width
	 */
	public void setColumnWidth(int width) {
		initTable();
		_table.setColumnWidth(width);
	}
	
	/**
	 * sets the TableCellRenderer to the given one
	 * 
	 * @param renderer the renderer to use
	 */
	public void setCellRenderer(TableCellRenderer renderer) {
		initTable();
		_table.setCellRenderer(renderer);
	}
	
	/**
	 * determines the position of the given component in the application recursivly
	 * 
	 * @param parent the component
	 * @return the position in the application
	 */
	public Point getPositionInApp(Container parent) {
		int x = parent.getX(),y = parent.getY();
		if(parent.getParent() != null) {
			Point p = getPositionInApp(parent.getParent());
			x += p.x;
			y += p.y;
		}
		
		return new Point(x,y);
	}
	
	/**
	 * Adds the given listener to the list. It will be notified as soon as a value
	 * has been selected
	 * 
	 * @param listener the listener to add
	 */
	public void addItemSelectedListener(ItemSelectedListener listener) {
		if(_table == null)
			_selItemListener.add(listener);
		else
			_table.addItemSelectedListener(listener);
	}
	
	/**
	 * Inits the table if necessary
	 */
	private void initTable() {
		if(_table != null)
			return;
		
		// build rows
		int numRows = (int)Math.ceil(_items.length / (double)_cols);
		Vector rows = new Vector(numRows);
		int a = 0;
		for(int i = 0;i < numRows;i++) {
			Vector v = new Vector(_cols);
			for(int x = 0;x < _cols;x++) {
				if(a < _items.length)
					v.add(_items[a++]);
				else
					v.add(null);
			}
			rows.add(v);
		}
		
		// build dummy columns
		Vector columns = new Vector(_cols);
		for(int i = 0;i < _cols;i++)
			columns.add("");
		
		_table = new PopupTable(rows,columns);
		_table.setAlignment(_align);
		_table.addItemSelectedListener(new ItemSelectedListener() {
			public void valueSelected(int row,int col,CellContent val) {
				_popup.setVisible(false);
			}
		});
		
		// we have to remove the table-header if we use a scrollpane
		_table.setTableHeader(null);
		
		// add listener
		Iterator it = _selItemListener.iterator();
		while(it.hasNext())
			_table.addItemSelectedListener((ItemSelectedListener)it.next());
		
		// put the table in a scrollpane to support scrolling
		JScrollPane scrollPane = new JScrollPane(_table);
		scrollPane.setBorder(new EmptyBorder(0,0,0,0));
		Dimension tblDim = _table.getPreferredSize();
		
		// make sure that the table doesn't get too high
		int height,width;
		if(tblDim.height > MAX_TABLE_HEIGHT) {
			height = MAX_TABLE_HEIGHT;
			width = tblDim.width + 20;
		}
		else {
			height = tblDim.height + 2;
			width = tblDim.width + 2;
		}
		scrollPane.setPreferredSize(new Dimension(width,height));
		
		_popup.add(scrollPane);
	}
}