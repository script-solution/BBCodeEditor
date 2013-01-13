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
import java.awt.Font;
import java.security.InvalidParameterException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;



/**
 * A class that provides the properties of the environments. Each environment-type
 * may have different properties like padding, default font-family, line-spacing
 * and so on.<br>
 * The properties can be retrieved and set with this class.
 * 
 * @author hrniels
 */
public class EnvironmentProperties {

	/**
	 * the property which defines the outer-padding of an environment.<br>
	 * That means the padding which will be applied before anything gets painted
	 */
	public static final Integer OUTER_PADDING					= new Integer(0);
	
	/**
	 * the property which defines the inner-padding of an environment.<br>
	 * Inner-padding means that this is the padding for the text from the
	 * environment-border
	 */
	public static final Integer INNER_PADDING					= new Integer(1);

	/**
	 * the property which defines wether an environment allows formating and paragraph-
	 * alignment
	 */
	public static final Integer CONTAINS_STYLES				= new Integer(2);
	
	/**
	 * the property which defines wether an environment allows sub-environments
	 */
	public static final Integer CONTAINS_ENVS					= new Integer(3);
	
	/**
	 * the property which defines the default font-family for an environment.
	 */
	public static final Integer DEF_FONT_FAMILY				= new Integer(4);
	
	/**
	 * the property which defines the default font-size for an environment.
	 */
	public static final Integer DEF_FONT_SIZE					= new Integer(5);
	
	/**
	 * the property which defines the default font-style for an environment.
	 */
	public static final Integer DEF_FONT_STYLE				= new Integer(6);
	
	/**
	 * the property which defines the default font-color for an environment.
	 */
	public static final Integer DEF_FONT_COLOR				= new Integer(7);
	
	/**
	 * the property which defines the default background-color for an environment.
	 */
	public static final Integer BACKGROUND_COLOR	= new Integer(8);
	
	/**
	 * the property which defines the spacing between the lines in an environment
	 */
	public static final Integer LINE_SPACING					= new Integer(9);
	
	/**
	 * the property which defines the wordwrap-strategie of an environment
	 */
	public static final Integer WORD_WRAP_STRATEGIE		= new Integer(10);
	
	/**
	 * the border-color of an environment (if available)
	 */
	public static final Integer BORDER_COLOR					= new Integer(12);
	
	/**
	 * the background-color of an environment-title (if available)
	 */
	public static final Integer TITLE_BG_COLOR				= new Integer(13);
	
	/**
	 * the font-color in an environment-title (if available)
	 */
	public static final Integer TITLE_FONT_COLOR			= new Integer(14);
	
	
	/**
	 * indicates that all paint-positions should be refreshed
	 */
	static final int REFRESH_PAINT_POS								= 0;
	
	/**
	 * indicates that the content should be recalculated
	 */
	static final int REFRESH_CONTENT									= 1;
	
	/**
	 * indicates that the fonts should be refreshed
	 */
	static final int REFRESH_FONTS										= 2;
	
	/**
	 * indicates that the word-wrap should be refreshed
	 */
	static final int REFRESH_WORD_WRAP								= 3;
	
	/**
	 * indicates that no refresh (except repaint) is necessary
	 */
	static final int REFRESH_NONE											= 4;
	
	/**
	 * the textfield instance
	 */
	private final AbstractTextField _textField;
	
	/**
	 * a map of maps:
	 * <pre>
	 * {
	 * 	envType1 => {property1,...,propertyN},
	 * 	...
	 * 	envTypeN => {property1,...,propertyN}
	 * }
	 * </pre>
	 */
	private final Map _properties = new HashMap();
	
	/**
	 * The event-listeners for property-changes
	 */
	private final EventListenerList _propertyListener = new EventListenerList();

	/**
	 * constructor
	 * 
	 * @param textField the textfield-instance
	 */
	public EnvironmentProperties(AbstractTextField textField) {
		_textField = textField;
		
		// we have to init the default styles
		initDefaultStyles(EnvironmentTypes.ENV_ROOT);
		initDefaultStyles(EnvironmentTypes.ENV_QUOTE);
		initDefaultStyles(EnvironmentTypes.ENV_LIST);
		initDefaultStyles(EnvironmentTypes.ENV_CODE);
	}
	
	/**
	 * Adds the given property-listener to the list
	 * 
	 * @param l the listener
	 */
	public void addPropertyListener(PropertyListener l) {
		if(l == null)
			throw new InvalidParameterException("l = null");
		
		_propertyListener.add(PropertyListener.class,l);
	}

	/**
	 * Removes the given property-listener from the list
	 * 
	 * @param l the listener
	 */
	public void removePropertyListener(PropertyListener l) {
		if(l == null)
			throw new InvalidParameterException("l = null");
		
		_propertyListener.remove(PropertyListener.class,l);
	}
	
	/**
	 * determines the refresh-type for the given property
	 * 
	 * @param property the property
	 * @return the refresh-type. see REFRESH_*
	 */
	int getRefreshType(Integer property) {
		if(property.equals(OUTER_PADDING) || property.equals(INNER_PADDING) ||
				property.equals(LINE_SPACING))
			return REFRESH_PAINT_POS;
		
		if(property.equals(CONTAINS_STYLES) || property.equals(CONTAINS_ENVS))
			return REFRESH_CONTENT;
		
		if(property.equals(WORD_WRAP_STRATEGIE))
			return REFRESH_WORD_WRAP;
		
		if(property.equals(BORDER_COLOR) ||
				property.equals(TITLE_BG_COLOR) || property.equals(TITLE_FONT_COLOR))
			return REFRESH_NONE;
		
		return REFRESH_FONTS; 
	}
	
