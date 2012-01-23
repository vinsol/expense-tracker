package com.vinsol.expensetracker.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ImageViewExtended extends ImageView {

	public ImageViewExtended(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageViewExtended(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewExtended(Context context) {
		super(context);
	}
	
	@Override
	public void setPressed(boolean pressed) {
		if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
		super.setPressed(pressed);
	}
	
	@Override
	public void setSelected(boolean selected) {
		if (selected && ((View) getParent()).isPressed()) {
            return;
        }
		super.setSelected(selected);
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if (gainFocus && ((View) getParent()).isPressed()) {
            return;
        }
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}
}