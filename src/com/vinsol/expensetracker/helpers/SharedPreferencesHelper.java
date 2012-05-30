/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {

	private static Context context = ExpenseTrackerApplication.getContext();
	
	public static SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static void setDeletePrefs(Boolean isChecked) {
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(context.getString(R.string.pref_key_delete_dialog), isChecked);
		prefEditor.commit();	
	}
	
	public static void setFlashPrefs(int resId) {
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(context.getString(R.string.pref_key_flash_res_id), resId);
		prefEditor.commit();	
	}
	
	public static void setBooleanPrefs(int keyId, boolean isFirstTime) {
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(context.getString(keyId), isFirstTime);
		prefEditor.commit();	
	}
	
	public static void setSyncPrefs(String email, String password) {
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putString(context.getString(R.string.pref_key_sync_email), email);
		prefEditor.putString(context.getString(R.string.pref_key_sync_password), password);
		prefEditor.commit();	
	}
	
}