	/**
	 * inits the default style for the given environment-type
	 * 
	 * @param envType the environment-type
	 */
	private void initDefaultStyles(int envType) {
		Map properties = new HashMap();
		if(envType != EnvironmentTypes.ENV_ROOT)
			properties.put(OUTER_PADDING,new Integer(5));
		else
			properties.put(OUTER_PADDING,new Integer(0));
		
		properties.put(INNER_PADDING,new Integer(5));

		if(envType == EnvironmentTypes.ENV_CODE) {
			properties.put(CONTAINS_STYLES,new Boolean(false));
			properties.put(CONTAINS_ENVS,new Boolean(false));
			
			properties.put(DEF_FONT_FAMILY,"Courier new");
			
			properties.put(WORD_WRAP_STRATEGIE,new WordWrapNoWrap());
		}
		else {
			properties.put(CONTAINS_STYLES,new Boolean(true));
			properties.put(CONTAINS_ENVS,new Boolean(true));
			
			properties.put(DEF_FONT_FAMILY,"Verdana");
			
			properties.put(WORD_WRAP_STRATEGIE,new WordWrapPixelBased(_textField));
		}

		properties.put(DEF_FONT_SIZE,new Integer(12));
		properties.put(DEF_FONT_STYLE,new Integer(Font.PLAIN));
		properties.put(DEF_FONT_COLOR,Color.BLACK);
		properties.put(BACKGROUND_COLOR,Color.WHITE);
		
		properties.put(LINE_SPACING,new Integer(3));
		
		if(envType == EnvironmentTypes.ENV_CODE || envType == EnvironmentTypes.ENV_QUOTE) {
			properties.put(TITLE_BG_COLOR,new Color(99,117,145));
			properties.put(TITLE_FONT_COLOR,Color.WHITE);
			properties.put(BORDER_COLOR,new Color(153,153,153));
		}
		
		_properties.put(new Integer(envType),properties);
		
		// no property change here because we do that at the very beginning
	}
	
	/**
	 * sets the value for the given property and the given environment-type
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @param value the new value
	 * @return true if the value has been changed
	 */
	public boolean setValue(Integer property,int envType,Object value) {
		if(!isValidProperty(property))
			return false;
		
		if(!EnvironmentTypes.isValidEnvType(envType))
			return false;
		
		if(value == null || !isValidValue(property,value))
			return false;
		
		Map envMap = (Map)_properties.get(new Integer(envType));
		if(envMap != null) {
			Object old = envMap.put(property,value);
			firePropertyChange(property,old,value);
			return !old.equals(value);
		}
		
		return false;
	}
	
	/**
	 * determines the value for the given property and the given environment-type
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return the value of the property or null if an error occurred
	 */
	public Object getValue(Integer property,int envType) {
		if(!isValidProperty(property))
			return null;
		
		if(!EnvironmentTypes.isValidEnvType(envType))
			return null;
		
		Map envMap = (Map)_properties.get(new Integer(envType));
		if(envMap != null)
			return envMap.get(property);
		
		return null;
	}
	
	/**
	 * Fires a property-changed event
	 * 
	 * @param property the property
	 * @param oldVal the old value
	 * @param newVal the new value
	 */
	private void firePropertyChange(Integer property,Object oldVal,Object newVal) {
		PropertyListener[] listener = (PropertyListener[])_propertyListener.getListeners(
				PropertyListener.class);
		for(int i = 0;i < listener.length;i++)
			listener[i].propertyChanged(property,oldVal,newVal);
	}
	
	/**
	 * checks wether the given value is allowed for the given property
	 * 
	 * @param property the property
	 * @param value the value to check
	 * @return true if the value is allowed for the property
	 */
	private boolean isValidValue(Integer property,Object value) {
		if(property.equals(OUTER_PADDING))
			return value instanceof Integer;
		if(property.equals(INNER_PADDING))
			return value instanceof Integer;
		
		if(property.equals(CONTAINS_STYLES))
			return value instanceof Boolean;
		if(property.equals(CONTAINS_ENVS))
			return value instanceof Boolean;
		
		if(property.equals(DEF_FONT_FAMILY))
			return value instanceof String;
		if(property.equals(DEF_FONT_SIZE))
			return value instanceof Integer;
		if(property.equals(DEF_FONT_STYLE))
			return value instanceof Integer;
		if(property.equals(DEF_FONT_COLOR))
			return value instanceof Color;
		if(property.equals(BACKGROUND_COLOR))
			return value instanceof Color;
		
		if(property.equals(LINE_SPACING))
			return value instanceof Integer;
		
		if(property.equals(WORD_WRAP_STRATEGIE))
			return value instanceof IWordWrap;
		
		if(property.equals(BORDER_COLOR))
			return value instanceof Color;
		if(property.equals(TITLE_BG_COLOR))
			return value instanceof Color;
		if(property.equals(TITLE_FONT_COLOR))
			return value instanceof Color;
		
		return false;
	}
	
	/**
	 * checks wether the given property is valid
	 * 
	 * @param property the property to check
	 * @return true if it is valid
	 */
	private boolean isValidProperty(Integer property) {
		if(property == null)
			return false;
		
		return property.intValue() >= OUTER_PADDING.intValue() &&
			property.intValue() <= TITLE_FONT_COLOR.intValue();
	}
	
	/**
	 * The property listener
	 * 
	 * @author hrniels
	 */
	public static interface PropertyListener extends EventListener {
		
		/**
		 * Will be called if a property has changed
		 * 
		 * @param property
		 * @param oldVal
		 * @param newVal
		 */
		void propertyChanged(Integer property,Object oldVal,Object newVal);
	}
}