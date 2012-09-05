/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
 */

package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.Strings;

public class FavoriteHelper implements OnClickListener {

	private ToggleButton showAddFavorite;
	private TextView showAddFavoriteTextView;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private FileHelper fileHelper;
	private Entry mShowList;
	private boolean isFromEditPage = false;

	public FavoriteHelper(Activity activity, DatabaseAdapter mDatabaseAdapter,
			FileHelper fileHelper, Entry mShowList) {
		this.mDatabaseAdapter = mDatabaseAdapter;
		this.activity = activity;
		this.mShowList = mShowList;
		this.fileHelper = fileHelper;
		setUIandClickListeners();
		if (this.mShowList.favorite != null) {
			if (!this.mShowList.favorite.equals("")) {
				showAddFavorite.setChecked(true);
				showAddFavoriteTextView.setText("Remove from Favorite");
			} else {
				showAddFavoriteTextView.setText("Add to Favorite");
				showAddFavorite.setChecked(false);
			}
		} else {
			showAddFavoriteTextView.setText("Add to Favorite");
			showAddFavorite.setChecked(false);
		}
	}

	public FavoriteHelper(Activity activity, DatabaseAdapter mDatabaseAdapter,
			FileHelper fileHelper, String type, String id, EditText amount,
			EditText description, Boolean isChanged) {
		this.mDatabaseAdapter = mDatabaseAdapter;
		this.activity = activity;
		this.fileHelper = fileHelper;
		isFromEditPage = true;
		mShowList = new Entry();
		mShowList.id = id;
		mShowList.type = type;
		mShowList.location = LocationHelper.currentAddress;
		setUIandClickListeners();
		showAddFavoriteTextView.setText("Add to Favorite");
		showAddFavorite.setChecked(false);
		MyTextWatcher myTextWatcher = new MyTextWatcher(amount, description,
				type, isChanged);
		amount.addTextChangedListener(myTextWatcher);
		description.addTextChangedListener(myTextWatcher);
	}

	private class MyTextWatcher implements TextWatcher {

		private EditText amount;
		private EditText description;
		private Boolean isChanged = false;
		private String type;

		public MyTextWatcher(EditText amount, EditText description,
				String type, Boolean isChanged) {
			this.amount = amount;
			this.description = description;
			this.isChanged = isChanged;
			this.type = type;
			favLayoutHandle(amount, description, type, isChanged);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			favLayoutHandle(amount, description, type, isChanged);
			if (showAddFavorite.isChecked()) {
				removeEntryFromFavorite();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do nothing
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (amount.getText() != null)
				mShowList.amount = amount.getText().toString();
			if (description.getText() != null)
				mShowList.description = description.getText().toString();
		}

	}

	private void favLayoutHandle(EditText amount, EditText description,
			String type, Boolean isChanged) {
		boolean checkStatus;
		if (type.equals(activity.getString(R.string.text))) {
			checkStatus = !amount.getText().toString().equals("")
					&& !description.getText().toString().equals("")
					&& !isChanged;
		} else if (type.equals(activity.getString(R.string.voice))
				|| type.equals(activity.getString(R.string.camera))) {
			checkStatus = !amount.getText().toString().equals("") && !isChanged;
		} else {
			checkStatus = false;
		}
		if (checkStatus) {
			showAddFavorite.setEnabled(true);
			showAddFavoriteTextView.setEnabled(true);
		} else {
			showAddFavorite.setEnabled(false);
			showAddFavoriteTextView.setEnabled(false);
		}
	}

	private void setUIandClickListeners() {
		showAddFavorite = (ToggleButton) activity
				.findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) activity
				.findViewById(R.id.show_add_favorite_textView);
		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		showAddFavorite.setOnClickListener(this);
		showAddFavoriteTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_add_favorite:
		case R.id.show_add_favorite_textView:
			Boolean toCheck;
			if (v.getId() == R.id.show_add_favorite) {
				toCheck = showAddFavorite.isChecked();
			} else {
				toCheck = !showAddFavorite.isChecked();
			}
			onClickFavorite(toCheck);
			SyncHelper.startSync();
			break;

