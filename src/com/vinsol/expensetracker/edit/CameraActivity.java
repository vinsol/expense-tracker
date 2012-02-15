/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.edit;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.cameraservice.Camera;
import com.vinsol.expensetracker.helpers.CameraFileSave;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.utils.ImagePreview;

public class CameraActivity extends EditAbstract {

	private static final int PICTURE_RESULT = 35;
	private LinearLayout editCameraDetails;
	private ImageView editImageDisplay;
	private RelativeLayout editLoadProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ////// ******** Initializing and assigning memory to UI Items ********** /////////
		editCameraDetails = (LinearLayout) findViewById(R.id.edit_camera_details);
		editImageDisplay = (ImageView) findViewById(R.id.edit_image_display);
		editLoadProgress = (RelativeLayout) findViewById(R.id.edit_load_progress);
		typeOfEntry = R.string.camera;
		typeOfEntryFinished = R.string.finished_cameraentry;
		typeOfEntryUnfinished = R.string.unfinished_cameraentry;
		editHelper();
		if (intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
			if(setUnknown) {
				startCamera();
			}
			File mFile = fileHelper.getCameraFileSmallEntry(entry.id);
			if (mFile.canRead() && mFile.exists()) {
				Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
				setImageResource(mDrawable);
			} else {
				editImageDisplay.setImageResource(R.drawable.no_image_small);
			}
		}
		
		setGraphicsCamera();
		setClickListeners();

		// ////// *********** Initializing Database Adaptor **********//////////
		mDatabaseAdapter = new DatabaseAdapter(this);
		dateViewString = dateBarDateview.getText().toString();
		if(entry.id == null ) {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				
				Entry toInsert = new Entry();
				if (!dateBarDateview.getText().toString().equals(dateViewString)) {
					try {
						if (!intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA)) {
							DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
							toInsert.timeInMillis = mDateHelper.getTimeMillis();
						} else {
							if(!intentExtras.containsKey(Constants.TIME_IN_MILLIS)) {
								DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong(Constants.TIME_IN_MILLIS));
								mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
								DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString(),mCalendar);
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							}
						}
					} catch (Exception e) {
					}
				} else {
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setFirstDayOfWeek(Calendar.MONDAY);
					toInsert.timeInMillis = mCalendar.getTimeInMillis();
				}
				
				if (LocationHelper.currentAddress != null && LocationHelper.currentAddress.trim() != "") {
					toInsert.location = LocationHelper.currentAddress;
				}
				
				toInsert.type = getString(R.string.camera);
				mDatabaseAdapter.open();
				entry.id = mDatabaseAdapter.insertToEntryTable(toInsert).toString();
				mDatabaseAdapter.close();
			}
		}
		
		if (!intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA))
			startCamera();
		
	}

	private void setImageResource(Drawable mDrawable) {
		if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
			final float scale = this.getResources().getDisplayMetrics().density;
			int width = (int) (84 * scale + 0.5f);
			int height = (int) (111 * scale + 0.5f);			
			editImageDisplay.setLayoutParams(new LayoutParams(width, height));
		}
		editImageDisplay.setImageDrawable(mDrawable);
	}
	
	private void startCamera() {
		/////// ******* Starting Camera to capture Image ******** //////////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Intent camera = new Intent(this, Camera.class);
			File file = fileHelper.getCameraFileLargeEntry(entry.id);
			camera.putExtra(Constants.FULL_SIZE_IMAGE_PATH, file.toString());
			startActivityForResult(camera, PICTURE_RESULT);
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (PICTURE_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				isChanged = true;
				new SaveAndDisplayImage().execute();
			} else {
				isChanged = false;
				if(!setUnknown) {
					File mFile = fileHelper.getCameraFileSmallEntry(entry.id);
					if (mFile.canRead()) {
						Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
						setImageResource(mDrawable);
					} else {
						DatabaseAdapter adapter = new DatabaseAdapter(this);
						adapter.open();
						adapter.deleteEntryTableEntryID(entry.id + "");
						adapter.close();
					}
				}
				if(!intentExtras.containsKey(Constants.IS_COMING_FROM_SHOW_PAGE)) {
					finish();
				}
			}
		}
	}

	private class SaveAndDisplayImage extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			editLoadProgress.setVisibility(View.VISIBLE);
			editImageDisplay.setVisibility(View.GONE);
			editDelete.setEnabled(false);
			editSaveEntry.setEnabled(false);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			new CameraFileSave(CameraActivity.this).resizeImageAndSaveThumbnails(entry.id + "");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			editLoadProgress.setVisibility(View.GONE);
			editImageDisplay.setVisibility(View.VISIBLE);
			File mFile = fileHelper.getCameraFileSmallEntry(entry.id);
			Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
			setImageResource(mDrawable);
			editDelete.setEnabled(true);
			editSaveEntry.setEnabled(true);
			super.onPostExecute(result);
		}
	}

	private void setGraphicsCamera() {
		////// ****** Shows Camera Details ********////////
		editCameraDetails.setVisibility(View.VISIBLE);
	}

	private void setClickListeners() {
		// ////// ******* Adding Click Listeners to UI Items ******** //////////
		ImageView editImageDisplay = (ImageView) findViewById(R.id.edit_image_display);
		editImageDisplay.setOnClickListener(this);
		Button editRetakeButton = (Button) findViewById(R.id.edit_retake_button);
		editRetakeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		////////// ********** Adding action if image is pressed ********		 ///////////
		if (v.getId() == R.id.edit_image_display) {
			File mFile = fileHelper.getCameraFileLargeEntry(entry.id);
			if(mFile.canRead()) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", entry.id);
				startActivity(intent);
			} else {
				Toast.makeText(this, "no image to preview", Toast.LENGTH_SHORT).show();
			}
		}

		///////// ********** Adding action if retake button is pressed ******//////////
		if (v.getId() == R.id.edit_retake_button) {
			startCamera();
		}
	}

	@Override
	protected void deleteFile() {
		fileHelper.deleteAllEntryFiles(entry.id);
	}
	
	@Override
	protected void startIntentAfterDelete(Bundle tempBundle) {
		super.startIntentAfterDelete(tempBundle);
		Intent mIntent = new Intent(this, ShowCameraActivity.class);
		mIntent.putExtras(tempBundle);
		setResult(Activity.RESULT_CANCELED, mIntent);
	}
	
	@Override
	protected void saveEntryStartIntent(Bundle tempBundle) {
		Intent mIntent = new Intent(this, ShowCameraActivity.class);
		mIntent.putExtras(tempBundle);
		setResult(Activity.RESULT_OK, mIntent);
	}

	@Override
	protected Boolean checkEntryModified() {
		if(super.checkEntryModified() || isChanged)
			return true;
		else 
			return false;
	}
	
	@Override
	protected Boolean checkFavoriteModified() {
		if(super.checkFavoriteModified() || isChanged)
			return true;
		else 
			return false;
	}
}
