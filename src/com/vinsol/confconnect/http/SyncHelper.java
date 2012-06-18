package com.vinsol.confconnect.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.CameraFileSave;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.models.Data;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.Sync;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Strings;

public class SyncHelper extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	private DatabaseAdapter adapter;
	private HTTP http;
	private ConvertCursorToListString convertCursorToListString;
	private Gson gson;
	private FileHelper fileHelper;
	
	public SyncHelper(Context context) {
		this.context = context;
		convertCursorToListString = new ConvertCursorToListString(context);
		adapter = new DatabaseAdapter(context);
		http = new HTTP(context);
		gson = new MyGson().get();
		fileHelper = new FileHelper();
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
	
	private void pull() throws IOException {
		pullData();
		pullFiles();
	}

	private void pullData() throws IOException {
		Log.d("*********************** Getting SyncData **********************************");
		String fetchedSyncResponse = http.getSyncData(); 
		Log.d(" *************  "+ fetchedSyncResponse);
		Sync sync = gson.fromJson(fetchedSyncResponse, Sync.class);
		if(sync != null) {
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
	}
	
	private void pullFiles() {
		Log.d("*********************** Pulling Files **********************************");
		List<Entry> entries = convertCursorToListString.getEntryListFilesToDownload();
		for(Entry entry : entries) {
			if(Strings.equal(entry.type, context.getString(R.string.voice))) {
				try {
					if(http.downloadExpenseFile(entry.id, entry.idFromServer, true)) {
						entry.fileToDownload = false;
						entry.fileUploaded = true;
						updateEntry(entry);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(Strings.equal(entry.type, context.getString(R.string.camera))) {
				try {
					if(http.downloadExpenseFile(entry.id, entry.idFromServer, false)) {
						new CameraFileSave(context).resizeImageAndSaveThumbnails(entry.id, false);
						entry.fileToDownload = false;
						entry.fileUploaded = true;
						updateEntry(entry);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<Favorite> favorites = convertCursorToListString.getFavoriteListFilesToDownload();
		for(Favorite favorite : favorites) {
			if(Strings.equal(favorite.type, context.getString(R.string.voice))) {
				try {
					if(http.downloadFavoriteFile(favorite.favId, favorite.idFromServer, true)) {
						favorite.fileToDownload = false;
						favorite.fileUploaded = true;
						updateFavorite(favorite);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(Strings.equal(favorite.type, context.getString(R.string.camera))) {
				try {
					if(http.downloadFavoriteFile(favorite.favId, favorite.idFromServer, false)) {
						new CameraFileSave(context).resizeImageAndSaveThumbnails(favorite.favId, true);
						favorite.fileToDownload = false;
						favorite.fileUploaded = true;
						updateFavorite(favorite);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("*********************** Pulling Files **********************************");
	}
	
	private void updateEntry(Entry entry) {
		adapter.open();
		adapter.editEntryTable(entry);
		adapter.close();
	}
	
	private void updateFavorite(Favorite favorite) {
		adapter.open();
		adapter.editFavoriteTable(favorite);
		adapter.close();
	}
	
	private void push() throws IOException {
		pushData();
		pushFiles();
	}

	private void pushData() throws IOException {
		Log.d("****************** Pushing Data ****************");
		create();
		update();
		delete();
		Log.d("****************** Data Pushed ****************");
	}

	private void create() {
		createEntry();
		createFavorites();
	}
	
	//Push request to update records
	private void update() {
		updateEntry();
		updateFavorites();
	}
	
	private void updateEntry() {
		List<Entry> entries = convertCursorToListString.getEntryListNotSyncedAndUpdated();
		if(entries.size() > 0) {
			String data = gson.toJson(entries);
			Log.d(data +" size "+convertCursorToListString.getEntryListNotSyncedAndUpdated().size());
			if(data != null) {
				try {
					String fetchedData = http.updateMultipleExpenses(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						updateExpenses(response.expenses);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void updateFavorites() {
		List<Favorite> favorites = convertCursorToListString.getFavoriteListNotSyncedAndUpdated();
		if(favorites.size() > 0) {
			String data = gson.toJson(favorites);
			Log.d(data +" size "+convertCursorToListString.getFavoriteListNotSyncedAndUpdated().size());
			if(data != null) {
				try {
					String fetchedData = http.updateMultipleFavorites(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						updateFavorites(response.favorites);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//Push request to delete records
	private void delete() {
		deleteEntry();
		deleteFavorites();
	}
	
	private void deleteEntry() {
		List<Entry> entries = convertCursorToListString.getEntryListNotSyncedAndDeleted();
		if(entries.size() > 0) {
			String data = gson.toJson(entries);
			Log.d(data +" size "+convertCursorToListString.getEntryListNotSyncedAndDeleted().size());
			if(data != null) {
				try {
					String fetchedData = http.deleteMultipleExpenses(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						deleteExpenses(response.expenses);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void deleteFavorites() {
		List<Favorite> favorites = convertCursorToListString.getFavoriteListNotSyncedAndDeleted();
		if(favorites.size() > 0) {
			String data = gson.toJson(favorites);
			Log.d(data +" size "+convertCursorToListString.getFavoriteListNotSyncedAndDeleted().size());
			if(data != null) {
				try {
					String fetchedData = http.deleteMultipleFavorites(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						deleteFavorites(response.favorites);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//Push Request to create new records which are not synced
	private void createEntry() {
		List<Entry> entries = convertCursorToListString.getEntryListNotSyncedAndCreated();
		if(entries.size() > 0) {
			String data = gson.toJson(entries);
			Log.d(data +" size "+convertCursorToListString.getEntryListNotSyncedAndCreated().size());
			if(data != null) {
				try {
					String fetchedData = http.addMultipleExpenses(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						updateExpenses(response.expenses);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void createFavorites() {
		List<Favorite> favorites = convertCursorToListString.getFavoriteListNotSyncedAndCreated();
		if(favorites.size() > 0) {
			String data = gson.toJson(favorites);
			Log.d(data +" size "+convertCursorToListString.getFavoriteListNotSyncedAndCreated().size());
			if(data != null) {
				try {
					String fetchedData = http.addMultipleFavorites(data);
					if(fetchedData != null) {
						Data response = gson.fromJson(fetchedData,Data.class);
						updateFavorites(response.favorites);
						Log.d(fetchedData + " en ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void pushFiles() {
		uploadExpenseFiles();
		uploadFavoriteFiles();
	}

	private void uploadExpenseFiles() {
		List<Entry> entries = convertCursorToListString.getEntryListFileNotUploaded();
		List<Entry> entriesToUpdate = new ArrayList<Entry>();
		for(Entry entry : entries) {
			if(entry.syncBit != context.getResources().getInteger(R.integer.syncbit_synced)) { continue; }
			boolean isAudio;
			if(Strings.equal(entry.type, context.getString(R.string.voice))) {
				isAudio = true; 
			} else if(Strings.equal(entry.type, context.getString(R.string.camera))) {
				isAudio = false;
			} else {
				continue;
			}
			String response;
			try {
				if(!isAudio) {
					response = http.uploadExpenseFile(fileHelper.getCameraFileLargeEntry(entry.id), entry.idFromServer, isAudio);
				} else {
					response = http.uploadExpenseFile(fileHelper.getAudioFileEntry(entry.id), entry.idFromServer, isAudio);
				}
				Log.d("******************* Getting Response *******************");
				if(response != null) {
					Log.d(response);
					Entry responseEntry = gson.fromJson(response, Entry.class);
					entriesToUpdate.add(responseEntry);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateExpenses(entriesToUpdate);
	}

	private void uploadFavoriteFiles() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
	}
	
	private void addExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			setSyncBitAndFileDownloaded(entry);
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
			setSyncBitAndFileDownloaded(entry);
			adapter.editEntryTable(entry);
		}
		adapter.close();
	}
	
	private void addFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			setSyncBitAndFileDownloaded(favorite);
			adapter.insertToFavoriteTable(favorite);
		}
		adapter.close();
	}
	
	private void updateFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			setSyncBitAndFileDownloaded(favorite);
			adapter.editFavoriteTable(favorite);
		}
		adapter.close();
	}
	
	private void deleteFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			setSyncBit(favorite);
			if(Strings.equal(favorite.type, context.getString(R.string.voice)) || Strings.equal(favorite.type, context.getString(R.string.camera))) {
				fileHelper.deleteAllFavoriteFiles(favorite.favId);
			}
			adapter.permanentDeleteFavoriteTableEntryID(favorite.favId);
		}
		adapter.close();
	}
	
	private void deleteExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			setSyncBit(entry);
			if(Strings.equal(entry.type, context.getString(R.string.voice)) || Strings.equal(entry.type, context.getString(R.string.camera))) {
				fileHelper.deleteAllFavoriteFiles(entry.favId);
			}
			adapter.permanentDeleteEntryTableEntryID(entry.id);
		}
		adapter.close();
	}
	
	private void setSyncBitAndFileDownloaded(Entry entry) {
		setSyncBit(entry);
		setDownloaded(entry);
	}
	
	private void setSyncBitAndFileDownloaded(Favorite favorite) {
		setSyncBit(favorite);
		setDownloaded(favorite);
	}
	
	private void setDownloaded(Entry entry) {
		entry.fileToDownload = true;
	}
	
	private void setDownloaded(Favorite favorite) {
		favorite.fileToDownload = true;
	}
	
	private void setSyncBit(Entry entry) {
		entry.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
	}
	
	private void setSyncBit(Favorite favorite) {
		favorite.syncBit = context.getResources().getInteger(R.integer.syncbit_synced);
	}
	
}