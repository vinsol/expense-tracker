/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.TextEntry;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShowTextActivity extends ShowAbstract {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentExtras = getIntent().getExtras();
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.finished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		showHelper();
		if (intentExtras.containsKey("mDisplayList")) {
			FavoriteHelper();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (SHOW_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getExtras();
				doTaskOnActivityResult();
				mShowList = intentExtras.getParcelable("mDisplayList");
				FavoriteHelper();
				showDelete.setOnClickListener(this);
				showEdit.setOnClickListener(this);
			}
		}
		if(resultCode == Activity.RESULT_CANCELED) {
			finish();
		}
	}

	@Override
	protected void editAction() {
		Intent editIntent = new Intent(this, TextEntry.class);
		editIntent.putExtras(intentExtras);
		startActivityForResult(editIntent,SHOW_RESULT);
	}
}
