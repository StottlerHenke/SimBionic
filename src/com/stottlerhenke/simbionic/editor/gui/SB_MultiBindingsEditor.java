package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;

import com.stottlerhenke.simbionic.editor.SimBionicEditor;

public class SB_MultiBindingsEditor
        extends AbstractBindingsEditor<SB_MultiBindingsTable> {

    static class MultiBindingsButtons extends AbstractBindingsEditor.Buttons {

        final JButton addActionButton;

        MultiBindingsButtons(JButton addActionButton,
                JButton addBindingButton, JButton deleteButton,
                JButton setValueButton,
                JButton moveUpButton, JButton moveDownButton) {
            super(addBindingButton, deleteButton, setValueButton, moveUpButton,
                    moveDownButton);
            this.addActionButton = addActionButton;
        }

        @Override
        List<JButton> getAllButtons() {
            return Arrays.asList(addActionButton, addBindingButton,
                    deleteButton, setValueButton,
                    moveUpButton, moveDownButton);
        }

        @Override
        Collection<JButton> addItemButtons() {
            return Arrays.asList(addActionButton, addBindingButton);
        }

        @Override
        JComponent genButtonComponent() {
            // TODO Auto-generated method stub
            modifyAllButtons();
            disableAllButtons();
            return AbstractBindingsEditor.genButtonRow(getAllButtons());
        }

        /**
         * Resizing button on font is based on
         * https://stackoverflow.com/a/3485153
         * XXX: Assumptions:
         * <li> It is OK for all buttons to use the same font
         * */
        private void modifyAllButtons() {
            Font firstFont = addActionButton.getFont();
            getAllButtons().forEach(button -> button.setFont(firstFont));

            //Give similar buttons the same button size
            AbstractBindingsEditor.setButtonsToSameSize(
                    Arrays.asList(addActionButton, addBindingButton),
                    firstFont);

            AbstractBindingsEditor.resizeButtonToText(deleteButton);

            AbstractBindingsEditor.setButtonsToSameSize(
                    Arrays.asList(moveUpButton, moveDownButton),
                    firstFont);

            AbstractBindingsEditor.resizeButtonToText(setValueButton);

            //XXX: 2018-05 Temporarily disable Set Value until it is used
            setValueButton.setVisible(false);
        }

    }

    SB_MultiBindingsEditor(SimBionicEditor editor) {
        super(new SB_MultiBindingsTable(editor));
    }

    @Override
    Buttons genButtons() {
        //Customized buttons

        JButton insertActionButton 
        = new JButton(new AbstractAction("+Action") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewAction();
            }
        });

        JButton insertBindingButton
                = new JButton(new AbstractAction("+Binding") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onNewBinding();
                    }
                });

        JButton deleteButton = new JButton(new AbstractAction("-") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });

        JButton setValueButton = new JButton(new AbstractAction("Set Value") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSetValue();
            }
        });

        JButton moveUpButton = new JButton(new AbstractAction("Up") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUp();
            }
        });

        JButton moveDownButton = new JButton(new AbstractAction("Down") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDown();
            }
        });

        return new MultiBindingsButtons(
                insertActionButton, insertBindingButton,
                deleteButton, setValueButton,
                moveUpButton, moveDownButton);
    }

    protected void onNewAction() {
        bindingsTable.insertAction();
    }

}
