/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.List;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.Log;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public class UnfinishedEntryCount extends AsyncTask<Void, Void, Void>{

	private List<Entry> entries;
	int countAll = 0;
	int countThisWeek = 0;
	int countThisMonth = 0;
	int countThisYear = 0;
	private TextView textViewThisWeek;
	private TextView textViewThisMonth;
	private TextView textViewThisYear;
	private TextView textViewAll;
	private DisplayDate displayDate;
	
	public UnfinishedEntryCount(List<Entry> entries, TextView textViewThisWeek, TextView textViewThisMonth, TextView textViewThisYear, TextView textViewAll) {
		this.entries = entries;
		this.textViewThisWeek = textViewThisWeek;
		this.textViewThisMonth = textViewThisMonth;
		this.textViewThisYear = textViewThisYear;
		this.textViewAll = textViewAll;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CheckEntryComplete checkEntryComplete = new CheckEntryComplete();
		for(Entry entry : entries) {
			displayDate = new DisplayDate(entry.timeInMillis);
			if(!checkEntryComplete.isEntryComplete(entry, ExpenseTrackerApplication.getContext())) {
				countAll++;
				if(displayDate.isCurrentWeek()) {
					countThisWeek++;
				}
				if(displayDate.isCurrentMonth()) {
					countThisMonth++;
				}
				if(displayDate.isCurrentYear()) {
					countThisYear++;
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("***********************");
		Log.d("countAll "+countAll);
		Log.d("countThisWeek "+countThisWeek);
		Log.d("countThisMonth "+countThisMonth);
		Log.d("countThisYear "+countThisYear);
		Log.d("***********************");
		
		if(textViewAll != null) {
			if(countAll > 0) {
				textViewAll.setVisibility(View.VISIBLE);
				textViewAll.setText(countAll+"");
			} else {
				textViewAll.setVisibility(View.INVISIBLE);
			}
		}
		
		if(textViewThisWeek != null) {
			if(countThisWeek > 0) {
				textViewThisWeek.setVisibility(View.VISIBLE);
				textViewThisWeek.setText(countThisWeek+"");
			} else {
				textViewThisWeek.setVisibility(View.INVISIBLE);
			}
		}
		
		if(textViewThisMonth != null) {
			if(countThisMonth > 0) {
				textViewThisMonth.setVisibility(View.VISIBLE);
				textViewThisMonth.setText(countThisMonth+"");
			} else {
				textViewThisMonth.setVisibility(View.INVISIBLE);
			}
		}
		
		if(textViewThisYear != null) {
			if(countThisYear > 0) {
				textViewThisYear.setVisibility(View.VISIBLE);
				textViewThisYear.setText(countThisYear+"");
			} else {
				textViewThisYear.setVisibility(View.INVISIBLE);
			}
		}
	}
	
}
