package com.vinsol.expensetracker;

import java.util.Calendar;

import com.vinsol.expensetracker.utils.DisplayDate;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class DatePickerDialog extends Dialog implements android.view.View.OnClickListener, OnDateChangedListener {

	private DatePicker datePicker;
	private TimePicker timePicker;
	private TextView dateView;
	private TextView textView;

	public DatePickerDialog(Context context, TextView view) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dateView = view;
		setContentView(R.layout.date_input_dialog);
		textView = (TextView) findViewById(R.id.new_date_label);
		datePicker = (DatePicker) findViewById(R.id.new_date_picker);
		timePicker = (TimePicker) findViewById(R.id.new_time_picker);
	}

	@Override
	public void show() {
		String dateViewString = dateView.getText().toString();
		timePicker.setVisibility(View.GONE);
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
		month = getMonth((String) dateViewString.subSequence(0, 3));
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
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			mCalendar.set(datePicker.getYear(), datePicker.getMonth(),datePicker.getDayOfMonth());
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

	private int getMonth(String i) {
		if (i.equals("Jan"))
			return 0;
		if (i.equals("Feb"))
			return 1;
		if (i.equals("Mar"))
			return 2;
		if (i.equals("Apr"))
			return 3;
		if (i.equals("May"))
			return 4;
		if (i.equals("Jun"))
			return 5;
		if (i.equals("Jul"))
			return 6;
		if (i.equals("Aug"))
			return 7;
		if (i.equals("Sep"))
			return 8;
		if (i.equals("Oct"))
			return 9;
		if (i.equals("Nov"))
			return 10;
		if (i.equals("Dec"))
			return 11;
		return 0;
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
			// textViewVisible();
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
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		tempCalendar.set(tempView.getYear(), tempView.getMonth(),tempView.getDayOfMonth(), 0, 0, 0);
		if (tempCalendar.after(mCalendar))
			return true;
		else
			return false;
	}
}
