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

import com.vinsol.expensetracker.utils.DisplayDate;

public class HandleGraph extends AsyncTask<Void, Void, Void> {

	private List<HashMap<String, String>> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private Context mContext;
	private List<HashMap<String, String>> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
	private ArrayList<String> horlabels;
	
	public HandleGraph(Context _context) {
		mContext = _context;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		lastDateCalendar = Calendar.getInstance();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		horlabels = new ArrayList<String>();
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		mConvertCursorToListString = new ConvertCursorToListString(mContext);
		mDataDateListGraph = mConvertCursorToListString.getDateListStringGraph();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		Log.v("mDataDateListGraph", mDataDateListGraph.toString());
		if (mDataDateListGraph.size() >= 1) {
			lastDateCalendar.setTimeInMillis(Long.parseLong(mSubList.get(mSubList.size()-1).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
//			List<List<String>> mTempList = getDateIDList();
//			new com.vinsol.expensetracker.utils.Log().d(mTempList);
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
//		DateHelper mDateHelper = new DateHelper(mGraphList.get(0).get(2).get(0));
//		Log.v("mGraphList", mGraphList.toString());
		
//		BarGraph barGraph = new BarGraph(mContext,mGraphList.get(0).get(0),getHorLabelList(mDataDateListGraph.get(0).get(DatabaseAdapter.KEY_DATE_TIME)));
//		main_graph.addView(barGraph, params);
//				
//		ArrayList<String> valueList = new ArrayList<String>();
//		valueList.add("2.20");
//		valueList.add("52.20");
//		valueList.add("32.20");
//		valueList.add("222.20?");
//		valueList.add("342.20?");
//		valueList.add("92.20");
//		valueList.add("12.20");
//		
//		ArrayList<String> _horLabels = new ArrayList<String>();
//		_horLabels.add("Sun");
//		_horLabels.add("Mon");
//		_horLabels.add("Tue");
//		_horLabels.add("Wed");
//		_horLabels.add("Thu");
//		_horLabels.add("Fri");
//		_horLabels.add("Sat");
//		RelativeLayout main_graph = (RelativeLayout) activity.findViewById(R.id.main_graph);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT,
//				main_graph.getBackground().getIntrinsicHeight()
//				);
		
//		BarGraph barGraph = new BarGraph(mContext, valueList, _horLabels);
//		main_graph.addView(barGraph, params);
		
		
		TextView main_graph_header_textview = (TextView) activity.findViewById(R.id.main_graph_header_textview);
		main_graph_header_textview.setText(mDataDateListGraph.get(0).get(DatabaseAdapter.KEY_DATE_TIME));
		super.onPostExecute(result);
	}
	
	private ArrayList<ArrayList<ArrayList<String>>> getGraphList(){
		ArrayList<ArrayList<ArrayList<String>>> graphList = new ArrayList<ArrayList<ArrayList<String>>>();
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		int j = 0;
		ArrayList<ArrayList<String>> subGraphList = new ArrayList<ArrayList<String>>();
		ArrayList<String> mArrayIDList = new ArrayList<String>();
		ArrayList<String> mArrayValues = new ArrayList<String>();
		ArrayList<String> mArrayHorLabels = new ArrayList<String>();
		List<List<String>> mList = getDateIDList();
		Log.v("mList", mList.toString());
		while(lastDateCalendar.before(mTempCalender) || lastDateCalendar.equals(mTempCalender)){
			DisplayDate mDisplayDate = new DisplayDate(mTempCalender);
			if(mDisplayDate.isCurrentWeek()){
				if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
					mArrayIDList.add(mList.get(j).get(0));
					mArrayValues.add(mList.get(j).get(1));
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
					j++;
				} else {
					mArrayIDList.add(null);
					mArrayValues.add(null);
					mArrayHorLabels.add(null);
				}
				mTempCalender.add(Calendar.DATE, -1);
				mDisplayDate = new DisplayDate(mTempCalender);
				if(!mDisplayDate.isCurrentWeek()){
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
				
				if(j >= mList.size()){
					break;
				}
			} else if(mDisplayDate.isCurrentMonth()){
				
				if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
					mArrayIDList.add(mList.get(j).get(0));
					mArrayValues.add(mList.get(j).get(1));
					
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
					j++;
				} else {
					mArrayIDList.add(null);
					mArrayValues.add(null);
					mArrayHorLabels.add(null);
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
					}
					mArrayIDList = new ArrayList<String>();
					mArrayValues = new ArrayList<String>();
					mArrayHorLabels = new ArrayList<String>();
					subGraphList = new ArrayList<ArrayList<String>>();
				}
				
				if(j >= mList.size()){
					break;
				}
			} else if(mDisplayDate.isPrevMonths() || mDisplayDate.isPrevYears()){
				if(mList.get(j).get(2).equals(mDisplayDate.getDisplayDateGraph())){
					Log.v("mList.get(j).get(2)", mList.get(j).get(2));
					mArrayIDList.add(mList.get(j).get(0));
					mArrayValues.add(mList.get(j).get(1));
					j++;
				} else {
					mArrayIDList.add(null);
					mArrayHorLabels.add(null);
					mArrayValues.add(null);
				}
				mTempCalender.add(Calendar.WEEK_OF_MONTH, -1);
				mDisplayDate = new DisplayDate(mTempCalender);
				if(mArrayIDList.size() >= 1){
					Collections.reverse(mArrayIDList);
					Collections.reverse(mArrayValues);
					subGraphList.add(mArrayIDList);
					subGraphList.add(mArrayValues);
					subGraphList.add(getHorLabelListPrevMonth(mTempCalender));
					graphList.add(subGraphList);
				}
				mArrayIDList = new ArrayList<String>();
				mArrayValues = new ArrayList<String>();
				mArrayHorLabels = new ArrayList<String>();
				subGraphList = new ArrayList<ArrayList<String>>();
				
				if(j >= mList.size()){
					break;
				}
			}
			
		}
		
		
		Log.v("graphList", graphList.toString());
		return graphList;
	}
	
	private ArrayList<String> getHorLabelListPrevMonth(Calendar mSubTempCalender) {
		
		mSubTempCalender.add(Calendar.WEEK_OF_MONTH, 1);
		int currentMonth = mSubTempCalender.get(Calendar.MONTH);
		do{
			mSubTempCalender.add(Calendar.WEEK_OF_MONTH, -1);
		} while(mSubTempCalender.get(Calendar.MONTH) == currentMonth);
		mSubTempCalender.add(Calendar.WEEK_OF_MONTH, 1);
		int noOfWeeks = mSubTempCalender.get(Calendar.WEEK_OF_MONTH);
		ArrayList<String> horLabelsList = new ArrayList<String>();
		for(int i = 1;i<=noOfWeeks ;i++){
			horLabelsList.add("Week "+i);
		}
		return horLabelsList;
	}


	private List<List<String>> getDateIDList() {
		List<List<String>> listString = new ArrayList<List<String>>();
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
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

	private String getWeekDay(int i) {
		switch(i){
		case 0:
			return "Mon";
		case 1:
			return "Tue";
		case 2:
			return "Wed";
		case 3:
			return "Thu";
		case 4:
			return "Fri";
		case 5:
			return "Sat";
		case 6:
			return "Sun";
		}
		return null;
	}

}
