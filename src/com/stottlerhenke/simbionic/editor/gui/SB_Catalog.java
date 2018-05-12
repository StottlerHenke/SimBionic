
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.stottlerhenke.simbionic.common.Table;
import com.stottlerhenke.simbionic.common.classes.SB_ClassMap;
import com.stottlerhenke.simbionic.common.xmlConverters.XMLObjectConverter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Action;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Constant;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ConstantFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Folder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Function;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Global;
import com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.GlobalFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.editor.ETypeType;
import com.stottlerhenke.simbionic.editor.ETypeValid;
import com.stottlerhenke.simbionic.editor.SB_Action;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_Class;
import com.stottlerhenke.simbionic.editor.SB_Constant;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SB_Folder;
import com.stottlerhenke.simbionic.editor.SB_Function;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SB_Package;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_Predicate;
import com.stottlerhenke.simbionic.editor.SB_TypeChangeListener;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.UserObject;
import com.stottlerhenke.simbionic.editor.Util;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;

/**
 * Catalog view shown in left pane in the editor.
 *
 * TODO: This class is in need of refactoring. E.g., move dialog boxes to separate files; replace 'if instance of' statements with interaces; etc.; move popup menus into separate files
 *
 */
public class SB_Catalog extends EditorTree implements Autoscroll, DragSourceListener,
        DragGestureListener, DropTargetListener, SB_TypeChangeListener
{
	// the file path for the core actions and predicates.
	public final static String CORE_ACTIONS_PREDICATES_FILE = "coreActionsPredicates/coreActionsPredicates.xml";
    final static int kPolyIndexInsert = 0;
    final static int kPolyIndexDelete = 1;
    final static int kPolyIndexRename = 2;
    final static int kPolyIndexMoveUp = 3;
    final static int kPolyIndexMoveDown = 4;
    final static int kPolyIndexSelect = 5;
    
    // default java classes 
    final static String[] DEFAULT_JAVA_CLASSES = new String[] {String.class.getName(),
                                                               Boolean.class.getName(),
                                                               Float.class.getName(),
                                                               Integer.class.getName(),
                                                               Vector.class.getName(),
                                                               Table.class.getName()};
    

    protected static ImageIcon _projectIcon = null;
    protected static ImageIcon _headingIcon = null;

    protected static final int START_USER_ID = 128;

    // Note: Types node is removed since no class specification files support 
    // for JavaScript version
    public DefaultMutableTreeNode _actions, _predicates, _behaviors, _constants, _globals; 
    public SB_Behavior _main;
    
    // root package stores all class specification info
    protected SB_Package _rootPackage;
    
    protected SB_ClassMap _classMap;

    // JavaScript related fields
    protected JPopupMenu _rootPopup;
    protected JMenuItem _javaScriptItem;
    protected JavaScriptDialog _javaScriptDialog = new JavaScriptDialog();

    // functions popup (actions/predicates/behaviors/folder)
    protected JPopupMenu _functionsPopup;
    protected JMenuItem _insertActionItem; // actions only
    protected JMenuItem _insertPredicateItem; // predicates only
    protected JMenuItem _insertBehaviorItem; // behaviors only
    protected JMenuItem _duplicateBehaviorItem; // behaviors only
    private final JMenuItem _insertConstantItem;
    private final JMenuItem _insertGlobalItem;
    private final JMenuItem _newFolderItem;
    protected JPopupMenu.Separator _separator;
    protected JMenuItem _renameFolderItem;
    protected JMenuItem _deleteFolderItem;

    // function popup (action/predicate/behavior)
    protected JPopupMenu _functionPopup;
    protected JMenuItem _insertParameterItem;
    protected JMenuItem _renameFunctionItem;
    protected JMenuItem _deleteFunctionItem;
    protected JMenu _retTypeSubmenu; // predicate only
    protected ButtonGroup _retTypeSubmenuButtonGroup; // predicate only
    protected JRadioButtonMenuItem[] _retTypeItems;
    protected JMenu _execSubmenu; // behavior only
    protected JRadioButtonMenuItem[] _execItems;
    protected JMenu _interruptSubmenu; // behavior only
    protected JRadioButtonMenuItem[] _interruptItems;
    protected JPopupMenu.Separator _descriptionSeparator;
    protected JMenuItem _descriptionItem;
    protected JPopupMenu.Separator _reservedSeparator;
    protected JCheckBoxMenuItem _reservedItem;
    protected JMenuItem _findFuntionOccurrencesItem;

    // variable popup (parameter/constant/global)
    protected JPopupMenu _variablePopup;
    protected JMenuItem _renameVariableItem;
    protected JMenuItem _deleteVariableItem;
    protected JMenu _typeSubmenu;
    protected ButtonGroup _typeSubmenuButtonGroup;
    protected JMenuItem[] _typeItems;
    protected JMenuItem _valueItem; // constant only
    protected JMenuItem _initialValueItem; // global only
    protected JMenuItem _variableDescriptionItem; //Descriptions for variables
    protected JMenuItem _moveUpItem;
    protected JMenuItem _moveDownItem;
    protected JMenuItem _findVariablesOccurrencesItem;

    private SB_TypeManager _typeManager;
    
    /*
     * // polymorphic global popup JPopupMenu _polyGlobalPopup; JMenuItem
     * _initialValueItemPG;
     */


    // constant value dialog
    protected static JDialog _constantValueDialog = null;
    protected static SB_AutocompleteTextArea _constantValueTextField;
    protected static JButton _constantValueOK;
    protected static JButton _constantValueCancel;

    // initial value dialog
    protected static JDialog _initialValueDialog = null;
    protected static SB_AutocompleteTextArea _initialValueTextField;
    protected static JButton _initialValueOK;
    protected static JButton _initialValueCancel;

   
    protected DragSource _dragSource = null;
    private static BufferedImage _image = new BufferedImage(100, 75, BufferedImage.TYPE_3BYTE_BGR);
    protected boolean _holdDrag = false;
    protected DefaultMutableTreeNode _dragNode = null;
    protected int _dragRow = -1;
    protected DropTarget _dropTarget = null;
    
    /**
     * Name of the folder for core actions
     */
    private static final String CORE_ACTIONS = "Core Actions";
    
    /**
     * Name of the folder for core predicates.
     */
    private static final String CORE_PREDICATES = "Core Predicates";
    
    public SB_Catalog(SimBionicEditor editor)
    {
        super(editor);

        _actions = new DefaultMutableTreeNode("Actions");
        _root.add(_actions);
        _predicates = new DefaultMutableTreeNode("Predicates");
        _root.add(_predicates);
        _behaviors = new DefaultMutableTreeNode("Behaviors");
        _root.add(_behaviors);
        _globals = new DefaultMutableTreeNode("Globals");
        _root.add(_globals);
        _constants = new DefaultMutableTreeNode("Constants");
        _root.add(_constants);
        _rootPackage = new SB_Package();
        
        // add JavaScript menu to the root node
        _rootPopup = new JPopupMenu();
        _javaScriptItem = new JMenuItem("JavaScript");
        _javaScriptItem.addActionListener(this);
        _rootPopup.add(_javaScriptItem);
        
        _functionsPopup = new JPopupMenu();
        _insertActionItem = new JMenuItem("Insert Action");
        _insertActionItem.addActionListener(this);
        _functionsPopup.add(_insertActionItem);
        _insertPredicateItem = new JMenuItem("Insert Predicate");
        _insertPredicateItem.addActionListener(this);
        _functionsPopup.add(_insertPredicateItem);
        _insertBehaviorItem = new JMenuItem("Insert Behavior");
        _insertBehaviorItem.addActionListener(this);
        _functionsPopup.add(_insertBehaviorItem);

        _insertConstantItem = new JMenuItem("Insert Constant");
        _insertConstantItem.addActionListener(this);
        _functionsPopup.add(_insertConstantItem);
        _insertGlobalItem = new JMenuItem("Insert Global");
        _insertGlobalItem.addActionListener(this);
        _functionsPopup.add(_insertGlobalItem);

        _newFolderItem = new JMenuItem("New Folder");
        _newFolderItem.addActionListener(this);
        _functionsPopup.add(_newFolderItem);
        _separator = new JPopupMenu.Separator();
        _functionsPopup.add(_separator);
        _renameFolderItem = new JMenuItem("Rename");
        _renameFolderItem.addActionListener(this);
        _functionsPopup.add(_renameFolderItem);
        _deleteFolderItem = new JMenuItem("Delete");
        _deleteFolderItem.addActionListener(this);
        _functionsPopup.add(_deleteFolderItem);

        _functionPopup = new JPopupMenu();
        _duplicateBehaviorItem = new JMenuItem("Duplicate Behavior");
        _duplicateBehaviorItem.addActionListener(this);
        _functionPopup.add(_duplicateBehaviorItem);
        _insertParameterItem = new JMenuItem("Insert Parameter");
        _insertParameterItem.addActionListener(this);
        _functionPopup.add(_insertParameterItem);
        _functionPopup.addSeparator();
        _renameFunctionItem = new JMenuItem("Rename");
        _renameFunctionItem.addActionListener(this);
        _functionPopup.add(_renameFunctionItem);
        _deleteFunctionItem = new JMenuItem("Delete");
        _deleteFunctionItem.addActionListener(this);
        _functionPopup.add(_deleteFunctionItem);
        
        _findFuntionOccurrencesItem = new JMenuItem("Find");
        _findFuntionOccurrencesItem.addActionListener(this);
        _functionPopup.add(_findFuntionOccurrencesItem);
        _functionPopup.addSeparator();
        
        initRetTypeSubMenu();

        _execSubmenu = new JMenu("Set Execution");
        ButtonGroup group = new ButtonGroup();
        int length = SB_Behavior.kExecNames.length;
        _execItems = new JRadioButtonMenuItem[length];
        for (int i = 0; i < length; ++i)
        {
            _execItems[i] = new JRadioButtonMenuItem(SB_Behavior.kExecNames[i]);
            _execItems[i].addActionListener(this);
            group.add(_execItems[i]);
            _execSubmenu.add(_execItems[i]);
        }
        _functionPopup.add(_execSubmenu);

        _interruptSubmenu = new JMenu("Set Interruptibility");
        group = new ButtonGroup();
        length = SB_Behavior.kInterruptNames.length;
        _interruptItems = new JRadioButtonMenuItem[length];
        for (int i = 0; i < length; ++i)
        {
            _interruptItems[i] = new JRadioButtonMenuItem(SB_Behavior.kInterruptNames[i]);
            _interruptItems[i].addActionListener(this);
            group.add(_interruptItems[i]);
            _interruptSubmenu.add(_interruptItems[i]);
        }
        _functionPopup.add(_interruptSubmenu);

        _descriptionSeparator = new JPopupMenu.Separator();
        _functionPopup.add(_descriptionSeparator);
        _descriptionItem = new JMenuItem("Set Description...");
        _descriptionItem.addActionListener(this);
        _functionPopup.add(_descriptionItem);

        if (SimBionicEditor.DEV)
        {
            _reservedSeparator = new JPopupMenu.Separator();
            _functionPopup.add(_reservedSeparator);
            _reservedItem = new JCheckBoxMenuItem("Reserved");
            _reservedItem.addActionListener(this);
            _functionPopup.add(_reservedItem);
        }

        _variablePopup = new JPopupMenu();
        _renameVariableItem = new JMenuItem("Rename");
        _renameVariableItem.addActionListener(this);
        _variablePopup.add(_renameVariableItem);
        _deleteVariableItem = new JMenuItem("Delete");
        _deleteVariableItem.addActionListener(this);
        _variablePopup.add(_deleteVariableItem);
        _variablePopup.addSeparator();

        initTypeSubMenu();

        group = new ButtonGroup();

        _valueItem = new JMenuItem("Set Value...");
        _valueItem.addActionListener(this);
        _variablePopup.add(_valueItem);
        _initialValueItem = new JMenuItem("Set Initial Value...");
        _initialValueItem.addActionListener(this);
        _variablePopup.add(_initialValueItem);
        _variableDescriptionItem = new JMenuItem("Set Description...");
        _variableDescriptionItem.addActionListener(this);
        _variablePopup.add(_variableDescriptionItem);
        
        _findVariablesOccurrencesItem = new JMenuItem("Find");
        _findVariablesOccurrencesItem.addActionListener(this);
        _variablePopup.add(_findVariablesOccurrencesItem);

        _variablePopup.addSeparator();
        _moveUpItem = new JMenuItem("Move Up");
        _moveUpItem.addActionListener(this);
        _variablePopup.add(_moveUpItem);
        _moveDownItem = new JMenuItem("Move Down");
        _moveDownItem.addActionListener(this);
        _variablePopup.add(_moveDownItem);
        
        addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent event)
            {
                // getEditor().clearEditItems();
            }

            public void focusLost(FocusEvent event)
            {
            }
        });

        _dragSource = new DragSource();
        _dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
        _dropTarget = new DropTarget(this, this);
        
        // allow double-click to expand and close the selected node.
        setToggleClickCount(2);
    }
    
    private void initTypeSubMenu(){
        _typeSubmenu = new JMenu("Set Type");
        _variablePopup.add(_typeSubmenu);
    }
    
    private void initRetTypeSubMenu(){
        _retTypeSubmenu = new JMenu("Set Return Type");
        _functionPopup.add(_retTypeSubmenu);
    }
    
    /**
     * @deprecated
     */
    private void populateTypeSubMenu(){
        ButtonGroup group = new ButtonGroup();
        int length = SB_Variable.kTypeNames.length;
        _typeItems = new JMenuItem[length];
        for (int i = 0; i < length; ++i)
        {
            if (i == SB_Variable.kUser)
            {
                // @kp 1.25.2005 removed because this functionality isn't
                // implemented yet
                _typeItems[i] = new JMenu("User Types");
                ((JMenu) _typeItems[i]).add("type 1");
                ((JMenu) _typeItems[i]).add("type 2");
                ((JMenu) _typeItems[i]).add("type 3");
                continue;
            } else
            {
                _typeItems[i] = new JRadioButtonMenuItem(SB_Variable.kTypeNames[i]);
                _typeItems[i].addActionListener(this);
                group.add(_typeItems[i]);
            }
            _typeSubmenu.add(_typeItems[i]);
        }
    }
    
    /*
     * Populates the type submenu with the types get from type manager.
     * Used to replace populateTypeSubMenu().
     */
    private void populateTypeSubMenu2(boolean isConstant, boolean isParam)
    {
    	_typeSubmenuButtonGroup = new ButtonGroup();
    	ArrayList items = _typeManager.getVarComboItems(isConstant, isParam);
    	_typeItems = new JMenuItem[items.size()];
    	for (int i = 0; i < _typeItems.length; i ++)
    	{
    		// creates all menu items;
    	      String typeName = (String)items.get(i);
            _typeItems[i] = new JRadioButtonMenuItem(typeName);
            _typeItems[i].addActionListener(this);
            _typeSubmenuButtonGroup.add(_typeItems[i]);
            if (_typeManager.isBaseType(typeName)) {
               _typeSubmenu.add(_typeItems[i]);
            } else {
               _typeSubmenuButtonGroup.add(_typeItems[i]);
            }
    	}
    	
    	// now, creates package sub-menus
    	HashMap packageMenus = new HashMap();
    	String[] packageItems = _typeManager.getPackageItems();
    	for (int i = 0; i < packageItems.length; i ++)
    	{
    		JMenu packageMenu = new JMenu(packageItems[i]);
    		packageMenus.put(packageItems[i], packageMenu);
    	}
    	
    	// now, adds class menus in proper package sub-menus
    	for (int i = 0; i < items.size(); i ++)
    	{
    	  String name = _typeManager.varComboIndexToName(i, isConstant, isParam);
    	  if (!_typeManager.isBaseType(name)) {

    	     String typePackage = _typeManager.getTypePackage(name);
    	     int idx = typePackage.lastIndexOf('.');
    	     String packageName = (idx < 0 ? "(default package)" : typePackage.substring(0, idx));
    	     JMenu packageMenu = (JMenu) packageMenus.get(packageName);
    	     int j = 0;
    	     for (; j < packageMenu.getItemCount(); j++)
    	     {
    	        if (_typeItems[i].getText().compareTo(packageMenu.getItem(j).getText()) < 0)
    	           break;
    	     }
    	     packageMenu.insert(_typeItems[i], j);
    	   }
    	}
    	
    	// now, adds package sub-menus if it is not empty
    	for (int i = 0; i < packageItems.length; i ++)
    	{
    		JMenu packageMenu = (JMenu) packageMenus.get(packageItems[i]);
    		if (packageMenu.getItemCount() > 0)
    			_typeSubmenu.add(packageMenu);
    	}
    }

    /*
     * Call this method to clear type sub menu items before 
     * re-populating menu items to prevent memory leaks.
     */
    private void clearTypeSubMenu(){
    	if (_typeItems != null)
    	{
    		for (int i = 0; i < _typeItems.length; i ++)
    		{
    			_typeItems[i].removeActionListener(this);
    			_typeSubmenuButtonGroup.remove(_typeItems[i]);
    			_typeItems[i] = null;
    		}
    		
    		_typeSubmenu.removeAll();
    		_typeSubmenuButtonGroup = null;
    		_typeItems = null;
    	}
    }

    /**
     * @deprecated
     */
    private void populateRetTypeSubMenu(){
        ButtonGroup group = new ButtonGroup();
        int length = SB_Variable.kTypeNames.length;
        _retTypeItems = new JRadioButtonMenuItem[length];
        for (int i = 0; i < length; ++i)
        {
            _retTypeItems[i] = new JRadioButtonMenuItem(SB_Variable.kTypeNames[i]);
            _retTypeItems[i].addActionListener(this);
            group.add(_retTypeItems[i]);
            _retTypeSubmenu.add(_retTypeItems[i]);
        }
    }
    
    /*
     * Populates the return type submenu with the types get from type manager.
     * Used to replace populateRetTypeSubMenu().
     */
    private void populateRetTypeSubMenu2(){
    	_retTypeSubmenuButtonGroup = new ButtonGroup();
    	ArrayList items = _typeManager.getReturnValueComboItems();
    	_retTypeItems = new JRadioButtonMenuItem[items.size()];
    	for (int i = 0; i < _retTypeItems.length; i ++)
    	{
    		// creates all menu items;
    	   String typeName = (String)items.get(i);
    		_retTypeItems[i] = new JRadioButtonMenuItem(typeName);
    		_retTypeItems[i].addActionListener(this);
    		
    		if (_typeManager.isBaseType(typeName)) {
    		  _retTypeSubmenu.add(_retTypeItems[i]);  
    		} else {
    		  _retTypeSubmenuButtonGroup.add(_retTypeItems[i]);   
    		}
    	}
    	
    	// now, creates package sub-menus
    	HashMap packageMenus = new HashMap();
    	String[] packageItems = _typeManager.getPackageItems();
    	for (int i = 0; i < packageItems.length; i ++)
    	{
    		JMenu packageMenu = new JMenu(packageItems[i]);
    		packageMenus.put(packageItems[i], packageMenu);
    	}
    	
    	// now, adds class menus in proper package sub-menus
    	for (int i = 0; i < items.size(); i ++)
    	{
    	   String name = _typeManager.returnValueComboIndexToName(i);
    	   if (!_typeManager.isBaseType(name)) {
    	      String typePackage = _typeManager.getTypePackage(name);
    	      int idx = typePackage.lastIndexOf('.');
    	      String packageName = (idx < 0 ? "(default package)" : typePackage.substring(0, idx));
    	      JMenu packageMenu = (JMenu) packageMenus.get(packageName);
    	      if (packageMenu == null) return; // UI is not ready yet.
    	      int j = 0;
    	      for (; j < packageMenu.getItemCount(); j++)
    	      {
    	         if (_retTypeItems[i].getText().compareTo(packageMenu.getItem(j).getText()) < 0)
    	            break;
    	      }
    	      packageMenu.insert(_retTypeItems[i], j);
    		}
    	}
    	
    	// now, adds package sub-menus if it is not empty
    	for (int i = 0; i < packageItems.length; i ++)
    	{
    		JMenu packageMenu = (JMenu) packageMenus.get(packageItems[i]);
    		if (packageMenu.getItemCount() > 0)
    			_retTypeSubmenu.add(packageMenu);
    	}
    }
    
    /*
     * Call this method to clear return type sub menu items before 
     * re-populating menu items to prevent memory leaks.
     */
    private void clearRetTypeSubMenu(){
    	if (_retTypeItems != null)
    	{
    		for (int i = 0; i < _retTypeItems.length; i ++)
    		{
    			_retTypeItems[i].removeActionListener(this);
    			_retTypeSubmenuButtonGroup.remove(_retTypeItems[i]);
    			_retTypeItems[i] = null;
    		}
    		
    		_retTypeSubmenu.removeAll();
    		_retTypeSubmenuButtonGroup = null;
    		_retTypeItems = null;
    	}
    }
    
    protected SimBionicEditor getEditor()
    {
        return  _editor;
    }

    protected void newCatalog()
    {
        _root.setUserObject("Catalog");

        try
        {
            loadBase();
        } catch (Exception exception)
        {
            System.err.println("error parsing core file: " + exception.getMessage());
            exception.printStackTrace();
        }
        newBehaviors();
        newGlobals();
        newConstants();
        newTypes();
      

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.reload();

        SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
        if (tabbedCanvas != null)
        {
            tabbedCanvas._behavior = null;
            tabbedCanvas.setBehavior(_main, false);
        }
    }
    
  

    /**
     * Load the file that contains the core actions and predicates.
     * @throws Exception
     */
    protected void loadBase() throws Exception
    {
        _actions.removeAllChildren();
        _predicates.removeAllChildren();
        
        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.reload();
        
        try
        {
           SimBionicJava baseDataModel = 
              XMLObjectConverter.getInstance().XMLToObject(new File(CORE_ACTIONS_PREDICATES_FILE));

           ActionFolderGroup actions = baseDataModel.getActions();
           for(int x = 0; x < actions.size(); x++) {
        	   
        	   ActionFolder coreFolder = (ActionFolder)actions.getActionOrActionFolder().get(x);
               addActionFolder(coreFolder, _actions); 
           }
           
           PredicateFolderGroup predicates = baseDataModel.getPredicates();
           for(int x = 0; x < predicates.size(); x++) {
        	   PredicateFolder corePredicateFolder = (PredicateFolder)predicates.getPredicateOrPredicateFolder().get(x);
               addPredicateFolder(corePredicateFolder, _predicates);
           }
                   
           // update the current dataModel with the base model
           SimBionicJava dataModel = getDataModel();
           dataModel.setActions(actions);
           dataModel.setPredicates(predicates);

        } catch (FileNotFoundException exception)
        {
            System.err.println("file not found: " + CORE_ACTIONS_PREDICATES_FILE);
        } catch (IOException exception)
        {
            System.err.println("i/o exception");
        } 
      
    }

    protected void newBehaviors()
    {
        _behaviors.removeAllChildren();
        Behavior mainBehavior = new Behavior();
        mainBehavior.setName("Main");
        BehaviorFolderGroup behaviors = getDataModel().getBehaviors();
        behaviors.addBehavior(mainBehavior);
        
        _main = new SB_Behavior(mainBehavior);
        _main.setMain(true);
        _main.setEditor(getEditor());
        _behaviors.add(new DefaultMutableTreeNode(_main));
    }

    protected void newGlobals()
    {
       // update the model
       SimBionicJava dataModel = getDataModel();
       dataModel.getGlobals().getGlobalOrGlobalFolder().clear();
       
        _globals.removeAllChildren();
        String type = SB_TypeManager.getStringTypeName();
        SB_Global global = createGlobal("gEmpty", type, true, _globals);
        global.setInitial(global.getName().substring(1));
        _globals.add(new DefaultMutableTreeNode(global));
    }

    /**
     * 2018-05-07 -jmm
     * <br>
     * Renamed to createGlobal by analogy to
     * {@link #createFolder(Object, DefaultMutableTreeNode) createFolder}
     * and {@link #createConstant(String, DefaultMutableTreeNode)
     * createConstant}, two methods that modify the SimBionicJava model but
     * delegate UI changes to
     * {@link #addToCatalog(DefaultMutableTreeNode, UserObject, boolean)
     * addToCatalog}.
     * */
    private SB_Global createGlobal(String globalName, String type,
            boolean polymorphic, DefaultMutableTreeNode parentNode) {

        Global globalModel = new Global();
        globalModel.setName(globalName);
        globalModel.setType(type);
        globalModel.setPolymorphic(polymorphic);
        if (polymorphic) {
            globalModel.setInitial(globalName.substring(1));
        }

        Object userObject = parentNode.getUserObject();
        SimBionicJava dataModel = getDataModel();

        if (userObject instanceof SB_Folder
            && ((SB_Folder) userObject).getDataModel()
               instanceof GlobalFolder) {
            GlobalFolder constFolderModel
            = (GlobalFolder) ((SB_Folder) userObject).getDataModel();
            constFolderModel.getGlobalChildren().addGlobal(globalModel);
        } else if (userObject.equals("Globals")) {
            dataModel.getGlobals().addGlobal(globalModel);
        } else {
            throw new IllegalArgumentException(
                    "Parent node has invalid userObject.");
        }

        return new SB_Global(globalModel);
    }

    protected void newConstants()
    {
       // update the model
       SimBionicJava dataModel = getDataModel();
       dataModel.getConstants().getConstantOrConstantFolder().clear();

        _constants.removeAllChildren();
    }
    
    protected void newTypes(){
       _rootPackage = new SB_Package();
       _typeManager.reset();
    }

    /**
     * Update this UI with the specified data model.
     * @param dataModel SimBionicJava data model.
     */
    protected void open(SimBionicJava dataModel) {
    	// clear all.
       _root.removeAllChildren();
       
       // actions
       _actions.removeAllChildren();
       ActionFolderGroup actions = dataModel.getActions();
       for (Object obj : actions.getActionOrActionFolder()) {
          if (obj instanceof Action) {
             addAction((Action)obj, _actions);
          } else { // folder
             addActionFolder((ActionFolder)obj, _actions);
          }
       }
       
       // predicates
       _predicates.removeAllChildren();
       PredicateFolderGroup predicates = dataModel.getPredicates();
       for (Object obj : predicates.getPredicateOrPredicateFolder()) {
          if (obj instanceof Predicate) {
             addPredicate((Predicate)obj, _predicates);
          } else { // folder
             addPredicateFolder((PredicateFolder)obj, _predicates);
          }
       }
       
       // behaviors
       _behaviors.removeAllChildren();
       BehaviorFolderGroup behaviors = dataModel.getBehaviors();
       for (Object obj : behaviors.getBehaviorOrBehaviorFolder()) {
          if (obj instanceof Behavior) {
             addBehavior((Behavior)obj, _behaviors);
          } else { // folder
             addBehaviorFolder((BehaviorFolder)obj, _behaviors);
          }
       }
       
       String mainName = dataModel.getMain();
       _main = findBehavior(mainName);
       _main.setMain(true);
       _main.setEditor(getEditor());
       if (_main == null)
       {
    	   // when there's no main bheavior, create a new behavior to be main.
           JOptionPane.showMessageDialog(ComponentRegistry.getFrame(),
               "New main behavior will be inserted.   ", "Missing Main Behavior",
               JOptionPane.WARNING_MESSAGE);
           Behavior model = new Behavior();
           model.setName("Main");
           _main = new SB_Behavior(model);
           _main.setMain(true);
           _main.setEditor(this.getEditor());
           _main.getPoly(0).setIndices(ComponentRegistry.getProjectBar()._descriptors
                   .getBasePolyIndices());
           _main.setNameToNextAvailable(_behaviors);
           DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(_main);
           insertNodeInto(mainNode, _behaviors);
       }

       SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
       tabbedCanvas._behavior = null;
       tabbedCanvas.setBehavior(_main, false);

       // constants
       _constants.removeAllChildren();
       ConstantFolderGroup constants = dataModel.getConstants();
       for (Object obj : constants.getConstantOrConstantFolder()) {
           if (obj instanceof Constant) {
               addConstant((Constant) obj, _constants);
           } else {
               // add folder
               addConstantFolder((ConstantFolder) obj, _constants);
           }
       }

       // globals
       _globals.removeAllChildren();
       GlobalFolderGroup globals = dataModel.getGlobals();
       for (Object obj : globals.getGlobalOrGlobalFolder()) {
           if (obj instanceof Global) {
               addGlobal((Global) obj, _globals);
           } else {
               // folder
               addGlobalFolder((GlobalFolder) obj, _globals);
           }
       }

       _root.add(_actions);
       _root.add(_predicates);
       _root.add(_behaviors);
       _root.add(_globals);
       _root.add(_constants);

       // types
       newTypes();
       registerfromJavaScript(dataModel.getJavaScript());
       
       DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
       treeModel.reload();
    }
    
    /**
     * Add the specified Action model to the parent node as its child.
     * @param actionModel Action Model to add.
     * @param parentNode The parent node for the action model.
     */
    private void addAction(Action actionModel, DefaultMutableTreeNode parentNode) {
       SB_Action sbAction = new SB_Action(actionModel);
       
       DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(sbAction);
       insertNodeInto(treeNode, parentNode, false);
       
       // add parameters
       for (Parameter param : actionModel.getParameters()) {
          SB_Parameter sbParam = new SB_Parameter(param);
          treeNode.add(new DefaultMutableTreeNode(sbParam));
       }
    }
    
    /**
     * Add the specified ActionFolder model to the parent node as its child.
     * @param actionFolderModel ActionFolder model to add.
     * @param parentNode The parent node for the actionFolder model.
     */
    private void addActionFolder(ActionFolder actionFolderModel, DefaultMutableTreeNode parentNode) {
       // create folder
       SB_Folder folder = new SB_Folder(actionFolderModel);
       boolean isCore = actionFolderModel.getName().equals(CORE_ACTIONS);
       folder.setEditable(!isCore);
       DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
       // add to the parent node
       parentNode.add(node);
       
       ActionFolderGroup children = actionFolderModel.getActionChildren();
       if (children != null) {
    	   // add action folder's children to the appropriate UI node.
          for (Object child : children.getActionOrActionFolder()) {
             if (child instanceof Action) {
                addAction((Action)child,  node);
             } else {
                addActionFolder((ActionFolder)child, node);
             }
          }
       }
    }
    
    /**
     * Add the specified Predicate model to the parent node as its child.
     * @param predicateModel Predicate model to add.
     * @param parentNode The parent node for the predicate model.
     */
    private void addPredicate(Predicate predicateModel, DefaultMutableTreeNode parentNode) {
       SB_Predicate sbPredicate = new SB_Predicate(predicateModel);
       
       DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(sbPredicate);
       insertNodeInto(treeNode, parentNode, false);
       
       // add parameters
       for (Parameter param : predicateModel.getParameters()) {
          SB_Parameter sbParam = new SB_Parameter(param);
          treeNode.add(new DefaultMutableTreeNode(sbParam));
       }
    }
    
    
    /**
     * Add the specified PredicateFolder model to the parent node as its child.
     * @param predicateFolderModel PredicateFolder model to add.
     * @param parentNode The parent node for the predicateFolder model.
     */
    private void addPredicateFolder(PredicateFolder predicateFolderModel, DefaultMutableTreeNode parentNode) {
       // create folder
       SB_Folder folder = new SB_Folder(predicateFolderModel);
       boolean isCore = predicateFolderModel.getName().equals(CORE_PREDICATES);
       folder.setEditable(!isCore);
       DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
       // add to the parent node
       parentNode.add(node);
       
       PredicateFolderGroup children = predicateFolderModel.getPredicateChildren();
       if (children != null) {
          for (Object child : children.getPredicateOrPredicateFolder()) {
             if (child instanceof Predicate) {
                addPredicate((Predicate)child,  node);
             } else {
                addPredicateFolder((PredicateFolder)child, node);
             }
          }
       }
    }
    
    /**
     * Add the specified Behavior model to the parent node as its child.
     * @param behaviorModel Behavior model to add.
     * @param parentNode The parent node for the behavior model.
     */
    private void addBehavior(Behavior behaviorModel, DefaultMutableTreeNode parentNode) {
       SB_Behavior sbBehavior = new SB_Behavior(behaviorModel);
       sbBehavior.setEditor(getEditor());
       
       DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(sbBehavior);
       insertNodeInto(treeNode, parentNode, false);
       
       // create and nodes for parameters
       for (Parameter param : behaviorModel.getParameters()) {
          SB_Parameter sbParam = new SB_Parameter(param);
          treeNode.add(new DefaultMutableTreeNode(sbParam));
       }
    }
    
    /**
     * Add the specified BehaviorFolder model to the parent node as its child.
     * @param behaviorFolderModel BehaviorFolder model to add.
     * @param parentNode The parent node for the behaviorFolder model.
     */
    private void addBehaviorFolder(BehaviorFolder behaviorFolderModel, DefaultMutableTreeNode parentNode) {
       // create folder
       SB_Folder folder = new SB_Folder(behaviorFolderModel);
       DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
       // add to the parent node
       insertNodeInto(node, parentNode);
       
       BehaviorFolderGroup children = behaviorFolderModel.getBehaviorChildren();
       if (children != null) {
          for (Object child : children.getBehaviorOrBehaviorFolder()) {
             if (child instanceof Behavior) {
                addBehavior((Behavior)child,  node);
             } else {
                addBehaviorFolder((BehaviorFolder)child, node);
             }
          }
       }
    }

    /**
     * Add the specified Constant model to the parent node as its child.
     * @param constantModel Constant Model to add.
     * @param parentNode The parent node for the action model.
     */
    private void addConstant(Constant constantModel,
            DefaultMutableTreeNode parentNode) {
        SB_Constant sbConstant = new SB_Constant(constantModel);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sbConstant);
        insertNodeInto(node, parentNode, false);
    }

    /**
     * Use the provided ConstantFolder model to create a SB_Folder to add to the
     * parent node as a child. Copies recursive behavior of
     * {@link #addBehavior(Behavior, DefaultMutableTreeNode)}
     */
    private void addConstantFolder(ConstantFolder constantFolderModel,
            DefaultMutableTreeNode parentNode) {
        // create folder
        SB_Folder folder = new SB_Folder(constantFolderModel);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        // add to the parent node
        insertNodeInto(node, parentNode);

        ConstantFolderGroup children
        = constantFolderModel.getConstantChildren();
        if (children != null) {
            for (Object child : children.getConstantOrConstantFolder()) {
                if (child instanceof Constant) {
                    addConstant((Constant) child, node);
                } else {
                    addConstantFolder((ConstantFolder) child, node);
                }
            }
        }
    }

    /**
     * Add the specified Global model to the parent node as its child.
     * @param globalModel Global Model to add.
     * @param parentNode The parent node for the action model.
     */
    private void addGlobal(Global globalModel,
            DefaultMutableTreeNode parentNode) {
        SB_Global sbGlobal = new SB_Global(globalModel);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sbGlobal);
        insertNodeInto(node, parentNode, false);
    }

    /**
     * Use the provided GlobalFolder model to create a SB_Folder to add to the
     * parent node as a child. Copies recursive behavior of
     * {@link #addBehavior(Behavior, DefaultMutableTreeNode)}
     */
    private void addGlobalFolder(GlobalFolder globalFolderModel,
            DefaultMutableTreeNode parentNode) {
        // create folder
        SB_Folder folder = new SB_Folder(globalFolderModel);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        // add to the parent node
        insertNodeInto(node, parentNode);

        GlobalFolderGroup children
        = globalFolderModel.getGlobalChildren();
        if (children != null) {
            for (Object child : children.getGlobalOrGlobalFolder()) {
                if (child instanceof Global) {
                    addGlobal((Global) child, node);
                } else {
                    addGlobalFolder((GlobalFolder) child, node);
                }
            }
        }
    }

    public Enumeration getBehaviors()
    {
        if (_behaviors != null)
            return _behaviors.preorderEnumeration();
        return null;
    }

    protected void mouseRightPressed(DefaultMutableTreeNode treeNode, int x, int y)
    {
    	// update the menu items depending on the current tree node selection
    	// and show appropriate popup menu.
        if (treeNode == _actions || treeNode == _predicates
            || treeNode == _behaviors
            || treeNode == _globals
            || treeNode == _constants)
        {
            _insertActionItem.setVisible(false);
            _insertPredicateItem.setVisible(false);
            _insertBehaviorItem.setVisible(false);
            _duplicateBehaviorItem.setVisible(false);
            _insertGlobalItem.setVisible(false);
            _insertConstantItem.setVisible(false);
            _separator.setVisible(false);
            _renameFolderItem.setVisible(false);
            _deleteFolderItem.setVisible(false);

            if (treeNode == _actions)
                _insertActionItem.setVisible(true);
            else if (treeNode == _predicates)
                _insertPredicateItem.setVisible(true);
            else if (treeNode == _constants) {
                _insertConstantItem.setVisible(true);
            } else if (treeNode == _globals) {
                _insertGlobalItem.setVisible(true);
            }
            else
            {
                _insertBehaviorItem.setVisible(true);
            }

            _functionsPopup.show(this, x, y);
        }
        else if (treeNode == _root) {
           _rootPopup.show(this, x, y);
        }
    }

    /**
     * TODO: Refactor this in an object-oriented fashion.
     */
    protected void mouseRightPressed(UserObject userObject, int x, int y)
    {
        boolean editable = userObject.isCellEditable();
        _duplicateBehaviorItem.setVisible(false);
        _findFuntionOccurrencesItem.setVisible(false);
        _findVariablesOccurrencesItem.setVisible(false);
        
        if (userObject instanceof SB_Function) {      
        	 _findFuntionOccurrencesItem.setVisible(true);
            if (userObject instanceof SB_Action) {
                _retTypeSubmenu.setVisible(false);
                _deleteFunctionItem.setEnabled(editable);
                _execSubmenu.setVisible(false);
                _interruptSubmenu.setVisible(false);
                _descriptionSeparator.setVisible(false);
            } 
            else if (userObject instanceof SB_Predicate) {
                // recreate return type menus
                clearRetTypeSubMenu();
                populateRetTypeSubMenu2();
                
                String type = ((SB_Predicate)userObject).getRetType();
                _retTypeItems[_typeManager.nameToReturnValueComboIndex(type, false)].setSelected(true);
                setItemsEnabled(_retTypeItems, editable);
                _retTypeSubmenu.setVisible(true);
                _deleteFunctionItem.setEnabled(editable);
                _execSubmenu.setVisible(false);
                _interruptSubmenu.setVisible(false);
                _descriptionSeparator.setVisible(false);
            } 
            else if (userObject instanceof SB_Behavior) {
                _retTypeSubmenu.setVisible(false);
                _deleteFunctionItem.setEnabled(editable && userObject != _main);
                SB_Behavior behavior = ((SB_Behavior) userObject);
                int exec = behavior.getExec();
                _execItems[SB_Behavior.execToIndex(exec)].setSelected(true);
                setItemsEnabled(_execItems, editable);
                _execSubmenu.setVisible(true);
                int interrupt = behavior.getInterrupt();
                _interruptItems[SB_Behavior.interruptToIndex(interrupt)].setSelected(true);
                setItemsEnabled(_interruptItems, editable);
                _interruptSubmenu.setVisible(true);
                _descriptionSeparator.setVisible(true);
                _duplicateBehaviorItem.setVisible(true);
            }
            _insertParameterItem.setEnabled(editable);
            _renameFunctionItem.setEnabled(editable);
            if (SimBionicEditor.DEV) {
                if (userObject instanceof SB_Behavior) {
                    _reservedSeparator.setVisible(false);
                    _reservedItem.setVisible(false);
                } 
                else {
                    boolean core = ((SB_Function) userObject).isCore();
                    _reservedItem.setSelected(core);
                    _reservedSeparator.setVisible(true);
                    _reservedItem.setVisible(true);
                }
            }
            _functionPopup.show(this, x, y);
        } 
        else if (userObject instanceof SB_Folder) {
            TreePath treePath = getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            _insertActionItem.setVisible(treeNode.isNodeAncestor(_actions));
            _insertPredicateItem.setVisible(treeNode.isNodeAncestor(_predicates));
            _insertBehaviorItem.setVisible(treeNode.isNodeAncestor(_behaviors));
            _insertGlobalItem.setVisible(treeNode.isNodeAncestor(_globals));
            _insertConstantItem.setVisible(
                    treeNode.isNodeAncestor(_constants));
            _separator.setVisible(true);
            _renameFolderItem.setVisible(true);
            _deleteFolderItem.setVisible(true);
            _renameFolderItem.setEnabled(editable);
            _deleteFolderItem.setEnabled(editable);
            _functionsPopup.show(this, x, y);
        } 
        else if (userObject instanceof SB_Variable) {
            _renameVariableItem.setEnabled(editable);
            _deleteVariableItem.setEnabled(editable);
            String type = ((SB_Variable)userObject).getType();
            TreePath treePath = getSelectionPath();
            int count = treePath.getPathCount();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath
                    .getPathComponent(count - 2);
            int index = treeModel.getIndexOfChild(parentNode, treeNode);
            //XXX: moveUp and moveDown disabled by default, re-enabled
            //for SB_Parameter instances only.
            _moveUpItem.setEnabled(false);
            _moveDownItem.setEnabled(false);

            if (userObject instanceof SB_Parameter) {
                //XXX: The separator cannot be easily disabled.
                _moveUpItem.setEnabled(editable && index > 0);
                _moveDownItem.setEnabled(editable && index < treeModel.getChildCount(parentNode) - 1);
                _valueItem.setVisible(false);
                _initialValueItem.setVisible(false);
            	clearTypeSubMenu();
                populateTypeSubMenu2(false, true);
                int typeIndex = _typeManager.nameToVarComboIndex(type, false, true);
                if (typeIndex >= 0) {
                	_typeItems[typeIndex].setSelected(true);
                }
                setItemsEnabled(_typeItems, editable);
            } 
            else if (userObject instanceof SB_Constant) {
            	_findVariablesOccurrencesItem.setVisible(true);
                _valueItem.setVisible(true);
                _initialValueItem.setVisible(false);
            	clearTypeSubMenu();
                populateTypeSubMenu2(true, false);
                int typeIndex = _typeManager.nameToVarComboIndex(type, true, false);
                if (typeIndex >=0) {
                	_typeItems[typeIndex].setSelected(true);
                }
                setItemsEnabled(_typeItems, editable);
            } 
            else if (userObject instanceof SB_Global) {
            	_findVariablesOccurrencesItem.setVisible(true);
                _valueItem.setVisible(false);
                _initialValueItem.setVisible(true);
            	clearTypeSubMenu();
                populateTypeSubMenu2(false, false);
                int typeIndex = _typeManager.nameToVarComboIndex(type);
                if (typeIndex >= 0 ) {
                	_typeItems[typeIndex].setSelected(true);
                }
                setItemsEnabled(_typeItems, editable);

                SB_Global global = (SB_Global) userObject;
                if (global.isPolymorphic()) {
                    //XXX: Special case for gEmpty
                    return;
                }
            }
            else {
            	clearTypeSubMenu();
                populateTypeSubMenu2(false, false);
                int typeIndex = _typeManager.nameToVarComboIndex(type);
                if (typeIndex >=0) {
                	_typeItems[typeIndex].setSelected(true);
                }
                setItemsEnabled(_typeItems, editable);
            }
            _variablePopup.show(this, x, y);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JComponent) {
            JComponent component = (JComponent) e.getSource();
            TreePath treePath = getSelectionPath();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            Object userObject = treeNode.getUserObject();
            if (component == _constantValueOK || component == _constantValueTextField) {
                SB_Constant constant = (SB_Constant) userObject;
                if (!constant.getValue().equals(_constantValueTextField.getText())) {
                    constant.setValue(_constantValueTextField.getText());
                    setAPDModified(true);
                }
                _constantValueDialog.setVisible(false);
            } 
            else if (component == _constantValueCancel) {
                _constantValueDialog.setVisible(false);
            } 
            else if (component == _initialValueOK || component == _initialValueTextField) {
                SB_Global global = (SB_Global) userObject;
                if (!global.getInitial().equals(_initialValueTextField.getText())) {
                    global.setInitial(_initialValueTextField.getText());
                    setSBPModified(true);
                }
                _initialValueDialog.setVisible(false);
            } 
            else if (component == _initialValueCancel) {
                _initialValueDialog.setVisible(false);
            } 
            else {
                super.actionPerformed(e);
            }
        }
    }

    
    /**
     * Method called when the description of an element has changed.&nbsp;The default implementation
     * is to mark the project as modified.&nbsp;Subclasses should override as needed.
     * @param descriptionHolder
     */
    @Override
    protected void onDescriptionChange( I_DescriptionHolder descriptionHolder) {
    	 if (descriptionHolder instanceof SB_Behavior) {
             ((SB_Behavior) descriptionHolder).setBTNModified(true);
    	 }
         else {
             setAPDModified(true);
         }
    }

    /**
     * Create  a predicate model and its corresponding UI object and then add it
     * to the specified tree node.
     * @param treeNode Parent tree node for the new predicate 
     * @param name Name of the new predicate.
     * @param userObject Parent user object.
     */
    protected void insertPredicate(DefaultMutableTreeNode treeNode, String name, Object userObject)
    {
    	// create a predicate model by setting the name and return type.
        String type = SB_TypeManager.getBooleanTypeName();
        Predicate predicateModel = new Predicate();
        predicateModel.setName(name);
        predicateModel.setReturnType(type);
        
        if (userObject instanceof SB_Folder) {
           // predicate folder
           SB_Folder sbFolder = (SB_Folder)userObject;
           PredicateFolder predicateFolderModel = (PredicateFolder)sbFolder.getDataModel();
           predicateFolderModel.getPredicateChildren().addPredicate(predicateModel);
        } else {
           PredicateFolderGroup predicates = getDataModel().getPredicates();
           predicates.addPredicate(predicateModel);
        }
        
        // create a corresponding UI object for the new predicate model.
        UserObject childUserObject = new SB_Predicate(predicateModel);
        ((SB_Predicate) childUserObject).setId(findUniqueId(_predicates, START_USER_ID));
        setAPDModified(true);
        childUserObject.setNameToNextAvailable(treeNode);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childUserObject);
        // insert the new node to the parent tree node.
        insertNodeInto(childNode, treeNode);
        if (childUserObject instanceof SB_Behavior)
            ComponentRegistry.getContent().setBehavior((SB_Behavior) childUserObject, true);
        else if (childUserObject instanceof SB_Variable)
            setVariableModified(childNode);
        startEditingAtPath(new TreePath(childNode.getPath()));
    }


    private SB_Constant createConstant(String constantName,
            DefaultMutableTreeNode parentNode) {

        Constant constantModel = new Constant();
        constantModel.setName(constantName);
        // default constant type is integer.
        constantModel.setType(SB_TypeManager.getIntegerTypeName());

        Object userObject = parentNode.getUserObject();
        SimBionicJava dataModel = getDataModel();

        if (userObject instanceof SB_Folder
            && ((SB_Folder) userObject).getDataModel()
                instanceof ConstantFolder) {
            ConstantFolder constFolderModel
            = (ConstantFolder) ((SB_Folder) userObject).getDataModel();
            constFolderModel.getConstantChildren().addConstant(constantModel);
        } else if (userObject.equals("Constants")) {
            dataModel.getConstants().addConstant(constantModel);
        } else {
            throw new IllegalArgumentException(
                    "Parent node has invalid userObject.");
        }

        return new SB_Constant(constantModel);
    }

    public SB_Behavior insertBehavior(String folderName, String behaviorName)
    {
        SB_Behavior behavior = null;
        if (folderName == null)
        {
            behavior=insertBehavior(_behaviors, behaviorName, false);
        } else
        {
            DefaultMutableTreeNode folder = this.findNodeByName(folderName, _behaviors);
            if (folder != null && folder.getUserObject() instanceof SB_Folder)
            {
                behavior=insertBehavior(folder, behaviorName, false);
            }
        }
        return behavior;
    }

    /**
     * Create  a behavior model and its corresponding UI object and then add it
     * to the specified tree node.
     * @param parentNode Parent tree node for the new behavior
     * @param name Name of the new behavior.
     */
    private SB_Behavior insertBehavior(DefaultMutableTreeNode parentNode, String name,
            boolean editNode)
    {
       Behavior behaviorModel = new Behavior();
       behaviorModel.setName(name);
       Object parentUserObject = parentNode.getUserObject();
       if (parentUserObject instanceof SB_Folder) {
          // behavior folder
          SB_Folder sbFolder = (SB_Folder)parentUserObject;
          BehaviorFolder behaviorFolderModel = (BehaviorFolder)sbFolder.getDataModel();
          behaviorFolderModel.getBehaviorChildren().addBehavior(behaviorModel);
       } else {
          BehaviorFolderGroup behaviors = getDataModel().getBehaviors();
          behaviors.addBehavior(behaviorModel);
       }

       SB_Behavior behavior = new SB_Behavior(behaviorModel);
       behavior.setEditor(getEditor());
       behavior.getPoly(0).setIndices(ComponentRegistry.getProjectBar()._descriptors.getBasePolyIndices());
       addBehavior(behavior,parentNode,editNode);
       return behavior;
    }
    
    /**
     * Initializes the given behavior and adds it to the tree.
     * @param newBehavior
     * @param parentNode
     * @param editNode true if the new node should start in edit mode
     */
    private DefaultMutableTreeNode addBehavior(SB_Behavior newBehavior,DefaultMutableTreeNode parentNode,boolean editNode)
    {
        String name = newBehavior.getName();
        newBehavior.setNameToNextAvailable(this._root);
        newBehavior.setEditor(getEditor());
        newBehavior.setBTNModified(true);
        setSBPModified(true);
        return addToCatalog(parentNode, newBehavior, editNode);        
    }

    private DefaultMutableTreeNode addToCatalog(DefaultMutableTreeNode parentNode, UserObject childUserObject,
            boolean editNode)
    {
        childUserObject.setNameToNextAvailable(parentNode);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childUserObject);
        insertNodeInto(childNode, parentNode);
        if (childUserObject instanceof SB_Behavior)
            ComponentRegistry.getContent().setBehavior((SB_Behavior) childUserObject, true);
        else if (childUserObject instanceof SB_Variable)
            setVariableModified(childNode);
        if (editNode)
            startEditingAtPath(new TreePath(childNode.getPath()));
        return childNode;
    }
    
    /**
     * Called when the JavaScript menu item is clicked.
     */
    public void handleJavaScriptMenuItem() {
    	 SimBionicJava dataModel = getDataModel();
    	// show the javaScript dialog
    	 _javaScriptDialog.setJavaScript(dataModel.getJavaScript());
    	 showJavaScriptDialog();
    }
    
    protected void showJavaScriptDialog () {
    	 _javaScriptDialog.setVisible(true);
        if (_javaScriptDialog.wasOkClicked()) {
           
           // update javaScript
           JavaScript newJavaScript = _javaScriptDialog.getJavaScript();
           newTypes();
           if (registerfromJavaScript(newJavaScript)) {
        	   // update the javaScript model.
        	   SimBionicJava dataModel = getDataModel();
        	   dataModel.setJavaScript(newJavaScript);
        	   setSBPModified(true);	
           } else {
        	   showJavaScriptDialog();
           }
        }
    }

    protected void handleMenuItem(JMenuItem menuItem, TreePath treePath)
    {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = treeNode.getUserObject();
        SimBionicJava dataModel = getDataModel();

        UserObject childUserObject = null;
        if (menuItem == _insertActionItem)
        {
           // add a new action 
            Action actionModel = new Action();
            actionModel.setName("NewAction");
            if (userObject instanceof SB_Folder) {
               // action folder
               SB_Folder sbFolder = (SB_Folder)userObject;
               ActionFolder actionFolderModel = (ActionFolder)sbFolder.getDataModel();
               actionFolderModel.getActionChildren().addAction(actionModel);
            } else {
               dataModel.getActions().addAction(actionModel);
            }
            
            childUserObject = new SB_Action(actionModel);
            ((SB_Action) childUserObject).setId(findUniqueId(_actions, START_USER_ID));
            setAPDModified(true);
        } else if (menuItem == _insertPredicateItem)
        {
            insertPredicate(treeNode, "NewPredicate", userObject);
        } else if (menuItem == _insertBehaviorItem)
        {
            insertBehavior(treeNode, "NewBehavior", true);
        } else if (menuItem == _duplicateBehaviorItem)
        {
            duplicateBehavior(treeNode);
        } else if (menuItem == _newFolderItem)
        {
           childUserObject = createFolder(userObject, treeNode);
        } else if (menuItem == _insertConstantItem)
        {
            childUserObject = createConstant("NewConstant", treeNode);
        } else if (menuItem == _insertGlobalItem)
        {
            childUserObject = createGlobal("NewGlobal",
                    SB_TypeManager.getIntegerTypeName(), false, treeNode);
        } else if (menuItem == _insertParameterItem)
        {
           if (userObject instanceof SB_Function) {
              Function functionModel = ((SB_Function)userObject).getDataModel();
              childUserObject = createParameter("NewParameter", SB_TypeManager.getIntegerTypeName(), functionModel);  
           }
            
        } else if (menuItem == _javaScriptItem) {
        	handleJavaScriptMenuItem();
        }
        
        if (childUserObject != null)
        {
            addToCatalog(treeNode, childUserObject, true);
            return;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        int index = -1;
        if (menuItem == _renameFolderItem || menuItem == _renameFunctionItem
                || menuItem == _renameVariableItem)
        {
            startEditingAtPath(treePath);
        } else if (menuItem == _deleteFolderItem || menuItem == _deleteFunctionItem
                || menuItem == _deleteVariableItem)
        {
            deleteNode(treeNode, true);
        } else if ((index = findItemIndex(menuItem, _retTypeItems)) != -1)
        {
            SB_Predicate predicate = (SB_Predicate) userObject;
        	   String type = _typeManager.returnValueComboIndexToName(index, false);
            if (!predicate.getRetType().equals(type))
            {
                predicate.setRetType(type);
                treeModel.nodeChanged(treeNode);
                setAPDModified(true);
            } 
        } else if ((index = findItemIndex(menuItem, _execItems)) != -1)
        {
            SB_Behavior behavior = (SB_Behavior) userObject;
            int exec = behavior.getExec();
            if (SB_Behavior.execToIndex(exec) != index)
            {
                behavior.setExec(SB_Behavior.indexToExec(index));
                behavior.setBTNModified(true);
            }
        } else if ((index = findItemIndex(menuItem, _interruptItems)) != -1)
        {
            SB_Behavior behavior = (SB_Behavior) userObject;
            int interrupt = behavior.getInterrupt();
            if (SB_Behavior.interruptToIndex(interrupt) != index)
            {
                behavior.setInterrupt(SB_Behavior.indexToInterrupt(index));
                behavior.setBTNModified(true);
            }
        } else if (menuItem == _descriptionItem || menuItem == _variableDescriptionItem)
        {
            showDescriptionDialog((I_DescriptionHolder) userObject);
        } else if (menuItem == _reservedItem)
        {
            SB_Function function = (SB_Function) userObject;
            function.setCore(!function.isCore());
            int size = treeNode.getChildCount();
            for (int i = 0; i < size; ++i)
            {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
                ((SB_Variable) childNode.getUserObject()).setEditable(!function.isCore());
            }
            if (function instanceof SB_Behavior)
            {
                SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
                if (tabbedCanvas._behavior == function)
                {
                    SB_Canvas canvas = tabbedCanvas.getActiveCanvas();
                    canvas.clearSingle();
                    canvas.updateSingle();
                    // _editor.updateTitle();
                }
                ((SB_Behavior) function).updateLocalsEditable();
                ((SB_Behavior) function).setBTNModified(true);
            } else
            {
                int startId = function.isCore() ? 0 : START_USER_ID;
                if (function instanceof SB_Action)
                    function.setId(findUniqueId(_actions, startId));
                else
                    function.setId(findUniqueId(_predicates, startId));
                setAPDModified(true);
            }
        } else if ((index = findItemIndex(menuItem, _typeItems)) != -1)
        {
            SB_Variable var = (SB_Variable) userObject;
            String type = null;
            if (var instanceof SB_Parameter)
            	type = _typeManager.varComboIndexToName(index, false, true);
            else if (var instanceof SB_Constant)
            	type = _typeManager.varComboIndexToName(index, true, false);
            else
            	type = _typeManager.varComboIndexToName(index, false, false);
            if (!var.getType().equals(type))
            {
                var.setType(type);
                treeModel.nodeChanged(treeNode);
                setVariableModified(treeNode);
            } 
        } else if (menuItem == _valueItem)
        {
            showConstantValueDialog((SB_Constant) userObject);
        } else if (menuItem == _initialValueItem)
        {
            showInitialValueDialog((SB_Global) userObject);
        } 
        else if (menuItem == _moveUpItem || menuItem == _moveDownItem)
        {
           moveUpOrDown(menuItem, treePath, treeNode);
        }
        else if (menuItem == _findFuntionOccurrencesItem || menuItem == _findVariablesOccurrencesItem) {
        	findOccurrences(treeNode);
        }
    }
    
    /**
     * Handles move up and move down menus.
     */
    private void moveUpOrDown(JMenuItem menuItem, TreePath treePath, DefaultMutableTreeNode treeNode) {
       int count = treePath.getPathCount();
       DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
       DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath
               .getPathComponent(count - 2);
       int index = treeModel.getIndexOfChild(parentNode, treeNode);
       treeModel.removeNodeFromParent(treeNode);
       int newIndex = -1;
       if (menuItem == _moveUpItem) {
          newIndex = index - 1;
       }
       else {
          newIndex = index + 1;
       }
       treeModel.insertNodeInto(treeNode, parentNode, newIndex);
       setSelectionPath(new TreePath(treeNode.getPath()));
       setVariableModified(treeNode);
       
       // update the underlying data model
       SimBionicJava dataModel = getDataModel();
       Object userObject = treeNode.getUserObject();
       if (userObject instanceof SB_Global) {
          throw new RuntimeException("Manual ordering of Globals no longer"
                  + " supported");
       } else if (userObject instanceof SB_Constant) {
           throw new RuntimeException("Manual ordering of Constants no longer"
                   + " supported");
       } else if (userObject instanceof SB_Parameter) {
          Parameter parameterModel = ((SB_Parameter)userObject).getDataModel();
          Object parentUserObject = parentNode.getUserObject();
          if (parentUserObject instanceof SB_Function) {
             Function functionModel = ((SB_Function)parentUserObject).getDataModel();
             functionModel.removeParameter(parameterModel);
             functionModel.addParameter(newIndex, parameterModel);
          }
       } 
    }
    
    /**
     * find all the occurrences of the user object associated with the treeNode: update the
     * find tab with the results.
     * @param treeNode
     */
    private void findOccurrences(DefaultMutableTreeNode treeNode) {
      try {
    	 Object userObject = treeNode.getUserObject();
    	 String text = (userObject instanceof UserObject)? ((UserObject)userObject).getName() : userObject.toString();
    	 if (text.length() == 0) {
    		 return;
    	 }

    	 String strFind = text;
    	 if (userObject instanceof SB_Function) {
    		 strFind += "(\\s)*\\(";//name of the function followed by a (
    	 }

    	 int flags = 0; //matchCase
    	 Pattern pattern = Pattern.compile(strFind, flags);
    	
    	 SB_OutputBar outputBar = SB_OutputBar.getInstance();
    	 SB_Output find = SB_OutputBar._find;
    	 find.clearLines();
    	 (SB_OutputBar.getInstance()).setSelectedIndex(SB_OutputBar.FIND_INDEX);
    	 SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
    	 
    	 int total = 0;
    	 
    	 try {
    		 total = projectBar._catalog.findOccurrences(pattern, null);
    	 } 
    	 catch (SB_CancelException e) {
    		 find.addLine(new SB_Line("Search cancelled."));
    		 find.setSel(-1);
    		 outputBar.requestFocus();
    		 return;
    	 }
    	 String str = total + " occurrence";
    	 if (total == 0 || total > 1)
    		 str += "s have been found.";
    	 else
    		 str += " has been found.";
    	 find.addLine(new SB_Line(str));
    	 find.setSel(-1);
    	 outputBar.requestFocus();
      }
      catch (Exception e) {
    	  e.printStackTrace();
      }
    }
    
    private SB_Parameter createParameter(String name, String type, Function functionModel) {
       Parameter paramModel = new Parameter();
       paramModel.setName(name);
       paramModel.setType(type);
       // add the new parameter model to the function model.
       functionModel.addParameter(paramModel);
       return new SB_Parameter(paramModel);
    }
    
    /**
     * Create and return a new SB_Folder. 
     * @param userObject Parent object for the new SB_Folder.
     * @param treeNode currently selected tree node.
     * @return a new SB_Folder.
     */
    private SB_Folder createFolder(Object userObject, DefaultMutableTreeNode treeNode) {
       SimBionicJava dataModel = getDataModel();
       ActionFolderGroup actions = dataModel.getActions();
       PredicateFolderGroup predicates = dataModel.getPredicates();
       BehaviorFolderGroup behaviors = dataModel.getBehaviors();
       // create a new folder data model depending on the parent user object type.
       Folder newFolderModel = null;
       if (userObject instanceof SB_Folder) {
          SB_Folder sbFolder = (SB_Folder)userObject;
          Folder folderModel = sbFolder.getDataModel();
          
          if (folderModel instanceof ActionFolder) {
             ActionFolder actionFolderModel = (ActionFolder)sbFolder.getDataModel();
             newFolderModel = new ActionFolder();
             actionFolderModel.getActionChildren().addActionFolder((ActionFolder)newFolderModel);
          } else if (folderModel instanceof PredicateFolder) {
             PredicateFolder predicateFolderModel = (PredicateFolder)sbFolder.getDataModel();
             newFolderModel = new PredicateFolder();
             predicateFolderModel.getPredicateChildren().addPredicateFolder(
                   (PredicateFolder)newFolderModel);
          } else if (folderModel instanceof BehaviorFolder) {
             BehaviorFolder behaviorFolderModel = (BehaviorFolder)sbFolder.getDataModel();
             newFolderModel = new BehaviorFolder();
             behaviorFolderModel.getBehaviorChildren().addBehaviorFolder(
                   (BehaviorFolder)newFolderModel);
          } else if (folderModel instanceof GlobalFolder) {
              GlobalFolder behaviorFolderModel
              = (GlobalFolder) sbFolder.getDataModel();
              newFolderModel = new GlobalFolder();
              behaviorFolderModel.getGlobalChildren().addGlobalFolder(
                    (GlobalFolder) newFolderModel);
           } else if (folderModel instanceof ConstantFolder) {
               ConstantFolder behaviorFolderModel
               = (ConstantFolder) sbFolder.getDataModel();
               newFolderModel = new ConstantFolder();
               behaviorFolderModel.getConstantChildren().addConstantFolder(
                     (ConstantFolder) newFolderModel);
            }
       } else if (userObject.equals("Actions")) {
          newFolderModel = new ActionFolder();
          actions.addActionFolder((ActionFolder)newFolderModel);
       } else if (userObject.equals("Predicates")) {
          newFolderModel = new PredicateFolder();
          predicates.addPredicateFolder((PredicateFolder)newFolderModel);
       } else if (userObject.equals("Behaviors")) {
          newFolderModel = new BehaviorFolder();
          behaviors.addBehaviorFolder((BehaviorFolder)newFolderModel);
       } else if (userObject.equals("Globals")) {
           GlobalFolder newGlobalFolder = new GlobalFolder();
           dataModel.getGlobals().addGlobalFolder(newGlobalFolder);
           newFolderModel = newGlobalFolder;
       } else if (userObject.equals("Constants")) {
           ConstantFolder newConstantFolder = new ConstantFolder();
           dataModel.getConstants().addConstantFolder(newConstantFolder);
           newFolderModel = newConstantFolder;
       }

       if (newFolderModel != null) {
          newFolderModel.setName("NewFolder");
          SB_Folder newSBFolder = new SB_Folder(newFolderModel);
          setFolderModified(treeNode);
          return newSBFolder;
       }
       
       return null;
    }

    /**
     * Duplicates the selected behavior, placing a copy in the Catalog.
     * @param behaviorNode the behavior to copy
     */
    private void duplicateBehavior(DefaultMutableTreeNode behaviorNode)
    {
        SB_Behavior origBehavior = (SB_Behavior)behaviorNode.getUserObject();
        SB_Behavior copyBehavior = (SB_Behavior)origBehavior.clone();
        
        // Update data model
        Object parentUserObject = ((DefaultMutableTreeNode) behaviorNode.getParent()).getUserObject();
        if (parentUserObject instanceof SB_Folder) {
           // behavior folder
           SB_Folder sbFolder = (SB_Folder)parentUserObject;
           BehaviorFolder behaviorFolderModel = (BehaviorFolder)sbFolder.getDataModel();
           behaviorFolderModel.getBehaviorChildren().addBehavior(copyBehavior.getBehaviorModel());
        } else {
           BehaviorFolderGroup behaviors = getDataModel().getBehaviors();
           behaviors.addBehavior(copyBehavior.getBehaviorModel());
        }
        
        DefaultMutableTreeNode copyNode = addBehavior(copyBehavior,(DefaultMutableTreeNode)behaviorNode.getParent(),true);
        
        // now clone the parameters
        for (int i=0; i < behaviorNode.getChildCount(); ++i)
        {
            DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode)behaviorNode.getChildAt(i);
            SB_Parameter copyParam = (SB_Parameter)((SB_Parameter)paramNode.getUserObject()).clone();
            addToCatalog(copyNode,copyParam,false);
        }
    }

    public void deleteNode(DefaultMutableTreeNode treeNode, boolean checkFolder)
    {
        Object userObject = treeNode.getUserObject();
        Object parentUserObject = ((DefaultMutableTreeNode)treeNode.getParent()).getUserObject();

        SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
        if (userObject instanceof SB_Folder)
        {
            if (checkFolder && !shouldDeleteFolder(treeNode))
                return;
            if (treeNode.isNodeAncestor(_behaviors))
            {
                if (findNode(tabbedCanvas._behavior, _behaviors).isNodeAncestor(treeNode))
                    tabbedCanvas.setBehavior(_main, true);
                SB_Behavior behavior = null;
                Enumeration e = treeNode.preorderEnumeration();
                DefaultMutableTreeNode next = (DefaultMutableTreeNode) e.nextElement();
                ;
                while (e.hasMoreElements())
                {
                    next = (DefaultMutableTreeNode) e.nextElement();
                    if (next.getUserObject() instanceof SB_Behavior)
                    {
                        behavior = (SB_Behavior) next.getUserObject();
                        // FIXME was this important? why is it commented out?
                        // getToolBar().removeBehavior(behavior);
                    }
                }
            }
            
            deleteFolder(userObject, parentUserObject);
            setSBPModified(true);
        } else if (userObject instanceof SB_Behavior)
        {
            SB_Behavior behavior = (SB_Behavior) userObject;
            if (tabbedCanvas._behavior == behavior)
                tabbedCanvas.setBehavior(_main, true);
            // getToolBar().removeBehavior(behavior);
            
            // remove the behavior model from its parent
            Behavior behaviorModel = behavior.getBehaviorModel();
            if (parentUserObject instanceof SB_Folder) {
               SB_Folder sbFolder = (SB_Folder)parentUserObject;
               BehaviorFolder behaviorFolder = (BehaviorFolder)sbFolder.getDataModel();
               behaviorFolder.getBehaviorChildren().removeBehavior(behaviorModel);
            } else {
               BehaviorFolderGroup behaviors = getDataModel().getBehaviors();
               behaviors.removeBehavior(behaviorModel);
            }
            setSBPModified(true);
        } else if (userObject instanceof SB_Action) {
        	// remove the action model from its parent.
           Action actionModel = ((SB_Action)userObject).getActionModel();
           if (parentUserObject instanceof SB_Folder) {
              SB_Folder sbFolder = (SB_Folder)parentUserObject;
              ActionFolder actionFolder = (ActionFolder)sbFolder.getDataModel();
              actionFolder.getActionChildren().removeAction(actionModel);
           } else {
              ActionFolderGroup actions = getDataModel().getActions();
              actions.removeAction(actionModel);
           }
           setAPDModified(true);
        } else if (userObject instanceof SB_Predicate) {
        	// remove the predicate model from its parent.
           Predicate predicateModel = ((SB_Predicate)userObject).getPredicateModel();
           if (parentUserObject instanceof SB_Folder) {
              SB_Folder sbFolder = (SB_Folder)parentUserObject;
              PredicateFolder predicateFolder = (PredicateFolder)sbFolder.getDataModel();
              predicateFolder.getPredicateChildren().removePredicate(predicateModel);
           } else {
              PredicateFolderGroup predicates = getDataModel().getPredicates();
              predicates.removePredicate(predicateModel);
           }
           setAPDModified(true);
        }
        else if (userObject instanceof SB_Variable) {
            setVariableModified(treeNode);
            
            // update the data model
            SimBionicJava dataModel = getDataModel();
            if (userObject instanceof SB_Global) {
            	// remove global
               Global model = ((SB_Global) userObject).getGlobalModel();
               GlobalFolderGroup globals = getDataModel().getGlobals();
               globals.removeGlobal(model);
            } else if (userObject instanceof SB_Constant) {
            	// remove constant
               Constant model = ((SB_Constant) userObject).getConstantModel();
               ConstantFolderGroup constants = getDataModel().getConstants();
               constants.removeConstant(model);
            } else {
            	// remove parameter
               Parameter paramModel = ((SB_Variable) userObject).getDataModel();
               if (parentUserObject instanceof SB_Function) {
                  Function functionModel = ((SB_Function)parentUserObject).getDataModel();
                  functionModel.removeParameter(paramModel);
               } 
            }
            
        }
        
        // remove the tree node from its parent
        DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
        treeModel.removeNodeFromParent(treeNode);
        if (userObject instanceof SB_Behavior)
        {
            SB_Canvas canvas = tabbedCanvas.getActiveCanvas();
            canvas._poly.getElements().updateComplex(getEditor());
            canvas.repaint();
        }
    }
    
    private void deleteFolder(Object userObject, Object parentUserObject) {
       // update the data model structure
       Folder folderModel = ((SB_Folder)userObject).getDataModel();
       if (folderModel instanceof ActionFolder) {
    	   // delete action folder
          ActionFolder actionFolderModel = (ActionFolder)folderModel;
          if (parentUserObject instanceof SB_Folder) {
             SB_Folder sbFolder = (SB_Folder)parentUserObject;
             ActionFolder actionFolder = (ActionFolder)sbFolder.getDataModel();
             actionFolder.getActionChildren().removeActionFolder(actionFolderModel);
          } else {
             ActionFolderGroup actions = getDataModel().getActions();
             actions.removeActionFolder(actionFolderModel);
          }
          
       } else if (folderModel instanceof PredicateFolder) {
    	   // delete predicate folder
          PredicateFolder predicateFolderModel = (PredicateFolder)folderModel;
          if (parentUserObject instanceof SB_Folder) {
             SB_Folder sbFolder = (SB_Folder)parentUserObject;
             PredicateFolder predicateFolder = (PredicateFolder)sbFolder.getDataModel();
             predicateFolder.getPredicateChildren().removePredicateFolder(predicateFolderModel);
          } else {
             PredicateFolderGroup predicates = getDataModel().getPredicates();
             predicates.removePredicateFolder(predicateFolderModel);
          }
          setAPDModified(true);
       } else if (folderModel instanceof BehaviorFolder) {
    	   // delete behavior folder
          BehaviorFolder behaviorFolderModel = (BehaviorFolder)folderModel;
          if (parentUserObject instanceof SB_Folder) {
             SB_Folder sbFolder = (SB_Folder)parentUserObject;
             BehaviorFolder behaviorFolder = (BehaviorFolder)sbFolder.getDataModel();
             behaviorFolder.getBehaviorChildren().removeBehaviorFolder(behaviorFolderModel);
          } else {
             BehaviorFolderGroup behaviors = getDataModel().getBehaviors();
             behaviors.removeBehaviorFolder(behaviorFolderModel);
          }
       }
    }

    protected boolean shouldDeleteFolder(DefaultMutableTreeNode folderNode)
    {
        if (folderNode.isLeaf())
            return true;
        else if (folderNode.isNodeAncestor(_behaviors)
                && findNode(_main, _behaviors).isNodeAncestor(folderNode))
        {
            JOptionPane.showMessageDialog(ComponentRegistry.getFrame(),
                "Folder contains main behavior.   ", "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            return false;
        } else
        {
            Enumeration e = folderNode.preorderEnumeration();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
            while (e.hasMoreElements())
            {
                treeNode = (DefaultMutableTreeNode) e.nextElement();
                if (treeNode.getUserObject() instanceof SB_Function)
                {
                    SB_Function function = (SB_Function) treeNode.getUserObject();
                    if (function.isCore() && !SimBionicEditor.DEV)
                    {
                        JOptionPane.showMessageDialog(ComponentRegistry.getFrame(),
                            "Folder contains reserved function.   ", "Cannot Delete",
                            JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }
        }
        int n = JOptionPane.showConfirmDialog(ComponentRegistry.getFrame(),
            "Delete folder and its contents?   ", null, JOptionPane.YES_NO_OPTION);
        return n == JOptionPane.YES_OPTION;
    }

  
    public int findUniqueId(DefaultMutableTreeNode parentNode, int startId)
    {
        TreeSet ids = new TreeSet();
        SB_Function function;
        Enumeration e = parentNode.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Function)
            {
                function = (SB_Function) treeNode.getUserObject();
                ids.add(new Integer(function.getId()));
            }
        }

        while (true)
        {
            if (!ids.contains(new Integer(startId)))
            {
                // System.out.println("uniqueId = " + startId);
                return startId;
            }
            ++startId;
        }
    }

    static int findItemIndex(JMenuItem menuItem, JMenuItem[] menuItems)
    {
    	if (menuItems != null){
            int length = menuItems.length;
            for (int i = 0; i < length; ++i)
            {
                if (menuItems[i] == menuItem)
                    return i;
            }
    	}
        return -1;
    }

    static void setItemsEnabled(JMenuItem[] menuItems, boolean enabled)
    {
        int length = menuItems.length;
        for (int i = 0; i < length; ++i)
            menuItems[i].setEnabled(enabled);
    }

    protected void mouseDoubleClicked(UserObject userObject, boolean shiftPressed)
    {
        if (userObject instanceof SB_Constant)
        {
            showConstantValueDialog((SB_Constant) userObject);
        }
        else if (userObject instanceof SB_Global)
        {
            showInitialValueDialog((SB_Global) userObject);
        }
        else
        if (userObject instanceof I_DescriptionHolder)
        {
            if (userObject instanceof SB_Behavior)
            {
                if (ComponentRegistry.getContent().setBehavior((SB_Behavior) userObject, true))
                    return;
            }
            showDescriptionDialog((I_DescriptionHolder) userObject);
        }
    }

    protected void mouseTripleClicked(UserObject userObject, boolean shiftPressed)
    {
        super.mouseTripleClicked(userObject, shiftPressed);
        _holdDrag = true;
    }

    static ImageIcon getProjectIcon()
    {
        if (_projectIcon == null)
            _projectIcon = Util.getImageIcon("Project.gif");
        return _projectIcon;
    }

    static ImageIcon getHeadingIcon()
    {
        if (_headingIcon == null)
            _headingIcon = Util.getImageIcon("Heading.gif");
        return _headingIcon;
    }

    protected Icon getIcon(DefaultMutableTreeNode treeNode)
    {
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNode.getParent();
        if (treeNode == _root)
        {
            return getProjectIcon();
        } else if (parentNode == _root)
        {
            return getHeadingIcon();
        } else
            return null;
    }

    protected void nodeRenamed(DefaultMutableTreeNode node, String oldName)
    {
        Object userObject = node.getUserObject();
        if (userObject instanceof SB_Folder)
            setFolderModified(node);
        if (userObject instanceof SB_Behavior)
        {
            SB_Canvas canvas = ComponentRegistry.getContent().getActiveCanvas();
            canvas._poly.getElements().updateComplex(getEditor());
            canvas.repaint();
            // TODO Fix title
            // if (canvas._poly._parent == userObject) _editor.updateTitle();
            ((SB_Behavior) userObject).setBTNModified(true);
            setSBPModified(true);
        } else if (userObject instanceof SB_Function)
            setAPDModified(true);
        else if (userObject instanceof SB_Variable)
            setVariableModified(node);
    }

    protected void setVariableModified(DefaultMutableTreeNode treeNode)
    {
        SB_Variable userObject = (SB_Variable) treeNode.getUserObject();
        Object parentUserObject = ((DefaultMutableTreeNode) treeNode.getParent()).getUserObject();
        if (userObject instanceof SB_Parameter)
        {
            if (parentUserObject instanceof SB_Behavior)
                ((SB_Behavior) parentUserObject).setBTNModified(true);
            else
                setAPDModified(true);
        } else if (userObject instanceof SB_Global)
            setSBPModified(true);
        else
            setAPDModified(true);
    }

    protected void setFolderModified(DefaultMutableTreeNode treeNode)
    {
        if (treeNode.isNodeAncestor(_behaviors))
            setSBPModified(true);
        else
            setAPDModified(true);
    }

    public SB_Function findFunction(String name)
    {
        SB_Function func;
        Enumeration e = _root.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Function)
            {
                func = (SB_Function) treeNode.getUserObject();
                if (func.getName().equals(name))
                    return func;
            }
        }
        return null;
    }

    public DefaultMutableTreeNode findFuncNode(String name)
    {
        SB_Function func;
        Enumeration e = _root.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Function)
            {
                func = (SB_Function) treeNode.getUserObject();
                if (func.getName().equals(name))
                    return treeNode;
            }
        }
        return null;
    }
    
    public DefaultMutableTreeNode findBehaviorNode(String name)
    {
        SB_Behavior behavior;
        Enumeration e = _root.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
               behavior = (SB_Behavior) treeNode.getUserObject();
                if (behavior.getName().equals(name))
                    return treeNode;
            }
        }
        return null;
    }

    protected void showFuncNode(String name)
    {
        DefaultMutableTreeNode treeNode = findFuncNode(name);
        if (treeNode != null)
        {
            TreePath treePath = new TreePath(treeNode.getPath());
            setSelectionPath(treePath);
            expandPath(treePath);
            int row = getSelectionRows()[0];
            scrollRowToVisible(row + treeNode.getChildCount());
            scrollRowToVisible(row);
        }
    }

    protected SB_Action findAction(String name)
    {
        SB_Action action;
        Enumeration e = _actions.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Action)
            {
                action = (SB_Action) treeNode.getUserObject();
                if (action.getName().equals(name))
                    return action;
            }
        }
        return null;
    }

    protected SB_Predicate findPredicate(String name)
    {
        SB_Predicate predicate;
        Enumeration e = _predicates.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Predicate)
            {
                predicate = (SB_Predicate) treeNode.getUserObject();
                if (predicate.getName().equals(name))
                    return predicate;
            }
        }
        return null;
    }

    protected SB_Behavior findBehavior(String name)
    {
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                if (behavior.getName().equals(name))
                    return behavior;
            }
        }
        return null;
    }

    protected void clearRunningState()
    {
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                int count = behavior.getPolyCount();
                for (int i = 0; i < count; ++i)
                {
                    behavior.getPoly(i).getElements().clearRunningState();
                }
            }
        }
    }

    static String extractFuncName(String expr)
    {
        int index = expr.indexOf('(');
        if (index == -1)
            index = expr.length();
        return expr.substring(0, index);
    }

   
    public SB_Variable findVariable(SB_Polymorphism poly, String name)
    {
        // local
        SB_Variable var;
        Enumeration e = poly.getLocals().preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Variable)
            {
                var = (SB_Variable) treeNode.getUserObject();
                if (var.getName().equals(name))
                    return var;
            }
        }

        // parameter
        DefaultMutableTreeNode behavNode = findNode(poly._parent, _root);
        // if (behavNode == null)
        // return null;
        e = behavNode.preorderEnumeration();
        treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Variable)
            {
                var = (SB_Variable) treeNode.getUserObject();
                if (var.getName().equals(name))
                    return var;
            }
        }

        // global
        return findGlobal(name);
    }

    protected SB_Variable findGlobal(String name)
    {
        SB_Global global;
        Enumeration e = _globals.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Global)
            {
                global = (SB_Global) treeNode.getUserObject();
                if (global.getName().equals(name))
                    return global;
            }
        }
        return null;
    }

    protected boolean selectVariable(SB_Variable var)
    {
        DefaultMutableTreeNode treeNode = findNode(var, _root);
        if (treeNode == null)
            return false;
        TreePath treePath = new TreePath(treeNode.getPath());
        setSelectionPath(treePath);
        scrollPathToVisible(treePath);
        return true;
    }

    protected int findOccurrences(Pattern pattern, String strReplace) throws SB_CancelException {
        int total = 0;
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                total += behavior.findOccurrences(pattern, strReplace);
            }
        }
        return total;
    }

    protected void updateProjectTitle()
    {
        String name = ComponentRegistry.getProjectBar()._projectFile.getName();
        name = name.substring(0, name.length() - 4);
        _root.setUserObject("Project '" + name + "' Catalog");
        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.nodeChanged(_root);
    }

    protected void updatePolyIndices(int type, int index, String name1, String name2)
    {
        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        DefaultMutableTreeNode globalNode = null;
        if (type != kPolyIndexInsert)
            globalNode = (DefaultMutableTreeNode) _globals.getChildAt(index);
        SB_Global global;
        switch (type)
        {
        case kPolyIndexInsert:
            String globalType = SB_TypeManager.getStringTypeName();
            global = createGlobal("g" + name1, globalType, true, _globals);
            treeModel.insertNodeInto(new DefaultMutableTreeNode(global), _globals, index);
            break;
        case kPolyIndexDelete:
        	 SB_Global sbGlobal = (SB_Global)globalNode.getUserObject();
        	 Global globalModel = sbGlobal.getGlobalModel();
        	 // remove data model 
        	 getDataModel().getGlobals().removeGlobal(globalModel);
             treeModel.removeNodeFromParent(globalNode);
            break;
        case kPolyIndexRename:
            global = (SB_Global) globalNode.getUserObject();
            if (global.getName().equals("g" + name1))
            {
            	// update name and initial 
                global.setName("g" + name2);
                global.setInitial(name2);
                treeModel.nodeChanged(globalNode);
            }
            break;
        case kPolyIndexMoveUp:
            handleMenuItem(_moveUpItem, new TreePath(globalNode.getPath()));
            break;
        case kPolyIndexMoveDown:
            handleMenuItem(_moveDownItem, new TreePath(globalNode.getPath()));
            break;
        default:
            break;
        }

        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                behavior.updatePolyIndices(type, index, name1, name2);
            }
        }

        SB_TabbedCanvas tabbedCanvas = ComponentRegistry.getContent();
        SB_Canvas canvas;
        int size = tabbedCanvas.getTabCount();
        for (int i = 0; i < size; ++i)
        {
            canvas = (SB_Canvas) ((JScrollPane) tabbedCanvas.getComponentAt(i)).getViewport()
                    .getView();
            tabbedCanvas.setTitleAt(i, canvas._poly.getIndicesLabel());
        }

        setAPDModified(true);
    }

    protected void showConstantValueDialog(final SB_Constant constant)
    {
        String type = constant.getType();
        
        // see if there is a custom editor for the constant type
        I_ExpressionEditor exprEditor = _editor.getEditorRegistry().getExpressionEditor(
        		EditorRegistry.EXPRESSION_TYPE_BINDING, 
        		type,
        		constant.getValue());
        if (exprEditor != null) {
        	exprEditor.editObject(constant.getValue(), new I_EditorListener() {
        		public void editingCanceled(I_ExpressionEditor source) {}
        		
        		public void editingCompleted(I_ExpressionEditor source, String result) {
        			constant.setValue(result);
        		}
        	});
        	return;
        }
        
        if (_constantValueDialog == null)
        {
            _constantValueDialog = new JDialog(ComponentRegistry.getFrame(), true);

            JPanel constantValuePanel = new JPanel();
            constantValuePanel.setLayout(new BorderLayout());
            constantValuePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel label = new JLabel("Constant Value:", JLabel.LEFT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            constantValuePanel.add(label, BorderLayout.NORTH);
            _constantValueTextField = _editor.createAutocompleteTextArea();
            _constantValueTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            JScrollPane constantValueScrollPane = new JScrollPane(_constantValueTextField);
            constantValueScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            constantValuePanel.add(constantValueScrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.add(Box.createHorizontalGlue());
            _constantValueOK = new JButton("OK");
            _constantValueOK.setFocusPainted(false);
            _constantValueOK.addActionListener(this);
            buttonPanel.add(_constantValueOK);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            _constantValueCancel = new JButton("Cancel");
            _constantValueCancel.setFocusPainted(false);
            _constantValueCancel.addActionListener(this);
            buttonPanel.add(_constantValueCancel);

            _constantValueDialog.getContentPane().add(constantValuePanel, BorderLayout.CENTER);
            _constantValueDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            _constantValueDialog.pack();

            Dimension dialogSize = _constantValueDialog.getSize();
            dialogSize.height = 200;
            dialogSize.width = 400;
            _constantValueDialog.setSize(dialogSize);
            Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
            _constantValueDialog.setLocation(frameBounds.x + (frameBounds.width - dialogSize.width)
                    / 2, frameBounds.y + (frameBounds.height - dialogSize.height) / 2);
        }
        _constantValueDialog.setTitle(constant.toString());
        _constantValueTextField.setText(constant.getValue());
        _constantValueTextField.selectAll();
        _constantValueTextField.requestFocus();
        _constantValueDialog.setVisible(true);
    }

    protected void showInitialValueDialog(final SB_Global global)
    {
        if (global.isPolymorphic())
            return;

        // check if array or table
        String type = global.getType();
        
        // see if there is a custom editor for the variable
        I_ExpressionEditor exprEditor = _editor.getEditorRegistry().getExpressionEditor(
        		EditorRegistry.EXPRESSION_TYPE_BINDING, type,
        		global.getInitial());
        if (exprEditor != null) {
        	exprEditor.editObject(global.getInitial(), new I_EditorListener() {
        		public void editingCanceled(I_ExpressionEditor source) {}
        		
        		public void editingCompleted(I_ExpressionEditor source, String result) {
        			global.setInitial(result);
        		}
        	});
        	return;
        }
        
        if (_initialValueDialog == null)
        {
            _initialValueDialog = new JDialog(ComponentRegistry.getFrame(), true);

            JPanel initialValuePanel = new JPanel();
            initialValuePanel.setLayout(new BorderLayout());
            initialValuePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel label = new JLabel("Initial Value:", JLabel.LEFT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            initialValuePanel.add(label, BorderLayout.NORTH);
            initialValuePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            _initialValueTextField = _editor.createAutocompleteTextArea();
            _initialValueTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            JScrollPane valueScrollPane = new JScrollPane(_initialValueTextField);
            valueScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            initialValuePanel.add(valueScrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.add(Box.createHorizontalGlue());
            _initialValueOK = new JButton("OK");
            _initialValueOK.setFocusPainted(false);
            _initialValueOK.addActionListener(this);
            buttonPanel.add(_initialValueOK);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            _initialValueCancel = new JButton("Cancel");
            _initialValueCancel.setFocusPainted(false);
            _initialValueCancel.addActionListener(this);
            buttonPanel.add(_initialValueCancel);

            _initialValueDialog.getContentPane().add(initialValuePanel, BorderLayout.CENTER);
            _initialValueDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            _initialValueDialog.pack();

            Dimension dialogSize = _initialValueDialog.getSize();
            dialogSize.height = 200;
            dialogSize.width = 400;
            _initialValueDialog.setSize(dialogSize);
            Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
            _initialValueDialog.setLocation(frameBounds.x + (frameBounds.width - dialogSize.width)
                    / 2, frameBounds.y + (frameBounds.height - dialogSize.height) / 2);
        }
        _initialValueDialog.setTitle(global.toString());
        _initialValueTextField.setText(global.getInitial());
        _initialValueTextField.selectAll();
        _initialValueTextField.requestFocus();
        _initialValueDialog.setVisible(true);
    }

    public boolean isBTNModified()
    {
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                if (behavior.isBTNModified())
                    return true;
            }
        }
        return false;
    }

    protected void setSBPModified(boolean modified)
    {
        ComponentRegistry.getProjectBar().setProjectModified(modified);
    }

    public void setBTNModified(boolean modified)
    {
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                behavior.setBTNModified(modified);
            }
        }
    }

    // actions, predicates, behaviors
    public void checkError(DefaultMutableTreeNode parentNode,
            SB_ErrorInfo errorInfo, I_CompileValidator validator)
    {
        SB_Function function;
        Enumeration e = parentNode.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Function)
            {
                function = (SB_Function) treeNode.getUserObject();
                checkErrorForParams(treeNode, validator);
                
                if (parentNode == _behaviors) {
                    ((SB_Behavior) function).checkError(errorInfo, _typeManager, validator);
                }
                else if (parentNode == _actions) {
                    validator.validateAction((SB_Action)function);
                }
                else if (parentNode == _predicates) {
                    validator.validatePredicate((SB_Predicate)function);
                }                
            }
        }
    }
    

    protected void checkErrorForParams(DefaultMutableTreeNode functionNode,I_CompileValidator validator)
    {
       // validate parameters
       int count = functionNode.getChildCount();
       DefaultMutableTreeNode treeNode;
       SB_Parameter param;
       for (int i = 0; i < count; ++i)
       {
          // validate parameter name and type
          treeNode = (DefaultMutableTreeNode) functionNode.getChildAt(i);
          param = (SB_Parameter) treeNode.getUserObject();
          validator.validateParam(param,(SB_Function)functionNode.getUserObject());
       }
    }

    /**
     * 2018-05-07 -jmm
     * <br>
     * Refit of validateGlobals to handle the introduction of folders.
     * */
    protected void validateGlobals(SB_ErrorInfo errorInfo,
            I_CompileValidator validator) {
        Enumeration preorderTraversal = _globals.preorderEnumeration();
        while (preorderTraversal.hasMoreElements()) {
            DefaultMutableTreeNode treeNode
            = (DefaultMutableTreeNode) preorderTraversal.nextElement();
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof SB_Global) {
                SB_Global global = (SB_Global) treeNode.getUserObject();
                global.checkError(getEditor(), errorInfo, _typeManager);
            }
        }
    }

    /**
     * 2018-05-07 -jmm
     * <br>
     * Refit of validateConstants to handle the introduction of folders.
     * */
    protected void validateConstants(SB_ErrorInfo errorInfo) {
        Enumeration preorderTraversal = _constants.preorderEnumeration();
        while (preorderTraversal.hasMoreElements()) {
            DefaultMutableTreeNode treeNode
            = (DefaultMutableTreeNode) preorderTraversal.nextElement();
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof SB_Constant) {
                SB_Constant constant = (SB_Constant) treeNode.getUserObject();
                constant.validate(getEditor(), errorInfo);
            }
        }
    }

    /**
     * 2018-05-07 -jmm
     * <br>
     * Refit of constantReplace to handle the introduction of folders.
     * */
    public String constantReplace(String expr) {
        Enumeration preorderTraversal = _constants.preorderEnumeration();
        while (preorderTraversal.hasMoreElements()) {
            DefaultMutableTreeNode treeNode
            = (DefaultMutableTreeNode) preorderTraversal.nextElement();
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof SB_Constant) {
                SB_Constant constant = (SB_Constant) treeNode.getUserObject();
                expr = constant.replace(expr);
            }
        }
        return expr;
    }

    protected int getDrawableCount(Class c)
    {
        int count = 0;
        SB_Behavior behavior;
        Enumeration e = _behaviors.preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        while (e.hasMoreElements())
        {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            if (treeNode.getUserObject() instanceof SB_Behavior)
            {
                behavior = (SB_Behavior) treeNode.getUserObject();
                count += behavior.getDrawableCount(c);
            }
        }
        return count;
    }

  

    public void autoscroll(Point cursorLocn)
    {
        Rectangle rect = getVisibleRect();
        if (cursorLocn.y > rect.y + rect.height / 2)
        {
            Dimension dim = getSize();
            rect.y = Math.min(rect.y + 10, dim.height - rect.height);
        } else
            rect.y = Math.max(rect.y - 10, 0);
        scrollRectToVisible(rect);
    }

    public Insets getAutoscrollInsets()
    {
        Rectangle rect = getVisibleRect();
        Dimension dim = getSize();
        Insets inset = new Insets(rect.y + 10, 0, dim.height - (rect.y + rect.height) + 10, 0);
        return inset;
    }

    public void dragEnter(DragSourceDragEvent event)
    {
    }

    public void dragOver(DragSourceDragEvent event)
    {
        Point location = event.getLocation();
        Point screenLocation = getLocationOnScreen();
        TreePath treePath = getPathForLocation(location.x - screenLocation.x, location.y
                - screenLocation.y);
        if (treePath != null && (isDroppable(_dragNode)))
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            if (treePath.getPathCount() == 2 || (treeNode.getUserObject() instanceof SB_Folder))
            {
                setSelectionPath(treePath);
                if (treeNode.getSharedAncestor(_dragNode) != _root)
                    event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
                else
                    event.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
            } else
            {
                setSelectionInterval(_dragRow, _dragRow);
                event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
            }
            return;
        }

        setSelectionInterval(_dragRow, _dragRow);

        if (ComponentRegistry.getContent().getActiveCanvas()._allowDrop)
            event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
        else
            event.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
    }

    public void dragExit(DragSourceEvent event)
    {
        // event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
    }

    public void dragDropEnd(DragSourceDropEvent event)
    {
        if (event.getDropSuccess())
        {
        }

        _dragNode = null;
        _dragRow = -1;

        SB_Canvas canvas = ComponentRegistry.getContent().getActiveCanvas();
        canvas._allowDrop = true;
        canvas._dropType = SB_Canvas.kDropUnknown;
    }

    public void dropActionChanged(DragSourceDragEvent event)
    {
    }

    public void dragGestureRecognized(DragGestureEvent event)
    {
        if (_holdDrag)
        {
            _holdDrag = false;
            return;
        }

        Point point = event.getDragOrigin();
        TreePath treePath = getPathForLocation(point.x, point.y);
        if (treePath == null)
            return;
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

        Object userObject = treeNode.getUserObject();
        int n = treeNode.getChildCount();
        SB_Element element = null;
        String expr = null;
        StringSelection text = null;
        boolean checkSupported = true;
        SB_Canvas canvas = ComponentRegistry.getContent().getActiveCanvas();
        canvas._allowDrop = true;
        if (userObject instanceof SB_Action)
        {
            ActionNode actionNodeModel = new ActionNode();
            element = new SB_Rectangle(actionNodeModel);
            expr = ((SB_Action) userObject).getWildName(n);
            text = new StringSelection("Action:" + expr);
            canvas._dropType = SB_Canvas.kDropAction;
        } else if (userObject instanceof SB_Predicate) {
            SB_Predicate userPred = (SB_Predicate) userObject;
            Condition conditionModel = new Condition();
            element = new SB_Condition(conditionModel);
            expr = userPred.getWildName(n);
            if (userPred.getRetType().equals("Boolean")) {
                text = new StringSelection("Predicate:" + expr);
                canvas._dropType = SB_Canvas.kDropPredicate;
            } else {
                text = new StringSelection("NonBooleanPredicate:" + expr);
                canvas._dropType = SB_Canvas.kDropNonBooleanPredicate;
            }

        } else if (userObject instanceof SB_Behavior)
        {
            ActionNode actionNodeModel = new ActionNode();
            element = new SB_Rectangle(actionNodeModel);
            ((SB_Rectangle) element).setIsBehavior(true);
            expr = ((SB_Behavior) userObject).getWildName(n);
            text = new StringSelection("Behavior:" + expr);
            canvas._dropType = SB_Canvas.kDropBehavior;
        } else if (userObject instanceof SB_Parameter)
        {
            SB_Parameter parameter = (SB_Parameter) userObject;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNode.getParent();
            SB_Function function = (SB_Function) parentNode.getUserObject();
            if (function == canvas._poly._parent)
            {
                text = new StringSelection("Parameter:" + parameter.getName());
                canvas._dropType = SB_Canvas.kDropParameter;
            } else
            {
                text = new StringSelection("ParameterNA:" + parameter.getName());
                canvas._dropType = SB_Canvas.kDropParameterNA;
            }
            checkSupported = false;
        } else if (userObject instanceof SB_Constant)
        {
            text = new StringSelection("Constant:" + ((UserObject) userObject).getName());
            checkSupported = false;
            canvas._dropType = SB_Canvas.kDropConstant;
        } else if (userObject instanceof SB_Global)
        {
            text = new StringSelection("Global:" + ((UserObject) userObject).getName());
            checkSupported = false;
            canvas._dropType = SB_Canvas.kDropGlobal;
        } else if (userObject instanceof SB_Folder)
        {
            text = new StringSelection("Folder:" + ((UserObject) userObject).getName());
            checkSupported = false;
            canvas._dropType = SB_Canvas.kDropFolder;
        }

        if (text != null)
        {
            if (checkSupported && DragSource.isDragImageSupported())
            {
                element.setExpr(expr);
                Graphics2D g2 = (Graphics2D) _image.getGraphics();
                g2.setPaint(Color.white);
                g2.fill(new Rectangle(100, 75));
                element.draw(g2);
                Rectangle rect = element.getRect();
                BufferedImage subimage = _image.getSubimage(0,0,99,74);
                Point center = new Point(-rect.width / 2 - 1, -rect.height / 2 - 1);
                _dragSource.startDrag(event, DragSource.DefaultCopyDrop, subimage, center, text,
                    this);
            } else
                _dragSource.startDrag(event, DragSource.DefaultCopyDrop, text, this);
            _dragNode = treeNode;
            _dragRow = getRowForPath(treePath);
            setEditable(false);
        }
    }

    public void dragEnter(DropTargetDragEvent event)
    {
        event.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    public void dragExit(DropTargetEvent event)
    {
    }

    public void dragOver(DropTargetDragEvent event)
    {
    }

    public void drop(DropTargetDropEvent event)
    {
        if (isDroppable(_dragNode)) {

            TreePath treePath = getSelectionPath();
            if (treePath == null)
                return;
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            if (treeNode != _dragNode && treeNode != _dragNode.getParent()
                    && treeNode.getSharedAncestor(_dragNode) != _root)
            {
                if (_dragNode.isNodeDescendant(treeNode))
                {
                    JOptionPane.showMessageDialog(ComponentRegistry.getFrame(),
                        "Folder cannot be moved to one of its subfolders.   ", null,
                        JOptionPane.WARNING_MESSAGE);
                } else
                {
                	// update the underlying data model.
                	Object dragUserObj = _dragNode.getUserObject();
                	// new parent object 
            		Object toParent = treeNode.getUserObject();
            		// old parent object.
            		Object fromParent = ((DefaultMutableTreeNode)_dragNode.getParent()).getUserObject();
            		
                	if (dragUserObj instanceof SB_Action) {
                		SB_Action sbAction = (SB_Action)dragUserObj;
                		Action actionModel = sbAction.getActionModel();
                		moveAction(actionModel, fromParent, toParent);
                	} else if (dragUserObj instanceof SB_Predicate) {
                		SB_Predicate sbPredicate = (SB_Predicate)dragUserObj;
                		Predicate predicateModel = sbPredicate.getPredicateModel();
                		movePredicate(predicateModel, fromParent, toParent);
                	} else if (dragUserObj instanceof SB_Behavior) {
                		SB_Behavior sbBehavior = (SB_Behavior)dragUserObj;
                		Behavior behaviorModel = sbBehavior.getBehaviorModel();
                		moveBehavior(behaviorModel, fromParent, toParent);
                    } else if (dragUserObj instanceof SB_Global) {
                        SB_Global sbGlobal = (SB_Global)dragUserObj;
                        Global globalModel = sbGlobal.getGlobalModel();
                        moveGlobal(globalModel, fromParent, toParent);
                    } else if (dragUserObj instanceof SB_Constant) {
                        SB_Constant sbConstant = (SB_Constant)dragUserObj;
                        Constant constantModel = sbConstant.getConstantModel();
                        moveConstant(constantModel, fromParent, toParent);
                    } else if (dragUserObj instanceof SB_Folder) {
                		SB_Folder sbFolder = (SB_Folder)dragUserObj;
                		Folder folderModel = sbFolder.getDataModel();
                		moveFolder(folderModel, fromParent, toParent);
                	}
                	
                	// update the UI
                    boolean expanded = isExpanded(_dragRow);
                    DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
                    treeModel.removeNodeFromParent(_dragNode);
                    ((UserObject) _dragNode.getUserObject()).setNameToNextAvailable(treeNode);
                    
                    insertNodeInto(_dragNode, treeNode);
                    if (expanded)
                        expandPath(getSelectionPath());
                    setFolderModified(_dragNode);
                }
            }
        }
    }

    /**
     * This method is an attempt to unify deciding whether a tree node should
     * be subject to drag-and-drop handing.
     * */
    private static boolean isDroppable(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        return (userObject instanceof SB_Function)
                || (userObject instanceof SB_Folder)
                || (userObject instanceof SB_Constant)
                || (userObject instanceof SB_Global);
    }

    public void dropActionChanged(DropTargetDragEvent event)
    {
    }
    

    /**
     * Move the specified action model from the fromParent object to the toParent object.
     * @param actionModel The Action model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void moveAction(Action actionModel, Object fromParent, Object toParent) {
    	SimBionicJava dataModel = getDataModel();
		// remove the action model from the old parent model.
		if (fromParent instanceof SB_Folder) {
			Folder folder = ((SB_Folder)fromParent).getDataModel();
			// folder must be ActionFolder.
			ActionFolder actionFolder = (ActionFolder)folder;
			actionFolder.getActionChildren().removeAction(actionModel);
		} else {
			dataModel.getActions().removeAction(actionModel);
		}
		
		// add the action model to the new parent model.
		if (toParent instanceof SB_Folder) {
		  Folder folder = ((SB_Folder)toParent).getDataModel();
		  // folder must be ActionFolder.
		  ActionFolder actionFolder = (ActionFolder)folder;
		  actionFolder.getActionChildren().addAction(actionModel);
		} else {
  		  dataModel.getActions().addAction(actionModel);	
		}
    }
    
    /**
     * Move the specified predicate model from the fromParent object to the toParent object.
     * @param predicateModel The Predicate model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void movePredicate(Predicate predicateModel, Object fromParent, Object toParent) {
    	SimBionicJava dataModel = getDataModel();
		// remove the predicate model from the old parent model.
		if (fromParent instanceof SB_Folder) {
			Folder folder = ((SB_Folder)fromParent).getDataModel();
			// folder must be PredicateFolder.
			PredicateFolder predicateFolder = (PredicateFolder)folder;
			predicateFolder.getPredicateChildren().removePredicate(predicateModel);
		} else {
			dataModel.getPredicates().removePredicate(predicateModel);
		}
		
		// add the predicate model to the new parent model.
		if (toParent instanceof SB_Folder) {
		  Folder folder = ((SB_Folder)toParent).getDataModel();
		  // folder must be PredicateFolder.
		  PredicateFolder predicateFolder = (PredicateFolder)folder;
		  predicateFolder.getPredicateChildren().addPredicate(predicateModel);
		} else {
  		  dataModel.getPredicates().addPredicate(predicateModel);	
		}
    }
    
    /**
     * Move the specified behavior model from the fromParent object to the toParent object.
     * @param behaviorModel The Behavior model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void moveBehavior(Behavior behaviorModel, Object fromParent, Object toParent) {
    	SimBionicJava dataModel = getDataModel();
		// remove the behavior model from the old parent model.
		if (fromParent instanceof SB_Folder) {
			Folder folder = ((SB_Folder)fromParent).getDataModel();
			// folder must be BehaviorFolder.
			BehaviorFolder behaviorFolder = (BehaviorFolder)folder;
			behaviorFolder.getBehaviorChildren().removeBehavior(behaviorModel);
		} else {
			dataModel.getBehaviors().removeBehavior(behaviorModel);
		}
		
		// add the behavior model to the new parent model.
		if (toParent instanceof SB_Folder) {
		  Folder folder = ((SB_Folder)toParent).getDataModel();
		  // folder must be BehaviorFolder.
		  BehaviorFolder behaviorFolder = (BehaviorFolder)folder;
		  behaviorFolder.getBehaviorChildren().addBehavior(behaviorModel);
		} else {
  		  dataModel.getBehaviors().addBehavior(behaviorModel);	
		}
    }

    /**
     * Move the specified global model from the fromParent object to the
     * toParent object.
     * 
     * @param globalModel The Global model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void moveGlobal(Global globalModel, Object fromParent,
            Object toParent) {
        SimBionicJava dataModel = getDataModel();
        // remove the global model from the old parent model.
        if (fromParent instanceof SB_Folder) {
            GlobalFolder globalFromFolder
            = (GlobalFolder) ((SB_Folder) fromParent).getDataModel();
            globalFromFolder.getGlobalChildren().removeGlobal(globalModel);
        } else {
            dataModel.getGlobals().removeGlobal(globalModel);
        }
        // add the global model to the new parent model.
        if (toParent instanceof SB_Folder) {
            GlobalFolder globalToFolder
            = (GlobalFolder) ((SB_Folder) toParent).getDataModel();
            globalToFolder.getGlobalChildren().addGlobal(globalModel);
        } else {
            dataModel.getGlobals().addGlobal(globalModel);
        }
    }

    /**
     * Move the specified constant model from the fromParent object to the
     * toParent object.
     * 
     * @param constantModel The Constant model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void moveConstant(Constant constantModel, Object fromParent,
            Object toParent) {
        SimBionicJava dataModel = getDataModel();
        // remove the constant model from the old parent model.
        if (fromParent instanceof SB_Folder) {
            ConstantFolder constantFromFolder
            = (ConstantFolder) ((SB_Folder) fromParent).getDataModel();
            constantFromFolder.getConstantChildren().removeConstant(constantModel);
        } else {
            dataModel.getConstants().removeConstant(constantModel);
        }
        // add the constant model to the new parent model.
        if (toParent instanceof SB_Folder) {
            ConstantFolder constantToFolder
            = (ConstantFolder) ((SB_Folder) toParent).getDataModel();
            constantToFolder.getConstantChildren().addConstant(constantModel);
        } else {
            dataModel.getConstants().addConstant(constantModel);
        }
    }

    /**
     * Move the specified folder model from the fromParent object to the toParent object.
     * @param folderModel The Folder model to move.
     * @param fromParent The old parent object.
     * @param toParent The new parent object.
     */
    private void moveFolder(Folder folderModel, Object fromParent, Object toParent) {
    	SimBionicJava dataModel = getDataModel();
    	if (folderModel instanceof ActionFolder) {
    		// move action folder
    		ActionFolder actionFolderToMove = (ActionFolder)folderModel;
    		if (fromParent instanceof SB_Folder) {
    			Folder folder = ((SB_Folder)fromParent).getDataModel();
    			ActionFolder actionFolder = (ActionFolder)folder;
    			actionFolder.getActionChildren().removeActionFolder(actionFolderToMove);
    		} else {
    			dataModel.getActions().removeActionFolder(actionFolderToMove);
    		}
    		
    		if (toParent instanceof SB_Folder) {
    		  Folder folder = ((SB_Folder)toParent).getDataModel();
    		  // folder must be ActionFolder.
    		  ActionFolder actionFolder = (ActionFolder)folder;
    		  actionFolder.getActionChildren().addActionFolder(actionFolderToMove);
    		} else {
      		  dataModel.getActions().addActionFolder(actionFolderToMove);	
    		}
    		
    	} else if (folderModel instanceof PredicateFolder) {
    		// move predicate folder
    		PredicateFolder predicateFolderToMove = (PredicateFolder)folderModel;
    		if (fromParent instanceof SB_Folder) {
    			Folder folder = ((SB_Folder)fromParent).getDataModel();
    			PredicateFolder predicateFolder = (PredicateFolder)folder;
    			predicateFolder.getPredicateChildren().removePredicateFolder(predicateFolderToMove);
    		} else {
    			dataModel.getPredicates().removePredicateFolder(predicateFolderToMove);
    		}
    		
    		if (toParent instanceof SB_Folder) {
    		  Folder folder = ((SB_Folder)toParent).getDataModel();
    		  // folder must be PredicateFolder.
    		  PredicateFolder predicateFolder = (PredicateFolder)folder;
    		  predicateFolder.getPredicateChildren().addPredicateFolder(predicateFolderToMove);
    		} else {
      		  dataModel.getPredicates().addPredicateFolder(predicateFolderToMove);	
    		}
    		
    	} else if (folderModel instanceof BehaviorFolder) {
    		// move behavior folder
    		BehaviorFolder behaviorFolderToMove = (BehaviorFolder)folderModel;
    		if (fromParent instanceof SB_Folder) {
    			Folder folder = ((SB_Folder)fromParent).getDataModel();
    			BehaviorFolder behaviorFolder = (BehaviorFolder)folder;
    			behaviorFolder.getBehaviorChildren().removeBehaviorFolder(behaviorFolderToMove);
    		} else {
    			dataModel.getBehaviors().removeBehaviorFolder(behaviorFolderToMove);
    		}
    		
    		if (toParent instanceof SB_Folder) {
    		  Folder folder = ((SB_Folder)toParent).getDataModel();
    		  // folder must be BehaviorFolder.
    		  BehaviorFolder behaviorFolder = (BehaviorFolder)folder;
    		  behaviorFolder.getBehaviorChildren().addBehaviorFolder(behaviorFolderToMove);
    		} else {
      		  dataModel.getBehaviors().addBehaviorFolder(behaviorFolderToMove);	
    		}
    		
    		
        } else if (folderModel instanceof GlobalFolder) {
            moveGlobalFolder(dataModel, (GlobalFolder) folderModel,
                    fromParent, toParent);
        } else if (folderModel instanceof ConstantFolder) {
            moveConstantFolder(dataModel, (ConstantFolder) folderModel,
                    fromParent, toParent);
    	} // unknown folder model
    }

    /**
     * This method attempts to clean up {@link
     * #moveFolder(Folder, Object, Object)} by extracting the specific code for
     * handling {@code SB_Folder} instances with {@cod GlobalFolder} data
     * models.
     * @param globalFolderToMove
     * @param fromParent The parent of {@code globalFolderToMove} before the
     * move
     * @param toParent The folder that should contain {@code
     * globalFolderToMove} after the move.
     * */
    private void moveGlobalFolder(SimBionicJava dataModel,
            GlobalFolder globalFolderToMove, Object fromParent,
            Object toParent) {

        if (fromParent instanceof SB_Folder) {
            GlobalFolder globalFromFolder
            = (GlobalFolder) ((SB_Folder) fromParent).getDataModel();
            globalFromFolder.getGlobalChildren()
                    .removeGlobalFolder(globalFolderToMove);
        } else {
            dataModel.getGlobals().removeGlobalFolder(globalFolderToMove);
        }

        if (toParent instanceof SB_Folder) {
            GlobalFolder globalToFolder
            = (GlobalFolder) ((SB_Folder) toParent).getDataModel();
            globalToFolder.getGlobalChildren()
                    .addGlobalFolder(globalFolderToMove);
        } else {
            dataModel.getGlobals().addGlobalFolder(globalFolderToMove);
        }
    }

    /**
     * This method attempts to clean up {@link
     * #moveFolder(Folder, Object, Object)} by extracting the specific code for
     * handling {@code SB_Folder} instances with {@cod ConstantFolder} data
     * models.
     * @param constantFolderToMove
     * @param fromParent The parent of {@code constantFolderToMove} before the
     * move
     * @param toParent The folder that should contain {@code
     * constantFolderToMove} after the move.
     * */
    private void moveConstantFolder(SimBionicJava dataModel,
            ConstantFolder constantFolderToMove, Object fromParent,
            Object toParent) {

        if (fromParent instanceof SB_Folder) {
            ConstantFolder constantFromFolder
            = (ConstantFolder) ((SB_Folder) fromParent).getDataModel();
            constantFromFolder.getConstantChildren()
                    .removeConstantFolder(constantFolderToMove);
        } else {
            dataModel.getConstants().removeConstantFolder(constantFolderToMove);
        }

        if (toParent instanceof SB_Folder) {
            ConstantFolder constantToFolder
            = (ConstantFolder) ((SB_Folder) toParent).getDataModel();
            constantToFolder.getConstantChildren()
                    .addConstantFolder(constantFolderToMove);
        } else {
            dataModel.getConstants().addConstantFolder(constantFolderToMove);
        }
    }

    public void setTypeManager(SB_TypeManager typeManager){
    	_typeManager = typeManager;
    	_typeManager.addTypeChangeListener(this);
    }
    
    public void typeUpdated(SB_TypeManager source) 
    {
       clearRetTypeSubMenu();
       populateRetTypeSubMenu2();
    }
    
    
    /*
     * Registers classes in the give package to the type manager.
     * The classes that have been registered will not be registered again.
     */
    private void registerPackage(SB_Package pack, boolean isDefault){
    	if (!(pack == null || pack.getChildren() == null)){
        	for (int i = 0; i < pack.getChildren().size(); i ++){
        		Object obj = pack.getChildren().get(i);
        		if (obj instanceof SB_Package)
        			registerPackage((SB_Package)obj, isDefault);
        		else if (obj instanceof SB_Class)
        			registerClass((SB_Class)obj, isDefault);
        	}
    	}
    }

    /*
     * Registers the given class to the type manager.
     * The class that has been registered will not be registered again.
     */
    private void registerClass(SB_Class cls, boolean isDefault){
    	if (!_typeManager.classExists(cls.getName()))
    	{
    	  int validFlags = 
              ETypeValid.kForVar.getState()|ETypeValid.kForParam.getState()|ETypeValid.kForRetVal.getState();
        int typeFlags =ETypeType.kJavaType.getState(); 
        if (isDefault) {
          validFlags = 
              ETypeValid.kForConst.getState()|ETypeValid.kForVar.getState()|ETypeValid.kForParam.getState()|ETypeValid.kForRetVal.getState();
        } 
        
        _typeManager.addType(cls.getAlias(), 
              validFlags, typeFlags, isDefault, cls.getName());
    	}
    }
    
    
    private void updateClassMap() throws ClassNotFoundException{
    	_classMap = new SB_ClassMap();
    	_rootPackage.toClassMap(_classMap, _typeManager);
    }
    
    public SB_ClassMap getClassMap(){
    	return _classMap;
    }
    
    /**
     * @param className	fully qualified class name, e.g., java.lang.String
     */
    public SB_Class lookupClass(String className){
    	return _rootPackage.lookupClass(className);
    }
    
    /**
     * @param classAlias	a short version of the class name, e.g., String
     */
    public SB_Class lookupClassByAlias(String classAlias){
    	return _rootPackage.lookupClassByAlias(classAlias);
    }

    public void nameChanged(UserObject userObject,String oldName)
    {
        if (userObject instanceof SB_Behavior)
            ComponentRegistry.getProjectBar().behaviorRenamed((SB_Behavior)userObject,oldName);
    }
    
    /**
     * register with Simbionic the java classes imported in the javaScript object.&nbsp;The method
     * returns true is no incorrect classes were found or if the user decide to remove those incorrect
     * classes. 
     * 
     * 
     * @param javaScript
     */
    private boolean registerfromJavaScript(JavaScript javaScript) {
       SB_ClassMap classMap = new SB_ClassMap();
       List<String> javaClasses = javaScript.getImportedJavaClasses();
       List<String> incorrectJavaClasses = new ArrayList<String>();
       
       StringBuilder warningMessages = new StringBuilder();
       for (String javaClassName : javaClasses) {
          SB_Class javaClass = SB_Class.createClass(javaClassName);
          if (javaClass == null) {
        	  warningMessages.append(javaClassName + "\n"); 
        	  incorrectJavaClasses.add(javaClassName);
          } else {
             classMap.addJavaClass(javaClassName, javaClassName);
          }
       }
       
       // updates root package - no comments
       _rootPackage.addClasses(classMap, new HashMap(), this);
       
       // register package and classes
       registerPackage(_rootPackage, false);
         
       try {
         // updates class map
         updateClassMap();
       } catch (Exception ex) {
          ex.printStackTrace();
       }
       
       if (!warningMessages.toString().isEmpty()) {
    	   
    	   String msg = warningMessages.toString();
    	   msg = msg.substring(0, msg.length() - 1);
    	   int option = JOptionPane.showConfirmDialog(ComponentRegistry.getFrame(),
    			    "The following classes are unknown : \n" + msg + "\n\nRemove classes?",
    			    "Warning",
    			    JOptionPane.OK_CANCEL_OPTION);
    	   if (option == JOptionPane.OK_OPTION) {
    		  //remove bad classes 
    		   for (String badClass : incorrectJavaClasses) {
    			   javaScript.removeImportedJavaClass(badClass);
    		   }
    	   }
    	   return option == JOptionPane.OK_OPTION;
       }
       return true;
    }
    
    protected SimBionicJava getDataModel() {
       return ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getDataModel();
    }

  
}
