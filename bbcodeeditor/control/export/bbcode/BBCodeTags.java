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

import java.util.Arrays;
import java.util.List;

import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.bbcode.blocks.Block;
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;


/**
 * @author Assi Nilsmussen
 * 
 * this class defines which BBCode-tags exist and a few methods to convert from one to the other
 */
public class BBCodeTags {

	/**
	 * unknown tag
	 */
	static final int UNDEFINED						= -1;
	
	/**
	 * the bold tag
	 */
	public static final int BOLD					= 0;
	
	/**
	 * the italic tag
	 */
	public static final int ITALIC				= 1;
	
	/**
	 * the underline-tag
	 */
	public static final int UNDERLINE			= 2;
	
	/**
	 * the strike-tag
	 */
	public static final int STRIKE				= 3;
	
	/**
	 * the subscript-tag (will be mapped to the position-attribute)
	 */
	public static final int SUBSCRIPT			= 4;

	/**
	 * the superscript-tag (will be mapped to the position-attribute)
	 */
	public static final int SUPERSCRIPT		= 5;
	
	/**
	 * the font-size tag
	 */
	public static final int FONT_SIZE			= 6;
	
	/**
	 * the font-family tag
	 */
	public static final int FONT_FAMILY		= 7;
	
	/**
	 * the font-color tag
	 */
	public static final int FONT_COLOR		= 8;
	
	/**
	 * the background-color tag
	 */
	public static final int BG_COLOR			= 9;
	
	/**
	 * the url-tag
	 */
	public static final int URL						= 10;
	
	/**
	 * the email-tag
	 */
	public static final int EMAIL					= 11;
	
	/**
	 * the quote-tag
	 */
	public static final int QUOTE					= 12;
	
	/**
	 * the code-tag
	 */
	public static final int CODE					= 13;
	
	/**
	 * the list-tag
	 */
	public static final int LIST					= 14;
	
	/**
	 * the image-tag
	 */
	public static final int IMAGE					= 15;
	
	/**
	 * the left-alignment-tag
	 */
	public static final int LEFT					= 16;
	
	/**
	 * the center-alignment-tag
	 */
	public static final int CENTER				= 17;
	
	/**
	 * the right-alignment-tag
	 */
	public static final int RIGHT					= 18;
	
	/**
	 * Not a real tag, but we want to allow the user to enable/disable highlighting
	 */
	public static final int HIGHLIGHT			= 19;
	
	
	/**
	 * the simple-type (no parameters)
	 */
	static final int TYPE_SIMPLE					= 0;
	
	/**
	 * the extended tag (with parameters)
	 */
	static final int TYPE_EXTENDED				= 1;
	
	/**
	 * tags with optional parameters
	 */
	static final int TYPE_BOTH						= 2;
	
	/**
	 * all simple tags
	 */
	static final int[] simpleTags = {
		BOLD,ITALIC,UNDERLINE,STRIKE,IMAGE,LEFT,RIGHT,CENTER,SUBSCRIPT,SUPERSCRIPT
	};
	
	/**
	 * all extended tags
	 */
	static final int[] extendedTags = {
		FONT_SIZE,FONT_FAMILY,FONT_COLOR,BG_COLOR
	};
	
	/**
	 * the tags with optional parameters
	 */
	static final int[] bothTags = {
		QUOTE,CODE,LIST,URL,EMAIL
	};
	
	/**
	 * @return a List with the ids of all tags
	 */
	public static List getAllTags() {
		return Arrays.asList(new Object[] {
				new Integer(BOLD),new Integer(ITALIC),new Integer(UNDERLINE),
				new Integer(STRIKE),
				new Integer(SUBSCRIPT),new Integer(SUPERSCRIPT),
				new Integer(FONT_SIZE),new Integer(FONT_FAMILY),new Integer(FONT_COLOR),
				new Integer(BG_COLOR),
				new Integer(URL),new Integer(EMAIL),
				new Integer(QUOTE),new Integer(CODE),new Integer(LIST),
				new Integer(LEFT),new Integer(CENTER),new Integer(RIGHT),
				new Integer(HIGHLIGHT)
		});
	}
	
	/**
	 * checks wether the given tag-id does exist
	 * 
	 * @param tag the tag-id
	 * @return true if the tag-id exists
	 */
	public static boolean isValidTag(int tag) {
		return tag >= 0 && tag <= 19;
	}
	
	/**
	 * @param tag the tag-name
	 * @return the id of the given tag
	 */
	public static int getIdFromTag(String tag) {
		if(tag.equals("b"))
			return BOLD;
		if(tag.equals("i"))
			return ITALIC;
		if(tag.equals("u"))
			return UNDERLINE;
		if(tag.equals("s"))
			return STRIKE;
		if(tag.equals("sub"))
			return SUBSCRIPT;
		if(tag.equals("sup"))
			return SUPERSCRIPT;
		if(tag.equals("size"))
			return FONT_SIZE;
		if(tag.equals("font"))
			return FONT_FAMILY;
		if(tag.equals("color"))
			return FONT_COLOR;
		if(tag.equals("bgcolor"))
			return BG_COLOR;
		if(tag.equals("url"))
			return URL;
		if(tag.equals("mail"))
			return EMAIL;
		if(tag.equals("code"))
			return CODE;
		if(tag.equals("quote"))
			return QUOTE;
		if(tag.equals("list"))
			return LIST;
		if(tag.equals("img"))
			return IMAGE;
		if(tag.equals("left"))
			return LEFT;
		if(tag.equals("center"))
			return CENTER;
		if(tag.equals("right"))
			return RIGHT;
		
		return UNDEFINED;
	}
	
