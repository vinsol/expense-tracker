/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class ExpenseTrackerApplication extends Application {
	
	private static Context applicationContext; 
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory().toString(); 
	
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        Initialize();
    }
    
    private void Initialize() {
		File mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_AUDIO);
		mFile.mkdirs();
		mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + Constants.DIRECTORY_AUDIO);
		mFile.mkdirs();
	}

	public static Context getContext() {
    	return applicationContext;
    }
}