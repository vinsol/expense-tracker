/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.GetArrayListFromString;

public class ExpenseListing extends ListingAbstract {

	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTab();
		intentExtras.putBoolean(Constants.IS_COMING_FROM_EXPENSE_LISTING, true);
		bundle = new Bundle();
		initListView();
	}
	
	@SuppressWarnings("unchecked")
	private void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this,highlightID);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDataDateList = mConvertCursorToListString.getDateListString(false,"");
		mSubList = mConvertCursorToListString.getListStringParticularDate("");
		int j = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<Entry>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<Entry> mList = new ArrayList<Entry>();
			String date = mDataDateList.get(i).dateTime;
			Calendar toCHeckCal = Calendar.getInstance();
			toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
			toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
			while (j < mSubList.size() && date.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate())) {
				Entry templist = new Entry();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				if (mDisplayDate.isCurrentWeek()) {
					templist = getListCurrentWeek(j);
					mList.add(templist);
					j++;
					if (j < mSubList.size()) {
						toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
						toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					} else {
						break;
					}
				} else if (mDisplayDate.isCurrentMonth() || mDisplayDate.isPrevMonths() || mDisplayDate.isPrevYears()) {
					toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
					toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					while (mDataDateList.get(i).dateTime.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate())) {
						// //// Adding i+" "+j as id
						Entry mTempSubList = new Entry();
						mTempSubList.id = mSubList.get(j).id +",";

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						int isYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.description = tempDisplayDate.getSubListTag();

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).amount;
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if (j < mSubList.size()) {
								toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
								toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
								tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if(((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) || ((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
												&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
												&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) 
												||  ((tempCalendar.get(Calendar.MONTH) == isMonth) 
														&& (tempCalendar.get(Calendar.YEAR) == isYear)))
									mTempSubList.id = mTempSubList.id+mSubList.get(j).id+",";
							} else {
								break;
							}
						} while (((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
								&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
								&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) || ((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) 
										||  ((tempCalendar.get(Calendar.MONTH) == isMonth) 
												&& (tempCalendar.get(Calendar.YEAR) == isYear)));
						
						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}
						mTempSubList.amount = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						mTempSubList.type = getString(R.string.sublist_weekwise);
						mTempSubList.timeInMillis = 0L;
						if(highlightID != null) {
							if (j <= mSubList.size()) {
								if(mTempSubList.id.contains(highlightID)) {
									startSubListing(mTempSubList);
								} 
							}
						}
						mList.add(mTempSubList);
						if (j == mSubList.size()) {
							break;
						}
					}
				}
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<Entry>(this, R.layout.expense_listing_tab, tt), mDataDateList);
		}
		doOperationsOnListview();
	}

	private void startSubListing(Entry entry) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(entry.id);
		for(int checkI=0;checkI<mArrayList.size();checkI++) {
			if(mArrayList.get(checkI).equals(highlightID)) {
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				Bundle extras = new Bundle();
				extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
				expenseSubListing.putExtras(extras);
				expenseSubListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(expenseSubListing,RESULT);
 				finish();
			}
		}	
	}
	
	@Override
	protected void onClickElse(Entry entry,int position) {
		Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
		Bundle extras = new Bundle();
		extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
		extras.putInt(Constants.POSITION, position);
		mSubListIntent.putExtras(extras);
		mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(mSubListIntent,RESULT);
	}
	
	@Override
	protected void unknownDialogAction(String id) {
		initListView();
		Toast.makeText(ExpenseListing.this, "Deleted", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void noItemButtonAction(Button noItemButton) {
		noItemButton.setOnClickListener(new GroupedIconDialogClickListener(unknownDialog, ExpenseListing.this, bundle,null));
	}
	
	@Override
	public void noItemLayout() {
		if (mDataDateList.size() == 0) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button noItemButton = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			noItemButtonAction(noItemButton);
		}
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
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.expense_listing_tab);
	}
	
}
