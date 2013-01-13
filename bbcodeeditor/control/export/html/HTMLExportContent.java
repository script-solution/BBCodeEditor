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

package bbcodeeditor.control.export.html;

import java.awt.Color;
import java.util.Iterator;
import java.util.Stack;

import bbcodeeditor.control.*;
import bbcodeeditor.control.export.ColorFunctions;
import bbcodeeditor.control.export.IExportContent;
import bbcodeeditor.control.tools.Pair;
import bbcodeeditor.control.tools.StringUtils;

/**
 * the implementation of the export-interface for HTML-Code
 * 
 * @author hrniels
 */
public class HTMLExportContent implements IExportContent {
	
	/**
	 * The string to replace tabs with
	 */
	private final String _tab;
	
	/**
	 * Constructor
	 * 
	 * @param tf the textfield
	 */
	public HTMLExportContent(AbstractTextField tf) {
		_tab = StringUtils.repeat("&nbsp;",tf.getTabWidth());
	}

	public String getText(String text) {
		text = StringUtils.stringToHTMLString(text);
		text = StringUtils.simpleReplace(text,"\t",_tab);
		return text;
	}
	
	public String getImage(SecImage img) {
		return "<img src=\"" + img.getImagePath() + "\" border=\"0\" alt=\"\">";
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
				attrStr.append("</b>");
			else if(attr.equals(TextAttributes.ITALIC))
				attrStr.append("</i>");
			else if(attr.equals(TextAttributes.UNDERLINE))
				attrStr.append("</u>");
			else if(attr.equals(TextAttributes.STRIKE))
				attrStr.append("</s>");
			else if(attr.equals(TextAttributes.POSITION)) {
				if(e.getValue().equals(new Byte(TextAttributes.POS_SUBSCRIPT)))
					attrStr.append("</sub>");
				else if(e.getValue().equals(new Byte(TextAttributes.POS_SUPERSCRIPT)))
					attrStr.append("</sup>");
			}
			else if(attr.equals(TextAttributes.BG_COLOR))
				attrStr.append("</span>");
			else if(attr.equals(TextAttributes.FONT_COLOR))
				attrStr.append("</span>");
			else if(attr.equals(TextAttributes.FONT_FAMILY))
				attrStr.append("</span>");
			else if(attr.equals(TextAttributes.FONT_SIZE))
				attrStr.append("</span>");
			else if(attr.equals(TextAttributes.URL))
				attrStr.append("</a>");
			else if(attr.equals(TextAttributes.EMAIL))
				attrStr.append("</a>");
		}
		
