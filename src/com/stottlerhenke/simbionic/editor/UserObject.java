
package com.stottlerhenke.simbionic.editor;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

abstract public class UserObject implements Externalizable {

    private static final long serialVersionUID = 2302585093L;

    private String _name;

    public boolean _editing = false;
    
    public UserObject() {
       
    }

    public UserObject(String name) {
        setName(name);
    }

    public String toString() {
        return getName();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    abstract public Icon getIcon();

    public Icon getDisabledIcon() {
        return getIcon();
    }

    public String getToolTipText() {
        return null;
    }

    public boolean isCellEditable() {
        return true;
    }

    public boolean shouldSort() {
        return false;
    }

    public String getSortName() {
        return getName();
    }

    public void prepare() {
        _editing = true;
    }

    public void restore() {
        _editing = false;
    }

    protected Class[] getRelatedClasses() {
        return null;
    } // classes to check for name conflicts

    public boolean update(DefaultMutableTreeNode node) {
        _editing = false;

        String name = node.toString();
        if (isNameAvailable(name, node, false)) {
            setName(name);
            return true;
        } else
            return false;
    }

    public boolean isNameValid(String name) {
        return name.matches("[_a-zA-Z]{1}[_a-zA-Z0-9]*");
    }

    public boolean isNameAvailable(String name, DefaultMutableTreeNode node,
            boolean includeNode) {
        Class[] classes = getRelatedClasses();
        if (classes != null) {
            DefaultMutableTreeNode next;
            Object userObject;
            Enumeration e = ((DefaultMutableTreeNode) node.getRoot())
                    .preorderEnumeration();
            while (e.hasMoreElements()) {
                next = (DefaultMutableTreeNode) e.nextElement();
                if (includeNode || next != node) {
                    userObject = next.getUserObject();
                    for (int i = 0; i < classes.length; ++i)
                        if (classes[i].isInstance(userObject)
                                && ((UserObject) userObject).getName().equals(name))
                            return false;
                }
            }
        } else {
            if (includeNode
                    && ((UserObject) node.getUserObject()).getName().equals(name))
                return false;
            DefaultMutableTreeNode sibling = node.getPreviousSibling();
            while (sibling != null) {
                if (((UserObject) sibling.getUserObject()).getName().equals(name))
                    return false;
                sibling = sibling.getPreviousSibling();
            }
            sibling = node.getNextSibling();
            while (sibling != null) {
                if (((UserObject) sibling.getUserObject()).getName().equals(name))
                    return false;
                sibling = sibling.getNextSibling();
            }
        }
        return true;
    }

    public void setNameToNextAvailable(DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode node;
        boolean includeNode = false;
        if (getRelatedClasses() == null) {
            if (parent.isLeaf())
                return;
            node = (DefaultMutableTreeNode) parent.getFirstChild();
            includeNode = true;
        } else
            node = (DefaultMutableTreeNode) parent.getRoot();
        if (isNameAvailable(getName(), node, includeNode))
            return;
        int suffix = 2;
        while (!isNameAvailable(getName() + suffix, node, includeNode))
            ++suffix;
        setName(getName() + suffix);
    }

    public boolean isNameAvailable(String name, Vector userObjects,
            boolean includeNode) {
        UserObject userObject;
        int size = userObjects.size();
        for (int i = 0; i < size; ++i) {
            userObject = (UserObject) userObjects.get(i);
            if (!includeNode && userObject == this)
                continue;
            if (name.equals(userObject.getName()))
                return false;
        }
        return true;
    }

    public void setNameToNextAvailable(Vector userObjects) {
        if (isNameAvailable(getName(), userObjects, false))
            return;
        int suffix = 2;
        while (!isNameAvailable(getName() + suffix, userObjects, false))
            ++suffix;
        setName(getName() + suffix);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getName());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException,
            IOException {
        setName((String) in.readObject());
    }
}