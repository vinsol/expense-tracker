package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.TextView;

abstract class ShowAbstract extends Activity implements OnClickListener{

	private TextView showAmount;
	private TextView showTextview;
	private String favID;
	private Long userId = null;
	private ArrayList<String> mShowList;
	private Bundle intentExtras;
	private int typeOfEntryFinished;
	private int typeOfEntryUnfinished;
	private int typeOfEntry;
	
	public void showHelper(Bundle intentExtras,int typeOfEntry,int typeOfEntryFinished,int typeOfEntryUnfinished) {
		// ///// ****** Assigning memory ******* /////////
		this.typeOfEntry = typeOfEntry;
		this.typeOfEntryFinished = typeOfEntryFinished;
		this.typeOfEntryUnfinished = typeOfEntryUnfinished;
		this.intentExtras = intentExtras;
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTextview = (TextView) findViewById(R.id.show_tag_textview);
		mShowList = new ArrayList<String>();
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
}
