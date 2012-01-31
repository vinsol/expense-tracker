package com.vinsol.expensetracker.listing;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;


public class ExpenseListingPrevYears extends TabLayoutListingAbstract {

	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isPrevYears();
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_prevyears;
	}
	
}
