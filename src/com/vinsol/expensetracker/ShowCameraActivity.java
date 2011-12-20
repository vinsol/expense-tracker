package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.vinsol.expensetracker.utils.FileDelete;

public class ShowCameraActivity extends ShowAbstract implements OnClickListener {

	private RelativeLayout dateBarRelativeLayout;
	private Button showCameraDelete;
	private DatabaseAdapter mDatabaseAdapter;
	private ImageView showImageDisplay;
	private TextView showHeaderTitle;
	private LinearLayout showCameraDetails;
	private Button showCameraEdit;
	private Bundle intentExtras;
	private ArrayList<String> mShowList;
	private Long userId = null;
	private static final int EDIT_RESULT = 35;
	protected static String favID = null;
	private FavoriteHelper mFavoriteHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_page);

		// ///// ****** Assigning memory ******* /////////
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_date_bar); 
		showCameraDelete = (Button) findViewById(R.id.show_delete);
		showImageDisplay = (ImageView) findViewById(R.id.show_image_display);
		showHeaderTitle = (TextView) findViewById(R.id.show_header_title);
		showCameraDetails = (LinearLayout) findViewById(R.id.show_camera_details);
		showCameraEdit = (Button) findViewById(R.id.show_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		
		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		
		// //////********* Get id from intent extras ******** ////////////

		setGraphicsCamera();

		intentExtras = getIntent().getBundleExtra("cameraShowBundle");
		showHelper(intentExtras,R.string.voice,R.string.finished_voiceentry,R.string.unfinished_voiceentry);
		if (intentExtras.containsKey("mDisplayList")) {
			getData();
			File mFile = new File("/sdcard/ExpenseTracker/" + userId+ "_small.jpg");
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
			mFavoriteHelper = new FavoriteHelper(this, mShowList);
		}

		showImageDisplay.setOnClickListener(this);
		showCameraDelete.setOnClickListener(this);
		showCameraEdit.setOnClickListener(this);

	}

	private void setGraphicsCamera() {
		// ///// ***** Sets Title Camera Entry *********///////
		showHeaderTitle.setText("Camera Entry");

		// //// ****** Shows Camera Details ********////////
		showCameraDetails.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.show_image_display) {
			if (userId != null) {
				
				Intent intent = new Intent(this, ImagePreview.class);
				intent.putExtra("id", userId);
				startActivity(intent);

			} else {
				Toast.makeText(this, "Error Opening Image", Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.show_delete) {
			if (userId != null) {
				new FileDelete(userId);

				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(userId));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}

		}
		
		if(v.getId() == R.id.show_edit){
			Intent editIntent = new Intent(this, CameraActivity.class);
			intentExtras.putBoolean("isFromShowPage", true);
			mShowList.set(4, favID);
			intentExtras.remove("mDisplayList");
			intentExtras.putStringArrayList("mDisplayList", mShowList);
			editIntent.putExtra("cameraBundle", intentExtras);
			startActivityForResult(editIntent,EDIT_RESULT);
		}

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (EDIT_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("cameraShowBundle");
				doTaskOnActivityResult(intentExtras);
				if (intentExtras.containsKey("mDisplayList")) {
					getData();
					File mFile = new File("/sdcard/ExpenseTracker/" + userId + "_small.jpg");
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
					mFavoriteHelper.setShowList(mShowList);
				}

				showImageDisplay.setOnClickListener(this);
				showCameraDelete.setOnClickListener(this);
				showCameraEdit.setOnClickListener(this);
			}
		}
		

		if(resultCode == Activity.RESULT_CANCELED){
			finish();
		}
	}
	
	private void getData(){
		favID = getFavID();
		userId = getId();
		mShowList = getShowList();
	}

}
