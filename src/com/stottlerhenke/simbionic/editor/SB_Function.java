
package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Function;
import com.stottlerhenke.simbionic.editor.gui.I_DescriptionHolder;

/**
 * Base class for all function objects.
 * 
 *
 */
abstract public class SB_Function extends UserObject implements I_DescriptionHolder
{
    public static final Class SB_ActionClass=com.stottlerhenke.simbionic.editor.SB_Action.class;
    public static final Class SB_PredicateClass=com.stottlerhenke.simbionic.editor.SB_Predicate.class;
    public static final Class SB_BehaviorClass=com.stottlerhenke.simbionic.editor.SB_Behavior.class;
    
    private static final long serialVersionUID = 2302585093L + 1;

    private Function _dataModel;
    private int _id = -1;
    protected  static Class[] _classes = null;

    public SB_Function() {
       
    }
    
    public SB_Function(Function model) {
       _dataModel = model;
    }
    
    public String getToolTipText()
    {
        String description = getDescription();
        if (description != null && description.length() > 0)
            return description;
        else
            return null;
    }
    
    public boolean shouldSort() { return true; }

    public String getWildName(int n)
    {
        String wild = "";
        for (int i = 0; i < n; ++i)
        {
            if (i < n - 1)
                wild += "*,";
            else
                wild += "*";
        }
        return getName() + "(" + wild + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);

      out.writeObject(getDescription());
      out.writeInt(_id);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);

      setDescription((String) in.readObject());
      setId(in.readInt());
    }

    public abstract String getTag();

    
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return _id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		_id = id;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
	   if (_dataModel.getDescription() == null) {
	      return "";
	   } 
		return _dataModel.getDescription();
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
	   _dataModel.setDescription(description);
	}
	
	@Override
   public String getName() {
      return _dataModel.getName();
   }
	
	@Override
	public void setName(String name) {
	   _dataModel.setName(name);
	}
	
	public boolean isCore() {
	   return false;
	}
	
	public abstract void setCore(boolean core);
	
	
   public Function getDataModel() {
      return _dataModel;
   }

   protected Class[] getRelatedClasses()
   {
       if (_classes == null)
       {
           _classes = new Class[3];
          
               _classes[0] = SB_ActionClass;
               _classes[1] = SB_PredicateClass;
               _classes[2] = SB_BehaviorClass;
          
       }
       return _classes;
   }
	
	
}
