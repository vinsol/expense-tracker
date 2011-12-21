package com.vinsol.expensetracker.utils;

import com.vinsol.expensetracker.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImagePreview extends Activity {

	private Long userId = null;
	private android.widget.ImageView mImageView;
	private LinearLayout progressBar;
	private String path;
	
	private Drawable imageDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.image_view_full_screen);

		//Extras
		userId = getIntent().getLongExtra("id", 0);
		boolean isFavorite = getIntent().getBooleanExtra("isFavorite", false);
		
		path = "/mnt/sdcard/ExpenseTracker/" + (isFavorite ? "Favorite/" : "") + userId + ".jpg";
		
		mImageView = (android.widget.ImageView) findViewById(R.id.image_view_full_screen_id);
		progressBar = (LinearLayout) findViewById(R.id.image_view_full_screen_progress);

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
			progressBar.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			imageDrawable = Drawable.createFromPath(path);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageDrawable(imageDrawable);
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		imageDrawable = null;
		mImageView.setImageDrawable(null);
	}
}
