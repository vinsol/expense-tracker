/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

import java.util.Calendar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.entry.CameraEntry;
import com.vinsol.expensetracker.entry.FavoriteEntry;
import com.vinsol.expensetracker.entry.Text;
import com.vinsol.expensetracker.entry.Voice;
import com.vinsol.expensetracker.expenselisting.ExpenseListing;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.GraphHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.helpers.UnfinishedEntryCount;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.Log;

public class Home extends BaseActivity implements OnClickListener {
	
	private Bundle bundle;
	private GraphHelper mHandleGraph;
	private ProgressBar graphProgressBar;
	private UnfinishedEntryCount unfinishedEntryCount;
	private ConvertCursorToListString mConvertCursorToListString;
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
		FlurryAgent.onEvent(getString(R.string.home_screen));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		bundle = new Bundle();
		mConvertCursorToListString = new ConvertCursorToListString(this);
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
		
		ImageView mainGenerateReport = (ImageView) findViewById(R.id.main_generate_report);
		mainGenerateReport.setVisibility(View.VISIBLE);
		mainGenerateReport.setOnClickListener(this);
		
		graphProgressBar = (ProgressBar) findViewById(R.id.graph_progress_bar);
		graphProgressBar.setVisibility(View.VISIBLE);
		
		if(ExpenseTrackerApplication.toSync) {
			SyncHelper.syncHelper = new SyncHelper(this);
        	SyncHelper.syncHelper.execute();
		}
		Log.d("******************************* Syncing syncing syncing **************************"+ExpenseTrackerApplication.toSync+" token "+SharedPreferencesHelper.getSharedPreferences().getString(getString(R.string.pref_key_token), ""+" key "+getString(R.string.pref_key_token)));
		Log.d("******************************* Syncing syncing syncing **************************"+
				ExpenseTrackerApplication.toSync+" token "+
				PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_key_token), "")
				+" key "+getString(R.string.pref_key_token));
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
		unfinishedEntryCount = new UnfinishedEntryCount(mConvertCursorToListString.getEntryList(false,""),null,null,null,((TextView)findViewById(R.id.home_unfinished_entry_count)));
		unfinishedEntryCount.execute();
		mHandleGraph.execute();
		super.onResume();
	}
	
	@Override
	public void onClick(View clickedView) {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(!ExpenseTrackerApplication.isInitialized){ExpenseTrackerApplication.Initialize();}
		}
		int idOfClickedView = clickedView.getId();
		cancelHandleGraphTask();
		cancelUnfinishedEntryTask();
		switch (idOfClickedView) {
			////// ******* opens TextEntry Activity ******** ///////////
			case R.id.main_text:
				Intent intentTextEntry = new Intent(this, Text.class);
				createDatabaseEntry(R.string.text);
				intentTextEntry.putExtras(bundle);
				startActivity(intentTextEntry);
				break;
				
			////// ******* opens Voice Activity ******** ///////////
			case R.id.main_voice:
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
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentCamera = new Intent(this, CameraEntry.class);
					bundle = new Bundle();
					intentCamera.putExtras(bundle);
					startActivity(intentCamera);
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			// //// ******* opens Favorite Activity ******** ///////////
			case R.id.main_favorite:
				Intent intentFavorite = new Intent(this, FavoriteEntry.class);
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
				SyncHelper.startSync();
				break;
			
			////// ******* opens ListView Activity ******** ///////////
			case R.id.main_listview:
				Intent intentListView2 = new Intent(this, ExpenseListing.class);
				startActivity(intentListView2);
				break;
				
			case R.id.main_generate_report:
				startGenerateReportActivity();
				break;
		}//end switch
	}//end onClick
	
	private void createDatabaseEntry(int typeOfEntry) {
		bundle.putLong(Constants.KEY_ID, Long.parseLong(insertToDatabase(typeOfEntry).toString()));
		
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
		
		list.timeInMillis = mCalendar.getTimeInMillis();

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
		cancelHandleGraphTask();
		cancelUnfinishedEntryTask();
		super.onPause();
	}
	
	private void cancelUnfinishedEntryTask() {
		if(unfinishedEntryCount != null && !unfinishedEntryCount.isCancelled()) {
			unfinishedEntryCount.cancel(true);
		}
	}
	
	private void cancelHandleGraphTask() {
		if(mHandleGraph != null && !mHandleGraph.isCancelled()) {
			mHandleGraph.cancel(true);
		}
	}
	
}