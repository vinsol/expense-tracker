/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.expenselisting.dialog;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.entry.CameraEntry;
import com.vinsol.expensetracker.entry.FavoriteEntry;
import com.vinsol.expensetracker.entry.Text;
import com.vinsol.expensetracker.entry.Voice;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.models.Entry;

public class GroupedIconDialogClickListener implements OnClickListener {
	
	private UnknownEntryDialog unknownDialog;
	private Activity activity;
	private Bundle bundle;
	private long timeInMillis;
	
	public GroupedIconDialogClickListener(UnknownEntryDialog unknownDialog,Activity activity,Bundle bundle,Long timeInMillis) {
		this.unknownDialog = unknownDialog;
		this.activity = activity;
		FlurryAgent.onEvent(activity.getString(R.string.dialog_opened_to)+" "+activity.getString(R.string.add_expenses_from_listing));
		if(bundle != null) {
			if(!bundle.isEmpty()) {
				this.bundle = bundle;
			} else {
				this.bundle = new Bundle();
			}
		} else {
			this.bundle = new Bundle();
		}
		if(timeInMillis != null)
			if(timeInMillis != 0)
				this.timeInMillis = timeInMillis;
		else
			this.timeInMillis = 0;
	}
	
	@Override
	public void onClick(View v) {
		final Entry toInsert = new Entry();
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(timeInMillis == 0)
			toInsert.timeInMillis = mCalendar.getTimeInMillis();
		else {
			toInsert.timeInMillis = timeInMillis;
		}
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			toInsert.location = LocationHelper.currentAddress;
		}
		unknownDialog = new UnknownEntryDialog(toInsert,activity,new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				// //// ******* opens TextEntry Activity ******** ///////////
				case R.id.home_text:
					unknownDialog.dismiss();
					Intent intentTextEntry = new Intent(activity, Text.class);
					createDatabaseEntry(R.string.text,toInsert);
					intentTextEntry.putExtras(bundle);
					activity.startActivity(intentTextEntry);
					break;
					
				// //// ******* opens Voice Activity ******** ///////////
				case R.id.home_voice:
					unknownDialog.dismiss();
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentVoice = new Intent(activity, Voice.class);
						createDatabaseEntry(R.string.voice,toInsert);
						intentVoice.putExtras(bundle);
						activity.startActivity(intentVoice);
					} else {
						Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;

				// //// ******* opens Camera Activity ******** ///////////
				case R.id.home_camera:
					unknownDialog.dismiss();
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentCamera = new Intent(activity, CameraEntry.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentCamera.putExtras(bundle);
						activity.startActivity(intentCamera);
					} else {
						Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;
					
				// //// ******* opens Favorite Activity ******** ///////////
				case R.id.home_favorite:
					if(new ConvertCursorToListString(activity).getFavoriteList().size() > 0) {
						unknownDialog.dismiss();
						Intent intentFavorite = new Intent(activity, FavoriteEntry.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentFavorite.putExtras(bundle);
						activity.startActivity(intentFavorite);	
					} else {
						Toast.makeText(activity, "favorite list empty", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.unknown_entry_dialog_cancel:
					unknownDialog.dismiss();
					break;
					
				default:
					break;
				}
			}
		});
	}
	
	private void createDatabaseEntry(int typeOfEntry, Entry toInsert) {	
		Long id = insertToDatabase(typeOfEntry,toInsert);
		bundle.putLong("_id", id);
		
		if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			bundle.putBoolean("setLocation", false);
		} else {
			bundle.putBoolean("setLocation", true);
		}	
	}

	///////// ******** function to mark entry into the database and returns the id of the new entry ***** //////
	private long insertToDatabase(int type, Entry toInsert) {
		if(timeInMillis != 0)
			bundle.putLong("timeInMillis", toInsert.timeInMillis);
		else 
			bundle.putLong("timeInMillis", toInsert.timeInMillis);
		if(activity != null)
			activity.finish();
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			toInsert.location = LocationHelper.currentAddress;
		}
		toInsert.type = activity.getString(type);
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(activity);
		mDatabaseAdapter.open();
		long id = mDatabaseAdapter.insertToEntryTable(toInsert);
		mDatabaseAdapter.close();
		return id;
	}

}
