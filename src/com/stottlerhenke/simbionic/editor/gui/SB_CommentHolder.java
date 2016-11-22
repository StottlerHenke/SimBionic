package com.stottlerhenke.simbionic.editor.gui;

public interface SB_CommentHolder
{
    static final public int FULL_LABEL = 0;
    static final public int TRUNCATED_LABEL = 1;
    static final public int COMMENT_LABEL = 2;

    public String getComment();

    public void setComment(String comment);

    public void updateComment();

    public int getLabelMode();

    public void setLabelMode(int mode);
}
