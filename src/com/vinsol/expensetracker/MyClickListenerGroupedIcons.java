package com.vinsol.expensetracker;

import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.helpers.LocationHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyClickListenerGroupedIcons implements OnClickListener{
	
	private UnknownEntryDialog unknownDialog;
	private Context mContext;
	private Bundle bundle;
	private long timeInMillis;
	
	public MyClickListenerGroupedIcons(UnknownEntryDialog _unknownDialog,Context _mContext,Bundle _bundle,Long _timeInMillis) {
		unknownDialog = _unknownDialog;
		mContext = _mContext;
		if(_bundle != null) {
			if(!_bundle.isEmpty()){
				bundle = _bundle;
			} else {
				bundle = new Bundle();
			}
		} else {
			bundle = new Bundle();
		}
		if(_timeInMillis != null || timeInMillis != 0)
			timeInMillis = _timeInMillis;
		else
			timeInMillis = 0;
	}
	
	@Override
	public void onClick(View v) {
		final HashMap<String, String> toInsert = new HashMap<String, String>();
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		if(timeInMillis == 0)
			toInsert.put(DatabaseAdapter.KEY_DATE_TIME,mCalendar.getTimeInMillis()+"");
		else {
			toInsert.put(DatabaseAdapter.KEY_DATE_TIME,timeInMillis+"");
		}
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			toInsert.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		unknownDialog = new UnknownEntryDialog(mContext,toInsert,new android.view.View.OnClickListener() {
			
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
	
	private void createDatabaseEntry(int typeOfEntry, HashMap<String, String> toInsert) {	
		Long _id = insertToDatabase(typeOfEntry,toInsert);
		bundle.putLong("_id", _id);
		
		if(LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			bundle.putBoolean("setLocation", false);
		} else {
			bundle.putBoolean("setLocation", true);
		}	
	}

	// /////// ******** function to mark entry into the database and returns the
	// id of the new entry ***** //////
	private long insertToDatabase(int type, HashMap<String, String> toInsert) {
		HashMap<String, String> _list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		Log.v("bundle", bundle+" "+toInsert+" ");
		if(timeInMillis != 0)
			bundle.putLong("timeInMillis", Long.parseLong(toInsert.get(DatabaseAdapter.KEY_DATE_TIME)));
		else 
			bundle.putLong("timeInMillis", Long.parseLong(toInsert.get(DatabaseAdapter.KEY_DATE_TIME)));
		_list.put(DatabaseAdapter.KEY_DATE_TIME,toInsert.get(DatabaseAdapter.KEY_DATE_TIME));
		Activity activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		activity.finish();
		if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
			_list.put(DatabaseAdapter.KEY_LOCATION, LocationHelper.currentAddress);
		}
		_list.put(DatabaseAdapter.KEY_TYPE, mContext.getString(type));
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(mContext);
		mDatabaseAdapter.open();
		long _id = mDatabaseAdapter.insert_to_database(_list);
		mDatabaseAdapter.close();
		return _id;
	}

}
