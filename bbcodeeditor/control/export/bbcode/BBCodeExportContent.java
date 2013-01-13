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

import java.awt.Color;
import java.util.Iterator;
import java.util.Stack;

import bbcodeeditor.control.*;
import bbcodeeditor.control.export.ColorFunctions;
import bbcodeeditor.control.export.IExportContent;
import bbcodeeditor.control.tools.Pair;

/**
 * the implementation of the export-interface for BBCode
 * 
 * @author hrniels
 */
public class BBCodeExportContent extends Object implements IExportContent {

	public String getText(String text) {
		return text;
	}
	
	public String getImage(SecImage img) {
		return "[img]" + img.getImagePath() + "[/img]";
	}
	
	public String getSmiley(SecSmiley smiley) {
		return smiley.getPrimaryCode();
	}
	
	public String getAttributeEndTags(Stack attributes,Stack current) {
		StringBuffer attrStr = new StringBuffer();
		
		while(attributes.size() > 0) {
			Pair e = (Pair)current.pop();
			Integer attr = (Integer)e.getKey();
			if(e.equals(attributes.peek()))
				attributes.pop();
			
			if(attr.equals(TextAttributes.BOLD))
				attrStr.append("[/b]");
			else if(attr.equals(TextAttributes.ITALIC))
				attrStr.append("[/i]");
			else if(attr.equals(TextAttributes.UNDERLINE))
				attrStr.append("[/u]");
			else if(attr.equals(TextAttributes.STRIKE))
				attrStr.append("[/s]");
			else if(attr.equals(TextAttributes.POSITION)) {
				if(e.getValue().equals(new Byte(TextAttributes.POS_SUPERSCRIPT)))
					attrStr.append("[/sup]");
				else
					attrStr.append("[/sub]");
			}
			else if(attr.equals(TextAttributes.BG_COLOR))
				attrStr.append("[/bgcolor]");
			else if(attr.equals(TextAttributes.FONT_COLOR))
				attrStr.append("[/color]");
			else if(attr.equals(TextAttributes.FONT_FAMILY))
				attrStr.append("[/font]");
			else if(attr.equals(TextAttributes.FONT_SIZE))
				attrStr.append("[/size]");
			else if(attr.equals(TextAttributes.URL))
				attrStr.append("[/url]");
			else if(attr.equals(TextAttributes.EMAIL))
				attrStr.append("[/mail]");
		}
		
		return attrStr.toString();
	}

	public String getAttributeStartTags(TextAttributes attributes,Stack current) {
		StringBuffer attrStr = new StringBuffer();
		
		// append urls and emails first because otherwise we can't apply formating
		// to the link-title
		String url = attributes.getURL();
		if(url != null)
		{
			attrStr.append("[url=" + url + "]");
			current.push(new Pair(TextAttributes.URL,url));
		}
		
		String eurl = attributes.getEmail();
		if(eurl != null)
		{
			attrStr.append("[mail=" + eurl + "]");
			current.push(new Pair(TextAttributes.EMAIL,eurl));
		}
		
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			Pair e = new Pair(attr,attributes.get(attr));
			
			if(attr.equals(TextAttributes.BOLD) && attributes.isBold())
				attrStr.append("[b]");
			else if(attr.equals(TextAttributes.ITALIC) && attributes.isItalic())
				attrStr.append("[i]");
			else if(attr.equals(TextAttributes.UNDERLINE) && attributes.isUnderline())
				attrStr.append("[u]");
			else if(attr.equals(TextAttributes.STRIKE) && attributes.isStrike())
				attrStr.append("[s]");
			else if(attr.equals(TextAttributes.POSITION)) {
				if(e.getValue().equals(new Byte(TextAttributes.POS_SUPERSCRIPT)))
					attrStr.append("[sup]");
				else if(e.getValue().equals(new Byte(TextAttributes.POS_SUBSCRIPT)))
					attrStr.append("[sub]");
				else
					continue;
			}
			else if(attr.equals(TextAttributes.BG_COLOR)) {
				Color color = (Color)e.getValue();
				String strColor = ColorFunctions.getStringFromColor(color);
				attrStr.append("[bgcolor=" + strColor + "]");
			}
			else if(attr.equals(TextAttributes.FONT_COLOR)) {
				Color color = (Color)e.getValue();
				String strColor = ColorFunctions.getStringFromColor(color);
				attrStr.append("[color=" + strColor + "]");
			}
			else if(attr.equals(TextAttributes.FONT_FAMILY)) {
				String fontFamily = (String)e.getValue();
				attrStr.append("[font=" + fontFamily + "]");
			}
			else if(attr.equals(TextAttributes.FONT_SIZE)) {
				int size = ((Integer)e.getValue()).intValue();
				attrStr.append("[size=" + size + "]");
			}
			else
				continue;
			
			// we don't want to put URL and EMAIL on the stack twice
			if(!attr.equals(TextAttributes.URL) && !attr.equals(TextAttributes.EMAIL))
				current.push(e);
		}
		
		return attrStr.toString();
	}

	public String getEnvironmentEnd(Environment env) {
		switch(env.getType()) {
			case EnvironmentTypes.ENV_QUOTE:
				return "[/quote]";
				
			case EnvironmentTypes.ENV_CODE:
				return "[/code]";
			
			case EnvironmentTypes.ENV_LIST:
				return "\n[/list]";
			
			default:
				return "";
		}
	}

	public String getEnvironmentStart(Environment env) {
		String param;
		switch(env.getType()) {
			case EnvironmentTypes.ENV_QUOTE:
				param = ((QuoteEnvironment)env).getAuthor();
				if(param != null && param.length() > 0)
					return "[quote=" + param + "]";

				return "[quote]";
				
			case EnvironmentTypes.ENV_CODE:
				Object syntax = ((CodeEnvironment)env).getHighlightSyntax();
				if(syntax != null)
					return "[code=" + syntax + "]";
				
				return "[code]";
			
			case EnvironmentTypes.ENV_LIST:
				param = ListTypes.getListParamName(((ListEnvironment)env).getListType());
				if(param != null && param.length() > 0)
					return "[list=" + param + "]";

				return "[list]";
			
			default:
				return "";
		}
	}

	public String getLineEnd(boolean isInList,boolean isEnvEnd) {
		if(isInList)
			return "";
		
		if(!isEnvEnd)
			return "\n";
		
		return "";
	}

	public String getParagraphStart(boolean isInList,boolean isEnvStart) {
		if(isInList)
			return "\n[*]";
		
		return "";
	}

	public String getLineAlignmentEnd(int align) {
		if(align == ParagraphAttributes.ALIGN_LEFT)
			return "";
		
		if(align == ParagraphAttributes.ALIGN_RIGHT)
			return "[/right]";
		
		return "[/center]";
	}

	public String getLineAlignmentStart(int align) {
		if(align == ParagraphAttributes.ALIGN_LEFT)
			return "";
		
		if(align == ParagraphAttributes.ALIGN_RIGHT)
			return "[right]";
		
		return "[center]";
	}
}