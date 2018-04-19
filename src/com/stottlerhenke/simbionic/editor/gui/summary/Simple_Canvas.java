package com.stottlerhenke.simbionic.editor.gui.summary;

import com.stottlerhenke.simbionic.editor.gui.SB_Canvas;

public class Simple_Canvas extends SB_Canvas {

	protected Simple_Canvas () {
		super(null);
	}
	
	@Override
	protected void updateEditItems() {}
	
	void setNeedToScroll(boolean needToScroll) {
		_needToScroll = needToScroll;
	}
	
	boolean isNeedToScroll() {
		return _needToScroll;
	}
}
