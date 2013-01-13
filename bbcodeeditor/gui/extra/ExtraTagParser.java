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

package bbcodeeditor.gui.extra;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bbcodeeditor.control.tools.XMLTools;
import bbcodeeditor.gui.Settings;

/**
 * An XML-parser for the extra-tags-file
 * 
 * @author hrniels
 */
public class ExtraTagParser {
	
	/**
	 * the xml-file
	 */
	private URL _xmlFile;

	/**
	 * constructor
	 * 
	 * @param xmlFile the file to parse
	 */
	public ExtraTagParser(URL xmlFile) {
		_xmlFile = xmlFile;
	}
	
	/**
	 * reads the extra tag from the xml-file and returns the list of tags
	 * 
	 * @return a Map with the found buttons and tags
	 */
	public Map getExtraButtons() {
		Map buttons = new HashMap();
		
		DocumentBuilderFactory factory = XMLTools.getDocumentBuilderFactory();
		
		try {
			DocumentBuilder builder  = factory.newDocumentBuilder();
	    Document doc = builder.parse(_xmlFile.openStream());
	    
	    // read buttons
	    NodeList nButtons = doc.getElementsByTagName("button");
	    for(int i = 0;i < nButtons.getLength();i++) {
	    	Node button = nButtons.item(i);
	    	
	    	// we don't want other node-types than ELEMENT_NODE's
	    	if(button.getNodeType() == Node.ELEMENT_NODE) {
	    		NamedNodeMap attributes = button.getAttributes();
	    		Node buttonName = attributes.item(0);

	    		ExtraButton eBtn = new ExtraButton(buttonName.getNodeValue());
	    		
	    		// go through all child-nodes
		    	NodeList children = button.getChildNodes();
		    	for(int x = 0;x < children.getLength();x++) {
		    		Node child = children.item(x);
		    		
		    		// is it an element?
		    		if(child.getNodeType() == Node.ELEMENT_NODE) {
		    			String nodeName = child.getNodeName();
		    			String nodeValue = child.getFirstChild() != null ? child.getFirstChild().getNodeValue() : "";
		    			
		    			if(nodeName.equals("tooltip"))
		    				eBtn.setTooltip(nodeValue);
		    			else if(nodeName.equals("image"))
		    				eBtn.setImage(nodeValue);
		    		}
		    	}
		    	
		    	buttons.put(eBtn.getName(),eBtn);
	    	}
	    }
	    
	    // read tags
	    NodeList tags = doc.getElementsByTagName("item");
	    for(int i = 0;i < tags.getLength();i++) {
	    	Node tag = tags.item(i);
	    	
    		// we don't want other node-types than ELEMENT_NODE's
	    	if(tag.getNodeType() == Node.ELEMENT_NODE) {
	    		NamedNodeMap attributes = tag.getAttributes();
	    		Node tagName = attributes.item(0);
	    		boolean isDeactivated = false;
	    		
	    		if(!Settings.isExtraTagEnabled(tagName.getNodeValue()))
	    			isDeactivated = true;
	    		
	    		ExtraTag eTag = new ExtraTag(tagName.getNodeValue());
	    		String button = null;
	    		
	    		// go through all child-nodes
		    	NodeList children = tag.getChildNodes();
		    	for(int x = 0;x < children.getLength();x++) {
		    		Node child = children.item(x);
		    		
		    		// is it an element?
		    		if(child.getNodeType() == Node.ELEMENT_NODE) {
		    			String nodeName = child.getNodeName();
		    			String nodeValue = child.getFirstChild() != null ? child.getFirstChild().getNodeValue() : "";
		    			
		    			if(nodeName.equals("comboName"))
		    				eTag.setComboName(nodeValue);
		    			else if(nodeName.equals("dialogTitle"))
		    				eTag.setDialogTitle(nodeValue);
		    			else if(nodeName.equals("parameterTitle"))
		    				eTag.setParameterTitle(nodeValue);
		    			else if(nodeName.equals("buttonID"))
		    				button = nodeValue;
		    			else if(nodeName.equals("hasParameter"))
		    				eTag.setHasParameter(Boolean.parseBoolean(nodeValue));
		    			else if(nodeName.equals("description"))
		    				eTag.setDescription(nodeValue);
		    		}
		    	}
		    	
		    	if(button != null) {
		    		ExtraButton btn = (ExtraButton)buttons.get(button);
		    		if(btn != null) {
		    			if(isDeactivated)
		    				btn.increaseItemCount();
		    			else
		    				btn.addItem(eTag);
		    		}
		    	}
	    	}
	    }
		}
		catch(Exception e) {
			System.out.println("Could not parse the file '" + _xmlFile + "'.\n");
			e.printStackTrace();
		}
		
		return buttons;
	}
}