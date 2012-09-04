/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.expenselisting;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.Log;

public class ExpenseSubListing extends ListingAbstract {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initListView();
	}
	
	@Override
	protected void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this,highlightID);
		intentExtras = getIntent().getExtras();
		ImageView listButton = (ImageView) findViewById(R.id.home_listview);
		TextView listingHeader = (TextView) findViewById(R.id.home_header_title);
		listButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ExpenseSubListing.this, ExpenseListing.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		Entry entry = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
		mDataDateList = mConvertCursorToListString.getDateListString(false, false, entry.id, type);
		mSubList = mConvertCursorToListString.getEntryList(false, entry.id);
		if(mSubList.size() > 0) {
			Calendar mTempCalendar = Calendar.getInstance();
			mTempCalendar.setTimeInMillis(mSubList.get(0).timeInMillis);
			mTempCalendar.set(mTempCalendar.get(Calendar.YEAR),mTempCalendar.get(Calendar.MONTH),mTempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
			mTempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			Log.d("listingHeader "+listingHeader);
			Log.d("getSubListHeaderType "+getSubListHeaderType());
			Log.d("mTempCalendar "+mTempCalendar);
			listingHeader.setText(new DisplayDate(mTempCalendar).getHeaderFooterListDisplayDate(getSubListHeaderType()));
			addSections();
		} else {
			finish();
		}
	}
	
	private int getSubListHeaderType() {
		switch (type) {
		case R.string.sublist_thisyear:
			return R.string.sublist_all;
		case R.string.sublist_thismonth:
			return R.string.sublist_thismonth;
		case R.string.sublist_thisweek:
			return 0;
		default:
			return R.string.sublist_thisweek;
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
		Intent intent = new Intent();
		intent.putExtras(intentExtras);
		setResult(Activity.RESULT_OK, intent);
		super.onBackPressed();
	}
	
	@Override
	public void noItemLayout() {
		if(mSeparatedListAdapter.getDataDateList() == null || mSeparatedListAdapter.getDataDateList().isEmpty()) {
			finish();
		}
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.expense_listing_no_tab);
	}

	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return true;
	}
	
	@Override
	protected int getType(Bundle intentExtras) {
		return intentExtras.getInt(Constants.KEY_TYPE);
	}

	@Override
	protected void setModifiedValues() {
		// do nothing
	}
	
}
