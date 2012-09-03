/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Strings;

public class ExpenseTrackerApplication extends Application {
	
	private static Context applicationContext; 
	public static String FILES_DIR;
	public static boolean isInitialized = false;
	public static boolean toSync = false;
	
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    	PreferenceManager.setDefaultValues(applicationContext, R.xml.preferences, false);
    	setSyncPrefs();
        Initialize();
    }
    
    public static void setSyncPrefs() {
    	String token = SharedPreferencesHelper.getSharedPreferences().getString(getContext().getString(R.string.pref_key_token), "");
    	if(Strings.notEmpty(token)) {
    		toSync = true;
    	} else {
    		toSync = false;
    	}
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    	Log.d(preferences.getString(applicationContext.getString(R.string.pref_key_token), "not found"));
    	Log.d("******************************* Syncing syncing syncing **************************"+ExpenseTrackerApplication.toSync+" token "+token+" key "+applicationContext.getString(R.string.pref_key_token));
    }

	public static void Initialize() {
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		FILES_DIR = applicationContext.getExternalFilesDir(null).toString();
    		if(FILES_DIR != null) {
    			Constants.DIRECTORY = FILES_DIR + Constants.ET_FOLDER;
        		File mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_AUDIO);
        		mFile.mkdirs();
        		mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + Constants.DIRECTORY_AUDIO);
        		mFile.mkdirs();
        		if(!SharedPreferencesHelper.getSharedPreferences().contains(applicationContext.getString(R.string.pref_key_run_first_time))) {
        			File prevVerDir = new File(Environment.getExternalStorageDirectory()+"/ExpenseTracker");
        			if(prevVerDir.exists()) {
        				try {
        					copyDirectory(prevVerDir, new File(FILES_DIR+"/ExpenseTracker"));
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        				
        				try {
        					deleteDirectory(prevVerDir);
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        			SharedPreferencesHelper.setBooleanPrefs(R.string.pref_key_run_first_time, false);
        		}
        		isInitialized = true;
    		}
    	} else {
    		Toast.makeText(applicationContext, "sdcard not available", Toast.LENGTH_LONG).show();
    	}
	}

	public static Context getContext() {
    	return applicationContext;
    }
	
	private static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
	
	private static void deleteDirectory(File fileOrDirectory) throws IOException{
	    if (fileOrDirectory.isDirectory()) {
	        for (File child : fileOrDirectory.listFiles()) { deleteDirectory(child);}
	    }
	    fileOrDirectory.delete();
	}

}