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

import java.net.URL;
import java.util.EventListener;


/**
 * The listener for an image-loading-request
 * 
 * @author hrniels
 */
public interface ImageLoadingRequestListener extends EventListener {

	/**
	 * this method will be invoked as soon as an image wants to get loaded
	 * 
	 * @param location the location to the image
	 * @param notifier the object you should notify if the image-loading-process is
	 * 								 finished
	 */
	public void imageLoadingRequest(URL location,ImageLoadingFinishedListener notifier);
}