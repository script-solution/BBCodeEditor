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

import bbcodeeditor.control.*;
import bbcodeeditor.control.export.bbcode.tags.BBCodeTag;
import bbcodeeditor.control.tools.StringUtils;


/**
 * @author Assi Nilsmussen
 *
 */
public class ListBlock extends Block {
	
	/**
	 * constructor
	 * 
	 * @param parent the parent-tag
	 * @param openingTag the opening-tag
	 * @param closingTag the opening-tag
	 */
	public ListBlock(Block parent,BBCodeTag openingTag,BBCodeTag closingTag) {
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
			int type = ListTypes.TYPE_DEFAULT;
			if(_openingTag.getValue() instanceof String)
				type = ListTypes.getListTypeFromParam((String)_openingTag.getValue());
			
			con.addListEnvironment(!isInList,type);
		}
		else
			con.pasteTextAtCursor(this._openingTag.getBBCodeTag(),attributes);
		
		insertContent(con,attributes,allowedTypes,align,isInList,replaceSmileys);
		
		if(allowedTypes.contains(TYPE_BLOCK))
			con.forward();
		else
			con.pasteTextAtCursor(this._closingTag.getBBCodeTag(),attributes);
	}
	
	public void insertContent(Controller con,TextAttributes attributes,List allowedTypes,
			int align,boolean isInList,boolean replaceSmileys) {
		boolean wasLastAlign = false;
		boolean foundFirst = false;
		
		for(int i = 0,len = _content.size();i < len;i++) {
			Content oCon = (Content)_content.get(i);
			
			int conAlign = wasLastAlign ? ParagraphAttributes.ALIGN_LEFT : align;
			
			if(oCon instanceof PlainContent) {
				String content = (String)oCon.getValue();
				
				// have we a new list-point
				// note that we can assume that every list-point has its own block because
				// strings like [...] get their own block
				if(content.equals("[*]")) {
					if(foundFirst)
						con.addNewLine();
					foundFirst = true;
				}
				// we want to skip leeding whitespace
				else if(foundFirst || !StringUtils.isWhiteSpace(content)) {
					// determine if the next block does not exist or is a list-point
					Content next = i < len - 1 ? (Content)_content.get(i + 1) : null;
					boolean nextIsNewPoint = next == null;
					if(next instanceof PlainContent && ((String)next.getValue()).equals("[*]"))
						nextIsNewPoint = true;

					// we want to trim the end if the next one is a new list-point or the end
					if(nextIsNewPoint)
						content = StringUtils.trimEnd(content);
					
					// don't apply alignment to whitespace
					int usedAlign = conAlign;
					if(StringUtils.isWhiteSpace(content))
						usedAlign = ParagraphAttributes.ALIGN_UNDEF;
					
					content = StringUtils.simpleReplace(content,"\n","\r");
					
					// paste the text					
					con.pasteTextAtCursor(content,new TextAttributes(),usedAlign);
					foundFirst = true;
				}
				
				wasLastAlign = false;
			}
			else if(oCon instanceof SmileyContent) {
				con.addImageAtCursor((SecSmiley)oCon.getValue());
				wasLastAlign = false;
			}
			else {
				Block b = (Block)oCon.getValue();
				b.parseContent(con,new TextAttributes(),getAllowedContentWith(allowedTypes),
						conAlign,true,replaceSmileys);
				
				wasLastAlign = b instanceof AlignmentBlock;
			}
		}
	}
}