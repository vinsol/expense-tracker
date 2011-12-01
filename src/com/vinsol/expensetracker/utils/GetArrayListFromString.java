package com.vinsol.expensetracker.utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.util.Log;

public class GetArrayListFromString {
	public ArrayList<String> getListFromTextArea(String textAreaValue){
	       ArrayList<String> list = new ArrayList<String>();
	       StringTokenizer tokens = new StringTokenizer(textAreaValue,",");
	       while(tokens.hasMoreTokens()){
	           list.add((String) tokens.nextElement());
	       }
	       Log.v("list", list.toString());
	       return list;
	   }
}
