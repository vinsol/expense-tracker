package com.vinsol.expensetracker.edit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.listing.ExpenseListing;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

abstract class EditAbstract extends Activity implements OnClickListener{
	protected ArrayList<String> mEditList;
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
			entry.userId = intentExtras.getLong("_id");

		if(intentExtras.containsKey("setLocation")){
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			entry.userId = Long.parseLong(mEditList.get(0));
			entry.amount = mEditList.get(2);
			entry.description = mEditList.get(1);
			if (!(entry.amount.equals("") || entry.amount == null)) {
				if (!entry.amount.contains("?"))
					editAmount.setText(entry.amount);
			}
			if(entry.description.equals(getString(R.string.unknown_entry)) || mEditList.get(5).equals(getString(R.string.unknown))){
				setUnknown = true;
			}
			
			if (!(entry.description.equals("") || entry.description == null || 
					entry.description.equals(getString(typeOfEntryUnfinished)) || entry.description.equals(getString(typeOfEntryFinished))  || entry.description.equals(getString(R.string.unknown_entry)))) {
				editTag.setText(entry.description);
			}
		}
		
		// //////******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, Long.parseLong(mEditList.get(6)));
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}
	}
	
	protected HashMap<String, String> getSaveEntryData(TextView editDateBarDateview,String dateViewString){
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> list = new HashMap<String, String>();
		list.put(DatabaseAdapter.KEY_ID, Long.toString(entry.userId));
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
	
	protected ArrayList<String> getListOnResult(HashMap<String, String> list){
		ArrayList<String> listOnResult = new ArrayList<String>();
		listOnResult.add(mEditList.get(0));
		listOnResult.add(list.get(DatabaseAdapter.KEY_TAG));
		listOnResult.add(list.get(DatabaseAdapter.KEY_AMOUNT));
		if(listOnResult.get(2) == null || listOnResult.get(2) == "") {
			listOnResult.set(2, "?");
		}
		
		if (listOnResult.get(1) == null || listOnResult.get(1).equals("") || listOnResult.get(1).equals(getString(typeOfEntryUnfinished)) || listOnResult.get(1).equals(getString(typeOfEntryFinished)) || listOnResult.get(1).equals(getString(R.string.unknown_entry))) {
			listOnResult.set(1, getString(typeOfEntryFinished));
		}
		
		if (mEditList.get(1) == null || mEditList.get(1).equals("") || mEditList.get(1).equals(getString(typeOfEntryUnfinished)) || mEditList.get(1).equals(getString(typeOfEntryFinished)) || mEditList.get(1).equals(getString(R.string.unknown_entry))) {
			mEditList.set(1, getString(typeOfEntryFinished));
		}
		
		if(list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) != null ){
			listOnResult.add(new DisplayDate().getLocationDate(list.get(DatabaseAdapter.KEY_DATE_TIME), mEditList.get(7)));
		} else if (list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) == null){
			listOnResult.add(new DisplayDate().getLocationDateDate(list.get(DatabaseAdapter.KEY_DATE_TIME)));
		} else {
			listOnResult.add(mEditList.get(3));
		}		
		
		Boolean isAmountNotEqual = false;
		try{
			isAmountNotEqual = Double.parseDouble(new StringProcessing().getStringDoubleDecimal(listOnResult.get(2))) != Double.parseDouble(mEditList.get(2));
		}catch(Exception e){
			isAmountNotEqual = true;
		}
		
		if((!mEditList.get(1).equals(listOnResult.get(1))) || isAmountNotEqual || isChanged ) {
			isChanged = false;
			StaticVariables.favID = null;
			HashMap<String, String> listForFav = new HashMap<String, String>();
			listForFav.put(DatabaseAdapter.KEY_FAVORITE, "");
			listForFav.put(DatabaseAdapter.KEY_ID, mEditList.get(0));
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(listForFav);
			mDatabaseAdapter.close();
			listOnResult.add("");
		} else 
			if(StaticVariables.favID == null) {
				listOnResult.add(mEditList.get(4));
			}
			else { 
				listOnResult.add(StaticVariables.favID.toString());
			}
			
			
		listOnResult.add(mEditList.get(5));
		if(list.containsKey(DatabaseAdapter.KEY_DATE_TIME)) {
			listOnResult.add(list.get(DatabaseAdapter.KEY_DATE_TIME));
		} else {
			listOnResult.add(mEditList.get(6));
		}
		listOnResult.add(mEditList.get(7));
		mEditList = new ArrayList<String>();
		mEditList.addAll(listOnResult);
		return listOnResult;
	}

	protected void saveEntry() {
		
		HashMap<String, String> toSave = getSaveEntryData(dateBarDateview,dateViewString);

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
			tempBundle.putStringArrayList("mDisplayList", getListOnResult(toSave));
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
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(entry.userId));
			mDatabaseAdapter.close();
			if(intentExtras.containsKey("isFromShowPage")){
				ArrayList<String> listOnResult = new ArrayList<String>();
				listOnResult.add("");
				Bundle tempBundle = new Bundle();
				tempBundle.putStringArrayList("mDisplayList", listOnResult);
				mEditList = new ArrayList<String>();
				mEditList.addAll(listOnResult);
				startIntentAfterDelete(tempBundle);
			}
			finish();
		}
	}
	
	protected void startIntentAfterDelete(Bundle tempBundle) {}

	protected void deleteAction(){}
}
