package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.vinsol.expensetracker.utils.FileCopyFavorite;
import com.vinsol.expensetracker.utils.FileDeleteFavorite;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

class FavoriteHelper implements OnClickListener{

	private Context mContext;
	private ArrayList<String> mShowList;
	private ToggleButton show_text_voice_camera_add_favorite;
	private DBAdapterFavorite mDbAdapterFavorite;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private LinearLayout show_text_voice_camera_add_favorite_layout;
	private TextView show_text_voice_camera_add_favorite_textView;
	
	FavoriteHelper(Context _context,ArrayList<String> _mShowList) {
		mContext = _context;
		mShowList = _mShowList;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_add_favorite = (ToggleButton) activity.findViewById(R.id.show_text_voice_camera_add_favorite);
		show_text_voice_camera_add_favorite_layout = (LinearLayout) activity.findViewById(R.id.show_text_voice_camera_add_favorite_layout);
		show_text_voice_camera_add_favorite_textView = (TextView) activity.findViewById(R.id.show_text_voice_camera_add_favorite_textView);
		
		show_text_voice_camera_add_favorite.setVisibility(View.VISIBLE);
		show_text_voice_camera_add_favorite_textView.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(mContext);
		mDatabaseAdapter = new DatabaseAdapter(mContext);
		if(mShowList.get(4) != null){
			if(!mShowList.get(4).equals("")){
				show_text_voice_camera_add_favorite.setChecked(true);
				show_text_voice_camera_add_favorite_textView.setText("Remove from Favorite");
			} else {
				show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
				show_text_voice_camera_add_favorite.setChecked(false);
			}
		} else {
			show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
			show_text_voice_camera_add_favorite.setChecked(false);
		}
		show_text_voice_camera_add_favorite_layout.setOnClickListener(this);
	}

	public void setShowList(ArrayList<String> mShowList2) {
		
		mShowList = mShowList2;
		if(mShowList.get(4) != null) {
			if(mShowList.get(4).equals("")){
				show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
				show_text_voice_camera_add_favorite.setChecked(false);
			} else {
				show_text_voice_camera_add_favorite_textView.setText("Remove from Favorite");
				show_text_voice_camera_add_favorite.setChecked(true);
			}
		} else {
			show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
			show_text_voice_camera_add_favorite.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		
		if(!show_text_voice_camera_add_favorite.isChecked()){
			HashMap<String, String> _list = new HashMap<String, String>();
			Long favID = null;
			_list.put(DBAdapterFavorite.KEY_AMOUNT, mShowList.get(2));
			_list.put(DBAdapterFavorite.KEY_TYPE, mShowList.get(5));
			if(mShowList.get(5).equals(mContext.getString(R.string.text))){
				_list.put(DBAdapterFavorite.KEY_TAG, mShowList.get(1));
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insert_to_database(_list);
				mDbAdapterFavorite.close();
				ShowTextActivity.favID = Long.toString(favID);
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
						} else {
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(Long.toString(favID));
							mDbAdapterFavorite.close();
						}
						ShowCameraActivity.favID = Long.toString(favID);
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if(mShowList.get(5).equals(mContext.getString(R.string.voice))){
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
							
						} else {
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(Long.toString(favID));
							mDbAdapterFavorite.close();
						}
						ShowVoiceActivity.favID = Long.toString(favID);
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			
			_list = new HashMap<String, String>();
			_list.put(DatabaseAdapter.KEY_ID, mShowList.get(0));
			_list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(_list);
			mDatabaseAdapter.close();
			show_text_voice_camera_add_favorite.setChecked(true);
			show_text_voice_camera_add_favorite_textView.setText("Remove from Favorite");
		} else if(mShowList.get(5).equals(mContext.getString(R.string.text))){
			
				ShowTextActivity.favID = null;
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
				show_text_voice_camera_add_favorite.setChecked(false);
				show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
				
			} else if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				ShowVoiceActivity.favID = null;
				ShowCameraActivity.favID = null;
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
				show_text_voice_camera_add_favorite.setChecked(false);
				show_text_voice_camera_add_favorite_textView.setText("Add to Favorite");
			} else {
				Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
			
			//TODO if deleted from favorites
		}	
		
}
