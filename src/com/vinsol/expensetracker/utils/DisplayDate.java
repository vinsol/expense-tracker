package com.vinsol.expensetracker.utils;

import java.util.Calendar;

public class DisplayDate {
	
	/////////    *******    Class to pass Calender and get date in display format  *******   ////////
	Calendar mCalendar;
	
	public DisplayDate(Calendar calendar) {
		mCalendar = calendar;
	}
	
	////////    ********    Function to get date in proper format to display in various activities ****** ///////
	public String getDisplayDate(){
    	String month,day,year;
    	month = getMonth(mCalendar.get(Calendar.MONTH));
    	day = mCalendar.get(Calendar.DAY_OF_MONTH)+"";
    	year = mCalendar.get(Calendar.YEAR)+"";
    	Calendar currentDate = Calendar.getInstance();
    	if(Integer.parseInt(day) == currentDate.get(Calendar.DAY_OF_MONTH) && 
    			mCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
    			Integer.parseInt(year) == currentDate.get(Calendar.YEAR)){
    		
    		return "Today, "+month+" "+day;
    	}
    	return month+" "+day+", "+year;
    }

	
	////////   *******   Function which returns month as string  ********    ///////////
	private String getMonth(int i) {
		switch(i){
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
}
