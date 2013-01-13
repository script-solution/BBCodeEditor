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

package bbcodeeditor.control.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * some general and basic functions
 * 
 * @author Assi Nilsmussen
 */
public class StringUtils {
	
	/**
	 * All html-entities
	 */
	private static final Map entities = new HashMap();
	
	/**
	 * Init the html-entities
	 */
	static {
		entities.put("quot",new Integer(34));
		entities.put("amp",new Integer(38));
		entities.put("lt",new Integer(60));
		entities.put("gt",new Integer(62));
		entities.put("nbsp",new Integer(160));
		entities.put("iexcl",new Integer(161));
		entities.put("cent",new Integer(162));
		entities.put("pound",new Integer(163));
		entities.put("curren",new Integer(164));
		entities.put("yen",new Integer(165));
		entities.put("brvbar",new Integer(166));
		entities.put("sect",new Integer(167));
		entities.put("uml",new Integer(168));
		entities.put("copy",new Integer(169));
		entities.put("ordf",new Integer(170));
		entities.put("laquo",new Integer(171));
		entities.put("not",new Integer(172));
		entities.put("shy",new Integer(173));
		entities.put("reg",new Integer(174));
		entities.put("macr",new Integer(175));
		entities.put("deg",new Integer(176));
		entities.put("plusmn",new Integer(177));
		entities.put("sup2",new Integer(178));
		entities.put("sup3",new Integer(179));
		entities.put("acute",new Integer(180));
		entities.put("micro",new Integer(181));
		entities.put("para",new Integer(182));
		entities.put("middot",new Integer(183));
		entities.put("cedil",new Integer(184));
		entities.put("sup1",new Integer(185));
		entities.put("ordm",new Integer(186));
		entities.put("raquo",new Integer(187));
		entities.put("frac14",new Integer(188));
		entities.put("frac12",new Integer(189));
		entities.put("frac34",new Integer(190));
		entities.put("iquest",new Integer(191));
		entities.put("Agrave",new Integer(192));
		entities.put("Aacute",new Integer(193));
		entities.put("Acirc",new Integer(194));
		entities.put("Atilde",new Integer(195));
		entities.put("Auml",new Integer(196));
		entities.put("Aring",new Integer(197));
		entities.put("AElig",new Integer(198));
		entities.put("Ccedil",new Integer(199));
		entities.put("Egrave",new Integer(200));
		entities.put("Eacute",new Integer(201));
		entities.put("Ecirc",new Integer(202));
		entities.put("Euml",new Integer(203));
		entities.put("Igrave",new Integer(204));
		entities.put("Iacute",new Integer(205));
		entities.put("Icirc",new Integer(206));
		entities.put("Iuml",new Integer(207));
		entities.put("ETH",new Integer(208));
		entities.put("Ntilde",new Integer(209));
		entities.put("Ograve",new Integer(210));
		entities.put("Oacute",new Integer(211));
		entities.put("Ocirc",new Integer(212));
		entities.put("Otilde",new Integer(213));
		entities.put("Ouml",new Integer(214));
		entities.put("times",new Integer(215));
		entities.put("Oslash",new Integer(216));
		entities.put("Ugrave",new Integer(217));
		entities.put("Uacute",new Integer(218));
		entities.put("Ucirc",new Integer(219));
		entities.put("Uuml",new Integer(220));
		entities.put("Yacute",new Integer(221));
		entities.put("THORN",new Integer(222));
		entities.put("szlig",new Integer(223));
		entities.put("agrave",new Integer(224));
		entities.put("aacute",new Integer(225));
		entities.put("acirc",new Integer(226));
		entities.put("atilde",new Integer(227));
		entities.put("auml",new Integer(228));
		entities.put("aring",new Integer(229));
		entities.put("aelig",new Integer(230));
		entities.put("ccedil",new Integer(231));
		entities.put("egrave",new Integer(232));
		entities.put("eacute",new Integer(233));
		entities.put("ecirc",new Integer(234));
		entities.put("euml",new Integer(235));
		entities.put("igrave",new Integer(236));
		entities.put("iacute",new Integer(237));
		entities.put("icirc",new Integer(238));
		entities.put("iuml",new Integer(239));
		entities.put("eth",new Integer(240));
		entities.put("ntilde",new Integer(241));
		entities.put("ograve",new Integer(242));
		entities.put("oacute",new Integer(243));
		entities.put("ocirc",new Integer(244));
		entities.put("otilde",new Integer(245));
		entities.put("ouml",new Integer(246));
		entities.put("divide",new Integer(247));
		entities.put("oslash",new Integer(248));
		entities.put("ugrave",new Integer(249));
		entities.put("uacute",new Integer(250));
		entities.put("ucirc",new Integer(251));
		entities.put("uuml",new Integer(252));
		entities.put("yacute",new Integer(253));
		entities.put("thorn",new Integer(254));
		entities.put("yuml",new Integer(255));
		entities.put("Alpha",new Integer(913));
		entities.put("alpha",new Integer(945));
		entities.put("Beta",new Integer(914));
		entities.put("beta",new Integer(946));
		entities.put("Gamma",new Integer(915));
		entities.put("gamma",new Integer(947));
		entities.put("Delta",new Integer(916));
		entities.put("delta",new Integer(948));
		entities.put("Epsilon",new Integer(917));
		entities.put("epsilon",new Integer(949));
		entities.put("Zeta",new Integer(918));
		entities.put("zeta",new Integer(950));
		entities.put("Eta",new Integer(919));
		entities.put("eta",new Integer(951));
		entities.put("Theta",new Integer(920));
		entities.put("theta",new Integer(952));
		entities.put("Iota",new Integer(921));
		entities.put("iota",new Integer(953));
		entities.put("Kappa",new Integer(922));
		entities.put("kappa",new Integer(954));
		entities.put("Lambda",new Integer(923));
		entities.put("lambda",new Integer(955));
		entities.put("Mu",new Integer(924));
		entities.put("mu",new Integer(956));
		entities.put("Nu",new Integer(925));
		entities.put("nu",new Integer(957));
		entities.put("Xi",new Integer(926));
		entities.put("xi",new Integer(958));
		entities.put("Omicron",new Integer(927));
		entities.put("omicron",new Integer(959));
		entities.put("Pi",new Integer(928));
		entities.put("pi",new Integer(960));
		entities.put("Rho",new Integer(929));
		entities.put("rho",new Integer(961));
		entities.put("Sigma",new Integer(931));
		entities.put("sigmaf",new Integer(962));
		entities.put("sigma",new Integer(963));
		entities.put("Tau",new Integer(932));
		entities.put("tau",new Integer(964));
		entities.put("Upsilon",new Integer(933));
		entities.put("upsilon",new Integer(965));
		entities.put("Phi",new Integer(934));
		entities.put("phi",new Integer(966));
		entities.put("Chi",new Integer(935));
		entities.put("chi",new Integer(967));
		entities.put("Psi",new Integer(936));
		entities.put("psi",new Integer(968));
		entities.put("Omega",new Integer(937));
		entities.put("omega",new Integer(969));
		entities.put("thetasym",new Integer(977));
		entities.put("upsih",new Integer(978));
		entities.put("piv",new Integer(982));
		entities.put("forall",new Integer(8704));
		entities.put("part",new Integer(8706));
		entities.put("exist",new Integer(8707));
		entities.put("empty",new Integer(8709));
		entities.put("nabla",new Integer(8711));
		entities.put("isin",new Integer(8712));
		entities.put("notin",new Integer(8713));
		entities.put("ni",new Integer(8715));
		entities.put("prod",new Integer(8719));
		entities.put("sum",new Integer(8721));
		entities.put("minus",new Integer(8722));
		entities.put("lowast",new Integer(8727));
		entities.put("radic",new Integer(8730));
		entities.put("prop",new Integer(8733));
		entities.put("infin",new Integer(8734));
		entities.put("ang",new Integer(8736));
		entities.put("and",new Integer(8869));
		entities.put("or",new Integer(8870));
		entities.put("cap",new Integer(8745));
		entities.put("cup",new Integer(8746));
		entities.put("int",new Integer(8747));
		entities.put("there4",new Integer(8756));
		entities.put("sim",new Integer(8764));
		entities.put("cong",new Integer(8773));
		entities.put("asymp",new Integer(8776));
		entities.put("ne",new Integer(8800));
		entities.put("equiv",new Integer(8801));
		entities.put("le",new Integer(8804));
		entities.put("ge",new Integer(8805));
		entities.put("sub",new Integer(8834));
		entities.put("sup",new Integer(8835));
		entities.put("nsub",new Integer(8836));
		entities.put("sube",new Integer(8838));
		entities.put("supe",new Integer(8839));
		entities.put("oplus",new Integer(8853));
		entities.put("otimes",new Integer(8855));
		entities.put("perp",new Integer(8869));
		entities.put("sdot",new Integer(8901));
		entities.put("loz",new Integer(9674));
		entities.put("lceil",new Integer(8968));
		entities.put("rceil",new Integer(8969));
		entities.put("lfloor",new Integer(8970));
		entities.put("rfloor",new Integer(8971));
		entities.put("lang",new Integer(9001));
		entities.put("rang",new Integer(9002));
		entities.put("larr",new Integer(8592));
		entities.put("uarr",new Integer(8593));
		entities.put("rarr",new Integer(8594));
		entities.put("darr",new Integer(8595));
		entities.put("harr",new Integer(8596));
		entities.put("crarr",new Integer(8629));
		entities.put("lArr",new Integer(8656));
		entities.put("uArr",new Integer(8657));
		entities.put("rArr",new Integer(8658));
		entities.put("dArr",new Integer(8659));
		entities.put("hArr",new Integer(8660));
		entities.put("bull",new Integer(8226));
		entities.put("hellip",new Integer(8230));
		entities.put("prime",new Integer(8242));
		entities.put("oline",new Integer(8254));
		entities.put("frasl",new Integer(8260));
		entities.put("weierp",new Integer(8472));
		entities.put("image",new Integer(8465));
		entities.put("real",new Integer(8476));
		entities.put("trade",new Integer(8482));
		entities.put("euro",new Integer(8364));
		entities.put("alefsym",new Integer(8501));
		entities.put("spades",new Integer(9824));
		entities.put("clubs",new Integer(9827));
		entities.put("hearts",new Integer(9829));
		entities.put("diams",new Integer(9830));
	}
	
