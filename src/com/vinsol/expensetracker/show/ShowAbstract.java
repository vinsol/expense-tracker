package com.vinsol.expensetracker.show;

import java.util.Calendar;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.ShowData;
import com.vinsol.expensetracker.models.StaticVariables;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

abstract class ShowAbstract extends Activity implements OnClickListener{

	protected TextView showAmount;
	protected TextView showTag;
	protected DisplayList mShowList;
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
		showHeaderTitle = (TextView) findViewById(R.id.header_title);
		mDatabaseAdapter = new DatabaseAdapter(this);
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTag = (TextView) findViewById(R.id.show_tag_textview);
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_date_bar); 
		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		showEdit.setOnClickListener(this);
		showDelete.setOnClickListener(this);
	}
	
	public void showHelper() {
		// ///// ****** Assigning memory ******* /////////
		
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getParcelable("mDisplayList");
			showData.userId = mShowList.userId;
			showData.amount = mShowList.amount;
			showData.description = mShowList.description;
			Log.v("mShowList", mShowList.favorite+" "+mShowList.userId+" "+mShowList.timeInMillis+" "+mShowList.location);
			if (!(showData.amount.equals("") || showData.amount == null)) {
				if (!showData.amount.contains("?"))
					showAmount.setText(showData.amount);
			}
			
			if (!(showData.description.equals("") || showData.description == null || showData.description.equals(getString(typeOfEntryUnfinished)))) {
				showTag.setText(showData.description);
			} else {
				showTag.setText(getString(typeOfEntryFinished));
			}
			
			if(mShowList.favorite != null){
				if(!mShowList.favorite.equals("")){
					StaticVariables.favID = Long.parseLong(mShowList.favorite);
				}
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(mShowList.timeInMillis);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.location != null)
				new ShowLocationHandler(this, mShowList.location);

			if(mShowList.timeInMillis != null) {
				new ShowDateHandler(this, mShowList.timeInMillis);
			}
			else {
				new ShowDateHandler(this,typeOfEntry);
			}
		}
	}
	
	public void doTaskOnActivityResult(Bundle _intentExtras){
		intentExtras = _intentExtras;
		mShowList = new DisplayList();
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getParcelable("mDisplayList");
			
			if(mShowList.userId != null){
				if(mShowList.userId != ""){
					showData.userId = mShowList.userId;
				} else {
					finish();
				}
			} else {
				finish();
			}
			showData.amount = mShowList.userId;
			showData.description = mShowList.userId;

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
			mCalendar.setTimeInMillis(mShowList.timeInMillis);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.location != null)
				new ShowLocationHandler(this, mShowList.location);
			
			if(mShowList.timeInMillis != null)
				new ShowDateHandler(this, mShowList.timeInMillis);
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
				mDatabaseAdapter.deleteDatabaseEntryID(showData.userId);
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_edit){
			intentExtras.putBoolean("isFromShowPage", true);
			if(StaticVariables.favID != null)
				mShowList.favorite = StaticVariables.favID.toString();
			intentExtras.remove("mDisplayList");
			intentExtras.putParcelable("mDisplayList", mShowList);
			editAction();
		}
	}
	
	protected void deleteAction(){
	}
	
	protected void editAction() {
	}
}
