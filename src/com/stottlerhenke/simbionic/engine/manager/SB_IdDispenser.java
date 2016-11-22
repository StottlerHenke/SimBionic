package com.stottlerhenke.simbionic.engine.manager;
import java.io.Serializable;
import java.util.ArrayList;

import com.stottlerhenke.simbionic.common.EIdType;
import com.stottlerhenke.simbionic.common.SB_ID;


public class SB_IdDispenser implements Serializable 
{
  private ArrayList _nextId = new ArrayList();

  public SB_IdDispenser() {
    // set up the "next id" values for each ID type
    for (int i=0; i < EIdType.kLAST_TYPE.getState(); i++){
      _nextId.add(new Long(0));
    }
  }

  public SB_ID ClaimId(EIdType type){
    long id = ((Long)_nextId.get(type.getState())).longValue();
    id++;
    _nextId.set(type.getState(), new Long(id));
    return new SB_ID(id, type );
  }
}