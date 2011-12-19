package com.vinsol.expensetracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShowTextActivity extends Activity implements OnClickListener{

	private final int SHOW_RESULT = 35;
	private Bundle intentExtras;
	protected static String favID = null;
	private ShowHelper mShowHelper;
	private Long _id = null;
	private Button show_text_voice_camera_delete;
	private Button show_text_voice_camera_edit;
	private DatabaseAdapter mDatabaseAdapter;
	private ArrayList<String> mShowList;
	private FavoriteHelper mFavoriteHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_text_voice_camera);

		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_edit = (Button) findViewById(R.id.show_text_voice_camera_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textShowBundle");
		mShowHelper = new ShowHelper(this, intentExtras,R.string.text,R.string.finished_textentry,R.string.unfinished_textentry);
		if (intentExtras.containsKey("mDisplayList")) {
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}
		if (intentExtras.containsKey("mDisplayList")) {
			getData();
		}
		show_text_voice_camera_delete.setOnClickListener(this);
		show_text_voice_camera_edit.setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (SHOW_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("textShowBundle");
				mShowHelper.doTaskOnActivityResult(intentExtras);
				if (intentExtras.containsKey("mDisplayList")) {
					getData();
					mFavoriteHelper.setShowList(mShowList);
				}
				show_text_voice_camera_delete.setOnClickListener(this);
				show_text_voice_camera_edit.setOnClickListener(this);
			}
		}
		if(resultCode == Activity.RESULT_CANCELED){
			finish();
		}
	}


	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.show_text_voice_camera_delete) {
			if (_id != null) {
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_text_voice_camera_edit){
			Intent editIntent = new Intent(this, TextEntry.class);
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, favID);
			intentExtras.remove("mDisplayList");
			intentExtras.putStringArrayList("mDisplayList", mShowList);
			editIntent.putExtra("textEntryBundle", intentExtras);
			startActivityForResult(editIntent,SHOW_RESULT);
		}
	}
	
	private void getData(){
		favID = mShowHelper.getFavID();
		_id = mShowHelper.getId();
		mShowList = mShowHelper.getShowList();
	}
}
