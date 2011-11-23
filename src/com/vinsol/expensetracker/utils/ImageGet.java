package com.vinsol.expensetracker.utils;

import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class ImageGet {

	private String mFileName;
	private File mFile;	
	
	///////   *********    ExpenseTracker Directory Location  ********  /////////
	private File mExpenseTrackerDirectory; 
	
	private Context mContext;
	
	////////   ********   Constructor **********  ///////////
	public ImageGet(String file,Context _context) {
		mContext = _context;
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			mFileName = file;
			mExpenseTrackerDirectory = new File("/sdcard/ExpenseTracker");
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	//////////   ******** get image of dimension 160x120  *********   ///////////
	public Bitmap getSmallImage(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			mFile = new File(mExpenseTrackerDirectory,mFileName+"_small"+".jpg");
			Bitmap bm = BitmapFactory.decodeFile(mFile.getPath());
			return bm;
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
			return null;
		}
	}
	
	
	///////////   *********  get image of dimension 60 x60   **********    /////////
	public Bitmap getThumbnailImage(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			mFile = new File(mExpenseTrackerDirectory,mFileName+"_thumbnail"+".jpg");
			Bitmap bm = BitmapFactory.decodeFile(mFile.getPath());
			return bm;
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
			return null;
		}
	} 
	
}
