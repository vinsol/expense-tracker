package com.vinsol.expensetracker.cameraservice;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.vinsol.expensetracker.R;

public class CameraFlashButton extends FrameLayout implements OnClickListener {

	private Button flashButton;
	private int[] resId = {R.drawable.flash_auto, R.drawable.flash_disable, R.drawable.flash_enable};
	private int selectedResId = 0;
	
	public CameraFlashButton(Context context) {
		super(context);
		flashButton = new Button(context);
		Initialize();
	}
	
	public CameraFlashButton(Context context,AttributeSet attr) {
		super(context,attr);
		flashButton = new Button(context);
		Initialize();
	}

	private void Initialize() {
		this.addView(flashButton);
		setButtonBackground();
		flashButton.setFocusable(false);
		flashButton.setFocusableInTouchMode(false);
		flashButton.setOnClickListener(this);
//		setFlashMode();
	}
	
	private void setButtonBackground() {
		flashButton.setBackgroundResource(resId[selectedResId]);
	}

	@Override
	public void onClick(View v) {
		selectedResId++;
		selectedResId%=3;
		setButtonBackground();
		this.refreshDrawableState();
//		setFlashMode();
	}

//	private void setFlashMode() {
//		switch (selectedResId) {
//		case 0:
//			Camera.open().getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//			break;
//		case 1:
//			Camera.open().getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//			break;
//		case 2:
//			Camera.open().getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_ON);	
//			break;
//		default:
//			break;
//		}
//	}

}
