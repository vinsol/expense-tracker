package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.DisplayDate;
import com.vinsol.expensetracker.utils.StringProcessing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EditHelper {
	private ArrayList<String> mEditList;
	private Long _id = null;
	private boolean setLocation = false;
	private EditText text_voice_camera_amount;
	private EditText text_voice_camera_tag;
	private Context mContext;
	private Activity mActivity;
	private Bundle intentExtras;
	private boolean setUnknown = false;
	private int typeOfEntryFinished;
	private int typeOfEntryUnfinished;
	private int typeOfEntry;
	private boolean isChanged = false;
	
	public EditHelper(Context _mContext,Bundle _intentExtras,int _typeOfEntry,int _typeOfEntryFinished,int _typeOfEntryUnfinished) {
		mContext = _mContext;
		intentExtras = _intentExtras;
		typeOfEntry = _typeOfEntry;
		typeOfEntryFinished = _typeOfEntryFinished;
		typeOfEntryUnfinished = _typeOfEntryUnfinished;
		mActivity = (mContext instanceof Activity) ? (Activity) mContext : null;
		text_voice_camera_amount = (EditText) mActivity.findViewById(R.id.text_voice_camera_amount);
		text_voice_camera_tag = (EditText) mActivity.findViewById(R.id.text_voice_camera_tag);
		
		if (intentExtras.containsKey("_id"))
			_id = intentExtras.getLong("_id");

		if(intentExtras.containsKey("setLocation")){
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mEditList.get(0));
			String amount = mEditList.get(2);
			String tag = mEditList.get(1);
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					text_voice_camera_amount.setText(amount);
			}
			if(tag.equals(mContext.getString(R.string.unknown_entry)) || mEditList.get(5).equals(mContext.getString(R.string.unknown))){
				setUnknown = true;
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(mContext.getString(typeOfEntryUnfinished)) || tag.equals(mContext.getString(typeOfEntryFinished))  || tag.equals(mContext.getString(R.string.unknown_entry)))) {
				text_voice_camera_tag.setText(tag);
			}
		}
		
		// //////******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(mContext, Long.parseLong(mEditList.get(6)));
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(mContext, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(mContext);
		}
	}
	
	public Long getId() {
		return _id;
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
		_id = id;
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
	public HashMap<String, String> getSaveEntryData(TextView text_voice_camera_date_bar_dateview,String dateViewString){
		// ///// ******* Creating HashMap to update info ******* ////////
				HashMap<String, String> _list = new HashMap<String, String>();
				_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));
		if (!text_voice_camera_amount.getText().toString().equals(".")&& !text_voice_camera_amount.getText().toString().equals("")) {
			Double mAmount = Double.parseDouble(text_voice_camera_amount.getText().toString());
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			_list.put(DatabaseAdapter.KEY_AMOUNT, mAmount.toString());
		} else {
			_list.put(DatabaseAdapter.KEY_AMOUNT, "");
		}
		if (text_voice_camera_tag.getText().toString() != "") {
			_list.put(DatabaseAdapter.KEY_TAG, text_voice_camera_tag.getText().toString());
		}
		if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey("mDisplayList")) {
					DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
					_list.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
				} else {
					if(!intentExtras.containsKey("timeInMillis")){
						DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
						_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
						_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(setLocation == true && LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
				_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		return _list;
	}
	
	public ArrayList<String> getListOnResult(HashMap<String, String> _list){
		ArrayList<String> listOnResult = new ArrayList<String>();
		listOnResult.add(mEditList.get(0));
		listOnResult.add(_list.get(DatabaseAdapter.KEY_TAG));
		listOnResult.add(_list.get(DatabaseAdapter.KEY_AMOUNT));
		if(listOnResult.get(2) == null || listOnResult.get(2) == "") {
			listOnResult.set(2, "?");
		}
		
		if (listOnResult.get(1) == null || listOnResult.get(1).equals("") || listOnResult.get(1).equals(mContext.getString(typeOfEntryUnfinished)) || listOnResult.get(1).equals(mContext.getString(typeOfEntryFinished)) || listOnResult.get(1).equals(mContext.getString(R.string.unknown_entry))) {
			listOnResult.set(1, mContext.getString(typeOfEntryFinished));
		}
		
		if (mEditList.get(1) == null || mEditList.get(1).equals("") || mEditList.get(1).equals(mContext.getString(typeOfEntryUnfinished)) || mEditList.get(1).equals(mContext.getString(typeOfEntryFinished)) || mEditList.get(1).equals(mContext.getString(R.string.unknown_entry))) {
			mEditList.set(1, mContext.getString(typeOfEntryFinished));
		}
		
		if(_list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) != null ){
			listOnResult.add(new DisplayDate().getLocationDate(_list.get(DatabaseAdapter.KEY_DATE_TIME), mEditList.get(7)));
		} else if (_list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) == null){
			listOnResult.add(new DisplayDate().getLocationDateDate(_list.get(DatabaseAdapter.KEY_DATE_TIME)));
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
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(mContext);
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
		if(_list.containsKey(DatabaseAdapter.KEY_DATE_TIME)) {
			listOnResult.add(_list.get(DatabaseAdapter.KEY_DATE_TIME));
		} else {
			listOnResult.add(mEditList.get(6));
		}
		listOnResult.add(mEditList.get(7));
		mEditList = new ArrayList<String>();
		mEditList.addAll(listOnResult);
		return listOnResult;
	}
	
}
