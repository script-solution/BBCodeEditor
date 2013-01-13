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
 * indicates the an invalid text position has been requested
 * 
 * @author hrniels
 */
final class InvalidTextPositionException extends Exception {

	private static final long serialVersionUID = -3262676730746413063L;
	
	/**
	 * the requested position
	 */
	private int _position;
	
	/**
	 * constructor
	 * 
	 * @param position the requested position
	 */
	public InvalidTextPositionException(int position) {
		this("Invalid position: " + position,position);
	}
	
	/**
	 * constructor
	 * 
	 * @param msg the exception-message
	 * @param position the requested position
	 */
	public InvalidTextPositionException(String msg,int position) {
		super(msg);
		
		_position = position;
	}
	
	/**
	 * @return the requested position
	 */
	public int getPosition() {
		return _position;
	}
}