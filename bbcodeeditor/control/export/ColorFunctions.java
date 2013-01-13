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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


/**
 * some usefull methods for colors
 * 
 * @author hrniels
 */
public class ColorFunctions {
	
	private ColorFunctions() {
		// prevent instantiation
	}
	
	/**
	 * a map with string-color-names mapped to the color in hexadecimal format
	 */
	private static final Map stringColors;
	
	static {
		stringColors = new HashMap();
		stringColors.put("aliceblue","#F0F8FF");
		stringColors.put("antiquewhite","#FAEBD7");
		stringColors.put("aqua","#00FFFF");
		stringColors.put("aquamarine","#7FFFD4");
		stringColors.put("azure","#F0FFFF");
		stringColors.put("beige","#F5F5DC");
		stringColors.put("black","#000000");
		stringColors.put("blue","#0000FF");
		stringColors.put("blueviolet","#8A2BE2");
		stringColors.put("brown","#A52A2A");
		stringColors.put("burlywood","#DEB887");
		stringColors.put("cadetblue","#5F9EA0");
		stringColors.put("chartreuse","#7FFF00");
		stringColors.put("chocolate","#D2691E");
		stringColors.put("coral","#FF7F50");
		stringColors.put("cornflowerblue","#6495ED");
		stringColors.put("cornsilk","#FFF8DC");
		stringColors.put("crimson","#DC143C");
		stringColors.put("darkblue","#00008B");
		stringColors.put("darkcyan","#008B8B");
		stringColors.put("darkgoldenrod","#B8860B");
		stringColors.put("darkgray","#A9A9A9");
		stringColors.put("darkgreen","#006400");
		stringColors.put("darkkhaki","#BDB76B");
		stringColors.put("darkmagenta","#8B008B");
		stringColors.put("darkolivegreen","#556B2F");
		stringColors.put("darkorange","#FF8C00");
		stringColors.put("darkorchid","#9932CC");
		stringColors.put("darkred","#8B0000");
		stringColors.put("darksalmon","#E9967A");
		stringColors.put("darkseagreen","#8FBC8F");
		stringColors.put("darkslateblue","#483D8B");
		stringColors.put("darkslategray","#2F4F4F");
		stringColors.put("darkturquoise","#00CED1");
		stringColors.put("darkviolet","#9400D3");
		stringColors.put("deeppink","#FF1493");
		stringColors.put("deepskyblue","#00BFFF");
		stringColors.put("dimgray","#696969");
		stringColors.put("dodgerblue","#1E90FF");
		stringColors.put("firebrick","#B22222");
		stringColors.put("floralwhite","#FFFAF0");
		stringColors.put("forestgreen","#228B22");
		stringColors.put("fuchsia","#FF00FF");
		stringColors.put("gainsboro","#DCDCDC");
		stringColors.put("ghostwhite","#F8F8FF");
		stringColors.put("gold","#FFD700");
		stringColors.put("goldenrod","#DAA520");
		stringColors.put("gray","#808080");
		stringColors.put("green","#008000");
		stringColors.put("greenyellow","#ADFF2F");
		stringColors.put("honeydew","#F0FFF0");
		stringColors.put("hotpink","#FF69B4");
		stringColors.put("indianred","#CD5C5C");
		stringColors.put("indigo","#4B0082");
		stringColors.put("ivory","#FFFFF0");
		stringColors.put("khaki","#F0E68C");
		stringColors.put("lavender","#E6E6FA");
		stringColors.put("lavenderblush","#FFF0F5");
		stringColors.put("lawngreen","#7CFC00");
		stringColors.put("lemonchiffon","#FFFACD");
		stringColors.put("lightblue","#ADD8E6");
		stringColors.put("lightcoral","#F08080");
		stringColors.put("lightcyan","#E0FFFF");
		stringColors.put("lightgoldenrodyellow","#FAFAD2");
		stringColors.put("lightgreen","#90EE90");
		stringColors.put("lightgrey","#D3D3D3");
		stringColors.put("lightpink","#FFB6C1");
		stringColors.put("lightsalmon","#FFA07A");
		stringColors.put("lightseagreen","#20B2AA");
		stringColors.put("lightskyblue","#87CEFA");
		stringColors.put("lightslategray","#778899");
		stringColors.put("lightsteelblue","#B0C4DE");
		stringColors.put("lightyellow","#FFFFE0");
		stringColors.put("lime","#00FF00");
		stringColors.put("limegreen","#32CD32");
		stringColors.put("linen","#FAF0E6");
		stringColors.put("maroon","#800000");
		stringColors.put("mediumaquamarine","#66CDAA");
		stringColors.put("mediumblue","#0000CD");
		stringColors.put("mediumorchid","#BA55D3");
		stringColors.put("mediumpurple","#9370DB");
		stringColors.put("mediumseagreen","#3CB371");
		stringColors.put("mediumslateblue","#7B68EE");
		stringColors.put("mediumspringgreen","#00FA9A");
		stringColors.put("mediumturquoise","#48D1CC");
		stringColors.put("mediumvioletred","#C71585");
		stringColors.put("midnightblue","#191970");
		stringColors.put("mintcream","#F5FFFA");
		stringColors.put("mistyrose","#FFE4E1");
		stringColors.put("moccasin","#FFE4B5");
		stringColors.put("navajowhite","#FFDEAD");
		stringColors.put("navy","#000080");
		stringColors.put("oldlace","#FDF5E6");
		stringColors.put("olive","#808000");
		stringColors.put("olivedrab","#6B8E23");
		stringColors.put("orange","#FFA500");
		stringColors.put("orangered","#FF4500");
		stringColors.put("orchid","#DA70D6");
		stringColors.put("palegoldenrod","#EEE8AA");
		stringColors.put("palegreen","#98FB98");
		stringColors.put("paleturquoise","#AFEEEE");
		stringColors.put("palevioletred","#DB7093");
		stringColors.put("papayawhip","#FFEFD5");
		stringColors.put("peachpuff","#FFDAB9");
		stringColors.put("peru","#CD853F");
		stringColors.put("pink","#FFC0CB");
		stringColors.put("plum","#DDA0DD");
		stringColors.put("powderblue","#B0E0E6");
		stringColors.put("purple","#800080");
		stringColors.put("red","#FF0000");
		stringColors.put("rosybrown","#BC8F8F");
		stringColors.put("royalblue","#4169E1");
		stringColors.put("saddlebrown","#8B4513");
		stringColors.put("salmon","#FA8072");
		stringColors.put("sandybrown","#F4A460");
		stringColors.put("seagreen","#2E8B57");
		stringColors.put("seashell","#FFF5EE");
		stringColors.put("sienna","#A0522D");
		stringColors.put("silver","#C0C0C0");
		stringColors.put("skyblue","#87CEEB");
		stringColors.put("slateblue","#6A5ACD");
		stringColors.put("slategray","#708090");
		stringColors.put("snow","#FFFAFA");
		stringColors.put("springgreen","#00FF7F");
		stringColors.put("steelblue","#4682B4");
		stringColors.put("tan","#D2B48C");
		stringColors.put("teal","#008080");
		stringColors.put("thistle","#D8BFD8");
		stringColors.put("tomato","#FF6347");
		stringColors.put("turquoise","#40E0D0");
		stringColors.put("violet","#EE82EE");
		stringColors.put("wheat","#F5DEB3");
		stringColors.put("white","#FFFFFF");
		stringColors.put("whitesmoke","#F5F5F5");
		stringColors.put("yellow","#FFFF00");
		stringColors.put("yellowgreen","#9ACD32");
	}
	
