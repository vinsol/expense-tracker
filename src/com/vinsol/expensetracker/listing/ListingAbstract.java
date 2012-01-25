/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.show.ShowVoiceActivity;

abstract class ListingAbstract extends Activity implements OnItemClickListener {

	protected List<ListDatetimeAmount> mDataDateList;
	protected SeparatedListAdapter mSeparatedListAdapter;
	protected List<Entry> mSubList;
	protected ConvertCursorToListString mConvertCursorToListString;
	protected StringProcessing mStringProcessing;
	protected ListView mListView;
	protected String highlightID = null;
	protected UnknownEntryDialog unknownDialog;
	protected final int RESULT = 35;
	protected Bundle intentExtras;
	private DatabaseAdapter mDatabaseAdapter;

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
		setContentView(R.layout.expense_listing);
		intentExtras = new Bundle();
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mStringProcessing = new StringProcessing();
		mDatabaseAdapter = new DatabaseAdapter(this);
		Bundle intentExtras = getIntent().getExtras();
		if(intentExtras != null) {
			if(intentExtras.containsKey(Constants.HIGHLIGHT)) {
				highlightID = intentExtras.getString(Constants.HIGHLIGHT);
			}
		}
	}
	
	protected Entry getListCurrentWeek(int j) {
		Entry templist = new Entry();
		templist.id = mSubList.get(j).id;
		if (mSubList.get(j).description != null && !mSubList.get(j).description.equals("")) {
			templist.description = mSubList.get(j).description;
		} else {
			CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
			if (mSubList.get(j).type.equals(getString(R.string.camera))) { 
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)) {
					templist.description = getString(R.string.finished_cameraentry);
				} else {
					templist.description = getString(R.string.unfinished_cameraentry);
				}
			} else if (mSubList.get(j).type.equals(getString(R.string.voice))) {
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)) {
					templist.description = getString(R.string.finished_voiceentry);
				} else {
					templist.description = getString(R.string.unfinished_voiceentry);
				}
			} else if (mSubList.get(j).type.equals(getString(R.string.text))) {
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)) {
					templist.description = getString(R.string.finished_textentry);
				} else {
					templist.description = getString(R.string.unfinished_textentry);
				}
			} else if (mSubList.get(j).type.equals(getString(R.string.favorite_entry))) {
				templist.description = "Unfinished Favorite Entry";
			} else if (mSubList.get(j).type.equals(getString(R.string.unknown))) {
				templist.description = getString(R.string.unknown_entry);
			}
		}

		if (mSubList.get(j).amount != null&& !mSubList.get(j).amount.equals("")) {
			templist.amount = mStringProcessing.getStringDoubleDecimal(mSubList.get(j).amount);
		} else {
			templist.amount = "?";
		}

		if (mSubList.get(j).favId != null && !mSubList.get(j).favId.equals("")) {
			templist.favId = mSubList.get(j).favId;
		} else {
			templist.favId = "";
		}

		if (mSubList.get(j).type != null && !mSubList.get(j).type.equals("")) {
			templist.type = mSubList.get(j).type;
		} else {
			templist.type = "";
		}

		templist.timeInMillis = mSubList.get(j).timeInMillis;
		templist.location = mSubList.get(j).location;
		return templist;
	}
	
	@Override
	public void onItemClick(final AdapterView<?> adapter, View v,final int position, long arg3) {
		final Entry mTempClickedList = (Entry) adapter.getItemAtPosition(position);
		String id = mTempClickedList.id;
		if (!id.contains(",")) {
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.ENTRY_LIST_EXTRA, mTempClickedList);
			bundle.putInt(Constants.POSITION, position);
			setBundleListingExtra(bundle);
			CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
			if (mTempClickedList.type.equals(getString(R.string.unknown))) {
				unknownDialog = new UnknownEntryDialog(this,mTempClickedList,new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ListingAbstract.this);
						mDatabaseAdapter.open();
						mDatabaseAdapter.deleteEntryTableEntryID(mTempClickedList.id);
						mDatabaseAdapter.close();
						unknownDialog.dismiss();
						unknownDialogAction(mTempClickedList.id);
						Toast.makeText(ListingAbstract.this, "Deleted", Toast.LENGTH_SHORT).show();
					}
				});
			} else { 
				Intent intent = null;
				if (mTempClickedList.type.equals(getString(R.string.camera))) {
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)) {
							intent = new Intent(this,CameraActivity.class);
						} else {
							intent = new Intent(this,ShowCameraActivity.class); 
						}
					} else {
						Toast.makeText(this, "sdcard not available",Toast.LENGTH_SHORT).show();
					}
				} else if (mTempClickedList.type.equals(getString(R.string.text))) {
					if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)) {
						intent = new Intent(this, TextEntry.class); 
					} else {
						intent = new Intent(this,ShowTextActivity.class); 
					}
				} else if (mTempClickedList.type.equals(getString(R.string.voice))) {
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)) {
							intent = new Intent(this, Voice.class);
						} else {
							intent = new Intent(this,ShowVoiceActivity.class);
						}
					} else {
						Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
				}
				if(intent != null) {
					intent.putExtras(bundle);
					startActivityForResult(intent,RESULT);
				}
			}
		} else {
			onClickElse(mTempClickedList,position);
		}
	}
	
	private void setBundleListingExtra(Bundle bundle) {
		if(intentExtras.containsKey(Constants.IS_COMING_FROM_EXPENSE_LISTING)) {
			bundle.putBoolean(Constants.IS_COMING_FROM_EXPENSE_LISTING, true);
			return;
		}
		if(intentExtras.containsKey(Constants.IS_COMING_FROM_EXPENSE_SUB_LISTING)) {
			bundle.putBoolean(Constants.IS_COMING_FROM_EXPENSE_SUB_LISTING, true);
			return;
		}
	}

	protected void doOperationsOnListview() {
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mSeparatedListAdapter);
		noItemLayout();
		registerForContextMenu(mListView);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		if(new DateHelper(mDataDateList.get(Integer.parseInt(mSeparatedListAdapter.getSectionNumber(info.position))).dateTime).isCurrentWeek()) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
		
			case R.id.expense_listing_listview:
	    	    String[] menuItems = getResources().getStringArray(R.array.listcontextmenu);
	    	    for (int i = 0; i<menuItems.length; i++) {
	    	    	menu.add(Menu.NONE, i, i, menuItems[i]);
	    	    }
				break;
				
			default:
				break;
		}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
  	    switch (item.getItemId()) {
		//Edit Action	
  	    case 0:
  	    	FlurryAgent.onEvent(getString(R.string.context_item_edit));
  	    	startEditPage(info.position);
			break;
			
		//Delete Action
  	    case 1:
  	    	FlurryAgent.onEvent(getString(R.string.context_item_delete));
  	    	removeItem(info.position);
  	    	break;
  	    	
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void removeItem(int position) {
		mDatabaseAdapter.open();
    	mDatabaseAdapter.deleteEntryTableEntryID(mSeparatedListAdapter.getItem(position).id);
    	mDatabaseAdapter.close();
    	mDataDateList.remove(mSeparatedListAdapter.getSectionNumber(position));
    	mSeparatedListAdapter.remove(position);
    	noItemLayout();
	}
	
	private void startEditPage(int position) {
		Entry mTempClickedList = mSeparatedListAdapter.getItem(position);
		Intent intent = null;
		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.ENTRY_LIST_EXTRA, mTempClickedList);
		bundle.putInt(Constants.POSITION, position);
		setBundleListingExtra(bundle);
		if (mTempClickedList.type.equals(getString(R.string.text))) {
			intent = new Intent(this, TextEntry.class);
		} else {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				if (mTempClickedList.type.equals(getString(R.string.camera))) {
					intent = new Intent(this, CameraActivity.class);
				} else if (mTempClickedList.type.equals(getString(R.string.voice))) {
					intent = new Intent(this, Voice.class);
				}
			} else {
				Toast.makeText(this, "sdcard not available",Toast.LENGTH_SHORT).show();
			}
		}
		if(intent != null) {
			intent.putExtras(bundle);
			startActivityForResult(intent,RESULT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getExtras();
				if(intentExtras.containsKey("isChanged")) {
					updateListView(intentExtras,intentExtras.getInt(Constants.POSITION));
				}
			} else if(Activity.RESULT_CANCELED == resultCode) {
				removeItem(intentExtras.getInt(Constants.POSITION));
			}
		}
	}

	protected void updateListView(Bundle bundle,int toUpdate) {
		Entry updatedEntry = bundle.getParcelable(Constants.ENTRY_LIST_EXTRA);
		mSeparatedListAdapter.update(updatedEntry, toUpdate);
		noItemLayout();
	}
	
	protected abstract void unknownDialogAction(String id);
	protected void onClickElse(Entry entry,int position) {}
	protected abstract void noItemButtonAction(Button noItemButton);
	protected abstract void noItemLayout();
	protected abstract void setTab();
	
}
