package com.stottlerhenke.simbionic.editor.gui;

import java.util.Vector;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * Override the standard SB_BindingsTable so that it doesn't work 
 * with the SB_Toolbar class.
 *
 */

public class SB_MultiBindingsTable extends SB_BindingsTable
{
	
	public SB_MultiBindingsTable(SimBionicEditor editor)
	{
		super(editor);
	}

	
	/**
	 * Override the default implementation which sets non-existant
	 * toolbar buttons (binding specific code).
	 * 
	 * Instead, set the autocomplete type
	 */
	protected void updateButtons() 
	{
		updateAutoComplete();
	}
	
	/**
	 * Override
	 */
	protected void updateSetValueButton() {
	}
	
	/**
	 * Set the autocomplete to match with Actions or Conditions 
	 * depending on the current selection.
	 *
	 */
	protected void updateAutoComplete()
	{
		int row = getSelectedRow();
		
		if(row >= 0)
		{
			if(SB_Binding.ACTION_BINDING.equals(this.getValueAt(row, 0)))
			{
				_expressionEditor._returnsValue = false;
			}
			else
			{
				_expressionEditor._returnsValue = true;
			}
		}
	}


	/**
	 * Add the ability to select an action for a void expression
	 * 
	 * @see com.stottlerhenke.simbionic.editor.gui.SB_BindingsTable#setBindings(com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism, java.util.Vector, boolean)
	 */
	protected void setBindings(SB_Polymorphism poly, Vector bindings, boolean insert)
	{
		super.setBindings(poly, bindings, insert);
		
		_comboBox.addItem(SB_Binding.ACTION_BINDING);
	}
	
	/**
	 * Insert an action into the bindings.
	 */
    protected void insertAction() 
    {
        Binding bindingModel = new Binding();
        bindingModel.setVar(SB_Binding.ACTION_BINDING);
        bindingModel.setExpr("");
        _bindings.add(new SB_Binding(bindingModel));
        revalidate();
        int row = _bindings.size() - 1;
        setRowSelectionInterval(row, row);
        repaint();
    }
	
}
