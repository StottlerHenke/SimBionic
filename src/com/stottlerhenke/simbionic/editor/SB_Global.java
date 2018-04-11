
package com.stottlerhenke.simbionic.editor;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Global;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_Line;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_OutputBar;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.parser.SB_ErrorNode;
import com.stottlerhenke.simbionic.editor.parser.SB_ParseNode;

/**
 * Represents a global in the editor
 */
public class SB_Global extends SB_Variable {
    public static final Class SB_GlobalClass = com.stottlerhenke.simbionic.editor.SB_Global.class;

    private static final long serialVersionUID = 2302585093L + 7;

    // protected static ImageIcon _icon = null;
    protected static ImageIcon _polyIcon = null;

    protected static Class[] _classes;

    public SB_Global() // constructor for Externalizable object
    {
    }

    public SB_Global(Global model) {
        super(model);
    }

   
    public boolean isPolymorphic() {
        return getGlobalModel().isPolymorphic();
    }

    public boolean isCellEditable() {
        return _editable && !isPolymorphic();
    }

    public Icon getIcon() {
        if (isPolymorphic()) {
            if (_polyIcon == null)
                _polyIcon = Util.getImageIcon("PolyGlobal.gif");
            return _polyIcon;
        } else {
            if (_icon == null)
                _icon = Util.getImageIcon("Global.gif");
            return _icon;
        }
    }

    protected Class[] getRelatedClasses() {
        if (_classes == null) {
            _classes = new Class[2];

            _classes[0] = SB_GlobalClass;
            _classes[1] = SB_Constant.SB_ConstantClass;

        }
        return _classes;
    }

    public void checkError(SimBionicEditor editor, 
            SB_ErrorInfo errorInfo, SB_TypeManager typeManager) {
       
       SB_OutputBar outputBar = SB_OutputBar.getInstance();
       SB_Output build = outputBar.getBuild();

       if (getInitial().length() == 0)
       {
          // must specify an initial value -- give a more helpful error message
          errorInfo._ne++;
          build.addLine(new SB_Line("ERROR: '" + getName()
                + "' global: no initial value specified", Color.red, null,
                null, this, SB_Line.ERROR));                
          return;
       }

       // parse initial expression
       SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
       String initial = projectBar.getCatalog().constantReplace(getInitial());
       SB_ParseNode ptree = parseValue(initial);
       if (ptree != null && !ptree.IsValid()) {

          SB_ErrorNode err = null;
          while ((err = ptree.GetNextError(err)) != null) {
             errorInfo._ne++;
             build.addLine(new SB_Line("ERROR: '" + getName()
                   + "' global: " + err.GetMsg(), Color.red, null,
                   null, this, SB_Line.ERROR));
          }
          return;
       }


    }

        
    public void writeExternal(ObjectOutput out) throws IOException {

        super.writeExternal(out);

        out.writeObject(getInitial());
        out.writeBoolean(isPolymorphic());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException,
            IOException {
        super.readExternal(in);

        setInitial((String) in.readObject());
        setPolymorphic(in.readBoolean());
    }

    /**
     * @return Returns the initial.
     */
    public String getInitial() {
        String initial = getGlobalModel().getInitial();
        if (initial == null) {
           return "";
        } else {
           return initial;
        }
    }

    /**
     * @param initial
     *            The initial to set.
     */
    public void setInitial(String initial) {
       getGlobalModel().setInitial(initial);
    }

    /**
     * @param polymorphic
     *            The polymorphic to set.
     */
    public void setPolymorphic(boolean polymorphic) {
        getGlobalModel().setPolymorphic(polymorphic);
    }
    
    public Global getGlobalModel() {
       return (Global) getDataModel();
    }
}