/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

public class DisplayTimeForChronometer {

	// ////// ******** Function which take time in millis and return time in 00:00 format ******* ////////
	public String getDisplayTime(long timeinmillis) {
		String minutes = "00";
		if (timeinmillis >= 60000) {
			Long temp = timeinmillis / 60000;
			if (temp < 10) {
				minutes = "0" + temp;
			} else {
				minutes = temp + "";
			}
		}
		String seconds = (timeinmillis % 60000) / 1000 + "";
		if ((timeinmillis % 60000) / 1000 < 10) {
			seconds = "0" + seconds;
		}
		return minutes + ":" + seconds;
	}

}
