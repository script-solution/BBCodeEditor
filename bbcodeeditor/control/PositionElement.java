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

import bbcodeeditor.control.tools.AbstractElement;
import bbcodeeditor.control.view.View;
import bbcodeeditor.control.view.Viewable;


/**
 * An abstract class for an element which has a position in the control<br>
 * It contains a start- and end-position and a length. Additionally every
 * position-element belongs to an environment.
 * 
 * @author hrniels
 */
public abstract class PositionElement extends AbstractElement implements Viewable {
	
	/**
	 * the Environment this section belongs to
	 */
	protected final Environment _env;
	
	/**
	 * The view of this element
	 */
	protected View _view;
	
	/**
	 * the start-position of this element in the container
	 */
	protected int _startPos;
	
	/**
	 * the end-position of this element in the container
	 */
	protected int _endPos;
	
	/**
	 * the length of this element
	 */
	protected int _length;
	
	/**
	 * constructor
	 * 
	 * @param parentEnv the environment which contains this section
	 * @param startPos the "global" start-position
	 * @param endPos the "global" end-position
	 * @param line the line of this section
	 */
	PositionElement(Environment parentEnv,int startPos,int endPos) {
		super(null,null);
		
		_env = parentEnv;
		_length = (endPos - startPos) + 1;
		_startPos = startPos;
		_endPos = endPos;
	}
	
	/**
	 * @return the textfield of this object
	 */
	public AbstractTextField getTextField() {
		return _env._textArea;
	}
	
	public View getView() {
		return _view;
	}
	
	/**
	 * @return the parent environment
	 */
	public Environment getParentEnvironment() {
		return _env;
	}
	
	/**
	 * the start-position of this element in the container
	 * 
	 * @return start-position of this element
	 */
	public int getElementStartPos() {
		return _startPos;
	}
	
	/**
	 * sets the position (start and end) to given value.
	 * 
	 * @param startPos the new start-position
	 * @param endPos the new end-position
	 */
	void setElementPos(int startPos,int endPos) {
		_startPos = startPos;
		_endPos = endPos;
		_length = (endPos - startPos) + 1;
	}
	
	/**
	 * the end-position of this element in the container
	 * 
	 * @return end-position of this element
	 */
	public int getElementEndPos()	{
		return _endPos;
	}
	
	/**
	 * the length of this element
	 * 
	 * @return length of this element
	 */
	public int getElementLength() {
		return _length;
	}

	/**
	 * decreases the start- and end-position of this section by the given value
	 * 
	 * @param val the value by which you want to decrease
	 */
	void decreaseElementPos(int val) {
		setElementPos(_startPos - val,_endPos - val);
	}
	
	/**
	 * increases the end-position of this section by the given value
	 * 
	 * @param val the value by which you want to increase
	 */
	void decreaseElementEndPos(int val) {
		setElementPos(_startPos,_endPos - val);
	}

	/**
	 * increases the start- and end-position of this section by the given value
	 * 
	 * @param val the value by which you want to increase
	 */
	void increaseElementPos(int val) {
		setElementPos(_startPos + val,_endPos + val);
	}
	
	/**
	 * increases the end-position of this section by the given value
	 * 
	 * @param val the value by which you want to increase
	 */
	void increaseElementEndPos(int val) {
		setElementPos(_startPos,_endPos + val);
	}
}