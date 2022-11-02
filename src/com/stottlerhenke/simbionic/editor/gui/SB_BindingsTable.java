package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;

/**
 * UI for the list of bindings.
 */
@SuppressWarnings("serial")
public class SB_BindingsTable extends JTable {

    protected SimBionicEditor _editor;

    private List<SB_Binding> _bindings;

    protected JComboBox<String> _comboBox = new JComboBox<>();

    private final TableCellEditor _varCellEditor;

    protected SB_Autocomplete _expressionEditor;

    /**
     * XXX: This list of listeners is used to handle the generation of a new
     * SB_BindingsTableModel every time {@link
     * #setBindings(SB_Polymorphism, List) setBindings} is called.
     * */
    private final List<TableModelListener> _tableModelListeners
    = new ArrayList<>();

    public SB_BindingsTable(SimBionicEditor editor) {
        _editor = editor;
        
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(21);

        ComboBoxRenderer renderer = new ComboBoxRenderer();
        //renderer.setPreferredSize(new Dimension(200, 130));
        _comboBox.setRenderer(renderer);
        //_comboBox.setMaximumRowCount(3);
        _comboBox.setFont(getFont());

        _varCellEditor = new DefaultCellEditor(_comboBox);

        _expressionEditor = _editor.createAutocomplete();
        TableCellEditor exprCellEditor
        = new DefaultCellEditor(_expressionEditor);
        setDefaultEditor(String.class, exprCellEditor);
    }

    protected void setBindings(SB_Polymorphism poly, List<SB_Binding> bindings) {
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
        List<SB_Global> globalList = catalog.getAllGlobals();
        for (SB_Global global : globalList) {
            _comboBox.addItem(global.getName());
        }

        setModel(genNewTableModel());
        getColumnModel().getColumn(0).setPreferredWidth(50);
        getColumnModel().getColumn(1).setPreferredWidth(250);

        getColumnModel().getColumn(0).setCellEditor(_varCellEditor);

    }

    /**
     * XXX: 2018-05-22 -jmm
     * <br>
     * It seems that all of the implementing classes of interface {@link
     * SB_BindingsHolder} naively use the provided list. Moreover, the classes
     * appear to assume that the SB_Binding objects in the list are also
     * not referenced elsewhere.
     * @return a deep copy {@link #_bindings}, using {@link #copyBindings(List)
     * copyBindings}
     * */
    List<SB_Binding> getBindingsCopy() {
        return copyBindings(_bindings);
    }

    void clearBindings() {
        _bindings = new Vector<>();
        setModel(new SB_BindingsTableModel(_bindings));
    }

