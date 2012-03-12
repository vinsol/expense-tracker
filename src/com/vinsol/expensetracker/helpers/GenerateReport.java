/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/ 

package com.vinsol.expensetracker.helpers;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
    
    private ReportDatePicker reportDatePicker;
    	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_report);
		((Button)findViewById(R.id.export_button)).setOnClickListener(this);
		((TextView)findViewById(R.id.custom_start_date)).setOnClickListener(this);
		((TextView)findViewById(R.id.custom_end_date)).setOnClickListener(this);
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
			break;

		case R.id.custom_start_date:
			reportDatePicker = new ReportDatePicker(this, mEndYear, mEndMonth, mEndDay);
			reportDatePicker.setOnDismissListener(mStartDismissListener);
			reportDatePicker.show();
			break;
			
		case R.id.custom_end_date:
			reportDatePicker = new ReportDatePicker(this, mEndYear, mEndMonth, mEndDay);
			reportDatePicker.setOnDismissListener(mEndDismissListener);
			reportDatePicker.show();
			break;
		default:
			break;
		}
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
    
    private OnDismissListener mStartDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if(reportDatePicker.isOk()) {
				mStartYear = reportDatePicker.getYear();
				mStartMonth = reportDatePicker.getMonth();
				mStartDay = reportDatePicker.getDay();
				((TextView)findViewById(R.id.custom_start_date)).setText(mStartDay+" "+getMonth(mStartMonth)+" "+mStartYear);
			}
		}
	};
	
	private OnDismissListener mEndDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if(reportDatePicker.isOk()) {
		        mEndYear = reportDatePicker.getYear();
	            mEndMonth = reportDatePicker.getMonth();
	            mEndDay = reportDatePicker.getDay();
	            ((TextView)findViewById(R.id.custom_end_date)).setText(mEndDay+" "+getMonth(mEndMonth)+" "+mEndYear);
			}
		}
	};
	
	////////******* Function which returns month as string ********///////////
	private String getMonth(int i) {
		switch (i) {
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jul";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		}
		return null;
	}

}
