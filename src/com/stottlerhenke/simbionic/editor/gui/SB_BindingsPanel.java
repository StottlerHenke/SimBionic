package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * This class is an attempt to factor out UI handling for SB_BindingsTable into
 * a JPanel (instead of a standalone dialog.)
 * @see SB_MultiBindingsTable
 * @see SB_MultiDialog
 * */
@SuppressWarnings("serial")
public class SB_BindingsPanel extends JPanel {

    /**
     * A convenience class used to hold both a SB_BindingsHolder instance and
     * the SB_Polymorphism instance containing it.
     * <br>
     * XXX: it is the caller's responsibility to ensure that poly is the actual
     * contining polymorphism of holder.
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
     * Horizontal padding for button sizes (padding w.r.t. contained text)
     * 2018-05-22 -jmm
     * For unknown reasons, this is the smallest viable size.
     * */
    private static int ADDITIONAL_WIDTH = 40;

    /**
     * Vertical padding for button sizes (padding w.r.t. contained text)
     * */
    private static int ADDITIONAL_HEIGHT = 10;

    //Items possibly obtained from component registry
    private final SB_TabbedCanvas tabbedCanvas;

    private final SB_BindingsTable bindingsTable;

    //Actions for buttons

    private final JButton _insertButton
            = new JButton(new AbstractAction("+") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onNewBinding();
                }
            });

    private final JButton _deleteButton
            = new JButton(new AbstractAction("-") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onDelete();
                }
            });

    private final JButton _setValueButton
            = new JButton(new AbstractAction("Set Value") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onSetValue();
                }
            });

    private final JButton _moveUpButton
            = new JButton(new AbstractAction("Up") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onUp();
                }
            });

    private final JButton _moveDownButton
            = new JButton(new AbstractAction("Down") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onDown();
                }
            });

    private final JButton okButton = new JButton(new AbstractAction("OK") {
        @Override
        public void actionPerformed(ActionEvent e) {
            onOk();
        }
    });

    private final JButton cancelButton
            = new JButton(new AbstractAction("Cancel") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            });

    private final List<JButton> buttonList = Arrays.asList(
            _insertButton, _deleteButton,
            _setValueButton,
            _moveUpButton, _moveDownButton,
            okButton, cancelButton);

    /**
     * XXX: members are not added or accessed in a threadsafe manner.
     * */
    private final List<Runnable> onOkListeners = new ArrayList<>();

    /**
     * XXX: members are not added or accessed in a threadsafe manner.
     * */
    private final List<Runnable> onCancelListeners = new ArrayList<>();

    /**
     * XXX: Potential race conditions
     * */
    private Optional<HolderAndPoly> holderAndPoly = Optional.empty();

    SB_BindingsPanel(SimBionicEditor editor, SB_TabbedCanvas tabbedCanvas) {
        this(editor, tabbedCanvas, false);
    }

    SB_BindingsPanel(SimBionicEditor editor, SB_TabbedCanvas tabbedCanvas,
            boolean useBottomButtonBar) {
        super(new BorderLayout());

        this.tabbedCanvas = tabbedCanvas;

        bindingsTable = new SB_BindingsTable(editor);
        JScrollPane scrollPane = new JScrollPane(bindingsTable);
        scrollPane.setPreferredSize(new Dimension(200, 175));

        addListeners();

        this.add(scrollPane, BorderLayout.CENTER);
        this.modifyAllButtons();
        if (useBottomButtonBar) {
            JComponent buttonComponent = genGenericBottomBar();
            this.add(buttonComponent, BorderLayout.SOUTH);
            //XXX: This exact size was chosen earlier to accomodate the bottom
            //button bar.
            scrollPane.setPreferredSize(new Dimension(525, 175));
        } else {
            JComponent buttonComponent = genNEPBottomBar();
            this.add(buttonComponent, BorderLayout.SOUTH);
        }


    }

    private void addListeners() {

        bindingsTable.addListenerToVarCellEditor(new CellEditorListener() {
            public void editingCanceled(ChangeEvent e) {}

            public void editingStopped(ChangeEvent e) {
                _setValueButton
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
     * Resizing button on font is based on https://stackoverflow.com/a/3485153
     * XXX: Assumptions:
     * <li> It is OK for all buttons to use the same font
     * */
    private void modifyAllButtons() {
        Font firstFont = _insertButton.getFont();
        buttonList.forEach(button -> button.setFont(firstFont));

        //Give similar buttons the same button size
        setButtonsToSameSize(Arrays.asList(_insertButton, _deleteButton),
                firstFont);

        setButtonsToSameSize(Arrays.asList(_moveUpButton, _moveDownButton),
                firstFont);

        resizeButtonToText(_setValueButton);

        //XXX: 2018-05 Temporarily disable Set Value until it is used
        _setValueButton.setVisible(false);
    }

    /**
     * Resizes all buttons in the list to share the size of the largest button
     * after resizing buttons to fit their containing text.
     * @param buttons A list of JButtons with the same font
     * */
    private static void setButtonsToSameSize(List<JButton> buttons,
            Font sharedFont) {

        Optional<Dimension> maxDimension = buttons.stream()
            .map(button -> {
                FontMetrics metrics = button.getFontMetrics(sharedFont);
                int width = metrics.stringWidth(button.getText());
                int height = metrics.getHeight();
                return new Dimension(width + ADDITIONAL_WIDTH,
                        height + ADDITIONAL_HEIGHT);
            })
            .reduce((dim1, dim2) -> {
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

    private static void resizeButtonToText(JButton button) {
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int width = metrics.stringWidth(button.getText());
        int height = metrics.getHeight();
        Dimension newDim = new Dimension(width + ADDITIONAL_WIDTH,
                height + ADDITIONAL_HEIGHT);
        button.setPreferredSize(newDim);
    }

    private JComponent genButtonSideBar() {

        disableAllButtons();

        return UIUtil.createButtonColumn(buttonList);
    }

    private JComponent genGenericBottomBar() {
        disableAllButtons();

        return UIUtil.createButtonRow(buttonList);
    }

    private JComponent genNEPBottomBar() {
        disableAllButtons();

        return UIUtil.createButtonRow(Arrays.asList(
                _insertButton, _deleteButton,
                _setValueButton,
                _moveUpButton, _moveDownButton));
    }

    /**
     * XXX: Copied from SB_MultiDialog
     * Stop any current editing
     *
     */
    protected void stopEditing()
    {
        TableCellEditor cellEditor = bindingsTable.getCellEditor();
        if (cellEditor != null)
            cellEditor.stopCellEditing();
    }

    private void updateSelectedBindingsHolder() {
        holderAndPoly.ifPresent(hp -> {
            List<SB_Binding> bindingsCopy = bindingsTable.getBindingsCopy();
            if (!SB_ToolBar.equalBindings(hp.holder.getBindings(),
                    bindingsCopy)) {
                hp.poly.addToUndoStack();
                hp.holder.setBindings(bindingsCopy);
                hp.poly.setModified(true);
            }
        });
    }

    private void setBindingsHolder(SB_Polymorphism containingPoly,
            SB_BindingsHolder holder) {
        holderAndPoly = Optional.of(HolderAndPoly.of(holder, containingPoly));
    }

    void populateBindingsFromHolder(SB_Polymorphism containingPoly,
            SB_BindingsHolder holder, boolean insert, boolean debug) {
        setBindingsHolder(containingPoly, holder);
        populateBindings(insert, debug);
    }

    private void populateBindings(boolean insert, boolean debugMode) {
        holderAndPoly.ifPresent(hp -> {
            this.setVisible(true);

            bindingsTable.setBindings(hp.poly, hp.holder.getBindings(),
                    insert);
            updateButtons(debugMode);
            bindingsTable.setEnabled(!debugMode);
            okButton.requestFocus();

        });
    }

    void clearBindings() {
        bindingsTable.clearBindings();
        holderAndPoly = Optional.empty();
        disableAllButtons();
    }

    private void disableAllButtons() {
        buttonList.forEach(button -> button.setEnabled(false));
    }

    void registerCancelListener(Runnable r) {
        onCancelListeners.add(Objects.requireNonNull(r));
    }

    void registerOkListener(Runnable r) {
        onOkListeners.add(Objects.requireNonNull(r));
    }

    /**
     * XXX: Allow other objects to provide Runnable instances to be called when
     * either Ok or Cancel is pressed.
     * */
    void registerTerminateEditingListener(Runnable r) {
        onOkListeners.add(r);
        onCancelListeners.add(r);
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
    private void updateButtons(boolean debugMode) {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);

        boolean enableButtons = !debugMode;
        int row = bindingsTable.getSelectedRow();
        int size = bindingsTable.getRowCount();

        _insertButton.setEnabled(enableButtons);

        _deleteButton.setEnabled(enableButtons && row >= 0);

        _moveUpButton.setEnabled(enableButtons && row > 0);
        _moveDownButton.setEnabled(enableButtons
                && (size > 0 && row != size - 1 && row != -1));

        _setValueButton.setEnabled(enableButtons
                && bindingsTable.enableSetValueButton());
    }

    //Hooks for Swing Action instances

    private void onNewBinding() {
        bindingsTable.insertBinding();
    }

    private void onSetValue() {
        bindingsTable.setVarValue();
    }

    private void onDelete() {
        bindingsTable.deleteBinding();
        updateButtons();
    }

    private void onUp() {
        bindingsTable.moveUp();
    }

    private void onDown() {
        bindingsTable.moveDown();
    }

    /**
     * Used for explicit cancel and handling "focus" loss.
     * */
    void onCancel() {
        stopEditing();
        //XXX: Should probably repopulate the bindings dialog
        //Setting debug to true means disabling editing.
        populateBindings(false, true);
        disableAllButtons();
        onCancelListeners.forEach(runnable -> runnable.run());
    }

    private void onOk() {
        stopEditing();
        updateSelectedBindingsHolder();
        onOkListeners.forEach(runnable -> runnable.run());
    }

}
