package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.common.Enum;

public class ENodeType extends Enum {
  public ENodeType(int value) {
    super(value);
  }

  public static final ENodeType kActionNode = new ENodeType(0);
  public static final ENodeType kBehaviorNode = new ENodeType(1);
  public static final ENodeType kFinalNode = new ENodeType(2);
  public static final ENodeType kCompoundNode = new ENodeType(5);

  public static ENodeType getNodeType(int value){
    switch(value){
      case 0: return kActionNode;
      case 1: return kBehaviorNode;
      case 2: return kFinalNode;
      case 5: return kCompoundNode;
    }
    return null;
  }
}