/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.edit;

import java.util.Calendar;

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
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.ConfirmSaveEntryDialog;
import com.vinsol.expensetracker.helpers.DateHandler;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.DeleteDialog;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.listing.ExpenseListing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;

abstract class EditAbstract extends Activity implements OnClickListener {
	
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
		editAmount.setOnKeyListener(focusTagOnEnter);
		editAmount.setOnFocusChangeListener(focusChangeListener);
		////////********* Get intent extras ******** ////////////
		intentExtras = getIntent().getExtras();
		if(intentExtras != null && intentExtras.containsKey(Constants.IS_COMING_FROM_FAVORITE)) {isFromFavorite = true;}
	}
	
	private OnKeyListener focusTagOnEnter = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_ENTER) {
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

		if (intentExtras.containsKey("_id"))
			entry.id = intentExtras.getLong("_id")+"";

		if(intentExtras.containsKey("setLocation")) {
			setLocation = intentExtras.getBoolean("setLocation");
		}
		
		if (!isFromFavorite && intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
			mEditList = intentExtras.getParcelable(Constants.ENTRY_LIST_EXTRA);
			mFavoriteList = null;
			entry.id = mEditList.id;
			entry.amount = mEditList.amount;
			entry.description = mEditList.description;
			setText(entry.amount, entry.description);
			editHeaderTitle.setText(new DisplayDate().getLocationDate(mEditList.timeInMillis, mEditList.location));
			////////******** Handle Date Bar ********* ////////
			if (intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
				new DateHandler(this, mEditList.timeInMillis);
			} else if (intentExtras.containsKey(Constants.TIME_IN_MILLIS)) {
				new DateHandler(this, intentExtras.getLong(Constants.TIME_IN_MILLIS));
			} else {
				new DateHandler(this);
			}
		} else if(isFromFavorite && intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
			mEditList = null;
			((LinearLayout)findViewById(R.id.edit_date_bar)).setVisibility(View.GONE);
			mFavoriteList = intentExtras.getParcelable(Constants.ENTRY_LIST_EXTRA); 
			setText(mFavoriteList.amount, mFavoriteList.description);
		}
		
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
		Entry list = new Entry();
		list.id = entry.id;
		entry.amount = editAmount.getText().toString();
		entry.description = editTag.getText().toString();
		if (!entry.amount.equals(".") && !entry.amount.equals("")) {
			Double mAmount = Double.parseDouble(entry.amount);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			list.amount = mAmount+"";
		} else {
			list.amount = "";
		}
		
		list.description = entry.description;
		if (!editDateBarDateview.getText().toString().equals(dateViewString)) {
			try {
				if (!intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
					DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
					list.timeInMillis = mDateHelper.getTimeMillis();
				} else {
					if(!intentExtras.containsKey(Constants.TIME_IN_MILLIS)) {
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString());
						list.timeInMillis = mDateHelper.getTimeMillis();
					} else {
						Calendar mCalendar = Calendar.getInstance();
						mCalendar.setTimeInMillis(intentExtras.getLong(Constants.TIME_IN_MILLIS));
						mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						DateHelper mDateHelper = new DateHelper(editDateBarDateview.getText().toString(),mCalendar);
						list.timeInMillis = mDateHelper.getTimeMillis();
					}
				}
			} catch (Exception e) {
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
		favorite.favId = mFavoriteList.favId;
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
		
		Boolean isAmountNotEqual = false;
		try {
			isAmountNotEqual = Double.parseDouble(new StringProcessing().getStringDoubleDecimal(displayList.amount)) != Double.parseDouble(mEditList.amount);
		}catch(Exception e) {
			isAmountNotEqual = true;
		}
		
		if((!mEditList.description.equals(displayList.description)) || isAmountNotEqual || isChanged ) {
			isChanged = false;
			Entry listForFav = new Entry();
			listForFav.favId = "";
			listForFav.id = mEditList.id;
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editEntryTable(listForFav);
			mDatabaseAdapter.close();
			displayList.favId = "";
		} else {
			displayList.favId = mEditList.favId;
		}
			
		displayList.type = mEditList.type;	

		if(list.timeInMillis != null) {
			displayList.timeInMillis = list.timeInMillis;
		} else {
			displayList.timeInMillis = mEditList.timeInMillis;
		}
		displayList.location = mEditList.location;
		isChanged = checkEntryModified();
		mEditList = displayList;
		return displayList;
	}
	
	private Favorite getFavoriteListOnResult(Favorite favList) {
		Favorite favorite = new Favorite();
		favorite.favId = mFavoriteList.favId;
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
		
		favorite.type = mFavoriteList.type;	

		isChanged = checkFavoriteModified();
		if(isChanged) {
			DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
			mDatabaseAdapter.open();
			mDatabaseAdapter.editFavoriteIdEntryTable(Long.parseLong(mFavoriteList.favId));
			mDatabaseAdapter.close();
		}
		mFavoriteList = favorite;
		return favorite;
	}

	private void saveEntry() {
		Entry toSave = getSaveEntryData(dateBarDateview,dateViewString);
		////// ******* Update database if user added additional info *******///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editEntryTable(toSave);
		mDatabaseAdapter.close();
		if(!intentExtras.containsKey(Constants.IS_COMING_FROM_SHOW_PAGE)) {
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString(Constants.HIGHLIGHT, toSave.id);
			if(toSave.timeInMillis != null)
				mToHighLight.putLong(Constants.TIME_IN_MILLIS_TO_SET_TAB, toSave.timeInMillis);
			intentExpenseListing.putExtras(mToHighLight);
			if(!intentExtras.containsKey(Constants.POSITION)) {
				startActivity(intentExpenseListing);
			} else {
				mToHighLight.putInt(Constants.POSITION, intentExtras.getInt(Constants.POSITION));
				mToHighLight.putParcelable(Constants.ENTRY_LIST_EXTRA, getEntryListOnResult(toSave));
				setActivityResult(mToHighLight);
			}
		} else {
			Bundle tempBundle = new Bundle();
			tempBundle.putParcelable(Constants.ENTRY_LIST_EXTRA, getEntryListOnResult(toSave));
			if(intentExtras.containsKey(Constants.POSITION)) {
				if(checkEntryModified()) {
					tempBundle.putInt(Constants.POSITION , intentExtras.getInt(Constants.POSITION));
					tempBundle.putBoolean(Constants.DATA_CHANGED, true);
				}
			}
			saveEntryStartIntent(tempBundle);
		}
		finish();
	}
	
	private void saveFavoriteEntry() {
		Favorite toSaveFav = getSaveFavoriteData();
		////// ******* Update database if user added additional info *******///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editFavoriteTable(toSaveFav);
		mDatabaseAdapter.close();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.ENTRY_LIST_EXTRA, getFavoriteListOnResult(toSaveFav));
		setActivityResult(bundle);
		finish();
	}
	
	private void setActivityResult(Bundle bundle) {
		Intent intent = new Intent();
		if(isChanged) {
			bundle.putBoolean(Constants.DATA_CHANGED, isChanged);
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
	
	protected Boolean checkFavoriteModified() {
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
				return false;
			} else {
				if(Double.parseDouble(editAmount.getText().toString()) != Double.parseDouble(amount))
					return true;
				else 
					return false;
			}
		}
	}
	
	private boolean isTagModified(String description) {
		if(editTag.getText().equals("")) {
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
			FlurryAgent.onEvent(getString(R.string.save_button));
			if(isFromFavorite) {
				saveFavoriteEntry();
			} else {
				saveEntry();
			}
			break;
		
		case R.id.edit_delete:
			if(new SharedPreferencesHelper(this).getSharedPreferences().getBoolean(getString(R.string.pref_key_delete_dialog), false)) {
				showDeleteDialog();
			} else {
				delete();
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
					delete();
				}
			}
		});
	}

	private void delete() {
		FlurryAgent.onEvent(getString(R.string.delete_button));
		isChanged = true;
		deleteFile();
		////// ******* Delete entry from database ******** /////////
		mDatabaseAdapter.open();
		mDatabaseAdapter.deleteEntryTableEntryID(entry.id);
		mDatabaseAdapter.close();
		Bundle tempBundle = new Bundle();
		if(intentExtras.containsKey(Constants.IS_COMING_FROM_SHOW_PAGE)) {
			Entry displayList = new Entry();
			tempBundle.putParcelable(Constants.ENTRY_LIST_EXTRA, displayList);
			mEditList = displayList;
			startIntentAfterDelete(tempBundle);
		}
		if(intentExtras.containsKey(Constants.POSITION)) {
			tempBundle = new Bundle();
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			if(isChanged) {
				tempBundle.putBoolean(Constants.DATA_CHANGED, isChanged);
			}
			intentExpenseListing.putExtras(tempBundle);
			setResult(Activity.RESULT_CANCELED, intentExpenseListing);
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		FlurryAgent.onEvent(getString(R.string.back_pressed));
		ConfirmSaveEntryDialog mConfirmSaveEntryDialog = new ConfirmSaveEntryDialog(this);
		if(intentExtras.containsKey(Constants.IS_COMING_FROM_SHOW_PAGE) || intentExtras.containsKey(Constants.POSITION)) {
			//if coming from show page or listing
			if(checkEntryModified()) {
				mConfirmSaveEntryDialog.setMessage(getString(R.string.backpress_edit_entry_text));
				doConfirmDialogAction(mConfirmSaveEntryDialog);
			} else {
				finishAndSetResult();
			}
		} else {
			if((editAmount.getText().toString().equals("") && editTag.getText().toString().equals("")) && (typeOfEntry == R.string.text || typeOfEntryFinished == R.string.finished_textentry || typeOfEntryUnfinished == R.string.unfinished_textentry)) {
				delete();
			} else {
				mConfirmSaveEntryDialog.setMessage(getString(R.string.backpress_new_entry_text));
				doConfirmDialogAction(mConfirmSaveEntryDialog);
			}
		}
	}
	
	private void doConfirmDialogAction(final ConfirmSaveEntryDialog mConfirmSaveEntryDialog) {
		mConfirmSaveEntryDialog.show();
		mConfirmSaveEntryDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mConfirmSaveEntryDialog.isToSave()) {
					if(isFromFavorite) {
						saveFavoriteEntry();
					} else {
						saveEntry();
					}
					
				} else {
					finishAndSetResult();
				}	
			}
		});
	}
	
	private void finishAndSetResult() {
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.POSITION , intentExtras.getInt(Constants.POSITION));
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

	protected void startIntentAfterDelete(Bundle tempBundle) {}
	protected void deleteFile(){}
	abstract protected void saveEntryStartIntent(Bundle tempBundle);
}
