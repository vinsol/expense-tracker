/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.expenselisting;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;

public class All extends TabLayoutListingAbstract {
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return true;
	}
	
	@Override
	protected void setType() {
		type = R.string.sublist_all;
	}

	@Override
	protected void setModifiedValues() {
		isModifiedThisYear = false;
		isModifiedThisMonth = false;
		isModifiedThisWeek = false;
		isModifiedAll = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!isModifiedAll) {
			initListView();
		}
	}
	
}
