package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vinsol.expensetracker.location.LocationData;
import com.vinsol.expensetracker.location.LocationLast;

public class MainActivity extends Activity implements OnClickListener {
	public static String mCurrentLocation;
	public static Location mLocation;
	private DatabaseAdapter mDatabaseAdapter;
	private long timeInMillis = 0;
	private Bundle bundle;
	private Long _id = null; 
	private ArrayList<String> mTempClickedList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		bundle = new Bundle();
		if (getIntent().hasExtra("mainBundle")) {
			Bundle tempBundle = getIntent().getBundleExtra("mainBundle");
			if (!tempBundle.isEmpty()) {
				if (tempBundle.containsKey("timeInMillis"))
					timeInMillis = tempBundle.getLong("timeInMillis");
				if(tempBundle.containsKey("mDisplayList")){
					mTempClickedList = tempBundle.getStringArrayList("mDisplayList");
					_id = Long.parseLong(mTempClickedList.get(0));
				}
			}
		}

		// /////// ********* DatabaseAdaptor initialize ********* ////////
		mDatabaseAdapter = new DatabaseAdapter(this);

		// //// ********* Adding Click Listeners to MainActivity ********** /////////

		// opens text entry Activity
		Button textButton = (Button) findViewById(R.id.main_text);
		textButton.setOnClickListener(this);

		// opens voice Activity
		Button voiceButton = (Button) findViewById(R.id.main_voice);
		voiceButton.setOnClickListener(this);

		// opens Camera Activity
		Button cameraButton = (Button) findViewById(R.id.main_camera);
		cameraButton.setOnClickListener(this);

		// opens Favorite Activity
		Button favoriteButton = (Button) findViewById(R.id.main_favorite);
		favoriteButton.setOnClickListener(this);

		// opens Save Reminder Activity
		Button saveReminderButton = (Button) findViewById(R.id.main_save_reminder);
		saveReminderButton.setOnClickListener(this);

		// opens ListView
		ImageView showListingButton = (ImageView) findViewById(R.id.main_listview);
		showListingButton.setOnClickListener(this);

	}


	@Override
	protected void onResume() {

		// /////// ******** Starts GPS and Check for Location each time Activity
		// Resumes ******* ////////
		new LocationData(this);
		LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		try{
			mTempClickedList.get(0);
		} catch(Exception e){
			_id = null;
		}
//		new HandleGraph(this).execute();
		super.onResume();
	}

	
	@Override
	protected void onPause() {
		if(getIntent().hasExtra("mainBundle")) {
			Bundle tempBundle = getIntent().getBundleExtra("mainBundle");
			if (!tempBundle.isEmpty()) {
				if(tempBundle.containsKey("mDisplayList")) {
					finish();
				}
			}
		}
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		mLocation = null;
		mCurrentLocation = null;
		super.onStop();
	}
	
	@Override
	public void onClick(View clickedView) {
		int idOfClickedView = clickedView.getId();
		switch (idOfClickedView) {
		// //// ******* opens TextEntry Activity ******** ///////////
		case R.id.main_text:
			Intent intentTextEntry = new Intent(this, TextEntry.class);
			if(_id == null ) {
				_id = insertToDatabase(R.string.text);
				bundle.putLong("_id", _id);
				if(mCurrentLocation != null) {
					if(!mCurrentLocation.equals("")) {
						bundle.putBoolean("setLocation", false);
					} else {
						bundle.putBoolean("setLocation", true);
					}
				} else {
					bundle.putBoolean("setLocation", true);
				}
			} else {
				Log.v("_id", _id+" 56");
				bundle.putStringArrayList("mDisplayList", mTempClickedList);
				editDatabase(R.string.text);
			}
			intentTextEntry.putExtra("textEntryBundle", bundle);
			startActivity(intentTextEntry);
			break;
			
		// //// ******* opens Voice Activity ******** ///////////
		case R.id.main_voice:
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				Intent intentVoice = new Intent(this, Voice.class);
				if(_id == null){
					_id = insertToDatabase(R.string.voice);
					bundle.putLong("_id", _id);
					if(mCurrentLocation != null){
						if(!mCurrentLocation.equals("")){
							bundle.putBoolean("setLocation", false);
						} else {
							bundle.putBoolean("setLocation", true);
						}
					} else {
						bundle.putBoolean("setLocation", true);
					}
				} else {
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					editDatabase(R.string.voice);
				}
				intentVoice.putExtra("voiceBundle", bundle);
				startActivity(intentVoice);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			break;

		// //// ******* opens Camera Activity ******** ///////////
		case R.id.main_camera:
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				Intent intentCamera = new Intent(this, CameraActivity.class);
				if(_id == null) {
					_id = insertToDatabase(R.string.camera);
					bundle.putLong("_id", _id);
					if(mCurrentLocation != null){
						if(!mCurrentLocation.equals("")){
							bundle.putBoolean("setLocation", false);
						} else {
							bundle.putBoolean("setLocation", true);
						}
					} else {
						bundle.putBoolean("setLocation", true);
					}
				} else {
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					editDatabase(R.string.camera);
				}
				intentCamera.putExtra("cameraBundle", bundle);
				startActivity(intentCamera);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			break;
			
		// //// ******* opens Favorite Activity ******** ///////////
		case R.id.main_favorite:
			Intent intentFavorite = new Intent(this, FavoriteActivity.class);
			if(_id == null) {
				if (timeInMillis != 0){
					bundle.putLong("timeInMillis", timeInMillis);
				}
			} else {
				bundle.putStringArrayList("mDisplayList", mTempClickedList);
			}
			// long _id = insertToDatabase(R.string.favorite_entry);
			// bundle.putLong("_id", _id);
			intentFavorite.putExtra("favoriteBundle", bundle);
			startActivity(intentFavorite);	
			break;
			
		// //// ******* opens List Activity and adds unknown entry to database ******** ///////////
		case R.id.main_save_reminder:
			if(_id == null) 
				insertToDatabase(R.string.unknown);
			Intent intentListView = new Intent(this, ExpenseListing.class);
			startActivity(intentListView);
			break;
		
		// //// ******* opens ListView Activity ******** ///////////
		case R.id.main_listview:
			Intent intentListView2 = new Intent(this, ExpenseListing.class);
			startActivity(intentListView2);
			break;
		}//end switch
	}

	// /////// ******** function to mark entry into the database and returns the
	// id of the new entry ***** //////
	private long insertToDatabase(int type) {
		HashMap<String, String> _list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		if (timeInMillis == 0)
			_list.put(DatabaseAdapter.KEY_DATE_TIME,Long.toString(mCalendar.getTimeInMillis()));
		else {
			bundle.putLong("timeInMillis", timeInMillis);
			_list.put(DatabaseAdapter.KEY_DATE_TIME,Long.toString(timeInMillis));
			finish();
		}

		if (MainActivity.mCurrentLocation != null) {
			_list.put(DatabaseAdapter.KEY_LOCATION,MainActivity.mCurrentLocation);
		}
		_list.put(DatabaseAdapter.KEY_TYPE, getString(type));
		mDatabaseAdapter.open();
		long _id = mDatabaseAdapter.insert_to_database(_list);
		mDatabaseAdapter.close();
		return _id;
	}
	
	private void editDatabase(int type) {
		HashMap<String, String> _list = new HashMap<String, String>();
		_list.put(DatabaseAdapter.KEY_ID,mTempClickedList.get(0));
		_list.put(DatabaseAdapter.KEY_TYPE, getString(type));
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
	}
}