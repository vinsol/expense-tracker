/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.entry;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.expenselisting.ExpenseListing;
import com.vinsol.expensetracker.expenselisting.dialog.AudioPlayDialog;
import com.vinsol.expensetracker.expenselisting.dialog.DescriptionDialog;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.ImagePreview;

public class FavoriteEntry extends BaseActivity implements OnItemClickListener {
	
	private List<Favorite> mListMain;
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
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_key));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
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
			headerTitle.setText(getString(R.string.manage_favorites));
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
		
		mListMain = mConvertCursorToListString.getFavoriteList();
		if(mListMain.size() == 0) {favListEmpty();}
		mAdapter = new MyAdapter(this, R.layout.expense_listing_inflated_row , mListMain);
		mFavoriteListview.setAdapter(mAdapter);
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			dateViewString = editDateBarDateview.getText().toString();
		} else {
			dateViewString = "";
		}
		mFavoriteListview.setOnItemClickListener(this);
		searchBox = (EditText) findViewById(R.id.favorite_search);
		setSearchBoxVisibility();
		
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
				for (int i = 0; i < mListMain.size(); i++) {
					if((textlength <= mListMain.get(i).description.length() || textlength <= mListMain.get(i).location.length() || textlength <= mListMain.get(i).amount.length()) && containsStringIgnoreCase(i)) {
						mList_sort.add(mListMain.get(i));
					}
				}
				mAdapter = new MyAdapter(FavoriteEntry.this, R.layout.expense_listing_inflated_row , mList_sort);
				mFavoriteListview.setAdapter(mAdapter);
			}
			
			private boolean containsStringIgnoreCase(int i) {
				return isStringInDescription(i) || isStringInLocation(i) || isStringInAmount(i);
			}
		});
		
		if(isManaging) {
			registerForContextMenu(mFavoriteListview);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(!isManaging){
			return super.onCreateOptionsMenu(menu);
		} else {
			return false;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
		
			case R.id.edit_body_favorite_listview:
	    	    String[] menuItems = getResources().getStringArray(R.array.listcontextmenu);
	    	    for (int i = 0; i<menuItems.length; i++) {
	    	    	menu.add(Menu.NONE, i, i, menuItems[i]);
	    	    }
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
  	    switch (item.getItemId()) {
		//Edit Action	
  	    case 0:
  	    	FlurryAgent.onEvent("Favorite "+getString(R.string.editing_using_context_menu));
  	    	startEditPage(info.position);
			break;
			
		//Delete Action
  	    case 1:
  	    	FlurryAgent.onEvent("Favorite "+getString(R.string.deleting_using_context_menu));
  	    	removeItem(info.position);
  	    	SyncHelper.startSync();
  	    	break;
  	    	
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void startEditPage(int position) {
		Favorite favoriteEntry = (Favorite) mAdapter.getItem(position);
		Intent intent = null;
		if(favoriteEntry.type.equals(getString(R.string.text))) {
			intent = new Intent(this, Text.class);
		} else if(favoriteEntry.type.equals(getString(R.string.voice))) {
			intent = new Intent(this, Voice.class);
		} else if(favoriteEntry.type.equals(getString(R.string.camera))) {
			intent = new Intent(this, CameraEntry.class);
		}
		Bundle intentExtras = new Bundle();
		intentExtras.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, favoriteEntry);
		intentExtras.putBoolean(Constants.KEY_IS_COMING_FROM_FAVORITE, true);
		intentExtras.putInt(Constants.KEY_POSITION, position);
		intent.putExtras(intentExtras);
		startActivityForResult(intent, ACTIVITY_RESULT);
	}
	
	private void removeItem(int position) {
		//XXX
		Favorite tempFav = ((Favorite)mAdapter.getItem(position));
		mDatabaseAdapter.open();
//		if(Strings.isEmpty(tempFav.updatedAt)) {
//			mDatabaseAdapter.permanentDeleteFavoriteTableEntryID(tempFav.favId);	
//		} else {
		mDatabaseAdapter.deleteFavoriteEntryByID(tempFav.id);
//		}
    	
    	mDatabaseAdapter.editFavoriteHashEntryTable(((Favorite)mAdapter.getItem(position)).myHash);
    	mDatabaseAdapter.close();
    	fileHelper.deleteAllFavoriteFiles(((Favorite)mAdapter.getItem(position)).id);
    	mListMain.remove(mAdapter.getItem(position));
    	if(mListMain.size() > 0) {
    		searchBox.setText(searchBox.getText().toString());
    		mAdapter.notifyDataSetChanged();
    	} else {
    		favListEmpty();
    	}
	}
	
	private void setSearchBoxVisibility() {
		DisplayMetrics metrics =  new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int screenHeight = (metrics.heightPixels * 160)/metrics.densityDpi;
		int rowHeight = (87 * 160)/metrics.densityDpi;
		int headerHeight = (55 * 160)/metrics.densityDpi;
		int dateBarHeight = (48 * 160)/metrics.densityDpi;
		int favBarHeight = (44 * 160)/metrics.densityDpi;
		int totalHeaderHeight = headerHeight + favBarHeight;
		if(!isManaging) {
			totalHeaderHeight = totalHeaderHeight + dateBarHeight; 
		}
		
		if(screenHeight <  ((mListMain.size() * rowHeight ) + totalHeaderHeight)) {
			searchBox.setVisibility(View.VISIBLE);
		} else {
			if(mAdapter.getCount() == 0) {
				searchBox.setText("");
			} else {
				searchBox.setText(searchBox.getText().toString());
			}
			if(searchBox.getText().toString().equals("")) {searchBox.setVisibility(View.GONE);}
		}
	}

	private boolean isStringInDescription(int i) {
		if(mListMain.get(i).description != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mListMain.get(i).description).find());
		} else {
			return false;
		}
	}
	
	private boolean isStringInLocation(int i) {
		if(mListMain.get(i).location != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mListMain.get(i).location).find());
		} else {
			return false;
		}
	}
	
	private boolean isStringInAmount(int i) {
		if(mListMain.get(i).amount != null) {
			return (Pattern.compile(Pattern.quote(searchBox.getText().toString()), Pattern.CASE_INSENSITIVE).matcher(mListMain.get(i).amount).find());
		} else {
			return false;
		}
	}
	
	private void favListEmpty() {
		Toast.makeText(getApplicationContext(), "favorite list empty", Toast.LENGTH_LONG).show();
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//XXX
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
				mListMain.remove(mAdapter.mList.get(position));
				
//				if(mAdapter.mList.contains(mAdapter.mList.get(position))) {mAdapter.mList.remove(mAdapter.mList.get(position));}
				setSearchBoxVisibility();
				if(mListMain.size() == 0) {
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
			
			if(viewHolder.rowImageViewBitmap != null) {viewHolder.rowImageViewBitmap.recycle();}
			
			viewHolder.rowImageview.setScaleType(ScaleType.CENTER_INSIDE);
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
			} else {
				viewHolder.rowLocationTime.setText("unknown location");
			}
			if(tempFavorite.type.equals(getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(tempFavorite.description != null && !tempFavorite.description.equals("") &&!tempFavorite.description.equals(R.string.unfinished_voiceentry)) {
						viewHolder.rowTag.setText(tempFavorite.description);
					} else {
						viewHolder.rowTag.setText(getString(R.string.finished_voiceentry));
					}
					try {
						File mFile = fileHelper.getAudioFileFavorite(tempFavorite.id);
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
			} else if(tempFavorite.type.equals(getString(R.string.camera))) {
				
				if(tempFavorite.description != null && !tempFavorite.description.equals("") && !tempFavorite.description.equals(R.string.unfinished_cameraentry)) {
					viewHolder.rowTag.setText(tempFavorite.description);
				} else {
					viewHolder.rowTag.setText(getString(R.string.finished_cameraentry));
				}
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						File mFileThumbnail = fileHelper.getCameraFileThumbnailFavorite(tempFavorite.id);
						File mFileSmall = fileHelper.getCameraFileSmallFavorite(tempFavorite.id);
						File mFile = fileHelper.getCameraFileLargeFavorite(tempFavorite.id);
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							viewHolder.rowImageViewBitmap = BitmapFactory.decodeFile(mFileThumbnail.getPath());
							viewHolder.rowImageview.setScaleType(ScaleType.FIT_CENTER);
							viewHolder.rowImageview.setImageBitmap(viewHolder.rowImageViewBitmap);
						} else {
							viewHolder.rowImageview.setScaleType(ScaleType.CENTER_INSIDE);
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
	
	private static class ViewHolder {
		TextView rowTag;
		TextView rowAmount;
		ImageView rowImageview;
		ImageView rowFavoriteIcon;
		TextView rowLocationTime;
		Bitmap rowImageViewBitmap;
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
					String id = tempFavorite.id;
					if (tempFavorite.type.equals(getString(R.string.voice))) {
						File mFile = fileHelper.getAudioFileFavorite(id);
						if (mFile.canRead()) {new AudioPlayDialog(FavoriteEntry.this,id,"fav");}
						
					} else if (tempFavorite.type.equals(getString(R.string.camera))) {
						File mFile = fileHelper.getCameraFileLargeFavorite(id);
						File mFileSmall = fileHelper.getCameraFileSmallFavorite(id);
						File mFileThumbnail = fileHelper.getCameraFileThumbnailFavorite(id);
						if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Intent intent = new Intent(FavoriteEntry.this, ImagePreview.class);
							intent.putExtra(Constants.KEY_ID, id);
							intent.putExtra(Constants.KEY_IS_FAVORITE, true);
							startActivity(intent);
						}
					}
				}
				if (tempFavorite.type.equals(getString(R.string.text))) {
					if (!tempFavorite.description.equals(getString(R.string.unfinished_textentry))) {
						new DescriptionDialog(FavoriteEntry.this, tempFavorite.description);
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
		if(isManaging) {
			FlurryAgent.onEvent(getString(R.string.page_opened_to_edit_favorite));
			startEditPage(position);
		} else {
			FlurryAgent.onEvent(getString(R.string.creating_new_entry_using_favorite));
			createNewEntry((Favorite) adapter.getItemAtPosition(position));
		}
	}

	private void createNewEntry(Favorite adapterList) {
		String favID = adapterList.id;
		String type = adapterList.type;
		String tag = adapterList.description;
		String amount = adapterList.amount;
		String idCreated = null;
		Entry toInsert = new Entry();
		Intent expenseListingIntent = new Intent(this, ExpenseListing.class);
		expenseListingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		if(id != null) {toInsert.id = id+"";}

		if(amount != null && !amount.contains("?") && !amount.equals("")) {toInsert.amount = amount;}
		
		toInsert.favorite = adapterList.myHash;
		
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
							Bundle bundle = new Bundle();
							bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
							bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteExpenseEntryByID(idCreated);
							mDatabaseAdapter.close();
						}
					} else {
						fileHelper.copyAllFromFavorite(favID.toString(), idCreated);
						File mFile = fileHelper.getCameraFileLargeEntry(id);
						File mFileSmall = fileHelper.getCameraFileSmallEntry(id);
						File mFileThumbnail = fileHelper.getCameraFileThumbnailEntry(id);
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Bundle bundle = new Bundle();
							bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
							bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							toInsert.syncBit = getString(R.string.syncbit_not_synced);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editExpenseEntryById(toInsert);
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
							Bundle bundle = new Bundle();
							bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
							bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							finish();
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteExpenseEntryByID(idCreated);
							mDatabaseAdapter.close();
						}
					} else {
						fileHelper.copyAllFromFavorite(favID.toString(), id);
						File mFile = fileHelper.getAudioFileEntry(id);
						if(mFile.canRead()) {
							Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
							Bundle bundle = new Bundle();
							bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
							bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
							expenseListingIntent.putExtras(bundle);
							startActivity(expenseListingIntent);
							mDatabaseAdapter.open();
							mDatabaseAdapter.editExpenseEntryById(toInsert);
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
					Bundle bundle = new Bundle();
					bundle.putString(Constants.KEY_HIGHLIGHT, idCreated+"");
					bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
					expenseListingIntent.putExtras(bundle);
					startActivity(expenseListingIntent);
					finish();
				} else {
					mDatabaseAdapter.open();
					mDatabaseAdapter.editExpenseEntryById(toInsert);
					mDatabaseAdapter.close();
					Bundle bundle = new Bundle();
					bundle.putString(Constants.KEY_HIGHLIGHT, toInsert.id);
					bundle.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toInsert.timeInMillis);
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