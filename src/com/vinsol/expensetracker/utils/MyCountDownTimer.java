package com.vinsol.expensetracker.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

	// /////// ********* CountdownTimer for Chronometer ********* //////////
	// countdowntimer is an abstract class, so extend it and fill in methods
	public class MyCountDownTimer extends CountDownTimer {

		private DisplayTime mDisplayTime;
		private Chronometer show_text_voice_camera_time_details_chronometer;
		private Button show_text_voice_camera_stop_button;
		private Button show_text_voice_camera_play_button;
		private AudioPlay mAudioPlay;
		
		public MyCountDownTimer(long millisInFuture, long countDownInterval,Chronometer chronometerTextView,Button stopButton,Button playButton,AudioPlay tempAudioPlay) {
			super(millisInFuture, countDownInterval);
			mDisplayTime = new DisplayTime();
			show_text_voice_camera_time_details_chronometer = chronometerTextView;
			show_text_voice_camera_stop_button = stopButton;
			show_text_voice_camera_play_button = playButton;
			mAudioPlay = tempAudioPlay;
		}

		@Override
		public void onFinish() {
			show_text_voice_camera_time_details_chronometer.setText(mDisplayTime.getDisplayTime(mAudioPlay.getPlayBackTime()));
			show_text_voice_camera_stop_button.setVisibility(View.GONE);
			show_text_voice_camera_play_button.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			show_text_voice_camera_time_details_chronometer.setText(mDisplayTime.getDisplayTime(millisUntilFinished));
		}
	}