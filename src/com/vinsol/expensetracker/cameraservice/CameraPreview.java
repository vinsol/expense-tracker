package com.vinsol.expensetracker.cameraservice;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.Log;

public class CameraPreview extends Activity {

	private Camera camera;
	private long minSpaceRequired = 10000000;
	private LinearLayout cameraPreview;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private Preview mPreview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_camera);
		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview_container);
		camera = Camera.open();
		if(camera == null) {
			Toast.makeText(this, getString(R.string.error_camera), Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_CANCELED);
			finish();
		} else {
			if(isSdCardAvailable()) {
				checkSDCardSpace();
				mPreview = new Preview(this);
				mPreview.setCamera(camera);
				cameraPreview.addView(mPreview);
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
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d("optimalSize "+optimalSize.height+" \t "+optimalSize.width+" \t "+w+" \t "+h);
        return optimalSize;
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
        if (camera != null) {
            camera.release();
            mPreview.setCamera(null);
            camera = null;
        }
	}
	
	private class Preview extends ViewGroup implements SurfaceHolder.Callback {

		private SurfaceView mSurfaceView;
		private SurfaceHolder mSurfaceHolder;
		private Camera mCamera;
		
		public Preview(Context context) {
			super(context);
			mSurfaceView = new SurfaceView(CameraPreview.this);
			cameraPreview.addView(mSurfaceView);
			mSurfaceHolder = mSurfaceView.getHolder();
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
		public void setCamera(Camera camera) {
	        mCamera = camera;
	        if (mCamera != null) {
	            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
	            requestLayout();
	        }
	    }
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// We purposely disregard child measurements because act as a
	        // wrapper to a SurfaceView that centers the camera preview instead
	        // of stretching it.
	        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
	        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
	        setMeasuredDimension(width, height);

	        if (mSupportedPreviewSizes != null) {
	            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
	        }
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// Now that the size is known, set up the camera parameters and begin the preview.
	        Camera.Parameters parameters = mCamera.getParameters();
	        mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
	        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	        mCamera.setDisplayOrientation(90);
	        cameraPreview.requestLayout();
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

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (changed && getChildCount() > 0) {
	            final View child = getChildAt(0);

	            final int width = r - l;
	            final int height = b - t;

	            int previewWidth = width;
	            int previewHeight = height;
	            if (mPreviewSize != null) {
	                previewWidth = mPreviewSize.width;
	                previewHeight = mPreviewSize.height;
	            }

	            // Center the child SurfaceView within the parent.
	            if (width * previewHeight > height * previewWidth) {
	                final int scaledChildWidth = previewWidth * height / previewHeight;
	                child.layout((width - scaledChildWidth) / 2, 0,
	                        (width + scaledChildWidth) / 2, height);
	            } else {
	                final int scaledChildHeight = previewHeight * width / previewWidth;
	                child.layout(0, (height - scaledChildHeight) / 2,
	                        width, (height + scaledChildHeight) / 2);
	            }
	        }
		}
		
	}
}
