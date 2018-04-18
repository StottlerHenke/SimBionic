
package com.stottlerhenke.simbionic.editor;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.Icon;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Constant;
import com.stottlerhenke.simbionic.editor.gui.SB_Line;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_OutputBar;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.parser.SB_ErrorNode;
import com.stottlerhenke.simbionic.editor.parser.SB_ParseNode;

/**
 * Represents a constant in the editor. 
 *
 */
public class SB_Constant extends SB_Variable
{
    public static final Class SB_ConstantClass=com.stottlerhenke.simbionic.editor.SB_Constant.class;
    
    private static final long serialVersionUID = 2302585093L + 8;

    protected static Class[] _classes;

    public SB_Constant()  // constructor for Externalizable object
    {
    }

    public SB_Constant(Constant model)
    {
        super(model);
    }

    public String getValue() {
       String value = getConstantModel().getValue(); 
       if (value == null) {
          return "";
       }
       return value;
    }

    public Icon getIcon()
    {
        if (_icon == null)
            _icon = Util.getImageIcon("Constant.gif");
        return _icon;
    }

    protected Class[] getRelatedClasses()
    {
        if (_classes == null)
        {
            _classes = new Class[2];
           
                _classes[0] = SB_Global.SB_GlobalClass;
                _classes[1] = SB_ConstantClass;
           
        }
        return _classes;
    }

    public void validate(SimBionicEditor editor, SB_ErrorInfo errorInfo)
    {
      // parse constant expression
      SB_ParseNode ptree = parseValue(getValue());
      if (ptree != null && !ptree.IsValid())
      {
        SB_ErrorNode err = null;
        while ((err = ptree.GetNextError(err)) != null)
        {
          SB_OutputBar outputBar = SB_OutputBar.getInstance();
          SB_Output build = outputBar.getBuild();

          errorInfo._ne++;
          build.addLine(new SB_Line("ERROR: '" + getName() + "' constant: " + err.GetMsg(),
                                    Color.red, null, null, this, SB_Line.ERROR));
        }
      }
    }

    public String replace(String expr)
    {
       return SB_ProjectBar.searchAndReplace(expr, getName(), getValue());
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      super.writeExternal(out);
      out.writeObject(getValue());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      super.readExternal(in);
      setValue((String) in.readObject());
    }
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
	   getConstantModel().setValue(value);
	}
	
	public Constant getConstantModel() {
	   return (Constant)getDataModel();
	}
}
