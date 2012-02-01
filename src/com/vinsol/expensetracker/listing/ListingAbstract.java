/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.ArrayAdapter;
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
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.show.ShowTextActivity;
import com.vinsol.expensetracker.show.ShowVoiceActivity;
import com.vinsol.expensetracker.utils.GetArrayListFromString;
import com.vinsol.expensetracker.utils.Log;

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
	protected int type;

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
		setContentView();
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
		type = getType(intentExtras);
	}

	protected int getType(Bundle intentExtras) {
		return 0;
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
	
	@SuppressWarnings("unchecked")
	protected void addSections() {
		int j = 0;
		List<ListDatetimeAmount> dateListToSend = new ArrayList<ListDatetimeAmount>();
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<Entry>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<Entry> mList = new ArrayList<Entry>();
			String date = mDataDateList.get(i).dateTime;
			Calendar toCHeckCal = Calendar.getInstance();
			toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
			toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
			Log.d(getString(type));
			Log.d(date +" \t "+new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate(type));
			while (j < mSubList.size() && date.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate(type))) {
				Entry templist = new Entry();
				Calendar mCalendar = Calendar.getInstance();
				mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
				mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
				mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
				DisplayDate mDisplayDate = new DisplayDate(mCalendar);
				if(!condition(mDisplayDate)) {
					j = mSubList.size()+1;
					break;
				}
				if (type == R.string.sublist_thisweek) {
					templist = getListCurrentWeek(j);
					mList.add(templist);
					j++;
					if (j < mSubList.size()) {
						toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
						toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					} else {
						break;
					}
				} else if (type == R.string.sublist_thismonth || type == R.string.sublist_thisyear || type == R.string.sublist_all) {
					toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
					toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
					while (mDataDateList.get(i).dateTime.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate(type))) {
						////// Adding i+" "+j as id
						Entry mTempSubList = new Entry();
						mTempSubList.id = mSubList.get(j).id +",";
						
						///// Adding tag
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
						tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
						tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
						mDisplayDate = new DisplayDate(tempCalendar);
						DisplayDate tempDisplayDate = new DisplayDate(tempCalendar);
						int isWeekOfMonth = tempCalendar.get(Calendar.WEEK_OF_MONTH);
						int isCurrentMonth = tempCalendar.get(Calendar.MONTH);
						int isCurrentYear = tempCalendar.get(Calendar.YEAR);
						
						mTempSubList.description = tempDisplayDate.getSubListTag(type);
						
						///// Adding Amount
						double temptotalAmount = 0;
						String totalAmountString = null;
						boolean isTempAmountNull = false;
						do {
							String tempAmount = mSubList.get(j).amount;
							if (tempAmount != null && !tempAmount.equals("")) {
								try {
									temptotalAmount += Double.parseDouble(tempAmount);
								} catch (NumberFormatException e) {
								}
							} else {
								isTempAmountNull = true;
							}
							j++;
							if (j < mSubList.size()) {
								toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
								toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
								tempCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
								tempCalendar.set(tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
								tempCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								tempDisplayDate = new DisplayDate(tempCalendar);
								if(getLoopCondition(tempCalendar,isWeekOfMonth,isCurrentMonth,isCurrentYear))
									mTempSubList.id = mTempSubList.id+mSubList.get(j).id+",";
							} else {
								break;
							}
						} while (getLoopCondition(tempCalendar,isWeekOfMonth,isCurrentMonth,isCurrentYear));
						
						if (isTempAmountNull) {
							if (temptotalAmount != 0) {
								totalAmountString = temptotalAmount + " ?";
							} else {
								totalAmountString = "?";
							}
						} else {
							totalAmountString = temptotalAmount + "";
						}
						mTempSubList.amount = mStringProcessing.getStringDoubleDecimal(totalAmountString);
						mTempSubList.type = getString(type);
						mTempSubList.timeInMillis = 0L;
						if(highlightID != null) {
							if (j <= mSubList.size()) {
								if(mTempSubList.id.contains(highlightID)) {
									startSubListing(mTempSubList);
								}
							}
						}
						mList.add(mTempSubList);
						if (j == mSubList.size()) {
							break;
						}
					}
				}
			}
			if(j > mSubList.size()) {
				break;
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			dateListToSend.add(mDataDateList.get(i));
			mSeparatedListAdapter.addSection(i + "", new ArrayAdapter<Entry>(this, R.layout.expense_listing_tab, tt), dateListToSend);
		}
		doOperationsOnListview();
	}

	protected void startSubListing(Entry entry) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(entry.id);
		for(int checkI=0; checkI<mArrayList.size(); checkI++) {
			if(mArrayList.get(checkI).equals(highlightID)) {
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				Bundle extras = new Bundle();
				extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
				extras.putInt(Constants.TYPE, getSubListType());
				expenseSubListing.putExtras(extras);
//				expenseSubListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(expenseSubListing, RESULT);
 				finish();
			}
		}	
	}
	
	protected int getSubListType() {
		switch (type) {
		case R.string.sublist_all:
			return R.string.sublist_thisyear;
		case R.string.sublist_thismonth:
			return R.string.sublist_thisweek;
		case R.string.sublist_thisyear:
			return R.string.sublist_thismonth;
		default:
			return R.string.sublist_thisweek;
		}
	}

	protected boolean getLoopCondition(Calendar tempCalendar, int isWeekOfMonth, int isCurrentMonth, int isCurrentYear) {
		switch (type) {
		case R.string.sublist_all:
			return tempCalendar.get(Calendar.YEAR) == isCurrentYear;
			
		case R.string.sublist_thismonth:
		case R.string.sublist_thisweek:
			return tempCalendar.get(Calendar.WEEK_OF_MONTH) == isWeekOfMonth
				&& tempCalendar.get(Calendar.MONTH) == isCurrentMonth
				&& tempCalendar.get(Calendar.YEAR) == isCurrentYear;
		case R.string.sublist_thisyear:
			return tempCalendar.get(Calendar.MONTH) == isCurrentMonth
				&& tempCalendar.get(Calendar.YEAR) == isCurrentYear;
		default:
			return false;
		}
	}
	
	protected void onClickElse(Entry entry,int position) {
		Intent mSubListIntent = new Intent(this, ExpenseSubListing.class);
		Bundle extras = new Bundle();
		extras.putParcelable(Constants.ENTRY_LIST_EXTRA, entry);
		extras.putInt(Constants.POSITION, position);
		extras.putInt(Constants.TYPE, getSubListType());
		mSubListIntent.putExtras(extras);
//		mSubListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(mSubListIntent,RESULT);
	}
	
	protected abstract void unknownDialogAction(String id);
	protected abstract void noItemButtonAction(Button noItemButton);
	protected abstract void noItemLayout();
	protected abstract void setContentView();
	protected abstract boolean condition(DisplayDate mDisplayDate);
	
	
}
