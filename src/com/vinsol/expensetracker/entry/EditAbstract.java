/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.entry;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.expenselisting.ExpenseListing;
import com.vinsol.expensetracker.helpers.ConfirmSaveEntryDialog;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.DeleteDialog;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.Strings;

abstract class EditAbstract extends BaseActivity implements OnClickListener {
	
	protected Entry mEditList;
	protected Favorite mFavoriteList;
	protected boolean setLocation = false;
	protected EditText editAmount;
	protected EditText editTag;
	protected Bundle intentExtras;
	protected boolean setUnknown = false;
	protected int typeOfEntryFinished;
	protected int typeOfEntryUnfinished;
	protected int typeOfEntry;
	protected boolean isChanged = false;
	protected DatabaseAdapter mDatabaseAdapter;
	protected TextView editHeaderTitle;
	protected TextView dateBarDateview;
	protected String dateViewString;
	protected Button editDelete;
	protected Button editSaveEntry;
	protected Entry entry;
	protected FileHelper fileHelper;
	protected boolean isFromFavorite = false; 
	private String flurryEventId;
	
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
		entry = new Entry();
		fileHelper = new FileHelper();
		editAmount = (EditText) findViewById(R.id.edit_amount);
		editHeaderTitle = (TextView) findViewById(R.id.header_title);
		editTag = (EditText) findViewById(R.id.edit_tag);
		dateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		editSaveEntry = (Button) findViewById(R.id.edit_save_entry);
		editDelete = (Button) findViewById(R.id.edit_delete);
		mDatabaseAdapter = new DatabaseAdapter(this);
		editSaveEntry.setOnClickListener(this);
		editDelete.setOnClickListener(this);
		editAmount.setSelection(editAmount.getText().length());
		editAmount.setOnEditorActionListener(setEnterButtonToNext);
		editAmount.setOnFocusChangeListener(focusChangeListener);

