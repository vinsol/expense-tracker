/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import com.vinsol.expensetracker.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {

	private Context context;
	
	public SharedPreferencesHelper(Context context) {
		this.context = context;
	}
	
	public SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void setDeletePrefs(Boolean isChecked) {
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(context.getString(R.string.pref_key_delete_dialog), isChecked);
		prefEditor.commit();	
	}
	
}
