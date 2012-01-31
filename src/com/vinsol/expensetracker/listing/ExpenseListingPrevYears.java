package com.vinsol.expensetracker.listing;

import java.util.Calendar;

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
	
	@Override
	protected boolean getLoopCondition(Calendar tempCalendar, int isWeekOfMonth, int isCurrentMonth, int isCurrentYear) {
		return tempCalendar.get(Calendar.YEAR) == isCurrentYear;
	}
	
}
