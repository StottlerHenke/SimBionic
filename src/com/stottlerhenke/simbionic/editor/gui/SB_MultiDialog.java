package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * This dialog allows the user to edit the bindings and actions inside
 * a compound action.
 *
 */

public class SB_MultiDialog extends StandardDialog
{
	protected final static String TITLE = "Edit Compound Action";
	
	protected AbstractAction _newAction;
	protected AbstractAction _newBinding;
	protected AbstractAction _setValue;
	protected AbstractAction _deleteAction;
	protected AbstractAction _upAction;
	protected AbstractAction _downAction;
	
	protected SB_MultiBindingsTable _bindingsTable;
	protected SimBionicEditor _editor;
	
	public SB_MultiDialog(Frame arg0, SimBionicEditor editor)
	{
		super(arg0);
		
		this.setTitle(TITLE);
		_editor = editor;
		
		initActions();
		initGui();
	}
	
	protected void initGui()
	{
		
		_bindingsTable = new SB_MultiBindingsTable(_editor);
		
		_bindingsTable.addListenerToVarCellEditor(new CellEditorListener() {
			public void editingCanceled(ChangeEvent e) {}
			
			public void editingStopped(ChangeEvent e) {
				_setValue.setEnabled(_bindingsTable.enableSetValueButton());
			}
		});

        _bindingsTable.addListenerToSelectionModel(
                new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) 
            {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;
                
                updateButtons();
            }
        });
        
		JScrollPane scrollPane = new JScrollPane(_bindingsTable);
		scrollPane.setPreferredSize(new Dimension(525, 175));

		ArrayList buttonList = new ArrayList();
		
		JButton button = new JButton(_newAction);
	    buttonList.add(button);
	    button = new JButton(_newBinding);
	    buttonList.add(button);
	    button = new JButton(_setValue);
	    buttonList.add(button);
	    button = new JButton(_deleteAction);
	    buttonList.add(button);
	    button = new JButton(_upAction);
	    buttonList.add(button);
	    button = new JButton(_downAction);
	    buttonList.add(button);
	    
	    JComponent buttonPanel = UIUtil.createButtonColumn(buttonList);
		
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BorderLayout());
	    
	    
	    mainPanel.add(scrollPane, BorderLayout.CENTER);
	    mainPanel.add(buttonPanel, BorderLayout.EAST);
	    
	    this.initDialog(mainPanel, null);
	    
	    //Center the dialog over the frame
	    Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
        this.setLocation(frameBounds.x + (frameBounds.width - getWidth()) / 2,
            frameBounds.y + (frameBounds.height - getHeight()) / 2);
	}
	
	protected void initActions()
	{
		_newAction = new AbstractAction("New Action")
        {
            public void actionPerformed(ActionEvent e)
            {
                onNewAction();
            }
        };
        _newAction.setEnabled(true);
        
        _newBinding = new AbstractAction("New Binding")
        {
            public void actionPerformed(ActionEvent e)
            {
                onNewBinding();
            }
        };
        _newBinding.setEnabled(true);

        _setValue = new AbstractAction("Set Value")
        {
        	public void actionPerformed(ActionEvent e) {
        		onSetValue();
        	}
        };
        
        _deleteAction = new AbstractAction("Delete")
        {
            public void actionPerformed(ActionEvent e)
            {
                onDelete();
            }
        };
        _deleteAction.setEnabled(true);
        
        _upAction = new AbstractAction("Up")
        {
            public void actionPerformed(ActionEvent e)
            {
                onUp();
            }

        };
        _upAction.setEnabled(true);
        
        _downAction = new AbstractAction("Down")
        {
            public void actionPerformed(ActionEvent e)
            {
                onDown();
            }
        };
        _downAction.setEnabled(true);
	}

	/**
	 * Update the delete/up/down buttons depending on the 
	 * current selection.
	 *
	 */
    protected void updateButtons() 
    {
        int row = _bindingsTable.getSelectedRow();

        _deleteAction.setEnabled(row >= 0);
        _upAction.setEnabled(row > 0);
        int size = _bindingsTable.getRowCount();
        _downAction.setEnabled(size > 0 && row != size - 1);
        _setValue.setEnabled(_bindingsTable.enableSetValueButton());
    }
    
    
    
	protected void onNewAction()
	{
		_bindingsTable.insertAction();
		
	}
	
	protected void onNewBinding()
	{
		_bindingsTable.insertBinding();
		
	}
	
	protected void onSetValue() {
		_bindingsTable.setVarValue();
	}

	protected void onDelete()
	{
		_bindingsTable.deleteBinding();
		updateButtons();
	}

	protected void onUp()
	{
		_bindingsTable.moveUp();
		
	}

	protected void onDown()
	{
		_bindingsTable.moveDown();
	}
	
	/**
	 * Set the compound action this dialog should display information for
	 * @param rect
	 */
	protected void setMultiRectangle(SB_Canvas canvas, SB_MultiRectangle rect)
	{
		_bindingsTable.setBindings(canvas._poly, rect.getBindings());
		updateButtons();
	}

	/**
	 * Stop any current editing
	 *
	 */
	protected void stopEditing()
	{
        TableCellEditor cellEditor = _bindingsTable.getCellEditor();
        if (cellEditor != null)
            cellEditor.stopCellEditing();
	}
	
	protected void onCancel()
	{
		stopEditing();
		
		super.onCancel();
	}

	protected void onOk()
	{
		stopEditing();
		
		super.onOk();
	}

	/**
	 * XXX: race conditions may invalidate this result;
	 * */
    public List<SB_Binding> getBindingsCopy() {
        return _bindingsTable.getBindingsCopy();
    }
	
	
	
}
