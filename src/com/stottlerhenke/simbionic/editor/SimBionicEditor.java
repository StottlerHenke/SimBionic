package com.stottlerhenke.simbionic.editor;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_Autocomplete;
import com.stottlerhenke.simbionic.editor.gui.SB_AutocompleteTextArea;
import com.stottlerhenke.simbionic.editor.gui.SB_BreakpointFrame;
import com.stottlerhenke.simbionic.editor.gui.SB_Catalog;
import com.stottlerhenke.simbionic.editor.gui.SB_Condition;
import com.stottlerhenke.simbionic.editor.gui.SB_Descriptors;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_Element;
import com.stottlerhenke.simbionic.editor.gui.SB_LocalsTree;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.gui.SB_Rectangle;
import com.stottlerhenke.simbionic.editor.gui.SB_ToolBar;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;

/**
 * SimBionic is the SimBionic editor main panel in a Java application, and can also be
 * run as a standalone application. Note: cannot run more than one instance of
 * editor now since parser is singleton.
 * 
 */
public class SimBionicEditor // implements ActionListener
{
    /** maximum number of most recent used files shown in the menu **/
	private final static int MAX_MOST_RECENT_FILES_NUM = 4;
    
    public static final String NEW_COMMAND = "New";
    public static final String NEW_COMMAND_TOOLTIP = "Create a new SimBionic project...";
    public static final String OPEN_COMMAND = "Open...";
    public static final String OPEN_COMMAND_TOOLTIP = "Open a SimBionic project...";
    public static final String SAVE_COMMAND = "Save";
    public static final String SAVEAS_COMMAND = "Save As...";
    public static final String CREATE_SUMMARY_COMMAND = "Create Listing";

    public static final String EXIT_COMMAND = "Exit";
    public static final String EXIT_COMMAND_TOOLTIP = "Exit SimBionic...";

    public static final String BUILD_COMMAND = "Build";
    public static final String CHECK_ERROR_COMMAND = "Check for Errors";

    public static final String EDIT_COMMAND = "Edit";
    public static final String UNDO_COMMAND = "Undo";
    public static final String REDO_COMMAND = "Redo";
    public static final String CUT_COMMAND = "Cut";
    public static final String CUT_NODE = "Cut Node";
    public static final String CUT_LINK = "Cut Link";
    public static final String COPY_COMMAND = "Copy";
    public static final String COPY_NODE = "Copy Node";
    public static final String COPY_LINK = "Copy Link";
    public static final String PASTE_COMMAND = "Paste";
    public static final String DELETE_COMMAND = "Delete";
    public static final String DELETE_NODE = "Delete Node";
    public static final String DELETE_LINK = "Delete Link";
    public static final String SELECTALL_COMMAND = "Select All";
    public static final String FIND_COMMAND = "Find";
    public static final String REPLACE_COMMAND = "Replace";
    public static final String FINDREPLACE_COMMAND = "Find and Replace...";
    public static final String VIEW_COMMAND = "View";
    public static final String BACK_COMMAND = "Go Back";
    public static final String FORWARD_COMMAND = "Go Forward";
    public static final String PREVERROR_COMMAND = "Previous Error";
    public static final String NEXTERROR_COMMAND = "Next Error";

    public static final boolean DEV = false;

    public static final String CHECK_ERROR_ITEM = "Check for Errors";
    public static final String SETTINGS_ITEM = "Connection Settings...";
    public static final String EDIT_MENU = "Edit";
    public static final String UNDO_ITEM = "Undo";
    public static final String REDO_ITEM = "Redo";
    public static final String CUT_ITEM = "Cut";
    public static final String COPY_ITEM = "Copy";
    public static final String PASTE_ITEM = "Paste";
    public static final String DELETE_ITEM = "Delete";
    public static final String FIND_REPLACE_ITEM = "Find and Replace...";
    public static final String VIEW_MENU = "View";
    public static final String BACK_ITEM = "Go Back";
    public static final String FORWARD_ITEM = "Go Forward";
    public static final String PREV_ERROR_ITEM = "Previous Error";
    public static final String NEXT_ERROR_ITEM = "Next Error";
    protected static int _count = 0;

