package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.utils.FileDelete;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCameraActivity extends Activity implements OnClickListener{

	private TextView show_text_voice_camera_amount;
	private TextView show_text_voice_camera_tag_textview;
	private Button show_text_voice_camera_delete;
	private DatabaseAdapter mDatabaseAdapter;
	private EditText show_text_voice_camera_tag;
	private View show_text_voice_camera_divider_amount_desc;
	private Button show_text_voice_camera_update_entry;
	private ImageView show_text_voice_camera_image_display;
	private TextView show_text_voice_camera_header_title;
	private ImageView show_text_voice_camera_voice_details_separator;
	private LinearLayout show_text_voice_camera_camera_details;
	private TextView show_text_voice_camera_description_show;
	
	private Bundle intentExtras;
	private ArrayList<String> mShowList;
	private Long _id = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_text_voice_camera);
		
		///////   ******  Assigning memory    *******   /////////
		show_text_voice_camera_amount = (TextView) findViewById(R.id.show_text_voice_camera_amount);
		show_text_voice_camera_tag_textview = (TextView) findViewById(R.id.show_text_voice_camera_tag_textview);
		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_tag = (EditText) findViewById(R.id.show_text_voice_camera_tag);
		show_text_voice_camera_divider_amount_desc = findViewById(R.id.show_text_voice_camera_divider_amount_desc);
		show_text_voice_camera_update_entry = (Button) findViewById(R.id.show_text_voice_camera_update_entry);
		show_text_voice_camera_image_display = (ImageView) findViewById(R.id.show_text_voice_camera_image_display);
		show_text_voice_camera_header_title = (TextView) findViewById(R.id.show_text_voice_camera_header_title);
		show_text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.show_text_voice_camera_voice_details_separator);
		show_text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.show_text_voice_camera_camera_details);
		show_text_voice_camera_description_show = (TextView) findViewById(R.id.show_text_voice_camera_description_show);
		
		mDatabaseAdapter = new DatabaseAdapter(this);
		////////*********     Get id from intent extras     ********   ////////////
		
		setGraphicsCamera();
	        
	    intentExtras = getIntent().getBundleExtra("cameraShowBundle");
		mShowList = new ArrayList<String>();
		
		if(intentExtras.containsKey("mDisplayList")){
        	mShowList = new ArrayList<String>();
        	mShowList = intentExtras.getStringArrayList("mDisplayList");
        	_id = Long.parseLong(mShowList.get(0));
        	String amount = mShowList.get(2);
        	String tag = mShowList.get(1);
        	if(!(amount.equals("") || amount == null)){
        		if(!amount.contains("?"))
        			show_text_voice_camera_amount.setText(amount);
        	}
        	if(!(tag.equals("") || tag == null || tag.equals(getString(R.string.unfinished_cameraentry)))){
        		show_text_voice_camera_tag_textview.setText(tag);
        	} else {
        		show_text_voice_camera_description_show.setVisibility(View.GONE);
        		show_text_voice_camera_tag_textview.setVisibility(View.GONE);
        		show_text_voice_camera_tag.setVisibility(View.VISIBLE);
        		show_text_voice_camera_divider_amount_desc.setVisibility(View.GONE);
        		show_text_voice_camera_update_entry.setVisibility(View.VISIBLE);
        		show_text_voice_camera_update_entry.setOnClickListener(this);
        	}
        	
        	File mFile = new File("/sdcard/ExpenseTracker/"+_id+"_small.jpg");
        	System.gc();
        	if(mFile.canRead()){
        		Drawable mDrawable = Drawable.createFromPath(mFile.getPath());
        		show_text_voice_camera_image_display.setImageDrawable(mDrawable);
        	} else {
        		show_text_voice_camera_image_display.setImageResource(R.drawable.no_image_small);
        	}
        	Calendar mCalendar = Calendar.getInstance();
        	mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
        	new ShowDateHandler(this, mCalendar);
        }
		
		show_text_voice_camera_image_display.setOnClickListener(this);
		show_text_voice_camera_delete.setOnClickListener(this);
		
	}
	
	private void setGraphicsCamera() {
		///////   ***** Sets Title Camera Entry *********///////
        show_text_voice_camera_header_title.setText("Camera Entry");
        
        ///////   ***** Sets Title Camera Entry *********///////
        show_text_voice_camera_voice_details_separator.setVisibility(View.VISIBLE);
        
        //////   ******  Shows Camera Details ********////////
        show_text_voice_camera_camera_details.setVisibility(View.VISIBLE);
	}
	

	public void onBackPressed() {
	    // This will be called either automatically for you on 2.0    
	    // or later, or by the code above on earlier versions of the platform.
		if(show_text_voice_camera_tag.isShown()){
			if(show_text_voice_camera_tag.getText().toString() != "")
				saveEntry();
		}
	    finish();
	}
	
	private void saveEntry() {
		///////    *******  Creating HashMap to update info   *******  ////////
		HashMap<String, String> _list = new HashMap<String, String>();
		_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));
		
		if(show_text_voice_camera_tag.getText().toString() != ""){
			_list.put(DatabaseAdapter.KEY_TAG, show_text_voice_camera_tag.getText().toString());
		}
		
		//////    *******   Update database if user added additional info   *******  ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
		finish();
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.show_text_voice_camera_image_display){
			if(_id != null){
				Intent intentImageViewActivity = new Intent(this, ImageViewActivity.class);
				Bundle intentImageViewActivityBundle = new Bundle();
				intentImageViewActivityBundle.putLong("_id", _id);
				intentImageViewActivity.putExtra("intentImageViewActivity", intentImageViewActivityBundle);
				startActivity(intentImageViewActivity);
			} else {
				Toast.makeText(this, "Error Opening Image", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_text_voice_camera_delete){
			if(_id != null){
				new FileDelete(_id);
				
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
			
		}
		
		if(v.getId() == R.id.show_text_voice_camera_update_entry){
			saveEntry();
		}
		
	}
	
}
