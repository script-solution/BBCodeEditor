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
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;


/**
 * @author Assi Nilsmussen
 *
 */
public class QuoteBlock extends Block {
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening-tag
	 * @param closingTag the opening-tag
	 */
	public QuoteBlock(Block parent,BBCodeTag openingTag,BBCodeTag closingTag) {
		super(parent,openingTag,closingTag);
	}
	
	public Integer getType() {
		return TYPE_BLOCK;
	}
	
	protected boolean allowNesting() {
		return true;
	}

	public void parseContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		if(allowedTypes.contains(TYPE_BLOCK)) {
			String author = null;
			if(_openingTag.getValue() instanceof String)
				author = (String)_openingTag.getValue();
			
			con.addQuoteEnvironment(!isInList,author);
		}
		else
			con.pasteTextAtCursor(this._openingTag.getBBCodeTag(),attributes);
		
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
		
		if(allowedTypes.contains(TYPE_BLOCK))
			con.forward();
		else
			con.pasteTextAtCursor(this._closingTag.getBBCodeTag(),attributes);
	}
}