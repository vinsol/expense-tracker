/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.GraphHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.listing.FavoriteActivity;
import com.vinsol.expensetracker.models.Entry;

public class Home extends Activity implements OnClickListener {
	
	private long timeInMillis = 0;
	private Bundle bundle;
	private Entry entry;
	private GraphHelper mHandleGraph;
	private ProgressBar graphProgressBar;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		bundle = new Bundle();
		entry = new Entry();

		////// ********* Adding Click Listeners to MainActivity ********** /////////
		
		//opens text entry Activity
		((Button) findViewById(R.id.main_text)).setOnClickListener(this);

		// opens voice Activity
		((Button) findViewById(R.id.main_voice)).setOnClickListener(this);

		// opens Camera Activity
		((Button) findViewById(R.id.main_camera)).setOnClickListener(this);

		// opens Favorite Activity
		((Button) findViewById(R.id.main_favorite)).setOnClickListener(this);

		// opens Save Reminder Activity
		((Button) findViewById(R.id.main_save_reminder)).setOnClickListener(this);

		// opens ListView
		((ImageView) findViewById(R.id.main_listview)).setOnClickListener(this);

		graphProgressBar = (ProgressBar) findViewById(R.id.graph_progress_bar);
		graphProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		//finding current location
		LocationHelper mLocationHelper = new LocationHelper();
		Location location = mLocationHelper.getBestAvailableLocation();
		if(location == null) {
			mLocationHelper.requestLocationUpdate();
		}
		
		mHandleGraph = new GraphHelper(this,graphProgressBar);
		mHandleGraph.execute();
		super.onResume();
	}
	
	@Override
	public void onClick(View clickedView) {
		int idOfClickedView = clickedView.getId();
		if(mHandleGraph != null)
			mHandleGraph.cancel(true);
		switch (idOfClickedView) {
			// //// ******* opens TextEntry Activity ******** ///////////
			case R.id.main_text:
				FlurryAgent.onEvent(getString(R.string.finished_textentry));
				Intent intentTextEntry = new Intent(this, TextEntry.class);
				createDatabaseEntry(R.string.text);
				intentTextEntry.putExtras(bundle);
				startActivity(intentTextEntry);
				break;
				
			// //// ******* opens Voice Activity ******** ///////////
			case R.id.main_voice:
				FlurryAgent.onEvent(getString(R.string.finished_voiceentry));
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentVoice = new Intent(this, Voice.class);
					createDatabaseEntry(R.string.voice);
					intentVoice.putExtras(bundle);
					startActivity(intentVoice);
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
	
			// //// ******* opens Camera Activity ******** ///////////
			case R.id.main_camera:
				FlurryAgent.onEvent(getString(R.string.finished_cameraentry));
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentCamera = new Intent(this, CameraActivity.class);
					bundle = new Bundle();
					intentCamera.putExtras(bundle);
					startActivity(intentCamera);
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			// //// ******* opens Favorite Activity ******** ///////////
			case R.id.main_favorite:
				FlurryAgent.onEvent(getString(R.string.favorite));
				Intent intentFavorite = new Intent(this, FavoriteActivity.class);
				bundle = new Bundle();
				intentFavorite.putExtras(bundle);
				startActivity(intentFavorite);	
				break;
				
			// //// ******* opens List Activity and adds unknown entry to database ******** ///////////
			case R.id.main_save_reminder:
				FlurryAgent.onEvent(getString(R.string.save_reminder));
				insertToDatabase(R.string.unknown);
				Intent intentListView = new Intent(this, ExpenseListing.class);
				startActivity(intentListView);
				break;
			
			////// ******* opens ListView Activity ******** ///////////
			case R.id.main_listview:
				FlurryAgent.onEvent(getString(R.string.list_view));
				Intent intentListView2 = new Intent(this, ExpenseListing.class);
				startActivity(intentListView2);
				break;
		}//end switch
	}//end onClick
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_optionsmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			Intent intent = new Intent(this, SetPreferences.class);
            startActivity(intent);
			break;

		case R.id.rate_app:
			Intent startMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.EXPENSE_TRACKER_MARKET_URI));
			startActivity(startMarket);
			break;
			
		case R.id.manage_favorite:
			Intent startManagingFavorite = new Intent(this, FavoriteActivity.class);
			startManagingFavorite.putExtra(Constants.KEY_MANAGE_FAVORITE, true);
			startActivity(startManagingFavorite);
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void createDatabaseEntry(int typeOfEntry) {
		entry.id = insertToDatabase(typeOfEntry).toString();
		bundle.putLong(Constants.KEY_ID, Long.parseLong(entry.id));
		
		if(LocationHelper.currentAddress != null && !LocationHelper.currentAddress.trim().equals("")) {
			bundle.putBoolean(Constants.KEY_SET_LOCATION, false);
		} else {
			bundle.putBoolean(Constants.KEY_SET_LOCATION, true);
		}
	}

	///////// ******** function to mark entry into the database and returns the id of the new entry ***** //////
	private Long insertToDatabase(int type) {
		Entry list = new Entry();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (timeInMillis == 0)
			list.timeInMillis = mCalendar.getTimeInMillis();
		else {
			bundle.putLong(Constants.KEY_TIME_IN_MILLIS, timeInMillis);
			list.timeInMillis = timeInMillis;
			finish();
		}

		if (LocationHelper.currentAddress != null && !LocationHelper.currentAddress.trim().equals("")) {
			list.location = LocationHelper.currentAddress;
		}
		list.type = getString(type);
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter.open();
		long id = mDatabaseAdapter.insertToEntryTable(list);
		mDatabaseAdapter.close();
		return id;
	}
	
	@Override
	protected void onPause() {
		if(mHandleGraph != null && !mHandleGraph.isCancelled()) {
			mHandleGraph.cancel(true);
		}
		super.onPause();
	}
	
}