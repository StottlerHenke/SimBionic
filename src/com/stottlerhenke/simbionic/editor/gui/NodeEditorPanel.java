package com.stottlerhenke.simbionic.editor.gui;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;

public class NodeEditorPanel implements CanvasSelectionListener {

    /**
     * This convenience class provides a hardcoded card layout that can be
     * switched between the panel used for editing noncompound actions
     * (with independent expression and binding editor) and the panel used
     * for editing compound actions (one editor for both).
     * */
    @SuppressWarnings("serial")
    private static class BindingAndExprPanel extends JPanel {

        private static class NoncompoundPanel extends JPanel {
            final SB_BindingsEditor bindingsEditor;
            final SB_ElementExprEditor expressionEditor;

            NoncompoundPanel(SimBionicEditor editor) {
                this(new SB_BindingsEditor(editor),
                        new SB_ElementExprEditor(editor));
            }

            private NoncompoundPanel(SB_BindingsEditor bindEditor,
                    SB_ElementExprEditor exprPanel) {
                this.bindingsEditor = bindEditor;
                this.expressionEditor = exprPanel;
                this.initLayout();
            }

            private void initLayout() {
                this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

                JPanel bindingsPanel = bindingsEditor.getContentPanel();

                bindingsPanel.setBorder(
                        BorderFactory.createTitledBorder("Bindings Editor"));

                JPanel expressionPanel = expressionEditor.getContentPanel();

                expressionPanel.setBorder(
                        BorderFactory.createTitledBorder("Expression Editor"));

                this.add(bindingsPanel);
                this.add(expressionPanel);
            }

            /**
             * Sets the contents of this panel as invisible.
             */
            private void clearContents() {
                bindingsEditor.clearBindings();
                bindingsEditor.setVisible(false);

                expressionEditor.clearDisplayedExpr();
                expressionEditor.setVisible(false);
            }

            void registerEditingFinishedListener(Runnable r) {
                bindingsEditor.registerTerminateEditingListener(r);
                expressionEditor.registerTerminateEditingListener(r);
            }

            /**
             * @param newSelection a non-null SB_Drawable
             */
            void handleNoncompoundBindingsHolder(SB_Polymorphism currentPoly,
                    SB_Drawable newSelection) {
                this.clearContents();

                if (newSelection instanceof SB_BindingsHolder) {
                    handleBindingsHolder(currentPoly,
                            (SB_BindingsHolder) newSelection);
                }
                if (newSelection instanceof SB_Element) {
                    handleElement(currentPoly, (SB_Element) newSelection);
                }
            }

            /**
             * @param holder a non-null SB_BindingsHolder
             */
            private void handleBindingsHolder(SB_Polymorphism currentPoly,
                    SB_BindingsHolder holder) {
                // XXX: Apparently, some polymorphisms might be read-only.
                boolean isParentCore = currentPoly.getParent().isCore();
                bindingsEditor.setVisible(true);
                bindingsEditor.populateBindingsFromHolder(currentPoly, holder,
                        isParentCore);
            }

            /**
             * {@link SB_Element} is the superclass of {@link SB_Condition} and
             * {@link SB_Rectangle}; it is arguably analogous to a
             * "SB_ExpressionHolder" type.
             * 
             * @param element a non-null SB_Element
             */
            private void handleElement(SB_Polymorphism currentPoly,
                    SB_Element element) {
                boolean isParentCore = currentPoly.getParent().isCore();
                expressionEditor.setVisible(true);
                expressionEditor.populateExprFromElement(currentPoly, element,
                        isParentCore);
            }
        }

        private final static String NONCOMPOUND = "noncompound";
        private final static String MULTI_BINDINGS = "multi";
        private final static String BLANK = "blank";

        final NoncompoundPanel noncompound;
        final SB_MultiBindingsEditor multiBindings;
        final CardLayout layout;

        BindingAndExprPanel(SimBionicEditor editor) {
            this(new NoncompoundPanel(editor),
                new SB_MultiBindingsEditor(editor));
        }

        private BindingAndExprPanel(NoncompoundPanel noncompound,
                SB_MultiBindingsEditor multiBindings) {
            this.noncompound = noncompound;
            this.multiBindings = multiBindings;
            this.layout = new CardLayout();
            this.setLayout(layout);
            this.add(new JPanel(), BLANK);
            this.add(noncompound, NONCOMPOUND);
            this.add(prepMultiBindingsComponent(multiBindings),
                    MULTI_BINDINGS);
        }

        private static Component prepMultiBindingsComponent(
                SB_MultiBindingsEditor multiEditor) {
            JPanel multiBindingsPanel = multiEditor.getContentPanel();
            multiBindingsPanel.setBorder(
                    BorderFactory.createTitledBorder(
                            "Compound Action Editor"));
            return multiBindingsPanel;
        }

