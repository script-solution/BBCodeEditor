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

import java.util.Hashtable;
import java.util.Random;

import javax.swing.SwingUtilities;

import bbcodeeditor.control.AbstractTextField;
import bbcodeeditor.control.History;
import bbcodeeditor.control.ParagraphAttributes;


public class ControlTester implements Runnable {

	private AbstractTextField _textField;
	
	private int _delay = 1;
	
	/**
	 * constructor
	 * 
	 * @param tf the textfield
	 */
	public ControlTester(AbstractTextField tf) {
		_textField = tf;
	}

	public void run() {
		final String[] texts = new String[] {
				"abc\n[quote=abc]harhar\n[code]<?php\necho \"what?\";\n?>[/code]\n[/quote]\nso nu aber :)\n\n[quote=abc]harhar\n[code]<?php\necho \"what?\";\n?>[/code]\n[/quote]",
				"[font=blub]abc[/font][color=#00FF0]a[/color][size=32]haha[/size]\n[font=courier new]abc[/font][color=#00FF00]a[/color][size=18]haha[/size]\n[list][*][quote]a[b]asd[/b][/quote][*]b[*]c[*]d[*][/list]abc[b]blub[i]haha[/i][u]hu?[/u][/b]hm[code][b][i]alter?[/i][/b][/code][font=blub]abc[/font][color=#00FF0]a[/color][size=32]haha[/size]\n[font=courier new]abc[/font][color=#00FF00]a[/color][size=18]haha[/size]\n[list][*][quote]a[b]asd[/b][/quote][*]b[*]c[*]d[*][/list]abc[b]blub[i]haha[/i][u]hu?[/u][/b]hm[code][b][i]alter?[/i][/b][/code]",
				"1,2,3 abc\n[b]fett[/b]blub test[quote]a[code]test[/code][/quote][u]unter[b]str[/b]ichen[/u]",
				"[size=29]abc\ndef[/size][quote][size=29]abc:-)[/size][code]bla:-)blub[/code][/quote][size=29]aa[/size]:-)",
				"abc\n\t[size=20]blub[/size]\nte[b]m[/b]p[quote][code]<?php\n\t\t\techo \"test\";\n?>[/code][url=bla]te[b]st[/b][/url][/quote]",
				"abc\ndef\n[b]ghi[/b]\n[img]./images/bigtest.jpg[/img]",
				"ghi[quote][b]h[color=red]a[font=Courier new][size=16][u]aha[i]hihi[/i]blub[/u][/size][/font][/color][/b][color=red][font=Courier new][size=16][u]test[/u][/size]ab[/font]cd[/color][/quote]haha",
				"abc\ndef\n[right][b]blub blub[/b][/right]",
				"def[quote]test[/quote]abc",
				"[url=test][b][i][u][font=courier][color=#FFFF00][size=20]abc[/size][/color][/font][/u][/i][/b][/url]",
				"[quote]\n[quote]blub[/quote]\n[/quote]abc",
				"[b][s]abc[/s][/b]:)[i]def[/i]blub\ntest[center]haha[/center] [quote]test[/quote]\n",
				"[center][img]./images/smileys/wink.png[/img][/center]"
		};

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				while(true) {
					for(int i = 0;i < texts.length;i++) {
						System.out.println("TEXT " + i + ":");
						checkRemoveText(texts[i]);
						System.out.println("\n");
					}
					
					try {
						Thread.sleep(100);
					}
					catch(InterruptedException e) {
						
					}
				}
			}
		});
	}
	
	private void checkAttributes(String text) {
		_textField.setText(text);
		
		int len = _textField.length();
		int undoSize = _textField.getHistory().getUndoLength();
		
		String beforeText = _textField.getText();
		
		Random r = new Random();
		for(int i = 0;i < 10;i++) {
			int attr = 3 + Math.abs(r.nextInt()) % 3;
			int start = Math.abs(r.nextInt()) % len;
			int end = (Math.abs(r.nextInt()) % (len - start)) + start + 1;
			
			_textField.toggleAttribute(start,end,new Integer(attr));
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		while(_textField.getHistory().getUndoLength() > undoSize) {
			_textField.undo();
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		String afterText = _textField.getText();
		
		if(afterText.equals(beforeText))
			System.out.println("SUCCESS");
		else {
			System.out.println("FAILED");
			System.out.println("BEFORE: '" + beforeText + "'");
			System.out.println("AFTER: '" + afterText + "'");
			System.exit(1);
		}
	}
	
	private void checkRemoveText(String text) {
		_textField.setText(text);
		int len = _textField.length();
		int undoSize = _textField.getHistory().getUndoLength();
		
		String beforeText = _textField.getText();
		
		Random r = new Random();
		for(int i = 0;i < 10;i++) {
			int start = Math.abs(r.nextInt()) % len;
			int end = (Math.abs(r.nextInt()) % (len - start)) + start + 1;
			
			System.out.println(i + ": " + start + "," + end);
			_textField.removeText(start,end);
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		int i = 9;
		while(_textField.getHistory().getUndoLength() > undoSize) {
			System.out.println(i--);
			_textField.undo();
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		String afterText = _textField.getText();
		
		if(afterText.equals(beforeText))
			System.out.println("SUCCESS");
		else {
			System.out.println("FAILED");
			System.out.println("BEFORE: '" + beforeText + "'");
			System.out.println("AFTER: '" + afterText + "'");
			System.exit(1);
		}
	}
	
	private void checkRemovePrevChar(String text) {
		_textField.setText(text);
		
		String beforeText = _textField.getText();
		
		for(int i = 0;i < 100 && _textField.getCurrentCursorPos() > 0;i++) {
			
			_textField.removePreviousChar();
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		History h = _textField.getHistory();
		while(h.getUndoLength() > 1) {
			_textField.undo();
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		String afterText = _textField.getText();
		if(afterText.equals(beforeText))
			System.out.println("SUCCESS");
		else {
			System.out.println("FAILED");
			System.out.println("BEFORE: '" + beforeText + "'");
			System.out.println("AFTER: '" + afterText + "'");
			System.exit(1);
		}
	}
	
	private void checkHistory(String text) {
		_textField.setText(text);
		
		String begin = _textField.getText();
		
		History h = _textField.getHistory();
		while(h.getUndoLength() > 0) {
			_textField.undo();
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		while(h.getRedoLength() > 0) {
			_textField.redo();
			
			try {
				Thread.sleep(_delay);
			}
			catch(InterruptedException e) {
				
			}
		}
		
		String end = _textField.getText();
		if(end.equals(begin))
			System.out.println("SUCCESS!");
		else
			System.out.println("FAILED!");
	}
}
