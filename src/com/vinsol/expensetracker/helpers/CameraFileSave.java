/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CameraFileSave {
	
	private String filename;
	private FileHelper fileHelper;

	// ///// ******* Declaring Constants ******** ///////////
	private int FULL_SIZE_IMAGE_WIDTH;
	private int FULL_SIZE_IMAGE_HEIGHT;
	
	private int SMALL_MAX_WIDTH = 160;
	private int SMALL_MAX_HEIGHT = 120;
	
	private int THUMBNAIL_MAX_HEIGHT = 60;
	private int THUMBNAIL_MAX_WIDTH = 60;

	// /////// ********* Constructors ******** /////////////
	public CameraFileSave(Context _context) {
		fileHelper = new FileHelper();
	}
	
	// /////// ********* Resize original Image and save thumbnails ******** /////////////
	public void resizeImageAndSaveThumbnails(String _filename, boolean isFromFavorite) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			filename = _filename;
			File fullSizeImage;
			if(isFromFavorite) {
				fullSizeImage = fileHelper.getCameraFileLargeFavorite(filename);
			} else {
				fullSizeImage = fileHelper.getCameraFileLargeEntry(filename);
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inJustDecodeBounds = true;
			Bitmap fullSizeBitmap = BitmapFactory.decodeFile(fullSizeImage.toString(), options);
			if(options.outWidth > 0 && options.outHeight > 0) {
				FULL_SIZE_IMAGE_WIDTH = options.outWidth;
				FULL_SIZE_IMAGE_HEIGHT = options.outHeight;
				if(fullSizeBitmap != null)
					fullSizeBitmap.recycle();
					//Save small image
				Bitmap bitmap = getBitmap(fullSizeImage, SMALL_MAX_WIDTH, SMALL_MAX_HEIGHT);
				File smallImage;
				if(isFromFavorite) {
					smallImage = fileHelper.getCameraFileSmallFavorite(filename);
				} else {
					smallImage = fileHelper.getCameraFileSmallEntry(filename);
				}
				saveImage(smallImage, bitmap);
				bitmap.recycle();
				//save Small thumbnail
				bitmap = getBitmap(fullSizeImage, THUMBNAIL_MAX_WIDTH, THUMBNAIL_MAX_HEIGHT);
				File thumbnail;
				if(isFromFavorite) {
					thumbnail = fileHelper.getCameraFileThumbnailFavorite(filename);
				} else {
					thumbnail = fileHelper.getCameraFileThumbnailEntry(filename);
				}
				saveImage(thumbnail, bitmap);
				bitmap.recycle();
			}
		}
	}

	// //////// ********* Function to save File ********* /////////
	private void saveImage(File file, Bitmap bitmapToSave) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 60, out);
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/////////// ********* Clear Bitmap to save VM space ********* /////////
		}
	}

	//////////// ******** To get Bitmap Image of the picture clicked through camera ********* ///////
	private Bitmap getBitmap(File file, int width, int height) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			int scale = getScale(FULL_SIZE_IMAGE_WIDTH, FULL_SIZE_IMAGE_HEIGHT, width, height);
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inPurgeable = true;
			o2.inSampleSize = scale;
			return BitmapFactory.decodeFile(file.toString(), o2);
		}
		return null;
	}


	// //////// ********** Scale to which Image Reduced ********* ////////////

	private int getScale(int originalImageWidth, int originalImageHeight, int requiredWidth, int requiredHeight) {
		int scale = 1;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			// Find the correct scale value. It should be the power of 2.
			while (true) {
				if (originalImageWidth / 2 < requiredWidth || originalImageHeight / 2 < requiredHeight)
					break;
				originalImageWidth /= 2;
				originalImageHeight /= 2;
				scale *= 2;
			}
		}
		return scale;
	}
}
