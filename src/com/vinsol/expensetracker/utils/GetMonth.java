/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

public class GetMonth {

	public int getMonth(String i) {
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
		return -1;
	}
}
