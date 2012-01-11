/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class GetArrayListFromString {
	public ArrayList<String> getListFromTextArea(String textAreaValue) {
	       ArrayList<String> list = new ArrayList<String>();
	       StringTokenizer tokens = new StringTokenizer(textAreaValue,",");
	       while(tokens.hasMoreTokens()) {
	           list.add((String) tokens.nextElement());
	       }
	       return list;
	   }
}
