package com.vinsol.expensetracker.show;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.DBAdapterFavorite;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.FileCopyFavorite;
import com.vinsol.expensetracker.helpers.FileDeleteFavorite;
import com.vinsol.expensetracker.models.Entry;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

abstract class ShowAbstract extends Activity implements OnClickListener{

	protected TextView showAmount;
	protected TextView showTag;
	protected Entry mShowList;
	protected Bundle intentExtras;
	protected int typeOfEntryFinished;
	protected int typeOfEntryUnfinished;
	protected int typeOfEntry;
	protected TextView showHeaderTitle;
	protected final int SHOW_RESULT = 35;
	protected DatabaseAdapter mDatabaseAdapter;	
	protected Button showDelete;
	protected Button showEdit;
	private RelativeLayout dateBarRelativeLayout;
	private ToggleButton showAddFavorite;
	private TextView showAddFavoriteTextView;
	private DBAdapterFavorite mDbAdapterFavorite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_page);
		showEdit = (Button) findViewById(R.id.show_edit);
		showAddFavorite = (ToggleButton) findViewById(R.id.show_add_favorite);
		showAddFavoriteTextView = (TextView) findViewById(R.id.show_add_favorite_textView);
		showDelete = (Button) findViewById(R.id.show_delete);
		showHeaderTitle = (TextView) findViewById(R.id.header_title);
		mDatabaseAdapter = new DatabaseAdapter(this);
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTag = (TextView) findViewById(R.id.show_tag_textview);
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_date_bar); 
		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		showEdit.setOnClickListener(this);
		showDelete.setOnClickListener(this);
	}
	
	public void showHelper() {
		// ///// ****** Assigning memory ******* /////////
		
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getParcelable("mDisplayList");
			
			if (!(mShowList.amount.equals("") || mShowList.amount == null)) {
				if (!mShowList.amount.contains("?"))
					showAmount.setText(mShowList.amount);
			}
			
			if (!(mShowList.description.equals("") || mShowList.description == null || mShowList.description.equals(getString(typeOfEntryUnfinished)))) {
				showTag.setText(mShowList.description);
			} else {
				showTag.setText(getString(typeOfEntryFinished));
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(mShowList.timeInMillis);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.location != null)
				new ShowLocationHandler(this, mShowList.location);

			if(mShowList.timeInMillis != null) {
				new ShowDateHandler(this, mShowList.timeInMillis);
			}
			else {
				new ShowDateHandler(this,typeOfEntry);
			}
		}
	}
	
	public void doTaskOnActivityResult(Bundle _intentExtras){
		intentExtras = _intentExtras;
		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getParcelable("mDisplayList");
			
			if(!new CheckEntryComplete().isEntryComplete(mShowList, this)){
				finish();
			}
			
			if (!(mShowList.description.equals("") || mShowList.description == null || mShowList.description.equals(getString(typeOfEntryUnfinished)) || mShowList.description.equals(getString(typeOfEntryFinished)))) {
				showTag.setText(mShowList.description);
			} else {
				showTag.setText(getString(typeOfEntryFinished));
			}
			
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(mShowList.timeInMillis);
			mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			
			if(mShowList.location != null)
				new ShowLocationHandler(this, mShowList.location);
			
			if(mShowList.timeInMillis != null)
				new ShowDateHandler(this, mShowList.timeInMillis);
			else {
				new ShowDateHandler(this,typeOfEntry);
			}
			
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_delete:
			if (mShowList.id != null) {
				deleteAction();
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(mShowList.id);
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.show_edit:
			intentExtras.putBoolean("isFromShowPage", true);
			intentExtras.remove("mDisplayList");
			intentExtras.putParcelable("mDisplayList", mShowList);
			editAction();
			break;
			
		case R.id.show_add_favorite:
		case R.id.show_add_favorite_textView:
			Boolean toCheck;
			if(v.getId() == R.id.show_add_favorite){
				toCheck = showAddFavorite.isChecked();
			} else {
				toCheck = !showAddFavorite.isChecked();
			}
			onClickFavorite(toCheck);
			
			break;
		default:
			break;
		}
		if (v.getId() == R.id.show_delete) {
			
		}
	}
	
	protected void deleteAction() {
	}
	
	protected void editAction() {
	}
	
	
	public void FavoriteHelper() {

		showAddFavorite.setVisibility(View.VISIBLE);
		showAddFavoriteTextView.setVisibility(View.VISIBLE);
		mDbAdapterFavorite = new DBAdapterFavorite(this);
		if(mShowList.favId != null){
			if(!mShowList.favId.equals("")){
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

	public void onClickFavorite(Boolean toCheck) {
		Long favID = null;
		if(toCheck){
			HashMap<String, String> list = new HashMap<String, String>();
			list.put(DBAdapterFavorite.KEY_AMOUNT, mShowList.amount);
			list.put(DBAdapterFavorite.KEY_TYPE, mShowList.type);
			if(mShowList.type.equals(getString(R.string.text))) {
				list.put(DBAdapterFavorite.KEY_TAG, mShowList.description);
				mDbAdapterFavorite.open();
				favID = mDbAdapterFavorite.insertToDatabase(list);
				mDbAdapterFavorite.close();
				
			} else if(mShowList.type.equals(getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.description.equals("") && !mShowList.description.equals(getString(R.string.unfinished_cameraentry)) && mShowList.description != null){
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
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if(mShowList.type.equals(getString(R.string.voice))){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mShowList.description.equals("") && !mShowList.description.equals(getString(R.string.unfinished_voiceentry)) && mShowList.description != null){
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
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			}
			list = new HashMap<String, String>();
			list.put(DatabaseAdapter.KEY_ID, mShowList.id);
			list.put(DatabaseAdapter.KEY_FAVORITE, Long.toString(favID));
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(list);
			mDatabaseAdapter.close();
			showAddFavorite.setChecked(true);
			showAddFavoriteTextView.setText("Remove from Favorite");
		} else if(mShowList.id.equals(getString(R.string.text))) {
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
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
		
		}	
}
