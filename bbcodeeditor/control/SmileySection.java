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
 * This section contains Smileys. This class is required (although there is no difference
 * to an ImageSection at the moment) because we need to know if it is a smiley or an image
 * if we export the content of the control.
 * <p>
 * TODO perhaps we can define the export-content by the SecImage / SecSmiley class. So that
 * we don't need this class anymore?
 * 
 * @author hrniels
 */
public final class SmileySection extends ImageSection {
	
	/**
	 * Constructor
	 * 
	 * @param env the environment which contains this section
	 * @param smiley the smiley of this section
	 * @param start the "global" start-position
	 * @param line the line of this section
	 * @param para the Paragraph of this section
	 */
	SmileySection(Environment env,SecSmiley smiley,int start,Line line,
			Paragraph para) {
		super(env,smiley,start,line,para);
	}
}