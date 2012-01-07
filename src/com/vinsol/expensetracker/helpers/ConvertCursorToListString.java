package com.vinsol.expensetracker.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.ListDatetimeAmount;

import android.content.Context;
import android.database.Cursor;

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
				mainlist.add(listFavorite);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		adapter.close();
		return mainlist;
	}
	
	private String getValue(DisplayDate mDisplayDate,boolean isGraph,String id) {
		if(isGraph == true) {
			return mDisplayDate.getDisplayDateHeaderGraph();
		} else {
			if(id == null || id.equals(""))
				return mDisplayDate.getHeaderFooterListDisplayDate();
			else 
				return mDisplayDate.getDisplayDate();
		}
	}
	
	public List<ListDatetimeAmount> getDateListString(boolean isGraph,String id) {
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
			do {
				Calendar mTempCalendar = Calendar.getInstance();
				mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mTempCalendar);
				listDatetimeAmount.dateTime = getValue(mDisplayDate, isGraph, id);
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
					if (!listDatetimeAmount.dateTime.equals(getValue(mTempDisplayDate, isGraph, id))) { 
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
	
	public List<Entry> getListStringParticularDate(String id) {
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
		adapter.close();
		return mainlist;
	}

}
