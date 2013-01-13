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

package bbcodeeditor.control.export;

import java.util.Stack;

import bbcodeeditor.control.*;


/**
 * the export-interface
 * 
 * @author hrniels
 */
public interface IExportContent {
	
	/**
	 * prepares the given text for storage
	 * with this method you may replace some special chars or something like that
	 * 
	 * @param text the text to modify
	 * @return the modified text
	 */
	String getText(String text);
	
	/**
	 * generates an image-tag for the given image
	 * 
	 * @param img the SecImage instance
	 * @return the image-tag
	 */
	String getImage(SecImage img);
	
	/**
	 * generates the smiley for the given SecSmiley-object
	 * 
	 * @param smiley the SecSmiley instance
	 * @return the smiley
	 */
	String getSmiley(SecSmiley smiley);
	
	/**
	 * generates the start-tag for an environment
	 * 
	 * @param env the Environment-instance
	 * @return the start-tag for the environment
	 */
	String getEnvironmentStart(Environment env);
	
	/**
	 * generates the end-tag for an environment
	 * 
	 * @param env the Environment-instance
	 * @return the end-tag for the environment
	 */
	String getEnvironmentEnd(Environment env);
	
	/**
	 * creates a tag or multiple tags to start a text with given attributes
	 * you have to add the attributes in the same order than you add them to
	 * the stack
	 * 
	 * @param attributes a container with all attributes to apply
	 * @param current the current attributes which have to be modified
	 * @return the tag(s)
	 */
	String getAttributeStartTags(TextAttributes attributes,Stack current);
	
	/**
	 * creates a tag or multiple tags to finish a text with given attributes
	 * you have to remove the attributes in the correct order!
	 * 
	 * @param attributes a container with all attributes to remove
	 * @param current the current attributes which you have to modify!
	 * @return the tag(s)
	 */
	String getAttributeEndTags(Stack attributes,Stack current);
	
	/**
	 * returns the start of a paragraph
	 * by default this will be empty, but in list-environments this may be not empty
	 * or for other reasons :)
	 * 
	 * @param isInList will be true if we are in a list-environment
	 * @param isEnvStart is it the first paragraph of the environment?
	 * @return the line-start
	 */
	String getParagraphStart(boolean isInList,boolean isEnvStart);
	
	/**
	 * returns a line-end
	 * 
	 * @param isInList will be true if we are in a list-environment
	 * @param isEnvEnd is it the last line of the environment?
	 * @return the line-end
	 */
	String getLineEnd(boolean isInList,boolean isEnvEnd);
	
	/**
	 * returns the line-alignment-start-tag
	 * 
	 * @param align the alignment. see Attributes.ALIGN_*
	 * @return the line-alignment-start-tag
	 * @see ParagraphAttributes
	 */
	String getLineAlignmentStart(int align);
	
	/**
	 * returns the line-alignment-end-tag
	 * 
	 * @param align the alignment. see Attributes.ALIGN_*
	 * @return the line-alignment-end-tag
	 * @see ParagraphAttributes
	 */
	String getLineAlignmentEnd(int align);
}