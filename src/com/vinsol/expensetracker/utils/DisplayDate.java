package com.vinsol.expensetracker.utils;

import java.util.Calendar;

public class DisplayDate {
	
	/////////    *******    Class to pass Calender and get date in display format  *******   ////////
	Calendar mCalendar;
	
	public DisplayDate(){
		
	}
	
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

//	public long getDisplayDateinMillisStart(String date){
//		Calendar calendar = Calendar.getInstance();
//		int year;
//		if(date.contains("Today")){
//			year = calendar.get(Calendar.YEAR);
//			date = date.subSequence(7, date.length()).toString();
//		} else {
//			year = Integer.parseInt(date.subSequence(date.length()-4, date.length()).toString());
//			date = date.subSequence(0, date.length() - 6 ).toString();
//		}
//		
//		Log.v("as", date.subSequence(0, 3).toString());
//		int month = getMonthReverse(date.subSequence(0, 3).toString());
//		date = date.subSequence(4, date.length()).toString();
//		Log.v("date", date+" "+year+" "+month);
//		int day = Integer.parseInt(date);
////		getMonthReverse(i);
//		calendar.set(year, month, day, 0, 0, 0);
//		
//		Log.v("Calender", calendar.getTimeInMillis()+" "+" year "+calendar.get(Calendar.YEAR)+" month "+calendar.get(Calendar.MONTH)+" day "+calendar.get(Calendar.DAY_OF_MONTH)+" hour "+calendar.get(Calendar.HOUR)+" min "+calendar.get(Calendar.MINUTE)+" sec "+calendar.get(Calendar.SECOND));
//		
//		return calendar.getTimeInMillis();
//	}
	
//	public long getDisplayDateinMillisEnd(String date){
//		Calendar calendar = Calendar.getInstance();
//		int year;
//		if(date.contains("Today")){
//			year = calendar.get(Calendar.YEAR);
//			date = date.subSequence(7, date.length()).toString();
//		} else {
//			year = Integer.parseInt(date.subSequence(date.length()-4, date.length()).toString());
//			date = date.subSequence(0, date.length() - 6 ).toString();
//		}
//		
//		Log.v("as", date.subSequence(0, 3).toString());
//		int month = getMonthReverse(date.subSequence(0, 3).toString());
//		date = date.subSequence(4, date.length()).toString();
//		Log.v("date", date+" "+year+" "+month);
//		int day = Integer.parseInt(date);
////		getMonthReverse(i);
//		calendar.set(year, month, day, 0, 0, 0);
//		calendar.add(Calendar.DATE, -1);
//		calendar.add(Calendar.MILLISECOND, 1);
//		Log.v("Calender", calendar.getTimeInMillis()+" "+" year "+calendar.get(Calendar.YEAR)+" month "+calendar.get(Calendar.MONTH)+" day "+calendar.get(Calendar.DAY_OF_MONTH)+" hour "+calendar.get(Calendar.HOUR)+" min "+calendar.get(Calendar.MINUTE)+" sec "+calendar.get(Calendar.SECOND));
//		
//		return calendar.getTimeInMillis();
//	}
	
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
	
//	////////*******   Function which returns month as int  ********    ///////////
//	private int getMonthReverse(String i) {
//		if(i.equals("Jan"))
//			return 0;
//		if(i.equals("Feb"))
//			return 1;
//		if(i.equals("Mar"))
//			return 2;
//		if(i.equals("Apr"))
//				return 3;
//		if(i.equals("May"))
//				return 4;
//		if(i.equals("Jun"))
//				return 5;
//		if(i.equals("Jul"))
//				return 6;
//		if(i.equals("Aug"))
//				return 7;
//		if(i.equals("Sep"))
//				return 8;
//		if(i.equals("Oct"))
//				return 9;
//		if(i.equals("Nov"))
//				return 10;
//		if(i.equals("Dec"))
//				return 11;
//		
//		return -1;
//	
//}
}