    public SB_ToolBar toolbar;  // TODO rth remove this ARASCMI hack!
    protected SB_ProjectBar _projectBar;
    protected EditorRegistry _editorRegistry;

    protected boolean _debugMode = false;
    
    /**
     * Allows for editing and displaying of breakpoints.
     */
    public SB_BreakpointFrame _breakpointFrame = new SB_BreakpointFrame();
    
    /**
     * <code>_name</code> appears on the MultiEditor tab for this editor.
     * Should be unique among the other editors.
     */
    protected String _name = "unnamed";

    /** Whether or not this editor is visible. */
    protected boolean _visible = true;

    /** Whether this editor's content has been modified since the last save. */
    protected boolean _dirty = false;

    /**
     * This variable describes whether the SimBionic editor is in standalone
     * mode or running as part of another application. This controls things like
     * whether it initializes its own menu or uses another application menu.
     */
    public static boolean standalone = false;

    protected static final int _maxRecent = 4;
    protected JMenuItem[] _recentItems;

    protected SB_Catalog mCatalog;
    protected SB_Descriptors mDescriptors;

    public Action newAction;
    public Action openAction;
    public Action saveAction;
    public Action saveAsAction;
    public Action exitAction;
    public Action createSummary;

    public Action undoAction;
    public Action redoAction;
    public Action cutAction, deleteAction;
    public Action copyAction;
    public Action pasteAction;
    public Action findAction, replaceAction, selectAllAction;

    public Action nextErrorAction;
    public Action prevErrorAction;
    public Action startAction, stopAction, pauseAction;
    public Action stepIntoAction, stepOverAction, stepOneTickAction, runToFinalAction;
    public Action showCurrentAction, connectAction, breakpointAction;
    public Action showBreakpointsAction;
    public Action checkErrorAction, settingsAction;
    public Action javaScriptSettingsAction;

    protected Rectangle _bounds;
    protected boolean _maximized;
    
    /** stack of most recent files opened **/
	private ArrayList _mostRecentUsedFiles;
	private JSeparator _mostRecentFilesSeparator;

    public SimBionicEditor()
    {
        super();
        _name = "SimBionic";
        FileManager.initialize(this);
        // ++_count;
        initializeSharedActions();

        // if (_count > 1) _name += _count;
        // SB_OutputBar sb = SB_OutputBar.getInstance();
        // if (standalone) _outputBar = sb;
        // sb.setSimBionic(this);
        // this.setDefaultOutputBar(sb);
        // SB_ProjectBar projectBar = (SB_ProjectBar) _projectBar;
        // SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) _content;
        //
        // tabbedCanvas.setBehavior(projectBar._catalog._main, false);
    }
    
    public JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem(newAction);
        newItem.setToolTipText(SimBionicEditor.NEW_COMMAND_TOOLTIP);
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem(openAction);
        openItem.setToolTipText(SimBionicEditor.OPEN_COMMAND_TOOLTIP);
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem(saveAction);
        saveItem.setToolTipText(null);
        fileMenu.add(saveItem);
        
        JMenuItem saveAsItem = new JMenuItem(saveAsAction);
        fileMenu.add(saveAsItem);

        fileMenu.addSeparator();
        
        fileMenu.add(new JMenuItem(createSummary));

        fileMenu.addSeparator();
        
        _recentItems = new JMenuItem[MAX_MOST_RECENT_FILES_NUM];
        for (int i = 0; i < MAX_MOST_RECENT_FILES_NUM; i ++) {
        	JMenuItem menuItem = new JMenuItem();
        	menuItem.setVisible(false);
        	_recentItems[i] = menuItem;
        	fileMenu.add(_recentItems[i]);
        }

        _mostRecentFilesSeparator = new JSeparator();
        _mostRecentFilesSeparator.setVisible(false);
        fileMenu.add(_mostRecentFilesSeparator);
        
        setMostRecentFileActions();
        

