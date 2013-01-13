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

import java.util.Iterator;
import java.util.Stack;

import bbcodeeditor.control.*;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.tools.Pair;


/**
 * exports a specified content from the control
 * 
 * @author hrniels
 */
public class Exporter {
	
	/**
	 * the public-controller-interface
	 */
	private IPublicController _pubCon;
	
	/**
	 * the implementation of the IExportContent interface
	 */
	private IExportContent _exportType;
	
	/**
	 * constructor
	 * 
	 * @param pubCon the public-controller-interface
	 * @param exportType the implementation of the IExportContent interface
	 */
	public Exporter(IPublicController pubCon,IExportContent exportType) {
		_pubCon = pubCon;
		_exportType = exportType;
	}
	
	/**
	 * returns the whole content of the control
	 * 
	 * @return the result-string to export
	 */
	public String getContent() {
		Environment rootEnv = _pubCon.getRootEnvironment();
		int len = rootEnv.getElementLength();
		return getContent(rootEnv,0,len,false);
	}
	
	/**
	 * returns the content of the control in the given interval
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @return the result-string to export
	 */
	public String getContent(int start,int end) {
		Environment rootEnv = _pubCon.getRootEnvironment();
		Paragraph startPara = rootEnv.getParagraphAtPosition(start);
		
		return getContent(rootEnv,startPara,start,end,false);
	}

	/**
	 * calculates the content to export
	 * 
	 * @param parent the Environment
	 * @param start the start-position in the environment
	 * @param end the end-position in the environment
	 * @param isInList true if we are in a list-environment
	 */
	private String getContent(Environment parent,int start,int end,boolean isInList) {
		return getContent(parent,null,start,end,isInList);
	}
	
	/**
	 * calculates the content to export
	 * 
	 * @param parent the Environment
	 * @param firstPara the paragraph to start with
	 * @param start the start-position in the environment
	 * @param end the end-position in the environment
	 * @param isInList true if we are in a list-environment
	 */
	private String getContent(Environment parent,Paragraph firstPara,int start,int end,boolean isInList) {
		StringBuffer content = new StringBuffer();
		Stack attributes = new Stack();

		int align = ParagraphAttributes.ALIGN_LEFT;
		
		Paragraph p;
		if(firstPara == null)
			p = parent.getFirstParagraph();
		else
			p = firstPara;
		
		if(p == null)
			return "";
		
		do {
			Section sec = p.getFirstSection();
			
			// start-pos not in this paragraph?
			if(start > p.getElementEndPos() + 1)
				continue;
			// are we finished?
			if(end < sec.getStartPosInEnv())
				break;
			
			do {
				// ensure that the given part will be collected
				if(start > sec.getEndPosInEnv() + 1)
					continue;
				if(end < sec.getStartPosInEnv())
					break;
				
				// is the current section an environment?
				if(sec instanceof Environment) {
					Environment eSec = (Environment)sec;
					
					// close all open tags
					content.append(_exportType.getAttributeEndTags((Stack)attributes.clone(),attributes));
					attributes.clear();
					
					// add alignment-end
					content.append(_exportType.getLineAlignmentEnd(align));
					align = ParagraphAttributes.ALIGN_LEFT;
					
					// add the environment-tags if we want to export more than just the env-content
					boolean addEnv = start < sec.getStartPosInEnv() || end > sec.getEndPosInEnv() + 1;
					boolean envAllowed = true;
					
					// check if the environment is enabled
					if(eSec instanceof CodeEnvironment && !_pubCon.isTagEnabled(BBCodeTags.CODE))
						envAllowed = false;
					else if(eSec instanceof QuoteEnvironment && !_pubCon.isTagEnabled(BBCodeTags.QUOTE))
						envAllowed = false;
					else if(eSec instanceof ListEnvironment && !_pubCon.isTagEnabled(BBCodeTags.LIST))
						envAllowed = false;
					
					// append env-start
					if(addEnv && envAllowed)
						content.append(_exportType.getEnvironmentStart(eSec));
					else if(addEnv)
						content.append(_exportType.getLineEnd(isInList && p.isListPoint(),false));

					// append the environment-content
					int envStart = start - p.getElementStartPos();
					int envEnd = end - p.getElementStartPos();
					boolean isList = sec instanceof ListEnvironment && addEnv;
					content.append(getContent(eSec,null,envStart,envEnd,isList));
					
					// append the env-end, if necessary
					if(addEnv && envAllowed)
						content.append(_exportType.getEnvironmentEnd(eSec));
					else if(addEnv)
						content.append(_exportType.getLineEnd(isInList && p.isListPoint(),false));
				}
				else {
					ContentSection textSec = (ContentSection)sec;
					boolean listNewLine = isInList && p.isListPoint();
					boolean alignChange = sec.isFirst() && p.getHorizontalAlignment() != align;
					boolean isNoTextSec = !(textSec instanceof TextSection);
					
					Stack remove = new Stack();
					TextAttributes add = new TextAttributes();
					TextAttributes newAttr;
					// we have to check if it contains styles, because the highlight-environments
					// contain no "public" styles, but internal ones for the highlighting...
					if(textSec instanceof TextSection && sec.getParentEnvironment().containsStyles()) {
						newAttr = ((TextSection)textSec).getAttributes();
						_pubCon.cleanAttributes(newAttr);
					}
					else
						newAttr = new TextAttributes();
					
					determineAttrDiff(attributes,newAttr,add,remove,
							listNewLine || alignChange || isNoTextSec);
					
					// close tags that have been removed					
					if(remove.size() > 0)
						content.append(_exportType.getAttributeEndTags(remove,attributes));
					
					// add alignment-end
					if(alignChange || listNewLine)
						content.append(_exportType.getLineAlignmentEnd(align));

					// add line-end if necessary
					Paragraph prevPara = (Paragraph)p.getPrev();
					if(prevPara != null && start <= prevPara.getElementEndPos() + 1 &&
							sec.isFirstInParagraph()) {
						if(!prevPara.containsEnvironment())
							content.append(_exportType.getLineEnd(listNewLine,false));
					}
					
					// add a paragraph-start if this section is in a new paragraph
					if(start < sec.getEndPosInEnv() && sec.isFirstInParagraph()) {
						// only add a paragraph-ending if the last section was a ContentSection
						Paragraph prev = (Paragraph)p.getPrev();
						if(prev == null || prev.getFirstSection() instanceof ContentSection)
							content.append(_exportType.getParagraphStart(listNewLine,false));
					}
					
					// add alignment-start
					if(sec.isFirst() && p.getHorizontalAlignment() != align ||
							(listNewLine && p.getHorizontalAlignment() != ParagraphAttributes.ALIGN_LEFT)) {
						align = p.getHorizontalAlignment();
						content.append(_exportType.getLineAlignmentStart(align));
					}
					
					if(sec.getElementLength() > 0) {
						// append the text of this section
						int textStart = Math.max(0,start - sec.getStartPosInEnv());
						int length = Math.min(sec.getElementLength() - textStart,
								end - sec.getStartPosInEnv() - textStart);
						
						if(length > 0) {
							// add new attributes
							if(!add.isEmpty())
								content.append(_exportType.getAttributeStartTags(add,attributes));
							
							if(textSec instanceof TextSection) {
								String text = ((TextSection)textSec).getText(textStart,length);
								content.append(_exportType.getText(text));
							}
							else if(textSec instanceof SmileySection) {
								SecSmiley smiley = (SecSmiley)((SmileySection)textSec).getImage();
								content.append(_exportType.getSmiley(smiley));
							}
							else {
								SecImage img = ((ImageSection)textSec).getImage();
								content.append(_exportType.getImage(img));
							}
						}
					}
					// we want to allow empty tags after new lines
					else if(sec.isFirst() && !add.isEmpty())
						content.append(_exportType.getAttributeStartTags(add,attributes));
				}
				
				// go to the next section in the current paragraph
			} while((sec = sec.getNextInParagraph()) != null);
			
			// go to the next paragraph
		} while((p = (Paragraph)p.getNext()) != null);
		
		// close remaining tags
		content.append(_exportType.getAttributeEndTags((Stack)attributes.clone(),attributes));
		
		// add alignment-end
		content.append(_exportType.getLineAlignmentEnd(align));
		
		// add line-end
		content.append(_exportType.getLineEnd(isInList,true));
		
		return content.toString();
	}
	
