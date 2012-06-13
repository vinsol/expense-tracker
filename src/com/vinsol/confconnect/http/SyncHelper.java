package com.vinsol.confconnect.http;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.models.Data;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.Sync;
import com.vinsol.expensetracker.utils.Log;

public class SyncHelper extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	private DatabaseAdapter adapter;
	private HTTP http;
	private ConvertCursorToListString convertCursorToListString;
	private Gson gson;
	
	public SyncHelper(Context context) {
		this.context = context;
		convertCursorToListString = new ConvertCursorToListString(context);
		adapter = new DatabaseAdapter(context);
		http = new HTTP(context);
		gson = new MyGson().get();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("************************** Starting Sync **********************************");
		try {
			pull();
			push();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void push() throws IOException {
		Log.d("****************** Pushing Data ****************");
		create();
		update();
		delete();
		Log.d("****************** Data Pushed ****************");
	}

	//Push request to delete records
	private void delete() {
		
	}

	//Push request to update records
	private void update() {
		
	}

	//Push Request to create new records which are not synced
	private void create() {
		String data = gson.toJson(convertCursorToListString.getEntryListNotSyncedAndCreated());
		Log.d(data +" size "+convertCursorToListString.getEntryListNotSyncedAndCreated().size());
		try {
			String fetchedData = http.addMultipleExpenses(data);
			Data response = gson.fromJson(fetchedData,Data.class);
			updateExpenses(response.expenses);
			Log.d(fetchedData + " en ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pull() throws IOException {
		Log.d("*********************** Getting SyncData **********************************");
		String fetchedSyncResponse = http.getSyncData(); 
		Log.d(" *************  "+ fetchedSyncResponse);
		Sync sync = new MyGson().get().fromJson(fetchedSyncResponse, Sync.class);
		SharedPreferencesHelper.setSyncTimeStamp(sync.timestamp);
		Log.d(" ******************** Started Adding Expenses To DB ****************************** ");
		Long startTimeInMilis = Calendar.getInstance().getTimeInMillis();
		addExpenses(sync.add.expenses);
		addFavorites(sync.add.favorites);
		updateExpenses(sync.update.expenses);
		updateFavorites(sync.update.favorites);
		deleteExpenses(sync.delete.expenses);
		deleteFavorites(sync.delete.favorites);
		Log.d(" ******************** Total Time Taken ****************************** "+(Calendar.getInstance().getTimeInMillis() - startTimeInMilis));
		Log.d(" ******************** Finished Adding Expenses To DB ****************************** ");
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
	}
	
	private void addExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			entry.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			if(!adapter.findEntryById(entry.id)) {
				adapter.insertToEntryTable(entry);
			} else {
				adapter.editEntryTable(entry);
			}
		}
		adapter.close();
	}
	
	private void updateExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			entry.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			adapter.editEntryTable(entry);
		}
		adapter.close();
	}
	
	private void addFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			favorite.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			adapter.insertToFavoriteTable(favorite);
		}
		adapter.close();
	}
	
	private void updateFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			favorite.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			adapter.editFavoriteTable(favorite);
		}
		adapter.close();
	}
	
	private void deleteFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			favorite.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			adapter.permanentDeleteFavoriteTableEntryID(favorite.favId);
		}
		adapter.close();
	}
	
	private void deleteExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			entry.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
			adapter.permanentDeleteEntryTableEntryID(entry.id);
		}
		adapter.close();
	}
	
}