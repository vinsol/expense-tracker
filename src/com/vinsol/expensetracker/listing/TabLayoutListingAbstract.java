package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.GetArrayListFromString;

abstract class TabLayoutListingAbstract extends ListingAbstract {
	
	private Bundle bundle;
	protected int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentExtras.putBoolean(Constants.IS_COMING_FROM_EXPENSE_LISTING, true);
		bundle = new Bundle();
		initListView();
	}

	@SuppressWarnings("unchecked")
	private void initListView() {
		mSeparatedListAdapter = new SeparatedListAdapter(this,highlightID);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDataDateList = mConvertCursorToListString.getDateListString(false,"");
		mSubList = mConvertCursorToListString.getListStringParticularDate("");
		int j = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<Entry>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<Entry> mList = new ArrayList<Entry>();
			String date = mDataDateList.get(i).dateTime;
			Calendar toCHeckCal = Calendar.getInstance();
			toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
			toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
			while (j < mSubList.size() && date.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate())) {
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				Entry templist = getList(toCHeckCal, i, j, mList, mDisplayDate);
				mList.add(templist);
				j++;
				
				if(j == mSubList.size() || !condition(mDisplayDate)) {
					j = mSubList.size();
					break;
				} else {
					toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
					toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
				}
			}
			if(j == mSubList.size()) {
				break;
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<Entry>(this, R.layout.expense_listing_tab, tt), mDataDateList);
		}
		doOperationsOnListview();
	}

	protected void startSubListing(Entry entry) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(entry.id);
		for(int checkI=0;checkI<mArrayList.size();checkI++) {
			if(mArrayList.get(checkI).equals(highlightID)) {
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				Bundle extras = new Bundle();
				extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
				expenseSubListing.putExtras(extras);
				expenseSubListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(expenseSubListing,RESULT);
 				finish();
			}
		}	
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
	protected void onClickElse(Entry entry,int position) {
		Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
		Bundle extras = new Bundle();
		extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
		extras.putInt(Constants.POSITION, position);
		mSubListIntent.putExtras(extras);
		mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(mSubListIntent,RESULT);
	}
	
	@Override
	public void noItemLayout() {
		if (mDataDateList.size() == 0) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button noItemButton = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			noItemButtonAction(noItemButton);
		}
	}
	
	protected abstract boolean condition(DisplayDate mDisplayDate);
	protected abstract Entry getList(Calendar toCHeckCal, int i, int j, List<Entry> mList, DisplayDate mDisplayDate);
	
}
