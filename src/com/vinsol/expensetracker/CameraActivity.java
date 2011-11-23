package com.vinsol.expensetracker;

import java.io.File;
import java.util.HashMap;
import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.CameraFileSave;
import com.vinsol.expensetracker.utils.FileDelete;
import com.vinsol.expensetracker.utils.ImageGet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener{

	private static final int PICTURE_RESULT = 35;
	private TextView text_voice_camera_header_title;
	private ImageView text_voice_camera_voice_details_separator;
	private LinearLayout text_voice_camera_camera_details;
	private long _id;
	private Bundle intentExtras;
	private EditText text_voice_camera_amount;
	private EditText text_voice_camera_tag;
	private DatabaseAdapter mDatabaseAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        
        ////////*********     Get id from intent extras     ********   ////////////
        intentExtras = getIntent().getBundleExtra("cameraBundle");
        _id = intentExtras.getLong("_id");
        
        ////////   ********    Initializing and assigning memory to UI Items **********    /////////
        
        text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
        text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.text_voice_camera_voice_details_separator);
        text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.text_voice_camera_camera_details);
        text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
        text_voice_camera_tag = (EditText) findViewById(R.id.text_voice_camera_tag);
        
        setGraphicsCamera();
        setClickListeners();
        startCamera();
        
        
        ////////********    Handle Date Bar   *********   ////////
        new DateHandler(this);
        
        ////////*********     Get Last most accurate location info   *********   /////////
        LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		
		////////     ***********      Initializing Database Adaptor   **********  //////////
		mDatabaseAdapter = new DatabaseAdapter(this);
	}
	
	private void startCamera() {
		
		///////   *******   Starting Camera to capture Image   ********    //////////
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
			File path = new File("/mnt/sdcard/ExpenseTracker");
			path.mkdirs();
			String name = _id+".jpg";
			File file = new File(path, name);
			camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(camera, PICTURE_RESULT);
			Log.v("_id", _id+"");
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(PICTURE_RESULT == requestCode && Activity.RESULT_OK == resultCode){
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				CameraFileSave cameraFileSave = new CameraFileSave(_id+"",this);
				cameraFileSave.create();
				ImageGet imageGet = new ImageGet(""+_id,this);
				Bitmap bm = imageGet.getSmallImage();
				ImageView text_voice_camera_image_display = (ImageView) findViewById(R.id.text_voice_camera_image_display);
				text_voice_camera_image_display.setImageBitmap(bm);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void setGraphicsCamera() {
		///////   ***** Sets Title Camera Entry *********///////
        text_voice_camera_header_title.setText("Camera Entry");
        
        ///////   ***** Sets Title Camera Entry *********///////
        text_voice_camera_voice_details_separator.setVisibility(View.VISIBLE);
        
        //////   ******  Shows Camera Details ********////////
        text_voice_camera_camera_details.setVisibility(View.VISIBLE);
	}
	


	private void setClickListeners() {
		////////    *******    Adding Click Listeners to UI Items ******** //////////
		
		Button text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_save_entry.setOnClickListener(this);
		
		Button text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);
		text_voice_camera_delete.setOnClickListener(this);
		
		ImageView text_voice_camera_image_display = (ImageView) findViewById(R.id.text_voice_camera_image_display);
		text_voice_camera_image_display.setOnClickListener(this);
		
		Button text_voice_camera_retake_button = (Button) findViewById(R.id.text_voice_camera_retake_button);
		text_voice_camera_retake_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		////////     ********  Adding Action to save entry     *********    ///////////
		
		if(v.getId() == R.id.text_voice_camera_save_entry){
			///////    *******  Creating HashMap to update info   *******  ////////
			HashMap<String, String> _list = new HashMap<String, String>();
			_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));
			_list.put(DatabaseAdapter.KEY_AMOUNT, text_voice_camera_amount.getText().toString());
			
			if(text_voice_camera_tag.getText().toString() != ""){
				_list.put(DatabaseAdapter.KEY_TAG, text_voice_camera_tag.getText().toString());
			}
			
			//////    *******   Update database if user added additional info   *******  ///////
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(_list);
			mDatabaseAdapter.close();
			finish();
		}
		
		
		/////////     *********   Adding action if delete button **********  /////////
		
		if(v.getId() == R.id.text_voice_camera_delete){
			new FileDelete(_id);
			
			//////   *******   Delete entry from database ********   /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
			mDatabaseAdapter.close();
			finish();
		}
		
		
		//////////      **********    Adding action if image is pressed   ******** ///////////
		
		if(v.getId() == R.id.text_voice_camera_image_display){
			Intent intentImageViewActivity = new Intent(this, ImageViewActivity.class);
			Bundle intentImageViewActivityBundle = new Bundle();
			intentImageViewActivityBundle.putLong("_id", _id);
			intentImageViewActivity.putExtra("intentImageViewActivity", intentImageViewActivityBundle);
			startActivity(intentImageViewActivity);
		}
		
		/////////   **********   Adding action if retake button is pressed     ******  ////////
		
		if(v.getId() == R.id.text_voice_camera_retake_button){
			startCamera();
		}
	}
}