		////////********* Get intent extras ******** ////////////
		intentExtras = getIntent().getExtras();
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_FAVORITE)) {isFromFavorite = true;}
		// Sets Title of activity
		setDefaultTitle();
	}
	
	private TextView.OnEditorActionListener setEnterButtonToNext = new TextView.OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
	        	editTag.requestFocus();
	            return true;
	        }
	        return false;
		}
	};
	
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(!hasFocus)
				setSoftPanToInputMode();
		}
	};
	
	private void setSoftPanToInputMode() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	protected void editHelper() {
		if (intentExtras.containsKey(Constants.KEY_ID)) {
			entry.id = intentExtras.getLong(Constants.KEY_ID)+"";
		}
		
		if(intentExtras.containsKey(Constants.KEY_SET_LOCATION)) {
			setLocation = intentExtras.getBoolean(Constants.KEY_SET_LOCATION);
		}
		
		if(intentExtras.containsKey(Constants.KEY_POSITION)) {
			editSaveEntry.setBackgroundResource(R.drawable.update_entry_states);
		}
		
		if (!isFromFavorite && intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			flurryEventId = getString(R.string.edit_entry);
			mEditList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
			mFavoriteList = null;
			entry.id = mEditList.id;
			entry.amount = mEditList.amount;
			entry.description = mEditList.description;
			((LinearLayout)findViewById(R.id.favorite_layout)).setVisibility(View.GONE);
			setText(entry.amount, entry.description);
			TextView locationTime = (TextView) findViewById(R.id.edit_location_time);
			locationTime.setVisibility(View.VISIBLE);
			locationTime.setText(new DisplayDate().getLocationDate(mEditList.timeInMillis, mEditList.location));
		} else if(isFromFavorite && intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			flurryEventId = getString(R.string.edit_fav);
			mEditList = null;
			((LinearLayout)findViewById(R.id.edit_date_bar)).setVisibility(View.GONE);
			mFavoriteList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA); 
			setText(mFavoriteList.amount, mFavoriteList.description);
		}
		
		if(!isFromFavorite) {
			////////******** Handle Date Bar ********* ////////
			if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
				new DateHandler(this, mEditList.timeInMillis);
			} else if (intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
				new DateHandler(this, intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
			} else {
				new DateHandler(this);
			}
		}
	}
	
	protected void setFavoriteHelper() {
		//New Entry
		if (!isFromFavorite && !intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			flurryEventId = getString(R.string.new_entry);
			new FavoriteHelper(this, mDatabaseAdapter, fileHelper, getString(typeOfEntry), entry.id, editAmount, editTag , isChanged);
		} else {
			findViewById(R.id.favorite_divider).setVisibility(View.GONE);
		}
	}

	protected String getTypeOfEntryForFlurry() {
		return getString(typeOfEntryFinished);
	}
	
	private void setText(String amount, String description) {
		if (!((amount == null || amount.equals(""))) && !amount.contains("?")) {
			if(amount.endsWith(".00") || amount.endsWith(".0")) {
				editAmount.setText(((int) Double.parseDouble(amount))+"");
			} else {
				editAmount.setText(amount);
			}
		}
		if(description.equals(getString(R.string.unknown_entry)) || description.equals(getString(R.string.unknown))) {
			setUnknown = true;
		}
		
		if (!(description.equals("") || description == null || 
				description.equals(getString(typeOfEntryUnfinished)) || description.equals(getString(typeOfEntryFinished))  || description.equals(getString(R.string.unknown_entry)))) {
			editTag.setText(description);
		}
		
	}
	
	private Entry getSaveEntryData(TextView editDateBarDateview,String dateViewString) {
		/////// ******* Creating HashMap to update info ******* ////////
		Map<String, String> map = new HashMap<String, String>();
		Entry list = new Entry();
		list.id = entry.id;
		if(mEditList != null)
			isChanged = checkEntryModified();
		entry.amount = editAmount.getText().toString();
		entry.description = editTag.getText().toString();
		if (!entry.amount.equals(".") && !entry.amount.equals("")) {
			Double mAmount = Double.parseDouble(entry.amount);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			list.amount = mAmount+"";
			map.put("Amount Digits", new StringProcessing().getStringDoubleDecimal(list.amount).length()+"");
		} else {
			list.amount = "";
		}
		map.put("type", getTypeOfEntryForFlurry());
		FlurryAgent.onEvent(flurryEventId,map);
		list.description = entry.description;
		if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
					DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
					list.timeInMillis = mDateHelper.getTimeMillis();
				} else {
					if(!intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
						list.timeInMillis = mDateHelper.getTimeMillis();
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
						list.timeInMillis = mDateHelper.getTimeMillis();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(setLocation == true && LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			list.location = LocationHelper.currentAddress;
		}
		return list;
	}
	
	private Favorite getSaveFavoriteData() {
		/////// ******* Creating HashMap to update info ******* ////////
		Favorite favorite = new Favorite();
		favorite.id = mFavoriteList.id;
		isChanged = checkFavoriteModified();
		mFavoriteList.amount = editAmount.getText().toString();
		mFavoriteList.description = editTag.getText().toString();
		if (!mFavoriteList.amount.equals(".") && !mFavoriteList.amount.equals("")) {
			Double mAmount = Double.parseDouble(mFavoriteList.amount);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			favorite.amount = mAmount+"";
		} else {
			favorite.amount = "";
		}
		
		favorite.description = mFavoriteList.description;
		
		return favorite;
	}
	
	private Entry getEntryListOnResult(Entry list) {
		Entry displayList = new Entry();
		displayList.id = mEditList.id;
		displayList.description = list.description;
		displayList.amount = list.amount;
		if(displayList.amount == null || displayList.amount.equals("")) {
			displayList.amount = "?";
		}
		
		if (displayList.description == null || displayList.description.equals("") || displayList.description.equals(getString(typeOfEntryUnfinished)) || displayList.description.equals(getString(typeOfEntryFinished)) || displayList.description.equals(getString(R.string.unknown_entry))) {
			displayList.description = getString(typeOfEntryFinished);
		}
		
		if (mEditList.description == null || mEditList.description.equals("") || mEditList.description.equals(getString(typeOfEntryUnfinished)) || mEditList.description.equals(getString(typeOfEntryFinished)) || mEditList.description.equals(getString(R.string.unknown_entry))) {
			mEditList.description = getString(typeOfEntryFinished);
		}
		
		if( isChanged ) {
			Entry listForFav = new Entry();
			listForFav.favorite = "";
			listForFav.id = mEditList.id;
			listForFav.syncBit = getString(R.string.syncbit_not_synced);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editExpenseEntryById(listForFav);
			mDatabaseAdapter.close();
			displayList.favorite = "";
		} else {
			displayList.favorite = mEditList.favorite;
		}
			
		displayList.type = mEditList.type;	

		if(list.timeInMillis != null) {
			displayList.timeInMillis = list.timeInMillis;
		} else {
			displayList.timeInMillis = mEditList.timeInMillis;
		}
		displayList.location = mEditList.location;
		displayList.deleted = mEditList.deleted;
		displayList.fileToDownload = mEditList.fileToDownload;
		displayList.fileUploaded = mEditList.fileUploaded;
		displayList.myHash = mEditList.myHash;
		
		if(Strings.isEmpty(list.syncBit)) {
			displayList.syncBit = "";
		} else {
			displayList.syncBit = list.syncBit;
		}
		
		if(Strings.isEmpty(mEditList.idFromServer)) {
			displayList.idFromServer = "";
		} else {
			displayList.idFromServer = mEditList.idFromServer;
		}
		
		if(Strings.isEmpty(mEditList.fileUpdatedAt)) {
			displayList.fileUpdatedAt = "";
		} else {
			displayList.fileUpdatedAt = mEditList.fileUpdatedAt;
		}
		
		if(Strings.isEmpty(mEditList.updatedAt)) {
			displayList.updatedAt = "";
		} else {
			displayList.updatedAt = mEditList.updatedAt;
		}
		
		mEditList = displayList;
		return displayList;
	}
	
	private Favorite getFavoriteListOnResult(Favorite favList) {
		Favorite favorite = new Favorite();
		favorite.id = mFavoriteList.id;
		favorite.description = favList.description;
		favorite.amount = favList.amount;
		if(favorite.amount == null || favorite.amount.equals("")) {
			favorite.amount = "?";
		}
		
		if (favorite.description == null || favorite.description.equals("") || favorite.description.equals(getString(typeOfEntryUnfinished)) || favorite.description.equals(getString(typeOfEntryFinished)) || favorite.description.equals(getString(R.string.unknown_entry))) {
			favorite.description = getString(typeOfEntryFinished);
		}
		
		if (mFavoriteList.description == null || mFavoriteList.description.equals("") || mFavoriteList.description.equals(getString(typeOfEntryUnfinished)) || mFavoriteList.description.equals(getString(typeOfEntryFinished)) || mFavoriteList.description.equals(getString(R.string.unknown_entry))) {
			mFavoriteList.description = getString(typeOfEntryFinished);
		}	
		
		if( isChanged ) {
			mDatabaseAdapter.open();
			mDatabaseAdapter.editFavoriteHashEntryTable(favorite.myHash);
			mDatabaseAdapter.close();
		}
			
		favorite.type = mFavoriteList.type;	
		favorite.location = mFavoriteList.location;
		favorite.deleted = mFavoriteList.deleted;
		favorite.fileToDownload = mFavoriteList.fileToDownload;
		favorite.fileUploaded = mFavoriteList.fileUploaded;
		favorite.myHash = mFavoriteList.myHash;
		
		if(Strings.isEmpty(favList.syncBit)) {
			favorite.syncBit = "";
		} else {
			favorite.syncBit = favList.syncBit;
		}
		
		if(Strings.isEmpty(mFavoriteList.idFromServer)) {
			favorite.idFromServer = "";
		} else {
			favorite.idFromServer = mFavoriteList.idFromServer;
		}
		
		if(Strings.isEmpty(mFavoriteList.fileUpdatedAt)) {
			favorite.fileUpdatedAt = "";
		} else {
			favorite.fileUpdatedAt = mFavoriteList.fileUpdatedAt;
		}
		
		if(Strings.isEmpty(mFavoriteList.updatedAt)) {
			favorite.updatedAt = "";
		} else {
			favorite.updatedAt = mFavoriteList.updatedAt;
		}
		mFavoriteList = favorite;
		return favorite;
	}

	private void saveEntry() {
		Entry toSave = getSaveEntryData(dateBarDateview,dateViewString);
		toSave.syncBit = getString(R.string.syncbit_not_synced);
		////// ******* Update database if user added additional info *******///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editExpenseEntryById(toSave);
		mDatabaseAdapter.close();

		if(!intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_SHOW_PAGE)) {
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			intentExpenseListing.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString(Constants.KEY_HIGHLIGHT, toSave.id);
			if(toSave.timeInMillis != null) {mToHighLight.putLong(Constants.KEY_TIME_IN_MILLIS_TO_SET_TAB, toSave.timeInMillis); }
			intentExpenseListing.putExtras(mToHighLight);
			SyncHelper.startSync();
			if(!intentExtras.containsKey(Constants.KEY_POSITION)) {
				startActivity(intentExpenseListing);
			} else {
				mToHighLight.putInt(Constants.KEY_POSITION, intentExtras.getInt(Constants.KEY_POSITION));
				mToHighLight.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, getEntryListOnResult(toSave));
				setActivityResult(mToHighLight);
			}
		} else {
			Bundle tempBundle = new Bundle();
			tempBundle.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, getEntryListOnResult(toSave));
			if(intentExtras.containsKey(Constants.KEY_POSITION)) {
				tempBundle.putInt(Constants.KEY_POSITION , intentExtras.getInt(Constants.KEY_POSITION));
				if(checkEntryModified()) {
					tempBundle.putBoolean(Constants.KEY_DATA_CHANGED, true);
				}
			}
			Intent mIntent = new Intent();
			mIntent.putExtras(tempBundle);
			setResult(Activity.RESULT_OK, mIntent);
			SyncHelper.startSync();
		}
		
		finish();
	}
	
	private void saveFavoriteEntry() {
		if(checkFavoriteComplete()) {
			Favorite toSaveFav = getSaveFavoriteData();
			toSaveFav.syncBit = getString(R.string.syncbit_not_synced);
			////// ******* Update database if user added additional info *******///////
			mDatabaseAdapter.open();
			mDatabaseAdapter.editFavoriteEntryById(toSaveFav);
			mDatabaseAdapter.close();
			SyncHelper.startSync();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, getFavoriteListOnResult(toSaveFav));
			setActivityResult(bundle);
			finish();
		} else {
			ConfirmSaveEntryDialog confirmSaveEntryDialog = new ConfirmSaveEntryDialog(this);
			confirmSaveEntryDialog.setMessage(getString(R.string.unfinish_fav_text));
			doConfirmDialogActionToDiscardOrDismiss(confirmSaveEntryDialog);
		}
	}
	
	private void setActivityResult(Bundle bundle) {
		Intent intent = new Intent();
		if(isChanged) {
			bundle.putBoolean(Constants.KEY_DATA_CHANGED, isChanged);
			intentExtras.putAll(bundle);
		}
		intent.putExtras(intentExtras);
		setResult(Activity.RESULT_OK, intent);
	}

	protected Boolean checkEntryModified() {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mEditList.timeInMillis);
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if ((isTagModified(mEditList.description) || isAmountModified(mEditList.amount)) && dateBarDateview.getText().toString().equals(new DisplayDate(mCalendar).getDisplayDate())) {
			return true;
		}
		return false;
	}
	
	private Boolean checkFavoriteModified() {
		if (isTagModified(mFavoriteList.description) || isAmountModified(mFavoriteList.amount)) {
			return true;
		}
		return false;
	}
	
	private boolean isAmountModified(String amount) {
		if(editAmount.getText().toString().equals("")) {
			if(amount.equals("?") || amount.equals("")) {
				return false;
			} else {
				return true;
			}
		} else {
			if(amount.equals("?") || amount.equals("")) {
				return true;
			} else {
				if(Double.parseDouble(editAmount.getText().toString()) != Double.parseDouble(amount))
					return true;
				else 
					return false;
			}
		}
	}
	
	private boolean isTagModified(String description) {
		if(editTag.getText().toString().equals("")) {
			if(description.equals(getString(typeOfEntryFinished)) || description.equals(getString(typeOfEntryUnfinished)) || description.equals("")) {
				return false;
			} else {
				return true;
			}
		} else {
			if(!editTag.getText().toString().equals(description))
				return true;
			else 
				return false;
		}
	}

	@Override
	public void onClick(View v) {
		setSoftPanToInputMode();
		////////******** Adding Action to save entry ********* ///////////
		switch (v.getId()) {
		case R.id.edit_save_entry:
			if(isFromFavorite) {
				saveFavoriteEntry();
			} else {
				saveEntry();
			}
			break;
		
		case R.id.edit_delete:
			if(SharedPreferencesHelper.getSharedPreferences().getBoolean(getString(R.string.pref_key_delete_dialog), false)) {
				showDeleteDialog();
			} else {
				if(isFromFavorite) {
					deleteFavorite();
				} else {
					deleteEntry();
				}
			}
			break;
		default:
			break;
		}
	}
	
	private void showDeleteDialog() {
		final DeleteDialog mDeleteDialog = new DeleteDialog(this);
		mDeleteDialog.show();
		mDeleteDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mDeleteDialog.isDelete()) {
					if(isFromFavorite) {
						deleteFavorite();
					} else {
						deleteEntry();
					}
				}
			}
		});
	}

	private void deleteEntry() {
		isChanged = true;
		
		////// ******* Delete entry from database ******** /////////
		mDatabaseAdapter.open();
//		if(Strings.isEmpty(entry.updatedAt)) {
//			deleteFile();
//			mDatabaseAdapter.permanentDeleteEntryTableEntryID(entry.id);
//		} else {
			mDatabaseAdapter.deleteExpenseEntryByID(entry.id);
//		}
		mDatabaseAdapter.close();

		Bundle tempBundle = new Bundle();
		if(intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_SHOW_PAGE)) {
			Entry displayList = new Entry();
			tempBundle.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, displayList);
			mEditList = displayList;
			startIntentAfterDelete(tempBundle);
		}
		if(intentExtras.containsKey(Constants.KEY_POSITION)) {
			tempBundle = new Bundle();
			Intent intent = new Intent();
			tempBundle.putInt(Constants.KEY_POSITION, intentExtras.getInt(Constants.KEY_POSITION));
			if(isChanged) {
				tempBundle.putBoolean(Constants.KEY_DATA_CHANGED, isChanged);
			}
			intent.putExtras(tempBundle);
			setResult(Activity.RESULT_CANCELED, intent);
		}
		SyncHelper.startSync();

		finish();
	}
	
	private void deleteFavorite() {
		isChanged = true;
		
		////// ******* Delete entry from database ******** /////////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editFavoriteHashEntryTable(mFavoriteList.myHash);
//		if(Strings.isEmpty(mFavoriteList.updatedAt)) {
//			fileHelper.deleteAllFavoriteFiles(mFavoriteList.favId);
//			mDatabaseAdapter.permanentDeleteFavoriteTableEntryID(mFavoriteList.favId);
//		} else {
			mDatabaseAdapter.deleteFavoriteEntryByID(mFavoriteList.id);
//		}
		
		mDatabaseAdapter.close();

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.KEY_DATA_CHANGED, true);
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_POSITION)) {
			bundle.putInt(Constants.KEY_POSITION, intentExtras.getInt(Constants.KEY_POSITION));
		}
		intent.putExtras(bundle);
		setResult(Activity.RESULT_CANCELED,intent);
		SyncHelper.startSync();
		finish();
	}

	@Override
	public void onBackPressed() {
		ConfirmSaveEntryDialog mConfirmSaveEntryDialog = new ConfirmSaveEntryDialog(this);
		if(intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_SHOW_PAGE) || intentExtras.containsKey(Constants.KEY_POSITION)) {
			mConfirmSaveEntryDialog.setMessage(getString(R.string.backpress_edit_entry_text));
		} else {
			mConfirmSaveEntryDialog.setMessage(getString(R.string.backpress_new_entry_text));
		}
		if(!isFromFavorite) {
			if(intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_SHOW_PAGE) || intentExtras.containsKey(Constants.KEY_POSITION)) {
				//if coming from show page or listing
				if(checkEntryModified()) {
					doConfirmDialogAction(mConfirmSaveEntryDialog);
				} else {
					finishAndSetResult();
				}
			} else {
				if((editAmount.getText().toString().equals("") && editTag.getText().toString().equals("")) && (typeOfEntry == R.string.text || typeOfEntryFinished == R.string.finished_textentry || typeOfEntryUnfinished == R.string.unfinished_textentry)) {
					deleteEntry();
				} else {
					doConfirmDialogActionWithDelete(mConfirmSaveEntryDialog);
				}
			}
		} else {
			if(checkFavoriteModified()) {
				doConfirmDialogAction(mConfirmSaveEntryDialog);
			} else {
				if(doTaskIfChanged()) {
					saveFavoriteEntry();
				}
				super.onBackPressed();
			}
		}
	}
	
	//Confirm
	private void doConfirmDialogActionWithDelete(final ConfirmSaveEntryDialog mConfirmSaveEntryDialog) {
		mConfirmSaveEntryDialog.show();
		mConfirmSaveEntryDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mConfirmSaveEntryDialog.isOK()) {
					deleteEntry();
				}
			}
		});
	}
	
	private void doConfirmDialogAction(final ConfirmSaveEntryDialog mConfirmSaveEntryDialog) {
		mConfirmSaveEntryDialog.show();
		mConfirmSaveEntryDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mConfirmSaveEntryDialog.isOK()) {
					if(isFromFavorite && doTaskIfChanged()) {
						saveFavoriteEntry();
					}
					finishAndSetResult();
				}
			}
		});
	}
	
	private void doConfirmDialogActionToDiscardOrDismiss(final ConfirmSaveEntryDialog mConfirmSaveEntryDialog) {
		mConfirmSaveEntryDialog.show();
		mConfirmSaveEntryDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mConfirmSaveEntryDialog.isOK()) {
					finish();	
				}
			}
		});
	}
	
	private void finishAndSetResult() {
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_POSITION , intentExtras.getInt(Constants.KEY_POSITION));
		setActivityResult(bundle);
		finish();
	}
	
	@Override
	protected void onPause() {
		setIMM();
		super.onPause();
	}
	
	private void setIMM() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editAmount.getWindowToken(), InputMethodManager.RESULT_HIDDEN);
	}

	protected void startIntentAfterDelete(Bundle tempBundle){
		Intent mIntent = new Intent();
		mIntent.putExtras(tempBundle);
		setResult(Activity.RESULT_CANCELED, mIntent);
	}
	
	protected boolean doTaskIfChanged(){
		return false;
	}
	
	protected void createDatabaseEntry() {
		if(!isFromFavorite && entry.id == null) {
			if(typeOfEntry == R.string.text) {
				addEntry();
			}
			else if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				addEntry();
			}
		}
	}
	
	private void addEntry() {
		Entry toInsert = new Entry();
		if (!dateBarDateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
					DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
					toInsert.timeInMillis = mDateHelper.getTimeMillis();
				} else {
					if(!intentExtras.containsKey(Constants.KEY_TIME_IN_MILLIS)) {
						DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
						toInsert.timeInMillis = mDateHelper.getTimeMillis();
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong(Constants.KEY_TIME_IN_MILLIS));
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString(),mCalendar);
						toInsert.timeInMillis = mDateHelper.getTimeMillis();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			toInsert.timeInMillis = mCalendar.getTimeInMillis();
		}
		
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			toInsert.location = LocationHelper.currentAddress;
		}
		
		toInsert.type = getString(typeOfEntry);
		mDatabaseAdapter.open();
		entry.id = mDatabaseAdapter.insertToEntryTable(toInsert).toString();
		mDatabaseAdapter.close();
	}
	
	protected abstract void setDefaultTitle();
	protected abstract boolean checkFavoriteComplete();
	protected void deleteFile(){}
}
