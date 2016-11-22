package com.stottlerhenke.simbionic.editor;

import com.stottlerhenke.simbionic.common.Enum;

/**
 * Defines the various types of types.
 */
public class ETypeType extends Enum {

	//public final static ETypeType kSimBionicType = new ETypeType(1);
	public final static ETypeType kJavaType	= new ETypeType(1 << 1);
	public final static ETypeType kEnumType	= new ETypeType(1 << 2);
	//public final static ETypeType kClassType = new ETypeType(1 << 3);
	
	private ETypeType(int state) {
		super(state);
	}

}
