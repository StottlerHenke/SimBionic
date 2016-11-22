package com.stottlerhenke.simbionic.editor.gui;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.UserObject;

// Abstract functions:
//
//	abstract void mouseRightPressed(DefaultMutableTreeNode treeNode, int x, int y);
//	abstract void mouseRightPressed(ED_UserObject userObject, int x, int y);
//	abstract void handleMenuItem(JMenuItem menuItem, TreePath treePath);
//
//	abstract Icon getIcon(DefaultMutableTreeNode treeNode);
//
// Overridables:
//
//	void iconPressed(ED_UserObject userObject);
//      void mouseDoubleClicked(ED_UserObject userObject, boolean shiftPressed)
//	void mouseDoubleClicked(DefaultMutableTreeNode treeNode, boolean shiftPressed);
//	void mouseTripleClicked(ED_UserObject userObject, boolean shiftPressed);
//
//      Icon getDisabledIcon(DefaultMutableTreeNode treeNode);
//	String getToolTipText(DefaultMutableTreeNode treeNode);
//
//      JFrame getEditorFrame();
//	void nodeRenamed(DefaultMutableTreeNode node, String oldName);

abstract public class EditorTree extends JTree implements ActionListener
{
    protected SimBionicEditor _editor;

    protected DefaultMutableTreeNode _root;
    protected UserObject _savedUserObject = null;   // user object being renamed

    public EditorTree(SimBionicEditor editor)
    {
        _editor = editor;

        _root = new DefaultMutableTreeNode("");

        DefaultTreeModel treeModel = new DefaultTreeModel(_root);
        treeModel.addTreeModelListener(new EditorTreeModelListener());
        setModel(treeModel);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setDismissDelay(3600000);
        EditorTreeCellRenderer renderer = new EditorTreeCellRenderer();
        setCellRenderer(renderer);
        setCellEditor(new EditorTreeCellEditor(this, renderer));
        setEditable(true);
        setToggleClickCount(4);

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                setEditable(true);
                requestFocus();

                TreePath treePath = getPathForLocation(e.getX(), e.getY());
                if (treePath == null)
                {
                    cancelEditing();
                    return;
                }
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                Object userObject = treeNode.getUserObject();

                if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0)
                {
                    setSelectionPath(treePath);
                    if (userObject instanceof UserObject)
                        mouseRightPressed((UserObject) userObject, e.getX(), e.getY());
                    else
                        mouseRightPressed(treeNode, e.getX(), e.getY());
                    return;
                }

