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

package bbcodeeditor.control.tools;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;


/**
 * Some XML-tools
 * 
 * @author hrniels
 */
public class XMLTools {
	
	private XMLTools() {
		// no instantiation
	}

	/**
	 * Tries to instantiate the documentBuilderFactory.
	 * If this is not possible the app exits.
	 * 
	 * @return the instance if successfull
	 */
	public static DocumentBuilderFactory getDocumentBuilderFactory() {
		try {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			return DocumentBuilderFactory.newInstance();
		}
		catch(FactoryConfigurationError ex) {
			try {
				System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
				return DocumentBuilderFactory.newInstance();
			}
			catch(FactoryConfigurationError ex2) {
				System.out.println(ex);
				System.exit(0);
			}
		}
		
		return null;
	}
}