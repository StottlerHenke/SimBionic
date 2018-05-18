package com.stottlerhenke.simbionic.editor.gui;

import java.util.List;

import com.stottlerhenke.simbionic.editor.SB_Binding;

public interface SB_BindingsHolder
{
  public List<SB_Binding> getBindings();
  public void setBindings(List<SB_Binding> bindings);
  public int getBindingCount();
  public SB_Binding getBinding(int i);
  public void addBinding(SB_Binding binding);
  public void addBinding(int i, SB_Binding binding);
  public void removeBinding(int i);
  public void updateBindings();
}
