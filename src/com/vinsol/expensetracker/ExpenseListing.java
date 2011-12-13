package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.DisplayDate;
import com.vinsol.expensetracker.utils.GetArrayListFromString;
import com.vinsol.expensetracker.utils.StringProcessing;

public class ExpenseListing extends Activity implements OnItemClickListener {

	private ListView mListView;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mDataDateList;
	private SeparatedListAdapter mSeparatedListAdapter;
	private List<HashMap<String, String>> mSubList;
	private String highlightID = null;
	private UnknownEntryDialog unknownDialog;
	private static int firstVisiblePosition;
	private StringProcessing mStringProcessing;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_listing);
		mStringProcessing = new StringProcessing();
		mConvertCursorToListString = new ConvertCursorToListString(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		// if(setResume){
		mSeparatedListAdapter = new SeparatedListAdapter(this);
		mDataDateList = mConvertCursorToListString.getDateListString();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		Bundle intentExtras = getIntent().getExtras();
		if(intentExtras != null){
			if(intentExtras.containsKey("toHighLight")){
				highlightID = intentExtras.getString("toHighLight");
			}
		}
		int j = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<List<String>>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<List<String>> mList = new ArrayList<List<String>>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);

			while (j < mSubList.size()&& date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
				List<String> _templist = new ArrayList<String>();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis")));
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				if (mDisplayDate.isCurrentWeek()) {
					_templist = getListCurrentWeek(j);
					mList.add(_templist);
					j++;
					if (j < mSubList.size()) {
					} else {
						break;
					}
				} else if (mDisplayDate.isCurrentMonth()) {

					while (mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME).equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
						// //// Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID) +",");

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag()); 

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if (j < mSubList.size()) {
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if(tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)
								mTempSubList.set(0, mTempSubList.get(0)+mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");
							} else {
								break;
							}
						} while (tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
								&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
								&& tempCalendar.get(Calendar.YEAR) == isCurrentYear);
						
						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}
						mTempSubList.add(mStringProcessing.getStringDoubleDecimal(totalAmountString));

						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
						mTempSubList.add("");
						mTempSubList.add("");
						if(highlightID != null){
							if (j <= mSubList.size()) {
								if(mTempSubList.get(0).contains(highlightID)){
									startSubListing(mTempSubList.get(0));
								} 
							}
						}
						mList.add(mTempSubList);
						if (j == mSubList.size()) {
							break;
						}
					}

				} else if (mDisplayDate.isPrevMonths()) {
					while (mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME).equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
						// //// Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag());

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if (j < mSubList.size()) {
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if(tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
										&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
										&& tempCalendar.get(Calendar.YEAR) == isCurrentYear)
									mTempSubList.set(0, mTempSubList.get(0)+mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");
							} else {
								break;
							}

						} while (tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
								&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
								&& tempCalendar.get(Calendar.YEAR) == isCurrentYear);

						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}
						
						mTempSubList.add(mStringProcessing.getStringDoubleDecimal(totalAmountString));
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
						mTempSubList.add("");
						mTempSubList.add("");
						if(highlightID != null){
							if (j <= mSubList.size()) {
								if(mTempSubList.get(0).contains(highlightID)){
									startSubListing(mTempSubList.get(0));
								} 
							}
						}
						mList.add(mTempSubList);
						if (j == mSubList.size()) {
							break;
						}
					}
				} else if (mDisplayDate.isPrevYears()) {
					while (mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME).equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))) {
						// //// Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");

						// /// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						int isYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag());

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double
											.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;

							if (j < mSubList.size()) {
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if((tempCalendar.get(Calendar.MONTH) == isMonth)
										&& (tempCalendar.get(Calendar.YEAR) == isYear))
									mTempSubList.set(0, mTempSubList.get(0)+mSubList.get(j).get(DatabaseAdapter.KEY_ID)+",");
							} else {
								break;
							}

						} while ((tempCalendar.get(Calendar.MONTH) == isMonth)
								&& (tempCalendar.get(Calendar.YEAR) == isYear));
						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}

						mTempSubList.add(mStringProcessing.getStringDoubleDecimal(totalAmountString));

						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_monthwise));
						mTempSubList.add("");
						mTempSubList.add("");
						if(highlightID != null){
							if (j <= mSubList.size()) {
								if(mTempSubList.get(0).contains(highlightID)){
									startSubListing(mTempSubList.get(0));
								} 
							}
						}
						mList.add(mTempSubList);
						if (j >= mSubList.size()) {
							break;
						}
					}
				}
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<String>(this, R.layout.expense_listing, tt), mDataDateList);

		}
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mSeparatedListAdapter);

		if (mDataDateList.size() < 1) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button expense_listing_listview_no_item_button = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			expense_listing_listview_no_item_button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							final ArrayList<String> mArrayList = insertToDatabase();
							unknownDialog = new UnknownEntryDialog(ExpenseListing.this, mArrayList, new android.view.View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ExpenseListing.this);
									mDatabaseAdapter.open();
									mDatabaseAdapter.deleteDatabaseEntryID(mArrayList.get(0));
									mDatabaseAdapter.close();
									unknownDialog.dismiss();
									Toast.makeText(ExpenseListing.this, "Deleted", Toast.LENGTH_SHORT).show();
								}
							});
							
							unknownDialog.setOnCancelListener(new OnCancelListener() {
								
								@Override
								public void onCancel(DialogInterface dialog) {
									DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ExpenseListing.this);
									mDatabaseAdapter.open();
									mDatabaseAdapter.deleteDatabaseEntryID(mArrayList.get(0));
									mDatabaseAdapter.close();
									unknownDialog.dismiss();
								}
							});
						}
					});
		}
		mListView.setSelection(firstVisiblePosition);
		super.onResume();
	}
	
	private ArrayList<String> insertToDatabase() {
		ArrayList<String> mArrayList = new ArrayList<String>();
		for(int i = 0;i<8;i++){
			mArrayList.add("");
		}
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ExpenseListing.this);
		HashMap<String, String> _list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		_list.put(DatabaseAdapter.KEY_DATE_TIME,mCalendar.getTimeInMillis()+"");
		mArrayList.set(6, _list.get(DatabaseAdapter.KEY_DATE_TIME));
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
			mArrayList.set(7, LocationHelper.currentAddress);
		}
		_list.put(DatabaseAdapter.KEY_TYPE, getString(R.string.unknown));
		mArrayList.set(5, _list.get(DatabaseAdapter.KEY_TYPE));
		mDatabaseAdapter.open();
		long _id = mDatabaseAdapter.insert_to_database(_list);
		mDatabaseAdapter.close();
		mArrayList.set(0,Long.toString(_id));
		return mArrayList;
	}
	
	private void startSubListing(String string) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(string);
		for(int checkI=0;checkI<mArrayList.size();checkI++){
			if(mArrayList.get(checkI).equals(highlightID)){
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				expenseSubListing.putExtra("idList", string);
				expenseSubListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(expenseSubListing);
				finish();
			}
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		firstVisiblePosition = mListView.getFirstVisiblePosition();
	}

	private List<String> getListCurrentWeek(int j) {
		
		List<String> _templist = new ArrayList<String>();
		_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
		if (mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TAG).equals("")) {
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
		} else {
			if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))) {
				if(isEntryComplete(mSubList.get(j))){
					_templist.add(getString(R.string.finished_cameraentry));
				} else {
					_templist.add(getString(R.string.unfinished_cameraentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
				if(isEntryComplete(mSubList.get(j))){
					_templist.add(getString(R.string.finished_voiceentry));
				} else {
					_templist.add(getString(R.string.unfinished_voiceentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
				if(isEntryComplete(mSubList.get(j))){		
					_templist.add(getString(R.string.finished_textentry));
				} else {
					_templist.add(getString(R.string.unfinished_textentry));
				}
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.favorite_entry))) {
				_templist.add("Unfinished Favorite Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.unknown))) {
				_templist.add(getString(R.string.unknown_entry));
			}
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != null&& !mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT).equals("")) {
			_templist.add(mStringProcessing.getStringDoubleDecimal(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT)));
		} else {
			_templist.add("?");
		}

		// ///// ******* Adding location date data to list ******* //////////

		if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis") != null 
				&& !mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis").equals("")
				&& mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null
				&& !mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals("")) {
			_templist.add(getLocationDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis"), mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		}

		else if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis") != null
				&& !mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis").equals("")
				&& (mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == null || mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals(""))) {
			_templist.add(getLocationDateDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis")));
		}

		else if ((mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis") == null || mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis").equals(""))&& mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null&& !mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals("")) {
			_templist.add("Unknown time at "+ mSubList.get(j).get(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		}

		else {
			_templist.add("Unknown Location and Date");
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != null
				&& !mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE).equals("")) {
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE));
		} else {
			_templist.add("");
		}

		if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != null
				&& !mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals("")) {
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE));
		} else {
			_templist.add("");
		}

		_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis"));
		_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION));
		return _templist;
	}

	private boolean isEntryComplete(HashMap<String, String> hashMap) {
		if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))) {
			if(hashMap.get(DatabaseAdapter.KEY_AMOUNT) != null){
				if (hashMap.get(DatabaseAdapter.KEY_AMOUNT).contains("?")) {
					return false;
				}
			} else {
				return false;
			}
			File mFileSmall = new File("/sdcard/ExpenseTracker/"
					+ hashMap.get(DatabaseAdapter.KEY_ID) + "_small.jpg");
			File mFile = new File("/sdcard/ExpenseTracker/"
					+ hashMap.get(DatabaseAdapter.KEY_ID) + ".jpg");
			File mFileThumbnail = new File("/sdcard/ExpenseTracker/"
					+ hashMap.get(DatabaseAdapter.KEY_ID) + "_thumbnail.jpg");
			if (mFile.canRead() && mFileSmall.canRead()
					&& mFileThumbnail.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
			if(hashMap.get(DatabaseAdapter.KEY_AMOUNT) != null){
				if (hashMap.get(DatabaseAdapter.KEY_AMOUNT).contains("?")) {
					return false;
				}
			} else {
				return false;
			}
			File mFile = new File("/sdcard/ExpenseTracker/Audio/"
					+ hashMap.get(DatabaseAdapter.KEY_ID) + ".amr");
			if (mFile.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (hashMap.get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
			if(hashMap.get(DatabaseAdapter.KEY_AMOUNT) != null){
				if (hashMap.get(DatabaseAdapter.KEY_AMOUNT).contains("?")) {
					return false;
				}
			}
			if(hashMap.get(DatabaseAdapter.KEY_TAG) != null){
				if (hashMap.get(DatabaseAdapter.KEY_TAG).equals("")) {
					return false;
				} else {
					return true;
				}
			}
		
		}
		return false;
	}

	private String getLocationDateDate(String dateInMillis) {
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
		if (tempCalendar.get(Calendar.MINUTE) != 0){
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + ":" + minute + " " + "PM"+ " at Unknown location";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + ":" + minute + " " + "AM" + " at Unknown location";
			}
		}
		else{ 
			if (tempCalendar.get(Calendar.AM_PM) == 1){
				return hour + "" + " " + "PM" + " at Unknown location";
			}
			if (tempCalendar.get(Calendar.AM_PM) == 0){
				return hour + "" + " " + "AM" + " at Unknown location";
			}
		}
		return null;
	}

	private String getLocationDate(String dateInMillis, String locationData) {
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

	@Override
	public void onItemClick(final AdapterView<?> adapter, View v,final int position, long arg3) {
		@SuppressWarnings("unchecked")
		final ArrayList<String> mTempClickedList = (ArrayList<String>) adapter.getItemAtPosition(position);
		String _id = mTempClickedList.get(0);
		if (!_id.contains(",")) {
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
						DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ExpenseListing.this);
						mDatabaseAdapter.open();
						mDatabaseAdapter.deleteDatabaseEntryID(mTempClickedList.get(0));
						mDatabaseAdapter.close();
						unknownDialog.dismiss();
						Intent intentExpenseListing = new Intent(ExpenseListing.this, ExpenseListing.class);
						intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle mToHighLight = new Bundle();
						mToHighLight.putString("toHighLight", mTempClickedList.get(0));
						intentExpenseListing.putExtras(mToHighLight);
						startActivity(intentExpenseListing);
						Toast.makeText(ExpenseListing.this, "Deleted", Toast.LENGTH_SHORT).show();
					}
				});
			}
		} else {
			Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
			mSubListIntent.putExtra("idList", mTempClickedList.get(0));
			mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mSubListIntent);
		}
	}

	private boolean isEntryComplete(ArrayList<String> toCheckList) {

		if (toCheckList.get(5).equals(getString(R.string.camera))) {
			if(toCheckList.get(2) != null){
				if (toCheckList.get(2).contains("?")) {
					return false;
				}
			}
			File mFileSmall = new File("/sdcard/ExpenseTracker/"
					+ toCheckList.get(0) + "_small.jpg");
			File mFile = new File("/sdcard/ExpenseTracker/"
					+ toCheckList.get(0) + ".jpg");
			File mFileThumbnail = new File("/sdcard/ExpenseTracker/"
					+ toCheckList.get(0) + "_thumbnail.jpg");
			if (mFile.canRead() && mFileSmall.canRead()
					&& mFileThumbnail.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (toCheckList.get(5).equals(getString(R.string.voice))) {
			if(toCheckList.get(2) != null){
				if (toCheckList.get(2).contains("?")) {
					return false;
				}
			}
			File mFile = new File("/sdcard/ExpenseTracker/Audio/"
					+ toCheckList.get(0) + ".amr");
			if (mFile.canRead()) {
				return true;
			} else {
				return false;
			}
		} else if (toCheckList.get(5).equals(getString(R.string.text))) {
			if(toCheckList.get(2) != null){
				if (toCheckList.get(2).contains("?")) {
					return false;
				}
			}
			if(toCheckList.get(1) != null){
				if (toCheckList.get(1).equals(getString(R.string.unfinished_textentry)) || toCheckList.get(1).equals(getString(R.string.finished_textentry))) {
					return false;
				} else {
					return true;
				}
			}
		}

		return false;
	}

}
