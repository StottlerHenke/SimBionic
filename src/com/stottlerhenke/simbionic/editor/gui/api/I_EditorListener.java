package com.stottlerhenke.simbionic.editor.gui.api;

/**
 * Callback interface implemented by callers of custom editors.
 * 
 *
 */
public interface I_EditorListener {
	
	/**
	 * Notify the caller that editing was canceled without any change.
	 * @param source
	 */
	public void editingCanceled(I_ExpressionEditor source);
	
	/**
	 * Notify the caller that editing was completed successfully and provide
	 * the resulting expression
	 * 
	 * @param source
	 * @param result the expression (replaces the original expression provided to the editor)
	 */
	public void editingCompleted(I_ExpressionEditor source, String result);
}
