package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTextActivity extends Activity implements OnClickListener{

	private static final int EDIT_RESULT = 35;
	private ArrayList<String> mShowList;
	private Bundle intentExtras;
	private Long _id = null;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView show_text_voice_camera_amount;
	private TextView show_text_voice_camera_tag_textview;
	private Button show_text_voice_camera_delete;
	private Button show_text_voice_camera_edit;
	protected static String favID = null;
	private FavoriteHelper mFavoriteHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_text_voice_camera);

		// ///// ****** Assigning memory ******* /////////
		show_text_voice_camera_amount = (TextView) findViewById(R.id.show_text_voice_camera_amount);
		show_text_voice_camera_tag_textview = (TextView) findViewById(R.id.show_text_voice_camera_tag_textview);
		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_edit = (Button) findViewById(R.id.show_text_voice_camera_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textShowBundle");
		mShowList = new ArrayList<String>();

		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mShowList.get(0));
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);
			show_text_voice_camera_tag_textview.setText(tag);
			show_text_voice_camera_amount.setText(amount);
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			if(mShowList.get(7) != null)
				new ShowLocationHandler(this, mShowList.get(7));
			if(mShowList.get(6) != null)
				new ShowDateHandler(this, mShowList.get(6));
			else {
				new ShowDateHandler(this,R.string.text);
			}
			if(mShowList.get(4) != null){
				if(!mShowList.get(4).equals("")){
					favID = mShowList.get(4);
				}
			}
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}
		show_text_voice_camera_delete.setOnClickListener(this);
		show_text_voice_camera_edit.setOnClickListener(this);
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
			startActivityForResult(editIntent,EDIT_RESULT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (EDIT_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("textShowBundle");
				mShowList = new ArrayList<String>();
				if (intentExtras.containsKey("mDisplayList")) {
					mShowList = intentExtras.getStringArrayList("mDisplayList");
					Log.v("mShowList", mShowList.toString());
					if(mShowList.get(0) != null){
						if(!mShowList.get(0).equals("")){
							_id = Long.parseLong(mShowList.get(0));
						} else {
							finish();
						}
					} else {
						finish();
					}
					
					String amount = mShowList.get(2);
					String tag = mShowList.get(1);
					if (!(tag.equals("") || tag == null || tag.equals(getString(R.string.unfinished_textentry)) || tag.equals(getString(R.string.finished_textentry)))) {
						show_text_voice_camera_tag_textview.setText(tag);
					} else {
						finish();
					}
					
					if (amount != null) {
						if(!amount.equals("") && !amount.equals("?")){
							show_text_voice_camera_amount.setText(amount);
						} else {
							finish();
						}
					} else {
						finish();
					}
					
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					if(mShowList.get(7) != null)
						new ShowLocationHandler(this, mShowList.get(7));
					if(mShowList.get(6) != null)
						new ShowDateHandler(this, mShowList.get(6));
					else {
						new ShowDateHandler(this,R.string.text);
					}
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
	
}
