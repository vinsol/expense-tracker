package com.vinsol.expensetracker.listing;

import java.util.List;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.show.ShowVoiceActivity;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.ListDatetimeAmount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

abstract class ListingAbstract extends Activity implements OnItemClickListener{

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_listing);
		intentExtras = new Bundle();
		mConvertCursorToListString = new ConvertCursorToListString(this);
		mStringProcessing = new StringProcessing();
		mSeparatedListAdapter = new SeparatedListAdapter(this);
	}
	
	protected Entry getListCurrentWeek(int j) {
		Entry templist = new Entry();
		templist.id = mSubList.get(j).id;
		if (mSubList.get(j).description != null && !mSubList.get(j).description.equals("")) {
			templist.description = mSubList.get(j).description;
		} else {
			CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
			if (mSubList.get(j).type.equals(getString(R.string.camera))) {
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)){
					templist.description = getString(R.string.finished_cameraentry);
				} else {
					templist.description = getString(R.string.unfinished_cameraentry);
				}
			} else if (mSubList.get(j).type.equals(getString(R.string.voice))) {
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)){
					templist.description = getString(R.string.finished_voiceentry);
				} else {
					templist.description = getString(R.string.unfinished_voiceentry);
				}
			} else if (mSubList.get(j).type.equals(getString(R.string.text))) {
				if(mCheckEntryComplete.isEntryComplete(mSubList.get(j),this)){
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
			bundle.putParcelable("mDisplayList", mTempClickedList);
			bundle.putInt("position", position);
			CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
			if (mTempClickedList.type.equals(getString(R.string.camera))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)){
						Intent intentCamera = new Intent(this,CameraActivity.class);
						intentCamera.putExtra("cameraBundle", bundle);
						startActivityForResult(intentCamera,RESULT);
					} else {
						Intent intentCamera = new Intent(this,ShowCameraActivity.class);
						intentCamera.putExtra("cameraShowBundle", bundle);
						startActivityForResult(intentCamera,RESULT); 
					}
				} else {
					Toast.makeText(this, "sdcard not available",Toast.LENGTH_SHORT).show();
				}
			} else if (mTempClickedList.type.equals(getString(R.string.text))) {
				if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)){
					Intent intentTextEntry = new Intent(this, TextEntry.class);
					intentTextEntry.putExtra("textEntryBundle", bundle);
					startActivityForResult(intentTextEntry,RESULT); 
				} else {
					Intent intentTextShow = new Intent(this,ShowTextActivity.class);
					intentTextShow.putExtra("textShowBundle", bundle);
					startActivityForResult(intentTextShow,RESULT); 
				}
			} else if (mTempClickedList.type.equals(getString(R.string.voice))) {
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)){
						Intent intentVoice = new Intent(this, Voice.class);
						intentVoice.putExtra("voiceBundle", bundle);
						startActivityForResult(intentVoice,RESULT);
					} else {
						Intent intentVoiceShow = new Intent(this,ShowVoiceActivity.class);
						intentVoiceShow.putExtra("voiceShowBundle", bundle);
						startActivityForResult(intentVoiceShow,RESULT);
					}
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
				}
			} else if (mTempClickedList.type.equals(getString(R.string.unknown))) {
				unknownDialog = new UnknownEntryDialog(this,mTempClickedList,new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ListingAbstract.this);
						mDatabaseAdapter.open();
						mDatabaseAdapter.deleteDatabaseEntryID(mTempClickedList.id);
						mDatabaseAdapter.close();
						unknownDialog.dismiss();
						unknownDialogAction(mTempClickedList.id);
						Toast.makeText(ListingAbstract.this, "Deleted", Toast.LENGTH_SHORT).show();
					}
				});
			}
		} else {
			onClickElse(mTempClickedList.id);
		}
	}
	
	protected void unknownDialogAction(String id){}
	
	protected void onClickElse(String id) {}
	
	protected void noItemButtonAction(Button noItemButton){}
	
	protected void getListToAdd(){}
	
	protected void doOperationsOnListview(){
		mListView = (ListView) findViewById(R.id.expense_listing_listview);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mSeparatedListAdapter);
		if (mDataDateList.size() < 1) {
			mListView.setVisibility(View.GONE);
			RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.expense_listing_listview_no_item);
			mRelativeLayout.setVisibility(View.VISIBLE);
			Button noItemButton = (Button) findViewById(R.id.expense_listing_listview_no_item_button);
			noItemButtonAction(noItemButton);
		}
		if(intentExtras.containsKey("position")) {
			mListView.setSelection(intentExtras.getInt("position"));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getExtras();
				if(intentExtras.containsKey("isChanged")) {
					updateListView();
				}
			}
			if(Activity.RESULT_CANCELED == resultCode) {
				updateListView();
			}
		}
	}

	protected void updateListView() {}
	
}
