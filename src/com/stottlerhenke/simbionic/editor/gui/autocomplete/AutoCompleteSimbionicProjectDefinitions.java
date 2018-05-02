package com.stottlerhenke.simbionic.editor.gui.autocomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;
import com.stottlerhenke.simbionic.editor.SB_Action;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Class;
import com.stottlerhenke.simbionic.editor.SB_Constant;
import com.stottlerhenke.simbionic.editor.SB_Function;
import com.stottlerhenke.simbionic.editor.SB_Global;
import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SB_Predicate;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.gui.AutoCompletionHelper;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_Catalog;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.gui.SB_TabbedCanvas;

/**
 * Class extracting common functionality used by the different autocomplete classes in the application
 * 
 * TODO -- use this class in class SB_Autocomplete
 * 
 *
 */
public class AutoCompleteSimbionicProjectDefinitions extends AutoCompletionHelper {

	protected Vector<DefaultMutableTreeNode> _predicates = new Vector<>();

	protected Vector<DefaultMutableTreeNode> _actionsBehaviors
	= new Vector<>();

	protected Vector<SB_Variable> _variables = new Vector<>();

	protected Vector _matchList = new Vector();

	protected int _matchSel = -1;
	
	protected boolean _returnsValue = true;
	   
	protected boolean _needToComplete = false;
	
    protected final Comparator<Object> _comparator
    = (obj1, obj2) -> obj1.toString().compareToIgnoreCase(obj2.toString());


    public AutoCompleteSimbionicProjectDefinitions () {
    }

	public void setReturnsValue (boolean returnsValue) {
		_returnsValue = returnsValue;
	}
	
	public boolean returnsValue() {
		return _returnsValue;
	}
	
	public void setMatchSelectionIndex(int index) {
		_matchSel = index;
	}
	
	public void decreaseMatchSelectionIndex() {
		_matchSel--;
	}
	
	public void increaseMatchSelectionIndex() {
		_matchSel++;
	}
	
	public int getMatchSelectionIndex() {
		return _matchSel;
	}
	
	
	/**
	 * clear the list of all possible autocompletion sources
	 */
    public void clearNames() {
        _actionsBehaviors.removeAllElements();
        _predicates.removeAllElements();
        _variables.removeAllElements();
        _matchList.removeAllElements();
        _matchSel = -1;
        _needToComplete = false;
        clearContent(); //from autocompletionHelper
    }

