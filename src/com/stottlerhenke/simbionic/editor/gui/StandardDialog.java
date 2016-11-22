package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * This is an enhanced version of the basic Swing dialog class
 * that provides common desirable functionality:
 * <ul>
 *  <li>OK and Cancel buttons
 *  <li>Close on ESC
 * </ul>
 */
public class StandardDialog extends JDialog
{
    /** label for the Ok button */
    protected static String LABEL_OK_BUTTON = "Ok";
    /** label for the Cancel button */
    protected static String LABEL_CANCEL_BUTTON = "Cancel";
    
    protected JPanel pane;
    protected JButton _okButton;
    /** tracks whether the OK button was clicked to close the dialog */
    protected boolean _okClicked = false;
    
    /**
     * Constructor
     *
     */
    public StandardDialog()
    {
    	super();
    }
    
    public StandardDialog(Frame owner)
    {
        super(owner);
    }
    
    /**
     * Constructor
     * @param owner
     */
    public StandardDialog(Dialog owner)
    {
    	super(owner);
    }
    
    /**
     * Sets up a standard dialog with the given content pane centered above 
     * a standard row of OK/Cancel buttons.  It will also ensure that the dialog
     * is large enough to display its entire current title.
     * @param content the content to display above the standard buttons
     * @param parent the parent window; if null, dialog will be centered on screen
     */
    public void initDialog(JComponent content,JComponent parent)
    {
        initDialog(content,parent,true);
    }
    
    /**
     * Sets up a standard dialog with the given content pane centered above 
     * a standard row of OK/Cancel buttons.  It will also ensure that the dialog
     * is large enough to display its entire current title.
     * @param content the content to display above the standard buttons
     * @param parent the parent window; if null, dialog will be centered on screen
     * @param showCancel a Cancel button is included iff this is true
     */
    public void initDialog(JComponent content,JComponent parent,boolean showCancel)
    {
        addCancelByEscapeKey();
        
        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        content.setAlignmentX((float) 0.5); // make sure content is centered
        
        pane.add(content);
        pane.add(getStandardButtons(showCancel));        
        getContentPane().add(pane);
        pack();

        setLocationRelativeTo(parent);
    }
    
    /**
     * The action taken when the user hits the OK button.
     * Typically this will involve processing any inputs made
     * in the dialog and then calling setVisible(false). The
     * invoking method can determine if the dialog was closed
     * by clicking OK by calling wasOkClicked().
     */
    protected void onOk()
    {
        _okClicked = true;
        setVisible(false);
    }
    
    /**
     * The action taken when the user hits the OK button.
     * Typically this will involve processing any inputs made
     * in the dialog and then calling setVisible(false). The
     * invoking method can determine if the dialog was closed
     * by clicking OK by calling wasOkClicked().
     */
    protected void onCancel()
    {
        _okClicked = false;
        dispose();
    }
    
    /**
     * @return true if the dialog was closed by clicking OK, false otherwise
     */
    public boolean wasOkClicked()
    {
        return _okClicked;
    }
    
    /**
     * Returns a standard row of Ok/Cancel buttons.
     * @param showCancel a Cancel button will be displayed iff this is true
     * @return 
     */
    protected JComponent getStandardButtons(boolean showCancel) 
    {
        List buttons = new ArrayList();

        _okButton = new JButton(LABEL_OK_BUTTON);
        _okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                onOk();
            }
        });
        getRootPane().setDefaultButton(_okButton);
        buttons.add(_okButton);
        
        if (showCancel) {
            JButton cancel = new JButton(LABEL_CANCEL_BUTTON);
            cancel.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    onCancel();
                }
            });
            buttons.add(cancel);
        }
        
        return UIUtil.createButtonRow(buttons);
    }
    
    /**
     * Wires the dialog to close when the ESC key is pressed.
     */
    protected void addCancelByEscapeKey()
    {
        String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
        int noModifiers = 0;
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, noModifiers, false);
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(escapeKey, CANCEL_ACTION_KEY);
        AbstractAction cancelAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                onCancel();
            }
        }; 
        getRootPane().getActionMap().put(CANCEL_ACTION_KEY, cancelAction);
    }

    public void setVisible(boolean b)
    {
        if (b)
            _okClicked = false;
        super.setVisible(b);
    }
    
    /**
     * Set the background color of the main box layout.
     * 
     * @param c
     */
    public void setBackgroundColor(Color c)
    {
    	super.setBackground(c);
    	
    	pane.setBackground(c);
    }
}
