package com.vinsol.expensetracker.edit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.show.ShowVoiceActivity;
import com.vinsol.expensetracker.utils.DateHandler;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.DisplayDate;
import com.vinsol.expensetracker.utils.StringProcessing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

abstract class EditAbstract extends Activity implements OnClickListener{
	private ArrayList<String> mEditList;
	private Long userId = null;
	private boolean setLocation = false;
	private EditText editAmount;
	private EditText editTag;
	private Bundle intentExtras;
	private boolean setUnknown = false;
	private int typeOfEntryFinished;
	private int typeOfEntryUnfinished;
	private int typeOfEntry;
	private boolean isChanged = false;
	
	public void editHelper(Bundle intentExtras,int typeOfEntry,int typeOfEntryFinished,int typeOfEntryUnfinished) {
		this.intentExtras = intentExtras;
		this.typeOfEntry = typeOfEntry;
		this.typeOfEntryFinished = typeOfEntryFinished;
		this.typeOfEntryUnfinished = typeOfEntryUnfinished;
		editAmount = (EditText) findViewById(R.id.edit_amount);
		editTag = (EditText) findViewById(R.id.edit_tag);
		
		if (intentExtras.containsKey("_id"))
			userId = intentExtras.getLong("_id");

		if(intentExtras.containsKey("setLocation")){
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			userId = Long.parseLong(mEditList.get(0));
			String amount = mEditList.get(2);
			String tag = mEditList.get(1);
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					editAmount.setText(amount);
			}
			if(tag.equals(getString(R.string.unknown_entry)) || mEditList.get(5).equals(getString(R.string.unknown))){
				setUnknown = true;
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(getString(typeOfEntryUnfinished)) || tag.equals(getString(typeOfEntryFinished))  || tag.equals(getString(R.string.unknown_entry)))) {
				editTag.setText(tag);
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
	
	public Long getId() {
		return userId;
	}
	
	public Bundle getIntentExtras() {
		return intentExtras;
	}
	
	public ArrayList<String> getEditList() {
		return mEditList;
	}
	
	public boolean isSetUnknown() {
		return setUnknown;
	}
	
	public void setId(Long id) {
		userId = id;
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	public HashMap<String, String> getSaveEntryData(TextView editDateBarDateview,String dateViewString){
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> list = new HashMap<String, String>();
		list.put(DatabaseAdapter.KEY_ID, Long.toString(userId));
		if (!editAmount.getText().toString().equals(".")&& !editAmount.getText().toString().equals("")) {
			Double mAmount = Double.parseDouble(editAmount.getText().toString());
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			list.put(DatabaseAdapter.KEY_AMOUNT, mAmount.toString());
		} else {
			list.put(DatabaseAdapter.KEY_AMOUNT, "");
		}
		if (editTag.getText().toString() != "") {
			list.put(DatabaseAdapter.KEY_TAG, editTag.getText().toString());
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
	
	public ArrayList<String> getListOnResult(HashMap<String, String> list){
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
			if(typeOfEntry == R.string.camera)
				ShowCameraActivity.favID = null;
			else if(typeOfEntry == R.string.voice)
				ShowVoiceActivity.favID = null;
			else if(typeOfEntry == R.string.text)
				ShowTextActivity.favID = null;
			HashMap<String, String> listForFav = new HashMap<String, String>();
			listForFav.put(DatabaseAdapter.KEY_FAVORITE, "");
			listForFav.put(DatabaseAdapter.KEY_ID, mEditList.get(0));
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(listForFav);
			mDatabaseAdapter.close();
			listOnResult.add("");
		} else 
			if(typeOfEntry == R.string.camera){
				if(ShowCameraActivity.favID == null) {
					listOnResult.add(mEditList.get(4));
				}
				else { 
					listOnResult.add(ShowCameraActivity.favID);
				}
			} else if (typeOfEntry == R.string.voice) {
				if(ShowVoiceActivity.favID == null) {
					listOnResult.add(mEditList.get(4));
				}
				else { 
					listOnResult.add(ShowVoiceActivity.favID);
				}
			} else if (typeOfEntry == R.string.text) {
				if(ShowTextActivity.favID == null) {
					listOnResult.add(mEditList.get(4));
				}
				else { 
					listOnResult.add(ShowTextActivity.favID);
				}
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
}
