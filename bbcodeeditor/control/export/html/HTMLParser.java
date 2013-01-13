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

package bbcodeeditor.control.export.html;

import java.util.*;
import java.util.Map.Entry;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.tools.StringUtils;



/**
 * A very simple HTML-parser which should be enough for our task :)<br>
 * Supports the basic formating tags and lists:
 * <ul>
 * 	<li>b,i,u,strike</li>
 * 	<li>font with the attributes size,color,face</li>
 * 	<li>p with the attributes align</li>
 * 	<li>span with the attributes style</li>
 *	<li>img with the attribute src</li>
 *	<li>a with the attribute href</li>
 * 	<li>ol,ul,li. ol supports the type attribute</li>
 * 	<li>br</li>
 * </ul>
 * The style-tag may contain the following CSS-attributes:
 * <ul>
 * 	<li>font-size</li>
 * 	<li>color</li>
 * 	<li>font-family</li>
 * 	<li>background-color or background</li>
 * 	<li>font-weight</li>
 * 	<li>font-style</li>
 * 	<li>text-decoration</li>
 * </ul>
 * All other tags and attributes will be ignored. Additionally the doc-type, head-tags,
 * stylesheets, comments, javascript and so on will be ignored.<br>
 * Everything outside the body-tag will be ignored as soon as there is a html- or
 * doctype-tag.
 * <p>
 * The sense of this class is to make it possible to paste HTML-code into the control.
 * The class will convert the HTML-code to BBCode. Note that HTML-errors will lead in
 * most cases to BBCode-errors which should be detected later by the BBCode-parser.
 * 
 * @author hrniels
 */
public class HTMLParser {
	
	/**
	 * the default-font-size
	 */
	private static final int DEFAULT_FONT_SIZE = 12;

	/**
	 * All no-content-tags
	 */
	private static final List _noContentTags = Arrays.asList(new String[] {
		"applet","area","colgroup","dir","dl","fieldset","frame","frameset","map",
		"menu","ol","optgroup","select","table","tbody","thead","tfoot","tr","ul"
	});
	
	/**
	 * All short-tags (for example <br />)
	 */
	private static final List _shortTags = Arrays.asList(new String[] {
		"area","base","basefont","br","col","frame","hr","img","input","isindex",
		"link","meta","param"
	});
	
	/**
	 * the used tokens
	 */
	private List _tokens;
	
	/**
	 * a stack with the open tags
	 */
	private Stack _openTags;
	
	/**
	 * the controller
	 */
	private Controller _con;

	/**
	 * constructor
	 *
	 * @param con the Controller of the textField
	 * @param tokens a List with all tokens to parse
	 */
	public HTMLParser(Controller con,List tokens) {
		_tokens = tokens;
		_con = con;
		
		removeNotNeededTags();
	}
	
