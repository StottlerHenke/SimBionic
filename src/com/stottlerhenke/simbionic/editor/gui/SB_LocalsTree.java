package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Local;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.editor.SB_TypeChangeListener;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.UserObject;

/**
 * UI for the local variables
 */
public class SB_LocalsTree extends EditorTree implements DragSourceListener, 
		DragGestureListener, SB_TypeChangeListener
{
    // locals popup
    protected JPopupMenu _localsPopup = null;
    protected JMenuItem _insertLocalItem = null;

    // local popup
    protected JPopupMenu _localPopup = null;
    protected JMenuItem _renameLocalItem = null;
    protected JMenuItem _deleteLocalItem = null;
    protected JMenu _typeSubmenu = null;
    protected JRadioButtonMenuItem[] _typeItems = null;
    protected ButtonGroup _typeSubmenuButtonGroup = null;

    protected DragSource _dragSource = null;
    private static BufferedImage _image = new BufferedImage(100, 75, BufferedImage.TYPE_3BYTE_BGR);
    protected boolean _holdDrag = false;
    
    private SB_TypeManager _typeManager;

    public SB_LocalsTree(SimBionicEditor editor)
    {
        super(editor);

        ComponentRegistry.setLocalsTree(this);

        _localsPopup = new JPopupMenu();
        _insertLocalItem = new JMenuItem("Insert Local...");
        _insertLocalItem.addActionListener(this);
        _localsPopup.add(_insertLocalItem);

        _localPopup = new JPopupMenu();
        _renameLocalItem = new JMenuItem("Rename");
        _renameLocalItem.addActionListener(this);
        _localPopup.add(_renameLocalItem);
        _deleteLocalItem = new JMenuItem("Delete");
        _deleteLocalItem.addActionListener(this);
        _localPopup.add(_deleteLocalItem);
        _localPopup.addSeparator();

        initTypeSubMenu();

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

        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F)
                    ComponentRegistry.getContent().showFindDialog(
                        ComponentRegistry.getToolBar()._localsDialog);
            }
        });

        _dragSource = new DragSource();
        _dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }
    
    private void initTypeSubMenu(){
        _typeSubmenu = new JMenu("Set Type");
        _localPopup.add(_typeSubmenu);
    }

    /**
     * @deprecated
     */
    private void populateTypeSubMenu(){
        ButtonGroup group = new ButtonGroup();
        int length = SB_Variable.kTypeNames.length;
        _typeItems = new JRadioButtonMenuItem[length];
        for (int i = 0; i < length; ++i)
        {
            _typeItems[i] = new JRadioButtonMenuItem(SB_Variable.kTypeNames[i]);
            // @kp 1.25.2005 removed because this functionality isn't
            // implemented yet
            if (i==SB_Variable.kUser){
                continue;
            }
            _typeItems[i].addActionListener(this);
            group.add(_typeItems[i]);
            _typeSubmenu.add(_typeItems[i]);
        }
    }
    
    /*
     * Populates the type submenu with the types get from type manager.
     * Used to replace populateTypeSubMenu().
     */
    private void populateTypeSubMenu2(){
    	_typeSubmenuButtonGroup = new ButtonGroup();
    	ArrayList items = _typeManager.getVarComboItems();
    	_typeItems = new JRadioButtonMenuItem[items.size()];
    	for (int i = 0; i < _typeItems.length; i ++)
    	{
    		// creates all menu items
    	   String typeName = (String)items.get(i);
    	   _typeItems[i] = new JRadioButtonMenuItem(typeName);
    	   _typeItems[i].addActionListener(this);

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
    	   String name = _typeManager.varComboIndexToName(i);
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

    protected void setRoot(DefaultMutableTreeNode root)
    {
        if (_root == root)
            return;

        _root = root;
        ((DefaultTreeModel) treeModel).setRoot(_root);
        
        //10/21/2016 The very first time a project is open and the local
        // variables are to be shown the tree does not show the type of the
        // variables. The code below fix the problem
        SwingUtilities.invokeLater(new Runnable () {
			public void run() {
				UIUtil.redrawAllTreeCells((DefaultTreeModel) treeModel);			
			}       	
        });
    }
    
	

    protected void mouseRightPressed(DefaultMutableTreeNode treeNode, int x, int y)
    {
        if (treeNode == _root)
        {
            SB_Polymorphism poly = ComponentRegistry.getContent().getActiveCanvas()._poly;
            _insertLocalItem.setEnabled(poly._parent.isCellEditable());
            _localsPopup.show(this, x, y);
        }
    }

    protected void mouseRightPressed(UserObject userObject, int x, int y)
    {
        if (userObject instanceof SB_Variable)
        {
            boolean editable = userObject.isCellEditable();
            _renameLocalItem.setEnabled(editable);
            _deleteLocalItem.setEnabled(editable);
            
            // repopulate the menus
            clearTypeSubMenu();
            populateTypeSubMenu2();
            String type = ((SB_Variable)userObject).getType();
            int comboIndex = _typeManager.nameToVarComboIndex(type);
            if ((comboIndex < 0) || (comboIndex >= _typeItems.length))
            {
                // must be a class type that no longer exists (importing problem)
                /*((SB_Variable)userObject).setType(0);
                type = SB_TypeManager.convertOldTypeId(((SB_Variable)userObject).getType());
                comboIndex = _typeManager.idToVarComboIndex(type); */
               System.err.println("comboIndex is incorrect!");
               return;
            }
            
            _typeItems[comboIndex].setSelected(true);
            SB_Catalog.setItemsEnabled(_typeItems, editable);
            TreePath treePath = getSelectionPath();
            int count = treePath.getPathCount();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath
                    .getPathComponent(count - 2);
            int index = treeModel.getIndexOfChild(parentNode, treeNode);
            _localPopup.show(this, x, y);
        }
    }

    public void insertLocal(String name, String type)
    {
        insertLocal(_root, name, type, false);
    }

    private void insertLocal(DefaultMutableTreeNode parent, String name, String type, boolean editNode)
    {
        SB_Polymorphism poly = ComponentRegistry.getContent().getActiveCanvas()._poly;
        Poly polyModel = poly.getDataModel();
        // create a new Local data model.
        Local localModel = new Local();
        localModel.setName(name);
        localModel.setType(type);
        polyModel.addLocal(localModel);
        SB_Variable childUserObject = new SB_Variable(localModel);
        childUserObject.setNameToNextAvailable(parent);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childUserObject);
        insertNodeInto(childNode, parent);
        if (editNode)
            startEditingAtPath(new TreePath(childNode.getPath()));
        poly.setModified(true);
    }

    protected void handleMenuItem(JMenuItem menuItem, TreePath treePath)
    {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = treeNode.getUserObject();

        SB_Polymorphism poly = ComponentRegistry.getContent().getActiveCanvas()._poly;

        if (menuItem == _insertLocalItem)
        {
            String type = SB_TypeManager.getIntegerTypeName();
            insertLocal(treeNode, "NewLocal", type, true);
            return;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        int index = -1;
        if (menuItem == _renameLocalItem)
        {
            startEditingAtPath(treePath);
        } else if (menuItem == _deleteLocalItem)
        {
            SB_Variable var = (SB_Variable) userObject;
            Local localModel = (Local)var.getDataModel();
            Poly polyModel = poly.getDataModel();
            polyModel.removeLocal(localModel);
            treeModel.removeNodeFromParent(treeNode);
            poly.setModified(true);
        } else if ((index = SB_Catalog.findItemIndex(menuItem, _typeItems)) != -1)
        {
            SB_Variable var = (SB_Variable) userObject;
          	String type = _typeManager.varComboIndexToName(index, false, false);
            if (!var.getType().equals(type))
            {
                var.setType(type);
                treeModel.nodeChanged(treeNode);
                poly.setModified(true);
            } 
        }
    }

    protected void mouseDoubleClicked(UserObject userObject, boolean shiftPressed)
    {
    }

    protected void mouseTripleClicked(UserObject userObject, boolean shiftPressed)
    {
        super.mouseTripleClicked(userObject, shiftPressed);
        _holdDrag = true;
    }

    protected Icon getIcon(DefaultMutableTreeNode treeNode)
    {
        return SB_Catalog.getHeadingIcon();
    }

    protected void nodeRenamed(DefaultMutableTreeNode node, String oldName)
    {
        SB_Polymorphism poly = ComponentRegistry.getContent().getActiveCanvas()._poly;
        poly.setModified(true);
    }

    public void dragEnter(DragSourceDragEvent event)
    {
    }

    public void dragOver(DragSourceDragEvent event)
    {
        if (ComponentRegistry.getContent().getActiveCanvas()._allowDrop)
            event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
        else
            event.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);
    }

    public void dragExit(DragSourceEvent event)
    {
        event.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
    }

    public void dragDropEnd(DragSourceDropEvent event)
    {
        if (event.getDropSuccess())
        {
        }
        // DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)
        // getLastSelectedPathComponent();
        // ED_UserObject userObject = (ED_UserObject) treeNode.getUserObject();
        // userObject._editing = false;
        // setEditable(true);
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
        StringSelection text = null;
        SB_Canvas canvas = ComponentRegistry.getContent().getActiveCanvas();
        canvas._allowDrop = true;
        if (userObject instanceof SB_Variable)
        {
            text = new StringSelection("Local:" + ((UserObject) userObject).getName());
            canvas._dropType = SB_Canvas.kDropLocal;
        }

        if (text != null)
        {
            _dragSource.startDrag(event, DragSource.DefaultCopyDrop, text, this);
            setEditable(false);
        }
    }
    
    public void setTypeManager(SB_TypeManager typeManager){
    	_typeManager = typeManager;
    	_typeManager.addTypeChangeListener(this);
    }
    
    public void typeUpdated(SB_TypeManager source)  
    {
    	clearTypeSubMenu();
    	populateTypeSubMenu2();
    }
    
}