	private StringUtils() {
		// prevent instantiation
	}
	
	/**
	 * Checks wether the character at the given position is escaped
	 * 
	 * @param str the string
	 * @param pos the position
	 * @param escChar the escape-character
	 * @return true if the char is escaped
	 */
	public static boolean isEscaped(String str,int pos,char escChar) {
		int c = 0;
		for(int i = pos - 1;i >= 0;i--) {
			if(str.charAt(i) != escChar)
				break;
			c++;
		}
		return c % 2 == 1;
	}
	
	/**
	 * replaces all whitespace-occurrences with the given string<br>
	 * Multiple whitespace characters in a row will be replaced with just one
	 * <code>replace</code>!<br>
	 * This is used for String.replaceAll("[\r|\n|\t| ]+"," "), because the
	 * replaceAll method is extremly slow due to the regular expression.
	 * 
	 * @param input the input-string
	 * @param replace the string which should replace the whitespaces
	 * @return the result-string
	 */
	public static String replaceWhiteSpace(String input,String replace) {
		StringBuffer buf = new StringBuffer();
		boolean lastWasWhiteSpace = false;
		
		for(int i = 0,len = input.length();i < len;i++) {
			char c = input.charAt(i);
			if(Character.isWhitespace(c)) {
				lastWasWhiteSpace = true;
				continue;
			}
			else if(lastWasWhiteSpace) {
				buf.append(replace);
				lastWasWhiteSpace = false;
			}

			buf.append(c);
		}
		
		// append the last one, if necessary
		if(lastWasWhiteSpace)
			buf.append(replace);
		
		return buf.toString();
	}
	
