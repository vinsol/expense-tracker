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
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
=======
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vinsol.android.graph.BarGraph;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.GraphDataList;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
<<<<<<< HEAD
=======
import com.vinsol.expensetracker.titlepagerindicator.TitlePageIndicator;
import com.vinsol.expensetracker.titlepagerindicator.TitleProvider;
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
import com.vinsol.expensetracker.utils.Log;

public class GraphHelper extends AsyncTask<Void, Void, Void> {

	private List<ListDatetimeAmount> mDataDateListGraph;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Entry> mSubList;
	private ArrayList<ArrayList<ArrayList<String>>> mGraphList;
	private Calendar lastDateCalendar;
	private Activity activity;
<<<<<<< HEAD
	private int j = 0;
	private LinearLayout mainGraph ;
	private LinearLayout.LayoutParams params ;
	private ProgressBar graphProgressBar;
	private Gallery graphGallery;
	private GalleryAdapter galleryAdapter;
//	private LinearLayout graphContainer;
	private TextView graphHeaderTextView;
=======
//	private int j = 0;
//	private LinearLayout mainGraph ;
//	private LinearLayout.LayoutParams params ;
//	private ProgressBar graphProgressBar;
	private TitlePageIndicator graphTitleIndicator;
//	private ImageAdapter imageAdapter;
	private ViewPager graphViewPager;
	private GraphPagerAdapter pagerAdapter;
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
	
	public GraphHelper(Activity activity,ProgressBar graphProgressBar) {
		this.activity = activity;
		lastDateCalendar = Calendar.getInstance();
<<<<<<< HEAD
		galleryAdapter = new GalleryAdapter();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mainGraph = (LinearLayout) activity.findViewById(R.id.main_graph);
		this.graphProgressBar = graphProgressBar;
		graphGallery = (Gallery) activity.findViewById(R.id.graph_gallery);
		graphHeaderTextView = (TextView) activity.findViewById(R.id.graph_header_textview);
//		graphContainer = (LinearLayout) activity.findViewById(R.id.graph_container);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,mainGraph.getBackground().getIntrinsicHeight());
		graphGallery.setSpacing(0);
		graphGallery.setFadingEdgeLength(0);
		
		
=======
//		imageAdapter = new ImageAdapter();
		pagerAdapter = new GraphPagerAdapter();
		lastDateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
//		mainGraph = (LinearLayout) activity.findViewById(R.id.graph_main);
//		graphProgressBar = (ProgressBar) activity.findViewById(R.id.graph_progress_bar);
		graphTitleIndicator = (TitlePageIndicator) activity.findViewById(R.id.graph_title_indicator);
		graphViewPager = (ViewPager) activity.findViewById(R.id.graph_viewpager);
		
//		graphGallery = (Gallery) activity.findViewById(R.id.graph_gallery);
//		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,mainGraph.getBackground().getIntrinsicHeight());
	}
	
	@Override
	protected void onPreExecute() {
//		graphProgressBar.setVisibility(View.VISIBLE);
		super.onPreExecute();
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
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
<<<<<<< HEAD
		graphProgressBar.setVisibility(View.GONE);
		mainGraph.setVisibility(View.GONE);
        graphGallery.setVisibility(View.VISIBLE);
        graphGallery.setAdapter(galleryAdapter);
        graphGallery.setSelection(galleryAdapter.getCount() - 1);
        graphGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
				graphHeaderTextView.setText(mDataDateListGraph.get(mDataDateListGraph.size() - position - 1).dateTime);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
				graphHeaderTextView.setText("");
			}
		});
//        graphGallery.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Log.d("Graph Horizontal Position "+event.getX());
//				graphHeaderTextView.setText(mDataDateListGraph.get(graphGallery.getSelectedItemPosition()).dateTime);
//				return false;
//			}
//		});
=======
//		graphProgressBar.setVisibility(View.GONE);
//		mainGraph.setVisibility(View.GONE);
		graphViewPager.setAdapter(pagerAdapter);
		graphTitleIndicator.setViewPager(graphViewPager);
