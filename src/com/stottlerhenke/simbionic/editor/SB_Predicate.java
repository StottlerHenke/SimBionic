
package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;

/**
 * This class represents a predicate in the editor.
 */
public class SB_Predicate extends SB_Function
{
    private static final long serialVersionUID = 2302585093L + 3;

    protected static ImageIcon _icon = null;
    protected static ImageIcon _coreIcon = null;

    
    public SB_Predicate(Predicate model)
    {
      super(model);
    }

    public String toString()
    {
      if (_editing)
        return getName();
      else
      {
        return getName() + " : " + getRetType();
      }
    }

   
    /**
     * @return The return type as string (e.g. 'String', 'Integer', etc)
     */
    public String getRetType() {
       SB_TypeManager typeManager = ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getTypeManager();
       String returnType = getPredicateModel().getReturnType(); 
       return typeManager.getTypeName(returnType);
    }
    
    /**
     * @return The full name for the return type. (e.g. java.lang.String)
     */
    public String getFullReturnTypeName() {
       return getPredicateModel().getReturnType(); 
    }
    
    public void setRetType(String retTypeName) { 
       SB_TypeManager typeManager = ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getTypeManager();
       String fullReturnType = typeManager.getTypePackage(retTypeName); 
       getPredicateModel().setReturnType(fullReturnType);
    }
    

    public Icon getIcon()
    {
      if (isCore())
      {
        if (_coreIcon == null)
          _coreIcon = Util.getImageIcon("CorePredicate.gif");
        return _coreIcon;
      }
      else
      {
        if (_icon == null)
          _icon = Util.getImageIcon("Predicate.gif");
        return _icon;
      }
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);
      out.writeObject(getRetType());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);
      setRetType((String) in.readObject());
    }

    public String getTag() { return "predicate"; }

    
    @Override
    public boolean isCore() {
       return getPredicateModel().isCore();
    }
    
    public void setCore(boolean core){
       getPredicateModel().setCore(core);
    }
    
    public boolean isCellEditable()
    {
        return !isCore() || SimBionicEditor.DEV;
    }
    
    
    public Predicate getPredicateModel() {
       return (Predicate)getDataModel();
    }
    
}
