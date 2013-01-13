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

package bbcodeeditor.control.tools;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * A tree-map for strings which maps strings to a value.<br>
 * The strings are saved in a tree-structure (for each character a node) so that the
 * access should be very fast
 * <p>
 * Note that this container has NO equals- and hashCode-method implemented so that
 * you should not add it to a container.
 * 
 * @author hrniels
 */
public final class StringTreeMap {
	
	/**
	 * the number of leafs
	 */
	private int _leafCount;

	/**
	 * the root-node
	 */
	private TreeNode _root;
	
	/**
	 * constructor
	 */
	public StringTreeMap() {
		clear();
	}

	/**
	 * @return the number of strings in the tree
	 */
	public int size() {
		return _leafCount;
	}
	
	/**
	 * determines if the tree is empty
	 * 
	 * @return true if the tree is empty
	 */
	public boolean isEmpty() {
		return _leafCount == 0;
	}
	
	/**
	 * Collects all entries in this container. Note that this may be a time-
	 * consuming process!
	 * 
	 * @return the map
	 */
	public Map getEntries() {
		Map m = new HashMap();
		getEntries(_root,new StringBuffer(),m);
		return m;
	}
	
	/**
	 * Collects all entries recursivly starting at the given TreeNode.
	 * 
	 * @param n the TreeNode
	 * @param buf the buffer with the string for this TreeNode
	 * @param m the map to fill
	 */
	private void getEntries(TreeNode n,StringBuffer buf,Map m) {
		if(n.getCharacter() > 0)
			buf.append(n.getCharacter());
		
		if(n.getValue() != null)
			m.put(buf.toString(),n.getValue());
		
		Map suc = n.getSuccessors();
		Iterator it = suc.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			getEntries((TreeNode)e.getValue(),buf,m);
		}
		