	/**
	 * @param input the input color
	 * @return the hex-value of the color in a string
	 */
	public static String getStringFromColor(Color input) {
		String red = Integer.toHexString(input.getRed());
		if(red.length() == 1)
			red += "0";
		
		String green = Integer.toHexString(input.getGreen());
		if(green.length() == 1)
			green += "0";
		
		String blue = Integer.toHexString(input.getBlue());
		if(blue.length() == 1)
			blue += "0";
		
		return ("#" + red + green + blue).toUpperCase();
	}
	
	/**
	 * determines the color in the given string
	 * 
	 * @param input the input-string in hex-format or the name of the color
	 * @param defaultColor the default-color which will be used if it is not possible to
	 * parse the string
	 * @return the corresponding Color-object
	 */
	public static Color getColorFromString(String input,Color defaultColor) {
		// parse the string for a hex-color
		int len = input.length();
		if(len == 0)
			return defaultColor;

		boolean isHex = input.charAt(0) == '#';

		// return if the string is too short
		if(isHex && input.length() < 4)
			return defaultColor;

		boolean isShortHex = len == 4;
		StringBuffer color = new StringBuffer();
		input = input.toLowerCase();
		if(isHex) {
			for(int i = 1;i < len;i++) {
				char c = input.charAt(i);

				if((c >= 'a' && c <= 'f') || (c >= '0' && c <= '9')) {
					// add twice if it is a short hex-code
					if(isShortHex)
						color.append(c);
					color.append(c);
				}
				// is can't be a hex-code if it is another char than a-f0-9
				else {
					isHex = false;
					break;
				}
			}
		}

		// break if the length if invalid
		if(isHex && color.length() != 6)
			return defaultColor;

		String fColor;
		// retrieve the color from the list if it is no hex-code
		if(!isHex) {
			fColor = (String)stringColors.get(input);
			if(fColor == null)
				return defaultColor;

			if(fColor.charAt(0) == '#')
				fColor = fColor.substring(1);
		}
		else
			fColor = color.toString();

		// calculate the color-parts
		int red = ColorFunctions.hexStringToInt(fColor.substring(0,2));
		int green = ColorFunctions.hexStringToInt(fColor.substring(2,4));
		int blue = ColorFunctions.hexStringToInt(fColor.substring(4,6));
		return new Color(red,green,blue);
	}
	
	/**
	 * NOTE: the input-string will not be verified if it is a valid hex-code!
	 * 
	 * @param hex the input-hex-string
	 * @return the integer-value
	 */
	public static int hexStringToInt(String hex) {
		hex = hex.toLowerCase();
		int a = 1;
		int result = 0;
		for(int i = hex.length() - 1;i >= 0;i--) {
			int val;
			char current = hex.charAt(i);
			switch(current) {
				case 'f':
					val = 15;
					break;
				case 'e':
					val = 14;
					break;
				case 'd':
					val = 13;
					break;
				case 'c':
					val = 12;
					break;
				case 'b':
					val = 11;
					break;
				case 'a':
					val = 10;
					break;
				default:
					val = current - 48;
					break;
			}
			
			result += val * a;
			a *= 16;
		}
		
		return result;
	}
}