	/**
	 * determines the difference between the two given attribute-maps <code>newAttr</code>
	 * and <code>current</code>.
	 * The method stores the attributes to add in the list <code>add</code> and the attributes
	 * to remove in <code>rem</code>
	 * 
	 * @param current your current attributes
	 * @param newAttr the new attributes
	 * @param add will contain the attributes which have been added
	 * @param rem will contain the attributes which have been removed
	 * @param closeAll close all tags and add them again afterwards?
	 */
	private void determineAttrDiff(Stack current,TextAttributes newAttr,TextAttributes add,
			Stack rem,boolean closeAll) {
		// at first we search for the attributes which have been removed
		// therefore we walk through our current attributes and look which are not enabled
		// in the new attributes
		boolean foundFirst = false;
		Iterator it = current.iterator();
		while(it.hasNext()) {
			Pair e = (Pair)it.next();
			Integer attrKey = (Integer)e.getKey();
			Object attrVal = newAttr.get(attrKey);
			
			// alignment-change?
			if(closeAll) {
				// so remove all keys
				rem.add(e);
				
				// and add it again if we don't want to disable it
				if(attrVal != null && (!(attrVal instanceof Boolean) || ((Boolean)attrVal).booleanValue()))
					add.set(attrKey,newAttr.get(attrKey));
			}
			// is the attribute not available or disabled
			else if(attrVal == null) {
				rem.add(e);
				foundFirst = true;
			}
			// is the value different?
			else if(!foundFirst && !attrVal.equals(e.getValue())) {
				rem.add(e);
				foundFirst = true;
			}
			// if we have already found an attribute to remove, all following attributes
			// will also be removed. therefore we want to add attributes again which have to be
			// closed but should not be disabled
			else if(foundFirst)
				add.set(attrKey,newAttr.get(attrKey));
		}
		
		// now we search for the attributes which have been added
		// so we walk through the new ones and look which of them are not enabled
		// in the current attributes
		it = newAttr.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			Pair e = new Pair(attr,newAttr.get(attr));
			
			// was the attribute not available?
			if(!current.contains(e))
				add.set(attr,e.getValue());
		}
	}
}