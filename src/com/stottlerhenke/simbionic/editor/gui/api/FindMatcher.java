package com.stottlerhenke.simbionic.editor.gui.api;

import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;

/**
 * Interface for a function object that implements a matching condition
 * for a find method.
 * 
 */
public interface FindMatcher
{
    /**
     * Determines if the given element meets the find criteria.
     * @param element
     * @return true if the element meets the criteria, otherwise false
     */
    public boolean matches(SB_Drawable element);
}
