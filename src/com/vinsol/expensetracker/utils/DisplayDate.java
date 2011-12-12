package com.vinsol.expensetracker.utils;

import java.util.Calendar;

public class DisplayDate {

	// /////// ******* Class to pass Calender and get date in display format
	// ******* ////////
	Calendar mCalendar;

	public DisplayDate() {

	}

	public DisplayDate(Calendar calendar) {
		mCalendar = calendar;
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
	public Calendar getCalendar(){
		return mCalendar;
	}

	// ////// ******** Function to get date in proper format to display in
	// various activities ****** ///////
	public String getDisplayDate() {
		String month, day, year;
		month = getMonth(mCalendar.get(Calendar.MONTH));
		day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
		year = mCalendar.get(Calendar.YEAR) + "";
		Calendar currentDate = Calendar.getInstance();
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		if (Integer.parseInt(day) == currentDate.get(Calendar.DAY_OF_MONTH)&& mCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)&& Integer.parseInt(year) == currentDate.get(Calendar.YEAR)) {
			return "Today, " + month + " " + day;
		}

		return month + " " + day + ", " + year;
	}

	// //////******** Function to get date in proper format to display in
	// various activities ****** ///////
	public String getHeaderFooterListDisplayDate() {
		String month, day, year;
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		month = getMonth(mCalendar.get(Calendar.MONTH));
		day = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
		year = mCalendar.get(Calendar.YEAR) + "";
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH),0,0,0);
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		if (Integer.parseInt(day) == currentDate.get(Calendar.DAY_OF_MONTH)
				&& mCalendar.get(Calendar.MONTH) == currentDate
						.get(Calendar.MONTH)
				&& Integer.parseInt(year) == currentDate.get(Calendar.YEAR)) {
			return "Today, " + month + " " + day;
		}

		if (isCurrentWeek()) {
			return month + " " + day + ", " + year;
		}

		if (isPrevMonths() || isCurrentMonth()) {
			return month + " " + mCalendar.get(Calendar.YEAR);
		}

		if (isPrevYears()) {
			return mCalendar.get(Calendar.YEAR) + "";
		}
		return null;
	}
	
	public String getDisplayDateGraph() {
		String month, year;
		month = getMonth(mCalendar.get(Calendar.MONTH));
		year = mCalendar.get(Calendar.YEAR) + "";
		
		if(isCurrentMonth()){
			return getDisplayDate();
		}
		
		if (isPrevMonths() || isPrevYears()) {
			return "Week "+mCalendar.get(Calendar.WEEK_OF_MONTH)+", "+month+" "+year;
		}
		
		return null;
	}
	
	public String getDisplayDateHeaderGraph() {
		String month, year;
		month = getMonth(mCalendar.get(Calendar.MONTH));
		year = mCalendar.get(Calendar.YEAR) + "";
		
		if(isCurrentMonth()){
			return "Week "+mCalendar.get(Calendar.WEEK_OF_MONTH)+", "+month+" "+year;
		}
		
		if (isPrevMonths() || isPrevYears()) {
			return month + " " + year;
		}
		
		return year;
	}

	public boolean isPrevYears() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (mTempCalendar.get(Calendar.YEAR) > mCalendar.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}

	public boolean isPrevMonths() {
		Calendar mTempCalender = Calendar.getInstance();
		mTempCalender.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mTempCalender.get(Calendar.MONTH) > mCalendar.get(Calendar.MONTH)) && (mTempCalender.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	public boolean isCurrentMonth() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mTempCalendar.get(Calendar.MONTH) == mCalendar.get(Calendar.MONTH)) && (mTempCalendar.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	public boolean isCurrentWeek() {
		Calendar mTempCalendar = Calendar.getInstance();
		mTempCalendar.set(mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
		mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		
		if ((mTempCalendar.get(Calendar.WEEK_OF_MONTH) == mCalendar.get(Calendar.WEEK_OF_MONTH))
				&& (mTempCalendar.get(Calendar.MONTH) == mCalendar.get(Calendar.MONTH))
				&& (mTempCalendar.get(Calendar.YEAR) == mCalendar.get(Calendar.YEAR))) {
			return true;
		}
		return false;
	}

	// ////// ******* Function which returns month as string ********
	// ///////////
	private String getMonth(int i) {
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

	public String getSubListTag() {

		if (isCurrentMonth()) {
			return "Week " + mCalendar.get(Calendar.WEEK_OF_MONTH);
		}

		if (isPrevMonths()) {
			return "Week " + mCalendar.get(Calendar.WEEK_OF_MONTH);
		}

		if (isPrevYears()) {
			return getMonth(mCalendar.get(Calendar.MONTH)) + " "
					+ mCalendar.get(Calendar.YEAR) + "";
		}
		return null;
	}
	
	public String getLocationDateDate(String dateInMillis) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (hour == 0) {
			hour = 12;
		}
		if (tempCalendar.get(Calendar.MINUTE) != 0){
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + ":" + minute + " " + "PM"+ " at Unknown location";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + minute + " " + "AM" + " at Unknown location";
			}
		}
		else{ 
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + "" + " " + "PM" + " at Unknown location";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + "" + " " + "AM" + " at Unknown location";
			}
		}
		return null;
	}

	public String getLocationDate(String dateInMillis, String locationData) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (hour == 0) {
			hour = 12;
		}
		if (tempCalendar.get(Calendar.MINUTE) != 0){
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + ":" + minute + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + minute + " " + "AM" + " at " + locationData;
			}
		}
		else{
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + "" + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + " " + "AM" + " at " + locationData;
			}
		}
		return null;
	}
}
