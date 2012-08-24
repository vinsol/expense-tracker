/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.entry.CameraEntry;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.utils.ImagePreview;

public class ShowCamera extends ShowAbstract {

	private ImageView showImageDisplay;
	private LinearLayout showCameraDetails;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ///// ****** Assigning memory ******* /////////
		showImageDisplay = (ImageView) findViewById(R.id.show_image_display);
		showCameraDetails = (LinearLayout) findViewById(R.id.show_camera_details);
		
		// //////********* Get id from intent extras ******** ////////////

		setGraphicsCamera();
		typeOfEntry = R.string.camera;
		typeOfEntryFinished = R.string.finished_cameraentry;
		typeOfEntryUnfinished = R.string.unfinished_cameraentry;
		showHelper();
		if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
			File mFile = fileHelper.getCameraFileSmallEntry(mShowList.id);
			if (mFile.canRead()) {
				bitmap = BitmapFactory.decodeFile(mFile.getPath());
				if(bitmap.getHeight() > bitmap.getWidth()) {
					final float scale = this.getResources().getDisplayMetrics().density;
					int width = (int) (84 * scale + 0.5f);
					int height = (int) (111 * scale + 0.5f);
					showImageDisplay.setLayoutParams(new LayoutParams(width, height));
				}
				showImageDisplay.setImageBitmap(bitmap);
			} else {
				showImageDisplay.setImageResource(R.drawable.no_image_small);
			}
			mFavoriteHelper = new FavoriteHelper(this,mDatabaseAdapter,fileHelper,mShowList);
		}

		showImageDisplay.setOnClickListener(this);
	}

	private void setGraphicsCamera() {
		// ///// ***** Sets Title Camera Entry *********///////
		showHeaderTitle.setText("Camera Entry");

		// //// ****** Shows Camera Details ********////////
		showCameraDetails.setVisibility(View.VISIBLE);
	}

	@Override
	protected void deleteFile() {
		fileHelper.deleteAllEntryFiles(mShowList.id);
	}
	
	@Override
	protected void editAction() {
		Intent editIntent = new Intent(this, CameraEntry.class);
		editIntent.putExtras(intentExtras);
		startActivityForResult(editIntent,SHOW_RESULT);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.show_image_display) {
			if (mShowList.id != null) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra(Constants.KEY_ID, mShowList.id);
				startActivity(intent);
			} else {
				Toast.makeText(this, "Error Opening Image", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (SHOW_RESULT == requestCode) {
			intentExtras = data.getExtras();
			if(Activity.RESULT_OK == resultCode) {
				doTaskOnActivityResult();
				if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
					File mFile = fileHelper.getCameraFileSmallEntry(mShowList.id);
					if (mFile.canRead()) {
						bitmap = BitmapFactory.decodeFile(mFile.getPath());
						if(bitmap.getHeight() > bitmap.getWidth()) {
							final float scale = this.getResources().getDisplayMetrics().density;
							int width = (int) (84 * scale + 0.5f);
							int height = (int) (111 * scale + 0.5f);
							showImageDisplay.setLayoutParams(new LayoutParams(width, height));
						}
						showImageDisplay.setImageBitmap(bitmap);
					} else {
						showImageDisplay.setImageResource(R.drawable.no_image_small);
					}
					mShowList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
					mFavoriteHelper = new FavoriteHelper(this,mDatabaseAdapter,fileHelper,mShowList);
				}
				showImageDisplay.setOnClickListener(this);
				showDelete.setOnClickListener(this);
				showEdit.setOnClickListener(this);
			}
		}

		if(resultCode == Activity.RESULT_CANCELED) {
			finish();
		}
	}
	
	private void recycleBitmap() {
		if(bitmap!=null) {bitmap.recycle();}
	}
	
	@Override
	public void onBackPressed() {
		recycleBitmap();
		super.onBackPressed();
	}

}
