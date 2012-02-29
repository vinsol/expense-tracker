/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SetPreferences extends PreferenceActivity {

	CheckBoxPreference mCheckBoxPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		mCheckBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_key_delete_dialog));
		mCheckBoxPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	
			@Override
			public boolean onPreferenceClick(Preference preference) {
				return true;
			}
		});
	}
	
}
