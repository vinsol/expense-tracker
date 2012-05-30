/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;

public class SetPreferences extends PreferenceActivity {
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getWindow().setBackgroundDrawableResource(R.color.white);
		getListView().setBackgroundColor(Color.TRANSPARENT);
		getListView().setCacheColorHint(Color.TRANSPARENT);
		final CheckBoxPreference mCheckBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.pref_key_delete_dialog));
		mCheckBoxPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Checked ", ""+mCheckBoxPreference.isChecked());
				FlurryAgent.onEvent(getString(R.string.preference_delete_dialog),map);
				return true;
			}
		});
		
		Preference syncSetUpPref = findPreference(getString(R.string.pref_key_set_up_sync));
		syncSetUpPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SetPreferences.this);
				final View view = getLayoutInflater().inflate(R.layout.pref_sync_dialog, null); 
				builder.setView(view);
				builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferencesHelper.setSyncPrefs(((EditText)view.findViewById(R.id.sync_email)).getText().toString(), ((EditText)view.findViewById(R.id.sync_password)).getText().toString());
					}
				});
				builder.setNegativeButton(getString(R.string.cancel), (OnClickListener)null);
				builder.show();
				return false;
			}
		});
	}
	
}
