package com.stottlerhenke.simbionic.editor.gui;

import java.util.Vector;

public interface SB_AutocompleteListener {
	public void matchListChanged(Vector matchList, String funcName, String paramName, int paramIndex);
	public void matchSelectionChanged(String matchSel);
	public void completeExpression();
}
