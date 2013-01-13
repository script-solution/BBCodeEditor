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

package bbcodeeditor.control.highlighter;

import java.util.HashMap;
import java.util.Map;

import bbcodeeditor.control.TextAttributes;
import bbcodeeditor.control.tools.StringTreeMap;


/**
 * The base-class for all highlighters
 * 
 * @author hrniels
 */
public abstract class Highlighter {
	
	/**
	 * Represents a number
	 */
	public static final int NUMBER									= 0;
	
	/**
	 * Represents a symbol
	 */
	public static final int SYMBOL									= 1;
	
	/**
	 * Returns the id of this highlighter
	 * 
	 * @return the id
	 */
	public abstract String getId();
	
	/**
	 * Returns the name of the language for which this highlighter is written
	 * 
	 * @return the language-name
	 */
	public abstract String getLangName();

	/**
	 * Returns a Map of StringTreeMaps with all different keyword-types that
	 * should be highlighted. It maps an id to a StringTreeMap with all keywords.
	 * The id will be used for the attributes.
	 * 
	 * @return the map
	 * @see #getKeywordAttributes(Object)
	 * @see #getKeywordSettings(Object)
	 */
	public abstract Map getKeywords();

	/**
	 * Returns all keywords of the given type
	 * 
	 * @param key the key in the keywords-list
	 * @return the list
	 * @see #getKeywordSettings(Object)
	 * @see #getKeywordAttributes(Object)
	 */
	public abstract StringTreeMap getKeywords(Object key);
	
	/**
	 * Returns a map with multi-line comment-types. The value should be a Pair
	 * for the start and end.
	 * The id will be used for the attributes.
	 * 
	 * @return the map
	 * @see #getMLCommentAttributes(Object)
	 */
	public abstract Map getMultiCommentLimiters();
	
	/**
	 * Returns a map with all single-line-comment-starts.
	 * The id will be used for the attributes.
	 * 
	 * @return the map
	 * @see #getSLCommentAttributes(Object)
	 */
	public abstract Map getSingleComments();
	
	/**
	 * A list with all strings that are special symbols in the language
	 * 
	 * @return the treeMap
	 */
	public abstract StringTreeMap getSymbols();
	
	/**
	 * Returns a map with all string-types.
	 * The id will be used for the attributes.
	 * 
	 * @return the map
	 */
	public abstract Map getStringQuotes();
	
	/**
	 * The escape-character for strings
	 * 
	 * @return the escape-character
	 */
	public abstract char getEscapeChar();
	
	/**
	 * Returns a map with regular expressions which should be highlighter.
	 * The map should contain instances of {@link RegExDesc}.
	 * The id will be used for the attributes.
	 *
	 * @return the map
	 * @see RegExDesc
	 * @see #getRegexpAttributes(Object)
	 */
	public abstract Map getRegexps();
	
	/**
	 * Returns settings for the keywords with given id
	 * 
	 * @param id the id in the keyword-list
	 * @return a KeywordSettings-object which contains the settings for the
	 * 	keywords
	 * @see #getKeywords()
	 */
	public abstract KeywordSettings getKeywordSettings(Object id);
	
	/**
	 * @return wether numbers should be highlighted
	 */
	public abstract boolean highlightNumbers();
	
	/**
	 * Returns the attributes for the regular expression with given index
	 * 
	 * @param id the id in the regexp-list
	 * @return the TextAttributes-object with the attributes
	 */
	public abstract TextAttributes getRegexpAttributes(Object id);
	
	/**
	 * Returns the attributes for the single-line-comment with given index
	 * 
	 * @param id the comment-id
	 * @return the TextAttributes-object with the attributes
	 */
	public abstract TextAttributes getSLCommentAttributes(Object id);

	/**
	 * Returns the attributes for the multi-line-comment with given index
	 * 
	 * @param id the comment-id
	 * @return the TextAttributes-object with the attributes
	 */
	public abstract TextAttributes getMLCommentAttributes(Object id);

	/**
	 * Returns the attributes for the string with given index
	 * 
	 * @param id the string-id
	 * @return the TextAttributes-object with the attributes
	 */
	public abstract TextAttributes getStringAttributes(Object id);
	
	/**
	 * Returns the attributes for the keywords of given type
	 * 
	 * @param id the id in the keyword-list
	 * @return the TextAttributes-object with the attributes
	 */
	public abstract TextAttributes getKeywordAttributes(Object id);
	
	/**
	 * This method will be used to determine the style of a highlight-element.
	 * You can use arbitrary formating. The <code>element</code> will be one of
	 * the following:
	 * <ul>
	 * 	<li>FUNCTION</li>
	 * 	<li>DATATYPE</li>
	 * 	<li>NUMBER</li>
	 * 	<li>SYMBOL</li>
	 * </ul>
	 * 
	 * @param element the element
	 * @return the attributes for that element
	 */
	public abstract TextAttributes getAttributes(int element);
	
	/**
	 * Builds a StringTreeMap from the given string-array
	 * 
	 * @param elements the elements to add
	 * @return the StringTreeMap
	 */
	protected static StringTreeMap getTreeMap(String[] elements) {
		StringTreeMap map = new StringTreeMap();
		for(int i = 0;i < elements.length;i++)
			map.add(elements[i],new Boolean(true));
		return map;
	}
	
	/**
	 * Builds a map with all given elements as values and assigns every
	 * element an id (which is the key). The ids are numeric (but strings)
	 * and start with 1.
	 * 
	 * @param elements the elements to put in the map
	 * @return the map
	 */
	protected static Map getDefaultMap(Object[] elements) {
		Map m = new HashMap();
		for(int i = 0;i < elements.length;i++)
			m.put(String.valueOf(i + 1),elements[i]);
		return m;
	}
}