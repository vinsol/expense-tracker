package com.vinsol.expensetracker.show;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.utils.FavoriteHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShowTextActivity extends ShowAbstract{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intentExtras = getIntent().getBundleExtra("textShowBundle");
		typeOfEntry = R.string.text;
		typeOfEntryFinished = R.string.finished_textentry;
		typeOfEntryUnfinished = R.string.unfinished_textentry;
		showHelper();
		if (intentExtras.containsKey("mDisplayList")) {
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (SHOW_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("textShowBundle");
				doTaskOnActivityResult(intentExtras);
				if (intentExtras.containsKey("mDisplayList")) {
					mFavoriteHelper.setShowList(mShowList);
				}
				showDelete.setOnClickListener(this);
				showEdit.setOnClickListener(this);
			}
		}
		if(resultCode == Activity.RESULT_CANCELED){
			finish();
		}
	}

	@Override
	protected void editAction() {
		super.editAction();
		Intent editIntent = new Intent(this, TextEntry.class);
		editIntent.putExtra("textEntryBundle", intentExtras);
		startActivityForResult(editIntent,SHOW_RESULT);
	}
}
