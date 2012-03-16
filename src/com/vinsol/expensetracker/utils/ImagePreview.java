/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.R;

public class ImagePreview extends Activity {

	private String id = null;
	private android.widget.ImageView mImageView;
	private String path;
	private String smallPath;
	
	private Drawable imageDrawable;

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
				new ImageViewAsyncTask().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	private class ImageViewAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mImageView.setImageDrawable(Drawable.createFromPath(smallPath));
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			imageDrawable = Drawable.createFromPath(path);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mImageView.setImageDrawable(imageDrawable);
			super.onPostExecute(result);
		}
	}
}
