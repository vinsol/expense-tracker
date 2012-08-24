/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.expenselisting;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.UnfinishedEntryCount;

public class ExpenseListing extends TabActivity implements OnClickListener{

	private static UnfinishedEntryCount unfinishedEntryCount;
	private static ConvertCursorToListString mConvertCursorToListString;
	private static TextView unfinishedEntryCountAll;
	private static TextView unfinishedEntryCountThisWeek;
	private static TextView unfinishedEntryCountThisMonth;
	private static TextView unfinishedEntryCountThisYear;
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.onEvent(getString(R.string.expense_listing_activity));
		setContentView(R.layout.expense_listing_tab);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		unfinishedEntryCountAll = (TextView)findViewById(R.id.unfinished_count_all);
		unfinishedEntryCountThisWeek = (TextView)findViewById(R.id.unfinished_count_this_week);
		unfinishedEntryCountThisMonth = (TextView)findViewById(R.id.unfinished_count_this_month);
		unfinishedEntryCountThisYear = (TextView)findViewById(R.id.unfinished_count_this_year);
		setTab();
		setUnfinishedEntryNotificationLayout();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		resetUnfinishedEntryCount();
	}
	
	public static void resetUnfinishedEntryCount() {
		if(unfinishedEntryCountAll != null && unfinishedEntryCountThisMonth != null && unfinishedEntryCountThisMonth != null && unfinishedEntryCountThisYear != null) {
			cancelUnfinishedEntryTask();
			unfinishedEntryCount = new UnfinishedEntryCount(mConvertCursorToListString.getEntryList(false,""), unfinishedEntryCountThisWeek, unfinishedEntryCountThisMonth, unfinishedEntryCountThisYear, unfinishedEntryCountAll);
			unfinishedEntryCount.execute();
		}
	}
	
	private void setUnfinishedEntryNotificationLayout() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, outMetrics.widthPixels/24, 0);
		unfinishedEntryCountThisMonth.setLayoutParams(params);
		unfinishedEntryCountThisWeek.setLayoutParams(params);
		unfinishedEntryCountThisYear.setLayoutParams(params);
		unfinishedEntryCountAll.setLayoutParams(params);
	}

	private void setTab() {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        Bundle intentExtras = getIntent().getExtras();
        
        // Create an Intent to launch an Activity for the tab (to be reused)
        Intent intentThisWeek = new Intent(this, ThisWeek.class);
        Intent intentThisMonth = new Intent(this, ThisMonth.class);
        Intent intentThisYear = new Intent(this, ThisYear.class);
        Intent intentAll = new Intent(this, All.class);
        setExtras(tabHost, intentThisWeek, intentThisMonth, intentThisYear, intentAll, intentExtras);
        // Initialize a TabSpec for each tab and add it to the TabHost
        TabSpec tabThisWeek = tabHost.newTabSpec(getString(R.string.tab_thisweek)).setIndicator("This Week", getResources().getDrawable(R.drawable.tab_this_week)).setContent(intentThisWeek);
        TabSpec tabThisMonth = tabHost.newTabSpec(getString(R.string.tab_thismonth)).setIndicator("This Month", getResources().getDrawable(R.drawable.tab_this_month)).setContent(intentThisMonth);
        TabSpec tabThisYear = tabHost.newTabSpec(getString(R.string.tab_thisyear)).setIndicator("This Year", getResources().getDrawable(R.drawable.tab_this_year)).setContent(intentThisYear);
        TabSpec tabAll = tabHost.newTabSpec(getString(R.string.tab_all)).setIndicator("All", getResources().getDrawable(R.drawable.tab_all)).setContent(intentAll);
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
			} else if (displayDate.isNotCurrentMonthAndCurrentYear()){
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
			} else if (displayDate.isNotCurrentMonthAndCurrentYear()){
				return getString(R.string.tab_thisyear);
			} else {
				return getString(R.string.tab_all);
			}
		} else {
			return getString(R.string.tab_thisweek);
		}
	}

	@Override
	public void onClick(View v) {
		cancelUnfinishedEntryTask();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		cancelUnfinishedEntryTask();
	}
	
	private static void cancelUnfinishedEntryTask() {
		if(unfinishedEntryCount != null && !unfinishedEntryCount.isCancelled()) {
			unfinishedEntryCount.cancel(true);
		}
	}
	
}