    static List<SB_Binding> copyBindings(List<SB_Binding> bindings) {
        List<SB_Binding> copy = new Vector<>();
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

    /**
     * A convenience method that generates a new table model with listeners
     * added.
     * */
    private SB_BindingsTableModel genNewTableModel() {
        SB_BindingsTableModel newTableModel
        = new SB_BindingsTableModel(_bindings);
        _tableModelListeners.forEach(l -> newTableModel
                .addTableModelListener(l));
        return newTableModel;
    }

    /**
     * XXX:
     * A convenience method that casts the current table model to its expected
     * static type. Inheritance makes this somewhat tricky;
     * */
    private AbstractTableModel getCurrentTableModel() {
        return (AbstractTableModel) getModel();
    }

    /**
     * XXX: Attempt to fail early before other calls that assume more specific
     * TableModel type.
     * {@inheritDoc}
     * */
    @Override
    public void setModel(TableModel model) {
        AbstractTableModel castAttempt = (AbstractTableModel) model;
        super.setModel(castAttempt);
    }

    protected void insertBinding() {
        insertBinding(_comboBox.getItemAt(0), "");
    }

    protected void insertBinding(String varValue, String expr) {
        Binding bindingModel = new Binding();
        bindingModel.setVar(varValue);
        bindingModel.setExpr(expr);
        _bindings.add(new SB_Binding(bindingModel));
        revalidate();
        int row = _bindings.size() - 1;
        getCurrentTableModel().fireTableDataChanged();
        setRowSelectionInterval(row, row);
        repaint();
    }

    protected void deleteBinding() {
        int row = getSelectedRow();
        if (row < 0) return;
        _bindings.remove(row);
        getCurrentTableModel().fireTableDataChanged();
        revalidate();
        if (row != 0 && row == _bindings.size())
            setRowSelectionInterval(row - 1, row - 1);
        else if (row < _bindings.size()) {
            setRowSelectionInterval(row, row);
        }
        if (_bindings.isEmpty()) clearSelection();
        repaint();
    }

    protected void moveUp() {
        int row = getSelectedRow();
        if (row <= 0) return;
        _bindings.add(row - 1, _bindings.remove(row));
        getCurrentTableModel().fireTableDataChanged();
        setRowSelectionInterval(row - 1, row - 1);
        revalidate();
        repaint();
    }

    protected void moveDown() {
        int row = getSelectedRow();
        if (row == _bindings.size() - 1) return;
        _bindings.add(row + 1, _bindings.remove(row));
        getCurrentTableModel().fireTableDataChanged();
        setRowSelectionInterval(row + 1, row + 1);
        revalidate();
        repaint();
    }

    void addListenerToVarCellEditor(CellEditorListener l) {
        _varCellEditor.addCellEditorListener(l);;
    }

    protected void addListenerToSelectionModel(ListSelectionListener l) {
        getSelectionModel().addListSelectionListener(l);
    }

    /**
     * Registers a listener that will be added to every TableModel generated by
     * SB_BindingsTable
     * <br>
     * TODO: Transform SB_BindingsTable is a JTable relationship into
     * SB_BindingsTable contains a JTable to better handle locking down the
     * ability to set arbitrary table models for SB_BindingsTable.
     * */
    void addListenerForTableModel(TableModelListener l) {
        _tableModelListeners.add(l);
    }

    protected void setVarValue(){
    	final int row = getSelectedRow();
        if (row < 0 || row>=_bindings.size()) return;
        
        if (isEditing()) 
        	getCellEditor().stopCellEditing();

        SB_Binding binding = _bindings.get(row);
        String varName = binding.getVar();
        String typeName = getTypeForVariableName(varName)
                .orElseThrow(() -> new RuntimeException("It is assumed that the"
                        + " type for variable " + varName
                        + " is known by this point."));

        // since currently the Dialog does not allow
        // switching between the two (table and array) type of dialog
        // we need one for each
        I_ExpressionEditor setValueEditor = getSetValueCustomEditor(typeName);
        if (setValueEditor != null) {
            setValueEditor.editObject(binding.getExpr(), new I_EditorListener() {
        		public void editingCanceled(I_ExpressionEditor source) {}
        		
        		public void editingCompleted(I_ExpressionEditor source, String result) {
        			binding.setExpr(result);
        			repaint();
        		}
        	});
        } else {
        	editCellAt(row, 1);
        	getEditorComponent().requestFocus();
        }
    }

    protected I_ExpressionEditor getSetValueCustomEditor(String setValueType) {
        int row = getSelectedRow();
        return _editor.getEditorRegistry().getExpressionEditor(
        		EditorRegistry.EXPRESSION_TYPE_BINDING,
        		setValueType,
        		//typeManager.getTypeName(SB_VarType.getTypeFromInt(_setValueType)), 
        		((SB_Binding)_bindings.get(row)).getExpr());
    }

    // TODO: the combobox at the toolbar has more element than the binding.
    /**
     * update the display of 'Set Value' button so that it is enabled only when
     * the variable is of type array or table
     */
    public boolean enableSetValueButton(){
  
        final int row = getSelectedRow();
        if (row<0) {
        	return false;
        } else if(_bindings.size()<=row){
        	return false;
        } else {
            String varName = _bindings.get(row).getVar();
            //XXX: May not handle null varName well, but no check is done to
            //preserve old behavior.
            return getTypeForVariableName(varName)
                    .map(type -> getSetValueCustomEditor(type) != null)
                    .orElse(false);
        }
    }

    /**
     * Looks for the name of 
     * 
     * This reproduces the behavior of the old implementation of
     * {@link #enableSetValueButton()}, where globals shadow parameters and
     * parameters shadow local variables. This behavior may not be desirable.
     * */
    private Optional<String> getTypeForVariableName(String varName) {

        //XXX: ComponentRegistry items
        SB_Catalog catalog = ComponentRegistry.getProjectBar()._catalog;
        SB_Polymorphism poly = ComponentRegistry.getContent()
                .getActiveCanvas()._poly;

        DefaultMutableTreeNode locals = poly.getLocals();
        Optional<SB_Variable> foundLocal
        = getChildVariableWithMatchingName(locals, varName);

        DefaultMutableTreeNode params = catalog.findNode(poly._parent,
                catalog._behaviors);
        Optional<SB_Variable> foundParam
        = getChildVariableWithMatchingName(params, varName);

        Optional<SB_Global> foundGlobal = catalog.getAllGlobals().stream()
                .filter(global -> global.getName().equals(varName))
                //XXX: reduce used to simulate "find last" of former behavior.
                .reduce((a, b) -> b);

        //XXX: Reproduces old implementation by checking globals, then params,
        //then locals, while traversing all elements of all three.
        return Stream.of(foundGlobal, foundParam, foundLocal)
                //XXX: Clearer in Java 9, where Optional#stream provides
                //a single method to make a stream out of the contents
                //of an Optional.
                .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
                .findFirst()
                .map(variable -> variable.getType());

    }

    /**
     * XXX: Many assumptions are made about the parentNode
     * @param parentNode a DefaultMutableTreeNode that is assumed to have only
     * DefaultMutableTreeNode children that contain non-null SB_Variable user
     * objects (note that SB_Constant, SB_Global, and SB_Parameter are all
     * subclasses of SB_Variable.)
     * */
    private static Optional<SB_Variable> getChildVariableWithMatchingName(
            DefaultMutableTreeNode parentNode, String varName) {
        //XXX: This unchecked conversion replicates earlier behavior, which
        //assumed that all children of the locals node are
        //DefaultMutableTreeNode instances.
        Enumeration<TreeNode> children = parentNode.children();
        return Collections.list(children).stream()
        		.filter((c)->c instanceof DefaultMutableTreeNode)
        		.map((c)->(DefaultMutableTreeNode)c)
                .map(child -> (SB_Variable) child.getUserObject())
                .filter(var -> var.getName().equals(varName))
                //XXX: reduce used to simulate "find last" of former behavior.
                .reduce((a, b) -> b);
    }

    static class SB_BindingsTableModel extends AbstractTableModel {

        /**
         * The list of bindings that should back this table model
         * <br>
         * XXX: this list is modified by the normal operation of this class.
         * */
        final List<SB_Binding> _bindings;

        final String[] columnNames = { "Variable", "Expression"};

        SB_BindingsTableModel(List<SB_Binding> _bindings) {
            this._bindings = _bindings;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            if (_bindings != null)
                return _bindings.size();
            else
                return 0;
        }

        @Override
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

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (_bindings.size() > row) {
                SB_Binding binding = (SB_Binding) _bindings.get(row);
                if (col == 0) {
                    String newVar = getVarString(value);
                    if (newVar == null) {
                        //Early termination replicates behavior of earlier
                        //implementation
                        return;
                    }
                    if (!newVar.equals(binding.getVar())) {
                        binding.setVar(newVar);
                        fireTableCellUpdated(row, col);
                    }
                } else {
                    String newExpr = (String) value;
                    if (!newExpr.equals(binding.getExpr())) {
                        binding.setExpr((String) value);
                        fireTableCellUpdated(row, col);
                    }
                }
            } else {
                //XXX: Unknown purpose. Debugging?
                int i = 0;
                int j = 2;
            }
        }

        private static String getVarString(Object value) {
            if (value instanceof SB_Variable)
                return ((SB_Variable) value).getName();
            else if (value instanceof String) return (String) value;
            else if (value == null) return null;
            else throw new IllegalArgumentException(
                        "Either String or SB_Variable instance expected.");
        }
    }

    static class ComboBoxRenderer extends JLabel
    implements ListCellRenderer<String> {

        private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            //setHorizontalAlignment(CENTER);
            //setVerticalAlignment(CENTER);
        }

        /**
         * XXX: Uses present approach of String instances as ComboBox contents;
         * will need refit if images are added (again?).
         * */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {
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