package com.vinsol.expensetracker.show;

import java.util.ArrayList;
import java.util.Calendar;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.utils.FavoriteHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

abstract class ShowAbstract extends Activity implements OnClickListener{

	protected TextView showAmount;
	protected TextView showTextview;
	public static String favID;
	protected Long userId = null;
	protected ArrayList<String> mShowList;
	protected Bundle intentExtras;
	protected int typeOfEntryFinished;
	protected int typeOfEntryUnfinished;
	protected int typeOfEntry;
	protected TextView showHeaderTitle;
	protected final int SHOW_RESULT = 35;
	protected FavoriteHelper mFavoriteHelper;
	protected DatabaseAdapter mDatabaseAdapter;	
	protected Button showDelete;
	protected Button showEdit;
	private RelativeLayout dateBarRelativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_page);
		showEdit = (Button) findViewById(R.id.show_edit);
		showDelete = (Button) findViewById(R.id.show_delete);
		showHeaderTitle = (TextView) findViewById(R.id.show_header_title);
		mDatabaseAdapter = new DatabaseAdapter(this);
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTextview = (TextView) findViewById(R.id.show_tag_textview);
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_date_bar); 
		mShowList = new ArrayList<String>();
		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		showEdit.setOnClickListener(this);
		showDelete.setOnClickListener(this);
	}
	
	public void showHelper() {
		// ///// ****** Assigning memory ******* /////////
		
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = new ArrayList<String>();
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			userId = Long.parseLong(mShowList.get(0));
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);
			
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					showAmount.setText(amount);
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(getString(typeOfEntryUnfinished)))) {
				showTextview.setText(tag);
			} else {
				showTextview.setText(getString(typeOfEntryFinished));
			}
			
			if(mShowList.get(4) != null){
				if(!mShowList.get(4).equals("")){
					favID = mShowList.get(4);
				}
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.get(7) != null)
				new ShowLocationHandler(this, mShowList.get(7));
			
			
			if(mShowList.get(6) != null) {
				new ShowDateHandler(this, mShowList.get(6));
			}
			else {
				new ShowDateHandler(this,typeOfEntry);
			}
			
		}
	}
	
	public String getFavID() {
		return favID;
	}
	
	public long getId() {
		return userId;
	}
	
	public ArrayList<String> getShowList() {
		return mShowList;
	}
	
	public void doTaskOnActivityResult(Bundle _intentExtras){
		intentExtras = _intentExtras;
		mShowList = new ArrayList<String>();
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = new ArrayList<String>();
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			
			if(mShowList.get(0) != null){
				if(mShowList.get(0) != ""){
					userId = Long.parseLong(mShowList.get(0));
				} else {
					finish();
				}
			} else {
				finish();
			}
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);

			if (amount != null) {
				if(!amount.equals("") && !amount.equals("?")){
					showAmount.setText(amount);
				} else {
					finish();
				}
			} else {
				finish();
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(getString(typeOfEntryUnfinished)) || tag.equals(getString(typeOfEntryFinished)))) {
				showTextview.setText(tag);
			} else {
				showTextview.setText(getString(typeOfEntryFinished));
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.get(7) != null)
				new ShowLocationHandler(this, mShowList.get(7));
			
			if(mShowList.get(6) != null)
				new ShowDateHandler(this, mShowList.get(6));
			else {
				new ShowDateHandler(this,typeOfEntry);
			}
			
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.show_delete) {
			if (userId != null) {
				deleteAction();
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
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, favID);
			intentExtras.remove("mDisplayList");
			intentExtras.putStringArrayList("mDisplayList", mShowList);
			editAction();
		}
	}
	
	protected void deleteAction(){
	}
	
	protected void editAction() {
	}
}
