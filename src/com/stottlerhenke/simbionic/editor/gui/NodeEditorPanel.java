package com.stottlerhenke.simbionic.editor.gui;

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

    private final SimBionicEditor editor;

    /**
     * XXX: Assumes that only one SB_TabbedCanvas instance is used during the
     * lifetime of this panel; the old approach of using the ComponentRegistry
     * would theoretically catch switching SB_TabbedCanvas instances (even
     * though no switch happened in practice.)
     * */
    private final SB_TabbedCanvas tabbedCanvas;

    private final JPanel panel;

    private final SB_BindingsPanel bindingsPanel;

    NodeEditorPanel(SimBionicEditor editor, SB_TabbedCanvas canvas) {
        this.editor = editor;
        this.tabbedCanvas = canvas;
        bindingsPanel = new SB_BindingsPanel(editor, tabbedCanvas);
        this.panel = genTestPanel(bindingsPanel);
        
        ComponentRegistry.setEditorPanel(this);
    }

    JPanel getPanel() {
        return this.panel;
    }

    void registerEditingFinishedListener(Runnable r) {
        bindingsPanel.registerTerminateEditingListener(r);
    }

    static JPanel genTestPanel(SB_BindingsPanel bindingsPanel) {
        JPanel nodeEditor = new JPanel();
        JTextField nodeEditorText = new JTextField();
        nodeEditorText.setEditable(false);
        nodeEditorText.setText("Future Node Editor Location");
        nodeEditorText.setMaximumSize(new Dimension(375, 21));
        nodeEditor.add(nodeEditorText);


        bindingsPanel.setBorder(BorderFactory.createTitledBorder("Bindings editor"));

        //"default" layout is flow layout (left to right...)
        nodeEditor.setLayout(new BoxLayout(nodeEditor, BoxLayout.Y_AXIS));
        JPanel expressionPanel = genExpressionPanel();
        nodeEditor.add(expressionPanel);
        nodeEditor.add(bindingsPanel);

        Dimension preferredSize = nodeEditor.getPreferredSize();

        int newPreferredWidth = Math.max(
                bindingsPanel.getPreferredSize().width,
                expressionPanel.getPreferredSize().width);

        Dimension newPreferredSize = new Dimension(preferredSize.height,
                newPreferredWidth);
        nodeEditor.setPreferredSize(newPreferredSize);

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
        // TODO Auto-generated method stub
        if (newSelection == null) {
            bindingsPanel.clearBindings();
            bindingsPanel.setVisible(false);
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
                //Default case: independent bindings and expressions.
                if (newSelection instanceof SB_BindingsHolder) {
                    handleBindingsHolder(currentPoly,
                            (SB_BindingsHolder) newSelection);
                }
                if (newSelection instanceof SB_Element) {
                    handleElement(currentPoly,
                            (SB_Element) newSelection);
                }
            }
        }
    }

    /**
     * @param holder a non-null SB_CommentHolder
     * */
    private void handleCommentHolder(SB_Polymorphism currentPoly,
            SB_CommentHolder holder) {
        boolean isParentCore = currentPoly.getParent().isCore();
        //TODO: Populate and make visible comment area 
    }

    /**
     * @param multiRect a non-null SB_MultiRectangle
     * */
    private void handleMultiRectangle(SB_Polymorphism currentPoly,
            SB_MultiRectangle multiRect) {
        boolean isParentCore = currentPoly.getParent().isCore();
        //TODO: Set expression and bindings area as non visible
        //set the existing bindings panel to invisible?
        bindingsPanel.clearBindings();
        bindingsPanel.setVisible(false);
    }

    /**
     * @param holder a non-null SB_BindingsHolder
     * */
    private void handleBindingsHolder(SB_Polymorphism currentPoly,
            SB_BindingsHolder holder) {
        //XXX: Apparently, some polymorphism might be read-only.
        boolean isParentCore = currentPoly.getParent().isCore();
        bindingsPanel.setVisible(true);
        bindingsPanel.populateBindingsFromHolder(currentPoly,
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
        
    }

}
