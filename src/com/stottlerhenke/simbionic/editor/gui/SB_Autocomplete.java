package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
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
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.AutoCompletionHelper.ParseInfo;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;



public class SB_Autocomplete extends JTextField implements Comparator
{
   
    protected SimBionicEditor _editor;

    protected SB_GlassPane _glassPane = new SB_GlassPane();

    protected boolean _returnsValue = true;
    
    protected Vector _predicates = new Vector();
    
    protected Vector _actionsBehaviors = new Vector();

    protected Vector _variables = new Vector();

    protected Vector _matchList = new Vector();

    protected int _matchSel = -1;

    protected boolean _needToComplete = false;

    protected boolean _escapePressed = false;

    protected boolean _ignoreCaretUpdate = false;
    
    protected boolean _ignoreFocusGained = false;
    
    private AutoCompletionHelper _completionHelper;
        
    protected List<SB_AutocompleteListener> _listeners = new ArrayList<SB_AutocompleteListener>();

	protected boolean _filterByType = false;
    
    public SB_Autocomplete(int cols, SimBionicEditor editor)
    {
        super(cols);
        setFocusTraversalKeysEnabled(false);

        _editor = editor;
        _completionHelper = new AutoCompletionHelper();
        
        addFocusListener(new FocusListener()
        {

            public void focusGained(FocusEvent event)
            {
            	// TODO rth custom editors set _ignoreFocusGained so that
            	// we don't reinvoke the editor when it returns the focus
            	// after editing
            	if (!_ignoreFocusGained) { 
            		if (!tryCustomEdit()) {
            			if (_glassPane != null) {
            				clearNames();
            				initializeNames();
	            			_glassPane.setLocation(SB_Autocomplete.this);
	            			_glassPane.setVisible(true);
            			}
            			// _editor._deleteItem.setAccelerator(null);
            			_escapePressed = false;
            		}
            	} else {
            		_ignoreFocusGained = false;
            		fireActionPerformed();
            	}
            }

            public void focusLost(FocusEvent event)
            {
            	if (_glassPane != null) {
	               if (_glassPane.list.getSelectedIndex() >= 0 && _matchList.size() > 0 && _needToComplete) {
	                  _matchSel = _glassPane.list.getSelectedIndex();
	                  onTextSelected();
	               }
	                _glassPane.setVisible(false);
	                clearNames();
            	}
                // if (_editor._deleteItem != null)
                // _editor._deleteItem.setAccelerator(KeyStroke.getKeyStroke(
                // KeyEvent.VK_DELETE, 0, false));
            }
        });

        addCaretListener(new CaretListener()
        {

            public void caretUpdate(CaretEvent event)
            {
                // used to make canvas more responsive
                if (_ignoreCaretUpdate)
                    return;

                if (ComponentRegistry.getFrame() != null
                        && ComponentRegistry.getFrame().getFocusOwner() != SB_Autocomplete.this)
                {
                    requestFocus();
                    initializeNames();
                }
                performAutocomplete(getText(), event.getDot());
            }
        });
    }
    
    public void addAutoCompleteListener(SB_AutocompleteListener listener) {
    	_listeners.add(listener);
    }
    
    public boolean removeAutoCompleteListener(SB_AutocompleteListener listener) {
    	return _listeners.remove(listener);
    }
    
