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
import bbcodeeditor.control.SecImage;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.bbcode.tags.SimpleTag;


/**
 * @author Assi Nilsmussen
 *
 */
public class ImageBlock extends Block {

	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening BBCodeTag of this block
	 * @param closingTag the closing BBCodeTag of this block
	 */
	public ImageBlock(Block parent,SimpleTag openingTag,SimpleTag closingTag) {
		super(parent,openingTag,closingTag);
	}
	
	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
	}

	public void insertContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		StringBuffer text = new StringBuffer();

		// do this in a loop because it can contain more than one section if the user has
		// included a [b] or something like that
		for(int i = 0,len = _content.size();i < len;i++) {
			Content content = (Content)_content.get(i);
			
			// ignore other content
			if(content instanceof PlainContent)
				text.append(content.getValue());
			else if(content instanceof SmileyContent)
				text.append(((SmileyContent)content).getSmileyCode());
			else
				text.append(content.toString());
		}
		
		if(allowedTypes.contains(Block.TYPE_INLINE))
			con.addImageAtCursor(new SecImage(con.getTextField(),text.toString()));
		else
			con.pasteTextAtCursor(toString(),attributes,align);
	}
}