package com.vinsol.expensetracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImageViewDialog extends Dialog implements
		android.content.DialogInterface.OnDismissListener,
		android.content.DialogInterface.OnCancelListener {

	private Context mContext;
	private Long _id = null;
	private android.widget.ImageView mImageView;
	private LinearLayout image_view_full_screen_progress;
	private String path;
	
	public ImageViewDialog(Context context, Long id) {
		super(context);
		_id = id;
		mContext = context;
		path = "/mnt/sdcard/ExpenseTracker/"+ _id + ".jpg";
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// //// ******** Making Layout Visible ******* /////////
		setContentView(R.layout.image_view_full_screen);

		mImageView = (android.widget.ImageView) findViewById(R.id.image_view_full_screen_id);
		image_view_full_screen_progress = (LinearLayout) findViewById(R.id.image_view_full_screen_progress);
		// _id = getBundleExtras.getLong("_id");
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			try {
				new ImageViewAsyncTask().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
		setOnDismissListener(this);
		setOnCancelListener(this);

		show();
	}
	
	public ImageViewDialog(Context context, Long id,String fav) {
		super(context);
		_id = id;
		mContext = context;
		path = "/mnt/sdcard/ExpenseTracker/Favorite/"+ _id + ".jpg";
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// //// ******** Making Layout Visible ******* /////////
		setContentView(R.layout.image_view_full_screen);

		mImageView = (android.widget.ImageView) findViewById(R.id.image_view_full_screen_id);
		image_view_full_screen_progress = (LinearLayout) findViewById(R.id.image_view_full_screen_progress);
		// _id = getBundleExtras.getLong("_id");
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			try {
				new ImageViewAsyncTask().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
		setOnDismissListener(this);
		setOnCancelListener(this);

		show();
	}

	private class ImageViewAsyncTask extends AsyncTask<Void, Void, Void> {

		Drawable drawable;

		@Override
		protected void onPreExecute() {
			image_view_full_screen_progress.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			drawable = Drawable.createFromPath(path);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			System.gc();
			image_view_full_screen_progress.setVisibility(View.GONE);

			mImageView.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mImageView.setImageDrawable(drawable);

			super.onPostExecute(result);
		}

	}

	// /// ****************** Handling back press of key ********** ///////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code above on earlier versions of the platform.
		dismiss();
		return;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		System.gc();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		System.gc();
	}
}
