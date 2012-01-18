package com.vinsol.expensetracker.cameraservice;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.Log;

public class CameraPreview extends Activity implements SurfaceHolder.Callback {

	private Camera mCamera;
	private long minSpaceRequired = 10000000;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private LinearLayout mCameraPreview;
	private Size tempCameraSize;
	private boolean isDisplayMetricsOpposite = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_camera);
		mCameraPreview = (LinearLayout) findViewById(R.id.camera_preview_container);
		mCamera = Camera.open();
		if(mCamera == null) {
			Toast.makeText(this, getString(R.string.error_camera), Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_CANCELED);
			finish();
		} else {
			if(isSdCardAvailable()) {
				checkSDCardSpace();
				setSupportedPreviewSizes();
				mSurfaceView = new SurfaceView(this);
				mCameraPreview.addView(mSurfaceView);
				if (mSupportedPreviewSizes != null) {
		            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
		        }
				if(mPreviewSize.width > mPreviewSize.height) {
					isDisplayMetricsOpposite = true;
					setTempCameraSize(mPreviewSize);
					mPreviewSize.height = tempCameraSize.height;
					mPreviewSize.width = tempCameraSize.width;
					Log.d("mPreviewSize "+mPreviewSize.height+" "+mPreviewSize.width);
				}
				mSurfaceHolder = mSurfaceView.getHolder();
				mSurfaceHolder.addCallback(this);
				mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			} else {
				Toast.makeText(this, getString(R.string.no_sdcard), Toast.LENGTH_LONG).show();
			}
		}
	}

	private void checkSDCardSpace() {
		StatFs stat = new StatFs(ExpenseTrackerApplication.SDCARD_PATH);
		if(!((stat.getAvailableBlocks()*(long)stat.getBlockSize()) > minSpaceRequired)) {
			Toast.makeText(this, getString(R.string.insufficient_storage), Toast.LENGTH_LONG).show();
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	private boolean isSdCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
        	setTempCameraSize(size);
        	Log.d("size "+" w "+size.width+" \t h "+size.height);
            double ratio = tempCameraSize.width/tempCameraSize.height;
            Log.d("ratio "+ratio+" targetRatio "+targetRatio);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(tempCameraSize.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(tempCameraSize.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
            	setTempCameraSize(size);
                if (Math.abs(tempCameraSize.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(tempCameraSize.height - targetHeight);
                }
            }
        }
        Log.d("optimalSize "+optimalSize.height+" \t "+optimalSize.width+" \t "+w+" \t "+h);
        return optimalSize;
    }
	
	private void setTempCameraSize(Size size) {
		tempCameraSize = size;
    	if(isDisplayMetricsOpposite) {
    		tempCameraSize.height = size.width;
    		tempCameraSize.width = size.height;
    	}
    	Log.d("tempCameraSize "+tempCameraSize.height+" \t "+tempCameraSize.width);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(Activity.RESULT_CANCELED);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// Camera object is a shared resource, it's very important to release it when the activity is paused.
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
	}
	
	public void setSupportedPreviewSizes() {
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        }
    }
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// Now that the size is known, set up the camera parameters and begin the preview.
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mPreviewSize.width, mPreviewSize.height);
       	mCameraPreview.setLayoutParams(params);
       	Camera.Parameters parameters = mCamera.getParameters();
       	parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       	Log.d(" getWidthSize(mPreviewSize) "+mPreviewSize.width+" \t getHeightSize(mPreviewSize) "+mPreviewSize.height);
       	mCamera.setDisplayOrientation(90);
	    mCameraPreview.requestLayout();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where to draw.
		try {
			if(mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}
		} catch (IOException e) {
			Log.d("IOException caused by setPreviewDisplay()"+ e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
    		mCamera.release();
        }
	}
	
}
