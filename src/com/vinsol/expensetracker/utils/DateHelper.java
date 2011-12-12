package com.vinsol.expensetracker.utils;

import java.util.Calendar;

public class DateHelper {
	Calendar mCalendar;

	public DateHelper(String dateViewString) {
		mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int year, month, day;
		if (dateViewString.contains("Today")) {
			year = mCalendar.get(Calendar.YEAR);
			dateViewString = (String) dateViewString.subSequence(7,
					dateViewString.length());
		} else {
			year = Integer.parseInt((String) dateViewString.subSequence(
					dateViewString.length() - 4, dateViewString.length()));
			dateViewString = (String) dateViewString.subSequence(0,
					dateViewString.length() - 6);
		}
		month = getMonth((String) dateViewString.subSequence(0, 3));
		dateViewString = (String) dateViewString.subSequence(4,
				dateViewString.length());
		day = Integer.parseInt(dateViewString);
		mCalendar.set(year, month, day);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}

	public DateHelper(String dateViewString, Calendar mCalendar2) {
		mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int year, month, day;
		if (dateViewString.contains("Today")) {
			year = mCalendar.get(Calendar.YEAR);
			dateViewString = (String) dateViewString.subSequence(7,
					dateViewString.length());
		} else {
			year = Integer.parseInt((String) dateViewString.subSequence(
					dateViewString.length() - 4, dateViewString.length()));
			dateViewString = (String) dateViewString.subSequence(0,
					dateViewString.length() - 6);
		}
		month = getMonth((String) dateViewString.subSequence(0, 3));
		dateViewString = (String) dateViewString.subSequence(4,
				dateViewString.length());
		day = Integer.parseInt(dateViewString);
		mCalendar.set(year, month, day, mCalendar2.get(Calendar.HOUR_OF_DAY),
				mCalendar2.get(Calendar.MINUTE),
				mCalendar2.get(Calendar.SECOND));
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}

	public long getTimeMillis() {
		return mCalendar.getTimeInMillis();
	}

	private int getMonth(String i) {
		if (i.equals("Jan"))
			return 0;
		if (i.equals("Feb"))
			return 1;
		if (i.equals("Mar"))
			return 2;
		if (i.equals("Apr"))
			return 3;
		if (i.equals("May"))
			return 4;
		if (i.equals("Jun"))
			return 5;
		if (i.equals("Jul"))
			return 6;
		if (i.equals("Aug"))
			return 7;
		if (i.equals("Sep"))
			return 8;
		if (i.equals("Oct"))
			return 9;
		if (i.equals("Nov"))
			return 10;
		if (i.equals("Dec"))
			return 11;

		return 0;
	}

}
