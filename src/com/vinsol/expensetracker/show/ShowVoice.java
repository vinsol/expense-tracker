/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.entry.Voice;
import com.vinsol.expensetracker.helpers.AudioPlay;
import com.vinsol.expensetracker.helpers.FavoriteHelper;
import com.vinsol.expensetracker.utils.DisplayTimeForChronometer;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.MyCountDownTimer;

public class ShowVoice extends ShowAbstract {
 
	private RelativeLayout showVoiceDetails;
	private Button showPlayButton;
	private Button showStopButton;
	private Chronometer showTimeDetailsChronometer;
	private MyCountDownTimer countDownTimer;
	private AudioPlay mAudioPlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showVoiceDetails = (RelativeLayout) findViewById(R.id.show_voice_details);
		showPlayButton = (Button) findViewById(R.id.show_play_button);
		showStopButton = (Button) findViewById(R.id.show_stop_button);
		showTimeDetailsChronometer = (Chronometer) findViewById(R.id.show_time_details_chronometer);
		typeOfEntry = R.string.voice;
		typeOfEntryFinished = R.string.finished_voiceentry;
		typeOfEntryUnfinished = R.string.unfinished_voiceentry;
		showHelper();
		showPlayButton.setOnClickListener(this);
		showStopButton.setOnClickListener(this);

		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			updateUI();
			if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
				File tempFile = fileHelper.getAudioFileEntry(mShowList.id);

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(mShowList.id, this, false);
					showStopButton.setVisibility(View.GONE);
					showPlayButton.setVisibility(View.VISIBLE);
					showTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					showTimeDetailsChronometer.setText("Audio File Missing");
					showTimeDetailsChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					showStopButton.setVisibility(View.GONE);
					showPlayButton.setVisibility(View.GONE);
				}
				mFavoriteHelper = new FavoriteHelper(this,mDatabaseAdapter,fileHelper,mShowList);
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	private void updateUI() {
		// //// ****** Shows Voice Details ********////////
		showVoiceDetails.setVisibility(View.VISIBLE);
	}

	@Override
	protected void deleteFile() {
		// /// ******* If Audio PlayBack is there stop playing audio*******//////
		try {
			if (mAudioPlay.isAudioPlaying()) {
				mAudioPlay.stopPlayBack();
			}
		} catch (NullPointerException e) {
		}
		showTimeDetailsChronometer.stop();
		fileHelper.deleteAllEntryFiles(mShowList.id);
	}
	
	@Override
	protected void editAction() {
		Intent editIntent = new Intent(this, Voice.class);
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {

		}
		editIntent.putExtras(intentExtras);
		startActivityForResult(editIntent, SHOW_RESULT);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.show_play_button:
			// //// ******** to handle playback of recorded file *********
			// ////////
			mAudioPlay = new AudioPlay(mShowList.id, this, false);

			// ///// ******* Chronometer Starts Countdown ****** ///////
			countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000, showTimeDetailsChronometer, showStopButton ,showPlayButton, mAudioPlay);

			// //// ****** Handles UI items on button click ****** ///////
			showPlayButton.setVisibility(View.GONE);
			showStopButton.setVisibility(View.VISIBLE);

			///// ******** Start Audio Playback and counter to play audio
			// ****** ///////
			if (!mAudioPlay.isAudioPlaying()) {
				mAudioPlay.startPlayBack();
			} else {
				mAudioPlay.stopPlayBack();
				mAudioPlay.startPlayBack();
			}
			countDownTimer.start();
			break;
			
		case R.id.show_stop_button:
			if(countDownTimer != null) {countDownTimer.cancel();}
			
			////// ****** Handles UI items on button click ****** ///////
			showStopButton.setVisibility(View.GONE);
			showPlayButton.setVisibility(View.VISIBLE);

			////// ******* Stop Recording Audio and stop chronometer ********
			//////////
			showTimeDetailsChronometer.stop();
			if (mAudioPlay != null && mAudioPlay.isAudioPlaying()) {mAudioPlay.stopPlayBack();}
			showTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		////// ***** Check whether audio is recording or not ******* ///////
		////// ****** If audio recording started then stop recording audio ***** ///////
		try {
			countDownTimer.cancel();
		} catch (NullPointerException e) {
		}
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {
			Log.d("Audio Play Stop");
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(SHOW_RESULT == requestCode) {
			intentExtras = data.getExtras();
			if (Activity.RESULT_OK == resultCode) {
				doTaskOnActivityResult();
				showDelete.setOnClickListener(this);
				showPlayButton.setOnClickListener(this);
				showStopButton.setOnClickListener(this);
				showEdit.setOnClickListener(this);
	
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					updateUI();
					if (intentExtras.containsKey(Constants.KEY_ENTRY_LIST_EXTRA)) {
						File tempFile = fileHelper.getAudioFileEntry(mShowList.id);
	
						if (tempFile.canRead()) {
							mAudioPlay = new AudioPlay(mShowList.id, this, false);
							showStopButton.setVisibility(View.GONE);
							showPlayButton.setVisibility(View.VISIBLE);
							showTimeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
						} else {
							showTimeDetailsChronometer.setText("Audio File Missing");
							showStopButton.setVisibility(View.GONE);
							showPlayButton.setVisibility(View.GONE);
						}
						mShowList = intentExtras.getParcelable(Constants.KEY_ENTRY_LIST_EXTRA);
						mFavoriteHelper = new FavoriteHelper(this,mDatabaseAdapter,fileHelper,mShowList);
					}
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
				}
			}
	
			if(resultCode == Activity.RESULT_CANCELED) {
				finish();
			}
		}
	}
	
}