//        graphGallery.setVisibility(View.VISIBLE);
//        graphGallery.setAdapter(imageAdapter);
//        graphGallery.setSelection(imageAdapter.getCount() - 1);
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
		super.onPostExecute(result);
	}
	
	private TextView graphNoItem(RelativeLayout.LayoutParams params) {
		TextView graphNoItem = new TextView(activity);
		graphNoItem.setGravity(Gravity.CENTER);
		graphNoItem.setText("No Items to Show");
		graphNoItem.setTextColor(Color.BLACK);
		graphNoItem.setPadding(0, 0, 0, 15);
		graphNoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		graphNoItem.setLayoutParams(params);
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
	
	
<<<<<<< HEAD
	public class GalleryAdapter extends BaseAdapter {

	    @Override
	    public int getCount() {
	    	if(mDataDateListGraph != null && mDataDateListGraph.size() > 0 )
=======
private class GraphPagerAdapter extends PagerAdapter implements TitleProvider{

		@Override
		public int getCount() {
			if(mDataDateListGraph.size() > 0 )
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
	    		return mDataDateListGraph.size();
	    	else 
	    		return 1;
		}

	    /**
	     * Create the page for the given position.  The adapter is responsible
	     * for adding the view to the container given here, although it only
	     * must ensure this is done by the time it returns from
	     * {@link #finishUpdate()}.
	     *
	     * @param container The containing View in which the page will be shown.
	     * @param position The page position to be instantiated.
	     * @return Returns an Object representing the new page.  This does not
	     * need to be a View, but can be some other container of the page.
	     */
		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			GraphViewHolder graphViewHolder;
			LinearLayout.LayoutParams paramsLinearLayout = null;
			RelativeLayout.LayoutParams paramsRelativeLayout = null;
//	    	if(convertView == null) {
	    		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		ViewGroup convertView = (ViewGroup) inflater.inflate(R.layout.graph, collection);
	    		graphViewHolder = new GraphViewHolder();
<<<<<<< HEAD
	    		convertView.setTag(graphViewHolder);
	    	} else {
	    		graphViewHolder = (GraphViewHolder) convertView.getTag();
	    	}
//	    	graphViewHolder.grahHeaderTextView = (TextView) convertView.findViewById(R.id.graph_header_textview);
	    	graphViewHolder.graphMainView = (LinearLayout) convertView.findViewById(R.id.main_graph);
=======
		    	graphViewHolder.graphMainView = (LinearLayout) convertView.findViewById(R.id.graph_main);
	    		paramsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,graphViewHolder.graphMainView.getBackground().getIntrinsicHeight());
	    		paramsRelativeLayout = new RelativeLayout.LayoutParams(paramsLinearLayout.width, paramsLinearLayout.height);
//	    		convertView.setTag(graphViewHolder);
//	    	} else {
//	    		graphViewHolder = (GraphViewHolder) convertView.getTag();
//	    	}
//	    		convertView.setLayoutParams(paramsLinearLayout);
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
	    	if(mGraphList != null && mGraphList.size() > 0) {
			    graphViewHolder.barGraph = new BarGraph(activity, mGraphList.get(position).get(1), mGraphList.get(position).get(2));
			    if(position == mDataDateListGraph.size() && !isNotNullAll(mGraphList.get(position).get(0))) {
			    	graphViewHolder.graphMainView.addView(graphNoItem(paramsRelativeLayout), paramsLinearLayout);
				} else {
					graphViewHolder.graphMainView.addView(graphViewHolder.barGraph, paramsLinearLayout);
				}
<<<<<<< HEAD
//			    graphViewHolder.grahHeaderTextView.setText(mDataDateListGraph.get(j).dateTime);
		    	return convertView;
	    	} else {
//	    		graphViewHolder.grahHeaderTextView.setText("");
	    		graphViewHolder.graphMainView.addView(graphNoItem(), params);
	    		return convertView;
=======
			    ((ViewPager) collection).addView(convertView);
		    	return graphViewHolder.graphMainView;
	    	} else {
	    		graphViewHolder.graphMainView.addView(graphNoItem(paramsRelativeLayout), paramsLinearLayout);
	    		((ViewPager) collection).addView(convertView);
	    		return graphViewHolder.graphMainView;
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
	    	}
		}

	    /**
	     * Remove a page for the given position.  The adapter is responsible
	     * for removing the view from its container, although it only must ensure
	     * this is done by the time it returns from {@link #finishUpdate()}.
	     *
	     * @param container The containing View from which the page will be removed.
	     * @param position The page position to be removed.
	     * @param object The same object that was returned by
	     * {@link #instantiateItem(View, int)}.
	     */
		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==(object);
		}

	    /**
	     * Called when the a change in the shown pages has been completed.  At this
	     * point you must ensure that all of the pages have actually been added or
	     * removed from the container as appropriate.
	     * @param container The containing View which is displaying this adapter's
	     * page views.
	     */
		@Override
		public void finishUpdate(View arg0) {}
		

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}

		@Override
		public String getTitle(int position) {
			return mDataDateListGraph.get(position).dateTime;
		}
    	
    }
	
//	public class ImageAdapter extends BaseAdapter {
//
//	    @Override
//	    public int getCount() {
//	    	if(mDataDateListGraph.size() > 0 )
//	    		return mDataDateListGraph.size();
//	    	else 
//	    		return 1;
//	    }
//
//	    @Override
//	    public Object getItem(int position) {
//	        return position;
//	    }
//
//	    @Override
//	    public long getItemId(int position) {
//	        return position;
//	    }
//
//	    @Override
//	    public View getView(int position, View convertView, ViewGroup parent) {
//	    	GraphViewHolder graphViewHolder;
//	    	if(convertView == null) {
//	    		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    		convertView = inflater.inflate(R.layout.graph, null);
//	    		graphViewHolder = new GraphViewHolder();
//	    		convertView.setTag(graphViewHolder);
//	    	} else {
//	    		graphViewHolder = (GraphViewHolder) convertView.getTag();
//	    	}
//	    	graphViewHolder.grahHeaderTextView = (TextView) convertView.findViewById(R.id.graph_header_textview);
//	    	graphViewHolder.graphMainView = (LinearLayout) convertView.findViewById(R.id.graph);
//	    	if(mGraphList != null && mGraphList.size() > 0) {
//	    		j = mDataDateListGraph.size() - position - 1;
//			    graphViewHolder.barGraph = new BarGraph(activity, mGraphList.get(j).get(1), mGraphList.get(j).get(2));
//			    if(j == 0 && !isNotNullAll(mGraphList.get(j).get(0))) {
//			    	graphViewHolder.graphMainView.addView(graphNoItem(), params);
//				} else {
//					graphViewHolder.graphMainView.addView(graphViewHolder.barGraph, params);
//				}
//			    graphViewHolder.grahHeaderTextView.setText(mDataDateListGraph.get(j).dateTime);
//		    	return convertView;
//	    	} else {
//	    		graphViewHolder.grahHeaderTextView.setText("");
//	    		graphViewHolder.graphMainView.addView(graphNoItem(), params);
//	    		return convertView;
//	    	}
//	    }
//	    
//	}
	
	
	private class GraphViewHolder {
<<<<<<< HEAD
//		TextView grahHeaderTextView;
=======
>>>>>>> ef1680a456776c29b13cd46ae8c6258806df98b0
		BarGraph barGraph;
		LinearLayout graphMainView;
	}
	
	
}
