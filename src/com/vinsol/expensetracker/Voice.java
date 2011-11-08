package com.vinsol.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Voice extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        Log.v("Voice", "Voice");
        
        ///////   ***** Sets Title Voice Entry *********///////
        TextView text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
        text_voice_camera_header_title.setText("Voice Entry");
        
        ///////   ***** Sets Title Voice Entry *********///////
        ImageView text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.text_voice_camera_voice_details_separator);
        text_voice_camera_voice_details_separator.setVisibility(View.VISIBLE);
        
        
        
        //////   ******  Shows Voice Details ********////////
        
        RelativeLayout text_voice_camera_voice_details = (RelativeLayout) findViewById(R.id.text_voice_camera_voice_details);
        text_voice_camera_voice_details.setVisibility(View.VISIBLE);
        
        
        
	}
	
}
