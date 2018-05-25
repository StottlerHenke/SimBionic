package com.stottlerhenke.simbionic.editor.gui;

import java.util.List;

/**
 * 2018-05-25 -jmm
 * <br>
 * The only implementing class implements only obsolete behavior for this
 * interface that is never accessed in practice; this interface and its methods
 * will probably be removed, hence the the deprecation warnings.
 * */
@Deprecated
public interface SB_AutocompleteListener {

    /**
     * 2018-05-02 -jmm
     * <br>
     * XXX: The only implementor of this method expects strings only; moreover,
     * only the unannotated variable and function names are used. For now, it
     * appears to be acceptable to simply provide the insertion strings when
     * this is called.
     * */
    @Deprecated
    public void matchListChanged(
            List<String> matchInsertionsList,
            String funcName, String paramName, int paramIndex);

    @Deprecated
    public void matchSelectionChanged(String matchSel);

    @Deprecated
    public void completeExpression();
}
