/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.expenselisting;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class ThisMonth extends TabLayoutListingAbstract {
		
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek() || mDisplayDate.isCurrentMonth();
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_thismonth;
	}

	@Override
	protected void setModifiedValues() {
		isModifiedThisYear = false;
		isModifiedThisMonth = true;
		isModifiedThisWeek = false;
		isModifiedAll = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!isModifiedThisMonth) {
			initListView();
		}
	}
	
}
