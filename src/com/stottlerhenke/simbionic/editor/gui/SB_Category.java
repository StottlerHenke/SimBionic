
package com.stottlerhenke.simbionic.editor.gui;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Category;
import com.stottlerhenke.simbionic.editor.Util;


public class SB_Category extends SB_Descriptor {

    private static final long serialVersionUID = 2302585093L + 11;

    private SB_Descriptor _selDescriptor;
    
    public SB_Category() // constructor for Externalizable object
    {
    }

    public SB_Category(Category dataModel) {
        super(dataModel);
        _selDescriptor = this;
    }

    public Icon getIcon() {
        if (isSelected()) {
            if (_selIcon == null)
                    _selIcon = Util.getImageIcon("SelCategory.gif");
            return _selIcon;
        } else {
            if (_icon == null) _icon = Util.getImageIcon("Category.gif");
            return _icon;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(getSelectedDescriptor());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException,
            IOException {
        super.readExternal(in);
        setSelectedDescriptor((SB_Descriptor) in.readObject());
    }
    
    public SB_Descriptor getSelectedDescriptor() {
       return _selDescriptor;
    }
    
    public void setSelectedDescriptor(SB_Descriptor descriptor) {
       _selDescriptor = descriptor;
    }
}