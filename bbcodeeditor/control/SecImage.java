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

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author Assi Nilsmussen
 *
 */
public class SecImage {
	
	/**
	 * The URL of the image
	 */
	private final URL _location;
	
	/**
	 * The path of the image
	 */
	private final String _strLoc;

	/**
	 * the image-object
	 */
	private Image _image = null;
	
	/**
	 * the dummy-image.<br>
	 * This one will be used if the other image could not been found!
	 */
	private Image _dummyImage;
	
	/**
	 * constructor
	 * 
	 * @param textField the textField-instance
	 * @param location the location of the image
	 */
	public SecImage(AbstractTextField textField,String location) {
		_strLoc = location;
		
		URL loc = null;
		try {
			loc = new URL(location);
		}
		catch(MalformedURLException e) {
			
		}
		_location = loc;

		// use dummy image if the image could not been found
		URL url = null;
		try {
			url = new URL(textField.getBaseURL(),"images/dummy.gif");
			_dummyImage = loadImage(textField,url);
		}
		catch(MalformedURLException e1) {
			
		}
	}
	
	/**
	 * loads the image of this object
	 * 
	 * @param textField the textField-instance
	 * @param location the location to use
	 * @return the image if successfull, null otherwise
	 */
	private Image loadImage(AbstractTextField textField,URL location) {
		Image img = null;
		MediaTracker mt = new MediaTracker(textField);
		try {
			img = Toolkit.getDefaultToolkit().getImage(location);
			
			mt.addImage(img,0);
			mt.waitForAll();
		}
		catch(InterruptedException e) {
			
		}
		
		return mt.isErrorID(0) ? null : img;
	}
	
	/**
	 * @return the width of this image
	 */
	public int getWidth() {
		Image img = getImage();
		return img != null ? img.getWidth(null) : 0;
	}
	
	/**
	 * @return the height of this image
	 */
	public int getHeight() {
		Image img = getImage();
		return img != null ? img.getHeight(null) : 0;
	}
	
	/**
	 * sets the image to the given one
	 * 
	 * @param img the new image
	 */
	void setImage(Image img) {
		_image = img;
	}
	
	/**
	 * @return the image-image
	 */
	public Image getImage() {
		return _image != null ? _image : _dummyImage;
	}
	
	/**
	 * This method will always return the "entered" URL of the image.
	 * getImageURL() may return null if the URL was invalid.
	 * 
	 * @return the image-path as string (not URL)
	 */
	public String getImagePath() {
		return _strLoc;
	}
	
	/**
	 * @return the location of the image
	 */
	public URL getImageURL() {
		return _location;
	}
}