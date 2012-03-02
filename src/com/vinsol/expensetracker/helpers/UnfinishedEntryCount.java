package com.vinsol.expensetracker.helpers;

import java.util.List;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.Log;

import android.os.AsyncTask;

public class UnfinishedEntryCount extends AsyncTask<Void, Void, Void>{

	private List<Entry> entries;
	int count = 0;
	
	public UnfinishedEntryCount(List<Entry> entries) {
		this.entries = entries;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		CheckEntryComplete checkEntryComplete = new CheckEntryComplete();
		for(Entry entry : entries) {
			if(!checkEntryComplete.isEntryComplete(entry, ExpenseTrackerApplication.getContext())) {
				count++;
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("***********************");
		Log.d("count "+count);
		Log.d("***********************");
	}
	
}
