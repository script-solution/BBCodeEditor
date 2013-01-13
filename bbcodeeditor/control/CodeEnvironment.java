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

import bbcodeeditor.control.highlighter.HighlightSyntax;
import bbcodeeditor.control.view.CodeEnvironmentView;



/**
 * An Environment for source-code<br>
 * Doesn't allow sub-environments and formating.<br>
 * Additionally it has a monospaced font and line-numbers will be printed.
 * 
 * @author hrniels
 */
public final class CodeEnvironment extends Environment {
	
	/**
	 * The highlight-syntax for this code-environment
	 */
	private Object _highlightSyntax;
	
	/**
	 * constructor
	 * 
	 * @param textArea the BBCTextField-instance
	 * @param parent the parent environment
	 * @param line the Line of this environment
	 * @param p the paragraph of this environment
	 */
	CodeEnvironment(AbstractTextField textArea,Environment parent,Line line,Paragraph p) {
		this(textArea,parent,line,p,null);
	}
	
	/**
	 * constructor
	 * 
	 * @param textArea the BBCTextField-instance
	 * @param parent the parent environment
	 * @param line the Line of this environment
	 * @param p the paragraph of this environment
	 * @param hlSyntax the highlight syntax to use
	 */
	CodeEnvironment(AbstractTextField textArea,Environment parent,Line line,
			Paragraph p,Object hlSyntax) {
		super(textArea,parent,line,p);

		_view = new CodeEnvironmentView(this);
		_view.forceRefresh(CodeEnvironmentView.CODE_TITLE);
		_view.forceRefresh(CodeEnvironmentView.LINE_NUMBERS);
		
		setHighlightSyntax(hlSyntax);
	}
	
	/**
	 * @return the highlight syntax to use
	 */
	public Object getHighlightSyntax() {
		return _highlightSyntax;
	}
	
	/**
	 * Sets the highlight-syntax of this environment. Updates all paragraphs in
	 * this env.
	 * 
	 * @param syntax the syntax. Null means that nothing will be highlighted
	 */
	void setHighlightSyntax(Object syntax) {
		if((syntax == null && _highlightSyntax != null) ||
				(syntax != null && !syntax.equals(_highlightSyntax))) {
			if(syntax != null) {
				if(!HighlightSyntax.announceHighlighter(syntax))
					return;
			}
			
			if(_highlightSyntax != null)
				HighlightSyntax.removeHighlighter(_highlightSyntax);
			
			_highlightSyntax = syntax;
			
			// clear highlighting?
			if(syntax == null) {
				TextAttributes empty = new TextAttributes();
				applyAttributes(0,getElementLength(),empty,true,true);
			}
			// update highlighting
			else
				updateHighlighting();
			
			_view.forceRefresh(CodeEnvironmentView.CODE_TITLE);
		}
	}
	
	public int getType() {
		return EnvironmentTypes.ENV_CODE;
	}
}