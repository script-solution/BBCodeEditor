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

import java.awt.geom.Rectangle2D;
import java.security.InvalidParameterException;
import java.util.Iterator;

import bbcodeeditor.control.view.TextSectionView;


/**
 * A TextSection is a ContentSection which contains text.<br>
 * If the Environment the section belongs to allows formatings
 * the formating of this TextSection may be changed.
 * <p>
 * A TextSection contains the text, a map with all attributes and the Font-instance
 * <p>
 * Tabs are also supported. The text does simply contain the tab-character (<code>\t</code>),
 * but the TextSections does additionally contain a cached text where tab-characters are
 * replaced by the corresponding number of spaces.
 * 
 * @author hrniels
 */
public final class TextSection extends ContentSection {
	
	/**
	 * the text of this section
	 * NOTE: use StringBuffer for multi-threading!
	 */
	private final StringBuffer _text = new StringBuffer();
	
	/**
	 * the attributes of this section
	 */
	private TextAttributes _attributes;
	
	/**
	 * Constructor
	 * 
	 * @param env the environment which contains this section
	 * @param text the text of the section
	 * @param start the "global" start-position
	 * @param line the line of this section
	 * @param p the paragraph of this section
	 * @param attributes the attributes for this section
	 */
	TextSection(Environment env,String text,int start,Line line,
			Paragraph p,TextAttributes attributes) {
		this(env,text,start,line,p,attributes,true);
	}
	
	/**
	 * Constructor
	 * 
	 * @param env the environment which contains this section
	 * @param text the text of the section
	 * @param start the "global" start-position
	 * @param line the line of this section
	 * @param p the paragraph of this section
	 * @param attributes the attributes for this section
	 * @param forceFontRefresh do you want to force a FONT and TEXT_BOUNDS refresh?
	 */
	TextSection(Environment env,String text,int start,Line line,
			Paragraph p,TextAttributes attributes,boolean forceFontRefresh) {
		super(env,start,start + text.length() - 1,line,p);
		
		_view = new TextSectionView(this);
		
		if(attributes != null)
			_attributes = (TextAttributes)attributes.clone();

		_text.append(text);
		_view.forceRefresh(TextSectionView.PAINT_TEXT);
		if(forceFontRefresh) {
			_view.forceRefresh(TextSectionView.FONT);
			_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
		}
	}
	
	/**
	 * @return the TextSectionView-implementation
	 */
	public TextSectionView getTextSectionView() {
		return (TextSectionView)_view;
	}
	
	/**
	 * removes the end of the text starting at given position
	 * 
	 * @param start the start-position inside this section
	 * 				(that means the first character in this section has the index 0)
	 * @throws IndexOutOfBoundsException if the given start-position is invalid
	 */
	void removeText(int start) throws IndexOutOfBoundsException {
		removeText(start,_length - start);
	}
	
	/**
	 * removes a text at given position and length
	 * 
	 * @param start the start-position inside this section
	 * 				(that means the first character in this section has the index 0)
	 * @param length the length of the text which should be deleted
	 * @throws IndexOutOfBoundsException if the given start-position or the length is invalid
	 */
	void removeText(int start,int length) throws IndexOutOfBoundsException {
		if(start < 0 || start > _length)
			throw new IndexOutOfBoundsException("Invalid index " + start);
	
		if(length <= 0 || length > _length)
			throw new IndexOutOfBoundsException("Invalid length " + length);
		
		_text.delete(start,start + length);
		_length -= length;
		_endPos -= length;

		_view.forceRefresh(TextSectionView.PAINT_TEXT);
		_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
	}
	
	/**
	 * inserts the given text at position <code>pos</code>
	 * 
	 * @param text the text you want to add
	 * @param pos the position of the text
	 * @param refreshTextBounds do you want to refresh the text-bounds?
	 * @throws IndexOutOfBoundsException if the position is invalid
	 * @throws InvalidParameterException if the text is empty
	 */
	void addTextAt(String text,int pos,boolean refreshTextBounds)
			throws IndexOutOfBoundsException,InvalidParameterException {
		if(pos < 0 || pos > _text.length())
			throw new IndexOutOfBoundsException("Invalid position " + pos);
		
		if(text == null || text.length() == 0)
			throw new InvalidParameterException("Empty text");
		
		if(_text.length() == pos)
			_text.append(text);
		else
			_text.insert(pos,text);

		_length += text.length();
		_endPos += text.length();

		_view.forceRefresh(TextSectionView.PAINT_TEXT);
		_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
	}
	
