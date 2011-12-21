package com.vinsol.expensetracker.show;

import java.util.ArrayList;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.favorite.FavoriteHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShowTextActivity extends ShowAbstract implements OnClickListener{

	private final int SHOW_RESULT = 35;
	private Bundle intentExtras;
	private Long userId = null;
	private Button showDelete;
	private Button showEdit;
	private DatabaseAdapter mDatabaseAdapter;
	private ArrayList<String> mShowList;
	private FavoriteHelper mFavoriteHelper;
	
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
		getData();
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
				getData();
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
	public void onClick(View v) {

		if (v.getId() == R.id.show_delete) {
			if (userId != null) {
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(userId));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_edit){
			Intent editIntent = new Intent(this, TextEntry.class);
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, favID);
			intentExtras.remove("mDisplayList");
			intentExtras.putStringArrayList("mDisplayList", mShowList);
			Log.v("mEditList ShowTextActivity", mShowList.get(4)+" show");
			editIntent.putExtra("textEntryBundle", intentExtras);
			startActivityForResult(editIntent,SHOW_RESULT);
		}
	}
	
	private void getData(){
		favID = getFavID();
		userId = getId();
		mShowList = getShowList();
	}
}
