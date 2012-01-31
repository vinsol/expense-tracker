package com.vinsol.expensetracker.listing;

import java.util.Calendar;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class ExpenseListingThisWeek extends TabLayoutListingAbstract {

	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek();
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_thisweek;
	}
	
	@Override
	protected boolean getLoopCondition(Calendar tempCalendar, int isWeekOfMonth, int isCurrentMonth, int isCurrentYear) {
		return tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
				&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
				&& tempCalendar.get(Calendar.YEAR) == isCurrentYear;
	}
	
}