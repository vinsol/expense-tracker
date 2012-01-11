/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.utils;

import com.vinsol.expensetracker.helpers.AudioPlay;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

	// /////// ********* CountdownTimer for Chronometer ********* //////////
	// countdowntimer is an abstract class, so extend it and fill in methods
	public class MyCountDownTimer extends CountDownTimer {

		private DisplayTimeForChronometer mDisplayTime;
		private Chronometer showDetailsChronometer;
		private Button showStopButton;
		private Button showPlayButton;
		private AudioPlay mAudioPlay;
		
		public MyCountDownTimer(long millisInFuture, long countDownInterval,Chronometer chronometerTextView,Button stopButton,Button playButton,AudioPlay tempAudioPlay) {
			super(millisInFuture, countDownInterval);
			mDisplayTime = new DisplayTimeForChronometer();
			showDetailsChronometer = chronometerTextView;
			showStopButton = stopButton;
			showPlayButton = playButton;
			mAudioPlay = tempAudioPlay;
		}

		@Override
		public void onFinish() {
			showDetailsChronometer.setText(mDisplayTime.getDisplayTime(mAudioPlay.getPlayBackTime()));
			showStopButton.setVisibility(View.GONE);
			showPlayButton.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			showDetailsChronometer.setText(mDisplayTime.getDisplayTime(millisUntilFinished));
		}
	}