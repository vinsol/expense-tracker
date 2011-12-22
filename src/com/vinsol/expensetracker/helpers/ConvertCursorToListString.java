package com.vinsol.expensetracker.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import android.content.Context;
import android.database.Cursor;

public class ConvertCursorToListString {
	Context context;

	public ConvertCursorToListString(Context context) {
		this.context = context;
	}

	public List<HashMap<String, String>> getFavoriteList() {
		List<HashMap<String, String>> mainlist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> list;
		DBAdapterFavorite mDbAdapterFavorite = new DBAdapterFavorite(context);
		mDbAdapterFavorite.open();
		Cursor cursor = mDbAdapterFavorite.getCompleteDatabase();
		if(cursor.getCount() >= 1){
			cursor.moveToFirst();
			do {
				list = new HashMap<String, String>();
				list.put(DBAdapterFavorite.KEY_AMOUNT, cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_AMOUNT)));
				list.put(DBAdapterFavorite.KEY_ID, cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_ID)));
				list.put(DBAdapterFavorite.KEY_TAG, cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_TAG)));
				list.put(DBAdapterFavorite.KEY_TYPE, cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_TYPE)));
				if (!list.isEmpty())
					mainlist.add(list);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		mDbAdapterFavorite.close();
		return mainlist;
	}
	
	private String getValue(DisplayDate mDisplayDate,boolean isGraph,String id){
		if(isGraph == true){
			return mDisplayDate.getDisplayDateHeaderGraph();
		} else {
			if(id == null || id.equals(""))
				return mDisplayDate.getDisplayDate();
			else 
				return mDisplayDate.getHeaderFooterListDisplayDate();
		}
	}
	
	public List<HashMap<String, String>> getDateListString(boolean isGraph,String id) {
		HashMap<String, String> list = new HashMap<String, String>();
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor;
		StringProcessing mStringProcessing = new StringProcessing();
		if(id == null || id.equals("")){
			cursor = adapter.getDateDatabase();
		} else {
			cursor = adapter.getDateDatabase(id);
		}
		List<HashMap<String, String>> mainlist = new ArrayList<HashMap<String, String>>();
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
				if (list.isEmpty()) {
					list.put(DatabaseAdapter.KEY_DATE_TIME,
					getValue(mDisplayDate, isGraph, id)); // /TODO
				}
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
					if (!list.get(DatabaseAdapter.KEY_DATE_TIME).equals(getValue(mTempDisplayDate, isGraph, id))) { // TODO
						totalAmountString = getTotalAmount(isTempAmountNull, temptotalAmount, totalAmountString);
						isTempAmountNull = false;
						totalAmountString = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
						temptotalAmount = 0;
					}
				} else {
					cursor.moveToLast();
					totalAmountString = getTotalAmount(isTempAmountNull, temptotalAmount, totalAmountString);
					isTempAmountNull = false;
					totalAmountString = mStringProcessing.getStringDoubleDecimal(totalAmountString);
					list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
					cursor.moveToNext();
				}

				if (!list.isEmpty() && totalAmountString != null) {
					mainlist.add(list);
					list = new HashMap<String, String>();
					totalAmountString = null;
				}
			} while (!cursor.isAfterLast());

		}
		adapter.close();
		return mainlist;
	}
		
	private String getTotalAmount(Boolean isTempAmountNull,double temptotalAmount,String totalAmountString){
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
	
	public List<HashMap<String, String>> getListStringParticularDate(String id) {
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor;
		if(id == null || id.equals("")){
			cursor = adapter.getDateDatabase();
		} else {
			cursor = adapter.getDateDatabase(id);
		}
		List<HashMap<String, String>> mainlist = new ArrayList<HashMap<String, String>>();
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				List<String> tempList = new ArrayList<String>();
				tempList.add(DatabaseAdapter.KEY_ID);
				tempList.add(DatabaseAdapter.KEY_AMOUNT);
				tempList.add(DatabaseAdapter.KEY_FAVORITE);
				tempList.add(DatabaseAdapter.KEY_LOCATION);
				tempList.add(DatabaseAdapter.KEY_TAG);
				tempList.add(DatabaseAdapter.KEY_TYPE);
				HashMap<String, String> list = getHashMap(tempList, cursor);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				calendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(calendar);
				list.put(DatabaseAdapter.KEY_DATE_TIME,mDisplayDate.getDisplayDate()); // TODO
				list.put(DatabaseAdapter.KEY_DATE_TIME + "Millis", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				if (!list.isEmpty())
					mainlist.add(list);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		adapter.close();
		return mainlist;
	}

	private HashMap<String, String> getHashMap(List<String> tempList,Cursor cursor) {
		HashMap<String, String> list = new HashMap<String, String>();
		try {
			for (int i = 0, j = tempList.size(); i < j; i++) {
				try {
					list.put(tempList.get(i), cursor.getString(cursor.getColumnIndex(tempList.get(i))));
				} catch (NullPointerException e) {
				}
			}
			return list;
		} catch (NullPointerException e) {
		}
		return null;
	}

}
