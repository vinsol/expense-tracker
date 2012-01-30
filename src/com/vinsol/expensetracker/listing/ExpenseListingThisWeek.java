package com.vinsol.expensetracker.listing;

import android.os.Bundle;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class ExpenseListingThisWeek extends TabLayoutListingAbstract {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = R.string.sublist_thisweek;
		
	}
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek();
	}
	
	@Override
	protected Entry getList(int j) {
		return getListCurrentWeek(j);
	}
	
}