        void registerEditingFinishedListener(Runnable r) {
            noncompound.registerEditingFinishedListener(r);
            multiBindings.registerTerminateEditingListener(r);
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

        void handleDrawable(SB_Polymorphism containingPoly,
                SB_Drawable drawable) {
            //SB_MultiRectangle needs to be special-cased to handle
            //the fact that it uses one editor for both bindings and exprs.
            if (drawable instanceof SB_MultiRectangle) {
                showMultiBindings();
                handleMultiRectangle(containingPoly,
                        (SB_MultiRectangle) drawable);
            } else {
                showNoncompound();
                noncompound.handleNoncompoundBindingsHolder(
                        containingPoly, drawable);
            }
        }

        /**
         * 2018-05-25 -jmm <br>
         * XXX: The current behavior is slightly inconsistent; the text of
         * the expression editor is written (replicating its "write on focus
         * loss" behavior), but the bindings editor will not write "ongoing"
         * changes (text areas still being edited). This is a no-op when the
         * multi-editor is enabled.
         */
        void writeOutstandingChanges() {
            noncompound.expressionEditor.updateSelectedElement();
        }

        /**
         * @param multiRect a non-null SB_MultiRectangle
         * */
        private void handleMultiRectangle(SB_Polymorphism currentPoly,
                SB_MultiRectangle multiRect) {
            boolean isParentCore = currentPoly.getParent().isCore();
            multiBindings.setVisible(true);
            multiBindings.populateBindingsFromHolder(currentPoly, multiRect,
                    isParentCore);
        }

    }

    private final SimBionicEditor editor;

    /**
     * The top-level Component managed by this instance.
     * */
    private final Component content;

    private final SB_CommentEditor commentEditor;

    /**
     * 
     * */
    private final BindingAndExprPanel bindingAndExprArea;

    NodeEditorPanel(SimBionicEditor editor) {
        this.editor = editor;
        commentEditor = new SB_CommentEditor();
        bindingAndExprArea = new BindingAndExprPanel(editor);

        this.content = genTestPanel(commentEditor, bindingAndExprArea);
    }

    Component getContent() {
        return this.content;
    }

    void registerEditingFinishedListener(Runnable r) {
        commentEditor.registerTerminateEditingListener(r);
        bindingAndExprArea.registerEditingFinishedListener(r);
    }

    private static Component genTestPanel(SB_CommentEditor commentEditor,
            BindingAndExprPanel bindAndExprPanel) {
        JPanel nodeEditor = new JPanel();
      //"default" layout is flow layout (left to right...)
        nodeEditor.setLayout(new BoxLayout(nodeEditor, BoxLayout.Y_AXIS));

        JPanel commentPanel = commentEditor.getContentPanel();
        commentPanel.setBorder(BorderFactory.createTitledBorder(
                "Comment Editor"));
        commentEditor.setVisible(false);
        nodeEditor.add(commentPanel);

        nodeEditor.add(bindAndExprPanel);

        JScrollPane scrollArea = new JScrollPane(nodeEditor);
        return scrollArea;
    }

    @Override
    public void selectionChanged(SB_Polymorphism currentPoly,
            SB_Drawable oldSelection, SB_Drawable newSelection) {
        onNewItemSelected(currentPoly, newSelection);
    }

    /**
     * 
     * */
    public void polyChanged(SB_Polymorphism oldPoly, SB_Polymorphism newPoly) {
        if (newPoly != null) {
            SB_Drawable currentlyHighlighted = newPoly.singleHighlight();
            onNewItemSelected(newPoly, currentlyHighlighted);
        } else {
            clearDisplay();
        }
    }

    /**
     * Implementation detail: when switching between nodes, the
     * formerly-selected node is considered irrelevant.
     * */
    private void onNewItemSelected(SB_Polymorphism containingPoly,
            SB_Drawable drawable) {
      //Write comment and expression
        writeOnSelectionChange();
        if (drawable == null) {
            clearDisplay();
        } else {
            //Comment handling is apparently independent
            if (drawable instanceof SB_CommentHolder) {
                handleCommentHolder(containingPoly,
                        (SB_CommentHolder) drawable);
            }
            bindingAndExprArea.handleDrawable(containingPoly, drawable);
        }
    }

    private void clearDisplay() {
        bindingAndExprArea.showBlank();
        commentEditor.setVisible(false);
    }

    /**
     * Changes should be flushed when switching from an editable item.
     * */
    private void writeOnSelectionChange() {
        commentEditor.updateSelectedCommentHolder();
        bindingAndExprArea.writeOutstandingChanges();
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

}
