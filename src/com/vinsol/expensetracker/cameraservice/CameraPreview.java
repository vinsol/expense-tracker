package com.vinsol.expensetracker.cameraservice;

import com.vinsol.expensetracker.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;

public class CameraPreview extends Activity implements SurfaceHolder.Callback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_camera);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
}
