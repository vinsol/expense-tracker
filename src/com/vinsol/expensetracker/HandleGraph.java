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
	private List<List<String>> mGraphList;
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
			List<List<String>> mTempList = getDateIDList();
			mGraphList = getGraphList(mTempList);
			Log.v("mGraphList", mGraphList.toString());
		} else {
//			TODO if no entry
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}
	
	private List<List<String>> getGraphList(List<List<String>> idList){
		List<List<String>> listString = new ArrayList<List<String>>();
		Calendar currentDateCalendar = Calendar.getInstance();
		DisplayDate currentDateDisplayDate = new DisplayDate(currentDateCalendar);
		int j = 0;
		while(lastDateCalendar.before(currentDateCalendar) || lastDateCalendar.equals(currentDateCalendar)){
			ArrayList<String> mList = new ArrayList<String>();
			if(idList.get(j).get(2).equals(currentDateDisplayDate.getDisplayDateGraph())){
				listString.add(idList.get(j));
				j++;
			} else {
				mList.add("");
				mList.add("?");
				if(currentDateDisplayDate.isCurrentMonth()){
					mList.add(currentDateDisplayDate.getDisplayDateGraph());
				} else {
					mList.add(currentDateDisplayDate.getDisplayDateGraph());
				}
				listString.add(mList);
			}
			if(currentDateDisplayDate.isCurrentMonth()){
				currentDateCalendar.add(Calendar.DATE, -1);
				currentDateDisplayDate = new DisplayDate(currentDateCalendar);
			} else {
				currentDateCalendar.add(Calendar.WEEK_OF_MONTH, -1);
				currentDateDisplayDate = new DisplayDate(currentDateCalendar);
			}
		}
		return listString;
	}
	
	private List<List<String>> getDateIDList() {
		List<List<String>> listString = new ArrayList<List<String>>();
		Calendar mTempCalender = Calendar.getInstance();
		DisplayDate mTempDisplayDate = new DisplayDate(mTempCalender);
		int j = 0;
		while(lastDateCalendar.before(mTempCalender) || lastDateCalendar.equals(mTempCalender)){
			ArrayList<String> mList = new ArrayList<String>();
			String idList = "";
			String tempDisplayDate = mTempDisplayDate.getDisplayDateGraph();
			Double temptotalAmount = 0.0;
			String totalAmountString = null;
			boolean isTempAmountNull = false;
			while(mTempDisplayDate.getDisplayDateGraph().equals(tempDisplayDate)){
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
				} else {
					break;
				}
			}
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
			if (j >= mSubList.size()) {
				break;
			}
		}
		return listString;
	}

}
