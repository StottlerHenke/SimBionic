package com.stottlerhenke.simbionic.editor.gui;

/**
 * 2018-05 -jmm
 * <br>
 * New class added in preparation for future restructuring of SB_Canvas.
 * */
public interface CanvasSelectionListener {
    void selectionChanged(SB_Polymorphism currentPoly,
            SB_Drawable oldSelection, SB_Drawable newSelection);
}
