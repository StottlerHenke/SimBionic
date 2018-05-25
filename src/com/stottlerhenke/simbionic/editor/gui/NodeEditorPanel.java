package com.stottlerhenke.simbionic.editor.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.Util;

public class NodeEditorPanel implements CanvasSelectionListener {

    @SuppressWarnings("serial")
    private static class NoncompoundPanel extends JPanel {
        final SB_BindingsEditor bindingsEditor;
        final JPanel expressionPanel;

        NoncompoundPanel(SB_BindingsEditor bindEditor, JPanel exprPanel) {
            this.bindingsEditor = bindEditor;
            this.expressionPanel = exprPanel;
            this.initLayout();
        }

        private void initLayout() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel bindingsPanel = bindingsEditor.getContentPanel();

            bindingsPanel.setBorder(
                    BorderFactory.createTitledBorder("Bindings Editor"));

            expressionPanel.setBorder(
                    BorderFactory.createTitledBorder("Expression Editor"));

            this.add(bindingsPanel);
            this.add(expressionPanel);
        }

        /**
         * Sets the contents of this panel as invisible.
         * */
        private void clearContents() {
            bindingsEditor.clearBindings();
            bindingsEditor.setVisible(false);
            
            expressionPanel.setVisible(false);
        }
    }

    /**
     * This convenience class provides a hardcoded card layout that can be
     * switched between the panel used for editing noncompound actions
     * (with independent expression and binding editor) and the panel used
     * for editing compound actions (one editor for both).
     * */
    @SuppressWarnings("serial")
    private static class BindingAndExprPanel extends JPanel {
        private final static String NONCOMPOUND = "noncompound";
        private final static String MULTI_BINDINGS = "multi";
        private final static String BLANK = "blank";

        final NoncompoundPanel noncompound;
        final JPanel multiBindings;
        final CardLayout layout;

        BindingAndExprPanel(NoncompoundPanel noncompound,
                JPanel multiBindings) {
            this.noncompound = noncompound;
            this.multiBindings = multiBindings;
            this.layout = new CardLayout();
            this.setLayout(layout);
            this.add(new JPanel(), BLANK);
            this.add(noncompound, NONCOMPOUND);
            this.add(multiBindings, MULTI_BINDINGS);
        }

        void showNoncompound() {
            layout.show(this, NONCOMPOUND);
        }

        void showMultiBindings() {
            layout.show(this, MULTI_BINDINGS);
        }

        void showBlank() {
            layout.show(this, BLANK);
        }

    }



    private final SimBionicEditor editor;

    /**
     * XXX: Assumes that only one SB_TabbedCanvas instance is used during the
     * lifetime of this panel; the old approach of using the ComponentRegistry
     * would theoretically catch switching SB_TabbedCanvas instances (even
     * though no switch happened in practice.)
     * */
    private final SB_TabbedCanvas tabbedCanvas;

    /**
     * The top-level JPanel managed by this instance.
     * */
    private final JPanel contentPanel;

    private final SB_CommentEditor commentEditor;

    private final JPanel commentPanel = null;

    /**
     * 
     * */
    private final BindingAndExprPanel bindingAndExprArea;

    /**
     * The JPanel used to contain the independent expression and binding panels
     * editable objects that are not SB_MultiRectangle instances.
     * */
    private final NoncompoundPanel noncompoundPanel;

    private final JPanel expressionPanel = null;

    /**
     * 
     * This panel should replace the entire {@link #noncompoundPanel}
     * (expression and bindings editor) when editing a compound action.
     * */
    private final SB_MultiBindingsEditor multiBindingsEditor;

    NodeEditorPanel(SimBionicEditor editor, SB_TabbedCanvas canvas) {
        this.editor = editor;
        this.tabbedCanvas = canvas;
        commentEditor = new SB_CommentEditor();
        multiBindingsEditor = new SB_MultiBindingsEditor(editor);
        JPanel expressionPanel = genExpressionPanel();
        noncompoundPanel
        = new NoncompoundPanel(new SB_BindingsEditor(editor), expressionPanel);

        bindingAndExprArea = new BindingAndExprPanel(noncompoundPanel,
                prepMultiBindingsPanel(multiBindingsEditor));

        this.contentPanel = genTestPanel(commentEditor, bindingAndExprArea);
        
        ComponentRegistry.setEditorPanel(this);
    }

    JPanel getPanel() {
        return this.contentPanel;
    }

    void registerEditingFinishedListener(Runnable r) {
        commentEditor.registerTerminateEditingListener(r);
        noncompoundPanel.bindingsEditor.registerTerminateEditingListener(r);
        multiBindingsEditor.registerTerminateEditingListener(r);
    }

    private static JPanel prepMultiBindingsPanel(
            SB_MultiBindingsEditor multiEditor) {
        JPanel multiBindingsPanel = multiEditor.getContentPanel();
        multiBindingsPanel.setBorder(
                BorderFactory.createTitledBorder("Compound Action Editor"));
        return multiBindingsPanel;
    }

    private static JPanel genTestPanel(SB_CommentEditor commentEditor,
            BindingAndExprPanel bindAndExprPanel) {
        JPanel nodeEditor = new JPanel();
      //"default" layout is flow layout (left to right...)
        nodeEditor.setLayout(new BoxLayout(nodeEditor, BoxLayout.Y_AXIS));
        //The size of the entire panel is apparently independent of
        //the text component.

        JTextField nodeEditorText = new JTextField();
        nodeEditorText.setEditable(false);
        nodeEditorText.setText("Future Node Editor Location");
        nodeEditorText.setMaximumSize(new Dimension(375, 21));
        nodeEditor.add(nodeEditorText);

        JPanel commentPanel = commentEditor.getContent();
        commentPanel.setBorder(BorderFactory.createTitledBorder(
                "Comment Editor"));
        commentEditor.setVisible(false);
        nodeEditor.add(commentPanel);

        nodeEditor.add(bindAndExprPanel);


        return nodeEditor;
    }

    private static JPanel genExpressionPanel() {

        //Use default flow layout
        JPanel expressionPanel = new JPanel();

        ExpressionAction _exprAction = new ExpressionAction("",
                Util.getImageIcon("Expression.png"), "Edit Expression",
                new Integer(KeyEvent.VK_E));

        JButton button = new JButton(_exprAction);
        button.setDisplayedMnemonicIndex(-1);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(23, 21));
        expressionPanel.add(button);
        JTextField _exprField = new JTextField(10);
                //_editor.createAutocomplete();
        _exprField.setEnabled(false);
        _exprField.setPreferredSize(new Dimension(375, 21));
