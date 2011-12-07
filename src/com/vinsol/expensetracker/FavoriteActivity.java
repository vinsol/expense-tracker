package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.FileCopyFavorite;

public class FavoriteActivity extends Activity implements OnItemClickListener{
	
	private TextView header_title;
	private ListView text_voice_camera_body_favorite_listview;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView text_voice_camera_date_bar_dateview;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	private ArrayList<String> mEditList;
	private String dateViewString;
	private Long _id = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_voice_camera);

		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		handleUI();
		
		header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
		text_voice_camera_body_favorite_listview = (ListView) findViewById(R.id.text_voice_camera_body_favorite_listview);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getBundleExtra("favoriteBundle");
		
		header_title.setText("Favorite Entry");
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mEditList.get(0));
		}
		
		// ////// ******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, Long.parseLong(mEditList.get(6)));
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}
	}

	@Override
	protected void onResume() {
		try{
			mEditList.get(0);
		} catch(Exception e){
			_id = null;
		}
		mList = mConvertCursorToListString.getFavoriteList();
		mAdapter = new MyAdapter(this, R.layout.expense_listing_inflated_row , mList);
		text_voice_camera_body_favorite_listview.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		if (intentExtras.containsKey("mDisplayList")){
			dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
		} else {
			dateViewString = "";
		}
		text_voice_camera_body_favorite_listview.setOnItemClickListener(this);
		super.onResume();
	}
	
	private class MyAdapter extends ArrayAdapter<String>{
		
		
		private LayoutInflater mInflater;
		List<HashMap<String, String>> mList;
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private MyAdapter(Context context, int resource,List list1) {
			super(context, resource,list1);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mList = list1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.expense_listing_inflated_row, null);
				viewHolder = new ViewHolder();
				viewHolder.expense_listing_row_tag = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_tag);
				viewHolder.expense_listing_row_amount = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_amount);
				viewHolder.expense_listing_row_imageview = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_imageview);
				viewHolder.expense_listing_inflated_row_location_time = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_location_time);
				viewHolder.expense_listing_inflated_row_favorite_icon= (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_favorite_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String tag = mList.get(position).get(DBAdapterFavorite.KEY_TAG);
			String amount = mList.get(position).get(DBAdapterFavorite.KEY_AMOUNT);
			String type = mList.get(position).get(DBAdapterFavorite.KEY_TYPE);
			String _id = mList.get(position).get(DBAdapterFavorite.KEY_ID);
			viewHolder.expense_listing_row_imageview.setFocusable(false);
			viewHolder.expense_listing_row_imageview.setOnClickListener(new MyClickListener(mList.get(position)));
			viewHolder.expense_listing_inflated_row_favorite_icon.setVisibility(View.INVISIBLE);
			viewHolder.expense_listing_inflated_row_location_time.setVisibility(View.GONE);
			if(type.equals(getString(R.string.voice))){
				if(tag != null){
					if(!tag.equals("") &&!tag.equals(R.string.unfinished_voiceentry)){
						viewHolder.expense_listing_row_tag.setText(tag);
					}
				} else {
					viewHolder.expense_listing_row_tag.setText(getString(R.string.finished_voiceentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.expense_listing_row_amount.setText(amount);
					}
				} else {
					viewHolder.expense_listing_row_amount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						
						//TODO image set for voice entry
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ _id + ".amr");
						if (mFile.canRead()) {
							viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.listing_voice_entry_icon);
						} else {
							viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
						}
					}catch(Exception e){
						viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}
				} else {
					viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					return convertView;
				}
			}
			else if(type.equals(getString(R.string.camera))){
				
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_cameraentry)){
						viewHolder.expense_listing_row_tag.setText(tag);
					}
				} else {
					viewHolder.expense_listing_row_tag.setText(getString(R.string.finished_cameraentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.expense_listing_row_amount.setText(amount);
					}
				} else {
					viewHolder.expense_listing_row_amount.setText("?");
				}
				//TODO image set for camera entry
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_thumbnail.jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_small.jpg");
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + ".jpg");
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							System.gc();
							Drawable drawable = Drawable.createFromPath(mFileThumbnail.getPath());
							viewHolder.expense_listing_row_imageview.setImageDrawable(drawable);
						} else {
							viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
						}
					} catch (Exception e) {
						// TODO if image not available on sdcard
						viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
						e.printStackTrace();
					}
				} else {
					viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.no_image_thumbnail);
					return convertView;
					// TODO if sdcard not available
				}
			}
			else if(type.equals(getString(R.string.text))){
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_textentry)){
						viewHolder.expense_listing_row_tag.setText(tag);
					}
				} else {
					viewHolder.expense_listing_row_tag.setText(getString(R.string.finished_textentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.expense_listing_row_amount.setText(amount);
					}
				} else {
					viewHolder.expense_listing_row_amount.setText("?");
				}
				//TODO image set for camera entry
				
				if(tag != null){
					if (!tag.equals("") && !tag.equals(getString(R.string.unfinished_textentry)) ) {
						viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.listing_text_entry_icon);
					} else {
						viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}
				} else {
					viewHolder.expense_listing_row_imageview.setImageResource(R.drawable.text_list_icon_no_tag);
				}
			}
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		TextView expense_listing_row_tag;
		TextView expense_listing_row_amount;
		ImageView expense_listing_row_imageview;
		ImageView expense_listing_inflated_row_favorite_icon;
		TextView expense_listing_inflated_row_location_time;
	}
	
	private void handleUI() {
		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		ScrollView mScrollView = (ScrollView) findViewById(R.id.text_voice_camera_body);
		mScrollView.setVisibility(View.GONE);
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.text_voice_camera_body_favorite);
		mRelativeLayout.setVisibility(View.VISIBLE);
		LinearLayout text_voice_camera_footer = (LinearLayout) findViewById(R.id.text_voice_camera_footer);
		text_voice_camera_footer.setVisibility(View.GONE);
	}
	
	private class MyClickListener implements OnClickListener {

		HashMap<String, String> mListenerList;

		public MyClickListener(HashMap<String, String> mlist) {
			mListenerList = mlist;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.expense_listing_inflated_row_imageview) {
				if (mListenerList != null)
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						String id = mListenerList.get(DBAdapterFavorite.KEY_ID);
						if (mListenerList.get(DBAdapterFavorite.KEY_TYPE).equals(getString(R.string.voice))) {
							File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ id + ".amr");
							if (mFile.canRead()) {
								new AudioPlayDialog(FavoriteActivity.this,id,"fav");
							} else {
								// TODO audio image change
							}
						} else if (mListenerList.get(DBAdapterFavorite.KEY_TYPE).equals(getString(R.string.camera))) {
							File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ id + ".jpg");
							File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ id + "_small.jpg");
							File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ id + "_thumbnail.jpg");
							if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
								new ImageViewDialog(FavoriteActivity.this,Long.parseLong(id),"fav");
							} else {
								// TODO if image not found
							}
						}
					}
				if (mListenerList.get(DBAdapterFavorite.KEY_TYPE).equals(getString(R.string.text))) {
					if (!mListenerList.get(DBAdapterFavorite.KEY_TAG).equals(getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(FavoriteActivity.this, mListenerList.get(DBAdapterFavorite.KEY_TAG));
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		saveEntry((HashMap<String, String>) adapter.getItemAtPosition(position));
	}

	private void saveEntry(HashMap<String, String> adapterList) {
		String favID = adapterList.get(DBAdapterFavorite.KEY_ID);
		String type = adapterList.get(DBAdapterFavorite.KEY_TYPE);
		String tag = adapterList.get(DBAdapterFavorite.KEY_TAG);
		String amount = adapterList.get(DBAdapterFavorite.KEY_AMOUNT);
		Long idCreated;
		HashMap<String, String> toInsert = new HashMap<String, String>();
		
		if(_id != null){
			toInsert.put(DatabaseAdapter.KEY_ID, Long.toString(_id));
		}
		
		if(amount != null){
			if(!amount.contains("?") && !amount.equals(""))
				toInsert.put(DatabaseAdapter.KEY_AMOUNT, amount);
		}
		
		if(favID != null) {
			if(!favID.equals(""))
				toInsert.put(DatabaseAdapter.KEY_FAVORITE, favID);
		}
		
		if(type.equals(getString(R.string.camera))) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				try{
					toInsert.put(DatabaseAdapter.KEY_TYPE, type);
					if(tag != null) {
						if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_cameraentry)) && !tag.equals(getString(R.string.finished_cameraentry)))
							toInsert.put(DatabaseAdapter.KEY_TAG, tag);
					}
					if(_id == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
						}
					}
					if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(
										text_voice_camera_date_bar_dateview.getText()
												.toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME,
										mDateHelper.getTimeMillis() + "");
							} else {
								if(!intentExtras.containsKey("timeInMillis")){
									DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(_id == null){
						mDatabaseAdapter.open();
						idCreated = mDatabaseAdapter.insert_to_database(toInsert);
						mDatabaseAdapter.close();
						new FileCopyFavorite(Long.parseLong(favID), idCreated,"from");
						File mFile = new File("/sdcard/ExpenseTracker/"+idCreated+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/"+idCreated+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/"+idCreated+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(idCreated));
							mDatabaseAdapter.close();
						}
					} else {
						new FileCopyFavorite(Long.parseLong(favID), _id,"from");
						File mFile = new File("/sdcard/ExpenseTracker/"+_id+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/"+_id+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/"+_id+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
							startActivity(expenseListingIntent);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editDatabase(toInsert);
							mDatabaseAdapter.close();
							finish();
						} else {
							Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
						}
					}
				}
				catch (Exception e){
					Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "you cannot use camera entry without sdcard", Toast.LENGTH_LONG).show();
			}
		} else if(type.equals(getString(R.string.voice))){
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				try{
					toInsert.put(DatabaseAdapter.KEY_TYPE, type);
					if(tag != null){
						if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_voiceentry)) && !tag.equals(getString(R.string.finished_voiceentry)))
							toInsert.put(DatabaseAdapter.KEY_TAG, tag);
					}
					if(_id == null){
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
						}
					}
					if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(
										text_voice_camera_date_bar_dateview.getText()
												.toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME,
										mDateHelper.getTimeMillis() + "");
							} else {
								if(!intentExtras.containsKey("timeInMillis")){
									DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(_id == null){
						mDatabaseAdapter.open();
						idCreated = mDatabaseAdapter.insert_to_database(toInsert);
						mDatabaseAdapter.close();
						new FileCopyFavorite(Long.parseLong(favID), idCreated,"from");
						File mFile = new File("/sdcard/ExpenseTracker/Audio/"+idCreated+".amr");
						if(mFile.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(idCreated));
							mDatabaseAdapter.close();
						}
					} else {
						new FileCopyFavorite(Long.parseLong(favID), _id,"from");
						File mFile = new File("/sdcard/ExpenseTracker/Audio/"+_id+".amr");
						if(mFile.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
							startActivity(expenseListingIntent);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editDatabase(toInsert);
							mDatabaseAdapter.close();
							finish();
						} else {
							Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
						}
					}
				} catch(Exception e){
					Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "you cannot use voice entry without sdcard", Toast.LENGTH_LONG).show();
			}
		} else if(type.equals(getString(R.string.text))) {
			try{
				toInsert.put(DatabaseAdapter.KEY_TYPE, type);
				if(tag != null){
					if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_textentry)) && !tag.equals(getString(R.string.finished_textentry)))
						toInsert.put(DatabaseAdapter.KEY_TAG, tag);
				}
				if(_id == null){
					if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
						toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
					}
				}
				if (!text_voice_camera_date_bar_dateview.getText().toString().equals(dateViewString)) {
					try {
						if (!intentExtras.containsKey("mDisplayList")) {
							DateHelper mDateHelper = new DateHelper(
									text_voice_camera_date_bar_dateview.getText()
											.toString());
							toInsert.put(DatabaseAdapter.KEY_DATE_TIME,
									mDateHelper.getTimeMillis() + "");
						} else {
							if(!intentExtras.containsKey("timeInMillis")){
								DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
								DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString(),mCalendar);
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(_id == null){
					mDatabaseAdapter.open();
					mDatabaseAdapter.insert_to_database(toInsert);
					mDatabaseAdapter.close();
					Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
					startActivity(expenseListingIntent);
					finish();
				} else {
					mDatabaseAdapter.open();
					mDatabaseAdapter.editDatabase(toInsert);
					mDatabaseAdapter.close();
					Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
					startActivity(expenseListingIntent);
					finish();
				}
			} catch(Exception e){
				Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
			}
		}
	}
}
