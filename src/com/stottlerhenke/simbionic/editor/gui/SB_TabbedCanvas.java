
package com.stottlerhenke.simbionic.editor.gui;

import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_NODE;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_NODE;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_NODE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_Function;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.UserObject;
import com.stottlerhenke.simbionic.editor.Util;

public class SB_TabbedCanvas extends JTabbedPane implements ActionListener, ClipboardOwner
{

    protected SimBionicEditor _editor;
    
    //Determines which menu options are available to the user
    private boolean debugMode = false;

    // TODO Change back to protected access
    public SB_Behavior _behavior = null;

    // These UI items need to be members so they can be enabled/disabled as
    // necessary
    protected JMenuItem _insertPolyItem, _duplicatePolyItem, _rectangleCutItem, _rectangleCopyItem,
            _rectangleDeleteItem,
            _rectangleDeclarationItem, _rectangleToggleBreakpointItem,
            _conditionCutItem, _conditionCopyItem, _conditionDeleteItem,
            _conditionDeclarationItem, _conditionToggleBreakpointItem,
            _pasteItem, _deletePolyItem, _insertNewActionItem, _insertNewCompoundActionItem, _insertNewConditionItem, _initialItem, 
            _catchItem,_alwaysItem,
            _finalItem, _connectorCutItem,
            _connectorCopyItem, _connectorDeleteItem ;

    protected JPopupMenu _polyPopup, _canvasPopup, _rectanglePopup, _conditionPopup,
            _connectorPopup;

    protected JMenu _rectangleLabelSubmenu, _conditionLabelSubmenu, _prioritySubmenu;

    protected JRadioButtonMenuItem _rectangleLabelFull, _rectangleLabelTruncated,
            _rectangleLabelComment, _conditionLabelFull, _conditionLabelTruncated,
            _conditionLabelComment, _connectorLabelFull, _connectorLabelTruncated,
            _connectorLabelComment;

    protected JCheckBoxMenuItem _interruptItem;

    // poly popup actions
    protected Action _insertPolyAction, _deletePolyAction, _duplicatePolyAction;

    // shared popup actions - should these be connected to SimBionic cut, copy,
    // paste actions?
    protected Action _canvasCutAction, _canvasCopyAction, _canvasPasteAction, _canvasDeleteAction,
            _declarationAction, _editBindingsAction, _setLabelFullAction,
            _setLabelTruncatedAction, _setLabelCommentAction;

    // canvas popup actions
    protected Action _insertNewActionAction, _insertNewConditionAction, _insertNewCompoundActionAction;

    // rectangle popup actions
    protected Action _setInitialAction, _setFinalAction, _setAlwaysAction,_setCatchAction;

    // connector popup actions
    protected Action _interruptAction;

    // other UI elements
    protected SB_FindDialog _findDialog;

    protected SB_ReplaceDialog _replaceDialog;

    protected int _downIndex = -1;

    protected boolean _dragging = false;

    protected static Clipboard _clipboard = null;
    
    // XXX: MOTL
    public static interface UpdateTitleListener {
        public void updateTitle();
    }
    
    // XXX: MOTL
    protected UpdateTitleListener updateTitleListener = null;

    private final NodeEditorPanel nodeEditor;

    public SB_TabbedCanvas(SimBionicEditor editor,
            NodeEditorPanel editorPanel) {
        super(BOTTOM);
        _editor = editor;
        ComponentRegistry.setContent(this);
        
        initializeActions();
        initializeMenus();
        
        // XXX: MOTL
        initializeChangeListener();
        initializeMouseListener();
        initializeMouseMotionListener();
        
        setMinimumSize(new Dimension(100, 50));
        _clipboard = new Clipboard("SimBionic Canvas");
        this.nodeEditor = editorPanel;
    }

