package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import com.vinsol.expensetracker.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DateHandler implements OnClickListener {

	private ImageButton previousArrow;
	private ImageButton nextArrow;
	private TextView dateview;
	private DisplayDate mDisplayDate;
	private Calendar mCalendar;
	private DatePickerDialog dialog;
	public static Calendar tempCalenderOnCancel;

	public DateHandler(Activity activity) {
		mCalendar = Calendar.getInstance();
		doCommonTaskAfter(activity);
	}

	public DateHandler(Activity activity, long long1) {
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(long1);
		doCommonTaskAfter(activity);
	}
	
	private void doCommonTaskAfter(Activity activity) {
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		mDisplayDate = new DisplayDate(mCalendar);
		dateview = (TextView) activity.findViewById(R.id.edit_date_bar_dateview);
		previousArrow = (ImageButton) activity.findViewById(R.id.edit_date_bar_previous_arrow);
		nextArrow = (ImageButton) activity.findViewById(R.id.edit_date_bar_next_arrow);

		if (!beforeCurrentDate(mCalendar))
			nextArrow.setVisibility(View.INVISIBLE);

		dateview.setText(mDisplayDate.getDisplayDate());
		dialog = new DatePickerDialog(activity, dateview);
		previousArrow.setOnClickListener(this);
		nextArrow.setOnClickListener(this);
		dateview.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.edit_date_bar_next_arrow) {
			mCalendar.add(Calendar.DATE, 1);
			mDisplayDate = new DisplayDate(mCalendar);
			dateview.setText(mDisplayDate.getDisplayDate());

			if (isCurrentDate(mCalendar)) {
				nextArrow.setVisibility(View.INVISIBLE);
			}
		}

		if (v.getId() == R.id.edit_date_bar_previous_arrow) {
			mCalendar.add(Calendar.DATE, -1);
			mDisplayDate = new DisplayDate(mCalendar);
			if (!nextArrow.isShown()) {
				nextArrow.setVisibility(View.VISIBLE);
			}
			dateview.setText(mDisplayDate.getDisplayDate());
		}

		if (v.getId() == R.id.edit_date_bar_dateview) {
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (tempCalenderOnCancel != null) {
						if (beforeCurrentDate(tempCalenderOnCancel)) {
							nextArrow.setVisibility(View.VISIBLE);
						} else {
							nextArrow.setVisibility(View.INVISIBLE);
						}
						mCalendar.setTime(tempCalenderOnCancel.getTime());
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						tempCalenderOnCancel = null;
					}
				}
			});
			dialog.show();
		}

	}

	private boolean beforeCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (mCalendar.get(Calendar.YEAR) > pCalendar.get(Calendar.YEAR)) {
			return true;
		} else if ((mCalendar.get(Calendar.MONTH) > pCalendar.get(Calendar.MONTH))) {
			return true;
		} else if ((mCalendar.get(Calendar.DAY_OF_MONTH) > pCalendar.get(Calendar.DAY_OF_MONTH))) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isCurrentDate(Calendar pCalendar) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((mCalendar.get(Calendar.MONTH) == pCalendar.get(Calendar.MONTH))&& (mCalendar.get(Calendar.YEAR) == pCalendar.get(Calendar.YEAR))&& (mCalendar.get(Calendar.DAY_OF_MONTH) == pCalendar.get(Calendar.DAY_OF_MONTH))) {
			return true;
		}
		return false;
	}
}
