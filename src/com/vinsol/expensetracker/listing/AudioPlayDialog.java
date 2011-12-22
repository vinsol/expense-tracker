package com.vinsol.expensetracker.listing;

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.helpers.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTimeForChronometer;
import com.vinsol.expensetracker.utils.MyCountDownTimer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;

public class AudioPlayDialog extends Dialog implements android.view.View.OnClickListener,android.content.DialogInterface.OnDismissListener,android.content.DialogInterface.OnCancelListener {

	private Button stopButton;
	private Button playButton;
	private Button cancelButton;
	private Chronometer timeDetailsChronometer;
	private AudioPlay mAudioPlay;
	private MyCountDownTimer countDownTimer;
	private String mFile;
	private Context mContext;

	public AudioPlayDialog(Context context, String id) {
		super(context);
		doCommonTaskBefore(context, id);
		mAudioPlay = new AudioPlay(id, context,false);
		doCommonTask();
	}

	public AudioPlayDialog(Context context, String id,String fav) {
		super(context);
		doCommonTaskBefore(context,id);
		mAudioPlay = new AudioPlay(id, context,true);
		doCommonTask();
	}
	
	private void doCommonTaskBefore(Context context, String id){
		
		mContext = context;
		mFile = id;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_play_dialog);
	}
	
	private void doCommonTask(){
		stopButton = (Button) findViewById(R.id.stop_button);
		playButton = (Button) findViewById(R.id.play_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		timeDetailsChronometer = (Chronometer) findViewById(R.id.time_details_chronometer);
		cancelButton.setOnClickListener(this);
		playButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		setOnDismissListener(this);
		setOnCancelListener(this);
		countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000, timeDetailsChronometer, stopButton,playButton,mAudioPlay);
		mAudioPlay.startPlayBack();
		countDownTimer.start();
		show();
	}
	
	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.cancel_button) {
			countDownTimer.cancel();
			mAudioPlay.stopPlayBack();
			dismiss();
		}

		if (v.getId() == R.id.play_button) {
			playButton.setVisibility(View.GONE);
			if (mAudioPlay.isAudioPlaying()) {
				countDownTimer.cancel();
				mAudioPlay.stopPlayBack();
			}
			mAudioPlay = new AudioPlay(mFile, mContext,false);
			countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000,timeDetailsChronometer,stopButton,playButton,mAudioPlay);
			countDownTimer.start();
			mAudioPlay.startPlayBack();
			stopButton.setVisibility(View.VISIBLE);
		}

		if (v.getId() == R.id.stop_button) {

			stopButton.setVisibility(View.GONE);
			mAudioPlay.stopPlayBack();
			countDownTimer.cancel();
			timeDetailsChronometer.setText(new DisplayTimeForChronometer().getDisplayTime(mAudioPlay.getPlayBackTime()));
			playButton.setVisibility(View.VISIBLE);
		}

	}

	
	////// ****  Stops Audio PlayBack When Dialog will dismiss *****  ///////
	@Override
	public void onDismiss(DialogInterface dialog) {
		if (mAudioPlay.isAudioPlaying()) {
			mAudioPlay.stopPlayBack();
		}
	}

	////****  Stops Audio PlayBack When Dialog will cancel *****  ///////
	@Override
	public void onCancel(DialogInterface dialog) {
		if (mAudioPlay.isAudioPlaying()) {
			mAudioPlay.stopPlayBack();
		}
	}
}
