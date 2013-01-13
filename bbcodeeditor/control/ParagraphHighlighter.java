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

package bbcodeeditor.control;

import java.awt.Point;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbcodeeditor.control.highlighter.HighlightSyntax;
import bbcodeeditor.control.highlighter.Highlighter;
import bbcodeeditor.control.highlighter.KeywordSettings;
import bbcodeeditor.control.highlighter.RegExDesc;
import bbcodeeditor.control.tools.Pair;
import bbcodeeditor.control.tools.StringTreeMap;
import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.control.tools.StringTreeMap.TreeNode;


/**
 * The highlighter for paragraphs
 * 
 * @author hrniels
 */
class ParagraphHighlighter {
	
	/**
	 * The pattern for numbers
	 */
	private static Pattern _numberPattern = null;
	
	/**
	 * The paragrah
	 */
	private final Paragraph _para;
	
	/**
	 * All highlight-types of this paragraph
	 */
	private final Set _hlTypes = new HashSet();
	
	/**
	 * Stores the visible area
	 */
	private final VisibleArea _visibleArea = new VisibleArea(0);
	
	/**
	 * The highlighter
	 */
	private Highlighter _hl;

	/**
	 * Constructor
	 * 
	 * @param p the paragraph
	 */
	public ParagraphHighlighter(Paragraph p) {
		_para = p;
	}
	
	/**
	 * @return the highlighter we should use
	 */
	private Highlighter getHighlighter() {
		if(_hl == null) {
			CodeEnvironment env = (CodeEnvironment)_para.getParentEnvironment();
			_hl = HighlightSyntax.getHighlighter(env.getHighlightSyntax());
		}
		
		return _hl;
	}
	
	/**
	 * Resets the highlighter so that we will re-determine it next time
	 */
	void resetHighlighter() {
		_hl = null;
		_visibleArea.setVisible();
		_hlTypes.clear();
	}
	
	/**
	 * Tries to find the type of the comment in this paragraph
	 * 
	 * @return the id if found or null
	 */
	public Object getCommentId() {
		Iterator it = _hlTypes.iterator();
		while(it.hasNext()) {
			HighlightType type = (HighlightType)it.next();
			if(type.getType() == HighlightType.CONTAINS_COMMENT ||
					type.getType() == HighlightType.CONTAINS_COMMENT_START)
				return type.getTypeId();
		}
		
		return null;
	}
	
	/**
	 * Tries to find the type of the string in this paragraph
	 * 
	 * @return the id if found or null
	 */
	public Object getStringId() {
		Iterator it = _hlTypes.iterator();
		while(it.hasNext()) {
			HighlightType type = (HighlightType)it.next();
			if(type.getType() == HighlightType.CONTAINS_STRING ||
					type.getType() == HighlightType.CONTAINS_STRING_START)
				return type.getTypeId();
		}
		
		return null;
	}
	
	/**
	 * Checks wether this paragraph contains a comment-start or is between a comment-start
	 * and comment-end.
	 * 
	 * @return true if so
	 */
	public boolean containsCommentOrStart() {
		HighlightType type1 = new HighlightType(HighlightType.CONTAINS_COMMENT);
		HighlightType type2 = new HighlightType(HighlightType.CONTAINS_COMMENT_START);
		return _hlTypes.contains(type1) || _hlTypes.contains(type2);
	}
	
	/**
	 * Checks wether this paragraph contains a comment-end
	 * 
	 * @return true if so
	 */
	public boolean containsCommentOrEnd() {
		HighlightType type1 = new HighlightType(HighlightType.CONTAINS_COMMENT);
		HighlightType type2 = new HighlightType(HighlightType.CONTAINS_COMMENT_END);
		return _hlTypes.contains(type1) || _hlTypes.contains(type2);
	}
	
