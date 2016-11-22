package com.stottlerhenke.simbionic.engine.comm;
import java.io.Serializable;
import java.util.*;

import com.stottlerhenke.simbionic.api.SB_Exception;


public class SB_CommGroup implements Serializable  
{
  private String _id;
  private ArrayList _members = new ArrayList();

  public SB_CommGroup(String id) {
    _id = id;
  }
  public void RemoveMember(SB_CommLink link){
    SB_CommLink it;
    for (int i = 0; i < _members.size(); i++){
      it = (SB_CommLink)_members.get(i);
      if (it == link){
        _members.remove(it);
        return;
      }
    }
  }

  public void AddMember(SB_CommLink link){
    _members.add( link );
  }

  public void SendMsg(SB_CommMsg msg)
  throws SB_Exception{
    SB_CommLink it;
    for (int i = 0; i < _members.size(); i++){
      it = (SB_CommLink)_members.get(i);
      it.Receive(new SB_CommMsg(msg));
    }
  }
  public String GetId() { return _id; }
  public int GetNumMembers() { return _members.size(); }
}