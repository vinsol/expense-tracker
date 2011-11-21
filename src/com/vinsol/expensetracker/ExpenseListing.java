package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;
import android.app.Activity;
import android.os.Bundle;
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
		for(int i=0;i<mDataDateList.size();i++){
			List<String> _list = new ArrayList<String>();
			String date = mDataDateList.get(i).get(DatabaseAdapter.KEY_DATE_TIME);
			while(j < mSubList.size() && date.equals(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME))){
				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_ID));
				j++;
			}
//			if(mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != null || mSubList.get(j).get(DatabaseAdapter.KEY_TAG) != "")
//				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_TAG));
//			else {
//				if(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.camera))){
//					_list.add("Unfinished Camera Entry");
//				} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.voice))) {
//					_list.add("Unfinished Voice Entry");
//				} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.text))) {
//					_list.add("Unfinished Text Entry");
//				} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.favorite_entry))) {
//					_list.add("Unfinished Favorite Entry");
//				} else if (mSubList.get(j).get(DatabaseAdapter.KEY_TYPE).equals(getString(R.string.unknown))) {
//					_list.add("Unknown Entry");
//				}
//			}
//			
//			if(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != null || mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT) != ""){
//				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_AMOUNT));
//			} else {
//				_list.add("");
//			}
//			
//			///////   *******  Adding location date data to list   *******   //////////
//			
//			if(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != "" &&
//					mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != "") {
//				_list.add(getLocationDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis"),mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
//			} 
//			
//			else if (mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != null && mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") != "" &&
//					(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == null || mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) == "")) {
//				_list.add(getLocationDateDate(mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis")));
//			} 
//			
//			else if ((mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") == null || mSubList.get(j).get(DatabaseAdapter.KEY_DATE_TIME+"Millis") == "") &&
//					mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != null && mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION) != "") {
//				_list.add("Unknown time at "+ mSubList.get(j).get(mSubList.get(j).get(DatabaseAdapter.KEY_LOCATION)));
//			} 
//			
//			else {
//				_list.add("Unknown Location and Date");
//			}
//			
//			
//			
//			if(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != null || mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE) != ""){
//				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_FAVORITE));
//			} else {
//				_list.add("");
//			}
//			
//			if(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != null || mSubList.get(j).get(DatabaseAdapter.KEY_TYPE) != ""){
//				_list.add(mSubList.get(j).get(DatabaseAdapter.KEY_TYPE));
//			} else {
//				_list.add("");
//			}
			mSeparatedListAdapter.addSection(i+"", new ArrayAdapter<String>(this,R.layout.expense_listing, _list) , mSubList, mDataDateList);
			
		}
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setAdapter(mSeparatedListAdapter);
	}
	
//	private String getLocationDateDate(String dateInMillis) {
//		Calendar tempCalendar = Calendar.getInstance();
//		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
//		if(tempCalendar.get(Calendar.MINUTE) != 0)
//			return tempCalendar.get(Calendar.HOUR) + ":"+ tempCalendar.get(Calendar.MINUTE)+" "+tempCalendar.get(Calendar.AM_PM)+" at Unknown location";
//		else 
//			return tempCalendar.get(Calendar.HOUR) + ":"+" "+tempCalendar.get(Calendar.AM_PM)+" at Unknown location";
//	}
//	
//	private String getLocationDate(String dateInMillis, String locationData) {
//		Calendar tempCalendar = Calendar.getInstance();
//		tempCalendar.setTimeInMillis(Long.parseLong(dateInMillis));
//		if(tempCalendar.get(Calendar.MINUTE) != 0)
//			return tempCalendar.get(Calendar.HOUR) + ":"+ tempCalendar.get(Calendar.MINUTE)+" "+tempCalendar.get(Calendar.AM_PM)+" at "+locationData;
//		else 
//			return tempCalendar.get(Calendar.HOUR) + ":"+" "+tempCalendar.get(Calendar.AM_PM)+" at "+locationData;
//	}
	
}