        JMenuItem exitItem = new JMenuItem(exitAction);
        fileMenu.add(exitItem);
        return fileMenu;
    }
    
    protected SB_ProjectBar createProjectBar()
    {
        return new SB_ProjectBar(this);
    }

    public SB_LocalsTree createLocalsTree()
    {
        return new SB_LocalsTree(this);
    }

    public SB_Output createOutput()
    {
        return new SB_Output(this);
    }

    public SB_Autocomplete createAutocomplete()
    {
        return new SB_Autocomplete(10, this);
    }
    
    public SB_AutocompleteTextArea createAutocompleteTextArea()
    {
        return new SB_AutocompleteTextArea(2,10, this);
    }

    /**
     * SB_AbstractAction is a tiny class that just exists so you dont have to keep
     * typing putValue over and over. If you use AbstractAction instead, don't
     * forget to putValue(Action.ACTION_COMMAND_KEY, COMMAND).
     */
    abstract class SB_AbstractAction extends AbstractAction
    {
        public SB_AbstractAction(String name)
        {
            this(name, null, null, null, null, null);
        }

        public SB_AbstractAction(String name, Icon icon)
        {
            this(name, icon, null, null, null, null);
        }

        public SB_AbstractAction(String name, Icon icon, String description)
        {
            this(name, icon, description, null, null, null);
        }

        public SB_AbstractAction(String name, Icon icon, String description, KeyStroke accelerator,
                Integer mnemonic)
        {
            this(name, icon, description, accelerator, null, mnemonic);
        }

        public SB_AbstractAction(String name, Icon icon, String description, KeyStroke accelerator)
        {
            this(name, icon, description, accelerator, null, null);
        }

        public SB_AbstractAction(String name, Icon icon, String description, KeyStroke accelerator,
                String command, Integer mnemonic)
        {
            super(name, icon);
            if (command == null)
                putValue(Action.ACTION_COMMAND_KEY, name);
            else
                putValue(Action.ACTION_COMMAND_KEY, command);
            if (description != null)
                putValue(Action.SHORT_DESCRIPTION, description);
            if (accelerator != null)
                putValue(Action.ACCELERATOR_KEY, accelerator);
            if (mnemonic != null)
                putValue(Action.MNEMONIC_KEY, mnemonic);
        }
    }
    
    /**
	 * helper class to open a file from the list of most recently used files
	 */
	class OpenMostRecentFileAction extends AbstractAction {
		private int index;
		
		OpenMostRecentFileAction(int index) {
			super((index+1) + " " + ((File)_mostRecentUsedFiles.get(index)).getName());
			this.index = index;
		}
		
		public void actionPerformed(ActionEvent e){
			
			_projectBar.loadProject((File)_mostRecentUsedFiles.get(index));
		}
	}

    /**
     * Initialize the actions that are shared between the standalone and
     * flexitrainer versions of SimBionic. <br>
     * TODO refactor these to point to proper functions (instead of all going to
     * actionPerformed)
     */
