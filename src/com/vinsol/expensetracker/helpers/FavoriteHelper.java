package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FileCopyFavorite;
import com.vinsol.expensetracker.helpers.FileDeleteFavorite;
import com.vinsol.expensetracker.models.ShowData;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FavoriteHelper implements OnClickListener{

	private Context mContext;
	private ArrayList<String> mShowList;
	private ToggleButton showAddFavorite;
	private DBAdapterFavorite mDbAdapterFavorite;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView showAddFavoriteTextView;
	
	public FavoriteHelper(Context context,ArrayList<String> mShowList) {
		this.mContext = context;
		this.mShowList = mShowList;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		showAddFavorite = (ToggleButton) activity.findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) activity.findViewById(R.id.show_add_favorite_textView);
		
		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(mContext);
		mDatabaseAdapter = new DatabaseAdapter(mContext);
		if(mShowList.get(4) != null){
			if(!mShowList.get(4).equals("")){
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

	public void setShowList(ArrayList<String> mShowList2) {
		mShowList = mShowList2;
		if(mShowList.get(4) != null) {
			if(mShowList.get(4).equals("")){
				showAddFavoriteTextView.setText("Add to Favorite");
				showAddFavorite.setChecked(false);
			} else {
				showAddFavoriteTextView.setText("Remove from Favorite");
				showAddFavorite.setChecked(true);
			}
		} else {
			showAddFavoriteTextView.setText("Add to Favorite");
			showAddFavorite.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		Boolean toCheck;
		if(v.getId() == R.id.show_add_favorite){
			toCheck = showAddFavorite.isChecked();
		} else {
			toCheck = !showAddFavorite.isChecked();
		}
		
		if(toCheck){
			HashMap<String, String> list = new HashMap<String, String>();
			Long favID = null;
			list.put(DBAdapterFavorite.KEY_AMOUNT, mShowList.get(2));
			list.put(DBAdapterFavorite.KEY_TYPE, mShowList.get(5));
			if(mShowList.get(5).equals(mContext.getString(R.string.text))) {
				list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insertToDatabase(list);
				mDbAdapterFavorite.close();
				
			} else if(mShowList.get(5).equals(mContext.getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.get(1).equals("") && !mShowList.get(1).equals(mContext.getString(R.string.unfinished_cameraentry)) && mShowList.get(1) != null){
						list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(Long.parseLong(mShowList.get(0)), favID);
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+favID+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
						} else {
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(Long.toString(favID));
							mDbAdapterFavorite.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if(mShowList.get(5).equals(mContext.getString(R.string.voice))){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.get(1).equals("") && !mShowList.get(1).equals(mContext.getString(R.string.unfinished_voiceentry)) && mShowList.get(1) != null){
						list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(Long.parseLong(mShowList.get(0)),favID);
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+favID+".amr");
						if(!mFile.canRead()){
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(Long.toString(favID));
							mDbAdapterFavorite.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			ShowData.staticFavID = Long.toString(favID);
			list = new HashMap<String, String>();
			list.put(DatabaseAdapter.KEY_ID, mShowList.get(0));
			list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(list);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
		} else if(mShowList.get(5).equals(mContext.getString(R.string.text))){
				ShowData.staticFavID = null;
				String favID = null;
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteId(mShowList.get(0));
				mDatabaseAdapter.close();
				
				mDbAdapterFavorite.open();
				mDbAdapterFavorite.deleteDatabaseEntryID(favID);
				mDbAdapterFavorite.close();
				
				mDatabaseAdapter.open();
				mDatabaseAdapter.editDatabaseFavorite(favID);
				mDatabaseAdapter.close();
				showAddFavorite.setChecked(false);
				showAddFavoriteTextView.setText("Add to Favorite");
				
			} else if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				ShowData.staticFavID = null;
				String favID = null;
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteId(mShowList.get(0));
				mDatabaseAdapter.close();
				new FileDeleteFavorite(Long.parseLong(favID));
				mDbAdapterFavorite.open();
				mDbAdapterFavorite.deleteDatabaseEntryID(favID);
				mDbAdapterFavorite.close();
				
				mDatabaseAdapter.open();
				mDatabaseAdapter.editDatabaseFavorite(favID);
				mDatabaseAdapter.close();
				showAddFavorite.setChecked(false);
				showAddFavoriteTextView.setText("Add to Favorite");
			} else {
				Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
		
		}	
		
}
