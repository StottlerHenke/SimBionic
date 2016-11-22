package com.stottlerhenke.simbionic.editor.gui;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * CanvasMemento
 */
public class SB_CanvasMomento
{

    protected SB_CanvasSelection _selection = null;

    public SB_CanvasMomento(SB_Polymorphism poly)
    {
        _selection = new SB_CanvasSelection(poly, false);
    }

    protected void restore(SB_Canvas canvas)
    {
        canvas._poly.setHighlighted(true);
        canvas._poly.removeHighlight();
        try
        {
            ByteArrayOutputStream out = (ByteArrayOutputStream) _selection
                    .getTransferData(SB_CanvasSelection.getCanvasFlavor());
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream s = new ObjectInputStream(in);
            canvas._poly.read(s, false);
            canvas._poly.setModified(true);
        } catch (IOException e)
        {
            System.err.println("i/o exception " + e.getMessage());
        } catch (UnsupportedFlavorException e)
        {
            System.err.println("unsupported flavor exception");
        }
        canvas.updateSingle();
        canvas.repaint();
    }
}