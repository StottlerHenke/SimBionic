package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.stottlerhenke.simbionic.editor.SB_Binding;

/**
 * This class is an attempt to implement functionality used by both
 * SB_BindingPanel and SB_MultiBindingsEditor without introducing a subclassing
 * relationship between the two classes.
 * */
abstract class AbstractBindingsEditor<T extends SB_BindingsTable> {

    /**
     * Horizontal padding for button sizes (padding w.r.t. contained text)
     * 2018-05-22 -jmm
     * For unknown reasons, this is the smallest viable size.
     * */
    private static int ADDITIONAL_WIDTH = 40;
    /**
     * Vertical padding for button sizes (padding w.r.t. contained text)
     * */
    private static int ADDITIONAL_HEIGHT = 10;

    private static int BUTTON_SPACING = 1;

    /**
     * A convenience class used to hold both a SB_BindingsHolder instance and
     * the SB_Polymorphism instance containing it.
     * <br>
     * XXX: it is the caller's responsibility to ensure that poly is the actual
     * containing polymorphism of holder.
     * */
    private static class HolderAndPoly {
        final SB_BindingsHolder holder;
        final SB_Polymorphism poly;

        HolderAndPoly(SB_BindingsHolder holder,
                SB_Polymorphism poly) {
            this.holder = Objects.requireNonNull(holder);
            this.poly = Objects.requireNonNull(poly);
        }

        static HolderAndPoly of(SB_BindingsHolder holder,
                SB_Polymorphism poly) {
            return new HolderAndPoly(holder, poly);
        }
    }

    /**
     * This class in an attempt to factor out button differences between
     * SB_BindingsPanel and SB_MutliBindingsPanel.
     * */
    protected static abstract class Buttons {

        protected final JButton addBindingButton;
        final JButton deleteButton;
        final JButton setValueButton;
        final JButton moveUpButton;
        final JButton moveDownButton;

        Buttons(JButton addBindingButton, JButton deleteButton,
                JButton setValueButton, JButton moveUpButton,
                JButton moveDownButton) {
            this.addBindingButton = addBindingButton;
            this.deleteButton = deleteButton;
            this.setValueButton = setValueButton;
            this.moveUpButton = moveUpButton;
            this.moveDownButton = moveDownButton;
        }

        abstract List<JButton> getAllButtons();

        abstract Collection<JButton> addItemButtons();

        /**
         * Generates a Swing JComponent containing all buttons.
         * */
        abstract JComponent genButtonComponent();

        private void updateButtons(boolean readOnly, boolean setValueEnabled,
                int row, int size) {

            boolean enableButtons = !readOnly;

            addItemButtons().forEach(button -> button
                    .setEnabled(enableButtons));

            deleteButton.setEnabled(enableButtons && row >= 0);

            moveUpButton.setEnabled(enableButtons && row > 0);
            moveDownButton.setEnabled(enableButtons
                    && (size > 0 && row != size - 1 && row != -1));

            setValueButton.setEnabled(enableButtons
                    && setValueEnabled);
        }

        void disableAllButtons() {
            getAllButtons().forEach(button -> button.setEnabled(false));
        }
 
    }

    /**
     * Resizes all buttons in the list to share the size of the largest button
     * after resizing buttons to fit their containing text.
     * 
     * @param buttons A list of JButtons with the same font
     */
    static void setButtonsToSameSize(List<JButton> buttons, Font sharedFont) {

        Optional<Dimension> maxDimension = buttons.stream().map(button -> {
            FontMetrics metrics = button.getFontMetrics(sharedFont);
            int width = metrics.stringWidth(button.getText());
            int height = metrics.getHeight();
            return new Dimension(width + ADDITIONAL_WIDTH,
                    height + ADDITIONAL_HEIGHT);
        }).reduce((dim1, dim2) -> {
            int maxHeight = Math.max(dim1.height, dim2.height);
            int maxWidth = Math.max(dim1.width, dim2.width);
            return new Dimension(maxWidth, maxHeight);
        });

        maxDimension.ifPresent(newDim -> {
            buttons.forEach(button -> {
                button.setPreferredSize(newDim);
            });
        });
    }

    static void resizeButtonToText(JButton button) {
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int width = metrics.stringWidth(button.getText());
        int height = metrics.getHeight();
        Dimension newDim = new Dimension(width + ADDITIONAL_WIDTH,
                height + ADDITIONAL_HEIGHT);
        button.setPreferredSize(newDim);
    }

    static JComponent genButtonRow(List<JButton> buttons) {
        Box row = new Box(BoxLayout.X_AXIS);
        row.setBorder(BorderFactory.createEmptyBorder(
                UIUtil.DEFAULT_BORDER,0,UIUtil.DEFAULT_BORDER,0));

        Iterator<JButton> buttonIt = buttons.iterator();
        while (buttonIt.hasNext())
        {
            row.add((JComponent)buttonIt.next());
            if (buttonIt.hasNext()) {
                row.add(Box.createRigidArea(new Dimension(
                        BUTTON_SPACING,0)));
            }
        }
        return row;
    }

