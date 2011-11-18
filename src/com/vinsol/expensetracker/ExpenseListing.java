package com.vinsol.expensetracker;

import java.util.HashMap;
import java.util.List;

import com.vinsol.expensetracker.location.LocationLast;

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
	private MyListAdapter mMyListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.expense_listing);
		
		
        ////////   *********     Get Last most accurate location info   *********   /////////
        LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		Log.v("created", "created");
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDataDateList = mConvertCursorToListString.getDateListString();
		if(!mDataDateList.isEmpty()){
			for(int i=0;i<mDataDateList.size();i++){
				Log.v("mDataDateList "+i, mDataDateList.get(i).toString());
			}
		} else {
			Log.v("mDataDateList ", "empty");
		}
		
		mMyListAdapter = new MyListAdapter(this, R.layout.expense_listing_inflatedlistview, mDataDateList);
		mListView.setAdapter(mMyListAdapter);
	}
	
	
	private class MyListAdapter extends ArrayAdapter<String>{
		
		List<HashMap<String, String>> mSubList;
		MySubListAdapter mMySubListAdapter;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private MyListAdapter(Context context, int resource,List list1) {
			super(context, resource,list1);
			mSubList = mConvertCursorToListString.getListString();
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row =inflater.inflate(R.layout.expense_listing_inflatedlistview, parent,false);

			TextView expenses_listing_list_date_view = (TextView) row.findViewById(R.id.expenses_listing_list_date_view);
			TextView expenses_listing_list_amount_view = (TextView) row.findViewById(R.id.expenses_listing_list_amount_view);
			TextView expenses_listing_add_expenses_textview = (TextView) row.findViewById(R.id.expenses_listing_add_expenses_textview);
			ListView expense_listing_inflated_listview = (ListView) row.findViewById(R.id.expense_listing_inflated_listview);
			HashMap<String, String> mHashMap = new HashMap<String, String>();
			
			mHashMap = mDataDateList.get(position);
			expenses_listing_add_expenses_textview.setText("Add Expenses to "+mHashMap.get(DatabaseAdapter.KEY_DATE_TIME));
			expenses_listing_list_date_view.setText(mHashMap.get(DatabaseAdapter.KEY_DATE_TIME));
			expenses_listing_list_amount_view.setText(mHashMap.get(DatabaseAdapter.KEY_AMOUNT));
			
			mMySubListAdapter = new MySubListAdapter(ExpenseListing.this, R.layout.expense_listing_inflated_row, mSubList);
			expense_listing_inflated_listview.setAdapter(mMySubListAdapter);
			return row;
		}
		
		private class MySubListAdapter extends ArrayAdapter<String>{
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			private MySubListAdapter(Context context, int resource,List list1) {
				super(context, resource,list1);
			}
		
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View row =inflater.inflate(R.layout.expense_listing_inflated_row, parent,false);

				////  TODO TODO 
				
				
				return row;
			}	
	    }
    }
	
}