		default:
			break;
		}
	}

	private void removeEntryFromFavorite() {
		String hash = null;
		if (mShowList.type.equals(activity.getString(R.string.text))) {
			mDatabaseAdapter.open();
			hash = mDatabaseAdapter.getFavoriteHashEntryTable(mShowList.id);
			mDatabaseAdapter.close();
			setDatabaseAndLayoutValues(hash);
		} else if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			mDatabaseAdapter.open();
			hash = mDatabaseAdapter.getFavoriteHashEntryTable(mShowList.id);
			mDatabaseAdapter.close();
			setDatabaseAndLayoutValues(hash);
		} else {
			Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void setDatabaseAndLayoutValues(String hash) {
		mDatabaseAdapter.open();
		mDatabaseAdapter.editFavoriteHashEntryTable(hash);
		mDatabaseAdapter.close();
		showAddFavorite.setChecked(false);
		mShowList.favorite = null;
		showAddFavoriteTextView.setText("Add to Favorite");
		Map<String, String> map = new HashMap<String, String>();
		map.put("Favorite Status ", false + "");
		map.put("Entry Type ", getTypeForFlurry());
		if (isFromEditPage) {
			FlurryAgent.onEvent(activity.getString(R.string.fav_from_new), map);
		} else {
			FlurryAgent
					.onEvent(activity.getString(R.string.fav_from_show), map);
		}
	}

	public void onClickFavorite(Boolean toCheck) {
		Long favID = null;
		String hash = null;
		if (toCheck) {
			if (mShowList.type.equals(activity.getString(R.string.text))) {
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.insertToFavoriteTable(mShowList);
				mDatabaseAdapter.close();
			} else if (mShowList.type.equals(activity
					.getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					try {
						mDatabaseAdapter.open();
						favID = mDatabaseAdapter
								.insertToFavoriteTable(mShowList);
						mDatabaseAdapter.close();
						fileHelper.copyAllToFavorite(mShowList.id,
								favID.toString());
						File mFile = fileHelper
								.getCameraFileLargeFavorite(favID.toString());
						File mFileSmall = fileHelper
								.getCameraFileSmallFavorite(favID.toString());
						File mFileThumbnail = fileHelper
								.getCameraFileThumbnailFavorite(favID
										.toString());
						if (!mFile.canRead() && !mFileSmall.canRead()
								&& !mFileThumbnail.canRead()) {
							mDatabaseAdapter.open();
							mDatabaseAdapter
									.deleteFavoriteEntryByID(favID + "");
							mDatabaseAdapter.close();
							// fileHelper.deleteAllFavoriteFiles(favID+"");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(activity, "sdcard not available",
							Toast.LENGTH_SHORT).show();
				}
			} else if (mShowList.type
					.equals(activity.getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					try {
						mDatabaseAdapter.open();
						favID = mDatabaseAdapter
								.insertToFavoriteTable(mShowList);
						mDatabaseAdapter.close();
						fileHelper.copyAllToFavorite(mShowList.id,
								favID.toString());
						File mFile = fileHelper.getAudioFileFavorite(favID
								.toString());
						if (!mFile.canRead()) {
							mDatabaseAdapter.open();
							mDatabaseAdapter
									.deleteFavoriteEntryByID(favID + "");
							mDatabaseAdapter.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(activity, "sdcard not available",
							Toast.LENGTH_SHORT).show();
				}
			}
			mShowList.syncBit = activity.getString(R.string.syncbit_not_synced);
			mDatabaseAdapter.open();
			mShowList.favorite = mDatabaseAdapter.getFavHashById(favID + "");
			mDatabaseAdapter.editExpenseEntryById(mShowList);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
			Map<String, String> map = new HashMap<String, String>();
			map.put("Favorite Status ", true + "");
			map.put("Entry Type ", getTypeForFlurry());
			if (isFromEditPage) {
				FlurryAgent.onEvent(activity.getString(R.string.fav_from_new),
						map);
			} else {
				FlurryAgent.onEvent(activity.getString(R.string.fav_from_show),
						map);
			}
			Toast.makeText(activity, "Added to Favorite", Toast.LENGTH_SHORT)
					.show();
		} else if (mShowList.type.equals(activity.getString(R.string.text))) {
			mDatabaseAdapter.open();
			hash = mDatabaseAdapter.getFavoriteHashEntryTable(mShowList.id);
			String tempId = mDatabaseAdapter.getFavIdByHash(hash);
			if (Strings.notEmpty(tempId))
				favID = Long.parseLong(tempId);
			mDatabaseAdapter.close();
			doTaskAfter(hash);
		} else if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			mDatabaseAdapter.open();
			hash = mDatabaseAdapter.getFavoriteHashEntryTable(mShowList.id);
			String tempId = mDatabaseAdapter.getFavIdByHash(hash);
			if (Strings.notEmpty(tempId))
				favID = Long.parseLong(tempId);
			mDatabaseAdapter.close();
			fileHelper.deleteAllFavoriteFiles(favID.toString());
			doTaskAfter(hash);
		} else {
			Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private String getTypeForFlurry() {
		if (mShowList.type.equals("0")) {
			return activity.getString(R.string.unknown_entry);
		} else if (mShowList.type.equals("1")) {
			return activity.getString(R.string.finished_textentry);
		} else if (mShowList.type.equals("2")) {
			return activity.getString(R.string.finished_cameraentry);
		} else if (mShowList.type.equals("3")) {
			return activity.getString(R.string.finished_voiceentry);
		}
		return "";
	}

	private void doTaskAfter(String hash) {
		mDatabaseAdapter.open();
		mDatabaseAdapter.deleteFavoriteEntryByHash(hash);
		mDatabaseAdapter.close();
		mDatabaseAdapter.open();
		mDatabaseAdapter.editFavoriteHashEntryTable(hash);
		mDatabaseAdapter.close();
		showAddFavorite.setChecked(false);
		mShowList.favorite = null;
		showAddFavoriteTextView.setText("Add to Favorite");
		Map<String, String> map = new HashMap<String, String>();
		map.put("Favorite Status ", false + "");
		map.put("Entry Type ", getTypeForFlurry());
		if (isFromEditPage) {
			FlurryAgent.onEvent(activity.getString(R.string.fav_from_new), map);
		} else {
			FlurryAgent
					.onEvent(activity.getString(R.string.fav_from_show), map);
		}
		Toast.makeText(activity, "Removed from Favorite", Toast.LENGTH_SHORT)
				.show();
	}

}
