/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.cameraservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;

public class Camera extends Activity implements View.OnClickListener, ShutterButton.OnShutterButtonListener, SurfaceHolder.Callback {

    private static final int FIRST_TIME_INIT = 2;
    private static final int NOT_FOUND = -1;
    private static final int RESTART_PREVIEW = 3;
    private static final int CLEAR_SCREEN_DELAY = 4;
    private static final int SET_CAMERA_PARAMETERS_WHEN_IDLE = 5;
    private static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
    private static final int UPDATE_PARAM_INITIALIZE = 1;
    private static final int UPDATE_PARAM_PREFERENCE = 4;
    private static final int UPDATE_PARAM_ALL = -1;
    private static final int ORIENTATION_HYSTERESIS = 5;
    private int mUpdateSet;
    private static final float DEFAULT_CAMERA_BRIGHTNESS = 0.7f;
    private static final int SCREEN_DELAY = 2 * 60 * 1000;
    private Parameters mParameters;
    private OrientationEventListener mOrientationListener;
    private int mLastOrientation = 0;
    private SharedPreferences mPreferences;

    private static final int IDLE = 1;
    private static final int IMAGE_DISPLAYED = 1000;
    private static final int SNAPSHOT_IN_PROGRESS = 2;
    private CameraFlashButton flashButton;
    private int mStatus = IDLE;

    private android.hardware.Camera mCameraDevice;
    private ContentProviderClient mMediaProviderClient;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder = null;
    private ShutterButton mShutterButton;
    private FocusRectangle mFocusRectangle;
    private boolean mStartPreviewFail = false;
    private static final int NO_STORAGE_ERROR = -1;
    private static final int CANNOT_STAT_ERROR = -2;
    
    private Uri mSaveUri;

    private ImageCapture mImageCapture = null;

    private boolean mPreviewing;
    private boolean mPausing;
    private boolean mFirstTimeInitialized;

    private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;
    private int mFocusState = FOCUS_NOT_STARTED;

    private ContentResolver mContentResolver;

    private final ShutterCallback mShutterCallback = new ShutterCallback();
    private final PostViewPictureCallback mPostViewPictureCallback = new PostViewPictureCallback();
    private final RawPictureCallback mRawPictureCallback = new RawPictureCallback();
    private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private final ErrorCallback mErrorCallback = new ErrorCallback();

    private long mFocusStartTime;
    private long mFocusCallbackTime;
    private long mCaptureStartTime;
    private long mShutterCallbackTime;
    private long mPostViewPictureCallbackTime;
    private long mRawPictureCallbackTime;
    private long mJpegPictureCallbackTime;
    private int mPicturesRemaining;
    private int mOrientationCompensation;
    
    private OnScreenHint mStorageHint;

    public long mAutoFocusTime;
    public long mShutterLag;
    public long mShutterToPictureDisplayedTime;
    public long mPictureDisplayedToJpegCallbackTime;
    public long mJpegCallbackFinishTime;

    public static boolean mMediaServerDied = false;

    private final Handler mHandler = new MainHandler();

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTART_PREVIEW: {
                    restartPreview();
                    if (mJpegPictureCallbackTime != 0) {
                        long now = System.currentTimeMillis();
                        mJpegCallbackFinishTime = now - mJpegPictureCallbackTime;
                        mJpegPictureCallbackTime = 0;
                    }
                    break;
                }

                case CLEAR_SCREEN_DELAY: {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }

                case FIRST_TIME_INIT: {
                    initializeFirstTime();
                    break;
                }

