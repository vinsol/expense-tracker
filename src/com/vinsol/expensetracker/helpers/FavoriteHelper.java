package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.util.HashMap;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.FileCopyFavorite;
import com.vinsol.expensetracker.helpers.FileDeleteFavorite;
import com.vinsol.expensetracker.models.Entry;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FavoriteHelper implements OnClickListener{

	private Context mContext;
	private Entry mEntry;
	
	private DBAdapterFavorite mDbAdapterFavorite;
	private Activity activity;
	private DatabaseAdapter mDatabaseAdapter;
	private ToggleButton showAddFavorite;
	private TextView showAddFavoriteTextView;
	private Long favID;
	
	public FavoriteHelper(Context context,Entry mEntry) {
		this.mContext = context;
		this.mEntry = mEntry;
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		showAddFavorite = (ToggleButton) activity.findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) activity.findViewById(R.id.show_add_favorite_textView);
		
		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(mContext);
		mDatabaseAdapter = new DatabaseAdapter(mContext);
		if(mEntry.favId != null){
			if(!mEntry.favId.equals("")){
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

	public void setShowList(Entry mEntry) {
		this.mEntry = mEntry;
		if(mEntry.favId != null) {
			if(mEntry.favId.equals("")){
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
			list.put(DBAdapterFavorite.KEY_AMOUNT, mEntry.amount);
			list.put(DBAdapterFavorite.KEY_TYPE, mEntry.type);
			if(mEntry.type.equals(mContext.getString(R.string.text))) {
				list.put(DBAdapterFavorite.KEY_TAG, mEntry.description);
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insertToDatabase(list);
				mDbAdapterFavorite.close();
				
			} else if(mEntry.type.equals(mContext.getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mEntry.description.equals("") && !mEntry.description.equals(mContext.getString(R.string.unfinished_cameraentry)) && mEntry.description != null){
						list.put(DBAdapterFavorite.KEY_TAG, mEntry.description);
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(mEntry.id, favID.toString());
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
			} else if(mEntry.type.equals(mContext.getString(R.string.voice))){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mEntry.description.equals("") && !mEntry.description.equals(mContext.getString(R.string.unfinished_voiceentry)) && mEntry.description != null){
						list.put(DBAdapterFavorite.KEY_TAG, mEntry.description);
					}
					try{
						mDbAdapterFavorite.open();
						favID = mDbAdapterFavorite.insertToDatabase(list);
						mDbAdapterFavorite.close();
						new FileCopyFavorite(mEntry.id,favID.toString());
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
			
			mEntry.favId = Long.toString(favID);
			list = new HashMap<String, String>();
			list.put(DatabaseAdapter.KEY_ID, mEntry.id);
			list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(list);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
		} else if(mEntry.id.equals(mContext.getString(R.string.text))) {
				doFavDeltask();
			} else if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				new FileDeleteFavorite(Long.parseLong(mEntry.favId));
				doFavDeltask();
			} else {
				Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
		
		}

	private void doFavDeltask() {
		mDbAdapterFavorite.open();
		mDbAdapterFavorite.deleteDatabaseEntryID(Long.parseLong(mEntry.favId));
		mDbAdapterFavorite.close();
		
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabaseFavorite(Long.parseLong(mEntry.favId));
		mDatabaseAdapter.close();
		mEntry.favId = null;
		showAddFavorite.setChecked(false);
		showAddFavoriteTextView.setText("Add to Favorite");
	}	
		
}
