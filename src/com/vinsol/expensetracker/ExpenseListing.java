package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

import com.vinsol.expensetracker.utils.DisplayDate;

public class ExpenseListing extends Activity implements OnItemClickListener {

	private ListView mListView;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mDataDateList;
	private SeparatedListAdapter mSeparatedListAdapter;
	private List<HashMap<String, String>> mSubList;
	private Long highlightID = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.expense_listing);

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
				highlightID = intentExtras.getLong("toHighLight");
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
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				mCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME + "Millis")));
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
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
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
						mTempSubList.add(getTotalAmountString(totalAmountString));

						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
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
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag()); // TODO

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
						
						mTempSubList.add(getTotalAmountString(totalAmountString));
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
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
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+ "Millis")));
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						int isYear = tempCalendar.get(Calendar.YEAR);
						mTempSubList.add(tempDisplayDate.getSubListTag()); // TODO

						// /// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).get(
									DatabaseAdapter.KEY_AMOUNT);
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

						mTempSubList.add(getTotalAmountString(totalAmountString));

						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_monthwise));
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
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<String>(
					this, R.layout.expense_listing, tt), mDataDateList);

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
							finish();
						}
					});
		}
		mSeparatedListAdapter.notifyDataSetChanged();
		
		super.onResume();
	}

	private String getTotalAmountString(String totalAmountString) {
		if (totalAmountString.contains("?") && totalAmountString.length() > 1) {
			String temp = totalAmountString.substring(0,
					totalAmountString.length() - 2);
			Double mAmount = Double.parseDouble(temp);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			if (mAmount.toString().contains(".")) {
				if (mAmount.toString().charAt(mAmount.toString().length() - 3) == '.') {
					totalAmountString = mAmount.toString() + " ?";
				} else if (mAmount.toString().charAt(
						mAmount.toString().length() - 2) == '.') {
					totalAmountString = mAmount.toString() + "0 ?";
				}

			} else {
				totalAmountString = mAmount.toString() + ".00 ?";
			}
		} else if (!totalAmountString.contains("?")) {
			String temp = totalAmountString.substring(0,
					totalAmountString.length());
			Double mAmount = Double.parseDouble(temp);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			if (mAmount.toString().contains(".")) {
				if (mAmount.toString().charAt(mAmount.toString().length() - 3) == '.') {
					totalAmountString = mAmount.toString() + "";
				} else if (mAmount.toString().charAt(
						mAmount.toString().length() - 2) == '.') {
					totalAmountString = mAmount.toString() + "0";
				}

			} else {
				totalAmountString = mAmount.toString() + ".00";
			}
		}

		return totalAmountString;
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
			String totalAmountString = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);

			if (totalAmountString.contains("?")&& totalAmountString.length() > 1) {
				String temp = totalAmountString.substring(0,totalAmountString.length() - 2);
				Double mAmount = Double.parseDouble(temp);
				mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
				if (mAmount.toString().contains(".")) {
					if (mAmount.toString().charAt(
							mAmount.toString().length() - 3) == '.') {
						totalAmountString = mAmount.toString() + " ?";
					} else if (mAmount.toString().charAt(
							mAmount.toString().length() - 2) == '.') {
						totalAmountString = mAmount.toString() + "0 ?";
					}

				} else {
					totalAmountString = mAmount.toString() + ".00 ?";
				}
			} else if (!totalAmountString.contains("?")) {
				String temp = totalAmountString.substring(0,
						totalAmountString.length());
				Double mAmount = Double.parseDouble(temp);
				mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);

				if (mAmount.toString().contains(".")) {
					if (mAmount.toString().charAt(
							mAmount.toString().length() - 3) == '.') {
						totalAmountString = mAmount.toString() + "";
					} else if (mAmount.toString().charAt(
							mAmount.toString().length() - 2) == '.') {
						totalAmountString = mAmount.toString() + "0";
					}

				} else {
					totalAmountString = mAmount.toString() + ".00";
				}
			}
			_templist.add(totalAmountString);
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
		_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION));
		if(highlightID != null){
			if(mSubList.get(j).get(DatabaseAdapter.KEY_ID).equals(Long.toString(highlightID))){
				_templist.add(Long.toString(highlightID));
			}
		}
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
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
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
		tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
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
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		@SuppressWarnings("unchecked")
		ArrayList<String> mTempClickedList = (ArrayList<String>) adapter.getItemAtPosition(position);
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
						// TODO
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
					// TODO
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
						// TODO
					}
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if (mTempClickedList.get(5).equals(getString(R.string.unknown))) {
				Intent intentMain = new Intent(this, MainActivity.class);
				intentMain.putExtra("mainBundle", bundle);
				intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intentMain);
				//TODO if unknown entry
			}
		} else {
//			ArrayList<String> idList = new GetArrayListFromString().getListFromTextArea(mTempClickedList.get(0));
			Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
			mSubListIntent.putExtra("idList", mTempClickedList.get(0));
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
