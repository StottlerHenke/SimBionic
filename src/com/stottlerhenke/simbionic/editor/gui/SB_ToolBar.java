package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.Util;

/**
 * SB_ToolBar.java
 */
public class SB_ToolBar extends JToolBar implements ActionListener, SB_AutocompleteListener, ListSelectionListener,
	MouseListener, MouseMotionListener
{
	
	protected boolean debugMode = false;
	
    protected JToggleButton _hackConnectorButton;
    
    protected JToolBar _toolBarTop;

    protected SimBionicEditor _editor;

    protected Action _backAction;
    protected Action _forwardAction;
    protected Action _homeAction;
    // protected Action _localsAction;

    protected JDialog _localsDialog = null;
    protected boolean _lastVisible = false;

    static final int MAX_STACK_SIZE = 10;
    final Vector<SB_Behavior> _backStack = new Vector<>();
    final Vector<SB_Behavior> _forwardStack = new Vector<>();

    protected JPopupMenu _backPopup = new JPopupMenu();
    protected JMenuItem[] _backMenuItems = new JMenuItem[MAX_STACK_SIZE];
    protected JPopupMenu _forwardPopup = new JPopupMenu();
    protected JMenuItem[] _forwardMenuItems = new JMenuItem[MAX_STACK_SIZE];

    public SB_ToolBar(SimBionicEditor editor)
    {
        _editor = editor;

        ComponentRegistry.setToolbar(this);
        setFloatable(false);
        setRollover(true);

        setOrientation(JToolBar.VERTICAL);

        _toolBarTop = new JToolBar();
        _toolBarTop.setFloatable(false);
        _toolBarTop.setRollover(true);
        _toolBarTop.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        _backAction = new BackAction("Go Back", Util.getImageIcon("Back16.gif"), "Previous canvas",
                new Integer(KeyEvent.VK_LEFT));

        _backAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
            InputEvent.ALT_DOWN_MASK, false));
        _forwardAction = new ForwardAction("Go Forward", Util.getImageIcon("Forward16.gif"),
                "Next canvas", new Integer(KeyEvent.VK_RIGHT));
        _forwardAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
            InputEvent.ALT_DOWN_MASK, false));
        _homeAction = new HomeAction("Return Home", Util.getImageIcon("Home16.gif"), "Home",
                new Integer(KeyEvent.VK_HOME));
        // _localsAction = new LocalsAction("Locals", Util.getImageIcon(
        // "Local.gif"), "Locals", new Integer(KeyEvent.VK_L));

        for (int i = 0; i < MAX_STACK_SIZE; ++i)
        {
            _backMenuItems[i] = new JMenuItem();
            _backMenuItems[i].addActionListener(this);
            _backPopup.add(_backMenuItems[i]);
            _forwardMenuItems[i] = new JMenuItem();
            _forwardMenuItems[i].addActionListener(this);
            _forwardPopup.add(_forwardMenuItems[i]);
        }

        if (ComponentRegistry.isStandAlone())
        {
            JButton newItem = new JButton(editor.newAction);
            newItem.setToolTipText(SimBionicEditor.NEW_COMMAND_TOOLTIP);
            newItem.setText("");
            _toolBarTop.add(newItem);

            JButton openItem = new JButton(editor.openAction);
            openItem.setToolTipText(SimBionicEditor.OPEN_COMMAND_TOOLTIP);
            openItem.setText("");
            _toolBarTop.add(openItem);

            JButton saveItem = new JButton(editor.saveAction);
            saveItem.setText("");
            saveItem.setToolTipText(null);
            _toolBarTop.add(saveItem);

            _toolBarTop.addSeparator();
        }

        _toolBarTop.add(editor.cutAction);
        _toolBarTop.add(editor.copyAction);
        _toolBarTop.add(editor.pasteAction);
        _toolBarTop.addSeparator();
        _toolBarTop.add(editor.undoAction);
        _toolBarTop.add(editor.redoAction);
        _toolBarTop.addSeparator();

        JButton button = new JButton(_backAction);
        button.setText(""); // an icon-only button
        button.setFocusPainted(false);
        // button.setMaximumSize(new Dimension(27, 26));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.addMouseListener(new MouseAdapter()
        {

            public void mousePressed(MouseEvent e)
            {
                if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0)
                {
                    SB_Behavior behavior;
                    JMenuItem menuItem;
                    int j = _backStack.size() - 1;
                    if (j < 0)
                        return;
                    for (int i = 0; i < MAX_STACK_SIZE; ++i)
                    {
                        menuItem = _backMenuItems[i];
                        if (j >= 0)
                        {
                            behavior = (SB_Behavior) _backStack.get(j);
                            menuItem.setText(behavior.getName());
                            menuItem.setVisible(true);
                            --j;
                        } else
                            menuItem.setVisible(false);
                    }
                    _backPopup.show(SB_ToolBar.this, e.getX(), e.getY());
                }
            }
        });
        _toolBarTop.add(button);

        _toolBarTop.add(Box.createHorizontalStrut(3));

        button = new JButton(_forwardAction);
        button.setText(""); // an icon-only button
        button.setFocusPainted(false);
        // button.setMaximumSize(new Dimension(27, 26));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.addMouseListener(new MouseAdapter()
        {

            public void mousePressed(MouseEvent e)
            {
                if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0)
                {
                    SB_Behavior behavior;
                    JMenuItem menuItem;
                    int j = _forwardStack.size() - 1;
                    if (j < 0)
                        return;
                    for (int i = 0; i < MAX_STACK_SIZE; ++i)
                    {
                        menuItem = _forwardMenuItems[i];
                        if (j >= 0)
                        {
                            behavior = (SB_Behavior) _forwardStack.get(j);
                            menuItem.setText(behavior.getName());
                            menuItem.setVisible(true);
                            --j;
                        } else
                            menuItem.setVisible(false);
                    }
                    _forwardPopup.show(SB_ToolBar.this, e.getX(), e.getY());
                }
            }
        });
        _toolBarTop.add(button);

        _toolBarTop.add(Box.createHorizontalStrut(3));

        button = new JButton(_homeAction);
        button.setText(""); // an icon-only button
        // button.setMaximumSize(new Dimension(27, 26));
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setFocusPainted(false);
        _toolBarTop.add(button);

        SB_Catalog cat = _editor.getCatalog();
        _toolBarTop.addSeparator();

        _hackConnectorButton = new JToggleButton(new AbstractAction("",Util.getImageIcon("Connector.gif")) 
        {
            public void actionPerformed(ActionEvent e)
            {
                // do nothing
            }
        });
        _hackConnectorButton.setToolTipText("Draw a connector on the canvas");
        _toolBarTop.add(_hackConnectorButton);
        
        button = new JButton(new LocalsAction("Locals", Util.getImageIcon("Local.gif"),
                "Open the variables local to this behavior", null));
        _toolBarTop.add(button);

        if (ComponentRegistry.isStandAlone())
        {
            _toolBarTop.addSeparator();
    
            button = new JButton(editor.checkErrorAction);
            _toolBarTop.add(button);
    
            _toolBarTop.addSeparator();
    
            _toolBarTop.add(new SB_Button(editor.connectAction));
            _toolBarTop.add(new SB_Button(editor.breakpointAction));
            _toolBarTop.add(new SB_Button(editor.showBreakpointsAction));
        }
        
        add(_toolBarTop);
    }
    
    /**
     * Disables appropriate toolbar items that should not be available
     * when using debugger.
     * 
     * @param _debugger
     */
    public void setDebugModeOn(){
    	_hackConnectorButton.setEnabled(false);
    	
    	debugMode = true;
    }
    
    /**
     * Reenables appropriate toolbar items upon leaving debug mode.
     *
     */
    public void setDebugModeOff(){
    	_hackConnectorButton.setEnabled(true);
    	debugMode = false;
    }

    public JToggleButton getHackConnectorButton()
    {
        return _hackConnectorButton;
    }
    
    public void addToTop(Component c)
    {
        _toolBarTop.add(c);
    }

    public void hideDialogs()
    {
        if (_localsDialog != null)
            _localsDialog.setVisible(false);
    }

    // convenience accessors
    protected SB_ProjectBar getProjectBar()
    {
        return (SB_ProjectBar) ComponentRegistry.getProjectBar();
    }

    protected SB_TabbedCanvas getTabbedCanvas()
    {
        return (SB_TabbedCanvas) ComponentRegistry.getContent();
    }

    public void setEnabled(boolean enabled)
    {
        if (enabled == _homeAction.isEnabled())
            return;

        if (enabled == false)
        {
            _backAction.setEnabled(false);
            _forwardAction.setEnabled(false);
            _homeAction.setEnabled(false);
            // _editor._localsAction.setEnabled(false);

            // _exprField.setText("");

            if (_localsDialog != null)
            {
                _lastVisible = _localsDialog.isVisible();
                if (_lastVisible)
                    _localsDialog.setVisible(false);
            } else
                _lastVisible = false;
        } else
        {
            _backAction.setEnabled(!_backStack.isEmpty());
            _forwardAction.setEnabled(!_forwardStack.isEmpty());
            _homeAction.setEnabled(true);
            // _editor._localsAction.setEnabled(true);

            getTabbedCanvas().getActiveCanvas().clearSingle();
            getTabbedCanvas().getActiveCanvas().updateSingle();

            if (_lastVisible)
                _localsDialog.setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JButton)
        {
            return;
        }

        JMenuItem menuItem = (JMenuItem) (e.getSource());

        SB_TabbedCanvas tabbedCanvas = getTabbedCanvas();
        for (int i = 0; i < MAX_STACK_SIZE; ++i)
        {
            if (menuItem == _backMenuItems[i])
            {
                addToForwardStack(tabbedCanvas._behavior);
                int j = _backStack.size();
                while (i >= 1)
                {
                    addToForwardStack((SB_Behavior) _backStack.remove(--j));
                    --i;
                }
                tabbedCanvas.setBehavior((SB_Behavior) _backStack.remove(--j), false);
                break;
            } else if (menuItem == _forwardMenuItems[i])
            {
                addToBackStack(tabbedCanvas._behavior);
                int j = _forwardStack.size();
                while (i >= 1)
                {
                    addToBackStack((SB_Behavior) _forwardStack.remove(--j));
                    --i;
                }
                tabbedCanvas.setBehavior((SB_Behavior) _forwardStack.remove(--j), false);
                break;
            }
        }

        _backAction.setEnabled(!_backStack.isEmpty());
        _forwardAction.setEnabled(!_forwardStack.isEmpty());
    }

    static boolean equalBindings(List<SB_Binding> bindings1,
            List<SB_Binding> bindings2)
    {
        int size = bindings1.size();
        if (size != bindings2.size())
            return false;
        for (int i = 0; i < size; ++i) {
            SB_Binding binding1 = bindings1.get(i);
            SB_Binding binding2 = bindings2.get(i);
            if (!binding1.equals(binding2))
                return false;
        }
        return true;
    }

    class BackAction extends AbstractAction
    {

        public BackAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            int size = _backStack.size();
            if (size > 0)
            {
                SB_TabbedCanvas tabbedCanvas = getTabbedCanvas();
                SB_Behavior behavior = (SB_Behavior) _backStack.get(size - 1);
                _backStack.remove(size - 1);
                addToForwardStack(tabbedCanvas._behavior);
                tabbedCanvas.setBehavior(behavior, false);
            }
        }
    }

    class ForwardAction extends AbstractAction
    {

        public ForwardAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            int size = _forwardStack.size();
            if (size > 0)
            {
                SB_TabbedCanvas tabbedCanvas = getTabbedCanvas();
                SB_Behavior behavior = (SB_Behavior) _forwardStack.get(size - 1);
                _forwardStack.remove(size - 1);
                addToBackStack(tabbedCanvas._behavior);
                tabbedCanvas.setBehavior(behavior, false);
            }
        }
    }

    class HomeAction extends AbstractAction
    {

        public HomeAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            getTabbedCanvas().setBehavior(getProjectBar()._catalog._main, true);
        }
    }

    class LocalsAction extends AbstractAction
    {

        public LocalsAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            if (mnemonic != null)
                putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            SB_LocalsTree localsTree = ComponentRegistry.getLocalsTree();
            if (_localsDialog == null)
            {
                _localsDialog = new JDialog(ComponentRegistry.getFrame(), "Local Variables", false);
                JScrollPane scrollPane = new JScrollPane(localsTree);
                scrollPane.setPreferredSize(new Dimension(188, 217));
                _localsDialog.getContentPane().add(scrollPane);
                _localsDialog.pack();

                Dimension dialogSize = _localsDialog.getSize();
                Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
                _localsDialog.setLocation(
                    frameBounds.x + frameBounds.width - dialogSize.width - 40, frameBounds.y + 118);
                _localsDialog.addWindowListener(new WindowAdapter()
                {

                    public void windowClosing(WindowEvent e)
                    {
                        getTabbedCanvas().getActiveCanvas().requestFocus();
                    }
                });
            }
            DefaultMutableTreeNode locals = getTabbedCanvas().getActiveCanvas()._poly.getLocals();
            localsTree.setRoot(locals);
            _localsDialog.setVisible(true);
        }
    }

    protected void addToBackStack(SB_Behavior behavior)
    {
        int size = _backStack.size();
        if (size >= MAX_STACK_SIZE)
            _backStack.remove(0);
        _backStack.add(behavior);
    }

    protected void addToForwardStack(SB_Behavior behavior)
    {
        int size = _forwardStack.size();
        if (size >= MAX_STACK_SIZE)
            _forwardStack.remove(0);
        _forwardStack.add(behavior);
    }

    protected void clearStacks()
    {
        _backStack.clear();
        _forwardStack.clear();
        _backAction.setEnabled(false);
        _forwardAction.setEnabled(false);
    }

    protected void removeBehavior(SB_Behavior behavior)
    {
        while (_backStack.remove(behavior))
            ;
        while (_forwardStack.remove(behavior))
            ;

        SB_Behavior prevBehavior = null;
        int size = _backStack.size();
        for (int i = size - 1; i >= 0; --i)
        {
            behavior = (SB_Behavior) _backStack.get(i);
            if (behavior == prevBehavior)
                _backStack.remove(i);
            else
                prevBehavior = behavior;
        }
        size = _backStack.size();
        if (size > 0 && _backStack.get(size - 1) == getTabbedCanvas()._behavior)
        {
            _backStack.remove(size - 1);
            --size;
        }
        _backAction.setEnabled(size > 0);

        prevBehavior = null;
        size = _forwardStack.size();
        for (int i = size - 1; i >= 0; --i)
        {
            behavior = (SB_Behavior) _forwardStack.get(i);
            if (behavior == prevBehavior)
                _forwardStack.remove(i);
            else
                prevBehavior = behavior;
        }
        size = _forwardStack.size();
        if (size > 0 && _forwardStack.get(size - 1) == getTabbedCanvas()._behavior)
        {
            _forwardStack.remove(size - 1);
            --size;
        }
        _forwardAction.setEnabled(size > 0);
    }
    
    // XXX: MOTL
    public Action getBackAction() {
        return _backAction;
    }
    
    // XXX: MOTL
    public Action getForwardAction() {
        return _forwardAction;
    }

    @Deprecated
    @Override
    public void matchListChanged(List<String> matchInsertionStrings,
            String funcName, String paramName, int paramIndex) {
	}

    /**
     * XXX: 2018-05-24 -jmm
     * <br>
     * This method was apparently never called since the initial commit to
     * GitHub; with the removal of null SB_GlassPane handling in
     * SB_Autocomplete and SB_AutocompleteTextArea, there no longer exist code
     * paths that can call this method.
     * */
    @Deprecated
	@Override
	public void matchSelectionChanged(String matchSel) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

    /**
     * XXX: 2018-05-24 -jmm
     * <br>
     * This method was apparently never called since the initial commit to
     * GitHub; with the removal of null SB_GlassPane handling in
     * SB_Autocomplete and SB_AutocompleteTextArea, there no longer exist code
     * paths that can call this method.
     * */
    @Deprecated
	@Override
	public void completeExpression() {
	}
	

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
