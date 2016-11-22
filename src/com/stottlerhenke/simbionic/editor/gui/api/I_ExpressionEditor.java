package com.stottlerhenke.simbionic.editor.gui.api;

/**
 * Custom expression editors must implement this interface.
 * 
 *
 */
public interface I_ExpressionEditor {	
	
	/**
	 * Defines any fine-grained conditions the expression must meet in order for the
	 * editor to apply (in addition to the course-grained conditions for which the
	 * editor is registered).
	 * 
	 * @param expression
	 * @return true if and only if the editor is applicable to the 
	 * given expression.
	 * 
	 * @see EditorRegistry#registerExpressionEditor(String, String, I_ExpressionEditor)
	 */
	public boolean isEditorApplicable(String expression);
	
	/**
	 * Edit an expression and notify the listener when editing is complete or
	 * canceled.
	 * 
	 * @param expression
	 * @param listener
	 */
	public void editObject(String expression, I_EditorListener listener);
}
