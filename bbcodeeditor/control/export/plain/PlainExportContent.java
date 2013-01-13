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

package bbcodeeditor.control.export.plain;

import java.util.Stack;

import bbcodeeditor.control.Environment;
import bbcodeeditor.control.SecImage;
import bbcodeeditor.control.SecSmiley;
import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.export.IExportContent;

/**
 * the implementation of the export-interface for plain-text
 * 
 * @author hrniels
 */
public class PlainExportContent extends Object implements IExportContent {

	public String getText(String text) {
		return text;
	}
	
	public String getImage(SecImage img) {
		return img.getImagePath().toString();
	}
	
	public String getSmiley(SecSmiley smiley) {
		return smiley.getPrimaryCode();
	}
	
	public String getAttributeEndTags(Stack attributes,Stack current) {
		return "";
	}

	public String getAttributeStartTags(TextAttributes attributes,Stack current) {
		return "";
	}

	public String getEnvironmentEnd(Environment env) {
		return "\n";
	}

	public String getEnvironmentStart(Environment env) {
		return "\n";
	}

	public String getLineEnd(boolean isInList,boolean isEnvEnd) {
		if(!isEnvEnd)
			return "\n";
		
		return "";
	}

	public String getParagraphStart(boolean isInList,boolean isEnvStart) {
		if(isInList)
			return "* ";
		
		return "";
	}

	public String getLineAlignmentEnd(int align) {
		return "";
	}

	public String getLineAlignmentStart(int align) {
		return "";
	}
}