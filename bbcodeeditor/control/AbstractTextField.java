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

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.Timer;

import bbcodeeditor.control.EnvironmentProperties.PropertyListener;
import bbcodeeditor.control.events.*;
import bbcodeeditor.control.export.bbcode.BBCodeTags;
import bbcodeeditor.control.view.FontCache;


/**
 * the basic textfield which adds some general methods that would be the same
 * for the BBCTextField and the SBBCTextField<br>
 * This class can't be instantiated. Please use one of the sub-classes!
 * 
 * @author hrniels
 */
public abstract class AbstractTextField extends JComponent
		implements IPublicController,FocusListener,Scrollable {
	
	private static final long serialVersionUID = -7183342611852688140L;
	
	/**
	 * our cursor
	 */
	private final TextCursor _cursor;
	
	/**
	 * the timer for the cursor
	 */
	private final Timer _timer;
	
	/**
	 * the listener-list for caret-position changes
	 */
	private final List _caretPositionChangedListeners = new ArrayList();
	
	/**
	 * the listener-list for attribute changes
	 */
	private final List _attributesChangedListeners = new ArrayList();
	
	/**
	 * the listener-list for bbcode-parse-errors
	 */
	private final List _bbcodeParseErrorListeners = new ArrayList();
	
	/**
	 * the listener-list for hyperlink-clicks
	 */
	private final List _hyperLinkListeners = new ArrayList();
	
	/**
	 * the empty set for our focusTraverselKeys-overwrite
	 */
	private final Set emptySet = Collections.emptySet();
	
	/**
	 * the environment-properties
	 */
	private final EnvironmentProperties _envProperties;
	
	/**
	 * Our font-cache
	 */
	private final FontCache _fontCache = new FontCache();

	/**
	 * the controller of the textField
	 * this should NOT be passed outside!
	 */
	protected Controller _controller;

	/**
	 * the image-loader
	 */
	private ImageLoadingRequestListener _imageLoader = new DefaultImageLoader();
	
	/**
	 * the popup-menu for this textField
	 */
	private JPopupMenu _popupMenu = null;
	
	/**
	 * The URL to the package-root
	 */
	private URL _baseURL = getClass().getResource("../../");
	
	/**
	 * The cached graphics-object
	 */
	private Graphics _graphics;
	
	/**
	 * enabled antialiasing?
	 */
	private boolean _antialiasing = true;
	
	/**
	 * The maximum size of images. Greater images will be resized so that the limit will
	 * not be exceeded. The user can toggle the size with a click on the image.
	 */
	private Dimension _maxImageSize = new Dimension(300,200);
	
	/**
	 * the tab-width for the textField
	 */
	protected int _tabWidth = 4;
	
	/**
	 * the maximum nesting level of environments
	 */
	protected int _maxTagNestingLevel = 5;
	
	/**
	 * The editor-mode
	 */
	protected int _editorMode = MODE_BBCODE;
	
	/**
	 * display line-numbers in code-environments?
	 */
	protected boolean _displayCodeLineNumbers = true;
	
	/**
	 * the required width to paint everything
	 */
	private int _requiredWidth = -1;
	
	/**
	 * the required height to paint everything
	 */
	private int _requiredHeight = -1;
	
	/**
	 * do we want to ignore caret-position-changes?
	 */
	private boolean _ignoreCaretChanges = false;
	
	/**
	 * this is used for forcing the cursor to paint
	 */
	private boolean _tempShowCursor = false;
	
	/**
	 * should the cursor blink?
	 */
	private boolean _blinkingCursor = true;
	
	/**
	 * is the textfield readonly?
	 */
	private boolean _isReadOnly = false;
	
	/**
	 * the last width of the control
	 */
	private int lastWidth = -1;
	
	/**
	 * constructor
	 */
	public AbstractTextField() {
		super();

		_envProperties = new EnvironmentProperties(this);
		_controller = new Controller(this);
		
		_cursor = new TextCursor(this);
		_timer = new Timer(500,_cursor);

		_timer.setRepeats(true);
		if(_blinkingCursor)
			_timer.start();
		
	  setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	  setBackground(Color.WHITE);
		
		addFocusListener(this);
	}
	
	/**
	 * @return the repaint-manager
	 */
	abstract bbcodeeditor.control.RepaintManager getRepaintManager();
	
	/**
	 * @return the paint-pos-manager
	 */
	public abstract PaintPosManager getPaintPosManager();
	
	/**
	 * @return the attributes-manager
	 */
	abstract AttributesManager getAttributesManager();
	
	/**
	 * @return the cursor-manager
	 */
	abstract CursorManager getCursorManager();
	
	/**
	 * @return the view-manager
	 */
	public abstract ViewManager getViewManager();
	
	/**
	 * @return the wordwrap-manager
	 */
	public abstract WordwrapManager getWordwrapManager();
	
	/**
	 * Finishes an action. Performs all necessary stuff (attributes change, cursor
	 * change, repaint)
	 */
	abstract void finish();
	
	/**
	 * @return the FontCache-object
	 */
	public FontCache getFontCache() {
		return _fontCache;
	}
	
	public Graphics getGraphics() {
		// TODO is this correct in every cases?
		// look for hierarchie-changes or something like that?
		if(_graphics == null)
			_graphics = super.getGraphics();
		
		return _graphics;
	}
	
	/**
	 * Sets the basic-key-listener fot this textField. This has to be an instance
	 * of {@link TextAreaKeyListener} or a subclass of it.
	 * Note that all other listeners which are an instance of {@link TextAreaKeyListener}
	 * will be removed before the new listener will be added!
	 * <p>
	 * This gives you the opportunity to change the default key-bindings and
	 * behaviours.
	 * 
	 * @param l the listener
	 */
	public void	setBasicKeyListener(TextAreaKeyListener l) {
		KeyListener[] listener = getKeyListeners();
		for(int i = 0;i < listener.length;i++) {
			if(listener[i] instanceof TextAreaKeyListener)
				removeKeyListener(listener[i]);
		}
		
		addKeyListener(l);
	}
	
	/**
	 * Changes the mode to the current one.
	 * 
	 * @param mode the new mode
	 */
	protected void changeMode(int mode) {
		_editorMode = mode;
		
		if(mode == MODE_HTML) {
			disableTags(Arrays.asList(new Integer[] {
				new Integer(BBCodeTags.CODE),new Integer(BBCodeTags.QUOTE)
			}));
		}
		else {
			enableTags(Arrays.asList(new Integer[] {
				new Integer(BBCodeTags.CODE),new Integer(BBCodeTags.QUOTE)
			}));
		}
		
		if(mode == MODE_TEXT_EDITOR) {
			Map values = new HashMap();
			values.put(EnvironmentProperties.INNER_PADDING,new Integer(0));
			setEnvProperties(values,EnvironmentTypes.ENV_CODE);
			
		  _controller.disableAllTags();
			_controller.enableTag(BBCodeTags.HIGHLIGHT);
		}
		else {
			Map values = new HashMap();
			values.put(EnvironmentProperties.INNER_PADDING,new Integer(5));
			setEnvProperties(values,EnvironmentTypes.ENV_CODE);
		  
		  _controller.enableAllTags();
		}
	  
		clear();
	}
	
	/**
	 * Returns the maximum size of images. Greater images will be resized so that the limit
	 * will not be exceeded. The user can toggle the size with a click on the image.
	 * 
	 * @return the maximum size of images
	 */
	public Dimension getMaxImageSize() {
		return _maxImageSize;
	}
	
	/**
	 * Sets the maximum size of images. Greater images will be resized so that the limit
	 * will not be exceeded. The user can toggle the size with a click on the image.
	 * 
	 * @param size the new size
	 */
	public void setMaxImageSize(Dimension size) {
		_maxImageSize = size;
	}
	
	/**
	 * @return wether line-numbers will be displayed in code-environments
	 */
	public boolean displayCodeLineNumbers() {
		return _displayCodeLineNumbers;
	}
	
	/**
	 * Sets wether line-numbers in code-environments should be displayed
	 * 
	 * @param display the new value
	 */
	public void setDisplayCodeLineNumbers(boolean display) {
		_displayCodeLineNumbers = display;
	}
	
	/**
	 * @return the used base URL
	 */
	public URL getBaseURL() {
		return _baseURL;
	}
	
	/**
	 * Sets the base URL for this textField<br>
	 * This will be used for example for the dummy image.<br>
	 * It should point to the root-directory
	 * 
	 * @param base the base URL
	 */
	public void setBaseURL(URL base) {
		_baseURL = base;
	}
	
	public boolean setEnvProperties(Map properties,int envType) {
		List refreshTypes = new ArrayList();
		
		// set properties
		Iterator it = properties.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			if(e.getKey() instanceof Integer) {
				Integer property = (Integer)e.getKey();
				if(_envProperties.setValue(property,envType,e.getValue())) {
					int refreshType = _envProperties.getRefreshType(property);
					Integer iRefreshType = new Integer(refreshType);
					if(!refreshTypes.contains(iRefreshType))
						refreshTypes.add(iRefreshType);
				}
			}
		}
		
		// refresh all necessary stuff
		if(refreshTypes.size() > 0) {
			if(refreshTypes.contains(new Integer(EnvironmentProperties.REFRESH_CONTENT))) {
				refreshContent();
				return true;
			}
			
			if(refreshTypes.contains(new Integer(EnvironmentProperties.REFRESH_WORD_WRAP))) {
				refreshWordWrap();
				return true;
			}
			
			if(refreshTypes.contains(new Integer(EnvironmentProperties.REFRESH_FONTS))) {
				refreshFonts();
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the maximum nesting level of tags
	 */
	public int getMaxTagNestingLevel() {
		return _maxTagNestingLevel;
	}
	
	/**
	 * sets the maximum nesting level of tags. That means that you can't nest the same
	 * tag more than this number.
	 * 
	 * @param level the new value
	 */
	public void setMaxTagNestingLevel(int level) {
		if(level >= 0)
			_maxTagNestingLevel = level;
	}
	
	public boolean setEnvProperty(Integer property,int envType,Object value) {
		boolean changed = _envProperties.setValue(property,envType,value);
		if(changed) {
			int refreshType = _envProperties.getRefreshType(property);
			switch(refreshType) {
				case EnvironmentProperties.REFRESH_PAINT_POS:
					_controller.refreshPaintPositions();
					break;
				case EnvironmentProperties.REFRESH_CONTENT:
					_controller.refreshContent();
					break;
				case EnvironmentProperties.REFRESH_FONTS:
					_controller.refreshFonts();
					break;
				case EnvironmentProperties.REFRESH_WORD_WRAP:
					_controller.refreshWordWrap();
					break;
			}
			
			repaint(true);
			
			return true;
		}
		
		return false;
	}
	
	public Object getEnvProperty(Integer property,int envType) {
		return _envProperties.getValue(property,envType);
	}
	
	/**
	 * retrieves the given integer-property for the given environment-type
	 * from the properties. If an error occurres the value will be 0
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return the value or 0
	 */
	public int getEnvIntProperty(Integer property,int envType) {
		Object res = _envProperties.getValue(property,envType);
		if(res instanceof Integer)
			return ((Integer)res).intValue();
		
		return 0;
	}
	
	/**
	 * retrieves the given boolean-property for the given environment-type
	 * from the properties. If an error occurres the value will be false
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return the value or false
	 */
	public boolean getEnvBoolProperty(Integer property,int envType) {
		Object res = _envProperties.getValue(property,envType);
		if(res instanceof Boolean)
			return ((Boolean)res).booleanValue();
		
		return false;
	}
	
	/**
	 * retrieves the given Color-property for the given environment-type
	 * from the properties. If an error occurres the value will be null
	 * 
	 * @param property the property-id
	 * @param envType the environment type. see EnvironmentTypes.*
	 * @return the color or null
	 */
	public Color getEnvColorProperty(Integer property,int envType) {
		Object res = _envProperties.getValue(property,envType);
		if(res instanceof Color)
			return (Color)res;
		
		return null;
	}
	
	/**
	 * Adds the given environment-property-listener to the list
	 * 
	 * @param l the listener
	 */
	public void addEnvPropertyListener(PropertyListener l) {
		_envProperties.addPropertyListener(l);
	}

	/**
	 * Removes the given environment-property-listener from the list
	 * 
	 * @param l the listener
	 */
	public void removeEnvPropertyListener(PropertyListener l) {
		_envProperties.removePropertyListener(l);
	}
	
	/**
	 * @return the image-loader used to load images
	 */
	public ImageLoadingRequestListener getImageLoader() {
		return _imageLoader;
	}
	
	/**
	 * sets the image-loader to the given one
	 * 
	 * @param listener the listener
	 */
	public void setImageLoader(ImageLoadingRequestListener listener) {
		if(listener != null)
			_imageLoader = listener;
	}
	
	/**
	 * @return wether the textField is read-only
	 */
	public boolean isReadOnly() {
		return _isReadOnly;
	}
	
	/**
	 * sets wether the textField should be read-only
	 * 
	 * @param readOnly the new value
	 */
	public void setReadOnly(boolean readOnly) {
		_isReadOnly = readOnly;
	}
	
	/**
	 * sets wether the cursor should blink
	 * 
	 * @param blink should it blink? :)
	 */
	public void setBlinkingCursor(boolean blink) {
		boolean change = blink != _blinkingCursor;
		_blinkingCursor = blink;
		
		if(change) {
			if(_blinkingCursor)
				_timer.start();
			else
				_timer.stop();
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	/**
	 * @return the popup-menu of this textField
	 */
	public JPopupMenu getPopupMenu() {
		return _popupMenu;
	}
	
	/**
	 * sets the popup-menu for this textField
	 * 
	 * @param menu the new menu
	 */
	public void setPopupMenu(JPopupMenu menu) {
		if(_popupMenu != null)
			remove(_popupMenu);
		
		_popupMenu = menu;
		add(_popupMenu);
	}
	
	/**
	 * @return wether the cursor should be displayed
	 */
	boolean showCursor() {
		return _cursor.showCursor();
	}
	
	/**
	 * @return true if antialiasing is enabled
	 */
	public boolean getAntialiasing() {
		return _antialiasing;
	}
	
	/**
	 * sets wether antialiasing is enabled
	 * 
	 * @param antialiasing do you want to enable antialiasing?
	 */
	public void setAntialiasing(boolean antialiasing) {
		_antialiasing = antialiasing;
	}
	
	/**
	 * Adds the given listener to the hyperlink-clicked-listener list. It will receive an
	 * event as soon as a hyperlink has been clicked
	 * 
	 * @param l the listener
	 */
	public void addHyperLinkListener(HyperLinkListener l) {
		if(l != null)
			_hyperLinkListeners.add(l);
	}
	
	/**
	 * adds the given listener to the list<br>
	 * The listener will be notified if the BBCode-parser detects an error
	 * 
	 * @param listener the listener to add
	 */
	public void addBBCodeParseErrorListener(BBCodeParseErrorListener listener) {
		if(listener != null)
			_bbcodeParseErrorListeners.add(listener);
	}

	/**
	 * Invokes all listeners that a hyperlink has been clicked
	 * 
	 * @param isEmail true if the link is an email
	 * @param url the URL of the link
	 */
	void invokeHyperLinkClickedListeners(boolean isEmail,String url) {
		Iterator it = _hyperLinkListeners.iterator();
		while(it.hasNext()) {
			HyperLinkListener l = (HyperLinkListener)it.next();
			l.hyperLinkClicked(isEmail,url);
		}
	}
	
	/**
	 * invokes all listeners that a parse-error has occurred
	 * 
	 * @param bbcode the bbcode-text that should be parsed
	 * @param error the error-type
	 * @param errorMsg the error-message
	 */
	void invokeBBCodeParseErrorListeners(String bbcode,int error,String errorMsg) {
		BBCodeParseError event = new BBCodeParseError(bbcode,error,errorMsg);
		Iterator it = _bbcodeParseErrorListeners.iterator();
		while(it.hasNext()) {
			BBCodeParseErrorListener l = (BBCodeParseErrorListener)it.next();
			l.parseError(event);
		}
	}
	
	/**
	 * adds the given listener to the list<br>
	 * The listener will be notified if any attribute in the control has changed
	 * 
	 * @param listener the listener to add
	 */
	public void addAttributesChangedListener(AttributesChangedListener listener) {
		if(listener != null)
			_attributesChangedListeners.add(listener);
	}
	
	/**
	 * invokes all listeners that an attribute has changed
	 */
	void invokeAttributesChangedListeners() {
		Iterator it = _attributesChangedListeners.iterator();
		while(it.hasNext()) {
			AttributesChangedListener l = (AttributesChangedListener)it.next();
			l.attributesChanged();
		}
	}
	
	/**
	 * @return wether caret-changes will be ignored
	 */
	boolean ignoreCaretChanges() {
		return _ignoreCaretChanges;
	}
	
	/**
	 * sets wether caret-changes should be ignored
	 * 
	 * @param ignore do you want to ignore them?
	 */
	void setIgnoreCaretChanges(boolean ignore) {
		_ignoreCaretChanges = ignore;
	}
	
	/**
	 * adds an CaretPositionChangedListener to the control. you will be notified if the value
	 * of the caret changes
	 * 
	 * @param cpcl the listener
	 */
	public void addCaretPositionChangedListener(CaretPositionChangedListener cpcl) {
		_caretPositionChangedListeners.add(cpcl);
	}
	
	/**
	 * this method will be invoked if the caret-position has changed
	 * and calls the listener-methods
	 * 
	 * @param oldPosition the old position of the cursor
	 * @param oldSection the old section of the cursor
	 * @param contentChanged has the content been changed?
	 * @return true if the paint-position has changed
	 */
	boolean invokeCaretPositionChangeListeners(int oldPosition,ContentSection oldSection,
			boolean contentChanged) {
		if(_ignoreCaretChanges)
			return false;
		
		if(oldSection == null)
			throw new InvalidParameterException("oldSection is null");
		
		// clear the temporary attributes if the cursor has moved
		_controller.clearTemporaryAttributes();
		
		ContentSection newSection = getCurrentSection();
		Line line = newSection.getSectionLine();
		Point lastPaintPos = newSection.getView().getPaintPos();
		
		// should never happen
		if(lastPaintPos == null)
			return false;
		
		// determine the pixel-position of the cursor
		int posInSec = newSection.getParentEnvironment().getCurrentCursorPos() -
									 getCurrentParagraph().getElementStartPos() -
									 getCurrentSection().getElementStartPos();
		
		// determine the number of pixels to the cursor
		int widthToCursor = 0;
		if(newSection instanceof TextSection) {
			if(posInSec == 0)
				widthToCursor = 0;
			else
				widthToCursor = ((TextSection)newSection).getTextSectionView().getStringWidth(0,posInSec);
		}
		else if(newSection instanceof ImageSection) {
			ImageSection iSec = (ImageSection)newSection;
			if(posInSec > 0)
				widthToCursor = iSec.getSectionView().getSectionWidth();
		}

		// we want to ensure that the user is always able to see the text
		// which he/she might type
		// therefore we display be default the position below the line
		// and if necessary the top of it
		int lineHeight = line.getLineView().getHeight();
		int xStart = lastPaintPos.x + widthToCursor;
		int yStart = lastPaintPos.y - 5;
		if(getParent() instanceof JViewport) {
			JViewport viewPort = (JViewport)getParent();
			// show line-beginning if possible
			if(xStart < viewPort.getVisibleRect().width)
				xStart = 0;
			
			// if the line is higher than the viewport use setViewPosition()
			// to force the viewport to show the bottom of the line
			// at the very bottom. this way we can see as much of the line
			// as possible
			if(lineHeight > viewPort.getViewRect().height) {
				yStart = lastPaintPos.y + lineHeight + 5 - viewPort.getViewRect().height;
				viewPort.setViewPosition(new Point(xStart,yStart));
			}
			// add a little bit so that the line is better visible
			else if(yStart + lineHeight > viewPort.getViewPosition().y + viewPort.getViewRect().height)
				yStart += 10;
		}
		
		// scroll the rect to visible
		Rectangle rect = new Rectangle(xStart,yStart,5,lineHeight);
		scrollRectToVisible(rect);
		
		// invoke listeners. But just if the position really changed. Because we may call
		// this method if we just want to scroll to the cursor (alignment changed,...)
		int newPosition = getCurrentCursorPos();
		if(newPosition != oldPosition) {
			CaretPositionChangedEvent eventData = new CaretPositionChangedEvent(oldPosition,oldSection,newPosition,
					newSection,contentChanged);
			for(int i = 0;i < _caretPositionChangedListeners.size();i++) {
				CaretPositionChangedListener cpcl =
					(CaretPositionChangedListener)_caretPositionChangedListeners.get(i);
				cpcl.caretPositionChanged(eventData);
			}
		}
		
		return false;
	}
	
	/**
	 * @return the complete height the text needs to render
	 */
	public int getRequiredHeight() {
		return _requiredHeight;
	}
	
	/**
	 * @return the complete width the text needs to render
	 */
	public int getRequiredWidth() {
		return _requiredWidth;
	}
	
	/**
	 * sets the complete size of the content
	 * 
	 * @param width the maximum required width
	 * @param height the total height
	 */
	void setCompleteSize(int width,int height) {
		// the values can't be correct if the width and height is not available
		if(getWidth() == 0 && getHeight() == 0)
			return;
		
		if(width != _requiredWidth || height != _requiredHeight) {
			// do nothing if yet no parent exists
			if(getParent() == null)
				return;
			
			Dimension parentSize = getParent().getSize();
			int newWidth = Math.max(parentSize.width,width);
			int newHeight = Math.max(parentSize.height,height);
			if(newWidth != getWidth() || newHeight != getHeight())
				setSize(newWidth,newHeight);
		}
		
		_requiredWidth = width;
		_requiredHeight = height;
	}
	
	public void repaint() {
		if(getIgnoreRepaint())
			return;
		
		super.repaint();
	}
	
	public void repaint(Rectangle paintRect) {
		if(getIgnoreRepaint())
			return;
		
		super.repaint(paintRect);
	}
	
	/**
	 * repaints the control if it is focused. <code>showCursor</code> forces the cursor to
	 * paint not related to the interval
	 * 
	 * @param showCursor do you want to show the cursor?
	 */
	public void repaint(boolean showCursor) {
		if(getIgnoreRepaint())
			return;
		
		repaint(getVisibleRect(),showCursor);
	}
	
	/**
	 * repaints the control if it is focused. <code>showCursor</code> forces the cursor to
	 * paint not related to the interval
	 * 
	 * @param paintRect the rectangle to draw
	 * @param showCursor do you want to show the cursor?
	 */
	public void repaint(Rectangle paintRect,boolean showCursor) {
		if(getIgnoreRepaint())
			return;
		
		if(showCursor)
			_tempShowCursor = true;

		super.repaint(paintRect);
	}
	
	public void paint(Graphics g) {
		// we have to refresh the paint-positions if there are not set yet
		if(_requiredHeight == -1 && _requiredWidth == -1)
			_controller.refreshPaintPositions();
		
		super.paint(g);
		
		Rectangle paintRect = g.getClipBounds();
		if(paintRect == null)
			paintRect = getVisibleRect();
		
		//System.out.println(paintRect);
		
		if(_antialiasing && g instanceof Graphics2D)
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth() - 1,getHeight() - 1);
		
		_controller.paint(g,paintRect,0,0,_tempShowCursor || showCursor());
		
		/*g.setColor(Color.RED);
		g.drawRect(paintRect.x,paintRect.y,paintRect.width - 1,paintRect.height - 1);*/
		
		if(_tempShowCursor)
			_tempShowCursor = false;
	}
	
	/**
	 * repaints the given lin
	 * 
	 * @param l the line to repaint
	 * @param complete do you want to paint the complete line? (the complete tf-width)
	 */
	protected void paintLine(Line l,boolean complete) {
		Rectangle rect = getLineRect(l,complete);
		repaint(rect,true);
	}
	
	/**
	 * repaints the given sections
	 * 
	 * @param sections the sections to paint
	 */
	protected void paintSections(ContentSection[] sections) {
		Rectangle rect = getSectionRect(sections);
		repaint(rect,true);
	}
	
	/**
	 * determines the rectangle of the given line
	 * if complete is enabled the complete width of the control will be used
	 * 
	 * @param l the line
	 * @param complete the complete width of the control?
	 * @return the rectangle which contains the line
	 */
	Rectangle getLineRect(Line l,boolean complete) {
		ContentSection first = null;
		ContentSection last = null;
		if(complete) {
			ContentSection firstInLine = (ContentSection)l.getFirstSection();
			if(firstInLine.getView().getPaintPos() == null)
				return getBounds();
			
			
			Point pp = firstInLine.getView().getPaintPos();
			int height = l.getLineView().getHeight();
			return new Rectangle(0,pp.y - 1,getWidth(),height + 4);
		}
		
		first = (ContentSection)l.getFirstSection();
		last = (ContentSection)l.getLastSection();
		// We can use just the first and last section because we'll use the line-height
		// so the first- and last-section-height may be lower than other section-heights
		
		if(first == last)
			return getSectionRect(new ContentSection[] {first});
		
		return getSectionRect(new ContentSection[] {first,last});
	}
	
	/**
	 * determines the rectangle of the given sections
	 * 
	 * @param sections an array with all sections
	 * @return the rectangle which contains all sections
	 */
	Rectangle getSectionRect(ContentSection[] sections) {
		Point min = new Point(Integer.MAX_VALUE,Integer.MAX_VALUE);
		Point max = new Point(0,0);
		
		for(int i = 0;i < sections.length;i++) {
			Point lastPaintPos = sections[i].getView().getPaintPos();
			if(lastPaintPos == null)
				continue;
			
			Point start = new Point(lastPaintPos.x - 4,lastPaintPos.y - 1);
			if(start.x < min.x)
				min.x = start.x;
			if(start.y < min.y)
				min.y = start.y;
			
			Point end = new Point(lastPaintPos.x + sections[i].getSectionView().getSectionWidth() + 4,
													  lastPaintPos.y + sections[i].getSectionLine().getLineView().getHeight() + 4);
			if(end.x > max.x)
				max.x = end.x;
			if(end.y > max.y)
				max.y = end.y;
		}
		
		return new Rectangle(min.x,min.y,max.x - min.x,max.y - min.y);
	}

	public void focusGained(FocusEvent e) {
		// we want to show the cursor immediatly and restart the timer
		repaint(true);
		
		if(_blinkingCursor)
			_timer.start();
	}
	
	public void focusLost(FocusEvent e) {
		// hide the cursor and stop the timer
		_cursor.setShowCursor(false);
		repaint(false);
		
		if(_blinkingCursor)
			_timer.stop();
	}

	public Dimension getPreferredSize() {
		Dimension parentSize = getParent().getSize();
		// ensure that we always have at least the size of the parent-view
		// and at least the size that we need
		int width = Math.max(parentSize.width,_requiredWidth);
		int height = Math.max(parentSize.height,_requiredHeight);
		
		// we have to refresh the wordwrap if the control has been resized.
		if(parentSize.width != lastWidth) {
			if(lastWidth > 0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						refreshWordWrap();
					}
				});
			}
			
			lastWidth = parentSize.width;
		}
		
		return new Dimension(width,height);
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		Dimension pref = getPreferredSize();
		int width = Math.max(pref.width,_requiredWidth);
		int height = Math.max(pref.height,_requiredHeight);
		return new Dimension(width,height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,int orientation,int direction) {
		if(orientation == SwingConstants.VERTICAL)
			return getPreferredSize().height / 3;
		
		return getPreferredSize().width / 3;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,int orientation,int direction) {
		return 15;
	}
	
	/**
   * Ensure that the forward/backeard traversal key (the tab)
   * does not move the component focus, - return an empty key set.
   * 
   * @param id the id of the key
   * @return a set with the available keys
   */
  public Set getFocusTraversalKeys(int id) {
    if (id == KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS ||
        id == KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS ||
        id == KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS ||
        id == KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS) {
      return emptySet;
    }
    
		return super.getFocusTraversalKeys(id);
  }
	
	public String toString() {
		return _controller.toString();
	}
  
  /**
   * the default image loader which will be used if there is no other one
   * 
   * @author hrniels
   */
  private final class DefaultImageLoader implements ImageLoadingRequestListener {

		public void imageLoadingRequest(final URL location,
				final ImageLoadingFinishedListener notifier) {
			Thread imageLoader = new Thread(new Runnable() {
				public void run() {
					Image img = null;
					MediaTracker mt = new MediaTracker(AbstractTextField.this);
					try {
						img = Toolkit.getDefaultToolkit().getImage(location);
						
						mt.addImage(img,0);
						mt.waitForAll();
					}
					catch(InterruptedException e) {
						
					}
					
					notifier.imageLoadingFinished(mt.isErrorID(0) ? null : img);
				}
			});
			imageLoader.start();
		}
  }
}