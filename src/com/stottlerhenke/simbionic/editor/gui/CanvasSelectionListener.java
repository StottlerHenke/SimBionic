package com.stottlerhenke.simbionic.editor.gui;

/**
 * 2018-05 -jmm
 * <br>
 * New class added in preparation for future restructuring of SB_Canvas.
 * */
public interface CanvasSelectionListener {

    /**
     * @param currentPoly The SB_Polymorphism object that contains {@code
     * newSelection}
     * @param oldSelection The previously-selected item. {@code null} indicates
     * no selection.
     * @param newSelection The new selected SB_Drawable; expected to be
     * distinct from oldSelection by callers. {@code null} indicates no
     * selection.
     * */
    void selectionChanged(SB_Polymorphism currentPoly,
            SB_Drawable oldSelection, SB_Drawable newSelection);
}
