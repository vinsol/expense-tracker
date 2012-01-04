package com.vinsol.expensetracker.edit;

import java.io.File;
import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.show.ShowCameraActivity;
import com.vinsol.expensetracker.helpers.CameraFileSave;
import com.vinsol.expensetracker.helpers.DateHelper;
import com.vinsol.expensetracker.helpers.FileDelete;
import com.vinsol.expensetracker.helpers.LocationHelper;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.utils.ImagePreview;

public class CameraActivity extends EditAbstract {

	private static final int PICTURE_RESULT = 35;
	private LinearLayout editCameraDetails;
	private ImageView editImageDisplay;
	private RelativeLayout editLoadProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// //////********* Get id from intent extras ******** ////////////
		intentExtras = getIntent().getBundleExtra("cameraBundle");
		
		// ////// ******** Initializing and assigning memory to UI Items ********** /////////
		editCameraDetails = (LinearLayout) findViewById(R.id.edit_camera_details);
		editImageDisplay = (ImageView) findViewById(R.id.edit_image_display);
		editLoadProgress = (RelativeLayout) findViewById(R.id.edit_load_progress);
		typeOfEntry = R.string.camera;
		typeOfEntryFinished = R.string.finished_cameraentry;
		typeOfEntryUnfinished = R.string.unfinished_cameraentry;
		editHelper();
		if (intentExtras.containsKey("mDisplayList")) {
			if(setUnknown) {
				startCamera();
			}
			File mFile = new File("/sdcard/ExpenseTracker/" + entry.id + "_small.jpg");
			if (mFile.canRead()) {
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
						if (!intentExtras.containsKey("mDisplayList")) {
							DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
							toInsert.timeInMillis = mDateHelper.getTimeMillis();
						} else {
							if(!intentExtras.containsKey("timeInMillis")) {
								DateHelper mDateHelper = new DateHelper(dateBarDateview.getText().toString());
								toInsert.timeInMillis = mDateHelper.getTimeMillis();
							} else {
								Calendar mCalendar = Calendar.getInstance();
								mCalendar.setTimeInMillis(intentExtras.getLong("timeInMillis"));
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
				entry.id = mDatabaseAdapter.insertToDatabase(toInsert).toString();
				mDatabaseAdapter.close();
			}
		}
		
		if (!intentExtras.containsKey("mDisplayList"))
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
		// ///// ******* Starting Camera to capture Image ******** //////////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File path = new File("/mnt/sdcard/ExpenseTracker");
			path.mkdirs();
			String name = entry.id + ".jpg";
			File file = new File(path, name);
			camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
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
					File mFile = new File("/sdcard/ExpenseTracker/" + entry.id+ "_small.jpg");
					if (mFile.canRead()) {
						Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
						setImageResource(mDrawable);
					} else {
						DatabaseAdapter adapter = new DatabaseAdapter(this);
						adapter.open();
						adapter.deleteDatabaseEntryID(entry.id + "");
						adapter.close();
					}
				}
				if(!intentExtras.containsKey("isFromShowPage")) {
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
			File mFile = new File("/sdcard/ExpenseTracker/" + entry.id+ "_small.jpg");
			Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
			setImageResource(mDrawable);
			editDelete.setEnabled(true);
			editSaveEntry.setEnabled(true);
			super.onPostExecute(result);
		}
	}

	private void setGraphicsCamera() {
		// ///// ***** Sets Title Camera Entry *********///////
		editHeaderTitle.setText("Camera Entry");

		// //// ****** Shows Camera Details ********////////
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
		
		// //////// ********** Adding action if image is pressed ********		 ///////////
		if (v.getId() == R.id.edit_image_display) {
			File mFile = new File("/sdcard/ExpenseTracker/" + entry.id + ".jpg");
			if(mFile.canRead()) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", entry.id);
				startActivity(intent);
			} else {
				Toast.makeText(this, "no image to preview", Toast.LENGTH_SHORT).show();
			}
		}

		// /////// ********** Adding action if retake button is pressed ******//////////
		if (v.getId() == R.id.edit_retake_button) {
			startCamera();
		}
	}

	@Override
	protected void deleteAction() {
		super.deleteAction();
		new FileDelete(entry.id);
	}
	
	@Override
	protected void startIntentAfterDelete(Bundle tempBundle) {
		super.startIntentAfterDelete(tempBundle);
		Intent mIntent = new Intent(this, ShowCameraActivity.class);
		mIntent.putExtra("cameraShowBundle", tempBundle);
		setResult(Activity.RESULT_CANCELED, mIntent);
	}
	
	@Override
	protected void saveEntryStartIntent(Bundle tempBundle) {
		Intent mIntent = new Intent(this, ShowCameraActivity.class);
		mIntent.putExtra("cameraShowBundle", tempBundle);
		setResult(Activity.RESULT_OK, mIntent);
	}

	@Override
	protected Boolean checkDataModified() {
		if(super.checkDataModified() || isChanged)
			return true;
		else 
			return false;
	}
}