    // XXX: MOTL
    /*private*/protected void initializeMenus()
    {
        _polyPopup = new JPopupMenu();
        _insertPolyItem = new JMenuItem(_insertPolyAction);
        _polyPopup.add(_insertPolyItem);
        _deletePolyItem = new JMenuItem(_deletePolyAction);
        _polyPopup.add(_deletePolyItem);
        _polyPopup.addSeparator();
        _duplicatePolyItem = new JMenuItem(_duplicatePolyAction);
        _polyPopup.add(_duplicatePolyItem);

        _canvasPopup = new JPopupMenu();
        _pasteItem = new JMenuItem(_canvasPasteAction);
        _canvasPopup.add(_pasteItem);
        _canvasPopup.addSeparator();
        _insertNewActionItem = new JMenuItem(_insertNewActionAction);
        _canvasPopup.add(_insertNewActionItem);
        _insertNewCompoundActionItem = new JMenuItem(_insertNewCompoundActionAction);
        _canvasPopup.add(_insertNewCompoundActionItem);
        _insertNewConditionItem = new JMenuItem(_insertNewConditionAction);
        _canvasPopup.add(_insertNewConditionItem);
        _rectanglePopup = new JPopupMenu();
        _rectangleCutItem = new JMenuItem(_canvasCutAction);
        _rectanglePopup.add(_rectangleCutItem);
        _rectangleCopyItem = new JMenuItem(_canvasCopyAction);
        _rectanglePopup.add(_rectangleCopyItem);
        _rectangleDeleteItem = new JMenuItem(_canvasDeleteAction);
        _rectanglePopup.add(_rectangleDeleteItem);
        _rectanglePopup.addSeparator();
        _initialItem = new JCheckBoxMenuItem(_setInitialAction);
        _rectanglePopup.add(_initialItem);
        _finalItem = new JCheckBoxMenuItem(_setFinalAction);
        _rectanglePopup.add(_finalItem);
        _alwaysItem = new JCheckBoxMenuItem(_setAlwaysAction);
        _rectanglePopup.add(_alwaysItem);
        _catchItem = new JCheckBoxMenuItem(_setCatchAction);
        _rectanglePopup.add(_catchItem);
        _rectanglePopup.addSeparator();
        _rectangleLabelSubmenu = new JMenu("Set Label");
        _rectanglePopup.add(_rectangleLabelSubmenu);
        ButtonGroup group = new ButtonGroup();
        _rectangleLabelFull = new JRadioButtonMenuItem(_setLabelFullAction);
        group.add(_rectangleLabelFull);
        _rectangleLabelSubmenu.add(_rectangleLabelFull);
        _rectangleLabelTruncated = new JRadioButtonMenuItem(_setLabelTruncatedAction);
        group.add(_rectangleLabelTruncated);
        _rectangleLabelSubmenu.add(_rectangleLabelTruncated);
        _rectangleLabelComment = new JRadioButtonMenuItem(_setLabelCommentAction);
        group.add(_rectangleLabelComment);
        _rectangleLabelSubmenu.add(_rectangleLabelComment);
        _rectanglePopup.addSeparator();
        _rectangleDeclarationItem = new JMenuItem(_declarationAction);
        _rectanglePopup.add(_rectangleDeclarationItem);
        _rectanglePopup.addSeparator();
        _rectangleToggleBreakpointItem = new JCheckBoxMenuItem(_editor.breakpointAction);
        _rectanglePopup.add(_rectangleToggleBreakpointItem);
        
        //condition popup
        _conditionPopup = new JPopupMenu();
        _conditionCutItem = new JMenuItem(_canvasCutAction);
        _conditionPopup.add(_conditionCutItem);
        _conditionCopyItem = new JMenuItem(_canvasCopyAction);
        _conditionPopup.add(_conditionCopyItem);
        _conditionDeleteItem = new JMenuItem(_canvasDeleteAction);
        _conditionPopup.add(_conditionDeleteItem);
        _conditionPopup.addSeparator();
        _conditionLabelSubmenu = new JMenu("Set Label");
        _conditionPopup.add(_conditionLabelSubmenu);
        group = new ButtonGroup();
        _conditionLabelFull = new JRadioButtonMenuItem(_setLabelFullAction);
        group.add(_conditionLabelFull);
        _conditionLabelSubmenu.add(_conditionLabelFull);
        _conditionLabelTruncated = new JRadioButtonMenuItem(_setLabelTruncatedAction);
        group.add(_conditionLabelTruncated);
        _conditionLabelSubmenu.add(_conditionLabelTruncated);
        _conditionLabelComment = new JRadioButtonMenuItem(_setLabelCommentAction);
        group.add(_conditionLabelComment);
        _conditionLabelSubmenu.add(_conditionLabelComment);
        _conditionPopup.addSeparator();
        _conditionDeclarationItem = new JMenuItem(_declarationAction);
        _conditionPopup.add(_conditionDeclarationItem);
        _conditionPopup.addSeparator();
        _conditionToggleBreakpointItem = new JCheckBoxMenuItem(_editor.breakpointAction);
        _conditionPopup.add(_conditionToggleBreakpointItem);
        
        //connector popup
        _connectorPopup = new JPopupMenu();
        _connectorCutItem = new JMenuItem(_canvasCutAction);
        _connectorPopup.add(_connectorCutItem);
        _connectorCopyItem = new JMenuItem(_canvasCopyAction);
        _connectorPopup.add(_connectorCopyItem);
        _connectorDeleteItem = new JMenuItem(_canvasDeleteAction);
        _connectorPopup.add(_connectorDeleteItem);
        _connectorPopup.addSeparator();
        _prioritySubmenu = new JMenu("Set Priority");
        _connectorPopup.add(_prioritySubmenu);
        _interruptItem = new JCheckBoxMenuItem(_interruptAction);
        _connectorPopup.add(_interruptItem);
        _connectorPopup.addSeparator();
        JMenu _connectorLabelSubmenu = new JMenu("Set Label");
        _connectorPopup.add(_connectorLabelSubmenu);
        group = new ButtonGroup();
        _connectorLabelFull = new JRadioButtonMenuItem(_setLabelFullAction);
        group.add(_connectorLabelFull);
        _connectorLabelSubmenu.add(_connectorLabelFull);
        _connectorLabelTruncated = new JRadioButtonMenuItem(_setLabelTruncatedAction);
        group.add(_connectorLabelTruncated);
        _connectorLabelSubmenu.add(_connectorLabelTruncated);
        _connectorLabelComment = new JRadioButtonMenuItem(_setLabelCommentAction);
        group.add(_connectorLabelComment);
        _connectorLabelSubmenu.add(_connectorLabelComment);
    }

    // convenience accessors
    public SB_ToolBar getToolBar()
    {
        return ComponentRegistry.getToolBar();
    }

    public SB_ProjectBar getProjectBar()
    {
        return ComponentRegistry.getProjectBar();
    }
    
    // XXX: MOTL
    public void setUpdateTitleListener(UpdateTitleListener listener) {
        this.updateTitleListener = listener;
    }
    
    public boolean setBehavior(SB_Behavior behavior, boolean addToStack)
    {
        if (_behavior == behavior)
            return false;
        storeLastValues();
        SB_Polymorphism poly;
        SB_Canvas canvas;
        JViewport viewport;
        JScrollPane scrollCanvas;
        int polyCount = behavior.getPolyCount();
        int tabCount = getTabCount();
        for (int i = 0; i < polyCount; ++i)
        {
            poly = behavior.getPoly(i);
            if (i < tabCount)
            {
                scrollCanvas = (JScrollPane) getComponentAt(i);
                viewport = scrollCanvas.getViewport();
                canvas = (SB_Canvas) viewport.getView();
                setTitleAt(i, poly.getIndicesLabel());
            } else
            {
                canvas = new SB_Canvas(_editor, nodeEditor);
                scrollCanvas = new JScrollPane(canvas);
                scrollCanvas.getHorizontalScrollBar().setUnitIncrement(10);
                scrollCanvas.getVerticalScrollBar().setUnitIncrement(10);
                addTab(poly.getIndicesLabel(), scrollCanvas);
                canvas._needToScroll = true; // note: must call
                // scrollRectToVisible() later...
            }
            setNewPolyForCanvas(canvas, poly);
            canvas.scrollRectToVisible(canvas._poly._lastViewRect); // ...because
            // here it
            // works
            // only for
            // previous
            // tabs
            scrollCanvas.setAutoscrolls(true);
        }
        for (int i = tabCount - 1; i >= polyCount; --i)
            removeTabAt(i);
        setSelectedIndex(behavior.getLastIndex());
        updateCanvas();
        SB_ToolBar toolbar = getToolBar();
        Vector backStack = toolbar._backStack;
        Vector forwardStack = toolbar._forwardStack;
        if (addToStack && _behavior != null)
        {
            forwardStack.removeAllElements();
            toolbar.addToBackStack(_behavior);
        }
        toolbar._backAction.setEnabled(!backStack.isEmpty());
        toolbar._forwardAction.setEnabled(!forwardStack.isEmpty());
        _behavior = behavior;
        updateOtherComponentsOnBehaviorChange();
        return true;
    }

    protected void updateOtherComponentsOnBehaviorChange(){
        if (ComponentRegistry.isStandAlone()){
            ((SimBionicFrame)ComponentRegistry.getFrame())
                .updateForBehaviorChange();
            return;
        }
        // XXX: MOTL
        if (updateTitleListener != null) {
            updateTitleListener.updateTitle();
        }
        // FIXME this is a hack so it'll work with flexitrainer
//        String s=ComponentRegistry.getFrame().getTitle();
//        if (_behavior==null) return;
//        
//        s=s.replaceAll("\\[[^\\]]*\\]", "["+_behavior.getName()+"]");
//        ComponentRegistry.getFrame().setTitle(s);        
    }
    
