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

import java.util.List;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.ParagraphAttributes;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;


/**
 * @author Assi Nilsmussen
 *
 */
public class LinkBlock extends Block {
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening-tag
	 * @param closingTag the opening-tag
	 */
	public LinkBlock(Block parent,BBCodeTag openingTag,BBCodeTag closingTag) {
		super(parent,openingTag,closingTag);
	}
	
	public Integer getType() {
		return TYPE_BLOCK;
	}
	
	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		Integer attrID = null;
		if(_openingTag != null) {
			attrID = _openingTag.getAttribute();
			if(allowedTypes.contains(TYPE_URL) && attrID != null) {
				Object val = _openingTag.getValue();
				if(!(val instanceof String))
					attributes.set(attrID,getBBCode(true));
				else
					attributes.set(attrID,val);
			}
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_openingTag.getBBCodeTag(),attributes,align);
		}
		
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
		
		if(_closingTag != null) {
			if(allowedTypes.contains(TYPE_URL) && attrID != null)
				attributes.unset(attrID);
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_closingTag.getBBCodeTag(),attributes,align);
		}
	}
	
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
				con.pasteTextAtCursor(((SmileyContent)content).getSmileyCode(),attributes,conAlign);
				wasLastAlign = false;
			}
			else {
				Block b = (Block)content.getValue();
				b.parseContent(con,attributes,getAllowedContentWith(allowedTypes),conAlign,
						isInList,false);
				
				wasLastAlign = b instanceof AlignmentBlock;
			}
		}
	}
}