
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Category;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Global;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.Util;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;

/**
 * Descriptors tree view in the editor
 */
public class SB_Descriptors extends EditorTree {

    public static final Class SB_DescriptorsClass=com.stottlerhenke.simbionic.editor.gui.SB_Descriptors.class;
    
    protected static ImageIcon _descriptorsIcon = null;

    // root popup
    protected JPopupMenu _rootPopup;
    protected JMenuItem _insertCategoryItem;

    // descriptor popup (category/descriptor)
    protected JPopupMenu _descriptorPopup;
    protected JMenuItem _insertDescriptorItem;
    protected JMenuItem _renameDescriptorItem;
    protected JMenuItem _deleteDescriptorItem;
    protected JMenuItem _selectDescriptorItem;
    protected JMenuItem _moveUpItem;
    protected JMenuItem _moveDownItem;
    
    private SB_TypeManager _typeManager;

    public SB_Descriptors(SimBionicEditor editor) {
        super(editor);

        _root.setUserObject("Descriptors");

        _rootPopup = new JPopupMenu();
        _insertCategoryItem = new JMenuItem("Insert Descriptor Category...");
        _insertCategoryItem.addActionListener(this);
        _rootPopup.add(_insertCategoryItem);

        _descriptorPopup = new JPopupMenu();
        _insertDescriptorItem = new JMenuItem("Insert Descriptor...");
        _insertDescriptorItem.addActionListener(this);
        _descriptorPopup.add(_insertDescriptorItem);
        _descriptorPopup.addSeparator();
        _renameDescriptorItem = new JMenuItem("Rename");
        _renameDescriptorItem.addActionListener(this);
        _descriptorPopup.add(_renameDescriptorItem);
        _deleteDescriptorItem = new JMenuItem("Delete");
        _deleteDescriptorItem.addActionListener(this);
        _descriptorPopup.add(_deleteDescriptorItem);
        _descriptorPopup.addSeparator();
        _selectDescriptorItem = new JMenuItem("Select Descriptor");
        _selectDescriptorItem.addActionListener(this);
        _descriptorPopup.add(_selectDescriptorItem);
        _descriptorPopup.addSeparator();
        _moveUpItem = new JMenuItem("Move Up");
        _moveUpItem.addActionListener(this);
        _descriptorPopup.add(_moveUpItem);
        _moveDownItem = new JMenuItem("Move Down");
        _moveDownItem.addActionListener(this);
        _descriptorPopup.add(_moveDownItem);

        addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent event) {
            	//getEditor().clearEditItems();
            }

