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

package bbcodeeditor.gui.international;


/**
 * @author hrniels
 */
public class LanguageException extends Exception {
	private static final long serialVersionUID = -1872057913238125760L;

	/**
	 * Constructor
	 * 
	 * @param lang the language-name
	 */
	public LanguageException(String lang) {
		super("Unable to find language '" + lang + "'");
	}

	/**
	 * Constructor
	 * 
	 * @param lang the language-name
	 * @param cause the cause
	 */
	public LanguageException(String lang,Throwable cause) {
		super("Unable to find language '" + lang + "'",cause);
	}
}
