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

package bbcodeeditor.control.export.bbcode.blocks;

import java.util.*;

import bbcodeeditor.control.*;
import bbcodeeditor.control.export.bbcode.BBCodeParser;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;



/**
 * @author Assi Nilsmussen
 *
 */
public class Block {
	
	/**
	 * the inline-type
	 */
	public static final Integer TYPE_INLINE		= new Integer(0);
	
	/**
	 * the url-type
	 */
	public static final Integer TYPE_URL			= new Integer(1);
	
	/**
	 * the block-type
	 */
	public static final Integer TYPE_BLOCK		= new Integer(2);
	
	/**
	 * an undefined type
	 */
	public static final Integer TYPE_UNDEF		= new Integer(3);
	
	
	/**
	 * the parent-block
	 */
	protected final Block _parent;
	
	/**
	 * the opening tag
	 */
	protected final BBCodeTag _openingTag;
	
	/**
	 * the closing-tag
	 */
	protected final BBCodeTag _closingTag;
	
	/**
	 * a list with all allowed content-types
	 */
	protected final List _allowedTypes;
	
	/**
	 * the type of this block
	 */
	protected Integer _type = TYPE_INLINE;
	
	/**
	 * a list with all content-blocks
	 */
	protected List _content = new ArrayList();
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening BBCodeTag of this block
	 * @param closingTag the closing BBCodeTag of this block
	 */
	public Block(Block parent,BBCodeTag openingTag,BBCodeTag closingTag) {
		_parent = parent;
		_openingTag = openingTag;
		_closingTag = closingTag;
		if(openingTag != null)
			_allowedTypes = BBCodeTags.getAllowedTypes(openingTag.getName());
		else {
			_allowedTypes = Arrays.asList(
					new Integer[] {Block.TYPE_BLOCK,Block.TYPE_INLINE,Block.TYPE_URL}
			);
		}
		
		checkValue();
	}
	
	/**
	 * @return the type of this block
	 */
	public Integer getType() {
		return _type;
	}
	
	/**
	 * @return a list with all allowed content-types
	 */
	public List getAllowedTypes() {
		return _allowedTypes;
	}

	/**
	 * checks the value of the starting-tag if it is an extended tag
	 */
	protected void checkValue() {
		// do nothing by default
	}
	
	/**
	 * determines the allowed types in this block with the given types.<br>
	 * If the given types contain values which are not allowed here the internal
	 * types-list will be used. otherwise the given list will be used.
	 * 
	 * @param allowed the list with the currently allowed blocks
	 * @return the allowed types in this block
	 */
	protected List getAllowedContentWith(List allowed) {
		if(_allowedTypes.size() < allowed.size())
			return _allowedTypes;
		
		return allowed;
	}
	
	/**
	 * @return wether this tag can be nested with itself
	 */
	protected boolean allowNesting() {
		return false;
	}
	
	/**
	 * @return the number of content-blocks
	 */
	public int getContentLength() {
		return _content.size();
	}
	
	/**
	 * @return an ArrayList with the content-objects
	 */
	public List getContent() {
		return _content;
	}
	
	/**
	 * adds the given content-object to the list
	 * 
	 * @param content a Content-object
	 */
	public void addContent(Content content) {
		_content.add(content);
	}
	
	/**
	 * @return the opening-tag of this block
	 */
	public BBCodeTag getOpeningTag() {
		return _openingTag;
	}
	
	/**
	 * @return the closing-tag of this block
	 */
	public BBCodeTag getClosingTag() {
		return _closingTag;
	}
	
	/**
	 * checks the syntax of the blocks
	 * 
	 * @param con the Controller
	 * @return the error-code. see Parser.ERR_*
	 */
	public int checkForSyntaxError(Controller con) {
		return checkForSyntaxError(con,Block.TYPE_BLOCK,_allowedTypes);
	}
	