            public void focusLost(FocusEvent event) {
            }
        });
    }

    // convenience accessors
    protected SimBionicEditor getEditor() {
        return (SimBionicEditor) _editor;
    }

    protected SB_ProjectBar getProjectBar() {
        return (SB_ProjectBar) ComponentRegistry.getProjectBar();
    }

    protected SB_TabbedCanvas getTabbedCanvas() {
        return (SB_TabbedCanvas) ComponentRegistry.getContent();
    }

    protected void newDescriptors() {
        _root.removeAllChildren();

        SB_Category category = createCategory("Empty");
        _root.add(new DefaultMutableTreeNode(category));

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.reload();
    }
    
    protected SB_Category createCategory(String name) {
       SimBionicJava dataModel = getDataModel();
       Category categoryModel = new Category();
       categoryModel.setName(name);
       categoryModel.setSelected(true);
       dataModel.addCategory(categoryModel);
       return new SB_Category(categoryModel);
    }
    
    protected SB_Descriptor createDescriptor(String name, Descriptor parentModel) {
       Descriptor descriptorModel = new Descriptor();
       descriptorModel.setName(name);
       parentModel.addDescriptor(descriptorModel);
       return new SB_Descriptor(descriptorModel);
    }
    
    protected SimBionicJava getDataModel() {
       return ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getDataModel();
    }

    public void clearDescriptors() {
        _root.removeAllChildren();

        //DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        //treeModel.reload();
    }
    
    protected void addDescriptor(SB_Category sbCategory, DefaultMutableTreeNode parentNode, Descriptor descriptor) {
       SB_Descriptor sbDescriptor = new SB_Descriptor(descriptor);
       DefaultMutableTreeNode descriptorNode = new DefaultMutableTreeNode(sbDescriptor);
       parentNode.add(descriptorNode);
       for (Descriptor child : descriptor.getDescriptors()) {
          addDescriptor(sbCategory, descriptorNode, child);
       }
       if (descriptor.isSelected()) {
          sbCategory.setSelectedDescriptor(sbDescriptor);
       } 
    }
    
    
    protected void open(SimBionicJava dataModel) {
       
       _root.removeAllChildren();
       
       for (Category category : dataModel.getCategories()) {
          SB_Category sbCategory = new SB_Category(category);
          DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(sbCategory);
          _root.add(categoryNode);
          
          for (Descriptor descriptor : category.getDescriptors()) {
             addDescriptor(sbCategory, categoryNode, descriptor);
          }
       }

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.setRoot(_root);

        expandAll();
    } 
    
    protected SB_Global createGlobal(SimBionicJava dataModel, String name, String type, boolean polymorphic) {
       Global globalModel = new Global();
       globalModel.setName(name);
       globalModel.setType(type);
       globalModel.setPolymorphic(polymorphic);
       dataModel.addGlobal(globalModel);
       return  new SB_Global(globalModel);
    }


    protected void mouseRightPressed(DefaultMutableTreeNode treeNode, int x,
            int y) {
        if (treeNode == _root) _rootPopup.show(this, x, y);
    }

    protected void mouseRightPressed(com.stottlerhenke.simbionic.editor.UserObject userObject, int x, int y) {
        if (userObject instanceof SB_Descriptor) {
            TreePath treePath = getSelectionPath();
            int count = treePath.getPathCount();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                    .getLastPathComponent();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath
                    .getPathComponent(count - 2);
            _deleteDescriptorItem.setEnabled(count > 2
                    || parentNode.getChildCount() > 1);
            _selectDescriptorItem.setEnabled(!getTabbedCanvas()._behavior
                    .isCore());
            int index = treeModel.getIndexOfChild(parentNode, treeNode);
            _moveUpItem.setEnabled(index > 0);
            _moveDownItem.setEnabled(index < treeModel
                    .getChildCount(parentNode) - 1);
            _descriptorPopup.show(this, x, y);
        }
    }

    protected void iconPressed(com.stottlerhenke.simbionic.editor.UserObject userObject) {
        if (getTabbedCanvas()._behavior.isCore()) return;

        TreePath treePath = getSelectionPath();
        DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) treePath
                .getLastPathComponent();
        DefaultMutableTreeNode baseNode = (DefaultMutableTreeNode) treePath
                .getPathComponent(1);
        if (selectDescriptor(selNode, baseNode)) {
            SB_TabbedCanvas tabbedCanvas = getTabbedCanvas();
            SB_Canvas canvas = tabbedCanvas.getActiveCanvas();
            canvas._poly.updateIndices(SB_Catalog.kPolyIndexSelect, _root
                    .getIndex(baseNode), selNode.toString(), null);
            int index = tabbedCanvas.getSelectedIndex();
            tabbedCanvas.setTitleAt(index, canvas._poly.getIndicesLabel());
        }
    }

    protected void handleMenuItem(JMenuItem menuItem, TreePath treePath) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                .getLastPathComponent();
        Object userObject = treeNode.getUserObject();

        SB_Catalog catalog = getProjectBar()._catalog;
        com.stottlerhenke.simbionic.editor.UserObject childUserObject = null;
        if (menuItem == _insertCategoryItem) {
            childUserObject = createCategory("NewCategory");
            if (getTabbedCanvas()._behavior.isCore()) {
                ((SB_Category) childUserObject).setSelected(false);
                ((SB_Category) childUserObject).setSelectedDescriptor(null);
            }
        } else if (menuItem == _insertDescriptorItem) {
            Descriptor descriptorModel = ((SB_Descriptor)userObject).getDataModel();
            childUserObject = createDescriptor("NewDescriptor", descriptorModel);
            getProjectBar().setProjectModified(true);
        }
        if (childUserObject != null) {
            childUserObject.setNameToNextAvailable(treeNode);
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                    childUserObject);
            insertNodeInto(childNode, treeNode);
            startEditingAtPath(new TreePath(childNode.getPath()));
            if (menuItem == _insertCategoryItem) {
                catalog.updatePolyIndices(SB_Catalog.kPolyIndexInsert, _root
                        .getIndex(childNode), childUserObject.getName(), null);
                if (getTabbedCanvas()._behavior.isCore())
                        updatePolyIndices(getTabbedCanvas().getActiveCanvas()._poly);
            }
            return;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        if (menuItem == _renameDescriptorItem) {
            startEditingAtPath(treePath);
        } else if (menuItem == _deleteDescriptorItem) {
            DefaultMutableTreeNode baseNode = (DefaultMutableTreeNode) treePath
                    .getPathComponent(1);
            SB_Category category = (SB_Category) baseNode.getUserObject();
            
            if (treeNode == baseNode) {
            	// removing category
            	getDataModel().removeCategory((Category)category.getDataModel());
                catalog.updatePolyIndices(SB_Catalog.kPolyIndexDelete, _root
                        .getIndex(baseNode), category.getName(), null);
            } else {
                SB_Descriptor descriptor = (SB_Descriptor) userObject;
                Descriptor descriptorModelToRemove = descriptor.getDataModel();
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treeNode.getParent();
                SB_Descriptor parent = (SB_Descriptor)parentNode.getUserObject();
                parent.getDataModel().removeDescriptor(descriptorModelToRemove);
                
                if (descriptor.isSelected()) {
                    category.setSelected(true);
                    category.setSelectedDescriptor(category);
                    catalog.updatePolyIndices(SB_Catalog.kPolyIndexRename,
                            _root.getIndex(baseNode), descriptor.getName(),
                            category.getName());
                }
            }
            treeModel.removeNodeFromParent(treeNode);
            if (getTabbedCanvas()._behavior.isCore())
                    updatePolyIndices(getTabbedCanvas().getActiveCanvas()._poly);
        } else if (menuItem == _selectDescriptorItem) {
            iconPressed((com.stottlerhenke.simbionic.editor.UserObject) userObject);
        } else if (menuItem == _moveUpItem || menuItem == _moveDownItem) {
            int count = treePath.getPathCount();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath
                    .getPathComponent(count - 2);
            int index = treeModel.getIndexOfChild(parentNode, treeNode);
            treeModel.removeNodeFromParent(treeNode);
            if (menuItem == _moveUpItem) {
                treeModel.insertNodeInto(treeNode, parentNode, index - 1);
                if (count == 2)
                        catalog.updatePolyIndices(SB_Catalog.kPolyIndexMoveUp,
                                index, null, null);
            } else {
                treeModel.insertNodeInto(treeNode, parentNode, index + 1);
                if (count == 2)
                        catalog.updatePolyIndices(
                                SB_Catalog.kPolyIndexMoveDown, index, null,
                                null);
            }
            if (getTabbedCanvas()._behavior.isCore())
                    updatePolyIndices(getTabbedCanvas().getActiveCanvas()._poly);
        }
    }

    protected ImageIcon getDescriptorsIcon() {
        if (_descriptorsIcon == null)
                _descriptorsIcon = Util.getImageIcon("Descriptors.gif");
        return _descriptorsIcon;
    }

    protected Icon getIcon(DefaultMutableTreeNode treeNode) {
        if (treeNode == _root)
            return getDescriptorsIcon();
        else
            return null;
    }

    protected void updatePolyIndices(SB_Polymorphism poly) {
        if (poly == null) return;

        DefaultMutableTreeNode baseNode = (DefaultMutableTreeNode) _root
                .getFirstChild();
        DefaultMutableTreeNode selNode;
        int size = poly.getIndices().size();
        for (int i = 0; i < size; ++i) {
            if (baseNode == null) return;
            selNode = findNodeByName((String) poly.getIndices().get(i), baseNode);
            selectDescriptor(selNode, baseNode);
            baseNode = baseNode.getNextSibling();
        }
        while (baseNode != null) {
            selectDescriptor(null, baseNode);
            baseNode = baseNode.getNextSibling();
        }
    }

    protected Vector getBasePolyIndices() {
        Vector polyIndices = new Vector();
        int size = _root.getChildCount();
        for (int i = 0; i < size; ++i)
            polyIndices.add(_root.getChildAt(i).toString());
        return polyIndices;
    }

    public boolean validPolyIndices(Vector indices) {
        int size = _root.getChildCount();
        if (size != indices.size()) return false;
        DefaultMutableTreeNode childNode;
        for (int i = 0; i < size; ++i) {
            childNode = (DefaultMutableTreeNode) _root.getChildAt(i);
            if (findNodeByName(indices.get(i).toString(), childNode) == null)
                    return false;
        }
        return true;
    }

    protected boolean selectDescriptor(DefaultMutableTreeNode selNode,
            DefaultMutableTreeNode baseNode) {
       
        SB_Category category = (SB_Category) baseNode.getUserObject();
        SB_Descriptor selDescriptor = selNode != null ? (SB_Descriptor) selNode
                .getUserObject() : null;
        if (selDescriptor == category.getSelectedDescriptor()) return false;
        
        if (category.getSelectedDescriptor() != null)
                category.getSelectedDescriptor().setSelected(false);
        
        category.setSelectedDescriptor(selDescriptor);
        if (selDescriptor != null) selDescriptor.setSelected(true);
        
        // redraw the view
        DefaultTreeModel treeModel = (DefaultTreeModel) this.treeModel;
        treeModel.reload(baseNode);
        expandAll();
        return true;
    }

    protected void nodeRenamed(DefaultMutableTreeNode node, String oldName) {
        SB_Descriptor descriptor = (SB_Descriptor) node.getUserObject();
        TreePath treePath = new TreePath(node.getPath());
        DefaultMutableTreeNode baseNode = (DefaultMutableTreeNode) treePath
                .getPathComponent(1);

        getProjectBar()._catalog.updatePolyIndices(SB_Catalog.kPolyIndexRename,
                _root.getIndex(baseNode), oldName, descriptor.getName());
    }

    protected void checkError(I_CompileValidator validator) {
       int count = _root.getChildCount();
       for (int i = 0; i < count; ++i) {
          validator.validateDescriptor((DefaultMutableTreeNode) _root.getChildAt(i));
       }
    }

    public void setTypeManager(SB_TypeManager typeManager){
    	_typeManager = typeManager;
    }
    
}