    protected void storeLastValues()
    {
        if (_behavior == null)
            return;
        _behavior.setLastIndex(getSelectedIndex());
        JScrollPane scrollCanvas;
        JViewport viewport;
        SB_Canvas canvas;
        int size = getTabCount();
        for (int i = 0; i < size; ++i)
        {
            scrollCanvas = (JScrollPane) getComponentAt(i);
            viewport = scrollCanvas.getViewport();
            canvas = (SB_Canvas) viewport.getView();
            canvas._poly._lastViewRect.setBounds(viewport.getViewRect());
        }
    }

    protected void updateCanvas()
    {
        SB_Canvas canvas = getActiveCanvas();
        if (canvas._poly != null)
        {
            canvas.clearSingle();
            canvas.updateSingle();
            canvas._poly.getElements().updateComplex(_editor);
            canvas.repaint();
            if (!getProjectBar()._catalog.isEditing())
                canvas.requestFocus();
        }
        getProjectBar()._descriptors.updatePolyIndices(canvas._poly);
        SB_LocalsTree localsTree = ComponentRegistry.getLocalsTree();
        if (localsTree != null && canvas._poly != null)
            localsTree.setRoot(canvas._poly.getLocals());
        /*
         * if (SimBionic._undoAction != null && canvas._poly != null) {
         * SimBionic._undoAction.setEnabled(canvas._poly.canUndo());
         * SimBionic._redoAction.setEnabled(canvas._poly.canRedo()); }
         */
    }

    public boolean setBehavior(String expr, boolean addToStack)
    {
        int index = expr.indexOf('(');
        if (index == -1)
            index = expr.length();
        String name = expr.substring(0, index);
        SB_Behavior behavior = getProjectBar()._catalog.findBehavior(name);
        if (behavior != null)
            return setBehavior(behavior, addToStack);
        else
            return false;
    }

    public SB_Canvas getActiveCanvas()
    {
        if (getSelectedComponent() == null)
            return null;
        return (SB_Canvas) ((JScrollPane) getSelectedComponent()).getViewport().getView();
    }

