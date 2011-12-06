package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.ConvertCursorToListString;
import com.vinsol.expensetracker.utils.DisplayDate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class HandleGraph extends AsyncTask<Void, Void, Void> {

	private List<HashMap<String, String>> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private Context mContext;
	private List<HashMap<String, String>> mSubList;
	private DisplayDate mDisplayDate;
	private List<List<String>> idList;
	private Calendar lastDateCalendar;
	
	public HandleGraph(Context _context) {
		mContext = _context;
		lastDateCalendar = Calendar.getInstance();
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		mConvertCursorToListString = new ConvertCursorToListString(mContext);
		mDataDateListGraph = mConvertCursorToListString.getDateListStringGraph();
		mSubList = mConvertCursorToListString.getListStringParticularDate();

		if (mDataDateListGraph.size() >= 1) {
			lastDateCalendar.setTimeInMillis(Long.parseLong(mSubList.get(mSubList.size()-1).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
			idList = getDateIDList();
		} else {
//			TODO if no entry
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}
	
	private List<List<String>> getDateIDList() {
		List<List<String>> listString = new ArrayList<List<String>>();
		Calendar mTempCalender = Calendar.getInstance();
		DisplayDate mTempDisplayDate = new DisplayDate(mTempCalender);
		int j = 0;
		boolean isAdd = false;
		while(lastDateCalendar.before(mTempCalender) || lastDateCalendar.equals(mTempCalender)){
			ArrayList<String> mList = new ArrayList<String>();
			String idList = "";
			String tempDisplayDate = mTempDisplayDate.getDisplayDate();
			Double temptotalAmount = 0.0;
			String totalAmountString = null;
			boolean isTempAmountNull = false;
			while(mTempDisplayDate.getDisplayDate().equals(tempDisplayDate)){
				String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
				if (tempAmount != null && !tempAmount.equals("")) {
					try {
						temptotalAmount += Double.parseDouble(tempAmount);
					} catch (NumberFormatException e) {
					}
				} else {
					isTempAmountNull = true;
				}
				idList = idList+mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",";
				j++;
				if (j < mSubList.size()) {
					mTempCalender.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
					mTempDisplayDate = new DisplayDate(mTempCalender);
					if(!mTempDisplayDate.getDisplayDate().equals(tempDisplayDate)){
						mTempCalender.setTimeInMillis(Long.parseLong(mSubList.get(1-j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						mTempCalender.add(Calendar.DATE, -1);
						Calendar mTempCurrentCalendar = Calendar.getInstance();
						mTempCurrentCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						if(!new DisplayDate(mTempCalender).equals(new DisplayDate(mTempCurrentCalendar))){
							mList.add(idList);
							mList.add(totalAmountString);
							mList.add(tempDisplayDate);
							listString.add(mList);
							isAdd = false;
						} else {
							isAdd = true;
						}
					}
				} else {
					break;
				}
			}
			if(isAdd){
				if (isTempAmountNull) {
					if (temptotalAmount != 0) {
						totalAmountString = temptotalAmount + " ?";
					} else {
						totalAmountString = "?";
					}
				} else {
					totalAmountString = temptotalAmount + "";
				}
			
				mList.add(idList);
				mList.add(totalAmountString);
				mList.add(tempDisplayDate);
				listString.add(mList);
			}
			if (j >= mSubList.size()) {
				break;
			}
		}
		Log.v("listString", listString.toString()+" asdf");
		return listString;
	}

}
