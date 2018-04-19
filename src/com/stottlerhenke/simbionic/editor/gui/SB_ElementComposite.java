
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;

public class SB_ElementComposite extends SB_DrawableComposite
{
    private static final long serialVersionUID = 2302585093L + 1003;
    
    public boolean add(SB_Drawable drawable)
    {
        SB_Element element = (SB_Element) drawable;
        // set the id
        element.setId(count(element.getClass()));
        return super.add(element);
    }

    /**
     * @return The initial SB_Rectangle object, can be null.
     */
    public SB_Rectangle getInitial()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
        {
            if (_drawables.get(i) instanceof SB_Rectangle)
            {
                SB_Rectangle next = (SB_Rectangle) _drawables.get(i);
                if (next.isInitial()) {
                    return next;
                }
            }
        }
        
        return null;
    }

    /**
     * Set the specified SB_Rectangle as the initial element.  can be null.
     * @param rectangle initial element or null.
     * @return True if the rectangle is found in this composite, false otherwise.
     */
    protected boolean setInitial(SB_Rectangle rectangle)
    {
	boolean found = false;
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
        {
            if (_drawables.get(i) instanceof SB_Rectangle)
            {
                SB_Rectangle next = (SB_Rectangle) _drawables.get(i);
                next.setInitial(next == rectangle);
                if (next == rectangle)
                    found = true;
            }
	}
	return found;
    }

    protected boolean needToResize()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            if (((SB_Element) _drawables.get(i))._needToResize)
                return true;
        return false;
    }

    public void updateComplex(SimBionicEditor editor)
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
          if (_drawables.get(i) instanceof SB_Rectangle)
          {
              SB_Rectangle next = (SB_Rectangle) _drawables.get(i);
              next.updateComplex();
          }
      }
    }

    protected void resetIds()
    {
      SB_Element element;
      int rectangleId = 0;
      int conditionId = 0;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        element = (SB_Element) _drawables.get(i);
        if (element instanceof SB_Rectangle)
        {
          element.setId(rectangleId);
          ++rectangleId;
        }
        else
        {
          element.setId(conditionId);
          ++conditionId;
        }
      }
    }

    protected void checkError(SB_Polymorphism poly, SB_ErrorInfo errorInfo, I_CompileValidator validator)
    {
      SB_OutputBar outputBar = SB_OutputBar.getInstance();
      SB_Output build = SB_OutputBar._build;

      // nodes
      int initialId = -1;
      SB_Rectangle initial = getInitial();
      if (initial == null)  // check if missing initial
      {
         // report error
         errorInfo._ne++;
         build.addLine(new SB_Line("ERROR: Missing initial node.", Color.red, poly, null, null, SB_Line.ERROR));
      }
      else
         initialId = initial.getId();
      Class nodeClass = null;
      Class conditionClass = null;
      nodeClass = SB_ProjectBar.SB_RectangleClass;
      conditionClass = SB_ProjectBar.SB_ConditionClass;
      int size = count(nodeClass);
      TreeMap map = new TreeMap();
      mapById(map, nodeClass);
      for (int i = 0; i < size; ++i)
      {
         SB_Rectangle node = (SB_Rectangle) map.get(new Integer(i));
         node.checkError(poly, errorInfo, null);
         validator.validateNode(node, poly);
      }
      map.clear();

      // conditions
      size = count(conditionClass);
      mapById(map, conditionClass);
      for (int i = 0; i < size; ++i)
      {
         SB_Condition condition = (SB_Condition) map.get(new Integer(i));
         condition.checkError(poly, errorInfo);
         validator.validateCondition(condition,poly);
      }
      map.clear();
    }


    public SB_Element findById(int id, int type)
    {
      SB_Element element;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        element = (SB_Element) _drawables.get(i);
        if (element.getId() == id)
        {
          if ((type == 0 && element instanceof SB_Rectangle)
              || (type == 1 && element instanceof SB_Condition))
            return element;
        }
      }
      return null;
    }

   

    protected int findOccurrences(Pattern pattern, String strReplace, SB_Polymorphism poly) throws SB_CancelException
    {
      int total = 0;
      SB_Element element;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        element = (SB_Element) _drawables.get(i);
        total += element.findOccurrences(pattern, strReplace, poly);
      }
      return total;
    }

    protected void clearRunningState()
    {
      SB_Element element;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        element = (SB_Element) _drawables.get(i);
        element._runningState = SB_Element.NOT_RUNNING;
      }
    }
    
}
