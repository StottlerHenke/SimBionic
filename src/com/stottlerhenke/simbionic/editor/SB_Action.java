package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Action;

/**
 * Represents an Action in the editor. 
 *
 */
public class SB_Action extends SB_Function
{
    private static final long serialVersionUID = 2302585093L + 2;

    protected static ImageIcon _icon = null;
    protected static ImageIcon _coreIcon = null;
    
    public SB_Action(Action model)
    {
       super(model);
    }

    public Icon getIcon()
    {
      if (isCore())
      {
        if (_coreIcon == null)
          _coreIcon = Util.getImageIcon("CoreAction.gif");
        return _coreIcon;
      }
      else
      {
        if (_icon == null)
          _icon = Util.getImageIcon("Action.gif");
        return _icon;
      }
    }
    
    
    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);
      out.writeBoolean(isCore());
    }
    
    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);
      setCore(in.readBoolean());
    }

    public String getTag() { return "action"; }
    
    @Override
    public boolean isCore() {
       return getActionModel().isCore();
    }
    
    public void setCore(boolean core){
       getActionModel().setCore(core);
    }
    
    public boolean isCellEditable()
    {
        return !isCore() || SimBionicEditor.DEV;
    }
    
    public Action getActionModel() {
       return (Action)getDataModel();
    }
    
    
}
