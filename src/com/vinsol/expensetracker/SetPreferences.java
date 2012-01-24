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
		setDeletePrefs();
		mCheckBoxPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	
			@Override
			public boolean onPreferenceClick(Preference preference) {
				setDeletePrefs();
				return true;
			}
		});
	}
	
	private void setDeletePrefs() {
		if(mCheckBoxPreference.isChecked()) {
			mCheckBoxPreference.setTitle(getString(R.string.delete_dialog_title_enable));
			mCheckBoxPreference.setSummary(getString(R.string.delete_dialog_summary_enable));
		} else {
			mCheckBoxPreference.setTitle(getString(R.string.delete_dialog_title_disable));
			mCheckBoxPreference.setSummary(getString(R.string.delete_dialog_summary_disable));
		}
	}
	
}
