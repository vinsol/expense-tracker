package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.utils.DisplayDate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UnknownEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private Button deleteButton;
	private Button textEntryButton;
	private Button voiceEntryButton;
	private Button cameraEntryButton;
	private Button favoriteEntryButton;
	private Button cancelButton;
	private Bundle bundle;
	private DatabaseAdapter mDatabaseAdapter;
	private ArrayList<String> mTempClickedList;
	private TextView headerTextView;
	private TextView locationTextView;
	
	public UnknownEntryDialog(Context mContext,HashMap<String, String> toInsert, android.view.View.OnClickListener myClickListener) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unknown_entry_dialog);
		textEntryButton = (Button) findViewById(R.id.main_text);
		deleteButton = (Button) findViewById(R.id.unknown_entry_dialog_delete);
		voiceEntryButton = (Button) findViewById(R.id.main_voice);
		cameraEntryButton = (Button) findViewById(R.id.main_camera);
		favoriteEntryButton = (Button) findViewById(R.id.main_favorite);
		cancelButton = (Button) findViewById(R.id.unknown_entry_dialog_cancel);
		headerTextView = (TextView) findViewById(R.id.unknown_entry_dialog_header_title);
		locationTextView = (TextView) findViewById(R.id.unknown_entry_dialog_location);
		textEntryButton.setOnClickListener(myClickListener);
		deleteButton.setVisibility(View.GONE);
		voiceEntryButton.setOnClickListener(myClickListener);
		cameraEntryButton.setOnClickListener(myClickListener);
		favoriteEntryButton.setOnClickListener(myClickListener);
		cancelButton.setOnClickListener(myClickListener);
		bundle = new Bundle();
		
		if(toInsert.containsKey(DatabaseAdapter.KEY_LOCATION))
			if(toInsert.get(DatabaseAdapter.KEY_LOCATION) != ""){
				locationTextView.setText(toInsert.get(DatabaseAdapter.KEY_LOCATION));
			}
		
		if(toInsert.containsKey(DatabaseAdapter.KEY_DATE_TIME)){
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(toInsert.get(DatabaseAdapter.KEY_DATE_TIME)));
			headerTextView.setText(new DisplayDate(mCalendar).getDisplayDate());
//			new ShowDateHandler(getContext(),headerTextView, toInsert.get(DatabaseAdapter.KEY_DATE_TIME));
		}
//		else {
//			new ShowDateHandler(getContext(),headerTextView, Integer.parseInt(mTempClickedList.get(5)));
//		}
		show();
	}
	
	public UnknownEntryDialog(Context mContext,ArrayList<String> _list,android.view.View.OnClickListener deleteClickListener) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unknown_entry_dialog);
		textEntryButton = (Button) findViewById(R.id.main_text);
		deleteButton = (Button) findViewById(R.id.unknown_entry_dialog_delete);
		voiceEntryButton = (Button) findViewById(R.id.main_voice);
		cameraEntryButton = (Button) findViewById(R.id.main_camera);
		favoriteEntryButton = (Button) findViewById(R.id.main_favorite);
		cancelButton = (Button) findViewById(R.id.unknown_entry_dialog_cancel);
		headerTextView = (TextView) findViewById(R.id.unknown_entry_dialog_header_title);
		locationTextView = (TextView) findViewById(R.id.unknown_entry_dialog_location);
		textEntryButton.setOnClickListener(this);
		deleteButton.setOnClickListener(deleteClickListener);
		voiceEntryButton.setOnClickListener(this);
		cameraEntryButton.setOnClickListener(this);
		favoriteEntryButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		bundle = new Bundle();
		mDatabaseAdapter = new DatabaseAdapter(getContext());
		mTempClickedList = _list;
		
		if(mTempClickedList.get(7) != null)
			if(mTempClickedList.get(7) != ""){
				locationTextView.setText(mTempClickedList.get(7));
			}
		
		if(mTempClickedList.get(6) != null){
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mTempClickedList.get(6)));
			headerTextView.setText(new DisplayDate(mCalendar).getDisplayDate());
//			new ShowDateHandler(getContext(),headerTextView, mTempClickedList.get(6));
		}
//		else {
//			new ShowDateHandler(getContext(),headerTextView, Integer.parseInt(mTempClickedList.get(5)));
//		}
		show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.main_text:
				Intent intentTextEntry = new Intent(getContext(), TextEntry.class);
				editDatabase(R.string.text);
				bundle.putStringArrayList("mDisplayList", mTempClickedList);
				intentTextEntry.putExtra("textEntryBundle", bundle);
				getContext().startActivity(intentTextEntry);
				dismiss();
				break;
				
			case R.id.unknown_entry_dialog_delete:
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(mTempClickedList.get(0));
				mDatabaseAdapter.close();
				
				dismiss();
				break;
				
			case R.id.main_voice:
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentVoice = new Intent(getContext(), Voice.class);
					editDatabase(R.string.voice);
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					intentVoice.putExtra("voiceBundle", bundle);
					getContext().startActivity(intentVoice);
					dismiss();
				} else {
					Toast.makeText(getContext(), "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.main_camera:
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					Intent intentCamera = new Intent(getContext(), CameraActivity.class);
					editDatabase(R.string.camera);
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					intentCamera.putExtra("cameraBundle", bundle);
					getContext().startActivity(intentCamera);
					dismiss();
				} else {
					Toast.makeText(getContext(), "sdcard not available", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.main_favorite:
				if(new ConvertCursorToListString(getContext()).getFavoriteList().size() >=1){
					Intent intentFavorite = new Intent(getContext(), FavoriteActivity.class);
					bundle.putStringArrayList("mDisplayList", mTempClickedList);
					intentFavorite.putExtra("favoriteBundle", bundle);
					getContext().startActivity(intentFavorite);
					dismiss();
				} else {
					Toast.makeText(getContext(), "no favorite added", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.unknown_entry_dialog_cancel:
				cancel();
				break;
				
			default:
				break;
		}
	}
	
	private void editDatabase(int type) {
		HashMap<String, String> list = new HashMap<String, String>();
		list.put(DatabaseAdapter.KEY_ID,mTempClickedList.get(0));
		list.put(DatabaseAdapter.KEY_TYPE, getContext().getString(type));
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(list);
		mDatabaseAdapter.close();
	}
}
