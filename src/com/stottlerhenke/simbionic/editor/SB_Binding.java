package com.stottlerhenke.simbionic.editor;

import java.awt.Color;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_BindingsHolder;
import com.stottlerhenke.simbionic.editor.gui.SB_Catalog;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_Line;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_OutputBar;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.gui.SB_TabbedCanvas;

/**
 * Represents a binding in the editor.
 * Binding is a pair of variable name and its value (string expression)
 *
 */
public class SB_Binding implements Externalizable
{
    private static final long serialVersionUID = 2302585093L + 2000;
    
    public final static String ACTION_BINDING = "(Action)";

    private Binding _dataModel;
    
    public SB_Binding() {
       _dataModel = new Binding();
    }

    public SB_Binding(Binding dataModel)
    {
       _dataModel = dataModel;
    }

    public String toString() { return getVar(); }  // for display in combo box

    public boolean equals(SB_Binding binding)
    {
      return getVar().equals(binding.getVar()) && getExpr().equals(binding.getExpr());
    }

    /**
     * Determine if the binding is an ACTION_BINDING
     * @return true if the binding is an action, false otherwise
     */
    public boolean isAction()
    {
    	return isAction(_dataModel);
    }
    
    public static boolean isAction(Binding bindingModel) {
       return ACTION_BINDING.equals(bindingModel.getVar());
    }
    
    public void checkError(SB_Polymorphism poly, SB_Drawable drawable, SB_ErrorInfo errorInfo)
    {
      SimBionicEditor editor = poly.getParent().getEditor();
      SB_OutputBar outputBar = SB_OutputBar.getInstance();
      SB_Output build = outputBar.getBuild();

    
      SB_ProjectBar projectBar = ComponentRegistry.getProjectBar();

      SB_Catalog catalog = projectBar.getCatalog();

      SB_Variable var = catalog.findVariable(poly, getVar());
      if (var == null)
      {
         errorInfo._ne++;
         build.addLine(new SB_Line("ERROR: Unknown variable '" + getVar() + "'.",
               Color.red, poly, drawable, this, SB_Line.ERROR));
         return;
      }

      int varKind = (var instanceof SB_Global ? 3 : 0);
      // SB_VarType type = SB_TypeManager.convertOldTypeId(var.getType());
      //        SB_VarType type = SB_EditorDataInterface.ConvertType(var.getType());

      // parse expression - TODO: syl - no more parsing expression 
      // but we'll still need to figure out if the expression is valid or not.
      String expr = getExpr();
      /* SB_ExpressionNode tree = SB_ParserEditorInterface.getParser().Parse(expr);
        SB_ParseNode ptree = (SB_ParseNode)( tree );
        if (!ptree.IsValid())
        {
          SB_ErrorNode err = null;
          while ((err = ptree.GetNextError(err)) != null)
          {
            errorInfo._ne++;
            build.addLine(new SB_Line("ERROR: " + err.GetMsg(), Color.red, poly, drawable, this, SB_Line.ERROR));
          }
        }
        else if (!projectBar.getTypeManager().canCastTypes(type, ptree.GetType()))
        {
            errorInfo._ne++;
            build.addLine(new SB_Line("ERROR: Binding expression returns wrong variable type.",
                                      Color.red, poly, drawable, this, SB_Line.ERROR));
        } */
      //        else if (ptree.GetType() != null && !type.equals(SB_VarType.kAny) && !ptree.GetType().equals(SB_VarType.kAny)
      //                 && !type.equals(ptree.GetType()))
      //        {
      //          // integers and floats are interchangeable
      //          if ((type.equals(SB_VarType.kFloat) && ptree.GetType().equals(SB_VarType.kInteger))
      //              || (type.equals(SB_VarType.kInteger) && ptree.GetType().equals(SB_VarType.kFloat)))
      //          {
      //            // no error
      //          }
      //          else
      //          {
      //            errorInfo._ne++;
      //            build.addLine(new SB_Line("ERROR: Binding expression returns wrong variable type.",
      //                                      Color.red, poly, drawable, this, SB_Line.ERROR));
      //          }
      //        }

      // TODO to support binding expression to class member, bindings editor also needs to be changed to support class memeber
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
      out.writeObject(getVar());
      out.writeObject(getExpr());
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
      setVar((String) in.readObject());
      setExpr((String) in.readObject());
    }


    public int findOccurrences(Pattern pattern, String strReplace, SB_Polymorphism poly, SB_Drawable drawable) throws SB_CancelException
    {
      int total = 0;
      for (int i = 0; i <= 1; ++i)
      {
        StringBuffer sb = null;
        StringBuffer sb_next = null;
        int delta = 0;
        Matcher matcher = pattern.matcher(i == 0 ? getVar() : getExpr());
        int length = (i == 0 ? 0 : getVar().length() + 3);
        while (matcher.find())
        {
          ++total;
          SimBionicEditor editor = poly.getParent().getEditor();
          SB_OutputBar outputBar = SB_OutputBar.getInstance();
          SB_Output find = outputBar.getFind();
          SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
          SB_Catalog catalog = projectBar.getCatalog();
          find.addFindLine(poly, drawable, this, length + matcher.start() + delta, length + matcher.end() + delta);

          if (strReplace != null)
          {
            int sel = find.getLines().size() - 1;
            find.setSel(sel);

            int n = JOptionPane.showConfirmDialog(projectBar, "Replace this occurrence?",
                                                  "Replace", JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.YES_OPTION)
            {
              if (sb == null)
              {
                sb = new StringBuffer();
                sb_next = new StringBuffer();
              }
              matcher.appendReplacement(sb, strReplace);
              delta = sb.length() - matcher.end();
              sb_next.setLength(0);
              sb_next.append(sb);
              matcher.appendTail(sb_next);
              poly.addToUndoStack();
              if (i == 0)
              {
                setVar(new String(sb_next));
                ((SB_BindingsHolder) drawable).updateBindings();
              }
              else
                setExpr(new String(sb_next));
              poly.setHighlighted(false);
              SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) ComponentRegistry.getContent();
              tabbedCanvas.getActiveCanvas().clearSingle();
              find.setSel(sel);
              poly.setModified(true);
            }
            else if (n == JOptionPane.CANCEL_OPTION)
              throw new SB_CancelException();
          }
        }
      }
 
      return total;
    }
	/**
	 * @return Returns the expr.
	 */
	public String getExpr() {
		return getDataModel().getExpr();
	}
	/**
	 * @param expr The expr to set.
	 */
	public void setExpr(String expr) {
	   getDataModel().setExpr(expr);
	}
	/**
	 * @return Returns the var.
	 */
	public String getVar() {
		return getDataModel().getVar();
	}
	/**
	 * @param var The var to set.
	 */
	public void setVar(String var) {
	   getDataModel().setVar(var);
	}
	
	public Binding getDataModel() {
	   return _dataModel;
	}
 }
