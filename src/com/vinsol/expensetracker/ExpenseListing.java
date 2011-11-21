package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.SeparatedListAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
//		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		Log.v("created", "created");
		mConvertCursorToListString = new ConvertCursorToListString(this);
		
		
		/////////     *********    Getting list of dates   *******    ///////////
		mDataDateList = mConvertCursorToListString.getDateListString();
		mSubList = mConvertCursorToListString.getListStringParticularDate();
		if(!mDataDateList.isEmpty()){
			for(int i=0;i<mDataDateList.size();i++){
				Log.v("mDataDateList "+i, mDataDateList.get(i).toString());
			}
		} else {
			Log.v("mDataDateList ", "empty");
		}
//		RelativeLayout expense_listing_list_date_amount_view = (RelativeLayout) findViewById(R.id.expense_listing_list_date_amount_view);
//		LinearLayout expense_listing_list_add_expenses = (LinearLayout) findViewById(R.id.expense_listing_list_add_expenses);
		
		
		//////////     *********    Setting adapter to listview   ******   ///////////
		
//		for(int i=0;i<mSubList.size();i++){
		{
			HashMap<String, String> _hashmap = mSubList.get(0);
			List<String> _list = new ArrayList<String>();
			_list.add(_hashmap.get(DatabaseAdapter.KEY_ID));
			_list.add("2");
			_list.add("5");
			mSeparatedListAdapter = new SeparatedListAdapter(this);
			mSeparatedListAdapter.addSection("Array test", new ArrayAdapter<String>(this,
					R.layout.expense_listing, _list));
			mSeparatedListAdapter.addSection("Array F", new ArrayAdapter<String>(this,
					R.layout.expense_listing, _list));
//			mMyListAdapter = new MyListAdapter(this, R.layout.expense_listing_inflated_row, _list);
//			View list_header = getLayoutInflater().inflate(R.layout.mainlist_header_view, null);
//			mListView.addHeaderView(list_header);
			
		}
//		Toast.makeText(this, ""+mSubList.size(), Toast.LENGTH_LONG);
//		mListView.addFooterView(expense_listing_list_add_expenses);
		
		
//		mListView.setAdapter(mMyListAdapter);
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setAdapter(mSeparatedListAdapter);
//		this.setContentView(mListView);
		
		
		
	}
	
	
	private class MyListAdapter extends ArrayAdapter<String>{
		
//		List<HashMap<String, String>> mSubList;
//		MySubListAdapter mMySubListAdapter;
//		Context mContext;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private MyListAdapter(Context context, int resource,List list1) {
			super(context, resource,list1);
//			mContext = context;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row =inflater.inflate(R.layout.expense_listing_inflated_row, parent,false);

			
			
			
			HashMap<String, String> mHashMap = new HashMap<String, String>();
			mHashMap = mSubList.get(position);
			
//			
//			
			TextView expense_listing_inflated_row_tag = (TextView) row.findViewById(R.id.expense_listing_inflated_row_tag);
			TextView expense_listing_inflated_row_location_time = (TextView) row.findViewById(R.id.expense_listing_inflated_row_location_time);
			TextView expense_listing_inflated_row_amount = (TextView) row.findViewById(R.id.expense_listing_inflated_row_amount);
			expense_listing_inflated_row_tag.setText(mHashMap.get(DatabaseAdapter.KEY_TAG));
			expense_listing_inflated_row_amount.setText(mHashMap.get(DatabaseAdapter.KEY_AMOUNT));
			expense_listing_inflated_row_location_time.setText(mHashMap.get(DatabaseAdapter.KEY_LOCATION));
			
//			TextView expenses_listing_list_date_view = (TextView) row.findViewById(R.id.expenses_listing_list_date_view);
//			TextView expenses_listing_list_amount_view = (TextView) row.findViewById(R.id.expenses_listing_list_amount_view);
//			TextView expenses_listing_add_expenses_textview = (TextView) row.findViewById(R.id.expenses_listing_add_expenses_textview);
//			ListView expense_listing_inflated_listview = (ListView) row.findViewById(R.id.expense_listing_inflated_listview);
//			HashMap<String, String> mHashMap = new HashMap<String, String>();
//			
//			mHashMap = mDataDateList.get(position);
//			expenses_listing_add_expenses_textview.setText("Add Expenses to "+mHashMap.get(DatabaseAdapter.KEY_DATE_TIME));
//			expenses_listing_list_date_view.setText(mHashMap.get(DatabaseAdapter.KEY_DATE_TIME));
//			expenses_listing_list_amount_view.setText(mHashMap.get(DatabaseAdapter.KEY_AMOUNT));
//			mSubList = mConvertCursorToListString.getListStringParticularDate();
//			Toast.makeText(mContext, "MainListView" , Toast.LENGTH_LONG).show();
			
//			mMySubListAdapter = new MySubListAdapter(ExpenseListing.this, R.id.expense_listing_inflated_listview, mSubList);
//			
//			expense_listing_inflated_listview.setAdapter(mMySubListAdapter);
			
			
//			expense_listing_inflated_listview.set
			
			return row;
		}
//		
//		private class MySubListAdapter extends ArrayAdapter<String>{
//			
//			@SuppressWarnings({ "rawtypes", "unchecked" })
//			private MySubListAdapter(Context context, int resource,List list1) {
//				super(context, resource,list1);
//				
//			}
//		
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				
//				LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				View row =inflater.inflate(R.layout.expense_listing_inflated_row, parent,false);
//				HashMap<String, String> mHashMap = new HashMap<String, String>();
//				mHashMap = mSubList.get(position);
//				
//				
//				TextView expense_listing_inflated_row_tag = (TextView) row.findViewById(R.id.expense_listing_inflated_row_tag);
//				TextView expense_listing_inflated_row_location_time = (TextView) row.findViewById(R.id.expense_listing_inflated_row_location_time);
//				TextView expense_listing_inflated_row_amount = (TextView) row.findViewById(R.id.expense_listing_inflated_row_amount);
//				expense_listing_inflated_row_tag.setText(mHashMap.get(DatabaseAdapter.KEY_TAG));
//				expense_listing_inflated_row_amount.setText(mHashMap.get(DatabaseAdapter.KEY_AMOUNT));
//				expense_listing_inflated_row_location_time.setText(mHashMap.get(DatabaseAdapter.KEY_LOCATION));
//				Toast.makeText(ExpenseListing.this, "Yo" , Toast.LENGTH_LONG).show();
//				
//				return row;
//			}	
//	    }
    }
	
}
