/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.entry;

import android.os.Bundle;
import android.widget.TextView;

import com.vinsol.expensetracker.R;

public class Text extends EditAbstract {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.unfinished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		editHelper();
		createDatabaseEntry();
		setFavoriteHelper();
	}
	
	@Override
	protected String getTypeOfEntryForFlurry() {
		return getString(R.string.finished_textentry);
	}

	@Override
	protected void setDefaultTitle() {
		if(isFromFavorite) {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.edit_favorite)+" "+getString(R.string.finished_textentry));
		} else {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.finished_textentry));
		}
	}
	
	@Override
	protected boolean checkFavoriteComplete() {
		if(editAmount != null && !editAmount.getText().toString().equals("") && editTag != null && !editTag.getText().toString().equals("")) {
			return true;
		}
		return false;
	}
}
