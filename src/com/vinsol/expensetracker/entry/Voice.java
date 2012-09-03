/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.entry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.AudioPlay;
import com.vinsol.expensetracker.helpers.RecordingHelper;
import com.vinsol.expensetracker.utils.DisplayTimeForChronometer;
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
		createDatabaseEntry();
		setFavoriteHelper();
		// ////// ******** Starts Recording each time activity starts ****** ///////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			setGraphicsVoice();

			if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA) && !setUnknown) {
				File tempFile;
				if(isFromFavorite) {
					tempFile = fileHelper.getAudioFileFavorite(mFavoriteList.id);
				} else {
					tempFile = fileHelper.getAudioFileEntry(entry.id);
				}
				
				if (tempFile.canRead()) {
					if(isFromFavorite) {
						mAudioPlay = new AudioPlay(mFavoriteList.id, this, isFromFavorite);
					} else {
						mAudioPlay = new AudioPlay(entry.id, this, isFromFavorite);
					}
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.VISIBLE);
					editRerecordButton.setVisibility(View.VISIBLE);
					editTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					editTimeDetailsChronometer.setText("Audio File Missing");
					editTimeDetailsChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					editRerecordButton.setVisibility(View.VISIBLE);
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.GONE);
				}
			} else {
				if(!isFromFavorite) {
					mRecordingHelper = new RecordingHelper(fileHelper.getAudioFileEntry(entry.id), this);
					mRecordingHelper.startRecording();
					controlVoiceChronometer();
				}
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
		setClickListeners();
	}
	
	@Override
	protected void onPause() {
		////// ****** stop recording & audio playback  ***** ///////
		stopRecordingAndPlayback();
		super.onPause();
	}

	private void setClickListeners() {
		//////// ******* Adding Click Listeners to UI Items ******** //////////
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
		// ///// ******** Adding On Click Actions to click listeners ********* //////////

		
		switch (v.getId()) {
		
		// // ***** if stop button pressed ****** //////
		case R.id.edit_stop_button:
			stopRecording(true);
			break;
		// // ***** if play button pressed ****** //////			
		case R.id.edit_play_button:
			// //// ******** to handle playback of recorded file ********* ////////
			if(isFromFavorite) {
				mAudioPlay = new AudioPlay(mFavoriteList.id, this, isFromFavorite);
			} else {
				mAudioPlay = new AudioPlay(entry.id, this, isFromFavorite);
			}

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
			break;

		// // ***** if rerecord button pressed ****** //////
		case R.id.edit_rerecord_button:
			isChanged = true;
			if(countDownTimer != null) {countDownTimer.cancel();}

			// /// ******* If Audio PlayBack is there stop playing audio *******//////
			if (mAudioPlay != null && mAudioPlay.isAudioPlaying()) {mAudioPlay.stopPlayBack();}

			// //// ****** Handles UI items on button click ****** ///////
			editPlayButton.setVisibility(View.GONE);
			editStopButton.setVisibility(View.VISIBLE);
			editRerecordButton.setVisibility(View.GONE);

			// //// ****** Restarts chronometer and recording ******* ////////
			if(mRecordingHelper != null && mRecordingHelper.isRecording()) {mRecordingHelper.stopRecording();}
			File mPath;
			mDatabaseAdapter.open();
			if(isFromFavorite) {
				mPath = fileHelper.getAudioFileFavorite(mFavoriteList.id);
				mDatabaseAdapter.updateFileUploadedFavoriteTable(mFavoriteList.id);
			} else {
				mPath = fileHelper.getAudioFileEntry(entry.id);
				mDatabaseAdapter.updateFileUploadedEntryTable(entry.id);
			}
			mDatabaseAdapter.close();
			mRecordingHelper = new RecordingHelper(mPath, this);
			mRecordingHelper.startRecording();
			editTimeDetailsChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
			editTimeDetailsChronometer.setBase(SystemClock.elapsedRealtime());
			editTimeDetailsChronometer.start();
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void deleteFile() {
		editTimeDetailsChronometer.stop();
		fileHelper.deleteAllEntryFiles(entry.id);
	}

	@Override
	protected Boolean checkEntryModified() {
		if(super.checkEntryModified() || isChanged)
			return true;
		else 
			return false;
	}
	
	@Override
	protected boolean doTaskIfChanged() {
		return isChanged;
	}
	

	@Override
	protected void setDefaultTitle() {
		if(isFromFavorite) {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.edit_favorite)+" "+getString(R.string.finished_voiceentry));
		} else {
			((TextView)findViewById(R.id.header_title)).setText(getString(R.string.finished_voiceentry));
		}
	}
	
	@Override
	protected boolean checkFavoriteComplete() {
		if(editAmount != null && !editAmount.getText().toString().equals("")) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		stopRecordingAndPlayback();
		super.onBackPressed();
	}
	
	private void stopRecordingAndPlayback() {
		if(mRecordingHelper != null && mRecordingHelper.isRecording()) {
			if(editTimeDetailsChronometer != null) {editTimeDetailsChronometer.stop();}
			mRecordingHelper.stopRecording();
		}
		if(mAudioPlay != null && mAudioPlay.isAudioPlaying()) {
			if(editTimeDetailsChronometer != null) {editTimeDetailsChronometer.stop();}
			if(isFromFavorite) {
				mAudioPlay = new AudioPlay(mFavoriteList.id , this, isFromFavorite);
			} else {
				mAudioPlay = new AudioPlay(entry.id , this, isFromFavorite);
			}
			if(editTimeDetailsChronometer != null) {editTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));}
			mAudioPlay.stopPlayBack();
		}
	}
	
	private void stopRecording(boolean isComingFromViewClick) {
		if(countDownTimer != null){countDownTimer.cancel();}

		// //// ****** Handles UI items on button click ****** ///////
		editStopButton.setVisibility(View.GONE);
		editPlayButton.setVisibility(View.VISIBLE);
		editRerecordButton.setVisibility(View.VISIBLE);

		// //// ******* Stop Recording Audio and stop chronometer ******** ////////
		if(mRecordingHelper != null && mRecordingHelper.isRecording()) {mRecordingHelper.stopRecording();}
		editTimeDetailsChronometer.stop();
		
		if(mAudioPlay != null && mAudioPlay.isAudioPlaying()) {mAudioPlay.stopPlayBack();}
		if(isFromFavorite) {
			mAudioPlay = new AudioPlay(mFavoriteList.id , this, isFromFavorite);
		} else {
			mAudioPlay = new AudioPlay(entry.id , this, isFromFavorite);
		}
		String displayTime = new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime());
		editTimeDetailsChronometer.setText(displayTime);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Display Time ", displayTime);
		FlurryAgent.onEvent(getString(R.string.audio_recording_time),map);
	}
	
}
