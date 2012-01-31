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
        intent = new Intent().setClass(this, ExpenseListingThisWeek.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("thisweek").setIndicator("This Week").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ExpenseListingThisMonth.class);
        spec = tabHost.newTabSpec("thismonth").setIndicator("This Month").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ExpenseListingThisYear.class);
        spec = tabHost.newTabSpec("thisyear").setIndicator("This Year").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ExpenseListingAll.class);
        spec = tabHost.newTabSpec("all").setIndicator("All").setContent(intent);
        tabHost.addTab(spec);
        
	}
	
}
