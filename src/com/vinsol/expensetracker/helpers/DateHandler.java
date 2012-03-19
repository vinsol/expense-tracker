/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.R;

public class DateHandler implements OnClickListener {

	private ImageButton previousArrow;
	private ImageButton nextArrow;
	private TextView dateview;
	private DisplayDate mDisplayDate;
	private Calendar mCalendar;
	private CustomDatePickerDialog dialog;
	public Calendar tempCalenderOnCancel;
	private Activity activity;

	public DateHandler(Activity activity) {
		mCalendar = Calendar.getInstance();
		doCommonTaskAfter(activity);
	}

	public DateHandler(Activity activity, long long1) {
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(long1);
		doCommonTaskAfter(activity);
	}
	
	private void doCommonTaskAfter(Activity activity) {
		this.activity = activity;
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mDisplayDate = new DisplayDate(mCalendar);
		dateview = (TextView) activity.findViewById(R.id.edit_date_bar_dateview);
		previousArrow = (ImageButton) activity.findViewById(R.id.edit_date_bar_previous_arrow);
		nextArrow = (ImageButton) activity.findViewById(R.id.edit_date_bar_next_arrow);
		if (!beforeCurrentDate(mCalendar)) {nextArrow.setVisibility(View.INVISIBLE);}
		dateview.setText(mDisplayDate.getDisplayDate());
		dialog = new CustomDatePickerDialog(activity, dateSetListener , dateview);
		previousArrow.setOnClickListener(this);
		nextArrow.setOnClickListener(this);
		dateview.setOnClickListener(this);
	}
	
	private CustomDatePickerDialog.OnDateSetListener dateSetListener = new CustomDatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			dateview.setText(new DisplayDate(mCalendar).getDisplayDate());
			tempCalenderOnCancel = Calendar.getInstance();
			tempCalenderOnCancel.setFirstDayOfWeek(Calendar.MONDAY);
			tempCalenderOnCancel.setTime(mCalendar.getTime());
		}
	};

	@Override
	public void onClick(View v) {
		Map<String, String> map = new HashMap<String, String>();
		switch (v.getId()) {
		
		case R.id.edit_date_bar_next_arrow:
			map.put("Using", "Next Arrow");
			FlurryAgent.onEvent(activity.getString(R.string.date_changed),map);
			mCalendar.add(Calendar.DATE, 1);
			mDisplayDate = new DisplayDate(mCalendar);
			dateview.setText(mDisplayDate.getDisplayDate());

			if (isCurrentDate(mCalendar)) {
				nextArrow.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.edit_date_bar_previous_arrow:
			map.put("Using", "Previous Arrow");
			FlurryAgent.onEvent(activity.getString(R.string.date_changed),map);
			mCalendar.add(Calendar.DATE, -1);
			mDisplayDate = new DisplayDate(mCalendar);
			if (!nextArrow.isShown()) {
				nextArrow.setVisibility(View.VISIBLE);
			}
			dateview.setText(mDisplayDate.getDisplayDate());
			break;
			
		case R.id.edit_date_bar_dateview:
			map.put("Using", "Date Picker Dialog");
			FlurryAgent.onEvent(activity.getString(R.string.date_changed),map);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (tempCalenderOnCancel != null) {
						if (beforeCurrentDate(tempCalenderOnCancel)) {
							nextArrow.setVisibility(View.VISIBLE);
						} else {
							nextArrow.setVisibility(View.INVISIBLE);
						}
						mCalendar.setTime(tempCalenderOnCancel.getTime());
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						tempCalenderOnCancel = null;
					}
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
	}

	private boolean beforeCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (mCalendar.get(Calendar.YEAR) > pCalendar.get(Calendar.YEAR)) {
			return true;
		} else if ((mCalendar.get(Calendar.MONTH) > pCalendar.get(Calendar.MONTH))) {
			return true;
		} else if ((mCalendar.get(Calendar.DAY_OF_MONTH) > pCalendar.get(Calendar.DAY_OF_MONTH))) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mCalendar.get(Calendar.MONTH) == pCalendar.get(Calendar.MONTH))&& (mCalendar.get(Calendar.YEAR) == pCalendar.get(Calendar.YEAR))&& (mCalendar.get(Calendar.DAY_OF_MONTH) == pCalendar.get(Calendar.DAY_OF_MONTH))) {
			return true;
		}
		return false;
	}
}