                case SET_CAMERA_PARAMETERS_WHEN_IDLE: {
                    setCameraParametersWhenIdle(0);
                    break;
                }
            }
        }
    }
    
    private void checkStorage() {
        calculatePicturesRemaining();
        updateStorageHint(mPicturesRemaining);
    }
    
    private void updateStorageHint(int remaining) {
        String noStorageText = null;

        if (remaining == NO_STORAGE_ERROR) {
            String state = Environment.getExternalStorageState();
            if (state == Environment.MEDIA_CHECKING) {
                noStorageText = getString(R.string.preparing_sd);
            } else {
                noStorageText = getString(R.string.no_storage);
            }
        } else if (remaining < 1) {
            noStorageText = getString(R.string.not_enough_space);
        }

        if (noStorageText != null) {
            if (mStorageHint == null) {
                mStorageHint = OnScreenHint.makeText(this, noStorageText);
            } else {
                mStorageHint.setText(noStorageText);
            }
            mStorageHint.show();
        } else if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;
        }
    }

    private void keepMediaProviderInstance() {
        if (mMediaProviderClient == null) {
            mMediaProviderClient = getContentResolver().acquireContentProviderClient(MediaStore.AUTHORITY);
        }
    }
    
    private int roundOrientation(int orientation) {
        boolean changeOrientation = false;
        if (mLastOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - mLastOrientation);
            dist = Math.min( dist, 360 - dist );
            changeOrientation = ( dist >= 45 + ORIENTATION_HYSTERESIS );
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return mLastOrientation;
    }

    private void initializeFirstTime() {
        if (mFirstTimeInitialized) return;
        mOrientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
            	if (orientation == ORIENTATION_UNKNOWN) return;
                mLastOrientation = roundOrientation(orientation);
                int orientationCompensation = mLastOrientation + getDisplayRotation();
                if (mOrientationCompensation != orientationCompensation) {
                    mOrientationCompensation = orientationCompensation;
                    setOrientationIndicator(mOrientationCompensation);
                }
            }
        };
        mOrientationListener.enable();
        keepMediaProviderInstance();
        checkStorage();
        mContentResolver = getContentResolver();
        mShutterButton = (ShutterButton) findViewById(R.id.shutter_button);
        mShutterButton.setOnShutterButtonListener(this);
        mShutterButton.setVisibility(View.VISIBLE);
        mFocusRectangle = (FocusRectangle) findViewById(R.id.focus_rectangle);
        updateFocusIndicator();
        initializeScreenBrightness();
        installIntentFilter();
        mFirstTimeInitialized = true;
    }

    private void initializeSecondTime() {
        mOrientationListener.enable();
        installIntentFilter();
        keepMediaProviderInstance();
        checkStorage();
    }

    private final class ShutterCallback implements android.hardware.Camera.ShutterCallback {
        public void onShutter() {
            mShutterCallbackTime = System.currentTimeMillis();
            mShutterLag = mShutterCallbackTime - mCaptureStartTime;
            clearFocusState();
        }
    }

    private final class PostViewPictureCallback implements PictureCallback {
    	public void onPictureTaken(byte [] data, android.hardware.Camera camera) {
            mPostViewPictureCallbackTime = System.currentTimeMillis();
        }
    }

    private final class RawPictureCallback implements PictureCallback {
        public void onPictureTaken(byte [] rawData, android.hardware.Camera camera) {
            mRawPictureCallbackTime = System.currentTimeMillis();
        }
    }

    private final class JpegPictureCallback implements PictureCallback {

        public void onPictureTaken( final byte [] jpegData, final android.hardware.Camera camera) {
            if (mPausing) {return;}
            mJpegPictureCallbackTime = System.currentTimeMillis();
            if (mPostViewPictureCallbackTime != 0) {
                mShutterToPictureDisplayedTime = mPostViewPictureCallbackTime - mShutterCallbackTime;
                mPictureDisplayedToJpegCallbackTime = mJpegPictureCallbackTime - mPostViewPictureCallbackTime;
            } else {
                mShutterToPictureDisplayedTime = mRawPictureCallbackTime - mShutterCallbackTime;
                mPictureDisplayedToJpegCallbackTime = mJpegPictureCallbackTime - mRawPictureCallbackTime;
            }
            mImageCapture.storeImage(jpegData, camera);
            calculatePicturesRemaining();
            if (!mHandler.hasMessages(RESTART_PREVIEW)) {
                long now = System.currentTimeMillis();
                mJpegCallbackFinishTime = now - mJpegPictureCallbackTime;
                mJpegPictureCallbackTime = 0;
            }
        }
    }

    private final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, android.hardware.Camera camera) {
            mFocusCallbackTime = System.currentTimeMillis();
            mAutoFocusTime = mFocusCallbackTime - mFocusStartTime;
            if (mFocusState == FOCUSING_SNAP_ON_FINISH) {
                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
                mImageCapture.onSnap();
            } else if (mFocusState == FOCUSING) {
                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
            }
            updateFocusIndicator();
        }
    }

    private final class ErrorCallback implements android.hardware.Camera.ErrorCallback {
        public void onError(int error, android.hardware.Camera camera) {
            if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
                 mMediaServerDied = true;
            }
        }
    }

    private class ImageCapture {

        byte[] mCaptureOnlyData;

        public void storeImage(final byte[] data,android.hardware.Camera camera) {
            mCaptureOnlyData = data;
            showPostCaptureAlert();
        }

        public void initiate() {
            if (mCameraDevice == null) {
                return;
            }

            capture();
        }

        public byte[] getLastCaptureData() {
            return mCaptureOnlyData;
        }

        private void capture() {
            mCaptureOnlyData = null;
            mParameters.setRotation(mLastOrientation);
            mCameraDevice.setParameters(mParameters);
            mCameraDevice.takePicture(mShutterCallback, mRawPictureCallback,mPostViewPictureCallback, new JpegPictureCallback());
            mPreviewing = false;
        }

        public void onSnap() {
            if (mPausing || mStatus == SNAPSHOT_IN_PROGRESS) {return;}
            mCaptureStartTime = System.currentTimeMillis();
            mPostViewPictureCallbackTime = 0;
            mStatus = SNAPSHOT_IN_PROGRESS;
            mImageCapture.initiate();
        }

        private void clearLastData() {
            mCaptureOnlyData = null;
        }
    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Thread startPreviewThread = new Thread(new Runnable() {
            public void run() {
                try {
                    mStartPreviewFail = false;
                    startPreview();
                } catch (CameraHardwareException e) {
                    if ("eng".equals(Build.TYPE)) {
                        throw new RuntimeException(e);
                    }
                    mStartPreviewFail = true;
                }
            }
        });
        startPreviewThread.start();

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        setupCaptureParams();

        LayoutInflater inflater = getLayoutInflater();

        ViewGroup rootView = (ViewGroup) findViewById(R.id.camera);
        inflater.inflate(R.layout.camera_control, rootView);
        flashButton = ((CameraFlashButton) findViewById(R.id.flash_button));

        try {
            startPreviewThread.join();
            if (mStartPreviewFail) {
                showCameraErrorAndFinish();
                return;
            }
        } catch (InterruptedException ex) {
        }
        ((CameraButton) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((CameraButton) findViewById(R.id.btn_done)).setOnClickListener(this);
        ((CameraButton) findViewById(R.id.btn_retake)).setOnClickListener(this);
        
    }
    private void setOrientationIndicator(int degree) {
    	((CameraFlashButton) findViewById(R.id.flash_button)).setDegree(degree);
    	((CameraButton) findViewById(R.id.btn_cancel)).setDegree(degree);
    	((CameraButton) findViewById(R.id.btn_done)).setDegree(degree);
    	((CameraButton) findViewById(R.id.btn_retake)).setDegree(degree);
    }
    
    private CameraFlashButtonCBInterface flashCB = new CameraFlashButtonCBInterface() {
		@Override
		public void onClickListener(int item) {
			Parameters parameters = mCameraDevice.getParameters();
			switch(item) {
			case 0:
				parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				mCameraDevice.setParameters(parameters);
				break;
			case 1:
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCameraDevice.setParameters(parameters);
				break;
			case 2:
				parameters.setFlashMode(Parameters.FLASH_MODE_ON);
				mCameraDevice.setParameters(parameters);
				break;
			}
		}		
	};

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaProviderClient != null) {
            mMediaProviderClient.release();
            mMediaProviderClient = null;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retake:
                hidePostCaptureAlert();
                restartPreview();
                break;
            case R.id.btn_done:
		        finish();
                break;
            case R.id.btn_cancel:
                doCancel();
        }
    }

    private Bitmap makeBitmap(final byte[] jpegData) {
        try {
        	if(jpegData != null && jpegData.length > 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                return null;
            }
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
        	}
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }
    
    private void doAttach() {
    	if (mPausing) {
            return;
        }

        byte[] data = mImageCapture.getLastCaptureData();
        if(mSaveUri != null && data != null) {
        	Bitmap bitmap = createCaptureBitmap(data);
		    OutputStream outputStream = null;
		    try {
		        outputStream = mContentResolver.openOutputStream(mSaveUri);
		        if(bitmap != null) {
			        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
			        outputStream.close();
			        bitmap.recycle();
			        setResult(RESULT_OK);
		        }
		    } catch (IOException ex) {
		    } finally {
		        closeSilently(outputStream);
		    }
        } else {
        	setResult(RESULT_CANCELED);
        }
    }
        
    private Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            if (degrees != 0) {
                m.postRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            }

            try {
                Bitmap b2 = Bitmap.createBitmap( b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
    
    private int getDisplayRotation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
    
    private boolean saveDataToFile(String filePath, byte[] data) {
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(filePath);
            f.write(data);
        } catch (IOException e) {
            return false;
        } finally {
        	closeSilently(f);
        }
        return true;
    }
    
    private int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
    
    private Bitmap createCaptureBitmap(final byte[] data) {
        String filepath = mSaveUri.getPath();
        int degree = 0;
        if (saveDataToFile(filepath, data)) {
            degree = getExifOrientation(filepath);
            new File(filepath).delete();
        }
        Bitmap bitmap = makeBitmap(data);
        bitmap = rotate(bitmap, degree+mOrientationCompensation);
        return bitmap;
    }
    
    private void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }
    
    private void doCancel() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
	public void onShutterButtonFocus(ShutterButton button, boolean pressed) {
        if (mPausing) {
            return;
        }
        switch (button.getId()) {
            case R.id.shutter_button:
                doFocus(pressed);
                break;
        }
    }

    @Override
    public void onShutterButtonClick(ShutterButton button) {
        if (mPausing) {
            return;
        }
        switch (button.getId()) {
            case R.id.shutter_button:
                doSnap();
                break;
        }
    }

    private void installIntentFilter() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addDataScheme("file");
    }

    private void initializeScreenBrightness() {
        Window win = getWindow();
        int mode = Settings.System.getInt(
                getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.screenBrightness = DEFAULT_CAMERA_BRIGHTNESS;
            win.setAttributes(winParams);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPausing = false;
        mJpegPictureCallbackTime = 0;
        mImageCapture = new ImageCapture();

        if (!mPreviewing && !mStartPreviewFail) {
            try {
                startPreview();
            } catch (CameraHardwareException e) {
                showCameraErrorAndFinish();
                return;
            }
        }

        if (mSurfaceHolder != null) {
            if (!mFirstTimeInitialized) {
                mHandler.sendEmptyMessage(FIRST_TIME_INIT);
            } else {
                initializeSecondTime();
            }
        }
        keepScreenOnAwhile();
    }
    
    @Override
    protected void onPause() {
        mPausing = true;
        stopPreview();
        closeCamera();
        resetScreenOn();
        if (mFirstTimeInitialized) {
            mOrientationListener.disable();
            hidePostCaptureAlert();
        }
        mImageCapture.clearLastData();
        mImageCapture = null;
        
        if (mStorageHint != null) {
            mStorageHint.cancel();
            mStorageHint = null;
        }
        
        mHandler.removeMessages(RESTART_PREVIEW);
        mHandler.removeMessages(FIRST_TIME_INIT);

        super.onPause();
    }

    private boolean canTakePicture() {
        return isCameraIdle() && mPreviewing && (mPicturesRemaining > 0);
    }

    private void autoFocus() {
        if (canTakePicture()) {
            mFocusStartTime = System.currentTimeMillis();
            mFocusState = FOCUSING;
            updateFocusIndicator();
            mCameraDevice.autoFocus(mAutoFocusCallback);
        }
    }

    private void cancelAutoFocus() {
        if (mFocusState == FOCUSING || mFocusState == FOCUS_SUCCESS || mFocusState == FOCUS_FAIL) {
            mCameraDevice.cancelAutoFocus();
        }
        if (mFocusState != FOCUSING_SNAP_ON_FINISH) {
            clearFocusState();
        }
    }

    private void clearFocusState() {
        mFocusState = FOCUS_NOT_STARTED;
        updateFocusIndicator();
    }

    private void updateFocusIndicator() {
        if (mFocusRectangle == null) return;

        if (mFocusState == FOCUSING || mFocusState == FOCUSING_SNAP_ON_FINISH) {
            mFocusRectangle.showStart();
        } else if (mFocusState == FOCUS_SUCCESS) {
            mFocusRectangle.showSuccess();
        } else if (mFocusState == FOCUS_FAIL) {
            mFocusRectangle.showFail();
        } else {
            mFocusRectangle.clear();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isCameraIdle()) {
            return;
        } else {
        	doCancel();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
                if (mFirstTimeInitialized && event.getRepeatCount() == 0) {
                    doFocus(true);
                }
                return true;
            case KeyEvent.KEYCODE_CAMERA:
                if (mFirstTimeInitialized && event.getRepeatCount() == 0) {
                    doSnap();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (mFirstTimeInitialized && event.getRepeatCount() == 0) {
                    doFocus(true);
                    if (mShutterButton.isInTouchMode()) {
                        mShutterButton.requestFocusFromTouch();
                    } else {
                        mShutterButton.requestFocus();
                    }
                    mShutterButton.setPressed(true);
                }
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:
                if (mFirstTimeInitialized) {
                    doFocus(false);
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void doSnap() {
        if ((mFocusState == FOCUS_SUCCESS
                || mFocusState == FOCUS_FAIL)) {
            mImageCapture.onSnap();
        } else if (mFocusState == FOCUSING) {
            mFocusState = FOCUSING_SNAP_ON_FINISH;
        } else if (mFocusState == FOCUS_NOT_STARTED) {
        }
    }

    private void doFocus(boolean pressed) {
	    if (pressed) {
	        autoFocus();
	    } else {
	        cancelAutoFocus();
	    }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {return;}
        mSurfaceHolder = holder;
        if (mCameraDevice == null) return;
        if (mPausing || isFinishing()) return;
        if (mPreviewing && holder.isCreating()) {
            setPreviewDisplay(holder);
        } else {
            restartPreview();
        }
        if (!mFirstTimeInitialized) {
            mHandler.sendEmptyMessage(FIRST_TIME_INIT);
        } else {
            initializeSecondTime();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        mSurfaceHolder = null;
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            CameraHolder.instance().release();
            mCameraDevice.setZoomChangeListener(null);
            mCameraDevice = null;
            mPreviewing = false;
        }
    }

    private void ensureCameraDevice() throws CameraHardwareException {
        if (mCameraDevice == null) {
            mCameraDevice = CameraHolder.instance().open();
        }
    }

    private void showCameraErrorAndFinish() {
        Resources ress = getResources();
        showFatalErrorAndFinish(ress.getString(R.string.camera_error_title),ress.getString(R.string.cannot_connect_camera));
    }

    private void restartPreview() {
        try {
            startPreview();
        } catch (CameraHardwareException e) {
            showCameraErrorAndFinish();
            return;
        }
    }
    
    private void showFatalErrorAndFinish(
            String title, String message) {
        DialogInterface.OnClickListener buttonListener =
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.details_ok, buttonListener)
                .show();
    }

    private void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCameraDevice.setPreviewDisplay(holder);
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("setPreviewDisplay failed", ex);
        }
    }

    private void startPreview() throws CameraHardwareException {
        if (mPausing || isFinishing()) return;

        ensureCameraDevice();
        flashButton.setButtonCallback(flashCB);
        if (mPreviewing) stopPreview();

        setPreviewDisplay(mSurfaceHolder);
        setCameraParameters(UPDATE_PARAM_ALL);

        mCameraDevice.setErrorCallback(mErrorCallback);

        try {
            mCameraDevice.startPreview();
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("startPreview failed", ex);
        }
        mPreviewing = true;
        mStatus = IDLE;
    }

    private void stopPreview() {
        if (mCameraDevice != null && mPreviewing) {
            mCameraDevice.stopPreview();
        }
        mPreviewing = false;
        // If auto focus was in progress, it would have been canceled.
        clearFocusState();
    }

    private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void updateCameraParametersInitialize() {
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

    }

    private boolean setCameraPictureSize(
            String candidate, List<Size> supported, Parameters parameters) {
        int index = candidate.indexOf('x');
        if (index == NOT_FOUND) return false;
        int width = Integer.parseInt(candidate.substring(0, index));
        int height = Integer.parseInt(candidate.substring(index + 1));
        for (Size size: supported) {
            if (size.width == width && size.height == height) {
                parameters.setPictureSize(width, height);
                return true;
            }
        }
        return false;
    }
    
    private void updateCameraParametersPreference() {
        String pictureSize = mPreferences.getString(KEY_PICTURE_SIZE, null);
        if (pictureSize == null) {
            initialCameraPictureSize(this, mParameters);
        } else {
            List<Size> supported = mParameters.getSupportedPictureSizes();
            setCameraPictureSize(
                    pictureSize, supported, mParameters);
        }
        Size size = mParameters.getPictureSize();
        PreviewFrameLayout frameLayout = (PreviewFrameLayout) findViewById(R.id.frame_layout);
        frameLayout.setAspectRatio((double) size.width / size.height);
        List<Size> sizes = mParameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(
                sizes, (double) size.width / size.height);
        if (optimalSize != null) {
            mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }

        mParameters.setJpegQuality(85);
    }
    
    private void initialCameraPictureSize(Context context, Parameters parameters) {
        List<Size> supported = parameters.getSupportedPictureSizes();
        if (supported == null) return;
        for (String candidate : context.getResources().getStringArray(R.array.pref_camera_picturesize_entryvalues)) {
            if (setCameraPictureSize(candidate, supported, parameters)) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(KEY_PICTURE_SIZE, candidate);
                editor.commit();
                return;
            }
        }
    }

    private void setCameraParameters(int updateSet) {
        mParameters = mCameraDevice.getParameters();

        if ((updateSet & UPDATE_PARAM_INITIALIZE) != 0) {
            updateCameraParametersInitialize();
        }
        
        if ((updateSet & UPDATE_PARAM_PREFERENCE) != 0) {
            synchronized (mPreferences) {
                updateCameraParametersPreference();
            }
        }

        mCameraDevice.setParameters(mParameters);
    }
    
    private void setCameraParametersWhenIdle(int additionalUpdateSet) {
        mUpdateSet |= additionalUpdateSet;
        if (mCameraDevice == null) {
            mUpdateSet = 0;
            return;
        } else if (isCameraIdle()) {
            setCameraParameters(mUpdateSet);
            mUpdateSet = 0;
        } else {
            if (!mHandler.hasMessages(SET_CAMERA_PARAMETERS_WHEN_IDLE)) {
                mHandler.sendEmptyMessageDelayed(
                        SET_CAMERA_PARAMETERS_WHEN_IDLE, 1000);
            }
        }
    }

    private boolean isCameraIdle() {
        return mStatus == IDLE && mFocusState == FOCUS_NOT_STARTED;
    }

    private void setupCaptureParams() {
    	Bundle extras = getIntent().getExtras();
    	if(extras.containsKey(Constants.FULL_SIZE_IMAGE_PATH)) {
    		mSaveUri = Uri.fromFile(new File(extras.getString(Constants.FULL_SIZE_IMAGE_PATH)));
    	} else {
    		Toast.makeText(getApplicationContext(), getString(R.string.camera_error_title), Toast.LENGTH_LONG).show();
    		finish();
    	}
    }

    private void showPostCaptureAlert() {
    	mStatus = IMAGE_DISPLAYED;
        doAttach();
        findViewById(R.id.shutter_button).setVisibility(View.GONE);
        flashButton.setVisibility(View.GONE);
        int[] pickIds = {R.id.btn_retake, R.id.btn_done, R.id.btn_cancel};
        for (int id : pickIds) {
            CameraButton button = (CameraButton) findViewById(id);
            button.setVisibility(View.VISIBLE);
        }
        mStatus = IDLE;
    }

    private void hidePostCaptureAlert() {
        int[] pickIds = {R.id.btn_retake, R.id.btn_done, R.id.btn_cancel};
        for (int id : pickIds) {
            CameraButton button = (CameraButton) findViewById(id);
            button.setVisibility(View.GONE);
        }
        findViewById(R.id.shutter_button).setVisibility(View.VISIBLE);
        flashButton.setVisibility(View.VISIBLE);
    }

    private int calculatePicturesRemaining() {
        try {
            if (!hasStorage()) {
                return NO_STORAGE_ERROR;
            } else {
                String storageDirectory =
                        Environment.getExternalStorageDirectory().toString();
                StatFs stat = new StatFs(storageDirectory);
                final int PICTURE_BYTES = 1500000;
                mPicturesRemaining = (int)(((float) stat.getAvailableBlocks()
                        * (float) stat.getBlockSize()) / PICTURE_BYTES);
            }
        } catch (Exception ex) {
        	mPicturesRemaining = CANNOT_STAT_ERROR; 
        }
        return mPicturesRemaining;
    }
    
    private boolean hasStorage() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            boolean writable = checkFsWritable();
            return writable;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    private boolean checkFsWritable() {
        return new File(Constants.DIRECTORY).canWrite();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        keepScreenOnAwhile();
    }

    private void resetScreenOn() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void keepScreenOnAwhile() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
    }
}

class FocusRectangle extends View {

    public FocusRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable(int resid) {
        setBackgroundDrawable(getResources().getDrawable(resid));
    }

    public void showStart() {
        setDrawable(R.drawable.focus_focusing);
    }

    public void showSuccess() {
        setDrawable(R.drawable.focus_focused);
    }

    public void showFail() {
        setDrawable(R.drawable.focus_focus_failed);
    }

    public void clear() {
        setBackgroundDrawable(null);
    }
}

