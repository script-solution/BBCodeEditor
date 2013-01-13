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

package bbcodeeditor.control.export.bbcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.Environment;
import bbcodeeditor.control.SecSmiley;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.bbcode.blocks.*;
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;
import bbcodeeditor.control.export.bbcode.tags.ExtendedTag;
import bbcodeeditor.control.export.bbcode.tags.SimpleTag;



/**
 * this class converts the tokens to an ArrayList with objects of the
 * corresponding BBCode-tags and strings with the remaining text
 * 
 * @author hrniels 
 */
public final class BBCodeParser {

	/**
	 * no error :)
	 */
	public static final int ERR_NO_ERROR						= -1;
	
	/**
	 * invalid content for a tag and we don't want to treat it as plain-text
	 */
	public static final int ERR_INVALID_CONTENT			= 0;
	
	/**
	 * a tag has been nested with itself
	 */
	public static final int ERR_NESTED_TAG					= 1;
	
	/**
	 * at least one open tag is missing
	 */
	public static final int ERR_MISS_OPEN_TAG				= 2;
	
	/**
	 * some tags have been closed in the wrong order
	 */
	public static final int ERR_WRONG_CLOSE_ORDER		= 3;
	
	/**
	 * the maximum nest-level has been reached
	 */
	public static final int ERR_MAX_NEST_LEVEL			= 4;

	/**
	 * a closing tag is missing
	 */
	public static final int ERR_MISSING_CLOSING_TAG	= 5;
	
	
	/**
	 * The controller
	 */
	private final Controller _con;

	/**
	 * the token-list
	 */
	private final List _tokens;
	
	/**
	 * the list of tags (will be created)
	 */
	private List _result;
	
	/**
	 * the recursive Block-structure (will be created)
	 */
	private Block _base;
	
	/**
	 * constructor
	 * the constructor will perform the action
	 * 
	 * @param con the Controller
	 * @param tokens the tokens
	 */
	public BBCodeParser(Controller con,List tokens) {
		_con = con;
		_tokens = tokens;
	}
	
	/**
	 * builds the error-message for the given error-type
	 * 
	 * @param error the error-type
	 * @return the error-message
	 */
	public static String getErrorMsg(int error) {
		switch(error) {
			case ERR_INVALID_CONTENT:
				return "Open tags at a opening-tag which does not allow open tags";
			
			case ERR_MAX_NEST_LEVEL:
				return "The maximum nested level has been reached";
				
			case ERR_MISS_OPEN_TAG:
				return "More closing tags than opening tags";
				
			case ERR_NESTED_TAG:
				return "A nested tag (...[b]...[b]...[/b]...[/b]...)";
				
			case ERR_WRONG_CLOSE_ORDER:
				return "Wrong closing-tag order";
			
			case ERR_MISSING_CLOSING_TAG:
				return "Missing closing tag";
				
			default:
				return "No error :)";
		}
	}
	
	/**
	 * parses and executes the tags with the given controller
	 * 
	 * @return the error-code. see ERR_*
	 */
	public int parse() {
		int error = convert();
		if(error == ERR_NO_ERROR) {
			removeNotNeededTags();
			
			_base = new Block(null,null,null);
			connectTags(0,_base);
			
			error = _base.checkForSyntaxError(_con);
			if(error == ERR_NO_ERROR) {
				_con.setReplaceSmileys(false);
				
				_base.parseContent(_con,new TextAttributes());
				
				_con.setReplaceSmileys(true);
			}
		}
		
		return error;
	}
	
