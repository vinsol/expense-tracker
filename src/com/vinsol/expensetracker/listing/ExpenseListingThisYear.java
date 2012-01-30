package com.vinsol.expensetracker.listing;

import java.util.Calendar;
import java.util.List;

import android.os.Bundle;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.models.Entry;

public class ExpenseListingThisYear extends TabLayoutListingAbstract {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = R.string.sublist_thisweek;
	}
	
	@Override
	protected boolean condition(DisplayDate mDisplayDate) {
		return mDisplayDate.isCurrentWeek() || mDisplayDate.isCurrentMonth() || mDisplayDate.isPrevMonths();
	}
	
	@Override
	protected Entry getList(Calendar toCHeckCal, int i, int j, List<Entry> mList, DisplayDate mDisplayDate) {
		toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
		toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
		while (mDataDateList.get(i).dateTime.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate())) {
			////// Adding i+" "+j as id
			Entry mTempSubList = new Entry();
			mTempSubList.id = mSubList.get(j).id +",";

			Calendar tempCalendar = Calendar.getInstance();
			tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
			tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
			tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			mDisplayDate = new DisplayDate(tempCalendar);
			DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
			int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
			int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
			int isCurrentYear = tempCalendar.get(Calendar.YEAR);
			///// Adding tag
			mTempSubList.description = tempDisplayDate.getSubListTag(type);

			///// Adding Amount
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
					toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
					toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
					tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
					tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					tempDisplayDate = new DisplayDate(tempCalendar);
					if((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
							&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
							&& tempCalendar.get(Calendar.YEAR) == isCurrentYear) && condition(mDisplayDate))
						mTempSubList.id = mTempSubList.id+mSubList.get(j).id+",";
				} else {
					break;
				}
			} while ((tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
					&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
					&& tempCalendar.get(Calendar.YEAR) == isCurrentYear) && condition(mDisplayDate));
			
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
			mTempSubList.type = getString(type);
			mTempSubList.timeInMillis = 0L;
			if(highlightID != null) {
				if (j <= mSubList.size()) {
					if(mTempSubList.id.contains(highlightID)) {
						startSubListing(mTempSubList);
					} 
				}
			}
			mList.add(mTempSubList);
			if (j == mSubList.size()) {
				break;
			}
			return mTempSubList;
		}
		return null;
	}

}
