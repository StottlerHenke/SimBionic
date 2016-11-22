package com.stottlerhenke.simbionic.editor.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.stottlerhenke.simbionic.editor.SB_Parameter;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.AutoCompletionHelper.ParseInfo;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;
import com.stottlerhenke.simbionic.editor.gui.autocomplete.AutoCompleteSimbionicProjectDefinitions;
import com.stottlerhenke.simbionic.editor.gui.autocomplete.SB_GlassPane;


@SuppressWarnings("serial")
public class SB_AutocompleteTextArea extends RSyntaxTextArea {
   
    protected SimBionicEditor _editor;

    protected SB_GlassPane _glassPane = new SB_GlassPane();

    protected boolean _escapePressed = false;

    protected boolean _ignoreCaretUpdate = false;
    
    protected boolean _ignoreFocusGained = false;
        
    private AutoCompleteSimbionicProjectDefinitions _autocompletionHelper;
      
    protected List<SB_AutocompleteListener> _listeners = new ArrayList<SB_AutocompleteListener>();

	protected boolean _filterByType = false;
    
    public SB_AutocompleteTextArea(int rows,int cols, SimBionicEditor editor)
    {
        super(rows,cols);
        
        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        this.setCodeFoldingEnabled(true);

        _editor = editor;
        _autocompletionHelper = new AutoCompleteSimbionicProjectDefinitions();
        
        
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
            	// TODO rth custom editors set _ignoreFocusGained so that
            	// we don't reinvoke the editor when it returns the focus
            	// after editing
            	if (!_ignoreFocusGained) { 
            		if (!tryCustomEdit()) {
            			if (_glassPane != null) {
            				clearNames();
            				initializeNames();
	            			_glassPane.setLocation(SB_AutocompleteTextArea.this);
	            			_glassPane.setVisible(true);
            			}
            			// _editor._deleteItem.setAccelerator(null);
            			_escapePressed = false;
            		}
            	} else {
            		_ignoreFocusGained = false;
            		//EMILIO what to do hiere fireActionPerformed();
            	}
            }

