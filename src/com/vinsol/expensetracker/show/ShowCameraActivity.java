package com.vinsol.expensetracker.show;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.CameraActivity;
import com.vinsol.expensetracker.helpers.FileDelete;
import com.vinsol.expensetracker.utils.ImagePreview;

public class ShowCameraActivity extends ShowAbstract {

	private ImageView showImageDisplay;
	private LinearLayout showCameraDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ///// ****** Assigning memory ******* /////////
		showImageDisplay = (ImageView) findViewById(R.id.show_image_display);
		showCameraDetails = (LinearLayout) findViewById(R.id.show_camera_details);
		
		// //////********* Get id from intent extras ******** ////////////

		setGraphicsCamera();
		intentExtras = getIntent().getBundleExtra("cameraShowBundle");
		typeOfEntry = R.string.camera;
		typeOfEntryFinished = R.string.finished_cameraentry;
		typeOfEntryUnfinished = R.string.unfinished_cameraentry;
		showHelper();
		if (intentExtras.containsKey("mDisplayList")) {
			File mFile = new File("/sdcard/ExpenseTracker/" + mShowList.id+ "_small.jpg");
			if (mFile.canRead()) {
				Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
				if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
					final float scale = this.getResources().getDisplayMetrics().density;
					int width = (int) (84 * scale + 0.5f);
					int height = (int) (111 * scale + 0.5f);
					showImageDisplay.setLayoutParams(new LayoutParams(width, height));
				}
				showImageDisplay.setImageDrawable(mDrawable);
			} else {
				showImageDisplay.setImageResource(R.drawable.no_image_small);
			}
			FavoriteHelper();
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
	protected void deleteAction() {
		super.deleteAction();
		new FileDelete(mShowList.id);
	}
	
	@Override
	protected void editAction() {
		super.editAction();
		Intent editIntent = new Intent(this, CameraActivity.class);
		editIntent.putExtra("cameraBundle", intentExtras);
		startActivityForResult(editIntent,SHOW_RESULT);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.show_image_display) {
			if (mShowList.id != null) {
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", mShowList.id);
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
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("cameraShowBundle");
				doTaskOnActivityResult();
				if (intentExtras.containsKey("mDisplayList")) {
					File mFile = new File("/sdcard/ExpenseTracker/" + mShowList.id + "_small.jpg");
					if (mFile.canRead()) {Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
						if(mDrawable.getIntrinsicHeight() > mDrawable.getIntrinsicWidth()) {
							final float scale = this.getResources().getDisplayMetrics().density;
							int width = (int) (84 * scale + 0.5f);
							int height = (int) (111 * scale + 0.5f);
							showImageDisplay.setLayoutParams(new LayoutParams(width, height));
						}
						showImageDisplay.setImageDrawable(mDrawable);
					} else {
						showImageDisplay.setImageResource(R.drawable.no_image_small);
					}
					mShowList = intentExtras.getParcelable("mDisplayList");
					FavoriteHelper();
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

}
