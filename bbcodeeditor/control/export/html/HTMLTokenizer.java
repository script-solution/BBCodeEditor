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

package bbcodeeditor.control.export.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * A tokenizer for HTML-Code
 * 
 * @author hrniels
 */
public final class HTMLTokenizer {
	
	/**
	 * the input-string
	 */
	private final String _input;
	
	private final List _splitChars = Arrays.asList(new Character[] {
			new Character('<'),new Character('>'),new Character('='),
			new Character('"'),new Character('\''),new Character('/')
	});

	/**
	 * the list with tokens (will be created)
	 */
	private List _result;
	
	/**
	 * constructor
	 * 
	 * @param input the input-string to parse
	 */
	public HTMLTokenizer(String input) {
		_input = input.trim();
		tokenize();
	}
	
	/**
	 * @return a List with the tokens
	 */
	public List getTokens() {
		return _result;
	}
	
	/**
	 * walks through the input-string and tokenizes it
	 */
	private void tokenize() {
		_result = new ArrayList();
		
		char last = 0;
		StringBuffer buffer = new StringBuffer();
		int len = _input.length();
		mainLoop:
		for(int i = 0;i < len;i++) {
			char c = _input.charAt(i);
			
			if(split(c,last,true)) {
				if(buffer.length() > 0) {
					_result.add(buffer.toString());
					buffer = new StringBuffer();
				}
				
				switch(c) {
					case '"':
					case '\'':
						buffer.append(c);
						if(i < len - 1) {
							char nc;
							do {
								nc = _input.charAt(++i);
								// we have to take care of other chars like < or >
								if(split(nc,nc,false) && nc != c) {
									i--;
									continue mainLoop;
								}
								buffer.append(nc);
							}
							while(nc != c && !split(nc,nc,false) && i < len - 1);
						}
						
						if(buffer.length() > 0) {
							_result.add(buffer.toString());
							buffer = new StringBuffer();
						}
						break;
					
					case '>':
						if(buffer.length() > 0) {
							_result.add(buffer.toString());
							buffer = new StringBuffer();
						}
						_result.add(String.valueOf(c));
						break;
					
					case '<':
						buffer.append(c);
						i++;
						while(i < len) {
							c = _input.charAt(i);
							if(Character.isWhitespace(c) || c == '>') {
								i--;
								break;
							}
							
							buffer.append(c);
							i++;
						}
						
						_result.add(buffer.toString());
						buffer = new StringBuffer();
						break;
					
					default:
						_result.add(String.valueOf(c));
						break;
				}
			}
			else
				buffer.append(c);
			
			last = c;
		}
		
		if(buffer.length() > 0)
			_result.add(buffer.toString());
	}
	
	private boolean split(char c,char last,boolean splitWhitespace) {
		if(splitWhitespace && Character.isWhitespace(c) && !Character.isWhitespace(last))
			return true;
		
		return _splitChars.contains(new Character(c));
	}
}