    public void processKeyEvent(KeyEvent event){       
//       if (_glassPane != null) {
    	   int id = event.getID();
    	   switch (event.getKeyCode()){
    	   case KeyEvent.VK_UP:
    		   if (id == KeyEvent.KEY_PRESSED){
    			   int size = _matchList.size();
    			   if (size > 1){
    				   --_matchSel;
    				   if (_matchSel == -1) {
    					   _matchSel = size - 1;
    				   }
    				   if (_glassPane != null)
    					   _glassPane.setText(generateMatchText(_matchList, _matchSel),_matchSel,_matchList.size());
    				   else {
    				         for (SB_AutocompleteListener listener : _listeners) {
    				        	 listener.matchSelectionChanged((String) _matchList.get(_matchSel));
    				         }
    				   }
    			   }
    		   }
    		   return;
    	   case KeyEvent.VK_DOWN:
    		   if (id == KeyEvent.KEY_PRESSED){
    			   int size = _matchList.size();
    			   if (size > 1){
    				   ++_matchSel;
    				   if (_matchSel == size) {
    					   _matchSel = 0;
    				   }
    				   if (_glassPane != null)
    					   _glassPane.setText(generateMatchText(_matchList, _matchSel),_matchSel,_matchList.size());
    				   else {
  				         for (SB_AutocompleteListener listener : _listeners) {
				        	 listener.matchSelectionChanged((String) _matchList.get(_matchSel));
				         }
    				   }
    			   }
    		   }
    		   return;
    	   case KeyEvent.VK_TAB:
    	   case KeyEvent.VK_ENTER:
    		   if (id == KeyEvent.KEY_PRESSED && _needToComplete) {
    			   if (_glassPane != null)
    				   onTextSelected();
    			   else {
    			         for (SB_AutocompleteListener listener : _listeners) {
    			        	 listener.completeExpression();
    			         }
    			   }
    			   return;
    		   }
    		   else
    			   break;// allows super.processKeyEvent
    		   /*
          return;
    		    */
    	   case KeyEvent.VK_ESCAPE:
    		   if (id == KeyEvent.KEY_PRESSED){
//         escapePressed = true;
    			   //_editor.getFrame().requestFocus();
    		   }
    		   return;
    	   default:
    	   }
//       }
       
       super.processKeyEvent(event);
     }
    
    protected void onTextSelected() {
       String str = (String) _matchList.get(_matchSel);
       if (str.charAt(0) != '"'){
          int pos = str.indexOf('(');
          if (pos != -1)
             ; // do nothing. When completing add the str = str.substring(0, pos);
          else{
             pos = str.indexOf(':');
             if (pos != -1)
                str = str.substring(0, pos - 1);
          }
       }
       str = getInsertString(str);
       String text = getText();
       int caretPos = getCaretPosition();
       text = text.substring(0, caretPos) + str + text.substring(caretPos);
       setText(text);
       caretPos += str.length();
       setCaretPosition(caretPos);
       _needToComplete = false;
    }


   /* public void processKeyEvent(KeyEvent event)
    {
        int id = event.getID();
        switch (event.getKeyCode())
        {
        case KeyEvent.VK_UP:
            if (id == KeyEvent.KEY_PRESSED)
            {
                int size = _matchList.size();
                if (size > 1)
                {
                    --_matchSel;
                    if (_matchSel == -1)
                        _matchSel = size - 1;
                    _glassPane.setText(generateMatchText(_matchList, _matchSel));
                }
            }
            return;
        case KeyEvent.VK_DOWN:
            if (id == KeyEvent.KEY_PRESSED)
            {
                int size = _matchList.size();
                if (size > 1)
                {
                    ++_matchSel;
                    if (_matchSel == size)
                        _matchSel = 0;
                    _glassPane.setText(generateMatchText(_matchList, _matchSel));
                }
            }
            return;
        case KeyEvent.VK_TAB:
            if (id == KeyEvent.KEY_PRESSED && _needToComplete)
            {
                String str = (String) _matchList.get(_matchSel);
                if (str.charAt(0) != '"')
                {
                    int pos = str.indexOf('(');
                    if (pos != -1)
                        str = str.substring(0, pos);
                    else
                    {
                        pos = str.indexOf(':');
                        if (pos != -1)
                            str = str.substring(0, pos - 1);
                    }
                }
                
                str = getInsertString(str);
                String text = getText();
                int caretPos = getCaretPosition();
                text = text.substring(0, caretPos) + str + text.substring(caretPos);
                setText(text);
                caretPos += str.length();
                setCaretPosition(caretPos);
                
                // if we just finished a completion for a function that has a custom editor,
                // launch that custom editor
                final ParseInfo info = new ParseInfo();
                final int end = caretPos;
                parseFunction(getText(), caretPos, info);
                String expr = text.substring(info.first, end);
                I_ExpressionEditor editor = _editor.getEditorRegistry().getExpressionEditor(
                		EditorRegistry.EXPRESSION_TYPE_FUNCTION, 
                		info.funcName, 
                		expr);
                if (editor != null) {
                	editor.editObject(expr, new I_EditorListener() {
                		public void editingCanceled(I_ExpressionEditor source) {}

                		public void editingCompleted(I_ExpressionEditor source, String result) {
                			String text = getText();
                			setText(text.substring(0, info.first) + result + text.substring(end));
                			setCaretPosition(info.first + result.length());
                			_ignoreFocusGained = true;
                		}
                	});
                }
            }
            return;
        case KeyEvent.VK_ESCAPE:
            if (id == KeyEvent.KEY_PRESSED)
            {
                _escapePressed = true;
                ComponentRegistry.getFrame().requestFocus();
            }
            return;
        default:
        }
        super.processKeyEvent(event);
    } */
    
