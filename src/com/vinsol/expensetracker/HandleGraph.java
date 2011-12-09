package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinsol.android.graph.BarGraph;
import com.vinsol.expensetracker.utils.DisplayDate;

public class HandleGraph extends AsyncTask<Void, Void, Void> {

	private List<HashMap<String, String>> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private Context mContext;
	private List<HashMap<String, String>> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
	
	public HandleGraph(Context _context) {
		mContext = _context;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		lastDateCalendar = Calendar.getInstance();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		mConvertCursorToListString = new ConvertCursorToListString(mContext);
		mDataDateListGraph = mConvertCursorToListString.getDateListStringGraph();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		Log.v("mDataDateListGraph", mDataDateListGraph.toString());
		if (mDataDateListGraph.size() >= 1) {
			lastDateCalendar.setTimeInMillis(Long.parseLong(mSubList.get(mSubList.size()-1).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
			mGraphList = getGraphList();
		} else {
//			TODO if no entry
		}
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result) {
		//view of graph
		// ******start view******//
		RelativeLayout main_graph = (RelativeLayout) activity.findViewById(R.id.main_graph);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,main_graph.getBackground().getIntrinsicHeight());
		main_graph.removeAllViews();
		if(mGraphList != null)
			if(mGraphList.size() >= 1 ) {
				BarGraph barGraph = new BarGraph(mContext,mGraphList.get(0).get(1),mGraphList.get(0).get(2));
				main_graph.addView(barGraph, params);
				TextView main_graph_header_textview = (TextView) activity.findViewById(R.id.main_graph_header_textview);
				
				if(mDataDateListGraph.size() > 0)
					main_graph_header_textview.setText(mDataDateListGraph.get(0).get(DatabaseAdapter.KEY_DATE_TIME));
			}
		super.onPostExecute(result);
	}
	
	private ArrayList<ArrayList<ArrayList<String>>> getGraphList(){
		DisplayDate lastDateDisplayDate = new DisplayDate(lastDateCalendar);
		ArrayList<ArrayList<ArrayList<String>>> graphList = new ArrayList<ArrayList<ArrayList<String>>>();
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		
		int j = 0;
		ArrayList<ArrayList<String>> subGraphList = new ArrayList<ArrayList<String>>();
		ArrayList<String> mArrayIDList = new ArrayList<String>();
		ArrayList<String> mArrayValues = new ArrayList<String>();
		ArrayList<String> mArrayHorLabels = new ArrayList<String>();
		List<List<String>> mList = getDateIDList();
		while(lastDateCalendar.before(mTempCalender) || lastDateDisplayDate.getDisplayDateGraph().equals(new DisplayDate(mTempCalender).getDisplayDateGraph())){
			DisplayDate mDisplayDate = new DisplayDate(mTempCalender);
			while(mDisplayDate.isCurrentWeek()){
				if(j < mList.size()) {
					if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
						mArrayIDList.add(mList.get(j).get(0));
						mArrayValues.add(mList.get(j).get(1));
						mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
						j++;
					} else {
						mArrayIDList.add(null);
						mArrayValues.add(null);
						mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
					}
				} else {
					mArrayIDList.add(null);
					mArrayValues.add(null);
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
				}
				mTempCalender.add(Calendar.DATE, -1);
				mDisplayDate = new DisplayDate(mTempCalender);
				if(!mDisplayDate.isCurrentWeek()){
					if(mArrayIDList.size() >= 1){
						Collections.reverse(mArrayIDList);
						Collections.reverse(mArrayValues);
						Collections.reverse(mArrayHorLabels);
						subGraphList.add(mArrayIDList);
						subGraphList.add(mArrayValues);
						subGraphList.add(mArrayHorLabels);
						graphList.add(subGraphList);
						mArrayIDList = new ArrayList<String>();
						mArrayValues = new ArrayList<String>();
						mArrayHorLabels = new ArrayList<String>();
						subGraphList = new ArrayList<ArrayList<String>>();
					}
				}
			} 

			while(!mDisplayDate.isCurrentWeek() && mDisplayDate.isCurrentMonth()){
				
				if(j < mList.size()) {
					if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
						mArrayIDList.add(mList.get(j).get(0));
						mArrayValues.add(mList.get(j).get(1));
						mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
						j++;
					} else {
						mArrayIDList.add(null);
						mArrayValues.add(null);
						mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
					}
				} else {
					mArrayIDList.add(null);
					mArrayValues.add(null);
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
				}
				
				mTempCalender.add(Calendar.DATE, -1);
				mDisplayDate = new DisplayDate(mTempCalender);
				if(!mDisplayDate.isCurrentMonth()){
					if(mArrayIDList.size() >= 1){
						Collections.reverse(mArrayIDList);
						Collections.reverse(mArrayValues);
						Collections.reverse(mArrayHorLabels);
						subGraphList.add(mArrayIDList);
						subGraphList.add(mArrayValues);
						subGraphList.add(mArrayHorLabels);
						graphList.add(subGraphList);
						mArrayIDList = new ArrayList<String>();
						mArrayValues = new ArrayList<String>();
						mArrayHorLabels = new ArrayList<String>();
						subGraphList = new ArrayList<ArrayList<String>>();
					}
				}
				
			} 
			
			if(mDisplayDate.isPrevMonths() || mDisplayDate.isPrevYears()){
				
				mTempCalender.set(Calendar.DAY_OF_MONTH, mTempCalender.getActualMaximum(Calendar.DAY_OF_MONTH));
				
				mDisplayDate = new DisplayDate(mTempCalender);
				String toCheckGraphDate = mDisplayDate.getDisplayDateHeaderGraph();
				while(mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)){
					if(j < mList.size()) {
						if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
							mArrayIDList.add(mList.get(j).get(0));
							mArrayValues.add(mList.get(j).get(1));
							mArrayHorLabels.add("Week "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
							j++;
						} else {
							mArrayIDList.add(null);
							mArrayValues.add(null);
							mArrayHorLabels.add("Week "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
						}
					} else {
						mArrayIDList.add(null);
						mArrayValues.add(null);
						mArrayHorLabels.add("Week "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
					}
					mTempCalender.add(Calendar.WEEK_OF_MONTH, -1);
					mTempCalender.set(Calendar.DAY_OF_WEEK, mTempCalender.getActualMinimum(Calendar.DAY_OF_WEEK));
					mDisplayDate = new DisplayDate(mTempCalender);
					if(!mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)){
						if(mArrayIDList.size() >= 1){
							Collections.reverse(mArrayIDList);
							Collections.reverse(mArrayValues);
							Collections.reverse(mArrayHorLabels);
							subGraphList.add(mArrayIDList);
							subGraphList.add(mArrayValues);
							subGraphList.add(mArrayHorLabels);
							graphList.add(subGraphList);
							mArrayIDList = new ArrayList<String>();
							mArrayValues = new ArrayList<String>();
							mArrayHorLabels = new ArrayList<String>();
							subGraphList = new ArrayList<ArrayList<String>>();
						}
					}
				}
				
			} 
		}
		return graphList;
	}


	private List<List<String>> getDateIDList() {
		List<List<String>> listString = new ArrayList<List<String>>();
		
		Log.v("mSubList", mSubList.toString());
		for(int i = 0 ;i < mSubList.size(); ) {
			ArrayList<String> mList = new ArrayList<String>();
			String tempDisplayDate = mSubList.get(i).get(DatabaseAdapter.KEY_DATE_TIME+"Millis");
			Calendar mTCalendar = Calendar.getInstance();
			mTCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			mTCalendar.setTimeInMillis(Long.parseLong(tempDisplayDate));
			String tempDisplayDateGraph = new DisplayDate(mTCalendar).getDisplayDateGraph();
			String idList = "";
			Double temptotalAmount = 0.0;
			String totalAmountString = null;
			boolean isTempAmountNull = false;
			while(new DisplayDate(mTCalendar).getDisplayDateGraph().equals(tempDisplayDateGraph)) {
				String tempAmount = mSubList.get(i).get(DatabaseAdapter.KEY_AMOUNT);
				if (tempAmount != null && !tempAmount.equals("")) {
					try {
						temptotalAmount += Double.parseDouble(tempAmount);
					} catch (NumberFormatException e) {
					}
				} else {
					isTempAmountNull = true;
				}
				idList = idList+mSubList.get(i).get(DatabaseAdapter.KEY_ID)+",";
				i++;
				
				if (i < mSubList.size()) {
					mTCalendar.setTimeInMillis(Long.parseLong(mSubList.get(i).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
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
			mList.add(tempDisplayDateGraph);
			listString.add(mList);
			
		}
		return listString;
	}

	private String getWeekDay(int i) {
		switch(i){
		case 2:
			return "Mon";
		case 3:
			return "Tue";
		case 4:
			return "Wed";
		case 5:
			return "Thu";
		case 6:
			return "Fri";
		case 7:
			return "Sat";
		case 1:
			return "Sun";
		}
		return null;
	}

}