	/**
	 * connects and nests the tags to blocks
	 * 
	 * @param pos the position
	 * @param parent the parent-block
	 */
	private int connectTags(int pos,Block parent) {
		int len = _result.size();
		int i;
		for(i = pos;i < len;i++) {
			Object obj = _result.get(i);
			
			if(obj instanceof BBCodeTag) {
				BBCodeTag start = (BBCodeTag)obj;
				
				// break here if we're at the end of the parent-block
				if(parent.getClosingTag() != null && start.isClosingtag() &&
						start.getName().equals(parent.getClosingTag().getName()))
					return i;
				
				// check if the tag is enabled
				int id = BBCodeTags.getIdFromTag(start.getName());
				if(_con.isTagEnabled(id)) {
					BBCodeTag end = null;
					if(!start.isClosingtag()) {
						// search for the closing-tag
						for(int a = i + 1;a < len;a++) {
							Object potEnd = _result.get(a);
							
							if(potEnd instanceof BBCodeTag) {
								end = (BBCodeTag)potEnd;
								// is the tag-name the same and is it a closing-tag?
								if(end.getName().equals(start.getName()) && end.isClosingtag())
									break;
							}
						}
						
						// break here if the closing-tag has not been found
						if(end != null) {
							Block block = null;
						
							// create the new block
							switch(id) {
								case BBCodeTags.BOLD:
								case BBCodeTags.ITALIC:
								case BBCodeTags.UNDERLINE:
								case BBCodeTags.STRIKE:
									block = new SimpleBlock(parent,(SimpleTag)start,(SimpleTag)end);
									break;
								case BBCodeTags.FONT_SIZE:
									block = new FontSizeBlock(parent,(ExtendedTag)start,(ExtendedTag)end);
									break;
								case BBCodeTags.FONT_FAMILY:
									block = new FontFamilyBlock(parent,(ExtendedTag)start,(ExtendedTag)end);
									break;
								case BBCodeTags.SUBSCRIPT:
									block = new SubScriptBlock(parent,(SimpleTag)start,(SimpleTag)end);
									break;
								case BBCodeTags.SUPERSCRIPT:
									block = new SuperScriptBlock(parent,(SimpleTag)start,(SimpleTag)end);
									break;
								case BBCodeTags.BG_COLOR:
								case BBCodeTags.FONT_COLOR:
									block = new FontColorBlock(parent,(ExtendedTag)start,(ExtendedTag)end);
									break;
								case BBCodeTags.EMAIL:
								case BBCodeTags.URL:
									block = new LinkBlock(parent,start,end);
									break;
								case BBCodeTags.QUOTE:
									block = new QuoteBlock(parent,start,end);
									break;
								case BBCodeTags.CODE:
									block = new CodeBlock(parent,start,end);
									break;
								case BBCodeTags.LIST:
									block = new ListBlock(parent,start,end);
									break;
								case BBCodeTags.IMAGE:
									block = new ImageBlock(parent,(SimpleTag)start,(SimpleTag)end);
									break;
								case BBCodeTags.RIGHT:
								case BBCodeTags.CENTER:
								case BBCodeTags.LEFT:
									block = new AlignmentBlock(parent,start,end);
									break;
							}
							
							if(block != null) {
								// recursiv call which collects the content of this block
								i = connectTags(i + 1,block);
								
								// add the block to content
								BlockContent content = new BlockContent(block);
								parent.addContent(content);
								continue;
							}
						}
					}
				}
			}
			
			// is it a smiley?
			SecSmiley smiley;
			String potSmileyCode = String.valueOf(obj);
			if((smiley = _con.getSmileys().getSmileyByCode(potSmileyCode)) != null) {
				SmileyContent content = new SmileyContent(potSmileyCode,smiley);
				parent.addContent(content);
			}
			else {
				// add a plain-content
				PlainContent content = new PlainContent(obj.toString());
				parent.addContent(content);
			}
		}
		
		return i;
	}
	
