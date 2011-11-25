package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.DisplayDate;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ExpenseListing extends Activity{
	
	private ListView mListView;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mDataDateList;
//	private MyListAdapter mMyListAdapter;
	private SeparatedListAdapter mSeparatedListAdapter;
	List<HashMap<String, String>> mSubList;
	private boolean isLastAttempt = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.expense_listing);
		
		
        ////////   *********     Get Last most accurate location info   *********   /////////
        LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		mConvertCursorToListString = new ConvertCursorToListString(this);
		
		
		/////////     *********    Getting list of dates   *******    ///////////
		mDataDateList = mConvertCursorToListString.getDateListString();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		Log.v("mSubList", mSubList.toString());
		//////////     *********    Setting adapter to listview   ******   ///////////
		int j = 0;
		mSeparatedListAdapter = new SeparatedListAdapter(this);
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<List<String>>>();
		for(int i=0;i<mDataDateList.size();i++){
			List<List<String>> mList = new ArrayList<List<String>>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);
			
			do{
				List<String> _templist = new ArrayList<String>();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				
				if(mDisplayDate.isCurrentWeek()){
					_templist = getListCurrentWeek(j);
					mList.add(_templist);
					j++;
				} else if(mDisplayDate.isCurrentMonth()) {
					
					while(mDisplayDate.getHeaderFooterListDisplayDate().equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
						//////    Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(i+" "+j);
						
						///// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isDayOfMonth = tempCalendar.get(Calendar.DAY_OF_MONTH);
						mTempSubList.add(tempDisplayDate.getSubListTag()); //TODO
						
						/////  Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do{
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if(tempAmount != null && !tempAmount.equals("")){
								try{
									temptotalAmount += Double.parseDouble(tempAmount);
								}catch(NumberFormatException e){}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if(j < mSubList.size()){
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
								tempDisplayDate = new DisplayDate(tempCalendar);
							} else {
								break;
							}
							
						}while(tempCalendar.get(Calendar.DAY_OF_MONTH) == isDayOfMonth);
						if(isTempAmountNull) {
							if(temptotalAmount != 0) {
								totalAmountString = temptotalAmount+" ?";
							}
							else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount+"";
						}
						mTempSubList.add(totalAmountString);
						
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_daywise));
						mList.add(mTempSubList);
						if(isLastAttempt){
							break;
						}
						
						if(j == mSubList.size()){
							j--;
							isLastAttempt = true;
							continue;
						}
					}
					
				} else if (mDisplayDate.isPrevMonths()) {
					
					while(mDisplayDate.getHeaderFooterListDisplayDate().equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
						Log.v("prevmonth", true+"");
						//////    Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(i+" "+j);
						
						///// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						mTempSubList.add(tempDisplayDate.getSubListTag()); //TODO
						
						/////  Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do{
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if(tempAmount != null && !tempAmount.equals("")){
								try{
									temptotalAmount += Double.parseDouble(tempAmount);
								}catch(NumberFormatException e){}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if(j < mSubList.size()){
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
								tempDisplayDate = new DisplayDate(tempCalendar);
							} else {
								break;
							}	
							
						}while(tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth);
						if(isTempAmountNull) {
							if(temptotalAmount != 0) {
								totalAmountString = temptotalAmount+" ?";
							}
							else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount+"";
						}
						mTempSubList.add(totalAmountString);
						
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_weekwise));
						mList.add(mTempSubList);
						if(isLastAttempt){
							break;
						}
						if(j == mSubList.size()){
							j--;
							isLastAttempt = true;
							continue;
						}
					}
				} else 
					
					///////   ListView if previous year
					
				if (mDisplayDate.isPrevYears()) {
					while(mDisplayDate.getHeaderFooterListDisplayDate().equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
//						Log.v("prevYear", msg)
						//////    Adding i+" "+j as id
						List<String> mTempSubList = new ArrayList<String>();
						mTempSubList.add(i+" "+j);
						
						///// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isMonth = tempCalendar.get(Calendar.MONTH);
						mTempSubList.add(tempDisplayDate.getSubListTag()); //TODO
						
						/////  Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do{
							String tempAmount = mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT);
							if(tempAmount != null && !tempAmount.equals("")){
								try{
									temptotalAmount += Double.parseDouble(tempAmount);
								}catch(NumberFormatException e){}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if(j < mSubList.size()){
								tempCalendar.setTimeInMillis(Long.parseLong(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
								tempDisplayDate = new DisplayDate(tempCalendar);
							} else {
								break;
							}
							
						}while(tempCalendar.get(Calendar.MONTH) == isMonth);
						if(isTempAmountNull) {
							if(temptotalAmount != 0) {
								totalAmountString = temptotalAmount+" ?";
							}
							else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount+"";
						}
						mTempSubList.add(totalAmountString);
						
						mTempSubList.add("");
						mTempSubList.add("");
						mTempSubList.add(getString(R.string.sublist_monthwise));
						mList.add(mTempSubList);
						if(isLastAttempt){
							break;
						}
						if(j == mSubList.size()){
							j--;
							isLastAttempt = true;
							continue;
						}
					}
				}
				}while(j < mSubList.size() && date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME)));
				listString.add(mList);
				@SuppressWarnings("rawtypes")
				List tt = (List) listString.get(i);
				mSeparatedListAdapter.addSection(i+"", new ArrayAdapter<String>(this,R.layout.expense_listing, tt), mDataDateList);
			}
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setAdapter(mSeparatedListAdapter);
		
		if(mDataDateList.size() < 1){
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			ImageButton expense_listing_listview_no_item_button = (ImageButton) findViewById(R.id.expense_listing_listview_no_item_button);
			expense_listing_listview_no_item_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		} 
		
	}
	
	private List<String> getListCurrentWeek(int j){
		List<String> _templist = new ArrayList<String>();
		_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
		if(mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TAG).equals("")){
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
		}
		else {
			if(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))){
				_templist.add("Unfinished Camera Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
				_templist.add("Unfinished Voice Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
				_templist.add("Unfinished Text Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.favorite_entry))) {
				_templist.add("Unfinished Favorite Entry");
			} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.unknown))) {
				_templist.add("Unknown Entry");
			}
		}

	
		if(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT).equals("")){
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT));
		} else {
			_templist.add("?");
		}

		///////   *******  Adding location date data to list   *******   //////////
		
		if(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && !mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis").equals("") &&
				mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals("")) {
			_templist.add(getLocationDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis"),mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		} 

		else if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && !mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis").equals("") &&
				(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == null || mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals(""))) {
			_templist.add(getLocationDateDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
		} 

		else if ((mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") == null || mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis").equals("")) &&
				mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION).equals("")) {
			_templist.add("Unknown time at "+ mSubList.get(j).get(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
		} 

		else {
			_templist.add("Unknown Location and Date");
		}
		
		if(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE).equals("")){
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE));
		} else {
			_templist.add("");
		}

		if(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals("")){
			_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE));
		} else {
			_templist.add("");
		}
		return _templist;
	}
	
	private String getLocationDateDate(String dateInMillis) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if(minute.length() == 1){
			minute = "0"+minute;
		}
		if(hour == 0){
			hour = 12;
		}
		if(tempCalendar.get(Calendar.MINUTE) != 0)
			if(tempCalendar.get(Calendar.AM_PM) == 1)
				return hour + ":"+ minute+" "+"PM"+" at Unknown location";
			if(tempCalendar.get(Calendar.AM_PM) == 0)
				return hour + ":"+ minute+" "+"AM"+" at Unknown location";
			
		else 
			if(tempCalendar.get(Calendar.AM_PM) == 1)
				return hour + ":"+" "+"PM"+" at Unknown location";
			if(tempCalendar.get(Calendar.AM_PM) == 0)
				return hour + ":"+" "+"AM"+" at Unknown location";
			
		return null;
	}
	
	private String getLocationDate(String dateInMillis, String locationData) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		int hour = tempCalendar.get(Calendar.HOUR);
		String minute = Integer.toString(tempCalendar.get(Calendar.MINUTE));
		if(minute.length() == 1){
			minute = "0"+minute;
		}
		if(hour == 0){
			hour = 12;
		}
		if(tempCalendar.get(Calendar.MINUTE) != 0)
			if(tempCalendar.get(Calendar.AM_PM) == 1)
				return  hour+ ":"+ minute+" "+"PM"+" at "+locationData;
			if(tempCalendar.get(Calendar.AM_PM) == 0)
				return  hour+ ":"+ minute+" "+"AM"+" at "+locationData;
		else 
			if(tempCalendar.get(Calendar.AM_PM) == 1)
				return hour + ":"+" "+"PM"+" at "+locationData;
			if(tempCalendar.get(Calendar.AM_PM) == 0)
				return hour + ":"+" "+"AM"+" at "+locationData;
		return null;
	}
	
}
