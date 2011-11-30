package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.vinsol.expensetracker.utils.FileCopyFavorite;
import com.vinsol.expensetracker.utils.FileDeleteFavorite;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

class FavoriteHelper implements OnCheckedChangeListener{

	private Context mContext;
	private ArrayList<String> mShowList;
	private ToggleButton show_text_voice_camera_add_favorite;
	private DBAdapterFavorite mDbAdapterFavorite;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	
	FavoriteHelper(Context _context,ArrayList<String> _mShowList) {
		mContext = _context;
		mShowList = _mShowList;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_add_favorite = (ToggleButton) activity.findViewById(R.id.show_text_voice_camera_add_favorite);
		show_text_voice_camera_add_favorite.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(mContext);
		mDatabaseAdapter = new DatabaseAdapter(mContext);
		if(mShowList.get(4) != null){
			if(!mShowList.get(4).equals("")){
				show_text_voice_camera_add_favorite.setChecked(true);
			} else {
				show_text_voice_camera_add_favorite.setChecked(false);
			}
		}
		show_text_voice_camera_add_favorite.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			HashMap<String, String> _list = new HashMap<String, String>();
			Long favID = null;
			_list.put(DBAdapterFavorite.KEY_AMOUNT, mShowList.get(2));
			_list.put(DBAdapterFavorite.KEY_TYPE, mShowList.get(5));
			if(mShowList.get(5).equals(mContext.getString(R.string.text))){
				_list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insert_to_database(_list);
				mDbAdapterFavorite.close();
			} else if(mShowList.get(5).equals(mContext.getString(R.string.camera))){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.get(1).equals("") && !mShowList.get(1).equals(mContext.getString(R.string.unfinished_cameraentry)) && mShowList.get(1) != null){
						_list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insert_to_database(_list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(Long.parseLong(mShowList.get(0)), favID);
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+favID+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
							Toast.makeText(mContext, "Exists", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mContext, "Not Exists", Toast.LENGTH_SHORT).show();
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
				Log.d("voice", "voice");
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.get(1).equals("") && !mShowList.get(1).equals(mContext.getString(R.string.unfinished_voiceentry)) && mShowList.get(1) != null){
						_list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insert_to_database(_list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(Long.parseLong(mShowList.get(0)),favID);
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+favID+".amr");
						if(mFile.canRead()){
							Toast.makeText(mContext, "Exists", Toast.LENGTH_SHORT).show();
							
						} else {
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(Long.toString(favID));
							mDbAdapterFavorite.close();
							Toast.makeText(mContext, "Not Exists", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			
			Log.d("Fac", favID+"");
			_list = new HashMap<String, String>();
			_list.put(DatabaseAdapter.KEY_ID, mShowList.get(0));
			_list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(_list);
			mDatabaseAdapter.close();
			Toast.makeText(mContext, "Added to Favorites", Toast.LENGTH_SHORT).show();
		} else {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				String favID = null;
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteId(mShowList.get(0));
				mDatabaseAdapter.close();
				
				Log.v("favID", favID);
				new FileDeleteFavorite(Long.parseLong(favID));
				mDbAdapterFavorite.open();
				mDbAdapterFavorite.deleteDatabaseEntryID(favID);
				mDbAdapterFavorite.close();
				
				mDatabaseAdapter.open();
				mDatabaseAdapter.editDatabaseFavorite(favID);
				mDatabaseAdapter.close();
				Toast.makeText(mContext, "Deleted from Favorites", Toast.LENGTH_SHORT).show();
				
			} else {
				Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			
			//TODO if deleted from favorites
			
		}	
	}
	
	
	
}
