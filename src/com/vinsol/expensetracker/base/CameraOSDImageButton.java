package com.vinsol.expensetracker.base;



import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraOSDImageButton extends FrameLayout {

	private Button mButton = null;
	private int[] mResourceIDs = null;
	private int mSelectedItem = 0;
	private boolean isInitialized = false;
	
	private CameraOSDButtonCBInterface mCallbackInterface=null;
	
	protected static final int DefaultWidth = 120;
	protected static final int DefaultHeight = 120;
	
	public CameraOSDImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mButton = new Button(context);
		prepareButton();
	}

	public CameraOSDImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mButton = new Button(context, attrs);
		prepareButton();
	}

	public void setImages(int[] resourceIDs) {
		mResourceIDs = resourceIDs.clone();
		if(mResourceIDs.length>0) {
			isInitialized = true;
			mButton.setVisibility(VISIBLE);
			mSelectedItem = 0;
			loadImage(mResourceIDs[mSelectedItem]);
			reportSelectedItem();
		} else {
			isInitialized = false;
			mButton.setVisibility(GONE);
		}
	}

	public void setButtonCallback(CameraOSDButtonCBInterface callback) {
		mCallbackInterface = callback;
		if(isInitialized) {
			reportSelectedItem();
		}
	}
	
	private void loadImage(int resourceID) {
		mButton.setBackgroundResource(resourceID);
	}
	
	private void prepareButton() {
		this.addView(mButton);
		
		if(mResourceIDs != null) {
			loadImage(mResourceIDs[mSelectedItem]);
		}

		mButton.setFocusable(false);
		mButton.setFocusableInTouchMode(false);
		mButton.setOnClickListener(listener);
	}
	
	protected void setButtonSize(int width, int height) {
		mButton.setWidth(width);
		mButton.setHeight(height);
	}
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mResourceIDs.length!=0) {
				mSelectedItem = (mSelectedItem + 1) % mResourceIDs.length;
				loadImage(mResourceIDs[mSelectedItem]);				
				reportSelectedItem();
			}
		}
	};
	
	private void reportSelectedItem() {
		if(mCallbackInterface!=null) {
			mCallbackInterface.onClickListener(mSelectedItem);
		}
	}
}
