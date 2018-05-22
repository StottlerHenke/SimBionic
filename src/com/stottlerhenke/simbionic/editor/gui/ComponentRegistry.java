
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Container;

import javax.swing.JFrame;

public class ComponentRegistry
{
    /**
     * This holds the one and only
     */
    private static ComponentRegistry mRegistry = new ComponentRegistry();

    private JFrame _mainFrame;
    private SB_TabbedCanvas _content;
    private SB_ProjectBar _project;
    private SB_OutputBar _output;
    private SB_ToolBar _toolbar;
    private SB_LocalsTree _localsTree;

    /**
     * XXX: 2018-05-21 This is an expedient way to replicate how SB_ToolBar
     * was able to "receive events" from every SB_Canvas instance. A better
     * approach would be to restructure SB_TabbedCanvas and SB_Canvas to accept
     * listeners.
     * */
    private NodeEditorPanel nodeEditorPanel;

    // private SB_MenuBar _menubar;

    private ComponentRegistry()
    {
        System.out.println("Component registry initialized!");
    }

    protected static void setFrame(JFrame frame)
    {
        mRegistry._mainFrame = frame;
    }

    public static JFrame getFrame()
    {
        if (mRegistry._mainFrame != null)
            return mRegistry._mainFrame;
        else
        {
            Container container;
            if (mRegistry._content != null)
                container = mRegistry._content.getParent();
            else if (mRegistry._project != null)
                container = mRegistry._project.getParent();
            else
                return null;

            while ((container != null) && !(container instanceof JFrame))
            {
                container = (Container) container.getParent();
            }
            return (JFrame) container;
        }
    }

    /**
     * @return Returns the _content.
     */
    public static SB_TabbedCanvas getContent()
    {
        return mRegistry._content;
    }

    /**
     * @param _content
     *            The _content to set.
     */
    protected static void setContent(SB_TabbedCanvas content)
    {
        mRegistry._content = content;
    }

    /**
     * @return Returns the _output.
     */
    public static SB_OutputBar getOutputBar()
    {
        return mRegistry._output;
    }

    /**
     * @param _output
     *            The _output to set.
     */
    protected static void setOutputBar(SB_OutputBar output)
    {
        mRegistry._output = output;
    }

    /**
     * @return Returns the _project.
     */
    public static SB_ProjectBar getProjectBar()
    {
        return mRegistry._project;
    }

    /**
     * @param _project
     *            The _project to set.
     */
    protected static void setProjectBar(SB_ProjectBar project)
    {
        mRegistry._project = project;
    }

    /**
     * @return Returns the _toolbar.
     */
    public static SB_ToolBar getToolBar()
    {
        return mRegistry._toolbar;
    }

    /**
     * @param _toolbar
     *            The _toolbar to set.
     */
    protected static void setToolbar(SB_ToolBar toolbar)
    {
        mRegistry._toolbar = toolbar;
    }

    /**
     * @return
     */
    public static boolean isStandAlone()
    {
        return mRegistry._mainFrame != null;
    }

    /**
     * @return Returns the localsTree.
     */
    public static SB_LocalsTree getLocalsTree()
    {
        return mRegistry._localsTree;
    }

    /**
     * Sets the value of localsTree.
     * 
     * @param localsTree
     *            The new value of localsTree.
     */
    public static void setLocalsTree(SB_LocalsTree localsTree)
    {
        mRegistry._localsTree = localsTree;
    }

    /**
     * Disposes of all singletons.
     */
    public static void clear()
    {
        mRegistry = null;
    }

    /**
     * XXX: stop-gap measure; should be removed with future ComponentRegistry
     * refactoring.
     * */
    static NodeEditorPanel getEditorPanel() {
        return mRegistry.nodeEditorPanel;
    }

    /**
     * XXX: stop-gap measure; should be removed with future ComponentRegistry
     * refactoring.
     * */
    static void setEditorPanel(NodeEditorPanel editorPanel) {
        mRegistry.nodeEditorPanel = editorPanel;
    }

}
