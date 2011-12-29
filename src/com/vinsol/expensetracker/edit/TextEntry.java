package com.vinsol.expensetracker.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.show.ShowTextActivity;

public class TextEntry extends EditAbstract {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textEntryBundle");
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.finished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		editHelper();
	}
	
	@Override
	protected void saveEntryStartIntent(Bundle tempBundle) {
		super.saveEntryStartIntent(tempBundle);
		Intent mIntent = new Intent(this, ShowTextActivity.class);
		mIntent.putExtra("textShowBundle", tempBundle);
		setResult(Activity.RESULT_OK, mIntent);
	}
	
}
