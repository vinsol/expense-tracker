package com.vinsol.expensetracker.show;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.DatabaseAdapter;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.edit.Voice;
import com.vinsol.expensetracker.utils.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTime;
import com.vinsol.expensetracker.utils.FavoriteHelper;
import com.vinsol.expensetracker.utils.FileDelete;
import com.vinsol.expensetracker.utils.MyCountDownTimer;

public class ShowVoiceActivity extends ShowAbstract {
 
	private RelativeLayout dateBarRelativeLayout;
	private RelativeLayout showVoiceDetails;
	private Button showPlayButton;
	private Button showStopButton;
	private Chronometer showTimeDetailsChronometer;
	private MyCountDownTimer countDownTimer;
	private AudioPlay mAudioPlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_page);
		
		dateBarRelativeLayout = (RelativeLayout) findViewById(R.id.show_date_bar); 
		showHeaderTitle = (TextView) findViewById(R.id.show_header_title);
		showVoiceDetails = (RelativeLayout) findViewById(R.id.show_voice_details);
		showDelete = (Button) findViewById(R.id.show_delete);
		showPlayButton = (Button) findViewById(R.id.show_play_button);
		showStopButton = (Button) findViewById(R.id.show_stop_button);
		showTimeDetailsChronometer = (Chronometer) findViewById(R.id.show_time_details_chronometer);
		showEdit = (Button) findViewById(R.id.show_edit);

		dateBarRelativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.date_bar_bg_wo_shadow));
		
		mDatabaseAdapter = new DatabaseAdapter(this);

		intentExtras = getIntent().getBundleExtra("voiceShowBundle");

		showHeaderTitle.setText(getString(R.string.finished_voiceentry));
		
		showHelper(intentExtras,R.string.voice,R.string.finished_voiceentry,R.string.unfinished_voiceentry);
		showDelete.setOnClickListener(this);
		showPlayButton.setOnClickListener(this);
		showStopButton.setOnClickListener(this);
		showEdit.setOnClickListener(this);

		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			updateUI();
			if (intentExtras.containsKey("mDisplayList")) {
				File tempFile = new File("/sdcard/ExpenseTracker/Audio/" + userId+ ".amr");

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(Long.toString(userId), this);
					showStopButton.setVisibility(View.GONE);
					showPlayButton.setVisibility(View.VISIBLE);
					showTimeDetailsChronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					showTimeDetailsChronometer.setText("Audio File Missing");
					showStopButton.setVisibility(View.GONE);
					showPlayButton.setVisibility(View.GONE);
				}
				mFavoriteHelper = new FavoriteHelper(this, mShowList);
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
	protected void deleteAction() {
		super.deleteAction();
		// /// ******* If Audio PlayBack is there stop playing audio
		// *******//////
		try {
			if (mAudioPlay.isAudioPlaying()) {
				mAudioPlay.stopPlayBack();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		showTimeDetailsChronometer.stop();

		new FileDelete(userId);
	}
	
	@Override
	protected void editAction() {
		super.editAction();
		Intent editIntent = new Intent(this, Voice.class);
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {

		}
		editIntent.putExtra("voiceBundle", intentExtras);
		startActivityForResult(editIntent, SHOW_RESULT);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.show_play_button) {
			// //// ******** to handle playback of recorded file *********
			// ////////
			mAudioPlay = new AudioPlay(userId + "", this);

			// ///// ******* Chronometer Starts Countdown ****** ///////
			countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000, showTimeDetailsChronometer, showStopButton ,showPlayButton, mAudioPlay);

			// //// ****** Handles UI items on button click ****** ///////
			showPlayButton.setVisibility(View.GONE);
			showStopButton.setVisibility(View.VISIBLE);

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

		if (v.getId() == R.id.show_stop_button) {
			try {
				countDownTimer.cancel();
			} catch (NullPointerException e) {
			}
			
			// //// ****** Handles UI items on button click ****** ///////
			showStopButton.setVisibility(View.GONE);
			showPlayButton.setVisibility(View.VISIBLE);

			// //// ******* Stop Recording Audio and stop chronometer ********
			// ////////
			showTimeDetailsChronometer.stop();
			try {
				if (mAudioPlay.isAudioPlaying()) {
					mAudioPlay.stopPlayBack();
				}
			} catch (Exception e) {
			}
			showTimeDetailsChronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
		}
	}

	@Override
	protected void onPause() {

		// //// ***** Check whether audio is recording or not ******* ///////
		// //// ****** If audio recording started then stop recording audio ***** ///////
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {

		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (SHOW_RESULT == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
				intentExtras = data.getBundleExtra("voiceShowBundle");
				doTaskOnActivityResult(intentExtras);
				showDelete.setOnClickListener(this);
				showPlayButton.setOnClickListener(this);
				showStopButton.setOnClickListener(this);
				showEdit.setOnClickListener(this);

				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					updateUI();
					if (intentExtras.containsKey("mDisplayList")) {
						File tempFile = new File("/sdcard/ExpenseTracker/Audio/" + userId + ".amr");

						if (tempFile.canRead()) {
							mAudioPlay = new AudioPlay(Long.toString(userId), this);
							showStopButton.setVisibility(View.GONE);
							showPlayButton.setVisibility(View.VISIBLE);
							showTimeDetailsChronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
						} else {
							showTimeDetailsChronometer.setText("Audio File Missing");
							showStopButton.setVisibility(View.GONE);
							showPlayButton.setVisibility(View.GONE);
						}
						mFavoriteHelper.setShowList(mShowList);
					}
				} else {
					Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
				}
			}
		}

		if(resultCode == Activity.RESULT_CANCELED){
			finish();
		}
	}
	
}
