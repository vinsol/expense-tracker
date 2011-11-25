package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.utils.DisplayDate;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ConvertCursorToListString {
	Context context;
	public ConvertCursorToListString(Context _context) {
		context=_context;
	}
	

	List<HashMap<String, String>> getDateListString(){
		HashMap<String, String> list = new HashMap<String, String>();
		DatabaseAdapter adapter=new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor= adapter.getDateDatabase();
		List<HashMap<String, String>> mainlist=new ArrayList<HashMap<String, String>>();
		double temptotalAmount = 0;
		String totalAmountString = null;
		boolean isTempAmountNull = false;
		if(cursor.getCount()>=1){
			cursor.moveToFirst();
			
			do {
				Calendar mTempCalendar = Calendar.getInstance();
				mTempCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
			
				DisplayDate mDisplayDate = new DisplayDate(mTempCalendar);
				if(list.isEmpty()){
					list.put(DatabaseAdapter.KEY_DATE_TIME, mDisplayDate.getHeaderFooterListDisplayDate());    ///TODO
				}
				String tempAmount = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_AMOUNT));
				if(tempAmount != null && !tempAmount.equals("")){
					try{
						temptotalAmount += Double.parseDouble(tempAmount);
					}catch(NumberFormatException e){}
				} else {
					isTempAmountNull = true;
				}
				cursor.moveToNext();
				
				if(!cursor.isAfterLast()) {
					Calendar mTempSubCalendar = Calendar.getInstance();
					mTempSubCalendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
					DisplayDate mTempDisplayDate = new DisplayDate(mTempSubCalendar);
					if(!list.get(DatabaseAdapter.KEY_DATE_TIME).equals(mTempDisplayDate.getHeaderFooterListDisplayDate())) { //TODO  
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
						if(totalAmountString.contains("?") && totalAmountString.length()>1){
							String temp = totalAmountString.substring(0, totalAmountString.length()-2);
							Double mAmount = Double.parseDouble(temp);
							mAmount = (double)((int)((mAmount+0.005)*100.0)/100.0);
							if(mAmount.toString().contains(".")){
								if(mAmount.toString().charAt(mAmount.toString().length()-3) == '.'){
									totalAmountString = mAmount.toString()+" ?";
								} else if(mAmount.toString().charAt(mAmount.toString().length()-2) == '.'){
									totalAmountString = mAmount.toString()+"0 ?";
								}
								
							} else {
									totalAmountString = mAmount.toString()+".00 ?";
							}
						} else  if(!totalAmountString.contains("?")){
							String temp = totalAmountString.substring(0, totalAmountString.length());
							Double mAmount = Double.parseDouble(temp);
							mAmount = (double)((int)((mAmount+0.005)*100.0)/100.0);
							Log.v("mAmount", mAmount.toString()+" "+mAmount.toString().charAt(mAmount.toString().length()-3)+" "+mAmount.toString().charAt(mAmount.toString().length()-2));
							
							if(mAmount.toString().contains(".")){
								if(mAmount.toString().charAt(mAmount.toString().length()-3) == '.'){
									totalAmountString = mAmount.toString()+"";
								} else if(mAmount.toString().charAt(mAmount.toString().length()-2) == '.'){
									totalAmountString = mAmount.toString()+"0";
								}
								
							} else {
									totalAmountString = mAmount.toString()+".00";
							}
						}
						list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
						temptotalAmount = 0;
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
					if(totalAmountString.contains("?") && totalAmountString.length()>1){
						String temp = totalAmountString.substring(0, totalAmountString.length()-2);
						Double mAmount = Double.parseDouble(temp);
						mAmount = (double)((int)((mAmount+0.005)*100.0)/100.0);
						if(mAmount.toString().contains(".")){
							if(mAmount.toString().charAt(mAmount.toString().length()-3) == '.'){
								totalAmountString = mAmount.toString()+" ?";
							} else if(mAmount.toString().charAt(mAmount.toString().length()-2) == '.'){
								totalAmountString = mAmount.toString()+"0 ?";
							}
							
						} else {
								totalAmountString = mAmount.toString()+".00 ?";
						}
					} else  if(!totalAmountString.contains("?")){
						String temp = totalAmountString.substring(0, totalAmountString.length());
						Double mAmount = Double.parseDouble(temp);
						mAmount = (double)((int)((mAmount+0.005)*100.0)/100.0);
						Log.v("mAmount", mAmount.toString()+" "+mAmount.toString().charAt(mAmount.toString().length()-3)+" "+mAmount.toString().charAt(mAmount.toString().length()-2));
						
						if(mAmount.toString().contains(".")){
							if(mAmount.toString().charAt(mAmount.toString().length()-3) == '.'){
								totalAmountString = mAmount.toString()+"";
							} else if(mAmount.toString().charAt(mAmount.toString().length()-2) == '.'){
								totalAmountString = mAmount.toString()+"0";
							}
							
						} else {
								totalAmountString = mAmount.toString()+".00";
						}
					}
					list.put(DatabaseAdapter.KEY_AMOUNT, totalAmountString);
					cursor.moveToNext();
				}
			
				if(!list.isEmpty() && totalAmountString != null) {
					mainlist.add(list);
					list = new HashMap<String, String>();
					totalAmountString = null;
				}
			}while(!cursor.isAfterLast());
			
			
		}
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

	
	
	public List<HashMap<String, String>> getListStringParticularDate() {
		DatabaseAdapter adapter=new DatabaseAdapter(context);
		adapter.open();
		Cursor cursor= adapter.getDateDatabase();
		List<HashMap<String, String>> mainlist=new ArrayList<HashMap<String, String>>();
		if(cursor.getCount()>=1){
			cursor.moveToFirst();
			do{
				List<String> tempList = new ArrayList<String>();
				tempList.add(DatabaseAdapter.KEY_ID);
				tempList.add(DatabaseAdapter.KEY_AMOUNT);
				tempList.add(DatabaseAdapter.KEY_FAVORITE);
				tempList.add(DatabaseAdapter.KEY_LOCATION);
				tempList.add(DatabaseAdapter.KEY_TAG);
				tempList.add(DatabaseAdapter.KEY_TYPE);
			
				HashMap<String, String> list=getHashMap(tempList,cursor);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				DisplayDate mDisplayDate = new DisplayDate(calendar);
				list.put(DatabaseAdapter.KEY_DATE_TIME, mDisplayDate.getHeaderFooterListDisplayDate());   //TODO
				list.put(DatabaseAdapter.KEY_DATE_TIME+"Millis", cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_TIME)));
				if(!list.isEmpty())
					mainlist.add(list);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
		}
		adapter.close();
		return mainlist;
	}
}
