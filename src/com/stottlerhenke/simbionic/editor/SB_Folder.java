package com.stottlerhenke.simbionic.editor;


import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Folder;

/**
 * represents a folder in the editor
 * A folder can be an action folder or predicate folder or behavior folder. 
 *
 */
public class SB_Folder extends UserObject
{
  private static final long serialVersionUID = 2302585093L + 9;
  protected static ImageIcon _icon = null;
  protected boolean _editable = true;
  
  /**
   * Folder data model.
   */
  private Folder _dataModel;


  public SB_Folder(Folder dataModel)
  {
    _dataModel = dataModel;
  }

  public Icon getIcon()
  {
    return javax.swing.plaf.metal.MetalIconFactory.getTreeFolderIcon();
  }

  public boolean isCellEditable()
  {
    return _editable;
  }

  public boolean isNameValid(String name)
  {
    return name.length() > 0;
  }

  public boolean shouldSort() { return true; }
  public String getSortName() { return " " + getName(); }
/*
  public void writeExternal(ObjectOutput out) throws IOException
  {
    super.writeExternal(out);
  }

  public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
  {
    super.readExternal(in);
  }*/
  /**
   * @return Returns the editable.
   */
  public boolean isEditable() {
     return _editable;
  }
  /**
   * @param editable The editable to set.
   */
  public void setEditable(boolean editable) {
     _editable = editable;
  }
  
  public String getName() {
     return _dataModel.getName();
  }
  
  public void setName(String name) {
     _dataModel.setName(name);
  }
  
  public Folder getDataModel() {
     return _dataModel;
  }
}
