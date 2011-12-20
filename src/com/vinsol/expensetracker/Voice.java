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

public class Voice extends Activity implements OnClickListener {

	private TextView text_voice_camera_header_title;
	private RelativeLayout text_voice_camera_voice_details;
	private Chronometer text_voice_camera_time_details_chronometer;
	private Button text_voice_camera_stop_button;
	private Button text_voice_camera_play_button;
	private Button text_voice_camera_rerecord_button;
	private MyCountDownTimer countDownTimer;
	private RecordingHelper mRecordingHelper;
	private AudioPlay mAudioPlay;
	private long _id;
	private Bundle intentExtras;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView text_voice_camera_date_bar_dateview;
	private String dateViewString;
	private ArrayList<String> mEditList; 
	private boolean setUnknown = false;
	private boolean isChanged = false;
	private EditHelper mEditHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.text_voice_camera);

		// ////// ******** Initializing and assigning memory to UI Items ********** /////////

		text_voice_camera_header_title = (TextView) findViewById(R.id.text_voice_camera_header_title);
		text_voice_camera_voice_details = (RelativeLayout) findViewById(R.id.text_voice_camera_voice_details);
		text_voice_camera_time_details_chronometer = (Chronometer) findViewById(R.id.text_voice_camera_time_details_chronometer);
		text_voice_camera_stop_button = (Button) findViewById(R.id.text_voice_camera_stop_button);
		text_voice_camera_play_button = (Button) findViewById(R.id.text_voice_camera_play_button);
		text_voice_camera_rerecord_button = (Button) findViewById(R.id.text_voice_camera_rerecord_button);
		text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
		mDatabaseAdapter = new DatabaseAdapter(this);

		// //////********* Get id from intent extras ******** ////////////
		intentExtras = getIntent().getBundleExtra("voiceBundle");
		mEditHelper = new EditHelper(this, intentExtras, R.string.voice, R.string.finished_voiceentry, R.string.unfinished_voiceentry);
		getData();
		// ////// ******** Starts Recording each time activity starts ****** ///////
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			setGraphicsVoice();

			if (intentExtras.containsKey("mDisplayList") && !setUnknown) {
				File tempFile = new File("/sdcard/ExpenseTracker/Audio/" + _id+ ".amr");

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(Long.toString(_id), this);
					text_voice_camera_stop_button.setVisibility(View.GONE);
					text_voice_camera_play_button.setVisibility(View.VISIBLE);
					text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
					text_voice_camera_time_details_chronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
				} else {
					text_voice_camera_time_details_chronometer.setText("Audio File Missing");
					text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
					text_voice_camera_stop_button.setVisibility(View.GONE);
					text_voice_camera_play_button.setVisibility(View.GONE);
				}
			} else {
				mRecordingHelper = new RecordingHelper(_id + "", this);
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
		dateViewString = text_voice_camera_date_bar_dateview.getText().toString();
	}


	
	private void getData() {
		_id = mEditHelper.getId();
		mEditList = mEditHelper.getEditList();
		intentExtras = mEditHelper.getIntentExtras();
		setUnknown = mEditHelper.isSetUnknown();
		isChanged = mEditHelper.isChanged();
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

		text_voice_camera_stop_button.setOnClickListener(this);
		text_voice_camera_play_button.setOnClickListener(this);
		text_voice_camera_rerecord_button.setOnClickListener(this);

		Button text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_save_entry.setOnClickListener(this);

		Button text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);
		text_voice_camera_delete.setOnClickListener(this);
	}

	private void controlVoiceChronometer() {
		text_voice_camera_time_details_chronometer.start();
		text_voice_camera_time_details_chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (text_voice_camera_time_details_chronometer.getText().length() > 5) {
					text_voice_camera_time_details_chronometer.stop();
					text_voice_camera_stop_button.setVisibility(View.GONE);
					text_voice_camera_play_button.setVisibility(View.VISIBLE);
					text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void setGraphicsVoice() {
		// ///// ***** Sets Title Voice Entry *********///////
		text_voice_camera_header_title.setText("Voice Entry");

		// //// ****** Shows Voice Details ********////////
		text_voice_camera_voice_details.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// ///// ******** Adding On Click Actions to click listeners *********
		// //////////

		// // ***** if stop button pressed ****** //////
		if (v.getId() == R.id.text_voice_camera_stop_button) {
			try {
				countDownTimer.cancel();
			} catch (NullPointerException e) {
			}
			

			// //// ****** Handles UI items on button click ****** ///////
			text_voice_camera_stop_button.setVisibility(View.GONE);
			text_voice_camera_play_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);

			// //// ******* Stop Recording Audio and stop chronometer ********
			// ////////
			try {
				if (mRecordingHelper.isRecording())
					mRecordingHelper.stopRecording();
			} catch (Exception e) {
			}
			text_voice_camera_time_details_chronometer.stop();
			try {
				if (mAudioPlay.isAudioPlaying())
					mAudioPlay.stopPlayBack();
			} catch (Exception e) {
			}
			try {
				mAudioPlay = new AudioPlay(_id + "", this);
				text_voice_camera_time_details_chronometer.setText(new DisplayTime().getDisplayTime(mAudioPlay.getPlayBackTime()));
			} catch (NullPointerException e) {

			}
		}

		// // ***** if play button pressed ****** //////
		else if (v.getId() == R.id.text_voice_camera_play_button) {
			// //// ******** to handle playback of recorded file *********
			// ////////
			mAudioPlay = new AudioPlay(_id + "", this);

			// ///// ******* Chronometer Starts Countdown ****** ///////
			countDownTimer = new MyCountDownTimer(mAudioPlay.getPlayBackTime(), 1000, text_voice_camera_time_details_chronometer,text_voice_camera_stop_button,text_voice_camera_play_button,mAudioPlay);

			// //// ****** Handles UI items on button click ****** ///////
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.VISIBLE);

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
		else if (v.getId() == R.id.text_voice_camera_rerecord_button) {
			isChanged = true;
			mEditHelper.setChanged(isChanged);
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
			text_voice_camera_play_button.setVisibility(View.GONE);
			text_voice_camera_stop_button.setVisibility(View.VISIBLE);
			text_voice_camera_rerecord_button.setVisibility(View.GONE);

			// //// ****** Restarts chronometer and recording ******* ////////
			if(mRecordingHelper != null)
				if (mRecordingHelper.isRecording())
					mRecordingHelper.stopRecording();
			mRecordingHelper = new RecordingHelper(_id + "", this);
			mRecordingHelper.startRecording();
			text_voice_camera_time_details_chronometer.setBase(SystemClock.elapsedRealtime());
			text_voice_camera_time_details_chronometer.start();
		}

		// //////******** Adding Action to save entry ********* ///////////

		if (v.getId() == R.id.text_voice_camera_save_entry) {
			saveEntry();
		}

		// /////// ********* Adding action if delete button ********** /////////

		if (v.getId() == R.id.text_voice_camera_delete) {
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
			text_voice_camera_time_details_chronometer.stop();

			new FileDelete(_id);

			// //// ******* Delete entry from database ******** /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
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
		HashMap<String, String> _list = mEditHelper.getSaveEntryData(text_voice_camera_date_bar_dateview,dateViewString);
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
			tempBundle.putStringArrayList("mDisplayList", mEditHelper.getListOnResult(_list));
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
