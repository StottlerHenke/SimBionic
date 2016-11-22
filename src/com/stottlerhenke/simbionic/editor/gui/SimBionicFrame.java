
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.stottlerhenke.simbionic.editor.FileManager;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * The main frame for the application
 *
 */
public class SimBionicFrame extends JFrame
{
    protected static SimBionicEditor _simbionic = null;

    //
    // The graphical elements
    //

    protected JPanel _editorPanel;

    static protected JComponent _defaultOutputBar;

    /**
     * Holds the default SimBionic project bar
     */
    protected SB_ProjectBar _projectBar;

    /**
     * Holds the tool bar.
     */
    protected SB_ToolBar _toolBar;

    /**
     * Holds something or other
     */
    protected JComponent _content;

    /**
     * Hold me
     */
    protected JComponent _outputBar;

    protected JMenuBar _menuBar;

    /**
     * Holds SB_ProjectBar and Canvas
     */
    protected JSplitPane splitPaneInner;

    /**
     * Holds inner split pane and the _outputBar
     */
    protected JSplitPane splitPaneOuter;

    /**
     * Holds execution stack and variables panels
     */
    protected JSplitPane debuggerInnerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);


    public SimBionicFrame(String fileName)
    {
        super("SimBionic");

        ComponentRegistry.setFrame(this);
        SimBionicEditor.standalone = true;

        _simbionic = new SimBionicEditor();

        _projectBar = new SB_ProjectBar(_simbionic);
        _simbionic.setProjectBar(_projectBar);

        _toolBar = new SB_ToolBar(_simbionic); // may need to call
                                                // simbionic.createStandaloneToolBar()

        _simbionic.toolbar = _toolBar;  // TODO rth remove this serious hack for ARASCMI

        _content = new SB_TabbedCanvas(_simbionic);
        _outputBar = new SB_OutputBar(_simbionic);
        SB_LocalsTree localsTree = new SB_LocalsTree(_simbionic);
        _projectBar._localsTree = localsTree;

        splitPaneInner = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _projectBar,
                _content);

        splitPaneOuter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPaneInner,
                _outputBar);


        setLayoutPreferences();

        this.getContentPane().add(splitPaneOuter, BorderLayout.CENTER);
        this.getContentPane().add(_toolBar, BorderLayout.NORTH);

        SB_MenuBar _menuBar = new SB_MenuBar();
        _menuBar.create(_simbionic);
        this.setJMenuBar(_menuBar);

        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosing(WindowEvent event)
            {
                if (_simbionic.saveIfModified())
                {
                    System.exit(0);
                }
            }
        });

        // TODO Action should be called from SimBionicEditor application
        ComponentRegistry.getProjectBar().newProject();

        if (fileName != null) {
            File projectFile = new File(fileName);
            _projectBar.loadProject( projectFile );
        }
    }

	private void setLayoutPreferences() {
		splitPaneInner.setOneTouchExpandable(true);
        splitPaneInner.setDividerLocation(288);
        splitPaneInner.setPreferredSize(new Dimension(965, 585));

        splitPaneOuter.setOneTouchExpandable(true);
        splitPaneOuter.setDividerLocation(471);
        splitPaneOuter.setPreferredSize(new Dimension(965, 615));
	}


    public void updateTitle()
    {
        Frame frame = ComponentRegistry.getFrame();
        if (frame == null)
            return;

        String projectName = FileManager.getInstance().getProjectFileName();
        if(ComponentRegistry.getProjectBar() != null && ComponentRegistry.getProjectBar()._projectFile != null)
        	projectName = ComponentRegistry.getProjectBar()._projectFile.getName();

        if (projectName == null || projectName.length() == 0)
        {
            projectName = "Untitled - ";
        } else
        {
            projectName += " - ";
        }
        SB_Behavior behavior = (ComponentRegistry.getContent())._behavior;
        if (behavior == null)
        {
            frame.setTitle(projectName);
            return;
        }
        String behaviorName = behavior.getName();
        if (behavior.isCore())
            behaviorName += " (Reserved)";
        String title = projectName + getApplicationName() + " - [" + behaviorName + "]";
        frame.setTitle(title);
    }

    public String getApplicationName()
    {
        return "SimBionic";
    }

    /**
     * Reassigns components to put Frame in Debug mode.
     *
     * @param _debugger
     */
    public void setDebugModeOn(SB_Debugger _debugger){
    	System.out.println("Debug mode ON");

    	// put Entities pane in tabbed pane with Catalog
    	JTabbedPane leftTabbed = new JTabbedPane();
        leftTabbed.insertTab("Entities",null,_debugger.entitiesPanel,"Entities currently controlled by the engine",0);
    	leftTabbed.insertTab("Catalog",null,_projectBar,"Catalog of all project elements",1);
    	splitPaneInner.setLeftComponent(leftTabbed);
    	splitPaneInner.setDividerLocation(.43);

    	// add other Debug Panels to a tabbed panel at bottom with the output bar
    	debuggerInnerSplitPane.setLeftComponent(_debugger.execStackPanel);
    	debuggerInnerSplitPane.setRightComponent(_debugger.variablesPanel);
    	JTabbedPane bottomTabbed = new JTabbedPane();
        bottomTabbed.insertTab("Execution Stack",null,debuggerInnerSplitPane,"Execution stack and variable values",0);
        bottomTabbed.insertTab("Breakpoints",null,_simbionic._breakpointFrame,"Breakpoints",1);
        bottomTabbed.insertTab("Debug Messages",null,_outputBar,"Messages between the debug client and server",2);
    	splitPaneOuter.setBottomComponent(bottomTabbed);
    	splitPaneOuter.setDividerLocation(.67);

    	((SB_TabbedCanvas)_content).setDebugMode(true);
    	_toolBar.setDebugModeOn();
    	_simbionic.setDebugModeOn();

    	this.repaint();

    }

    /**
     * Reassigns components to reset Frame to default editing mode.
     *
     */
    public void setDebugModeOff(SB_Debugger _debugger){
    	System.out.println("Debug mode OFF");

    	splitPaneInner.setLeftComponent(_projectBar);
    	splitPaneOuter.setBottomComponent(_outputBar);
    	setLayoutPreferences();

    	//propogate notification to other components
    	((SB_TabbedCanvas)_content).setDebugMode(false);
    	_toolBar.setDebugModeOff();
    	_simbionic.setDebugModeOff();

    	this.repaint();

    }

    public static void main(String[] args)
    {
        try
        {
            String laf = System.getProperty("swing.defaultlaf", UIManager
                    .getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(laf);
        } catch (Exception e)
        {
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ee)
            {
            }
        }

        String fileName = null;
        if (args.length > 0) {
            fileName = args[0];
        }

        SimBionicFrame frame = new SimBionicFrame(fileName);
        frame.pack();
        frame.setVisible(true);
    }

}