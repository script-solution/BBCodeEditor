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

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bbcodeeditor.control.export.bbcode.BBCodeTags;


/**
 * The settings object which stores the settings for the application
 * 
 * @author hrniels
 */
public final class Settings {

	/**
	 * Represents the no-wrap wordwrap which means that nothing will be wrapped
	 */
	public static final int WORD_WRAP_NO_WRAP				= 0;

	/**
	 * Represents the pixel- and word-based wordwrap
	 */
	public static final int WORD_WRAP_PIXEL_BASED		= 1;

	/**
	 * Represents the word-based wordwrap
	 */
	public static final int WORD_WRAP_WORD_BASED		= 2;
	
	/**
	 * Represents the char-based wordwrap
	 */
	public static final int WORD_WRAP_CHAR_BASED		= 3;
	
	/**
	 * A list with all enabled tags
	 */
	public static List ENABLED_TAGS = Arrays.asList(new Integer[] {
			new Integer(BBCodeTags.BOLD),
			new Integer(BBCodeTags.ITALIC),
			new Integer(BBCodeTags.UNDERLINE),
			new Integer(BBCodeTags.FONT_SIZE),
			new Integer(BBCodeTags.FONT_COLOR),
			new Integer(BBCodeTags.FONT_FAMILY),
			new Integer(BBCodeTags.QUOTE),
			new Integer(BBCodeTags.CODE),
			new Integer(BBCodeTags.LIST),
			new Integer(BBCodeTags.URL),
			new Integer(BBCodeTags.EMAIL),
			new Integer(BBCodeTags.IMAGE),
			
			new Integer(BBCodeTags.LEFT),
			new Integer(BBCodeTags.CENTER),
			new Integer(BBCodeTags.RIGHT),
			new Integer(BBCodeTags.SUBSCRIPT),
			new Integer(BBCodeTags.SUPERSCRIPT),
			new Integer(BBCodeTags.BG_COLOR),
			new Integer(BBCodeTags.STRIKE)
	});
	
	/**
	 * A list with all enabled extra-tags
	 */
	public static List ENABLED_EXTRA_TAGS = new ArrayList();
	
	/**
	 * Indicates wether smileys are enabled
	 */
	public static boolean ENABLE_SMILEYS							= true;
	
	/**
	 * The available fonts
	 */
	public static List ENABLED_FONTS									= new ArrayList();
	
	/**
	 * The wordwrap policy for the editor
	 */
	public static int WORD_WRAP												= WORD_WRAP_PIXEL_BASED;
	
	/**
	 * The position of the wraps. Will be used for the word- and char-based wrap strategies
	 */
	public static int WORD_WRAP_POSITION							= 100;
	
	/**
	 * Do you want to display line-numbers in code-environments?
	 */
	public static boolean DISPLAY_CODE_LINE_NUMBERS		= true;
	
	/**
	 * The component which will be used for dialogs
	 */
	public static Component DIALOG_COMPONENT;
	
	/**
	 * The size of buttons
	 */
	public static Dimension BUTTON_SIZE								= new Dimension(35,30);
	
	/**
	 * the base-url
	 */
	private static URL BASE_URL = null;

	private Settings() {
		// no instances are allowed
	}
	
	/**
	 * sets the base-url
	 * 
	 * @param u the new value
	 * @param path the additional path
	 */
	public static void setBaseURL(URL u,String path) {
		try {
			BASE_URL = new URL(u,path);
		}
		catch(MalformedURLException e) {
			
		}
	}
	
	/**
	 * @return the code-base-URL
	 */
	public static URL getBaseURL() {
		if(BASE_URL != null)
			return BASE_URL;
		
		try {
			return new File("./").toURI().toURL();
		}
		catch(MalformedURLException e) {
			
		}
		
		return null;
	}
	
	/**
	 * determines wether the given tag is enabled
	 * 
	 * @param tag the tag to check. See BBCodeTags.*
	 * @return true if it is enabled
	 */
	public static boolean isTagEnabled(int tag) {
		return ENABLED_TAGS.contains(new Integer(tag));
	}
	
	/**
	 * Checks wether the given extra-tag is enabled
	 * 
	 * @param tag the name of the tag
	 * @return true if so
	 */
	public static boolean isExtraTagEnabled(String tag) {
		return ENABLED_EXTRA_TAGS.contains(tag);
	}
}