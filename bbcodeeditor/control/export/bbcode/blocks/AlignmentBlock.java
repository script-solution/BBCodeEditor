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
 * a block for the horizontal alignment
 * 
 * @author hrniels
 */
public class AlignmentBlock extends Block {
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening-tag
	 * @param closingTag the opening-tag
	 */
	public AlignmentBlock(Block parent,BBCodeTag openingTag,BBCodeTag closingTag) {
		super(parent,openingTag,closingTag);
	}
	
	public Integer getType() {
		return TYPE_BLOCK;
	}
	
	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		boolean allowed = allowedTypes.contains(TYPE_BLOCK);
		if(!allowed)
			con.pasteTextAtCursor(_openingTag.getBBCodeTag(),attributes);
		
		int conAlign = align;
		if(allowed) {
			int tagID = BBCodeTags.getIdFromTag(_openingTag.getName());
			switch(tagID) {
				case BBCodeTags.LEFT:
					conAlign = ParagraphAttributes.ALIGN_LEFT;
					break;
				case BBCodeTags.CENTER:
					conAlign = ParagraphAttributes.ALIGN_CENTER;
					break;
				case BBCodeTags.RIGHT:
					conAlign = ParagraphAttributes.ALIGN_RIGHT;
					break;
			}
		}
		
		insertContent(con,attributes,allowedTypes,conAlign,isInList,replaceSmileys);
		
		if(!allowed)
			con.pasteTextAtCursor(_closingTag.getBBCodeTag(),attributes);
	}
}