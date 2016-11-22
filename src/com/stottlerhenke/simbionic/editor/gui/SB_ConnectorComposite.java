
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Point;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;

public class SB_ConnectorComposite extends SB_DrawableComposite
{
    private static final long serialVersionUID = 2302585093L + 1004;

    public boolean add(SB_Drawable drawable)
    {
      SB_Connector connector = (SB_Connector) drawable;
      connector.setId(count());
      Start startModel =  ((SB_Connector)drawable).getStartModel();
      SB_Element endElement = connector.getEndElement();
      SB_Element startElement = connector.getStartElement();
      // The next line is important but hard to understand. Briefly:
      // when the connector gets deserialized, it has a reference to the new (deserialized)
      // start element, but the id is still the "old" id, so we need to update it.
      // Same thing for the end element.
      if (startModel != null && startElement != null)
	 startModel.setId(startElement.getDataModel().getId()); 
      if (endElement != null) {
	 connector.getDataModel().setEndId(endElement.getId());
	 //connector.getDataModel().setEndType(endElement);
      }
      return super.add(connector);
    }

    public SB_Connector containsPoint_StartEnd(Point point, SB_DragType dragType)
    {
        SB_Connector connector;
        int size = _drawables.size();
	for (int i = size - 1; i >= 0; --i)
	{
            connector = (SB_Connector) _drawables.get(i);
                if (connector.getEndRect().contains(point))
		{
                    dragType.type = SB_DragType.kDragEndPoint;
                    return connector;
		}
		else if (connector.getStartRect().contains(point))
		{
                    dragType.type = SB_DragType.kDragStartPoint;
                    return connector;
		}
	}
	return null;
    }

    protected SB_Connector containsPoint_End(Point point)
    {
        SB_Connector connector;
        int size = _drawables.size();
	for (int i = size - 1; i >= 0; --i)
	{
            connector = (SB_Connector) _drawables.get(i);
                if (connector.getEndRect().contains(point))
                    return connector;
	}
	return null;
    }

    protected SB_Connector containsPoint_Label(Point point)
    {
        SB_Connector connector;
        int size = _drawables.size();
	for (int i = size - 1; i >= 0; --i)
	{
            connector = (SB_Connector) _drawables.get(i);
                if (connector.containsPoint_Label(point) != null)
                    return connector;
	}
	return null;
    }

    protected void updatePoints()
    {
        int size = _drawables.size();
        for (int i = 0; i < size; ++i)
            ((SB_Connector) _drawables.get(i)).updatePoints();
    }

    public void updateTwoWay()
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
          ((SB_Connector) _drawables.get(i)).updateTwoWay();
      updatePoints();
    }

    protected void resetIds()
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
        ((SB_Connector) _drawables.get(i)).setId(i);
    }

    protected void checkError( SB_Polymorphism poly, SB_ErrorInfo errorInfo, I_CompileValidator validator)
    {
       // transitions
       int size = count();
       TreeMap map = new TreeMap();
       mapById(map);
       for (int i = 0; i < size; ++i)
       {
          SB_Connector connector = (SB_Connector) map.get(new Integer(i));
          connector.checkError(poly, errorInfo);
          validator.validateConnector(connector,poly);
       }
       map.clear();

    }

    protected boolean isUnreachable(SB_Element element)
    {
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        if (((SB_Connector) _drawables.get(i)).getEndElement() == element)
          return false;
      }
      return true;
    }
   

    protected int findOccurrences(Pattern pattern, String strReplace, SB_Polymorphism poly) throws SB_CancelException
    {
      int total = 0;
      SB_Connector connector;
      int size = _drawables.size();
      for (int i = 0; i < size; ++i)
      {
        connector = (SB_Connector) _drawables.get(i);
        total += connector.findOccurrences(pattern, strReplace, poly);
      }
      return total;
    }
}
