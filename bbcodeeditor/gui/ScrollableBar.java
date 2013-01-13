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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The main-class of the scrollable bar
 */
public class ScrollableBar extends JComponent implements SwingConstants,MouseListener,
		ChangeListener,PropertyChangeListener {

	private static final long serialVersionUID = 7361404797047546756L;
	
	private Component comp;
  private boolean horizontal;
  private int inc;
  
	private JViewport scroll;
	private JButton scrollF;
	private JButton scrollB;
	private boolean pressed = false;
	
  /**
   * Constructor
   * 
   * @param comp the component for the scrollable bar
   */
  public ScrollableBar(Component comp) {
    this(comp, HORIZONTAL);
  }

  /**
   * Constructor
   * 
   * @param comp the component for the scrollable bar
   * @param orientation the orientation. See SwingConstants.
   */
  public ScrollableBar(Component comp,int orientation) {
    this.comp = comp;
    if(orientation == HORIZONTAL)
      horizontal = true;
    else
      horizontal = false;
    
    // Scroll width in pixels.
    inc = 6;
    
    inc = getIncrement();

		// Create the Buttons
		int sbSize = ((Integer)(UIManager.get("ScrollBar.width"))).intValue();
		scrollB = createButton(isHorizontal() ? WEST : NORTH,sbSize);
		scrollB.setVisible(false);
		scrollB.addMouseListener(this);
		scrollB.setEnabled(false);

		scrollF = createButton(isHorizontal() ? EAST : SOUTH,sbSize);
		scrollF.setVisible(false);
		scrollF.addMouseListener(this);

		int axis = isHorizontal() ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS;
		setLayout(new BoxLayout(this,axis));

		scroll = new JViewport() {
			
			private static final long serialVersionUID = -6360466361119148797L;

			// ... "see source code"
			// Create a customized layout manager
			protected LayoutManager createLayoutManager() {
				return new ViewportLayout() {
					
					private static final long serialVersionUID = -5297684897539418936L;

					public Dimension minimumLayoutSize(Container parent) {
						Component view = ((JViewport)parent).getView();
						if(view == null)
							return new Dimension(4,4);

						Dimension d = view.getPreferredSize();
						if(isHorizontal())
							return new Dimension(4,(int)d.getHeight());
						
						return new Dimension((int)d.getWidth(),4);
					}
				};
			}
		};

		Component box = getComponent();
		scroll.setView(box);

		add(scrollB);
		add(scroll);
		add(scrollF);

		// Install the change listeners
		scroll.addChangeListener(this);
		addPropertyChangeListener(this);
  }

	protected JButton createButton(int direction,int width) {
		// NOTE: no support for vertical bars!
		String image;
		if(direction == WEST)
			image = "./images/arrow_left.png";
		else
			image = "./images/arrow_right.png";

		JButton button = new JButton(new ImageIcon(Helper.getFileInDocumentBase(image)));
		button.setPreferredSize(new Dimension(width,width - 1));
		return button;
	}

  /**
   * @return the component
   */
  public Component getComponent() {
    return comp;
  }

  /**
   * Sets the component of this scrollable bar
   * 
   * @param comp the component
   */
  public void setComponent(Component comp) {
    if(this.comp != comp) {
      Component old = this.comp;
      this.comp = comp;
      firePropertyChange("component",old,comp);
    }
  }
  
  /**
   * @return the current value of increment
   */
  public int getIncrement() {
    return inc;
  }

  /**
   * Sets the increment for the scrollbar
   * 
   * @param inc the increment-value
   */
  public void setIncrement(int inc) {
    if(inc > 0 && inc != this.inc) {
      int old = this.inc;
      this.inc = inc;
      firePropertyChange("increment",old,inc);
    }
  }

  /**
   * @return wether this bar is horizontal
   */
  public boolean isHorizontal() {
    return horizontal;
  }

	public void propertyChange(PropertyChangeEvent evt) {
		if("increment".equals(evt.getPropertyName())) {
			inc = ((Integer)evt.getNewValue()).intValue();
		}
		else if("component".equals(evt.getPropertyName())) {
			scroll.setView((Component)evt.getNewValue());
		}
	}

	public void stateChanged(ChangeEvent e) {
		boolean cond = isHorizontal() ?
				getWidth() < scroll.getViewSize().width : getHeight() < scroll.getViewSize().height;
		if(cond) {
			scrollB.setVisible(true);
			scrollF.setVisible(true);
		}
		else {
			scrollB.setVisible(false);
			scrollF.setVisible(false);
			doLayout();
		}
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		pressed = false;
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}

	public void mousePressed(MouseEvent e) {
		pressed = true;
		final Object o = e.getSource();
		Thread scroller = new Thread(new Runnable() {

			public void run() {
				int accl = 200;
				while(pressed) {
					Point p = scroll.getViewPosition();
					// ... "Compute new view position"
					if(isHorizontal()) {
						if(o == scrollB) {
							scrollF.setEnabled(true);
							p.x -= inc;
							if(p.x < 0) {
								p.x = 0;
								scrollB.setEnabled(false);
								scroll.setViewPosition(p);
								return;
							}
						}
						else {
							scrollB.setEnabled(true);
							if(scroll.getViewSize().width - p.x - scroll.getExtentSize().width > inc) {
								p.x += inc;
							}
							else {
								p.x = scroll.getViewSize().width - scroll.getExtentSize().width;
								scrollF.setEnabled(false);
								scroll.setViewPosition(p);
								return;
							}
						}
					}
					else {
						if(o == scrollB) {
							p.y -= inc;
							if(p.y < 0) {
								p.y = 0;
								scroll.setViewPosition(p);
								return;
							}
						}
						else {
							if(scroll.getViewSize().height - p.y - scroll.getExtentSize().height > inc) {
								p.y += inc;
							}
							else {
								p.y = scroll.getViewSize().height - scroll.getExtentSize().height;
								scroll.setViewPosition(p);
								return;
							}
						}
					}
					// ...
					scroll.setViewPosition(p);
					try {
						Thread.sleep(accl);
						if(accl <= 10)
							accl = 10;
						else
							accl /= 2;
					}
					catch(InterruptedException ie) {
						
					}
				}
			}
		});
		scroller.start();
	}
}