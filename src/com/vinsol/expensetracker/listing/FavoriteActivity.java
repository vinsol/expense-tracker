package com.vinsol.expensetracker.listing;

import java.io.File;
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
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.FileCopyFavorite;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.utils.ImagePreview;

public class FavoriteActivity extends Activity implements OnItemClickListener{
	
	private TextView headerTitle;
	private ListView editFavoriteListview;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Favorite> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView editDateBarDateview;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	private DisplayList mEditList;
	private String dateViewString;
	private Long userId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_page);

		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		handleUI();
		
		headerTitle = (TextView) findViewById(R.id.header_title);
		editFavoriteListview = (ListView) findViewById(R.id.edit_body_favorite_listview);
		editDateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getBundleExtra("favoriteBundle");
		
		headerTitle.setText("Favorite Entry");
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new DisplayList();
			mEditList = intentExtras.getParcelable("mDisplayList");
			userId = Long.parseLong(mEditList.userId);
		}
		
		// ////// ******** Handle Date Bar ********* ////////
		if (intentExtras.containsKey("mDisplayList")) {
			new DateHandler(this, mEditList.timeInMillis);
		} else if (intentExtras.containsKey("timeInMillis")) {
			new DateHandler(this, intentExtras.getLong("timeInMillis"));
		} else {
			new DateHandler(this);
		}
	}

	@Override
	protected void onResume() {
//		try{
//			mEditList.userId;
//		} catch(Exception e){
//			userId = null;
//		}
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
		List<Favorite> mList;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
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
			Favorite tempFavorite= mList.get(position);
			viewHolder.rowImageview.setFocusable(false);
			viewHolder.rowImageview.setOnClickListener(new MyClickListener(tempFavorite));
			viewHolder.rowFavoriteIcon.setVisibility(View.INVISIBLE);
			viewHolder.rowLocationTime.setVisibility(View.GONE);
			if(tempFavorite.type.equals(getString(R.string.voice))){
				if(tempFavorite.description != null){
					if(!tempFavorite.description.equals("") &&!tempFavorite.description.equals(R.string.unfinished_voiceentry)){
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_voiceentry));
				}
				if(tempFavorite.amount != null ){
					if(!tempFavorite.amount.equals("")){
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						
						//TODO image set for voice entry
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ tempFavorite.userId + ".amr");
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
			else if(tempFavorite.type.equals(getString(R.string.camera))){
				
				if(tempFavorite.description != null){
					if(!tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_cameraentry)){
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_cameraentry));
				}
				if(tempFavorite.amount != null ){
					if(!tempFavorite.amount.equals("")){
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				//TODO image set for camera entry
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ tempFavorite.userId + "_thumbnail.jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ tempFavorite.userId + "_small.jpg");
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ tempFavorite.userId + ".jpg");
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
			else if(tempFavorite.type.equals(getString(R.string.text))){
				if(tempFavorite.description != null){
					if(!tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_textentry)){
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_textentry));
				}
				if(tempFavorite.amount != null ){
					if(!tempFavorite.amount.equals("")){
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				//TODO image set for camera entry
				
				if(tempFavorite.description != null){
					if (!tempFavorite.description.equals("") && !tempFavorite.description.equals(getString(R.string.unfinished_textentry)) ) {
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

		Favorite tempFavorite;

		public MyClickListener(Favorite tempFavorite) {
			this.tempFavorite = tempFavorite;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.expense_listing_inflated_row_imageview) {
//				if (mListenerList != null)
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						String id = tempFavorite.userId;
						if (tempFavorite.type.equals(getString(R.string.voice))) {
							File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+ id + ".amr");
							if (mFile.canRead()) {
								new AudioPlayDialog(FavoriteActivity.this,id,"fav");
							} else {
								// TODO audio image change
							}
						} else if (tempFavorite.type.equals(getString(R.string.camera))) {
							File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+ id + ".jpg");
							File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+ id + "_small.jpg");
							File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+ id + "_thumbnail.jpg");
							if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
								Intent intent = new Intent(FavoriteActivity.this, ImagePreview.class);
								intent.putExtra("id", id);
								intent.putExtra("isFavorite", true);
								startActivity(intent);
							} else {
								// TODO if image not found
							}
						}
					}
				if (tempFavorite.type.equals(getString(R.string.text))) {
					if (!tempFavorite.description.equals(getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(FavoriteActivity.this, tempFavorite.description);
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		saveEntry((Favorite) adapter.getItemAtPosition(position));
	}

	private void saveEntry(Favorite adapterList) {
		String favID = adapterList.userId;
		String type = adapterList.type;
		String tag = adapterList.description;
		String amount = adapterList.amount;
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
						new FileCopyFavorite(favID.toString(), idCreated.toString(),"from");
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
						new FileCopyFavorite(favID.toString(), userId.toString(),"from");
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
						new FileCopyFavorite(favID.toString(), idCreated.toString(),"from");
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
						new FileCopyFavorite(favID.toString(), userId.toString(),"from");
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
