package com.vinsol.expensetracker.cameraservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.Log;

public class CameraPreview extends Activity implements CameraServiceCallback,SurfaceHolder.Callback,OnClickListener {

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
	private CameraServiceCallback mCameraServiceCallback = null;
	private File mTempFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_camera);
		mCameraPreview = (LinearLayout) findViewById(R.id.camera_preview_container);
		flashButton = (CameraFlashButton) findViewById(R.id.camera_flash_button);
		takePicButton = (Button) findViewById(R.id.take_pic_button);
		takePicButton.setOnClickListener(this);
		mTempFile = new File(getIntent().getStringExtra("FullSizeImagePath"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
					Log.d("mPreviewSize "+mPreviewSize.height+" "+mPreviewSize.width);
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
            Log.d("ratio "+ratio+" "+targetRatio+" tempCameraSize "+tempCameraSizeWidth+" "+tempCameraSizeHeight+" size "+size.width+" "+size.height);
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
        Log.d("optimalSize "+optimalSize.height+" \t "+optimalSize.width+" \t "+w+" \t "+h);
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
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mPreviewSize.width, mPreviewSize.height);
       	mCameraPreview.setLayoutParams(params);
       	Camera.Parameters parameters = mCamera.getParameters();
       	parameters.setPreviewSize(mPreviewSize.height, mPreviewSize.width);
       	Log.d(" getWidthSize(mPreviewSize) "+mPreviewSize.width+" \t getHeightSize(mPreviewSize) "+mPreviewSize.height);
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

       	mCamera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
    		mCamera.release();
        }
	}
	
	private CameraFlashButtonCBInterface flashCB = new CameraFlashButtonCBInterface() {
		@Override
		public void onClickListener(int item) {
			Log.d("Flash: " + item);
			switch(item) {
			case 0:
				mCamera.getParameters().setFlashMode(Parameters.FLASH_MODE_AUTO);
				break;
			case 1:
				mCamera.getParameters().setFlashMode(Parameters.FLASH_MODE_OFF);
				break;
			case 2:
				mCamera.getParameters().setFlashMode(Parameters.FLASH_MODE_ON);
				break;
			}
		}		
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_pic_button:
			isTakingPic = true;
			takePicButton.setOnClickListener(null);
			flashButton.setButtonCallback(null);
			focusAndTakePicture();
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
    		mCamera.release();
    	}
    }
    
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		
		@Override
		public void onShutter() {
			mCameraServiceCallback.onShutter();
		}
	};
	
	private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {
				Log.d("Picture taken, length:" + imageData.length);
				
				// rotate the image such that 
				byte[] newImage=imageData;
				if(mCameraServiceCallback!=null){
					mCameraServiceCallback.pictureTaken(newImage);
				}
				
				// unlock camera
				isTakingPic=false;
			}
		}
	};

	@Override
	public void onShutter() {
		showConfirmationScreen();
	}

	@Override
	public void pictureTaken(byte[] imageData) {
		File mTempFile = saveImage(imageData);
		
		if (mTempFile != null) {
			updatePicConfirmationScreen();
		}
		else {
			Toast toast = Toast.makeText(this, "There was a problem with taking a Picture! Please retake!", Toast.LENGTH_LONG); 
			toast.show();
			retakePicture();
		}
	}
	
	private void retakePicture() {
		takePicButton.setVisibility(View.VISIBLE);
		flashButton.setVisibility(View.VISIBLE);
		takePicButton.setOnClickListener(this);
		flashButton.setButtonCallback(flashCB);
		// start preview because of energy consumption modifications
		mCamera.startPreview();
	}
	
	private void showConfirmationScreen() {
		flashButton.setVisibility(View.INVISIBLE);
		takePicButton.setVisibility(View.INVISIBLE);
	}
	
	private void updatePicConfirmationScreen() {
		if(mTempFile != null) {
			mCameraPreview.setBackgroundDrawable(new BitmapDrawable(mTempFile.toString()));
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
		Log.d("File"+mTempFile.toString()+" "+imageData.length);
	    FileOutputStream fileOutputStream = null;
		try {
	
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 1;
			
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,imageData.length,options);
			fileOutputStream = new FileOutputStream(mTempFile.toString());
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
	
			if(quality!=49) {
				myImage.compress(CompressFormat.JPEG, quality, bos);
			}
			else {
				bos.write(imageData);
			}
	
			bos.flush();
			bos.close();
	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return true;
	}
	
	
}
