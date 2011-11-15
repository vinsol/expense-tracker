package com.vinsol.expensetracker;

import com.vinsol.expensetracker.location.LocationLast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;

public class TextEntry extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

        ///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        Log.v("Text", "Text");
        
        ///////  ******* Sets Header Margin  ******* ////////
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 20);
        
        EditText text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
        text_voice_camera_amount.setLayoutParams(params);
        
        
        ////////   ********    Handle Date Bar   *********   ////////
        new DateHandler(this);
	}
	
	@Override
	protected void onResume() {
		new LocationLast(this);
		super.onResume();
	}
	
}
