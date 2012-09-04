/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.expenselisting;

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
import com.vinsol.expensetracker.BaseActivity;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.entry.CameraEntry;
import com.vinsol.expensetracker.entry.Text;
import com.vinsol.expensetracker.entry.Voice;
import com.vinsol.expensetracker.expenselisting.dialog.UnknownEntryDialog;
import com.vinsol.expensetracker.helpers.CheckEntryComplete;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.DisplayDate;
import com.vinsol.expensetracker.helpers.StringProcessing;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.ListDatetimeAmount;
import com.vinsol.expensetracker.show.ShowCamera;
import com.vinsol.expensetracker.show.ShowText;
import com.vinsol.expensetracker.show.ShowVoice;
import com.vinsol.expensetracker.sync.SyncHelper;
import com.vinsol.expensetracker.utils.GetArrayListFromString;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Strings;

abstract class ListingAbstract extends BaseActivity implements OnItemClickListener {

	protected List<ListDatetimeAmount> mDataDateList;
	protected SeparatedListAdapter mSeparatedListAdapter;
	protected List<Entry> mSubList;
	protected ConvertCursorToListString mConvertCursorToListString;
	protected StringProcessing mStringProcessing;
	protected ListView mListView;
	protected String highlightID = null;
	protected UnknownEntryDialog unknownDialog;
	private static final int RESULT = 35;
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
	public boolean removeGenerateReport(Menu menu) {
		return false;
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
		if(intentExtras != null && intentExtras.containsKey(Constants.KEY_HIGHLIGHT)) {
			highlightID = intentExtras.getString(Constants.KEY_HIGHLIGHT);
		}
		type = getType(intentExtras);
	}
	
	protected int getType(Bundle intentExtras) {
		return 0;
	}

	protected Entry getListCurrentWeek(int j) {
		Entry templist = new Entry();
		templist.id = mSubList.get(j).id;
		templist.deleted = mSubList.get(j).deleted;
		templist.myHash = mSubList.get(j).myHash;
		templist.fileToDownload = mSubList.get(j).fileToDownload;
		templist.fileUploaded = mSubList.get(j).fileUploaded;
		templist.timeInMillis = mSubList.get(j).timeInMillis;
		
		
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

		if (mSubList.get(j).favorite != null && !mSubList.get(j).favorite.equals("")) {
			templist.favorite = mSubList.get(j).favorite;
		} else {
			templist.favorite = "";
		}

		if (mSubList.get(j).type != null && !mSubList.get(j).type.equals("")) {
			templist.type = mSubList.get(j).type;
		} else {
			templist.type = "";
		}
		
		if(Strings.isEmpty(mSubList.get(j).idFromServer)) {
			templist.idFromServer = ""; 
		} else {
			templist.idFromServer = mSubList.get(j).idFromServer;
		}
		
		if(Strings.isEmpty(mSubList.get(j).syncBit)) {
			templist.syncBit = ""; 
		} else {
			templist.syncBit = mSubList.get(j).syncBit;
		}
		
		if(Strings.isEmpty(mSubList.get(j).updatedAt)) {
			templist.updatedAt = ""; 
		} else {
			templist.updatedAt = mSubList.get(j).updatedAt;
		}
		
		if(Strings.isEmpty(mSubList.get(j).fileUpdatedAt)) {
			templist.fileUpdatedAt = ""; 
		} else {
			templist.fileUpdatedAt = mSubList.get(j).fileUpdatedAt;
		}
		
		if(Strings.isEmpty(mSubList.get(j).location)) {
			templist.location = ""; 
		} else {
			templist.location = mSubList.get(j).location;
		}
		
		return templist;
	}
	