	/**
	 * the main convert-method
	 * 
	 * @return the error-code. see ERR_*
	 */
	private int convert() {
		Stack openTags = new Stack();
		
		// we have to take care of the current location because we may be at a location
		// where some block-types are not allowed
		Environment env = _con.getCurrentEnvironment();
		List allowedTagsRoot = new ArrayList();
		if(env.containsStyles())
		{
			allowedTagsRoot.add(Block.TYPE_INLINE);
			allowedTagsRoot.add(Block.TYPE_URL);
		}
		if(env.containsSubEnvironments())
			allowedTagsRoot.add(Block.TYPE_BLOCK);
		
		List allowedTags = allowedTagsRoot;
		
		_result = new ArrayList();
		int len = _tokens.size();
		for(int i = 0;i < len;i++) {
			String current = (String)_tokens.get(i);
			
			// are we at a bbcode-tag?
			if(current.equals("[")) {
				String test = getValue(i + 1);
				boolean isClosingTag = test != null && test.equals("/");
				String name = getTagName(isClosingTag,i);
				
				// is this an empty tag?
				if(test == null || name == null) {
					i = addTagAsText(i);
					continue;
				}
				
				// is the tag allowed here? if not, treat it as plain-text
				String lname = name.toLowerCase();
				Integer type = BBCodeTags.getBlockType(lname);
				if(!name.equals("*") && !isClosingTag && !allowedTags.contains(type)) {
					_result.add(current);
					continue;
				}
				
				int id = BBCodeTags.getIdFromTag(lname);
				// is this tag a known BBCode-tag?
				if(id == BBCodeTags.UNDEFINED) {
					// we have to handle [*] different because we need it as one token
					if(name.equals("*"))
						i = addTagAsText(i);
					else {
						_result.add("[" + test);
						i++;
					}
					continue;
				}
				
				// calculate the type and the parts of the tag
				int ptype = BBCodeTags.getTagParamType(id);
				String compare = getValue(i + 2);
				String value = getTagValue(isClosingTag,ptype,i);
				
				// break here if it is an extended tag but has no =xxx
				if(ptype == BBCodeTags.TYPE_EXTENDED && !isClosingTag &&
					 (compare == null || !compare.equals("=") || value == null)) {
					i = addTagAsText(i);
					continue;
				}
				// simple tag but with parameter?
				else if(ptype == BBCodeTags.TYPE_SIMPLE && compare != null &&
								compare.equals("=")) {
					i = addTagAsText(i);
					continue;
				}
				
				if(!isClosingTag) {
					// save the tag-id in the stack to remember the last opening tags
					openTags.push(new Integer(id));
					
					allowedTags = BBCodeTags.getAllowedTypes(lname);
				}
				else {
					Integer topId = null;
					if(openTags.size() > 0)
						topId = (Integer)openTags.peek();
					else
						topId = new Integer(BBCodeTags.UNDEFINED);
					
					// is this the last tag we have opened?
					if(topId == null || !topId.equals(new Integer(id))) {
						// if the tag is not allowed here we treat it as plain-text
						if(!allowedTags.contains(type)) {
							i = addTagAsText(i);
							continue;
						}

						// ok, the order is wrong. So we try to correct it. We do this by swapping the current
						// tag with the one that we except. If there is no such tag or an opening tag is
						// in front of it we report an error (missing closing tag).

						// search the following tags for the required closing-tag
						int swapid = -1;
						int swapPos = -1;
						String swapName = null;
						for(int a = i + 5;a < len;a += 4) {
							String tok = (String)_tokens.get(a);
							// if it is an opening-tag we stop here
							if(!tok.equals("/"))
								break;

							// have we found the tag?
							tok = (String)_tokens.get(a + 1);
							swapName = tok.toLowerCase();
							swapid = BBCodeTags.getIdFromTag(swapName);
							if(topId.equals(new Integer(swapid))) {
								swapPos = a + 1;
								break;
							}
						}

						// no tag found?
						if(swapPos == -1)
							return ERR_MISSING_CLOSING_TAG;
						
						// swap the tags (just the names)
						String t = (String)_tokens.get(i + 2);
						_tokens.set(i + 2,_tokens.get(swapPos));
						_tokens.set(swapPos,t);
						
						// we have to refresh some values because we swapped 2 elements
						id = swapid;
						name = swapName;
						ptype = BBCodeTags.getTagParamType(id);
						compare = getValue(i + 2);
						value = getTagValue(isClosingTag,ptype,i);
						
						// otherwise there is an error in the bbcode
						//return ERR_WRONG_CLOSE_ORDER;
					}
					
					// break here if this tags has no opening tag
					if(!openTags.contains(new Integer(id)))
						return ERR_MISS_OPEN_TAG;
					
					try {
						// add the closing-tags of the last, not closed opening tags in front of this closing-tag
						int lastOpenTagID = ((Integer)openTags.pop()).intValue();
						while(lastOpenTagID != id) {
							int lastOpenTagType = BBCodeTags.getTagParamType(lastOpenTagID);
							addByType(i,lastOpenTagType,lastOpenTagID,true,
									BBCodeTags.getTagFromID(lastOpenTagID),"",null);
							lastOpenTagID = ((Integer)openTags.pop()).intValue();
						}
						
						// set the allowed tags for the parent-tag
						if(openTags.size() == 0)
							allowedTags = allowedTagsRoot;
						else {
							Integer newTopId = (Integer)openTags.peek();
							String newTopName = BBCodeTags.getTagFromID(newTopId.intValue());
							allowedTags = BBCodeTags.getAllowedTypes(newTopName);
						}
					}
					catch(Exception e) {
						i = addTagAsText(i);
						continue;
					}
				}
				
				// add this tag
				i = addByType(i,ptype,id,isClosingTag,name,value,compare);
			}
			// add the string
			else
				_result.add(current);
		}
		
		// close all remaining open tags
		try {
			int i = 0;
			while(openTags.size() > 0) {
				int id = ((Integer)openTags.pop()).intValue();
				int type = BBCodeTags.getTagParamType(id);
				addByType(i,type,id,true,BBCodeTags.getTagFromID(id),"",null);
			}
		}
		catch(Exception e) {
			
		}
		
		return ERR_NO_ERROR;
	}
	
