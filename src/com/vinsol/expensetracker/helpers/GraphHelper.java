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
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.vinsol.android.graph.BarGraph;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.GraphDataList;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.utils.Log;

public class GraphHelper extends AsyncTask<Void, Void, Void> implements OnClickListener {

	private List<ListDatetimeAmount> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Entry> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
	private int j = 0;
	private LinearLayout mainGraph ;
	private LinearLayout.LayoutParams params ;
	private static BarGraph barGraph;
	private static TextView graphNoItem;
	private TextView graphHeaderTextview;
	private View.OnTouchListener gestureListener;
	private ProgressBar graphProgressBar;
	
	public GraphHelper(Activity activity) {
		this.activity = activity;
		lastDateCalendar = Calendar.getInstance();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mainGraph = (LinearLayout) activity.findViewById(R.id.main_graph);
		graphHeaderTextview = (TextView) activity.findViewById(R.id.main_graph_header_textview);
		graphProgressBar = (ProgressBar) activity.findViewById(R.id.main_graph_progress_bar);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,mainGraph.getBackground().getIntrinsicHeight());
		final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
			}
        };
	}
	
	@Override
	protected void onPreExecute() {
		removeGraphView();
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		mConvertCursorToListString = new ConvertCursorToListString(activity);
		mDataDateListGraph = mConvertCursorToListString.getDateListString(true,"");
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
		//view of graph******start view******//
		if(mGraphList != null) {
			if(mGraphList.size() >= 1 ) {
				createBarGraph();
			}
			
		} else {
			graphNoItem();
			mainGraph.addView(graphNoItem);
			graphHeaderTextview.setText("");
		}
		super.onPostExecute(result);
	}
	
	private void removeGraphView() {
		mainGraph.removeView(barGraph);
		mainGraph.removeView(graphNoItem);
		graphProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void createBarGraph() {
		barGraph = new BarGraph(activity,mGraphList.get(j).get(1),mGraphList.get(j).get(2));
		graphProgressBar.setVisibility(View.GONE);
		mainGraph.addView(barGraph, params);
		if(j == 0) {
			if(!isNotNullAll(mGraphList.get(j).get(0))) {
				mainGraph.removeView(barGraph);
				graphNoItem();
				mainGraph.addView(graphNoItem);
			}
		}
		Log.d("*********************************");
		Log.d("Setting gesture listener on graph");
		Log.d("*********************************");
		mainGraph.setOnTouchListener(gestureListener);
		mainGraph.setOnClickListener(this);
		graphHeaderTextview.setText(mGraphList.get(j).get(3).get(0));
	}
	
	private void graphNoItem() {
		graphNoItem = new TextView(activity);
		graphNoItem.setGravity(Gravity.CENTER);
		graphNoItem.setText("No Items to Show");
		LayoutParams textParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, mainGraph.getBackground().getIntrinsicHeight());
		graphNoItem.setTextColor(Color.BLACK);
		graphNoItem.setPadding(0, 0, 0, 15);
		graphNoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		graphNoItem.setLayoutParams(textParams);
	}
	
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
					if(j < mList.size()) {
						if(mList.get(j).dateTime.equals(mDisplayDate.getDisplayDateGraph())) {
							mArrayIDList.add(mList.get(j).idList);
							mArrayValues.add(mList.get(j).amount);
							mArrayHorLabels.add("W "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
							j++;
						} else {
							mArrayIDList.add(null);
							mArrayValues.add(null);
							mArrayHorLabels.add("W "+mTempCalender.get(Calendar.WEEK_OF_MONTH));
						}
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
	
	@Override
	public void onClick(View v) {
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		
		private int SWIPE_MIN_DISTANCE ;
	    
		public MyGestureDetector() {
			SWIPE_MIN_DISTANCE = ViewConfiguration.getTouchSlop();
		}
		
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    	
	    	 if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                 leftSwipeAction();
             }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                 rightSwipeAction();
             }

	        return true;
	    }
	}
	
	private boolean leftSwipeAction() {
		Log.d("Left Swipe");
		if(j == 0) {
			overScrollingEffect();
			return true;
		}
		j--;
		removeGraphView();
		createBarGraph();
		return true;
	}
	
	private boolean rightSwipeAction() {
		Log.d("Right Swipe");
		if(j == mGraphList.size()-1) {
			overScrollingEffect();
			return true;
		}
		j++;
		removeGraphView();
		createBarGraph();
		return true;
	}

	private void overScrollingEffect() {
		// TODO Auto-generated method stub
	}
	
}
