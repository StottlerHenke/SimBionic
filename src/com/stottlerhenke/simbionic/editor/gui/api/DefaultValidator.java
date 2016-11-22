package com.stottlerhenke.simbionic.editor.gui.api;

import java.awt.Color;

import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.editor.SB_Action;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SB_Function;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_Predicate;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.gui.SB_Condition;
import com.stottlerhenke.simbionic.editor.gui.SB_Connector;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_Line;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_Rectangle;

/**
 * Do-nothing compile validator (also serves as a handy adapter
 * for applications that don't need all of the validation methods).
 * 
 */
public class DefaultValidator implements I_CompileValidator
{
    protected SB_ErrorInfo _errorInfo;
    protected SB_Output _output;
    
    public void setErrorInfo(SB_ErrorInfo errorInfo)
    {
        _errorInfo = errorInfo;
    }

    public void setOutputBar(SB_Output output)
    {
        _output = output;
    }

    protected void addError(String message,SB_Polymorphism badPoly,SB_Drawable badElement)
    {
        _errorInfo._ne++;
        _output.addLine(new SB_Line(message,Color.RED,badPoly,badElement,null,SB_Line.ERROR));        
    }
    
    public void validateAction(SB_Action action)
    {
    }

    public void validateDescriptor(DefaultMutableTreeNode descriptorNode)
    {
    }

    public void validateParam(SB_Parameter param, SB_Function function)
    {
    }

    public void validatePredicate(SB_Predicate predicate)
    {
    }

    public void validateCondition(SB_Condition condition, SB_Polymorphism poly)
    {
    }

    public void validateConnector(SB_Connector connector, SB_Polymorphism poly)
    {
    }

    public void validateLocalVariable(SB_Variable local, SB_Polymorphism polymorphism)
    {
    }

    public void validateNode(SB_Rectangle node, SB_Polymorphism poly)
    {
    }
}
