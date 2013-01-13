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

import java.awt.Color;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.ColorFunctions;
import bbcodeeditor.control.tools.Pair;
import bbcodeeditor.control.tools.StringTreeMap;
import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.control.tools.XMLTools;


/**
 * A custom highlighter that reads all information from an XML-file
 * 
 * @author hrniels
 */
public class CustomHighlighter extends Highlighter {
	
	/**
	 * The file to parse
	 */
	private final URL _file;
	
	/**
	 * An empty tree map
	 */
	private final StringTreeMap _emptyTreeMap = new StringTreeMap();
	
	/**
	 * An empty attribute-map
	 */
	private final TextAttributes _emptyAttr = new TextAttributes();
	
	/**
	 * The id of the language
	 */
	private final String _id;
	
	/**
	 * All string-quotes
	 */
	private Map _stringQuotes = null;
	
	/**
	 * The multiline-comments
	 */
	private Map _multiLineComments = null;
	
	/**
	 * The single-line-comments
	 */
	private Map _singleLineComments = null;
	
	/**
	 * All symbols
	 */
	private StringTreeMap _symbols = null;
	
	/**
	 * The regexp-list
	 */
	private Map _regexps = null;
	
	/**
	 * All different keywords that should be highlighted
	 */
	private Map _keywords = null;
	
	/**
	 * The attributes for numbers
	 */
	private TextAttributes _numberAttrs = null;
	
	/**
	 * The attributes for symbols
	 */
	private TextAttributes _symbolAttrs = null;
	
	/**
	 * The attributes for strings
	 */
	private Map _stringAttrs = null;
	
	/**
	 * The attributes for keywords
	 */
	private Map _keyWordAttrs = null;
	
	/**
	 * The attributes for the different multi-line-comments
	 */
	private Map _mlCommentAttrs = null;

	/**
	 * The attributes for the different single-line-comments
	 */
	private Map _slCommentAttrs = null;
	
	/**
	 * The attributes for the different regexps
	 */
	private Map _regexpAttrs = null;
	
	/**
	 * Stores which keywords are case-sensitive
	 */
	private Map _keywordSettings = new HashMap();
	
	/**
	 * Highlight numbers?
	 */
	private boolean _highlightNumbers = false;
	
	/**
	 * The escape-char
	 */
	private char _escapeChar = '\\';
	
	/**
	 * The name of the language
	 */
	private String _name = null;
	
	/**
	 * Constructor
	 * 
	 * @param id the highlighter-id
	 * @param file the URL to the file with the highlighter-definitions
	 */
	public CustomHighlighter(String id,URL file) {
		_file = file;
		_id = id;
		
		readFromFile();
	}

	public TextAttributes getAttributes(int element) {
		switch(element) {
			case NUMBER:
				if(_numberAttrs != null)
					return _numberAttrs;
				break;
			
			case SYMBOL:
				if(_symbolAttrs != null)
					return _symbolAttrs;
				break;
		}
		
		return _emptyAttr;
	}
	
	public String getId() {
		return _id;
	}

	public char getEscapeChar() {
		return _escapeChar;
	}

	public TextAttributes getKeywordAttributes(Object id) {
		return getAttributesFor(_keyWordAttrs,id);
	}

	public Map getKeywords() {
		return getElementsFor(_keywords);
	}

	public StringTreeMap getKeywords(Object key) {
		if(_keywords == null)
			return _emptyTreeMap;
		
		StringTreeMap map = (StringTreeMap)_keywords.get(key);
		if(map == null)
			return _emptyTreeMap;
		
		return map;
	}

	public String getLangName() {
		return _name == null ? "Unknown" : _name;
	}

	public TextAttributes getMLCommentAttributes(Object id) {
		return getAttributesFor(_mlCommentAttrs,id);
	}

	public Map getMultiCommentLimiters() {
		return getElementsFor(_multiLineComments);
	}

	public TextAttributes getRegexpAttributes(Object id) {
		return getAttributesFor(_regexpAttrs,id);
	}

	public Map getRegexps() {
		return getElementsFor(_regexps);
	}

	public TextAttributes getSLCommentAttributes(Object id) {
		return getAttributesFor(_slCommentAttrs,id);
	}

	public Map getSingleComments() {
		return getElementsFor(_singleLineComments);
	}

