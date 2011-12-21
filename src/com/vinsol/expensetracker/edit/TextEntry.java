package com.vinsol.expensetracker.edit;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.utils.DateHandler;
import com.vinsol.expensetracker.utils.FileDelete;

public class TextEntry extends EditAbstract {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.edit_date_bar).setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg));
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textEntryBundle");
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.finished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		editHelper();
		
		// ////// ******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, Long.parseLong(mEditList.get(6)));
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}
	}

	@Override
	public void onClick(View v) {
		// //////******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.edit_save_entry) {
			saveEntry();
		}

		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.edit_delete) {
			new FileDelete(userId);

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(userId));
			mDatabaseAdapter.close();
			if(intentExtras.containsKey("isFromShowPage")){
				Intent mIntent = new Intent(this, ShowTextActivity.class);
				ArrayList<String> listOnResult = new ArrayList<String>();
				listOnResult.add("");
				Bundle tempBundle = new Bundle();
				tempBundle.putStringArrayList("mDisplayList", listOnResult);
				mEditList = new ArrayList<String>();
				mEditList.addAll(listOnResult);
				mIntent.putExtra("textShowBundle", tempBundle);
				setResult(Activity.RESULT_CANCELED, mIntent);
			}
			finish();
		}
	}
	
	@Override
	protected void saveEntryStartIntent(Bundle tempBundle) {
		super.saveEntryStartIntent(tempBundle);
		Intent mIntent = new Intent(this, ShowTextActivity.class);
		mIntent.putExtra("textShowBundle", tempBundle);
		setResult(Activity.RESULT_OK, mIntent);
	}

}