    protected Document createDefaultModel(){
       return new PlainDocument(){
          public void insertString(int offs, String str, AttributeSet a) throws BadLocationException{
             if (str == null) return;
             boolean needToClean = false;
             char c = ' ';
             if (_needToComplete && str.length() == 1 && _matchSel != -1){
                c = str.charAt(0);
                String line = (String) _matchList.get(_matchSel);
                int pos = line.indexOf('(');
                if (c == '('){
                   if (pos != -1)
                	   str = line.substring(0, pos + 1);
                }
                else if (c == ',' || c == ')' || c == ' ' || c == '*' || c == '/'
                      || c == '+' || c == '-' || c == '=' || c == '<' || c == '>'
                      || c == '!' || c == '.' || c == '&' || c == '|'){
                   if (line.charAt(0) == '"')
                      str = line + c;
                   else if (pos == -1){
                      pos = line.indexOf(':');
                      if (pos != -1) {
                         str = line.substring(0, pos - 1) + c;
                      }
                   }
                }
                else if (c == '"'){
                   if (line.charAt(0) == '"')
                      str = line;
                }
                
                if (str.length() > 1) {
                   str = getInsertString(str);
                   needToClean = true;
                }
             }
             
             super.insertString(offs, str, a);
             
             if (needToClean) {
            	 String text = SB_Autocomplete.this.getText();
            	 int n = text.length();
            	 int removeLen = 0;
            	 for (int i = offs + str.length(); i < n; i++) {
            		 char next = text.charAt(i);
            		 if (('a' <= next && next <= 'z')  || ('A' <= next && next <= 'Z')
            				 || ('0' <= next && next <= '1') || next == '_')
            			 removeLen++;
            		 else {
            			 if (next == c)
            				 removeLen++;
            			 break;
            		 }
            	 }
            	 super.remove(offs + str.length(), removeLen);
             }
          }
       };
    } 

    
  /*  protected Document createDefaultModel()
    {
        return new PlainDocument()
        {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException
            {
                if (str == null)
                    return;
                if (_needToComplete && str.length() == 1)
                {
                    char c = str.charAt(0);
                    String line = (String) _matchList.get(_matchSel);
                    int pos = line.indexOf('(');
                    if (c == '(')
                    {
                        if (pos != -1)
                            str = line.substring(0, pos + 1);
                    }
                    else if (c == ',' || c == ')' || c == ' ' || c == '*' || c == '/' || c == '+'
                            || c == '-' || c == '=' || c == '<' || c == '>' || c == '!' || c == '.'
                            || c == '&' || c == '|')
                    {
                        if (line.charAt(0) == '"')
                            str = line + c;
                        else if (pos == -1)
                        {
                            pos = line.indexOf(':');
                            str = line.substring(0, pos - 1) + c;
                        }
                    } else if (c == '"')
                    {
                        if (line.charAt(0) == '"')
                            str = line;
                    }

                    if (str.length() > 1)
                        str = getInsertString(str);
                }
                super.insertString(offs, str, a);
            }
        };
    } */