//        _exprField.addActionListener(new ActionListener()
//        {
//
//            public void actionPerformed(ActionEvent event)
//            {
//                SB_Canvas canvas = getTabbedCanvas().getActiveCanvas();
//                canvas.requestFocus();
//            }
//        });
        //What is the importance of making the new focus listener the
        //"second to last" one?
        FocusListener[] fls = _exprField.getFocusListeners();
        FocusListener fl = fls[fls.length - 1];
//        _exprField.removeFocusListener(fl);
//        _exprField.addFocusListener(new FocusListener()
//        {
//
//            public void focusGained(FocusEvent event)
//            {
//                SB_Canvas canvas = getTabbedCanvas().getActiveCanvas();
//                SB_Drawable selDrawable = canvas._selDrawable;
//                _exprField.setReturnsValue(
//                        selDrawable instanceof SB_Condition);
//            }
//
//            public void focusLost(FocusEvent event)
//            {
//                handleFocusLost(_exprField);
//            }
//        });
//        _exprField.addFocusListener(fl);
        expressionPanel.add(_exprField);
        expressionPanel.add(Box.createHorizontalStrut(5));

        return expressionPanel;
    }


    static class ExpressionAction extends AbstractAction
    {

        public ExpressionAction(String text, ImageIcon icon, String desc, Integer mnemonic)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            //showExpressionDialog();
        }
    }


    @Override
    public void selectionChanged(SB_Polymorphism currentPoly,
            SB_Drawable oldSelection, SB_Drawable newSelection) {
        if (newSelection == null) {
            bindingAndExprArea.showBlank();
            commentEditor.setVisible(false);
        } else {

            //Comment handling is apparently independent
            if (newSelection instanceof SB_CommentHolder) {
                handleCommentHolder(currentPoly,
                        (SB_CommentHolder) newSelection);
            }
            //However, SB_MultiRectangle needs to be special-cased to handle
            //the fact that it uses one editor for both bindings and exprs.
            if (newSelection instanceof SB_MultiRectangle) {
                handleMultiRectangle(currentPoly,
                        (SB_MultiRectangle) newSelection);
            } else {
                handleNoncompoundBindingsHolder(currentPoly, newSelection);
            }
        }
    }

    /**
     * @param holder a non-null SB_CommentHolder
     * */
    private void handleCommentHolder(SB_Polymorphism currentPoly,
            SB_CommentHolder holder) {
        boolean isParentCore = currentPoly.getParent().isCore();
        commentEditor.populateCommentFromHolder(currentPoly, holder,
                isParentCore);
        commentEditor.setVisible(true);
    }

    /**
     * @param multiRect a non-null SB_MultiRectangle
     * */
    private void handleMultiRectangle(SB_Polymorphism currentPoly,
            SB_MultiRectangle multiRect) {
        bindingAndExprArea.showMultiBindings();
        boolean isParentCore = currentPoly.getParent().isCore();
        multiBindingsEditor.setVisible(true);
        multiBindingsEditor.populateBindingsFromHolder(currentPoly, multiRect,
                isParentCore);
    }

    /**
     * @param newSelection a non-null SB_Drawable
     * */
    private void handleNoncompoundBindingsHolder(SB_Polymorphism currentPoly,
            SB_Drawable newSelection) {
        multiBindingsEditor.clearBindings();

        bindingAndExprArea.showNoncompound();
        noncompoundPanel.clearContents();

        if (newSelection instanceof SB_BindingsHolder) {
            handleBindingsHolder(currentPoly,
                    (SB_BindingsHolder) newSelection);
        }
        if (newSelection instanceof SB_Element) {
            handleElement(currentPoly,
                    (SB_Element) newSelection);
        }
    }

    /**
     * @param holder a non-null SB_BindingsHolder
     * */
    private void handleBindingsHolder(SB_Polymorphism currentPoly,
            SB_BindingsHolder holder) {
        //XXX: Apparently, some polymorphism might be read-only.
        boolean isParentCore = currentPoly.getParent().isCore();
        noncompoundPanel.bindingsEditor.setVisible(true);
        noncompoundPanel.bindingsEditor.populateBindingsFromHolder(currentPoly,
                holder, isParentCore);
    }

    /**
     * {@link SB_Element} is the superclass of {@link SB_Condition} and
     * {@link SB_Rectangle}; it is arguably analogous to a
     * "SB_ExpressionHolder" type.
     * @param element a non-null SB_Element
     * */
    private void handleElement(SB_Polymorphism currentPoly,
            SB_Element element) {
        noncompoundPanel.expressionPanel.setVisible(true);
    }

}
