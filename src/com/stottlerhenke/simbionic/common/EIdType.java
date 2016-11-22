package com.stottlerhenke.simbionic.common;

public class EIdType extends Enum {
  private EIdType(int value) {
    super(value);
  }
  public static final EIdType kInvalidId = new EIdType(-1);
  public static final EIdType kEntityId = new EIdType(0);
  public static final EIdType kEntGroupId = new EIdType(1);
  public static final EIdType kLAST_TYPE = new EIdType(2);

}