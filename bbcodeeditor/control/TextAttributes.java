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

import java.awt.Color;
import java.util.*;

import bbcodeeditor.control.export.ColorFunctions;
import bbcodeeditor.control.view.IEnvironmentView;

/**
 * 
 * 
 * @author hrniels
 */
public class TextAttributes implements Cloneable {
	
	/**
	 * Represents the value "sup" for the position
	 */
	public static final byte POS_SUPERSCRIPT	= 1;
	
	/**
	 * Represents the value "sub" for the position
	 */
	public static final byte POS_SUBSCRIPT		= 2;
	
	/**
	 * Represents the default text-position
	 */
	public static final byte POS_NORMAL				= 3;
	

	/**
	 * The highlight-attribute<br>
	 * The value is a java.awt.Color object
	 */
	public static final Integer HIGHLIGHT			= new Integer(12);

	/**
	 * Background-color<br>
	 * The value is a java.awt.Color object
	 */
	public static final Integer BG_COLOR			= new Integer(11);

	/**
	 * An email-address<br>
	 * The value is a java.lang.String object
	 */
	public static final Integer EMAIL					= new Integer(10);

	/**
	 * An URL<br>
	 * The value is a java.lang.String object
	 */
	public static final Integer URL						= new Integer(9);

	/**
	 * The position of the text<br>
	 * The value is a java.lang.Byte object with the values: TextAttributes.POS_NORMAL,
	 * TextAttributes.POS_SUPERSCRIPT or TextAttributes.POS_SUBSCRIPT
	 */
	public static final Integer POSITION			= new Integer(7);

	/**
	 * Strike<br>
	 * The value is a java.lang.Boolean object
	 */
	public static final Integer STRIKE				= new Integer(6);

	/**
	 * Underlined<br>
	 * The value is a java.lang.Boolean object
	 */
	public static final Integer UNDERLINE			= new Integer(5);

	/**
	 * Italic<br>
	 * The value is a java.lang.Boolean object
	 */
	public static final Integer ITALIC				= new Integer(4);

	/**
	 * Bold<br>
	 * The value is a java.lang.Boolean object
	 */
	public static final Integer BOLD					= new Integer(3);

	/**
	 * The font-color<br>
	 * The value is a java.awt.Color object
	 */
	public static final Integer FONT_COLOR		= new Integer(2);

	/**
	 * The font-size<br>
	 * The value is a java.lang.Integer object
	 */
	public static final Integer FONT_SIZE			= new Integer(1);

	/**
	 * The font-family<br>
	 * The value is simply the string with the font-family-name (case-insensitive)
	 */
	public static final Integer FONT_FAMILY		= new Integer(0);
	
	
	/**
	 * The number of attributes that are available
	 */
	private static final int NUMBER_OF_ATTRIBUTES = 13;
	
	/**
	 * A list with all attributes
	 */
	private static final List _allAttributes = Arrays.asList(new Integer[] {
		TextAttributes.BOLD,
		TextAttributes.ITALIC,
		TextAttributes.UNDERLINE,
		TextAttributes.STRIKE,
		TextAttributes.POSITION,
		TextAttributes.FONT_SIZE,
		TextAttributes.FONT_COLOR,
		TextAttributes.FONT_FAMILY,
		TextAttributes.EMAIL,
		TextAttributes.URL,
		TextAttributes.BG_COLOR,
		TextAttributes.HIGHLIGHT
	});
	
	/**
	 * The allowed fonts
	 */
	private static List _allowedFonts = Arrays.asList(new String[] {
		"verdana","tahoma","courier new","times new roman","sans serif",
		"arial","comic sans ms"
	});
	
	/**
	 * Builds a List with all possible attributes(-keys).
	 * The Vector contains instances of integer
	 * 
	 * @return the list
	 * @see #ensureSet(List)
	 */
	public static List getAll() {
		return _allAttributes;
	}
	
