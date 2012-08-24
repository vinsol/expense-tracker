/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.expenselisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.expenselisting.dialog.GroupedIconDialogClickListener;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;

abstract class TabLayoutListingAbstract extends ListingAbstract {
	
	private Bundle bundle;
	protected static boolean isModifiedThisWeek = true;
	protected static boolean isModifiedThisMonth = true;
	protected static boolean isModifiedThisYear = true;
	protected static boolean isModifiedAll = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setType();
		bundle = new Bundle();
		initListView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSeparatedListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this,highlightID);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDataDateList = mConvertCursorToListString.getDateListString(false,false,"",type);
		mSubList = mConvertCursorToListString.getEntryList(false,"");
		addSections();
	}

	@Override
	protected void unknownDialogAction(String id) {
		initListView();
		Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void noItemButtonAction(Button noItemButton) {
		noItemButton.setOnClickListener(new GroupedIconDialogClickListener(unknownDialog, this, bundle,null));
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.expense_listing_listview_common);
	}
	
	@Override
	public void noItemLayout() {
		if (mSeparatedListAdapter.getDataDateList() == null || mSeparatedListAdapter.getDataDateList().isEmpty()) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button noItemButton = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			noItemButtonAction(noItemButton);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setModifiedValues();
	}
	
	protected abstract void setType();
}
