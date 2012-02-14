/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.cameraservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CameraButton extends ImageView {
	private static final int ANIMATION_SPEED = 180; // 180 deg/sec

    private int mCurrentDegree = 0; // [0, 359]
    private int mStartDegree = 0;
    private int mTargetDegree = 0;

    private boolean mClockwise = false;

    private long mAnimationStartTime = 0;
    private long mAnimationEndTime = 0;
	
	public CameraButton(Context context) {
		super(context);
	}
	
	public CameraButton(Context context,AttributeSet attr) {
		super(context,attr);
	}
	
	public void setDegree(int degree) {
        degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
        if (degree == mTargetDegree) return;

        mTargetDegree = degree;
        mStartDegree = mCurrentDegree;
        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

        int diff = mTargetDegree - mCurrentDegree;
        diff = diff >= 0 ? diff : 360 + diff; // make it in range [0, 359]

        diff = diff > 180 ? diff - 360 : diff;
        mClockwise = diff >= 0;
        mAnimationEndTime = mAnimationStartTime + Math.abs(diff) * 1000 / ANIMATION_SPEED;

        invalidate();
    }
	
	@Override
    protected void onDraw(Canvas canvas) {
		
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;

        if (w == 0 || h == 0) return; // nothing to draw

        if (mCurrentDegree != mTargetDegree) {
            long time = AnimationUtils.currentAnimationTimeMillis();
            if (time < mAnimationEndTime) {
                int deltaTime = (int)(time - mAnimationStartTime);
                int degree = mStartDegree + ANIMATION_SPEED * (mClockwise ? deltaTime : -deltaTime) / 1000;
                degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
                mCurrentDegree = degree;
                invalidate();
            } else {
                mCurrentDegree = mTargetDegree;
            }
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        int width = getWidth() - left - right;
        int height = getHeight() - top - bottom;

        int saveCount = canvas.getSaveCount();
        canvas.translate(left + width / 2, top + height / 2);
        canvas.rotate(-mCurrentDegree);
        canvas.translate(-w / 2, -h / 2);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
}
