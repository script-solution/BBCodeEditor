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

package bbcodeeditor.gui;

import java.awt.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import bbcodeeditor.gui.combobox.LimitedImageIcon;


/**
 * Small helper methods
 * 
 * @author hrniels
 */
public class Helper {
	
	private Helper() {
		// prevent instantiation
	}

	/**
	 * Loads the image and returns the image-icon to use.
	 * If the image is too big the LimitedImageIcon will be used.
	 * 
	 * @param c the component
	 * @param image the image-path
	 * @return the image-icon
	 */
	public static ImageIcon getLimitedImageIcon(Component c,String image) {
		Image img = Toolkit.getDefaultToolkit().getImage(image);
		Dimension size = new Dimension(Settings.BUTTON_SIZE.width,Settings.BUTTON_SIZE.height);
		return Helper.getLimitedImageIcon(c,img,size);
	}

	/**
	 * Loads the image and returns the image-icon to use.
	 * If the image is too big the LimitedImageIcon will be used.
	 * 
	 * @param c the component
	 * @param image the image-URL
	 * @return the image-icon
	 */
	public static ImageIcon getLimitedImageIcon(Component c,URL image) {
		Image img = Toolkit.getDefaultToolkit().getImage(image);
		Dimension size = new Dimension(Settings.BUTTON_SIZE.width,Settings.BUTTON_SIZE.height);
		return Helper.getLimitedImageIcon(c,img,size);
	}
	
	/**
	 * Loads the image and returns the image-icon to use.
	 * If the image is too big the LimitedImageIcon will be used.
	 * 
	 * @param c the component
	 * @param img the image
	 * @param size the max-size of the image
	 * @return the image-icon
	 */
	public static ImageIcon getLimitedImageIcon(Component c,Image img,Dimension size) {
		Dimension imgSize = new Dimension(
			size.width - 8,
			size.height - 4
		);
  	
  	// load the image
  	MediaTracker mt = new MediaTracker(c);
  	mt.addImage(img,1);
  	try {
  		mt.waitForAll();
  	}
  	catch(InterruptedException e) {
  		
  	}
  	
  	// use the limited image?
  	if(img.getWidth(c) > imgSize.width + 2 || img.getHeight(c) > imgSize.height + 4)
  		return new LimitedImageIcon(img,imgSize.width,imgSize.height);
  	
  	// ok, the default one is enough
  	return new ImageIcon(img);
	}

	/**
	 * Builds the location in the document-base with the given file-path/name
	 * 
	 * @param file the path / filename to use
	 * @return the complete URL
	 */
	public static URL getFileInDocumentBase(String file) {
		try {
			return new URL(Settings.getBaseURL(),file);
		}
		catch(MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Builds a separator panel
	 * 
	 * @return the panel
	 */
	public static JPanel getSeparator() {
		JPanel sep = new JPanel() {
			private static final long serialVersionUID = 3543668010487797227L;

			public void paintComponent(Graphics g) {
				int half = getWidth() / 2;
				g.setColor(UIManager.getColor("controlShadow"));
				g.drawLine(half,0,half,getHeight());
			}
		};
    sep.setPreferredSize(new Dimension(10,20));
    return sep;
	}
	
	/**
	 * Tries to open the given URL in the default browser
	 * 
	 * @param url the URL to open
	 */
	public static void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			// Mac OS
			if(osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",new Class[] {String.class});
				openURL.invoke(null,new Object[] {url});
			}
			// windows
			else if(osName.startsWith("Windows"))
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			// assume Unix or Linux
			else {
				// search for an installed browser
				String[] browsers = {"firefox","opera","konqueror","epiphany","mozilla","netscape"};
				String browser = null;
				for(int count = 0;count < browsers.length && browser == null;count++) {
					if(Runtime.getRuntime().exec(new String[] {"which",browsers[count]}).waitFor() == 0)
						browser = browsers[count];
				}
				
				if(browser == null)
					throw new Exception("Could not find web browser");
				
				Runtime.getRuntime().exec(new String[] {browser,url});
			}
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null,"Error attempting to launch web browser:\n"
					+ e.getLocalizedMessage());
		}
	}
}