	/**
	 * Splits the string <code>input</code> by <code>sep</code>.<br>
	 * This version does not use regular-expressions. Therefore it may be much faster than
	 * java.lang.String.split(String) depending mainly on <code>sep</code>!
	 * 
	 * @param input the input-string to split
	 * @param sep the separator. at which character do you want to split?
	 * @return an array with all parts
	 */
	public static String[] simpleSplit(String input,String sep) {
		List l = new ArrayList();

		int sepLen = sep.length();
		int lastIndex = 0;
		int index = input.indexOf(sep);
		while(index >= 0) {
			l.add(input.substring(lastIndex,index));
			
			lastIndex = index + sepLen;
			index = input.indexOf(sep,lastIndex);
		}
		
		if(lastIndex < input.length())
			l.add(input.substring(lastIndex));
		
		return (String[])l.toArray(new String[0]);
	}
	
	/**
	 * Replaces all occurrences of <code>search</code> in <code>input</code> with
	 * <code>replace</code>. Will not use regular-expressions. Therefore this method may be
	 * much faster than java.lang.String.replaceAll(String,String) depending mainly
	 * on <code>sep</code>!
	 * 
	 * @param input the input-string
	 * @param search the string to search for and replace
	 * @param replace the string with which to replace
	 * @return the result-string
	 */
	public static String simpleReplace(String input,String search,String replace) {
		StringBuffer buf = new StringBuffer();
		int slen = search.length();
		int lastIndex = 0;
		int index = input.indexOf(search);
		while(index >= 0) {
			buf.append(input.substring(lastIndex,index));
			buf.append(replace);
			
			lastIndex = index + slen;
			index = input.indexOf(search,lastIndex);
		}
		
		if(lastIndex < input.length())
			buf.append(input.substring(lastIndex));
		
		return buf.toString();
	}
	
