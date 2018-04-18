package com.stottlerhenke.simbionic.editor.gui;

/**
 * UI objects that implement this interface to set/get their description.
 */
public interface I_DescriptionHolder {

    public String getDescription();

    public void setDescription(String description);

    /**
     *
     * @return true if this is a pre-defined object and the user should not generally be allowed to change the description.
     */
    public boolean isCore();

    public void setCore(boolean core);

    /**
     *
     * @return true if the description should be edited.
     */
    public boolean isCellEditable();
}
