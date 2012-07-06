/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
 */

package com.vinsol.expensetracker.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.utils.Log;

public class ConvertCursorToListString {

	private Context context;
	private DatabaseAdapter adapter;

	public ConvertCursorToListString(Context context) {
		this.context = context;
		adapter = new DatabaseAdapter(context);
	}

	public List<Favorite> getFavoriteList() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteTableComplete());
	}
	
	public List<Favorite> getFavoriteList(Cursor cursor) {
		List<Favorite> mainlist = new ArrayList<Favorite>();
		Favorite listFavorite;
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				listFavorite = new Favorite();
				listFavorite.amount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				listFavorite.id = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				listFavorite.description = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TAG));
				listFavorite.type = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE));
				listFavorite.location = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_LOCATION));
				listFavorite.myHash = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_MY_HASH));
				listFavorite.updatedAt = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_UPDATED_AT));
				listFavorite.deleted = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_DELETE_BIT))>0;
				listFavorite.idFromServer = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID_FROM_SERVER));
				listFavorite.fileUploaded = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_UPLOADED))>0;
				listFavorite.fileToDownload = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_TO_DOWNLOAD))>0;
				listFavorite.syncBit = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_SYNC_BIT));
				listFavorite.fileUpdatedAt = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_UPDATED_AT));
				
				if (listFavorite.description == null
						|| listFavorite.description.equals("")) {
					if (listFavorite.type.equals(context.getString(R.string.text))) {
						listFavorite.description = context.getString(R.string.finished_textentry);
					} else if (listFavorite.type.equals(context.getString(R.string.voice))) {
						listFavorite.description = context.getString(R.string.finished_voiceentry);
					} else if (listFavorite.type.equals(context.getString(R.string.camera))) {
						listFavorite.description = context.getString(R.string.finished_cameraentry);
					}
				}
				mainlist.add(listFavorite);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		cursor.close();
		adapter.close();
		
		return mainlist;
	}

	private String getValue(DisplayDate mDisplayDate, boolean isGraph,
			String id, int type) {
		if (isGraph == true) {
			return mDisplayDate.getDisplayDateHeaderGraph();
		} else {
			return mDisplayDate.getHeaderFooterListDisplayDate(type);
		}
	}

	public List<ListDatetimeAmount> getDateListString(boolean isAscending,
			boolean isGraph, String id, int type) {
		ListDatetimeAmount listDatetimeAmount = new ListDatetimeAmount();
		adapter.open();
		Cursor cursor;
		if (!isAscending) {
			cursor = getCursor(id, false);
		} else {
			cursor = getCursor(id, true);
		}
		StringProcessing mStringProcessing = new StringProcessing();
		List<ListDatetimeAmount> mainlist = new ArrayList<ListDatetimeAmount>();
		double temptotalAmount = 0;
		String totalAmountString = null;
		boolean isTempAmountNull = false;
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			Calendar mTempCalendar = Calendar.getInstance();
			mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
			mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			DisplayDate mDisplayDate = new DisplayDate(mTempCalendar);
			if (!mDisplayDate.isCurrentWeek()) {
				DisplayDate currentWeekDisplayDate = new DisplayDate(Calendar.getInstance());
				listDatetimeAmount.dateTime = getValue(currentWeekDisplayDate,isGraph, id, type);
				listDatetimeAmount.amount = "";
				mainlist.add(listDatetimeAmount);
				listDatetimeAmount = new ListDatetimeAmount();
			}
			do {
				mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				mDisplayDate = new DisplayDate(mTempCalendar);
				if (!mDisplayDate.isCurrentWeek()) {
					DisplayDate currentWeekDisplayDate = new DisplayDate(Calendar.getInstance());
					listDatetimeAmount.dateTime = getValue(currentWeekDisplayDate, isGraph, id, type);
				}
				listDatetimeAmount.dateTime = getValue(mDisplayDate, isGraph,id, type);
				String tempAmount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				if (tempAmount != null && !tempAmount.equals("")) {
					try {
						temptotalAmount += Double.parseDouble(tempAmount);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
					isTempAmountNull = true;
				}
				cursor.moveToNext();

				if (!cursor.isAfterLast()) {
					Calendar mTempSubCalendar = Calendar.getInstance();
					mTempSubCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
					mTempSubCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					DisplayDate mTempDisplayDate = new DisplayDate(mTempSubCalendar);
					if (!listDatetimeAmount.dateTime.equals(getValue(mTempDisplayDate, isGraph, id, type))) {
						totalAmountString = getTotalAmount(isTempAmountNull,temptotalAmount, totalAmountString);
						isTempAmountNull = false;
						totalAmountString = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						listDatetimeAmount.amount = totalAmountString;
						temptotalAmount = 0;
					}
				} else {
					cursor.moveToLast();
					totalAmountString = getTotalAmount(isTempAmountNull,temptotalAmount, totalAmountString);
					isTempAmountNull = false;
					totalAmountString = mStringProcessing.getStringDoubleDecimal(totalAmountString);
					listDatetimeAmount.amount = totalAmountString;
					cursor.moveToNext();
				}

				if (totalAmountString != null) {
					mainlist.add(listDatetimeAmount);
					listDatetimeAmount = new ListDatetimeAmount();
					totalAmountString = null;
				}
			} while (!cursor.isAfterLast());
		}
		cursor.close();
		adapter.close();
		return mainlist;
	}

	private String getTotalAmount(Boolean isTempAmountNull,
			double temptotalAmount, String totalAmountString) {
		if (isTempAmountNull) {
			if (temptotalAmount != 0) {
				totalAmountString = temptotalAmount + " ?";
			} else {
				totalAmountString = "?";
			}
		} else {
			totalAmountString = temptotalAmount + "";
		}
		return totalAmountString;
	}
	
	public List<Entry> getEntryListFileNotUploaded() {
		adapter.open();
		return getEntryList(adapter.getEntryDataFileNotUploaded());
	}
	
	public List<Favorite> getFavoriteListFileNotUploaded() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteDataFileNotUploaded());
	}

	public List<Entry> getEntryListNotSyncedAndCreated() {
		adapter.open();
		return getEntryList(adapter.getEntryDataNotSyncedAndCreated());
	}
	
	public List<Entry> getEntryListNotSyncedAndUpdated() {
		adapter.open();
		return getEntryList(adapter.getEntryDataNotSyncedAndUpdated());
	}
	
	public List<Favorite> getFavoriteListNotSyncedAndUpdated() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteDataNotSyncedAndUpdated());
	}

	public List<Entry> getEntryListNotSyncedAndDeleted() {
		adapter.open();
		return getEntryList(adapter.getEntryDataNotSyncedAndDeleted());
	}
	
	public List<Favorite> getFavoriteListNotSyncedAndDeleted() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteDataNotSyncedAndDeleted());
	}
	
	public List<Favorite> getFavoriteListNotSyncedAndCreated() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteDataNotSyncedAndCreated());
	}
	
	public List<Entry> getEntryList(Boolean isAscending, String id) {
		adapter.open();
		return getEntryList(getCursor(id, isAscending));
	}
	
	public List<Entry> getEntryListFilesToDownload() {
		adapter.open();
		return getEntryList(adapter.getEntryDataFileToDownload());
	}
	
	public List<Favorite> getFavoriteListFilesToDownload() {
		adapter.open();
		return getFavoriteList(adapter.getFavoriteDataFileToDownload());
	}
	
	public Favorite getSingleFavoriteByHash(String hash) {
		adapter.open();
		List<Favorite> favorites = getFavoriteList(adapter.getFavoriteByHash(hash));
		if(favorites.size() == 1) {
			return favorites.get(0);
		}
		return null;
	}
	
	public Entry getSingleEntryByHash(String hash) {
		adapter.open();
		List<Entry> entries = getEntryList(adapter.getEntryByHash(hash));
		if(entries.size() == 1) {
			return entries.get(0);
		}
		return null;
	}

	public List<Entry> getEntryList(Cursor cursor) {
		List<Entry> mainlist = new ArrayList<Entry>();
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				Entry mEntry = new Entry();
				mEntry.id = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				mEntry.amount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				mEntry.favorite = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_FAVORITE));
				mEntry.location = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_LOCATION));
				mEntry.description = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TAG));
				mEntry.type = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE));
				mEntry.timeInMillis = cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME));
				mEntry.myHash = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_MY_HASH));
				mEntry.idFromServer = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID_FROM_SERVER));
				mEntry.updatedAt = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_UPDATED_AT));
				mEntry.deleted = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_DELETE_BIT))>0;
				mEntry.fileUploaded = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_UPLOADED))>0;
				mEntry.fileToDownload = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_TO_DOWNLOAD))>0;
				mEntry.syncBit = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_SYNC_BIT));
				mEntry.fileUpdatedAt = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_FILE_UPDATED_AT));
				mainlist.add(mEntry);
				Log.d("***  "+mEntry.deleted);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		cursor.close();
		adapter.close();
		return mainlist;
	}

	private Cursor getCursor(String id, boolean isAscending) {
		if (isAscending) {
			if (id == null || id.equals("")) {
				return adapter.getEntryTableDateDatabaseAscending();
			} else {
				return adapter.getEntryTableDateDatabaseAscending(id);
			}
		}
		if (id == null || id.equals("")) {
			return adapter.getEntryTableDateDatabaseDescending();
		} else {
			return adapter.getEntryTableDateDatabaseDescending(id);
		}
	}

}