	/**
	 * @param id the id of the tag
	 * @return the value of of the given BBCodeTag-id
	 */
	static int getValueTypeFromID(int id) {
		switch(id) {
			case FONT_SIZE:
				return BBCodeTag.VALUE_TYPE_INTEGER;
				
			case BG_COLOR:
			case FONT_COLOR:
				return BBCodeTag.VALUE_TYPE_COLOR;
			
			case FONT_FAMILY:
			case URL:
			case EMAIL:
			case QUOTE:
			case LIST:
				return BBCodeTag.VALUE_TYPE_STRING;
				
			default:
				return BBCodeTag.VALUE_TYPE_NONE;
		}
	}
	
	/**
	 * @param id the id of the tag you're looking for
	 * @return the corresponding tag-name of the given id
	 */
	static String getTagFromID(int id) {
		switch(id) {
			case BOLD:
				return "b";
			case ITALIC:
				return "i";
			case UNDERLINE:
				return "u";
			case STRIKE:
				return "s";
			case SUBSCRIPT:
				return "sub";
			case SUPERSCRIPT:
				return "sup";
			case FONT_SIZE:
				return "size";
			case FONT_FAMILY:
				return "font";
			case FONT_COLOR:
				return "color";
			case BG_COLOR:
				return "bgcolor";
			case URL:
				return "url";
			case EMAIL:
				return "mail";
			case QUOTE:
				return "quote";
			case CODE:
				return "code";
			case LIST:
				return "list";
			case IMAGE:
				return "img";
			case LEFT:
				return "left";
			case CENTER:
				return "center";
			case RIGHT:
				return "right";
		}
		
		return "";
	}
	
	/**
	 * @param id the id of the tag
	 * @return the Attributes-id of the given tag-id. null if the given tag is no attribute
	 */
	static Integer getAttributeOfID(int id) {
		switch(id) {
			case BOLD:
				return TextAttributes.BOLD;
			case ITALIC:
				return TextAttributes.ITALIC;
			case UNDERLINE:
				return TextAttributes.UNDERLINE;
			case STRIKE:
				return TextAttributes.STRIKE;
			case SUBSCRIPT:
				return TextAttributes.POSITION;
			case SUPERSCRIPT:
				return TextAttributes.POSITION;
			case FONT_SIZE:
				return TextAttributes.FONT_SIZE;
			case FONT_FAMILY:
				return TextAttributes.FONT_FAMILY;
			case FONT_COLOR:
				return TextAttributes.FONT_COLOR;
			case BG_COLOR:
				return TextAttributes.BG_COLOR;
			case URL:
				return TextAttributes.URL;
			case EMAIL:
				return TextAttributes.EMAIL;
			default:
				return null;
		}
	}
	
	/**
	 * determines the block-type of the given tag-name
	 * 
	 * @param tag the tag-name
	 * @return the block-type
	 * @see Block
	 */
	public static Integer getBlockType(String tag) {
		if(tag.equals("b") || tag.equals("i") || tag.equals("u") || tag.equals("s") ||
				tag.equals("size") || tag.equals("font") || tag.equals("color") ||
				tag.equals("img") || tag.equals("sup") || tag.equals("sub") || tag.equals("bgcolor"))
			return Block.TYPE_INLINE;

		if(tag.equals("url") || tag.equals("mail"))
			return Block.TYPE_URL;

		if(tag.equals("code") || tag.equals("quote") || tag.equals("list") ||
				tag.equals("left") || tag.equals("center") || tag.equals("right"))
			return Block.TYPE_BLOCK;
		
		return Block.TYPE_UNDEF;
	}
	
	/**
	 * Determines which types are allowed in the given tag
	 * 
	 * @param tag the tag-name
	 * @return a List with all allowed types: Block.TYPE_*
	 */
	public static List getAllowedTypes(String tag) {
		if(tag.equals("left") || tag.equals("center") || tag.equals("right") ||
				tag.equals("color") || tag.equals("font") || tag.equals("size") ||
				tag.equals("b") || tag.equals("i") || tag.equals("u") || tag.equals("sub") ||
				tag.equals("sup") || tag.equals("s") || tag.equals("bgcolor"))
			return Arrays.asList(new Integer[] {Block.TYPE_INLINE,Block.TYPE_URL});
		
		if(tag.equals("url") || tag.equals("mail"))
			return Arrays.asList(new Integer[] {Block.TYPE_INLINE});
		
		if(tag.equals("code") || tag.equals("img"))
			return Arrays.asList(new Integer[] {});
		
		return Arrays.asList(new Integer[] {Block.TYPE_BLOCK,Block.TYPE_INLINE,Block.TYPE_URL});
	}
	
	/**
	 * @param tagID the id of the tag
	 * @return the tag-type. TYPE_SIMPLE, TYPE_EXTENDED or TYPE_BOTH. UNDEFINED if unknown
	 */
	static int getTagParamType(int tagID) {
		for(int i = 0;i < bothTags.length;i++) {
			if(tagID == bothTags[i])
				return TYPE_BOTH;
		}
		
		for(int i = 0;i < simpleTags.length;i++) {
			if(tagID == simpleTags[i])
				return TYPE_SIMPLE;
		}

		for(int i = 0;i < extendedTags.length;i++) {
			if(tagID == extendedTags[i])
				return TYPE_EXTENDED;
		}
		
		return UNDEFINED;
	}
}