	/**
	 * checks the syntax of the blocks
	 * 
	 * @param con the Controller
	 * @param type the type of the parent-block
	 * @param allowedTypes the currently allowed types
	 * @return the error-code. see Parser.ERR_*
	 */
	protected int checkForSyntaxError(Controller con,Integer type,List allowedTypes) {
		// we have to add an empty content if there is nothing
		if(_content.size() == 0)
			addContent(new PlainContent(""));
		
		boolean allowed = true;
		if(_openingTag != null) {
			allowed = allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName()));
			if(!allowed && type.equals(Block.TYPE_INLINE))
				return BBCodeParser.ERR_INVALID_CONTENT;
		}
		
		int maxNestingLevel = con.getMaxTagNestingLevel();
		for(int i = 0,len = _content.size();i < len;i++) {
			Content content = (Content)_content.get(i);
			
			if(content instanceof BlockContent) {
				Block b = (Block)content.getValue();
				
				// can we nest this tag?
				if(!allowNesting() && _openingTag != null &&
						b.getOpeningTag().getName().equals(_openingTag.getName()))
					return BBCodeParser.ERR_NESTED_TAG;
				
				// nested more than allowed?
				if(_openingTag != null && getNestedNum(_openingTag.getName()) > maxNestingLevel)
					return BBCodeParser.ERR_MAX_NEST_LEVEL;
				
				// just pass the type of this block to the intern block
				// if this block is allowed in the parent-tag
				Integer nType = allowed ? getType() : type;
				int error = b.checkForSyntaxError(con,nType,getAllowedContentWith(allowedTypes));
				if(error != BBCodeParser.ERR_NO_ERROR)
					return error;
			}
		}
		
		return BBCodeParser.ERR_NO_ERROR;
	}
	
	/**
	 * determines the number of nested tags with the given tag-name
	 * 
	 * @param tagName the tag-name to search for
	 * @return the nest count
	 */
	private int getNestedNum(String tagName) {
		int num = 2;
		Block parent = _parent;
		while(parent._openingTag != null) {
			if(parent._openingTag.getName().equals(tagName))
				num++;
			
			parent = parent._parent;
		}
		
		return num;
	}
	
	/**
	 * parses the content
	 * 
	 * @param con the Controller
	 * @param attributes the attributes to start with
	 */
	public void parseContent(Controller con,TextAttributes attributes) {
		// determine the allowed types in the current environment
		// because if we are for example in a code-environment we want to interpret
		// the inserted bbcodes in an other way than in quote-envs
		Environment env = con.getCurrentEnvironment();
		List allowed = new ArrayList();
		if(env.containsStyles())
		{
			allowed.add(TYPE_INLINE);
			allowed.add(TYPE_URL);
		}
		if(env.containsSubEnvironments())
			allowed.add(TYPE_BLOCK);
		
		boolean replaceSmileys = env.containsStyles();
		parseContent(con,attributes,allowed,ParagraphAttributes.ALIGN_UNDEF,false,replaceSmileys);
	}
	
	/**
	 * parses the content
	 * 
	 * @param con the Controller
	 * @param attributes the attributes to start with
	 * @param allowedTypes a List with all allowed types in this call
	 * @param align the alignment of the content
	 * @param isInList are we in a list?
	 * @param replaceSmileys replace smileys?
	 */
	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		Integer attrID = null;
		if(_openingTag != null) {
			attrID = _openingTag.getAttribute();
			if(allowedTypes.contains(TYPE_INLINE) && attrID != null)
				attributes.set(attrID,_openingTag.getValue());
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_openingTag.getBBCodeTag(),attributes,align);
		}
		
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
		
		if(_closingTag != null) {
			if(allowedTypes.contains(TYPE_INLINE) && attrID != null)
				attributes.unset(attrID);
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_closingTag.getBBCodeTag(),attributes,align);
		}
	}
	
	/**
	 * inserts the content of this block
	 * 
	 * @param con the Controller
	 * @param attributes the attributes to start with
	 * @param allowedTypes a List with all allowed types in this call
	 * @param align the alignment of the content
	 * @param isInList are we in a list?
	 * @param replaceSmileys replace smileys?
	 */
	public void insertContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		boolean wasLastAlign = false;
		for(int i = 0,len = _content.size();i < len;i++) {
			Content content = (Content)_content.get(i);
			
			int conAlign = wasLastAlign ? ParagraphAttributes.ALIGN_LEFT : align;
			
			if(content instanceof PlainContent) {
				String text = (String)content.getValue();
				if(isInList)
					text = text.replace('\n','\r');
				
				con.pasteTextAtCursor(text,attributes,conAlign);
				wasLastAlign = false;
			}
			else if(content instanceof SmileyContent) {
				if(replaceSmileys)
					con.addImageAtCursor((SecSmiley)content.getValue());
				else
					con.pasteTextAtCursor(((SmileyContent)content).getSmileyCode(),attributes,conAlign);
				wasLastAlign = false;
			}
			else {
				Block b = (Block)content.getValue();
				b.parseContent(con,attributes,getAllowedContentWith(allowedTypes),conAlign,
						isInList,replaceSmileys);
				
				wasLastAlign = b instanceof AlignmentBlock;
			}
		}
	}
	
	/**
	 * collects the BBCode of this block and removes all tags if necessary
	 * 
	 * @param removeTags do you want to remove all tags?
	 * @return the content
	 */
	public String getBBCode(boolean removeTags) {
		if(!removeTags)
			return toString();
		
		StringBuffer buf = new StringBuffer();
		Iterator it = _content.iterator();
		while(it.hasNext()) {
			Content c = (Content)it.next();
			if(c instanceof BlockContent) {
				Block b = (Block)((BlockContent)c).getValue();
				buf.append(b.getBBCode(true));
			}
			else if(c instanceof SmileyContent)
				buf.append(((SmileyContent)c).getSmileyCode());
			else
				buf.append(c);
		}
		
		return buf.toString();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if(_openingTag != null) {
			buf.append("[");
			buf.append(_openingTag.getName());
			buf.append("]");
		}
		
		Iterator it = _content.iterator();
		while(it.hasNext()) {
			Object n = it.next();
			buf.append(n);
		}
		
		if(_openingTag != null) {
			buf.append("[/");
			buf.append(_openingTag.getName());
			buf.append("]");
		}
		
		return buf.toString();
	}
}