		return attrStr.toString();
	}

	public String getAttributeStartTags(TextAttributes attributes,Stack current) {
		StringBuffer attrStr = new StringBuffer();
		
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			Pair e = new Pair(attr,attributes.get(attr));
			
			if(attr.equals(TextAttributes.BOLD) && attributes.isBold())
				attrStr.append("<b>");
			else if(attr.equals(TextAttributes.ITALIC) && attributes.isItalic())
				attrStr.append("<i>");
			else if(attr.equals(TextAttributes.UNDERLINE) && attributes.isUnderline())
				attrStr.append("<u>");
			else if(attr.equals(TextAttributes.STRIKE) && attributes.isStrike())
				attrStr.append("<s>");
			else if(attr.equals(TextAttributes.POSITION)) {
				if(e.getValue().equals(new Byte(TextAttributes.POS_SUBSCRIPT)))
					attrStr.append("<sub>");
				else if(e.getValue().equals(new Byte(TextAttributes.POS_SUPERSCRIPT)))
					attrStr.append("<sup>");
				else
					continue;
			}
			else if(attr.equals(TextAttributes.BG_COLOR)) {
				Color color = (Color)e.getValue();
				String strColor = ColorFunctions.getStringFromColor(color);
				attrStr.append("<span style=\"background-color: " + strColor + ";\">");
			}
			else if(attr.equals(TextAttributes.FONT_COLOR)) {
				Color color = (Color)e.getValue();
				String strColor = ColorFunctions.getStringFromColor(color);
				attrStr.append("<span style=\"color: " + strColor + ";\">");
			}
			else if(attr.equals(TextAttributes.FONT_FAMILY)) {
				String fontFamily = (String)e.getValue();
				attrStr.append("<span style=\"font-family: " + fontFamily + ";\">");
			}
			else if(attr.equals(TextAttributes.FONT_SIZE)) {
				int size = ((Integer)e.getValue()).intValue();
				attrStr.append("<span style=\"font-size: " + size + "px;\">");
			}
			else if(attr.equals(TextAttributes.URL)) {
				String url = (String)e.getValue();
				attrStr.append("<a href=\"" + url + "\">");
			}
			else if(attr.equals(TextAttributes.EMAIL)) {
				String eurl = (String)e.getValue();
				attrStr.append("<a href=\"mailto:" + eurl + "\">");
			}
			else
				continue;
			
			current.push(e);
		}
		
		return attrStr.toString();
	}

	public String getEnvironmentEnd(Environment env) {
		switch(env.getType()) {
			case EnvironmentTypes.ENV_QUOTE:
				return "</div></div>\n";
				
			case EnvironmentTypes.ENV_CODE:
				return "</div></div>\n";
			
			case EnvironmentTypes.ENV_LIST:
				int type = ((ListEnvironment)env).getListType();
				switch(type) {
					case ListTypes.TYPE_NUM:
					case ListTypes.TYPE_ROMAN_S:
					case ListTypes.TYPE_ROMAN_B:
					case ListTypes.TYPE_ALPHA_S:
					case ListTypes.TYPE_ALPHA_B:
						return "</ol>\n";
					default:
							return "</ul>\n";
				}
			
			default:
				return "";
		}
	}

	public String getEnvironmentStart(Environment env) {
		StringBuffer buf = new StringBuffer();
		switch(env.getType()) {
			case EnvironmentTypes.ENV_QUOTE:
				QuoteEnvironment qenv = (QuoteEnvironment)env;
				buf.append("<div style=\"border: 1px solid #999999; background-color: #7283A0;");
				buf.append(" margin: 5px 10px 5px 10px;\">");
				buf.append("<div style=\"padding: 5px; background-color: #7283A; color: #FFFFFF;\">");
				
				if(qenv.getAuthor() != null)
					buf.append("<b>" + qenv.getAuthor() + "</b> wrote the following:");
				else
					buf.append("<b>Quote:</b>");
				
				buf.append("</div><div style=\"padding: 5px; background-color: #FFFFFF;\">");
				
				return buf.toString();
				
			case EnvironmentTypes.ENV_CODE:
				CodeEnvironment cenv = (CodeEnvironment)env;
				buf.append("<div style=\"border: 1px solid #999999; background-color: #7283A0;");
				buf.append(" margin: 5px 10px 5px 10px; overflow: hidden;\">");
				buf.append("<div style=\"padding: 5px; background-color: #7283A; color: #FFFFFF;\"><b>");
				
				if(cenv.getHighlightSyntax() != null)
					buf.append("<b>" + cenv.getHighlightSyntax() + ":</b>");
				else
					buf.append("<b>Code:</b>");

				buf.append("</div><div style=\"padding: 5px; background-color: #FFFFFF;");
				buf.append(" overflow: auto; font-family: Courier new;\">");
				
				return buf.toString();
			
			case EnvironmentTypes.ENV_LIST:
				int type = ((ListEnvironment)env).getListType();
				switch(type) {
					case ListTypes.TYPE_CIRCLE:
						return "<ul type=\"circle\">\n";
					case ListTypes.TYPE_DISC:
						return "<ul type=\"disc\">\n";
					case ListTypes.TYPE_SQUARE:
						return "<ul type=\"square\">\n";
					case ListTypes.TYPE_ALPHA_B:
						return "<ol type=\"A\">\n";
					case ListTypes.TYPE_ALPHA_S:
						return "<ol type=\"a\">\n";
					case ListTypes.TYPE_ROMAN_B:
						return "<ol type=\"I\">\n";
					case ListTypes.TYPE_ROMAN_S:
						return "<ol type=\"i\">\n";
					case ListTypes.TYPE_NUM:
						return "<ol type=\"1\">\n";
					default:
							return "<ul>\n";
				}
			
			default:
				return "";
		}
	}

	public String getLineEnd(boolean isInList,boolean isEnvEnd) {
		if(isInList)
			return "</li>\n";
		
		if(!isEnvEnd)
			return "<br>\n";
		
		return "";
	}

	public String getParagraphStart(boolean isInList,boolean isEnvStart) {
		if(isInList)
			return "<li>";
		
		return "";
	}

	public String getLineAlignmentEnd(int align) {
		if(align == ParagraphAttributes.ALIGN_LEFT)
			return "";
		
		if(align == ParagraphAttributes.ALIGN_RIGHT)
			return "</div>";
		
		return "</div>";
	}

	public String getLineAlignmentStart(int align) {
		if(align == ParagraphAttributes.ALIGN_LEFT)
			return "";
		
		if(align == ParagraphAttributes.ALIGN_RIGHT)
			return "<div align=\"right\">";
		
		return "<div align=\"center\">";
	}
}