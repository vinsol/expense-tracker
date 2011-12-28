package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.util.HashMap;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FileCopyFavorite;
import com.vinsol.expensetracker.helpers.FileDeleteFavorite;
import com.vinsol.expensetracker.models.DisplayList;
import com.vinsol.expensetracker.models.StaticVariables;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FavoriteHelper implements OnClickListener{

	private Context mContext;
	private DisplayList mShowList;
	private ToggleButton showAddFavorite;
	private DBAdapterFavorite mDbAdapterFavorite;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView showAddFavoriteTextView;
	private Long favID;
	
	public FavoriteHelper(Context context,DisplayList mShowList2) {
		this.mContext = context;
		this.mShowList = mShowList2;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		showAddFavorite = (ToggleButton) activity.findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) activity.findViewById(R.id.show_add_favorite_textView);
		
		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(mContext);
		mDatabaseAdapter = new DatabaseAdapter(mContext);
		if(mShowList2.favorite != null){
			if(!mShowList2.favorite.equals("")){
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

	public void setShowList(DisplayList mShowList2) {
		mShowList = mShowList2;
		if(mShowList.favorite != null) {
			if(mShowList.favorite.equals("")){
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
			list.put(DBAdapterFavorite.KEY_AMOUNT, mShowList.amount);
			list.put(DBAdapterFavorite.KEY_TYPE, mShowList.type);
			if(mShowList.type.equals(mContext.getString(R.string.text))) {
				list.put(DBAdapterFavorite.KEY_TAG, mShowList.description);
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insertToDatabase(list);
				mDbAdapterFavorite.close();
				
			} else if(mShowList.type.equals(mContext.getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.description.equals("") && !mShowList.description.equals(mContext.getString(R.string.unfinished_cameraentry)) && mShowList.description != null){
						list.put(DBAdapterFavorite.KEY_TAG, mShowList.description);
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(mShowList.id, favID.toString());
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/"+favID+".jpg");
						File mFileSmall = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_small.jpg");
						File mFileThumbnail = new File("/sdcard/ExpenseTracker/Favorite/"+favID+"_thumbnail.jpg");
						if(mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()){
						} else {
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(favID);
							mDbAdapterFavorite.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if(mShowList.type.equals(mContext.getString(R.string.voice))){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.description.equals("") && !mShowList.description.equals(mContext.getString(R.string.unfinished_voiceentry)) && mShowList.description != null){
						list.put(DBAdapterFavorite.KEY_TAG, mShowList.description);
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(mShowList.id,favID.toString());
						File mFile = new File("/sdcard/ExpenseTracker/Favorite/Audio/"+favID+".amr");
						if(!mFile.canRead()){
							mDbAdapterFavorite.open();
							mDbAdapterFavorite.deleteDatabaseEntryID(favID);
							mDbAdapterFavorite.close();
						}
					} catch (Exception e) {	
					}
				} else {
					Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			StaticVariables.favID = favID;
			list = new HashMap<String, String>();
			list.put(DatabaseAdapter.KEY_ID, mShowList.id);
			list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(list);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
		} else if(mShowList.id.equals(mContext.getString(R.string.text))){
				StaticVariables.favID = null;
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteId(mShowList.id);
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
				StaticVariables.favID = null;
				mDatabaseAdapter.open();
				favID = mDatabaseAdapter.getFavoriteId(mShowList.id);
				mDatabaseAdapter.close();
				new FileDeleteFavorite(favID);
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
