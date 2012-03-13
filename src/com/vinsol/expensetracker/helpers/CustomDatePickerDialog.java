/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.GetMonth;

public class CustomDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    private final String YEAR = "year";
    private final String MONTH = "month";
    private final String DAY = "day";
    
    private final DatePicker mDatePicker;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    
    private int mInitialYear;
    private int mInitialMonth;
    private int mInitialDay;

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }
    
    public CustomDatePickerDialog(Context context,OnDateSetListener callBack,TextView textView) {
    	super(context);
    	mCallBack = callBack;
    	String dateViewString = textView.getText().toString();
    	int year, month, day;
    	if(dateViewString != null && !dateViewString.equals("")) {
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
    	} else {
    		Calendar tempCalendar = Calendar.getInstance();
    		tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
    		year = tempCalendar.get(Calendar.YEAR);
    		month = tempCalendar.get(Calendar.MONTH);
    		day = tempCalendar.get(Calendar.DAY_OF_MONTH);
    	}
        mInitialYear = year;
        mInitialMonth = month;
        mInitialDay = day;
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialYear, mInitialMonth, mInitialDay);
        setButton(context.getText(R.string.date_time_set), this);
        setButton2(context.getText(R.string.cancel), (OnClickListener) null);
        setIcon(R.drawable.ic_dialog_time);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, this);
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            mDatePicker.clearFocus();
            mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
        }
    }
    
    @Override
    public void onDateChanged(DatePicker view, int year,int month, int day) {
        boolean color = true;
		if (isDateAfter(view)) {
			textViewVisible();
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			if (color) {
				view.init(mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH), this);
				color = false;
			} else {
				view.init(year, month, day, null);
			}

		} else {
			textViewInvisible();
	        updateTitle(year, month, day);
		}
    }

    private void updateTitle(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        setTitle(new DisplayDate(mCalendar).getDisplayDate());
    }
    
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
        updateTitle(year, month, day);
    }
    
    private void textViewInvisible() {
		((TextView) findViewById(R.id.new_date_label)).setVisibility(View.INVISIBLE);
	}

	private void textViewVisible() {
		((TextView) findViewById(R.id.new_date_label)).setVisibility(View.VISIBLE);
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
