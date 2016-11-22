package com.stottlerhenke.simbionic.editor.gui;

import java.awt.*;
import java.util.Vector;
import java.util.Collections;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.stottlerhenke.simbionic.editor.SB_Breakpoint;

/**
 * Panel for displaying and editing all breakpoints.
 *
 */
public class SB_BreakpointFrame extends JPanel {

	protected Vector _breakpoints = new Vector();
	
	protected SB_BreakpointsTable _breakpointsTable;
	
	public SB_BreakpointFrame() {
		_breakpointsTable = new SB_BreakpointsTable();
		JScrollPane scrollPane = new JScrollPane(_breakpointsTable);
        scrollPane.setPreferredSize(new Dimension(475, 145));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.getViewport().setBackground(Color.white);
        
        scrollPane.setPreferredSize(new Dimension(700, 210));
        add(scrollPane);
	}
	
	/**
	 * Creates a new Breakpoint with the lowest current available
	 * ID.
	 * @return
	 */
	public SB_Breakpoint addBreakpoint(){
		SB_Breakpoint bp = new SB_Breakpoint(getLowestAvailableID());
		_breakpoints.add(bp);
		_breakpointsTable.revalidate();
		_breakpointsTable.repaint();
		return bp;
	}
	
	/**
	 * Removes breakpoint identified by behavior and varName.
	 * @param behavior
	 * @param varName
	 */
	public SB_Breakpoint removeVarBreakpoint(String behavior, String varName){
		for(int i = 0;i<_breakpoints.size();i++){
			SB_Breakpoint bp = (SB_Breakpoint) _breakpoints.get(i);
			if(bp._type==2&&bp._behavior.equals(behavior)&&bp._varName.equals(varName)){
				_breakpoints.remove(i);
				_breakpointsTable.revalidate();
				_breakpointsTable.repaint();
				return bp;
			}
		}
		return null;
	}
	
