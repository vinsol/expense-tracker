/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

public class Log  {
	
    private static String app = "ExpenseTracker";
    private static boolean DEBUG = true;
    
    public static final void d(Throwable throwable) {
    	if (DEBUG)
    		android.util.Log.d(app, "", throwable);
    }

    public static final void d(Object object) {
    	if (DEBUG)
    		android.util.Log.d(app, object!=null ? object.toString() : null);
    }

    public static final void d(Object object, Throwable throwable) {
    	if (DEBUG)
    		android.util.Log.d(app, object!=null ? object.toString() : null, throwable);
    }
}