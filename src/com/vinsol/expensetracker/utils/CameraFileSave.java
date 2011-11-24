package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

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
	
	private Context mContext;
	
	/////////    *********   Constructors   ********    /////////////
	public CameraFileSave(String _filename,Context _context) {
		mContext = _context;
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			filename = _filename;
			mExpenseTrackerDirectory = new File("/sdcard/ExpenseTracker");
			mExpenseTrackerDirectory.mkdirs();
			mPathImageByCamera = new File(mExpenseTrackerDirectory, filename+".jpg");
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(mPathImageByCamera);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.gc();
			imageByCamera = BitmapFactory.decodeStream(fileInputStream);
			
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
			//////////   *******   To handle Portrait Layout   *******   /////////
			if(imageByCamera.getHeight() > imageByCamera.getWidth()){
				SMALL_MAX_WIDTH = 120;
				SMALL_MAX_HEIGHT = 160;
			}	
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	
	//////////     *********    Function to save File    *********    /////////
	private void CameraFileSaveFunc(Bitmap bitmapToSave) {
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(mFileToSaveBitmap);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        
			///////////    *********   Clear Bitmap to save VM space   *********   /////////
			bitmapToSave.recycle();
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	/////////     ***********   Create Both Small and thumbnail file   *********   /////////
	public void create(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			createSmall();
			createThumbnail();
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	
	///////   ********* Create Small Bitmap   of size 160 x 120   ********   ///////////
	public void createSmall(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			width = SMALL_MAX_WIDTH;
			height = SMALL_MAX_HEIGHT;
			mFileToSaveBitmap = new File(mExpenseTrackerDirectory, filename+"_small"+".jpg");
			CameraFileSaveFunc(getBitmap());
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	////////////  *********   To create thumbnail Image of size 60 x 60  **********   ///////////
	
	public void createThumbnail(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			width = THUMBNAIL_MAX_WIDTH;
			height = THUMBNAIL_MAX_HEIGHT;
			mFileToSaveBitmap = new File(mExpenseTrackerDirectory, filename+"_thumbnail"+".jpg");
			CameraFileSaveFunc(getBitmap());
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	
	////////////    ********    To get Bitmap Image of the picture clicked through camera    ********* ///////
	
	
	public Bitmap getBitmap(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			int scale = getScale(imageByCamera);
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			try {
				System.gc();
				return BitmapFactory.decodeStream(new FileInputStream(mPathImageByCamera), null, o2);
			} catch (FileNotFoundException e) {
				Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
		return null;
	}
	
	
	//////////     **********     Scale to which Image Reduced    *********   ////////////
	
	private int getScale(Bitmap bip){
		int scale=1;
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
		//Find the correct scale value. It should be the power of 2.
			int width_tmp=bip.getWidth(), height_tmp=bip.getHeight();
			bip.recycle();
			while(true){
				if(width_tmp/2<width || height_tmp/2<height)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale*=2;
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
        return scale;
	}
	
}
