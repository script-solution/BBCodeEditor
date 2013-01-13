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
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.events.ImageLoadingFinishedListener;
import bbcodeeditor.control.events.ImageLoadingRequestListener;
import bbcodeeditor.control.tools.StringUtils;
import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * the image-loading-panel which will be shown in the status-bar
 * 
 * @author hrniels
 */
public class ImageLoadingPanel extends JPanel implements ImageLoadingRequestListener {

	private static final long serialVersionUID = 1558372916428295258L;
	
	private final AbstractTextField _textField;

	/**
	 * the label for the text
	 */
	private final JLabel _title;
	
	/**
	 * the progressbar
	 */
	private final JProgressBar _progressBar;
	
	/**
	 * constructor
	 * 
	 * @param textField the textfield
	 */
	public ImageLoadingPanel(AbstractTextField textField) {
		super(new FlowLayout(FlowLayout.RIGHT));
		
		_textField = textField;
		
		_title = new JLabel();
		add(_title);
		
		_progressBar = new JProgressBar(0,100);
		_progressBar.setIndeterminate(true);
		add(_progressBar);
		
		stopLoading();
	}
	
	/**
	 * starts the loading-progress
	 * 
	 * @param title the title of the loading-progress
	 */
	public void startLoading(String title) {
		_title.setText(title);
		_progressBar.setVisible(true);
	}
	
	/**
	 * stops the loading progress
	 */
	public void stopLoading() {
		_title.setText("");
		_progressBar.setVisible(false);
	}
	
	public void imageLoadingRequest(final URL location,
			final ImageLoadingFinishedListener notifier) {
		if(location == null)
			return;
		
		Thread imageLoader = new Thread(new Runnable() {
			public void run() {
				Image img = null;
				final MediaTracker mt = new MediaTracker(_textField);
				try {
					img = Toolkit.getDefaultToolkit().getImage(location);
					
					mt.addImage(img,0);
					mt.waitForAll();
				}
				catch(InterruptedException e) {
					
				}
				
				final Image createdImage = mt.isErrorID(0) ? null : img;
				notifier.imageLoadingFinished(createdImage);
				stopLoading();
			}
		});
		
		String errorMsg = LanguageContainer.getText(Language.GUI_STATUSBAR_LOADING_IMAGE);
		startLoading(StringUtils.simpleReplace(errorMsg,"%s",location.toString()));
		imageLoader.start();
	}
}