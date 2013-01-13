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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bbcodeeditor.control.tools.StringTreeMap;


/**
 * @author Assi Nilsmussen
 *
 */
public class SmileyContainer {

	/**
	 * all smiley-codes for fast lookup
	 */
	private StringTreeMap _smileyCodes;
	
	/**
	 * a list with all smileys (SecSmiley-instances)
	 */
	private List _smileys;
	
	/**
	 * the maximum length of a smiley in this container
	 */
	private int _maxSmileyLength = 0;
	
	/**
	 * constructor
	 */
	public SmileyContainer() {
		_smileyCodes = new StringTreeMap();
		_smileys = new ArrayList();
	}
	
	/**
	 * determines the smiley at the beginning of the input-string
	 * 
	 * @param input the input-string
	 * @return the smiley at the end or null if not found
	 */
	public String getSmileyAtBeginning(String input) {
		return (String)_smileyCodes.get(input);
	}
	
	/**
	 * adds the given smiley to the container
	 * 
	 * @param smiley the smiley to add
	 */
	public void addSmiley(SecSmiley smiley) {
		String primCode = smiley.getPrimaryCode();
		if(primCode == null || primCode.length() == 0 || containsSmileyWithCode(primCode))
			return;
		
		String secCode = smiley.getSecondaryCode();
		_smileys.add(smiley);
		_smileyCodes.add(primCode,primCode);
		if(secCode != null && secCode.length() > 0 && !secCode.equals(primCode))
			_smileyCodes.add(secCode,secCode);
		
		if(primCode.length() > _maxSmileyLength)
			_maxSmileyLength = primCode.length();
		if(secCode.length() > _maxSmileyLength)
			_maxSmileyLength = secCode.length();
	}
	
	/**
	 * @return the maximum length of a smiley in this container
	 */
	public int getMaxSmileyLength() {
		return _maxSmileyLength;
	}
	
	/**
	 * @return a List with the smileys
	 */
	public List getSmileys() {
		return _smileys;
	}

	/**
	 * @param primCode the primary code of the smiley
	 * @return true if this container contains a smiley with given primary code
	 */
	public boolean containsSmileyWithPrimCode(String primCode) {
		return getSmileyByPrimCode(primCode) != null;
	}
	
	/**
	 * @param code the primary or secondary code of the smiley
	 * @return true if this container contains a smiley with given primary code
	 */
	public boolean containsSmileyWithCode(String code) {
		return getSmileyByCode(code) != null;
	}
	
	/**
	 * searches for the smiley with the given primary code
	 * 
	 * @param primCode the primary code of the smiley
	 * @return the corresponding smiley or null if not found
	 */
	public SecSmiley getSmileyByPrimCode(String primCode) {
		Iterator it = _smileys.iterator();
		while(it.hasNext()) {
			SecSmiley smiley = (SecSmiley)it.next();
			
			if(smiley.getPrimaryCode().equals(primCode))
				return smiley;
		}
		
		return null;
	}
	
	/**
	 * searches for the smiley with the given primary *or* secondary code
	 * 
	 * @param code the primary or secondary code of the smiley
	 * @return the corresponding smiley or null if not found
	 */
	public SecSmiley getSmileyByCode(String code) {
		Iterator it = _smileys.iterator();
		while(it.hasNext()) {
			SecSmiley smiley = (SecSmiley)it.next();
			
			if(smiley.getPrimaryCode().equals(code) || smiley.getSecondaryCode().equals(code))
				return smiley;
		}
		
		return null;
	}
}