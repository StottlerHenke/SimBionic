package com.stottlerhenke.simbionic.editor;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_Canvas;
import com.stottlerhenke.simbionic.editor.gui.SB_Catalog;
import com.stottlerhenke.simbionic.editor.gui.SB_CommentHolder;
import com.stottlerhenke.simbionic.editor.gui.SB_Connector;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_Element;
import com.stottlerhenke.simbionic.editor.gui.SB_LocalsTree;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.gui.SB_Rectangle;
import com.stottlerhenke.simbionic.editor.gui.SB_TabbedCanvas;

/**
 * SimBionicEditorAPI is an API that allows other applications to easily access
 * important SimBionic editor code without having to know the guts of the
 * SimBionic editor. This class is only being written "as necessary" and is not
 * going to be comprehensive, so there will be missing methods, parameters, etc.
 * This class will be useful for client applications that want to customize
 * SimBionic - ie add shortcuts, build behaviors automatically, etc. <br>
 * <br>
 * The functions here should just call functions that do the real work in other
 * parts of SimBionic; these functions should not re-implement existing code or
 * do any actual implementation details.
 * 
 */
public class SimBionicEditorAPI
{
    private SimBionicEditorAPI()
    {
    }

    /**
     * @return The active canvas, or null if there isn't one.
     */
    private static SB_Canvas getCanvas()
    {
        SB_TabbedCanvas tcanvas = ComponentRegistry.getContent();
        if (tcanvas == null)
            return null;
        SB_Canvas canvas = tcanvas.getActiveCanvas();
        return canvas;
    }

