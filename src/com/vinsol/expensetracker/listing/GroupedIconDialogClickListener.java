/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.listing;

import java.util.Calendar;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.listing.FavoriteActivity;
import com.vinsol.expensetracker.listing.UnknownEntryDialog;
import com.vinsol.expensetracker.models.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GroupedIconDialogClickListener implements OnClickListener {
	
	private UnknownEntryDialog unknownDialog;
	private Activity activity;
	private Bundle bundle;
	private long timeInMillis;
	
	public GroupedIconDialogClickListener(UnknownEntryDialog unknownDialog,Activity activity,Bundle bundle,Long timeInMillis) {
		FlurryAgent.onEvent(Constants.GROUPED_ICON_CLICK);
		this.unknownDialog = unknownDialog;
		this.activity = activity;
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
				unknownDialog.dismiss();
				switch (v.getId()) {
				// //// ******* opens TextEntry Activity ******** ///////////
				case R.id.main_text:
					Intent intentTextEntry = new Intent(activity, TextEntry.class);
					createDatabaseEntry(R.string.text,toInsert);
					intentTextEntry.putExtra("textEntryBundle", bundle);
					activity.startActivity(intentTextEntry);
					break;
					
				// //// ******* opens Voice Activity ******** ///////////
				case R.id.main_voice:
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentVoice = new Intent(activity, Voice.class);
						createDatabaseEntry(R.string.voice,toInsert);
						intentVoice.putExtra("voiceBundle", bundle);
						activity.startActivity(intentVoice);
					} else {
						Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;

				// //// ******* opens Camera Activity ******** ///////////
				case R.id.main_camera:
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentCamera = new Intent(activity, CameraActivity.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentCamera.putExtra("cameraBundle", bundle);
						activity.startActivity(intentCamera);
					} else {
						Toast.makeText(activity, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;
					
				// //// ******* opens Favorite Activity ******** ///////////
				case R.id.main_favorite:
					
					if(new ConvertCursorToListString(activity).getFavoriteList().size() >=1) {
						Intent intentFavorite = new Intent(activity, FavoriteActivity.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentFavorite.putExtra("favoriteBundle", bundle);
						activity.startActivity(intentFavorite);	
					}
					else {
						Toast.makeText(activity, "no favorite added", Toast.LENGTH_SHORT).show();
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
