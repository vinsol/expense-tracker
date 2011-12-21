package com.vinsol.expensetracker.show;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.utils.FavoriteHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ShowTextActivity extends ShowAbstract{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_page);

		showDelete = (Button) findViewById(R.id.show_delete);
		showEdit = (Button) findViewById(R.id.show_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textShowBundle");
		showHelper(intentExtras,R.string.text,R.string.finished_textentry,R.string.unfinished_textentry);
		if (intentExtras.containsKey("mDisplayList")) {
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}
		showDelete.setOnClickListener(this);
		showEdit.setOnClickListener(this);
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
