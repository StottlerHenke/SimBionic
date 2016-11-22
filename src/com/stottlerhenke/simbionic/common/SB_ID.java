package com.stottlerhenke.simbionic.common;

import java.io.Serializable;

public class SB_ID implements Serializable  
{

  public long _id;
  public EIdType _type;
  public static final long NULL_ENTITY = -1;
  public SB_ID() {
    _id = 0;
    _type = EIdType.kInvalidId;
  }
  public SB_ID(long id, EIdType type){
    _id = id;
    _type = type;
  }
  public SB_ID(long id){
    this(id, EIdType.kEntityId);
  }
  public boolean equals( SB_ID id2) {
    return (_id==id2._id) && (_type==id2._type);
  }
  public boolean opNOTEq(SB_ID id2){
    return (_id!=id2._id) || (_type!=id2._type);
  }

  public String toString(){
    return "ID" + _id;
  }
  public static SB_ID NULL_ID(){
    return new SB_ID(NULL_ENTITY, EIdType.kEntityId);
  }
}