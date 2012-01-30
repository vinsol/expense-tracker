package com.vinsol.expensetracker.listing;

import android.os.Bundle;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;


public class ExpenseListingPrevYears extends TabLayoutListingAbstract {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = R.string.sublist_prevyears;
	}
	
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isPrevYears();
	}
	
	@Override
	protected Entry getList(int j) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
