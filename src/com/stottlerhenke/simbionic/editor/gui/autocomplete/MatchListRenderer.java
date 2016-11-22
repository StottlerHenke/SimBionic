package com.stottlerhenke.simbionic.editor.gui.autocomplete;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings({ "serial", "rawtypes" })
public class MatchListRenderer extends JLabel implements ListCellRenderer {
    
    public MatchListRenderer() {
       setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
       String str = value.toString();
       setText("<html>" + str + "</html>" );
       return this;
    }
    
  }
