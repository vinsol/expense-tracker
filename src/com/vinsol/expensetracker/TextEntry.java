package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.DisplayDate;
import com.vinsol.expensetracker.utils.FileDelete;
import com.vinsol.expensetracker.utils.StringProcessing;

public class TextEntry extends Activity implements OnClickListener {

	private DatabaseAdapter mDatabaseAdapter;
	private Long _id;
	private Bundle intentExtras;
	private EditText text_voice_camera_amount;
	private EditText text_voice_camera_tag;
	private TextView text_voice_camera_date_bar_dateview;
	private String dateViewString;
	private ArrayList<String> mEditList;
	private Boolean setLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_voice_camera);

		mDatabaseAdapter = new DatabaseAdapter(this);
		findViewById(R.id.text_voice_camera_date_bar).setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg));
		text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
		text_voice_camera_tag = (EditText) findViewById(R.id.text_voice_camera_tag);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);

		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textEntryBundle");
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
			if (!(tag.equals("") || tag == null || tag.equals(getString(R.string.unfinished_textentry)) || tag.equals(getString(R.string.finished_textentry)) || tag.equals(getString(R.string.unknown_entry)))) {
				text_voice_camera_tag.setText(tag);
			}
		}
		
		// ////// ******** Handle Date Bar ********* ////////
		if(!intentExtras.containsKey("isFromShowPage")){
			if (intentExtras.containsKey("mDisplayList")) {
				new DateHandler(this, Long.parseLong(mEditList.get(6)));
			} else if (intentExtras.containsKey("timeInMillis")) {
				new DateHandler(this, intentExtras.getLong("timeInMillis"));
			} else {
				new DateHandler(this);
			}
		}
		
		if(intentExtras.containsKey("isFromShowPage")){
			
			if(mEditList.get(7) != null)
				new EditLocationHandler(this, mEditList.get(7));
			else 
				new EditLocationHandler(this, "unknown location");
			
			if(mEditList.get(6) != null)
				new EditDateHandler(this, mEditList.get(6));
			else {
				new EditDateHandler(this);
			}
		}
		setClickListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
	}

	private void setClickListeners() {
		// ////// ******* Adding Click Listeners to UI Items ******** //////////

		Button text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_save_entry.setOnClickListener(this);

		Button text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);
		text_voice_camera_delete.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// //////******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.text_voice_camera_save_entry) {
			saveEntry();
		}

		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.text_voice_camera_delete) {
			new FileDelete(_id);

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
			mDatabaseAdapter.close();
			if(intentExtras.containsKey("isFromShowPage")){
				Intent mIntent = new Intent(this, ShowTextActivity.class);
				ArrayList<String> listOnResult = new ArrayList<String>();
				listOnResult.add("");
				Bundle tempBundle = new Bundle();
				tempBundle.putStringArrayList("mDisplayList", listOnResult);
				mEditList = new ArrayList<String>();
				mEditList.addAll(listOnResult);
				mIntent.putExtra("textShowBundle", tempBundle);
				setResult(Activity.RESULT_CANCELED, mIntent);
			}
			finish();
		}
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
		return;
	}

	private void saveEntry() {
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
		// //// ******* Update database if user added additional info *******
		// ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();

		if(!intentExtras.containsKey("isFromShowPage")){
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString("toHighLight", _list.get(DatabaseAdapter.KEY_ID));
			intentExpenseListing.putExtras(mToHighLight);
			intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentExpenseListing);
		} else {
			Intent mIntent = new Intent(this, ShowTextActivity.class);
			Bundle tempBundle = new Bundle();
			ArrayList<String> listOnResult = new ArrayList<String>();
			listOnResult.add(mEditList.get(0));
			listOnResult.add(_list.get(DatabaseAdapter.KEY_TAG));
			listOnResult.add(_list.get(DatabaseAdapter.KEY_AMOUNT));
			if(listOnResult.get(2) == null || listOnResult.get(2) == ""){
				listOnResult.set(2, "?");
			}
			if(_list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) != null ){
				listOnResult.add(new DisplayDate().getLocationDate(_list.get(DatabaseAdapter.KEY_DATE_TIME), mEditList.get(7)));
			} else if (_list.containsKey(DatabaseAdapter.KEY_DATE_TIME) && mEditList.get(7) == null){
				listOnResult.add(new DisplayDate().getLocationDateDate(_list.get(DatabaseAdapter.KEY_DATE_TIME)));
			} else {
				listOnResult.add(mEditList.get(3));
			}
			
			if((!mEditList.get(1).equals(listOnResult.get(1))) || (!mEditList.get(2).equals(new StringProcessing().getStringDoubleDecimal(listOnResult.get(2))))) {
				ShowTextActivity.favID = null;
				HashMap<String, String> listForFav = new HashMap<String, String>();
				listForFav.put(DatabaseAdapter.KEY_FAVORITE, "");
				listForFav.put(DatabaseAdapter.KEY_ID, mEditList.get(0));
				mDatabaseAdapter.open();
				mDatabaseAdapter.editDatabase(listForFav);
				mDatabaseAdapter.close();
				listOnResult.add("");
			} else if(ShowTextActivity.favID == null) {
					listOnResult.add(mEditList.get(4));
				}
				else { 
					listOnResult.add(ShowTextActivity.favID);
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
			tempBundle.putStringArrayList("mDisplayList", listOnResult);
			mIntent.putExtra("textShowBundle", tempBundle);
			mIntent.putExtra("toHighLight", listOnResult.get(0));
			setResult(Activity.RESULT_OK, mIntent);
		}
		finish();
	}

}
