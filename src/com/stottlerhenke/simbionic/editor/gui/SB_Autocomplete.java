package com.stottlerhenke.simbionic.editor.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;
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
import com.stottlerhenke.simbionic.editor.gui.autocomplete.AutoCompleteSimbionicProjectDefinitions;
import com.stottlerhenke.simbionic.editor.gui.autocomplete.SB_GlassPane;


/**
 * XXX: A more appropriate name would be SB_AutocompleteTextField, by analogy
 * to SB_AutocompleteTextArea.
 * */
public class SB_Autocomplete extends JTextField
{
   
    protected SimBionicEditor _editor;

    private final SB_GlassPane _glassPane = new SB_GlassPane();

    protected boolean _escapePressed = false;

    protected boolean _ignoreCaretUpdate = false;
    
    protected boolean _ignoreFocusGained = false;

    private AutoCompleteSimbionicProjectDefinitions
    _autocompletionSBHelper = new AutoCompleteSimbionicProjectDefinitions();

    protected List<SB_AutocompleteListener> _listeners = new ArrayList<SB_AutocompleteListener>();

	protected boolean _filterByType = false;
    
    public SB_Autocomplete(int cols, SimBionicEditor editor)
    {
        super(cols);
        setFocusTraversalKeysEnabled(false);

        _editor = editor;

        addFocusListener(new FocusListener()
        {

            public void focusGained(FocusEvent event)
            {
            	// TODO rth custom editors set _ignoreFocusGained so that
            	// we don't reinvoke the editor when it returns the focus
            	// after editing
            	if (!_ignoreFocusGained) { 
            		if (!tryCustomEdit()) {
            				clearNames();
            				initializeNames();
	            			_glassPane.setLocation(SB_Autocomplete.this);
	            			_glassPane.setVisible(true);
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
                    if (_glassPane.list.getSelectedIndex() >= 0 && _autocompletionSBHelper.needsToComplete()) {
                        _autocompletionSBHelper.setMatchSelectionIndex(_glassPane.list.getSelectedIndex());
                        onTextSelected();
                     }
	                _glassPane.setVisible(false);
	                clearNames();
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

    /**
     * This is almost identical to
     * {@link SB_AutocompleteTextArea#processKeyEvent(KeyEvent)}; the main
     * difference is the removal of {@code super.processKeyEvent(event);}
     * (as "move up and down" is irrelevant for a JTextField.)
     * */
    public void processKeyEvent(KeyEvent event) {
//      if (_glassPane != null) {
          int id = event.getID();
          switch (event.getKeyCode()){
          case KeyEvent.VK_UP:
              if (id == KeyEvent.KEY_PRESSED){
                  int size = _autocompletionSBHelper.getNumberOfMatches();
                  if (size > 1){
                      _autocompletionSBHelper.decreaseMatchSelectionIndex();
                      if (_autocompletionSBHelper.getMatchSelectionIndex() == -1) {
                          _autocompletionSBHelper.setMatchSelectionIndex(size - 1);
                      }
                      _glassPane.setText(
                            _autocompletionSBHelper.generateCompletionsText(),
                            _autocompletionSBHelper.getMatchSelectionIndex(),
                            _autocompletionSBHelper.getNumberOfMatches());
                  }
              }
              return;
          case KeyEvent.VK_DOWN:
              if (id == KeyEvent.KEY_PRESSED){
                  int size = _autocompletionSBHelper.getNumberOfMatches();
                  if (size > 1){
                      _autocompletionSBHelper.increaseMatchSelectionIndex();
                      if (_autocompletionSBHelper.getMatchSelectionIndex() == size) {
                          _autocompletionSBHelper.setMatchSelectionIndex(0);
                      }
                      _glassPane.setText(
                            _autocompletionSBHelper.generateCompletionsText(),
                            _autocompletionSBHelper.getMatchSelectionIndex(),
                            _autocompletionSBHelper.getNumberOfMatches());
                  }
              }
              return;
          case KeyEvent.VK_TAB:
          case KeyEvent.VK_ENTER:
              if (id == KeyEvent.KEY_PRESSED && _autocompletionSBHelper.needsToComplete()) {
                      onTextSelected();
                  return;
              }
              else
                  break;// allows super.processKeyEvent
              /*
         return;
               */
          case KeyEvent.VK_ESCAPE:
              if (id == KeyEvent.KEY_PRESSED){
//        escapePressed = true;
                  //_editor.getFrame().requestFocus();
              }
              return;
          default:
          }
//      }
      
      super.processKeyEvent(event);
    }

    // XXX: Identical to SB_AutocompleteTextArea behavior
    protected void onTextSelected() {
        Optional<String> insertion
        = _autocompletionSBHelper.getSelectedMatchInsertion();
        insertion.ifPresent(str -> {
            str = getInsertString(str);
            String text = getText();
            int caretPos = getCaretPosition();
            text = text.substring(0, caretPos) + str
                    + text.substring(caretPos);
            setText(text);
            caretPos += str.length();
            setCaretPosition(caretPos);
            _autocompletionSBHelper.setNeedsToComplete(false);
        });
    }

    @Override
    protected Document createDefaultModel(){
       return new PlainDocument(){
          @Override
          public void insertString(int offs, String str, AttributeSet a) throws BadLocationException{
             if (str == null) return;
             boolean needToClean = false;
             char c = ' ';
             Optional<String> optLine
             = Optional.ofNullable(_autocompletionSBHelper.getSelectedText());
             if (_autocompletionSBHelper.needsToComplete()
                 && optLine.isPresent()
                 && str.length() == 1) {
                c = str.charAt(0);
                String line = optLine.get();
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

    //XXX: Identical to SB_AutocompleteTextArea behavior
    protected String getInsertString(String str) {
        return _autocompletionSBHelper.getInsertString(str, getText(),
                getCaretPosition());
    }

    //XXX: Identical to SB_AutocompleteTextArea behavior
    protected boolean tryCustomEdit() {
    	// if the expression can be edited with a custom editor,
    	// launch the custom editor
    	String text = getText();
    	final ParseInfo info = new ParseInfo();
        _autocompletionSBHelper.parseFunction(text, text.length(), info);
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

    //XXX: Identical to SB_AutocompleteTextArea behavior
    protected void initializeNames()
    {
        _autocompletionSBHelper.initializeNames();

             _glassPane.setText(null);

    }

    //XXX: Identical to SB_AutocompleteTextArea behavior
    protected void clearNames()
    {
        _autocompletionSBHelper.clearNames();
    }

    protected void performAutocomplete(String expr, int pos) {
        _autocompletionSBHelper.clearMatchList();
        _autocompletionSBHelper.setNeedsToComplete(false);
        
        ParseInfo info = new ParseInfo();
        _autocompletionSBHelper.parseDot(expr,pos,info);
        if (info.index != -1) {
            _autocompletionSBHelper.setNeedsToComplete( _autocompletionSBHelper.matchPartialDot(info.funcName, info.paramName));
        }  
        else {
            _autocompletionSBHelper.parseFunction(expr, pos, info);
            if (_autocompletionSBHelper.returnsValue())  {
               info.paren = -1;   // ignore parent
            }
            
            if (info.index != -1){  // parameter parsed, i.e. current position within parenthesis
               matchPartialFunction(info.paramName, info.paren == 0);
               matchPartialVariable(info.paramName);
               _autocompletionSBHelper.matchPartialClassName(info.paramName);
               if (_autocompletionSBHelper.hasMatches()) { // parameter is partial predicate or variable
                   _autocompletionSBHelper.setNeedsToComplete(true);
               }
               else{   // parameter is not partial predicate or variable, move to function
                       _autocompletionSBHelper.matchFunction(info.funcName, info.index, info.paren == 1);
                       if (_autocompletionSBHelper.hasMatches()){   // function found
                           if (info.paren == 1){
                           }
                       }
                       else{   // function unknown
                       }
               }
            }
            else{   // parameter not parsed, i.e. current position not within parenthesis
               matchPartialFunction(info.funcName, info.paren == 0);   // partial function
               /*
                if (info.paren != 0)
                */
               matchPartialVariable(info.funcName);                  // partial variable
               _autocompletionSBHelper.matchPartialClassName(info.funcName);
               if (_autocompletionSBHelper.hasMatches())
                   _autocompletionSBHelper.setNeedsToComplete(true);
            }
         }
        
        if (_filterByType  && info.paren > 0) {
            DefaultMutableTreeNode funcNode = null;
            if (info.paren == 1 && !_autocompletionSBHelper.returnsValue())
                funcNode = getActionBehavior(info.funcName);
            else
                funcNode = getPredicate(info.funcName);
            if (funcNode != null) {
                int index = info.index;
                if (index != -1 && index < funcNode.getChildCount()) {
                    DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) funcNode.getChildAt(info.index);
                    SB_Parameter param = ((SB_Parameter) paramNode.getUserObject());
                    String type = param.getFullTypeName();
                 
                    for (int i = _autocompletionSBHelper.getNumberOfMatches() - 1; i >= 0; i--) {
                        String matchText = (String) _autocompletionSBHelper.getMatchWithFullTypeNames(i);
                        int typePos = matchText.lastIndexOf(":");
                        if (typePos != -1) {
                            typePos += 2;
                            if (!matchText.substring(typePos).equals(type))
                                _autocompletionSBHelper.removeMatch(i);
                        }
                    }
                }
                if (index != -1 && index >= funcNode.getChildCount()) {
                    _autocompletionSBHelper.clearMatchList();
                }
            }
        }

         //show the completions (if any)
         if (!_autocompletionSBHelper.hasMatches()){
             _autocompletionSBHelper.setMatchSelectionIndex(-1);
              _glassPane.setText(null);
         }
         else{
             _autocompletionSBHelper.setMatchSelectionIndex(0);
               _glassPane.setText(_autocompletionSBHelper.generateCompletionsText());
         }
         
        for (SB_AutocompleteListener listener : _listeners) {
            listener.matchListChanged(
                    _autocompletionSBHelper.getMatchInsertionStrings(),
                    info.funcName, info.paramName, info.index);
        }

    }

    // if found, select given partial function
    protected void matchPartialFunction(String text, boolean firstParen) {
            if (text.length() == 0)
                return;
        _autocompletionSBHelper.matchPartialFunction(text, firstParen);
    }

    // if found, select given partial variable
    protected void matchPartialVariable(String text) {
            if (text.length() == 0)
                return;

        _autocompletionSBHelper.matchPartialVariable(text);
    }

    //XXX: Identical to SB_AutocompleteTextArea
    protected void startAutoComplete() {
        //setCaretPosition(0);
        performAutocomplete("", 0);
    }

    //XXX: Identical to SB_AutocompleteTextArea behavior
    public DefaultMutableTreeNode getActionBehavior(String funcName) {
        return _autocompletionSBHelper.getActionBehavior(funcName);
    }

    //XXX: Identical to SB_AutocompleteTextArea behavior
    public DefaultMutableTreeNode getPredicate(String funcName) {
        return _autocompletionSBHelper.getPredicate(funcName);
    }

    //XXX: Identical to SB_AutocompleteTextArea behavior
    public void changeMatchSelection(String sel) {
        _autocompletionSBHelper.changeMatchSelection(sel);
    }

    /**
     * XXX: Access to the replacement to {@code _returnsValue}
     * */
    public void setReturnsValue(boolean returnsValue) {
        _autocompletionSBHelper.setReturnsValue(returnsValue);
    }

    /**
     * XXX: Access to the replacement to {@code _returnsValue}
     * */
    public boolean returnsValue() {
        return _autocompletionSBHelper.returnsValue();
    }
}
