package com.stottlerhenke.simbionic.editor.gui;

import java.util.List;

public interface SB_AutocompleteListener {

    /**
     * 2018-05-02 -jmm
     * <br>
     * XXX: The only implementor of this method expects strings only; moreover,
     * only the unannotated variable and function names are used. For now, it
     * appears to be acceptable to simply provide the insertion strings when
     * this is called.
     * */
    public void matchListChanged(
            List<String> matchInsertionsList,
            String funcName, String paramName, int paramIndex);

    public void matchSelectionChanged(String matchSel);

    public void completeExpression();
}
