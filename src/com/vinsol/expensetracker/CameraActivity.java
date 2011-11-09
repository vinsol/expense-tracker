package com.vinsol.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraActivity extends Activity implements OnClickListener{

	private static final int PICTURE_RESULT = 35;
	private TextView text_voice_camera_header_title;
	private ImageView text_voice_camera_voice_details_separator;
	private LinearLayout text_voice_camera_camera_details;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        
        ///////   *******   Starting Camera to capture Image   ********    //////////
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
        startActivityForResult(camera, PICTURE_RESULT);

        
        
        
        ////////   ********    Initializing and assigning memory to UI Items **********    /////////
        
        text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
        text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.text_voice_camera_voice_details_separator);
        text_voice_camera_camera_details = (LinearLayout) findViewById(R.id.text_voice_camera_camera_details);
        
        setGraphicsCamera();
        setClickListeners();
		
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
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
