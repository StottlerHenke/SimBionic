package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;


class SB_ElementExprEditor {

    /**
     * A convenience class used to hold both a SB_BindingsHolder instance and
     * the SB_Polymorphism instance containing it.
     * <br>
     * XXX: it is the caller's responsibility to ensure that poly is the actual
     * containing polymorphism of holder.
     * */
    private static class ElementAndPoly {
        final SB_Element element;
        final SB_Polymorphism poly;

        ElementAndPoly(SB_Element holder,
                SB_Polymorphism poly) {
            this.element = Objects.requireNonNull(holder);
            this.poly = Objects.requireNonNull(poly);
        }

        static ElementAndPoly of(SB_Element holder,
                SB_Polymorphism poly) {
            return new ElementAndPoly(holder, poly);
        }
    }

    private final SB_AutocompleteTextArea autocompleteArea;

    private final JPanel contentPanel;

    private Optional<ElementAndPoly> elementAndPoly = Optional.empty();

    /**
     * XXX: members are not added or accessed in a threadsafe manner.
     * */
    private final List<Runnable> onEditListeners = new ArrayList<>();

    SB_ElementExprEditor(SimBionicEditor editor) {

        autocompleteArea = editor.createAutocompleteTextArea();
        JScrollPane scroll = new JScrollPane(autocompleteArea);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        addListeners();
    }

    /**
     * XXX: only update when focus lost to avoid update on every keystroke.
     * XXX: The complications of focusLost (filtering on the other component
     * that is gaining focus) suggests that managing "auto write-through" using
     * focus may not be ideal. The motivation of this is the fact that clicking
     * the SB_GlassPane displayed by autocomplete causes two focus switches
     * that should not be interpreted as "done editing".
     * */
    private void addListeners() {
        //
        autocompleteArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()
                    && !autocompleteArea.hasComponentInGlassPane(
                        e.getOppositeComponent())) {
                    updateSelectedElement();
                }
            }
            
        });
    }

    JPanel getContentPanel() {
        return contentPanel;
    }

    void setVisible(boolean aFlag) {
        contentPanel.setVisible(aFlag);
    }

    private void setElement(SB_Polymorphism containingPoly,
            SB_Element element) {
        elementAndPoly = Optional.of(
                ElementAndPoly.of(element, containingPoly));
    }

    void populateExprFromElement(SB_Polymorphism containingPoly,
            SB_Element element, boolean readonly) {
        setElement(containingPoly, element);
        populateExpression(readonly);
    }

    private void populateExpression(boolean readonly) {
        elementAndPoly.ifPresent(hp -> {
            contentPanel.setVisible(true);

            //XXX: Naively copied from SB_ToolBar code
            autocompleteArea.setReturnsValue(
                    hp.element instanceof SB_Condition);
            autocompleteArea.clearNames();
            autocompleteArea.initializeNames();
            String expr = hp.element.getExpr();
            autocompleteArea.setText(expr);
            autocompleteArea.setCaretPosition(0);
            autocompleteArea.startAutoComplete();
        });
    }

    /**
     * 2018-05-25 -jmm
     * <br>
     * This method needs to be explicitly called when switching between two
     * SB_Element instances because the switch will not result in a focus
     * event.
     * */
    void updateSelectedElement() {
        elementAndPoly.ifPresent(hp -> {
            //For some reason, getText is assumed to return non-null during
            //normal operation by other parts of the code.
            String currentText = Objects.requireNonNull(
                    autocompleteArea.getText());
            String currentExpr = hp.element.getExpr();
            if (!currentText.equals(currentExpr)) {
                hp.poly.addToUndoStack();
                hp.element.setExpr(currentText);
                hp.poly.setModified(true);
                onEditListeners.forEach(r -> r.run());
            }
        });
    }

    void clearDisplayedExpr() {
        elementAndPoly = Optional.empty();
    }

    /**
     * Allow other objects to provide Runnable instances to be called when an
     * edit is written to the underlying SB_CommentHolder.
     */
    void registerTerminateEditingListener(Runnable r) {
        onEditListeners.add(r);
    }

}
