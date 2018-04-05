
package com.stottlerhenke.simbionic.editor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.api.SB_ParamType;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.parser.SB_ParseNode;

public class SB_Variable extends UserObject {

    private static final long serialVersionUID = 2302585093L + 5;

    public final static int kUser = 0xA; // used by deprecated methods
    
    /**
     * @deprecated
     */
    public final static String[] kTypeNames = 
    { "float", "string", "entity", "vector", "data", "integer", 
      "boolean", "any", "array", "table", "user" };

    protected boolean _editable = true;
    
    protected Object _userData = null;
    
    protected ImageIcon _icon = null;
    
    private Parameter _dataModel;

    public SB_Variable() // constructor for Externalizable object
    {
    }

    public SB_Variable(Parameter model) {
        _dataModel = model;
    }

    public String toString() {
        if (_editing)
            return getName();
        else {
           return getName() + " : " + getType();
        }
    }

    public void setName(String name) {
        _dataModel.setName(name);
    }
    
    public String getName() {
       return _dataModel.getName();
    }

    public String getType() {
        SB_TypeManager typeManager = ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getTypeManager();
        String type = _dataModel.getType();
        // return just the name of the type (not the full name)
        return typeManager.getTypeName(type);
    }
    
    public String getFullTypeName() {
       return _dataModel.getType();
    }

    public void setType(String type) {
       SB_TypeManager typeManager = ((SB_ProjectBar) ComponentRegistry.getProjectBar()).getTypeManager();
       String fullType = typeManager.getTypePackage(type); 
       // store the full name
       _dataModel.setType(fullType);
    }

    public String getToolTipText()
    {
        String description = getDescription();
        if (description != null && description.length() > 0)
            return description;
        else
            return null;
    }

    public String getDescription() {
        if (_dataModel.getDescription() == null) {
            return "";
        }
        return _dataModel.getDescription();
    }

    /**
     * @deprecated
     */
    public static int typeToInt(String type){
    	for (int i=0;i<kTypeNames.length;i++){
    		if (kTypeNames[i].equals(type))
    			return i;
    	}
    	return -1;
    }

    public boolean isCellEditable() {
        return _editable || SimBionicEditor.DEV;
    }

    public Icon getIcon() {
        if (_icon == null) _icon = Util.getImageIcon("Local.gif");
        return _icon;
    }


    /**
     * convert from combo box position to SIM variable type index
     * @deprecated
     */
    protected SB_ParamType posToIndex(int pos) {
        switch (pos) {
        /*case kFloat:
            return SB_ParamType.kSB_Float;
        case kString:
            return SB_ParamType.kSB_String;
        case kEntity:
            return SB_ParamType.kSB_Entity;
        case kVector:
            return SB_ParamType.kSB_Vector; 
        case kInteger:
            return SB_ParamType.kSB_Integer;
        case kBoolean:
            return SB_ParamType.kSB_Boolean;
        case kAny:
            return SB_ParamType.kSB_StrConst; */
        default:
            return SB_ParamType.kSB_Invalid;
        }
    }

    // TODO: syl - how to validate value?
    protected SB_ParseNode parseValue(String value) {
        SB_ParseNode ptree = null;
        return ptree;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(getType());
        out.writeBoolean(_editable);
        out.writeObject(_userData);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException,
            IOException {
        super.readExternal(in);

        setType((String) in.readObject());
        
        _editable = in.readBoolean();
        _userData = in.readObject();
    }

    /**
     * @return Returns the editable.
     */
    public boolean isEditable() {
        return _editable;
    }

    /**
     * @param editable
     *            The editable to set.
     */
    public void setEditable(boolean editable) {
        _editable = editable;
    }

    /**
     * @return Returns the userData.
     */
    public Object getUserData() {
        return _userData;
    }

    /**
     * @param userData
     *            The userData to set.
     */
    public void setUserData(Object userData) {
        _userData = userData;
    }
    
    public Parameter getDataModel() {
       return _dataModel;
    }
    
   
}