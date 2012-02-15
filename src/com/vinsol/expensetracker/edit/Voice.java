/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.edit;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.AudioPlay;
import com.vinsol.expensetracker.helpers.RecordingHelper;
import com.vinsol.expensetracker.show.ShowVoiceActivity;
import com.vinsol.expensetracker.utils.DisplayTimeForChronometer;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.MyCountDownTimer;

public class Voice extends EditAbstract {

	private RelativeLayout editVoiceDetails;
	private Chronometer editTimeDetailsChronometer;
	private Button editStopButton;
	private Button editPlayButton;
	private Button editRerecordButton;
	private MyCountDownTimer countDownTimer;
	private RecordingHelper mRecordingHelper;
	private AudioPlay mAudioPlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ////// ******** Initializing and assigning memory to UI Items ********** /////////
		editVoiceDetails = (RelativeLayout) findViewById(R.id.edit_voice_details);
		editTimeDetailsChronometer = (Chronometer) findViewById(R.id.edit_time_details_chronometer);
		editStopButton = (Button) findViewById(R.id.edit_stop_button);
		editPlayButton = (Button) findViewById(R.id.edit_play_button);
		editRerecordButton = (Button) findViewById(R.id.edit_rerecord_button);
		typeOfEntry = R.string.voice;
		typeOfEntryFinished = R.string.finished_voiceentry;
		typeOfEntryUnfinished = R.string.unfinished_voiceentry;
		editHelper();
		// ////// ******** Starts Recording each time activity starts ****** ///////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			setGraphicsVoice();

			if (intentExtras.containsKey(Constants.ENTRY_LIST_EXTRA) && !setUnknown) {
				File tempFile = fileHelper.getAudioFileEntry(entry.id);

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(entry.id, this, false);
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.VISIBLE);
					editRerecordButton.setVisibility(View.VISIBLE);
					editTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					editTimeDetailsChronometer.setText("Audio File Missing");
					editRerecordButton.setVisibility(View.VISIBLE);
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.GONE);
				}
			} else {
				mRecordingHelper = new RecordingHelper(entry.id + "", this);
				mRecordingHelper.startRecording();
				controlVoiceChronometer();
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
		setClickListeners();
	}
	
	@Override
	protected void onPause() {

		// //// ***** Check whether audio is recording or not ******* ///////
		// //// ****** If audio recording started then stop recording audio  ***** ///////
		try {
			if (mRecordingHelper.isRecording()) {
				mRecordingHelper.stopRecording();
			}
		} catch (Exception e) {
			Log.d("Recording Stops");
		}
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {
			Log.d("Audio Play Stops");
		}
		super.onPause();
	}

	private void setClickListeners() {
		// ////// ******* Adding Click Listeners to UI Items ******** //////////

		editStopButton.setOnClickListener(this);
		editPlayButton.setOnClickListener(this);
		editRerecordButton.setOnClickListener(this);
	}

	private void controlVoiceChronometer() {
		editTimeDetailsChronometer.start();
		editTimeDetailsChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (editTimeDetailsChronometer.getText().length() > 5) {
					editTimeDetailsChronometer.stop();
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.VISIBLE);
					editRerecordButton.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void setGraphicsVoice() {
		////// ****** Shows Voice Details ********////////
		editVoiceDetails.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		// ///// ******** Adding On Click Actions to click listeners *********
		// //////////

		// // ***** if stop button pressed ****** //////
		if (v.getId() == R.id.edit_stop_button) {
			try {
				countDownTimer.cancel();
			} catch (NullPointerException e) {
			}
			

			// //// ****** Handles UI items on button click ****** ///////
			editStopButton.setVisibility(View.GONE);
			editPlayButton.setVisibility(View.VISIBLE);
			editRerecordButton.setVisibility(View.VISIBLE);

			// //// ******* Stop Recording Audio and stop chronometer ********
			// ////////
			try {
				if (mRecordingHelper.isRecording())
					mRecordingHelper.stopRecording();
			} catch (Exception e) {
			}
			editTimeDetailsChronometer.stop();
			try {
				if (mAudioPlay.isAudioPlaying())
					mAudioPlay.stopPlayBack();
			} catch (Exception e) {
			}
			try {
				mAudioPlay = new AudioPlay(entry.id , this, false);
				editTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
			} catch (NullPointerException e) {

			}
		}

		// // ***** if play button pressed ****** //////
		else if (v.getId() == R.id.edit_play_button) {
			// //// ******** to handle playback of recorded file ********* ////////
			mAudioPlay = new AudioPlay(entry.id, this, false);

			// ///// ******* Chronometer Starts Countdown ****** ///////
			countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000, editTimeDetailsChronometer,editStopButton,editPlayButton,mAudioPlay);

			// //// ****** Handles UI items on button click ****** ///////
			editPlayButton.setVisibility(View.GONE);
			editStopButton.setVisibility(View.VISIBLE);
			editRerecordButton.setVisibility(View.VISIBLE);

			// /// ******** Start Audio Playback and counter to play audio
			// ****** ///////
			if (!mAudioPlay.isAudioPlaying()) {
				mAudioPlay.startPlayBack();
			} else {
				mAudioPlay.stopPlayBack();
				mAudioPlay.startPlayBack();
			}
			countDownTimer.start();
		}

		// // ***** if rerecord button pressed ****** //////
		else if (v.getId() == R.id.edit_rerecord_button) {
			isChanged = true;
			try {
				countDownTimer.cancel();
			} catch (NullPointerException e) {
			}

			// /// ******* If Audio PlayBack is there stop playing audio
			// *******//////
			try {
				if (mAudioPlay.isAudioPlaying()) {
					mAudioPlay.stopPlayBack();
				}
			} catch (NullPointerException e) {
			}

			// //// ****** Handles UI items on button click ****** ///////
			editPlayButton.setVisibility(View.GONE);
			editStopButton.setVisibility(View.VISIBLE);
			editRerecordButton.setVisibility(View.GONE);

			// //// ****** Restarts chronometer and recording ******* ////////
			if(mRecordingHelper != null)
				if (mRecordingHelper.isRecording())
					mRecordingHelper.stopRecording();
			mRecordingHelper = new RecordingHelper(entry.id + "", this);
			mRecordingHelper.startRecording();
			editTimeDetailsChronometer.setBase(SystemClock.elapsedRealtime());
			editTimeDetailsChronometer.start();
		}
	}
	
	@Override
	protected void startIntentAfterDelete(Bundle tempBundle) {
		super.startIntentAfterDelete(tempBundle);
		Intent mIntent = new Intent(this, ShowVoiceActivity.class);
		mIntent.putExtras(tempBundle);
		setResult(Activity.RESULT_CANCELED, mIntent);
	}
	
	@Override
	protected void deleteFile() {
		editTimeDetailsChronometer.stop();
		fileHelper.deleteAllEntryFiles(entry.id);
	}
	
	@Override
	protected void saveEntryStartIntent(Bundle tempBundle) {
		Intent mIntent = new Intent(this, ShowVoiceActivity.class);
		mIntent.putExtras(tempBundle);
		setResult(Activity.RESULT_OK, mIntent);
	}

	@Override
	protected Boolean checkEntryModified() {
		if(super.checkEntryModified() || isChanged)
			return true;
		else 
			return false;
	}
	
	@Override
	protected Boolean checkFavoriteModified() {
		if(super.checkFavoriteModified() || isChanged)
			return true;
		else 
			return false;
	}
	
}