    // XXX: MOTL
    /*private*/protected void initializeActions()
    {
        // poly popup actions
        _insertPolyAction = new AbstractAction("Insert Polymorphism", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                insertPoly();
            }
        };
        _deletePolyAction = new AbstractAction("Delete Polymorphism", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                deletePoly();
            }
        };
        _duplicatePolyAction = new AbstractAction("Duplicate Polymorphism", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                duplicatePoly();
            }
        };
        // shared popup actions - should these be connected to SimBionic cut,
        // copy, paste actions?
        _canvasCutAction = new AbstractAction("Cut", Util.getImageIcon("Cut16.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                canvasCut();
            }
        };
        _canvasCopyAction = new AbstractAction("Copy", Util.getImageIcon("Copy16.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                canvasCopy();
            }
        };
        _canvasPasteAction = new AbstractAction("Paste", Util.getImageIcon("Paste16.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                canvasPaste(e);
            }
        };
        _canvasDeleteAction = new AbstractAction("Delete", Util.getImageIcon("Delete16.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                canvasDelete();
            }
        };
        _declarationAction = new AbstractAction("Go To Declaration", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                goToDeclaration();
            }
        };
        _setLabelFullAction = new AbstractAction("Full", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                setLabelFull();
            }
        };
        _setLabelTruncatedAction = new AbstractAction("Truncated", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                setLabelTruncated();
            }
        };
        _setLabelCommentAction = new AbstractAction("Comment", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                setLabelComment();
            }
        };

        // canvas popup actions
        _insertNewActionAction = new AbstractAction("Insert Action", Util
                .getImageIcon("Action.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                insertNewAction();
            }
        };
        
        _insertNewCompoundActionAction = new AbstractAction("Insert Compound Action", Util
                .getImageIcon("Action.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                insertNewCompoundAction();
            }
        };
        
        _insertNewConditionAction = new AbstractAction("Insert Condition", Util
                .getImageIcon("Predicate.gif"))
        {
            public void actionPerformed(ActionEvent e)
            {
                insertNewCondition();
            }
        };
        _setInitialAction = new AbstractAction("Initial", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                Object s = e.getSource();
                if (s instanceof AbstractButton)
                {
                    setInitial(((AbstractButton) s).isSelected());
                } else
                {
                    System.err.println("Error occurred in SB_TabbedCanvas:"
                            + " unexpected source for Initial action.");
                }
            }
        };
        _setFinalAction = new AbstractAction("Final", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                Object s = e.getSource();
                if (s instanceof AbstractButton)
                {
                    setFinal(((AbstractButton) s).isSelected());
                } else
                {
                    System.err.println("Error occurred in SB_TabbedCanvas:"
                            + " unexpected source for Initial action.");
                }
            }
        };
        
        _setCatchAction = new AbstractAction("Catch") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object s = e.getSource();
                if (s instanceof AbstractButton)
                {
                    setCatch(((AbstractButton) s).isSelected());
                } else
                {
                    System.err.println("Error occurred in SB_TabbedCanvas:"
                            + " unexpected source for Catch action.");
                }
			}
		};
		
		 _setAlwaysAction = new AbstractAction("Always") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Object s = e.getSource();
	                if (s instanceof AbstractButton)
	                {
	                    setAlways(((AbstractButton) s).isSelected());
	                } else
	                {
	                    System.err.println("Error occurred in SB_TabbedCanvas:"
	                            + " unexpected source for Catch action.");
	                }
				}
			};
        _interruptAction = new AbstractAction("Interrupt", null)
        {
            public void actionPerformed(ActionEvent e)
            {
                Object s = e.getSource();
                if (s instanceof AbstractButton)
                {
                    setInterrupt(((AbstractButton) s).isSelected());
                } else
                {
                    System.err.println("Error occurred in SB_TabbedCanvas: "
                            + "unexpected source for Initial action.");
                }
            }
        };
    }
    
    // XXX: MOTL
    protected void initializeChangeListener() {
        addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent event)
            {
                updateCanvas();
            }
        });
    }
    
    // XXX: MOTL
    protected void initializeMouseListener() {
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                {
                    SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) e.getSource();
                    tabbedCanvas._downIndex = tabbedCanvas.indexAtLocation(e.getX(), e.getY());
                }
            }

            public void mouseReleased(MouseEvent e)
            {
                SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) e.getSource();
                if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0)
                {
                    tabbedCanvas._deletePolyItem.setEnabled(tabbedCanvas.getTabCount() > 1);
                    tabbedCanvas._polyPopup.show(tabbedCanvas, e.getX(), e.getY());
                }
                if (getActiveCanvas() != null)
                    getActiveCanvas().requestFocus();
                if (tabbedCanvas != null && tabbedCanvas._dragging)
                {
                    tabbedCanvas._dragging = false;
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    int upIndex = tabbedCanvas.indexAtLocation(e.getX(), e.getY());
                    int downIndex = tabbedCanvas._downIndex;
                    // FIXME This can throw an index out of range exception
                    // in swapPoly and tabbedCanvas.getComponentAt(downIndex)
                    // KP added "_behavior.getPolyCount()>1" to avoid the exceptions,
                    // but not sure if that is correct (6/17/05)
                    if (upIndex != downIndex && _behavior.getPolyCount()>1)
                    {
                        _behavior.swapPoly(upIndex, downIndex);
                        _behavior.setBTNModified(true);
                        ComponentRegistry.getProjectBar().setProjectModified(true);
                        JScrollPane scrollCanvas = (JScrollPane) tabbedCanvas
                                .getComponentAt(upIndex);
                        JViewport viewport = scrollCanvas.getViewport();
                        SB_Canvas canvas = (SB_Canvas) viewport.getView();
                        setNewPolyForCanvas(canvas,
                                _behavior.getPoly(upIndex));
                        scrollCanvas = (JScrollPane) tabbedCanvas.getComponentAt(downIndex);
                        viewport = scrollCanvas.getViewport();
                        canvas = (SB_Canvas) viewport.getView();
                        setNewPolyForCanvas(canvas,
                                _behavior.getPoly(downIndex));
                        String title = tabbedCanvas.getTitleAt(upIndex);
                        tabbedCanvas.setTitleAt(upIndex, tabbedCanvas.getTitleAt(downIndex));
                        tabbedCanvas.setTitleAt(downIndex, title);
                        tabbedCanvas.setSelectedIndex(upIndex);
                    }
                }
                tabbedCanvas._downIndex = -1;
            }
        });
    }
    
    // XXX: MOTL
    protected void initializeMouseMotionListener() {
        addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent e)
            {
                SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) e.getSource();
                if (!tabbedCanvas._dragging && tabbedCanvas._downIndex != -1)
                {
                    tabbedCanvas._dragging = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
        });
    }

    public void canvasCut()
    {
        SB_Canvas canvas = getActiveCanvas();
        copySelection();
        if (canvas != null)
        {
            canvas.deleteSelection();
            canvas.updateEditItems();
        }
    }

    public void canvasCopy()
    {
        copySelection();
        SB_Canvas canvas = getActiveCanvas();
        if (canvas != null)
            canvas.updateEditItems();
    }

    public void canvasPaste(ActionEvent ae)
    {
        Object source = ae.getSource();
        SB_Canvas canvas = getActiveCanvas();
        if (canvas == null)
            return;
        Transferable clipboardContent = _clipboard.getContents(this);
        if (clipboardContent != null
                && clipboardContent.isDataFlavorSupported(SB_CanvasSelection.getCanvasFlavor()))
        {
            canvas._poly.addToUndoStack();
            SB_Rectangle initial = canvas._poly.getElements().getInitial();
            try
            {
                ByteArrayOutputStream out = (ByteArrayOutputStream) clipboardContent
                        .getTransferData(SB_CanvasSelection.getCanvasFlavor());
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                ObjectInputStream s = new ObjectInputStream(in);
                canvas._poly.read(s, true);
                canvas._poly.setModified(true);
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
            if (initial != null) {
                if (canvas._poly.getElements().setInitial(initial)) {
                   Poly polyModel = canvas._poly.getDataModel();
                   polyModel.getNodes().setInitial(initial.getId());
                }
            }
            int delta_x = 10;
            int delta_y = 10;
            if (source == _pasteItem)
            {
                Rectangle ur = new Rectangle();
                ur = canvas._poly.unionRect(ur, true);
                delta_x = canvas._downPoint.x - ur.x;
                delta_y = canvas._downPoint.y - ur.y;
            }
            canvas._poly.getElements().offset(delta_x, delta_y, true);
            canvas._poly.getConnectors().offset(delta_x, delta_y, true);
            canvas._poly.getElements().updateComplex(_editor);
            canvas._poly.getConnectors().updateTwoWay();
            canvas.repaint();
        }
        canvas.updateEditItems();
    }

    public void canvasDelete()
    {
        SB_Canvas canvas = getActiveCanvas();
        if (canvas == null)
            return;
        canvas.deleteSelection();
        canvas.updateEditItems();
    }

    private void goToDeclaration()
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Element element = (SB_Element) canvas._selDrawable;
        SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
        projectBar._catalog.showFuncNode(SB_Catalog.extractFuncName(element.getExpr()));
    }

    private void setLabelTruncated()
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_CommentHolder holder = (SB_CommentHolder) canvas._selDrawable;
        if (holder.getLabelMode() != SB_CommentHolder.TRUNCATED_LABEL)
        {
            canvas._poly.addToUndoStack();
            holder.setLabelMode(SB_CommentHolder.TRUNCATED_LABEL);
            holder.updateComment();
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private void setLabelFull()
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_CommentHolder holder = (SB_CommentHolder) canvas._selDrawable;
        if (holder.getLabelMode() != SB_CommentHolder.FULL_LABEL)
        {
            canvas._poly.addToUndoStack();
            holder.setLabelMode(SB_CommentHolder.FULL_LABEL);
            holder.updateComment();
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private void setLabelComment()
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_CommentHolder holder = (SB_CommentHolder) canvas._selDrawable;
        if (holder.getLabelMode() != SB_CommentHolder.COMMENT_LABEL)
        {
            canvas._poly.addToUndoStack();
            holder.setLabelMode(SB_CommentHolder.COMMENT_LABEL);
            holder.updateComment();
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private void insertNewAction()
    {
        SB_Canvas canvas = getActiveCanvas();
        Point point = new Point(canvas._downPoint.x + 21, canvas._downPoint.y + 14);
        canvas.insertAction(point, "");
    }

    private void insertNewCompoundAction()
    {
    	SB_Canvas canvas = getActiveCanvas();
        Point point = new Point(canvas._downPoint.x + 21, canvas._downPoint.y + 14);
        canvas.insertCompoundAction(point);
    }
    
    private void insertNewCondition()
    {
        SB_Canvas canvas = getActiveCanvas();
        Point point = new Point(canvas._downPoint.x + 21, canvas._downPoint.y + 14);
        canvas.insertCondition(point, "");
    }

    private void setInitial(boolean isSelected)
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Rectangle rectangle = (SB_Rectangle) canvas._selDrawable;
        if (isSelected && !rectangle.isInitial())
        {
            canvas._poly.addToUndoStack();
            if (canvas._poly._elements.setInitial(rectangle)) {
               Poly polyModel = canvas._poly.getDataModel();
               polyModel.getNodes().setInitial(rectangle.getId());
            }
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private void setFinal(boolean isSelected)
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Rectangle rectangle = (SB_Rectangle) canvas._selDrawable;
        if (rectangle.isFinal() != isSelected)
        {
            canvas._poly.addToUndoStack();
            rectangle.setFinal(isSelected);
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private void setInterrupt(boolean isSelected)
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Connector connector = (SB_Connector) canvas._selDrawable;
        if (connector.isInterrupt() != isSelected)
        {
            canvas._poly.addToUndoStack();
            connector.setInterrupt(isSelected);
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    public void undo()
    {
        SB_Canvas canvas = getActiveCanvas();
        canvas._poly.undo(canvas);
    }

    public void redo()
    {
        SB_Canvas canvas = getActiveCanvas();
        canvas._poly.redo(canvas);
    }

    public void selectAll()
    {
        SB_Canvas canvas = getActiveCanvas();
        canvas.selectAll();
    }

    private void duplicatePoly()
    {
        SB_Canvas canvas = getActiveCanvas();
        Poly duplicatePoly = new Poly(canvas._poly.getDataModel());
        SB_Polymorphism poly = new SB_Polymorphism(_behavior, duplicatePoly);
        _behavior.addPoly(poly);
        insertPoly(poly);
    }

    private void deletePoly()
    {
        SB_Polymorphism poly = _behavior.getPoly(getSelectedIndex());
        _behavior.removePoly(poly);
        remove(getSelectedComponent());
        _behavior.setBTNModified(true);
    }

    private void insertPoly()
    {
        Poly polyModel = new Poly();
        SB_Polymorphism poly = new SB_Polymorphism(_behavior, polyModel);
        poly.setIndices(getProjectBar()._descriptors.getBasePolyIndices());
        _behavior.addPoly(poly);
        insertPoly(poly);
    }

    // TODO instead of using getSource, use Actions
    public void actionPerformed(ActionEvent e)
    {
        boolean handled = true;
        String command = e.getActionCommand();
        if (command == null)
            return;

        // these commands are called from the SimBionic actions
        if (command.equals(SimBionicEditor.UNDO_COMMAND))
        {
            undo();
        } else if (command.equals(SimBionicEditor.REDO_COMMAND))
        {
            redo();
        } else if (command.equals(SimBionicEditor.CUT_COMMAND))
        {
            canvasCut();
        } else if (command.equals(SimBionicEditor.COPY_COMMAND))
        {
            canvasCopy();

        } else if (command.equals(SimBionicEditor.PASTE_COMMAND))
        {
            canvasPaste(e);
        } else if (command.equals(SimBionicEditor.DELETE_COMMAND))
        {
            canvasDelete();
        } else if (command.equals(SimBionicEditor.SELECTALL_COMMAND))
        {
            selectAll();
        } else if (command.equals(SimBionicEditor.FIND_COMMAND))
        {
            showFindDialog();
        } else if (command.equals(SimBionicEditor.REPLACE_COMMAND))
        {
            showReplaceDialog();
        } else
        {
            System.err.println("Error: unrecognized action command in "
                    + "SB_TabbedCanvas.actionPerformed: " + command);
        }

    }

    protected void insertPoly(SB_Polymorphism poly)
    {
        SB_Canvas canvas = new SB_Canvas(_editor, nodeEditor);
        JScrollPane scrollCanvas = new JScrollPane(canvas);
        scrollCanvas.getHorizontalScrollBar().setUnitIncrement(10);
        scrollCanvas.getVerticalScrollBar().setUnitIncrement(10);
        addTab(poly.getIndicesLabel(), scrollCanvas);
        setNewPolyForCanvas(canvas, poly);
        scrollCanvas.setAutoscrolls(true);
        setSelectedComponent(scrollCanvas);
        _behavior.setBTNModified(true);
    }

    protected void copySelection()
    {
        Transferable clipboardContent = _clipboard.getContents(this);
        if (clipboardContent != null)
            ((SB_CanvasSelection) clipboardContent).reset();
        SB_CanvasSelection contents = null;
        if (getActiveCanvas() != null)
            contents = new SB_CanvasSelection(getActiveCanvas()._poly, true);
        if (contents != null)
            _clipboard.setContents(contents, this);
        _editor.pasteAction.setEnabled(true);
    }

    public void lostOwnership(Clipboard clipboard, Transferable transferable)
    {
        System.out.println("lost ownership");
    }

    protected void handleCanvasPopup(SB_Canvas canvas)
    {
        boolean core = canvas._poly.getParent().isCore();
       	_pasteItem.setEnabled(_editor.pasteAction.isEnabled()&&!debugMode);
       	
       	_insertNewActionItem.setEnabled(!core&&!debugMode);
       	_insertNewCompoundActionItem.setEnabled(!core&&!debugMode);
       	_insertNewConditionItem.setEnabled(!core&&!debugMode);
        _canvasPopup.show(canvas, canvas._point.x, canvas._point.y);
    }

    protected void handleElementPopup(SB_Canvas canvas, SB_Element element)
    {
        boolean core = canvas._poly.getParent().isCore() || canvas._selDrawable == null;
        if (element instanceof SB_Rectangle)
        {
            SB_Rectangle rectangle = (SB_Rectangle) element;
            _rectangleCutItem.setEnabled(!debugMode);
            if (_rectangleCutItem.isEnabled()) {
            	_rectangleCutItem.setText(CUT_NODE);
            } else {
            	_rectangleCutItem.setText(CUT_COMMAND);
            }
            _rectangleDeleteItem.setEnabled(!debugMode);
            if (_rectangleDeleteItem.isEnabled()) {
            	_rectangleDeleteItem.setText(DELETE_NODE);
            } else {
            	_rectangleDeleteItem.setText(DELETE_COMMAND);
            }
            _rectangleCopyItem.setEnabled(!debugMode);
            if (_rectangleCopyItem.isEnabled()) {
            	_rectangleCopyItem.setText(COPY_NODE);
            } else {
            	_rectangleCopyItem.setText(COPY_COMMAND);
            }
            _initialItem.setSelected(rectangle.isInitial());
            _finalItem.setSelected(rectangle.isFinal());
            
            _catchItem.setSelected(rectangle.isCatch());
            _alwaysItem.setSelected(rectangle.isAlways());
            
            _initialItem.setEnabled(!core && !rectangle.isInitial() && !rectangle.isFinal() && !debugMode);
            _finalItem.setEnabled(!core && !rectangle.isInitial() && !debugMode);
            _catchItem.setEnabled(!core && (rectangle.isCatch() || !rectangle.isSpecial()) && !debugMode);
            _alwaysItem.setEnabled(!core && (rectangle.isAlways() || !rectangle.isSpecial()) && !debugMode);
            boolean hasDeclaration = false;
            if (canvas._selDrawable != null)
            {
                String expr = rectangle.getExpr();
                if (expr.length() > 0)
                {
                    if (rectangle.isBehavior())
                        hasDeclaration = true;
                    else
                    {
                        String name = SB_Catalog.extractFuncName(expr);
                        SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
                        hasDeclaration = projectBar._catalog.findAction(name) != null;
                    }
                }
            }
            int labelMode = rectangle.getLabelMode();
            if (labelMode == SB_CommentHolder.FULL_LABEL)
                _rectangleLabelFull.setSelected(true);
            else if (labelMode == SB_CommentHolder.TRUNCATED_LABEL)
                _rectangleLabelTruncated.setSelected(true);
            else
                _rectangleLabelComment.setSelected(true);
            _rectangleDeclarationItem.setEnabled(hasDeclaration && !debugMode);
            _rectanglePopup.show(canvas, canvas._point.x, canvas._point.y);
        } else
        {
            SB_Condition condition = (SB_Condition) element;
            _conditionCutItem.setEnabled(!debugMode);
            if (_conditionCutItem.isEnabled()) {
            	_conditionCutItem.setText(CUT_NODE);
            } else {
            	_conditionCutItem.setText(CUT_COMMAND);
            }
            _conditionDeleteItem.setEnabled(!debugMode);
            if (_conditionDeleteItem.isEnabled()) {
            	_conditionDeleteItem.setText(DELETE_NODE);
            } else {
            	_conditionDeleteItem.setText(DELETE_COMMAND);
            }
            _conditionCopyItem.setEnabled(!debugMode);
            if (_conditionCopyItem.isEnabled()) {
            	_conditionCopyItem.setText(COPY_NODE);
            } else {
            	_conditionCopyItem.setText(COPY_COMMAND);
            }
            
            boolean hasDeclaration = false;
            if (canvas._selDrawable != null)
            {
                String expr = condition.getExpr();
                if (expr.length() > 0)
                {
                    String name = SB_Catalog.extractFuncName(expr);
                    SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
                    hasDeclaration = projectBar._catalog.findPredicate(name) != null;
                }
            }
            int labelMode = condition.getLabelMode();
            if (labelMode == SB_CommentHolder.FULL_LABEL)
                _conditionLabelFull.setSelected(true);
            else if (labelMode == SB_CommentHolder.TRUNCATED_LABEL)
                _conditionLabelTruncated.setSelected(true);
            else
                _conditionLabelComment.setSelected(true);
            _conditionDeclarationItem.setEnabled(hasDeclaration && !debugMode);
            _conditionPopup.show(canvas, canvas._point.x, canvas._point.y);
        }
    }

    protected void handleConnectorPopup(SB_Canvas canvas, SB_Connector connector)
    {
        boolean core = canvas._poly.getParent().isCore() || canvas._selDrawable == null;
        SB_Element element = connector.getStartElement();
        _prioritySubmenu.removeAll();
        if (element != null)
        {
            ButtonGroup group = new ButtonGroup();
            JRadioButtonMenuItem rbMenuItem;
            int n, k;
            n = element.numPriorities();
            k = connector.getPriority();
            for (int i = 1; i <= n; ++i)
            {
                final int newPriority = i;
                Action priorityChange = new AbstractAction("" + i, null)
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        changeConnectorPriority(newPriority);
                    }
                };
                rbMenuItem = new JRadioButtonMenuItem(priorityChange);
                if (i == k)
                    rbMenuItem.setSelected(true);
                rbMenuItem.setEnabled(!core);
                // rbMenuItem.addActionListener(this);
                group.add(rbMenuItem);
                _prioritySubmenu.add(rbMenuItem);
            }
            _prioritySubmenu.setEnabled(true);
        } else
            _prioritySubmenu.setEnabled(false);
        if(debugMode){_prioritySubmenu.setEnabled(false);}
        _interruptItem.setSelected(connector.isInterrupt());
        _interruptItem.setEnabled(!core && !debugMode);
        _connectorCutItem.setEnabled(!debugMode);
        if (_connectorCutItem.isEnabled()) {
        	_connectorCutItem.setText(CUT_LINK);
        } else {
        	_connectorCutItem.setText(CUT_COMMAND);
        }
        _connectorDeleteItem.setEnabled(!debugMode);
        if (_connectorDeleteItem.isEnabled()) {
        	_connectorDeleteItem.setText(DELETE_LINK);
        } else {
        	_connectorDeleteItem.setText(DELETE_COMMAND);
        }
        _connectorCopyItem.setEnabled(!debugMode);
        if (_connectorCopyItem.isEnabled()) {
        	_connectorCopyItem.setText(COPY_LINK);
        } else {
        	_connectorCopyItem.setText(COPY_COMMAND);
        }
        int labelMode = connector.getLabelMode();
        if (labelMode == SB_CommentHolder.FULL_LABEL)
            _connectorLabelFull.setSelected(true);
        else if (labelMode == SB_CommentHolder.TRUNCATED_LABEL)
            _connectorLabelTruncated.setSelected(true);
        else
            _connectorLabelComment.setSelected(true);
        _connectorPopup.show(canvas, canvas._point.x, canvas._point.y);
    }

    private void changeConnectorPriority(int newPriority)
    {
        SB_Canvas canvas = getActiveCanvas();

        SB_Connector connector = (SB_Connector) canvas._selDrawable;
        if (connector.getPriority() != newPriority)
        {
            canvas._poly.addToUndoStack();
            connector.getStartElement().reprioritize(connector, newPriority);
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    private boolean _findLastUsed = false;

    public void showFindDialog(Window window)
    {
        if (_findDialog == null)
            _findDialog = new SB_FindDialog();
        _findDialog.initFind(window);
        _findDialog.setVisible(true);
    }

    public void showFindDialog()
    {
        showFindDialog(ComponentRegistry.getFrame());
    }

    private class SB_FindDialog extends JDialog
    {

        private JComboBox _findComboBox;

        private JCheckBox _matchWholeWordCheckBox;

        private JCheckBox _matchCaseCheckBox;

        public SB_FindDialog()
        {
            super(ComponentRegistry.getFrame(), "Find", true);
            JPanel editPanel = new JPanel();
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Find what:");
            editPanel.add(label);
            editPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            _findComboBox = new JComboBox();
            _findComboBox.setEditable(true);
            _findComboBox.setPreferredSize(new Dimension(275, 23));
            _findComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            JTextField textField = (JTextField) _findComboBox.getEditor().getEditorComponent();
            textField.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent e)
                {
                    findOccurrences();
                }
            });
            editPanel.add(_findComboBox);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            JPanel matchPanel = new JPanel();
            matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.X_AXIS));
            matchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            _matchWholeWordCheckBox = new JCheckBox("Match whole word ");
            _matchWholeWordCheckBox.setFocusPainted(false);
            matchPanel.add(_matchWholeWordCheckBox);
            _matchCaseCheckBox = new JCheckBox("Match case");
            _matchCaseCheckBox.setFocusPainted(false);
            matchPanel.add(_matchCaseCheckBox);
            editPanel.add(matchPanel);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.add(Box.createHorizontalGlue());
            JButton findButton = new JButton("Find");
            findButton.setFocusPainted(false);
            findButton.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent event)
                {
                    findOccurrences();
                }
            });
            buttonPanel.add(findButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent event)
                {
                    setVisible(false);
                }
            });
            buttonPanel.add(cancelButton);
            getContentPane().add(editPanel, BorderLayout.NORTH);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            pack();
            Dimension dialogSize = getSize();
            Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
            setLocation(frameBounds.x + (frameBounds.width - dialogSize.width) / 2, frameBounds.y
                    + (frameBounds.height - dialogSize.height) / 2);
        }

        public String getFindText()
        {
            return (String) _findComboBox.getItemAt(0);
        }

        public boolean getMatchWholeWord()
        {
            return _matchWholeWordCheckBox.isSelected();
        }

        public boolean getMatchCase()
        {
            return _matchCaseCheckBox.isSelected();
        }

        public void initFind(Window window)
        {
            String text = null;
            Component component = window.getFocusOwner();
            if (component instanceof EditorTree)
            {
                EditorTree tree = (EditorTree) component;
                TreePath treePath = tree.getSelectionPath();
                if (treePath != null)
                {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath
                            .getLastPathComponent();
                    if (treeNode.getUserObject() instanceof SB_Function
                            || treeNode.getUserObject() instanceof SB_Variable)
                    {
                        UserObject userObject = (UserObject) treeNode.getUserObject();
                        text = userObject.getName();
                    }
                }
            } else if (component instanceof SB_Canvas)
            {
                SB_Canvas canvas = (SB_Canvas) component;
                if (canvas._selDrawable instanceof SB_Element)
                {
                    SB_Element element = (SB_Element) canvas._selDrawable;
                    text = SB_Catalog.extractFuncName(element.getExpr());
                    if (text.length() == 0)
                        text = null;
                }
            } else if (!_findLastUsed && _replaceDialog != null)
            {
                text = _replaceDialog.getFindText();
            }
            if (text != null)
                _findComboBox.setSelectedItem(text);
            else if (_findComboBox.getModel().getSize() > 0)
                _findComboBox.setSelectedIndex(0);
            else
                _findComboBox.setSelectedItem(null);
            if (!_findLastUsed && _replaceDialog != null)
            {
                _matchWholeWordCheckBox.setSelected(_replaceDialog.getMatchWholeWord());
                _matchCaseCheckBox.setSelected(_replaceDialog.getMatchCase());
            }
            JTextField textField = (JTextField) _findComboBox.getEditor().getEditorComponent();
            textField.selectAll();
            textField.requestFocus();
        }

        private void findOccurrences()
        {
            JTextField textField = (JTextField) _findComboBox.getEditor().getEditorComponent();
            String text = textField.getText();
            if (text.length() == 0)
            {
                JOptionPane.showMessageDialog(this, "Please specify text to find.   ", "No Text",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            setVisible(false);
            _findLastUsed = true;
            _findComboBox.removeItem(text);
            _findComboBox.insertItemAt(text, 0);
            boolean matchWholeWord = _matchWholeWordCheckBox.isSelected();
            boolean matchCase = _matchCaseCheckBox.isSelected();
            String strFind = text;
            if (matchWholeWord)
                strFind = "\\b" + text + "\\b";
            int flags = 0;
            if (!matchCase)
                flags = Pattern.CASE_INSENSITIVE;

            strFind = strFind.replaceAll("\\(", "\\\\(");
            strFind = strFind.replaceAll("\\)", "\\\\)");
            strFind = strFind.replaceAll("\\[", "\\\\[");
            strFind = strFind.replaceAll("\\]", "\\\\]");
            strFind = strFind.replaceAll("\\{", "\\\\{");
            strFind = strFind.replaceAll("\\}", "\\\\}");

            Pattern pattern = Pattern.compile(strFind, flags);
            String strReplace = null;
            SB_OutputBar outputBar = SB_OutputBar.getInstance();
            SB_Output find = SB_OutputBar._find;
            find.clearLines();
            find.addLine(new SB_Line("Searching for '" + text + "'..."));
            // make sure output bar is visible with find tab selected
            // _editor.showOutputBar();
            (SB_OutputBar.getInstance()).setSelectedIndex(SB_OutputBar.FIND_INDEX);
            SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();

            int total = 0;
            try
            {
                total = projectBar._catalog.findOccurrences(pattern, strReplace);
            } catch (SB_CancelException e)
            {
                System.err.println("cancel exception");
                return;
            }
            String str = total + " occurrence";
            if (total == 0 || total > 1)
                str += "s have been found.";
            else
                str += " has been found.";
            find.addLine(new SB_Line(str));
            outputBar.requestFocus();
        }
    }

    public void showReplaceDialog(String strFind, String strReplace)
    {
        if (_replaceDialog == null)
            _replaceDialog = new SB_ReplaceDialog();
        _replaceDialog.initReplace(strFind, strReplace);
        _replaceDialog.setVisible(true);
    }

    // @kp added for jdk1.3 compliance (copied from java 1.4 source code)
    // REMOVE if you are using jdk1.4+
    /*
     * public int indexAtLocation(int x, int y) { if (ui != null) { return
     * ((TabbedPaneUI)ui).tabForCoordinate(this, x, y); } return -1; }
     */
    public void showReplaceDialog()
    {
        showReplaceDialog(null, null);
    }

    private class SB_ReplaceDialog extends JDialog
    {

        private JTextField _findTextField;

        private JTextField _replaceTextField;

        private JCheckBox _matchWholeWordCheckBox;

        private JCheckBox _matchCaseCheckBox;

        private Enumeration _enumeration;

        private String _typeName;

        private boolean _matchWholeWord;

        private boolean _matchCase;

        public SB_ReplaceDialog()
        {
            super(ComponentRegistry.getFrame(), "Replace", true);
            JPanel editPanel = new JPanel();
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.add(new JLabel("Find what:"));
            editPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            _findTextField = new JTextField();
            _findTextField.setPreferredSize(new Dimension(275, 23));
            _findTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            _findTextField.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent e)
                {
                    _replaceTextField.requestFocus();
                }
            });
            editPanel.add(_findTextField);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            editPanel.add(new JLabel("Replace with:"));
            editPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            _replaceTextField = new JTextField();
            _replaceTextField.setPreferredSize(new Dimension(275, 23));
            _replaceTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            _replaceTextField.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent e)
                {
                    replaceOccurrences();
                }
            });
            editPanel.add(_replaceTextField);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            JPanel matchPanel = new JPanel();
            matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.X_AXIS));
            matchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            _matchWholeWordCheckBox = new JCheckBox("Match whole word ");
            _matchWholeWordCheckBox.setFocusPainted(false);
            matchPanel.add(_matchWholeWordCheckBox);
            _matchCaseCheckBox = new JCheckBox("Match case");
            _matchCaseCheckBox.setFocusPainted(false);
            matchPanel.add(_matchCaseCheckBox);
            editPanel.add(matchPanel);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.add(Box.createHorizontalGlue());
            JButton findButton = new JButton("Find");
            findButton.setFocusPainted(false);
            findButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    replaceOccurrences();
                }
            });
            buttonPanel.add(findButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent event)
                {
                    setVisible(false);
                }
            });
            buttonPanel.add(cancelButton);
            getContentPane().add(editPanel, BorderLayout.NORTH);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            pack();
            Dimension dialogSize = getSize();
            Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
            setLocation(frameBounds.x + (frameBounds.width - dialogSize.width) / 2 + 50,
                frameBounds.y + (frameBounds.height - dialogSize.height) / 2);
        }

        public String getFindText()
        {
            return _findTextField.getText();
        }

        public boolean getMatchWholeWord()
        {
            return _matchWholeWordCheckBox.isSelected();
        }

        public boolean getMatchCase()
        {
            return _matchCaseCheckBox.isSelected();
        }

        public void initReplace(String strFind, String strReplace)
        {
            if (_findLastUsed)
            {
                if (strFind == null)
                {
                    _findTextField.setText(_findDialog.getFindText());
                }
                _matchWholeWordCheckBox.setSelected(_findDialog.getMatchWholeWord());
                _matchCaseCheckBox.setSelected(_findDialog.getMatchCase());
            }
            if (strFind != null)
                _findTextField.setText(strFind);
            if (strReplace != null)
                _replaceTextField.setText(strReplace);
            _findTextField.selectAll();
            _findTextField.requestFocus();
        }

        private void replaceOccurrences()
        {
            String text = _findTextField.getText();
            if (text.length() == 0)
            {
                JOptionPane.showMessageDialog(this, "Please specify text to find.   ", "No Text",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            setVisible(false);
            _findLastUsed = false;
            boolean matchWholeWord = _matchWholeWordCheckBox.isSelected();
            boolean matchCase = _matchCaseCheckBox.isSelected();
            String strFind = text;
            if (matchWholeWord)
                strFind = "\\b" + text + "\\b";
            int flags = 0;
            if (!matchCase)
                flags = Pattern.CASE_INSENSITIVE;

            strFind = strFind.replaceAll("\\(", "\\\\(");
            strFind = strFind.replaceAll("\\)", "\\\\)");
            strFind = strFind.replaceAll("\\[", "\\\\[");
            strFind = strFind.replaceAll("\\]", "\\\\]");
            strFind = strFind.replaceAll("\\{", "\\\\{");
            strFind = strFind.replaceAll("\\}", "\\\\}");

            Pattern pattern = Pattern.compile(strFind, flags);
            String strReplace = _replaceTextField.getText();
            SB_OutputBar outputBar = SB_OutputBar.getInstance();
            SB_Output find = SB_OutputBar._find;
            find.clearLines();
            find.addLine(new SB_Line("Replacing '" + text + "' by '" + strReplace + "'..."));
            // make sure output bar is visible with find tab selected
            // _editor.showOutputBar();
            (SB_OutputBar.getInstance()).setSelectedIndex(1);
            SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();
            int total = 0;
            try
            {
                total = projectBar._catalog.findOccurrences(pattern, strReplace);
            } catch (SB_CancelException e)
            {
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
    }

    /**
     * @return Returns the behavior.
     */
    public SB_Behavior getBehavior()
    {
        return _behavior;
    }
    
    protected void setDebugMode(boolean debugMode){
    	this.debugMode = debugMode;
    	this.getActiveCanvas().setDebugMode(debugMode);
    }
    
    private void setAlways(boolean isSelected)
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Rectangle rectangle = (SB_Rectangle) canvas._selDrawable;
        if (rectangle.isAlways() != isSelected)
        {
            canvas._poly.addToUndoStack();
            rectangle.setAlways(isSelected);
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }
    
    private void setCatch(boolean isSelected)
    {
        SB_Canvas canvas = getActiveCanvas();
        SB_Rectangle rectangle = (SB_Rectangle) canvas._selDrawable;
        if (rectangle.isCatch() != isSelected)
        {
            canvas._poly.addToUndoStack();
            rectangle.setCatch(isSelected);
            canvas.repaint();
            canvas._poly.setModified(true);
        }
    }

    /**
     * XXX: An attempt to pass all write accesses done to an SB_Canvas instance
     * by this class through a single method. Ideally, {@link SB_Canvas}would
     * be refactored to have {@link SB_Canvas#_poly} accessible through a
     * setter only; this is a stop-gap measure.
     * */
    private void setNewPolyForCanvas(SB_Canvas canvas,
            SB_Polymorphism newPoly) {
        SB_Polymorphism oldPoly = canvas._poly;
        canvas._poly = newPoly;
        if (oldPoly != newPoly) {
            nodeEditor.polyChanged(oldPoly, newPoly);
        }
    }


}

class SB_CanvasSelection implements Transferable
{

    static public DataFlavor _canvasFlavor = null;

    private DataFlavor[] _supportedFlavors =
    { _canvasFlavor };

    protected ByteArrayOutputStream _out;

    protected ObjectOutputStream _s;

    public SB_CanvasSelection(SB_Polymorphism poly, boolean highlightOnly)
    {
        try
        {
            _out = new ByteArrayOutputStream();
            _s = new ObjectOutputStream(_out);
            poly.write(_s, highlightOnly);
            _s.flush();
        } catch (IOException e)
        {
            System.err.println("i/o exception");
        }
    }

    static public DataFlavor getCanvasFlavor()
    {
        if (_canvasFlavor == null)
        {
            try
            {
                _canvasFlavor = new DataFlavor(Class.forName("java.io.ByteArrayOutputStream"),
                        "Canvas");
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return _canvasFlavor;
    }

    public synchronized DataFlavor[] getTransferDataFlavors()
    {
        return (_supportedFlavors);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return (flavor.equals(getCanvasFlavor()));
    }

    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
    {
        if (flavor.equals(getCanvasFlavor()))
            return (_out);
        else
            throw new UnsupportedFlavorException(_canvasFlavor);
    }

    protected void reset()
    {
        _out.reset();
    }
    
   
}
