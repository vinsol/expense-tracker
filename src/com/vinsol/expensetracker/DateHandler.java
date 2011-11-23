package com.vinsol.expensetracker;

import java.util.Calendar;
import com.vinsol.expensetracker.utils.DisplayDate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DateHandler implements OnClickListener{

	private ImageButton date_bar_previous_arrow;
	private ImageButton date_bar_next_arrow;
	private TextView date_bar_dateview;
	private DisplayDate mDisplayDate;
	private Calendar mCalendar;
	private Activity activity;
	private DatePickerDialog dialog;
	static Calendar tempCalenderOnCancel;
	
	protected DateHandler(Context mContext) {
		activity = (mContext instanceof Activity) ? (Activity)mContext : null;
		mCalendar = Calendar.getInstance();
		mDisplayDate = new DisplayDate(mCalendar);
		date_bar_dateview = (TextView) activity.findViewById(R.id.text_voice_camera_date_bar_dateview);
		date_bar_previous_arrow = (ImageButton) activity.findViewById(R.id.text_voice_camera_date_bar_previous_arrow);
		date_bar_next_arrow = (ImageButton) activity.findViewById(R.id.text_voice_camera_date_bar_next_arrow);
		date_bar_next_arrow.setVisibility(View.INVISIBLE);
		date_bar_dateview.setText(mDisplayDate.getDisplayDate());
		dialog = new DatePickerDialog(mContext , date_bar_dateview );
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
				date_bar_next_arrow.setVisibility(View.INVISIBLE);
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
			
			
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					if(tempCalenderOnCancel != null){
						if(beforeCurrentDate(tempCalenderOnCancel)){
							date_bar_next_arrow.setVisibility(View.VISIBLE);
						} else {
							date_bar_next_arrow.setVisibility(View.INVISIBLE);
						}
						mCalendar.setTime(tempCalenderOnCancel.getTime());
						tempCalenderOnCancel = null;
					}
				}
			});
			dialog.show();
		}
		
	}

	private boolean beforeCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		if(mCalendar.get(Calendar.YEAR) > pCalendar.get(Calendar.YEAR)){
			return true;
		} else if((mCalendar.get(Calendar.MONTH) > pCalendar.get(Calendar.MONTH))){
			return true;
		} else if((mCalendar.get(Calendar.DAY_OF_MONTH) > pCalendar.get(Calendar.DAY_OF_MONTH))){
			return true;
		} else {
			return false;
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
