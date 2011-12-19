package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class ShowHelper{

	private TextView show_text_voice_camera_amount;
	private TextView show_text_voice_camera_tag_textview;
	private String favID;
	private Long _id = null;
	private Activity mActivity;
	private ArrayList<String> mShowList;
	private Context mContext;
	private Bundle intentExtras;
	private int typeOfEntryFinished;
	private int typeOfEntryUnfinished;
	private int typeOfEntry;
	
	public ShowHelper(Context _mContext,Bundle _intentExtras,int _typeOfEntry,int _typeOfEntryFinished,int _typeOfEntryUnfinished) {
		// ///// ****** Assigning memory ******* /////////
		mContext = _mContext;
		typeOfEntry = _typeOfEntry;
		typeOfEntryFinished = _typeOfEntryFinished;
		typeOfEntryUnfinished = _typeOfEntryUnfinished;
		intentExtras = _intentExtras;
		mActivity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_amount = (TextView) mActivity.findViewById(R.id.show_text_voice_camera_amount);
		show_text_voice_camera_tag_textview = (TextView) mActivity.findViewById(R.id.show_text_voice_camera_tag_textview);
		mShowList = new ArrayList<String>();
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = new ArrayList<String>();
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mShowList.get(0));
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);
			
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					show_text_voice_camera_amount.setText(amount);
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(mContext.getString(typeOfEntryUnfinished)))) {
				show_text_voice_camera_tag_textview.setText(tag);
			} else {
				show_text_voice_camera_tag_textview.setText(mContext.getString(typeOfEntryFinished));
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
				new ShowLocationHandler(mContext, mShowList.get(7));
			
			
			if(mShowList.get(6) != null) {
				new ShowDateHandler(mContext, mShowList.get(6));
			}
			else {
				new ShowDateHandler(mContext,typeOfEntry);
			}
			
		}
	}
	
	public String getFavID() {
		return favID;
	}
	
	public long getId() {
		return _id;
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
					_id = Long.parseLong(mShowList.get(0));
				} else {
					mActivity.finish();
				}
			} else {
				mActivity.finish();
			}
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);

			if (amount != null) {
				if(!amount.equals("") && !amount.equals("?")){
					show_text_voice_camera_amount.setText(amount);
				} else {
					mActivity.finish();
				}
			} else {
				mActivity.finish();
			}
			
			if (!(tag.equals("") || tag == null || tag.equals(mContext.getString(typeOfEntryUnfinished)) || tag.equals(mContext.getString(typeOfEntryFinished)))) {
				show_text_voice_camera_tag_textview.setText(tag);
			} else {
				show_text_voice_camera_tag_textview.setText(mContext.getString(typeOfEntryFinished));
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.get(7) != null)
				new ShowLocationHandler(mContext, mShowList.get(7));
			
			if(mShowList.get(6) != null)
				new ShowDateHandler(mContext, mShowList.get(6));
			else {
				new ShowDateHandler(mContext,typeOfEntry);
			}
			
		}
	}
}