public void initializeSharedActions()

    {
        settingsAction = new SB_AbstractAction(SETTINGS_ITEM, null)
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().actionPerformed(e);
            }
        };

        newAction = new SB_AbstractAction(NEW_COMMAND, Util.getImageIcon("New16.gif"), "New", KeyStroke
                .getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK, false), new Integer(KeyEvent.VK_N))
        {
            public void actionPerformed(ActionEvent e)
            {
            	 if (!saveIfModified())
                     return;
                ComponentRegistry.getProjectBar().newProject();
            }
        };

        openAction = new SB_AbstractAction(OPEN_COMMAND, Util.getImageIcon("Open16.gif"), "Open", KeyStroke
                .getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK, false), new Integer(KeyEvent.VK_O))
        {
            public void actionPerformed(ActionEvent e)
            {
                // e.setSource(_openItem);
                if (!saveIfModified())
                    return;
                ComponentRegistry.getProjectBar().openProject();
            }
        };

        saveAction = new SB_AbstractAction(SAVE_COMMAND, Util.getImageIcon("Save16.gif"),"Save",KeyStroke
            .getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK, false), new Integer(KeyEvent.VK_N))
        {
            public void actionPerformed(ActionEvent e)
            {
                // e.setSource(_saveItem);
                ComponentRegistry.getProjectBar().saveProject();
                this.setEnabled(false);
            }
        };
        saveAction.setEnabled(true);

        saveAsAction = new SB_AbstractAction(SAVEAS_COMMAND, Util.getImageIcon("SaveAs16.gif"),"Save As",null,
            new Integer(KeyEvent.VK_A))
        {
            public void actionPerformed(ActionEvent e)
            {
                // e.setSource(_saveItem);
                ComponentRegistry.getProjectBar().saveProjectAs();
            }
        };
        saveAsAction.setEnabled(true);

        exitAction = new SB_AbstractAction(EXIT_COMMAND,null,EXIT_COMMAND_TOOLTIP)
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(isDirty()) {
            		if( ComponentRegistry.getProjectBar().saveIfModified() )
            			System.exit(0);
            	}
            	else
            		System.exit(0);
            }
        };
        
        createSummary = new SB_AbstractAction(CREATE_SUMMARY_COMMAND,null,"Create Summary") {
        	public void actionPerformed(ActionEvent e) {
        		ComponentRegistry.getProjectBar().actionPerformed(e);
        	}
        };

        //
        // Undo
        //
        undoAction = new SB_AbstractAction("Undo", Util.getImageIcon("Undo16.gif"),"Undo",KeyStroke.getKeyStroke(KeyEvent.VK_Z,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_U))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().undo();
            }
        };

        //
        // Redo
        //
        redoAction = new SB_AbstractAction("Redo", Util.getImageIcon("Redo16.gif"),"Redo",KeyStroke.getKeyStroke(KeyEvent.VK_Y,
            InputEvent.CTRL_MASK, false), new Integer(KeyEvent.VK_R))
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().redo();
            }
        };

        findAction = new SB_AbstractAction("Find", Util.getImageIcon("Find16.gif"), "Find",KeyStroke.getKeyStroke(KeyEvent.VK_F,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_F))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().showFindDialog();
            }
        };

        replaceAction = new SB_AbstractAction("Replace", null,"Replace",KeyStroke.getKeyStroke(KeyEvent.VK_H,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_R))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().showReplaceDialog();
            }
        };

        selectAllAction = new SB_AbstractAction("Select All", null, "Select All", KeyStroke.getKeyStroke(KeyEvent.VK_A,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_A))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().selectAll();
            }
        };

        //
        // Cut
        //
        cutAction = new SB_AbstractAction("Cut", Util.getImageIcon("Cut16.gif"),"Cut",KeyStroke.getKeyStroke(KeyEvent.VK_X,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_T))
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().canvasCut();
            }
        };

        // Delete
        deleteAction = new SB_AbstractAction("Delete",Util.getImageIcon("Delete16.gif"),"Delete",KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
            0, false),new Integer(KeyEvent.VK_D))
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().canvasDelete();
            }
        };

        //
        // Copy
        //
        copyAction = new SB_AbstractAction("Copy", Util.getImageIcon("Copy16.gif"),"Copy",KeyStroke.getKeyStroke(KeyEvent.VK_C,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_C))
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().canvasCopy();
            }
        };

        //
        // Paste
        //
        pasteAction = new SB_AbstractAction("Paste", Util.getImageIcon("Paste16.gif"),"Paste",KeyStroke.getKeyStroke(KeyEvent.VK_V,
            InputEvent.CTRL_MASK, false),new Integer(KeyEvent.VK_P))
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getContent().canvasPaste(e);
            }
        };

        prevErrorAction = new SB_AbstractAction("Previous Error")
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().goToPreviousError();
            }
        };
        prevErrorAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP,
            InputEvent.ALT_DOWN_MASK, false));
        prevErrorAction.setEnabled(false);

        nextErrorAction = new SB_AbstractAction("Next Error")
        {

            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().goToNextError();
            }
        };
        nextErrorAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
            InputEvent.ALT_DOWN_MASK, false));
        nextErrorAction.setEnabled(false);

        checkErrorAction = new SB_AbstractAction(CHECK_ERROR_COMMAND, Util.getImageIcon("Compile.gif"), 
        		"Check for Errors",KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0,
            false),new Integer(KeyEvent.VK_C))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().checkError();
            }
        };

        connectAction = new SB_AbstractAction("Connect to Execution Engine", Util.getImageIcon("Connect.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().connectDebug();
            }
        };
        connectAction.putValue(Action.SHORT_DESCRIPTION, "Connect to Execution Engine");

        breakpointAction = new SB_AbstractAction("Toggle Breakpoint", Util.getImageIcon("Breakpoint.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                SB_Drawable drawable = ComponentRegistry.getContent().getActiveCanvas()._selDrawable;
                if(drawable instanceof SB_Rectangle || drawable instanceof SB_Condition){
                	((SB_Element)drawable)._isBreakpoint = !((SB_Element)drawable)._isBreakpoint;
                	ComponentRegistry.getContent().repaint();
                	int nodeId = ((SB_Element)drawable).getId();
                	String behName = ComponentRegistry.getContent().getBehavior().getName();
                	if(((SB_Element)drawable)._isBreakpoint){
                		SB_Breakpoint breakpoint = _breakpointFrame.addBreakpoint();
                		breakpoint._elem = (SB_Element)drawable;
                		if(drawable instanceof SB_Rectangle){breakpoint._type = SB_Breakpoint.RECTANGLE;}
                		else{breakpoint._type = SB_Breakpoint.CONDITION;}
                		breakpoint._behavior = behName;
                		breakpoint._enabled = true;
                		breakpoint._entityId = SB_Breakpoint.ALL_ENTITIES;
                		breakpoint._elemId = nodeId;
                		breakpoint._constraint = "";
                		breakpoint._varName = ((SB_Element)drawable).getExpr();
                		if(_debugMode){
                			_projectBar.addBreakElemDebug(breakpoint);
                		}
                	}
                	else{
                		SB_Breakpoint breakpoint = _breakpointFrame.removeElemBreakpoint(behName, nodeId);
                		if(_debugMode){
                			_projectBar.removeBreakpointDebug(breakpoint);
                		}
                	}
                }
                else{
                	Toolkit.getDefaultToolkit().beep();
                }
            }
        };
        // TODO remove this when implemented
        breakpointAction.setEnabled(true);
        breakpointAction.putValue(Action.SHORT_DESCRIPTION, "Toggle Breakpoint (F9)");
        breakpointAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0,
            false));

        showBreakpointsAction = new SB_AbstractAction("Breakpoint List", Util.getImageIcon("BreakpointsList.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
            	_breakpointFrame.setVisible(true);
            }
        };
        showBreakpointsAction.setEnabled(true);
        showBreakpointsAction.putValue(Action.SHORT_DESCRIPTION, "Breakpoint List");
        
        startAction = new SB_AbstractAction("Start", Util.getImageIcon("Start.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().startDebug();
            }
        };
        startAction.setEnabled(true);
        startAction.putValue(Action.SHORT_DESCRIPTION, "Start (F5)");
        startAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0,
            false));

        stopAction = new SB_AbstractAction("Stop", Util.getImageIcon("Stop.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().stopDebug();
            }
        };
        stopAction.putValue(Action.SHORT_DESCRIPTION, "Stop (Shift+F5)");
        stopAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5,
            InputEvent.SHIFT_MASK, false));

        pauseAction = new SB_AbstractAction("Pause", Util.getImageIcon("Pause.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().pauseDebug();
            }
        };
        pauseAction.setEnabled(false);
        pauseAction.putValue(Action.SHORT_DESCRIPTION, "Pause (F6)");
        pauseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0,
            false));

        stepIntoAction = new SB_AbstractAction("Step Into", Util.getImageIcon("StepInto.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                ComponentRegistry.getProjectBar().stepIntoDebug();
            }
        };
        stepIntoAction.putValue(Action.SHORT_DESCRIPTION, "Step Into (F11)");
        stepIntoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0,
            false));

        stepOverAction = new SB_AbstractAction("Step Over", Util.getImageIcon("StepOver.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                // TODO oscar is working on this
                ComponentRegistry.getProjectBar().stepOverDebug();
            }
        };
        // TODO remove this when implemented
        stepOverAction.setEnabled(false);
        stepOverAction.putValue(Action.SHORT_DESCRIPTION, "Step Over (F10)");
        stepOverAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0,
            false));

        stepOneTickAction = new SB_AbstractAction("Step One Tick", Util.getImageIcon("StepOneTick.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                // TODO oscar is working on this
                ComponentRegistry.getProjectBar().stepOneTickDebug();
            }
        };
        // TODO remove this when implemented
        stepOneTickAction.setEnabled(false);
        stepOneTickAction.putValue(Action.SHORT_DESCRIPTION, "Step One Tick (Shift+F10)");
        stepOneTickAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F10,
            InputEvent.SHIFT_MASK, false));

        runToFinalAction = new SB_AbstractAction("Run To Final", Util.getImageIcon("RunToFinal.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                // TODO oscar is working on this
                ComponentRegistry.getProjectBar().runToFinal();
            }
        };
        // TODO remove this when implemented
        runToFinalAction.putValue(Action.SHORT_DESCRIPTION, "Run To Final (Shift+F11)");
        runToFinalAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F11,
            InputEvent.SHIFT_MASK, false));

        showCurrentAction = new SB_AbstractAction("Show Current", Util.getImageIcon("ShowCurrent.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                // TODO oscar is working on this
                ComponentRegistry.getProjectBar()._debugger.showCurrentRectangle();
            }
        };
        // TODO remove this when implemented
        showCurrentAction.setEnabled(true);
        showCurrentAction.putValue(Action.SHORT_DESCRIPTION, "Show Current");
        
       
        javaScriptSettingsAction =  new SB_AbstractAction("Javascript Settings...", null)
        {
            public void actionPerformed(ActionEvent e)
            {
            	ComponentRegistry.getProjectBar()._catalog.handleJavaScriptMenuItem();
            }
        };
       
    }
       

    /**
     * Disables appropriate toolbar items that should not be available
     * when using debugger.
     * 
     * @param _debugger
     */
    public void setDebugModeOn(){
    	_debugMode = true;
    	undoAction.setEnabled(false);
        redoAction.setEnabled(false);
        cutAction.setEnabled(false);
        deleteAction.setEnabled(false);
        pasteAction.setEnabled(false);
        
        stepOneTickAction.setEnabled(false);
       	runToFinalAction.setEnabled(false);
        
        newAction.setEnabled(false);
        openAction.setEnabled(false);
        saveAsAction.setEnabled(false);
        
    }
    
    /**
     * Reenables appropriate toolbar items upon leaving debug mode.
     *
     */
    public void setDebugModeOff(){
    	_debugMode = false;
    	newAction.setEnabled(true);
        openAction.setEnabled(true);
        saveAsAction.setEnabled(true);
    }
    
    public void requestContentFocus()
    {
        (ComponentRegistry.getContent()).getActiveCanvas().requestFocus();
    }

    public SB_Catalog getCatalog()
    {
        return (ComponentRegistry.getProjectBar())._catalog;
    }
    
    public boolean isDirty()
    {
        return isModified();
    }

    public boolean isModified()
    {
        SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        return projectBar.isPaintingForPrint() || catalog.isBTNModified();
    }

    public boolean saveIfModified()
    {
        return (ComponentRegistry.getProjectBar()).saveIfModified();
    }

    public void setActive(boolean active)
    {
        if (active)
        {
            // setContent(ComponentRegistry.getContent());
            // showOutputBar();
            // if (_splitPaneInner != null)
            // _splitPaneInner.setDividerLocation(200);
            // if (_splitPaneOuter != null)
            // _splitPaneOuter.setDividerLocation(0.8);
            ComponentRegistry.getContent().requestFocus();
        } else
        {
            ComponentRegistry.getToolBar().hideDialogs();
            // Component jc = _splitPaneOuter.getBottomComponent();
            // if (jc != null && jc.equals(_defaultOutputBar))
            // {
            // _splitPaneOuter.remove(jc);
            // }
        }
    }

   

    /*
     * (non-Javadoc)
     * 
     * @see com.stottlerhenke.simbionic.editor.gui.AbstractEditor#loadContent(java.io.File)
     */
    public boolean loadContent(File pBaseFilename)
    {
        // FileManager manager = FileManager.getInstance();
        // TODO This stuff should be moved to the file manager
        return ComponentRegistry.getProjectBar().loadProject(pBaseFilename);
    }

    /**
     * @return Returns the project bar.
     */
    public SB_ProjectBar getProjectBar()
    {
        return _projectBar;
    }

    /**
     * @param bar the current project bar for the editor
     */
    public void setProjectBar(SB_ProjectBar bar)
    {
        _projectBar = bar;
    }

    /**
     * 
     * @return the custom editor registry for the editor
     */
    public EditorRegistry getEditorRegistry() {
    	if (_editorRegistry == null)
    		_editorRegistry = new EditorRegistry();
    	return _editorRegistry;
    }
    
    /**
     * set the custom editor registry for the editor
     * @param registry
     */
    public void setEditorRegistry(EditorRegistry registry) {
    	_editorRegistry = registry;
    }

    /**
     * Sets the dirty flag for the current project.
     * @param dirty the new value for the flag
     */
    public void setDirty(boolean dirty)
    {
        saveAction.setEnabled(dirty);
    }
    
    /**
	 * add file to the list of recent opened files and updates the file 
	 * menu adding the file as the first file in the list of recent opened files
	 * @param file
	 */
	public void updateMenuMostRecentUsedFiles(File file) {
		_mostRecentUsedFiles.remove(file);
		addMostRecentUsedFile(file);
		setMostRecentFileActions();
	}
	
	/**
	 * removes the given file from the list of most recent files
	 * Nothing is done if the file was not in the list of most recent used files
	 * @param file
	 */
	public void removeFromMostRecentUsedFiles(File file) {
		if (_mostRecentUsedFiles.remove(file) ) {
			setMostRecentFileActions();
			updateCacheMostRecentUsedFiles();
		}
	}
	

	
	private void setMostRecentFileActions() {
		Action[] actions = new Action[getMostRecentUsedFiles().size()];
		for (int i = 0; i < getMostRecentUsedFiles().size(); i ++)
			actions[i] = new OpenMostRecentFileAction(i);
		
		
		for (int i = 0; i < MAX_MOST_RECENT_FILES_NUM; i ++) {
			if (i < actions.length) {
				_recentItems[i].setVisible(true);
				_recentItems[i].setAction(actions[i]);
			}
			else
				_recentItems[i].setVisible(false);
		}
		_mostRecentFilesSeparator.setVisible(actions.length > 0);
	}
	
	/**
	 * return the list of most recent maintained in the variable {@link #_mostRecentUsedFiles}.
	 * If this variable is null, then the variable is initialized with the the list stored 
	 * in the java preferences {@link Preferences}, under the "MostRecentUsedFiles" key.
	 * @return
	 */
	private ArrayList getMostRecentUsedFiles() {
		if (_mostRecentUsedFiles == null) {
			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			String str = prefs.get("MostRecentUsedFiles", "");
			_mostRecentUsedFiles = new ArrayList();
			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			while (tokenizer.hasMoreTokens())
				_mostRecentUsedFiles.add(new File(tokenizer.nextToken()));
		}
		
		return _mostRecentUsedFiles;
	}
	
	/**
	 * add file to the list of most recently used files. The GUI is not updated but 
	 * just the data model defined by {@link #mostRecentUsedFiles} and the long term
	 * stored defined by the key "MostRecentUsedFiles" in {@link Preferences}.
	 * 
	 * @param file
	 * @see {@link #getMostRecentUsedFiles}
	 */
	private void addMostRecentUsedFile(File file) {
		if (_mostRecentUsedFiles == null)
			_mostRecentUsedFiles = new ArrayList();
		
		_mostRecentUsedFiles.add(0, file);
		
		if (_mostRecentUsedFiles.size() > MAX_MOST_RECENT_FILES_NUM)
			_mostRecentUsedFiles.remove(MAX_MOST_RECENT_FILES_NUM);
		
		updateCacheMostRecentUsedFiles();
	}
	
	/**
	 * updated the preferences to store the most recent files
	 */
	private void updateCacheMostRecentUsedFiles() {		
		if (_mostRecentUsedFiles ==null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		for (Iterator it = _mostRecentUsedFiles.iterator(); it.hasNext(); )
		{
			sb.append(((File)it.next()).getAbsolutePath());
			if (it.hasNext())
				sb.append(",");
		}
		
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.put("MostRecentUsedFiles", sb.toString());
	}
	
	
}