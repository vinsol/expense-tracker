package com.vinsol.expensetracker;

import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.edit.TextEntry;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.listing.FavoriteActivity;
import com.vinsol.expensetracker.listing.UnknownEntryDialog;
import com.vinsol.expensetracker.models.DisplayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GroupedIconDialogClickListener implements OnClickListener{
	
	private UnknownEntryDialog unknownDialog;
	private Context mContext;
	private Bundle bundle;
	private long timeInMillis;
	
	public GroupedIconDialogClickListener(UnknownEntryDialog unknownDialog,Context mContext,Bundle bundle,Long timeInMillis) {
		this.unknownDialog = unknownDialog;
		this.mContext = mContext;
		if(bundle != null) {
			if(!bundle.isEmpty()){
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
		final DisplayList toInsert = new DisplayList();
		
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
		unknownDialog = new UnknownEntryDialog(toInsert,mContext,new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				unknownDialog.dismiss();
				switch (v.getId()) {
				// //// ******* opens TextEntry Activity ******** ///////////
				case R.id.main_text:
					Intent intentTextEntry = new Intent(mContext, TextEntry.class);
					createDatabaseEntry(R.string.text,toInsert);
					intentTextEntry.putExtra("textEntryBundle", bundle);
					mContext.startActivity(intentTextEntry);
					break;
					
				// //// ******* opens Voice Activity ******** ///////////
				case R.id.main_voice:
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentVoice = new Intent(mContext, Voice.class);
						createDatabaseEntry(R.string.voice,toInsert);
						intentVoice.putExtra("voiceBundle", bundle);
						mContext.startActivity(intentVoice);
					} else {
						Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;

				// //// ******* opens Camera Activity ******** ///////////
				case R.id.main_camera:
					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						Intent intentCamera = new Intent(mContext, CameraActivity.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentCamera.putExtra("cameraBundle", bundle);
						mContext.startActivity(intentCamera);
					} else {
						Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_SHORT).show();
					}
					break;
					
				// //// ******* opens Favorite Activity ******** ///////////
				case R.id.main_favorite:
					
					if(new ConvertCursorToListString(mContext).getFavoriteList().size() >=1){
						Intent intentFavorite = new Intent(mContext, FavoriteActivity.class);
						if (timeInMillis != 0) {
							bundle.putLong("timeInMillis", timeInMillis);
						}
						intentFavorite.putExtra("favoriteBundle", bundle);
						mContext.startActivity(intentFavorite);	
					}
					else {
						Toast.makeText(mContext, "no favorite added", Toast.LENGTH_SHORT).show();
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
	
	private void createDatabaseEntry(int typeOfEntry, DisplayList toInsert) {	
		Long id = insertToDatabase(typeOfEntry,toInsert);
		bundle.putLong("_id", id);
		
		if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			bundle.putBoolean("setLocation", false);
		} else {
			bundle.putBoolean("setLocation", true);
		}	
	}

	// /////// ******** function to mark entry into the database and returns the
	// id of the new entry ***** //////
	private long insertToDatabase(int type, DisplayList toInsert) {
		HashMap<String, String> list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(timeInMillis != 0)
			bundle.putLong("timeInMillis", toInsert.timeInMillis);
		else 
			bundle.putLong("timeInMillis", toInsert.timeInMillis);
		list.put(DatabaseAdapter.KEY_DATE_TIME,toInsert.timeInMillis.toString());
		Activity activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		activity.finish();
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		list.put(DatabaseAdapter.KEY_TYPE, mContext.getString(type));
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(mContext);
		mDatabaseAdapter.open();
		long id = mDatabaseAdapter.insertToDatabase(list);
		mDatabaseAdapter.close();
		return id;
	}

}
