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

/**
 * An interface that ensures that a container has a getText() and setText(String) method
 * 
 * @author hrniels
 */
public interface TextAreaContainer {

	/**
	 * @return the text of the editor
	 */
	String getText();

	/**
	 * sets the given text to the editor
	 * 
	 * @param text the new text
	 */
	void setText(String text);
	
	/**
	 * inserts the given text at the cursor-position
	 * 
	 * @param text the text to insert
	 */
	void insertText(String text);
}