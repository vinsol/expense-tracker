package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExpenseListing extends Activity{
	
	private ListView mListView;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mDataDateList;
//	private MyListAdapter mMyListAdapter;
	private SeparatedListAdapter mSeparatedListAdapter;
	List<HashMap<String, String>> mSubList;
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
//		if(!mDataDateList.isEmpty()){
//			for(int i=0;i<mDataDateList.size();i++){
//				Log.v("mDataDateList "+i, mDataDateList.get(i).toString());
//			}
//		} else {
//			Log.v("mDataDateList ", "empty");
//		}
		
		//////////     *********    Setting adapter to listview   ******   ///////////
		int j = 0;
		mSeparatedListAdapter = new SeparatedListAdapter(this);
		List listString = new ArrayList<List<List<String>>>();
//		for()
		for(int i=0;i<mDataDateList.size();i++){
			List<List<String>> mList = new ArrayList<List<String>>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);
			
			while(j < mSubList.size() && date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
				List<String> _templist = new ArrayList<String>();
				_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
				if(mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null && !mSubList.get(j).get(DatabaseAdapter.KEY_TAG).equals("")){
					_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
					Log.v("true", "true "+j+" "+mSubList.get(j).get(DatabaseAdapter.KEY_TAG)+" io");
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
				mList.add(_templist);
				j++;
			
				}
				listString.add(mList);
				List tt = (List) listString.get(i);
				mSeparatedListAdapter.addSection(i+"", new ArrayAdapter<String>(this,R.layout.expense_listing, tt), mDataDateList);
			
			}
		Log.v("mlist", listString.toString());
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setAdapter(mSeparatedListAdapter);
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
		Log.v("hour", hour+"");
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
