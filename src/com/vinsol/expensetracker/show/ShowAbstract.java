package com.vinsol.expensetracker.show;

import java.util.ArrayList;
import java.util.Calendar;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.models.ShowData;
import com.vinsol.expensetracker.models.StaticVariables;

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
	protected TextView showTag;
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
	protected ShowData showData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_page);
		showData = new ShowData();
		showEdit = (Button) findViewById(R.id.show_edit);
		showDelete = (Button) findViewById(R.id.show_delete);
		showHeaderTitle = (TextView) findViewById(R.id.show_header_title);
		mDatabaseAdapter = new DatabaseAdapter(this);
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTag = (TextView) findViewById(R.id.show_tag_textview);
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
			showData.userId = Long.parseLong(mShowList.get(0));
			showData.amount = mShowList.get(2);
			showData.description = mShowList.get(1);
			
			if (!(showData.amount.equals("") || showData.amount == null)) {
				if (!showData.amount.contains("?"))
					showAmount.setText(showData.amount);
			}
			
			if (!(showData.description.equals("") || showData.description == null || showData.description.equals(getString(typeOfEntryUnfinished)))) {
				showTag.setText(showData.description);
			} else {
				showTag.setText(getString(typeOfEntryFinished));
			}
			
			if(mShowList.get(4) != null){
				if(!mShowList.get(4).equals("")){
					StaticVariables.favID = Long.parseLong(mShowList.get(4));
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
	
	public void doTaskOnActivityResult(Bundle _intentExtras){
		intentExtras = _intentExtras;
		mShowList = new ArrayList<String>();
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = new ArrayList<String>();
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			
			if(mShowList.get(0) != null){
				if(mShowList.get(0) != ""){
					showData.userId = Long.parseLong(mShowList.get(0));
				} else {
					finish();
				}
			} else {
				finish();
			}
			showData.amount = mShowList.get(2);
			showData.description = mShowList.get(1);

			if (showData.amount != null) {
				if(!showData.amount.equals("") && !showData.amount.equals("?")){
					showAmount.setText(showData.amount);
				} else {
					finish();
				}
			} else {
				finish();
			}
			
			if (!(showData.description.equals("") || showData.description == null || showData.description.equals(getString(typeOfEntryUnfinished)) || showData.description.equals(getString(typeOfEntryFinished)))) {
				showTag.setText(showData.description);
			} else {
				showTag.setText(getString(typeOfEntryFinished));
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
			if (showData.userId != null) {
				deleteAction();
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(showData.userId));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_edit){
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, StaticVariables.favID.toString());
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