	@Override
	public void onItemClick(final AdapterView<?> adapter, View v,final int position, long arg3) {
		final Entry mTempClickedList = (Entry) adapter.getItemAtPosition(position);
		String id = mTempClickedList.id;
		if (!id.contains(",")) {
			Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, mTempClickedList);
			bundle.putInt(Constants.KEY_POSITION, position);
			CheckEntryComplete mCheckEntryComplete = new CheckEntryComplete();
			if (mTempClickedList.type.equals(getString(R.string.unknown))) {
				unknownDialog = new UnknownEntryDialog(this,mTempClickedList,new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(ListingAbstract.this);
						mDatabaseAdapter.open();
						mDatabaseAdapter.deleteExpenseEntryByID(mTempClickedList.id);
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
							intent = new Intent(this,CameraEntry.class);
						} else {
							intent = new Intent(this,ShowCamera.class); 
						}
					} else {
						Toast.makeText(this, "sdcard not available",Toast.LENGTH_SHORT).show();
					}
				} else if (mTempClickedList.type.equals(getString(R.string.text))) {
					if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)) {
						intent = new Intent(this, Text.class); 
					} else {
						intent = new Intent(this,ShowText.class); 
					}
				} else if (mTempClickedList.type.equals(getString(R.string.voice))) {
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						if(!mCheckEntryComplete.isEntryComplete(mTempClickedList,this)) {
							intent = new Intent(this, Voice.class);
						} else {
							intent = new Intent(this,ShowVoice.class);
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
  	    	FlurryAgent.onEvent(getString(R.string.editing_using_context_menu));
  	    	startEditPage(info.position);
			break;
			
		//Delete Action
  	    case 1:
  	    	FlurryAgent.onEvent(getString(R.string.deleting_using_context_menu));
  	    	Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
  	    	removeItem(info.position);
  	    	ExpenseListing.resetUnfinishedEntryCount();
  	    	SyncHelper.startSync();
  	    	break;
  	    	
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void removeItem(int position) {
		Entry tempEntry = ((Entry)mSeparatedListAdapter.getItem(position));
		mDatabaseAdapter.open();
//		if(Strings.isEmpty(tempEntry.updatedAt)) {
//			mDatabaseAdapter.permanentDeleteEntryTableEntryID(tempEntry.id);
//		} else {
			mDatabaseAdapter.deleteExpenseEntryByID(tempEntry.id);
//		}
    	mDatabaseAdapter.close();
    	//XXX
    	if(!mSeparatedListAdapter.remove(position)) {
    		initListView();
    		return;
    	}
    	setModifiedValues();
    	noItemLayout();
	}
	
	private void startEditPage(int position) {
		Entry mTempClickedList = (Entry) mSeparatedListAdapter.getItem(position);
		Intent intent = null;
		Bundle bundle = new Bundle();
		bundle.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, mTempClickedList);
		bundle.putInt(Constants.KEY_POSITION, position);
		if (mTempClickedList.type.equals(getString(R.string.text))) {
			intent = new Intent(this, Text.class);
		} else {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				if (mTempClickedList.type.equals(getString(R.string.camera))) {
					intent = new Intent(this, CameraEntry.class);
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
		if (RESULT == requestCode) {
			if(data != null && data.getExtras() != null){
				intentExtras = data.getExtras();
			} else {
				intentExtras = null;
			}
			if(Activity.RESULT_OK == resultCode) {
				if(intentExtras != null && intentExtras.containsKey(Constants.KEY_DATA_CHANGED)) {
					updateListView(intentExtras);
					intentExtras.remove(Constants.KEY_DATA_CHANGED);
				} else {
					initListView();
				}
			} else if(Activity.RESULT_CANCELED == resultCode) {
				int position = -1;
				if(intentExtras != null && intentExtras.containsKey(Constants.KEY_POSITION)) {
					position = intentExtras.getInt(Constants.KEY_POSITION);
				}
				if(position != -1) {
					removeItem(position);
				} else {
					initListView();
				}
			}
		}
	}

	protected void updateListView(Bundle bundle) {
		Entry updatedEntry = bundle.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
		int position = -1;
		if(bundle.containsKey(Constants.KEY_POSITION)) {position = bundle.getInt(Constants.KEY_POSITION);}
		Log.d("************************");
		Log.d("updating "+position);
		Log.d("************************");
		if(position != -1) {
			String sectionNumber = mSeparatedListAdapter.getSectionNumber(position);
			Object prevEntry = mSeparatedListAdapter.getItem(position);
			if(sectionNumber != null && !sectionNumber.equals("") && prevEntry != null && !prevEntry.equals("")) {
				try{
					mSeparatedListAdapter.update(updatedEntry, position, sectionNumber, (Entry) prevEntry);
				} catch (Exception e) {
					//XXX dont want to do this but sometimes it throwing error
					//
					initListView();
				}
			} else {
				initListView();
			}
			noItemLayout();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void addSections() {
		int j = 0;
		List<ListDatetimeAmount> dateListToSend = new ArrayList<ListDatetimeAmount>();
		int sectionNum = 0;
		@SuppressWarnings("rawtypes")
		List listString = new ArrayList<List<Entry>>();
		for (int i = 0; i < mDataDateList.size(); i++) {
			List<Entry> mList = new ArrayList<Entry>();
			
			String date = mDataDateList.get(i).dateTime;
			if(j < mSubList.size()) {
				Calendar toCHeckCal = Calendar.getInstance();
				toCHeckCal.setTimeInMillis(mSubList.get(j).timeInMillis);
				toCHeckCal.setFirstDayOfWeek(Calendar.MONDAY);
				while (j < mSubList.size() && date.equals(new DisplayDate(toCHeckCal).getHeaderFooterListDisplayDate(type))) {
					Entry templist = new Entry();
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setTimeInMillis(mSubList.get(j).timeInMillis);
					mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),0,0,0);
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					DisplayDate mDisplayDate = new DisplayDate(mCalendar);
					if(!condition(mDisplayDate)) {
						j = mSubList.size() + 1;
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
										e.printStackTrace();
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
									if(getLoopCondition(tempCalendar,isWeekOfMonth,isCurrentMonth,isCurrentYear)) {
										
										mTempSubList.id = mTempSubList.id + mSubList.get(j).id+",";
									}
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
							mTempSubList.deleted = false;
							mTempSubList.fileToDownload = false;
							mTempSubList.fileUpdatedAt = "";
							mTempSubList.fileUploaded = false;
							mTempSubList.idFromServer = "";
							mTempSubList.myHash = "";
							mTempSubList.syncBit = "";
							mTempSubList.updatedAt = "";
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
				
			} else {
				break;
			}
			listString.add(mList);
			@SuppressWarnings("rawtypes")
			List tt = (List) listString.get(i);
			if(tt.size() > 0) {
				int temp = i;
				if(i == 0 && !new DisplayDate(mSubList.get(0).timeInMillis).isCurrentWeek()) {temp++;}
				dateListToSend.add(mDataDateList.get(temp));
				Log.d("************ Amount ***************** "+temp);
				Log.d("mDataDateList "+mDataDateList.get(temp).amount);
				mSeparatedListAdapter.addSection(sectionNum + "", new ArrayAdapter<Entry>(this, R.layout.expense_listing_tab, tt), dateListToSend);
				sectionNum++;
			}
		}
		doOperationsOnListview();
	}

	protected void startSubListing(Entry entry) {
		ArrayList<String> mArrayList = new GetArrayListFromString().getListFromTextArea(entry.id);
		for(int checkI=0; checkI<mArrayList.size(); checkI++) {
			if(mArrayList.get(checkI).equals(highlightID)) {
				Intent expenseSubListing = new Intent(this, ExpenseSubListing.class);
				Bundle extras = new Bundle();
				extras.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, entry);
				extras.putInt(Constants.KEY_TYPE, getSubListType());
				if(highlightID != null)
					extras.putString(Constants.KEY_HIGHLIGHT, highlightID);
				expenseSubListing.putExtras(extras);
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
		extras.putParcelable(Constants.KEY_ENTRY_LIST_EXTRA, entry);
		extras.putInt(Constants.KEY_POSITION, position);
		extras.putInt(Constants.KEY_TYPE, getSubListType());
		mSubListIntent.putExtras(extras);
		startActivityForResult(mSubListIntent,RESULT);
	}
	
	protected abstract void unknownDialogAction(String id);
	protected abstract void noItemButtonAction(Button noItemButton);
	protected abstract void noItemLayout();
	protected abstract void setContentView();
	protected abstract boolean condition(DisplayDate mDisplayDate);
	protected abstract void initListView();
	protected abstract void setModifiedValues();
	
}
