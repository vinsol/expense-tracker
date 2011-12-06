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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vinsol.android.graph.BarGraph;
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
	
	/** Called when the activity is first created. */
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
		
		// temp view of graph
		// ******start view******//
		Double[] values = new Double[] { 400.0,100.5, 56.0, 140.0 ,
			 78.0,89.0,72.0 };
			 String[] horlabels = new String[] { "mon", "tue", "wed",
			 "thu","fri","sat","sun" };
		LinearLayout main_graph = (LinearLayout) findViewById(R.id.main_graph);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.FILL_PARENT,
		main_graph.getBackground().getIntrinsicHeight()
		);
		
		BarGraph barGraph = new BarGraph(this,values,horlabels,"Nov 21 2011");
		main_graph.addView(barGraph, params);
		
		
		 
		// ******stop view******//

//		 DatabaseAdapter adapter =new DatabaseAdapter(this);
//		 adapter.open();
//		 adapter.drop_table();
//		 adapter.close();
		//

		// /////// ********* DatabaseAdaptor initialize ********* ////////
		mDatabaseAdapter = new DatabaseAdapter(this);

		// //// ********* Adding Click Listeners to MainActivity **********
		// /////////

		// opens text entry Activity
		Button main_text = (Button) findViewById(R.id.main_text);
		main_text.setOnClickListener(this);

		// opens voice Activity
		Button main_voice = (Button) findViewById(R.id.main_voice);
		main_voice.setOnClickListener(this);

		// opens Camera Activity
		Button main_camera = (Button) findViewById(R.id.main_camera);
		main_camera.setOnClickListener(this);

		// opens Favorite Activity
		Button main_favorite = (Button) findViewById(R.id.main_favorite);
		main_favorite.setOnClickListener(this);

		// opens Save Reminder Activity
		Button main_save_reminder = (Button) findViewById(R.id.main_save_reminder);
		main_save_reminder.setOnClickListener(this);

		// opens ListView
		ImageView main_listview = (ImageView) findViewById(R.id.main_listview);
		main_listview.setOnClickListener(this);

		// /////// ******** Finished Adding Click Listeners *********
		// ///////////

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
		new HandleGraph(this).execute();
		super.onResume();
	}

	@Override
	public void onClick(View v) {

		// //// ******* opens TextEntry Activity ******** ///////////
			if (v.getId() == R.id.main_text) {
				Intent intentTextEntry = new Intent(this, TextEntry.class);
				if(_id == null ){
					_id = insertToDatabase(R.string.text);
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
					Log.v("_id", _id+" 56");
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					editDatabase(R.string.text);
				}
				intentTextEntry.putExtra("textEntryBundle", bundle);
				startActivity(intentTextEntry);
			}
			
			// //// ******* opens Voice Activity ******** ///////////
			else if (v.getId() == R.id.main_voice) {
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
			}

			// //// ******* opens Camera Activity ******** ///////////
			else if (v.getId() == R.id.main_camera) {
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
			}
			
			// //// ******* opens Favorite Activity ******** ///////////
			else if (v.getId() == R.id.main_favorite) {
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
			}

			// //// ******* opens List Activity and adds unknown entry to database
			// ******** ///////////
			else if (v.getId() == R.id.main_save_reminder) {
				if(_id == null)
					insertToDatabase(R.string.unknown);
				Intent intentListView = new Intent(this, ExpenseListing.class);
				startActivity(intentListView);
			}
			
			// //// ******* opens ListView Activity ******** ///////////
			else if (v.getId() == R.id.main_listview) {
				Intent intentListView = new Intent(this, ExpenseListing.class);
				startActivity(intentListView);
			}
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

	@Override
	protected void onPause() {
		if(getIntent().hasExtra("mainBundle")){
			Bundle tempBundle = getIntent().getBundleExtra("mainBundle");
			if (!tempBundle.isEmpty()) {
				if(tempBundle.containsKey("mDisplayList")){
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

}