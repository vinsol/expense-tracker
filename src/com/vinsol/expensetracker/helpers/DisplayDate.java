/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import com.vinsol.expensetracker.R;

public class DisplayDate {

	// /////// ******* Class to pass Calender and get date in display format ******* ////////
	private Calendar mCalendar;

	public DisplayDate() {}

	public DisplayDate(Calendar calendar) {
		mCalendar = calendar;
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	public DisplayDate(Long timeInMillis) {
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(timeInMillis);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	public Calendar getCalendar() {
		return mCalendar;
	}

	// ////// ******** Function to get date in proper format to display in various activities ****** ///////
	public String getDisplayDate() {
		String month;
		int day, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		month = getMonth(mCalendar.get(Calendar.MONTH));
		day = mCalendar.get(Calendar.DAY_OF_MONTH);
		year = mCalendar.get(Calendar.YEAR);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		if (day == currentDate.get(Calendar.DAY_OF_MONTH)&& mCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)&& year == currentDate.get(Calendar.YEAR)) {
			return "Today, " + month + " " + day;
		}

		return month + " " + day + ", " + year;
	}
	
	//////// ******** Function to get date in proper format to display in various activities ****** ///////
	public String getDisplayDateReport(long timeInMillis) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(timeInMillis);
		return date(mCalendar);
	}
	
	////////******** Function to get date in proper format to display in various activities ****** ///////
	public String getDisplayDateReport(Calendar calendar) {
		return date(calendar);
	}
	
	public String getReportDateRange(int mStartDay, int mStartMonth,int mStartYear, int mEndDay, int mEndMonth, int mEndYear) {
		return getMonth(mStartMonth) + " " + mStartDay + ", " + mStartYear+" - "+getMonth(mEndMonth) + " " + mEndDay + ", " + mEndYear;
	}
	
	private String date(Calendar mCalendar) {
		String month, day, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		month = getMonth(mCalendar.get(Calendar.MONTH));
		day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
		year = mCalendar.get(Calendar.YEAR) + "";
		Calendar currentDate = Calendar.getInstance();
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		return month + " " + day + ", " + year;
	}

	// //////******** Function to get date in proper format to display in various activities ****** ///////
	public String getHeaderFooterListDisplayDate(int type) {
		String month, day, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		month = getMonth(mCalendar.get(Calendar.MONTH));
		day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
		year = mCalendar.get(Calendar.YEAR) + "";
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH),0,0,0);
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		switch (type) {
		
		case R.string.sublist_thisweek:
			if (Integer.parseInt(day) == currentDate.get(Calendar.DAY_OF_MONTH)
				&& mCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
				&& Integer.parseInt(year) == currentDate.get(Calendar.YEAR)) {
				return "Today, " + month + " " + day;
			} else {
				return month + " " + day + ", " + year;
			}
		case R.string.sublist_thismonth:
			return month + " " + mCalendar.get(Calendar.YEAR);
		case R.string.sublist_thisyear:
		case R.string.sublist_all:
			return mCalendar.get(Calendar.YEAR) + "";
		default:
			return "Week "+mCalendar.get(Calendar.WEEK_OF_MONTH)+", "+month+" "+year;
		}
	}
	
	public String getDisplayDateGraph() {
		String month, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		month = getMonth(mCalendar.get(Calendar.MONTH));
		year = mCalendar.get(Calendar.YEAR) + "";
		if(isCurrentMonth()) {
			return getDisplayDate();
		}
		
		if (isNotCurrentMonthAndCurrentYear() || isPrevYears()) {
			return "Week "+mCalendar.get(Calendar.WEEK_OF_MONTH)+", "+month+" "+year;
		}
		
		return null;
	}
	
	public String getDisplayDateHeaderGraph() {
		String month, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		month = getMonth(mCalendar.get(Calendar.MONTH));
		year = mCalendar.get(Calendar.YEAR) + "";
		if(isCurrentMonth()) {
			return "Week "+mCalendar.get(Calendar.WEEK_OF_MONTH)+", "+month+" "+year;
		}
		
		if (isNotCurrentMonthAndCurrentYear() || isPrevYears()) {
			return month + " " + year;
		}
		
		return year;
	}

	public boolean isPrevYears() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.set(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (mTempCalendar.get(Calendar.YEAR) > mCalendar.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}

	public boolean isNotCurrentMonthAndCurrentYear() {
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.set(mTempCalender.get(Calendar.YEAR), mTempCalender.get(Calendar.MONTH), mTempCalender.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mTempCalender.get(Calendar.MONTH) > mCalendar.get(Calendar.MONTH)) && (mTempCalender.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	public boolean isCurrentYear() {
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.set(mTempCalender.get(Calendar.YEAR), mTempCalender.get(Calendar.MONTH), mTempCalender.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (mTempCalender.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}
	
	public boolean isCurrentMonth() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.set(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mTempCalendar.get(Calendar.MONTH) == mCalendar.get(Calendar.MONTH)) && (mTempCalendar.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	public boolean isCurrentWeek() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.set(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mTempCalendar.get(Calendar.WEEK_OF_MONTH) == mCalendar.get(Calendar.WEEK_OF_MONTH))
				&& (mTempCalendar.get(Calendar.MONTH) == mCalendar.get(Calendar.MONTH))
				&& (mTempCalendar.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	//////// ******* Function which returns month as string ********///////////
	private static String getMonth(int i) {
		switch (i) {
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jul";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		}
		return null;
	}

	public String getSubListTag(int type) {
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		switch (type) {
		case R.string.sublist_thismonth:
			return "Week " + mCalendar.get(Calendar.WEEK_OF_MONTH);
		case R.string.sublist_thisyear:
			return getMonth(mCalendar.get(Calendar.MONTH)) + " " + mCalendar.get(Calendar.YEAR) + "";
		case R.string.sublist_all:
			return mCalendar.get(Calendar.YEAR) + "";
		default:
			return null;
		}
	}
	
	public String getLocationDate(Long timeInMillis, String locationData) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(timeInMillis);
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (hour == 0) {
			hour = 12;
		}
		if(locationData == null || locationData.equals("")) {
				locationData = "Unknown location";
		}
		if (tempCalendar.get(Calendar.MINUTE) != 0) {
			if (tempCalendar.get(Calendar.AM_PM) == 1) {
				return hour + ":" + minute + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0) {
				return hour + ":" + minute + " " + "AM" + " at " + locationData;
			}
		}
		else {
			if (tempCalendar.get(Calendar.AM_PM) == 1) {
				return hour + "" + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0) {
				return hour + ":" + " " + "AM" + " at " + locationData;
			}
		}
		return null;
	}
}
