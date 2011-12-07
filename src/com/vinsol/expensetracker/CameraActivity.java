package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.CameraFileSave;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.FileCopyFavorite;
import com.vinsol.expensetracker.utils.FileDelete;
import com.vinsol.expensetracker.utils.ImageGet;

public class CameraActivity extends Activity implements OnClickListener {

	private static final int PICTURE_RESULT = 35;
	private TextView text_voice_camera_header_title;
	private LinearLayout text_voice_camera_camera_details;
	private Long _id = null;
	private Bundle intentExtras;
	private EditText text_voice_camera_amount;
	private EditText text_voice_camera_tag;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView text_voice_camera_date_bar_dateview;
	private String dateViewString;
	private ArrayList<String> mEditList;
	private ImageView text_voice_camera_image_display;
	private RelativeLayout text_voice_camera_load_progress;
	private Button text_voice_camera_delete;
	private Button text_voice_camera_save_entry;
	private boolean setLocation = false;
	private boolean setUnknown = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_voice_camera);

		// //////********* Get id from intent extras ******** ////////////
		intentExtras = getIntent().getBundleExtra("cameraBundle");
		if (intentExtras.containsKey("_id"))
			_id = intentExtras.getLong("_id");

		if(intentExtras.containsKey("setLocation")){
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		// ////// ******** Initializing and assigning memory to UI Items
		// ********** /////////

		text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
		text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.text_voice_camera_camera_details);
		text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
		text_voice_camera_tag = (EditText) findViewById(R.id.text_voice_camera_tag);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
		text_voice_camera_image_display = (ImageView) findViewById(R.id.text_voice_camera_image_display);
		text_voice_camera_load_progress = (RelativeLayout) findViewById(R.id.text_voice_camera_load_progress);
		text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);

		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mEditList.get(0));
			String amount = mEditList.get(2);
			String tag = mEditList.get(1);
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					text_voice_camera_amount.setText(amount);
			}
			if(tag.equals(getString(R.string.unknown_entry))){
				setUnknown = true;
				startCamera();
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(getString(R.string.unfinished_cameraentry)) || tag.equals(getString(R.string.finished_cameraentry))  || tag.equals(getString(R.string.unknown_entry)))) {
				text_voice_camera_tag.setText(tag);
			}
			File mFile = new File("/sdcard/ExpenseTracker/" + _id
					+ "_small.jpg");
			System.gc();
			if (mFile.canRead()) {
				Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
				text_voice_camera_image_display.setImageDrawable(mDrawable);
			} else {
				text_voice_camera_image_display
						.setImageResource(R.drawable.no_image_small);
			}
		}
		setGraphicsCamera();
		setClickListeners();
		

		// //////******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, Long.parseLong(mEditList.get(6)));
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}

		// ////// *********** Initializing Database Adaptor **********
		// //////////
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
		
		if(_id == null ) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				mDatabaseAdapter.open();
				HashMap<String, String> _list = new HashMap<String, String>();
				if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
					try {
						if (!intentExtras.containsKey("mDisplayList")) {
							DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
							_list.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
						} else {
							if(!intentExtras.containsKey("timeInMillis")){
								DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
								_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
								DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
								_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Calendar mCalendar = Calendar.getInstance();
					_list.put(DatabaseAdapter.KEY_DATE_TIME, mCalendar.getTimeInMillis()+"");
				}
				
				if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
					_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
				}
				
				_list.put(DatabaseAdapter.KEY_TYPE, getString(R.string.camera));
				_id = mDatabaseAdapter.insert_to_database(_list);
				mDatabaseAdapter.close();
			}
		}
		
		if (!intentExtras.containsKey("mDisplayList"))
			startCamera();
	}
	
	private void startCamera() {

		// ///// ******* Starting Camera to capture Image ******** //////////
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File path = new File("/mnt/sdcard/ExpenseTracker");
			path.mkdirs();
			String name = _id + ".jpg";
			File file = new File(path, name);
			camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(file));
			startActivityForResult(camera, PICTURE_RESULT);
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (PICTURE_RESULT == requestCode && Activity.RESULT_OK == resultCode) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {

				new ImageViewCameraAsyncTask().execute();

			} else {
				text_voice_camera_image_display
						.setImageResource(R.drawable.no_image_small);
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			if(!setUnknown){
				File mFile = new File("/sdcard/ExpenseTracker/" + _id+ "_small.jpg");
				if (mFile.canRead()) {
					ImageGet imageGet = new ImageGet("" + _id, this);
					Bitmap bm = imageGet.getSmallImage();
					text_voice_camera_image_display.setImageBitmap(bm);
				} else {
					DatabaseAdapter adapter = new DatabaseAdapter(this);
					adapter.open();
					adapter.deleteDatabaseEntryID(_id + "");
					adapter.close();
				}
			}
			finish();
		}
	}

	private class ImageViewCameraAsyncTask extends AsyncTask<Void, Void, Void> {

		Bitmap bm;

		@Override
		protected void onPreExecute() {
			text_voice_camera_load_progress.setVisibility(View.VISIBLE);
			text_voice_camera_image_display.setVisibility(View.GONE);
			text_voice_camera_delete.setEnabled(false);
			text_voice_camera_save_entry.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			CameraFileSave cameraFileSave = new CameraFileSave(_id + "",
					CameraActivity.this);
			cameraFileSave.create();
			System.gc();
			ImageGet imageGet = new ImageGet("" + _id, CameraActivity.this);
			bm = imageGet.getSmallImage();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			text_voice_camera_load_progress.setVisibility(View.GONE);
			text_voice_camera_image_display.setVisibility(View.VISIBLE);
			text_voice_camera_image_display.setImageBitmap(bm);
			text_voice_camera_delete.setEnabled(true);
			text_voice_camera_save_entry.setEnabled(true);
			super.onPostExecute(result);
		}

	}

	private void setGraphicsCamera() {
		// ///// ***** Sets Title Camera Entry *********///////
		text_voice_camera_header_title.setText("Camera Entry");

		// //// ****** Shows Camera Details ********////////
		text_voice_camera_camera_details.setVisibility(View.VISIBLE);
	}

	private void setClickListeners() {
		// ////// ******* Adding Click Listeners to UI Items ******** //////////

		text_voice_camera_save_entry.setOnClickListener(this);

		text_voice_camera_delete.setOnClickListener(this);

		ImageView text_voice_camera_image_display = (ImageView) findViewById(R.id.text_voice_camera_image_display);
		text_voice_camera_image_display.setOnClickListener(this);

		Button text_voice_camera_retake_button = (Button) findViewById(R.id.text_voice_camera_retake_button);
		text_voice_camera_retake_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// ////// ******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.text_voice_camera_save_entry) {
			saveEntry();
		}

		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.text_voice_camera_delete) {
			new FileDelete(_id);

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
			mDatabaseAdapter.close();
			finish();
		}

		// //////// ********** Adding action if image is pressed ********
		// ///////////

		if (v.getId() == R.id.text_voice_camera_image_display) {
			
			new ImageViewDialog(this, _id);

		}

		// /////// ********** Adding action if retake button is pressed ******
		// ////////

		if (v.getId() == R.id.text_voice_camera_retake_button) {
			startCamera();
		}
	}

	private void saveEntry() {
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> _list = new HashMap<String, String>();
		_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));

		if (!text_voice_camera_amount.getText().toString().equals(".")&& !text_voice_camera_amount.getText().toString().equals("")) {
			Double mAmount = Double.parseDouble(text_voice_camera_amount.getText().toString());
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			_list.put(DatabaseAdapter.KEY_AMOUNT, mAmount.toString());
		} else {
			_list.put(DatabaseAdapter.KEY_AMOUNT, null);
		}
		if (text_voice_camera_tag.getText().toString() != "") {
			_list.put(DatabaseAdapter.KEY_TAG, text_voice_camera_tag.getText().toString());
		}

		if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey("mDisplayList")) {
					DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
					_list.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
				} else {
					if(!intentExtras.containsKey("timeInMillis")){
						DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
						_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
						DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
						_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(setLocation == true && LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
				_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		// //// ******* Update database if user added additional info *******
		// ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
		finish();
		if (!intentExtras.containsKey("timeInMillis")
				&& !intentExtras.containsKey("mDisplayList")) {
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			startActivity(intentExpenseListing);
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
		saveEntry();
		return;
	}
}
