package com.stottlerhenke.simbionic.editor;

import com.stottlerhenke.simbionic.common.Enum;

/**
 * Flags for specifying which types are valid where.
 */
public class ETypeValid extends Enum {

	public final static ETypeValid kForConst = new ETypeValid(1);
	public final static ETypeValid kForVar = new ETypeValid(1 << 1);
	public final static ETypeValid kForParam = new ETypeValid(1 << 2);
	public final static ETypeValid kForRetVal = new ETypeValid(1 << 3);
	public final static ETypeValid kForClass = new ETypeValid(1 << 4);
	
	private ETypeValid(int state) {
		super(state);
	}

}