	/**
	 * determines the character at given index
	 * 
	 * @param index the index of the character
	 * @return the char at given index
	 */
	public char getCharAt(int index) {
		return _text.charAt(index);
	}
	
	/**
	 * the content of this section
	 * this does NOT support tabs (which is important for painting-issues)
	 * 
	 * @return the text in this section
	 */
	public String getText() {
		return _text.toString();
	}
	
	/**
	 * the content of this section. returns an empty string if the start-position is invalid
	 * this does NOT support tabs (which is important for painting-issues)
	 * 
	 * @param start the start-position
	 * @return the text in this section starting at <code>start</code>
	 * @throws IndexOutOfBoundsException if the start-position is invalid
	 */
	public String getText(int start) throws IndexOutOfBoundsException {
		if(start < 0 || start > _length)
			throw new IndexOutOfBoundsException("Invalid start-position " + start);
		
		return _text.substring(start);
	}
	
	/**
	 * the content of this section. returns an empty string if the start-position or length
	 * is invalid
	 * this does NOT support tabs (which is important for painting-issues)
	 * 
	 * @param start the start-position
	 * @param length the length of the text
	 * @return the text in this section starting at <code>start</code>
	 * @throws IndexOutOfBoundsException if the start-position or length is invalid
	 */
	public String getText(int start,int length) throws IndexOutOfBoundsException {
		if(start < 0 || start > _length)
			throw new IndexOutOfBoundsException("Invalid start-position " + start);
		
		if(length <= 0 || length > _length)
			throw new IndexOutOfBoundsException("Invalid length " + length);
			
		return _text.substring(start,start + length);
	}
	
	/**
	 * makes a clone of the attributes and returns them.
	 * 
	 * @return a clone of the attributes.
	 */
	public TextAttributes getCloneOfAttributes() {
		return getCloneOfAttributes(false);
	}
	
	/**
	 * makes a clone of the attributes and returns them
	 * 
	 * @param removeLinks do you want to remove URLs and emails?
	 * @return a clone of the attributes.
	 */
	public TextAttributes getCloneOfAttributes(boolean removeLinks) {
		TextAttributes attr;
		if(_attributes == null)
			attr = new TextAttributes();
		else
			attr = (TextAttributes)_attributes.clone();
		
		if(removeLinks) {
			// TODO remove or unset?
			// don't copy URLs/emails
			attr.remove(TextAttributes.URL);
			attr.remove(TextAttributes.EMAIL);
		}
		
		return attr;
	}
	
	/**
	 * returns a SortedHashtable of all attributes
	 * 
	 * @return all attributes of this section
	 */
	public TextAttributes getAttributes() {
		return _attributes == null ? new TextAttributes() : _attributes;
	}
	
	/**
	 * returns the value of given attribute
	 * If it does not exist the method will return <code>null</code>
	 * 
	 * @param attribute the attribute you are looking for
	 * @return the value [Object]
	 */
	public Object getAttribute(Integer attribute) {
		return getAttribute(attribute,null);
	}
	
	/**
	 * returns the value of given attribute
	 * If it does not exist the method will <code>defValue</code>
	 * 
	 * @param attribute the attribute you are looking for
	 * @param defValue the default value
	 * @return the value [Object]
	 */
	public Object getAttribute(Integer attribute,Object defValue) {
		if(_attributes == null || !_attributes.isSet(attribute))
			return defValue;
		
		return _attributes.get(attribute);
	}
	
	/**
	 * sets the current attributes to <code>attributes</code>
	 * But be carefull: all existing will be deleted!
	 * NOTE: you don't have to clone the map because it will be cloned in this method!
	 * 
	 * @param attributes the new attributes
	 */
	void setAttributeRange(TextAttributes attributes) {
		setAttributeRange(attributes,true);
	}
	
