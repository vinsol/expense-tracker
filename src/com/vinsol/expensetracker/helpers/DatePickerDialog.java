package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.utils.GetMonth;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

public class DatePickerDialog extends Dialog implements android.view.View.OnClickListener, OnDateChangedListener {

	private DatePicker datePicker;
	private TextView dateView;
	private TextView textView;

	public DatePickerDialog(Context context, TextView view) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dateView = view;
		setContentView(R.layout.date_input_dialog);
		textView = (TextView) findViewById(R.id.new_date_label);
		datePicker = (DatePicker) findViewById(R.id.new_date_picker);
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
		Button okDateButton = (Button) findViewById(R.id.new_date_dialog_ok_button);
		Button cancelDateButton = (Button) findViewById(R.id.new_date_dialog_cancel_button);
		okDateButton.setOnClickListener(this);
		cancelDateButton.setOnClickListener(this);
		super.show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.new_date_dialog_ok_button) {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(datePicker.getYear(), datePicker.getMonth(),datePicker.getDayOfMonth());
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			dateView.setText(new DisplayDate(mCalendar).getDisplayDate());
			DateHandler.tempCalenderOnCancel = Calendar.getInstance();
			DateHandler.tempCalenderOnCancel.setFirstDayOfWeek(Calendar.MONDAY);
			DateHandler.tempCalenderOnCancel.setTime(mCalendar.getTime());
			dismiss();
		}

		if (v.getId() == R.id.new_date_dialog_cancel_button) {
			dismiss();
		}
	}

	private void textViewInvisible() {
		textView.setVisibility(View.INVISIBLE);
	}

	private void textViewVisible() {
		textView.setVisibility(View.VISIBLE);
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
}
