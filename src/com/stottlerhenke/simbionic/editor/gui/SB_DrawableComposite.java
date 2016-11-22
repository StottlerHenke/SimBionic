
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;


public class SB_DrawableComposite extends SB_Drawable
{
    private static final long serialVersionUID = 2302585093L + 1001;
    public Vector _drawables = new Vector();

    public void draw(Graphics2D g2)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).draw(g2);
    }

    public void highlight(Graphics2D g2)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).highlight(g2);
    }

    public SB_Drawable containsPoint(Point point)
    {
        SB_Drawable drawable;
        int size = _drawables.size();
        for (int i = size - 1; i >= 0; --i)
        {
            drawable = (SB_Drawable) _drawables.get(i);
            drawable = drawable.containsPoint(point);
            if (drawable != null) return drawable;
        }
        return null;
    }

    public String toString(){
        return _drawables.toString();
    }
    
    public boolean intersectsRect(Rectangle rect)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            if (((SB_Drawable) _drawables.get(i)).intersectsRect(rect))
                    return true;
        return false;
    }

    public void offset(int delta_x, int delta_y, boolean highlightOnly)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).offset(delta_x, delta_y, highlightOnly);
    }

    public Rectangle unionRect(Rectangle ur, boolean highlightOnly)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ur = ((SB_Drawable) _drawables.get(i)).unionRect(ur, highlightOnly);
        return ur;
    }

    public boolean isHighlighted()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            if (((SB_Drawable) _drawables.get(i)).isHighlighted())
                    return true;
        return false;
    }

    public void setHighlighted(boolean isHighlighted)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).setHighlighted(isHighlighted);
    }

    public boolean isPrehighlighted()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            if (((SB_Drawable) _drawables.get(i)).isPrehighlighted())
                    return true;
        return false;
    }

    public void setPrehighlighted(boolean isPrehighlighted)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).setPrehighlighted(isPrehighlighted);
    }

    public void updatePrehighlight()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).updatePrehighlight();
    }

    public void bringHighlightToFront()
    {
        SB_Drawable drawable;
        int n = 0;
        int size = _drawables.size();
        for (int i = 0; i < size - n; ++i)
        {
            drawable = (SB_Drawable) _drawables.get(i);
            if (drawable.isHighlighted())
            {
                ++n;
                _drawables.removeElementAt(i);
                --i;
                _drawables.add(drawable);
                drawable.bringHighlightToFront();
            }
        }
    }

    public void makeSelection(Rectangle rect)
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).makeSelection(rect);
    }

    public int count(Class c)
    {
      int result = 0;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
          result += ((SB_Drawable) _drawables.get(i)).count(c);
      return result;
    }

    public int count()
    {
      return count(null);
    }

    public int countHighlight()
    {
    	int result = 0;
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            result += ((SB_Drawable) _drawables.get(i)).countHighlight();
        return result;
    }
    
    public SB_Drawable findDrawable(int id, Class c)
    {
    	SB_Drawable drawable;
    	int size = _drawables.size();
        for (int i = 0; i < size; ++i)
        {
        	drawable = ((SB_Drawable) _drawables.get(i)).findDrawable(id, c);
        	if (drawable != null)
        		return drawable;
        }
        return null;
    }

    public void mapById(TreeMap map, Class c)
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
        ((SB_Drawable) _drawables.get(i)).mapById(map, c);
    }

    public void mapById(TreeMap map)
    {
      mapById(map, null);
    }

    public boolean exists(SB_Drawable drawable)
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        if (((SB_Drawable) _drawables.get(i)).exists(drawable))
          return true;
      }
      return false;
    }

    // returns first highlighted drawable
    public SB_Drawable firstHighlight()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
        {
            SB_Drawable next = ((SB_Drawable) _drawables.get(i)).singleHighlight();
            if (next != null) return next;
        }
        return null;
    }

    public SB_Drawable singleHighlight()
    {
        if (countHighlight() == 1)
            return firstHighlight();
	else
            return null;
    }

    public boolean isComposite() { return true; }

    public boolean add(SB_Drawable drawable)
    {
        _drawables.add(drawable);
        return true;
    }

    public List<SB_Drawable> removeHighlight()
    {
        List<SB_Drawable> removedList = new ArrayList<SB_Drawable>();
        SB_Drawable drawable;
        int n = 0;
        int size = _drawables.size();
        for (int i = 0; i < size - n; ++i)
        {
            drawable = (SB_Drawable) _drawables.get(i);
            if (drawable.isHighlighted())
            {
                if (drawable.isComposite())
                {
                   removedList.addAll(drawable.removeHighlight());
                    // note: does not erase when size 0
                }
                else
                {
                    drawable.removeHighlight();
                    ++n;
                    SB_Drawable drawableToRemove = (SB_Drawable)_drawables.get(i);
                    removedList.add(drawableToRemove);
                    _drawables.removeElementAt(i);
                    --i;
                }
            }
        }
        
        return removedList;
    }

    public void removeHighlightDependencies()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Drawable) _drawables.get(i)).removeHighlightDependencies();
    }

    // one deep
    public void write(ObjectOutputStream s, boolean highlightOnly)
    {
      _highlightOnly = highlightOnly;
      try
      {
        SB_Drawable drawable;
        int size = _drawables.size();
        if (_highlightOnly)
          s.writeInt(countHighlight());
        else
          s.writeInt(size);
        for (int i = 0; i < size; ++i)
        {
          drawable = (SB_Drawable) _drawables.get(i);
          if (!_highlightOnly || drawable.isHighlighted())
            s.writeObject(drawable);
        }
      }
      catch (IOException e)
      {
        System.err.println("i/o exception");
      }
      _highlightOnly = false;
    }

    // one deep
    public void read(ObjectInputStream s, boolean highlightOnly)
    {
      _highlightOnly = highlightOnly;
      if (_highlightOnly)
        setHighlighted(false);
      try
      {
        SB_Drawable drawable;
        int size = s.readInt();
        for (int i = 0; i < size; ++i)
        {
          drawable = (SB_Drawable) s.readObject();
          add(drawable);
          if (_highlightOnly)
            drawable.setHighlighted(true);
        }
      }
      catch (IOException e)
      {
        System.err.println("i/o exception");
      }
      catch (ClassNotFoundException e)
      {
        System.err.println("class not found");
        e.printStackTrace();
      }
      _highlightOnly = false;
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      //super.writeExternal(out);
      out.writeObject(_drawables);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      //super.readExternal(in);
      _drawables = (Vector) in.readObject();
    }
}
