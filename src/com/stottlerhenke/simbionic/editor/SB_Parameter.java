
package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;

/**
 * Represents a parameter in the editor.
 *
 */
public class SB_Parameter extends SB_Variable 
{
    private static final long serialVersionUID = 2302585093L + 6;

   // protected static ImageIcon _icon = null;

//    transient private String _fullName = null;
    
    public SB_Parameter()  // constructor for Externalizable object
    {
    }

    public SB_Parameter(Parameter model)
    {
        super(model);
    }


    public String toString()
    {
      if (_editing)
        return getName();
      else
      { 
        return getName() + " : " + getType();
      }
    }



    public Icon getIcon()
    {
        if (_icon == null)
            _icon = Util.getImageIcon("Parameter.gif");
        return _icon;
    }


    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);
    }
    
    public Object clone()
    {
        return new SB_Parameter(getDataModel());
    }

}