	/**
	 * removes the tags which are not needed, which means that the
	 * closing-tags which have no opening-tag will be removed
	 */
	private void removeNotNeededTags() {
		for(int i = 0,len = _result.size();i < len;i++) {
			Object o = _result.get(i);
			if(o instanceof BBCodeTag) {
				BBCodeTag b = (BBCodeTag)o;
				
				// is it a closing tag?
				if(b.isClosingtag()) {
					// remove the tag if it has no opening-tag
					if(!hasStartTag(b.getName(),i))
						_result.set(i,"[/" + b.getName() + "]");
				}
			}
		}
	}
	
	/**
	 * @param tag the tag-name
	 * @param pos the position to start
	 * @return true if the given tag has an opening-tag
	 */
	private boolean hasStartTag(String tag,int pos) {
		for(pos--;pos >= 0;pos--) {
			Object o = _result.get(pos);
			if(o instanceof BBCodeTag) {
				BBCodeTag b = (BBCodeTag)o;
				if(!b.isClosingtag() && b.getName().equals(tag))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param isClosingTag will be true if the current tag is a closing tag
	 * @param type the tag-type
	 * @param i the current position
	 * @return the value-part of the tag
	 */
	private String getTagValue(boolean isClosingTag,int type,int i) {
		if(isClosingTag)
			return null;
		
		switch(type) {
			case BBCodeTags.TYPE_SIMPLE:
				return null;
			
			default:
				return getValue(i + 3);
		}
	}
	
	/**
	 * @param isClosingTag will be true if the current tag is a closing tag
	 * @param i the current position
	 * @return the name-part of the tag
	 */
	private String getTagName(boolean isClosingTag,int i) {
		return getValue(isClosingTag ? i + 2 : i + 1);
	}
	
	/**
	 * adds the given tag to the list
	 * 
	 * @param i the current position
	 * @param type the type of the tag
	 * @param id the id of the tag
	 * @param isClosingTag is it a closing tag?
	 * @param name the name of the tag
	 * @param value the value of the tag
	 * @param compare the compare-part
	 * @return the new position
	 */
	private int addByType(int i,int type,int id,boolean isClosingTag,String name,String value,
			String compare) {
		// correct the type if it is a both-type
		if(type == BBCodeTags.TYPE_BOTH) {
			if(compare != null && compare.equals("=") && value != null)
				type = BBCodeTags.TYPE_EXTENDED;
			else
				type = BBCodeTags.TYPE_SIMPLE;
		}
		
		Integer attribute = BBCodeTags.getAttributeOfID(id);
		switch(type) {
			case BBCodeTags.TYPE_SIMPLE:
				_result.add(new SimpleTag(attribute,name,isClosingTag));
				return i + (isClosingTag ? 3 : 2);

			case BBCodeTags.TYPE_EXTENDED:
				_result.add(new ExtendedTag(attribute,BBCodeTags.getValueTypeFromID(id),name,
						value,isClosingTag));
				return i + (isClosingTag ? 3 : 4);
		}
		
		return i;
	}
	
	/**
	 * adds all text until ] will be found
	 * 
	 * @param i the position of the start-tag
	 * @return the new position
	 */
	private int addTagAsText(int i) {
		StringBuffer collect = new StringBuffer();
		String end;
		int len = _tokens.size();
		do {
			end = (String)_tokens.get(i);
			collect.append(end);
			i++;
		} while(i < len && !end.equals("]"));
		
		_result.add(collect.toString());
		return i - 1;
	}
	
	/**
	 * this is used to be sure not to leave the bounds of the ArrayList
	 * 
	 * @param i the current position
	 * @return the String at given position
	 */
	private String getValue(int i) {
		if(i >= _tokens.size())
			return null;
		
		return (String)_tokens.get(i);
	}
}