                boolean shiftPressed = e.isShiftDown();
                if (userObject instanceof UserObject)
                {
                    Icon icon = ((UserObject) userObject).getIcon();
                    int width = icon != null ? icon.getIconWidth() : 16;
                    treePath = getPathForLocation(e.getX() - width, e.getY());
                    if (treePath == null)
                        iconPressed((UserObject) userObject);

                    if (e.getClickCount() == 2)
                        mouseDoubleClicked((UserObject) userObject, shiftPressed);

                    if (e.getClickCount() == 3)
                        mouseTripleClicked((UserObject) userObject, shiftPressed);
                }
                else
                {
                    if (e.getClickCount() == 2)
                        mouseDoubleClicked(treeNode, shiftPressed);
                }
            }
        });
    }

    public DefaultMutableTreeNode getRoot() { return _root; }
    abstract protected void mouseRightPressed(DefaultMutableTreeNode treeNode, int x, int y);
    abstract protected void mouseRightPressed(UserObject userObject, int x, int y);
    protected void iconPressed(UserObject userObject) { }
    protected void mouseDoubleClicked(UserObject userObject, boolean shiftPressed) { }
    protected void mouseDoubleClicked(DefaultMutableTreeNode treeNode, boolean shiftPressed) { }
    protected void mouseTripleClicked(UserObject userObject, boolean shiftPressed)
    {
        if (userObject.isCellEditable())
            startEditingAtPath(getSelectionPath());
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JMenuItem)
        {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            TreePath treePath = getSelectionPath();
            handleMenuItem(menuItem, treePath);
        }
    }

    abstract protected void handleMenuItem(JMenuItem menuItem, TreePath treePath);

    public DefaultMutableTreeNode findNode(Object userObject, DefaultMutableTreeNode parentNode)
    {
      DefaultMutableTreeNode treeNode;
      Enumeration e = parentNode.preorderEnumeration();
      while (e.hasMoreElements())
      {
          treeNode = (DefaultMutableTreeNode) e.nextElement();
          if (userObject.equals(treeNode.getUserObject()))
            return treeNode;
      }
      return null;
    }

    public DefaultMutableTreeNode findNodeByName(String name, DefaultMutableTreeNode parentNode)
    {
      DefaultMutableTreeNode treeNode;
      Enumeration e = parentNode.preorderEnumeration();
      while (e.hasMoreElements())
      {
        treeNode = (DefaultMutableTreeNode) e.nextElement();
        if (treeNode.toString().equals(name))
          return treeNode;
      }
      return null;
    }

    /**
     * Invoked when a user object has been renamed.
     * @param userObject
     * @param oldName
     */
    public void nameChanged(UserObject userObject,String oldName)
    {
        
    }

    public void expandAll()
    {
      DefaultMutableTreeNode treeNode;
      Enumeration e = _root.depthFirstEnumeration();
      while (e.hasMoreElements())
      {
        treeNode = (DefaultMutableTreeNode) e.nextElement();
        if (treeNode.isLeaf())
          makeVisible(new TreePath(treeNode.getPath()));
      }
    }

    abstract protected Icon getIcon(DefaultMutableTreeNode treeNode);
    protected Icon getDisabledIcon(DefaultMutableTreeNode treeNode) { return getIcon(treeNode); }
    protected String getToolTipText(DefaultMutableTreeNode treeNode) { return null; }

    public void startEditingAtPath(TreePath treePath)
    {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = treeNode.getUserObject();
        if (isEditable() && (userObject instanceof UserObject)
            && ((UserObject) userObject).isCellEditable()
            && !((UserObject) userObject)._editing)
        {
            ((UserObject) userObject).prepare();
            _savedUserObject = (UserObject) userObject;
            super.startEditingAtPath(treePath);
        }
    }

    protected String getSortName(DefaultMutableTreeNode treeNode)
    {
      if (treeNode.getUserObject() instanceof UserObject)
        return ((UserObject) treeNode.getUserObject()).getSortName();
      else
        return treeNode.toString();
    }

    protected void nodeRenamed(DefaultMutableTreeNode node, String oldName) { }

    protected void insertNodeInto(DefaultMutableTreeNode childNode, DefaultMutableTreeNode parentNode)
    {
      insertNodeInto(childNode, parentNode, true);
    }

    protected void insertNodeInto(DefaultMutableTreeNode childNode, DefaultMutableTreeNode parentNode, boolean selected)
    {
        DefaultTreeModel treeModel = (DefaultTreeModel) EditorTree.this.getModel();
        int index = parentNode.getChildCount();
        Object userObject = childNode.getUserObject();
        if ((userObject instanceof UserObject) && ((UserObject) userObject).shouldSort())
        {
            String sortName = ((UserObject) userObject).getSortName();
            DefaultMutableTreeNode siblingNode;
            while (index > 0)
            {
               siblingNode = (DefaultMutableTreeNode) parentNode.getChildAt(index - 1);
               if (sortName.compareToIgnoreCase(getSortName(siblingNode)) >= 0)
                   break;
                --index;
            }
        }
        treeModel.insertNodeInto(childNode, parentNode, index);
        if (selected)
        {
          TreePath treePath = new TreePath(childNode.getPath());
          setSelectionPath(treePath);
          scrollPathToVisible(treePath);
        }
    }


private class EditorTreeModelListener implements TreeModelListener
{
    public void treeNodesChanged(TreeModelEvent e)
    {
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();

        /*
         * If the event lists children, then the changed
         * node is the child of the node we've already
         * gotten.  Otherwise, the changed node and the
         * specified node are the same.
         */
        try
        {
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) node.getChildAt(index);
        }
        catch (NullPointerException exc)
        {
        }