	/**
	 * converts the HTML-tokens to BBCode
	 * 
	 * @return the converted BBCode
	 */
	public String convertToBBCode() {
		StringBuffer buf = new StringBuffer();
		_openTags = new Stack();
		int openParas = 0;
		
		for(int i = 0,len = _tokens.size();i < len;i++) {
			String token = (String)_tokens.get(i);
			char first = token.charAt(0);
			
			switch(first) {
				case '<':
					String tagName = token.substring(1).toLowerCase();
					
					if(tagName.equals("p")) {
						openParas++;
						Map attrs = new HashMap();
						i = collectAttributes(attrs,i + 1,true);
						String alignVal = (String)attrs.get("align");
						if(alignVal != null) {
							alignVal = alignVal.toLowerCase();
							if(alignVal.equals("right")) {
								closeOpenBBCodeTags(buf,"right");
								if(addToOpenedTags("p","right"))
									buf.append("[right]");
							}
							else if(alignVal.equals("center")) {
								closeOpenBBCodeTags(buf,"center");
								if(addToOpenedTags("p","center"))
									buf.append("[center]");
							}
							else if(!peekOpenedTags("li"))
								buf.append("\n");
						}
						// don't do this in lists because oowriter exports list-points
						// like that
						else if(!peekOpenedTags("li"))
							buf.append("\n");
						
						i = skipWhiteSpaceAfterTag(i - 1);
					}
					else if(tagName.equals("/p")) {
						openParas--;
						if(peekOpenedTags("p"))
							closeLastOpenedTags(buf);
						buf.append("\n");
						
						i = skipWhiteSpaceAfterTag(i);
					}
					else if(tagName.equals("font")) {
						Map attrs = new HashMap();
						i = collectAttributes(attrs,i + 1,true);
						
						List bbcodeTags = new ArrayList();
						Iterator it = attrs.entrySet().iterator();
						while(it.hasNext()) {
							Entry e = (Entry)it.next();
							String name = (String)e.getKey();
							name = name.trim().toLowerCase();
							String value = (String)e.getValue();
							
							if(name.equals("color") && _con.isTagEnabled(BBCodeTags.getIdFromTag("color"))) {
								closeOpenBBCodeTags(buf,"color");
								String param = value.substring(1,value.length() - 1);
								bbcodeTags.add(new TagEntry("color",param));
								buf.append("[color=" + param + "]");
							}
							else if(name.equals("size") && _con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
								closeOpenBBCodeTags(buf,"size");
								int param = getFontSizeForFontTag(value);
								bbcodeTags.add(new TagEntry("size",String.valueOf(param)));
								buf.append("[size=" + param + "]");
							}
							else if(name.equals("font-weight") && _con.isTagEnabled(BBCodeTags.getIdFromTag("b"))) {
								closeOpenBBCodeTags(buf,"b");
								if(value.equals("600") || value.equals("700") || value.equals("800") ||
										value.equals("900") || value.equals("bold") || value.equals("bolder")) {
									bbcodeTags.add(new TagEntry("b",""));
									buf.append("[b]");
								}
							}
							else if(name.equals("face") && _con.isTagEnabled(BBCodeTags.getIdFromTag("font"))) {
								closeOpenBBCodeTags(buf,"font");
								int lastComma = value.lastIndexOf(',');
								if(lastComma >= 0)
									value = value.substring(1,lastComma);
								else
									value = value.substring(1,value.length() - 1);
								bbcodeTags.add(new TagEntry("font",value));
								buf.append("[font=" + value + "]");
							}
						}
						
						if(bbcodeTags.size() > 0)
							addToOpenedTags("font",bbcodeTags);
					}
					else if(tagName.equals("/font")) {
						if(peekOpenedTags("font"))
							closeLastOpenedTags(buf);
					}
					else if(tagName.equals("b") || tagName.equals("i") || tagName.equals("u") ||
							tagName.equals("sub") || tagName.equals("sup")) {
						// skip attributes
						i = collectAttributes(new HashMap(),i + 1,false);

						closeOpenBBCodeTags(buf,tagName);
						if(addToOpenedTags(tagName,tagName))
							buf.append("[" + tagName + "]");
					}
					else if(tagName.equals("/b") || tagName.equals("/i") || tagName.equals("/u") ||
							tagName.equals("/sub") || tagName.equals("/sup")) {
						if(peekOpenedTags(tagName.substring(1)))
							closeLastOpenedTags(buf);
					}
					else if(tagName.equals("s") || tagName.equals("strike")) {
						// skip attributes
						i = collectAttributes(new HashMap(),i + 1,false);

						closeOpenBBCodeTags(buf,"s");
						if(addToOpenedTags(tagName,"s"))
							buf.append("[s]");
					}
					else if(tagName.equals("/s") || tagName.equals("/strike")) {
						if(peekOpenedTags(tagName.substring(1)))
							closeLastOpenedTags(buf);
					}
					else if(tagName.equals("span")) {
						Map attrs = new HashMap();
						i = collectAttributes(attrs,i + 1,true);
						if(attrs.containsKey("style")) {
							String style = (String)attrs.get("style");
							if(style.length() > 0 && (style.charAt(0) == '\'' || style.charAt(0) == '"'))
								style = style.substring(1,style.length() - 1);
							List openedTags = parseStyle(style,buf);
							if(openedTags.size() > 0)
								addToOpenedTags("span",openedTags);
						}
						
						i = skipWhiteSpaceAfterTag(i);
					}
					else if(tagName.equals("/span")) {
						if(peekOpenedTags("span"))
							closeLastOpenedTags(buf);
					}
					else if(tagName.equals("ol")) {
						Map m = new HashMap();
						i = collectAttributes(m,i + 1,true);
						
						String type = "1";
						if(m.containsKey("type")) {
							String typeVal = (String)m.get("type");
							if(typeVal.equals("a") || typeVal.equals("A") || typeVal.equals("i") ||
									typeVal.equals("I") || typeVal.equals("circle") || typeVal.equals("disc") ||
									typeVal.equals("square"))
								type = typeVal;
						}
						
						if(addToOpenedTags("ol","list",type))
							buf.append("[list=" + type + "]\n");
					}
					else if(tagName.equals("ul")) {
						// skip attributes
						i = collectAttributes(new HashMap(),i + 1,false);
						
						if(addToOpenedTags("ul","list"))
							buf.append("[list]\n");
					}
					else if(tagName.equals("/ol") || tagName.equals("/ul")) {
						if(peekOpenedTags(tagName.substring(1)))
							closeLastOpenedTags(buf);
						// fix "bug" of oo.org that the end of list-points is not specified
						else if(peekOpenedTags("li")) {
							closeLastOpenedTags(buf);
							closeLastOpenedTags(buf);
						}
						
						i = skipWhiteSpaceAfterTag(i);
					}
					else if(tagName.equals("li")) {
						if(peekOpenedTags("li"))
							closeLastOpenedTags(buf);
						
						// skip attributes
						i = collectAttributes(new HashMap(),i + 1,false);
						
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("list"))) {
							buf.append("[*]");
							addToOpenedTags("li","*");
						}
					}
					else if(tagName.equals("/li")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("list"))) {
							buf.append("\n");
							if(_openTags.size() > 0)
								_openTags.pop();
						}
					}
					else if(tagName.equals("a")) {
						Map attrs = new HashMap();
						i = collectAttributes(attrs,i + 1,true);
						if(attrs.containsKey("href")) {
							String val = (String)attrs.get("href");
							if(val.length() >= 2 && (val.charAt(0) == '"' || val.charAt(0) == '\''))
								val = val.substring(1,val.length() - 1);
							
							// is it a mail?
							if(val.startsWith("mailto:") && _con.isTagEnabled(BBCodeTags.getIdFromTag("mail"))) {
								closeOpenBBCodeTags(buf,"mail");
								String href = replaceSpecialChars(val.substring(7));
								buf.append("[mail=" + href + "]");
								addToOpenedTags("a","mail",href);
							}
							// or an url?
							else if(_con.isTagEnabled(BBCodeTags.getIdFromTag("url"))) {
								closeOpenBBCodeTags(buf,"url");
								String href = replaceSpecialChars(val);
								buf.append("[url=" + href + "]");
								addToOpenedTags("a","url",href);
							}
						}
					}
					else if(tagName.equals("/a")) {
						if(peekOpenedTags("a"))
							closeLastOpenedTags(buf);
					}
					else if(tagName.equals("img")) {
						Map attrs = new HashMap();
						i = collectAttributes(attrs,i + 1,true);
						if(attrs.containsKey("src")) {
							if(_con.isTagEnabled(BBCodeTags.getIdFromTag("img"))) {
								closeOpenBBCodeTags(buf,"img");
								String url = (String)attrs.get("src");
								if(url.length() >= 2)
									url = url.substring(1,url.length() - 1);
								buf.append("[img]" + url + "[/img]");
							}
						}
					}
					else if(tagName.equals("h1")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=25][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","25"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h1",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("h2")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=22][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","22"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h2",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("h3")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=19][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","19"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h3",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("h4")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=16][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","16"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h4",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("h5")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=13][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","13"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h5",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("h6")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							closeOpenBBCodeTags(buf,"size");
							closeOpenBBCodeTags(buf,"b");
							buf.append("[size=10][b]");
							List l = new ArrayList();
							l.add(new TagEntry("size","10"));
							l.add(new TagEntry("b",""));
							addToOpenedTags("h6",l);
						}
						i = collectAttributes(new HashMap(),i + 1,false);
					}
					else if(tagName.equals("/h1") || tagName.equals("/h2") || tagName.equals("/h3") ||
							tagName.equals("/h4") || tagName.equals("/h5") || tagName.equals("/h6")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
							Entry e = (Entry)_openTags.peek();
							if(e.getKey().equals(tagName.substring(1))) {
								closeLastOpenedTags(buf);
								buf.append("\n");
							}
						}
						i = skipWhiteSpaceAfterTag(i);
					}
					else if(tagName.equals("pre") || tagName.equals("code")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("code"))) {
							i = collectAttributes(new HashMap(),i + 1,false);
							i = skipWhiteSpaceAfterTag(i - 1);
							
							buf.append("[code]");
							addToOpenedTags(tagName,"code");
						}
					}
					else if(tagName.equals("/pre") || tagName.equals("/code")) {
						if(_con.isTagEnabled(BBCodeTags.getIdFromTag("code"))) {
							Entry e = (Entry)_openTags.peek();
							if(e.getKey().equals(tagName.substring(1)))
								closeLastOpenedTags(buf);
						}
						i = skipWhiteSpaceAfterTag(i);
					}
					else if(tagName.equals("br")) {
						// skip attributes
						i = collectAttributes(new HashMap(),i + 1,false);
						i = skipWhiteSpaceAfterTag(i - 1);
						
						buf.append("\n");
					}
					// skip tag
					else {
						if(tagName.length() > 0 && tagName.charAt(0) == '/' && !_openTags.empty()) {
							Entry e = (Entry)_openTags.peek();
							if(!_shortTags.contains(e.getKey()))
								_openTags.pop();
						}
						else if(!_shortTags.contains(tagName))
							addToOpenedTags(tagName,Collections.emptyList());
						
						// add new line
						if(tagName.equals("/div") || tagName.equals("/tr"))
							buf.append("\n");
						
						i = collectAttributes(new HashMap(),i + 1,false);
						i = skipWhiteSpaceAfterTag(i - 1);
					}
					break;
				
				case '>':
					// skip
					break;
				
				default:
					// we don't want to have whitespace at the line-start (except &nbsp;)
					if(buf.length() > 0 && buf.charAt(buf.length() - 1) == '\n')
						token = StringUtils.trimStart(token);
					
					// keep whitespace?
					token = replaceSpecialChars(token);
					if(topOnStackIsNoContentTag())
						token = StringUtils.replaceWhiteSpace(token,"");
					else if(!topOnStackIsPre()) {
						//if(openParas > 0 && token.length() > 0 && token.charAt(0) == '\n')
						//	token = "";
						token = StringUtils.replaceWhiteSpace(token," ");
					}
					
					buf.append(token);
					break;
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * determines the font-size for the bbcode-tag from a "font"-tag
	 * 
	 * @param size the font-size
	 * @return the size to use for BBCode
	 */
	private int getFontSizeForFontTag(String size) {
		if(size.length() > 0) {
			int iSize = DEFAULT_FONT_SIZE;
			if(size.charAt(0) == '-') {
				int s = parseFontSize(size.substring(1));
				if(s >= 0)
					iSize -= s - 1;
			}
			else if(size.charAt(0) == '+') {
				int s = parseFontSize(size.substring(1));
				if(s >= 0)
					iSize += s;
			}
			else {
				int s = parseFontSize(size);
				if(s >= 0)
					iSize = s;
			}
			
			iSize = Math.max(1,Math.min(iSize,7));
			return getFontSizeForFontTag(iSize);
		}
		
		return DEFAULT_FONT_SIZE;
	}
	
	/**
	 * determines the font-size for bbcode from the given font-tag-size
	 * 
	 * @param size the size from the font-tag
	 * @return the size for the bbcode-tag
	 */
	private int getFontSizeForFontTag(int size) {
		switch(size) {
			case 1:
				return 8;
			case 2:
				return 10;
			case 3:
				return 12;
			case 4:
				return 14;
			case 5:
				return 18;
			case 6:
				return 24;
			case 7:
				return 29;
		}
		
		return DEFAULT_FONT_SIZE;
	}
	
	/**
	 * tries to parse the font-size from the css-attribute font-size.
	 * 
	 * @param size the value from font-size
	 * @return the font-size to use for the bbcode-tag
	 */
	private int getFontSizeForCSS(String size) {
		if(size.equals("xx-small"))
			return getFontSizeForFontTag(1);
		if(size.equals("x-small"))
			return getFontSizeForFontTag(2);
		if(size.equals("small"))
			return getFontSizeForFontTag(3);
		if(size.equals("medium"))
			return getFontSizeForFontTag(4);
		if(size.equals("large"))
			return getFontSizeForFontTag(5);
		if(size.equals("x-large"))
			return getFontSizeForFontTag(6);
		if(size.equals("xx-large"))
			return getFontSizeForFontTag(7);
		
		if(size.endsWith("pt") || size.endsWith("px")) {
			int iSize = parseFontSize(size.substring(0,size.length() - 2));
			if(iSize < 0)
				return DEFAULT_FONT_SIZE;
			
			return Math.max(8,Math.min(29,iSize));
		}
		
		return DEFAULT_FONT_SIZE;
	}
	
	/**
	 * parses an integer from the given string
	 * 
	 * @param size the string-size
	 * @return the integer or -1 if an error occurred
	 */
	private int parseFontSize(String size) {
		try {
			return Integer.parseInt(size.trim());
		}
		catch(NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * replaces the html-special-chars
	 * 
	 * @param input the input-string
	 * @return the result-string
	 */
	private String replaceSpecialChars(String input) {
		return StringUtils.htmlStringToString(input);
	}
	
	/**
	 * Determines wether the top element is a not-content-tag
	 * 
	 * @return true if so
	 */
	private boolean topOnStackIsNoContentTag() {
		if(_openTags.size() > 0) {
			Entry e = (Entry)_openTags.peek();
			String key = (String)e.getKey();
			return _noContentTags.contains(key.toLowerCase());
		}
		
		return false;
	}
	
	/**
	 * Determines wether the top element is a pre-tag
	 * 
	 * @return true if so
	 */
	private boolean topOnStackIsPre() {
		if(_openTags.size() > 0) {
			Entry e = (Entry)_openTags.peek();
			String key = (String)e.getKey();
			return key.equals("pre");
		}
		
		return false;
	}
	
	/**
	 * checks wether the given html-tag is on the top of the stack
	 * 
	 * @param htmlTag the html-tag to check
	 * @return true if the given html-tag is on the top of the stack
	 */
	private boolean peekOpenedTags(String htmlTag) {
		if(_openTags.size() > 0) {
			Entry e = (Entry)_openTags.peek();
			return e.getKey().equals(htmlTag);
		}
		
		return false;
	}
	
	/**
	 * Walks through the opened tags and closes the given tag, if it is open
	 * 
	 * @param buf the buffer to append the closing-tags to
	 * @param tag the bbcode-tag
	 */
	private void closeOpenBBCodeTags(StringBuffer buf,String tag) {
		ListIterator it = _openTags.listIterator(_openTags.size());
		for(int i = _openTags.size() - 1;it.hasPrevious();i--) {
			Entry e = (Entry)it.previous();
			List l = (List)e.getValue();
			ListIterator lit = l.listIterator();
			for(int a = 0;lit.hasNext();a++) {
				Entry openTag = (Entry)lit.next();
				
				// is this the tag we want to close?
				if(openTag.getKey().equals(tag)) {
					// remove the tags after this one
					for(int x = _openTags.size() - 1;x > i;x--) {
						Entry ex = (Entry)_openTags.get(x);
						List lx = (List)ex.getValue();
						ListIterator litx = lx.listIterator(lx.size());
						while(litx.hasPrevious()) {
							Entry openTagX = (Entry)litx.previous();
							buf.append("[/" + openTagX.getKey() + "]");
						}
					}

					// remove the other tags in the list
					for(int x = l.size() - 1;x > a;x--) {
						Entry openTagY = (Entry)l.get(x);
						buf.append("[/" + openTagY.getKey() + "]");
					}
					
					buf.append("[/" + openTag.getKey() + "]");
					l.remove(openTag);
					
					// note that we have removed an entry. so we run until from a
					for(int x = a;x < l.size();x++) {
						Entry openTagY = (Entry)l.get(x);
						buf.append("[" + openTagY.getKey());
						if(!openTagY.getValue().equals(""))
							buf.append("=" + openTagY.getValue());
						buf.append("]");
					}
					
					// add the other tags again
					for(int x = i;x < _openTags.size();x++) {
						Entry ex = (Entry)_openTags.get(x);
						List lx = (List)ex.getValue();
						ListIterator litx = lx.listIterator();
						while(litx.hasNext()) {
							Entry openTagX = (Entry)litx.next();
							buf.append("[" + openTagX.getKey());
							if(!openTagX.getValue().equals(""))
								buf.append("=" + openTagX.getValue());
							buf.append("]");
						}
					}
					
					return;
				}
			}
		}
	}
	
	/**
	 * closes all bbcode-tags which are in the top-element of the stack
	 * 
	 * @param buf the buffer to append the closing-tags to
	 */
	private void closeLastOpenedTags(StringBuffer buf) {
		if(_openTags.size() > 0) {
			Entry e = (Entry)_openTags.pop();
			List l = (List)e.getValue();
			ListIterator it = l.listIterator(l.size());
			while(it.hasPrevious()) {
				Entry bbc = (Entry)it.previous();
				if(bbc.getKey().equals("*"))
					buf.append("\n");
				else
					buf.append("[/" + bbc.getKey() + "]");
			}
		}
	}
	
	/**
	 * adds the given tags to the opened tags
	 * 
	 * @param htmlTag the name of the html-tag
	 * @param bbcodeTag the name of the added bbcode-tag
	 * @return true if the tag has been added
	 */
	private boolean addToOpenedTags(String htmlTag,String bbcodeTag) {
		return addToOpenedTags(htmlTag,bbcodeTag,"");
	}
	
	/**
	 * adds the given tags to the opened tags
	 * 
	 * @param htmlTag the name of the html-tag
	 * @param bbcodeTag the name of the added bbcode-tag
	 * @param param the parameter of the bbcode-tag
	 * @return true if the tag has been added
	 */
	private boolean addToOpenedTags(String htmlTag,String bbcodeTag,String param) {
		if(bbcodeTag.equals("*") || _con.isTagEnabled(BBCodeTags.getIdFromTag(bbcodeTag))) {
			List l = new ArrayList();
			l.add(new TagEntry(bbcodeTag,param));
			addToOpenedTags(htmlTag,l);
			return true;
		}
		
		return false;
	}

	/**
	 * adds the given tags to the opened tags
	 * 
	 * @param htmlTag the name of the html-tag
	 * @param bbcodeTags a list with the names of all added bbcodetags
	 */
	private void addToOpenedTags(String htmlTag,List bbcodeTags) {
		TagEntry e = new TagEntry(htmlTag,bbcodeTags);
		_openTags.push(e);
	}
	
	private static final class TagEntry implements Entry {

		private Object _key;
		private Object _value;
		
		/**
		 * constructor
		 * 
		 * @param key the key
		 * @param value the value
		 */
		public TagEntry(Object key,Object value) {
			_key = key;
			_value = value;
		}
		
		public Object getKey() {
			return _key;
		}

		public Object getValue() {
			return _value;
		}

		public Object setValue(Object o) {
			Object old = _value;
			_value = o;
			return old;
		}
	}
	
	/**
	 * parses the given style-value, adds the corresponding BBCode-tags to the given buffer
	 * and returns a list with the opened tags
	 * 
	 * @param style the style-value
	 * @param buf the buffer to append the BBCodes to
	 * @return a list with the opened tags
	 */
	private List parseStyle(String style,StringBuffer buf) {
		List tags = new ArrayList();
		String[] cssAttrs = StringUtils.simpleSplit(style,";");
		for(int x = 0;x < cssAttrs.length;x++) {
			String[] cssAttr = StringUtils.simpleSplit(cssAttrs[x],":");
			// skip invalid attributes
			if(cssAttr.length < 2)
				continue;
			
			String cssName = cssAttr[0].toLowerCase().trim();
			String cssValue = cssAttr[1].trim();
			
			if(cssName.equals("font-family") && _con.isTagEnabled(BBCodeTags.getIdFromTag("font"))) {
				closeOpenBBCodeTags(buf,"font");
				int lastComma = cssValue.lastIndexOf(',');
				if(lastComma >= 0)
					cssValue = cssValue.substring(1,lastComma);
				tags.add(new TagEntry("font",cssValue));
				
				buf.append("[font=" + cssValue + "]");
			}
			else if(cssName.equals("font-size")) {
				if(_con.isTagEnabled(BBCodeTags.getIdFromTag("size"))) {
					closeOpenBBCodeTags(buf,"size");
					int param = getFontSizeForCSS(cssValue);
					tags.add(new TagEntry("size",String.valueOf(param)));
					buf.append("[size=" + param + "]");
				}
			}
			else if(cssName.equals("color")) {
				if(_con.isTagEnabled(BBCodeTags.getIdFromTag("color"))) {
					closeOpenBBCodeTags(buf,"color");
					tags.add(new TagEntry("color",cssValue));
					buf.append("[color=" + cssValue + "]");
				}
			}
			else if(cssName.equals("background-color") || cssName.equals("background")) {
				if(_con.isTagEnabled(BBCodeTags.getIdFromTag("bgcolor"))) {
					closeOpenBBCodeTags(buf,"bgcolor");
					tags.add(new TagEntry("bgcolor",cssValue));
					buf.append("[bgcolor=" + cssValue + "]");
				}
			}
			else if(cssName.equals("font-weight")) {
				if(cssValue.toLowerCase().equals("bold") &&
						_con.isTagEnabled(BBCodeTags.getIdFromTag("b"))) {
					closeOpenBBCodeTags(buf,"b");
					tags.add(new TagEntry("b",""));
					buf.append("[b]");
				}
			}
			else if(cssName.equals("font-style")) {
				if(cssValue.toLowerCase().equals("italic") &&
						_con.isTagEnabled(BBCodeTags.getIdFromTag("i"))) {
					closeOpenBBCodeTags(buf,"i");
					tags.add(new TagEntry("i",""));
					buf.append("[i]");
				}
			}
			else if(cssName.equals("text-decoration")) {
				if(cssValue.toLowerCase().equals("underline") &&
						_con.isTagEnabled(BBCodeTags.getIdFromTag("u"))) {
					closeOpenBBCodeTags(buf,"u");
					tags.add(new TagEntry("u",""));
					buf.append("[u]");
				}
			}
		}
		
		return tags;
	}
	
	/**
	 * collects the attributes in the given map, starting at the given position
	 * 
	 * @param m the map to fill
	 * @param i the position where to start
	 * @param collect do you really want to collect the attributes or just skip them?
	 * @return the position after the collection
	 */
	private int collectAttributes(Map m,int i,boolean collect) {
		for(int len = _tokens.size();i < len;i++) {
			String token = (String)_tokens.get(i);
			if(collect && token.equals("=")) {
				String name = (String)_tokens.get(i - 1);
				name = name.trim().toLowerCase();
				
				StringBuffer sval = new StringBuffer();
				String value = (String)_tokens.get(i + 1);
				sval.append(value);
				if(value.startsWith("\"") && !value.endsWith("\"")) {
					for(i++;i < len;i++) {
						String s = (String)_tokens.get(i + 1);
						if(s.startsWith("\"")) {
							sval.append("\"");
							i--;
							break;
						}
						sval.append(s);
					}
				}
				
				m.put(name,sval.toString());
				
				// walk to the next token
				i++;
			}
			// have we reached the end?
			else if(token.equals(">")) {
				i--;
				break;
			}
		}
		
		return i;
	}
	
	/**
	 * removes all not needed tags
	 */
	private void removeNotNeededTags() {
		List newTokens = new ArrayList();
		
		boolean hasHTMLTag = false;
		boolean inBodyTag = true;
		for(int i = 0,len = _tokens.size();i < len;i++) {
			String token = (String)_tokens.get(i);
			char first = token.charAt(0);
			
			switch(first) {
				case '<':
					if(token.toLowerCase().equals("<!doctype")) {
						inBodyTag = false;
						hasHTMLTag = true;
						i = runToTagEnd(i);
					}
					else if(token.startsWith("<!--")) {
						i = runToCommentEnd(i);
					}
					else if(token.toLowerCase().equals("<html")) {
						inBodyTag = false;
						hasHTMLTag = true;
						i = runToTagEnd(i);
					}
					else if(token.toLowerCase().equals("<body")) {
						inBodyTag = true;
						i = runToTagEnd(i);
					}
					else if(token.toLowerCase().equals("</body")) {
						inBodyTag = false;
						i = runToTagEnd(i);
					}
					else if(token.toLowerCase().equals("<style")) {
						i = runToClosingTag(i,"style");
					}
					else {
						if(!inBodyTag)
							i = runToTagEnd(i);
						else
							newTokens.add(token);
					}
					break;
				
				default:
					if(inBodyTag)
						newTokens.add(token);
					break;
			}
		}
		
		if(hasHTMLTag) {
			// remove whitespace at the beginning
			while(newTokens.size() > 0 && StringUtils.isWhiteSpace((String)newTokens.get(0)))
				newTokens.remove(0);
			
			// remove whitespace at the end
			while(newTokens.size() > 0 && StringUtils.isWhiteSpace((String)newTokens.get(newTokens.size() - 1)))
				newTokens.remove(newTokens.size() - 1);
		}
		
		_tokens = newTokens;
	}
	
	/**
	 * runs to the next not-whitespace token
	 * 
	 * @param i the method starts checking with i + 2
	 * @return the position before the next not whitespace
	 */
	private int skipWhiteSpaceAfterTag(int i) {
		i += 2;
		String sToken = " ";
		while(i < _tokens.size() && StringUtils.isWhiteSpace(sToken)) {
			sToken = (String)_tokens.get(i);
			i++;
		}
		
		// we don't want to skip the next not-whitespace
		return i - 2;
	}
	
	private int runToCommentEnd(int i) {
		i++;
		for(int len = _tokens.size();i < len;i++) {
			String tok = (String)_tokens.get(i);
			if(tok.equals(">") && i > 1) {
				String prev = (String)_tokens.get(i - 1);
				if(prev.endsWith("--"))
					break;
			}
		}
		
		return i;
	}
	
	/**
	 * Runs the to closing-tag of the tag with given name
	 * 
	 * @param i the position where to start
	 * @param name the name of the tag
	 * @return the position of the end-tag
	 */
	private int runToClosingTag(int i,String name) {
		i++;
		for(int len = _tokens.size();i < len;i++) {
			String token = (String)_tokens.get(i);
			if(token.toLowerCase().equals("</" + name))
				break;
		}
		
		return i;
	}
	
	/**
	 * runs to the end-tag
	 * 
	 * @param i the position where to start
	 * @return the position of the end-tag
	 */
	private int runToTagEnd(int i) {
		i++;
		for(int len = _tokens.size();i < len;i++) {
			String token = (String)_tokens.get(i);
			if(token.charAt(0) == '>')
				break;
		}
		
		return i;
	}
}