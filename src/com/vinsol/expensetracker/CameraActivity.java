package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.CameraFileSave;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.FileDelete;

public class CameraActivity extends Activity implements OnClickListener {

	private static final int PICTURE_RESULT = 35;
	private TextView text_voice_camera_header_title;
	private LinearLayout text_voice_camera_camera_details;
	private Long _id = null;
	private Bundle intentExtras;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView text_voice_camera_date_bar_dateview;
	private String dateViewString;
	private ArrayList<String> mEditList;
	private ImageView text_voice_camera_image_display;
	private RelativeLayout text_voice_camera_load_progress;
	private Button text_voice_camera_delete;
	private Button text_voice_camera_save_entry;
	private boolean setUnknown = false;
	private EditHelper mEditHelper;
	private boolean isChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_voice_camera);

		// //////********* Get id from intent extras ******** ////////////
		intentExtras = getIntent().getBundleExtra("cameraBundle");
		
		// ////// ******** Initializing and assigning memory to UI Items
		// ********** /////////

		text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
		text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.text_voice_camera_camera_details);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
		text_voice_camera_image_display = (ImageView) findViewById(R.id.text_voice_camera_image_display);
		text_voice_camera_load_progress = (RelativeLayout) findViewById(R.id.text_voice_camera_load_progress);
		text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);
		mEditHelper = new EditHelper(this, intentExtras, R.string.camera, R.string.finished_cameraentry, R.string.unfinished_cameraentry);
		getData();
		if (intentExtras.containsKey("mDisplayList")) {
			if(setUnknown){
				startCamera();
			}
			File mFile = new File("/sdcard/ExpenseTracker/" + _id + "_small.jpg");
			if (mFile.canRead()) {
				Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
				setImageResource(mDrawable);
			} else {
				text_voice_camera_image_display.setImageResource(R.drawable.no_image_small);
			}
		}
		
		setGraphicsCamera();
		setClickListeners();

		// ////// *********** Initializing Database Adaptor **********
		// //////////
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
		
		if(_id == null ) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				
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
								mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
								_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					_list.put(DatabaseAdapter.KEY_DATE_TIME, mCalendar.getTimeInMillis()+"");
				}
				
				if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
					_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
				}
				
				_list.put(DatabaseAdapter.KEY_TYPE, getString(R.string.camera));
				mDatabaseAdapter.open();
				_id = mDatabaseAdapter.insert_to_database(_list);
				mDatabaseAdapter.close();
				mEditHelper.setId(_id);
			}
		}
		
		if (!intentExtras.containsKey("mDisplayList"))
			startCamera();
		
	}
	
	private void getData() {
		_id = mEditHelper.getId();
		mEditList = mEditHelper.getEditList();
		intentExtras = mEditHelper.getIntentExtras();
		setUnknown = mEditHelper.isSetUnknown();
		isChanged = mEditHelper.isChanged();
	}

	private void setImageResource(Drawable mDrawable) {
		if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
			final float scale = this.getResources().getDisplayMetrics().density;
			int width = (int) (84 * scale + 0.5f);
			int height = (int) (111 * scale + 0.5f);			
			text_voice_camera_image_display.setLayoutParams(new LayoutParams(width, height));
		}
		text_voice_camera_image_display.setImageDrawable(mDrawable);
	}
	
	private void startCamera() {
		// ///// ******* Starting Camera to capture Image ******** //////////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File path = new File("/mnt/sdcard/ExpenseTracker");
			path.mkdirs();
			String name = _id + ".jpg";
			File file = new File(path, name);
			camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(camera, PICTURE_RESULT);
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (PICTURE_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				isChanged = true;
				mEditHelper.setChanged(isChanged);
				new SaveAndDisplayImage().execute();
			} else {
				isChanged = false;
				mEditHelper.setChanged(isChanged);
				if(!setUnknown) {
					File mFile = new File("/sdcard/ExpenseTracker/" + _id+ "_small.jpg");
					if (mFile.canRead()) {
						Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
						setImageResource(mDrawable);
					} else {
						DatabaseAdapter adapter = new DatabaseAdapter(this);
						adapter.open();
						adapter.deleteDatabaseEntryID(_id + "");
						adapter.close();
					}
				}
				if(!intentExtras.containsKey("isFromShowPage")) {
					finish();
				}
			}
		}
	}

	private class SaveAndDisplayImage extends AsyncTask<Void, Void, Void> {
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
			new CameraFileSave(CameraActivity.this).resizeImageAndSaveThumbnails(_id + "");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			text_voice_camera_load_progress.setVisibility(View.GONE);
			text_voice_camera_image_display.setVisibility(View.VISIBLE);
			File mFile = new File("/sdcard/ExpenseTracker/" + _id+ "_small.jpg");
			Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
			setImageResource(mDrawable);
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
			if(intentExtras.containsKey("isFromShowPage")){
				Intent mIntent = new Intent(this, ShowTextActivity.class);
				ArrayList<String> listOnResult = new ArrayList<String>();
				listOnResult.add("");
				Bundle tempBundle = new Bundle();
				tempBundle.putStringArrayList("mDisplayList", listOnResult);
				mEditList = new ArrayList<String>();
				mEditList.addAll(listOnResult);
				mIntent.putExtra("textShowBundle", tempBundle);
				setResult(Activity.RESULT_CANCELED, mIntent);
			}
			finish();
		}

		// //////// ********** Adding action if image is pressed ********		 ///////////
		if (v.getId() == R.id.text_voice_camera_image_display) {
			File mFile = new File("/sdcard/ExpenseTracker/" + _id + ".jpg");
			if(mFile.canRead()) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", _id);
				startActivity(intent);
			} else {
				Toast.makeText(this, "no image to preview", Toast.LENGTH_SHORT).show();
			}
		}

		// /////// ********** Adding action if retake button is pressed ******//////////
		if (v.getId() == R.id.text_voice_camera_retake_button) {
			startCamera();
		}
	}

	private void saveEntry() {
		
		HashMap<String, String> _list = mEditHelper.getSaveEntryData(text_voice_camera_date_bar_dateview,dateViewString);
		
		// //// ******* Update database if user added additional info *******		 ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
		if(!intentExtras.containsKey("isFromShowPage")){
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString("toHighLight", _list.get(DatabaseAdapter.KEY_ID));
			intentExpenseListing.putExtras(mToHighLight);
			intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentExpenseListing);
		} else {
			Intent mIntent = new Intent(this, ShowCameraActivity.class);
			Bundle tempBundle = new Bundle();
			tempBundle.putStringArrayList("mDisplayList", mEditHelper.getListOnResult(_list));
			getData();
			mIntent.putExtra("cameraShowBundle", tempBundle);
			setResult(Activity.RESULT_OK, mIntent);
		}
		finish();
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
