package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.stottlerhenke.simbionic.editor.SB_Behavior;

/**
 * XXX: It may not be appropriate to extend JPanel if additional processing is
 * needed.
 * */
public class SB_BehaviorSummaryPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * XXX: Hacky way to get AWT default font for JLabel, which appears to be
     * the desired non-monospace serif font used by other non-editable text
     * components.
     * */
    private static final Font DEFAULT_FONT = new JLabel().getFont();

    /**
     * XXX: It is assumed that the name of the behavior should fit within a
     * single line
     * */
    private final JTextField nameLine;

    /**
     * XXX: Customized JTextArea used to display behavior descriptions
     * */
    private final JTextArea descriptionArea;

    /**
     * XXX: It might not be necessary to maintain this reference.
     * */
    private final JScrollPane descriptionScroll;

    SB_BehaviorSummaryPane() {
        nameLine = genNameTextField(20);

        descriptionArea = genConfiguredTextArea(2, 20);
        descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(null);

        //XXX: CENTER auto-resizes, but other parts of the border layout do
        //not.
        this.setLayout(new BorderLayout());
        this.add(nameLine, BorderLayout.NORTH);
        this.add(descriptionScroll, BorderLayout.CENTER);
        this.setPreferredSize(calcPreferredDimension());
        //Color.LIGHT_GRAY;
        this.setBorder(createLayeredBorder());

    }

    private Dimension calcPreferredDimension() {
        int preferredHeight = nameLine.getPreferredSize().height
                + descriptionScroll.getPreferredSize().height;
        int preferredWidth = nameLine.getPreferredSize().width;

        return new Dimension(preferredWidth, preferredHeight);
    }

    void setBehaviorName(String behaviorName) {
        nameLine.setText(behaviorName);
    }

    void setDescription(String description) {
        descriptionArea.setText(description);
        descriptionArea.setCaretPosition(0);
    }

    void setBehavior(SB_Behavior behavior) {
        if (behavior == null) {
            //TODO: determine if SB_TabbedCanvas#_behavior is ever null.
            nameLine.setText("");
            descriptionArea.setText("");
        } else {
            setBehaviorName(behavior.getName());
            setDescription(behavior.getDescription());
        }
    }

    /**
     * XXX: Fragile UI tweaking done to align the SB_BehaviorSummaryPane with
     * the SB_TabbedCanvas below it.
     * */
    private static CompoundBorder createLayeredBorder() {
        Border outerBorder = BorderFactory.createEmptyBorder(1, 2, 1, 3);
        Border innerBorder = BorderFactory.createLineBorder(Color.GRAY);
        return BorderFactory.createCompoundBorder(outerBorder, innerBorder);
    }

    private static JTextField genNameTextField(int length) {
        JTextField nameArea = new JTextField(length);
        nameArea.setEditable(false);
        //XXX: Hacky way to reverse grayout of JTextField to match
        //JTextArea.
        nameArea.setBackground(Color.WHITE);
        nameArea.setBorder(null);
        float newSize = (float) DEFAULT_FONT.getSize() * 2;
        Font newFont = DEFAULT_FONT.deriveFont(newSize);
        nameArea.setFont(newFont);
        return nameArea;
    }

    /**
     * Expectation: This gets wrapped with JScrollPane
     * */
    private static JTextArea genConfiguredTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(DEFAULT_FONT);
        textArea.setEditable(false);

        return textArea;
    }

}
