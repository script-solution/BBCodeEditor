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


/**
 * a class that can convert integers to roman-numerals
 * 
 * @author hrniels
 */
public final class RomanNumerals {
	
	/**
	 * maps a number to the corresponding roman letter
	 */
	private static final NumberToRoman[] _roman = new NumberToRoman[] {
			new NumberToRoman(1,'I'),
			new NumberToRoman(5,'V'),
			new NumberToRoman(10,'X'),
			new NumberToRoman(50,'L'),
			new NumberToRoman(100,'C'),
			new NumberToRoman(500,'D'),
			new NumberToRoman(1000,'M')
	};

	private RomanNumerals() {
		// prevent instantiation
	}

	/**
	 * calculates the representation in roman-chars of the given number
	 * (in upper case!)
	 * 
	 * @param number the number to display
	 * @return the roman-representation
	 */
	public static String intToRoman(int number) {
		StringBuffer buf = new StringBuffer();
		int maxRoman = _roman.length - 1;
		while(number > 0) {
			for(int i = maxRoman;i >= 0;i--) {
				int rNum = _roman[i].number;
				if(number >= rNum) {
					boolean append = true;
					if(i < maxRoman) {
						NumberToRoman curr;
						if(i % 2 == 0)
							curr = _roman[i];
						else
							curr = i > 0 ? _roman[i - 1] : null;
						int prev = _roman[i + 1].number;
							
						if(curr != null && number >= (prev - curr.number)) {
							buf.append(curr.letter);
							buf.append(_roman[i + 1].letter);
							number -= prev - curr.number;
							append = false;
						}
					}
					
					if(append) {
						number -= rNum;
						buf.append(_roman[i].letter);
					}
					break;
				}
			}
		}
		
		buf.append('.');
		return buf.toString();
	}
	
	/**
	 * maps a number to the corresponding roman letter
	 * 
	 * @author hrniels
	 */
	private static final class NumberToRoman {
		
		/**
		 * the number which represents the roman letter
		 */
		public int number;
		
		/**
		 * the letter for the number
		 */
		public char letter;
		
		/**
		 * constructor
		 * 
		 * @param number the number which represents the roman letter
		 * @param letter the letter for the number
		 */
		public NumberToRoman(int number,char letter) {
			this.number = number;
			this.letter = letter;
		}
	}
}