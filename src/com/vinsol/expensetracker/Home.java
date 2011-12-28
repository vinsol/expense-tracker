package com.vinsol.expensetracker;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vinsol.android.graph.HandleGraph;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.listing.FavoriteActivity;
import com.vinsol.expensetracker.models.Entry;

public class Home extends Activity implements OnClickListener {
	private DatabaseAdapter mDatabaseAdapter;
	private long timeInMillis = 0;
	private Bundle bundle;
	private Entry entry;
	private HandleGraph mHandleGraph;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		bundle = new Bundle();
		entry = new Entry();
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
		
		//finding current location
		Location location = LocationHelper.getBestAvailableLocation();
		if(location == null) {
			LocationHelper.requestLocationUpdate();
		}
		mHandleGraph = new HandleGraph(this);
		mHandleGraph.execute();
		super.onResume();
	}
	
	@Override
	public void onClick(View clickedView) {
		int idOfClickedView = clickedView.getId();
		switch (idOfClickedView) {
		// //// ******* opens TextEntry Activity ******** ///////////
		case R.id.main_text:
			Intent intentTextEntry = new Intent(this, TextEntry.class);
			createDatabaseEntry(R.string.text);
			intentTextEntry.putExtra("textEntryBundle", bundle);
			startActivity(intentTextEntry);
			break;
			
		// //// ******* opens Voice Activity ******** ///////////
		case R.id.main_voice:
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				Intent intentVoice = new Intent(this, Voice.class);
				createDatabaseEntry(R.string.voice);
				intentVoice.putExtra("voiceBundle", bundle);
				startActivity(intentVoice);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			break;

		// //// ******* opens Camera Activity ******** ///////////
		case R.id.main_camera:
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				Intent intentCamera = new Intent(this, CameraActivity.class);
				bundle = new Bundle();
				intentCamera.putExtra("cameraBundle", bundle);
				startActivity(intentCamera);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			break;
			
		// //// ******* opens Favorite Activity ******** ///////////
		case R.id.main_favorite:
			if(new ConvertCursorToListString(this).getFavoriteList().size() >=1){
				Intent intentFavorite = new Intent(this, FavoriteActivity.class);
				bundle = new Bundle();
				intentFavorite.putExtra("favoriteBundle", bundle);
				startActivity(intentFavorite);	
			}
			else {
				Toast.makeText(this, "no favorite added", Toast.LENGTH_SHORT).show();
			}
			break;
			
		// //// ******* opens List Activity and adds unknown entry to database ******** ///////////
		case R.id.main_save_reminder:
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
	}//end onClick
	
	private void createDatabaseEntry(int typeOfEntry) {
		entry.id = insertToDatabase(typeOfEntry).toString();
		bundle.putLong("_id", Long.parseLong(entry.id));
		
		if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			bundle.putBoolean("setLocation", false);
		} else {
			bundle.putBoolean("setLocation", true);
		}
	}

	// /////// ******** function to mark entry into the database and returns the
	// id of the new entry ***** //////
	private Long insertToDatabase(int type) {
		HashMap<String, String> list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (timeInMillis == 0)
			list.put(DatabaseAdapter.KEY_DATE_TIME,Long.toString(mCalendar.getTimeInMillis()));
		else {
			bundle.putLong("timeInMillis", timeInMillis);
			list.put(DatabaseAdapter.KEY_DATE_TIME,Long.toString(timeInMillis));
			finish();
		}

		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		list.put(DatabaseAdapter.KEY_TYPE, getString(type));
		mDatabaseAdapter.open();
		long id = mDatabaseAdapter.insertToDatabase(list);
		mDatabaseAdapter.close();
		return id;
	}
}