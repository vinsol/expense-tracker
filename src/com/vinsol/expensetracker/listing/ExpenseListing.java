/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class ExpenseListing extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_listing_tab);
		setTab();
	}
	
	private void setTab() {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        Bundle intentExtras = getIntent().getExtras();
        
        // Create an Intent to launch an Activity for the tab (to be reused)
        
        Intent intentThisWeek = new Intent(this, ExpenseListingThisWeek.class);
        Intent intentThisMonth = new Intent(this, ExpenseListingThisMonth.class);
        Intent intentThisYear = new Intent(this, ExpenseListingThisYear.class);
        Intent intentAll = new Intent(this, ExpenseListingAll.class);
        setExtras(tabHost, intentThisWeek, intentThisMonth, intentThisYear, intentAll, intentExtras);
        // Initialize a TabSpec for each tab and add it to the TabHost
        TabSpec tabThisWeek = tabHost.newTabSpec(getString(R.string.tab_thisweek)).setIndicator("This Week").setContent(intentThisWeek);
        TabSpec tabThisMonth = tabHost.newTabSpec(getString(R.string.tab_thismonth)).setIndicator("This Month").setContent(intentThisMonth);
        TabSpec tabThisYear = tabHost.newTabSpec(getString(R.string.tab_thisyear)).setIndicator("This Year").setContent(intentThisYear);
        TabSpec tabAll = tabHost.newTabSpec(getString(R.string.tab_all)).setIndicator("All").setContent(intentAll);
        tabHost.addTab(tabThisWeek);
        tabHost.addTab(tabThisMonth);
        tabHost.addTab(tabThisYear);
        tabHost.addTab(tabAll);
        tabHost.setCurrentTabByTag(getTag(intentExtras));
	}

	private void setExtras(TabHost tabHost, Intent intentThisWeek, Intent intentThisMonth, Intent intentThisYear, Intent intentAll, Bundle intentExtras) {
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB)) {
			Long timeInMillis = intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeInMillis);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			DisplayDate displayDate = new DisplayDate(calendar);
			if(displayDate.isCurrentWeek()) {
				intentThisWeek.putExtras(intentExtras);
				return;
			} else if (displayDate.isCurrentMonth()){
				intentThisMonth.putExtras(intentExtras);
				return;
			} else if (displayDate.isCurrentYear()){
				intentThisYear.putExtras(intentExtras);
				return;
			} else {
				intentAll.putExtras(intentExtras);
				return;
			}
		}
	}

	private String getTag(Bundle intentExtras) {
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB)) {
			Long timeInMillis = intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeInMillis);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			DisplayDate displayDate = new DisplayDate(calendar);
			if(displayDate.isCurrentWeek()) {
				return getString(R.string.tab_thisweek);
			} else if (displayDate.isCurrentMonth()){
				return getString(R.string.tab_thismonth);
			} else if (displayDate.isCurrentYear()){
				return getString(R.string.tab_thisyear);
			} else {
				return getString(R.string.tab_all);
			}
		} else {
			return getString(R.string.tab_thisweek);
		}
	}
	
}