		if(n.getCharacter() > 0)
			buf.delete(buf.length() - 1,buf.length());
	}
	
	/**
	 * determines wether the given string does exist in this tree<br>
	 * Due to this class stores the strings in a tree this method should
	 * be very fast.
	 * 
	 * @param s the string to search for
	 * @return true if the string has been found
	 */
	public boolean contains(String s) {
		return get(s) != null;
	}
	
	/**
	 * determines wether all elements in the given collection do exist in this tree<br>
	 * Due to this class stores the strings in a tree this method should
	 * be very fast.<br>
	 * 
	 * @param c the collection
	 * @return true if all elements in the collection do exist here
	 */
	public boolean containsAll(Collection c) {
		if(c == null)
			throw new InvalidParameterException("c = null");
		
		Iterator it = c.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof String))
				return false;

			String s = (String)o;
			if(!contains(s))
				return false;
		}
		
		return true;
	}
	
	/**
	 * adds the given string with given value to the tree
	 * 
	 * @param s the string to add
	 * @param value the value of the string
	 * @return true if something has been added
	 */
	public boolean add(String s,Object value) {
		if(s == null)
			throw new InvalidParameterException("s = null");
		
		if(s.length() == 0)
			return false;
		
		if(add(_root,s,value,0)) {
			_leafCount++;
			return true;
		}
		
		return false;
	}
	
	/**
	 * adds the given string with given value recursivly to the tree
	 * 
	 * @param node the current node
	 * @param s the string to add
	 * @param value the value of the string
	 * @param pos the current position
	 * @return true if something has been added
	 */
	private boolean add(TreeNode node,String s,Object value,int pos) {
		char c = s.charAt(pos);
		TreeNode suc = node.getSuccessor(c);
		if(suc == null) {
			if(pos == s.length() - 1) {
				node.addSuccessor(c,value);
				return true;
			}
			
			suc = node.addSuccessor(c);
		}
		else if(pos == s.length() - 1) {
			// is there already a value?
			if(suc.getValue() != null && suc.getValue().equals(value))
				return false;
			
			// otherwise we set it
			suc.setValue(value);
			return true;
		}
		
		return add(suc,s,value,pos + 1);
	}
	
	/**
	 * adds all entries of the map to this tree<br>
	 * the keys of the map have to be strings!
	 * 
	 * @param m the map
	 * @return true if something has been added
	 */
	public boolean addAll(Map m) {
		if(m == null)
			throw new InvalidParameterException("m = null");
		
		boolean added = false;
		Iterator it = m.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			
			if(!(e.getKey() instanceof String))
				continue;

			String s = (String)e.getKey();
			if(add(s,e.getValue()))
				added = true;
		}
		
		return added;
	}
	
	/**
	 * determines the value of the given string.<br>
	 * Due to this class stores the strings in a tree this method should
	 * be very fast.
	 * 
	 * @param s the string to search for
	 * @return the value of this string
	 */
	public Object get(String s) {
		if(s == null)
			throw new InvalidParameterException("s = null");
		
		if(s.length() == 0)
			return null;
		
		TreeNode n = getNode(_root,s,0,true);
		if(n != null)
			return n.getValue();
		
		return null;
	}
	
	/**
	 * Checks wether the path to the given string exists. That means that
	 * there is at least one entry which has the given string as prefix.
	 *
	 * @param s the string
	 * @return true if so
	 */
	public boolean pathExists(String s) {
		if(s == null)
			throw new InvalidParameterException("s = null");
		
		if(s.length() == 0)
			return false;
		
		TreeNode n = getNode(_root,s,0,false);
		return n != null;
	}
	
	/**
	 * @return returns the root-node
	 */
	public TreeNode getRoot() {
		return _root;
	}
	
	/**
	 * The recursive implementation for get()<br>
	 * Gets the parent-node, the string to search for and the current position.<br>
	 * Checks wether the node has a successor with the character at the current position.
	 * If no has been found it returns null. If we have reached the end of the
	 * string it returns the node. Otherwise it calls this method recursivly with
	 * the next position and the successor-node
	 * 
	 * @param node the parent-node
	 * @param s the string to search for
	 * @param pos the current-position
	 * @param checkDeep if enabled we will take a look for other matches if we have
	 * 	found one. Because we want to given longer matches higher priority than
	 * 	shorter ones
	 * @return the node or null if not found
	 */
	public TreeNode getNode(TreeNode node,String s,int pos,boolean checkDeep) {
		char c = s.charAt(pos);
		TreeNode suc = node.getSuccessor(c);
		if(suc == null)
			return null;
		
		if(pos == s.length() - 1)
			return suc;
		
		// if we have found a value we have to continue the search because
		// we want to give longer smileys higher priority. Otherwise it would be
		// possible to match a smiley in a smiley.
		if(checkDeep && suc.getValue() != null) {
			TreeNode deepMatch = getNode(suc,s,pos + 1,checkDeep);
			if(deepMatch != null)
				return deepMatch;
			
			return suc;
		}
		
		return getNode(suc,s,pos + 1,checkDeep);
	}
	
	/**
	 * clears everything
	 */
	public void clear() {
		_leafCount = 0;
		_root = new TreeNode(null,(char)0);
	}
	
	/**
	 * removes the given string from the tree
	 * 
	 * @param s the string to remove
	 * @return true if the string has been removed
	 */
	public boolean remove(String s) {
		if(s == null)
			throw new InvalidParameterException("s = null");
		
		TreeNode node = getNode(_root,s,0,true);
		if(node == null)
			return false;
		
		boolean res = remove(node);
		if(res) {
			_leafCount--;
			return true;
		}
		
		return false;
	}
	
	/**
	 * removes the given node and (if necessary) the parent-nodes recursivly
	 * 
	 * @param node the node to remove
	 * @return true if something has been removed
	 */
	private boolean remove(TreeNode node) {
		TreeNode parent = node.getParent();
		if(parent == null)
			return false;
		
		parent.removeSuccessor(node);
		
		if(parent.size() == 1 && node.getValue() == null)
			remove(parent);
		
		return true;
	}
	
	/**
	 * removes all strings in the given Collection from this tree
	 * 
	 * @param c the Collection
	 * @return true if something has been removed
	 */
	public boolean removeAll(Collection c) {
		if(c == null)
			throw new InvalidParameterException("c = null");
		
		boolean removed = false;
		Iterator it = c.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof String))
				continue;

			String s = (String)o;
			if(remove(s))
				removed = true;
		}
		
		return removed;
	}
	
	public String toString() {
		return "StringTreeMap[" + _leafCount + "]";
	}
	
	/**
	 * the node for the tree<br>
	 * contains the character and a Map with successors
	 * 
	 * @author hrniels
	 */
	public static final class TreeNode {
		
		private final TreeNode _parent;
		
		/**
		 * the character of this node
		 */
		private final char _character;
		
		/**
		 * a Map with all successors of this node
		 */
		private final Map _successors = new HashMap();
		
		/**
		 * the value of this TreeNode
		 */
		private Object _value;
		
		/**
		 * constructor
		 * 
		 * @param parent the parent-node
		 * @param character the character of this node
		 */
		public TreeNode(TreeNode parent,char character) {
			this(parent,character,null);
		}
		
		/**
		 * constructor
		 * 
		 * @param parent the parent-node
		 * @param character the character of this node
		 * @param value the value of this node
		 */
		public TreeNode(TreeNode parent,char character,Object value) {
			_parent = parent;
			_character = character;
			_value = value;
		}
		
		public boolean equals(Object o) {
			if(!(o instanceof TreeNode))
				return false;
			
			if(o == this)
				return true;
			
			TreeNode tn = (TreeNode)o;
			return tn._character == _character;
		}
		
		public int hashCode() {
			return _character;
		}
		
		/**
		 * @return the number of successors
		 */
		public int size() {
			return _successors.size();
		}
		
		/**
		 * @return the parent TreeNode (null for the root-node)
		 */
		public TreeNode getParent() {
			return _parent;
		}
		
		/**
		 * @return the character of this node
		 */
		public char getCharacter() {
			return _character;
		}
		
		/**
		 * @return the value of this node (may be null)
		 */
		public Object getValue() {
			return _value;
		}
		
		/**
		 * sets the value of this node
		 * 
		 * @param val the new value
		 */
		public void setValue(Object val) {
			_value = val;
		}
		
		/**
		 * @return the map with all successors
		 */
		public Map getSuccessors() {
			return _successors;
		}
		
		/**
		 * determines the successor-node of the given char
		 * 
		 * @param c the character to look for
		 * @return the TreeNode or null if not found
		 */
		public TreeNode getSuccessor(char c) {
			TreeNode node = (TreeNode)_successors.get(new Character(c));
			return node;
		}
		
		/**
		 * adds the given character to the successor-map
		 * 
		 * @param c the character
		 * @return the created TreeNode
		 */
		public TreeNode addSuccessor(char c) {
			return addSuccessor(c,null);
		}
		
		/**
		 * adds the given character to the successor-map
		 * 
		 * @param c the character
		 * @param value the value of the node
		 * @return the created TreeNode
		 */
		public TreeNode addSuccessor(char c,Object value) {
			Character cc = new Character(c);
			TreeNode ex = (TreeNode)_successors.get(cc);
			if(ex == null) {
				TreeNode n = new TreeNode(this,c,value);
				_successors.put(cc,n);
				return n;
			}
			
			return ex;
		}
		
		/**
		 * removes the given node from the successor-list
		 * 
		 * @param node the node to remove
		 */
		public void removeSuccessor(TreeNode node) {
			removeSuccessor(node.getCharacter());
		}
		
		/**
		 * removes the given character from the successor-list
		 * 
		 * @param c the character to remove
		 */
		public void removeSuccessor(char c) {
			_successors.remove(new Character(c));
		}
	}
}