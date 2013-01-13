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

import bbcodeeditor.control.view.QuoteEnvironmentView;


/**
 * Represents an Environment to quote something.
 * Will behave like the root-environment, but has a little border and indent.<br>
 * Additionally it is possible to define the author of the quote, which will
 * be painted in the header of the Environment
 * 
 * @author hrniels
 */
public final class QuoteEnvironment extends Environment {
	
	/**
	 * the author-attribute of the quote-environment
	 */
	private String _author;
	
	/**
	 * constructor
	 * 
	 * @param textArea the BBCTextField-instance
	 * @param parent the parent environment
	 * @param line the Line of this environment
	 * @param p the paragraph of this environment
	 * @param author the author of the quote
	 */
	QuoteEnvironment(AbstractTextField textArea,Environment parent,Line line,Paragraph p,
			String author) {
		super(textArea,parent,line,p);
		
		_view = new QuoteEnvironmentView(this);
		
		setAuthor(author);
	}
	
	public int getType() {
		return EnvironmentTypes.ENV_QUOTE;
	}
	
	/**
	 * @return the name of the author or null if no author is set
	 */
	public String getAuthor() {
		return _author;
	}
	
	/**
	 * sets the author
	 * 
	 * @param name the new name
	 */
	void setAuthor(String name) {
		_author = name != null && name.length() > 0 ? name : null;
		_view.forceRefresh(QuoteEnvironmentView.QUOTE_TITLE);
	}
}