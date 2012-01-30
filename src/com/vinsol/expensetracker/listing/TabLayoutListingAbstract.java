package com.vinsol.expensetracker.listing;

import android.app.Activity;
import android.os.Bundle;

import com.vinsol.expensetracker.R;

public class TabLayoutListingAbstract extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_listing_listview_common);
	}

}
