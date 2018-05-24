package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class SB_BindingsEditor
        extends AbstractBindingsEditor<SB_BindingsTable> {

    static class BindingsButtons extends AbstractBindingsEditor.Buttons {

        BindingsButtons(JButton addBindingButton, JButton deleteButton,
                JButton setValueButton, JButton moveUpButton,
                JButton moveDownButton) {
            super(addBindingButton, deleteButton, setValueButton, moveUpButton,
                    moveDownButton);
        }

        @Override
        List<JButton> getAllButtons() {
            return Arrays.asList(addBindingButton, deleteButton,
                    setValueButton,
                    moveUpButton, moveDownButton);
        }

        @Override
        Collection<JButton> addItemButtons() {
            return Arrays.asList(addBindingButton);
        }

        @Override
        JComponent genButtonComponent() {
            modifyAllButtons();
            disableAllButtons();
            return UIUtil.createButtonRow(getAllButtons());
        }

        /**
         * Resizing button on font is based on
         * https://stackoverflow.com/a/3485153
         * XXX: Assumptions:
         * <li> It is OK for all buttons to use the same font
         * */
        private void modifyAllButtons() {
            Font firstFont = addBindingButton.getFont();
            getAllButtons().forEach(button -> button.setFont(firstFont));

            //Give similar buttons the same button size
            AbstractBindingsEditor.setButtonsToSameSize(
                    Arrays.asList(addBindingButton, deleteButton),
                    firstFont);

            AbstractBindingsEditor.setButtonsToSameSize(
                    Arrays.asList(moveUpButton, moveDownButton),
                    firstFont);

            AbstractBindingsEditor.resizeButtonToText(setValueButton);

            //XXX: 2018-05 Temporarily disable Set Value until it is used
            setValueButton.setVisible(false);
        }
    }




    SB_BindingsEditor(SimBionicEditor editor) {
        super(new SB_BindingsTable(editor));
    }

    @Override
    BindingsButtons genButtons() {

        //Customized buttons

        JButton _insertButton = new JButton(new AbstractAction("+") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewBinding();
            }
        });

        JButton _deleteButton = new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });

        JButton _setValueButton = new JButton(new AbstractAction("Set Value") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSetValue();
            }
        });

        JButton _moveUpButton = new JButton(new AbstractAction("Up") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUp();
            }
        });

        JButton _moveDownButton = new JButton(new AbstractAction("Down") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDown();
            }
        });

        return new BindingsButtons(_insertButton, _deleteButton,
                _setValueButton,
                _moveUpButton, _moveDownButton);
    }

}
