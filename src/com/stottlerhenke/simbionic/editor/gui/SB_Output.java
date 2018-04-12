package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SB_Constant;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

public class SB_Output extends JPanel implements ActionListener
{
    protected SimBionicEditor _editor;

    protected JPopupMenu _outputPopup;
    protected JMenuItem _clearItem;
    protected boolean _lastButton1 = false;

    protected int _width = 750;

    final static int _font_point = 12;
    final static Font _font = new Font("Courier New", Font.PLAIN, _font_point);
    final static Color _darkBlue = new Color(0, 0, 0x66);

    protected Vector _lines = new Vector();
    protected int _sel = -1;

    protected boolean _isBuild = false;
    protected int _firstError = -1;
    protected int _lastError = -1;

    public SB_Output(SimBionicEditor editor)
    {
        _editor = editor;

        _outputPopup = new JPopupMenu();
        _clearItem = new JMenuItem("Clear");
        _clearItem.addActionListener(this);
        _outputPopup.add(_clearItem);

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent event)
            {
                boolean button1 = (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0;
                if (event.getClickCount() == 2 && button1 && _lastButton1)
                {
                    setSel((event.getY() - 1) / (_font_point + 4));
                } else if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0)
                {
                    _outputPopup.show(SB_Output.this, event.getX(), event.getY());
                }
                _lastButton1 = button1;
            }
        });

        /*
         * addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) {
         * System.out.println("key pressed"); } });
         */

        setBackground(Color.white);
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(_font);
        SB_Line line;
        int size = _lines.size();
        for (int i = 0; i < size; ++i)
        {
            line = (SB_Line) _lines.get(i);
            if (i == _sel)
            {
                int width = Math.max(1500, _width);
                g2.setPaint(_darkBlue);
                g2.fillRect(0, i * (_font_point + 4) + 1, width, _font_point + 5);
            }
            if (i == _sel && line._color == Color.black)
                g2.setPaint(Color.white);
            else
                g2.setPaint(line._color);
            int y = (i + 1) * (_font_point + 4) - 2;
            g2.drawString(line._text, 5, y);
            if (line._index1 != -1)
            {
                g2.setPaint(Color.red);
                g2.drawString(line._text.substring(line._index1, line._index2),
                    5 + 7 * line._index1, y);
            }
        }
    }

    public void actionPerformed(ActionEvent event)
    {
        JMenuItem menuItem = (JMenuItem) event.getSource();
        if (menuItem == _clearItem)
        {
            clearLines();
            repaint();
        }
    }

    /**
     * @return the index of the SB_Polymorphism's data model in the list of Poly data models. Used to update the screen to show the selected behavior and polymorphism.
     */
    protected int findMatchingPoly(SB_Polymorphism poly, List<Poly> list) {
        return list.indexOf(poly.getDataModel());
    }

    public void setSel(int sel)
    {
        boolean valid = true;
        if (0 <= sel && sel < _lines.size())
        {
            SB_Line line = (SB_Line) _lines.get(sel);
            if (line._poly == null && line._drawable == null && line._data == null)
                valid = false;
            else
            {
                // may be something important i just commented here
                // if (_editor.getMultiEditor()!=null)
                // _editor.getMultiEditor().switchToEditor(_editor);
                _sel = sel;
                repaint();
                if (line._poly != null)
                {
                    // select poly
                    SB_Polymorphism poly = line._poly;
                    SB_Behavior behav = poly._parent;
                    if (ComponentRegistry.getProjectBar().getCatalog()
                            .findBehavior(behav.getName()) != null)
                    {
                        ComponentRegistry.getContent().setBehavior(behav, true);
                        int index = findMatchingPoly(poly, behav.getPolys());
                        if (index != -1)
                            ComponentRegistry.getContent().setSelectedIndex(index);
                        else
                            valid = false;

                    } else
                        valid = false; // behavior not found
                    if (line._drawable != null && valid)
                    {
                        // scroll to drawable
                        SB_Drawable drawable = line._drawable;
                        if (poly.exists(drawable))
                        {
                            SB_Canvas canvas = ComponentRegistry.getContent().getActiveCanvas();
                            poly.setHighlighted(false);
                            drawable.setHighlighted(true);
                            canvas.updateSingle();

                            canvas.scrollToDrawable(drawable);
                            // Rectangle rect;
                            // if (drawable instanceof SB_Element)
                            // {
                            // SB_Element element = (SB_Element) drawable;
                            // rect = element.getRect();
                            // }
                            // else
                            // {
                            // SB_Connector connector = (SB_Connector) drawable;
                            // rect = connector.getStartRect();
                            // rect = rect.union(connector.getStartRect());
                            // }
                            // Rectangle rectDrawable = new Rectangle(rect);
                            // Rectangle rectScrollPane =
                            // ComponentRegistry.getContent().getSelectedComponent().getBounds();
                            // int h = Math.max((rectScrollPane.width -
                            // rectDrawable.width)/2, 0);
                            // int v = Math.max((rectScrollPane.height -
                            // rectDrawable.height)/2, 0);
                            // rectDrawable.grow(h, v);
                            // canvas.scrollRectToVisible(rectDrawable);
                            // canvas.repaint();
                            if (line._data instanceof SB_Binding)
                            {
                                // select binding
                                SB_Binding binding = (SB_Binding) line._data;
                                Vector bindings = ((SB_BindingsHolder) drawable).getBindings();
                                int index = bindings.indexOf(binding);
                                if (index != -1)
                                {
                                    ComponentRegistry.getToolBar()._varComboBox
                                            .setSelectedIndex(index);
                                    SB_Autocomplete bindingField = ComponentRegistry.getToolBar()._bindingField;
                                    bindingField.setText(binding.getExpr());
                                    bindingField.requestFocus();
                                    bindingField._glassPane.setVisible(false);
                                } else
                                    valid = false; // binding not found
                            }
                        } else
                            valid = false; // drawable not found
                    }
                    if (line._data instanceof SB_Descriptors)
                    {
                        // select descriptors
                        ComponentRegistry.getProjectBar().setSelectedIndex(1);
                    }
                } else if (line._data instanceof SB_Behavior)
                {
                    // select behavior
                    SB_Behavior behav = (SB_Behavior) line._data;
                    if (ComponentRegistry.getProjectBar().getCatalog()
                            .findBehavior(behav.getName()) != null)
                        ComponentRegistry.getContent().setBehavior(behav, true);
                    else
                        valid = false; // behavior not found
                } else if (line._data instanceof SB_Global)
                {
                    // select global
                    SB_Global global = (SB_Global) line._data;
                    ComponentRegistry.getProjectBar().setSelectedIndex(0);
                    valid = ComponentRegistry.getProjectBar().getCatalog().selectVariable(global);
                } else if (line._data instanceof SB_Constant)
                {
                    // select constant
                    SB_Constant constant = (SB_Constant) line._data;
                    ComponentRegistry.getProjectBar().setSelectedIndex(0);
                    valid = ComponentRegistry.getProjectBar().getCatalog().selectVariable(constant);
                }
            }
        } else if (sel == -1)
        {
            _sel = -1;
            repaint();
        } else
            valid = false;

        if (!valid)
            Toolkit.getDefaultToolkit().beep();

        updateMenuItems();
    }

    public void addLine(SB_Line line)
    {
        _lines.add(line);
        int size = _lines.size();
        int width = 10 + 7 * line._text.length();
        if (width > _width)
            _width = width;
        setPreferredSize(new Dimension(_width, size * (_font_point + 4) + 6));
        revalidate();
        scrollRectToVisible(new Rectangle(0, size * (_font_point + 4) + 6, 0, 0));
        repaint();
    }

    public void addFindLine(SB_Polymorphism poly, SB_Drawable drawable, SB_Binding binding,
            int index1, int index2)
    {
        String text = poly._parent.getName() + ": " + poly.getIndicesLabel() + ": ";
        int length = text.length();
        if (binding != null)
            text += binding.getVar() + " = " + binding.getExpr();
        else
        {
            SB_Element element = (SB_Element) drawable;
            text += element.getExpr();
        }
        addLine(new SB_Line(text, Color.black, poly, drawable, binding, length + index1, length
                + index2));
    }

    public int getLineCount()
    {
        return _lines.size();
    }

    public void clearLines()
    {
        _lines.removeAllElements();
        _sel = -1;
        _width = 750;
        setPreferredSize(new Dimension(_width, _font_point));
        revalidate();

        updateFirstLastErrors();
        updateMenuItems();
    }

    public boolean isError(int index)
    {
        SB_Line line = (SB_Line) _lines.get(index);
        if (line._poly == null && line._drawable == null && line._data == null)
            return false;
        else
            return line._color.equals(Color.red);
    }

    public int getPrevError()
    {
        if (!_isBuild)
            return -1;
        if (_sel <= _firstError)
            return -1;
        for (int i = _sel - 1; _sel >= 0; --i)
        {
            if (isError(i))
                return i;
        }
        return -1;
    }

    public int getNextError()
    {
        if (!_isBuild)
            return -1;
        if (_sel >= _lastError)
            return -1;
        int size = _lines.size();
        for (int i = _sel + 1; _sel < size; ++i)
        {
            if (isError(i))
                return i;
        }
        return -1;
    }

    public void scrollToBottom()
    {
        this.scrollRectToVisible(new Rectangle(0, this.getHeight() - 11, 10, 10));
    }

    public void scrollToSel()
    {
        if (_sel != -1)
        {
            scrollRectToVisible(new Rectangle(0, _sel * (_font_point + 4) + 1, 0, _font_point + 9));
            repaint();
            requestFocus();
        }
    }

    public void updateFirstLastErrors()
    {
        if (_isBuild)
        {
            _firstError = -1;
            _lastError = -1;
        }
        SB_Line line;
        int size = _lines.size();
        for (int i = 0; i < size; ++i)
        {
            if (isError(i))
            {
                if (_firstError == -1)
                    _firstError = i;
                _lastError = i;
            }
        }
    }

    public void updateMenuItems()
    {
        if (_isBuild)
        {
            _editor.prevErrorAction.setEnabled(_sel > _firstError);
            _editor.nextErrorAction.setEnabled(_sel < _lastError);
        }
    }

    /**
     * @return Returns the lines.
     */
    public Vector getLines()
    {
        return _lines;
    }

    /**
     * Sets the specified line to be selected.
     * @param line
     */
    public void setSelected(SB_Line line)
    {
        for (int i=0; i<_lines.size(); ++i)
        {
            if (_lines.get(i).equals(line))
            {
                setSel(i);
                return;
            }
        }
    }
}
