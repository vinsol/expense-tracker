package com.vinsol.expensetracker.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.models.ListDatetimeAmount;

import android.content.Context;
import android.database.Cursor;

public class ConvertCursorToListString {
	Context context;

	public ConvertCursorToListString(Context context) {
		this.context = context;
	}

	public List<Favorite> getFavoriteList() {
		List<Favorite> mainlist = new ArrayList<Favorite>();
		Favorite listFavorite;
		DBAdapterFavorite mDbAdapterFavorite = new DBAdapterFavorite(context);
		mDbAdapterFavorite.open();
		Cursor cursor = mDbAdapterFavorite.getCompleteDatabase();
		if(cursor.getCount() >= 1){
			cursor.moveToFirst();
			do {
				listFavorite = new Favorite();
				listFavorite.amount = cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_AMOUNT));
				listFavorite.userId = cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_ID));
				listFavorite.description = cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_TAG));
				listFavorite.type = cursor.getString(cursor.getColumnIndex(DBAdapterFavorite.KEY_TYPE));
				mainlist.add(listFavorite);
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
				return mDisplayDate.getHeaderFooterListDisplayDate();
			else 
				return mDisplayDate.getDisplayDate();
		}
	}
	
	public List<ListDatetimeAmount> getDateListString(boolean isGraph,String id) {
		ListDatetimeAmount listDatetimeAmount = new ListDatetimeAmount();
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor;
		StringProcessing mStringProcessing = new StringProcessing();
		if(id == null || id.equals("")){
			cursor = adapter.getDateDatabase();
		} else {
			cursor = adapter.getDateDatabase(id);
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
				listDatetimeAmount.dateTime = getValue(mDisplayDate, isGraph, id);// /TODO 
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
					if (!listDatetimeAmount.dateTime.equals(getValue(mTempDisplayDate, isGraph, id))) { // TODO
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
	
	public List<DisplayList> getListStringParticularDate(String id) {
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor;
		if(id == null || id.equals("")){
			cursor = adapter.getDateDatabase();
		} else {
			cursor = adapter.getDateDatabase(id);
		}
		List<DisplayList> mainlist = new ArrayList<DisplayList>();
		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			do {
				DisplayList mDisplayList = new DisplayList();
				mDisplayList.userId = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				mDisplayList.amount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				mDisplayList.favorite = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_FAVORITE));
				mDisplayList.location = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_LOCATION));
				mDisplayList.description = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TAG));
				mDisplayList.type = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE));
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				calendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(calendar);
				if(id == null || id.equals("")){
					mDisplayList.displayTime = mDisplayDate.getHeaderFooterListDisplayDate();
				} else {
					mDisplayList.displayTime = mDisplayDate.getDisplayDate();
				}
				mDisplayList.timeInMillis = cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME));
				mainlist.add(mDisplayList);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
		}
		adapter.close();
		return mainlist;
	}

}
