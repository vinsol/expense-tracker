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
        TabSpec spec;
        Intent intent;
        tabHost.setup();
        Bundle intentExtras = getIntent().getExtras();
        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent(this, ExpenseListingThisWeek.class);
        setExtras(intent, intentExtras);
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec(getString(R.string.tab_thisweek)).setIndicator("This Week").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent(this, ExpenseListingThisMonth.class);
        setExtras(intent, intentExtras);
        spec = tabHost.newTabSpec(getString(R.string.tab_thismonth)).setIndicator("This Month").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, ExpenseListingThisYear.class);
        setExtras(intent, intentExtras);
        spec = tabHost.newTabSpec(getString(R.string.tab_thisyear)).setIndicator("This Year").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, ExpenseListingAll.class);
        setExtras(intent, intentExtras);
        spec = tabHost.newTabSpec(getString(R.string.tab_all)).setIndicator("All").setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTabByTag(getTag(intentExtras));
	}
	
	private void setExtras(Intent intent, Bundle intentExtras) {
		if(intentExtras != null)
        	intent.putExtras(intentExtras);	
	}

	private String getTag(Bundle intentExtras) {
		if(intentExtras != null && intentExtras.containsKey(Constants.TIME_IN_MILLIS_TO_SET_TAB)) {
			Long timeInMillis = intentExtras.getLong(Constants.TIME_IN_MILLIS_TO_SET_TAB);
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
