/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/ 

package com.vinsol.expensetracker.helpers;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.R;

public class GenerateReport extends BaseActivity implements OnClickListener,OnItemSelectedListener{
	
	private Spinner period;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_report);
		((Button)findViewById(R.id.export_button)).setOnClickListener(this);
		period = (Spinner) findViewById(R.id.period_spinner);
		period.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.export_button:
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

}
