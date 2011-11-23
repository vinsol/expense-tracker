package com.vinsol.expensetracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageViewActivity extends Activity {
	private Long _id;
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//////   ********  Making Layout Visible   *******  /////////
		setContentView(R.layout.image_view_full_screen);
		
		Bundle getBundleExtras= getIntent().getBundleExtra("intentImageViewActivity");
		mImageView = (ImageView) findViewById(R.id.image_view_full_screen_id);
		
		getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		_id = getBundleExtras.getLong("_id");
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/ExpenseTracker/"+_id+".jpg");
			mImageView.setImageBitmap(bitmap);
			} 
		else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
}
