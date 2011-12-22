package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.GroupedIconDialogClickListener;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.utils.GetArrayListFromString;
import com.vinsol.expensetracker.helpers.StringProcessing;

public class ExpenseListing extends ListingAbstract {

	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = new Bundle();
		setContentView(R.layout.expense_listing);
		mStringProcessing = new StringProcessing();
		mConvertCursorToListString = new ConvertCursorToListString(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		mDataDateList = mConvertCursorToListString.getDateListString();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		Bundle intentExtras = getIntent().getExtras();
		if(intentExtras != null){
			if(intentExtras.containsKey("toHighLight")){
				highlightID = intentExtras.getString("toHighLight");
			}
		}
		int j = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<List<String>>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<List<String>> mList = new ArrayList<List<String>>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);

			while (j < mSubList.size()&& date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
				List<String> templist = new ArrayList<String>();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis")));
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

					while (mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME).equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
						// //// Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID) +",");

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						int isYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag()); 

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
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
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
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
								mTempSubList.set(0, mTempSubList.get(0)+mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");
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
						mTempSubList.add(mStringProcessing.getStringDoubleDecimal(totalAmountString));
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
						mTempSubList.add("");
						mTempSubList.add("");
						if(highlightID != null){
							if (j <= mSubList.size()) {
								if(mTempSubList.get(0).contains(highlightID)){
									startSubListing(mTempSubList.get(0));
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
	protected void onClickElse(String userId) {
		Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
		mSubListIntent.putExtra("idList", userId);
		mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mSubListIntent);
	}
	
	@Override
	protected void unknownDialogAction(String userId) {
		super.unknownDialogAction(userId);
		Intent intentExpenseListing = new Intent(ExpenseListing.this, ExpenseListing.class);
		intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle mToHighLight = new Bundle();
		mToHighLight.putString("toHighLight", userId);
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
