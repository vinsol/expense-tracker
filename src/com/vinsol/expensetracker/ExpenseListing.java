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
			List<String> _list = new ArrayList<String>();
			List<List<String>> mList = new ArrayList<List<String>>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);
//			while(j < mSubList.size() && date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
//				listString = new ArrayList<List<String>>();
//				j++;
//			}
			
			while(j < mSubList.size() && date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
				List<String> _templist = new ArrayList<String>();
				_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
				
			
				if(mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null || mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != "")
					_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
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
			
				if(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != null || mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != ""){
					_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT));
				} else {
					_templist.add("");
				}
			
				///////   *******  Adding location date data to list   *******   //////////
			
				if(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != "" &&
					mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != "") {
					_templist.add(getLocationDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis"),mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
				} 
			
				else if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != "" &&
						(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == null || mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == "")) {
					_templist.add(getLocationDateDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
				} 
			
				else if ((mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") == null || mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") == "") &&
						mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != "") {
					_templist.add("Unknown time at "+ mSubList.get(j).get(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
				} 
			
				else {
					_templist.add("Unknown Location and Date");
				}
			
			
			
				if(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != null || mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != ""){
					_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE));
				} else {
					_templist.add("");
				}
			
				if(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != null || mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != ""){
					_templist.add(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE));
				} else {
					_templist.add("");
				}
				mList.add(_templist);
				j++;
			
				}
				listString.add(mList);
				List tt = (List) listString.get(i);
				mSeparatedListAdapter.addSection(i+"", new ArrayAdapter<String>(this,R.layout.expense_listing, tt) , listString, mDataDateList);
			
			
			}
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setAdapter(mSeparatedListAdapter);
	}
	
	private String getLocationDateDate(String dateInMillis) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		if(tempCalendar.get(Calendar.MINUTE) != 0)
			return tempCalendar.get(Calendar.HOUR) + ":"+ tempCalendar.get(Calendar.MINUTE)+" "+tempCalendar.get(Calendar.AM_PM)+" at Unknown location";
		else 
			return tempCalendar.get(Calendar.HOUR) + ":"+" "+tempCalendar.get(Calendar.AM_PM)+" at Unknown location";
	}
	
	private String getLocationDate(String dateInMillis, String locationData) {
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
		if(tempCalendar.get(Calendar.MINUTE) != 0)
			return tempCalendar.get(Calendar.HOUR) + ":"+ tempCalendar.get(Calendar.MINUTE)+" "+tempCalendar.get(Calendar.AM_PM)+" at "+locationData;
		else 
			return tempCalendar.get(Calendar.HOUR) + ":"+" "+tempCalendar.get(Calendar.AM_PM)+" at "+locationData;
	}
	
}