	/**
	 * trims the start of the given string
	 * 
	 * @param input the string to trim
	 * @return the result-string
	 */
	public static String trimStart(String input) {
		int start = 0;
		int len = input.length();
		while(start < len && input.charAt(start) <= ' ')
			start++;
		if(start > 0)
			return input.substring(start);
		
		return input;
	}
	
	/**
	 * trims the end of the given string
	 * 
	 * @param input the string to trim
	 * @return the result-string
	 */
	public static String trimEnd(String input) {
		int len = input.length();
		while(len > 0 && input.charAt(len - 1) <= ' ')
			len--;
		if(len < input.length())
			return input.substring(0,len);
		
		return input;
	}
	
	/**
	 * checks wether the given string contains just whitespace<br>
	 * Note that empty strings will be treaten as whitespace, too!
	 * 
	 * @param input the string to check
	 * @return true if the string contains just whitespace
	 */
	public static boolean isWhiteSpace(String input) {
		for(int i = 0,len = input.length();i < len;i++) {
			if(!Character.isWhitespace(input.charAt(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * repeats the given character <code>count</code> times.
	 * 
	 * @param c the character
	 * @param count the number of repeats
	 * @return the created string
	 */
	public static String repeat(char c,int count) {
		return repeat(String.valueOf(c),count);
	}

	/**
	 * repeats the given string <code>count</code> times.
	 * 
	 * @param s the string
	 * @param count the number of repeats
	 * @return the created string
	 */
	public static String repeat(String s,int count) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0;i < count;i++)
			buf.append(s);
		return buf.toString();
	}
	
	/**
	 * Converts the given HTML-string to a string. Therefore this is the undo-operation for
	 * {@link #stringToHTMLString(String)}.
	 * 
	 * @param html the HTML-code
	 * @return the string
	 */
	public static String htmlStringToString(String html) {
		StringBuffer sb = new StringBuffer(html.length());

		// true if last char was blank
		int len = html.length();
		char c;

		for(int i = 0;i < len;i++) {
			c = html.charAt(i);
			
			if(c == '&') {
				// search for the ";"
				int endPos = html.indexOf(';',i);
				if(endPos == -1)
					continue;
				
				String inner = html.substring(i + 1,endPos);
				
				// do we know this entity?
				Integer code = (Integer)entities.get(inner);
				if(code != null)
					sb.append(Character.toChars(code.intValue()));
				// is it decimal / hexadecimal
				else if(inner.startsWith("#")) {
					int no;
					// hex?
					if(inner.toLowerCase().startsWith("#x")) {
						String number = inner.substring(2);
						no = Integer.parseInt(number,16);
					}
					else {
						String number = inner.substring(1);
						no = Integer.parseInt(number);
					}
					
					sb.append(Character.toChars(no));
				}
				
				// continue at the character after ;
				i = endPos;
			}
			else
				sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * Converts the given string to an HTML-string. Will replace the special
	 * chars and so on.
	 * 
	 * @param string the input-string
	 * @return the result
	 */
	public static String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());

		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for(int i = 0;i < len;i++) {
			c = string.charAt(i);
			if(c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss 
				// word breaking
				if(lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				}
				else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			}
			else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if(c == '"')
					sb.append("&quot;");
				else if(c == '&')
					sb.append("&amp;");
				else if(c == '<')
					sb.append("&lt;");
				else if(c == '>')
					sb.append("&gt;");
				else if(c == '\n')
					// Handle Newline
					sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if(ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}
}