package com.stottlerhenke.simbionic.editor.gui.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.stottlerhenke.simbionic.editor.SB_TypeManager;

/**
 * Registration API for custom editors. All custom editors register with an
 * instance of this class (obtained from the SimBionicEditor), which then
 * provides editor objects to components of the GUI as needed.
 * 
 * 
 */
public class EditorRegistry {
	public static final String EXPRESSION_TYPE_FUNCTION = "Function";

	public static final String EXPRESSION_TYPE_BINDING = "Binding";

	Map /* <String, List<I_ExpressionEditor>> */expressionEditorLists;

	public EditorRegistry() {
		expressionEditorLists = new HashMap();
	}

	/**
	 * Register an editor for expressions of a particular type and subtype.
	 * Valid types are defined in {@link ExpressionType}. The subtype of a
	 * functional expression (action, predicate, behavior, or class method
	 * invocation) is the name of the function being invoked. The subtype of a
	 * binding expression is the name of the variable type of the variable being
	 * bound, as returned by {@link SB_TypeManager#getTypeName(SB_VarType)}.
	 * 
	 * For example, to register a custom editor for an action called
	 * publishEvent, call
	 * <code>registerExpressionEditor(ExpressionType.FUNCTION, "publishEvent", editor);</code>
	 * 
	 * @param type
	 * @param subtype
	 * @param editor
	 * 
	 * @see ExpressionType
	 * @see SB_TypeManager
	 * @see SB_VarType
	 */
	public void registerExpressionEditor(String type, String subtype,
			I_ExpressionEditor editor) {
		List editors = (List) expressionEditorLists.get(type + subtype);
		if (editors == null) {
			editors = new ArrayList();
			expressionEditorLists.put(type + subtype, editors);
		}
		editors.add(editor);
	}

	/**
	 * Unregister an expression editor.
	 * 
	 * @param type
	 * @param subtype
	 * @param editor
	 */
	public void unregisterExpressionEditor(String type, String subtype,
			I_ExpressionEditor editor) {
		List editors = (List) expressionEditorLists.get(type + subtype);
		if (editors != null)
			editors.remove(editor);
	}

	/**
	 * Retrieve the expression editor for a given expression. Each editor
	 * registered for the given type and subtype is queried to see if it is
	 * applicable to the expression, in order from last registered to first
	 * registered, and the first applicable editor is returned. If no applicable
	 * editor is found in this list, the editors registered for the given type
	 * (with subtype <code>""</code>) are queried. If no applicable editor is
	 * found, the method returns <code>null</code>.
	 * 
	 * @param type
	 * @param subtype
	 * @param expression
	 */
	public I_ExpressionEditor getExpressionEditor(String type, String subtype,
			String expression) {
		List editors = (List) expressionEditorLists.get(type + subtype);
		if (editors == null) {
			return null;
		}
		ListIterator iter = editors.listIterator(editors.size());
		while (iter.hasPrevious()) {
			I_ExpressionEditor editor = (I_ExpressionEditor) iter.previous();
			if (editor.isEditorApplicable(expression)) {
				return editor;
			}
		}
		if (!subtype.equals("")) {
			editors = (List) expressionEditorLists.get(type);
			if (editors == null) {
				return null;
			}
			iter = editors.listIterator(editors.size());
			while (iter.hasPrevious()) {
				I_ExpressionEditor editor = (I_ExpressionEditor) iter
						.previous();
				if (editor.isEditorApplicable(expression)) {
					return editor;
				}
			}
		}
		return null;
	}
}
