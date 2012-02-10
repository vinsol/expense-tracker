package com.vinsol.expensetracker.cameraservice;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShutterButton extends ImageView {
    public interface OnShutterButtonListener {
        void onShutterButtonFocus(ShutterButton b, boolean pressed);
        void onShutterButtonClick(ShutterButton b);
    }

    private OnShutterButtonListener mListener;
    private boolean mOldPressed;

    public ShutterButton(Context context) {
        super(context);
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShutterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnShutterButtonListener(OnShutterButtonListener listener) {
        mListener = listener;
    }
    
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean pressed = isPressed();
        if (pressed != mOldPressed) {
            if (!pressed) {
                post(new Runnable() {
                    public void run() {
                        callShutterButtonFocus(pressed);
                    }
                });
            } else {
                callShutterButtonFocus(pressed);
            }
            mOldPressed = pressed;
        }
    }

    private void callShutterButtonFocus(boolean pressed) {
        if (mListener != null) {
            mListener.onShutterButtonFocus(this, pressed);
        }
    }

    @Override
    public boolean performClick() {
        boolean result = super.performClick();
        if (mListener != null) {
            mListener.onShutterButtonClick(this);
        }
        return result;
    }
}
