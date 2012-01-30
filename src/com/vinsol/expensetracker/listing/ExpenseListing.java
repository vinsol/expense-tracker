/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.vinsol.expensetracker.R;

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
        
        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, ExpenseListingToday.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("today").setIndicator("Today").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ExpenseListingWeekly.class);
        spec = tabHost.newTabSpec("weekly").setIndicator("Weekly").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ExpenseListingMonthly.class);
        spec = tabHost.newTabSpec("monthly").setIndicator("Monthly").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ExpenseListingYearly.class);
        spec = tabHost.newTabSpec("yearly").setIndicator("Yearly").setContent(intent);
        tabHost.addTab(spec);
        
	}
	
}
