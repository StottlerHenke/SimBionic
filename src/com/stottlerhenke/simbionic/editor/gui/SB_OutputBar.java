package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;

public class SB_OutputBar extends JTabbedPane
{

    public static final int BUILD_INDEX = 0;
    public static final int DEBUG_INDEX = 1;
    public static final int FIND_INDEX = 2;

    private static final long serialVersionUID = 6229832116617727448L;

    protected static SimBionicEditor _editor;

    protected static SB_Output _build;
    protected static SB_Output _debug;
    protected static SB_Output _find;

    protected static SB_OutputBar _instance;

    public static SB_OutputBar getInstance()
    {// SimBionic editor){
        if (_instance == null)
            _instance = new SB_OutputBar();
        return _instance;
    }

    public SB_OutputBar(SimBionicEditor editor)
    {
        this();
        ComponentRegistry.setOutputBar(this);
        this.setSimBionic(editor);
    }

    public void setSimBionic(SimBionicEditor editor)
    {
        if (_editor != null && this.getTabCount() > 1)
        {
            this.removeTabAt(1);
            this.removeTabAt(0);
        }
        _editor = editor;

        _build = _editor.createOutput();
        _build._isBuild = true;
        JScrollPane scrollBuild = new JScrollPane(_build);
        scrollBuild.getVerticalScrollBar().setUnitIncrement(SB_Output._font_point + 4);
        addTab("Build", scrollBuild);
        /*
         * _build.addLine(new SB_Line("Test")); _build.addLine(new SB_Line("Test
         * Again", Color.red, null, null, null)); _build.addLine(new
         * SB_Line("0")); _build.addLine(new SB_Line("1")); _build.addLine(new
         * SB_Line("2")); _build.addLine(new SB_Line("3")); _build.addLine(new
         * SB_Line("4")); _build.addLine(new SB_Line("5")); _build.addLine(new
         * SB_Line("6")); _build.addLine(new SB_Line("7")); _build.addLine(new
         * SB_Line("8")); _build.addLine(new SB_Line("9"));
         */

        _debug = _editor.createOutput();
        JScrollPane scrollDebug = new JScrollPane(_debug);
        scrollDebug.getVerticalScrollBar().setUnitIncrement(SB_Output._font_point + 4);
        addTab("Debug", scrollDebug);

        _find = _editor.createOutput();
        JScrollPane scrollFind = new JScrollPane(_find);
        scrollFind.getVerticalScrollBar().setUnitIncrement(SB_Output._font_point + 4);
        addTab("Find", scrollFind);
    }

    protected SB_OutputBar()
    {
        super(BOTTOM);
        _instance = this;
        setMinimumSize(new Dimension(100, 50));
    }

    public SB_Output getBuildOutput()
    {
        return _build;
    }

    public SB_Output getDebugOutput()
    {
        return _debug;
    }

    public SB_Output getFindOutput()
    {
        return _find;
    }

    /**
     * @return Returns the build.
     */
    public SB_Output getBuild()
    {
        return _build;
    }

    /**
     * @param build
     *            The build to set.
     */
    public void setBuild(SB_Output build)
    {
        _build = build;
    }

    /**
     * @return Returns the find.
     */
    public SB_Output getFind()
    {
        return _find;
    }
}