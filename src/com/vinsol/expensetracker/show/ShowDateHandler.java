/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import java.util.Calendar;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import android.app.Activity;
import android.widget.TextView;

public class ShowDateHandler {
	private TextView showHeaderTitle;

	public ShowDateHandler(Activity activity, Long timeInMillis) {
		showHeaderTitle = (TextView) activity.findViewById(R.id.header_title);
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(timeInMillis);
		String date = getDate(mCalendar);
		DisplayDate mDisplayDate = new DisplayDate(mCalendar);
		showHeaderTitle.setText(mDisplayDate.getDisplayDate()+" at "+date);
	}

	public ShowDateHandler(Activity activity,int typeOfEntry) {
		showHeaderTitle = (TextView) activity.findViewById(R.id.header_title);
		showHeaderTitle.setText(activity.getString(typeOfEntry));
	}
	
	public ShowDateHandler(Activity activity,TextView resource ,Long timeInMillis) {
		showHeaderTitle = resource;
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(timeInMillis);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		String date = getDate(mCalendar);
		DisplayDate mDisplayDate = new DisplayDate(mCalendar);
		showHeaderTitle.setText(mDisplayDate.getDisplayDate()+" at "+date);
	}

	public ShowDateHandler(Activity activity,TextView resource,int typeOfEntry) {
		showHeaderTitle = resource;
		showHeaderTitle.setText(activity.getString(typeOfEntry));
	}
	
	private String getDate(Calendar tempCalendar) {
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (hour == 0) {
			hour = 12;
		}
		if (tempCalendar.get(Calendar.MINUTE) != 0) {
			if (tempCalendar.get(Calendar.AM_PM) == 1) {
				return hour + ":" + minute + " " + "PM";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0) {
				return hour + ":" + minute + " " + "AM";
			}
		}
		else { 
			if (tempCalendar.get(Calendar.AM_PM) == 1) {
				return hour + "" + " " + "PM";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0) {
				return hour + "" + " " + "AM";
			}
		}
		return null;
	}
}
