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
 * @author Assi Nilsmussen
 *
 */
public class SecSmiley extends SecImage {

	/**
	 * the primary code of the smiley
	 */
	private final String _primCode;
	
	/**
	 * the secondary code of the smiley
	 */
	private final String _secCode;
	
	/**
	 * constructor
	 * 
	 * @param textField the textField-instance
	 * @param location the location of the smiley
	 * @param primCode the primary code of the smiley
	 */
	public SecSmiley(AbstractTextField textField,String location,String primCode) {
		this(textField,location,primCode,"");
	}
	
	/**
	 * constructor
	 * 
	 * @param textField the textField-instance
	 * @param location the location of the smiley
	 * @param primCode the primary code of the smiley
	 * @param secCode the secondary code of the smiley
	 */
	public SecSmiley(AbstractTextField textField,String location,String primCode,String secCode) {
		super(textField,location);
		
		_primCode = primCode;
		_secCode = secCode;
	}
	
	/**
	 * @return the primary code of the smiley
	 */
	public String getPrimaryCode() {
		return _primCode;
	}
	
	/**
	 * @return the secondary code of the smiley
	 */
	public String getSecondaryCode() {
		return _secCode;
	}
}