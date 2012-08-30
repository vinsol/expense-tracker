package com.vinsol.expensetracker.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.confconnect.http.HTTP;
import com.vinsol.expensetracker.ExpenseTrackerApplication;
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
	
	private static Context context;
	private DatabaseAdapter adapter;
	private HTTP http;
	private ConvertCursorToListString convertCursorToListString;
	private Gson gson;
	private FileHelper fileHelper;
	public static AsyncTask<Void, Void, Void> syncHelper;
	public static boolean toConinue = false;
	
	public SyncHelper(Context context) {
		SyncHelper.context = context;
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
			addExpenses(sync.add.expenses,true);
			addFavorites(sync.add.favorites,true);
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
			Log.d("***** entry id "+entry.id+"  "+entry.idFromServer);
			if(Strings.equal(entry.type, context.getString(R.string.voice))) {
				Log.d("***** entry id "+entry.id+"  "+entry.idFromServer);
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
					if(http.downloadFavoriteFile(favorite.id, favorite.idFromServer, true)) {
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
					if(http.downloadFavoriteFile(favorite.id, favorite.idFromServer, false)) {
						new CameraFileSave(context).resizeImageAndSaveThumbnails(favorite.id, true);
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
		adapter.editExpenseEntryById(entry);
		adapter.close();
	}
	
	private void updateFavorite(Favorite favorite) {
		adapter.open();
		adapter.editFavoriteEntryById(favorite);
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
		
		Log.d("************************* Deleting ***************************");
		
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
					Log.d(fetchedData + " en ");
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
		boolean toUpdate = false;
		for(Entry entry : entries) {
			if(!Strings.equal(entry.syncBit, context.getString(R.string.syncbit_synced))) { continue; }
			toUpdate = true;
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
		if(toUpdate) updateExpenses(entriesToUpdate);
	}

	private void uploadFavoriteFiles() {
		List<Favorite> favorites = convertCursorToListString.getFavoriteListFileNotUploaded();
		List<Favorite> favoritesToUpdate = new ArrayList<Favorite>();
		boolean toUpdate = false;
		for(Favorite favorite : favorites) {
			if(!Strings.equal(favorite.syncBit, context.getString(R.string.syncbit_synced))) { continue; }
			toUpdate = true;
			boolean isAudio;
			if(Strings.equal(favorite.type, context.getString(R.string.voice))) {
				isAudio = true; 
			} else if(Strings.equal(favorite.type, context.getString(R.string.camera))) {
				isAudio = false;
			} else {
				continue;
			}
			String response;
			try {
				if(!isAudio) {
					response = http.uploadFavoriteFile(fileHelper.getCameraFileLargeFavorite(favorite.id), favorite.idFromServer, isAudio);
				} else {
					response = http.uploadFavoriteFile(fileHelper.getAudioFileFavorite(favorite.id), favorite.idFromServer, isAudio);
				}
				Log.d("******************* Getting Response *******************");
				if(response != null) {
					Log.d(response);
					Favorite responseFavorite = gson.fromJson(response, Favorite.class);
					favoritesToUpdate.add(responseFavorite);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(toUpdate) updateFavorites(favoritesToUpdate);
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
		if(toConinue) {
			toConinue = false;
			SyncHelper.syncHelper = new SyncHelper(context).execute();
		}
	}
	
	private void addExpenses(List<Entry> entries,boolean isPull) {
		adapter.open();
		for(Entry entry : entries) {
//			if(isPull) {
//				setSyncBitAndFileDownloaded(entry);
//			} else {
//				setSyncBit(entry);
//			}
			setSyncBit(entry);
			Log.d("*************************"+"fileToDOwnload "+entry.fileToDownload);
			
			String tempId = adapter.getEntryIdByHash(entry.myHash);
			if(Strings.isEmpty(tempId)) {
				adapter.insertToEntryTable(entry);
			} else {
				entry.id = tempId;
				adapter.editExpenseEntryById(entry);
			}
		}
		adapter.close();
	}
	
	private void addFavorites(List<Favorite> favorites, boolean isPull) {
		adapter.open();
		for(Favorite favorite : favorites) {
//			if(isPull) {
//				setSyncBitAndFileDownloaded(favorite);
//			} else {
//				setSyncBit(favorite);
//			}
			setSyncBit(favorite);
			String tempId = adapter.getFavIdByHash(favorite.myHash);
			if(Strings.isEmpty(tempId)) {
				adapter.insertToFavoriteTable(favorite);
			} else {
				favorite.id = tempId;
				adapter.editFavoriteEntryByHash(favorite);
			}
		}
		adapter.close();
	}
	
	private void updateExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			Entry tempEntry = convertCursorToListString.getSingleEntryByHash(entry.myHash);
			boolean toFetchFile = true;
			if(tempEntry != null && Strings.equal(entry.fileUpdatedAt, tempEntry.fileUpdatedAt)) {
				toFetchFile = false;
			}
			if(toFetchFile) {
				setSyncBitAndFileDownloaded(entry);
			} else {
				setSyncBit(entry);
			}
			setSyncBit(entry);
			entry.id = tempEntry.id;
			entry.favorite = adapter.getFavIdByHash(entry.favorite);
			adapter.editExpenseEntryByHash(entry);
		}
		adapter.close();
	}
	
	private void updateFavorites(List<Favorite> favorites) {
		Log.d("*****************  Check ");
		Log.d("favorite "+adapter+"  "+favorites);
		adapter.open();
		for(Favorite favorite : favorites) {
			Favorite tempFavorite = convertCursorToListString.getSingleFavoriteByHash(favorite.myHash);
			boolean toFetchFile = true;
			if(tempFavorite != null && Strings.equal(favorite.fileUpdatedAt, tempFavorite.fileUpdatedAt)) {
				toFetchFile = false;
			}
			if(toFetchFile) {
				setSyncBitAndFileDownloaded(favorite);
			} else {
				setSyncBit(favorite);
			}
			setSyncBit(favorite);
			favorite.id = tempFavorite.id;
			adapter.editFavoriteEntryByHash(favorite);
		}
		adapter.close();
	}
	
	private void deleteFavorites(List<Favorite> favorites) {
		adapter.open();
		for(Favorite favorite : favorites) {
			setSyncBit(favorite);
//			favorite.id = adapter.getFavIdByHash(favorite.myHash);
//			if(Strings.equal(favorite.type, context.getString(R.string.voice)) || Strings.equal(favorite.type, context.getString(R.string.camera))) {
//				fileHelper.deleteAllFavoriteFiles(favorite.id);
//			}
//			adapter.permanentDeleteFavoriteTableEntryID(favorite.id);
		}
		adapter.close();
	}
	
	private void deleteExpenses(List<Entry> entries) {
		adapter.open();
		for(Entry entry : entries) {
			setSyncBit(entry);
//			entry.id = adapter.getEntryIdByHash(entry.myHash);
//			if(Strings.equal(entry.type, context.getString(R.string.voice)) || Strings.equal(entry.type, context.getString(R.string.camera))) {
//				fileHelper.deleteAllEntryFiles(entry.id);
//			}
//			adapter.permanentDeleteEntryTableEntryID(entry.id);
		}
		adapter.close();
	}
	
	private void setSyncBitAndFileDownloaded(Entry entry) {
		setSyncBit(entry);
		setDownloaded(entry);
		setUploaded(entry);
	}
	
	private void setSyncBitAndFileDownloaded(Favorite favorite) {
		setSyncBit(favorite);
		setDownloaded(favorite);
		setUploaded(favorite);
	}
	
	private void setDownloaded(Entry entry) {
//		entry.fileToDownload = true;
	}
	
	private void setDownloaded(Favorite favorite) {
//		favorite.fileToDownload = true;
	}
	
	private void setUploaded(Entry entry) {
		entry.fileUploaded = true;
	}
	
	private void setUploaded(Favorite favorite) {
		favorite.fileUploaded = true;
	}
	
	private void setSyncBit(Entry entry) {
		entry.syncBit = context.getString(R.string.syncbit_synced);
	}
	
	private void setSyncBit(Favorite favorite) {
		favorite.syncBit = context.getString(R.string.syncbit_synced);
	}
	
	public static void startSync() {
		if(ExpenseTrackerApplication.toSync) {
			if(SyncHelper.syncHelper.getStatus().equals(AsyncTask.Status.RUNNING)) {
				toConinue = true;
			} else {
				toConinue = false;
				SyncHelper.syncHelper = new SyncHelper(context).execute();
			}
		}
	}
	
}