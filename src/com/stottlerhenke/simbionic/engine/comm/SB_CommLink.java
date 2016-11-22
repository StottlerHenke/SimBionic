package com.stottlerhenke.simbionic.engine.comm;
import com.stottlerhenke.simbionic.engine.parser.*;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.*;

import java.io.Serializable;
import java.util.*;


public class SB_CommLink implements Serializable  
{
  private   SB_ID	_owner;
  private   SB_CommCenter	_commCenter;
  private   ArrayList		_incomingMsgs = new ArrayList();

  public SB_CommLink( SB_ID owner,SB_CommCenter comm){
    _owner = owner;
    _commCenter = comm;
    _commCenter.RegisterEntity(this);
  }

  public SB_CommMsg CurrentMsg(){
    if (_incomingMsgs.size() > 0){
      return (SB_CommMsg)_incomingMsgs.get(0);
    }
    return null;
  }

  public void NextMsg(){
    if (_incomingMsgs.size() > 0){
      //_incomingMsgs.remove(_incomingMsgs.size() - 1);
      _incomingMsgs.remove(0);
    }
  }

  /**
   * 2016-7-25
   * <p>
   * This implements the stated functionality of IsMsg as
   * described in the SimBionic User Guide (2016-07-01):
   * <tt>true</tt> is returned iff there is at least
   * one message in the (incoming) message queue;
   * <tt>false</tt> is returned otherwise.
   * */
  public boolean HasMsg() {
	  return _incomingMsgs.size() > 0;
  }

  public void Send(SB_CommMsg msg) throws SB_Exception
  {
    _commCenter.RouteMessage(msg);
  }

  public void Receive(SB_CommMsg msg){
    _incomingMsgs.add(msg);
  }

  public void JoinGroup( String groupId){
    _commCenter.JoinGroup(groupId,this);
  }

  public void QuitGroup( String groupId){
    _commCenter.QuitGroup(groupId,this);
  }

  public int GetNumMembers( String groupId){
    return _commCenter.GetNumMembers(groupId);
  }

  public void CreateBBoard( String bboardId, SB_Logger logger){
    _commCenter.CreateBlackboard(bboardId, logger);
  }

  public void DestroyBBoard( String bboardId){
    _commCenter.DestroyBlackboard(bboardId);
  }
  public void DestroyGroup(String groupId){
   _commCenter.DestroyGroup(groupId);
 }
  public void PostBBoard( String bboardId, String region,SB_Variable data)
  throws SB_Exception
  {
    _commCenter.PostBlackboard(bboardId,region,data);
  }

  public SB_Variable ReadBBoard( String bboardId, String region){
    return _commCenter.ReadBlackboard(bboardId,region);
  }

  public boolean IsBBoard( String bboardId){
    return _commCenter.IsBlackboard(bboardId);
  }

}