	/**
	 * sets the current attributes to <code>attributes</code>
	 * But be carefull: all existing will be deleted!
	 * NOTE: you don't have to clone the map because it will be cloned in this method!
	 * 
	 * @param attributes the new attributes
	 * @param refreshFont do you want to refresh the font?
	 */
	void setAttributeRange(TextAttributes attributes,boolean refreshFont) {
		// if both are empty we have nothing to do
		if((attributes == null || attributes.isEmpty()) &&
				(_attributes == null || _attributes.isEmpty()))
    	return;
		
		if(attributes == null || attributes.isEmpty())
			_attributes = null;
		else
			_attributes = (TextAttributes)attributes.clone();
		
		if(refreshFont) {
			_view.forceRefresh(TextSectionView.FONT);
			_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
		}
	}
	
	/**
	 * Adds or removes the given attributes to/from this section
	 * if the value is null the attribute will be removed, otherwise
	 * it will be added
	 * 
	 * @param attributes the attributes to add/remove
	 * @return true if something has changed
	 */
	boolean setAttributes(TextAttributes attributes) {
		boolean changed = false;
		Iterator it = attributes.iterator();
		while(it.hasNext()) {
			Integer attr = (Integer)it.next();
			if(setAttribute(attr,attributes.get(attr),false))
				changed = true;
		}
		
		if(changed) {
			if(_attributes == null || _attributes.idNeedsRefresh())
				_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
			_view.forceRefresh(TextSectionView.FONT);
		}
		
		return changed;
	}
	
	/**
	 * sets the given attribute to <code>value</code>
	 * 
	 * @param attribute the attribute
	 * @param value the new value of the given attribute
	 * @return true if something has changed
	 */
	boolean setAttribute(Integer attribute,Object value) {
		return setAttribute(attribute,value,true);
	}
	
	/**
	 * sets the given attribute to <code>value</code>
	 * 
	 * @param attribute the attribute
	 * @param value the new value of the given attribute
	 * @param refreshFont refresh the font?
	 * @return true if something has changed
	 */
	private boolean setAttribute(Integer attribute,Object value,boolean refreshFont) {
		boolean changed = false;
		
		if(value == null)
			return removeAttribute(attribute,refreshFont);
		
		if(_attributes == null)
			_attributes = new TextAttributes();
		
		changed = _attributes.set(attribute,value);
		
		if(changed && refreshFont) {
			if(_attributes.idNeedsRefresh())
					_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
			_view.forceRefresh(TextSectionView.FONT);
		}
		
		// TODO keep this? can only happen if the value is invalid
		if(_attributes != null && _attributes.isEmpty())
			_attributes = null;
		
		return changed;
	}
	
	/**
	 * removes the given attribute from the list
	 * 
	 * @param attribute the attribute you want to remove
	 * @return true if something has changed
	 */
	boolean removeAttribute(Integer attribute) {
		return removeAttribute(attribute,true);
	}
	
	/**
	 * removes the given attribute from the list
	 * 
	 * @param attribute the attribute you want to remove
	 * @param refreshFont do you want to refresh the font?
	 * @return true if something has changed
	 */
	private boolean removeAttribute(Integer attribute,boolean refreshFont) {
		if(_attributes == null)
			return false;

		boolean changed = _attributes.unset(attribute);
		
		if(changed) {
			if(_attributes.isEmpty())
				_attributes = null;
			
			if(refreshFont) {
				if(_attributes.idNeedsRefresh())
					_view.forceRefresh(TextSectionView.TEXT_BOUNDS);
				_view.forceRefresh(TextSectionView.FONT);
			}
		}
		
		return changed;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		String id = String.valueOf(hashCode());
		result.append("Class: TextSection ID: " + id + "\n");
		result.append("PaintPos: " + getTextSectionView().getPaintPos() + "\n");
		
		Rectangle2D rect = getTextSectionView().getBounds();
		String rectStr = null;
		if(rect != null) {
			rectStr = String.format(
				"[x=%.2f,y=%.2f,w=%.2f,h=%.2f]",
				new Double[] {Double.valueOf(rect.getX()),Double.valueOf(rect.getY()),
						Double.valueOf(rect.getWidth()),Double.valueOf(rect.getHeight())}
			);
		}
		
		result.append("Bounds: " + rectStr + "\n");
		result.append("PaintText: '" + getTextSectionView().getPaintText() + "'\n");
		result.append("Text: '" + _text + "'\n");
		result.append("[S:" + _startPos + ",E:" + _endPos + ",L:" + _length + "]\n");
		
		result.append("Attributes: " + _attributes + "\n");
		
		return result.toString();
	}
}