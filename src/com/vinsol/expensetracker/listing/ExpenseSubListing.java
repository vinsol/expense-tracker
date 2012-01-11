/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ExpenseSubListing extends ListingAbstract {

	private String idList;
	private TextView listingHeader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initListView();
	}
	
	@SuppressWarnings("unchecked")
	private void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this);
		idList = getIntent().getStringExtra("idList");
		listingHeader = (TextView) findViewById(R.id.expense_listing_header_title);
		mDataDateList = mConvertCursorToListString.getDateListString(false,idList);
		mSubList = mConvertCursorToListString.getListStringParticularDate(idList);
		if(mSubList.size() > 0) {
			Bundle intentExtras = getIntent().getExtras();
			if(intentExtras != null){
				if(intentExtras.containsKey("toHighLight")) {
					highlightID = intentExtras.getString("toHighLight");
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
				mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<String>(this, R.layout.expense_listing, tt), mDataDateList);
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
		super.unknownDialogAction(id);
		initListView();
	}
	
	@Override
	protected void noItemButtonAction(Button noItemButton) {
		super.noItemButtonAction(noItemButton);
		noItemButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void updateListView() {
		super.updateListView();
		initListView();
	}
	
	// /// ****************** Handling back press of key ********** ///////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(this, ExpenseListing.class);
			intent.putExtras(intentExtras);
			setResult(Activity.RESULT_OK, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
