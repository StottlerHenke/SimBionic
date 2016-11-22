
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.api.SB_ParamType;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Breakpoint;
import com.stottlerhenke.simbionic.editor.SB_Entity;
import com.stottlerhenke.simbionic.editor.SB_Frame;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * The display for the interactive debugger. Control
 * logic for the debug client is located in SB_ProjectBar.
 *
 */
public class SB_Debugger extends JPanel implements ListSelectionListener {
	protected SimBionicEditor _editor;
	
	protected JToolBar toolBar;
	
	public JMenu debugMenu;
	
	protected Vector _entities = new Vector();
	
	protected long _currentEntity = -1;    // current in the engine
	protected long _selectedEntity = -1;   // selected in the debugger
	protected int _selectedFrame = -1; // selected in the debugger
	
	protected SB_EntitiesTable _entitiesTable;
	protected SB_ExecStackTable _execStackTable;
	protected SB_VariablesTable _variablesTable;
    
	protected JPanel entitiesPanel = new JPanel(new BorderLayout());
	protected JPanel execStackPanel = new JPanel(new BorderLayout());
	protected JPanel variablesPanel = new JPanel(new BorderLayout());
	
	public SB_Debugger(SimBionicEditor editor) {
		_editor = editor;
		
		debugMenu = createDebugMenu(_editor);
		
		setLayout(new BorderLayout());
		
    	toolBar = new JToolBar();
    	toolBar.setFloatable(false);
    	toolBar.setRollover(true);
    	
       	_editor.startAction.setEnabled(true);
       	_editor.pauseAction.setEnabled(false);
       	_editor.stepOverAction.setEnabled(true);
       	_editor.stepOneTickAction.setEnabled(false);
       	_editor.runToFinalAction.setEnabled(false);
       	_editor.showCurrentAction.setEnabled(true);

       	toolBar.add(new SB_Button(_editor.startAction));
    	toolBar.add(new SB_Button(_editor.stopAction));
    	toolBar.add(new SB_Button(_editor.pauseAction));
        
        toolBar.addSeparator();

    	toolBar.add(new SB_Button(_editor.stepIntoAction));
    	toolBar.add(new SB_Button(_editor.stepOverAction));
    	toolBar.add(new SB_Button(_editor.stepOneTickAction));
    	toolBar.add(new SB_Button(_editor.runToFinalAction));
        
        toolBar.addSeparator();

    	toolBar.add(new SB_Button(_editor.showCurrentAction));
    	toolBar.add(new SB_Button(_editor.breakpointAction));
    	
		JPanel tablesPanel = new JPanel();
		tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        tablesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		_entitiesTable = new SB_EntitiesTable();
        JScrollPane scrollPane = new JScrollPane(_entitiesTable);
        scrollPane.setPreferredSize(new Dimension(475, 145));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getViewport().setBackground(Color.white);
        
        
        
        JLabel label = new JLabel("Entities:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel entitiesPanelInner = new JPanel(new BorderLayout());
        entitiesPanelInner.add(label,BorderLayout.NORTH);
        entitiesPanelInner.add(scrollPane,BorderLayout.CENTER);
        entitiesPanel.add(toolBar,BorderLayout.NORTH);
        entitiesPanel.add(entitiesPanelInner,BorderLayout.CENTER);
        tablesPanel.add(entitiesPanel);
        tablesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
       
        _execStackTable = new SB_ExecStackTable();
        scrollPane = new JScrollPane(_execStackTable);
        scrollPane.setPreferredSize(new Dimension(425, 145));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getViewport().setBackground(Color.white);
      
        label = new JLabel("Execution Stack:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        execStackPanel.add(label,BorderLayout.NORTH);
        execStackPanel.add(scrollPane, BorderLayout.CENTER);
        tablesPanel.add(execStackPanel);
        tablesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
       
        _variablesTable = new SB_VariablesTable();
        scrollPane = new JScrollPane(_variablesTable);
        scrollPane.setPreferredSize(new Dimension(425, 145));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getViewport().setBackground(Color.white);
       
        label = new JLabel("Variables:");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        variablesPanel.add(label, BorderLayout.NORTH);
        variablesPanel.add(scrollPane, BorderLayout.CENTER);
        tablesPanel.add(variablesPanel);       
        add(tablesPanel, BorderLayout.CENTER);
        
        _entitiesTable.getSelectionModel().addListSelectionListener(this);
        _execStackTable.getSelectionModel().addListSelectionListener(this);
	}
	
	protected SB_Catalog getCatalog()
	{
		return _editor.getCatalog();
	}
	
    protected SB_TabbedCanvas getTabbedCanvas()
    {
    	return (SB_TabbedCanvas) ComponentRegistry.getContent();
    }

	protected void createEntity(long entityId, String name)
	{
		SB_Entity entity = new SB_Entity(entityId, name);
		_entities.add(entity);
		
       	setCurrentEntity(entity);
	}
	
	/**
	 * Updates the global variables in the variable table for the currently-selected entity.
	 * @param globalNames
	 * @param globalValues
	 */
    protected void updateGlobalVariables(ArrayList globalNames, Vector globalValues)
    {
    	_variablesTable._tableModel._globalNames = globalNames;
    	_variablesTable._tableModel._globalValues = globalValues;
    	_variablesTable._tableModel.setBreakpointEnablements();
    	_variablesTable.revalidate();
    	_variablesTable.repaint();
    }
    
    /**
     * Updates the local variables in the variable table for the currently-selected entity.
     * @param localNames
     * @param localValues
     */
    protected void updateLocalVariables(ArrayList localNames, Vector localValues)
    {
        _variablesTable._tableModel._localNames = localNames;
        _variablesTable._tableModel._localValues = localValues;
        _variablesTable._tableModel.setBreakpointEnablements();
        _variablesTable.revalidate();
        _variablesTable.repaint();
    }

    protected void discardFrame(long entityId, int frame)
    {
        // do nothing since the stack will be updated at the end of the step
    }
    
	protected void createFrame(long entityId, int frame, int parent, String behav_name, ArrayList polyIndices, int currentNode, int interrupt)
	{
		SB_Entity entity = findEntity(entityId);

        SB_Frame sb_frame = makeFrame(parent,behav_name,polyIndices,currentNode,interrupt);
        entity._frames.add(sb_frame);
        
        setCurrentFrame(entityId,frame);        
	}

	/**
	 * Creates a new frame object.
	 * @param parent parent frame
	 * @param behav_name
	 * @param 
	 * @param currentNode
	 * @param interrupt
	 * @return the new frame
	 */
	protected SB_Frame makeFrame(int parent, String behav_name, ArrayList polyIndices, int currentNode, int interrupt)
	{
        SB_Behavior behav = getCatalog().findBehavior(behav_name);
        SB_Polymorphism poly = behav.findPoly(polyIndices);
        SB_Rectangle rectangle = (SB_Rectangle) poly.getElements().findDrawable(currentNode, SB_ProjectBar.SB_RectangleClass);
        return new SB_Frame(poly, rectangle, parent);
	}
	
	protected void completeFrame(long entityId, int frame)
	{
	    // do nothing since the stack will be updated at the end of the step
	}

	protected SB_Entity findEntity(long entityId)
	{
		SB_Entity entity;
		int size = _entities.size();
		for (int i = 0; i < size; ++i)
		{
			entity = (SB_Entity) _entities.get(i);
			if (entity._entityId == entityId)
				return entity;
		}
		return null;
	}
	protected int findEntityIndex(long entityId)
	{
		SB_Entity entity;
		int size = _entities.size();
		for (int i = 0; i < size; ++i)
		{
			entity = (SB_Entity) _entities.get(i);
			if (entity._entityId == entityId)
				return i;
		}
		return -1;
	}
	
	protected void endEntity(long entityId)
	{
	    // do nothing?
	}
	
    protected void destroyEntity(long entityId)
	{
		int index = findEntityIndex(entityId);
		if (index != -1)
		{
			_entities.remove(index);
			_entitiesTable.revalidate();
			_entitiesTable.repaint();
			
			if (entityId == _currentEntity) {
			    setCurrentEntity(null);			    
			}
			if (entityId == _selectedEntity) {
			    setSelectedEntity(null,true);
			}
		}
	}

	protected void checkCondition(long entityId, int frame, int conditionId, SB_Param conditionValue)
	{
		SB_Entity entity = findEntity(entityId);
		SB_Frame fFrame = (SB_Frame) entity._frames.get(frame);
		fFrame._condition = (SB_Condition) fFrame._poly.getElements().findDrawable(conditionId, SB_ProjectBar.SB_ConditionClass);
		fFrame._condition._runningState = SB_Element.RUNNING_CHECKED;
		
		setCurrentFrame(entityId,frame);
	}
	
	protected void followCondition(long entityId, int frame, int conditionId)
	{
		SB_Entity entity = findEntity(entityId);
        SB_Frame fFrame = (SB_Frame) entity._frames.get(frame);
        fFrame._condition = (SB_Condition) fFrame._poly.getElements().findDrawable(conditionId, SB_ProjectBar.SB_ConditionClass);
        fFrame._condition._runningState = SB_Element.RUNNING_FOLLOWED;
        
        setCurrentFrame(entityId,frame);
	}
	
	protected void changeNode(long entityId, int frame, int nodeId)
	{
		SB_Entity entity = findEntity(entityId);
		assert (frame == entity._currentFrame);
		SB_Frame fframe = (SB_Frame) entity._frames.get(frame);
		fframe._condition = null;
		fframe._rectangle = (SB_Rectangle) fframe._poly.getElements().findDrawable(nodeId, SB_ProjectBar.SB_RectangleClass);
		
		setCurrentFrame(entityId,frame);
	}
	
	/**
	 * Highlights the current rectangle.
	 */
	public void showCurrentRectangle()
	{
		if (_currentEntity != -1) {		    
			SB_Entity entity = findEntity(_currentEntity);
			if ((entity != null) && (entity._currentFrame != -1)) {
				SB_Frame frame = (SB_Frame) entity._frames.get(entity._currentFrame);

				// bring the poly to the front
		        SB_Behavior behav = frame._poly._parent;
                SB_TabbedCanvas canvas = getTabbedCanvas();
		        canvas.setBehavior(behav, true);
		        int index = behav.getPolys().indexOf(frame._poly);
		        if (index != -1) {
		            canvas.setSelectedIndex(index);
				
    		        // select the current node
    				SB_Rectangle rectangle = frame._rectangle;
    				if (rectangle != null) {
        				rectangle._runningState = SB_Element.RUNNING;
        				canvas.getActiveCanvas().scrollToDrawable(rectangle);
    				}
		        }
		        
				_execStackTable.revalidate();
				_execStackTable.repaint();
			}
		}
	}
	
	protected void selectPoly(SB_Polymorphism poly)
	{
		SB_TabbedCanvas tabbedCanvas = getTabbedCanvas();
		SB_Behavior behav = poly._parent;
		tabbedCanvas.setBehavior(behav, true);
		int index = behav.getPolys().indexOf(poly.getDataModel());
		if (index != -1)
			getTabbedCanvas().setSelectedIndex(index);
	}
	
	protected void changeVariable(long entityId,int frame,String varName,SB_Param value)
	{
        assert (entityId == _currentEntity);

        _variablesTable._tableModel._changedVars.add(varName);
	}
	
    protected void changeGlobalVariable(long entityId,int frame,String varName,SB_Param value)
    {
        assert (entityId == _currentEntity);
        
        _variablesTable._tableModel._changedVars.add(varName);
    }
    
	protected void clearChangedVars()
	{
		_variablesTable._tableModel._changedVars.clear();
		_variablesTable.revalidate();
		_variablesTable.repaint();
	}
	
	class SB_EntitiesTable extends JTable {

		public boolean _programmaticallySetting = false;
		
	    public SB_EntitiesTable() {
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        setRowHeight(19);
	        
	        SB_EntitiesTableModel tableModel = new SB_EntitiesTableModel();
	        setModel(tableModel);
	        int length = tableModel.columnWidths.length;
	        for (int i = 0; i < length; ++i)
	        {
	        	getColumnModel().getColumn(i).setPreferredWidth(tableModel.columnWidths[i]);
	        }
	    }
	}
	
    class SB_EntitiesTableModel extends AbstractTableModel {

    	final static int ID_COL = 0;
    	final static int NAME_COL = 1;
    	final static int BEHAV_COL = 2;
    	final static int STACK_COL = 3;
    	final static int ALIVE_COL = 4;
    	final static int WATCH_COL = 5;
        final String[] columnNames = { "Id", "Entity Name", "Current Behavior", "Stack", "H:M:S", "Watch" };
        final int[] columnWidths = { 30, 140, 140, 45, 45, 45 };
				  
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return _entities.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	SB_Entity entity = (SB_Entity) _entities.get(row);
        	int stackSize = entity._frames.size();
        	SB_Frame frame = null;
        	if ((stackSize > 0) && (entity._currentFrame >= 0) && (entity._currentFrame < stackSize)) {
        		frame = (SB_Frame) entity._frames.get(entity._currentFrame);
        	}
            switch (col)
			{
            case ID_COL:
            	return Long.toString(entity._entityId);
            case NAME_COL:
            	return entity._name;
            case BEHAV_COL:
            	if (frame != null)
            		return frame._poly._parent.getName();
            	else
            		return "<none>";
            case STACK_COL:
            	return Integer.toString(stackSize);
            case ALIVE_COL:
            	return Long.toString(entity._alive);
            case WATCH_COL:
            	return new Boolean(entity._watch);
            default:
            	return "";
			}
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return col == WATCH_COL;
        }

        public void setValueAt(Object value, int row, int col) {
        	SB_Entity entity = (SB_Entity) _entities.get(row);
        	if (col == WATCH_COL)
        	{
        		entity._watch = !entity._watch;
        		if(entity._watch){
        			ComponentRegistry.getProjectBar().addToWatchDebug();
        		}
        		else{
        			ComponentRegistry.getProjectBar().removeFromWatchDebug();
        		}
        		
        	}
            fireTableCellUpdated(row, col);
        }
    }
    
	class SB_ExecStackTable extends JTable {
		
		public SB_ExecStackTableModel _tableModel;
        public boolean _programmaticallySetting = false;
		
 	    public SB_ExecStackTable() {
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        setRowHeight(19);
	        
	        _tableModel = new SB_ExecStackTableModel();
	        setModel(_tableModel);
	        int length = _tableModel.columnWidths.length;
	        for (int i = 0; i < length; ++i)
	        {
	        	getColumnModel().getColumn(i).setPreferredWidth(_tableModel.columnWidths[i]);
	        }
	    }
	}
	
	/**
	 * Model for execution stack table.  Does not store any data
	 * itself but retrieves it from the selected entity.
	 *
	 * @author houlette
	 */
    class SB_ExecStackTableModel extends AbstractTableModel {
    	
    	final static int LEVEL_COL = 0;
    	final static int BEHAV_COL = 1;
    	final static int RECT_COL = 2;
    	final static int PARENT_COL = 3;
        final String[] columnNames = { "Level", "Behavior", "Current Rectangle", "Invoking Frame" };
        final int[] columnWidths = { 40, 140, 140, 75 };
        
        public SB_Entity _entity = null;
       
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
        	if (_entity == null)
        		return 0;
        	else
        		return _entity._frames.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	SB_Frame frame = (SB_Frame) _entity._frames.get(row);
            switch (col)
			{
            case LEVEL_COL:
            	return Integer.toString(row + 1);
            case BEHAV_COL:
            	return frame._poly._parent.getName();
            case RECT_COL:
            {
                String name = "None";
                if (frame._rectangle != null) {
                    name = SB_Catalog.extractFuncName(frame._rectangle.getExpr());
                }
            	return name;
            }
            case PARENT_COL:
            	if (frame._parent == 0)
            		return "-";
            	else
            		return Integer.toString(frame._parent);
            default:
            	return "";
			}
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }
	
	class SB_VariablesTable extends JTable {

		public SB_VariablesTableModel _tableModel;
		
	    public SB_VariablesTable() {
	        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        setRowHeight(19);
	        
	        _tableModel = new SB_VariablesTableModel();
	        setModel(_tableModel);
	        int length = _tableModel.columnWidths.length;
	        for (int i = 0; i < length; ++i)
	        {
	        	getColumnModel().getColumn(i).setPreferredWidth(_tableModel.columnWidths[i]);
	        }
	        
	        setDefaultRenderer(Object.class, new SB_VariablesTableRenderer()); 
	    }
	    
	    
	}
	
    class SB_VariablesTableModel extends AbstractTableModel {
    	public ArrayList _globalNames;
    	public Vector _globalValues;
    	public ArrayList _localNames;
    	public Vector _localValues;
    	public TreeSet _changedVars = new TreeSet();
    	
    	final static int GLP_COL = 0;
    	final static int NAME_COL = 1;
    	final static int VALUE_COL = 2;
    	final static int BREAKPOINT_COL = 3;
        final String[] columnNames = { "G/L/P", "Variable Name", "Value", "Breakpoint" };
        final int[] columnWidths = { 40, 140, 160, 65 };
				  
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return (_globalNames != null && _localNames != null) ? _globalNames.size() + _localNames.size() : 0;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	String name = "";
        	SB_Param param = null;
        	
        	int global_size = _globalNames.size();
        	if (row < global_size)
        	{
        		name = (String) _globalNames.get(row);
        		param = (SB_Param) _globalValues.get(row);       		
        	}
        	else
        	{
        		name = (String) _localNames.get(row - global_size);
        		param = (SB_Param) _localValues.get(row - global_size);
        	}
        	
        	switch (col)
			{
            case GLP_COL:
            	if (row < global_size)
            		return "G";
            	else
            		return "L\\P";
            case NAME_COL:
            	return name;
            case VALUE_COL:
            	return param;
            case BREAKPOINT_COL:
            	return new Boolean(param._isBreakpoint);
            default:
            	return "";
			}
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
        	return (col==VALUE_COL||col==BREAKPOINT_COL);  
        }
        
        public void setValueAt(Object value, int row, int col) {
        	if (col == BREAKPOINT_COL)
        	{
        		String name = "";
            	SB_Param param = null;
            	int global_size = _globalNames.size();
            	boolean isGlobal = false;
            	if (row < global_size)
            	{
            		isGlobal = true;
            		name = (String) _globalNames.get(row);
            		param = (SB_Param) _globalValues.get(row);       		
            	}
            	else
            	{
            		name = (String) _localNames.get(row - global_size);
            		param = (SB_Param) _localValues.get(row - global_size);
            	}
            		
            	param._isBreakpoint = !param._isBreakpoint;
            	
            	String behavior = _execStackTable.getValueAt(_execStackTable.getSelectedRow(), 1).toString();
            	if(isGlobal){behavior = "Global";}
            	SB_BreakpointFrame bpList = _editor._breakpointFrame;
            	if(param._isBreakpoint){
            		SB_Breakpoint bp = bpList.addBreakpoint();
            		bp._type = SB_Breakpoint.VARIABLE;
            		bp._behavior = behavior;
            		bp._varName = name;
            		bp._enabled = true;
            		bp._entityId = SB_Breakpoint.ALL_ENTITIES;
            		bp._constraint = "";
            		bp._iterations = 0;
            		if(ComponentRegistry.getProjectBar()._debugging){
            			ComponentRegistry.getProjectBar().addBreakVarDebug(bp);
            		}
            	}
            	else{
            		SB_Breakpoint bp = bpList.removeVarBreakpoint(behavior, name);
            		if(ComponentRegistry.getProjectBar()._debugging){
            			ComponentRegistry.getProjectBar().removeBreakpointDebug(bp);
            		}
            	}
        	}
        	if (col == VALUE_COL)
        	{
        		String name = "";
            	SB_Param param = null;
            	
            	int global_size = _globalNames.size();
            	if (row < global_size)
            	{
            		name = (String) _globalNames.get(row);
            		param = (SB_Param) _globalValues.get(row);       		
            	}
            	else
            	{
            		name = (String) _localNames.get(row - global_size);
            		param = (SB_Param) _localValues.get(row - global_size);
            	}
            	
            	SB_ParamType paramType = param.getType();
            	if (paramType == SB_ParamType.kSB_Data) {
            	   param.setData(value);
            	}
            	
            	
            	
            	if(getValueAt(row,GLP_COL).equals("G")){
            		ComponentRegistry.getProjectBar().setGlobalDebug(name,param);
            	}
            	else{
            		ComponentRegistry.getProjectBar().setLocalDebug(name,param);
            	}
        	}
            fireTableCellUpdated(row, col);
        }
        
        public void setBreakpointEnablements()
        {
        	if (_globalNames != null) {
        		for (int i = 0; i<_globalNames.size(); i++) {
        			String behavior = "Global";
        			String varName = (String) _globalNames.get(i);
        			SB_Param param = (SB_Param) _globalValues.get(i);
        			param._isBreakpoint = _editor._breakpointFrame.hasBreakpoint(behavior, varName);
        		}
        	}
        	if ((_localNames != null) && (_execStackTable._tableModel._entity._frames.size() > 0)) {
        	    int selectedFrame = _execStackTable.getSelectedRow();
        	    if (selectedFrame != -1) {
            		for (int i = 0; i<_localNames.size(); i++) {
            			String behavior = _execStackTable.getValueAt(selectedFrame, 1).toString();
            			String varName = (String) _localNames.get(i);
            			SB_Param param = (SB_Param) _localValues.get(i);
            			param._isBreakpoint = _editor._breakpointFrame.hasBreakpoint(behavior, varName);
            		}
        	    }
        	}
	    }
    }
    
