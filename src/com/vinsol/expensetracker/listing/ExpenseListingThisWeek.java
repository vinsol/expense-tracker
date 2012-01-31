package com.vinsol.expensetracker.listing;

import java.util.Calendar;
import java.util.List;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class ExpenseListingThisWeek extends TabLayoutListingAbstract {

	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek();
	}
	
	@Override
	protected Entry getList(Calendar toCHeckCal, int i, int j, List<Entry> mList, DisplayDate mDisplayDate) {
		return getListCurrentWeek(j);
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_thisweek;
	}
	
}