	public TextAttributes getStringAttributes(Object id) {
		return getAttributesFor(_stringAttrs,id);
	}

	public Map getStringQuotes() {
		return getElementsFor(_stringQuotes);
	}

	public StringTreeMap getSymbols() {
		if(_symbols == null)
			return _emptyTreeMap;
		
		return _symbols;
	}

	public boolean highlightNumbers() {
		return _highlightNumbers;
	}

	public KeywordSettings getKeywordSettings(Object id) {
		return (KeywordSettings)_keywordSettings.get(id);
	}
	
	/**
	 * The default implementation for getAttributes()-methods
	 * 
	 * @param attr the element-map
	 * @param key the key
	 * @return the attributes to use
	 */
	private TextAttributes getAttributesFor(Map attr,Object key) {
		if(attr == null)
			return _emptyAttr;
		
		TextAttributes attrs = (TextAttributes)attr.get(key);
		if(attrs == null)
			return _emptyAttr;
		
		return attrs;
	}

	/**
	 * The default implementation for getElements()-methods
	 * 
	 * @param elements the elements
	 * @return the map to use
	 */
	private Map getElementsFor(Map elements) {
		if(elements == null)
			return Collections.emptyMap();
		
		return elements;
	}

	/**
	 * Reads the highlighter-properties from file
	 */
	private void readFromFile() {
		DocumentBuilderFactory factory = XMLTools.getDocumentBuilderFactory();
		
		// init the element-maps
		_keywords = Collections.emptyMap();
		_multiLineComments = Collections.emptyMap();
		_singleLineComments = Collections.emptyMap();
		_regexps = Collections.emptyMap();
		_stringQuotes = Collections.emptyMap();
		
		// init the attribute-maps
		_keyWordAttrs = new HashMap();
		_mlCommentAttrs = new HashMap();
		_slCommentAttrs = new HashMap();
		_regexpAttrs = new HashMap();
		_stringAttrs = new HashMap();

		_numberAttrs = new TextAttributes();
		_symbolAttrs = new TextAttributes();
		
		try {
			DocumentBuilder builder  = factory.newDocumentBuilder();
			Document doc = builder.parse(_file.openStream());
			
			NodeList childs = doc.getElementsByTagName("highlighter").item(0).getChildNodes();
			for(int i = 0;i < childs.getLength();i++) {
				Node child = childs.item(i);
				
				// we don't want other node-types than ELEMENT_NODE's
				if(child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getNodeName().equals("name"))
						_name = child.getFirstChild().getNodeValue();
					else if(child.getNodeName().equals("hlNumbers"))
						_highlightNumbers = Boolean.parseBoolean(child.getFirstChild().getNodeValue());
					else if(child.getNodeName().equals("escapeChar"))
						_escapeChar = String.valueOf(child.getFirstChild().getNodeValue()).charAt(0);
					else if(child.getNodeName().equals("stringQuotes"))
						_stringQuotes = readDefMap(child,true);
					else if(child.getNodeName().equals("slComments"))
						_singleLineComments = readDefMap(child,false);
					else if(child.getNodeName().equals("mlComments"))
						_multiLineComments = readMLComments(child);
					else if(child.getNodeName().equals("symbols"))
						_symbols = readTreeMap(child,true);
					else if(child.getNodeName().equals("regexps"))
						_regexps = readRegexps(child);
					else if(child.getNodeName().equals("keywords"))
						_keywords = readKeywords(child);
					else if(child.getNodeName().equals("colors"))
						readColors(child);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the colors for the higlighter
	 * 
	 * @param n the parent-node
	 */
	private void readColors(Node n) {
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				if(child.getNodeName().equals("mlComments"))
					addAttributesWithId(_mlCommentAttrs,child);
				else if(child.getNodeName().equals("slComments"))
					addAttributesWithId(_slCommentAttrs,child);
				else if(child.getNodeName().equals("keywords"))
					addAttributesWithId(_keyWordAttrs,child);
				else if(child.getNodeName().equals("regexp"))
					addAttributesWithId(_regexpAttrs,child);
				else if(child.getNodeName().equals("strings"))
					addAttributesWithId(_stringAttrs,child);
				else if(child.getNodeName().equals("numbers"))
					_numberAttrs = readAttributes(child);
				else if(child.getNodeName().equals("symbols"))
					_symbolAttrs = readAttributes(child);
			}
		}
	}

	/**
	 * Adds the attributes in the node to the given map if the
	 * id-attribute-exists in the node.
	 * 
	 * @param map the map
	 * @param n the parent-node
	 */
	private void addAttributesWithId(Map map,Node n) {
		Node id = n.getAttributes().getNamedItem("id");
		if(id != null) {
			TextAttributes attr = readAttributes(n);
			map.put(id.getNodeValue(),attr);
		}
	}

	/**
	 * Reads all attributes from the given node
	 * 
	 * @param n the parent-node
	 * @return the TextAttributes
	 */
	private TextAttributes readAttributes(Node n) {
		TextAttributes m = new TextAttributes();
		
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				Node name = child.getAttributes().getNamedItem("name");
				Node value = child.getAttributes().getNamedItem("value");
				Integer attr = TextAttributes.getAttributeFromName(name.getNodeValue());
				// we don't want to use URLs or Emails for highlighting
				if(attr.equals(TextAttributes.URL) || attr.equals(TextAttributes.EMAIL))
					continue;
				
				m.set(attr,TextAttributes.getValidValueFor(attr,value.getNodeValue()));
			}
		}
		return m;
	}

	/**
	 * Reads the map of definitions from the given node
	 * 
	 * @param n the parent-node
	 * @param storeFirstChar store just the first char of the value?
	 * @return the map with the definitions
	 */
	private Map readDefMap(Node n,boolean storeFirstChar) {
		Map m = new HashMap();
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				Node id = child.getAttributes().getNamedItem("id");
				String val = child.getFirstChild().getNodeValue();
				if(storeFirstChar)
					m.put(id.getNodeValue(),new Character(val.charAt(0)));
				else
					m.put(id.getNodeValue(),val);
			}
		}
		return m;
	}

	/**
	 * Reads the regexps from the given node
	 * 
	 * @param n the parent-node
	 * @return the map with the regexps
	 */
	private Map readRegexps(Node n) {
		Map m = new HashMap();
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				Node id = child.getAttributes().getNamedItem("id");
				Node pattern = child.getAttributes().getNamedItem("pattern");
				Node cs = child.getAttributes().getNamedItem("cs");
				Node group = child.getAttributes().getNamedItem("group");
				
				Pattern p;
				if(cs != null && (cs.getNodeValue().equals("false") || cs.getNodeValue().equals("0")))
					p = Pattern.compile(pattern.getNodeValue(),Pattern.CASE_INSENSITIVE);
				else
					p = Pattern.compile(pattern.getNodeValue());
				
				RegExDesc desc;
				if(group != null)
					desc = new RegExDesc(p,Integer.parseInt(group.getNodeValue()));
				else
					desc = new RegExDesc(p);
				
				m.put(id.getNodeValue(),desc);
			}
		}
		return m;
	}

	/**
	 * Reads the keywords from the given node
	 * 
	 * @param n the parent-node
	 * @return the map with the keywords
	 */
	private Map readKeywords(Node n) {
		Map m = new HashMap();
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				Node id = child.getAttributes().getNamedItem("id");
				Node cs = child.getAttributes().getNamedItem("cs");
				Node reqWord = child.getAttributes().getNamedItem("reqWord");
				boolean caseSensitive = cs != null && cs.getNodeValue().equals("true");
				boolean requireWord = reqWord == null || reqWord.getNodeValue().equals("true");
				StringTreeMap map = readTreeMap(child,caseSensitive);
				m.put(id.getNodeValue(),map);
				
				_keywordSettings.put(id.getNodeValue(),new KeywordSettings(caseSensitive,requireWord));
			}
		}
		return m;
	}
	
	/**
	 * Reads the multiline-comments from the given node
	 * 
	 * @param n the parent-node
	 * @return the map with the ml-comments
	 */
	private Map readMLComments(Node n) {
		Map m = new HashMap();
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				Node id = child.getAttributes().getNamedItem("id");
				Node start = child.getAttributes().getNamedItem("start");
				Node end = child.getAttributes().getNamedItem("end");
				m.put(id.getNodeValue(),new Pair(start.getNodeValue(),end.getNodeValue()));
			}
		}
		return m;
	}
	
	/**
	 * Reads a list of elements from the childs of the given node and builds
	 * a StringTreeMap with them
	 * 
	 * @param n the parent-node
	 * @param caseSensitive are the entries case-sensitive?
	 * @return the StringTreeMap
	 */
	private StringTreeMap readTreeMap(Node n,boolean caseSensitive) {
		StringTreeMap m = new StringTreeMap();
		NodeList childs = n.getChildNodes();
		for(int i = 0;i < childs.getLength();i++) {
			Node child = childs.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				String val = child.getFirstChild().getNodeValue();
				if(!caseSensitive)
					val = val.toLowerCase();
				m.add(val,new Boolean(true));
			}
		}
		return m;
	}
	
	/**
	 * Generates the XML-file for this highlighter
	 * 
	 * @return the XML-string
	 */
	public String toXML() {
		Iterator it;
		TextAttributes attr;
		
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<highlighter>\n");
		
		buf.append("\t<name>" + prepareText(getLangName()) + "</name>\n");
		buf.append("\t<hlNumbers>" + highlightNumbers() + "</hlNumbers>\n");
		buf.append("\t<escapeChar>" + prepareText(getEscapeChar()) + "</escapeChar>\n");
		
		// strings
		buf.append("\t<stringQuotes>\n");
		it = getStringQuotes().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<def id=\"" + prepareText(e.getKey()) + "\">");
			buf.append(prepareText(e.getValue()) + "</def>\n");
		}
		buf.append("\t</stringQuotes>\n");
		
		// sl comments
		buf.append("\t<slComments>\n");
		it = getSingleComments().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<def id=\"" + prepareText(e.getKey()) + "\">");
			buf.append(prepareText(e.getValue()) + "</def>\n");
		}
		buf.append("\t</slComments>\n");
		
		// ml comments
		buf.append("\t<mlComments>\n");
		it = getMultiCommentLimiters().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			Pair p = (Pair)e.getValue();
			buf.append("\t\t<def id=\"" + prepareText(e.getKey()) + "\" start=\"");
			buf.append(prepareText(p.getKey()) + "\"");
			buf.append(" end=\"" + prepareText(p.getValue()) + "\"/>\n");
		}
		buf.append("\t</mlComments>\n");
		
		// symbols
		buf.append("\t<symbols>\n");
		Map symbols = getSymbols().getEntries();
		it = symbols.keySet().iterator();
		while(it.hasNext())
			buf.append("\t\t<kw>" + prepareText(it.next()) + "</kw>\n");
		buf.append("\t</symbols>\n");
		
		// regexps
		buf.append("\t<regexps>\n");
		it = getRegexps().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			RegExDesc desc = (RegExDesc)e.getValue();
			buf.append("\t\t<def id=\"" + prepareText(e.getKey()) + "\"");
			buf.append(" pattern=\"" + prepareText(desc.getPattern()) + "\"");
			if((desc.getPattern().flags() & Pattern.CASE_INSENSITIVE) != 0)
				buf.append(" cs=\"0\"");
			if(desc.getGroup() > 0)
				buf.append(" group=\"" + desc.getGroup() + "\"");
			buf.append("/>\n");
		}
		buf.append("\t</regexps>\n");

		// keywords
		buf.append("\t<keywords>\n");
		it = getKeywords().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			StringTreeMap map = (StringTreeMap)e.getValue();
			KeywordSettings settings = getKeywordSettings(e.getKey());
			buf.append("\t\t<def id=\"" + prepareText(e.getKey()) + "\"");
			buf.append(" cs=\"" + settings.isCaseSensitive() + "\">\n");
			buf.append(" reqWord=\"" + settings.requireWord() + "\">\n");
			Iterator it2 = map.getEntries().keySet().iterator();
			while(it2.hasNext())
				buf.append("\t\t\t<kw>" + prepareText(it2.next()) + "</kw>\n");
			buf.append("\t\t</def>\n");
		}
		buf.append("\t</keywords>\n");

		// colors
		buf.append("\t<colors>\n");
		
		// colors for keywords
		it = getKeywords().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<keywords id=\"" + prepareText(e.getKey()) + "\">\n");
			attr = getKeywordAttributes(e.getKey());
			buf.append(attributesToXML(attr,3));
			buf.append("\t\t</keywords>\n");
		}
		
		// colors for symbols
		attr = getAttributes(Highlighter.SYMBOL);
		buf.append("\t\t<symbols>\n");
		buf.append(attributesToXML(attr,3));
		buf.append("\t\t</symbols>\n");
		
		// colors for numbers
		attr = getAttributes(Highlighter.NUMBER);
		buf.append("\t\t<numbers>\n");
		buf.append(attributesToXML(attr,3));
		buf.append("\t\t</numbers>\n");
		
		// colors for strings
		it = getStringQuotes().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<strings id=\"" + prepareText(e.getKey()) + "\">\n");
			attr = getStringAttributes(e.getKey());
			buf.append(attributesToXML(attr,3));
			buf.append("\t\t</strings>\n");
		}
		
		// colors for regexps
		it = getRegexps().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<regexp id=\"" + prepareText(e.getKey()) + "\">\n");
			attr = getRegexpAttributes(e.getKey());
			buf.append(attributesToXML(attr,3));
			buf.append("\t\t</regexp>\n");
		}
		
		// colors for ml-comments
		it = getMultiCommentLimiters().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<mlComments id=\"" + prepareText(e.getKey()) + "\">\n");
			attr = getMLCommentAttributes(e.getKey());
			buf.append(attributesToXML(attr,3));
			buf.append("\t\t</mlComments>\n");
		}
		
		// colors for sl-comments
		it = getSingleComments().entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			buf.append("\t\t<slComments id=\"" + prepareText(e.getKey()) + "\">\n");
			attr = getSLCommentAttributes(e.getKey());
			buf.append(attributesToXML(attr,3));
			buf.append("\t\t</slComments>\n");
		}
		
		buf.append("\t</colors>\n");
		
		buf.append("</highlighter>");
		return buf.toString();
	}

	/**
	 * Prepares the given char for adding it to the XML-string.
	 * (Just a convenience method)
	 * 
	 * @param c the char
	 * @return the string to add to the XML-string
	 */
	private String prepareText(char c) {
		return prepareText(String.valueOf(c));
	}

	/**
	 * Prepares the given object for adding it to the XML-string.
	 * (Just a convenience method)
	 * 
	 * @param o the object
	 * @return the string to add to the XML-string
	 */
	private String prepareText(Object o) {
		return prepareText(String.valueOf(o));
	}

	/**
	 * Prepares the given string for adding it to the XML-string.
	 * Replaces the necessary chars to the HTML-special-char-encodings.
	 * For example:
	 * <br>
	 * <code>&amp;</code> => <code>&amp;amp;</code>
	 * 
	 * @param text the text
	 * @return the string to add to the XML-string
	 */
	private String prepareText(String text) {
		text = StringUtils.simpleReplace(text,"&","&amp;");
		text = StringUtils.simpleReplace(text,"<","&lt;");
		text = StringUtils.simpleReplace(text,">","&gt;");
		text = StringUtils.simpleReplace(text,"\"","&quot;");
		text = StringUtils.simpleReplace(text,"'","&#039;");
		return text;
	}
	
	/**
	 * Generates the XML-representation of the given attributes
	 * 
	 * @param attributes the attribute-map
	 * @param indent the number of indent-steps
	 * @return the XML-string
	 */
	private String attributesToXML(TextAttributes attributes,int indent) {
		StringBuffer sindent = new StringBuffer();
		for(int i = 0;i < indent;i++)
			sindent.append("\t");
		
		StringBuffer buf = new StringBuffer();
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer key = (Integer)it.next();
			String name = TextAttributes.getAttributeName(key);
			String value = getAttributeValue(key,attributes.get(key));
			buf.append(sindent + "<attr name=\"" + prepareText(name) + "\"");
			buf.append(" value=\"" + prepareText(value) + "\"/>\n");
		}
		
		return buf.toString();
	}
	
	/**
	 * Returns the value for the given attribute which should be used in the
	 * XML-document
	 * 
	 * @param attr the attribute
	 * @param value the value
	 * @return the value for the XML-document
	 */
	private String getAttributeValue(Integer attr,Object value) {
		if(attr.equals(TextAttributes.FONT_COLOR) || attr.equals(TextAttributes.BG_COLOR) ||
				attr.equals(TextAttributes.HIGHLIGHT))
			return ColorFunctions.getStringFromColor((Color)value);
		
		return String.valueOf(value);
	}
}