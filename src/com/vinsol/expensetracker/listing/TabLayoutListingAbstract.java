package com.vinsol.expensetracker.listing;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;

abstract class TabLayoutListingAbstract extends ListingAbstract {
	
	private Bundle bundle;
	
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
		mDataDateList = mConvertCursorToListString.getDateListString(false,"",type);
		mSubList = mConvertCursorToListString.getListStringParticularDate("");
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
	
	protected abstract void setType();
	
}
