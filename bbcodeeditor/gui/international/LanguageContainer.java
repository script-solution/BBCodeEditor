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

import java.security.InvalidParameterException;


/**
 * The language-container manages the selected language and is the public interface
 * to get texts etc. in the selected language
 * 
 * @author hrniels
 */
public class LanguageContainer {

	/**
	 * The currently selected language
	 */
	private static Language selectedLanguage = null;
	
	private LanguageContainer() {
		// prevent instantiation
	}

	/**
	 * sets the language which should be used
	 * 
	 * @param lang the language.
	 * @throws LanguageException if loading the language fails
	 */
	public static void setLanguage(String lang) throws LanguageException {
		selectedLanguage = new Language(lang);
	}
	
	/**
	 * retrieves the text for the given id in the currently selected language
	 * 
	 * @param id the id of the text
	 * @return the text for the given id in the currently selected language
	 * @throws InvalidParameterException if the selected language is null
	 */
	public static String getText(String id) {
		if(selectedLanguage == null)
			throw new InvalidParameterException("Call setLanguage(int) first!");
		
		return selectedLanguage.getText(id);
	}
}