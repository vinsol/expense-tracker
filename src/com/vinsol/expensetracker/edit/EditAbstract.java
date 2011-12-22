package com.vinsol.expensetracker.edit;

import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.StaticVariables;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

abstract class EditAbstract extends Activity implements OnClickListener{
	protected DisplayList mEditList;
	protected boolean setLocation = false;
	protected EditText editAmount;
	protected EditText editTag;
	protected Bundle intentExtras;
	protected boolean setUnknown = false;
	protected int typeOfEntryFinished;
	protected int typeOfEntryUnfinished;
	protected int typeOfEntry;
	protected boolean isChanged = false;
	protected DatabaseAdapter mDatabaseAdapter;
	protected TextView editHeaderTitle;
	protected TextView dateBarDateview;
	protected String dateViewString;
	protected Button editDelete;
	protected Button editSaveEntry;
	protected Entry entry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_page);
		entry = new Entry();
		editAmount = (EditText) findViewById(R.id.edit_amount);
		editHeaderTitle = (TextView) findViewById(R.id.header_title);
		editTag = (EditText) findViewById(R.id.edit_tag);
		dateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		editSaveEntry = (Button) findViewById(R.id.edit_save_entry);
		editDelete = (Button) findViewById(R.id.edit_delete);
		mDatabaseAdapter = new DatabaseAdapter(this);
		editSaveEntry.setOnClickListener(this);
		editDelete.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		dateViewString = dateBarDateview.getText().toString();
	}

	protected void editHelper() {

		if (intentExtras.containsKey("_id"))
			entry.userId = intentExtras.getLong("_id")+"";

		if(intentExtras.containsKey("setLocation")){
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new DisplayList();
			mEditList = intentExtras.getParcelable("mDisplayList");
			entry.userId = mEditList.userId;
			entry.amount = mEditList.amount;
			entry.description = mEditList.description;
			if (!(entry.amount.equals("") || entry.amount == null)) {
				if (!entry.amount.contains("?"))
					editAmount.setText(entry.amount);
			}
			if(entry.description.equals(getString(R.string.unknown_entry)) || mEditList.description.equals(getString(R.string.unknown))){
				setUnknown = true;
			}
			
			if (!(entry.description.equals("") || entry.description == null || 
					entry.description.equals(getString(typeOfEntryUnfinished)) || entry.description.equals(getString(typeOfEntryFinished))  || entry.description.equals(getString(R.string.unknown_entry)))) {
				editTag.setText(entry.description);
			}
		}
		
		// //////******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, mEditList.timeInMillis);
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}
	}
	
	protected HashMap<String, String> getSaveEntryData(TextView editDateBarDateview,String dateViewString){
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> list = new HashMap<String, String>();
		list.put(DatabaseAdapter.KEY_ID, entry.userId);
		entry.amount = editAmount.getText().toString();
		entry.description = editTag.getText().toString();
		if (!entry.amount.equals(".") && !entry.amount.equals("")) {
			Double mAmount = Double.parseDouble(entry.amount);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			list.put(DatabaseAdapter.KEY_AMOUNT, mAmount.toString());
		} else {
			list.put(DatabaseAdapter.KEY_AMOUNT, "");
		}
		if (!entry.description.equals("")) {
			list.put(DatabaseAdapter.KEY_TAG, entry.description);
		}
		if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey("mDisplayList")) {
					DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
					list.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
				} else {
					if(!intentExtras.containsKey("timeInMillis")){
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
						list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
						list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(setLocation == true && LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
				list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		return list;
	}
	
	protected DisplayList getListOnResult(HashMap<String, String> list){
		DisplayList displayList = new DisplayList();
		displayList.userId = mEditList.userId;
		displayList.description = list.get(DatabaseAdapter.KEY_TAG);
		displayList.amount = list.get(DatabaseAdapter.KEY_AMOUNT);
		if(displayList.amount == null || displayList.amount.equals("")) {
			displayList.amount = "?";
		}
		
		if (displayList.description == null || displayList.description.equals("") || displayList.description.equals(getString(typeOfEntryUnfinished)) || displayList.description.equals(getString(typeOfEntryFinished)) || displayList.description.equals(getString(R.string.unknown_entry))) {
			displayList.description = getString(typeOfEntryFinished);
		}
		
		if (mEditList.description == null || mEditList.description.equals("") || mEditList.description.equals(getString(typeOfEntryUnfinished)) || mEditList.description.equals(getString(typeOfEntryFinished)) || mEditList.description.equals(getString(R.string.unknown_entry))) {
			mEditList.description = getString(typeOfEntryFinished);
		}
		
		if(list.containsKey(DatabaseAdapter.KEY_DATE_TIME)){
			displayList.timeLocation = new DisplayDate().getLocationDate(Long.parseLong(list.get(DatabaseAdapter.KEY_DATE_TIME)), mEditList.location);
		} else {
			displayList.timeLocation = mEditList.timeLocation;
		}		
		
		Boolean isAmountNotEqual = false;
		try{
			isAmountNotEqual = Double.parseDouble(new StringProcessing().getStringDoubleDecimal(displayList.amount)) != Double.parseDouble(mEditList.amount);
		}catch(Exception e){
			isAmountNotEqual = true;
		}
		
		if((!mEditList.description.equals(displayList.description)) || isAmountNotEqual || isChanged ) {
			isChanged = false;
			StaticVariables.favID = null;
			HashMap<String, String> listForFav = new HashMap<String, String>();
			listForFav.put(DatabaseAdapter.KEY_FAVORITE, "");
			listForFav.put(DatabaseAdapter.KEY_ID, mEditList.userId.toString());
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(listForFav);
			mDatabaseAdapter.close();
			displayList.favorite = "";
		} else 
			if(StaticVariables.favID == null) {
				displayList.favorite = mEditList.favorite;
			}
			else { 
				displayList.favorite = StaticVariables.favID.toString();
			}
			
		displayList.type = mEditList.type;	

		if(list.containsKey(DatabaseAdapter.KEY_DATE_TIME)) {
			displayList.timeInMillis = Long.parseLong(list.get(DatabaseAdapter.KEY_DATE_TIME));
		} else {
			displayList.timeInMillis = mEditList.timeInMillis;
		}
		displayList.location = mEditList.location;
		mEditList = displayList;
		return displayList;
	}

	protected void saveEntry() {
		
		HashMap<String, String> toSave = getSaveEntryData(dateBarDateview,dateViewString);

		Log.v("Before Save", toSave.toString());
		// //// ******* Update database if user added additional info *******		 ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(toSave);
		mDatabaseAdapter.close();
		if(!intentExtras.containsKey("isFromShowPage")){
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString("toHighLight", toSave.get(DatabaseAdapter.KEY_ID));
			intentExpenseListing.putExtras(mToHighLight);
			intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentExpenseListing);
		} else {
			Bundle tempBundle = new Bundle();
			tempBundle.putParcelable("mDisplayList", getListOnResult(toSave));
			saveEntryStartIntent(tempBundle);
		}
		finish();
	}
	
	// /// ****************** Handling back press of key ********** ///////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code above on earlier versions of the platform.
		saveEntry();
		actionAfterSaveOnBackButton();
		return;
	}
	
	protected void actionAfterSaveOnBackButton(){}
		
	protected void saveEntryStartIntent(Bundle tempBundle){}
	
	@Override
	public void onClick(View v) {

		// //////******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.edit_save_entry) {
			saveEntry();
		}
		
		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.edit_delete) {
			deleteAction();

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(entry.userId);
			mDatabaseAdapter.close();
			if(intentExtras.containsKey("isFromShowPage")){
				Bundle tempBundle = new Bundle();
				DisplayList displayList = new DisplayList();
				tempBundle.putParcelable("mDisplayList", displayList);
				mEditList = displayList;
				startIntentAfterDelete(tempBundle);
			}
			finish();
		}
	}
	
	protected void startIntentAfterDelete(Bundle tempBundle) {}

	protected void deleteAction(){}
}