	/**
	 * Removes element breakpoint identified by behavior and varName.
	 * @param behavior
	 * @param varName
	 */
	public SB_Breakpoint removeElemBreakpoint(String behavior, int Id){
		for(int i = 0;i<_breakpoints.size();i++){
			SB_Breakpoint bp = (SB_Breakpoint) _breakpoints.get(i);
			if(bp._type<2&&bp._behavior.equals(behavior)&&bp._elemId==Id){
				_breakpoints.remove(i);
				_breakpointsTable.revalidate();
				_breakpointsTable.repaint();
				return bp;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if there is a breakpoing identified by behavior 
	 * and varName.
	 * @return
	 */
	public boolean hasBreakpoint(String behavior, String varName){
		for(int i = 0;i<_breakpoints.size();i++){
			SB_Breakpoint bp = (SB_Breakpoint) _breakpoints.get(i);
			if(bp._behavior.equals(behavior)&&bp._varName.equals(varName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the lowest available breakpoint ID.
	 * 
	 * @return
	 */
	public int getLowestAvailableID(){
		Collections.sort(_breakpoints);
		for(int i = 0;i<_breakpoints.size();i++){
			if(((SB_Breakpoint)_breakpoints.get(i))._breakpointId!=i){
				return i;
			}
		}
		return _breakpoints.size();
	}
	
	/**
	 * Creates (and if neccessary, disables) all the breakpoints.  
	 * This method is called whenever a new debug connection is made.
	 *
	 */
	public void initBreakpoints(){
		for(int i = 0;i<_breakpoints.size();i++){
			SB_Breakpoint bp = (SB_Breakpoint) _breakpoints.get(i);
			if(bp._type==SB_Breakpoint.VARIABLE){
				ComponentRegistry.getProjectBar().addBreakVarDebug(bp);
				if(!bp._enabled){
					ComponentRegistry.getProjectBar().disableBreakpoint(bp._breakpointId);
				}
			}
			else{
				ComponentRegistry.getProjectBar().addBreakElemDebug(bp);
				if(!bp._enabled){
					ComponentRegistry.getProjectBar().disableBreakpoint(bp._breakpointId);
				}
			}
		}
	}
	
	
	
	
	   class SB_BreakpointsTable extends JTable {

			public SB_BreakpointsTableModel _tableModel;
			
		    public SB_BreakpointsTable() {
		        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        setRowHeight(19);
		        
		        _tableModel = new SB_BreakpointsTableModel();
		        setModel(_tableModel);
		        int length = _tableModel.columnWidths.length;
		        for (int i = 0; i < length; ++i)
		        {
		        	getColumnModel().getColumn(i).setPreferredWidth(_tableModel.columnWidths[i]);
		        }
		        
		        setDefaultRenderer(Object.class, new SB_BreakpointsTableRenderer()); 
		    }
		}
	    
	    class SB_BreakpointsTableModel extends AbstractTableModel {
	    	final static int NAME_COL = 0;
	    	final static int ENTITY_COL = 1;
	    	final static int ITERATIONS_COL = 2;
	    	final static int CONSTRAINT_COL = 3;
	    	final static int ENABLED_COL = 4;
	        final String[] columnNames = { "Breakpoint", "Entity", "#", "Constraint", "Enabled" };
	        final int[] columnWidths = { 170, 40, 40, 140, 40 };
					  
	        public int getColumnCount() {
	            return columnNames.length;
	        }

	        public int getRowCount() {
	            return _breakpoints.size();
	        }

	        public String getColumnName(int col) {
	            return columnNames[col];
	        }

	        public Object getValueAt(int row, int col) {
	        	SB_Breakpoint breakpoint = (SB_Breakpoint)_breakpoints.get(row);
	    		
	        	switch (col)
				{
	            case NAME_COL:
	            	if(breakpoint._type == SB_Breakpoint.VARIABLE){
	            		if(breakpoint._behavior.equals("Global")){
	            			return new String(breakpoint._behavior + " " + breakpoint._varName);
	            		}
	            		return new String("Behavior " + breakpoint._behavior + " : " + breakpoint._varName);
	            	}
	            	else if(breakpoint._type == SB_Breakpoint.RECTANGLE){
	            		return new String("Behavior " + breakpoint._behavior + " : " + "Node " + breakpoint._varName);
	            	}
	            	else if(breakpoint._type == SB_Breakpoint.CONDITION){
	            		return new String("Behavior " + breakpoint._behavior + " : " + "Condition " + breakpoint._varName);
	            	}
	            case ENTITY_COL:
	            	if (breakpoint._entityId == SB_Breakpoint.ALL_ENTITIES) {
	            	    return "A";
	            	}
	            	else if (breakpoint._entityId == SB_Breakpoint.WATCHED_ENTITIES) {
	            	    return "W";
	            	}
	            	return breakpoint._entityId;
	            case ITERATIONS_COL:
	            	return Integer.valueOf(breakpoint._iterations);
	            case CONSTRAINT_COL:
	            	return breakpoint._constraint;
	            case ENABLED_COL:
	            	return Boolean.valueOf(breakpoint._enabled);
	            default:
	            	return "";
				}
	        }

	        public Class getColumnClass(int c) {
	            return getValueAt(0, c).getClass();
	        }

	        public boolean isCellEditable(int row, int col) {
	        	return (col==ENABLED_COL||col==ENTITY_COL||col==ITERATIONS_COL||col==CONSTRAINT_COL);  
	        }
	        
	    public void setValueAt(Object value, int row, int col) {
	    	SB_Breakpoint breakpoint = (SB_Breakpoint)_breakpoints.get(row);
	    		if (col == ENABLED_COL)
	        	{
	        		breakpoint._enabled = !breakpoint._enabled;
	        		if(breakpoint._enabled&&ComponentRegistry.getProjectBar()._debugging){
	        			ComponentRegistry.getProjectBar().enableBreakpoint(breakpoint._breakpointId);
	        		}
	        		else if(ComponentRegistry.getProjectBar()._debugging){
	        			ComponentRegistry.getProjectBar().disableBreakpoint(breakpoint._breakpointId);
	        		}
	        		
	        		//for drawable elements, show that breakpoints has been disabled
	        		if(breakpoint._type<2){
	        			breakpoint._elem._breakpointEnabled = breakpoint._enabled;
	        			ComponentRegistry.getContent().repaint();
	        		}	
	        	}
	        	else if (col == ENTITY_COL){
	        		String entity = value.toString();
	        		boolean updateBreakpoint = false;
	        		if(entity.equals("A")){
	        			breakpoint._entityId = SB_Breakpoint.ALL_ENTITIES;
	        			updateBreakpoint = true;
	        		}
	        		else if(entity.equals("W")){
        				breakpoint._entityId = SB_Breakpoint.WATCHED_ENTITIES;
        				updateBreakpoint = true;
	        		}
	        		else {
	        		    breakpoint._entityId = Integer.valueOf(entity);  // TODO rth check for valid entity ID?
	        		    updateBreakpoint = true;
	        		}
	        		if(ComponentRegistry.getProjectBar()._debugging
	        			&& updateBreakpoint){
	        			if(breakpoint._type==2){
	        				ComponentRegistry.getProjectBar().addBreakVarDebug(breakpoint);
	        			}
	        			else{
	        				ComponentRegistry.getProjectBar().addBreakElemDebug(breakpoint);
	        			}
	        		}
	        	}
	        	else if(col == ITERATIONS_COL){
	        		int iterations = Integer.parseInt(value.toString());
	        		if(iterations>-1){
	        			breakpoint._iterations = iterations;
	        			if(ComponentRegistry.getProjectBar()._debugging){
		        			if(breakpoint._type==2){
		        				ComponentRegistry.getProjectBar().addBreakVarDebug(breakpoint);
		        			}
		        			else{
		        				ComponentRegistry.getProjectBar().addBreakElemDebug(breakpoint);
		        			}
		        		}
	        		}
	        	}
	        	else if(col == CONSTRAINT_COL){
	        		String expr = value.toString();
	        		breakpoint._constraint = expr;
	        		if(ComponentRegistry.getProjectBar()._debugging){
	        			if(breakpoint._type==2){
	        				ComponentRegistry.getProjectBar().addBreakVarDebug(breakpoint);
	        			}
	        			else{
	        				ComponentRegistry.getProjectBar().addBreakElemDebug(breakpoint);
	        			}
	        		}
	        		
	        	}
	            fireTableCellUpdated(row, col);
	        }
	    }
	    
	    class SB_BreakpointsTableRenderer extends DefaultTableCellRenderer { 

	        public Component getTableCellRendererComponent(JTable table, 
	                                 Object value, 
	                                 boolean isSelected, 
	                                 boolean hasFocus, 
	                                 int row, 
	                                 int column) { 

	        	SB_BreakpointsTable breakTable = (SB_BreakpointsTable) table;
	        	SB_BreakpointsTableModel breakTableModel = breakTable._tableModel;
	        	JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            
	            renderer.setForeground(Color.black);
	            return renderer;
	        }
	   }
	
}
