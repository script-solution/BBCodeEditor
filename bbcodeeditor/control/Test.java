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
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import bbcodeeditor.control.export.html.HTMLParser;
import bbcodeeditor.control.export.html.HTMLTokenizer;
import bbcodeeditor.control.tools.StringUtils;



public class Test extends JFrame {
	
	private static class T2 {
		private T1 _t1;
		
		public T2(T1 t1) {
			_t1 = t1;
			System.out.println("t2 created");
		}
		
		public void finalize() {
			System.out.println("t2 destroyed");
		}
	}
	
	private static class T1 {
		private T2 _t2;
		
		public T1() {
			_t2 = new T2(this);
			System.out.println("t1 created");
		}
		
		public void finalize() {
			//_t2.finalize();
			System.out.println("t1 destroyed");
		}
	}
	
	private T1 _t1;
	
	public Test() {
		_t1 = new T1();
		System.out.println(_t1);
		
		_t1 = null;
		
		System.gc();
		
		System.out.println("hier");
	}

	/*public Test() {
		setSize(new Dimension(800,600));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
//		JComponent tf = new JTextPane(new HTMLDocument());
//		((JTextPane)tf).setEditorKit(new HTMLEditorKit());
		
		JComponent tf = new BBCTextField(null);
		((BBCTextField)tf).setEnvProperty(EnvironmentProperties.WORD_WRAP_STRATEGIE,
				EnvironmentTypes.ENV_ROOT,new Integer(IPublicController.WORD_WRAP_PIXEL_BASED));
		
		getContentPane().add(new JScrollPane(tf));
		
		setVisible(true);
		
		Random r = new Random();
		StringBuffer total = new StringBuffer();
		for(int i = 0;i < 1000;i++) {
			StringBuffer temp = new StringBuffer();
			int len = Math.abs(r.nextInt() % 30) + 100;
			for(int a = 0;a < len;a++) {
				char c;
				if(r.nextInt() % 7 == 0)
					c = ' ';
				else
					c = (char)(Math.abs(r.nextInt() % 26) + 97);
				temp.append(c);
			}
			
			String tStr = temp.toString();
			if(r.nextInt() % 5 == 0)
				tStr = "[size=" + (Math.abs(r.nextInt()) % 10 + 20) + "]" + tStr + "[/size]";
			if(r.nextInt() % 5 == 0)
				tStr = "[b]" + tStr + "[/b]";
			if(r.nextInt() % 5 == 0)
				  tStr = "[sup]" + tStr + "[/sup]";
			else if(r.nextInt() % 5 == 0)
			    tStr = "[sub]" + tStr + "[/sub]";
			if(r.nextInt() % 5 == 0)
				tStr = "[quote]" + tStr + "[/quote]";
			else if(r.nextInt() % 6 == 0)
				tStr = "[list][*]" + tStr + "[/list]";
			
			if(tf instanceof JTextPane) {
				tStr = StringUtils.simpleReplace(tStr,"[","<");
				tStr = StringUtils.simpleReplace(tStr,"]",">");
			}
			
			total.append(tStr);
			if(tf instanceof BBCTextField)
				total.append("\n");
			else
				total.append("<br>");
		}
		
		long start = System.currentTimeMillis();
		
		if(tf instanceof BBCTextField)
			((BBCTextField)tf).setText(total.toString());
		else
			((JTextPane)tf).setText(total.toString());
		
		long end = System.currentTimeMillis();
		
		System.out.println(end - start);
	}*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		/*buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
		buf.append("<HTML>");
		buf.append("<HEAD>");
		buf.append("	<META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=utf-8\">");
		buf.append("	<TITLE></TITLE>");
		buf.append("	<META NAME=\"GENERATOR\" CONTENT=\"OpenOffice.org 2.0  (Linux)\">");
		buf.append("	<META NAME=\"AUTHOR\" CONTENT=\"Assi Nilsmussen\">");
		buf.append("	<META NAME=\"CREATED\" CONTENT=\"20070318;17433000\">");
		buf.append("	<META NAME=\"CHANGED\" CONTENT=\"16010101;0\">");
		buf.append("	<STYLE TYPE=\"text/css\">");
		buf.append("	<!--");
		buf.append("		@page { size: 21cm 29.7cm; margin: 2cm }");
		buf.append("		P { margin-bottom: 0.21cm }");
		buf.append("	-->");
		buf.append("	</STYLE>");
		buf.append("</HEAD>");
		buf.append("<BODY DIR=\"LTR\">");
		buf.append("<P ALIGN=LEFT STYLE=\"margin-bottom: 0cm; background: transparent; line-height: 200%; text-decoration: none\">\n");
		buf.append("<FONT COLOR=\"#660066\"><FONT SIZE=3><B><SPAN STYLE=\"background: transparent\">ich\n");
		buf.append("teste das mal kurz hier :)</SPAN></B></FONT></FONT></P>\n");
		buf.append("<P ALIGN=CENTER STYLE=\"margin-bottom: 0cm\">mal sehen <STRIKE>wie</STRIKE>\n");
		buf.append("das <I>aussieht,</I> wenn ich <U>das</U> kopiere...</P>\n");
		buf.append("<P STYLE=\"margin-bottom: 0cm\"><BR>test<BR><FONT SIZE=2>bla</FONT><BR><FONT SIZE=5><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT COLOR=\"#808000\">blub</FONT></FONT></FONT></P>\n");
		buf.append("<OL>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#808000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5>abc</FONT></FONT></FONT></P>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#808000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5><I>def</I></FONT></FONT></FONT></P>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#808000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5><U>ghi</U></FONT></FONT></FONT></P>\n");
		buf.append("</OL>\n");
		buf.append("<UL>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#808000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5><B><SPAN STYLE=\"background: #4700b8\">das</SPAN></B></FONT></FONT></FONT></P>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#808000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5><B><SPAN STYLE=\"background: #4700b8\">ist</SPAN></B></FONT></FONT></FONT></P>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#ff0000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5><B>ein</B></FONT></FONT></FONT></P>\n");
		buf.append("	<LI><P STYLE=\"margin-bottom: 0cm\"><FONT COLOR=\"#ff0000\"><FONT FACE=\"Trebuchet MS, sans-serif\"><FONT SIZE=5>test</FONT></FONT></FONT></P>\n");
		buf.append("</UL>\n");
		buf.append("</BODY>\n");
		buf.append("</HTML>");*/
		
		//SwingUtilities.invokeLater(new Runnable() {
		//	public void run() {
				new Test();
		//	}
		//});
		/*StringBuffer buf = new StringBuffer();
		buf.append(Test.loadFile("./htmltest.htm"));

		HTMLTokenizer tok = new HTMLTokenizer(buf.toString());
		BBCTextField tf = new BBCTextField(null);
		HTMLParser p = new HTMLParser(tf._controller,tok.getTokens());
		System.out.println(p.convertToBBCode());*/
	}
	
	/*private static String loadFile(String filename) {
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(filename));
		}
		catch(FileNotFoundException e) {
			return "";
		}
		
		StringBuffer buffer = new StringBuffer();
		// read file
		try {
			String line;
			while((line = buf.readLine()) != null) {
				buffer.append(line + "\n");
			}
			buf.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
	}*/
}