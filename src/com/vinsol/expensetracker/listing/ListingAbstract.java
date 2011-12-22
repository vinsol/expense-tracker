package com.vinsol.expensetracker.listing;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.show.ShowVoiceActivity;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.StringProcessing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

abstract class ListingAbstract extends Activity implements OnItemClickListener{

	protected List<HashMap<String, String>> mDataDateList;
	protected SeparatedListAdapter mSeparatedListAdapter;
	protected List<HashMap<String, String>> mSubList;
	protected ConvertCursorToListString mConvertCursorToListString;
	protected StringProcessing mStringProcessing;
	protected ListView mListView;
	protected String highlightID = null;
	protected static int firstVisiblePosition;
	protected UnknownEntryDialog unknownDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_listing);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mStringProcessing = new StringProcessing();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSeparatedListAdapter = new SeparatedListAdapter(this);
	}

	protected boolean isEntryComplete(HashMap<String, String> hashMap) {
		if(isAmountValid(hashMap.get(DatabaseAdapter.KEY_AMOUNT))) {
			if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))) {
				return isCameraFileReadable(hashMap.get(DatabaseAdapter.KEY_ID));
			} else if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
				return isAudioFileReadable(hashMap.get(DatabaseAdapter.KEY_ID));
			} else if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
				return isTagValid(hashMap.get(DatabaseAdapter.KEY_TAG));
			}
		}
		return false;
	}
	
	private boolean isAmountValid(String amount){
		if( amount!= null){
			if (amount.contains("?")) {
				return false;
			} else {
				return true;
			} 
				
		} else {
			return false;
		}
	}
	private boolean isTagValid(String tag) {
		if(tag != null){
			if (tag.equals("")) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean isAudioFileReadable(String userId) {
		File mFile = new File("/sdcard/ExpenseTracker/Audio/" + userId + ".amr");
		if (mFile.canRead()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isCameraFileReadable(String userId) {
		File mFileSmall = new File("/sdcard/ExpenseTracker/" + userId + "_small.jpg");
		File mFile = new File("/sdcard/ExpenseTracker/" + userId + ".jpg");
		File mFileThumbnail = new File("/sdcard/ExpenseTracker/" + userId + "_thumbnail.jpg");
		if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
			return true;
		}
		return false;
	}

	protected boolean isEntryComplete(ArrayList<String> toCheckList) {
		if(isAmountValid(toCheckList.get(2))) {
			if (toCheckList.get(5).equals(getString(R.string.camera))) {
				return isCameraFileReadable(toCheckList.get(0));
			} else if (toCheckList.get(5).equals(getString(R.string.voice))) {
				return isAudioFileReadable(toCheckList.get(0));
			} else if (toCheckList.get(5).equals(getString(R.string.text))) {
				return isTagValid(toCheckList.get(1));
			}
		}
		return false;
	}
	
	protected String getLocationDate(String dateInMillis, String locationData) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (hour == 0) {
			hour = 12;
		}
		if(locationData == null || locationData.equals("")){
				locationData = "Unknown location";
		}
		if (tempCalendar.get(Calendar.MINUTE) != 0){
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + ":" + minute + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + minute + " " + "AM" + " at " + locationData;
			}
		}
		else{
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + "" + " " + "PM" + " at " + locationData;
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + " " + "AM" + " at " + locationData;
			}
		}
		return null;
	}
	
	protected List<String> getListCurrentWeek(int j) {
		List<String> templist = new ArrayList<String>();
		templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
		if (mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TAG).equals("")) {
			templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
		} else {
			if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))) {
				if(isEntryComplete(mSubList.get(j))){
					templist.add(getString(R.string.finished_cameraentry));
				} else {
					templist.add(getString(R.string.unfinished_cameraentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
				if(isEntryComplete(mSubList.get(j))){
					templist.add(getString(R.string.finished_voiceentry));
				} else {
					templist.add(getString(R.string.unfinished_voiceentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
				if(isEntryComplete(mSubList.get(j))){		
					templist.add(getString(R.string.finished_textentry));
				} else {
					templist.add(getString(R.string.unfinished_textentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.favorite_entry))) {
				templist.add("Unfinished Favorite Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.unknown))) {
				templist.add(getString(R.string.unknown_entry));
			}
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != null&& !mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT).equals("")) {
			templist.add(mStringProcessing.getStringDoubleDecimal(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT)));
		} else {
			templist.add("?");
		}

		// ///// ******* Adding location date data to list ******* //////////

		if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis") != null  && !mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis").equals("")) {
			templist.add(getLocationDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis"), mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		} else if ((mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis") == null || mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis").equals(""))&& mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null&& !mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals("")) {
			templist.add("Unknown time at "+ mSubList.get(j).get(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		} else {
			templist.add("Unknown Location and Date");
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE).equals("")) {
			templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE));
		} else {
			templist.add("");
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals("")) {
			templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE));
		} else {
			templist.add("");
		}

		templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis"));
		templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION));
		return templist;
	}
	
	@Override
	public void onItemClick(final AdapterView<?> adapter, View v,final int position, long arg3) {
		@SuppressWarnings("unchecked")
		final ArrayList<String> mTempClickedList = (ArrayList<String>) adapter.getItemAtPosition(position);
		String userId = mTempClickedList.get(0);
		if (!userId.contains(",")) {
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("mDisplayList", mTempClickedList);
			if (mTempClickedList.get(5).equals(getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if (!isEntryComplete(mTempClickedList)) {
						Intent intentCamera = new Intent(this,CameraActivity.class);
						intentCamera.putExtra("cameraBundle", bundle);
						startActivity(intentCamera);
					} else {
						Intent intentCamera = new Intent(this,ShowCameraActivity.class);
						intentCamera.putExtra("cameraShowBundle", bundle);
						startActivity(intentCamera);
					}
				} else {
					Toast.makeText(this, "sdcard not available",Toast.LENGTH_SHORT).show();
				}
			} else if (mTempClickedList.get(5).equals(getString(R.string.text))) {
				if (!isEntryComplete(mTempClickedList)) {
					Intent intentTextEntry = new Intent(this, TextEntry.class);
					intentTextEntry.putExtra("textEntryBundle", bundle);
					startActivity(intentTextEntry);
				} else {
					Intent intentTextShow = new Intent(this,ShowTextActivity.class);
					intentTextShow.putExtra("textShowBundle", bundle);
					startActivity(intentTextShow);
				}

			} else if (mTempClickedList.get(5).equals(getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if (!isEntryComplete(mTempClickedList)) {
						Intent intentVoice = new Intent(this, Voice.class);
						intentVoice.putExtra("voiceBundle", bundle);
						startActivity(intentVoice);
					} else {
						Intent intentVoiceShow = new Intent(this,ShowVoiceActivity.class);
						intentVoiceShow.putExtra("voiceShowBundle", bundle);
						startActivity(intentVoiceShow);
					}
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if (mTempClickedList.get(5).equals(getString(R.string.unknown))) {
				unknownDialog = new UnknownEntryDialog(this,mTempClickedList,new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ListingAbstract.this);
						mDatabaseAdapter.open();
						mDatabaseAdapter.deleteDatabaseEntryID(mTempClickedList.get(0));
						mDatabaseAdapter.close();
						unknownDialog.dismiss();
						unknownDialogAction(mTempClickedList.get(0));
						Toast.makeText(ListingAbstract.this, "Deleted", Toast.LENGTH_SHORT).show();
					}
				});
			}
		} else {
			onClickElse(mTempClickedList.get(0));
		}
	}
	
	protected void unknownDialogAction(String userId){}
	
	protected void onClickElse(String userId) {}
	
	protected void noItemButtonAction(Button noItemButton){}
	
	protected void getListToAdd(){}
	
	protected void doOperationsOnListview(){
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mSeparatedListAdapter);
		if (mDataDateList.size() < 1) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button noItemButton = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			noItemButtonAction(noItemButton);
		}
		mListView.setSelection(firstVisiblePosition);
	}
}
