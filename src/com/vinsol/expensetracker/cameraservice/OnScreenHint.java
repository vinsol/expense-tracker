/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.cameraservice;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vinsol.expensetracker.R;

public class OnScreenHint {

    int mGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;
    View mView;
    View mNextView;

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private final WindowManager mWM;
    private final Handler mHandler = new Handler();

    public OnScreenHint(Context context) {
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mY = context.getResources().getDimensionPixelSize(
                R.dimen.hint_y_offset);

        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = R.style.Animation_OnScreenHint;
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        mParams.setTitle("OnScreenHint");
    }

    public void show() {
        if (mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        mHandler.post(mShow);
    }

    public void cancel() {
        mHandler.post(mHide);
    }

    public static OnScreenHint makeText(Context context, CharSequence text) {
        OnScreenHint result = new OnScreenHint(context);

        LayoutInflater inflate =
                (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.on_screen_hint, null);
        TextView tv = (TextView) v.findViewById(R.id.message);
        tv.setText(text);

        result.mNextView = v;

        return result;
    }

    public void setText(CharSequence s) {
        if (mNextView == null) {
            throw new RuntimeException("This OnScreenHint was not "
                    + "created with OnScreenHint.makeText()");
        }
        TextView tv = (TextView) mNextView.findViewById(R.id.message);
        if (tv == null) {
            throw new RuntimeException("This OnScreenHint was not "
                    + "created with OnScreenHint.makeText()");
        }
        tv.setText(s);
    }

    private synchronized void handleShow() {
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide();
            mView = mNextView;
            final int gravity = mGravity;
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK)
                    == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK)
                    == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mWM.addView(mView, mParams);
        }
    }

    private synchronized void handleHide() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mView = null;
        }
    }

    private final Runnable mShow = new Runnable() {
        public void run() {
            handleShow();
        }
    };

    private final Runnable mHide = new Runnable() {
        public void run() {
            handleHide();
        }
    };
}

