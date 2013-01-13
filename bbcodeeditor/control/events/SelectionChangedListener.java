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

package bbcodeeditor.control.events;

import java.util.EventListener;


/**
 * The selection-changed listener
 * 
 * @author hrniels
 */
public interface SelectionChangedListener extends EventListener {

	/**
	 * Will be invoked as soon as the selection changes
	 * 
	 * @param empty will be true if the selection is empty
	 * @param start the start-position (-1 = no selection)
	 * @param end the end-position (-1 = no selection)
	 * @param direction the direction of the selection. see Selection.DIR_*
	 */
	void selectionChanged(boolean empty,int start,int end,int direction);
}