	/**
	 * Clones the attributes given by the list from the given object
	 * 
	 * @param attr the object to clone
	 * @param attributes a list with all attributes to clone
	 * @return the clone
	 */
	public static TextAttributes getCloneWith(TextAttributes attr,List attributes) {
		TextAttributes clone = new TextAttributes();
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer key = (Integer)it.next();
			clone.set(key,attr.get(key));
		}
		clone.regenerateId();
		return clone;
	}
	
	/**
	 * debugging-information
	 * 
	 * @param attribute the attribute you are looking for
	 * @return the name of the given attribute
	 * @see #getAttributeFromName(String)
	 */
	public static String getAttributeName(Integer attribute) {
		if(attribute.equals(TextAttributes.FONT_FAMILY))
			return "fontFamily";
		if(attribute.equals(TextAttributes.FONT_SIZE))
			return "fontSize";
		if(attribute.equals(TextAttributes.FONT_COLOR))
			return "fontColor";
		if(attribute.equals(TextAttributes.BOLD))
			return "bold";
		if(attribute.equals(TextAttributes.ITALIC))
			return "italic";
		if(attribute.equals(TextAttributes.UNDERLINE))
			return "underline";
		if(attribute.equals(TextAttributes.STRIKE))
			return "strike";
		if(attribute.equals(TextAttributes.POSITION))
			return "pos";
		if(attribute.equals(TextAttributes.URL))
			return "URL";
		if(attribute.equals(TextAttributes.EMAIL))
			return "email";
		if(attribute.equals(TextAttributes.BG_COLOR))
			return "bgColor";
		
		return "Unknown attribute";
	}
	
	/**
	 * Determines the attribute for the given name
	 * 
	 * @param name the attribute-name
	 * @return the attribute or null
	 * @see #getAttributeName(Integer)
	 */
	public static Integer getAttributeFromName(String name) {
		if(name.equals("fontFamily"))
			return TextAttributes.FONT_FAMILY;
		if(name.equals("fontSize"))
			return TextAttributes.FONT_SIZE;
		if(name.equals("fontColor"))
			return TextAttributes.FONT_COLOR;
		if(name.equals("bold"))
			return TextAttributes.BOLD;
		if(name.equals("italic"))
			return TextAttributes.ITALIC;
		if(name.equals("underline"))
			return TextAttributes.UNDERLINE;
		if(name.equals("strike"))
			return TextAttributes.STRIKE;
		if(name.equals("pos"))
			return TextAttributes.POSITION;
		if(name.equals("URL"))
			return TextAttributes.URL;
		if(name.equals("email"))
			return TextAttributes.EMAIL;
		if(name.equals("bgColor"))
			return TextAttributes.BG_COLOR;
		
		return null;
	}
	
	/**
	 * Ensures that the value for the given attribute is valid. If it is not
	 * the corresponding default value will be returned
	 * 
	 * @param attribute the attibute
	 * @param val the value to check
	 * @return the valid value for the attribute
	 */
	public static Object getValidValueFor(Integer attribute,Object val) {
		if(isToggleAttribute(attribute))
			return new Boolean(Boolean.parseBoolean(String.valueOf(val)));
		
		if(attribute.equals(FONT_FAMILY)) {
			if(_allowedFonts.contains(String.valueOf(val).toLowerCase()))
				return val;
			
			return "verdana";
		}
		
		if(attribute.equals(FONT_SIZE)) {
			try {
				int i = Integer.parseInt(String.valueOf(val));
				if(i >= 0 && i <= 29)
					return new Integer(i);
				
				return new Integer(12);
			}
			catch(Exception e) {
				return new Integer(12);
			}
		}
		
		if(attribute.equals(FONT_COLOR) || attribute.equals(BG_COLOR))
			return ColorFunctions.getColorFromString(String.valueOf(val),Color.BLACK);
		
		if(attribute.equals(POSITION)) {
			if(((Byte)val).byteValue() == POS_SUBSCRIPT)
				return new Byte(POS_SUBSCRIPT);
			return new Byte(POS_SUPERSCRIPT);
		}
	
		return val;
	}
	
	/**
	 * checks wether the given attribute is a toggle-attribute
	 * 
	 * @param attribute the attribute to check
	 * @return true if it is a toggle-attribute
	 */
	public static boolean isToggleAttribute(Integer attribute) {
		return attribute.equals(BOLD) || attribute.equals(ITALIC) ||
					 attribute.equals(UNDERLINE) || attribute.equals(STRIKE);
	}

	/**
	 * we have one existing section: A
	 * and one we want to add into that section: B
	 * this method determines if the text of B could simply be pasted into A or if you have
	 * to create a new section in A and split A
	 * 
	 * @param env the Environment the attribute-sets belong to
	 * @param a the first set
	 * @param b the second set
	 * @return true if you should split the sections
	 */
	public static boolean splitSectionBFromA(Environment env,TextAttributes a,TextAttributes b) {
		// check if the attribute-sets are empty
		if((a == null || a.isEmpty()) && (b == null || b.isEmpty()))
			return false;
		
		boolean aIsLink = a != null && (a.getURL() != null || a.getEmail() != null);
		boolean bIsLink = b != null && (b.getURL() != null || b.getEmail() != null);
		
		// split them if a is not a link and b is a link
		if(!aIsLink && bIsLink)
			return true;
		
		// split them if both are a link
		if(aIsLink && bIsLink)
			return true;
		
		// otherwise we want to split them if the attributes are not equal
		return !compareAttributes(env,a,b);
	}

	/**
	 * compares the given attribute-sets
	 * takes care of the default-values, null and so on
	 * 
	 * @param env the Environment the attribute-sets belong to
	 * @param a the first set
	 * @param b the second set
	 * @return true if they are equal (which does NOT mean that the containers are equal!)
	 */
	public static boolean compareAttributes(Environment env,TextAttributes a,TextAttributes b) {
		boolean aEmpty = a == null || a.isEmpty();
		boolean bEmpty = b == null || b.isEmpty();
		
		// both empty?
		if(aEmpty && bEmpty)
			return true;
		
		// a empty and b not or b empty and a not?
		if((aEmpty && !bEmpty) || (bEmpty && !aEmpty))
			return false;
		
		return a.equals(b);
	}

	/**
	 * compares the two given attribute-values in the given environment
	 * 
	 * @param env the environment
	 * @param attribute the attribute to compare
	 * @param aoVal the value of the first one
	 * @param boVal the value of the second one
	 * @return true if the two values are equal
	 */
	public static boolean compareAttribute(Environment env,Integer attribute,Object aoVal,Object boVal) {
		IEnvironmentView envView = env.getEnvView();
		if(aoVal != null || boVal != null) {
			if(isToggleAttribute(attribute)) {
				// TODO the environment may have default bold/italic/underline styles...
				if(aoVal == null && boVal != null) {
					boolean bVal = ((Boolean)boVal).booleanValue();
					if(bVal)
						return false;
				}
				else if(aoVal != null && boVal == null) {
					boolean aVal = ((Boolean)aoVal).booleanValue();
					if(aVal)
						return false;
				}
				else {
					boolean bVal = ((Boolean)boVal).booleanValue();
					boolean aVal = ((Boolean)aoVal).booleanValue();
					if(bVal != aVal)
						return false;
				}
			}
			else if(attribute.equals(POSITION)) {
				if(aoVal == null && boVal != null) {
					Byte bVal = (Byte)boVal;
					if(bVal.byteValue() != POS_NORMAL)
						return false;
				}
				else if(aoVal != null && boVal == null) {
					Byte aVal = (Byte)aoVal;
					if(aVal.byteValue() != POS_NORMAL)
						return false;
				}
				else {
					Byte bVal = (Byte)boVal;
					Byte aVal = (Byte)aoVal;
					if(aVal.byteValue() != bVal.byteValue())
						return false;
				}
			}
			else if(attribute.equals(FONT_FAMILY)) {
				if(aoVal == null && boVal != null) {
					String bVal = (String)boVal;
					String defFF = envView.getDefaultFontFamily();
					if(!bVal.equals(defFF))
						return false;
				}
				else if(aoVal != null && boVal == null) {
					String aVal = (String)aoVal;
					String defFF = envView.getDefaultFontFamily();
					if(!aVal.equals(defFF))
						return false;
				}
				else {
					String bVal = (String)boVal;
					String aVal = (String)aoVal;
					if(!aVal.equals(bVal))
						return false;
				}
			}
			else if(attribute.equals(FONT_SIZE)) {
				if(aoVal == null && boVal != null) {
					int bVal = ((Integer)boVal).intValue();
					int defFS = envView.getDefaultFontSize();
					if(bVal != defFS)
						return false;
				}
				else if(aoVal != null && boVal == null) {
					int aVal = ((Integer)aoVal).intValue();
					int defFS = envView.getDefaultFontSize();
					if(aVal != defFS)
						return false;
				}
				else {
					int bVal = ((Integer)boVal).intValue();
					int aVal = ((Integer)aoVal).intValue();
					if(bVal != aVal)
						return false;
				}
			}
			else if(attribute.equals(FONT_COLOR) ||
					attribute.equals(BG_COLOR) ||
					attribute.equals(HIGHLIGHT)) {
				Color defCol;
				if(attribute.equals(FONT_COLOR))
					defCol = envView.getDefaultFontColor();
				else
					defCol = null;
				
				if(aoVal == null && boVal != null) {
					Color bVal = (Color)boVal;
					if(!bVal.equals(defCol))
						return false;
				}
				else if(aoVal != null && boVal == null) {
					Color aVal = (Color)aoVal;
					if(!aVal.equals(defCol))
						return false;
				}
				else {
					Color bVal = (Color)boVal;
					Color aVal = (Color)aoVal;
					if(!aVal.equals(bVal))
						return false;
				}
			}
			else if(attribute.equals(URL) || attribute.equals(EMAIL)) {
				if(aoVal != null && boVal != null) {
					String bVal = (String)boVal;
					String aVal = (String)aoVal;
					if(!aVal.equals(bVal))
						return false;
				}
				else
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Wether the text is bold
	 * 
	 * TODO: we can optimize that by using a byte instead of 4 booleans for the style
	 */
	private byte _bold = -1;
	
	/**
	 * Wether the text is italic
	 */
	private byte _italic = -1;
	
	/**
	 * Wether the text is underlined
	 */
	private byte _underline = -1;
	
	/**
	 * Wether the text is striked
	 */
	private byte _strike = -1;
	
	/**
	 * The font-family
	 * 
	 * TODO: maybe we can optimize this by storing the id (e.g. int) of the font
	 * 	instead of the name of it
	 */
	private String _fontFamily = null;
	
	/**
	 * The font-size
	 */
	private byte _fontSize = -1;
	
	/**
	 * The foreground-color
	 */
	private Color _fontColor = null;
	
	/**
	 * The background-color
	 */
	private Color _bgColor = null;
	
	/**
	 * The highlight-color
	 */
	private Color _highlight = null;
	
	/**
	 * The position of the text
	 */
	private byte _position = -1;
	
	/**
	 * The URL
	 */
	private String _URL = null;
	
	/**
	 * The email-address
	 */
	private String _email = null;
	
	/**
	 * Stores which fields are set. This makes it possible to mark
	 * fields which should be removed and other things.
	 */
	private int _setFields = 0;
	
	/**
	 * The id which identifies this attributes.
	 */
	private int _id = 1;
	
	/**
	 * Stores wether the id should be refreshed
	 */
	private boolean _idNeedsRefresh = false;
	
	/**
	 * Constructor
	 */
	public TextAttributes() {
		regenerateId();
	}
	
	/**
	* Private constructor that allows you to prevent the generation
	* of the id.
	* 
	* @param generateId do you want to generate the id?
	*/
	private TextAttributes(boolean generateId) {
		if(generateId)
			regenerateId();
	}
	
	public Object clone() {
		TextAttributes ta = new TextAttributes(false);
		ta._bold = _bold;
		ta._italic = _italic;
		ta._underline = _underline;
		ta._strike = _strike;
		ta._fontColor = _fontColor != null ? new Color(_fontColor.getRGB()) : null;
		ta._bgColor = _bgColor != null ? new Color(_bgColor.getRGB()) : null;
		ta._highlight = _highlight != null ? new Color(_highlight.getRGB()) : null;
		ta._fontSize = _fontSize;
		ta._fontFamily = _fontFamily;
		ta._URL = _URL != null ? new String(_URL) : null;
		ta._email = _email != null ? new String(_email) : null;
		ta._position = _position;
		ta._setFields = _setFields;
		ta._id = _id;
		ta._idNeedsRefresh = _idNeedsRefresh;
		return ta;
	}
	
	/**
	 * Determines wether no attributes are set
	 * 
	 * @return true if so
	 */
	public boolean isEmpty() {
		return _setFields == 0;
	}
	
	/**
	 * The id which identifies this attributes.
	 * This will not be unique for all possible permutations but we can
	 * use it to map to the different font- and fontMetrics-objects. Therefore
	 * not all attributes will affect the id but only that ones which affect
	 * the font- and fontMetrics-object.
	 * 
	 * @return the id
	 */
	public int getId() {
		return _id;
	}
	
	/**
	 * @return wether the id should be refreshed
	 */
	public boolean idNeedsRefresh() {
		return _idNeedsRefresh || _position != -1;
	}
	
	/**
	 * Regenerates the id of this attributes
	 */
	public void regenerateId() {
		if(_position != -1)
			_id = hashCode();
		else {
			int[] parts = new int[5];
			if(_bold == 1)
				parts[0] = TextAttributes.BOLD.intValue();
			if(_italic == 1)
				parts[1] = TextAttributes.ITALIC.intValue();
			if(_fontSize >= 0)
				parts[2] = _fontSize;
			if(_fontFamily != null)
				parts[3] = _fontFamily.hashCode();
			if(_position != -1)
				parts[4] = _position;
			
			_id = Arrays.hashCode(parts);
		}
		
		_idNeedsRefresh = false;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof TextAttributes))
			return false;
		
		if(o == this)
			return true;
		
		TextAttributes ta = (TextAttributes)o;
		if(ta._bold != _bold)
			return false;
		if(ta._italic != _italic)
			return false;
		if(ta._underline != _underline)
			return false;
		if(ta._strike != _strike)
			return false;
		if(ta._fontSize != _fontSize)
			return false;
		if(ta._position != _position)
			return false;
		if(!isEqual(ta._fontColor,_fontColor))
			return false;
		if(!isEqual(ta._bgColor,_bgColor))
			return false;
		if(!isEqual(ta._fontFamily,_fontFamily))
			return false;
		if(!isEqual(ta._URL,_URL))
			return false;
		if(!isEqual(ta._email,_email))
			return false;
		
		return true;
	}
	
	public int hashCode() {
		Object[] prop = new Object[] {
			new Byte(_bold),new Byte(_italic),new Byte(_underline),new Byte(_strike),
			new Integer(_fontSize),_fontFamily,_bgColor,_fontColor,_highlight,_URL,_email,
			new Byte(_position)
		};
		return Arrays.hashCode(prop);
	}
	
	/**
	 * @return an implementation of Iterator to loop through all attributes
	 */
	public Iterator iterator() {
		return new AttributesIterator();
	}
	
	/**
	 * Checks wether the given attribute is currently set.
	 * "Set" does not mean that the value isn't empty. "Set" indicates that the
	 * field has been set and should therefore be applied to another map, removed
	 * in it or something like that.
	 * 
	 * @param attr the attribute
	 * @return wether it is set
	 */
	public boolean isSet(Integer attr) {
		// Note that we add 1 to the attribute because they start with 0
		return (_setFields & (1 << (attr.intValue() + 1))) != 0;
	}
	
	/**
	 * Marks the given attribute as "set".
	 * 
	 * @param attr the attribute
	 */
	private void markSet(Integer attr) {
		_setFields |= 1 << (attr.intValue() + 1);
	}
	
	/**
	 * Marks the given attribute as "unset"
	 * 
	 * @param attr the attribute
	 */
	private void markUnset(Integer attr) {
		_setFields &= ~(1 << (attr.intValue() + 1));
	}
	
	/**
	 * Unsets the given attribute. Note that this is <b>not</b> the same as
	 * {@link #remove(Integer)}! This method marks the given attribute as not
	 * set. Therefore the attribute will not be recognized by anyone.
	 * <p>
	 * So if you have set an attribute and want to remove it completely you
	 * have to call this method.
	 * 
	 * @param attr the attribute
	 * @return true if something has changed
	 */
	public boolean unset(Integer attr) {
		boolean res = remove(attr);
		markUnset(attr);
		return res;
	}
	
	/**
	 * Sets/removes all attributes in the given TextAttributes-object.
	 * For every entry which is set will be called {@link #remove(Integer)} or
	 * {@link #set(Integer, Object)} depending on wether the value of the
	 * attribute is <code>null</code> or not.
	 * 
	 * @param attributes the attributes to apply
	 * @return true if something has changed
	 */
	public boolean setAll(TextAttributes attributes) {
		boolean changed = false;
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			Object val = attributes.get(attr);
			if(val == null)
				changed |= remove(attr);
			else
				changed |= set(attr,val);
		}
		
		return changed;
	}
	
	/**
	 * This method ensures that all attributes in the given list
	 * are set in this object. That means that all attributes in the list
	 * for which {@link #isSet(Integer)} returns false will be set
	 * via {@link #remove(Integer)}.
	 * 
	 * @param attributes the attributes to set if necessary
	 * @return true if something has changed
	 */
	public boolean ensureSet(List attributes) {
		boolean changed = false;
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer i = (Integer)it.next();
			if(!isSet(i)) {
				markSet(i);
				changed = true;
			}
		}
		
		return changed;
	}
	
	/**
	 * This method sets the given attribute to given value. Additionally it will
	 * be marked as "existing". You may use {@link #unset(Integer)} to undo this.
	 * <p>
	 * The type of <code>value</code> depends on the attribute:
	 * <ul>
	 * 	<li>FONT_COLOR, BG_COLOR, HIGHLIGHT: java.awt.Color</li>
	 * 	<li>BOLD, ITALIC, UNDERLINE, STRIKE: java.lang.Boolean</li>
	 * 	<li>FONT_SIZE: java.lang.Integer</li>
	 * 	<li>FONT_FAMILY, EMAIL, URL: java.lang.String</li>
	 * 	<li>POSITION: java.lang.Byte: {@link #POS_NORMAL},
	 * 		{@link #POS_SUPERSCRIPT} or {@link #POS_SUBSCRIPT}</li>
	 * </ul>
	 * Note that a <code>value</code> of <code>null</code> will call
	 * {@link #remove(Integer)}!
	 * 
	 * @param attr the attribute
	 * @param value the value of the attribute
	 * @return true if something has changed
	 * @see #unset(Integer)
	 * @see #remove(Integer)
	 */
	public boolean set(Integer attr,Object value) {
		if(value == null)
			return remove(attr);
		
		boolean res = false;
		if(attr.equals(TextAttributes.FONT_COLOR)) {
			if(value instanceof Color)
				res = setFontColor((Color)value);
		}
		else if(attr.equals(TextAttributes.FONT_SIZE)) {
			if(value instanceof Integer)
				res = setFontSize(((Integer)value).intValue());
		}
		else if(attr.equals(TextAttributes.FONT_FAMILY)) {
			if(value instanceof String)
				res = setFontFamily((String)value);
		}
		else if(attr.equals(TextAttributes.BOLD)) {
			if(value instanceof Boolean)
				res = setBold(((Boolean)value).booleanValue());
		}
		else if(attr.equals(TextAttributes.ITALIC)) {
			if(value instanceof Boolean)
				res = setItalic(((Boolean)value).booleanValue());
		}
		else if(attr.equals(TextAttributes.UNDERLINE)) {
			if(value instanceof Boolean)
				res = setUnderline(((Boolean)value).booleanValue());
		}
		else if(attr.equals(TextAttributes.STRIKE)) {
			if(value instanceof Boolean)
				res = setStrike(((Boolean)value).booleanValue());
		}
		else if(attr.equals(TextAttributes.BG_COLOR)) {
			if(value instanceof Color)
				res = setBgColor((Color)value);
		}
		else if(attr.equals(TextAttributes.HIGHLIGHT)) {
			if(value instanceof Color)
				res = setHighlight((Color)value);
		}
		else if(attr.equals(TextAttributes.POSITION)) {
			if(value instanceof Byte)
				res = setPosition(((Byte)value).byteValue());
		}
		else if(attr.equals(TextAttributes.URL)) {
			if(value instanceof String)
				res = setURL((String)value);
		}
		else if(attr.equals(TextAttributes.EMAIL)) {
			if(value instanceof String)
				res = setEmail((String)value);
		}
		
		if(res)
			markSet(attr);
		
		return res;
	}
	
	/**
	 * Removes the given attribute. Note that this method is <b>not</b> the same
	 * as {@link #unset(Integer)}! This method removes the given attribute (sets
	 * it to the empty-value) <u>and</u> marks the given attribute as set.
	 * Therefore you can use this method if you would like to mark the attribute
	 * for removal.
	 * 
	 * @param attr the attribute to remove
	 * @return true if something has changed
	 * @see #unset(Integer)
	 * @see #set(Integer, Object)
	 */
	public boolean remove(Integer attr) {
		boolean res = false;
		if(attr.equals(TextAttributes.FONT_COLOR))
			res = setFontColor(null);
		else if(attr.equals(TextAttributes.FONT_SIZE))
			res = setFontSize(-1);
		else if(attr.equals(TextAttributes.FONT_FAMILY))
			res = setFontFamily(null);
		else if(attr.equals(TextAttributes.BOLD))
			res = setBold(false);
		else if(attr.equals(TextAttributes.ITALIC))
			res = setItalic(false);
		else if(attr.equals(TextAttributes.UNDERLINE))
			res = setUnderline(false);
		else if(attr.equals(TextAttributes.STRIKE))
			res = setStrike(false);
		else if(attr.equals(TextAttributes.BG_COLOR))
			res = setBgColor(null);
		else if(attr.equals(TextAttributes.HIGHLIGHT))
			res = setHighlight(null);
		else if(attr.equals(TextAttributes.POSITION))
			res = setPosition((byte)-1);
		else if(attr.equals(TextAttributes.URL))
			res = setURL(null);
		else if(attr.equals(TextAttributes.EMAIL))
			res = setEmail(null);
		
		// mark it always set because the values are initialized with the
		// "null-value"
		markSet(attr);
		
		return res;
	}
	
	/**
	 * Builds a Vector with all keys that are set in this object.
	 * "Set" means that {@link #isSet(Integer)} returns true for an attribute.
	 * 
	 * @return a Vector with all keys
	 */
	public Vector getKeys() {
		Vector v = new Vector();
		Iterator it = iterator();
		while(it.hasNext()) {
			Integer i = (Integer)it.next();
			v.add(i);
		}
		return v;
	}
	
	/**
	 * A convenience-method for getting the value of given attribute.
	 * <p>
	 * The type of <code>value</code> depends on the attribute:
	 * <ul>
	 * 	<li>FONT_COLOR, BG_COLOR, HIGHLIGHT: java.awt.Color</li>
	 * 	<li>BOLD, ITALIC, UNDERLINE, STRIKE: java.lang.Boolean</li>
	 * 	<li>FONT_SIZE: java.lang.Integer</li>
	 * 	<li>FONT_FAMILY, EMAIL, URL: java.lang.String</li>
	 * 	<li>POSITION: java.lang.Byte: {@link #POS_NORMAL},
	 * 		{@link #POS_SUPERSCRIPT} or {@link #POS_SUBSCRIPT}</li>
	 * </ul>
	 * If {@link #isSet(Integer)} returns true the value will be the value that
	 * has been set. Otherwise the method will return <code>null</code>.
	 * 
	 * @param attr the attribute
	 * @return the value of it
	 */
	public Object get(Integer attr) {
		if(!isSet(attr))
			return null;
		
		if(attr.equals(TextAttributes.FONT_COLOR))
			return getFontColor();
		else if(attr.equals(TextAttributes.FONT_SIZE)) {
			byte size = getFontSize();
			return size == -1 ? null : Integer.valueOf(size);
		}
		else if(attr.equals(TextAttributes.FONT_FAMILY))
			return getFontFamily();
		else if(attr.equals(TextAttributes.BOLD))
			return Boolean.valueOf(isBold());
		else if(attr.equals(TextAttributes.ITALIC))
			return Boolean.valueOf(isItalic());
		else if(attr.equals(TextAttributes.UNDERLINE))
			return Boolean.valueOf(isUnderline());
		else if(attr.equals(TextAttributes.STRIKE))
			return Boolean.valueOf(isStrike());
		else if(attr.equals(TextAttributes.BG_COLOR))
			return getBgColor();
		else if(attr.equals(TextAttributes.HIGHLIGHT))
			return getHighlight();
		else if(attr.equals(TextAttributes.POSITION)) {
			byte pos = getPosition();
			return pos == -1 ? null : Byte.valueOf(pos);
		}
		else if(attr.equals(TextAttributes.URL))
			return getURL();
		else if(attr.equals(TextAttributes.EMAIL))
			return getEmail();
		
		return null;
	}
	
	/**
	 * @return wether BOLD is enabled
	 */
	public boolean isBold() {
		return _bold == 1;
	}

	/**
	 * @param bold wether the text should be bold
	 * @return wether something has changed
	 */
	private boolean setBold(boolean bold) {
		boolean changed = (bold && _bold != 1) || (!bold && _bold != 0);
		if(changed)
			_idNeedsRefresh = true;
		
		_bold = (byte)(bold ? 1 : 0);
		return changed;
	}
	
	/**
	 * @return wether ITALIC is enabled
	 */
	public boolean isItalic() {
		return _italic == 1;
	}
	
	/**
	 * @param italic wether the text should be italic
	 * @return wether something has changed
	 */
	private boolean setItalic(boolean italic) {
		boolean changed = (italic && _italic != 1) || (!italic && _italic != 0);
		if(changed)
			_idNeedsRefresh = true;
		
		_italic = (byte)(italic ? 1 : 0);
		return changed;
	}
	
	/**
	 * @return wether UNDERLINE is enabled
	 */
	public boolean isUnderline() {
		return _underline == 1;
	}
	
	/**
	 * @param underline wether the text should be underlined
	 * @return wether something has changed
	 */
	private boolean setUnderline(boolean underline) {
		boolean changed = (underline && _underline != 1) || (!underline && _underline != 0);
		_underline = (byte)(underline ? 1 : 0);
		return changed;
	}
	
	/**
	 * @return wether STRIKE is enabled
	 */
	public boolean isStrike() {
		return _strike == 1;
	}
	
	/**
	 * @param strike wether the text should be striked
	 * @return wether something has changed
	 */
	private boolean setStrike(boolean strike) {
		boolean changed = (strike && _strike != 1) || (!strike && _strike != 0);
		_strike = (byte)(strike ? 1 : 0);
		return changed;
	}
	
	/**
	 * @return the font-family. Will return <code>null</code> if it is not set.
	 */
	public String getFontFamily() {
		return _fontFamily;
	}
	
	/**
	 * @param family the font-family to set
	 * @return wether something has changed
	 */
	private boolean setFontFamily(String family) {
		boolean changed = !isEqual(_fontFamily,family);
		if(changed)
			_idNeedsRefresh = true;
		
		_fontFamily = family;
		return changed;
	}
	
	/**
	 * @return the font-size. Will return <code>-1</code> if it is not set.
	 */
	public byte getFontSize() {
		return _fontSize;
	}
	
	/**
	 * @param size the font-size to set
	 * @return wether something has changed
	 */
	private boolean setFontSize(int size) {
		boolean changed = _fontSize != (byte)size;
		if(changed)
			_idNeedsRefresh = true;
		
		_fontSize = (byte)size;
		return changed;
	}
	
	/**
	 * @return the font-color. Will return <code>null</code> if it is not set.
	 */
	public Color getFontColor() {
		return _fontColor;
	}
	
	/**
	 * @param color the font-color to set
	 * @return wether something has changed
	 */
	private boolean setFontColor(Color color) {
		boolean changed = !isEqual(_fontColor,color);
		_fontColor = color;
		return changed;
	}
	
	/**
	 * @return the background-color. Will return <code>null</code> if it is not set.
	 */
	public Color getBgColor() {
		return _bgColor;
	}
	
	/**
	 * @param color the background-color to set
	 * @return wether something has changed
	 */
	private boolean setBgColor(Color color) {
		boolean changed = !isEqual(_bgColor,color);
		_bgColor = color;
		return changed;
	}
	
	/**
	 * @return the position. Will return <code>-1</code> if it is not set.
	 */
	public byte getPosition() {
		return _position;
	}
	
	/**
	 * @param position the position to set
	 * @return wether something has changed
	 */
	private boolean setPosition(byte position) {
		boolean changed = _position != position;
		if(changed)
			_idNeedsRefresh = true;
		
		_position = position;
		return changed;
	}
	
	/**
	 * @return the URL. Will return <code>null</code> if it is not set.
	 */
	public String getURL() {
		return _URL;
	}
	
	/**
	 * @param url the URL to set
	 * @return wether something has changed
	 */
	private boolean setURL(String url) {
		boolean changed = !isEqual(_URL,url);
		_URL = url;
		return changed;
	}

	/**
	 * @return the email-address. Will return <code>null</code> if it is not set.
	 */
	public String getEmail() {
		return _email;
	}
	
	/**
	 * @param email the email-address to set
	 * @return wether something has changed
	 */
	private boolean setEmail(String email) {
		boolean changed = !isEqual(_email,email);
		_email = email;
		return changed;
	}
	
	/**
	 * @return the highlight-color. Will return <code>null</code> if it is not set.
	 */
	public Color getHighlight() {
		return _highlight;
	}
	
	/**
	 * @param highlight the highlight-color to set
	 * @return wether something has changed
	 */
	private boolean setHighlight(Color highlight) {
		boolean changed = !isEqual(_highlight,highlight);
		_highlight = highlight;
		return changed;
	}
	
	/**
	 * Compares the given objects and takes care of null-values
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return true if they are equal
	 */
	private boolean isEqual(Object o1,Object o2) {
		if(o1 == null && o2 == null)
			return true;
		
		if((o1 == null && o2 != null) || (o1 != null && o2 == null))
			return false;
		
		return o1.equals(o2);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[id=" + _id + "] {\n");
		
		if(isBold())
			buf.append("bold=true\n");
		if(isItalic())
			buf.append("italic=true\n");
		if(isUnderline())
			buf.append("underline=true\n");
		if(isStrike())
			buf.append("strike=true\n");
		if(getFontColor() != null)
			buf.append("color=" + getFontColor() + "\n");
		if(getBgColor() != null)
			buf.append("bg=" + getBgColor() + "\n");
		if(getHighlight() != null)
			buf.append("hl=" + getHighlight() + "\n");
		if(getFontSize() >= 0)
			buf.append("size=" + getFontSize() + "\n");
		if(getFontFamily() != null)
			buf.append("font=" + getFontFamily() + "\n");
		if(getPosition() >= 0) {
			byte pos = getPosition();
			buf.append("pos=");
			if(pos == POS_NORMAL)
				buf.append("normal");
			else if(pos == POS_SUBSCRIPT)
				buf.append("sub");
			else
				buf.append("super");
			buf.append("\n");
		}
		if(getURL() != null)
			buf.append("URL=" + getURL() + "\n");
		if(getEmail() != null)
			buf.append("Email=" + getEmail() + "\n");
		
		buf.append("}");
		return buf.toString();
	}

	/**
	 * The iterator to loop through all set attributes in {@link TextAttributes}
	 * 
	 * @author hrniels
	 */
	private final class AttributesIterator implements Iterator {
		
		/**
		 * Stores the current position
		 */
		private int _pos = 0;
		
		/**
		 * Stores wether the next position has to be calculated and if there is one
		 */
		private byte _next = 0;
		
		public boolean hasNext() {
			if(_next == 0) {
				for(;_pos < NUMBER_OF_ATTRIBUTES;_pos++) {
					if((_setFields & (1 << (_pos + 1))) != 0) {
						_next = 1;
						return true;
					}
				}
				_next = -1;
			}
			
			return _next != -1;
		}

		/**
		 * Returns the next attribute-key that is set in the TextAttributes-object
		 * 
		 * @return the attribute-key
		 */
		public Object next() {
			if(_next == 0)
				hasNext();
			
			if(_next == -1)
				return null;
			
			_next = 0;
			_pos++;
			return new Integer(_pos - 1);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}