        if (_savedUserObject != null)
        {
            String name = node.toString().trim();
            node.setUserObject(name);
            String oldName = _savedUserObject.getName();
            boolean valid = _savedUserObject.isNameValid(name);
            if (!valid) _savedUserObject._editing = false;
            boolean updated = valid && _savedUserObject.update(node);
            node.setUserObject(_savedUserObject);
            if (!_savedUserObject.getName().equals(oldName)) nodeRenamed(node, oldName);
            nameChanged(_savedUserObject,oldName);

            DefaultTreeModel treeModel = (DefaultTreeModel) EditorTree.this.getModel();
            if (updated && _savedUserObject.shouldSort())
            {
                DefaultMutableTreeNode previousNode = node.getPreviousSibling();
                DefaultMutableTreeNode nextNode = node.getNextSibling();
                TreePath treePath = getSelectionPath();

                if ((previousNode != null && _savedUserObject.getSortName().compareToIgnoreCase(getSortName(previousNode)) < 0)
                    || (nextNode != null && _savedUserObject.getSortName().compareToIgnoreCase(getSortName(nextNode)) > 0))
                {
                    _savedUserObject = null;
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                    boolean expanded = isExpanded(treePath);
                    boolean selected = treePath != null && node == treePath.getLastPathComponent();
                    treeModel.removeNodeFromParent(node);
                    insertNodeInto(node, parentNode, selected);
                    if (expanded)
                      expandPath(treePath);
                    return;
                }
            }
            _savedUserObject = null;
            treeModel.nodeChanged(node);
            if (!valid)
              JOptionPane.showMessageDialog(ComponentRegistry.getFrame(), "Name '" + name
                  + "' is invalid. ", "Invalid Name", JOptionPane.WARNING_MESSAGE);
            else if (!updated)
                JOptionPane.showMessageDialog(ComponentRegistry.getFrame(), "Name '" + name
                    + "' is already in use. ", "Duplicate Name", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void treeNodesInserted(TreeModelEvent e)
    {
    }

    public void treeNodesRemoved(TreeModelEvent e)
    {
    }

    public void treeStructureChanged(TreeModelEvent e)
    {
    }

}
    private class EditorTreeCellEditor extends DefaultTreeCellEditor
    {
        boolean _escapePressed = false;

        EditorTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer)
        {
            super(tree, renderer);
        }

        public boolean isCellEditable(EventObject event)
        {
            if ((event instanceof MouseEvent) && ((MouseEvent) event).getClickCount() == 3)
                return false;
            TreePath treePath = getSelectionPath();
            if (treePath == null) return false;
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            Object userObject = treeNode.getUserObject();
            if ((userObject instanceof UserObject) && ((UserObject) userObject).isCellEditable())
                return super.isCellEditable(event);
            else
                return false;
        }

        public void determineOffset(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row)
        {
            super.determineOffset(tree, value, sel, expanded, leaf, row);
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
            Object userObject = treeNode.getUserObject();
            Icon icon;
            if (userObject instanceof UserObject)
                icon = ((UserObject) userObject).getIcon();
            else
                icon = EditorTree.this.getIcon(treeNode);
            if (icon != null)
                editingIcon = icon;
        }

        protected void prepareForEditing()
        {
            super.prepareForEditing();
            if (editingComponent != null)
            {
              ((JTextField) editingComponent).selectAll();
              int n = editingComponent.getKeyListeners().length;
              if (n == 0)
              {
                editingComponent.addKeyListener(new KeyListener() {
                  public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
                      _escapePressed = true;
                  }

                  public void keyReleased(KeyEvent event) {
                  }

                  public void keyTyped(KeyEvent event) {
                  }
                });
              }
            }
            _escapePressed = false;
        }

        public void cancelCellEditing()
        {
          String name = ((JTextField) editingComponent).getText();
          super.cancelCellEditing();

          if (_savedUserObject != null)
          {
            UserObject savedUserObject = _savedUserObject;
            if (!_escapePressed && !name.equals(_savedUserObject.getName()))
            {
              DefaultMutableTreeNode treeNode = findNode(_savedUserObject, _root);
              treeNode.setUserObject(name);
              DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
              treeModel.nodeChanged(treeNode);
            }
            savedUserObject.restore();
            _savedUserObject = null;
          }
        }
    }
    private class EditorTreeCellRenderer extends DefaultTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
            Object userObject = treeNode.getUserObject();
            Icon icon;
            if (userObject instanceof UserObject)
            {
                if (isEnabled())
                  icon = ((UserObject) userObject).getIcon();
                else
                  icon = ((UserObject) userObject).getDisabledIcon();
                setToolTipText(((UserObject) userObject).getToolTipText());
            }
            else
            {
                if (isEnabled())
                  icon = EditorTree.this.getIcon(treeNode);
                else
                  icon =  EditorTree.this.getDisabledIcon(treeNode);
                setToolTipText(EditorTree.this.getToolTipText(treeNode));
            }
            if (icon != null)
            {
              setIcon(icon);
              setDisabledIcon(icon);
            }

            return this;
        }
    }
}
