package com.vinsol.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Voice extends Activity implements OnClickListener{
	
	private TextView text_voice_camera_header_title;
	private ImageView text_voice_camera_voice_details_separator;
	private RelativeLayout text_voice_camera_voice_details;
	private Chronometer text_voice_camera_time_details_chronometer;
	private Button text_voice_camera_stop_button;
	private Button text_voice_camera_play_button;
	private Button text_voice_camera_rerecord_button;
	private MyCount countDownTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        
        
        ////////   ********    Initializing and assigning memory to UI Items **********    /////////
        
        text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
        text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.text_voice_camera_voice_details_separator);
        text_voice_camera_voice_details = (RelativeLayout) findViewById(R.id.text_voice_camera_voice_details);
        text_voice_camera_time_details_chronometer = (Chronometer) findViewById(R.id.text_voice_camera_time_details_chronometer);
        text_voice_camera_stop_button = (Button) findViewById(R.id.text_voice_camera_stop_button);
        text_voice_camera_play_button = (Button) findViewById(R.id.text_voice_camera_play_button);
        text_voice_camera_rerecord_button = (Button) findViewById(R.id.text_voice_camera_rerecord_button);
        
        setGraphicsVoice();
        controlVoiceChronometer();
        setClickListeners();
        
        ////////********    Handle Date Bar   *********   ////////
        new DateHandler(this);
	}

	private void setClickListeners() {
		////////    *******    Adding Click Listeners to UI Items ******** //////////
		
		text_voice_camera_stop_button.setOnClickListener(this);
		text_voice_camera_play_button.setOnClickListener(this);
		text_voice_camera_rerecord_button.setOnClickListener(this);
		
	}

	private void controlVoiceChronometer() {
		text_voice_camera_time_details_chronometer.start();
		text_voice_camera_time_details_chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if(text_voice_camera_time_details_chronometer.getText().length() > 5){
					text_voice_camera_time_details_chronometer.stop();
					text_voice_camera_stop_button.setVisibility(View.GONE);
					text_voice_camera_play_button.setVisibility(View.VISIBLE);
					text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void setGraphicsVoice() {
		///////   ***** Sets Title Voice Entry *********///////
        text_voice_camera_header_title.setText("Voice Entry");
        
        ///////   ***** Sets Title Voice Entry *********///////
        text_voice_camera_voice_details_separator.setVisibility(View.VISIBLE);
        
        //////   ******  Shows Voice Details ********////////
        text_voice_camera_voice_details.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		///////   ********     Adding On Click Actions to click listeners *********    //////////
		
		
						////  ***** if stop button pressed ****** //////
		if(v.getId() == R.id.text_voice_camera_stop_button){
			try{
				countDownTimer.cancel();
			}catch(NullPointerException e){};
			
			text_voice_camera_time_details_chronometer.stop();
			text_voice_camera_stop_button.setVisibility(View.GONE);
			text_voice_camera_play_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
		}
						////  ***** if play button pressed ****** //////		
		else if(v.getId() == R.id.text_voice_camera_play_button){
			Log.v("hello", text_voice_camera_time_details_chronometer.getText()+"");
			countDownTimer = new MyCount(300000, 1000);
			countDownTimer.start();
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
			
		}
						////  ***** if rerecord button pressed ****** //////		
		else if(v.getId() == R.id.text_voice_camera_rerecord_button){
			try{
				countDownTimer.cancel();
			}catch(NullPointerException e){};
			text_voice_camera_time_details_chronometer.setBase(SystemClock.elapsedRealtime());
			text_voice_camera_time_details_chronometer.start();
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.GONE);
		}
		
	}
	
	
	/////////   *********       CountdownTimer for Chronometer    *********    //////////      
	//countdowntimer is an abstract class, so extend it and fill in methods
	private class MyCount extends CountDownTimer{

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			text_voice_camera_time_details_chronometer.setText("done!");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			String minutes = "00";
			if(millisUntilFinished >= 60000){
				Long temp = millisUntilFinished / 60000;
				if(temp < 10){
					minutes = "0"+temp;
				}else{
					minutes = temp+"";
				}
			}
			String seconds = (millisUntilFinished%60000)/1000+"";
			if((millisUntilFinished%60000)/1000 < 10){
					seconds = "0"+seconds;
			}
			text_voice_camera_time_details_chronometer.setText(minutes +":" + seconds);
		}

	}
	
}
