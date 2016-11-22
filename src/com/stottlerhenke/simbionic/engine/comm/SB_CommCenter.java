package com.stottlerhenke.simbionic.engine.comm;
import java.io.Serializable;
import java.util.*;

import com.stottlerhenke.simbionic.engine.SB_SingletonBook;
import com.stottlerhenke.simbionic.engine.parser.*;
import com.stottlerhenke.simbionic.api.SB_Exception;
import com.stottlerhenke.simbionic.common.SB_Logger;


public class SB_CommCenter implements Serializable 
{
  private HashMap _groups = new HashMap();
  private HashMap _bboards = new HashMap();
  private ArrayList _entityLinks = new ArrayList();

  public SB_CommCenter() {
  }
  public void CreateBlackboard( String id, SB_Logger logger)
  {
      // if the bboard already exists, don't create a new one
      if (!_bboards.containsKey(id))
          _bboards.put(id, new SB_Blackboard(id, logger));
  }

  public void DestroyBlackboard( String id)
  {
      _bboards.remove(id);

      // fail quietly if no such bboard exists
  }
 public void DestroyGroup(String groupId){
    _groups.remove(groupId);
    // fail quietly if no such group exists
 }


  public boolean IsBlackboard( String bboardId){
    return (FindBlackboard(bboardId) != null);
  }

  public void PostBlackboard( String bboardId, String region, SB_Variable data)
  throws SB_Exception
  {
    SB_Blackboard bboard = FindBlackboard(bboardId);

    // fail quietly if no such bboard exists
    if (bboard != null){
      bboard.Post(region,data);
    }
  }

  public SB_Variable ReadBlackboard( String bboardId, String region){
    SB_Blackboard bboard = FindBlackboard(bboardId);
    // fail quietly if no such bboard exists
    if (bboard != null)
    {
      return bboard.Read(region);
    }

    return new SB_VarInvalid();
  }

  public SB_Blackboard FindBlackboard( String bboardId){
    SB_Blackboard it = (SB_Blackboard)_bboards.get(bboardId);
    return it;
  }

  public void JoinGroup( String groupId,SB_CommLink link){
    SB_CommGroup group = FindGroup(groupId);

    if (group == null){
      // named group doesn't exist, so lazy create it
      group = new SB_CommGroup(groupId);
      _groups.put(groupId, group);
    }

    group.AddMember(link);
  }

  public  void QuitGroup( String groupId,SB_CommLink link){
     Set entrySet = _groups.entrySet();
     Iterator it = entrySet.iterator();
     while(it.hasNext()){
       Map.Entry entry = (Map.Entry)it.next();
       SB_CommGroup group = (SB_CommGroup)entry.getValue();
       if(group.GetId().compareTo(groupId) == 0){
         group.RemoveMember(link);
         if(group.GetNumMembers() == 0){
           entrySet.remove(group);
         }
         return;
       }
     }

          // fail quietly if no such group exists
  }

  public SB_CommGroup FindGroup( String groupId){
    return (SB_CommGroup)_groups.get(groupId);
  }

  public int GetNumMembers( String groupId) {
    SB_CommGroup group = FindGroup(groupId);

    if (group != null){
      return group.GetNumMembers();
    }
    return 0; // fail quietly
  }

  public void UnregisterEntity(SB_CommLink commLink){
    SB_CommLink commLinkIt = null;
    for(int i = 0; i < _entityLinks.size(); i++){
      commLinkIt = (SB_CommLink)_entityLinks.get(i);
      if(commLink == commLinkIt){
        break;
      }
    }
    if (commLinkIt != null)  {
      _entityLinks.remove(commLink);
    }
  }

  public void RegisterEntity(SB_CommLink commLink){
          _entityLinks.add(commLink);
  }

  public void RouteMessage(SB_CommMsg msg)
  throws SB_Exception
  {
    String targetGroup = msg.GetRecipient();

    SB_CommGroup group = FindGroup(targetGroup);
    if (group != null){
      group.SendMsg(msg);
    }

          // fail quietly if no such group exists
  }

  /**
	 * Trickle down finishing to all SB_Blackboards
	 * 
	 * @param book
	 * @throws SB_Exception
	 */
	public void finishDeserialization(SB_SingletonBook book) throws SB_Exception
  {
	    Collection values = _bboards.values();
	    for (Iterator it = values.iterator(); it.hasNext();) 
	    {
	        ((SB_Blackboard)it.next()).finishDeserialization(book);
	    }
  }

}