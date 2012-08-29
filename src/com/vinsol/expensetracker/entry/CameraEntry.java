/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.entry;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.cameraservice.Camera;
import com.vinsol.expensetracker.helpers.CameraFileSave;
import com.vinsol.expensetracker.helpers.DatabaseAdapter;
import com.vinsol.expensetracker.utils.ImagePreview;
import com.vinsol.expensetracker.utils.Log;

public class CameraEntry extends EditAbstract {

	private static final int PICTURE_RESULT = 35;
	private LinearLayout editCameraDetails;
	private ImageView editImageDisplay;
	private RelativeLayout editLoadProgress;
	private float scale;
	private int width;
	private int height;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//////// ******** Initializing and assigning memory to UI Items ********** /////////
		editCameraDetails = (LinearLayout) findViewById(R.id.edit_camera_details);
		editImageDisplay = (ImageView) findViewById(R.id.edit_image_display);
		editLoadProgress = (RelativeLayout) findViewById(R.id.edit_load_progress);
		typeOfEntry = R.string.camera;
		typeOfEntryFinished = R.string.finished_cameraentry;
		typeOfEntryUnfinished = R.string.unfinished_cameraentry;
		
		editHelper();
		scale = this.getResources().getDisplayMetrics().density;
		width = (int) (84 * scale + 0.5f);
		height = (int) (111 * scale + 0.5f);
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			if(setUnknown) {
				startCamera();
			}
			File mFile;
			if(isFromFavorite) {
				mFile = fileHelper.getCameraFileSmallFavorite(mFavoriteList.id);
			} else {
				mFile = fileHelper.getCameraFileSmallEntry(entry.id);
			}
			if (mFile.canRead() && mFile.exists()) {
				bitmap = BitmapFactory.decodeFile(mFile.getPath());
				setImageResource();
			} else {
				editImageDisplay.setImageResource(R.drawable.no_image_small);
			}
		}
		
		setGraphicsCamera();
		setClickListeners();

		// ////// *********** Initializing Database Adaptor **********//////////
		mDatabaseAdapter = new DatabaseAdapter(this);
		dateViewString = dateBarDateview.getText().toString();
		
		createDatabaseEntry();
		//New Entry
		super.setFavoriteHelper();
		
		if (!intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA))
			startCamera();
	}
	
	@Override
	protected void setFavoriteHelper() {
		//DO Nothing
	}
	
	private void setImageResource() {
		if(bitmap.getHeight() > bitmap.getWidth()) {
			editImageDisplay.setLayoutParams(new LayoutParams(width, height));
		} else {
			editImageDisplay.setLayoutParams(new LayoutParams(height, width));
		}
		editImageDisplay.setImageBitmap(bitmap);
	}
	
	private void startCamera() {
		/////// ******* Starting Camera to capture Image ******** //////////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Intent camera = new Intent(this, Camera.class);
			File file;
			if(isFromFavorite) {
				file = fileHelper.getCameraFileLargeFavorite(mFavoriteList.id);
			} else {
				file = fileHelper.getCameraFileLargeEntry(entry.id);
			}
			Log.d("camera file path +++++++++++++++++ "+file.toString() );
			camera.putExtra(Constants.KEY_FULL_SIZE_IMAGE_PATH, file.toString());
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
				
				if(intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
					if(isFromFavorite) {
						mDatabaseAdapter.open();
						mDatabaseAdapter.updateFileUploadedFavoriteTable(mFavoriteList.id);
						mDatabaseAdapter.close();
					} else {
						mDatabaseAdapter.open();
						mDatabaseAdapter.updateFileUploadedEntryTable(entry.id);
						mDatabaseAdapter.close();
					}
				}
				new SaveAndDisplayImage().execute();
			} else {
				isChanged = false;
				if(!setUnknown) {
					File mFile;
					if(isFromFavorite) {
						mFile = fileHelper.getCameraFileSmallFavorite(mFavoriteList.id);
					} else {
						mFile = fileHelper.getCameraFileSmallEntry(entry.id);
					}
					if (mFile.canRead()) {
						bitmap = BitmapFactory.decodeFile(mFile.getPath());
						setImageResource();
					} else {
						DatabaseAdapter adapter = new DatabaseAdapter(this);
						adapter.open();
						adapter.deleteExpenseEntryByID(entry.id + "");
						adapter.close();
					}
				}
				if(!intentExtras.containsKey(Constants.KEY_IS_COMING_FROM_SHOW_PAGE)) {finish();}
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
			String id;
			if(isFromFavorite) {
				id = mFavoriteList.id;
			} else {
				id = entry.id;
			}
			new CameraFileSave(CameraEntry.this).resizeImageAndSaveThumbnails(id + "",isFromFavorite);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			editLoadProgress.setVisibility(View.GONE);
			editImageDisplay.setVisibility(View.VISIBLE);
			File mFile;
			if(isFromFavorite) {
				mFile = fileHelper.getCameraFileSmallFavorite(mFavoriteList.id);
			} else {
				mFile = fileHelper.getCameraFileSmallEntry(entry.id);
			}
			bitmap = BitmapFactory.decodeFile(mFile.getPath());
			setImageResource();
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
		switch (v.getId()) {
		case R.id.edit_image_display:
			File mFile = fileHelper.getCameraFileLargeEntry(entry.id);
			if(mFile.canRead()) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra(Constants.KEY_ID, entry.id);
				startActivity(intent);
			} else {
				Toast.makeText(this, "no image to preview", Toast.LENGTH_SHORT).show();
			}
			break;

		///////// ********** Adding action if retake button is pressed ******//////////
		case R.id.edit_retake_button:
			startCamera();
		default:
			break;
		}
	}

	@Override
	protected void deleteFile() {
		fileHelper.deleteAllEntryFiles(entry.id);
	}

	@Override
	protected Boolean checkEntryModified() {
		if(super.checkEntryModified() || isChanged)
			return true;
		else 
			return false;
	}
	
	@Override
	protected boolean doTaskIfChanged() {
		return isChanged;
	}
	
	@Override
	protected void setDefaultTitle() {
		if(isFromFavorite) {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.edit_favorite)+" "+getString(R.string.finished_cameraentry));
		} else {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.finished_cameraentry));
		}
	}
	
	@Override
	protected boolean checkFavoriteComplete() {
		if(editAmount != null && !editAmount.getText().toString().equals("")) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		recycleBitmap();
		super.onBackPressed();
	}
	
	private void recycleBitmap() {
		if(bitmap!=null) {bitmap.recycle();}
	}
}
