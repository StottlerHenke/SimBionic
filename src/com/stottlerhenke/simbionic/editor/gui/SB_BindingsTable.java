package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;

/**
 * UI for the list of bindings.
 */
public class SB_BindingsTable extends JTable {

    protected SimBionicEditor _editor;

    protected Vector _bindings;

    protected JComboBox _comboBox = new JComboBox();
    protected DefaultCellEditor _varCellEditor;
    
    protected SB_Autocomplete _expressionEditor;

    protected String _setValueType;
    
    public SB_BindingsTable(SimBionicEditor editor) {
        _editor = editor;
        
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(21);

        ComboBoxRenderer renderer = new ComboBoxRenderer();
        //renderer.setPreferredSize(new Dimension(200, 130));
        _comboBox.setRenderer(renderer);
        //_comboBox.setMaximumRowCount(3);
        _comboBox.setFont(getFont());

        CellEditorListener listener = new CellEditorListener(){
        	public void editingCanceled(ChangeEvent e){}
            //This tells the listeners the editor has canceled editing
        	public void editingStopped(ChangeEvent e){
        		updateSetValueButton();
        	}
            //This tells the listeners the editor has ended editing
        };
        
        _varCellEditor =  new DefaultCellEditor(_comboBox);       
        _varCellEditor.addCellEditorListener(listener);
        
        _expressionEditor = _editor.createAutocomplete();
        DefaultCellEditor exprCellEditor = new DefaultCellEditor(_expressionEditor);
        exprCellEditor.addCellEditorListener(listener);
        setDefaultEditor(String.class, exprCellEditor);
    }

    protected void setBindings(SB_Polymorphism poly, Vector bindings,
            boolean insert) {
        _bindings = copyBindings(bindings);

        _comboBox.removeAllItems();
        // add locals
        DefaultMutableTreeNode locals = poly.getLocals();
        int size = locals.getChildCount();
        for (int i = 0; i < size; ++i) {
            SB_Variable local = (SB_Variable) ((DefaultMutableTreeNode) locals
                    .getChildAt(i)).getUserObject();
            _comboBox.addItem(local.getName());
        }
        // add parameters
        SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        DefaultMutableTreeNode params = catalog.findNode(poly._parent,
                catalog._behaviors);
        size = params.getChildCount();
        for (int i = 0; i < size; ++i) {
            SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params
                    .getChildAt(i)).getUserObject();
            _comboBox.addItem(param.getName());
        }
        // add globals
        DefaultMutableTreeNode globals = catalog._globals;
        size = globals.getChildCount();
        for (int i = 0; i < size; ++i) {
            SB_Global global = (SB_Global) ((DefaultMutableTreeNode) globals
                    .getChildAt(i)).getUserObject();
            _comboBox.addItem(global.getName());
        }
        
        if (insert) {
           Binding bindingModel = new Binding();
           bindingModel.setVar((String)_comboBox.getItemAt(0));
           bindingModel.setExpr("");

           _bindings.add(new SB_Binding(bindingModel));

        }

        setModel(new SB_TableModel());
        getColumnModel().getColumn(0).setPreferredWidth(50);
        getColumnModel().getColumn(1).setPreferredWidth(250);
        // TODO
        
        getColumnModel().getColumn(0).setCellEditor(_varCellEditor);

