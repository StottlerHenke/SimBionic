
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * SB_Button
 */
public class SB_Button extends JButton {

    //private Border _raisedBevelBorder;
    static private Point _toolTipLocation = new Point(10, -19);

    public SB_Button(Action action) {
        super(action);
        setText(null);
        //_raisedBevelBorder = BorderFactory.createRaisedBevelBorder();
        //setBorder(_raisedBevelBorder);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
    }

    public Point getToolTipLocation(MouseEvent event) {
        return _toolTipLocation;
    }
}