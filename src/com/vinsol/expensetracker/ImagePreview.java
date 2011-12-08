package com.vinsol.expensetracker;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImagePreview extends Activity {

	private Long _id = null;
	private android.widget.ImageView mImageView;
	private LinearLayout image_view_full_screen_progress;
	private String path;
	
	private Drawable imageDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.image_view_full_screen);

		//Extras
		_id = getIntent().getLongExtra("id", 0);
		boolean isFavorite = getIntent().getBooleanExtra("isFavorite", false);
		
		path = "/mnt/sdcard/ExpenseTracker/" + (isFavorite ? "Favorite/" : "") + _id + ".jpg";
		
		mImageView = (android.widget.ImageView) findViewById(R.id.image_view_full_screen_id);
		image_view_full_screen_progress = (LinearLayout) findViewById(R.id.image_view_full_screen_progress);

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
			image_view_full_screen_progress.setVisibility(View.VISIBLE);
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
			image_view_full_screen_progress.setVisibility(View.GONE);

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
	
	
	

//	// /// ****************** Handling back press of key ********** ///////////
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			onBackPressed();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	public void onBackPressed() {
//		// This will be called either automatically for you on 2.0
//		// or later, or by the code above on earlier versions of the platform.
//		dismiss();
//		return;
//	}
}
