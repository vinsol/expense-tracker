package com.vinsol.expensetracker.listing;

import java.util.Calendar;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class ExpenseListingThisYear extends TabLayoutListingAbstract {
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek() || mDisplayDate.isCurrentMonth() || mDisplayDate.isPrevMonths();
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_thisyear;
	}
	
	@Override
	protected boolean getLoopCondition(Calendar tempCalendar, int isWeekOfMonth, int isCurrentMonth, int isCurrentYear) {
		return tempCalendar.get(Calendar.MONTH) == isCurrentMonth
				&& tempCalendar.get(Calendar.YEAR) == isCurrentYear;
	}

}
