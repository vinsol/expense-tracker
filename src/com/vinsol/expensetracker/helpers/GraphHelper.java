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
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.vinsol.android.graph.BarGraph;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.GraphDataList;
import com.vinsol.expensetracker.models.ListDatetimeAmount;

public class GraphHelper extends AsyncTask<Void, Void, Void> implements OnClickListener{

	private List<ListDatetimeAmount> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Entry> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
	private LinearLayout mainGraph;
	private ProgressBar graphProgressBar;
	private Gallery graphGallery;
	private GalleryAdapter galleryAdapter;
	private TextView graphHeaderTextViewCenter;
	private TextView graphHeaderTextViewLeft;
	private TextView graphHeaderTextViewRight;
	
	public GraphHelper(Activity activity,ProgressBar graphProgressBar) {
		this.activity = activity;
		lastDateCalendar = Calendar.getInstance();
		galleryAdapter = new GalleryAdapter();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mainGraph = (LinearLayout) activity.findViewById(R.id.main_graph);
		this.graphProgressBar = graphProgressBar;
		graphGallery = (Gallery) activity.findViewById(R.id.graph_gallery);
		graphHeaderTextViewCenter = (TextView) activity.findViewById(R.id.graph_header_textview_center);
		graphHeaderTextViewLeft = (TextView) activity.findViewById(R.id.graph_header_textview_left);
		graphHeaderTextViewRight = (TextView) activity.findViewById(R.id.graph_header_textview_right);
		graphGallery.setSpacing(0);
		graphGallery.setFadingEdgeLength(0);
		graphHeaderTextViewLeft.setOnClickListener(this);
		graphHeaderTextViewRight.setOnClickListener(this);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		mConvertCursorToListString = new ConvertCursorToListString(activity);
		mDataDateListGraph = mConvertCursorToListString.getDateListString(false, true,"",R.string.sublist_thisweek);
		mSubList = mConvertCursorToListString.getEntryList(false,"");
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
		mainGraph.setVisibility(View.GONE);
        graphGallery.setVisibility(View.VISIBLE);
        graphGallery.setAdapter(galleryAdapter);
        graphGallery.setSelection(galleryAdapter.getCount() - 1);
        graphGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
				int j;
				if(mDataDateListGraph != null && !mDataDateListGraph.isEmpty()) {
					j = mDataDateListGraph.size() - position - 1;
					graphHeaderTextViewCenter.setText(mDataDateListGraph.get(j).dateTime);
					if(j + 1  != mDataDateListGraph.size())
						graphHeaderTextViewLeft.setText(mDataDateListGraph.get(j + 1).dateTime);
					else 
						graphHeaderTextViewLeft.setText("");
					if(j - 1 != -1)
						graphHeaderTextViewRight.setText(mDataDateListGraph.get(j - 1).dateTime);
					else
						graphHeaderTextViewRight.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				graphHeaderTextViewCenter.setText("");
			}
		});
		super.onPostExecute(result);
	}
	
	private TextView graphNoItem() {
		TextView graphNoItem = new TextView(activity);
		graphNoItem.setGravity(Gravity.CENTER);
		graphNoItem.setText("No Items to Show");
		graphNoItem.setTextColor(Color.BLACK);
		graphNoItem.setPadding(0, 0, 0, 15);
		graphNoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		
		return graphNoItem;
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
			
			if(mDisplayDate.isNotCurrentMonthAndCurrentYear() || mDisplayDate.isPrevYears()) {
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
	
	public class GalleryAdapter extends BaseAdapter {

		private Gallery.LayoutParams params;
		
		public GalleryAdapter() {
			params = new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT, activity.getResources().getDrawable(R.drawable.graph).getIntrinsicHeight());
		}
		
	    @Override
	    public int getCount() {
	    	if(mDataDateListGraph != null && mDataDateListGraph.size() > 0 )
	    		return mDataDateListGraph.size();
	    	else 
	    		return 1;
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
	    	graphViewHolder.graphMainView = (LinearLayout) convertView.findViewById(R.id.main_graph);
	    	int j;
	    	if(mGraphList != null && mGraphList.size() > 0) {
	    		j = mDataDateListGraph.size() - position - 1;
			    graphViewHolder.barGraph = new BarGraph(activity, mGraphList.get(j).get(1), mGraphList.get(j).get(2));
			    if(j == 0 && !isNotNullAll(mGraphList.get(j).get(0))) {
			    	graphViewHolder.graphMainView.addView(graphNoItem());
				} else {
					graphViewHolder.graphMainView.addView(graphViewHolder.barGraph);
				}
	    	} else {
	    		graphViewHolder.graphMainView.addView(graphNoItem());
	    	}
	    	convertView.setLayoutParams(params);
    		return convertView;
	    }
	    
	}
	
	private class GraphViewHolder {
		BarGraph barGraph;
		LinearLayout graphMainView;
	}

	@Override
	public void onClick(View v) {
		FlurryAgent.onEvent(activity.getString(R.string.graph_changed_using_textview));
		int j;
		j = graphGallery.getSelectedItemPosition();
		switch (v.getId()) {
		case R.id.graph_header_textview_left:
			j--;
			break;

		case R.id.graph_header_textview_right:
			j++;
			break;
		default:
			break;
		}
		graphGallery.setSelection(j);
	}
	
}
