package com.vinsol.expensetracker.listing;

import java.io.File;
import java.util.Calendar;
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
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.utils.ImagePreview;

public class FavoriteActivity extends Activity implements OnItemClickListener {
	
	private TextView headerTitle;
	private ListView editFavoriteListview;
	private ConvertCursorToListString mConvertCursorToListString;
	private List<Favorite> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView editDateBarDateview;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	private Entry mEditList;
	private String dateViewString;
	private String id = null;
	private FileHelper fileHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_page);

		// ///// ******* Hide Main Body of layout and make favorite body visible ******* ///////
		handleUI();
		fileHelper = new FileHelper();
		headerTitle = (TextView) findViewById(R.id.header_title);
		editFavoriteListview = (ListView) findViewById(R.id.edit_body_favorite_listview);
		editDateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getBundleExtra("favoriteBundle");
		
		headerTitle.setText("Favorite Entry");
		
		if (intentExtras.containsKey("mDisplayList")) {
			mEditList = new Entry();
			mEditList = intentExtras.getParcelable("mDisplayList");
			id = mEditList.id;
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
		mList = mConvertCursorToListString.getFavoriteList();
		mAdapter = new MyAdapter(this, R.layout.expense_listing_inflated_row , mList);
		editFavoriteListview.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		if (intentExtras.containsKey("mDisplayList")) {
			dateViewString = editDateBarDateview.getText().toString();
		} else {
			dateViewString = "";
		}
		editFavoriteListview.setOnItemClickListener(this);
		super.onResume();
	}
	
	private class MyAdapter extends ArrayAdapter<String> {
		
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
			if(tempFavorite.type.equals(getString(R.string.voice))) {
				if(tempFavorite.description != null) {
					if(!tempFavorite.description.equals("") &&!tempFavorite.description.equals(R.string.unfinished_voiceentry)) {
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_voiceentry));
				}
				if(tempFavorite.amount != null ) {
					if(!tempFavorite.amount.equals("")) {
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFile = fileHelper.getAudioFileFavorite(tempFavorite.favId);
						if (mFile.canRead()) {
							viewHolder.rowImageview.setImageResource(R.drawable.listing_voice_entry_icon);
						} else {
							viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
						}
					}catch(Exception e) {
						viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					}
				} else {
					viewHolder.rowImageview.setImageResource(R.drawable.no_voice_file_thumbnail);
					return convertView;
				}
			}
			else if(tempFavorite.type.equals(getString(R.string.camera))) {
				
				if(tempFavorite.description != null) {
					if(!tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_cameraentry)) {
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_cameraentry));
				}
				if(tempFavorite.amount != null ) {
					if(!tempFavorite.amount.equals("")) {
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFileThumbnail = fileHelper.getCameraFileThumbnailFavorite(tempFavorite.favId);
						File mFileSmall = fileHelper.getCameraFileSmallFavorite(tempFavorite.favId);
						File mFile = fileHelper.getCameraFileLargeFavorite(tempFavorite.favId);
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Drawable drawable = Drawable.createFromPath(mFileThumbnail.getPath());
							viewHolder.rowImageview.setImageDrawable(drawable);
						} else {
							viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
						}
					} catch (Exception e) {
						viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
					}
				} else {
					viewHolder.rowImageview.setImageResource(R.drawable.no_image_thumbnail);
					return convertView;
				}
			}
			else if(tempFavorite.type.equals(getString(R.string.text))) {
				if(tempFavorite.description != null) {
					if(!tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_textentry)) {
						viewHolder.rowTag.setText(tempFavorite.description);
					}
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_textentry));
				}
				if(tempFavorite.amount != null ) {
					if(!tempFavorite.amount.equals("")) {
						viewHolder.rowAmount.setText(tempFavorite.amount);
					}
				} else {
					viewHolder.rowAmount.setText("?");
				}
				
				if(tempFavorite.description != null) {
					if (!tempFavorite.description.equals("") && !tempFavorite.description.equals(getString(R.string.unfinished_textentry))) {
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
		// ///// ******* Hide Main Body of layout and make favorite body visible ******* ///////
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
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					String id = tempFavorite.favId;
					if (tempFavorite.type.equals(getString(R.string.voice))) {
						File mFile = fileHelper.getAudioFileFavorite(id);
						if (mFile.canRead()) {
							new AudioPlayDialog(FavoriteActivity.this,id,"fav");
						} else {
						}
					} else if (tempFavorite.type.equals(getString(R.string.camera))) {
						File mFile = fileHelper.getCameraFileLargeFavorite(id);
						File mFileSmall = fileHelper.getCameraFileSmallFavorite(id);
						File mFileThumbnail = fileHelper.getCameraFileThumbnailFavorite(id);
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Intent intent = new Intent(FavoriteActivity.this, ImagePreview.class);
							intent.putExtra("id", id);
							intent.putExtra("isFavorite", true);
							startActivity(intent);
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
		String favID = adapterList.favId;
		String type = adapterList.type;
		String tag = adapterList.description;
		String amount = adapterList.amount;
		String idCreated = null;
		Entry toInsert = new Entry();
		Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
		expenseListingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		
		if(id != null) {
			toInsert.id = id+"";
		}

		if(amount != null) {
			if(!amount.contains("?") && !amount.equals(""))
				toInsert.amount = amount;
		}
		
		if(favID != null) {
			if(!favID.equals(""))
				toInsert.favId = favID;
		}
		
		if(type.equals(getString(R.string.camera))) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				try {
					toInsert.type = type;
					if(tag != null) {
						if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_cameraentry)) && !tag.equals(getString(R.string.finished_cameraentry)))
							toInsert.description = tag;
					}
					if(id == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.location = LocationHelper.currentAddress;
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								if(!intentExtras.containsKey("timeInMillis")) {
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								}
							}
						} catch (Exception e) {
						}
					}
					if(id == null) {
						mDatabaseAdapter.open();
						idCreated = Long.toString(mDatabaseAdapter.insertToEntryTable(toInsert));
						mDatabaseAdapter.close();
						fileHelper.copyAllFromFavorite(favID.toString(), idCreated);
						File mFile = fileHelper.getCameraFileLargeEntry(idCreated);
						File mFileSmall = fileHelper.getCameraFileSmallEntry(idCreated);
						File mFileThumbnail = fileHelper.getCameraFileThumbnailEntry(idCreated);
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", idCreated+"");
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteEntryTableEntryID(idCreated);
							mDatabaseAdapter.close();
						}
					} else {
						fileHelper.copyAllFromFavorite(favID.toString(), idCreated);
						File mFile = fileHelper.getCameraFileLargeEntry(id);
						File mFileSmall = fileHelper.getCameraFileSmallEntry(id);
						File mFileThumbnail = fileHelper.getCameraFileThumbnailEntry(id);
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", toInsert.id);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editEntryTable(toInsert);
							mDatabaseAdapter.close();
							finish();
						} else {
							Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
						}
					}
				}
				catch (Exception e) {
					Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "you cannot use camera entry without sdcard", Toast.LENGTH_LONG).show();
			}
		} else if(type.equals(getString(R.string.voice))) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				try {
					toInsert.type = type;
					if(tag != null) {
						if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_voiceentry)) && !tag.equals(getString(R.string.finished_voiceentry)))
							toInsert.description = tag;
					}
					if(id == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.location = LocationHelper.currentAddress;
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey("mDisplayList")) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								if(!intentExtras.containsKey("timeInMillis")) {
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
									mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								}
							}
						} catch (Exception e) {
						}
					}
					if(id == null) {
						mDatabaseAdapter.open();
						idCreated = Long.toString(mDatabaseAdapter.insertToEntryTable(toInsert));
						mDatabaseAdapter.close();
						fileHelper.copyAllFromFavorite(favID.toString(), idCreated);
						File mFile = fileHelper.getAudioFileEntry(idCreated);
						if(mFile.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", idCreated+"");
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteEntryTableEntryID(idCreated);
							mDatabaseAdapter.close();
						}
					} else {
						fileHelper.copyAllFromFavorite(favID.toString(), id);
						File mFile = fileHelper.getAudioFileEntry(id);
						if(mFile.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							bundle.putString("toHighLight", toInsert.id);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editEntryTable(toInsert);
							mDatabaseAdapter.close();
							finish();
						} else {
							Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
						}
					}
				} catch(Exception e) {
					Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "you cannot use voice entry without sdcard", Toast.LENGTH_LONG).show();
			}
		} else if(type.equals(getString(R.string.text))) {
			try {
				toInsert.type = type;
				if(tag != null) {
					if(!tag.equals("") && !tag.equals(getString(R.string.unfinished_textentry)) && !tag.equals(getString(R.string.finished_textentry)))
						toInsert.description = tag;
				}
				if(id == null) {
					if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
						toInsert.location = LocationHelper.currentAddress;
					}
				}
				if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
					try {
						if (!intentExtras.containsKey("mDisplayList")) {
							DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
							toInsert.timeInMillis = mDateHelper.getTimeMillis();
						} else {
							if(!intentExtras.containsKey("timeInMillis")) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
								mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							}
						}
					} catch (Exception e) {
					}
				}
				if(id == null) {
					mDatabaseAdapter.open();
					idCreated = mDatabaseAdapter.insertToEntryTable(toInsert)+"";
					mDatabaseAdapter.close();
					bundle.putString("toHighLight", idCreated+"");
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				} else {
					mDatabaseAdapter.open();
					mDatabaseAdapter.editEntryTable(toInsert);
					mDatabaseAdapter.close();
					bundle.putString("toHighLight", toInsert.id);
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				}
			} catch(Exception e) {
				Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
			}
		}
	}

}
