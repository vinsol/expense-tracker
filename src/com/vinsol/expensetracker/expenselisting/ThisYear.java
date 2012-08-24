/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.expenselisting;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class ThisYear extends TabLayoutListingAbstract {
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek() || mDisplayDate.isCurrentMonth() || mDisplayDate.isNotCurrentMonthAndCurrentYear();
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_thisyear;
	}

	@Override
	protected void setModifiedValues() {
		isModifiedThisYear = true;
		isModifiedThisMonth = false;
		isModifiedThisWeek = false;
		isModifiedAll = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!isModifiedThisYear) {
			initListView();
		}
	}
	
}
