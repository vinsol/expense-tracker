/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class ExpenseSubListing extends ListingAbstract {

	private TextView listingHeader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentExtras.putBoolean(Constants.IS_COMING_FROM_EXPENSE_LISTING, true);
		initListView();
	}
	
	@SuppressWarnings("unchecked")
	private void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this,highlightID);
		intentExtras = getIntent().getExtras();
		Entry entry = intentExtras.getParcelable(Constants.ENTRY_LIST_EXTRA);
		entry = getIntent().getParcelableExtra(Constants.ENTRY_LIST_EXTRA);
		listingHeader = (TextView) findViewById(R.id.expense_listing_header_title);
		mDataDateList = mConvertCursorToListString.getDateListString(false,entry.id);
		mSubList = mConvertCursorToListString.getListStringParticularDate(entry.id);
		if(mSubList.size() > 0) {
			Bundle intentExtras = getIntent().getExtras();
			if(intentExtras != null){
				if(intentExtras.containsKey(Constants.HIGHLIGHT)) {
					highlightID = intentExtras.getString(Constants.HIGHLIGHT);
				}
			}
			Calendar mTempCalendar = Calendar.getInstance();
			mTempCalendar.setTimeInMillis(mSubList.get(0).timeInMillis);
			mTempCalendar.set(mTempCalendar.get(Calendar.YEAR),mTempCalendar.get(Calendar.MONTH),mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
			mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			listingHeader.setText(new DisplayDate(mTempCalendar).getDisplayDateSubListingHeader());
			int j = 0;
			@SuppressWarnings("rawtypes")
			List listString = new ArrayList<List<Entry>>();
			for (int i = 0; i < mDataDateList.size(); i++) {
				List<Entry> mList = new ArrayList<Entry>();
				String date = mDataDateList.get(i).dateTime;
				Calendar toCHeckCal = Calendar.getInstance();
				toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
				toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
				while (j < mSubList.size() && date.equals(new DisplayDate(toCHeckCal).getDisplayDate())) {
					Entry templist = new Entry();
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					templist = getListCurrentWeek(j);
					mList.add(templist);
					j++;
					if (j < mSubList.size()) {
						toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
						toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					} else {
						break;
					}
				}
				listString.add(mList);
				@SuppressWarnings("rawtypes")
				List tt = (List) listString.get(i);
				mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<Entry>(this, R.layout.expense_listing, tt), mDataDateList);
			}
			doOperationsOnListview();
		} else {
			Intent mIntent = new Intent(this, ExpenseListing.class);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mIntent);
			finish();
		}
	}

	@Override
	protected void unknownDialogAction(String id) {
		initListView();
	}
	
	@Override
	protected void noItemButtonAction(Button noItemButton) {
		noItemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, ExpenseListing.class);
		intent.putExtras(intentExtras);
		setResult(Activity.RESULT_OK, intent);
		super.onBackPressed();
	}
	
	@Override
	public void noItemLayout() {
		if(mDataDateList.size() == 0) {
			finish();
		}
	}
	
	@Override
	protected void setTab() {
		// TODO Auto-generated method stub
		
	}
	
}
