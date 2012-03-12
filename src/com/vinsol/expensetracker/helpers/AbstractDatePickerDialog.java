/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.vinsol.expensetracker.R;

public abstract class AbstractDatePickerDialog extends Dialog implements android.view.View.OnClickListener,OnDateChangedListener {

	protected DatePicker datePicker;
	private boolean isOk;

	public AbstractDatePickerDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.date_input_dialog);
		datePicker = (DatePicker) findViewById(R.id.new_date_picker);
		((Button) findViewById(R.id.new_date_dialog_ok_button)).setOnClickListener(this);
		((Button) findViewById(R.id.new_date_dialog_cancel_button)).setOnClickListener(this);
	}

	private void textViewInvisible() {
		((TextView) findViewById(R.id.new_date_label)).setVisibility(View.INVISIBLE);
	}

	private void textViewVisible() {
		((TextView) findViewById(R.id.new_date_label)).setVisibility(View.VISIBLE);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		boolean color = true;
		if (isDateAfter(view)) {
			textViewVisible();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			if (color) {
				view.init(mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH), this);
				color = false;
			} else {
				view.init(year, monthOfYear, dayOfMonth, null);
			}

		} else {
			textViewInvisible();
		}
	}

	private boolean isDateAfter(DatePicker tempView) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(tempView.getYear(), tempView.getMonth(),tempView.getDayOfMonth(), 0, 0, 0);
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if (tempCalendar.after(mCalendar))
			return true;
		else
			return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_date_dialog_ok_button:
			isOk = true;
			okTask();
			break;

		case R.id.new_date_dialog_cancel_button:
			isOk = false;
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public boolean isOk() {
		return isOk;
	}
	protected abstract void okTask();
}
