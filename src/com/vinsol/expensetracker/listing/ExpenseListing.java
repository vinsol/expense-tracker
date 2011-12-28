package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.vinsol.expensetracker.GroupedIconDialogClickListener;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.utils.GetArrayListFromString;

public class ExpenseListing extends ListingAbstract {

	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = new Bundle();
		setContentView(R.layout.expense_listing);
		mConvertCursorToListString = new ConvertCursorToListString(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		mDataDateList = mConvertCursorToListString.getDateListString(false,"");
		mSubList = mConvertCursorToListString.getListStringParticularDate("");
		Bundle intentExtras = getIntent().getExtras();
		if(intentExtras != null){
			if(intentExtras.containsKey("toHighLight")){
				highlightID = intentExtras.getString("toHighLight");
			}
		}
		int j = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<DisplayList>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<DisplayList> mList = new ArrayList<DisplayList>();
			String date = mDataDateList.get(i).dateTime;

			while (j < mSubList.size()&& date.equals(mSubList.get(j).displayTime)) {
				DisplayList templist = new DisplayList();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				if (mDisplayDate.isCurrentWeek()) {
					templist = getListCurrentWeek(j);
					mList.add(templist);
					j++;
					if (j < mSubList.size()) {
					} else {
						break;
					}
				} else if (mDisplayDate.isCurrentMonth() || mDisplayDate.isPrevMonths() || mDisplayDate.isPrevYears()) {

					while (mDataDateList.get(i).dateTime.equals(mSubList.get(j).displayTime)) {
						// //// Adding i+" "+j as id
						DisplayList mTempSubList = new DisplayList();
						mTempSubList.id = mSubList.get(j).id +",";

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						int isYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.description = tempDisplayDate.getSubListTag();

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).amount;
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if (j < mSubList.size()) {
								tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if(((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) || ((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
												&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
												&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) 
												||  ((tempCalendar.get(Calendar.MONTH) == isMonth) 
														&& (tempCalendar.get(Calendar.YEAR) == isYear)))
									mTempSubList.id = mTempSubList.id+mSubList.get(j).id+",";
							} else {
								break;
							}
						} while (((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
								&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
								&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) || ((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)) 
										||  ((tempCalendar.get(Calendar.MONTH) == isMonth) 
												&& (tempCalendar.get(Calendar.YEAR) == isYear)));
						
						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}
						mTempSubList.amount = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						mTempSubList.type = getString(R.string.sublist_weekwise);
						if(highlightID != null){//TODO not proper method to check highlight id
							if (j <= mSubList.size()) {
								if(mTempSubList.id.contains(highlightID)){
									startSubListing(mTempSubList.id);
								} 
							}
						}
						mList.add(mTempSubList);
						if (j == mSubList.size()) {
							break;
						}
					}
				}
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<String>(this, R.layout.expense_listing, tt), mDataDateList);
		}
		doOperationsOnListview();
	}
	
	private void startSubListing(String string) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(string);
		for(int checkI=0;checkI<mArrayList.size();checkI++){
			if(mArrayList.get(checkI).equals(highlightID)){
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				expenseSubListing.putExtra("idList", string);
				expenseSubListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(expenseSubListing);
				finish();
			}
		}	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		firstVisiblePosition = mListView.getFirstVisiblePosition();
	}
	
	@Override
	protected void onClickElse(String id) {
		Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
		mSubListIntent.putExtra("idList", id);
		mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mSubListIntent);
	}
	
	@Override
	protected void unknownDialogAction(String id) {
		super.unknownDialogAction(id);
		Intent intentExpenseListing = new Intent(ExpenseListing.this, ExpenseListing.class);
		intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle mToHighLight = new Bundle();
		mToHighLight.putString("toHighLight", id);
		intentExpenseListing.putExtras(mToHighLight);
		startActivity(intentExpenseListing);
		Toast.makeText(ExpenseListing.this, "Deleted", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void noItemButtonAction(Button noItemButton) {
		super.noItemButtonAction(noItemButton);
		noItemButton.setOnClickListener(new GroupedIconDialogClickListener(unknownDialog, ExpenseListing.this, bundle,null));
	}
}
