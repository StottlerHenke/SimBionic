package com.stottlerhenke.simbionic.editor.gui.autocomplete;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

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
import com.stottlerhenke.simbionic.editor.gui.SB_AutocompleteTextArea;
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


    /**
     * XXX: Calls to {@link #matchPartialFunction(Vector, String, boolean)}
     * and {@link #matchFunction(List, String, int, boolean)} must be
     * synchronized with {@link #clearNames()} to prevent IndexOutOfBounds
     * exceptions. Unfortunately for maintainers, it is not clear that adding
     * to this list with {@link #initializeNames()} with the concurrent
     * execution of either of the two {@code match...} functions is an error.
     * */
    private List<DefaultMutableTreeNode> _predicates = new Vector<>();

    /**
     * XXX: Calls to {@link #matchPartialFunction(Vector, String, boolean)}
     * and {@link #matchFunction(List, String, int, boolean)} must be
     * synchronized with {@link #clearNames()} to prevent IndexOutOfBounds
     * exceptions. Unfortunately for maintainers, it is not clear that adding
     * to this list with {@link #initializeNames()} with the concurrent
     * execution of either of the two {@code match...} functions is an error.
     * */
    private List<DefaultMutableTreeNode> _actionsBehaviors = new Vector<>();

    /**
     * XXX: Calls to {@link #matchPartialVariable(Vist, String)} must be
     * synchronized with {@link #clearNames()} to prevent CMEs under the new
     * system (historically, the synchronization would be necessary to prevent
     * IndexOutOfBounds exceptions.)
     * */
    private List<SB_Variable> _variables = new Vector<>();

    private List<SB_Auto_Match> _matchList = new Vector<>();

    private int _matchSel = -1;

    private boolean _returnsValue = true;

    private boolean _needToComplete = false;

    private final Comparator<Object> _comparator
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
        _actionsBehaviors.clear();
        _predicates.clear();
        _variables.clear();
        _matchList.clear();
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
    private void matchFunction(List<SB_Auto_Match> matchList, String text,
            int index, boolean actionBehaviorOrPredicate) {
        List<DefaultMutableTreeNode> functions
        = (actionBehaviorOrPredicate) ? _actionsBehaviors : _predicates;

        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;

        int size = functions.size();
        for (int i = 0; i < size; i++) {
            treeNode = functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.equals(funcName)) {
                matchList.add(genFunctionMatch(treeNode, index));
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
    private void matchPartialFunction(List<SB_Auto_Match> matchList,
            String text, boolean actionBehaviorOrPredicate) {
        List<DefaultMutableTreeNode> functions
        = actionBehaviorOrPredicate ? _actionsBehaviors : _predicates;

        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;
        int size = functions.size();
        for (int i = 0; i < size; i++) {
            treeNode = functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.regionMatches(0, funcName, 0, text.length())) {
                matchList.add(genFunctionMatch(treeNode, -1));
            }
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
     * if a variable name starts with text then add to matchList
     * variableName:type as a possible completion for text
     * 
     * @param matchList
     * @param text
     */
    private void matchPartialVariable(
            List<SB_Auto_Match> matchList, String text) {

        /**
         * XXX: note that this approach is no less threadsafe than the previous
         * approach, as the only modification possible for a nonempy _variables
         * field is emptying it, which will produce an
         * IndexOutOfBoundsException under the old approach.
         */
        for (SB_Variable var : _variables) {
            String varName = var.getName();
            if (text.regionMatches(0, varName, 0, text.length())) {
                matchList.add(genVariableMatch(var));
            }
        }
    }

    private SB_Auto_Match genVariableMatch(SB_Variable var) {
        String varName = var.getName();
        String display = varName + " : "
                + getUnambiguousName(var.getFullTypeName());
        String canonical = varName + " : " + var.getFullTypeName();
        return SB_Auto_Match.of(canonical, varName, display);
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
     * 2018-05-02 -jmm
     * <br>
     * Most of the logic is copied from the old getFullName function.
     * @param treeNode A DefaultMutableTreeNode instance assumed to have a
     * SB_Function user object.
     * @param index The index of the parameter in the parameter list that
     * should receive special formatting in the display name.
     * */
    private SB_Auto_Match genFunctionMatch(
            DefaultMutableTreeNode treeNode, int index) {

        SB_Function function = ((SB_Function) treeNode.getUserObject());

        String canonical = function.getName();
        String display = function.getName();
        String insertion = function.getName();

        int size = treeNode.getChildCount();

        List<String> canonicalParameterStrings = new ArrayList<>();
        List<String> displayParameterStrings = new ArrayList<>();
        List<String> insertionParameterStrings = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            DefaultMutableTreeNode childNode
                    = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            SB_Parameter param = ((SB_Parameter) childNode.getUserObject());

            String canonicalParameterString
            = param.getName() + " : " + param.getFullTypeName();
            canonicalParameterStrings.add(canonicalParameterString);

            String displayParameterString = param.getName() + " : "
                    + getUnambiguousName(param.getFullTypeName());
            if (i == index) {
                displayParameterString
                = "<B>" + displayParameterString + "</B>";
            }
            displayParameterStrings.add(displayParameterString);

            insertionParameterStrings.add(param.getName());
        }

        canonical += ("(" + String.join(", ",
                canonicalParameterStrings) + ")");

        display += ("(" + String.join(", ", displayParameterStrings) + ")");
        if (function instanceof SB_Predicate) {
            canonical += " : "
                    + ((SB_Predicate) function).getFullReturnTypeName();

            display += " : " + getUnambiguousName(
                    ((SB_Predicate) function).getFullReturnTypeName());
        }

        insertion
        += ("(" + String.join(", ", insertionParameterStrings) + ")");

        //XXX: Test run with unformatted canonical string
        return SB_Auto_Match.of(canonical, insertion, display);
    }

	public void changeMatchSelection(String sel) {
		if (sel != null) {
			int index = 0;
			for (SB_Auto_Match match : _matchList) {
				if (match.getDisplay().startsWith(sel)) {	// found
					setMatchSelectionIndex(index);
					return;
				}
				index++;
			}
		}
		setMatchSelectionIndex(-1);
	}

	/**XXX: Past uses appear to expect the "display string"; onTextSelected
	 * then extracts the "insert string", but then "bounce back" to
	 * {@link #getInsertString(String, String, int)} when doing insertion.
	 */
	public String getSelectedText () {
		if(getMatchSelectionIndex() < 0 || getMatchSelectionIndex() >= _matchList.size()) {
			return null;
		}
		else {
			return _matchList.get(getMatchSelectionIndex()).getDisplay();
		}
		
	}

    /**
     * 
     * XXX: Like {@link #getSelectedText()}, its predecessor, this method is
     * potentially vulnerable to race conditions if {@link #_matchSel} or
     * {@link #_matchList} is changed concurrently with the execution of this
     * function.
     * */
    public Optional<SB_Auto_Match> getSelectedMatch() {
        //XXX: potential for race conditions
        int selectionIndex = getMatchSelectionIndex();
        if(selectionIndex < 0
           || selectionIndex >= _matchList.size()) {
            return Optional.empty();
        } else {
            return Optional.of(_matchList.get(selectionIndex));
        }
    }

    public Optional<String> getSelectedMatchInsertion() {
        return getSelectedMatch()
                .map(match -> match.getStringToInsert());
    }

    /**
     * XXX: This method assumes that it is called only if the current match
     * selection index is valid.
     * XXX: This method appears to only be called when the argument {@code str}
     * is an "insertion" string obtained from this class.
     * */
    public String getInsertString(String str, String text, int caretPosition) {
    	int pos = caretPosition - 1;
        if (pos != -1) {
	        char c = text.charAt(pos);
	        if (str.charAt(0) == '"') {
	            
	            String line = getSelectedMatchInsertion().get();
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
        Deque<SB_StackData> pred_stack = new ArrayDeque<>();
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
                if (pred_stack.isEmpty()) {
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
    private static String generateCompletionsText(
            List<SB_Auto_Match> matchList, int matchIndex) {
        String text = "";
        String line;
        int pos;
        int size = matchList.size();
        for (int i = 0; i < size; ++i){
          line = matchList.get(i).getDisplay();
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
    	_matchList.clear();
    }

    /**
     * A convenience method that creates a list containing the insertion
     * strings of all {@code SB_Auto_Match} instances in this instance.
     * */
    public List<String> getMatchInsertionStrings() {
        return _matchList.stream()
                .map(match -> match.getStringToInsert())
                .collect(Collectors.toList());
    }

    public int getNumberOfMatches () {
    	return _matchList.size();
    }
    
    public boolean hasMatches() {
    	return !_matchList.isEmpty();
    }

    /**
     * 2018-05-02
     * XXX: Past uses expect the "display" name with fully-qualified type names
     * (see {@link SB_AutocompleteTextArea#performAutocomplete(String, int)}.
     * */
    public String getMatchWithFullTypeNames (int index) {
    	return _matchList.get(index).getFullyQualifiedAnnotations();
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
