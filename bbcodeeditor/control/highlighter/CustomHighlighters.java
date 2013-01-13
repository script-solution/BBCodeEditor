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

package bbcodeeditor.control.highlighter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bbcodeeditor.control.tools.XMLTools;

/**
 * Contains all highlighters that are available.
 * 
 * @author hrniels
 */
public class CustomHighlighters {
	
	/**
	 * The list of all highlighters
	 */
	private static List _highlighter = new ArrayList(); 
	
	/**
	 * Inits the highlighters in given folder defined in the given language-file
	 * 
	 * @param hlFolder the folder which contains the highlighters
	 * @param langFilename the name of the file which contains all available
	 * 	highlighters
	 */
	public static void init(URL hlFolder,String langFilename) {
		_highlighter.clear();
		// read highlighters from file and add it to the control
		try {
			readFile(hlFolder,new URL(hlFolder,langFilename));
		}
		catch(MalformedURLException e2) {
			e2.printStackTrace();
		}
		
		Iterator it = _highlighter.iterator();
		while(it.hasNext())
			HighlightSyntax.addHighlighter((HighlighterEntry)it.next());
	}

	private CustomHighlighters() {
		// no instantation
	}
	
	/**
	 * @return a list with all highlighters
	 */
	public static List getHighlighter() {
		return _highlighter;
	}
	
	/**
	 * Reads the file and stores the highlighters
	 * 
	 * @param hlFolder the folder which contains the highlighters
	 * @param file the file to read
	 */
	private static void readFile(URL hlFolder,URL file) {
		DocumentBuilderFactory factory = XMLTools.getDocumentBuilderFactory();
		try {
			DocumentBuilder builder  = factory.newDocumentBuilder();
			Document doc = builder.parse(file.openStream());
			
			NodeList childs = doc.getElementsByTagName("languages").item(0).getChildNodes();
			for(int i = 0;i < childs.getLength();i++) {
				Node child = childs.item(i);
				
				// we don't want other node-types than ELEMENT_NODE's
				if(child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getNodeName().equals("language")) {
						Node nId = child.getAttributes().getNamedItem("id");
						Node nName = child.getAttributes().getNamedItem("name");
						Node nFile = child.getAttributes().getNamedItem("file");
						if(nId != null && nName != null && nFile != null) {
							try {
								URL url = new URL(hlFolder,nFile.getNodeValue());
								HighlighterEntry e = new HighlighterEntry(
										nId.getNodeValue(),nName.getNodeValue(),url);
								_highlighter.add(e);
							}
							catch(MalformedURLException e) {
								
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			
		}
		
		// now sort the highlighters
		Collections.sort(_highlighter,new Comparator() {
			public int compare(Object arg0,Object arg1) {
				HighlighterEntry e1 = (HighlighterEntry)arg0;
				HighlighterEntry e2 = (HighlighterEntry)arg1;
				return e1.getId().compareTo(e2.getId());
			}
		});
	}
}