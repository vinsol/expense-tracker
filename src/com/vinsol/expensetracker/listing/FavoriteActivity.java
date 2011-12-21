package com.vinsol.expensetracker.listing;

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

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.favorite.DBAdapterFavorite;
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.utils.ConvertCursorToListString;
import com.vinsol.expensetracker.utils.DateHandler;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.FileCopyFavorite;
import com.vinsol.expensetracker.utils.ImagePreview;
import com.vinsol.expensetracker.utils.LocationHelper;

public class FavoriteActivity extends Activity implements OnItemClickListener{
	
	private TextView headerTitle;
	private ListView editFavoriteListview;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<HashMap<String, String>> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView editDateBarDateview;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	private ArrayList<String> mEditList;
	private String dateViewString;
	private Long userId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_page);

		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		handleUI();
		
		headerTitle = (TextView) findViewById(R.id.edit_header_title);
		editFavoriteListview = (ListView) findViewById(R.id.edit_body_favorite_listview);
		editDateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getBundleExtra("favoriteBundle");
		
		headerTitle.setText("Favorite Entry");
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new ArrayList<String>();
			mEditList = intentExtras.getStringArrayList("mDisplayList");
			userId = Long.parseLong(mEditList.get(0));
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
			userId = null;
		}
		mList = mConvertCursorToListString.getFavoriteList();
		mAdapter = new MyAdapter(this, R.layout.expense_listing_inflated_row , mList);
		editFavoriteListview.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		if (intentExtras.containsKey("mDisplayList")){
			dateViewString = editDateBarDateview.getText().toString();
		} else {
			dateViewString = "";
		}
		editFavoriteListview.setOnItemClickListener(this);
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
				viewHolder.rowTag = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_tag);
				viewHolder.rowAmount = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_amount);
				viewHolder.rowImageview = (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_imageview);
				viewHolder.rowLocationTime = (TextView) convertView.findViewById(R.id.expense_listing_inflated_row_location_time);
				viewHolder.rowFavoriteIcon= (ImageView) convertView.findViewById(R.id.expense_listing_inflated_row_favorite_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String tag = mList.get(position).get(DBAdapterFavorite.KEY_TAG);
			String amount = mList.get(position).get(DBAdapterFavorite.KEY_AMOUNT);
			String type = mList.get(position).get(DBAdapterFavorite.KEY_TYPE);
			String _id = mList.get(position).get(DBAdapterFavorite.KEY_ID);
			viewHolder.rowImageview.setFocusable(false);
			viewHolder.rowImageview.setOnClickListener(new MyClickListener(mList.get(position)));
			viewHolder.rowFavoriteIcon.setVisibility(View.INVISIBLE);
			viewHolder.rowLocationTime.setVisibility(View.GONE);
			if(type.equals(getString(R.string.voice))){
				if(tag != null){
					if(!tag.equals("") &&!tag.equals(R.string.unfinished_voiceentry)){
						viewHolder.rowTag.setText(tag);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_voiceentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.rowAmount.setText(amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						
						//TODO image set for voice entry
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ _id + ".amr");
						if (mFile.canRead()) {
							viewHolder.rowImageview.setImageResource(R.drawable.listing_voice_entry_icon);
						} else {
							viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
						}
					}catch(Exception e){
						viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}
				} else {
					viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					return convertView;
				}
			}
			else if(type.equals(getString(R.string.camera))){
				
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_cameraentry)){
						viewHolder.rowTag.setText(tag);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_cameraentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.rowAmount.setText(amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				//TODO image set for camera entry
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_thumbnail.jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + "_small.jpg");
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ _id + ".jpg");
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Drawable drawable = Drawable.createFromPath(mFileThumbnail.getPath());
							viewHolder.rowImageview.setImageDrawable(drawable);
						} else {
							viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
						}
					} catch (Exception e) {
						// TODO if image not available on sdcard
						viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
						e.printStackTrace();
					}
				} else {
					viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
					return convertView;
					// TODO if sdcard not available
				}
			}
			else if(type.equals(getString(R.string.text))){
				if(tag != null){
					if(!tag.equals("") && !tag.equals(R.string.unfinished_textentry)){
						viewHolder.rowTag.setText(tag);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_textentry));
				}
				if(amount != null ){
					if(!amount.equals("")){
						viewHolder.rowAmount.setText(amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				//TODO image set for camera entry
				
				if(tag != null){
					if (!tag.equals("") && !tag.equals(getString(R.string.unfinished_textentry)) ) {
						viewHolder.rowImageview.setImageResource(R.drawable.listing_text_entry_icon);
					} else {
						viewHolder.rowImageview.setImageResource(R.drawable.text_list_icon_no_tag);
					}
				} else {
					viewHolder.rowImageview.setImageResource(R.drawable.text_list_icon_no_tag);
				}
			}
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		TextView rowTag;
		TextView rowAmount;
		ImageView rowImageview;
		ImageView rowFavoriteIcon;
		TextView rowLocationTime;
	}
	
	private void handleUI() {
		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		ScrollView mScrollView = (ScrollView) findViewById(R.id.edit_body);
		mScrollView.setVisibility(View.GONE);
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.edit_body_favorite);
		mRelativeLayout.setVisibility(View.VISIBLE);
		LinearLayout editFooter = (LinearLayout) findViewById(R.id.edit_footer);
		editFooter.setVisibility(View.GONE);
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
								Intent intent = new Intent(FavoriteActivity.this, ImagePreview.class);
								intent.putExtra("id", Long.parseLong(id));
								intent.putExtra("isFavorite", true);
								startActivity(intent);
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
		Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
		expenseListingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		
		if(userId != null){
			toInsert.put(DatabaseAdapter.KEY_ID, Long.toString(userId));
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
					if(userId == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
							} else {
								if(!intentExtras.containsKey("timeInMillis")){
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(userId == null){
						mDatabaseAdapter.open();
						idCreated = mDatabaseAdapter.insertToDatabase(toInsert);
						mDatabaseAdapter.close();
						new FileCopyFavorite(Long.parseLong(favID), idCreated,"from");
						File mFile = new File("/sdcard/ExpenseTracker/"+idCreated+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/"+idCreated+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/"+idCreated+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", idCreated+"");
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(idCreated));
							mDatabaseAdapter.close();
						}
					} else {
						new FileCopyFavorite(Long.parseLong(favID), userId,"from");
						File mFile = new File("/sdcard/ExpenseTracker/"+userId+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/"+userId+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/"+userId+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", toInsert.get(DatabaseAdapter.KEY_ID));
							expenseListingIntent.putExtras(bundle);
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
					if(userId == null){
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
							} else {
								if(!intentExtras.containsKey("timeInMillis")){
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
									toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(userId == null){
						mDatabaseAdapter.open();
						idCreated = mDatabaseAdapter.insertToDatabase(toInsert);
						mDatabaseAdapter.close();
						new FileCopyFavorite(Long.parseLong(favID), idCreated,"from");
						File mFile = new File("/sdcard/ExpenseTracker/Audio/"+idCreated+".amr");
						if(mFile.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", idCreated+"");
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(idCreated));
							mDatabaseAdapter.close();
						}
					} else {
						new FileCopyFavorite(Long.parseLong(favID), userId,"from");
						File mFile = new File("/sdcard/ExpenseTracker/Audio/"+userId+".amr");
						if(mFile.canRead()){
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", toInsert.get(DatabaseAdapter.KEY_ID));
							expenseListingIntent.putExtras(bundle);
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
				if(userId == null){
					if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
						toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
					}
				}
				if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
					try {
						if (!intentExtras.containsKey("mDisplayList")) {
							DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
							toInsert.put(DatabaseAdapter.KEY_DATE_TIME,mDateHelper.getTimeMillis() + "");
						} else {
							if(!intentExtras.containsKey("timeInMillis")){
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
								mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
								toInsert.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(userId == null){
					mDatabaseAdapter.open();
					idCreated = mDatabaseAdapter.insertToDatabase(toInsert);
					mDatabaseAdapter.close();
					bundle.putString("toHighLight", idCreated+"");
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				} else {
					mDatabaseAdapter.open();
					mDatabaseAdapter.editDatabase(toInsert);
					mDatabaseAdapter.close();
					bundle.putString("toHighLight", toInsert.get(DatabaseAdapter.KEY_ID));
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				}
			} catch(Exception e){
				Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
			}
		}
	}

}
