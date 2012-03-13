/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/ 

package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.R;

public class GenerateReport extends BaseActivity implements OnClickListener,OnItemSelectedListener{
	
	private Spinner period;
	
	private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    
    private TextView customStartDateTextView;
    private TextView customEndDateTextView;
    
    private Calendar endCalendar;
    private Calendar startCalendar;
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_report);
		((Button)findViewById(R.id.export_button)).setOnClickListener(this);
		customStartDateTextView = (TextView)findViewById(R.id.custom_start_date);
		customStartDateTextView.setOnClickListener(this);
		customEndDateTextView = (TextView)findViewById(R.id.custom_end_date);
		customEndDateTextView.setOnClickListener(this);
		period = (Spinner) findViewById(R.id.period_spinner);
		period.setOnItemSelectedListener(this);
		
		//set default end day values
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		mEndYear = calendar.get(Calendar.YEAR);
		mEndMonth = calendar.get(Calendar.MONTH);
		mEndDay = calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.export_button:
			setStartEndDate();
			switch ((int)((Spinner) findViewById(R.id.type_spinner)).getSelectedItemId()) {
			//case if Exporting to PDF
			case 0:
				exportToPDF();
				break;
			//case if Exporting to CSV
			case 1:
				exportToCSV();
				break;

			default:
				break;
			}
			break;

		case R.id.custom_start_date:
			new CustomDatePickerDialog(this, mStartDateSetListener, customStartDateTextView).show();
			break;
			
		case R.id.custom_end_date:
			new CustomDatePickerDialog(this, mEndDateSetListener, customStartDateTextView).show();
			break;
		default:
			break;
		}
	}

	private void exportToCSV() {
		// TODO Auto-generated method stub
	}

	private void exportToPDF() {
		// TODO
	}

	private void setStartEndDate() {
		endCalendar = Calendar.getInstance();
		endCalendar.set(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	
		startCalendar = (Calendar) endCalendar.clone();
		switch ((int)period.getSelectedItemId()) {
		//case if period is 1 Month
		case 0:
			endCalendar.add(Calendar.MONTH, -1);
			setDateParameters(startCalendar,endCalendar);
			break;

		//case if period is 1 Quarter
		case 1:
			endCalendar.add(Calendar.MONTH, -3);
			setDateParameters(startCalendar,endCalendar);
			break;

		//case if period is Half Year
		case 2:
			endCalendar.add(Calendar.MONTH, -6);
			setDateParameters(startCalendar,endCalendar);
			break;
			
		//case if period is 1 Year
		case 3:
			endCalendar.add(Calendar.YEAR, -1);
			setDateParameters(startCalendar,endCalendar);
			break;
			
		//case if period is Custom
		case 4:
			checkStartEndDate(true);
			break;
		default:
			break;
		}
	}

	private void setDateParameters(Calendar startCalendar, Calendar endCalendar) {
		mEndYear = endCalendar.get(Calendar.YEAR);
		mEndMonth = endCalendar.get(Calendar.MONTH);
		mEndDay = endCalendar.get(Calendar.DAY_OF_MONTH);
		mStartYear = startCalendar.get(Calendar.YEAR);
		mStartMonth = startCalendar.get(Calendar.MONTH);
		mStartDay = startCalendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View v, int position,long id) {
		if(id == 4) {
			((LinearLayout)findViewById(R.id.custom_date_layout)).setVisibility(View.VISIBLE);
		} else {
			((LinearLayout)findViewById(R.id.custom_date_layout)).setVisibility(View.GONE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapter) {
		// do nothing
	}
	
	private CustomDatePickerDialog.OnDateSetListener mStartDateSetListener = new CustomDatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mStartYear = year;
			mStartMonth = monthOfYear;
			mStartDay = dayOfMonth;
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			((TextView)findViewById(R.id.custom_start_date)).setText(new DisplayDate(mCalendar).getDisplayDate());
			checkStartEndDate(false);
		}
	};
	
	private CustomDatePickerDialog.OnDateSetListener mEndDateSetListener = new CustomDatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mEndYear = year;
			mEndMonth = monthOfYear;
			mEndDay = dayOfMonth;
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			((TextView)findViewById(R.id.custom_end_date)).setText(new DisplayDate(mCalendar).getDisplayDate());
			mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            checkStartEndDate(false);
		}
	};
	
	private boolean checkStartEndDate(boolean isToShowToast) {
		if(customEndDateTextView.getText().toString().equals("") || customStartDateTextView.getText().toString().equals("")) {
			if(isToShowToast) {
				Toast.makeText(getApplicationContext(), "Set Start Date and End Date before exporting", Toast.LENGTH_LONG).show();
			}
			return false;
		}
		if(!isCombinationCorrect()) {
			Toast.makeText(getApplicationContext(), "End Date must be greater than Start Date", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private boolean isCombinationCorrect() {
		if(mStartDay == mEndDay && mStartMonth == mEndMonth && mEndYear == mStartYear) {return true;}
		if(mStartYear > mEndYear) {return false;}
		if(mStartYear == mEndYear && mStartMonth > mEndMonth) {return false;}
		if(mStartYear == mEndYear && mStartMonth == mEndMonth && mStartDay > mEndDay) {return false;}
		return true;
	}

}