	/**
	 * Checks wether this paragraph contains a comment-end of the given type
	 * 
	 * @param typeId the type
	 * @return true if so
	 */
	public boolean containsCommentOrEnd(Object typeId) {
		Iterator it = _hlTypes.iterator();
		while(it.hasNext()) {
			HighlightType hl = (HighlightType)it.next();
			if(hl.getType() == HighlightType.CONTAINS_COMMENT ||
					hl.getType() == HighlightType.CONTAINS_COMMENT_END) {
				if(hl.getTypeId().equals(typeId))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks wether this paragraph contains a string or a string-start
	 * 
	 * @return true if so
	 */
	public boolean containsStringOrStart() {
		HighlightType type1 = new HighlightType(HighlightType.CONTAINS_STRING);
		HighlightType type2 = new HighlightType(HighlightType.CONTAINS_STRING_START);
		return _hlTypes.contains(type1) || _hlTypes.contains(type2);
	}

	/**
	 * Checks wether this paragraph contains a string or a string-end
	 * 
	 * @return true if so
	 */
	public boolean containsStringOrEnd() {
		HighlightType type1 = new HighlightType(HighlightType.CONTAINS_STRING);
		HighlightType type2 = new HighlightType(HighlightType.CONTAINS_STRING_END);
		return _hlTypes.contains(type1) || _hlTypes.contains(type2);
	}

	/**
	 * Checks wether this paragraph contains a string or a string-end of
	 * the given type
	 * 
	 * @param typeId the type
	 * @return true if so
	 */
	public boolean containsStringOrEnd(Object typeId) {
		Iterator it = _hlTypes.iterator();
		while(it.hasNext()) {
			HighlightType hl = (HighlightType)it.next();
			if(hl.getType() == HighlightType.CONTAINS_STRING ||
					hl.getType() == HighlightType.CONTAINS_STRING_END) {
				if(hl.getTypeId() == typeId)
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Highlights this paragraph as a string of given type
	 * 
	 * @param stringIndex the type
	 * @return true if we have found a string-end
	 */
	public boolean highlightAsString(Object stringIndex) {
		Highlighter hl = getHighlighter();
		String pText = _para.getText();
		Map strTypes = hl.getStringQuotes();
		TextAttributes attributes = hl.getStringAttributes(stringIndex);

		// clear all types because we determine them again
		_hlTypes.clear();
		
		char c = ((Character)strTypes.get(stringIndex)).charValue();
		int p = 0;
		int eIndex;
		do {
			eIndex = pText.indexOf(c,p);
		
			// search for string-end
			if(eIndex >= 0 && !StringUtils.isEscaped(pText,eIndex,hl.getEscapeChar())) {
				_hlTypes.add(new HighlightType(HighlightType.CONTAINS_STRING_END,stringIndex));

				// mark visible area
				_visibleArea.setStart(eIndex + 1);
				_visibleArea.setEnd();
				highlightText(0,eIndex + 1,attributes,pText);
				
				// mark paragraph-content as dirty (highlightDefault() might do nothing)
				_para.getTextField().getViewManager().markParagraphDirty(_para);

				// highlight the rest, by default
				highlightDefault();
				return true;
			}
			
			p = eIndex + 1;
		} while(eIndex >= 0);
		
		// ok, no string-end, so we are in the string
		_visibleArea.setInvisible();
		highlightText(0,_para.getElementLength(),attributes,pText);
		_para.getTextField().getViewManager().markParagraphDirty(_para);
			
		_hlTypes.add(new HighlightType(HighlightType.CONTAINS_STRING,stringIndex));
		return false;
	}
	
	/**
	 * Highlights this paragraph as a comment of given type
	 * 
	 * @param commentId the type
	 * @return true if we have found a comment-end
	 */
	public boolean highlightAsComment(Object commentId) {
		Highlighter hl = getHighlighter();
		String pText = _para.getText();
		Map mlTypes = hl.getMultiCommentLimiters();
		TextAttributes attributes = hl.getMLCommentAttributes(commentId);
		
		// clear all types because we determine them again
		_hlTypes.clear();
		
		Pair p = (Pair)mlTypes.get(commentId);
		
		String end = (String)p.getValue();
		int eIndex = pText.indexOf(end);
		
		// have we found the comment-end?
		if(eIndex >= 0) {
			_hlTypes.add(new HighlightType(HighlightType.CONTAINS_COMMENT_END,commentId));
			
			// mark the visible area
			_visibleArea.setStart(eIndex + end.length());
			_visibleArea.setEnd();
			highlightText(0,eIndex + end.length(),attributes,pText);
			
			// mark paragraph-content as dirty (highlightDefault() might do nothing)
			_para.getTextField().getViewManager().markParagraphDirty(_para);
			
			// highlight the rest by default
			highlightDefault();
			return true;
		}
		
		// ok, no comment-end, so we are in a comment
		_visibleArea.setInvisible();
		highlightText(0,_para.getElementLength(),attributes,pText);
		_para.getTextField().getViewManager().markParagraphDirty(_para);
			
		_hlTypes.add(new HighlightType(HighlightType.CONTAINS_COMMENT,commentId));
		return false;
	}
	
	/**
	 * Highlights the paragraph
	 */
	public void highlight() {
		// clear all
		_hlTypes.clear();
		_visibleArea.setVisible();
		
		highlightDefault();
	}
	
	/**
	 * Performs the default-highlighting.
	 */
	private void highlightDefault() {
		// is there nothing to do?
		if(_visibleArea.isInvisible() || _para.getElementLength() == 0)
			return;
		
		Highlighter hl = getHighlighter();
		String pText = _para.getText();

		List deniedAreas = new ArrayList();
		TextAttributes attributes;
		
		// grab some vars from the highlighter
		Map mlTypes = hl.getMultiCommentLimiters();
		Map slTypes = hl.getSingleComments();
		Map strQuotes = hl.getStringQuotes();
		char escChar = hl.getEscapeChar();
		Point area = _visibleArea.getArea(_para);
		
		// at first we clear the not-denied intervals
		attributes = new TextAttributes();
		highlightText(area.x,area.y,attributes,pText);
		
		// init some flags and vars
		int cStart = -1;
		int sStart = -1;
		char sStartChar = 0;
		Object stringId = null;
		Object commentId = null;
		boolean inStr = false;
		boolean inComment = false;
		boolean foundSLC = false;
		
		// search for multiline comments and strings
		for(int i = area.x,len = area.y;i < len;i++) {
			char c = pText.charAt(i);
			
			// search for strings
			Object strId = null;
			if(!inComment && (strId = getStringId(strQuotes,c)) != null &&
					!StringUtils.isEscaped(pText,i,escChar)) {
				// is it a closing char?
				if(c == sStartChar && inStr) {
					inStr = false;
					TextAttributes strAttrs = hl.getStringAttributes(strId);
					highlightText(sStart,i + 1,strAttrs,pText);
					deniedAreas.add(new Point(sStart,i + 1));
				}
				// just add the start if it is a real start (not in other elements)
				else if(!inStr) {
					sStart = i;
					sStartChar = c;
					stringId = strId;
					inStr = true;
				}
				
				continue;
			}
			
			// don't search comments in strings
			if(!inStr) {
				if(!inComment) {
					// search for single-line-comments
					Object slStartId = getSLCommentStart(slTypes,pText,i);
					if(slStartId != null) {
						// if there is one, highlight it and break here
						TextAttributes slAttrs = hl.getSLCommentAttributes(slStartId);
						highlightText(i,_para.getElementLength(),slAttrs,pText);
						foundSLC = true;
						deniedAreas.add(new Point(i,_para.getElementLength()));
						break;
					}
					
					// search for comment-start-tags
					Object startTagId = getCommentStart(mlTypes,pText,i);
					if(startTagId != null) {
						cStart = i;
						inComment = true;
						commentId = startTagId;
						continue;
					}
				}
				
				// search for comment-end-tags
				Object endTagId = getCommentEnd(mlTypes,pText,i);
				if(endTagId != null) {
					// is the comment-start also in this paragraph?
					if(inComment) {
						if(mlTypes.get(commentId).equals(mlTypes.get(endTagId))) {
							inComment = false;
							TextAttributes mlAttrs = hl.getMLCommentAttributes(endTagId);
							highlightText(cStart,i + 2,mlAttrs,pText);
							deniedAreas.add(new Point(cStart,i + 2));
						}
					}
				}
			}
		}
		
		// is a comment not closed?
		if(inComment && (!inStr || sStart > cStart)) {
			_visibleArea.setEnd(cStart);
			TextAttributes mlAttrs = hl.getMLCommentAttributes(commentId);
			highlightText(cStart,_para.getElementLength(),mlAttrs,pText);
		}
		// is a string not closed?
		else if(inStr) {
			_visibleArea.setEnd(sStart);
			TextAttributes strAttrs = hl.getStringAttributes(stringId);
			highlightText(sStart,_para.getElementLength(),strAttrs,pText);
		}
		
		String pLower = pText.toLowerCase();
		
		// replace keywords
		Map keywords = hl.getKeywords();
		Iterator it = keywords.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			StringTreeMap treeMap = (StringTreeMap)e.getValue();
			KeywordSettings settings = hl.getKeywordSettings(e.getKey());
			replaceWords(treeMap,settings.isCaseSensitive() ? pText : pLower,
					hl.getKeywordAttributes(e.getKey()),deniedAreas,
					settings.isCaseSensitive(),settings.requireWord());
		}
		
		// numbers
		if(hl.highlightNumbers()) {
			attributes = hl.getAttributes(Highlighter.NUMBER);
			Pattern p = _numberPattern;
			if(p == null) {
				p = Pattern.compile("\\b-?(\\d+|\\d*\\.\\d+)\\b");
				_numberPattern = p;
			}
			
			Matcher m = p.matcher(pText);
			while(m.find()) {
				String match = m.group();
				int start = m.start();
				int end = start + match.length();
	
				// highlight, if the area is not denied
				if(!isInDeniedArea(deniedAreas,start,end)) {
					highlightText(start,end,attributes,pText);
					deniedAreas.add(new Point(start,end));
				}
			}
		}
		
		// symbols
		replaceWords(hl.getSymbols(),pLower,hl.getAttributes(Highlighter.SYMBOL),
				deniedAreas,true,false);
		
		// other regeexps
		if(hl.getRegexps().size() > 0) {
			Map regexps = hl.getRegexps();
			it = regexps.entrySet().iterator();
			while(it.hasNext()) {
				Entry e = (Entry)it.next();
				attributes = hl.getRegexpAttributes(e.getKey());
				RegExDesc regex = (RegExDesc)e.getValue();
				
				// find matches
				Matcher m = regex.getPattern().matcher(pText);
				while(m.find()) {
					String match = m.group(regex.getGroup());
					int start = m.start(regex.getGroup());
					int end = start + match.length();

					// highlight, if the area is not denied
					if(!isInDeniedArea(deniedAreas,start,end)) {
						highlightText(start,end,attributes,pText);
					}
				}
			}
		}
		
		
		removeStrings();
		removeStringStarts();
		removeComments();
		removeCommentStarts();
		
		// mark the paragraph-content as dirty, because applyAttributes() does not
		// mark the sections as dirty!
		_para.getTextField().getViewManager().markParagraphDirty(_para);
		
		// comment-start?
		if(inComment && (!inStr || sStart > cStart)) {
			if(_visibleArea.isVisible(cStart))
				_hlTypes.add(new HighlightType(HighlightType.CONTAINS_OTHER));
			
			_hlTypes.add(new HighlightType(HighlightType.CONTAINS_COMMENT_START,commentId));
			return;
		}
		
		// open string?
		if(inStr && !foundSLC) {
			if(_visibleArea.isVisible(sStart))
				_hlTypes.add(new HighlightType(HighlightType.CONTAINS_OTHER));
			
			_hlTypes.add(new HighlightType(HighlightType.CONTAINS_STRING_START,
					getStringId(strQuotes,sStartChar)));
			return;
		}
		
		// default
		removeOther();
		_hlTypes.add(new HighlightType(HighlightType.CONTAINS_OTHER));
		_visibleArea.setEnd();
	}
	
	/**
	 * Searches all entries in the given treemap and highlights them with the given
	 * attributes.
	 *
	 * @param treeMap the treeMap with the words to highlight
	 * @param pText the text of the paragraph
	 * @param attributes the attributes to apply
	 * @param deniedAreas the list with the denied areas
	 * @param caseSensitive match case-sensitive?
	 * @param reqWord are words required? (word-boundary on each side)
	 */
	private void replaceWords(StringTreeMap treeMap,String pText,TextAttributes attributes,
			List deniedAreas,boolean caseSensitive,boolean reqWord) {
		// nothing to do?
		if(treeMap.size() == 0)
			return;
		
		// TODO we can optimize that by using a Heap in the StringTreeMap for the
		// successors instead of Maps, right?
		
		Point varea = _visibleArea.getArea(_para);
		StringBuffer buf = new StringBuffer();
		int p = -1;
		TreeNode n = treeMap.getRoot();
		
		// loop through the visible area
		for(int i = varea.x,len = varea.y;i < len;i++) {
			p++;
			char c = pText.charAt(i);
			char last = reqWord && p > 0 ? pText.charAt(i - 1) : 0;
			
			// we append it to the buffer if it is empty or we have a word-boundary
			if(!reqWord || buf.length() > 0 || last == 0 || isWordBoundary(last)) {
				buf.append(c);
				
				// if the path does not exist we may have found a match (without
				// the current char)
				n = n.getSuccessor(c);
				if(n == null) {
					if(buf.length() > 1 && (!reqWord || isWordBoundary(c))) {
						// check if the entry exists in the map
						String func = buf.substring(0,buf.length() - 1);
						if(treeMap.contains(func)) {
							// apply the attribute, if the area is not denied
							int start = i - buf.length() + 1;
							int end = start + func.length();
							if(!isInDeniedArea(deniedAreas,start,end)) {
								highlightText(start,end,attributes,pText);
								deniedAreas.add(new Point(start,end));
							}
						}
					}
					
					// clear the buffer for new matches
					buf = new StringBuffer();
					if(!reqWord || isWordBoundary(last)) {
						n = treeMap.getRoot().getSuccessor(c);
						if(n == null)
							n = treeMap.getRoot();
						else
							buf.append(c);
					}
					else
						n = treeMap.getRoot();
				}
			}
		}
		
		// replace match at the end?
		if(buf.length() > 0) {
			// check if the entry exists in the map
			if(treeMap.contains(buf.toString())) {
				// apply the attribute, if the area is not denied
				int start = varea.y - buf.length();
				int end = start + buf.length();
				if(!isInDeniedArea(deniedAreas,start,end)) {
					highlightText(start,end,attributes,pText);
					deniedAreas.add(new Point(start,end));
				}
			}
		}
	}
	
	/**
	 * Highlights the given text-part
	 * 
	 * @param start the start-position
	 * @param end the end-position
	 * @param attributes the attributes
	 * @param text the text of the whole paragraph
	 */
	private void highlightText(int start,int end,TextAttributes attributes,String text) {
		_para.applyAttributes(start,end,attributes,true);
	}
	
	/**
	 * Checks wether the given char is a word-boundary
	 *
	 * @param c the character
	 * @return true if so
	 */
	private boolean isWordBoundary(char c) {
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_')
			return false;
		
		// TODO: how to handle that?
		if(c == '$')
			return false;
		
		return true;
	}
	
	/**
	 * Removes other
	 */
	private void removeOther() {
		_hlTypes.remove(new HighlightType(HighlightType.CONTAINS_OTHER));
	}
	
	/**
	 * Removes comments
	 */
	private void removeComments() {
		_hlTypes.remove(new HighlightType(HighlightType.CONTAINS_COMMENT));
	}
	
	/**
	 * Removes all comment-starts
	 */
	private void removeCommentStarts() {
		_hlTypes.remove(new HighlightType(HighlightType.CONTAINS_COMMENT_START));
	}
	
	/**
	 * Removes all string-starts
	 */
	private void removeStringStarts() {
		_hlTypes.remove(new HighlightType(HighlightType.CONTAINS_STRING_START));
	}
	
	/**
	 * Removes all strings
	 */
	private void removeStrings() {
		_hlTypes.remove(new HighlightType(HighlightType.CONTAINS_STRING));
	}
	
	/**
	 * Checks wether the given interval is in one of the given denied areas.
	 * 
	 * @param deniedAreas the list with denied areas
	 * @param start the start-pos
	 * @param end the end-pos
	 * @return true if so
	 */
	private boolean isInDeniedArea(List deniedAreas,int start,int end) {
		if(!_visibleArea.isVisible(start) || !_visibleArea.isVisible(end))
			return true;
		
		Iterator daIt = deniedAreas.iterator();
		while(daIt.hasNext()) {
			Point denied = (Point)daIt.next();
			if((start > denied.x && start < denied.y) || (end > denied.x && end < denied.y))
				return true;
		}
		
		return false;
	}
	
	// TODO good idea, but has to be implemented in a better way :)
	private Entry[] _stringIdCache = null;
	
	/**
	 * Determines the id of the given character in the string-map
	 * 
	 * @param strings the string-quotes-map
	 * @param c the character
	 * @return the id or null if not found
	 */
	private Object getStringId(Map strings,char c) {
		if(_stringIdCache == null) {
			_stringIdCache = new Entry[strings.size()];
			Iterator mlIt = strings.entrySet().iterator();
			for(int i = 0;mlIt.hasNext();i++) {
				Entry e = (Entry)mlIt.next();
				_stringIdCache[i] = e;
			}
		}
		
		for(int i = 0;i < _stringIdCache.length;i++) {
			if(((Character)_stringIdCache[i].getValue()).charValue() == c)
				return _stringIdCache[i].getKey();
		}
		
		return null;
		
		/*Character cc = Character.valueOf(c);
		Iterator mlIt = strings.entrySet().iterator();
		while(mlIt.hasNext()) {
			Entry e = (Entry)mlIt.next();
			if(cc.equals(e.getValue()))
				return e.getKey();
		}
		
		return null;*/
	}
	
	/**
	 * Checks wether the given position in the given text is a single-line
	 * comment-start
	 * 
	 * @param mlTypes the single-line-comment-types
	 * @param text the text
	 * @param pos the position
	 * @return the id of the end or null if not found
	 */
	private Object getSLCommentStart(Map slTypes,String text,int pos) {
		Iterator mlIt = slTypes.entrySet().iterator();
		while(mlIt.hasNext()) {
			Entry e = (Entry)mlIt.next();
			if(text.startsWith((String)e.getValue(),pos))
				return e.getKey();
		}
		
		return null;
	}
	
	/**
	 * Checks wether the given position in the given text is a multi-line
	 * comment-start
	 * 
	 * @param mlTypes the multi-line-comment-types
	 * @param text the text
	 * @param pos the position
	 * @return the id of the end or null if not found
	 */
	private Object getCommentStart(Map mlTypes,String text,int pos) {
		Iterator mlIt = mlTypes.entrySet().iterator();
		while(mlIt.hasNext()) {
			Entry e = (Entry)mlIt.next();
			Pair p = (Pair)e.getValue();
			if(text.startsWith((String)p.getKey(),pos))
				return e.getKey();
		}
		
		return null;
	}
	
	/**
	 * Checks wether the given position in the given text is a multi-line
	 * comment-end
	 * 
	 * @param mlTypes the multi-line-comment-types
	 * @param text the text
	 * @param pos the position
	 * @return the id of the end or null if not found
	 */
	private Object getCommentEnd(Map mlTypes,String text,int pos) {
		Iterator mlIt = mlTypes.entrySet().iterator();
		while(mlIt.hasNext()) {
			Entry e = (Entry)mlIt.next();
			Pair p = (Pair)e.getValue();
			if(text.startsWith((String)p.getValue(),pos))
				return e.getKey();
		}
		
		return null;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(_visibleArea + " " + _hlTypes);
		return buf.toString();
	}
	
	/**
	 * The visible area to prevent that we will highlight something in multi-line
	 * comments, multi-line strings and so on
	 * 
	 * @author hrniels
	 */
	private static final class VisibleArea {
		
		/**
		 * Represents the end of the paragraph
		 */
		private static final int END_OF_PARAGRAPH = -1;
		
		/**
		 * The start-pos
		 */
		private int _start;
		
		/**
		 * The end-pos
		 */
		private int _end;
		
		/**
		 * Constructor. Sets the end-position to the paragraph-end
		 * 
		 * @param start the start-position
		 */
		public VisibleArea(int start) {
			this(start,END_OF_PARAGRAPH);
		}
		
		/**
		 * Constructor
		 * 
		 * @param start the start-position
		 * @param end the end-position
		 */
		public VisibleArea(int start,int end) {
			_start = start;
			_end = end;
		}
		
		/**
		 * Determines the area that is visible for the given paragraph
		 * 
		 * @param p the paragraph
		 * @return the area
		 */
		public Point getArea(Paragraph p) {
			if(_end == END_OF_PARAGRAPH)
				return new Point(_start,p.getElementLength());
			
			return new Point(_start,_end);
		}
		
		/**
		 * Checks wether the position is visible
		 * 
		 * @param pos the position
		 * @return true if so
		 */
		public boolean isVisible(int pos) {
			return pos >= _start && (_end == END_OF_PARAGRAPH || pos <= _end);
		}
		
		/**
		 * @return true if nothing is visible
		 */
		public boolean isInvisible() {
			return _start == _end;
		}
		
		/**
		 * Sets that everything is visible
		 */
		public void setVisible() {
			_start = 0;
			_end = END_OF_PARAGRAPH;
		}
		
		/**
		 * Sets that nothing is visible
		 */
		public void setInvisible() {
			_start = _end = 0;
		}
		
		/**
		 * Sets the start-position
		 * 
		 * @param start the new value
		 */
		public void setStart(int start) {
			_start = start;
		}

		/**
		 * Sets the end-position to the paragraph-end
		 */
		public void setEnd() {
			setEnd(END_OF_PARAGRAPH);
		}

		/**
		 * Sets the end-position
		 * 
		 * @param end the new value
		 */
		public void setEnd(int end) {
			_end = end;
		}
		
		public String toString() {
			return "(" + _start + "," + _end + ")";
		}
	}
	
	/**
	 * A highlight-type which contains additional information if needed
	 * 
	 * @author hrniels
	 */
	private static final class HighlightType {
		
		/**
		 * Indicates that this paragraph is a multiline-comment
		 */
		public static final int CONTAINS_COMMENT					= 0;
		
		/**
		 * Indicates that this paragraph contains a multiline-comment-start
		 */
		public static final int CONTAINS_COMMENT_START		= 1;
		
		/**
		 * Indicates that this paragraph contains a multiline-comment-end
		 */
		public static final int CONTAINS_COMMENT_END			= 2;
		
		/**
		 * Indicates that a paragraph contains an open string (not closed in
		 * this paragraph)
		 */
		public static final int CONTAINS_STRING_START			= 3;
		
		/**
		 * Indicates that a paragraph contains a string (not opened and closed
		 * in this paragraph)
		 */
		public static final int CONTAINS_STRING						= 4;
		
		/**
		 * Indicates that a paragraph contains a closing string (not opened in
		 * this paragraph)
		 */
		public static final int CONTAINS_STRING_END				= 5;
		
		/**
		 * Indicates that a paragraph may contain multiple highlight elements
		 */
		public static final int CONTAINS_OTHER						= 6;
		
		/**
		 * The type
		 */
		private final int _type;
		
		/**
		 * The type-index
		 */
		private final Object _typeId;
		
		/**
		 * Constructor
		 * 
		 * @param type the type
		 */
		public HighlightType(int type) {
			this(type,null);
		}
		
		/**
		 * Constructor
		 * 
		 * @param type the type
		 * @param typeId the type-index
		 */
		public HighlightType(int type,Object typeId) {
			_type = type;
			_typeId = typeId;
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof HighlightType))
				return false;
			
			if(o == this)
				return true;
			
			HighlightType type = (HighlightType)o;
			return type._type == _type;
		}
		
		public int hashCode() {
			return _type;
		}

		/**
		 * @return the type
		 */
		public int getType() {
			return _type;
		}
		
		/**
		 * @return the type-id
		 */
		public Object getTypeId() {
			return _typeId;
		}
		
		public String toString() {
			return "HT[" + HighlightType.valueOf(_type) + "]";
		}
		
		/**
		 * Returns the string-representation of the given type
		 * 
		 * @param type the type
		 * @return the string for the type
		 */
		public static String valueOf(int type) {
			switch(type) {
				case CONTAINS_COMMENT:
					return "COM";
				case CONTAINS_COMMENT_START:
					return "COMSTART";
				case CONTAINS_COMMENT_END:
					return "COMEND";
				case CONTAINS_STRING_START:
					return "STRSTART";
				case CONTAINS_STRING:
					return "STR";
				case CONTAINS_STRING_END:
					return "STREND";
				case CONTAINS_OTHER:
					return "OTHER";
			}
			return "UNKNOWN";
		}
	}
}