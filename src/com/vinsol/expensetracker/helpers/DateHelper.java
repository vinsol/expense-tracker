/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import com.vinsol.expensetracker.utils.GetMonth;

public class DateHelper {
	Calendar mCalendar;

	public DateHelper(String dateViewString) {
		doCommonTask(dateViewString, null);
	}

	public DateHelper(String dateViewString, Calendar mCalendar2) {
		doCommonTask(dateViewString, mCalendar2);		
	}
	
	private void doCommonTask(String dateViewString, Calendar mCalendar2) {
		mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int year, month, day;
		if (dateViewString.contains("Today")) {
			year = mCalendar.get(Calendar.YEAR);
			dateViewString = (String) dateViewString.subSequence(7,dateViewString.length());
		} else {
			year = Integer.parseInt((String) dateViewString.subSequence(dateViewString.length() - 4, dateViewString.length()));
			dateViewString = (String) dateViewString.subSequence(0,dateViewString.length() - 6);
		}
		month = new GetMonth().getMonth((String) dateViewString.subSequence(0, 3));
		dateViewString = (String) dateViewString.subSequence(4,dateViewString.length());
		day = Integer.parseInt(dateViewString);
		if(mCalendar2 == null) {
			mCalendar.set(year, month, day);
		} else {
			mCalendar.set(year, month, day, mCalendar2.get(Calendar.HOUR_OF_DAY),mCalendar2.get(Calendar.MINUTE),mCalendar2.get(Calendar.SECOND));
		}
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}

	public long getTimeMillis() {
		return mCalendar.getTimeInMillis();
	}
}
