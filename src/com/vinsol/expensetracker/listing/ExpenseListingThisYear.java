package com.vinsol.expensetracker.listing;

import android.os.Bundle;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class ExpenseListingThisYear extends TabLayoutListingAbstract {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = R.string.sublist_thisweek;
	}
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek() || mDisplayDate.isCurrentMonth() || mDisplayDate.isPrevMonths();
	}
	
	@Override
	protected Entry getList(int j) {
		// TODO Auto-generated method stub
		return null;
	}

}
