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
 * contains all content-types that are supported
 * that means environments and attributes
 * 
 * @author hrniels
 */
public final class EnvironmentTypes {

	/**
	 * represents the root-environment
	 */
	public static final int ENV_ROOT							= -1;
	
	/**
	 * represents a quote-environment
	 */
	public static final int ENV_QUOTE							= 0;
	
	/**
	 * represents a code-environment
	 */
	public static final int ENV_CODE							= 1;
	
	/**
	 * represents a list-environment
	 */
	public static final int ENV_LIST							= 2;
	
	/**
	 * determines wether the given type is valid
	 * 
	 * @param type the type to check
	 * @return true if it is a valid type
	 */
	public static boolean isValidEnvType(int type) {
		return type >= ENV_ROOT && type <= ENV_LIST;
	}
}