            public void focusLost(FocusEvent event) {
            	if (_glassPane != null) {
	               if (_glassPane.list.getSelectedIndex() >= 0 && _autocompletionHelper.needsToComplete()) {
	                  _autocompletionHelper.setMatchSelectionIndex(_glassPane.list.getSelectedIndex());
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

        addCaretListener(new CaretListener(){
            public void caretUpdate(CaretEvent event)  {
            	_glassPane.setLocation(SB_AutocompleteTextArea.this);
                // used to make canvas more responsive
                if (_ignoreCaretUpdate)
                    return;

                if (ComponentRegistry.getFrame() != null
                        && ComponentRegistry.getFrame().getFocusOwner() != SB_AutocompleteTextArea.this)
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
    
    @Override
    public void processKeyEvent(KeyEvent event){       
//       if (_glassPane != null) {
    	   int id = event.getID();
    	   switch (event.getKeyCode()){
    	   case KeyEvent.VK_UP:
    		   super.processKeyEvent(event); //needed to navigate the input text area
    		   if (id == KeyEvent.KEY_PRESSED){
    			   int size = _autocompletionHelper.getNumberOfMatches();
    			   if (size > 1){
    				   _autocompletionHelper.decreaseMatchSelectionIndex();
    				   if (_autocompletionHelper.getMatchSelectionIndex() == -1) {
    					   _autocompletionHelper.setMatchSelectionIndex(size - 1);
    				   }
    				   if (_glassPane != null) {
    					   _glassPane.setText(_autocompletionHelper.generateCompletionsText(),_autocompletionHelper.getMatchSelectionIndex(),_autocompletionHelper.getNumberOfMatches());
    				   }
    				   else {
    					   for (SB_AutocompleteListener listener : _listeners) {
    						   listener.matchSelectionChanged(_autocompletionHelper.getSelectedText());
    					   }
    				   }
    			   }
    		   }
    		   return;
    	   case KeyEvent.VK_DOWN:
    		   super.processKeyEvent(event);//needed to navigate the input text area
    		   if (id == KeyEvent.KEY_PRESSED){
    			   int size = _autocompletionHelper.getNumberOfMatches();
    			   if (size > 1){
    				   _autocompletionHelper.increaseMatchSelectionIndex();
    				   if (_autocompletionHelper.getMatchSelectionIndex() == size) {
    					   _autocompletionHelper.setMatchSelectionIndex(0);
    				   }
    				   if (_glassPane != null)
    					   _glassPane.setText(_autocompletionHelper.generateCompletionsText(),_autocompletionHelper.getMatchSelectionIndex(),_autocompletionHelper.getNumberOfMatches());
    				   else {
  				         for (SB_AutocompleteListener listener : _listeners) {
				        	 listener.matchSelectionChanged(_autocompletionHelper.getSelectedText());
				         }
    				   }
    			   }
    		   }
    		   return;
    	   case KeyEvent.VK_TAB:
    	   case KeyEvent.VK_ENTER:
    		   if (id == KeyEvent.KEY_PRESSED && _autocompletionHelper.needsToComplete()) {
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
       String str = _autocompletionHelper.getSelectedText();
       if (str==null) return;
       
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
       _autocompletionHelper.setNeedsToComplete(false);
    }

    protected String getInsertString(String str) {
    	return _autocompletionHelper.getInsertString(str, getText(), getCaretPosition());
    }

    protected boolean tryCustomEdit() {
    	// if the expression can be edited with a custom editor,
    	// launch the custom editor
    	String text = getText();
    	final ParseInfo info = new ParseInfo();
        _autocompletionHelper.parseFunction(text, text.length(), info);
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
    
    protected void initializeNames() {
    	_autocompletionHelper.initializeNames();
       // if (_variables.size() > 0)
       //     return;

        if (_glassPane != null)
        	_glassPane.setText(null);
    }


    protected void performAutocomplete(String expr, int pos) {
    	_autocompletionHelper.clearMatchList();
    	_autocompletionHelper.setNeedsToComplete(false);
        
        ParseInfo info = new ParseInfo();
        _autocompletionHelper.parseDot(expr,pos,info);
        if (info.index != -1) {
        	_autocompletionHelper.setNeedsToComplete( _autocompletionHelper.matchPartialDot(info.funcName, info.paramName));
        }  
        else {
            _autocompletionHelper.parseFunction(expr, pos, info);
            if (_autocompletionHelper.returnsValue())  {
               info.paren = -1;   // ignore parent
            }
            
            if (info.index != -1){  // parameter parsed, i.e. current position within parenthesis
               matchPartialFunction(info.paramName, info.paren == 0);
               matchPartialVariable(info.paramName);
               _autocompletionHelper.matchPartialClassName(info.paramName);
               if (_autocompletionHelper.hasMatches()) { // parameter is partial predicate or variable
            	   _autocompletionHelper.setNeedsToComplete(true);
               }
               else{   // parameter is not partial predicate or variable, move to function
            	   if (_glassPane != null) {
            		   _autocompletionHelper.matchFunction(info.funcName, info.index, info.paren == 1);
            		   if (_autocompletionHelper.hasMatches()){   // function found
            			   if (info.paren == 1){
            			   }
            		   }
            		   else{   // function unknown
            		   }
            	   }
               }
            }
            else{   // parameter not parsed, i.e. current position not within parenthesis
               matchPartialFunction(info.funcName, info.paren == 0);   // partial function
               /*
                if (info.paren != 0)
                */
               matchPartialVariable(info.funcName);                  // partial variable
               _autocompletionHelper.matchPartialClassName(info.funcName);
               if (_autocompletionHelper.hasMatches())
            	   _autocompletionHelper.setNeedsToComplete(true);
            }
         }
        
        if (_filterByType  && info.paren > 0) {
        	DefaultMutableTreeNode funcNode = null;
        	if (info.paren == 1 && !_autocompletionHelper.returnsValue())
        		funcNode = getActionBehavior(info.funcName);
        	else
        		funcNode = getPredicate(info.funcName);
        	if (funcNode != null) {
        		int index = info.index;
        		if (index != -1 && index < funcNode.getChildCount()) {
        			DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) funcNode.getChildAt(info.index);
                    SB_Parameter param = ((SB_Parameter) paramNode.getUserObject());
                    String type = param.getFullTypeName();
                 
                    for (int i = _autocompletionHelper.getNumberOfMatches() - 1; i >= 0; i--) {
                    	String matchText = (String) _autocompletionHelper.getMatch(i);
                    	int typePos = matchText.lastIndexOf(":");
                    	if (typePos != -1) {
                    		typePos += 2;
                    		if (!matchText.substring(typePos).equals(type))
                    			_autocompletionHelper.removeMatch(i);
                    	}
                    }
        		}
        		if (index != -1 && index >= funcNode.getChildCount()) {
        			_autocompletionHelper.clearMatchList();
        		}
        	}
        }

         //show the completions (if any)
         if (!_autocompletionHelper.hasMatches()){
        	 _autocompletionHelper.setMatchSelectionIndex(-1);
          if (_glassPane != null)
        	  _glassPane.setText(null);
         }
         else{
        	 _autocompletionHelper.setMatchSelectionIndex(0);
           if (_glassPane != null)
        	   _glassPane.setText(_autocompletionHelper.generateCompletionsText());
         }
         
         for (SB_AutocompleteListener listener : _listeners) {
        	 listener.matchListChanged(_autocompletionHelper.getMatchList(), info.funcName, info.paramName, info.index);
         }

    }
    

    public DefaultMutableTreeNode getActionBehavior(String funcName) {
		return _autocompletionHelper.getActionBehavior(funcName);
	}

	public DefaultMutableTreeNode getPredicate(String funcName) {
		return _autocompletionHelper.getPredicate(funcName);
	}

	public void setReturnsValue (boolean returnsValue) {
		_autocompletionHelper.setReturnsValue(returnsValue);
	}
	
	public boolean returnsValue() {
		return _autocompletionHelper.returnsValue();
	}

    // if found, select given partial function
    protected void matchPartialFunction(String text, boolean firstParen) {
    	if (_glassPane != null) {
	        if (text.length() == 0)
	            return;
    	}
    	_autocompletionHelper.matchPartialFunction(text, firstParen);
    }

    // if found, select given partial variable
    protected void matchPartialVariable(String text) {
    	if (_glassPane != null) {
	        if (text.length() == 0)
	            return;
    	}

    	_autocompletionHelper.matchPartialVariable(text);
    }
    
    
   
	protected void startAutoComplete() {
//		setCaretPosition(0);
		performAutocomplete("", 0);
	}

	public void clearNames() {
	   _autocompletionHelper.clearNames();
	}
	

	public void changeMatchSelection(String sel) {
		_autocompletionHelper.changeMatchSelection(sel);
	}
}
