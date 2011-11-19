package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.utils.DisplayDate;
import android.content.Context;
import android.database.Cursor;

public class ConvertCursorToListString {
	Context context;
	public ConvertCursorToListString(Context _context) {
		context=_context;
	}
	
	List<HashMap<String, String>> getListStringPArticularDate(){
		DatabaseAdapter adapter=new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor= adapter.getCompleteDatabase();
		List<HashMap<String, String>> mainlist=new ArrayList<HashMap<String, String>>();
		cursor.moveToFirst();
		do{
			List<String> tempList = new ArrayList<String>();
			tempList.add(DatabaseAdapter.KEY_ID);
			tempList.add(DatabaseAdapter.KEY_AMOUNT);
			tempList.add(DatabaseAdapter.KEY_DATE_TIME);
			tempList.add(DatabaseAdapter.KEY_FAVORITE);
			tempList.add(DatabaseAdapter.KEY_LOCATION);
			tempList.add(DatabaseAdapter.KEY_TAG);
			tempList.add(DatabaseAdapter.KEY_TYPE);
			HashMap<String, String> list=getHashMap(tempList,cursor);
			if(!list.isEmpty())
				mainlist.add(list);
			cursor.moveToNext();
		}
		while(!cursor.isAfterLast());
		adapter.close();
		return mainlist;
	}

	List<HashMap<String, String>> getDateListString(){
		HashMap<String, String> list = new HashMap<String, String>();
		DatabaseAdapter adapter=new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor= adapter.getDateDatabase();
		List<HashMap<String, String>> mainlist=new ArrayList<HashMap<String, String>>();
		long temptotalAmount = 0;
		String totalAmountString = null;
		boolean isTempAmountNull = false;
		cursor.moveToFirst();
		do {
			
			Calendar mTempCalendar = Calendar.getInstance();
			mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
			
			DisplayDate mDisplayDate = new DisplayDate(mTempCalendar);
			if(list.isEmpty()){
				list.put(DatabaseAdapter.KEY_DATE_TIME, mDisplayDate.getDisplayDate());
			}
			String tempAmount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
			if(tempAmount != null){
				temptotalAmount += Long.parseLong(tempAmount);  
			} else {
				tempAmount = "";
				isTempAmountNull = true;
			}
			
			cursor.moveToNext();
			
			if(!cursor.isAfterLast()) {
				Calendar mTempSubCalendar = Calendar.getInstance();
				mTempSubCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				DisplayDate mTempDisplayDate = new DisplayDate(mTempSubCalendar);
				if(!list.get(DatabaseAdapter.KEY_DATE_TIME).equals(mTempDisplayDate.getDisplayDate())){
					if(isTempAmountNull) {
						if(temptotalAmount != 0) {
							totalAmountString = temptotalAmount+" ?";
						}
						else {
							totalAmountString = "?";
						}
					} else {
						totalAmountString = temptotalAmount+"";
					}
					isTempAmountNull = false;
					list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
				} 
			}
			 else {
				 cursor.moveToLast();
				 if(isTempAmountNull) {
					 if(temptotalAmount != 0) {
							totalAmountString = temptotalAmount+" ?";
						}
						else {
							totalAmountString = "?";
						}
					} else {
						totalAmountString = temptotalAmount+"";
					}
				isTempAmountNull = false;
				list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
				cursor.moveToNext();
			}
			
			if(!list.isEmpty() && totalAmountString != null) {
				mainlist.add(list);
				list = new HashMap<String, String>();
				totalAmountString = null;
			}
		}while(!cursor.isAfterLast());
		adapter.close();
		return mainlist;
	}

	private HashMap<String, String> getHashMap(List<String> tempList,Cursor cursor) {
		HashMap<String, String> list=new HashMap<String, String>();
		try{
			for(int i=0,j=tempList.size();i<j;i++){
				try	{
					list.put(tempList.get(i), cursor.getString(cursor.getColumnIndex(tempList.get(i))));
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
			return list;
			}catch(NullPointerException e){}
		return null;
	}
}