    /**
     * If the behavior exists in the project, select the behavior in the catalog
     * and show it on the canvas. TODO Selecting the behavior works, but opening
     * it does not.
     * 
     * @param behavior
     *            The behavior to display on the simbionic canvas
     */
    public static void openBehavior(final SB_Behavior behavior)
    {
        final SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();

        DefaultMutableTreeNode treeNode = catalog.findNode(behavior, catalog.getRoot());
        if (treeNode == null)
            return;
        final TreePath treePath = new TreePath(treeNode.getPath());
        catalog.setSelectionPath(treePath);
        catalog.expandPath(treePath);
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // FIXME This is currently protected
                // ComponentRegistry.getContent().setBehavior(behavior, true);
                catalog.scrollPathToVisible(treePath);
            }
        });

    }

    /**
     * @param folderName
     *            The name of a behaviors folder, or null if the behavior should
     *            go in the top-level "Behaviors".
     * @param behaviorName
     *            The name of the new behavior. This name will get changed if a
     *            behavior already exists with this name or if the name is
     *            invalid.
     * @param executionMode
     *            One of SB_Behavior.kExecMultiTick, SB_Behavior.kExecOneTick,
     *            SB_Behavior.kExecUntilBlocked.
     * 
     * @return The newly created behavior, or null if one cannot be created.
     *         Will return null if the folder cannot be found, or if SimBionic
     *         hasn't been initialized yet.
     */
   /* public static SB_Behavior insertBehavior(String folderName, String behaviorName,
            int executionMode)
    {
        return insertBehavior(folderName,behaviorName,executionMode,SB_Behavior.kInterruptYes);
    } */

    /**
     * @param folderName
     *            The name of a behaviors folder, or null if the behavior should
     *            go in the top-level "Behaviors".
     * @param behaviorName
     *            The name of the new behavior. This name will get changed if a
     *            behavior already exists with this name or if the name is
     *            invalid.
     * @param executionMode
     *            One of SB_Behavior.kExecMultiTick, SB_Behavior.kExecOneTick,
     *            SB_Behavior.kExecUntilBlocked.
     * @param interruptMode
     *            One of SB_Behavior.kInterruptYes, SB_Behavior.kInterruptNo
     * 
     * @return The newly created behavior, or null if one cannot be created.
     *         Will return null if the folder cannot be found, or if SimBionic
     *         hasn't been initialized yet.
     */
  /*  public static SB_Behavior insertBehavior(String folderName, String behaviorName,
            int executionMode, int interruptMode)
    {
        SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();
        if (catalog == null)
            return null;
        SB_Behavior behavior = catalog.insertBehavior(folderName, behaviorName);
        if (behavior != null)
        {
            behavior.setExec(executionMode);
            behavior.setInterrupt(interruptMode);
        }
        return behavior;
    } */

    /**
     * Gets the selected item from the catalog, and returns the name of the
     * most-specific folder that the item is in (including the item itself).
     * FIXME This returns any folder (from actions, preds, behaviors, etc), so
     * is currently not very useful. Need a way to check which catalog the
     * folder/selected item is in.
     * 
     * @return If the user has a item in the catalog selected, returns the name
     *         of the most-specific folder that the item is in, or the item
     *         itself, if it is a folder. If there is no item selected, or the
     *         item is not in a folder, returns null.
     * 
     */
    public static String getSelectedFolderName()
    {
        SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();
        if (catalog == null)
            return null;
        TreePath p = catalog.getSelectionPath();
        if (p == null)
            return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
        DefaultMutableTreeNode parent = node;
        while (parent != null)
        {
            Object o = parent.getUserObject();
            if (o instanceof SB_Folder)
            {
                return ((SB_Folder) o).getName();
            }
            parent = (DefaultMutableTreeNode) parent.getParent();
        }

        return null;
    }

    /**
     * Inserts an action rectangle on the currently active canvas.
     * 
     * @param p
     *            Middle point of the new action rectangle on the currently
     *            active canvas.
     * @param actionExpression
     *            Expression for the action (is not yet compiled).
     * @param fullLabel
     *            If true, set the label to be a full label; if false, set to
     *            truncated label. You can also set it to be a comment label,
     *            but since the new action doesn't have a comment yet, this
     *            function doesn't provide that option.
     * @return The newly created action rectangle, or null if one couldn't be
     *         created (ie, if SimBionic hasnt been initialized yet, or if there
     *         is no currently active canvas).
     */
    public static SB_Rectangle insertAction(Point p, String actionExpression, boolean fullLabel)
    {
        SB_Canvas canvas = getCanvas();
        if (canvas == null)
            return null;
        SB_Rectangle rect = canvas.insertAction(p, actionExpression);
        if (rect != null)
            rect.setLabelMode(fullLabel ? SB_CommentHolder.FULL_LABEL
                    : SB_CommentHolder.TRUNCATED_LABEL);
        return rect;
    }

    /**
     * Adds a connector to the active canvas between two canvas elements on the
     * active canvas. Warning: Strange things will happen if the to/from
     * elements are not on the active canvas! TODO We should figure out a way to
     * do this without passing the SB_Elements around.
     * 
     * @param from
     *            Start point of connector.
     * @param to
     *            End point of connector.
     * @return The newly created connector, or null if one could not be created.
     *         If from or to is null, or there is no active canvas, or SimBionic
     *         hasn't been initialized, this will return null.
     */
    public static SB_Connector insertConnector(SB_Element from, SB_Element to)
    {
        SB_Canvas canvas = getCanvas();
        if (canvas == null || from == null || to == null)
            return null;
        return canvas.insertConnector(from, to);
    }

    /**
     * Inserts a condition rectangle on the currently active canvas.
     * 
     * @param p
     *            Middle point of the new condition oval on the currently active
     *            canvas.
     * @param conditionExpression
     *            Expression for the condition (is not yet compiled).
     * @param fullLabel
     *            If true, set the label to be a full label; if false, set to
     *            truncated label. You can also set it to be a comment label,
     *            but since the new condition doesn't have a comment yet, this
     *            function doesn't provide that option.
     * @return The newly created condition rectangle (oval), or null if one
     *         couldn't be created (ie, if SimBionic hasnt been initialized yet,
     *         or if there is no currently active canvas).
     */
    public static SB_Rectangle insertCondition(Point p, String conditionExpression,
            boolean fullLabel)
    {
        SB_Canvas canvas = getCanvas();
        if (canvas == null)
            return null;
        SB_Rectangle rect = canvas.insertAction(p, conditionExpression);
        rect.setLabelMode(fullLabel ? SB_CommentHolder.FULL_LABEL
                : SB_CommentHolder.TRUNCATED_LABEL);
        return rect;
    }

    /**
     * Inserts a local variable into the currently open polymorphism (of the
     * currently open behavior) with the given name and type (see SB_Variable
     * for the list of types).
     * 
     * @param name
     *            Name of new local variable. Will be changed if one already
     *            exists.
     * @param type
     *            An int from SB_Variable representing the type of the new
     *            local.
     * @return True on success.
     */
    public static boolean insertLocal(String name, String type)
    {
        SB_LocalsTree tree = ComponentRegistry.getLocalsTree();
        if (tree == null)
            return false;
        tree.insertLocal(name, type);
        tree.revalidate();
        tree.repaint();
        return true;
    }

    /**
     * Retrieves the behavior with the given name.
     * @param name the name of the behavior to retrieve
     * @return the behavior, or null if it doesn't exist
     */
    public static SB_Behavior getBehavior(String name)
    {
        return (SB_Behavior) getUserObject(name);
    }

    /**
     * Deletes the specified behavior from the project.
     * @param name name of the behavior to delete
     */
    public static void deleteBehavior(String name)
    {
        deleteUserObject(name);
    }

    
    
    /**
     * Retrieves the constant with the given name.
     * @param name the name of the constant to retrieve
     * @return the constant, or null if it doesn't exist
     */
    public static SB_Constant getConstant(String name)
    {
        return (SB_Constant) getUserObject(name);
    }
    
    /**
     * Retrieves the local variables of the currently open polymorphism (of the currently open behavior).
     * @return a list of SB_Variables, or null if there is no active canvas
     */
    public static List getLocals()
    {
		List localsList = new LinkedList();
		SB_Canvas canvas = getCanvas();
		if (canvas == null) {
			return null;
		}
        DefaultMutableTreeNode locals = canvas.getPoly().getLocals();
        int localSize = locals.getChildCount();
        for(int k=0;k<localSize;k++){
        	SB_Variable local = (SB_Variable) ((DefaultMutableTreeNode) locals.getChildAt(k)).getUserObject();
        	localsList.add(local);
        }
        return localsList;
    }
    
    /**
     * Retrieves the parameters of the currently open behavior.
     * @return a list of SB_Parameters, or null if there is no active canvas 
     */
    public static List getParameters()
    {
    	List paramsList = new LinkedList();
    	SB_Canvas canvas = getCanvas();
    	if (canvas == null) {
    		return null;
    	}
    	SB_Polymorphism poly = canvas.getPoly();
    	SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        DefaultMutableTreeNode params = catalog.findNode(poly.getParent(), catalog._behaviors);
        int paramSize = params.getChildCount();
        for (int i = 0; i < paramSize; ++i) {
            SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params.getChildAt(i)).getUserObject();
            paramsList.add(param);
        }
        return paramsList;
    }
    
    /**
     * Retrieves the parameters of the specified behavior.
     * @return List<SB_Parameter> 
     */
    public static List getParameters(String behaviorName)
    {
        List paramsList = new LinkedList();
        SB_Behavior behavior = getBehavior(behaviorName);
        if (behavior == null)
            return paramsList;
        
        SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        DefaultMutableTreeNode params = catalog.findNode(behavior, catalog._behaviors);
        int paramSize = params.getChildCount();
        for (int i = 0; i < paramSize; ++i) {
            SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params.getChildAt(i)).getUserObject();
            paramsList.add(param);
        }
        return paramsList;
    }

    /**
     * Retrieves the global variables.
     * @return a list of SB_Globals
     */
    public static List getGlobals()
    {
    	List globalsList = new LinkedList();
    	SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();
    	DefaultMutableTreeNode globals = catalog._globals;
        int GlobalSize = globals.getChildCount();
        for (int i = 0; i < GlobalSize; ++i) {
            SB_Global global = (SB_Global) ((DefaultMutableTreeNode) globals.getChildAt(i)).getUserObject();
            globalsList.add(global);
        }
        return globalsList;
    }
    
   

    private static Object getUserObject(String name)
    {
        SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();
        DefaultMutableTreeNode node = catalog.findNodeByName(name,catalog.getRoot());
        if (node == null)
            return null;
        
        return node.getUserObject();
    }

    private static void deleteUserObject(String name)
    {
        SB_Catalog catalog = ComponentRegistry.getProjectBar().getCatalog();
        DefaultMutableTreeNode node = catalog.findNodeByName(name,catalog.getRoot());
        if (node != null)
            catalog.deleteNode(node,false);
    }

    /**
     * Contains a behavior element (connector, node, condition) along with
     * the containing polymorphism (which cannot currently be retrieved
     * from the element itself).
     *
     * @author houlette
     */
    public static class ElementContext
    {
        private SB_Drawable _element;
        private SB_Polymorphism _poly;
        
        public ElementContext(SB_Drawable element,SB_Polymorphism poly)
        {
            _element = element;
            _poly = poly;
        }

        public SB_Drawable getElement()
        {
            return _element;
        }

        public SB_Polymorphism getPoly()
        {
            return _poly;
        }
        
    }
}
