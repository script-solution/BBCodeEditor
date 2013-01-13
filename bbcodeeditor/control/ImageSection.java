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

import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;

import javax.swing.SwingUtilities;

import bbcodeeditor.control.events.ImageLoadingFinishedListener;
import bbcodeeditor.control.view.ILineView;
import bbcodeeditor.control.view.ImageSectionView;



/**
 * a ContentSection which contains an image
 * 
 * @author hrniels
 */
public class ImageSection extends ContentSection {

	/**
	 * this text for this section<br>
	 * we need this to prevent many problems. because many methods assume that every
	 * section contains text, is not empty etc.
	 */
	public static final String dummyText = "-";
	
	/**
	 * indicates wether the image is currently maximized
	 */
	private boolean _isMaximized;
	
	/**
	 * the currently used image-size
	 */
	private Dimension _imageSize;
	
	/**
	 * the image of this section
	 */
	private SecImage _image;
	
	/**
	 * Constructor
	 * 
	 * @param textArea the textArea-object
	 * @param env the environment which contains this section
	 * @param img the image of this section
	 * @param start the "global" start-position
	 * @param line the line of this section
	 * @param para the Paragraph of this section
	 */
	ImageSection(Environment env,SecImage img,int start,Line line,
			Paragraph para) {
		super(env,start,start,line,para);
		
		_view = new ImageSectionView(this);
		_image = img;
		setImageSize(true);
		
		loadImage(img.getImageURL());
	}
	
	/**
	 * Loads the given image
	 * 
	 * @param imageURL the URL of the image
	 */
	private void loadImage(URL imageURL) {
		// let the image-loader of the textarea load the image
		_env.getTextField().getImageLoader().imageLoadingRequest(imageURL,
			new ImageLoadingFinishedListener() {
				public void imageLoadingFinished(Image image) {
					if(image != null) {
						_image.setImage(image);
						setImageSize(true);
						
						// TODO is this the right location?
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								// we have to perform wordwrap if we have a pixel-based wordwrap
								IWordWrap ww = _env.getWordWrapStrategie();
								if(ww instanceof WordWrapPixelBased)
									_env.getTextField().getWordwrapManager().markDirty(_paragraph);
								
								// refresh line height
								_line.getView().forceRefresh(ILineView.LINE_HEIGHT);
								
								// we want to jump to the current cursor-position
								_env.getTextField().getCursorManager().forceCursorChange();
								
								_env.getTextField().getRepaintManager().markCompletlyDirty();
								_env.getTextField().finish();
							}
						});
					}
				}
			}
		);
	}
	
	/**
	 * @return wether the image is too big so that it can be maximized or minimized
	 */
	public boolean isMaximizable() {
		Dimension max = _env.getTextField().getMaxImageSize();
		return _image.getWidth() > max.width || _image.getHeight() > max.height;
	}
	
	/**
	 * @return wether the image is currently maximized
	 */
	public boolean isMaximized() {
		return _isMaximized;
	}
	
	/**
	 * Sets the image-size for this image-section<br>
	 * Note that the paint-positions have to refreshed after the call and the control
	 * has to be repainted!
	 * 
	 * @param maximized should the image been maximized?
	 * @return true if the size has changed
	 */
	boolean setImageSize(boolean maximized) {
		Dimension old = _imageSize == null ? null : new Dimension(_imageSize.width,
				_imageSize.height);
		
		if(maximized)
			_imageSize = new Dimension(_image.getWidth(),_image.getHeight());
		else {
			Dimension max = _env.getTextField().getMaxImageSize();
			int imgWidth = _image.getWidth();
			int imgHeight = _image.getHeight();
			
			int targetWidth = -1;
			int targetHeight = -1;
			
			if(imgWidth > max.width && imgHeight > max.height) {
				if(imgWidth > imgHeight) {
					targetWidth = max.width;
					targetHeight = (int)(targetWidth * ((float)imgHeight / imgWidth));
				}
				else {
					targetHeight = max.height;
					targetWidth = (int)(targetHeight * ((float)imgWidth / imgHeight));
				}
			}
			else if(imgWidth > max.width) {
				targetWidth = max.width;
				targetHeight = (int)(targetWidth * ((float)imgHeight / imgWidth));
			}
			else if(imgHeight > max.height) {
				targetHeight = max.height;
				targetWidth = (int)(targetHeight * ((float)imgWidth / imgHeight));
			}
			else {
				targetWidth = imgWidth;
				targetHeight = imgHeight;
			}
			
			_imageSize = new Dimension(targetWidth,targetHeight);
		}
		
		_isMaximized = maximized;
		_line.getView().forceRefresh(ILineView.LINE_HEIGHT);
		
		return old != null && (old.width != _imageSize.width || old.height != _imageSize.height);
	}
	
	/**
	 * @return the height of the image
	 */
	public int getImageHeight() {
		return _imageSize.height;
	}
	
	/**
	 * @return the width of the image
	 */
	public int getImageWidth() {
		return _imageSize.width;
	}
	
	/**
	 * @return the image of this section
	 */
	public SecImage getImage() {
		return _image;
	}
	
	/**
	 * sets the image of this section
	 * 
	 * @param img the new image
	 */
	void setImage(SecImage img) {
		_image = img;
		loadImage(img.getImageURL());
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		String id = String.valueOf(hashCode());
		result.append("Class: ImageSection, ID: " + id + "\n");
		result.append("PaintPos: " + getView().getPaintPos() + "\n");
		result.append("[S:" + _startPos + ",E:" + _endPos + ",L:" + _length + "]\n");
		result.append("Img-Path: " + _image.getImageURL() + "\n");
		return result.toString();
	}
}