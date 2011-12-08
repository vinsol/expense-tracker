package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
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
	private ArrayList<String> horlabels;
	
	public HandleGraph(Context _context) {
		mContext = _context;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		lastDateCalendar = Calendar.getInstance();
		horlabels = new ArrayList<String>();
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
//		mConvertCursorToListString = new ConvertCursorToListString(mContext);
//		mDataDateListGraph = mConvertCursorToListString.getDateListStringGraph();
//		mSubList = mConvertCursorToListString.getListStringParticularDate();
//
//		if (mDataDateListGraph.size() >= 1) {
//			lastDateCalendar.setTimeInMillis(Long.parseLong(mSubList.get(mSubList.size()-1).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
//			List<List<String>> mTempList = getDateIDList();
//			mGraphList = getGraphList(mTempList);
//		} else {
////			TODO if no entry
//		}
		return null;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result) {
		//view of graph
		// ******start view******//
//		LinearLayout main_graph = (LinearLayout) activity.findViewById(R.id.main_graph);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT,
//				main_graph.getBackground().getIntrinsicHeight()
//				);
////		DateHelper mDateHelper = new DateHelper(mGraphList.get(0).get(2).get(0));
//		Log.v("mGraphList", mGraphList.toString());
//		
//		BarGraph barGraph = new BarGraph(mContext,mGraphList.get(0).get(0),getHorLabelList(mDataDateListGraph.get(0).get(DatabaseAdapter.KEY_DATE_TIME)),mDataDateListGraph.get(0).get(DatabaseAdapter.KEY_DATE_TIME));
//		main_graph.addView(barGraph, params);
//				
		ArrayList<String> valueList = new ArrayList<String>();
		valueList.add("2.20");
		valueList.add("52.20");
		valueList.add("32.20");
		valueList.add("222.20?");
		valueList.add("342.20?");
		valueList.add("92.20");
		valueList.add("12.20");
		
		ArrayList<String> _horLabels = new ArrayList<String>();
		_horLabels.add("Sun");
		_horLabels.add("Mon");
		_horLabels.add("Tue");
		_horLabels.add("Wed");
		_horLabels.add("Thu");
		_horLabels.add("Fri");
		_horLabels.add("Sat");
		RelativeLayout main_graph = (RelativeLayout) activity.findViewById(R.id.main_graph);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				main_graph.getBackground().getIntrinsicHeight()
				);
		
		BarGraph barGraph = new BarGraph(mContext, valueList, _horLabels);
		main_graph.addView(barGraph, params);
		TextView main_graph_header_textview = (TextView) activity.findViewById(R.id.main_graph_header_textview);
		main_graph_header_textview.setText("tyu");
		super.onPostExecute(result);
	}
	
	private ArrayList<ArrayList<ArrayList<String>>> getGraphList(List<List<String>> idList){
		ArrayList<ArrayList<String>> listString = new ArrayList<ArrayList<String>>();
		Calendar currentDateCalendar = Calendar.getInstance();
		DisplayDate currentDateDisplayDate = new DisplayDate(currentDateCalendar);
		int j = 0;
		while(lastDateCalendar.before(currentDateCalendar) || lastDateCalendar.equals(currentDateCalendar)){
			ArrayList<String> mList = new ArrayList<String>();
			if(idList.get(j).get(2).equals(currentDateDisplayDate.getDisplayDateGraph())){
				listString.add((ArrayList<String>) idList.get(j));
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
		
		for(int i = 0;i<mDataDateListGraph.size();i++){
			Log.v("mDataDateListGraph "+i, mDataDateListGraph.get(i)+" /t uio");
			ArrayList<ArrayList<String>> arrayArrayList = new ArrayList<ArrayList<String>>();
			ArrayList<String> arrayList = new ArrayList<String>();
			
		}
//		return listString;
		return null;
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
	
	private ArrayList<String> getHorLabelList(String str){
		ArrayList<String> mList = new ArrayList<String>();
		if(str.contains("Week")){
			for(int i = 0 ;i<7 ;i++){
				mList.add(getWeekDay(i));
			}
		}
		return mList;
	}


	private String getWeekDay(int i) {
		switch(i){
		case 0:
			return "Sun";
		case 1:
			return "Mon";
		case 2:
			return "Tue";
		case 3:
			return "Wed";
		case 4:
			return "Thu";
		case 5:
			return "Fri";
		case 6:
			return "Sat";
		}
		return null;
	}

}
