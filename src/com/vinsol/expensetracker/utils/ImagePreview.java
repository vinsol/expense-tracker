/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.R;

public class ImagePreview extends Activity {

	private String id = null;
	private android.widget.ImageView mImageView;
	private String path;
	private String smallPath;
	private AsyncTask<Void, Void, Void> imageViewAsyncTask;
	private Bitmap largeFileBitmap;
	private Bitmap smallFileBitmap;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.image_view_full_screen);

		//Extras
		id = getIntent().getStringExtra(Constants.KEY_ID);
		boolean isFavorite = getIntent().getBooleanExtra(Constants.KEY_IS_FAVORITE, false);
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(!ExpenseTrackerApplication.isInitialized){ExpenseTrackerApplication.Initialize();}
		}
		path = Constants.DIRECTORY + (isFavorite ? "Favorite/" : "") + id + Constants.IMAGE_LARGE_SUFFIX;
		smallPath = Constants.DIRECTORY + (isFavorite ? "Favorite/" : "") + id + Constants.IMAGE_SMALL_SUFFIX;
		mImageView = (android.widget.ImageView) findViewById(R.id.image_view_full_screen_id);

		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			try {
				if(new File(path).canRead() && new File(smallPath).canRead()) {
					imageViewAsyncTask = new ImageViewAsyncTask().execute();
				} else {
					Toast.makeText(this, "File Not Found", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	private class ImageViewAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			smallFileBitmap = BitmapFactory.decodeFile(smallPath);
			if(!smallFileBitmap.isRecycled()) {mImageView.setImageBitmap(smallFileBitmap);}
		}

		@Override
		protected Void doInBackground(Void... params) {
			largeFileBitmap = BitmapFactory.decodeFile(path);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			smallFileBitmap.recycle();
			if(!largeFileBitmap.isRecycled()) {mImageView.setImageBitmap(largeFileBitmap);}
		}
	}
	
	@Override
	public void onBackPressed() {
		if(imageViewAsyncTask != null && (imageViewAsyncTask.getStatus().equals(Status.PENDING) || imageViewAsyncTask.getStatus().equals(Status.RUNNING))) {
			imageViewAsyncTask.cancel(true);
		}
		if(largeFileBitmap != null && !largeFileBitmap.isRecycled()) {
			largeFileBitmap.recycle();
		}
		if(smallFileBitmap != null && !smallFileBitmap.isRecycled()) {
			smallFileBitmap.recycle();
		}
		super.onBackPressed();
	}
}
