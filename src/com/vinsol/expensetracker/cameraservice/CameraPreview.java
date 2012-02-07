package com.vinsol.expensetracker.cameraservice;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.utils.Log;

public class CameraPreview extends Activity implements SurfaceHolder.Callback, OnClickListener {

	private Camera mCamera;
	private long minSpaceRequired = 10000000;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private LinearLayout mCameraPreview;
	private int tempCameraSizeHeight;
	private int tempCameraSizeWidth;
	private CameraFlashButton flashButton;
	private Button takePicButton;
	private boolean isTakingPic = false;
	private File mTempFile;
	private LinearLayout takePicPreviewContainer;
	private ProgressBar mProgressBar;
	private LinearLayout mProgressBarLayout;
	private Button useButton;
	private Button cancelButton;
	private Button retakeButton;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_camera);
		mCameraPreview = (LinearLayout) findViewById(R.id.camera_preview_container);
		flashButton = (CameraFlashButton) findViewById(R.id.camera_flash_button);
		takePicButton = (Button) findViewById(R.id.take_pic_button);
		takePicPreviewContainer = (LinearLayout) findViewById(R.id.take_pic_preview_container);
		mProgressBar = (ProgressBar) findViewById(R.id.camera_progress_bar);
		mProgressBarLayout = (LinearLayout) findViewById(R.id.camera_progress_bar_layout);
		useButton = (Button) findViewById(R.id.camera_use_button);
		cancelButton = (Button) findViewById(R.id.camera_cancel_button);
		retakeButton = (Button) findViewById(R.id.camera_retake_button);
		takePicButton.setOnClickListener(this);
		useButton.setOnClickListener(this);
		retakeButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		mTempFile = new File(getIntent().getStringExtra("FullSizeImagePath"));
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
					int temp;
					temp = mPreviewSize.height;
					mPreviewSize.height = mPreviewSize.width;
					mPreviewSize.width = temp;
				}
				mSurfaceHolder = mSurfaceView.getHolder();
				mSurfaceHolder.addCallback(this);
				mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				flashButton.setButtonCallback(flashCB);
			} else {
				Toast.makeText(this, getString(R.string.no_sdcard), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mCamera == null)
			mCamera = Camera.open();
	}

	private void checkSDCardSpace() {
		StatFs stat = new StatFs(Constants.DIRECTORY);
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
            double ratio = ((double)tempCameraSizeWidth/tempCameraSizeHeight);
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(tempCameraSizeHeight - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(tempCameraSizeHeight - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
            	setTempCameraSize(size);
                if (Math.abs(tempCameraSizeHeight - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(tempCameraSizeHeight - targetHeight);
                }
            }
        }
        
        return optimalSize;
    }
	
	private void setTempCameraSize(Size size) {
    	if(size.width > size.height) {
    		tempCameraSizeHeight = size.width;
    		tempCameraSizeWidth = size.height;
    	} else {
    		tempCameraSizeHeight = size.height;
    		tempCameraSizeWidth = size.width;
    	}
	}
	
	@Override
	public void onBackPressed() {
		if(!isTakingPic) {
			super.onBackPressed();
			FlurryAgent.onEvent(getString(R.string.back_pressed));
			setResult(Activity.RESULT_CANCELED);
		}
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
       	mCamera.setDisplayOrientation(90);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mPreviewSize.width, mPreviewSize.height);
       	mCameraPreview.setLayoutParams(params);
       	mProgressBarLayout.setLayoutParams(params);
       	Camera.Parameters parameters = mCamera.getParameters();
       	parameters.setPreviewSize(mPreviewSize.height, mPreviewSize.width);
	    mCameraPreview.requestLayout();
        mCamera.setParameters(parameters);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
        	mCamera.stopPreview();
        }
	}
	
	private CameraFlashButtonCBInterface flashCB = new CameraFlashButtonCBInterface() {
		@Override
		public void onClickListener(int item) {
			Parameters parameters = mCamera.getParameters();
			switch(item) {
			case 0:
				FlurryAgent.onEvent(getString(R.string.flash_auto));
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCamera.setParameters(parameters);
				break;
			case 1:
				FlurryAgent.onEvent(getString(R.string.flash_off));
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
				break;
			case 2:
				FlurryAgent.onEvent(getString(R.string.flash_on));
				parameters.setFlashMode(Parameters.FLASH_MODE_ON);
				mCamera.setParameters(parameters);
				break;
			}
		}		
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_pic_button:
			FlurryAgent.onEvent(getString(R.string.camera_take_pic));
			isTakingPic = true;
			takePicButton.setOnClickListener(null);
			flashButton.setButtonCallback(null);
			focusAndTakePicture();
			break;

		case R.id.camera_cancel_button:
			FlurryAgent.onEvent(getString(R.string.camera_cancel_button));
			setResult(Activity.RESULT_CANCELED);
			deleteFile();
			finish();
			break;
			
		case R.id.camera_use_button:
			FlurryAgent.onEvent(getString(R.string.camera_use_button));
			setResult(Activity.RESULT_OK);
			finish();
			break;
			
		case R.id.camera_retake_button:
			FlurryAgent.onEvent(getString(R.string.camera_retake_button));
			retakePicture();
			break;
		default:
			break;
		}
	}

	private void focusAndTakePicture() {
		focus();
	} 
	
	public void focus() {
    	if(isTakingPic) {
    		mCamera.autoFocus(afcb);
    	}
    }
	
	private AutoFocusCallback afcb = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(isTakingPic)
				takePicture();
		}
    };

    private void takePicture() {
    	if(isTakingPic) {
    		mCamera.takePicture(mShutterCallback, null, mPictureCallback);
    		mCamera.stopPreview();
    	}
    }
    
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			showConfirmationScreen();
		}
	};
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				imageData=rotateImage(baos, imageData, 90);
				
				try {
					baos.flush();
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				File mTempFile = saveImage(imageData);
				if (mTempFile != null) {
					updatePicConfirmationScreen();
				}
				else {
					Toast toast = Toast.makeText(CameraPreview.this, "There was a problem with taking a Picture! Please retake!", Toast.LENGTH_LONG); 
					toast.show();
					retakePicture();
				}
				// unlock camera
				isTakingPic=false;
			}
		}
	};
		
	private byte[] rotateImage(ByteArrayOutputStream baos, byte[] imageData, int degrees) {
		int width = 0;
		int height = 0;
		Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		if (image == null)
			return imageData;
		width = image.getWidth();
		height = image.getHeight();
		Log.d("Rotating image");
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap rotatedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
		rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
		image.recycle();
		rotatedBitmap.recycle();
		return baos.toByteArray();
	}
	
	private void retakePicture() {
		takePicButton.setVisibility(View.VISIBLE);
		flashButton.setVisibility(View.VISIBLE);
		takePicButton.setOnClickListener(this);
		flashButton.setButtonCallback(flashCB);
		takePicPreviewContainer.setVisibility(View.GONE);
		useButton.setVisibility(View.GONE);
		retakeButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		mCameraPreview.setVisibility(View.VISIBLE);
		deleteFile();
		mCamera.startPreview();
	}
	
	private void deleteFile() {
		new FileHelper().delete(mTempFile);
	}
	
	private void showConfirmationScreen() {
		flashButton.setVisibility(View.GONE);
		takePicButton.setVisibility(View.GONE);
		mCameraPreview.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void updatePicConfirmationScreen() {
		if(mTempFile != null) {
			mProgressBar.setVisibility(View.GONE);
			useButton.setVisibility(View.VISIBLE);
			retakeButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			takePicPreviewContainer.setVisibility(View.VISIBLE);
			Drawable drawable = new BitmapDrawable(mTempFile.toString());
			takePicPreviewContainer.setBackgroundDrawable(drawable);
			drawable.invalidateSelf();
 		}
	}
	
	public File saveImage(byte[] imageData) {
		if (imageData == null) {
			mTempFile=null;
			return mTempFile;
		}
		if(mTempFile!=null) {
			storeByteImage(this, imageData, 60);		
		}
		return mTempFile;
	}	
	
	private boolean storeByteImage(Context mContext, byte[] imageData, int quality) {
	    FileOutputStream fileOutputStream = null;
		try {
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inPurgeable = true;
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
			fileOutputStream = new FileOutputStream(mTempFile.toString());
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			if(quality!=49) {
				myImage.compress(CompressFormat.JPEG, quality, bos);
			}
			else {
				bos.write(imageData);
			}
			myImage.recycle();
			fileOutputStream.flush();
			bos.flush();
			fileOutputStream.close();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
