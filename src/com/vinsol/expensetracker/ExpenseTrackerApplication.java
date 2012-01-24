/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker;

import java.io.File;

import com.flurry.android.FlurryAgent;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class ExpenseTrackerApplication extends Application {
	
	private static Context applicationContext; 
	public static String SDCARD_PATH ;
	public static SharedPreferences sharedPreferences;
	
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        Initialize();
    }
    
    private void Initialize() {
    	sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
    	SDCARD_PATH = getExternalCacheDir().toString();
		File mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_AUDIO);
		mFile.mkdirs();
		mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + Constants.DIRECTORY_AUDIO);
		mFile.mkdirs();
		FlurryAgent.setReportLocation(false);
	}

	public static Context getContext() {
    	return applicationContext;
    }
}