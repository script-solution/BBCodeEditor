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

import java.awt.Image;
import java.util.EventListener;


/**
 * The interface to indicate that an image has finished loading
 * 
 * @author hrniels
 */
public interface ImageLoadingFinishedListener extends EventListener {

	/**
	 * Please call this method as soon as an image has finished loading
	 * 
	 * @param image the created image (null indicates an error)
	 */
	public void imageLoadingFinished(Image image);
}