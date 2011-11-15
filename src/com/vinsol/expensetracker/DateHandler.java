package com.vinsol.expensetracker;

import java.util.Calendar;
import com.vinsol.expensetracker.utils.DisplayDate;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DateHandler implements OnClickListener{

	ImageButton date_bar_previous_arrow;
	ImageButton date_bar_next_arrow;
	TextView date_bar_dateview;
	DisplayDate mDisplayDate;
	Calendar mCalendar;
	Activity activity;
	
	protected DateHandler(Context mContext) {
		activity = (mContext instanceof Activity) ? (Activity)mContext : null;
		mCalendar = Calendar.getInstance();
		mDisplayDate = new DisplayDate(mCalendar);
		date_bar_dateview = (TextView) activity.findViewById(R.id.text_voice_camera_date_bar_dateview);
		date_bar_previous_arrow = (ImageButton) activity.findViewById(R.id.text_voice_camera_date_bar_previous_arrow);
		date_bar_next_arrow = (ImageButton) activity.findViewById(R.id.text_voice_camera_date_bar_next_arrow);
		date_bar_next_arrow.setVisibility(View.GONE);
		date_bar_dateview.setText(mDisplayDate.getDisplayDate());
		date_bar_previous_arrow.setOnClickListener(this);
		date_bar_next_arrow.setOnClickListener(this);
		date_bar_dateview.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.text_voice_camera_date_bar_next_arrow){
			mCalendar.add(Calendar.DATE, 1);
			mDisplayDate = new DisplayDate(mCalendar);
			date_bar_dateview.setText(mDisplayDate.getDisplayDate());
			
			if(isCurrentDate(mCalendar)){
				date_bar_next_arrow.setVisibility(View.GONE);
			}
		}
		
		if(v.getId() == R.id.text_voice_camera_date_bar_previous_arrow){
			mCalendar.add(Calendar.DATE, -1);
			mDisplayDate = new DisplayDate(mCalendar);
			if(!date_bar_next_arrow.isShown()){
				date_bar_next_arrow.setVisibility(View.VISIBLE);
			}
			date_bar_dateview.setText(mDisplayDate.getDisplayDate());
		}
		
		if(v.getId() == R.id.text_voice_camera_date_bar_dateview){
			
		}
		
	}

	private boolean isCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		if((mCalendar.get(Calendar.MONTH) == pCalendar.get(Calendar.MONTH)) && 
				(mCalendar.get(Calendar.YEAR) == pCalendar.get(Calendar.YEAR)) &&
				(mCalendar.get(Calendar.DAY_OF_MONTH) == pCalendar.get(Calendar.DAY_OF_MONTH))){
			return true;
		}
		return false;
	}
	
}
