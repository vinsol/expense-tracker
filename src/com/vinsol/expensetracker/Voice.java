package com.vinsol.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class Voice extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice);
        Log.v("Voice", "Voice");
	}
	
}
