package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CameraFileSave {
	private String filename;
	private int width;
	private int height;
	
	///////   *******    Declaring Constants   ********   ///////////
	private int SMALL_MAX_HEIGHT = 120;
	private int SMALL_MAX_WIDTH = 160;

	private int THUMBNAIL_MAX_HEIGHT = 60;
	private int THUMBNAIL_MAX_WIDTH = 60;
	
	///////   ********  Image Clicked using Camera   *******   /////////
	private Bitmap imageByCamera ;
	
	///////   *********    ExpenseTracker Directory Location  ********  /////////
	private File mExpenseTrackerDirectory; 
	
	///////   **********    File to save Bitmap *********   /////////
	private File mFileToSaveBitmap;
	
	////////   ********  Location of Image Clicked by camera   ********   //////////
	private File mPathImageByCamera;
	
	
	public CameraFileSave(String _filename) {
		filename = _filename;
		mExpenseTrackerDirectory = new File("/mnt/sdcard/ExpenseTracker");
		mExpenseTrackerDirectory.mkdirs();
		mPathImageByCamera = new File(mExpenseTrackerDirectory, filename+".jpg");
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(mPathImageByCamera);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageByCamera = BitmapFactory.decodeStream(fileInputStream);
		try {
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(imageByCamera.getHeight() > imageByCamera.getWidth()){
			SMALL_MAX_WIDTH = 120;
			SMALL_MAX_HEIGHT = 160;
		}
	}

	private void CameraFileSaveFunc(Bitmap bitmapToSave) {
		FileOutputStream out = null;
		mFileToSaveBitmap.mkdirs();
		try {
			out = new FileOutputStream(mFileToSaveBitmap);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
        	out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        bitmapToSave.recycle();
	}
	
	public void create(){
		createSmall();
		createThumbnail();
	}
	
	public void createSmall(){
		width = SMALL_MAX_WIDTH;
		height = SMALL_MAX_HEIGHT;
		mFileToSaveBitmap = new File(mExpenseTrackerDirectory, filename+"_small"+".jpg");
		CameraFileSaveFunc(getBitmap());
	}
	
	
	
	////////////  *********   To create thumbnail Image of size 60 x 60  **********   ///////////
	
	public void createThumbnail(){
		width = THUMBNAIL_MAX_WIDTH;
		height = THUMBNAIL_MAX_HEIGHT;
		mFileToSaveBitmap = new File(mExpenseTrackerDirectory, filename+"_thumbnail"+".jpg");
		CameraFileSaveFunc(getBitmap());
	}
	
	
	////////////    ********    To get Bitmap Image of the picture clicked through camera    ********* ///////
	
	
	public Bitmap getBitmap(){
		int scale = getScale(imageByCamera);
		BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
			return BitmapFactory.decodeStream(new FileInputStream(mPathImageByCamera), null, o2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	
	
	//////////     **********     Scale to which Image Reduced    *********   ////////////
	
	private int getScale(Bitmap bip){
		//Find the correct scale value. It should be the power of 2.
        int width_tmp=bip.getWidth(), height_tmp=bip.getHeight();
        bip.recycle();
		int scale=1;
        while(true){
            if(width_tmp/2<width || height_tmp/2<height)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }
        Log.v("scale", scale+"");
        return scale;
	}
	
}
