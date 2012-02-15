package com.vinsol.expensetracker.helpers;

import java.io.File;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FavoriteHelper implements OnClickListener{
	
	private ToggleButton showAddFavorite;
	private TextView showAddFavoriteTextView;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private FileHelper fileHelper;
	private Entry mShowList;
	
	public FavoriteHelper(Activity activity,DatabaseAdapter mDatabaseAdapter,FileHelper fileHelper,Entry mShowList) {
		this.mDatabaseAdapter = mDatabaseAdapter;
		this.activity = activity;
		this.mShowList = mShowList;
		this.fileHelper = fileHelper;
		showAddFavorite = (ToggleButton) activity.findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) activity.findViewById(R.id.show_add_favorite_textView);
		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		if(this.mShowList.favId != null) {
			if(!this.mShowList.favId.equals("")) {
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
		showAddFavorite.setOnClickListener(this);
		showAddFavoriteTextView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.show_add_favorite:
			case R.id.show_add_favorite_textView:
				FlurryAgent.onEvent(activity.getString(R.string.added_to_favorite));
				Boolean toCheck;
				if(v.getId() == R.id.show_add_favorite) {
					toCheck = showAddFavorite.isChecked();
				} else {
					toCheck = !showAddFavorite.isChecked();
				}
				onClickFavorite(toCheck);
				break;

		default:
			break;
		}
	}
	
	public void onClickFavorite(Boolean toCheck) {
		Long favID = null;
		if(toCheck) {
			if(mShowList.type.equals(activity.getString(R.string.text))) {
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.insertToFavoriteTable(mShowList);
				mDatabaseAdapter.close();
			} else if(mShowList.type.equals(activity.getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						mDatabaseAdapter.open();
						favID = mDatabaseAdapter.insertToFavoriteTable(mShowList);
						mDatabaseAdapter.close();
						
						fileHelper.copyAllToFavorite(mShowList.id, favID.toString());
						File mFile = fileHelper.getCameraFileLargeFavorite(favID.toString());
						File mFileSmall = fileHelper.getCameraFileSmallFavorite(favID.toString());
						File mFileThumbnail = fileHelper.getCameraFileThumbnailFavorite(favID.toString());
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
						} else {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteFavoriteTableEntryID(favID+"");
							mDatabaseAdapter.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if(mShowList.type.equals(activity.getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					try {
						mDatabaseAdapter.open();
						favID = mDatabaseAdapter.insertToFavoriteTable(mShowList);
						mDatabaseAdapter.close();
						fileHelper.copyAllToFavorite(mShowList.id, favID.toString());
						File mFile = fileHelper.getAudioFileFavorite(favID.toString());
						if(!mFile.canRead()) {
							mDatabaseAdapter.open();
							mDatabaseAdapter.deleteFavoriteTableEntryID(favID+"");
							mDatabaseAdapter.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			mShowList.favId = favID+"";
			mDatabaseAdapter.open();
			mDatabaseAdapter.editEntryTable(mShowList);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
		} else if(mShowList.id.equals(activity.getString(R.string.text))) {
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteIdEntryTable(mShowList.id);
				mDatabaseAdapter.close();
				doTaskAfter(favID);
			} else if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteIdEntryTable(mShowList.id);
				mDatabaseAdapter.close();
				fileHelper.deleteAllFavoriteFiles(favID.toString());
				doTaskAfter(favID);
			} else {
				Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
	}
	
	private void doTaskAfter(Long favID) {
		mDatabaseAdapter.open();
		mDatabaseAdapter.deleteFavoriteTableEntryID(favID+"");
		mDatabaseAdapter.close();
		mDatabaseAdapter.open();
		mDatabaseAdapter.editFavoriteIdEntryTable(favID+"");
		mDatabaseAdapter.close();
		showAddFavorite.setChecked(false);
		mShowList.favId = null;
		showAddFavoriteTextView.setText("Add to Favorite");
	}	

}
