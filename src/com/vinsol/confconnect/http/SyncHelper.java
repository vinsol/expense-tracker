package com.vinsol.confconnect.http;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.Sync;
import com.vinsol.expensetracker.utils.Log;

public class SyncHelper extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	private DatabaseAdapter adapter;
	private HTTP http;
	
	public SyncHelper(Context context) {
		this.context = context;
		adapter = new DatabaseAdapter(context);
		http = new HTTP(context);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("************************** Starting Sync **********************************");
		Log.d("*********************** Getting SyncData **********************************");
		try {
			String fetchedSyncResponse = http.getSyncData(); 
			Log.d(" *************  "+ fetchedSyncResponse);
			
			Sync sync = new MyGson().get().fromJson(fetchedSyncResponse, Sync.class);
			Log.d("************* Sync TimeStamp ********************* " + sync.timestamp);
			SharedPreferencesHelper.setSyncTimeStamp(sync.timestamp);
			Log.d(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_key_sync_timestamp), null) + " ****** ");
			String timestamp = SharedPreferencesHelper.getSharedPreferences().getString(context.getString(R.string.pref_key_sync_timestamp), null);
			Log.d("************** timestamp ****************** "+timestamp);
			Log.d(" ******************** Started Adding Expenses To DB ****************************** ");
			Long startTimeInMilis = Calendar.getInstance().getTimeInMillis();
			addExpenses(sync.add.expenses);
			updateExpenses(sync.update.expenses);
			addFavorites(sync.add.favorites);
			updateFavorites(sync.update.favorites);
			Log.d(" ******************** Total Time Taken ****************************** "+(Calendar.getInstance().getTimeInMillis() - startTimeInMilis));
			Log.d(" ******************** Finished Adding Expenses To DB ****************************** ");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
	}
	
	private void addExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			entry.syncBit = 1;
			adapter.insertToEntryTable(entry);
		}
		adapter.close();
	}
	
	private void updateExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			entry.syncBit = 1;
			adapter.editEntryTable(entry);
		}
		adapter.close();
	}
	
	private void addFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			favorite.syncBit = 1;
			adapter.insertToFavoriteTable(favorite);
		}
		adapter.close();
	}
	
	private void updateFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			favorite.syncBit = 1;
			adapter.editFavoriteTable(favorite);
		}
		adapter.close();
	}
	
}