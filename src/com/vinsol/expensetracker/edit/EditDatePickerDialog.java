/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.edit;

import java.util.Calendar;

import android.content.Context;
import android.widget.TextView;

import com.vinsol.expensetracker.helpers.AbstractDatePickerDialog;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.utils.GetMonth;

public class EditDatePickerDialog extends AbstractDatePickerDialog {

	private TextView dateView;
	
	public EditDatePickerDialog(Context context, TextView view) {
		super(context);
		dateView = view;
	}

	@Override
	public void show() {
		String dateViewString = dateView.getText().toString();
		int year, month, day;
		if (dateViewString.contains("Today")) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			year = mCalendar.get(Calendar.YEAR);
			dateViewString = (String) dateViewString.subSequence(7,dateViewString.length());
		} else {
			year = Integer.parseInt((String) dateViewString.subSequence(dateViewString.length() - 4, dateViewString.length()));
			dateViewString = (String) dateViewString.subSequence(0,dateViewString.length() - 6);
		}
		month = new GetMonth().getMonth((String) dateViewString.subSequence(0, 3));
		dateViewString = (String) dateViewString.subSequence(4,dateViewString.length());
		day = Integer.parseInt(dateViewString);
		datePicker.init(year, month, day, this);
		super.show();
	}

	@Override
	protected void okTask() {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.set(datePicker.getYear(), datePicker.getMonth(),datePicker.getDayOfMonth());
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		dateView.setText(new DisplayDate(mCalendar).getDisplayDate());
		DateHandler.tempCalenderOnCancel = Calendar.getInstance();
		DateHandler.tempCalenderOnCancel.setFirstDayOfWeek(Calendar.MONDAY);
		DateHandler.tempCalenderOnCancel.setTime(mCalendar.getTime());
		dismiss();
	}
}