    protected String getInsertString(String str)
    {
        String text = getText();
        int pos = getCaretPosition() - 1;
        if (pos != -1) {
	        char c = text.charAt(pos);
	        if (str.charAt(0) == '"')
	        {
	            String line = (String) _matchList.get(_matchSel);
	            if (line.equals(text.substring(Math.max(0, pos + 1 - line.length()), pos + 1)))
	                pos -= line.length();
	            else
	            {
	                while (c != '"')
	                {
	                    --pos;
	                    c = text.charAt(pos);
	                }
	                --pos;
	            }
	        } else
	        {
	            while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
	                    || c == '_')
	            {
	                --pos;
	                if (pos == -1)
	                    break;
	                c = text.charAt(pos);
	            }
	        }
        }
        return str.substring(getCaretPosition() - (pos + 1));
    }

    protected boolean tryCustomEdit() {
    	// if the expression can be edited with a custom editor,
    	// launch the custom editor
    	String text = getText();
    	final ParseInfo info = new ParseInfo();
        parseFunction(text, text.length(), info);
        // expression must be a sequence of word characters (letters, digits, underscores)
        // followed by an open paren, and must have matching parens
        Pattern p = Pattern.compile("(\\w+)\\(.*");
        Matcher m = p.matcher(text);
        if (info.paren == 0 && m.matches()) {
        	String function = m.group(1);
        	I_ExpressionEditor editor = _editor.getEditorRegistry().getExpressionEditor(
        			EditorRegistry.EXPRESSION_TYPE_FUNCTION, 
        			function, 
        			text);
        	if (editor != null) {
        		editor.editObject(text, new I_EditorListener() {
        			public void editingCanceled(I_ExpressionEditor source) {}

        			public void editingCompleted(I_ExpressionEditor source, String result) {
        				setText(result);
        				_ignoreFocusGained = true;
        			}
        		});
        		return true;
        	}
        }
        return false;
    }
    
    protected void initializeNames()
    {
        if (_variables.size() > 0)
            return;

        if (_glassPane != null)
        	_glassPane.setText(null);

        SB_ProjectBar projectBar = (SB_ProjectBar) ComponentRegistry.getProjectBar();
        SB_Catalog catalog = projectBar._catalog;
        Enumeration e = catalog.getRoot().preorderEnumeration();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
        Object userObject;
        List<String> javaClasses = new ArrayList<String>();
        
        while (e.hasMoreElements())
        {
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
            else if ((userObject instanceof SB_Global) || (userObject instanceof SB_Constant))
                _variables.add(userObject);
            else if (userObject instanceof SB_Class) {
               SB_Class javaClass = (SB_Class)userObject;
               javaClasses.add(javaClass.getName());
            }
        }
        // add locals
        SB_TabbedCanvas tabbedCanvas = (SB_TabbedCanvas) ComponentRegistry.getContent();
        SB_Polymorphism poly = tabbedCanvas.getActiveCanvas()._poly;
        DefaultMutableTreeNode locals = poly.getLocals();
        int size = locals.getChildCount();
        for (int i = 0; i < size; ++i)
        {
            SB_Variable local = (SB_Variable) ((DefaultMutableTreeNode) locals.getChildAt(i))
                    .getUserObject();
            _variables.add(local);
        }
        // add parameters
        DefaultMutableTreeNode params = catalog.findNode(poly._parent, catalog._behaviors);
        size = params.getChildCount();
        for (int i = 0; i < size; ++i)
        {
            SB_Parameter param = (SB_Parameter) ((DefaultMutableTreeNode) params.getChildAt(i))
                    .getUserObject();
            _variables.add(param);
        }

        if (!_returnsValue)
           Collections.sort(_actionsBehaviors, this);
       Collections.sort(_predicates, this);
       Collections.sort(_variables, this);
        
        //initialize the class Map needed when completing "."
        JavaScript javaScript = projectBar.getDataModel().getJavaScript();
        _completionHelper.initializeContent(_variables,
              javaScript.getImportedJavaClasses()); 
    }

    // @kp added for jdk1.3 - should be REMOVED if we use jdk1.4+
    // public synchronized FocusListener[] getFocusListeners() {
    // return (FocusListener[]) (getListeners(FocusListener.class));
    // }
    protected void clearNames()
    {
        _actionsBehaviors.removeAllElements();
        _predicates.removeAllElements();
        _variables.removeAllElements();
        _matchList.removeAllElements();
        _matchSel = -1;
        _needToComplete = false;
        _completionHelper.clearContent();
    }

    public int compare(Object object1, Object object2)
    {
        return object1.toString().compareToIgnoreCase(object2.toString());
    }

    protected void performAutocomplete(String expr, int pos)
    {
        _matchList.removeAllElements();
        _needToComplete = false;
        
        ParseInfo info = new ParseInfo();
        _completionHelper.parseDot(expr,pos,info);
        if (info.index != -1) {
           _needToComplete = _completionHelper.matchPartialDot(_matchList, info.funcName, info.paramName);
        }  else {
            parseFunction(expr, pos, info);
            if (_returnsValue)  {//ER the variable returnsValue is always false?
               info.paren = -1;   // ignore parent
            }
            
            if (info.index != -1){  // parameter parsed, i.e. current position within parenthesis
               matchPartialFunction(_matchList, info.paramName, info.paren == 0);
               matchPartialVariable(_matchList, info.paramName);
               _completionHelper.matchPartialClassName(_matchList, info.paramName);
               if (_matchList.isEmpty()==false) { // parameter is partial predicate or variable
                  _needToComplete = true;
               }
               else{   // parameter is not partial predicate or variable, move to function
            	   if (_glassPane != null) {
            		   matchFunction(_matchList, info.funcName, info.index, info.paren == 1);
            		   if (_matchList.isEmpty()==false){   // function found
            			   if (info.paren == 1){
            			   }
            		   }
            		   else{   // function unknown
            		   }
            	   }
               }
            }
            else{   // parameter not parsed, i.e. current position not within parenthesis
               matchPartialFunction(_matchList, info.funcName, info.paren == 0);   // partial function
               /*
                if (info.paren != 0)
                */
               matchPartialVariable(_matchList, info.funcName);                  // partial variable
               _completionHelper.matchPartialClassName(_matchList, info.funcName);
               if (!_matchList.isEmpty())
                  _needToComplete = true;
            }
         }
        
        if (_filterByType  && info.paren > 0) {
        	DefaultMutableTreeNode funcNode = null;
        	if (info.paren == 1 && !_returnsValue)
        		funcNode = getActionBehavior(info.funcName);
        	else
        		funcNode = getPredicate(info.funcName);
        	if (funcNode != null) {
        		int index = info.index;
        		if (index != -1 && index < funcNode.getChildCount()) {
        			DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) funcNode.getChildAt(info.index);
                    SB_Parameter param = ((SB_Parameter) paramNode.getUserObject());
                    String type = param.getFullTypeName();
                    
//                    System.out.println("type = " + type);
                    
                    for (int i = _matchList.size() - 1; i >= 0; i--) {
                    	String matchText = (String) _matchList.get(i);
                    	int typePos = matchText.lastIndexOf(":");
                    	if (typePos != -1) {
                    		typePos += 2;
//                    		System.out.println(matchText);
//                    		System.out.println(matchText.substring(typePos));
                    		if (!matchText.substring(typePos).equals(type))
                    			_matchList.remove(i);
                    	}
                    }
        		}
        		if (index != -1 && index >= funcNode.getChildCount()) {
        			_matchList.removeAllElements();
        		}
        	}
        }

         //show the completions (if any)
         if (_matchList.isEmpty()){
          _matchSel = -1;
          if (_glassPane != null)
        	  _glassPane.setText(null);
         }
         else{
           _matchSel = 0;
           if (_glassPane != null)
        	   _glassPane.setText(generateMatchText(_matchList, _matchSel));
         }
         
         for (SB_AutocompleteListener listener : _listeners) {
//        	 System.out.println(info);
//        	 System.out.println(_matchList);
//        	 System.out.println(_matchSel);
//        	 System.out.println("----------");
        	 listener.matchListChanged(_matchList, info.funcName, info.paramName, info.index);
         }

      /*  // search for latest string pattern of the form PREDICATE(...,PARAMETER
        // note PARAMETER could be a partial predicate
        // also note PARAMETER not parsed when current position not within
        // parenthesis
        SB_ParseInfo info = new SB_ParseInfo();
        parseFunction(expr, pos, info);
        if (_returnsValue)
            info.paren = -1; // ignore paren
        if (info.index != -1) // parameter parsed, i.e. current position
        // within parenthesis
        {
        	if (info.paramName != null && info.paramName.indexOf('.') > 0)
        	{
        		matchClassMemberOrMethod(info.paramName, false, info.index);
        	}
        	else
        	{
        		matchPartialFunction(_matchList, info.paramName, info.paren == 0);
        		matchPartialVariable(_matchList, info.paramName);
        		matchPartialExtra(_matchList, expr, pos, info);
        	}
        	
            if (!_matchList.isEmpty()) // parameter is partial predicate or
            // variable
            {
                // look for '(' to auto-complete
                // if (bOpenParen || bNonAlphaNum)
                // OnDblclkListPred();
                _needToComplete = true;
            } else
            // parameter is not partial predicate or variable, move to function
            {
            	if (info.funcName != null && info.funcName.indexOf('.') > 0)
            	{
            		matchClassMemberOrMethod(info.funcName, (info.paren == 1), info.index);
            	}
            	else
            	{
                    matchFunction(_matchList, info.funcName, info.index, info.paren == 1);
            	}
            	
                if (!_matchList.isEmpty()) // function found
                {
                    if (info.paren == 1)
                    {
                    }
                } else
                // function unknown
                {
                }
            }
        } else
        // parameter not parsed, i.e. current position not within parenthesis
        {
        	if (info.funcName != null && info.funcName.indexOf('.') > 0)
        	{
        		matchClassMemberOrMethod(info.funcName, (info.paren == 0), info.index);
        	}
        	else
        	{
        		matchPartialFunction(_matchList, info.funcName, info.paren == 0); // partial function
        		if (info.paren != 0)
        			matchPartialVariable(_matchList, info.funcName); // partial variable
        	}
        	
            if (!_matchList.isEmpty())
                _needToComplete = true;
        }

        if (_matchList.isEmpty())
        {
            _matchSel = -1;
            _glassPane.setText(null);
        } else
        {
            _matchSel = 0;
            _glassPane.setText(generateMatchText(_matchList, _matchSel));
        } */
    }
    
    private String generateMatchText(Vector matchList, int matchIndex){
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

   /* protected String generateMatchText(Vector matchList, int matchSel)
    {
        String text = "";
        String line;
        int pos;
        int size = matchList.size();
    	int start = -1, end = -1;
        for (int i = 0; i < size; ++i)
        {
            line = (String) matchList.get(i);
            pos = line.indexOf('@');
            if (pos >= 0)
            {
            	start = Integer.parseInt(line.substring(pos+1, line.indexOf(',', pos)));
            	end = Integer.parseInt(line.substring(line.indexOf(',', pos)+1));
            	line = line.substring(0, pos);
            }
            
            if (i == matchSel)
            {
                if (line.charAt(0) == '"')
                    pos = line.length();
                else
                {
                    pos = line.indexOf('(');
                    if (pos == -1)
                        pos = line.indexOf(':') - 1;
                }
                if (start >= 0 && end >= start)
                	line = "<B>" + line.substring(0, pos) + "</B>" + line.substring(pos, start) + "<B>" + line.substring(start, end) + "</B>" + line.substring(end);
                else
                	line = "<B>" + line.substring(0, pos) + "</B>" + line.substring(pos);
            }
            text += line;
            if (i < size - 1)
                text += "<BR>\n";
        }
        return text;
    } */

    protected class SB_StackData
    {

        int first, count; // mid string location

        int index; // parameter index

        SB_StackData(int f)
        {
            first = f;
            count = 0;
            index = 0;
        }
    }

    // in: expression and selection position
    // out: function, parameter, parameter index, parenthesis level
    protected void parseFunction(String expr, int pos, ParseInfo info)
    {
        Stack pred_stack = new Stack();
        pred_stack.push(new SB_StackData(0));
        boolean in_quotes = false;
        SB_StackData stackData;
        int j = -1;
        for (int i = 0; i < pos; i++)
        {
            char c = expr.charAt(i);
            if (c == '"')
            {
                in_quotes = !in_quotes;
                if (in_quotes)
                    j = i;
            }
            if (in_quotes)
                continue;
            stackData = (SB_StackData) pred_stack.peek();
            switch (c)
            {
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
                if (pred_stack.empty())
                {
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
        if (info.paren > 0)
        {
            stackData = (SB_StackData) pred_stack.peek();
            if (in_quotes || (pos > 0 && expr.charAt(pos - 1) == '"'))
                info.paramName = expr.substring(j, pos);
            else
                info.paramName = expr.substring(stackData.first, stackData.first + stackData.count);
            info.index = stackData.index;
            pred_stack.pop();
        } else
        {
            info.index = -1;
        }
        stackData = (SB_StackData) pred_stack.peek();
        info.first = stackData.first;
        info.funcName = expr.substring(stackData.first, stackData.first + stackData.count);
    }

    // if found, select given function
    protected void matchFunction(Vector matchList, String text, int index, boolean firstParen)
    {
        Vector functions;
        if (firstParen)
            functions = _actionsBehaviors;
        else
            functions = _predicates;
        
        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;
        
        int size = functions.size();
        for (int i = 0; i < size; i++)
        {
            treeNode = (DefaultMutableTreeNode) functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.equals(funcName))
            {
                matchList.add(getFullName(treeNode, index));
                return;
            }
        }
    }

    protected String getFullName(DefaultMutableTreeNode treeNode, int index)
    {
        SB_Function function = ((SB_Function) treeNode.getUserObject());
        String fullName = function.getName() + "(";
        DefaultMutableTreeNode childNode;
        SB_Parameter param;
        int size = treeNode.getChildCount();
        for (int i = 0; i < size; ++i)
        {
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
    
	

    // if found, select given partial function
    protected void matchPartialFunction(Vector matchList, String text, boolean firstParen)
    {
    	if (_glassPane != null) {
	        if (text.length() == 0)
	            return;
    	}

        Vector functions;
        if (firstParen)
            functions = _actionsBehaviors;
        else
            functions = _predicates;
        
        DefaultMutableTreeNode treeNode;
        SB_Function function;
        String funcName;
        int size = functions.size();
        for (int i = 0; i < size; i++)
        {
            treeNode = (DefaultMutableTreeNode) functions.get(i);
            function = ((SB_Function) treeNode.getUserObject());
            funcName = function.getName();
            if (text.regionMatches(0, funcName, 0, text.length()))
            {
                matchList.add(getFullName(treeNode, -1));
            }
            // else if (found)
            // return;
        }
    }

    // if found, select given partial variable
    protected void matchPartialVariable(Vector matchList, String text)
    {
    	if (_glassPane != null) {
	        if (text.length() == 0)
	            return;
    	}

        SB_Variable var;
        String varName;
        int size = _variables.size();
        for (int i = 0; i < size; i++)
        {
            var = (SB_Variable) _variables.get(i);
            varName = var.getName();
            if (text.regionMatches(0, varName, 0, text.length()))
            {
//                matchList.add(var.toString());
                matchList.add(var.getName() + " : " + var.getFullTypeName());
            }
            // else if (found)
            // return;
        }
    }
    
    class SB_GlassPane extends JPanel{

       public SB_GlassPane(){
        setLayout(null);
        setOpaque(false);
        
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        int colorValue = Integer.parseInt("FFFF8C", 16);
        list.setBackground(new Color(colorValue));
        list.setCellRenderer(new MatchListRenderer());
        
        completionList = new JScrollPane(list);
        add(completionList);
       }

       public void setLocation(JTextField textField){
        Container container = textField.getParent();
        while (container != null && !(container instanceof Window))
           container = container.getParent();
           
        int offset = 0;
        if (container instanceof JFrame) {
           ((JFrame)container).setGlassPane(this);
           offset = 4;
        }
        else if (container instanceof JDialog)
           ((JDialog)container).setGlassPane(this);
           
        try{
           Point textFieldLocation = textField.getLocationOnScreen();
           Point windowLocation = container.getLocationOnScreen();
           list.setLocation(textFieldLocation.x - windowLocation.x, textFieldLocation.y - windowLocation.y + offset);
           completionList.setLocation(textFieldLocation.x - windowLocation.x, textFieldLocation.y - windowLocation.y + offset);
        }
        catch (IllegalComponentStateException ex){
           // TODO
        }
       }

       /**
        * updates the glass panel label with the given text 
        * @param text - html formated text indicating the completions and the current selected
        *    completions.
        */
       public void setText(String text){
        list.removeAll();
        
        if (text == null || text.length() == 0) {
           completionList.setVisible(false);
           completionList.validate();
        }
        else {
          final String[] textList = text.split("<BR>\n");
          if (textList.length > 0 ) {
             list.setModel(new AbstractListModel() {
                @Override
                public int getSize() {
                   return textList.length;
                }

                @Override
                public Object getElementAt(int index) {
                   return textList[index]; 
                }
             });

            adjustPaneSize();  
          }
        }
       }
       
       /**
        * set the size of the scroll pane containing the label such that
        * it "fit" the label.
        */
       protected void adjustPaneSize() {
        // set the size of the scroll pane to "fit" the label 
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
              completionList.invalidate();
              completionList.validate(); //calculate new label dimensions 

              //maximum width/height for the scroll panel containing the 
              //list with the completions
              int maxW = 400;
              int maxH = 300;

              //todo the max sizes should depend on the available space and the
              // location of the caret. 

              //actual sizes for the scroll bar
              Dimension preferredSize = completionList.getPreferredSize();

              int w = preferredSize.width;
              int h = preferredSize.height;

              //account for the space taken by the bars (if any)
              Dimension listPreferredSize = list.getPreferredSize();

              if (completionList.getHorizontalScrollBar()!= null) {
                 if (w>=maxW ) {
                    //the width of the panel will be set to maxW which will cause the
                    // horizontal bar to show up, which means more height might be needed
                    h += completionList.getHorizontalScrollBar().getHeight();
                 }
                 else {
                    if (!completionList.getHorizontalScrollBar().isVisible() ) {
                       //sometimes the panel preferred size includes space for the bar although is not visible
                       //compare the label size and the panel size. If there is not bar, the difference of those
                       //values should be at most the size of bar size
                       if (preferredSize.height - listPreferredSize.height > completionList.getHorizontalScrollBar().getHeight()) {
                          h -= completionList.getHorizontalScrollBar().getHeight();
                       }
                    }
                 }
              }

              if (completionList.getVerticalScrollBar()!= null) {
                 if (h >=maxH) {
                    //the height of the panel will be set to maxH which will cause the
                    // vertical bar to show up, which means more width might be needed
                    w += completionList.getVerticalScrollBar().getWidth();
                 }
                 else {
                    if (!completionList.getVerticalScrollBar().isVisible()) {
                       //sometimes the panel preferred size includes space for the bar although is not visible
                       //compare the label size and the panel size. If there is not bar, the difference of those
                       //values should be at most the size of bar size
                       if (preferredSize.width -listPreferredSize.width > completionList.getVerticalScrollBar().getWidth()) {
                          w -= completionList.getVerticalScrollBar().getWidth();
                       }
                    }  
                 }
              }
              
              if (list.getModel().getSize() == 1) { // special case
                 h = listPreferredSize.height + 20;
              }
              
              
              w = Math.min(w, maxW);
              h = Math.min(h, maxH);

              Dimension d = new Dimension(w,h);            
              completionList.setSize(d);
              completionList.setVisible(true);
              completionList.invalidate();
              completionList.validate();
              
           }
        });
       }
       
       /**
        * like {@link #setText(String)} but places the vertical scroll bar (if any)
        * so that the selectedIndex is shown at the start of the window
        * 
        * @param text
        * @param selectedIndex
        * @param total
        */
       public void setText(String text, int selectedIndex, int total) {
          setText(text);
          if (text == null || text.length() == 0)  return;
          //move the scrollbar to show the selection
          if (completionList.getVerticalScrollBar() != null) {
             int range = completionList.getVerticalScrollBar().getMaximum() - completionList.getVerticalScrollBar().getMinimum();
             int val = (int)(((float)(selectedIndex*range)) / ((float) total));
             int value = completionList.getVerticalScrollBar().getMinimum() + val;
             completionList.getVerticalScrollBar().setValue(value);
          }
       }
       
       private JList list;
       /** scroll pane containing the label **/
       private JScrollPane completionList;
       
     }
    
    class MatchListRenderer extends JLabel implements ListCellRenderer {
       
       public MatchListRenderer() {
          setFont(new Font("Arial", Font.PLAIN, 12));
       }
       
       
       @Override
       public Component getListCellRendererComponent(JList list, Object value,
             int index, boolean isSelected, boolean cellHasFocus) {
          String str = value.toString();
          setText("<html>" + str + "</html>" );
          return this;
       }
       
     }

	protected void startAutoComplete() {
//		setCaretPosition(0);
		performAutocomplete("", 0);
	}

	public DefaultMutableTreeNode getActionBehavior(String funcName) {
		return getFunction(funcName, _actionsBehaviors);
	}

	public DefaultMutableTreeNode getPredicate(String funcName) {
		return getFunction(funcName, _predicates);
	}

	protected DefaultMutableTreeNode getFunction(String funcName, Vector functions) {
		int size = functions.size();
        for (int i = 0; i < size; i++) {
        	DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) functions.get(i);
            SB_Function func = ((SB_Function) treeNode.getUserObject());
            if (func.getName().equals(funcName)) {
            	return treeNode;
            }
        }
        return null;
	}

	public void changeMatchSelection(String sel) {
		if (sel != null) {
			int index = 0;
			for (Object match : _matchList) {
				String matchText = (String) match;
				if (matchText.startsWith(sel)) {	// found
					_matchSel = index;
					return;
				}
				index++;
			}
		}
		_matchSel = -1;
	}
}
