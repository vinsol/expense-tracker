/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vinsol.android.graph.BarGraph;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.GraphDataList;
import com.vinsol.expensetracker.models.ListDatetimeAmount;

public class GraphHelper extends AsyncTask<Void, Void, Void> {

	private List<ListDatetimeAmount> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Entry> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
	private int j = 0;
	private LinearLayout mainGraph ;
	private LinearLayout.LayoutParams params ;
//	private static TextView graphNoItem;
	private ProgressBar graphProgressBar;
	private Gallery graphGallery;
	private ImageAdapter imageAdapter;
	private LinearLayout graphContainer;
	
	public GraphHelper(final Activity activity) {
		this.activity = activity;
		lastDateCalendar = Calendar.getInstance();
		imageAdapter = new ImageAdapter();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mainGraph = (LinearLayout) activity.findViewById(R.id.graph);
		graphProgressBar = (ProgressBar) activity.findViewById(R.id.graph_progress_bar);
		graphGallery = (Gallery) activity.findViewById(R.id.graph_gallery);
		graphContainer = (LinearLayout) activity.findViewById(R.id.graph_container);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,mainGraph.getBackground().getIntrinsicHeight());
	}
	
	@Override
	protected void onPreExecute() {
		graphProgressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		mConvertCursorToListString = new ConvertCursorToListString(activity);
		mDataDateListGraph = mConvertCursorToListString.getDateListString(true,"",R.string.sublist_thisweek);
		mSubList = mConvertCursorToListString.getListStringParticularDate("");
		if (mDataDateListGraph.size() >= 1) {
			lastDateCalendar.setTimeInMillis(mSubList.get(mSubList.size()-1).timeInMillis);
			lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			mGraphList = getGraphList();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		graphProgressBar.setVisibility(View.GONE);
		//view of graph******start view******//
//		j = 0;
//		if(mGraphList != null) {
//			if(mGraphList.size() >= 1 ) {
//				createBarGraph();
//			}
//			
//		} else {
//			graphNoItem();
//			mainGraph.addView(graphNoItem);
//			graphHeaderTextview.setText("");
//		}
//		graphGallery.setLayoutParams(new LinearLayout.LayoutParams(graphContainer.getLayoutParams()));
		graphContainer.setVisibility(View.GONE);
        graphGallery.setVisibility(View.VISIBLE);
		graphGallery.setAdapter(imageAdapter);
		graphGallery.setSelection(mDataDateListGraph.size() - 1);
		super.onPostExecute(result);
	}
	
//	private void removeGraphView() {
//		mainGraph.removeView(barGraph);
//		mainGraph.removeView(graphNoItem);
//	}
//	
//	private void createBarGraph() {
//		barGraph = new BarGraph(activity, mGraphList.get(j).get(1), mGraphList.get(j).get(2));
//		mainGraph.addView(barGraph , params);
//		if(j == 0) {
//			if(!isNotNullAll(mGraphList.get(j).get(0))) {
//				mainGraph.removeView(barGraph);
//				graphNoItem();
//				mainGraph.addView(graphNoItem);
//			}
//		}
//		graphHeaderTextview.setText(mDataDateListGraph.get(j).dateTime);
//	}
//	
//	private void graphNoItem() {
//		graphNoItem = new TextView(activity);
//		graphNoItem.setGravity(Gravity.CENTER);
//		graphNoItem.setText("No Items to Show");
//		LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, mainGraph.getBackground().getIntrinsicHeight());
//		graphNoItem.setTextColor(Color.BLACK);
//		graphNoItem.setPadding(0, 0, 0, 15);
//		graphNoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//		graphNoItem.setLayoutParams(textParams);
//	}
	
	private ArrayList<ArrayList<ArrayList<String>>> getGraphList() {
		DisplayDate lastDateDisplayDate = new DisplayDate(lastDateCalendar);
		ArrayList<ArrayList<ArrayList<String>>> graphList = new ArrayList<ArrayList<ArrayList<String>>>();
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.set(mTempCalender.get(Calendar.YEAR), mTempCalender.get(Calendar.MONTH), mTempCalender.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		
		int j = 0;
		ArrayList<ArrayList<String>> subGraphList = new ArrayList<ArrayList<String>>();
		ArrayList<String> mArrayIDList = new ArrayList<String>();
		ArrayList<String> mArrayValues = new ArrayList<String>();
		ArrayList<String> mArrayHorLabels = new ArrayList<String>();
		List<GraphDataList> mList = getDateIDList();
		while(lastDateCalendar.before(mTempCalender) || lastDateDisplayDate.getDisplayDateGraph().equals(new DisplayDate(mTempCalender).getDisplayDateGraph())) {
			DisplayDate mDisplayDate = new DisplayDate(mTempCalender);
			while(mDisplayDate.isCurrentWeek()) {
				String toCheckGraphDate = mDisplayDate.getDisplayDateHeaderGraph();
				if(j < mList.size() && mList.get(j).dateTime.equals(mDisplayDate.getDisplayDateGraph())) {
					mArrayIDList.add(mList.get(j).idList);
					mArrayValues.add(mList.get(j).amount);
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
					j++;
				} else {
					mArrayIDList.add(null);
					mArrayValues.add(null);
					mArrayHorLabels.add(getWeekDay(mTempCalender.get(Calendar.DAY_OF_WEEK)));
				}
				mTempCalender.add(Calendar.DATE, -1);
				mDisplayDate = new DisplayDate(mTempCalender);
				if(!mDisplayDate.isCurrentWeek()) {
					if(mArrayIDList.size() >= 1) {
						Collections.reverse(mArrayIDList);
						Collections.reverse(mArrayValues);
						Collections.reverse(mArrayHorLabels);
						subGraphList.add(mArrayIDList);
						subGraphList.add(mArrayValues);
						subGraphList.add(mArrayHorLabels);
						ArrayList<String> displayDate = new ArrayList<String>();
						displayDate.add(toCheckGraphDate);
						subGraphList.add(displayDate);
						graphList.add(subGraphList);
						mArrayIDList = new ArrayList<String>();
						mArrayValues = new ArrayList<String>();
						mArrayHorLabels = new ArrayList<String>();
						subGraphList = new ArrayList<ArrayList<String>>();
					}
				}
			} 

			if(!mDisplayDate.isCurrentWeek() && mDisplayDate.isCurrentMonth()) {
				
				String toCheckGraphDate = mDisplayDate.getDisplayDateHeaderGraph();
				while(mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)) {
					if(j < mList.size()) {
						if(mList.get(j).dateTime.equals(mDisplayDate.getDisplayDateGraph())) {
							mArrayIDList.add(mList.get(j).idList);
							mArrayValues.add(mList.get(j).amount);
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
					if(!mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)) {
						if(mArrayIDList.size() >= 1) {
							Collections.reverse(mArrayIDList);
							Collections.reverse(mArrayValues);
							Collections.reverse(mArrayHorLabels);
							subGraphList.add(mArrayIDList);
							subGraphList.add(mArrayValues);
							subGraphList.add(mArrayHorLabels);
							ArrayList<String> displayDate = new ArrayList<String>();
							displayDate.add(toCheckGraphDate);
							subGraphList.add(displayDate);
							if(isNotNullAll(mArrayIDList))
								graphList.add(subGraphList);
							mArrayIDList = new ArrayList<String>();
							mArrayValues = new ArrayList<String>();
							mArrayHorLabels = new ArrayList<String>();
							subGraphList = new ArrayList<ArrayList<String>>();
						}
					}
				}
			}
			
			if(mDisplayDate.isPrevMonths() || mDisplayDate.isPrevYears()) {
				mTempCalender.set(Calendar.DAY_OF_MONTH, mTempCalender.getActualMaximum(Calendar.DAY_OF_MONTH));
				mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
				mDisplayDate = new DisplayDate(mTempCalender);
				String toCheckGraphDate = mDisplayDate.getDisplayDateHeaderGraph();
				while(mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)) {
					if(j < mList.size() && mList.get(j).dateTime.equals(mDisplayDate.getDisplayDateGraph())) {
						mArrayIDList.add(mList.get(j).idList);
						mArrayValues.add(mList.get(j).amount);
						mArrayHorLabels.add("W "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
						j++;
					} else {
						mArrayIDList.add(null);
						mArrayValues.add(null);
						mArrayHorLabels.add("W "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
					}
					mTempCalender.add(Calendar.WEEK_OF_MONTH, -1);
					mTempCalender.set(Calendar.DAY_OF_WEEK, mTempCalender.getActualMinimum(Calendar.DAY_OF_WEEK));
					mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
					mDisplayDate = new DisplayDate(mTempCalender);
					if(!mDisplayDate.getDisplayDateHeaderGraph().equals(toCheckGraphDate)) {
						if(mArrayIDList.size() >= 1) {
							Collections.reverse(mArrayIDList);
							Collections.reverse(mArrayValues);
							Collections.reverse(mArrayHorLabels);
							subGraphList.add(mArrayIDList);
							subGraphList.add(mArrayValues);
							subGraphList.add(mArrayHorLabels);
							ArrayList<String> displayDate = new ArrayList<String>();
							displayDate.add(toCheckGraphDate);
							subGraphList.add(displayDate);
							if(isNotNullAll(mArrayIDList))
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

	private boolean isNotNullAll(ArrayList<String> mArrayIDList) {
		for(int i = 0;i<mArrayIDList.size();i++) {
			if(mArrayIDList.get(i) != null) {
				return true;
			}
		}
		return false;
	}

	private List<GraphDataList> getDateIDList() {
		List<GraphDataList> listString = new ArrayList<GraphDataList>();
		for(int i = 0 ;i < mSubList.size(); ) {
			GraphDataList mList = new GraphDataList();
			Long tempDisplayDate = mSubList.get(i).timeInMillis;
			Calendar mTCalendar = Calendar.getInstance();
			mTCalendar.setTimeInMillis(tempDisplayDate);
			mTCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			String tempDisplayDateGraph = new DisplayDate(mTCalendar).getDisplayDateGraph();
			String idList = "";
			Double temptotalAmount = 0.0;
			String totalAmountString = null;
			boolean isTempAmountNull = false;
			while(new DisplayDate(mTCalendar).getDisplayDateGraph().equals(tempDisplayDateGraph)) {
				String tempAmount = mSubList.get(i).amount;
				if (tempAmount != null && !tempAmount.equals("")) {
					try {
						temptotalAmount += Double.parseDouble(tempAmount);
					} catch (NumberFormatException e) {
					}
				} else {
					isTempAmountNull = true;
				}
				idList = idList+mSubList.get(i).id+",";
				i++;
				
				if (i < mSubList.size()) {
					mTCalendar.setTimeInMillis(mSubList.get(i).timeInMillis);
					mTCalendar.setFirstDayOfWeek(Calendar.MONDAY);
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
			mList.idList = idList;
			mList.amount = totalAmountString;
			mList.dateTime = tempDisplayDateGraph;
			listString.add(mList);
		}
		return listString;
	}

	private String getWeekDay(int i) {
		switch(i){
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		case Calendar.SUNDAY:
			return "Sun";
		}
		return null;
	}
	
	public class ImageAdapter extends BaseAdapter {

	    @Override
	    public int getCount() {
	        return mDataDateListGraph.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return position;
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	GraphViewHolder graphViewHolder;
	    	if(convertView == null) {
	    		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		convertView = inflater.inflate(R.layout.graph, null);
	    		graphViewHolder = new GraphViewHolder();
	    		convertView.setTag(graphViewHolder);
	    	} else {
	    		graphViewHolder = (GraphViewHolder) convertView.getTag();
	    	}
	    	
	    	j = mDataDateListGraph.size() - position - 1;
	    	graphViewHolder.grahHeaderTextView = (TextView) convertView.findViewById(R.id.graph_header_textview);
	    	graphViewHolder.barGraph = new BarGraph(activity, mGraphList.get(j).get(1), mGraphList.get(j).get(2));
	    	graphViewHolder.graphMainView = (LinearLayout) convertView.findViewById(R.id.graph);
	    	graphViewHolder.grahHeaderTextView.setText(mDataDateListGraph.get(j).dateTime);
	    	graphViewHolder.graphMainView.addView(graphViewHolder.barGraph, params);
    		return convertView;
	    }
	    
	}
	
	private class GraphViewHolder {
		TextView grahHeaderTextView;
		BarGraph barGraph;
		LinearLayout graphMainView;
	}
	
}
