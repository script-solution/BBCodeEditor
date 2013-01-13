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

package bbcodeeditor.control.events;


/**
 * the event-data for a bbcode-parse-error
 * 
 * @author hrniels
 */
public class BBCodeParseError {

	/**
	 * the bbcode-text that should be parsed
	 */
	private final String _bbcode;
	
	/**
	 * the error-type
	 */
	private final int _error;
	
	/**
	 * the error-message
	 */
	private final String _errorMsg;
	
	/**
	 * constructor
	 * 
	 * @param bbcode the bbcode-text that should be parsed
	 * @param error the error-type
	 * @param errorMsg the error-message
	 */
	public BBCodeParseError(String bbcode,int error,String errorMsg) {
		_bbcode = bbcode;
		_error = error;
		_errorMsg = errorMsg;
	}
	
	/**
	 * @return the bbcode-text that should be parsed
	 */
	public String getBBCode() {
		return _bbcode;
	}
	
	/**
	 * @return the error-type
	 */
	public int getError() {
		return _error;
	}
	
	/**
	 * @return the error-message
	 */
	public String getErrorMsg() {
		return _errorMsg;
	}
}