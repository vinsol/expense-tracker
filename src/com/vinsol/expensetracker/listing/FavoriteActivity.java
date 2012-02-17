/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.utils.ImagePreview;

public class FavoriteActivity extends Activity implements OnItemClickListener {
	
	private List<Favorite> mList;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView editDateBarDateview;
	private Bundle intentExtras;
	private MyAdapter mAdapter;
	private String dateViewString;
	private String id = null;
	private FileHelper fileHelper;
	private boolean isManaging = false;
	private static final int ACTIVITY_RESULT = 1135;
	private ListView mFavoriteListview;
	private EditText searchBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_page);

		// ///// ******* Hide Main Body of layout and make favorite body visible ******* ///////
		handleUI();
		Entry mEditList = null;
		fileHelper = new FileHelper();
		final List<Favorite> mList_sort = new ArrayList<Favorite>();
		TextView headerTitle = (TextView) findViewById(R.id.header_title);
		mFavoriteListview = (ListView) findViewById(R.id.edit_body_favorite_listview);
		editDateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		ConvertCursorToListString mConvertCursorToListString = new ConvertCursorToListString(this);
		mDatabaseAdapter = new DatabaseAdapter(this);
		intentExtras = getIntent().getExtras();
		
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_MANAGE_FAVORITE)) {
			isManaging = true;
			headerTitle.setText("Managing Favorites");
			((LinearLayout)findViewById(R.id.edit_date_bar)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.edit_body_favorite_tag)).setText("Choose an entry to edit or delete");
		} else {
			headerTitle.setText("Favorite Entry");
			if (intentExtras != null && intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
				mEditList = new Entry();
				mEditList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
				id = mEditList.id;
			}
			
			//////// ******** Handle Date Bar ********* ////////
			if (intentExtras != null && intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
				new DateHandler(this, mEditList.timeInMillis);
			} else if (intentExtras != null && intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
				new DateHandler(this, intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
			} else {
				new DateHandler(this);
			}
		}
		
		mList = mConvertCursorToListString.getFavoriteList();
		if(mList.size() == 0) {favListEmpty();}
		mAdapter = new MyAdapter(this, R.layout.expense_listing_inflated_row , mList);
		mFavoriteListview.setAdapter(mAdapter);
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			dateViewString = editDateBarDateview.getText().toString();
		} else {
			dateViewString = "";
		}
		mFavoriteListview.setOnItemClickListener(this);
		searchBox = (EditText) findViewById(R.id.favorite_search);
		mFavoriteListview.setOnScrollListener(toggleSearchBoxListener);
		searchBox.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//do nothing
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				//do nothing
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				int textlength = searchBox.getText().length();
				mList_sort.clear();
				for (int i = 0; i < mList.size(); i++) {
					if (textlength <= mList.get(i).description.length() || textlength <= mList.get(i).location.length() || textlength <= mList.get(i).amount.length()) {
						if(containsStringIgnoreCase(i)){
							mList_sort.add(mList.get(i));
						}	
					}
				}
				mAdapter = new MyAdapter(FavoriteActivity.this, R.layout.expense_listing_inflated_row , mList_sort);
				mFavoriteListview.setAdapter(mAdapter);
			}
			
			private boolean containsStringIgnoreCase(int i) {
				return isStringInDescription(i) || isStringInLocation(i) || isStringInAmount(i);
			}
		});
	}
	
	private boolean isStringInDescription(int i) {
		if(mList.get(i).description != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mList.get(i).description).find());
		} else {
			return false;
		}
	}
	
	private boolean isStringInLocation(int i) {
		if(mList.get(i).location != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mList.get(i).location).find());
		} else {
			return false;
		}
	}
	
	private boolean isStringInAmount(int i) {
		if(mList.get(i).amount != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mList.get(i).amount).find());
		} else {
			return false;
		}
	}
	
	private OnScrollListener toggleSearchBoxListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// do nothing
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			if(visibleItemCount < mAdapter.getCount()) {
				searchBox.setVisibility(View.VISIBLE);
			} else {
				searchBox.setVisibility(View.GONE);
			}
			mFavoriteListview.setOnScrollListener(null);
		}
		
	};
	
	private void favListEmpty() {
		Toast.makeText(getApplicationContext(), "favorite list empty", Toast.LENGTH_LONG).show();
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ACTIVITY_RESULT == requestCode && data != null && data.getExtras() != null) {
			intentExtras = data.getExtras();
			int position = -1;
			if(intentExtras != null && intentExtras.containsKey(Constants.KEY_POSITION)) {
				position = intentExtras.getInt(Constants.KEY_POSITION);
			}
			if(Activity.RESULT_OK == resultCode && intentExtras != null && intentExtras.containsKey(Constants.KEY_DATA_CHANGED) && position != -1) {
				mAdapter.mList.set(position, (Favorite) intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA));
			}
			if(Activity.RESULT_CANCELED == resultCode && intentExtras != null && intentExtras.containsKey(Constants.KEY_DATA_CHANGED) && position != -1) {
				mAdapter.mList.remove(position);
				mFavoriteListview.setOnScrollListener(toggleSearchBoxListener);
				if(mAdapter.mList.size() == 0) {
					favListEmpty();
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private class MyAdapter extends ArrayAdapter<Favorite> {
		
		private LayoutInflater mInflater;
		private List<Favorite> mList;
		
		private MyAdapter(Context context, int resource,List<Favorite> list) {
			super(context, resource,list);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mList = list;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.expense_listing_inflated_row, null);
				viewHolder = new ViewHolder();
				viewHolder.rowTag = (TextView) convertView.findViewById(R.id.row_tag);
				viewHolder.rowAmount = (TextView) convertView.findViewById(R.id.row_amount);
				viewHolder.rowImageview = (ImageView) convertView.findViewById(R.id.row_imageview);
				viewHolder.rowLocationTime = (TextView) convertView.findViewById(R.id.row_location_time);
				viewHolder.rowFavoriteIcon= (ImageView) convertView.findViewById(R.id.row_favorite_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Favorite tempFavorite= mList.get(position);
			viewHolder.rowImageview.setFocusable(false);
			viewHolder.rowImageview.setOnClickListener(new MyClickListener(tempFavorite));
			viewHolder.rowFavoriteIcon.setVisibility(View.INVISIBLE);
			
			if(tempFavorite.amount != null && !tempFavorite.amount.equals("")) {
				viewHolder.rowAmount.setText(new StringProcessing().getStringDoubleDecimal(tempFavorite.amount));
			} else {
				viewHolder.rowAmount.setText("?");
			}
			if(tempFavorite.location != null && !tempFavorite.location.equals("")) {
				viewHolder.rowLocationTime.setText(tempFavorite.location);
			}
			if(tempFavorite.type.equals(getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(tempFavorite.description != null && !tempFavorite.description.equals("") &&!tempFavorite.description.equals(R.string.unfinished_voiceentry)) {
						viewHolder.rowTag.setText(tempFavorite.description);
					} else {
						viewHolder.rowTag.setText(getString(R.string.finished_voiceentry));
					}
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
				
				if(tempFavorite.description != null && !tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_cameraentry)) {
					viewHolder.rowTag.setText(tempFavorite.description);
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_cameraentry));
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
				if(tempFavorite.description != null && !tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_textentry)) {
					viewHolder.rowTag.setText(tempFavorite.description);
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_textentry));
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
		/////// ******* Hide Main Body of layout and make favorite body visible ******* ///////
		((ScrollView) findViewById(R.id.edit_body)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.edit_body_favorite)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.edit_footer)).setVisibility(View.GONE);
	}
	
	private class MyClickListener implements OnClickListener {

		Favorite tempFavorite;
		
		public MyClickListener(Favorite tempFavorite) {
			this.tempFavorite = tempFavorite;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.row_imageview:
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
							intent.putExtra(Constants.KEY_ID, id);
							intent.putExtra(Constants.KEY_IS_FAVORITE, true);
							startActivity(intent);
						}
					}
				}
				if (tempFavorite.type.equals(getString(R.string.text))) {
					if (!tempFavorite.description.equals(getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(FavoriteActivity.this, tempFavorite.description);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		Favorite favoriteEntry = (Favorite) adapter.getItemAtPosition(position);
		if(isManaging) {
			Intent intent = null;
			if(favoriteEntry.type.equals(getString(R.string.text))) {
				intent = new Intent(this, TextEntry.class);
			} else if(favoriteEntry.type.equals(getString(R.string.voice))) {
				intent = new Intent(this, Voice.class);
			} else if(favoriteEntry.type.equals(getString(R.string.camera))) {
				intent = new Intent(this, CameraActivity.class);
			}
			Bundle intentExtras = new Bundle();
			intentExtras.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, favoriteEntry);
			intentExtras.putBoolean(Constants.KEY_IS_COMING_FROM_FAVORITE, true);
			intentExtras.putInt(Constants.KEY_POSITION, position);
			intent.putExtras(intentExtras);
			startActivityForResult(intent, ACTIVITY_RESULT);
		} else {
			createNewEntry(favoriteEntry);
		}
	}

	private void createNewEntry(Favorite adapterList) {
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
					if(tag != null && !tag.equals("") && !tag.equals(getString(R.string.unfinished_cameraentry)) && !tag.equals(getString(R.string.finished_cameraentry))) {
						toInsert.description = tag;
					} else {
						toInsert.description = getString(R.string.finished_cameraentry);
					}
					if(id == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.location = LocationHelper.currentAddress;
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								if(!intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
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
							bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
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
							bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
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
					if(tag != null && !tag.equals("") && !tag.equals(getString(R.string.unfinished_voiceentry)) && !tag.equals(getString(R.string.finished_voiceentry))) {
						toInsert.description = tag;
					} else {
						toInsert.description = getString(R.string.finished_voiceentry);
					}
					if(id == null) {
						if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
							toInsert.location = LocationHelper.currentAddress;
						}
					}
					if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
						try {
							if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								if(!intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
									DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
									toInsert.timeInMillis = mDateHelper.getTimeMillis();
								} else {
									Calendar mCalendar = Calendar.getInstance();
									mCalendar.setTimeInMillis(intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
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
							bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
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
							bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
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
						if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
							DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
							toInsert.timeInMillis = mDateHelper.getTimeMillis();
						} else {
							if(!intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
								DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
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
					bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				} else {
					mDatabaseAdapter.open();
					mDatabaseAdapter.editEntryTable(toInsert);
					mDatabaseAdapter.close();
					bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
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