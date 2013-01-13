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

package bbcodeeditor.control.export.bbcode;

import java.util.ArrayList;
import java.util.List;

import bbcodeeditor.control.Controller;
import bbcodeeditor.control.SmileyContainer;



/**
 * this class tokenizes the string and saves the tokens in a List
 * 
 * @author hrniels
 */
public final class BBCodeTokenizer {
	
	/**
	 * the controller of the textField
	 */
	private final Controller _con;
	
	/**
	 * the input-string
	 */
	private String _input;

	/**
	 * the list with tokens (will be created)
	 */
	private List _result;
	
	/**
	 * a temporary buffer
	 */
	private StringBuffer _buffer;
	
	/**
	 * constructor
	 * 
	 * @param con the Controller
	 * @param input the input-string to parse
	 */
	public BBCodeTokenizer(Controller con,String input) {
		_con = con;
		_input = input;
		
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
		
		SmileyContainer smileys = _con.getSmileys();
		int maxSmileyLen = smileys.getMaxSmileyLength();
		_buffer = new StringBuffer();
		int len = _input.length();
		
		mainLoop:
		for(int i = 0;i < len;i++) {
			char c = _input.charAt(i);
			
			// search for smileys
			String sub = _input.substring(i,Math.min(len,i + maxSmileyLen));
			String smiley = smileys.getSmileyAtBeginning(sub);
			if(smiley != null) {
				// we require a space, a (potential) BBCode-tag or nothing after the smiley
				/*int send = i + smiley.length();
				if(send >= len || Character.isWhitespace(_input.charAt(send)) ||
						_input.charAt(send) == '[') {*/
					addToResult(false);
					
					_result.add(smiley);
					
					i += smiley.length() - 1;
					continue;
				//}
			}
			
			if(c == '[') {
				addToResult(false);
				
				// add the starting-tag
				_result.add("[");
				
				// look if it is a closing tag
				i++;
				if(i >= len)
					break;
				
				if(_input.charAt(i) == '/') {
					_result.add("/");
					i++;
				}
				
				// walk to the end of the bbcode-tag
				boolean inTagName = true;
				for(;i < len;i++) {
					char t = _input.charAt(i);
					if(inTagName && t == '=') {
						addToResult(false);
						_result.add("=");
						inTagName = false;
						continue;
					}
					
					// break here if we've found a "[" because this is no valid bbcode-tag
					// and now may follow a valid tag
					if(t == '[') {
						addToResult(false);
						i--;
						continue mainLoop;
					}
					
					if(t == ']')
						break;
					_buffer.append(t);
				}
				
				// add the tag-content to the result
				addToResult(false);
				
				// add the ending-tag
				_result.add("]");
			}
			else
				_buffer.append(c);
		}
		
		addToResult(false);
	}
	
	/**
	 * adds the buffer-content to the result if not empty
	 * 
	 * @param trim set this to true if you want to trim the text
	 */
	private void addToResult(boolean trim) {
		if(_buffer.length() > 0) {
			String text = _buffer.toString();
			if(trim)
				text = text.trim();
			_result.add(text);
			_buffer = new StringBuffer();
		}
	}
}