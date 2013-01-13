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


/**
 * The available list-types
 * 
 * @author hrniels
 */
public class ListTypes {

	/**
	 * the list-type circle<br>
	 * contains circles for each list-point on each layer
	 */
	public static final int TYPE_CIRCLE		= 0;
	
	/**
	 * the list-type sqare<br>
	 * contains squares for each list-point on each layer
	 */
	public static final int TYPE_SQUARE		= 1;
	
	/**
	 * the list-type disc<br>
	 * contains discs for each list-point on each layer
	 */
	public static final int TYPE_DISC			= 2;
	
	/**
	 * the numeric list-type<br>
	 * simply prints the number of the point, starting with 1 (1,2,3,...)
	 */
	public static final int TYPE_NUM			= 3;
	
	/**
	 * the roman list-type<br>
	 * prints the number of the point with roman numerals (I,II,III,...)
	 * this is the uppercase version
	 */
	public static final int TYPE_ROMAN_B	= 4;
	
	/**
	 * the roman list-type<br>
	 * prints the number of the point with roman numerals (i,ii,iii,...)
	 * this is the lowercase version
	 */
	public static final int TYPE_ROMAN_S	= 5;
	
	/**
	 * the alpha list-type<br>
	 * prints the number of the point with alpha-characters (A,B,C,...)
	 * this is the uppercase version
	 */
	public static final int TYPE_ALPHA_B	= 6;
	
	/**
	 * the alpha list-type<br>
	 * prints the number of the point with alpha-characters (a,b,c,...)
	 * this is the lowercase version
	 */
	public static final int TYPE_ALPHA_S	= 7;
	
	/**
	 * the default list-type<br>
	 * layer1:			circles<br>
	 * layer2:			discs<br>
	 * layer3...n:	squares<br>
	 */
	public static final int TYPE_DEFAULT	= 8;
	
	/**
	 * No instantiation!
	 */
	private ListTypes() {
		
	}
	
	/**
	 * determines wether the given list-type is valid
	 * 
	 * @param listType the type to check
	 * @return true if it is valid
	 */
	public static boolean isValidType(int listType) {
		return listType == TYPE_ALPHA_B || listType == TYPE_ALPHA_S ||
			listType == TYPE_ROMAN_B || listType == TYPE_ROMAN_S ||
			listType == TYPE_NUM ||
			listType == TYPE_CIRCLE || listType == TYPE_DISC ||
			listType == TYPE_SQUARE || listType == TYPE_DEFAULT;
	}
	
	/**
	 * determines the list-type from the given param
	 * 
	 * @param param the argument for the list-tag
	 * @return the list-type
	 */
	public static int getListTypeFromParam(String param) {
		if(param == null || param.equals(""))
			return TYPE_DEFAULT;
		if(param.equals("circle"))
			return TYPE_CIRCLE;
		if(param.equals("disc"))
			return TYPE_DISC;
		if(param.equals("square"))
			return TYPE_SQUARE;
		if(param.equals("A"))
			return TYPE_ALPHA_B;
		if(param.equals("a"))
			return TYPE_ALPHA_S;
		if(param.equals("I"))
			return TYPE_ROMAN_B;
		if(param.equals("i"))
			return TYPE_ROMAN_S;
		if(param.equals("1"))
			return TYPE_NUM;
		
		return TYPE_DEFAULT;
	}
	
	/**
	 * determines the name of the given list-param
	 * 
	 * @param listType the type of the list
	 * @return the name for the parameter
	 */
	public static String getListParamName(int listType) {
		switch(listType) {
			case TYPE_CIRCLE:
				return "circle";
			case TYPE_DISC:
				return "disc";
			case TYPE_SQUARE:
				return "square";
			case TYPE_ALPHA_B:
				return "A";
			case TYPE_ALPHA_S:
				return "a";
			case TYPE_ROMAN_B:
				return "I";
			case TYPE_ROMAN_S:
				return "i";
			case TYPE_NUM:
				return "1";
			default:
					return "";
		}
	}
}