    class SB_VariablesTableRenderer extends DefaultTableCellRenderer { 

        public Component getTableCellRendererComponent(JTable table, 
                                 Object value, 
                                 boolean isSelected, 
                                 boolean hasFocus, 
                                 int row, 
                                 int column) { 

        	SB_VariablesTable varTable = (SB_VariablesTable) table;
        	SB_VariablesTableModel varTableModel = varTable._tableModel;
        	JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == SB_VariablesTableModel.VALUE_COL)
            {
                String varName = (String) varTableModel.getValueAt(row, SB_VariablesTableModel.NAME_COL);
				if (varTableModel._changedVars.contains(varName))
				{
					renderer.setForeground(Color.red);
					return renderer;
				}
            }
            
            renderer.setForeground(Color.black);
            return renderer;
        }
   }
    
    public static JMenu createDebugMenu(SimBionicEditor simbionic){
    	JMenu debugMenu = new JMenu("Debug");
    	
    	JMenuItem startItem = new JMenuItem(simbionic.startAction);
    	debugMenu.add(startItem);
    	JMenuItem stopItem = new JMenuItem(simbionic.stopAction);
    	debugMenu.add(stopItem);
    	JMenuItem pauseItem = new JMenuItem(simbionic.pauseAction);
    	debugMenu.add(pauseItem);
    	debugMenu.addSeparator();
    	
    	JMenuItem stepOverItem = new JMenuItem(simbionic.stepOneTickAction);
    	debugMenu.add(stepOverItem);
    	JMenuItem stepIntoItem = new JMenuItem(simbionic.stepIntoAction);
    	debugMenu.add(stepIntoItem);
    	JMenuItem stepOneTickItem = new JMenuItem(simbionic.stepOneTickAction);
    	debugMenu.add(stepOneTickItem);
    	JMenuItem runToFinalItem = new JMenuItem(simbionic.runToFinalAction);
    	debugMenu.add(runToFinalItem);
    	debugMenu.addSeparator();
    	
    	JMenuItem breakpointItem = new JMenuItem(simbionic.breakpointAction);
    	debugMenu.add(breakpointItem);
    	debugMenu.addSeparator();
    	
    	JMenuItem showCurrentItem = new JMenuItem(simbionic.showCurrentAction);
    	debugMenu.add(showCurrentItem);
    	
    	return debugMenu;
    }

    /**
     * Sets the specified existing stack frame to be current.
     * @param entityId
     * @param frameId
     */
    public void setCurrentFrame(long entityId, int frameId)
    {
        assert (_currentEntity == entityId);
        
        // refresh the entity table so the current frame shows up
        SB_Entity entity = findEntity(entityId);
        entity._currentFrame = frameId;
        _entitiesTable.revalidate();
        _entitiesTable.repaint();      
    }

    /**
     * Sets the currently-executing entity.
     * @param entity
     */
    void setCurrentEntity(SB_Entity entity)
    {        
        _currentEntity = (entity == null) ? -1 : entity._entityId;
    }
    
    /**
     * Selects the specified entity in the entities table 
     * and updates the other windows accordingly.
     * @param entity
     * @param selectEntity 
     */
    void setSelectedEntity(SB_Entity entity,boolean selectEntity)
    {
        System.out.println("setSelectedEntity: " + entity + " " + selectEntity);
        
        if (entity == null) {
            _entitiesTable._programmaticallySetting = true;
            _entitiesTable.clearSelection();
            _entitiesTable._programmaticallySetting = false;

            _selectedEntity = -1;
        }
        else if (_selectedEntity != entity._entityId) {            
            _selectedEntity = entity._entityId;

            if (selectEntity) {
                // select the entity in the entities table
                int row = findEntityIndex(entity._entityId);
                _entitiesTable._programmaticallySetting = true;
                _entitiesTable.setRowSelectionInterval(row, row);
                _entitiesTable._programmaticallySetting = false;
            }

            if (entity._currentFrame == -1) {
                // query server to get stack for this entity
                ComponentRegistry.getProjectBar().queryEntityState(entity);
            }
        }

        // always refresh in case the current frame has changed or other
        // entities have been added to/deleted from the list
        _entitiesTable.revalidate();
        _entitiesTable.repaint();        
                
        // set the entity's current frame to be selected in the execution stack
        // always do this even if the selected entity hasn't changed to force
        // the other views to refresh
        int frame = (entity == null) ? -1 : entity._currentFrame;
        setSelectedFrame(entity,frame,true);            
    }
    
    /**
     * Selects the specified frame in the stack table 
     * and updates the other windows accordingly.
     * @param entity
     * @param frame
     * @param selectFrame
     */
    private void setSelectedFrame(SB_Entity entity,int frame,boolean selectFrame)
    {        
        System.out.println("setSelectedFrame: " + entity + ", frame " + frame + " " + selectFrame);

        if ((_selectedFrame != frame) || (_execStackTable._tableModel._entity != entity)) {
            _selectedFrame = frame;
            _execStackTable._tableModel._entity = entity;

            if ((frame == -1) || (entity == null)) {
                // clear the stack and canvas
                getTabbedCanvas().getActiveCanvas().getPoly().getElements().clearRunningState();
                _execStackTable.revalidate();
                _execStackTable.repaint();   
                updateLocalVariables(null,null);                
                return;           
            }

            if (selectFrame) {
                // select the frame in the stack table
                _execStackTable._programmaticallySetting = true;
                _execStackTable.setRowSelectionInterval(frame, frame);
                _execStackTable._programmaticallySetting = false;
            }
        }
            
        // always refresh in case stack details have changed
        _execStackTable.revalidate();
        _execStackTable.repaint();        

        // bring the frame's behavior to the front of the canvas
        SB_Frame theFrame = (SB_Frame) _execStackTable._tableModel._entity._frames.get(frame);
        getTabbedCanvas().setBehavior(theFrame._poly._parent, true);
        selectPoly(theFrame._poly);
    
        // select the frame's current node
        theFrame._poly.getElements().clearRunningState();
        if (theFrame._condition != null) {
            theFrame._condition._runningState = SB_Element.RUNNING;
            getTabbedCanvas().getActiveCanvas().scrollToDrawable(theFrame._condition);                
        }
        else if (theFrame._rectangle != null) {
            theFrame._rectangle._runningState = SB_Element.RUNNING;
            getTabbedCanvas().getActiveCanvas().scrollToDrawable(theFrame._rectangle);
        }

        // update the variable window to show this frame's variables
        ComponentRegistry.getProjectBar().queryEntityLocals(_execStackTable._tableModel._entity._entityId,frame);
        ComponentRegistry.getProjectBar().queryEntityGlobals(_execStackTable._tableModel._entity._entityId);            
    }
    
    /**
     * Notifies the debugger that the given entity is current
     * and should be displayed.
     * @param entityId
     */
    public void startEntity(long entityId)
    {        
        setCurrentEntity(findEntity(entityId));
    }

    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting())
            return; // ignore the selection clearing for the previous selection
        
        // only adjust the current selection if it was a result of user action
        // to avoid undoing the debugger's programmatic selection changes
        if (e.getSource() == _entitiesTable.getSelectionModel()) {
            if (!_entitiesTable._programmaticallySetting) {
                int row = _entitiesTable.getSelectedRow();
                setSelectedEntity((SB_Entity) _entities.get(row),false);
            }
        }
        else if (e.getSource() == _execStackTable.getSelectionModel()) { 
            if (!_execStackTable._programmaticallySetting) {
                int row = _execStackTable.getSelectedRow();
                setSelectedFrame(_execStackTable._tableModel._entity,row,false);
            }
        }
    }

    /**
     * Clears the execution stack for the specified entity
     * and creates a new frame.
     * @param entityId
     * @param behaviorName
     * @param polyIndices
     */
    public void changeBehavior(long entityId,String behaviorName,ArrayList polyIndices)
    {
        SB_Entity entity = findEntity(entityId);
        entity._frames.clear();
        entity._frames.add(makeFrame(-1,behaviorName,polyIndices,-1,-1));
        
        setCurrentFrame(entityId,0);    
    }
    
    public String toString()
    {
        StringBuffer strBuff = new StringBuffer();
        
        strBuff.append("------------------------------------------------\n");
        strBuff.append("ENTITIES:\n");
        for (Object obj : _entities) {
            SB_Entity entity = (SB_Entity)obj;
            strBuff.append("\t" + entity + "\n");
        }

        if (_entities.size() > 0) {
            strBuff.append("CURRENT ENTITY: " + ((_currentEntity != -1) ? findEntity(_currentEntity)._name : "none") + "\n");
            strBuff.append("SELECTED ENTITY: " + ((_selectedEntity != -1) ? findEntity(_selectedEntity)._name : "none") + "\n");
    
            if (_selectedEntity != -1) {
                strBuff.append("SELECTED STACK: \n");            
                SB_Entity entity = findEntity(_selectedEntity);
                for (Object obj : entity._frames) {
                    SB_Frame frame = (SB_Frame)obj;
                    strBuff.append("\t" + frame + "\n");
                }            
            }
        }
        
        return strBuff.toString();
    }

    /**
     * @return the ID of the current entity, or -1 if no entity is current
     */
    public long getCurrentEntity()
    {
        return _currentEntity;
    }

    /**
     * @return the ID of the current stack frame, or -1 if no frame is current
     */
    public int getCurrentFrame()
    {
        return (_currentEntity == -1) ? -1 : findEntity(_currentEntity)._currentFrame;
    }

}
