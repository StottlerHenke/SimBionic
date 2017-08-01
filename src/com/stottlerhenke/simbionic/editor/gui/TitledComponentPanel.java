package com.stottlerhenke.simbionic.editor.gui;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class TitledComponentPanel extends JPanel {
	public TitledComponentPanel(String title, JComponent component) {
    	this(new JLabel(title), component);
	}
	
	public TitledComponentPanel(JLabel titleLabel, JComponent component) {
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	titleLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    	add(titleLabel);
    	component.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    	add(component);
	}
}