    /**
     * initialize the list of all possible autocompletion sources
     */
    public  void initializeNames() {
        if (_variables.size() > 0) {
            return; //???
        }
        
        SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        Enumeration e = catalog.getRoot().preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        Object userObject;
        List<String> javaClasses = new ArrayList<String>();
        
        while (e.hasMoreElements()) {
            treeNode = (DefaultMutableTreeNode) e.nextElement();
            userObject = treeNode.getUserObject();
            // add actions and behaviors
            if (!_returnsValue
                  && ((userObject instanceof SB_Action) || (userObject instanceof SB_Behavior)))
              _actionsBehaviors.add(treeNode);
            // add predicates
            else if (userObject instanceof SB_Predicate)
                _predicates.add(treeNode);
            // add globals and constants
            // the cast to SB_Variable will always succeed as SB_Variable is
            // the superclass of SB_Global and SB_Constant
            else if ((userObject instanceof SB_Global) || (userObject instanceof SB_Constant))
                _variables.add((SB_Variable) userObject);
            else if (userObject instanceof SB_Class) {
               SB_Class javaClass = (SB_Class)userObject;
               javaClasses.add(javaClass.getName());
            }
        }
        // add locals
        SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) ComponentRegistry.getContent();
        SB_Polymorphism poly = tabbedCanvas.getActiveCanvas().getPoly();
        DefaultMutableTreeNode locals = poly.getLocals();
        int size = locals.getChildCount();
        for (int i = 0; i < size; ++i) {
            SB_Variable local = (SB_Variable) ((DefaultMutableTreeNode) locals.getChildAt(i))
                    .getUserObject();
            _variables.add(local);
        }
        // add parameters
        DefaultMutableTreeNode params = catalog.findNode(poly.getParent(), catalog._behaviors);
        size = params.getChildCount();
        for (int i = 0; i < size; ++i) {
            SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params.getChildAt(i)).getUserObject();
            _variables.add(param);
        }

        if (!_returnsValue) {
           Collections.sort(_actionsBehaviors, _comparator);
        }
        
       Collections.sort(_predicates, _comparator);
       Collections.sort(_variables, _comparator);
        
        //initialize the class Map needed when completing "."
        JavaScript javaScript = projectBar.getDataModel().getJavaScript();
        initializeContent(_variables,javaScript.getImportedJavaClasses()); //from AutocompletionHelper
    }
    
    /**
     * if text corresponds to the name of a function (action/behavior/predicate) then add the function description 
     * to possible completions for text
     * 
     * @param text 
     * @param index ith argument in the action/behavior/predicate that will be highlighted in the completion
     * @param actionBehaviorOrPredicate  if true search actions/behaviors, if false search predicates
     */
    public void matchFunction(String text, int index, boolean actionBehaviorOrPredicate) {
    	matchFunction(_matchList,text,index,actionBehaviorOrPredicate);	
    }
    
    /**
     * if text corresponds to the name of a function (action/behavior/predicate) then add to matchList 
     * the function description as a possible possible completion for text   
     * 
     * @param matchList
     * @param text
     * @param index ith argument in the action/behavior/predicate that will be highlighted in the completion
     * @param actionBehaviorOrPredicate if true search actions/behaviors, if false search predicates
     */
    private void matchFunction(Vector matchList, String text, int index, boolean actionBehaviorOrPredicate) {
        Vector functions = (actionBehaviorOrPredicate) ? _actionsBehaviors : _predicates;
    
        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;
        
        int size = functions.size();
        for (int i = 0; i < size; i++) {
            treeNode = (DefaultMutableTreeNode) functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.equals(funcName)) {
                matchList.add(getFullName(treeNode, index));
                return;
            }
        }
    }
    
    
    /**
     * if a function (action/behavior/predicate) name starts with text then add the function description to the
     * list of possible completions for text
     * 
     * @param text
     * @param actionBehaviorOrPredicate if true search actions/behaviors, if false search predicates
     */
    public void matchPartialFunction(String text, boolean actionBehaviorOrPredicate)  {
    	matchPartialFunction(_matchList,text,actionBehaviorOrPredicate);
    }
    
    /**
     * if a function (action/behavior/predicate) name starts with text then add to matchList the function description
     * as a possible completion for text
     * 
     * @param matchList
     * @param text
     * @param actionBehaviorOrPredicate if true search actions/behaviors, if false search predicates
     */
    private void matchPartialFunction(Vector matchList, String text, boolean actionBehaviorOrPredicate)  {
        Vector<DefaultMutableTreeNode> functions
        = actionBehaviorOrPredicate ? _actionsBehaviors : _predicates;
        
        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;
        int size = functions.size();
        for (int i = 0; i < size; i++) {
            treeNode = (DefaultMutableTreeNode) functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.regionMatches(0, funcName, 0, text.length())) {
                matchList.add(getFullName(treeNode, -1));
            }
            // else if (found)
            // return;
        }
    }
    
    /**
     * if a variable name starts with text then add variableName:type as a
     * possible completion for text
     * @param text
     */
    
    public void matchPartialVariable(String text) {
    	matchPartialVariable(_matchList,text);
    }
    
    /**
     * if a variable name starts with text then add to matchList variableName:type as a
     * possible completion for text

     * @param matchList
     * @param text
     */
    private void matchPartialVariable(Vector matchList, String text) {
        SB_Variable var;
        String varName;
        int size = _variables.size();
        for (int i = 0; i < size; i++) {
            var = (SB_Variable) _variables.get(i);
            varName = var.getName();
            if (text.regionMatches(0, varName, 0, text.length())) {
//                matchList.add(var.toString());
                matchList.add(var.getName() + " : " + var.getFullTypeName());
            }
            // else if (found)
            // return;
        }
    }
    
    /**
    * if a known java class starts with classNamePrefix add the complete class
    * name to the list of possible completions for classNamePrefix
    * 
     * @param classNamePrefix
     */
    public void matchPartialClassName(String classNamePrefix) {
    	matchPartialClassName(_matchList, classNamePrefix); //autocompletionHelper
    }
    
    /**
     * Generate completions for "variableName.dotArg".&nbsp;Returns true if some completions where added to matchList. 
     * 
     * 
     * @param variableName
     * @param dotArg
     * @return
     */
    public boolean  matchPartialDot(String variableName, String dotArg) {
    	return matchPartialDot(_matchList, variableName, dotArg); //autocompletionHelper
    }

    /**
     * For a function (action,behavior,predicate) produce html string of the  functionName (arg1:type1,...,argN:typeN)
     * with the ith (index) argument in bold
     * @param treeNode node in the simbionic catalog referring to an action,behavior,predicate
     * @param index the ith arguments in the action,behavior,predicate
     * @return
     */
    private static String getFullName(DefaultMutableTreeNode treeNode, int index) {
        SB_Function function = ((SB_Function) treeNode.getUserObject());
        String fullName = function.getName() + "(";
        DefaultMutableTreeNode childNode;
        SB_Parameter param;
        int size = treeNode.getChildCount();
        for (int i = 0; i < size; ++i) {
            childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            param = ((SB_Parameter) childNode.getUserObject());
            if (i == index)
                fullName += "<B>";
            // fullName += SB_Variable.kTypeNames[param.getType()] + " " +
            // param.getName();
            fullName += param.getName() + " : " + param.getFullTypeName();
//            fullName += param.getName() + " : " + SB_Variable.kTypeNames[param.getType()];
            if (i == index)
                fullName += "</B>";
            if (i < size - 1)
                fullName += ", ";
            // fullName += ",";
        }
        fullName += ")";
        if (function instanceof SB_Predicate)
            fullName += " : " + ((SB_Predicate) function).getFullReturnTypeName();
//            fullName += " : " + SB_Variable.kTypeNames[((SB_Predicate) function).getRetType()];
        return fullName;
    }
    
	public void changeMatchSelection(String sel) {
		if (sel != null) {
			int index = 0;
			for (Object match : _matchList) {
				String matchText = (String) match;
				if (matchText.startsWith(sel)) {	// found
					setMatchSelectionIndex(index);
					return;
				}
				index++;
			}
		}
		setMatchSelectionIndex(-1);
	}
	
	public String getSelectedText () {
		if(getMatchSelectionIndex() < 0 || getMatchSelectionIndex() >= _matchList.size()) {
			return null;
		}
		else {
			return (String) _matchList.get(getMatchSelectionIndex());
		}
		
	}
	
    public String getInsertString(String str, String text, int caretPosition) {
    	int pos = caretPosition - 1;
        if (pos != -1) {
	        char c = text.charAt(pos);
	        if (str.charAt(0) == '"') {
	            String line = (String) _matchList.get(getMatchSelectionIndex());
	            if (line.equals(text.substring(Math.max(0, pos + 1 - line.length()), pos + 1))) {
	                pos -= line.length();
	            }
	            else {
	                while (c != '"') {
	                    --pos;
	                    c = text.charAt(pos);
	                }
	                --pos;
	            }
	        } 
	        else {
	            while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')  || c == '_') {
	                --pos;
	                if (pos == -1)
	                    break;
	                c = text.charAt(pos);
	            }
	        }
        }
        return str.substring(caretPosition - (pos + 1));
    }
    
    // in: expression and selection position
    // out: function, parameter, parameter index, parenthesis level
    public static void parseFunction(String expr, int pos, ParseInfo info) {
        Stack pred_stack = new Stack();
        pred_stack.push(new SB_StackData(0));
        boolean in_quotes = false;
        SB_StackData stackData;
        int j = -1;
        for (int i = 0; i < pos; i++) {
            char c = expr.charAt(i);
            if (c == '"') {
                in_quotes = !in_quotes;
                if (in_quotes)
                    j = i;
            }
            if (in_quotes)
                continue;
            stackData = (SB_StackData) pred_stack.peek();
            
            switch (c){
            case '(':
                pred_stack.push(new SB_StackData(i + 1));
                break;
            case ',':
                stackData.first = i + 1;
                stackData.count = 0;
                stackData.index++;
                break;
            case ')':
                pred_stack.pop();
                if (pred_stack.empty()) {
                    info.funcName = "";
                    info.index = -1;
                    info.paren = 0;
                    return;
                }
                stackData = (SB_StackData) pred_stack.peek();
                stackData.first = i + 1;
                stackData.count = 0;
                break;
            default:
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                        || c == '_' || c == '.')
                {
                    stackData.count++;
                } else
                {
                    stackData.first = i + 1;
                    stackData.count = 0;
                }
            }
        }

        info.paren = pred_stack.size() - 1;
        if (info.paren > 0) {
            stackData = (SB_StackData) pred_stack.peek();
            if (in_quotes || (pos > 0 && expr.charAt(pos - 1) == '"'))
                info.paramName = expr.substring(j, pos);
            else
                info.paramName = expr.substring(stackData.first, stackData.first + stackData.count);
            info.index = stackData.index;
            pred_stack.pop();
        } 
        else {
            info.index = -1;
        }
        stackData = (SB_StackData) pred_stack.peek();
        info.first = stackData.first;
        info.funcName = expr.substring(stackData.first, stackData.first + stackData.count);
    }
    
    private static class SB_StackData {

        int first, count; // mid string location

        int index; // parameter index

        SB_StackData(int f) {
            first = f;
            count = 0;
            index = 0;
        }
    }
    
    //------------------------------------------------------------------------------------------------------------
    // 
    //------------------------------------------------------------------------------------------------------------
    
    /**
     * returns a html string denoting all possible completions with the currently selected completion in blue.&nbsp;
     * A 'br' element is used to identify/separate the possible completions.
     * @return
     */
    public String generateCompletionsText () {
    	return generateCompletionsText(_matchList, getMatchSelectionIndex());
    }
    
    /**
     * returns a html string denoting all possible completions in matchList with the matchIndex completion in blue.&nbsp;
     * A 'br' element is used to identify/separate the possible completions.
     * 
     * @param matchList
     * @param matchIndex
     * @return
     */
    private static String generateCompletionsText(Vector matchList, int matchIndex) {
        String text = "";
        String line;
        int pos;
        int size = matchList.size();
        for (int i = 0; i < size; ++i){
          line = (String) matchList.get(i);
          if (i == matchIndex){
             if (line.charAt(0) == '"')
              pos = line.length();
             else{
              pos = line.indexOf('(');
              if (pos == -1) {
                 int k = line.indexOf(':');
                 if (k>0) {
                    pos = k - 1;
                 }
                 else {
                    pos = line.length();
                 }
              }
             }
             line = "<B><span style='color: blue;'>" + line.substring(0, pos) + "</span></B>" + line.substring(pos);
          }
          text += line;
          if (i < size - 1)
              text += "<BR>\n";
        }
        return text;
     }
    
    public boolean needsToComplete() {
    	return _matchList.size() > 0 && _needToComplete;
    }
    
    public void setNeedsToComplete(boolean needsToComplete) {
    	_needToComplete = needsToComplete;
    }
    
    public void clearMatchList() {
    	_matchList.removeAllElements();
    }
    
    public Vector getMatchList() {
    	return _matchList;
    }
    
    public int getNumberOfMatches () {
    	return _matchList.size();
    }
    
    public boolean hasMatches() {
    	return !_matchList.isEmpty();
    }
    
    public String getMatch (int index) {
    	return (String)_matchList.get(index);
    }
    
    public void removeMatch (int index) {
    	_matchList.remove(index);
    }
    
    
    //-----------------------------------------------------------------------------
    // UI
    //-----------------------------------------------------------------------------
    
    private static DefaultMutableTreeNode getFunction(String funcName,
            List<DefaultMutableTreeNode> functions) {
        int size = functions.size();
        for (int i = 0; i < size; i++) {
            DefaultMutableTreeNode treeNode = functions.get(i);
            SB_Function func = ((SB_Function) treeNode.getUserObject());
            if (func.getName().equals(funcName)) {
            	return treeNode;
            }
        }
        return null;
	}
	
	public DefaultMutableTreeNode getActionBehavior(String funcName) {
		return getFunction(funcName, _actionsBehaviors);
	}

	public DefaultMutableTreeNode getPredicate(String funcName) {
		return getFunction(funcName, _predicates);
	}
	
}
