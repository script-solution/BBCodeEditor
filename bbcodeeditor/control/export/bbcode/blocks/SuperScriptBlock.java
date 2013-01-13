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
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.export.bbcode.tags.SimpleTag;


/**
 * The superscript-block
 * 
 * @author hrniels
 */
public class SuperScriptBlock extends Block {
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening-tag
	 * @param closingTag the opening-tag
	 */
	public SuperScriptBlock(Block parent,SimpleTag openingTag,SimpleTag closingTag) {
		super(parent,openingTag,closingTag);
	}
	
	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		if(_openingTag != null) {
			if(allowedTypes.contains(TYPE_INLINE))
				attributes.set(TextAttributes.POSITION,new Byte(TextAttributes.POS_SUPERSCRIPT));
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_openingTag.getBBCodeTag(),attributes,align);
		}
		
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
		
		if(_closingTag != null) {
			if(allowedTypes.contains(TYPE_INLINE))
				attributes.unset(TextAttributes.POSITION);
			else if(!allowedTypes.contains(BBCodeTags.getBlockType(_openingTag.getName())))
				con.pasteTextAtCursor(_closingTag.getBBCodeTag(),attributes,align);
		}
	}
}