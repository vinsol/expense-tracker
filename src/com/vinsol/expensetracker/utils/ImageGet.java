package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageGet {

	private String mFileName;
	private File mFile;

	private int width;
	private int height;
	
	
	///////   *********    ExpenseTracker Directory Location  ********  /////////
	private File mExpenseTrackerDirectory; 
	
	///////   *******    Declaring Constants   ********   ///////////
	private int SMALL_MAX_HEIGHT = 120;
	private int SMALL_MAX_WIDTH = 160;

	private int THUMBNAIL_MAX_HEIGHT = 60;
	private int THUMBNAIL_MAX_WIDTH = 60;
	
	
	////////   ********   Constructor **********  ///////////
	public ImageGet(String file) {
		mFileName = file;
		mExpenseTrackerDirectory = new File("/mnt/sdcard/ExpenseTracker");
		
		File mPathImageByCamera = new File(mExpenseTrackerDirectory, file+".jpg");
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(mPathImageByCamera);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap imageByCamera = BitmapFactory.decodeStream(fileInputStream);
		try {
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(imageByCamera.getHeight() > imageByCamera.getWidth()){
			Log.v("true", "true");
			SMALL_MAX_WIDTH = 120;
			SMALL_MAX_HEIGHT = 160;
		}
		imageByCamera.recycle();
	}
	
	//////////   ******** get image of dimension 160x120  *********   ///////////
	public Bitmap getSmallImage(){
		mFile = new File(mExpenseTrackerDirectory,mFileName+"_small"+".jpg");
		height = SMALL_MAX_HEIGHT;
		width = SMALL_MAX_WIDTH;
		return getBitmap();
	}
	
	
	///////////   *********  get image of dimension 60 x60   **********    /////////
	public Bitmap getThumbnailImage(){
		mFile = new File(mExpenseTrackerDirectory,mFileName+"_thumbnail"+".jpg");
		height = THUMBNAIL_MAX_HEIGHT;
		width = THUMBNAIL_MAX_WIDTH;
		return getBitmap();
	} 
	
	
	///////////   **********   get cropped bitmap Image   **********   ///////////
	private Bitmap getBitmap(){
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(mFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap tempBitmap = BitmapFactory.decodeStream(fileInputStream);
		
		int defaultHeight = tempBitmap.getHeight();
        int defaultWidth = tempBitmap.getWidth();
        int tempHeight = height;
        int tempWidth = width;
        int i = 1;
        do{
     	   tempHeight = height * i;
     	   tempWidth = width * i;
     	   Log.v("height calc", height+"  hjkhkj height   "+defaultHeight);
     	   Log.v("width calc", width+"   hasdkash  width   "+defaultWidth);
     	   i++;
        }while(tempHeight < (defaultHeight-height) && tempWidth < (defaultWidth-width) );
        i--;
        
        Log.v("defaultHeight", defaultHeight+" height "+height+" tempheight "+tempHeight);
        Log.v("defaultWidth", defaultWidth+" Width "+width+" tempWidth "+tempWidth);
        
        
        int diffHeight = defaultHeight - height;
        int diffWidth = defaultWidth - width;
        int x = diffWidth / 2;
        int y = diffHeight / 2;
        int finalx;
        int finaly;
        if(diffWidth % 2 == 0)
        	   finalx = defaultWidth - x;
        else
     	   finalx = defaultWidth - x+1;
        if(diffHeight % 2 == 0)
        	   finaly = defaultHeight - y;
        else
     	   finaly = defaultHeight - y+1;
        
		Log.v("height", tempBitmap.getHeight()+" "+y+" "+finaly + " "+defaultHeight+ " "+defaultHeight + " "+height);
		Log.v("width", tempBitmap.getWidth()+" "+x+" "+finalx + " "+defaultWidth+ " "+defaultWidth+" "+width);
		
        return Bitmap.createBitmap(tempBitmap, x, y, finalx, finaly);
	}
	
}
