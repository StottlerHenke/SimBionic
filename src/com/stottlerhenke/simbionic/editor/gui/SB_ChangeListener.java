
package com.stottlerhenke.simbionic.editor.gui;

import com.stottlerhenke.simbionic.editor.SB_Behavior;

/**
 * Interface that can register to listen for changes to
 * the SimBionic model.
 *
 *
 */
public interface SB_ChangeListener
{
    /**
     * Invoked when a behavior is renamed.
     * @param behavior the renamed behavior
     * @param oldName the original name of the behavior
     */
    public void behaviorRenamed(SB_Behavior behavior, String oldName);
}
