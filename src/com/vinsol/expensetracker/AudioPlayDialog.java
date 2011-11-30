package com.vinsol.expensetracker;

import com.vinsol.expensetracker.utils.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTime;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;

public class AudioPlayDialog extends Dialog implements
		android.view.View.OnClickListener,
		android.content.DialogInterface.OnDismissListener,
		android.content.DialogInterface.OnCancelListener {

	private Button audio_play_dialog_stop_button;
	private Button audio_play_dialog_play_button;
	private Button audio_play_dialog_cancel_button;
	private Chronometer audio_play_dialog_time_details_chronometer;
	private AudioPlay mAudioPlay;
	private MyCount countDownTimer;
	private String mFile;
	private Context mContext;

	public AudioPlayDialog(Context context, String id) {
		super(context);
		mContext = context;
		mFile = id;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_play_dialog);
		mAudioPlay = new AudioPlay(id, context);
		countDownTimer = new MyCount(mAudioPlay.getPlayBackTime(), 1000);
		audio_play_dialog_stop_button = (Button) findViewById(R.id.audio_play_dialog_stop_button);
		audio_play_dialog_play_button = (Button) findViewById(R.id.audio_play_dialog_play_button);
		audio_play_dialog_cancel_button = (Button) findViewById(R.id.audio_play_dialog_cancel_button);
		audio_play_dialog_time_details_chronometer = (Chronometer) findViewById(R.id.audio_play_dialog_time_details_chronometer);
		audio_play_dialog_cancel_button.setOnClickListener(this);
		audio_play_dialog_play_button.setOnClickListener(this);
		audio_play_dialog_stop_button.setOnClickListener(this);
		setOnDismissListener(this);
		setOnCancelListener(this);
		mAudioPlay.startPlayBack();
		countDownTimer.start();
		show();
	}

	public AudioPlayDialog(Context context, String id,String fav) {
		super(context);
		mContext = context;
		mFile = id;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_play_dialog);
		mAudioPlay = new AudioPlay(id, context,fav);
		countDownTimer = new MyCount(mAudioPlay.getPlayBackTime(), 1000);
		audio_play_dialog_stop_button = (Button) findViewById(R.id.audio_play_dialog_stop_button);
		audio_play_dialog_play_button = (Button) findViewById(R.id.audio_play_dialog_play_button);
		audio_play_dialog_cancel_button = (Button) findViewById(R.id.audio_play_dialog_cancel_button);
		audio_play_dialog_time_details_chronometer = (Chronometer) findViewById(R.id.audio_play_dialog_time_details_chronometer);
		audio_play_dialog_cancel_button.setOnClickListener(this);
		audio_play_dialog_play_button.setOnClickListener(this);
		audio_play_dialog_stop_button.setOnClickListener(this);
		setOnDismissListener(this);
		setOnCancelListener(this);
		mAudioPlay.startPlayBack();
		countDownTimer.start();
		show();
	}
	
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.audio_play_dialog_cancel_button) {
			countDownTimer.cancel();
			mAudioPlay.stopPlayBack();
			dismiss();
		}

		if (v.getId() == R.id.audio_play_dialog_play_button) {
			audio_play_dialog_play_button.setVisibility(View.GONE);
			if (mAudioPlay.isAudioPlaying()) {
				countDownTimer.cancel();
				mAudioPlay.stopPlayBack();
			}
			mAudioPlay = new AudioPlay(mFile, mContext);
			countDownTimer = new MyCount(mAudioPlay.getPlayBackTime(), 1000);
			countDownTimer.start();
			mAudioPlay.startPlayBack();
			audio_play_dialog_stop_button.setVisibility(View.VISIBLE);
		}

		if (v.getId() == R.id.audio_play_dialog_stop_button) {

			audio_play_dialog_stop_button.setVisibility(View.GONE);
			mAudioPlay.stopPlayBack();
			countDownTimer.cancel();
			audio_play_dialog_time_details_chronometer
					.setText(new DisplayTime().getDisplayTime(mAudioPlay
							.getPlayBackTime()));
			audio_play_dialog_play_button.setVisibility(View.VISIBLE);
		}

	}

	// /////// ********* CountdownTimer for Chronometer ********* //////////
	// countdowntimer is an abstract class, so extend it and fill in methods
	private class MyCount extends CountDownTimer {

		DisplayTime mDisplayTime;

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mDisplayTime = new DisplayTime();
		}

		@Override
		public void onFinish() {
			audio_play_dialog_time_details_chronometer.setText(mDisplayTime
					.getDisplayTime(mAudioPlay.getPlayBackTime()));
			audio_play_dialog_stop_button.setVisibility(View.GONE);
			audio_play_dialog_play_button.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			audio_play_dialog_time_details_chronometer.setText(mDisplayTime
					.getDisplayTime(millisUntilFinished));
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Log.v("mAudioPlay.isAudioPlaying() dismiss",
				mAudioPlay.isAudioPlaying() + "");
		if (mAudioPlay.isAudioPlaying()) {
			mAudioPlay.stopPlayBack();
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		Log.v("mAudioPlay.isAudioPlaying() cancel", mAudioPlay.isAudioPlaying()
				+ "");
		if (mAudioPlay.isAudioPlaying()) {
			mAudioPlay.stopPlayBack();
		}
	}
}
