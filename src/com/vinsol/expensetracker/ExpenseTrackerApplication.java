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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;

public class ExpenseTrackerApplication extends Application {
	
	private static Context applicationContext; 
	public static String FILES_DIR ;
	
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        Initialize();
    }

	private void Initialize() {
    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		FILES_DIR = getExternalFilesDir(null).toString();
    		File mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_AUDIO);
    		mFile.mkdirs();
    		mFile = new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + Constants.DIRECTORY_AUDIO);
    		mFile.mkdirs();
    		if(!new SharedPreferencesHelper(applicationContext).getSharedPreferences().contains(getString(R.string.pref_key_run_first_time))) {
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
    			new SharedPreferencesHelper(applicationContext).setBooleanPrefs(R.string.pref_key_run_first_time, false);
    		}
    	} else {
    		Toast.makeText(applicationContext, "sdcard not available", Toast.LENGTH_LONG).show();
    	}
	}

	public static Context getContext() {
    	return applicationContext;
    }
	
	public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
	        
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
	
	void deleteDirectory(File fileOrDirectory) throws IOException{
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles()) { deleteDirectory(child);}
	    fileOrDirectory.delete();
	}

}