package com.vinsol.expensetracker;

import android.app.Application;
import android.content.Context;

public class ExpenseTrackerApplication extends Application {
	
	private static Context applicationContext; 
	
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }
    
    public static Context getContext() {
    	return applicationContext;
    }
}