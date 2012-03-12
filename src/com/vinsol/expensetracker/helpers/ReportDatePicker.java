/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import android.content.Context;

public class ReportDatePicker extends AbstractDatePickerDialog {

	public ReportDatePicker(Context context, int year, int monthOfYear, int dayOfMonth) {
		super(context);
		datePicker.init(year, monthOfYear, dayOfMonth, this);
	}

	public int getYear() {
		return datePicker.getYear();
	}

	public int getMonth() {
		return datePicker.getMonth();
	}

	public int getDay() {
		return datePicker.getDayOfMonth();
	}
	
	@Override
	protected void okTask() {
		dismiss();
	}

}