        if (_bindings.size() > 0 ) {
            SB_ToolBar toolBar = (SB_ToolBar) ComponentRegistry.getToolBar();
            
            if(toolBar._varComboBox.isEnabled())
            {
	            int index = toolBar._varComboBox.getSelectedIndex();
	            if (insert && _bindings.size() == 1) index = 0;
	            setRowSelectionInterval(index, index);
            }
        }
        updateButtons();
        ListSelectionModel rowSM = getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;
                updateButtons();
            }
        });
    }

    public static Vector copyBindings(Vector bindings) {
        Vector copy = new Vector();
        int size = bindings.size();
        for (int i = 0; i < size; ++i) {
            SB_Binding binding = (SB_Binding) bindings.get(i);
            Binding bindingModel = new Binding();
            bindingModel.setVar(new String(binding.getVar()));
            bindingModel.setExpr(new String(binding.getExpr()));
            copy.add(new SB_Binding(bindingModel));
        }
        return copy;
    }

    protected void insertBinding() {
        Binding bindingModel = new Binding();
        bindingModel.setVar((String) _comboBox.getItemAt(0));
        bindingModel.setExpr("");
        _bindings.add(new SB_Binding(bindingModel));
        revalidate();
        int row = _bindings.size() - 1;
        setRowSelectionInterval(row, row);
        repaint();
    }

    protected void deleteBinding() {
        int row = getSelectedRow();
        if (row < 0) return;
        _bindings.remove(row);
        revalidate();
        if (row != 0 && row == _bindings.size())
                setRowSelectionInterval(row - 1, row - 1);
        if (_bindings.isEmpty()) clearSelection();
        updateButtons();
        repaint();
    }

    protected void moveUp() {
        int row = getSelectedRow();
        if (row <= 0) return;
        _bindings.add(row - 1, _bindings.remove(row));
        setRowSelectionInterval(row - 1, row - 1);
        revalidate();
        repaint();
    }

    protected void moveDown() {
        int row = getSelectedRow();
        if (row == _bindings.size() - 1) return;
        _bindings.add(row + 1, _bindings.remove(row));
        setRowSelectionInterval(row + 1, row + 1);
        revalidate();
        repaint();
    }
    
    protected DefaultCellEditor getVarCellEditor() {
    	return _varCellEditor;
    }

    protected void setVarValue(){
    	final int row = getSelectedRow();
        if (row < 0 || row>=_bindings.size()) return;
        
        if (isEditing()) 
        	getCellEditor().stopCellEditing();
        
        // since currently the Dialog does not allow
        // switching between the two (table and array) type of dialog
        // we need one for each
        I_ExpressionEditor setValueEditor = getSetValueCustomEditor();
        if (setValueEditor != null) {
        	setValueEditor.editObject(((SB_Binding)_bindings.get(row)).getExpr(), new I_EditorListener() {
        		public void editingCanceled(I_ExpressionEditor source) {}
        		
        		public void editingCompleted(I_ExpressionEditor source, String result) {
        			((SB_Binding)_bindings.get(row)).setExpr(result);
        			repaint();
        		}
        	});
        } else {
        	editCellAt(row, 1);
        	getEditorComponent().requestFocus();
        }
    }
    
    protected void setValueType(String valueType) {
    	_setValueType = valueType;
    }
    
    protected I_ExpressionEditor getSetValueCustomEditor() {
    	int row = getSelectedRow();
    	SB_TypeManager typeManager = ComponentRegistry.getProjectBar().getTypeManager();
        return _editor.getEditorRegistry().getExpressionEditor(
        		EditorRegistry.EXPRESSION_TYPE_BINDING,
        		_setValueType,
        		//typeManager.getTypeName(SB_VarType.getTypeFromInt(_setValueType)), 
        		((SB_Binding)_bindings.get(row)).getExpr());
    }
    
   
    
    /**
     * update the display of the button so that they are enabled or disabled appropriately
     *
     */
    protected void updateButtons() {
        int row = getSelectedRow();
        SB_ToolBar toolBar = ComponentRegistry.getToolBar();
        toolBar._deleteButton.setEnabled(row >= 0);
        toolBar._moveUpButton.setEnabled(row > 0);
        int size = _bindings.size();
        toolBar._moveDownButton.setEnabled(size > 0 && row != size - 1);
        updateSetValueButton();
    }
    
    protected void updateSetValueButton() {
    	ComponentRegistry.getToolBar()._setValueButton.setEnabled(enableSetValueButton());
    }

    // TODO: the combobox at the toolbar has more element than the binding.
    /**
     * update the display of 'Set Value' button so that it is enabled only when
     * the variable is of type array or table
     */
    public boolean enableSetValueButton(){
  
        int row = getSelectedRow();
        SB_ToolBar toolBar = ComponentRegistry.getToolBar();
        if (row<0) {
        	return false;
        } else if(_bindings.size()<=row){
        	return false;
        } else {
            /* if(toolBar._varComboBox.isEnabled())
            {
                    int size = holder.getBindingCount();
                    if (_varComboBox.getSelectedItem().equals("Insert Binding..."))
            }*/
        	String varName = ((SB_Binding)_bindings.get(row)).getVar();         	
            if (row != -1)
            {
                _setValueType = null;
                // check local variable (code similar to SetBinding)
            	SB_Polymorphism poly = toolBar.getTabbedCanvas().getActiveCanvas()._poly;
                DefaultMutableTreeNode locals = poly.getLocals();
                int localSize = locals.getChildCount();
                for(int k=0;k<localSize;k++){
                	SB_Variable local = (SB_Variable) ((DefaultMutableTreeNode) locals.getChildAt(k)).getUserObject();
                	if(local.getName().equals(varName)){
                		_setValueType = local.getType();
                	}
                }
                // add parameters
                SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
                SB_Catalog catalog = projectBar._catalog;
                DefaultMutableTreeNode params = catalog.findNode(poly._parent, catalog._behaviors);
                int paramSize = params.getChildCount();
                for (int i = 0; i < paramSize; ++i) {
                    SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params.getChildAt(i)).getUserObject();
                    if(param.getName().equals(varName)){
                    	_setValueType = param.getType();
                    }
                }
                // add globals
                DefaultMutableTreeNode globals = catalog._globals;
                int GlobalSize = globals.getChildCount();
                for (int i = 0; i < GlobalSize; ++i) {
                    SB_Global global = (SB_Global) ((DefaultMutableTreeNode) globals.getChildAt(i)).getUserObject();
                    if(global.getName().equals(varName)){
                    	_setValueType = global.getType();
                    }
                }
                if (getSetValueCustomEditor() != null) {
                	return true;
                }
            }
        }
        return false;
    }
    
    class SB_TableModel extends AbstractTableModel {

        final String[] columnNames = { "Variable", "Expression"};

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            if (_bindings != null)
                return _bindings.size();
            else
                return 0;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            SB_Binding binding = (SB_Binding) _bindings.get(row);
            if (col == 0)
                return binding.getVar();
            else
                return binding.getExpr();
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            if (_bindings.size() > row){
            SB_Binding binding = (SB_Binding) _bindings.get(row);
            if (col == 0) {
                if (value instanceof SB_Variable)
                    binding.setVar(((SB_Variable) value).getName());
                else if (value instanceof String)
                        binding.setVar((String) value);
            } else
                binding.setExpr((String) value);
            fireTableCellUpdated(row, col);
            }else{
                int i=0;
                int j=2;
            }
        }
    }

    class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            //setHorizontalAlignment(CENTER);
            //setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
         */
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text. If icon was null, say so.
            String varName = (String) value;
            // RTH icons removed for now to fix combo box selection problem
//            Icon icon = var.getIcon();
            String text = varName;
//            setIcon(icon);
//            if (icon != null) {
                setText(text);
                setFont(list.getFont());
//            } else {
//                setUhOhText(text + " (no image available)", list.getFont());
//            }

            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
    }

}