    final T bindingsTable;

    /**
     * XXX: members are not added or accessed in a threadsafe manner.
     * */
    private final List<Runnable> onEditListeners = new ArrayList<>();

    /**
     * XXX: Potential race conditions
     * */
    private Optional<HolderAndPoly> holderAndPoly = Optional.empty();

    private final JPanel contentPanel = new JPanel(new BorderLayout());

    private final Buttons buttons;

    AbstractBindingsEditor(T bindingsTable) {
        this.bindingsTable = bindingsTable;
        this.buttons = genButtons();

        JScrollPane scrollPane = new JScrollPane(bindingsTable);

        addListeners();

        JComponent buttonComponent = buttons.genButtonComponent();

        Dimension buttonDim = buttonComponent.getMinimumSize();
        int buttonsMinWidth = buttonDim.width;

        Dimension newScrollDim = new Dimension(
                buttonsMinWidth, 150);

        scrollPane.setPreferredSize(newScrollDim);

        Dimension newPanelDim = new Dimension(buttonsMinWidth,
                buttonDim.height + newScrollDim.height);


        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonComponent, BorderLayout.SOUTH);
        contentPanel.setPreferredSize(newPanelDim);
    }

    abstract Buttons genButtons();

    private void addListeners() {

        bindingsTable.addListenerToVarCellEditor(new CellEditorListener() {
            public void editingCanceled(ChangeEvent e) {}

            public void editingStopped(ChangeEvent e) {
                buttons.setValueButton
                        .setEnabled(bindingsTable.enableSetValueButton());
            }
        });

        //This specific listener means that every modification is
        //written through to the underlying BindingsHolder object.
        //(OK button is almost redundant; Cancel button no longer does what
        //it is supposed to do.)
        bindingsTable.addListenerForTableModel(e -> {
            updateSelectedBindingsHolder();
        });

        bindingsTable.addListenerToSelectionModel(event -> {
            // Ignore extra messages.
            if (event.getValueIsAdjusting()) {
                return;
            } else {
                //XXX: Make sure selection cannot happen in debug mode
                updateButtons();
            }
        });
    }

    /**
     * 
     * @return The JPanel managed by this instance.
     * */
    JPanel getContentPanel() {
        return this.contentPanel;
    }

    /**
     * Sets the visibility of the panel managed by this instance.
     * */
    void setVisible(boolean aFlag) {
        this.contentPanel.setVisible(aFlag);
    }


    private void updateSelectedBindingsHolder() {
        holderAndPoly.ifPresent(hp -> {
            List<SB_Binding> bindingsCopy = bindingsTable.getBindingsCopy();
            if (!SB_ToolBar.equalBindings(hp.holder.getBindings(),
                    bindingsCopy)) {
                hp.poly.addToUndoStack();
                hp.holder.setBindings(bindingsCopy);
                hp.poly.setModified(true);
                onEditListeners.forEach(r -> r.run());
            }
        });
    }

    private void setBindingsHolder(SB_Polymorphism containingPoly,
            SB_BindingsHolder holder) {
        holderAndPoly = Optional.of(HolderAndPoly.of(holder, containingPoly));
    }

    void populateBindingsFromHolder(SB_Polymorphism containingPoly,
            SB_BindingsHolder holder, boolean debug) {
        setBindingsHolder(containingPoly, holder);
        populateBindings(debug);
    }

    private void populateBindings(boolean debugMode) {
        holderAndPoly.ifPresent(hp -> {
            contentPanel.setVisible(true);

            bindingsTable.setBindings(hp.poly, hp.holder.getBindings());
            updateButtons(debugMode);
            bindingsTable.setEnabled(!debugMode);

        });
    }

    void clearBindings() {
        bindingsTable.clearBindings();
        holderAndPoly = Optional.empty();
        disableAllButtons();
    }

    private void disableAllButtons() {
        buttons.disableAllButtons();
    }

    /**
     * Allow other objects to provide Runnable instances to be called when an
     * edit is written to the underlying SB_BindingsHolder.
     */
    void registerTerminateEditingListener(Runnable r) {
        onEditListeners.add(r);
    }


    protected void updateButtons() {
        updateButtons(false);
    }

    /**
     * XXX: Copied from SB_MultiDialog
     * Update the delete/up/down buttons depending on the 
     * current selection.
     *
     */
    private void updateButtons(boolean readOnly) {
        int row = bindingsTable.getSelectedRow();
        int size = bindingsTable.getRowCount();
        boolean setValueEnabled = bindingsTable.enableSetValueButton();
        buttons.updateButtons(readOnly, setValueEnabled, row, size);
    }

    //Hooks for Swing Action instances

    protected void onNewBinding() {
        bindingsTable.insertBinding();
    }

    protected void onSetValue() {
        bindingsTable.setVarValue();
    }

    protected void onDelete() {
        bindingsTable.deleteBinding();
        updateButtons();
    }

    protected void onUp() {
        bindingsTable.moveUp();
    }

    protected void onDown() {
        bindingsTable.moveDown();
    }

}
