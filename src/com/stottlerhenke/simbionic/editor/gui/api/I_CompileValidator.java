package com.stottlerhenke.simbionic.editor.gui.api;

import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.editor.SB_Action;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SB_Function;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_Predicate;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.gui.SB_Condition;
import com.stottlerhenke.simbionic.editor.gui.SB_Connector;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_Rectangle;

/**
 * This interface enables applications embedding the SimBionic editor to 
 * add their own custom error/warning messages during compilation based on
 * the special constraints of the domain.
 * 
 */
public interface I_CompileValidator
{
    /**
     * Sets the output bar where this validator should publish warnings
     * and errors.
     * @param build
     */
    public void setOutputBar(SB_Output build);

    /**
     * Sets the counter object for errors and warnings.
     * @param errorInfo
     */
    public void setErrorInfo(SB_ErrorInfo errorInfo);
    
    /**
     * Checks the given descriptor.
     * @param node
     */
    public void validateDescriptor(DefaultMutableTreeNode descriptorNode);

    /**
     * Checks the given action.
     * @param action
     */
    public void validateAction(SB_Action action);

    /**
     * Checks the given predicate.
     * @param predicate
     */
    public void validatePredicate(SB_Predicate predicate);

    /**
     * Checks the given function parameter.
     * @param param
     * @param function
     */
    public void validateParam(SB_Parameter param, SB_Function function);

    /**
     * Checks the given local variable.
     * @param local
     * @param polymorphism
     */
    public void validateLocalVariable(SB_Variable local, SB_Polymorphism polymorphism);

    /**
     * Checks the given node.
     * @param node
     * @param poly
     */
    public void validateNode(SB_Rectangle node, SB_Polymorphism poly);

    /**
     * Checks the given condition.
     * @param condition
     * @param poly
     */
    public void validateCondition(SB_Condition condition, SB_Polymorphism poly);

    /**
     * Checks the given connector.
     * @param connector
     * @param poly
     */
    public void validateConnector(SB_Connector connector, SB_Polymorphism poly);

    
}
