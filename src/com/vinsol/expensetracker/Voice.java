package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.utils.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTime;
import com.vinsol.expensetracker.utils.FileDelete;
import com.vinsol.expensetracker.utils.MyCountDownTimer;
import com.vinsol.expensetracker.utils.RecordingHelper;

public class Voice extends EditAbstract implements OnClickListener {

	private TextView editHeaderTitle;
	private RelativeLayout editVoiceDetails;
	private Chronometer editTimeDetailsChronometer;
	private Button editStopButton;
	private Button editPlayButton;
	private Button editRerecordButton;
	private MyCountDownTimer countDownTimer;
	private RecordingHelper mRecordingHelper;
	private AudioPlay mAudioPlay;
	private long userId;
	private Bundle intentExtras;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView editDateBarDateview;
	private String dateViewString;
	private ArrayList<String> mEditList; 
	private boolean setUnknown = false;
	private boolean isChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_page);

		// ////// ******** Initializing and assigning memory to UI Items ********** /////////

		editHeaderTitle = (TextView) findViewById(R.id.edit_header_title);
		editVoiceDetails = (RelativeLayout) findViewById(R.id.edit_voice_details);
		editTimeDetailsChronometer = (Chronometer) findViewById(R.id.edit_time_details_chronometer);
		editStopButton = (Button) findViewById(R.id.edit_stop_button);
		editPlayButton = (Button) findViewById(R.id.edit_play_button);
		editRerecordButton = (Button) findViewById(R.id.edit_rerecord_button);
		editDateBarDateview = (TextView) findViewById(R.id.edit_date_bar_dateview);
		mDatabaseAdapter = new DatabaseAdapter(this);

		// //////********* Get id from intent extras ******** ////////////
		intentExtras = getIntent().getBundleExtra("voiceBundle");
		editHelper(intentExtras, R.string.voice, R.string.finished_voiceentry, R.string.unfinished_voiceentry);
		getData();
		// ////// ******** Starts Recording each time activity starts ****** ///////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			setGraphicsVoice();

			if (intentExtras.containsKey("mDisplayList") && !setUnknown) {
				File tempFile = new File("/sdcard/ExpenseTracker/Audio/" + userId + ".amr");

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(Long.toString(userId), this);
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.VISIBLE);
					editRerecordButton.setVisibility(View.VISIBLE);
					editTimeDetailsChronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					editTimeDetailsChronometer.setText("Audio File Missing");
					editRerecordButton.setVisibility(View.VISIBLE);
					editStopButton.setVisibility(View.GONE);
					editPlayButton.setVisibility(View.GONE);
				}
			} else {
				mRecordingHelper = new RecordingHelper(userId + "", this);
				mRecordingHelper.startRecording();
				controlVoiceChronometer();
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG).show();
		}
		setClickListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		dateViewString = editDateBarDateview.getText().toString();
	}


	
	private void getData() {
		userId = getId();
		mEditList = getEditList();
		intentExtras = getIntentExtras();
		setUnknown = isSetUnknown();
		isChanged = isChanged();
	}
	
	@Override
	protected void onPause() {

		// //// ***** Check whether audio is recording or not ******* ///////
		// //// ****** If audio recording started then stop recording audio  ***** ///////
		try {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				if (mRecordingHelper.isRecording()) {
					mRecordingHelper.stopRecording();
				}
			}
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {

		}
		super.onPause();
	}

	private void setClickListeners() {
		// ////// ******* Adding Click Listeners to UI Items ******** //////////

		editStopButton.setOnClickListener(this);
		editPlayButton.setOnClickListener(this);
		editRerecordButton.setOnClickListener(this);

		Button edit_save_entry = (Button) findViewById(R.id.edit_save_entry);
		edit_save_entry.setOnClickListener(this);

		Button edit_delete = (Button) findViewById(R.id.edit_delete);
		edit_delete.setOnClickListener(this);
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
		// ///// ***** Sets Title Voice Entry *********///////
		editHeaderTitle.setText("Voice Entry");

		// //// ****** Shows Voice Details ********////////
		editVoiceDetails.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
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
				mAudioPlay = new AudioPlay(userId + "", this);
				editTimeDetailsChronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
			} catch (NullPointerException e) {

			}
		}

		// // ***** if play button pressed ****** //////
		else if (v.getId() == R.id.edit_play_button) {
			// //// ******** to handle playback of recorded file *********
			// ////////
			mAudioPlay = new AudioPlay(userId + "", this);

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
			setChanged(isChanged);
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
			mRecordingHelper = new RecordingHelper(userId + "", this);
			mRecordingHelper.startRecording();
			editTimeDetailsChronometer.setBase(SystemClock.elapsedRealtime());
			editTimeDetailsChronometer.start();
		}

		// //////******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.edit_save_entry) {
			saveEntry();
		}

		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.edit_delete) {
			// //// ***** Check whether audio is recording or not *******
			// ///////
			// //// ****** If audio recording started then stop recording audio
			// ***** ///////
			try {
				if (mRecordingHelper.isRecording()) {
					mRecordingHelper.stopRecording();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			// /// ******* If Audio PlayBack is there stop playing audio
			// *******//////
			try {
				if (mAudioPlay.isAudioPlaying()) {
					mAudioPlay.stopPlayBack();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			editTimeDetailsChronometer.stop();

			new FileDelete(userId);

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(userId));
			mDatabaseAdapter.close();
			
			if(intentExtras.containsKey("isFromShowPage")){
				Intent mIntent = new Intent(this, ShowTextActivity.class);
				ArrayList<String> listOnResult = new ArrayList<String>();
				listOnResult.add("");
				Bundle tempBundle = new Bundle();
				tempBundle.putStringArrayList("mDisplayList", listOnResult);
				mEditList = new ArrayList<String>();
				mEditList.addAll(listOnResult);
				mIntent.putExtra("textShowBundle", tempBundle);
				setResult(Activity.RESULT_CANCELED, mIntent);
			}
			finish();
		}
	}

	private void saveEntry() {
		
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> _list = getSaveEntryData(editDateBarDateview,dateViewString);
		// //// ******* Update database if user added additional info *******		 ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
		
		if(!intentExtras.containsKey("isFromShowPage")){
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			Bundle mToHighLight = new Bundle();
			mToHighLight.putString("toHighLight", _list.get(DatabaseAdapter.KEY_ID));
			intentExpenseListing.putExtras(mToHighLight);
			intentExpenseListing.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentExpenseListing);
		} else {
			
			Intent mIntent = new Intent(this, ShowVoiceActivity.class);
			Bundle tempBundle = new Bundle();
			tempBundle.putStringArrayList("mDisplayList", getListOnResult(_list));
			mIntent.putExtra("voiceShowBundle", tempBundle);
			setResult(Activity.RESULT_OK, mIntent);
		}
		finish();
	}

	// /// ****************** Handling back press of key ********** ///////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code above on earlier versions of the platform.
		saveEntry();
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {
		}
		return;
	}
	
}
