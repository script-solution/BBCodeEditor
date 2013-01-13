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

package bbcodeeditor.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bbcodeeditor.gui.international.Language;
import bbcodeeditor.gui.international.LanguageContainer;


/**
 * @author Assi Nilsmussen
 */
public class EditDialog extends JDialog {

	private static final long serialVersionUID = 2593452914988789307L;
	
	// the controls
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	private JTextArea descLabel;
	
	// the maps for the controls
	private HashMap _titles = new HashMap();
	private HashMap _controls = new HashMap();
	private HashMap _validations = new HashMap();
	
	// the dialog title
	private String _dialogTitle;
	
	// the description to display
	private String _description = "";
	
	// has ok been clicked?
	private boolean _okClicked = false;
	
	private Component _comp;
	
	/**
	 * constructor
	 * 
	 * @param comp the component to which the dialog-position should be relativ to
	 * @param parent the frame
	 * @param dialogTitle the title of this dialog
	 */
	public EditDialog(Component comp,Frame parent,String dialogTitle) {
		super(parent);
		
		_comp = comp;
		_dialogTitle = dialogTitle;
	}
	
	/**
	 * @return true if OK has been clicked
	 */
	public boolean okClicked() {
		return _okClicked;
	}
	
	/**
	 * determines the value of the control with given id
	 * 
	 * @param id the id of the control
	 * @return the value. null if not found
	 */
	public Object getValueOf(int id) {
		Object control = _controls.get(new Integer(id));
		if(control instanceof JTextField)
			return ((JTextField)control).getText();
		
		if(control instanceof JComboBox)
			return ((JComboBox)control).getSelectedItem();
		
		if(control instanceof JCheckBox)
			return new Boolean(((JCheckBox)control).isSelected());
		
		return null;
	}
	
	/**
	 * adds a textField with given title to the dialog
	 * 
	 * @param id the id to identify this control
	 * @param title the title of the control
	 * @param text the text of the textField
	 * @param disabled disable textfield?
	 */
	protected void addTextField(int id,String title,String text,boolean disabled) {
		_titles.put(new Integer(id),title);
		JTextField tf = new JTextField();
		tf.setText(text);
		tf.setEnabled(!disabled);
		tf.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					okButton.doClick();
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					cancelButton.doClick();
			}
		});
		_controls.put(new Integer(id),tf);
	}
	
	/**
	 * adds a checkbox with given title to the dialog
	 * 
	 * @param id the id to identify this control
	 * @param title the title of the checkbox
	 */
	protected void addCheckBox(int id,String title) {
		_titles.put(new Integer(id),"");
		JCheckBox cb = new JCheckBox(title);
		_controls.put(new Integer(id),cb);
	}
	
	/**
	 * adds the a comboBox with given title to the dialog
	 * 
	 * @param id the id to identify this control
	 * @param title the title of the control
	 * @param items the items of the comboBox
	 * @param selItem the selected item
	 */
	protected void addList(int id,String title,Object[] items,Object selItem) {
		_titles.put(new Integer(id),title);
		JComboBox cb = new JComboBox(items);
		cb.setSelectedItem(selItem);
		_controls.put(new Integer(id),cb);
	}
	
	/**
	 * Sets the description of this dialog
	 * 
	 * @param desc the new value
	 */
	protected void setDescription(String desc) {
		if(desc != null)
			_description = desc;
	}
	
	/**
	 * adds a validation for the given control-id
	 * 
	 * @param id the id of the control
	 * @param validation the validation object
	 */
	public void addValidation(int id,InputValidation validation) {
		_validations.put(new Integer(id),validation);
	}

	/**
	 * validates all controls and returns the error-message or an empty string
	 * 
	 * @return the error message or an empty string
	 */
	private String validateControls() {
		Iterator it = _validations.entrySet().iterator();
		while(it.hasNext()) {
			Entry e = (Entry)it.next();
			int id = ((Integer)e.getKey()).intValue();
			InputValidation iv = (InputValidation)e.getValue();
			String error = iv.validate(getValueOf(id));
			if(error.length() > 0)
				return error;
		}
		
		return "";
	}

	/**
	 * inits the components
	 */
	protected void initLayout() {
		setTitle(_dialogTitle);
		setModal(true);
		
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		buttonBar = new JPanel();
		descLabel = new JTextArea();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			dialogPane.setLayout(new BorderLayout());
			
			if(_description.length() > 0) {
				//======== descPanel ========
				{
					descLabel.setText( _description);
					descLabel.setLineWrap(true);
					descLabel.setMargin(new Insets(5,0,5,0));
					// for some reason we have to create a new color with the property-color
					// otherwise this does not seem to work :/
					Color bg = new Color(UIManager.getColor("Label.background").getRGB());
					Color fg = new Color(UIManager.getColor("Label.foreground").getRGB());
					descLabel.setForeground(fg);
					descLabel.setBackground(bg);
					descLabel.setFocusable(false);
					dialogPane.add(descLabel,BorderLayout.NORTH);
				}
			}

			//======== contentPanel ========
			{
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
				((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
				((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
				((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

				Iterator it = _titles.entrySet().iterator();
				for(int i = 0;it.hasNext();i++) {
					Entry e = (Entry)it.next();
					
					String title = (String)e.getValue();
					JComponent comp = (JComponent)_controls.get(e.getKey());
					
					//---- label1 ----
					if(title.length() > 0) {
						JLabel lbl = new JLabel(title);
						lbl.setPreferredSize(new Dimension(100,0));
						contentPanel.add(lbl, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, i == 0 ? 5 : 0, 5), 0, 0));
					}
					comp.setPreferredSize(new Dimension(200,comp.getPreferredSize().height));
					contentPanel.add(comp, new GridBagConstraints(title.length() > 0 ? 1 : 0, i,
							title.length() > 0 ? 1 : 2, 1, 0.0, 0.0,GridBagConstraints.CENTER,
							GridBagConstraints.BOTH,new Insets(0, 0, i == 0 ? 5 : 0,
									title.length() > 0 ? 0 : 5), 0, 0));
				}
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setLayout(new FlowLayout(FlowLayout.RIGHT,0,5));

				//---- okButton ----
				okButton.setText(LanguageContainer.getText(Language.GUI_DIALOG_OK));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						_okClicked = true;
						String error = validateControls();
						if(error.length() > 0) {
							JOptionPane.showMessageDialog(_comp,error,"Error",JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						setVisible(false);
					}
				});
				buttonBar.add(okButton);

				//---- cancelButton ----
				cancelButton.setText(LanguageContainer.getText(Language.GUI_DIALOG_CANCEL));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				buttonBar.add(cancelButton);
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);

		// we have to pack it twice because otherwise the size of the textarea
		// can't be determined correctly 
		pack();
		pack();
		setLocationRelativeTo(_comp);
	}
}