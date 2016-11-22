package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A Modal Dialog with filechooser support
 *
 */
public class ModalDialog extends StandardDialog
{

	/**
	 * Constructor
	 *
	 */
    public ModalDialog(String title)
    {
    	super(ComponentRegistry.getFrame());
    	this.setTitle(title);
        initActions();
    	initGUI();
    	this.setModal(true); // this dialog should be modal
    }
    
    /**
     * to be overriden by subclass
     *
     */
    protected void initGUI(){
    }
  
    /**
     * to be overriden by subclass
     *
     */
    protected void initActions(){
    }
    
    /**
     * 
     * Create a Text Field with a file chooser and returns the JPanel, the TextField and the Button component.
     * 
     * NOTE: The created file chooser will only select FILES, not directories, though you will be able to browse directories
     * @param dialogTitle dialof title for the file choose dialog
     * @param extDesc file extension description	
     * @return components element[0]:the panel (JPanel) with the textfield and the button
     * 			element[1]: the textfield (JTextField), element[2]: the file chooser button (JButton)
     */
    public ArrayList getTextFieldWithFileChooser(String dialogTitle, String extDesc, String fileExt){
    	// the panel
    	JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // the textfield
        JTextField inputTextField = new JTextField();
        inputTextField.setMaximumSize(new Dimension(1000, 20));
        inputTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(inputTextField);
        inputPanel.add(Box.createRigidArea(new Dimension(7, 0)));
        // button
        JButton inputButton = new JButton("...");
        inputButton.setMaximumSize(new Dimension(25, 20));
        inputButton.setPreferredSize(new Dimension(25, 20));
        FileChooserAction fileChooser = new FileChooserAction(dialogTitle,
        		extDesc,
        		fileExt,
        		inputTextField);
        inputButton.addActionListener(fileChooser);
        
        inputButton.setFocusPainted(false);
        inputPanel.add(inputButton);
        // prepare output
        ArrayList components = new ArrayList();
        components.add(inputPanel);
        components.add(inputTextField);
        components.add(inputButton);
        
        components.add(fileChooser );
        return components;
    }
 
}
