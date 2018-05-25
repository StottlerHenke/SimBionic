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
import javax.swing.JTextArea;

class SB_CommentEditor {

    /**
     * A convenience class used to hold both a SB_BindingsHolder instance and
     * the SB_Polymorphism instance containing it.
     * <br>
     * XXX: it is the caller's responsibility to ensure that poly is the actual
     * containing polymorphism of holder.
     * */
    private static class HolderAndPoly {
        final SB_CommentHolder holder;
        final SB_Polymorphism poly;

        HolderAndPoly(SB_CommentHolder holder,
                SB_Polymorphism poly) {
            this.holder = Objects.requireNonNull(holder);
            this.poly = Objects.requireNonNull(poly);
        }

        static HolderAndPoly of(SB_CommentHolder holder,
                SB_Polymorphism poly) {
            return new HolderAndPoly(holder, poly);
        }
    }

    private final JPanel contentPanel;

    private final JTextArea textArea;

    private Optional<HolderAndPoly> holderAndPoly = Optional.empty();

    /**
     * XXX: members are not added or accessed in a threadsafe manner.
     * */
    private final List<Runnable> onEditListeners = new ArrayList<>();

    SB_CommentEditor() {
        textArea = new JTextArea(3,20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(textArea);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scroll, BorderLayout.CENTER);
        addListeners();
    }

    private void addListeners() {
        //XXX: only update when focus lost to avoid update on every keystroke.
        textArea.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateSelectedCommentHolder();
            }
            
        });
    }

    JPanel getContentPanel() {
        return contentPanel;
    }

    void setVisible(boolean aFlag) {
        contentPanel.setVisible(aFlag);
    }

    private void setBindingsHolder(SB_Polymorphism containingPoly,
            SB_CommentHolder holder) {
        holderAndPoly = Optional.of(HolderAndPoly.of(holder, containingPoly));
    }

    void populateCommentFromHolder(SB_Polymorphism containingPoly,
            SB_CommentHolder holder, boolean readonly) {
        setBindingsHolder(containingPoly, holder);
        populateComment(readonly);
    }

    private void populateComment(boolean readonly) {
        holderAndPoly.ifPresent(hp -> {
            contentPanel.setVisible(true);
            textArea.setText(hp.holder.getComment());
            textArea.setEditable(!readonly);

        });
    }

    private void updateSelectedCommentHolder() {
        holderAndPoly.ifPresent(hp -> {
            //For some reason, getText is assumed to return non-null during
            //normal operation by other parts of the code.
            String currentText = Objects.requireNonNull(textArea.getText());
            //XXX: The comment in the commentHolder might be null.
            String currentComment = hp.holder.getComment();
            if (!currentText.equals(currentComment)) {
                hp.poly.addToUndoStack();
                hp.holder.setComment(currentText);
                hp.holder.updateComment();
                hp.poly.setModified(true);
                onEditListeners.forEach(r -> r.run());
            }
        });
    }

    void clearDisplayedComment() {
        holderAndPoly = Optional.empty();
        textArea.setText("");
    }

    /**
     * Allow other objects to provide Runnable instances to be called when an
     * edit is written to the underlying SB_CommentHolder.
     */
    void registerTerminateEditingListener(Runnable r) {
        onEditListeners.add(r);
    }
}
