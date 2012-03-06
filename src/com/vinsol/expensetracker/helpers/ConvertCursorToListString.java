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

public class ConvertCursorToListString {
	Context context;

	DatabaseAdapter adapter ;
	public ConvertCursorToListString(Context context) {
		this.context = context;
		adapter = new DatabaseAdapter(context);
	}

	public List<Favorite> getFavoriteList() {
		List<Favorite> mainlist = new ArrayList<Favorite>();
		Favorite listFavorite;
		adapter.open();
		Cursor cursor = adapter.getFavoriteTableComplete();
		if(cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				listFavorite = new Favorite();
				listFavorite.amount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				listFavorite.favId = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				listFavorite.description = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TAG));
				listFavorite.type = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE));
				listFavorite.location = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_LOCATION));
				if(listFavorite.description == null || listFavorite.description.equals("")) {
					if(listFavorite.type.equals(context.getString(R.string.text))) {
						listFavorite.description = context.getString(R.string.finished_textentry);
					} else if(listFavorite.type.equals(context.getString(R.string.voice))) {
						listFavorite.description = context.getString(R.string.finished_voiceentry);
					} else if(listFavorite.type.equals(context.getString(R.string.camera))) {
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
	
	private String getValue(DisplayDate mDisplayDate,boolean isGraph,String id,int type) {
		if(isGraph == true) {
			return mDisplayDate.getDisplayDateHeaderGraph();
		} else {
			return mDisplayDate.getHeaderFooterListDisplayDate(type);
		}
	}
	
	public List<ListDatetimeAmount> getDateListString(boolean isGraph,String id,int type) {
		ListDatetimeAmount listDatetimeAmount = new ListDatetimeAmount();
		adapter.open();
		Cursor cursor;
		StringProcessing mStringProcessing = new StringProcessing();
		if(id == null || id.equals("")) {
			cursor = adapter.getEntryTableDateDatabase();
		} else {
			cursor = adapter.getEntryTableDateDatabase(id);
		}
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
			if(!mDisplayDate.isCurrentWeek()) {
				DisplayDate currentWeekDisplayDate = new DisplayDate(Calendar.getInstance());
				listDatetimeAmount.dateTime = getValue(currentWeekDisplayDate, isGraph, id, type);
				listDatetimeAmount.amount = "";
				mainlist.add(listDatetimeAmount);
				listDatetimeAmount = new ListDatetimeAmount();
			}
			do {
				mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				mDisplayDate = new DisplayDate(mTempCalendar);
				if(!mDisplayDate.isCurrentWeek()) {
					DisplayDate currentWeekDisplayDate = new DisplayDate(Calendar.getInstance());
					listDatetimeAmount.dateTime = getValue(currentWeekDisplayDate, isGraph, id, type);
				}
				listDatetimeAmount.dateTime = getValue(mDisplayDate, isGraph, id, type);
				String tempAmount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				if (tempAmount != null && !tempAmount.equals("")) {
					try {
						temptotalAmount += Double.parseDouble(tempAmount);
					} catch (NumberFormatException e) {
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
						totalAmountString = getTotalAmount(isTempAmountNull, temptotalAmount, totalAmountString);
						isTempAmountNull = false;
						totalAmountString = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						listDatetimeAmount.amount = totalAmountString;
						temptotalAmount = 0;
					}
				} else {
					cursor.moveToLast();
					totalAmountString = getTotalAmount(isTempAmountNull, temptotalAmount, totalAmountString);
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
		
	private String getTotalAmount(Boolean isTempAmountNull,double temptotalAmount,String totalAmountString) {
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
	
	public List<Entry> getEntryList(String id) {
		adapter.open();
		Cursor cursor;
		if(id == null || id.equals("")) {
			cursor = adapter.getEntryTableDateDatabase();
		} else {
			cursor = adapter.getEntryTableDateDatabase(id);
		}
		List<Entry> mainlist = new ArrayList<Entry>();
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				Entry mEntry = new Entry();
				mEntry.id = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				mEntry.amount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				mEntry.favId = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_FAVORITE));
				mEntry.location = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_LOCATION));
				mEntry.description = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TAG));
				mEntry.type = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE));
				mEntry.timeInMillis = cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME));
				mainlist.add(mEntry);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		cursor.close();
		adapter.close();
		return mainlist;
	}

}
