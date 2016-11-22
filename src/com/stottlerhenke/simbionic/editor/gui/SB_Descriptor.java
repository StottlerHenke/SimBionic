package com.stottlerhenke.simbionic.editor.gui;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor;
import com.stottlerhenke.simbionic.editor.Util;

public class SB_Descriptor extends com.stottlerhenke.simbionic.editor.UserObject {

  private static final long serialVersionUID = 2302585093L + 10;

  protected ImageIcon _icon = null;
  protected ImageIcon _selIcon = null;
  protected static Class[] _classes = null;
  
  private Descriptor _dataModel;

  public SB_Descriptor() // constructor for Externalizable object
  {
  }

  public SB_Descriptor(Descriptor dataModel) {
     _dataModel = dataModel;
  }

  public Icon getIcon() {
      if (isSelected()) {
          if (_selIcon == null)
                  _selIcon = Util.getImageIcon("SelDescriptor.gif");
          return _selIcon;
      } else {
          if (_icon == null) _icon = Util.getImageIcon("Descriptor.gif");
          return _icon;
      }
  }

  protected Class[] getRelatedClasses() {
      if (_classes == null) {
          _classes = new Class[1];
       
              _classes[0] = SB_Descriptors.SB_DescriptorsClass;
          
      }
      return _classes;
  }

  public boolean isNameValid(String name) {
      return name.length() > 0;
  }

  public void writeExternal(ObjectOutput out) throws IOException {
      super.writeExternal(out);
      out.writeBoolean(isSelected());
  }

  public void readExternal(ObjectInput in) throws ClassNotFoundException,
          IOException {
      super.readExternal(in);
      setSelected(in.readBoolean());
  }
  
  public String getName() {
     return _dataModel.getName();
  }
  
  public void setName(String newName) {
     _dataModel.setName(newName);
  }
  
  protected boolean isSelected() {
     return _dataModel.isSelected();
  }
  
  protected void setSelected(boolean selected) {
     _dataModel.setSelected(selected);
  }
  
  public Descriptor getDataModel() {
     return _dataModel;
  }
}
