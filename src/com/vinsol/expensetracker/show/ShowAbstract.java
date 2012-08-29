/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import java.util.Calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.DeleteDialog;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.helpers.FileHelper;
import com.vinsol.expensetracker.helpers.SharedPreferencesHelper;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.Log;

abstract class ShowAbstract extends BaseActivity implements OnClickListener {

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
	private String tempfavID;
	protected FileHelper fileHelper;
	protected FavoriteHelper mFavoriteHelper;
	
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
		setContentView(R.layout.show_page);
		fileHelper = new FileHelper();
		showEdit = (Button) findViewById(R.id.show_edit);
		showDelete = (Button) findViewById(R.id.show_delete);
		showHeaderTitle = (TextView) findViewById(R.id.header_title);
		mDatabaseAdapter = new DatabaseAdapter(this);
		showAmount = (TextView) findViewById(R.id.show_amount);
		showTag = (TextView) findViewById(R.id.show_tag_textview);
		showEdit.setOnClickListener(this);
		showDelete.setOnClickListener(this);
		intentExtras = getIntent().getExtras();
	}
	
	public void showHelper() {
		// ///// ****** Assigning memory ******* /////////
		
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			mShowList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);

			if ((!(mShowList.amount.equals("") || mShowList.amount == null)) && !mShowList.amount.contains("?")) {
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
			} else {
				new ShowDateHandler(this,typeOfEntry);
			}
			
			tempfavID = mShowList.favorite;
		}
	}
	
	public void doTaskOnActivityResult() {
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			mShowList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
			
			if(!new CheckEntryComplete().isEntryComplete(mShowList, this)) {
				finish();
			}
			
			if (!(mShowList.amount.equals("") || mShowList.amount == null)) {
				if (!mShowList.amount.contains("?"))
					if(mShowList.amount.endsWith(".0")) {
						mShowList.amount = mShowList.amount+"0";
						showAmount.setText(mShowList.amount);
					}
					else { 
						showAmount.setText(mShowList.amount);
					}
				else
					showAmount.setText("?");
			} else {
				showAmount.setText("?");
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
		setResultModifiedToListing();
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
			case R.id.show_delete:
				if(SharedPreferencesHelper.getSharedPreferences().getBoolean(getString(R.string.pref_key_delete_dialog), false)) {
					showDeleteDialog();
				} else {
					delete();
				}
				break;
	
			case R.id.show_edit:
				intentExtras.putBoolean(Constants.KEY_IS_COMING_FROM_SHOW_PAGE, true);
				intentExtras.remove(Constants.KEY_ENTRY_LIST_EXTRA);
				if(mShowList.amount.endsWith(".00")) {
					mShowList.amount = mShowList.amount.substring(0, mShowList.amount.length()-3);
				}
				intentExtras.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, mShowList);
				editAction();
				break;
				
			default:
				break;
		}
	}

	private void delete() {
		Log.d("*********************** Deleting From ShowAbstract*************************");
		if (mShowList.id != null) {
			mDatabaseAdapter.open();
//			if(Strings.isEmpty(mShowList.updatedAt)) {
//				deleteFile();
//				mDatabaseAdapter.permanentDeleteEntryTableEntryID(mShowList.id);
//			} else {
				mDatabaseAdapter.deleteExpenseEntryByID(mShowList.id);
//			}
			mDatabaseAdapter.close();
			SyncHelper.startSync();
			Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
			if(intentExtras.containsKey(Constants.KEY_POSITION)) {
				intentExtras.putBoolean(Constants.KEY_DATA_CHANGED, true);
				setResultCanceled();
			}

			
			finish();
		} else {
			Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setResultCanceled() {
		Intent intent = new Intent();
		intent.putExtras(intentExtras);
		setResult(Activity.RESULT_CANCELED, intent);
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

	private void setResultModifiedToListing() {
		if(!istempfavIdequalsfavId()) {
			intentExtras.putBoolean(Constants.KEY_DATA_CHANGED, true);
		}
		Intent intent = new Intent();
		intent.putExtras(intentExtras);
		setResult(Activity.RESULT_OK, intent);
	}

	private boolean istempfavIdequalsfavId() {
		if(tempfavID == null && mShowList.favorite == null) {
			return true;
		}
		if(tempfavID == null || mShowList.favorite == null) {
			return false;
		}
		if(tempfavID.equals(mShowList.favorite)) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_DATA_CHANGED)) {
			setResultModifiedToListing();	
		}
		finish();
		super.onBackPressed();
	}
	
	protected abstract void editAction();

	protected void deleteFile() {}

}
