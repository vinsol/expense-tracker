package com.vinsol.expensetracker;

import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTime;
import com.vinsol.expensetracker.utils.RecordingHelper;

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
	private RecordingHelper mRecordingHelper;
	private String mFileName = "test1";
	private AudioPlay mAudioPlay;
	
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
        
        ////////   ********  Handle Date Bar   *********   ////////
        new DateHandler(this);
        
        
        ////////   ********   Starts Recording each time activity starts   ******   ///////
        mRecordingHelper = new RecordingHelper(mFileName);
		mRecordingHelper.startRecording();
		
		
		////////*********     Get Last most accurate location info   *********   /////////
		LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
	}
	
	@Override
	protected void onPause() {
		
		//////   *****  Check whether audio is recording or not   *******   ///////
		//////   ******   If audio recording started then stop recording audio   *****   ///////
		if(mRecordingHelper.isRecording()){
			mRecordingHelper.stopRecording();
		}
		super.onPause();
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
			
			//////   ******   Handles UI items on button click  ******  ///////
			text_voice_camera_stop_button.setVisibility(View.GONE);
			text_voice_camera_play_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
			
			//////  *******  Stop Recording Audio and stop chronometer  ********   ////////
			mRecordingHelper.stopRecording();
			text_voice_camera_time_details_chronometer.stop();
		}
		
		
		////  ***** if play button pressed ****** //////		
		else if(v.getId() == R.id.text_voice_camera_play_button){
			//////	     ********   to handle playback of recorded file   *********   ////////
			mAudioPlay = new AudioPlay(mFileName);
			Log.v("hello", text_voice_camera_time_details_chronometer.getText()+"");
			
			///////   *******   Chronometer Starts Countdown   ******  ///////
			countDownTimer = new MyCount(mAudioPlay.getPlayBackTime(), 1000);
			
			//////   ******   Handles UI items on button click  ******  ///////
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
			
			/////   ********   Start Audio Playback and counter to play audio   ****** ///////
			if(!mAudioPlay.isAudioPlaying()){
				mAudioPlay.startPlayBack();
			} else {
				mAudioPlay.stopPlayBack();
				mAudioPlay.startPlayBack();
			}
			countDownTimer.start();
		}
		
		////  ***** if rerecord button pressed ****** //////		
		else if(v.getId() == R.id.text_voice_camera_rerecord_button){
			try{
				countDownTimer.cancel();
			}catch(NullPointerException e){};
			
			/////  *******   If Audio PlayBack is there stop playing audio   *******//////
			try{
				if(mAudioPlay.isAudioPlaying()){
					mAudioPlay.stopPlayBack();
				}
			}catch(NullPointerException e){}
			
			//////   ******   Handles UI items on button click  ******  ///////
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.GONE);
			
			//////  ******  Restarts chronometer and recording   *******  ////////
			if(mRecordingHelper.isRecording())
				mRecordingHelper.stopRecording();
			mRecordingHelper = new RecordingHelper(mFileName);
			mRecordingHelper.startRecording();
			text_voice_camera_time_details_chronometer.setBase(SystemClock.elapsedRealtime());
			text_voice_camera_time_details_chronometer.start();
		}
	}
	
	
	/////////   *********       CountdownTimer for Chronometer    *********    //////////      
	//countdowntimer is an abstract class, so extend it and fill in methods
	private class MyCount extends CountDownTimer{

		DisplayTime mDisplayTime;
		
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mDisplayTime = new DisplayTime();
		}

		@Override
		public void onFinish() {
			text_voice_camera_time_details_chronometer.setText(mDisplayTime.getDisplayTime(mAudioPlay.getPlayBackTime()));
			text_voice_camera_stop_button.setVisibility(View.GONE);
			text_voice_camera_play_button.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			text_voice_camera_time_details_chronometer.setText(mDisplayTime.getDisplayTime(millisUntilFinished));
		}

	}
	
}
