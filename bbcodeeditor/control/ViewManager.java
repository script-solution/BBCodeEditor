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

package bbcodeeditor.control;

import java.util.*;

import bbcodeeditor.control.view.*;


/**
 * The manager which collects the dirty views and refreshes them
 * 
 * @author hrniels
 */
public class ViewManager {

	/**
	 * All dirty sections
	 */
	private final Set _dirtySections = new HashSet();

	/**
	 * All dirty lines
	 */
	private final Set _dirtyLines = new HashSet();

	/**
	 * All dirty paragraphs
	 */
	private final List _dirtyParas = new ArrayList();

	/**
	 * All other dirty views
	 */	
	private final Set _dirtyOther = new HashSet();
	
	/**
	 * All paragraphs which content should be refreshed (FONT, TEXT_BOUNDS, LINE_HEIGHT)
	 */
	private final Set _dirtyParaContents = new HashSet();
	
	/**
	 * Constructor
	 */
	public ViewManager() {
		
	}
	
	/**
	 * Marks the content of the paragraph as "dirty".
	 * This means that all lines and sections in it will be refreshed
	 * 
	 * @param para the paragraph
	 */
	public void markParagraphDirty(Paragraph para) {
		_dirtyParaContents.add(para);
	}
	
	/**
	 * Marks the given view as dirty
	 * 
	 * @param v the view
	 */
	public void markDirty(View v) {
		// NOTE: we have to split that because we have to ensure that the
		// sections will be refreshed first, after that the lines and so on
		
		if(v instanceof IContentSectionView)
			_dirtySections.add(v);
		else if(v instanceof ILineView)
			_dirtyLines.add(v);
		else if(v instanceof IParagraphView) {
			if(!_dirtyParas.contains(v))
				_dirtyParas.add(v);
		}
		else
			_dirtyOther.add(v);
	}
	
	/**
	 * Refresh all dirty views
	 */
	public void refresh() {
		if(_dirtyParaContents.size() > 0) {
			Iterator it = _dirtyParaContents.iterator();
			while(it.hasNext()) {
				Paragraph p = (Paragraph)it.next();
				Line l = p.getFirstLine();
				do {
					Section s = l.getFirstSection();
					do {
						if(s instanceof TextSection) {
							TextSectionView tview = (TextSectionView)s.getView();
							tview.forceRefresh(TextSectionView.FONT,false);
							tview.forceRefresh(TextSectionView.TEXT_BOUNDS,false);
							s.getView().refresh();
						}
						
						s = (Section)s.getNext();
					}
					while(s != null);
					
					l.getView().refresh();
					l = (Line)l.getNext();
				}
				while(l != null);
			}
			
			_dirtyParaContents.clear();
		}
		
		if(_dirtySections.size() > 0) {
			Iterator it = _dirtySections.iterator();
			while(it.hasNext()) {
				View v = (View)it.next();
				v.refresh();
			}
			_dirtySections.clear();
		}

		if(_dirtyLines.size() > 0) {
			Iterator it = _dirtyLines.iterator();
			while(it.hasNext()) {
				View v = (View)it.next();
				v.refresh();
			}
			_dirtyLines.clear();
		}
		
		if(_dirtyParas.size() > 0) {
			Iterator it = _dirtyParas.iterator();
			while(it.hasNext()) {
				View v = (View)it.next();
				v.refresh();
			}
			_dirtyParas.clear();
		}
		
		if(_dirtyOther.size() > 0) {
			Iterator it = _dirtyOther.iterator();
			while(it.hasNext()) {
				View v = (View)it.next();
				v.refresh();
			}
			_dirtyOther.clear();
		}
	}
}